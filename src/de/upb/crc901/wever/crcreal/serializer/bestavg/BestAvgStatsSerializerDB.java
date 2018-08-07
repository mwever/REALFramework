package de.upb.crc901.wever.crcreal.serializer.bestavg;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.aeonbits.owner.ConfigCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractModelObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractTestObjective;
import de.upb.crc901.wever.crcreal.model.AbstractCandidate;
import de.upb.crc901.wever.crcreal.model.events.ModelPopulationEvent;
import de.upb.crc901.wever.crcreal.model.events.TestPopulationEvent;
import de.upb.crc901.wever.crcreal.util.Pair;
import de.upb.crc901.wever.crcreal.util.chunk.REALTask;
import de.upb.crc901.wever.crcreal.util.evaluation.BestAvgUtil;
import de.upb.crc901.wever.model.ExperimentRunnerConfig;
import jaicore.basic.SQLAdapter;

public class BestAvgStatsSerializerDB {

	private static final Logger LOGGER = LoggerFactory.getLogger(BestAvgStatsSerializerDB.class);

	public static final String BASE_PATH = "evalresult/";
	public static final String CONFIGLIST_FILE = "configlist.list";
	private static final String CONFIG_FILE = "realconfig.conf";
	private final Lock configListFileLock = new ReentrantLock();

	private REALTask task;
	private int runID;

	private final LinkedBlockingQueue<LogDatum> logBuffer = new LinkedBlockingQueue<>();

	private final SQLAdapter adapter;
	private final ExperimentRunnerConfig runConfig = ConfigCache.getOrCreate(ExperimentRunnerConfig.class);

	public BestAvgStatsSerializerDB(final SQLAdapter adapter) {
		this.adapter = adapter;
		this.runID = this.runConfig.runID();
	}

	private Pair<BestAvgMap, BestAvgMap> processObjectiveResults(final List<? extends AbstractObjective> objectives,
			final List<? extends AbstractCandidate> population) {
		final BestAvgMap realMap = new BestAvgMap();
		final BestAvgMap heurMap = new BestAvgMap();

		for (final AbstractObjective objective : objectives) {
			if (objective.hasRealData()) {
				final List<Double> realEvaluationValueList = population.stream()
						.map(x -> x.getRealObjectiveValue(objective.getID())).collect(Collectors.toList());
				realMap.put(objective.getID(), BestAvgUtil.computeBestAvgFromList(objective, realEvaluationValueList));
			}
			final List<Double> heurEvaluationValueList = population.stream()
					.map(x -> x.getHeuristicObjectiveValue(objective.getID())).collect(Collectors.toList());
			heurMap.put(objective.getID(), BestAvgUtil.computeBestAvgFromList(objective, heurEvaluationValueList));
		}
		return new Pair<>(realMap, heurMap);
	}

	@Subscribe
	public void rcvModelPopulationEvaluationEvent(final ModelPopulationEvent e) {
		final List<AbstractModelObjective> objectives = new LinkedList<>();
		final Set<AbstractObjective> objSet = new HashSet<>();
		e.getPopulation().stream().map(x -> x.getObjectives()).forEach(x -> {
			objSet.addAll(x);
		});

		try {
			objSet.stream().map(x -> (AbstractModelObjective) x).forEach(objectives::add);
		} catch (final ClassCastException exc) {
			System.out.println(objSet);
			System.exit(0);
		}

		final Pair<BestAvgMap, BestAvgMap> processingResult = this.processObjectiveResults(objectives,
				e.getPopulation());
		this.writeLogFile(objectives, "model", e.getRoundCounter(), e.getGenerationCounter(), processingResult.getX(),
				processingResult.getY());
	}

	@Subscribe
	public void rcvTestPopulationEvent(final TestPopulationEvent e) {
		LOGGER.trace("Received TestPopulationEvent");
		final List<AbstractTestObjective> objectives = new LinkedList<>();
		final Set<AbstractObjective> objSet = new HashSet<>();
		e.getPopulation().stream().map(x -> x.getObjectives()).forEach(objSet::addAll);
		try {
			objSet.stream().map(x -> (AbstractTestObjective) x).forEach(objectives::add);
		} catch (final ClassCastException exc) {
			System.out.println(objSet);
			System.exit(0);
		}
		final Pair<BestAvgMap, BestAvgMap> processingResult = this.processObjectiveResults(objectives,
				e.getPopulation());
		this.writeLogFile(objectives, "test", e.getRoundCounter(), e.getGenerationCounter(), processingResult.getX(),
				processingResult.getY());
	}

	private void writeLogFile(final List<? extends AbstractObjective> objectives, final String type, final int round,
			final int generation, final BestAvgMap real, final BestAvgMap heur) {

		Map<String, String> entryMap = new HashMap<>();
		entryMap.put("runId", this.runConfig.runID() + "");
		entryMap.put("type", type);
		entryMap.put("round", round + "");
		entryMap.put("generation", generation + "");

		for (final AbstractObjective objective : objectives) {
			entryMap.put("objectiveType", "heur");
			entryMap.put("objective", objective.getID());
			entryMap.put("objectiveValueBest", heur.get(objective.getID()).getBest() + "");
			entryMap.put("objectiveValueAvg", heur.get(objective.getID()).getAverage() + "");

			System.out.println(entryMap);
			try {
				this.adapter.insert("realevaluation", entryMap);
			} catch (SQLException e) {
				e.printStackTrace();
			}

			if (!real.isEmpty()) {
				entryMap.put("objectiveType", "real");
				entryMap.put("objective", objective.getID());
				entryMap.put("objectiveValueBest", real.get(objective.getID()).getBest() + "");
				entryMap.put("objectiveValueAvg", real.get(objective.getID()).getAverage() + "");
			}

			try {
				this.adapter.insert("realevaluation", entryMap);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
