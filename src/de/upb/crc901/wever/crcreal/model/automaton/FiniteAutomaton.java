package de.upb.crc901.wever.crcreal.model.automaton;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

import de.upb.crc901.wever.crcreal.model.alphabet.Alphabet;
import de.upb.crc901.wever.crcreal.model.alphabet.InputSymbol;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingExample;
import de.upb.crc901.wever.crcreal.model.trainingdata.TrainingSet;
import de.upb.crc901.wever.crcreal.model.word.EWordLabel;
import de.upb.crc901.wever.crcreal.model.word.Word;

public class FiniteAutomaton {

	private final static AtomicInteger AUTOMATON_COUNTER = new AtomicInteger(0);

	private final static int INITIAL_STATE = 0;

	private final int automatonID;
	private final int numberOfStates;
	private final Alphabet alphabet;
	private int size = -1;
	private final TransitionFunction transitionMatrix;
	private final Map<Integer, EWordLabel> stateLabels = new HashMap<>();

	/**
	 * Creates a DFA according to the given parameters. The initial state labels are all set to NONE.
	 *
	 * @param pNumberOfStates
	 *            The number of states of the automaton. Implicitly defines the states themselves.
	 * @param pTransitionMatrix
	 *            The transition matrix delta for the automaton.
	 * @param pAlphabet
	 *            The alphabet of the automaton.
	 */
	public FiniteAutomaton(final int pNumberOfStates, final TransitionFunction pTransitionMatrix, final Alphabet pAlphabet) {
		this.automatonID = AUTOMATON_COUNTER.getAndIncrement();
		this.numberOfStates = pNumberOfStates;
		this.transitionMatrix = pTransitionMatrix;
		this.alphabet = pAlphabet;
		IntStream.range(0, this.numberOfStates).forEach(x -> this.stateLabels.put(x, EWordLabel.NONE));
	}

	public FiniteAutomaton(final int pNumberOfStates, final int[][] pTransitionMatrixArray, final Alphabet pAlphabet) {
		this(pNumberOfStates, TransitionFunction.readFromArray(pTransitionMatrixArray), pAlphabet);
	}

	public FiniteAutomaton(final FiniteAutomaton otherAutomaton) {
		this(otherAutomaton.numberOfStates, new TransitionFunction(otherAutomaton.transitionMatrix), otherAutomaton.alphabet);
	}

	/**
	 * Checks whether the automaton is consistent with a given input sequence.
	 *
	 * @param example
	 *            The input sequence to check the automaton.
	 * @return Returns true iff the automaton's classification is the same es stated in the input sequence.
	 */
	public boolean consistentWithTrainingExample(final TrainingExample example) {
		return this.execute(example.getWord()).getLabel() == example.getLabel();
	}

	/**
	 * Executes the DFA on the given input sequence and returns the respective label the automaton produces for the given input sequence.
	 *
	 * @param pInputSequence
	 *            Input sequence the automaton shall be executed on.
	 * @return Returns the label of the resulting state.
	 */
	public ExecutionTrace execute(final Word pWord) {
		final LinkedList<Integer> stateTrace = new LinkedList<>();
		stateTrace.add(INITIAL_STATE);
		for (final InputSymbol input : pWord) {
			stateTrace.add(this.transitionMatrix.getNextState(stateTrace.getLast(), this.alphabet.indexOf(input)));
		}
		final EWordLabel label = this.stateLabels.get(stateTrace.getLast());

		return new ExecutionTrace(pWord, stateTrace, label);
	}

	/**
	 * Computes a trace of states used to process a given input sequence.
	 *
	 * @param pInputSequence
	 *            Input Sequence to run the DFA on starting in INITIAL_STATE.
	 * @return Returns the trace route of states used to process the given input sequence.
	 */
	public List<Integer> traceOfDFARun(final Word pInputSequence) {
		final List<Integer> traceRoute = new LinkedList<>();
		traceRoute.add(INITIAL_STATE);

		pInputSequence.stream().forEach(input -> {
			traceRoute.add(this.transitionMatrix.getNextState(traceRoute.get(traceRoute.size() - 1), this.alphabet.indexOf(input)));
		});

		return traceRoute;
	}

	/**
	 * @param pState
	 *            State to obtain label for
	 * @return Returns the label of the given state.
	 */
	public EWordLabel getLabelOfState(final int pState) {
		return this.stateLabels.get(pState);
	}

	/**
	 * This method outputs a graph to visualize the automaton in graphstream.
	 *
	 * @return Graph containing all the nodes of the automaton and labeled edges wrt. the given alphabet.
	 */
	public MultiGraph toGraphStream() {
		final MultiGraph g = this.toGraph();
		final Dijkstra dijkstra = this.executeDijkstraOnGraph(g);

		final Set<Edge> obsoleteEdgeSet = this.getNonTraversableEdges(g, dijkstra);
		final Set<Node> obsoleteNodeSet = this.getUnreachableNodes(g, dijkstra);
		obsoleteEdgeSet.stream().forEach(g::removeEdge);
		obsoleteNodeSet.stream().forEach(g::removeNode);

		// Double-check that we have not removed to much
		final Dijkstra dijkstraDC = this.executeDijkstraOnGraph(g);
		for (final Node n : g) {
			assert (dijkstraDC.getPathLength(n) != Double.POSITIVE_INFINITY) : "Unreachable nodes in graph after minimizing the automaton.";
		}

		return g;
	}

	private Set<Edge> getNonTraversableEdges(final Graph g, final Dijkstra dijkstra) {
		return g.getEdgeSet().stream().filter(x -> {
			return (dijkstra.getPathLength(x.getNode0()) == Double.POSITIVE_INFINITY || dijkstra.getPathLength(x.getNode1()) == Double.POSITIVE_INFINITY);
		}).collect(Collectors.toSet());

	}

	private Set<Node> getUnreachableNodes(final Graph g, final Dijkstra dijkstra) {
		return g.getNodeSet().stream().filter(x -> {
			return dijkstra.getPathLength(x) == Double.POSITIVE_INFINITY;
		}).collect(Collectors.toSet());
	}

	public void executeSmartLabelingAlgorithm(final TrainingSet trainingData) {
		final int[] labelCount = new int[this.numberOfStates];

		for (final TrainingExample example : trainingData) {
			final List<Integer> traceroute = this.traceOfDFARun(example.getWord());
			labelCount[traceroute.get(traceroute.size() - 1)] += (example.getLabel() == EWordLabel.ACCEPTING) ? 1 : -1;
		}

		for (int i = 0; i < labelCount.length; i++) {
			if (labelCount[i] >= 0) {
				this.stateLabels.put(i, EWordLabel.ACCEPTING);
			} else {
				this.stateLabels.put(i, EWordLabel.REJECTING);
			}
		}
	}

	public boolean allStatesReachable() {
		final Graph g = this.toGraph();
		return this.getUnreachableNodes(g, this.executeDijkstraOnGraph(g)).isEmpty();
	}

	private MultiGraph toGraph() {
		final MultiGraph g = new MultiGraph("DFA");

		// add nodes to the graph
		IntStream.range(0, this.numberOfStates).forEach(x -> {
			final Node n = g.addNode(x + "");
			n.setAttribute("ui.label", x + "");
			n.setAttribute("ui.class", "standard");

			if (this.getLabelOfState(x) == EWordLabel.ACCEPTING) {
				n.setAttribute("ui.style", "fill-color: #00dd00; text-size: 20px; size: 30px; shape: circle; stroke-mode: plain;");
			} else {
				n.setAttribute("ui.style", "fill-color: #dd0000; text-size: 20px; size: 30px; shape: circle; stroke-mode: plain;");
			}
		});

		// add edges with label
		for (int u = 0; u < this.numberOfStates; u++) {
			final Map<Integer, List<Integer>> targetNodeToInputs = new HashMap<>();
			for (int in = 0; in < this.alphabet.size(); in++) {
				final int v = this.transitionMatrix.getNextState(u, in);
				List<Integer> inputList = targetNodeToInputs.get(v);
				if (inputList == null) {
					inputList = new LinkedList<>();
					targetNodeToInputs.put(v, inputList);
				}
				inputList.add(in);
			}

			for (final Integer targetNode : targetNodeToInputs.keySet()) {
				final Edge e = g.addEdge(u + "->" + targetNode + ":" + targetNodeToInputs.get(targetNode), u + "", targetNode + "", true);

				String label = "";
				for (final Integer input : targetNodeToInputs.get(targetNode)) {
					label += ", " + this.alphabet.get(input);
				}
				label = label.substring(2);
				e.setAttribute("ui.label", label);
				e.setAttribute("ui.style", "text-size: 15px;");
				e.setAttribute("layout.weight", 10);
				e.setAttribute("length", 1);
			}
		}
		return g;
	}

	private Dijkstra executeDijkstraOnGraph(final Graph g) {
		// Compute unreachable nodes and remove the from the graph
		final Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "length");
		dijkstra.init(g);
		dijkstra.setSource(g.getNode(0 + ""));
		dijkstra.compute();
		return dijkstra;
	}

	public Alphabet getAlphabet() {
		return this.alphabet;
	}

	public TransitionFunction getTransitionFunction() {
		return this.transitionMatrix;
	}

	public int getID() {
		return this.automatonID;
	}

	public void setLabeling(final Map<Integer, EWordLabel> pLabelMap) {
		this.stateLabels.clear();
		this.stateLabels.putAll(pLabelMap);
	}

	public int size() {
		if (this.size == -1) {
			final MultiGraph g = this.toGraph();
			this.size = this.numberOfStates - this.getUnreachableNodes(g, this.executeDijkstraOnGraph(g)).size();
		}
		return this.size;
	}

	public int getNumberOfStates() {
		return this.numberOfStates;
	}

	@Override
	public String toString() {
		return "" + this.automatonID;
	}

	public Map<Integer, EWordLabel> getLabeling() {
		return this.stateLabels;
	}

	public String getTextualRepresentation() {
		final StringBuilder sb = new StringBuilder();

		sb.append("#" + this.getID() + ";");
		sb.append("numberOfStates=" + this.getNumberOfStates() + ";");
		sb.append("sizeOfAlphabet=" + this.alphabet.size() + ";");
		sb.append("transitionFunction=" + this.getTransitionFunction().getTextualRepresentation() + ";");
		sb.append("acceptingStates=");

		boolean first = true;
		for (final Integer state : this.stateLabels.keySet()) {
			if (this.stateLabels.get(state) == EWordLabel.ACCEPTING) {
				if (first) {
					first = false;
				} else {
					sb.append(",");
				}
				sb.append(state + "");
			}
		}
		sb.append(";");

		return sb.toString();
	}

}
