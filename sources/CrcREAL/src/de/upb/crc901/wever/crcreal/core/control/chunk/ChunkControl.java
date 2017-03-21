package de.upb.crc901.wever.crcreal.core.control.chunk;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Semaphore;

import org.moeaframework.core.PRNG;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.Subscribe;

import de.upb.crc901.wever.crcreal.core.control.AbstractControl;
import de.upb.crc901.wever.crcreal.model.events.FinishedTaskProcessingEvent;
import de.upb.crc901.wever.crcreal.model.events.InitialSetupEvent;
import de.upb.crc901.wever.crcreal.model.events.SeedInitializationEvent;
import de.upb.crc901.wever.crcreal.model.events.ShutdownEvent;
import de.upb.crc901.wever.crcreal.model.events.StartTaskProcessingEvent;
import de.upb.crc901.wever.crcreal.model.events.TaskDefinitionEvent;
import de.upb.crc901.wever.crcreal.util.rand.IRandomGenerator;
import de.upb.crc901.wever.crcreal.util.rand.PseudoRandomGenerator;
import de.upb.wever.util.chunk.Chunk;
import de.upb.wever.util.chunk.Task;

public class ChunkControl extends AbstractControl {

	private static final String ID = "crc.real.control.chunk";

	private static final Logger LOGGER = LoggerFactory.getLogger(ChunkControl.class);

	private final Chunk chunk;
	private final Semaphore nextRunSemaphore = new Semaphore(0);

	public ChunkControl(final Chunk pChunk) {
		super(ID);
		this.setNumberOfJobs(pChunk.size());
		this.chunk = pChunk;
	}

	@Override
	public void run() {
		for (final Task chunkTask : this.chunk) {
			try {
				this.startTimeMeasure();
				final IRandomGenerator prg = new PseudoRandomGenerator(chunkTask.getRootSeed());

				// Select instances needed for the next task
				this.getEventBus().post(new InitialSetupEvent(chunkTask.getAlgorithmID(), chunkTask.getGeneratorID(), chunkTask.getOracleID(), chunkTask.getValidatorID(),
						chunkTask.getSupplierID()));
				this.waitForEventDelivery();

				// Set the initial seeds for pseudo randomness
				this.getEventBus().post(new SeedInitializationEvent(prg.nextSeed(), prg.nextSeed(), prg.nextSeed(), prg.nextSeed()));
				PRNG.setSeed(prg.nextSeed());

				// Send task definition to all REAL entities
				this.getEventBus().post(new TaskDefinitionEvent(chunkTask));
				this.waitForEventDelivery();

				LOGGER.info("Execute algorithm {} with g={}, p={}, r={}, #s={}, alphabetSize={}, #t={}", chunkTask.getAlgorithmID(), chunkTask.getNumberOfGenerations(),
						chunkTask.getSizeOfPopulation(), chunkTask.getNumberOfRounds(), chunkTask.getNumberOfStates(), chunkTask.getSizeOfAlphabet(),
						chunkTask.getSizeOfTrainingSet());

				this.getEventBus().post(new StartTaskProcessingEvent());

				LOGGER.trace("ChunkControl WAIT for next run");
				this.nextRunSemaphore.acquire();
				LOGGER.trace("ChunkControl WAITED ENOUGH for next run");

				this.jobDone();
				this.writeJobStatus();
			} catch (final InterruptedException e) {
				LOGGER.error("{} {}", e.getMessage(), e.getStackTrace());
			} catch (final Exception e) {
				LOGGER.error("{} {}", e.getMessage(), e.getStackTrace());
			}
			LOGGER.debug("Semaphore has been released for the next execution run.");
		}
		LOGGER.debug("Send shutdown event");
		this.getEventBus().post(new ShutdownEvent());
		LOGGER.trace("ChunkControl finished.");
	}

	@Subscribe
	public void rcvFinishedTaskProcessingEvent(final FinishedTaskProcessingEvent e) {
		LOGGER.debug("Received LearnerResultEvent and release on semaphore.");
		this.nextRunSemaphore.release();
	}

	/**
	 * This methods puts the current thread to sleep to wait for the delivery of all initial setup events. This is only needed if the event bus is asynchronous.
	 *
	 * @throws InterruptedException
	 */
	private void waitForEventDelivery() {
		if (this.getEventBus() instanceof AsyncEventBus) {
			try {
				Thread.sleep(50);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void writeJobStatus() {
		try (FileWriter chunkStateWriter = new FileWriter("chunkstate.txt")) {
			chunkStateWriter.write("chunkID=" + this.chunk.getChunkID());
			chunkStateWriter.write("&state=" + Math.round(this.getPercentageStatus()));
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
