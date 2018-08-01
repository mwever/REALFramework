package de.upb.crc901.wever.crcreal.core.supplier;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.eventbus.Subscribe;

import de.upb.crc901.wever.crcreal.model.automaton.ExecutionTrace;
import de.upb.crc901.wever.crcreal.model.automaton.FiniteAutomaton;
import de.upb.crc901.wever.crcreal.model.events.AnnounceTargetModelEvent;
import de.upb.crc901.wever.crcreal.model.events.SupplierRequestEvent;
import de.upb.crc901.wever.crcreal.model.events.SupplierResultEvent;
import de.upb.crc901.wever.crcreal.model.word.EWordLabel;
import de.upb.crc901.wever.crcreal.model.word.Word;
import de.upb.crc901.wever.crcreal.util.rand.IRandomGenerator;

public class FAKTQSupplier extends ASupplier {

	private final int numberOfNegativeStates;
	private FiniteAutomaton supplierModel = null;

	private final Lock modelLock = new ReentrantLock();
	private final Condition modelNotThereCondition = this.modelLock.newCondition();

	public FAKTQSupplier(final String pIdentifier, final IRandomGenerator pPRG, final int numberOfNegativeStates) {
		super(pIdentifier, pPRG);
		this.numberOfNegativeStates = numberOfNegativeStates;
	}

	@Subscribe
	public void rcvAnnounceTargetModelEvent(final AnnounceTargetModelEvent e) {
		FiniteAutomaton supplierModel = new FiniteAutomaton(e.getTargetModel());
		Map<Integer, EWordLabel> labeling = supplierModel.getLabeling();
		List<Integer> rejectingStates = new LinkedList<>();
		for (Integer state : labeling.keySet()) {
			if (labeling.get(state) == EWordLabel.REJECTING) {
				rejectingStates.add(state);
			} else {
				assert labeling.get(
						state) == EWordLabel.ACCEPTING : "Unlabeled state in target model detected. This should not happen!";
			}
		}

		if (this.numberOfNegativeStates < rejectingStates.size()) {
			while (rejectingStates.size() > this.numberOfNegativeStates) {
				int indexToReject = this.getRandomGenerator().nextInteger(rejectingStates.size());
				Integer state = rejectingStates.remove(indexToReject);
				supplierModel.getLabeling().put(state, EWordLabel.ACCEPTING);
			}
		}

		this.modelLock.lock();
		try {
			System.out.println("Received and set up supplier model. Now ready to answer supplier request events");
			this.supplierModel = supplierModel;
			this.modelNotThereCondition.signalAll();
		} finally {
			this.modelLock.unlock();
		}
	}

	@Subscribe
	public void rcvSupplierRequestEvent(final SupplierRequestEvent e) {
		this.modelLock.lock();
		try {
			if (this.supplierModel == null) {
				System.out.println("Supplier not yet available.");
				this.modelNotThereCondition.await();
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} finally {
			this.modelLock.unlock();
		}

		Map<Word, EWordLabel> labelsForRequestedWordsMap = new HashMap<>();

		for (Word w : e.getRequestedWord()) {
			ExecutionTrace ex = this.supplierModel.execute(w);
			if (ex.getLabel() == EWordLabel.REJECTING) {
				labelsForRequestedWordsMap.put(w, ex.getLabel());
			} else {
				labelsForRequestedWordsMap.put(w, EWordLabel.NONE);
			}
		}

		this.sendSupplierResultEvent(e, labelsForRequestedWordsMap);
	}

	public void sendSupplierResultEvent(final SupplierRequestEvent e,
			final Map<Word, EWordLabel> labelsForRequestedWordsMap) {
		SupplierResultEvent er = new SupplierResultEvent(e, labelsForRequestedWordsMap);
		this.getEventBus().post(er);
	}

}
