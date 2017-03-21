package de.upb.crc901.wever.crcreal.core.learner.algorithm.simple;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import de.upb.crc901.wever.crcreal.core.learner.algorithm.AbstractAlgorithm;
import de.upb.crc901.wever.crcreal.core.learner.algorithm.AbstractModelAlgorithm;
import de.upb.crc901.wever.crcreal.model.AbstractCandidate;
import de.upb.crc901.wever.crcreal.model.alphabet.Alphabet;
import de.upb.crc901.wever.crcreal.model.automaton.CandidateModel;
import de.upb.crc901.wever.crcreal.model.automaton.FiniteAutomaton;
import de.upb.crc901.wever.crcreal.model.automaton.TransitionFunction;
import de.upb.crc901.wever.crcreal.model.config.AlgorithmConfig;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;
import de.upb.crc901.wever.crcreal.util.CandidateModelUtil;
import de.upb.crc901.wever.crcreal.util.Pair;
import de.upb.crc901.wever.crcreal.util.rand.IRandomGenerator;

public class SimpleModelAlgorithm extends AbstractModelAlgorithm {

	public static final String ID = "crc.real.algorithm.model.simpleModelAlgorithm";
	public static final double SELECTION_FACTOR = 0.75;

	private final List<CandidateModel> population;

	public SimpleModelAlgorithm(final AlgorithmConfig pAlgorithmConfig, final int pNumberOfStates, final Alphabet pAlphabet, final TrainingSet pTrainingData,
			final int pMaxTestLength, final IRandomGenerator pPRG) {
		super(pAlgorithmConfig, pNumberOfStates, pAlphabet, pTrainingData, pMaxTestLength, pPRG);
		this.population = new LinkedList<>();
	}

	public SimpleModelAlgorithm() {
		super();
		this.population = null;
	}

	@Override
	public void initPopulation() {
		final List<CandidateModel> newPopulation = new LinkedList<>();
		if (this.getSeededPopulation() && !this.population.isEmpty()) {
			newPopulation.addAll(this.getResult());
		}
		while (newPopulation.size() < this.getSizeOfPopulation()) {
			newPopulation.add(new CandidateModel(this.getObjectives(), CandidateModelUtil.generateRandomAutomaton(this.getNumberOfStates(), this.getAlphabet(), this.getPRG())));
		}
		newPopulation.parallelStream().forEach(this::computeFitnessForModel);

		this.population.clear();
		this.population.addAll(newPopulation);
	}

	@Override
	public void step() {
		for (int i = 0; i < this.population.size() * SELECTION_FACTOR; i++) {
			final Pair<AbstractCandidate, AbstractCandidate> tournamentResult = SimpleTournamentSelector.select(this.getPRG(), this.population,
					new ChainedCandidateComparator(this.getObjectives().stream().filter(x -> x.isActive()).collect(Collectors.toList())));
			final CandidateModel mutatedIndividual = this.mutateModel((CandidateModel) tournamentResult.getX());
			this.computeFitnessForModel(mutatedIndividual);

			this.population.remove(tournamentResult.getY());
			this.population.add(mutatedIndividual);
		}
	}

	private void computeFitnessForModel(final CandidateModel pCandidateModel) {
		pCandidateModel.getModel().executeSmartLabelingAlgorithm(this.getTrainingData());
		this.getObjectives().stream().forEach(x -> pCandidateModel.setHeuristicObjectiveValue(x.getID(), x.evaluate(this.getTrainingData(), pCandidateModel.getModel())));
	}

	private CandidateModel mutateModel(final CandidateModel individualToMutate) {
		final FiniteAutomaton mutatedAutomaton = new FiniteAutomaton(individualToMutate.getModel());

		final TransitionFunction mutatedTransitionFunction = mutatedAutomaton.getTransitionFunction();
		final int stateIndex = this.getPRG().nextInteger(this.getNumberOfStates());
		final int inputSymbol = this.getPRG().nextInputSymbol(this.getAlphabet().size());
		int newTargetState = this.getPRG().nextInteger(this.getNumberOfStates());
		while (newTargetState == mutatedTransitionFunction.getNextState(stateIndex, inputSymbol)) {
			newTargetState = this.getPRG().nextInteger(this.getNumberOfStates());
		}
		mutatedTransitionFunction.updateTransition(stateIndex, inputSymbol, newTargetState);

		return new CandidateModel(this.getObjectives(), mutatedAutomaton);
	}

	@Override
	public List<CandidateModel> getResult() {
		Collections.sort(this.population, new ChainedCandidateComparator(this.getObjectives().stream().filter(x -> x.isActive()).collect(Collectors.toList())));
		final List<CandidateModel> resultList = new LinkedList<>();
		resultList.add(this.population.get(0));
		return resultList;
	}

	@Override
	public List<CandidateModel> getPopulation() {
		final List<CandidateModel> resultList = new LinkedList<>(this.population);
		return resultList;
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public AbstractAlgorithm newInstance(final AlgorithmConfig pAlgorithmConfig, final int pNumberOfStates, final Alphabet pAlphabet, final TrainingSet pTrainingData,
			final int pMaxTestLength, final IRandomGenerator pPRG) {
		return new SimpleModelAlgorithm(pAlgorithmConfig, pNumberOfStates, pAlphabet, pTrainingData, pMaxTestLength, pPRG);
	}
}
