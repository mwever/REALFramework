package de.upb.crc901.wever.crcreal.serializer.bestavg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractModelObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.AbstractTestObjective;
import de.upb.crc901.wever.crcreal.model.AbstractCandidate;
import de.upb.crc901.wever.crcreal.model.events.ModelPopulationEvent;
import de.upb.crc901.wever.crcreal.model.events.ShutdownEvent;
import de.upb.crc901.wever.crcreal.model.events.TaskDefinitionEvent;
import de.upb.crc901.wever.crcreal.model.events.TestPopulationEvent;
import de.upb.crc901.wever.crcreal.util.Pair;
import de.upb.crc901.wever.crcreal.util.chunk.Task;
import de.upb.crc901.wever.crcreal.util.evaluation.BestAvgUtil;

public class BestAvgStatsSerializer {

	private static final Logger LOGGER = LoggerFactory.getLogger(BestAvgStatsSerializer.class);

	public static final String BASE_PATH = "evalresult/";
	public static final String CONFIGLIST_FILE = "configlist.list";
	private static final String CONFIG_FILE = "realconfig.conf";
	private final Lock configListFileLock = new ReentrantLock();

	private Task task;
	private String runID;

	private final LinkedBlockingQueue<LogDatum> logBuffer = new LinkedBlockingQueue<>();
	private final LogDispatcher logDispatcher;

	public BestAvgStatsSerializer() {
		this.runID = "realRun" + System.currentTimeMillis();
		this.logDispatcher = new LogDispatcher();
		this.logDispatcher.start();
	}

	private class LogDispatcher extends Thread {
		private boolean keepRunning = true;

		public void shutdown() {
			this.keepRunning = false;
		}

		@Override
		public void run() {
			while (this.keepRunning || !this.getSuper().logBuffer.isEmpty()) {
				if (!this.keepRunning) {
					LOGGER.debug("Still logs to write in the buffer: {} logs to write", this.getSuper().logBuffer.size());
				}

				try {
					final LogDatum datum = this.getSuper().logBuffer.poll(5000, TimeUnit.MILLISECONDS);

					if (datum != null) {
						final File datumFile = new File(datum.getFile());
						datumFile.getParentFile().mkdirs();

						try (FileWriter writer = new FileWriter(datum.getFile(), true)) {
							writer.write(datum.getLog() + "\n");
						} catch (final IOException e) {
							e.printStackTrace();
						}
					}
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
			LOGGER.debug("Logdispatcher shut down correctly");
		}

		public BestAvgStatsSerializer getSuper() {
			return BestAvgStatsSerializer.this;
		}
	}

	private Pair<BestAvgMap, BestAvgMap> processObjectiveResults(final List<? extends AbstractObjective> objectives, final List<? extends AbstractCandidate> population) {
		final BestAvgMap realMap = new BestAvgMap();
		final BestAvgMap heurMap = new BestAvgMap();

		for (final AbstractObjective objective : objectives) {
			if (objective.hasRealData()) {
				final List<Double> realEvaluationValueList = population.stream().map(x -> x.getRealObjectiveValue(objective.getID())).collect(Collectors.toList());
				realMap.put(objective.getID(), BestAvgUtil.computeBestAvgFromList(objective, realEvaluationValueList));
			}
			final List<Double> heurEvaluationValueList = population.stream().map(x -> x.getHeuristicObjectiveValue(objective.getID())).collect(Collectors.toList());
			heurMap.put(objective.getID(), BestAvgUtil.computeBestAvgFromList(objective, heurEvaluationValueList));
		}
		return new Pair<>(realMap, heurMap);
	}

	@Subscribe
	public void rcvModelPopulationEvaluationEvent(final ModelPopulationEvent e) {
		final List<AbstractModelObjective> objectives = new LinkedList<>();
		final Set<AbstractObjective> objSet = new HashSet<>();
		e.getPopulation().stream().map(x -> x.getObjectives()).forEach(objSet::addAll);
		try {
			objSet.stream().map(x -> (AbstractModelObjective) x).forEach(objectives::add);
		} catch (final ClassCastException exc) {
			System.out.println(objSet);
			System.exit(0);
		}
		final Pair<BestAvgMap, BestAvgMap> processingResult = this.processObjectiveResults(objectives, e.getPopulation());
		this.writeLogFile(objectives, "model", e.getRoundCounter(), e.getGenerationCounter(), processingResult.getX(), processingResult.getY());
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
		final Pair<BestAvgMap, BestAvgMap> processingResult = this.processObjectiveResults(objectives, e.getPopulation());
		this.writeLogFile(objectives, "test", e.getRoundCounter(), e.getGenerationCounter(), processingResult.getX(), processingResult.getY());
	}

	private void writeLogFile(final List<? extends AbstractObjective> objectives, final String type, final int round, final int generation, final BestAvgMap real,
			final BestAvgMap heur) {
		final String fileName = "task" + this.task.getTaskID() + ".log";

		final StringBuilder sb = new StringBuilder();
		sb.append("t=" + type);
		sb.append("&r=" + round);
		sb.append("&g=" + generation);
		for (final AbstractObjective objective : objectives) {
			sb.append("&heur-" + objective.getID() + "=" + heur.get(objective.getID()));
			if (!real.isEmpty()) {
				sb.append("&real-" + objective.getID() + "=" + real.get(objective.getID()));
			}
		}

		try {
			this.logBuffer.put(new LogDatum(BASE_PATH + this.runID + "/" + fileName, sb.toString()));
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Subscribe
	public void rcvTaskDefinitionEvent(final TaskDefinitionEvent e) {
		this.configListFileLock.lock();
		try {
			this.task = e.getTask();
			this.runID = "chunk" + this.task.getChunkID();
			final File taskFile = new File(BASE_PATH + this.runID + "/task" + this.task.getTaskID() + ".log");
			taskFile.getParentFile().mkdirs();

			try (FileWriter writer = new FileWriter(BASE_PATH + this.runID + "/task" + this.task.getTaskID() + ".log")) {
			} catch (final IOException e1) {
				e1.printStackTrace();
			}
			this.writeConfigFile();
		} finally {
			this.configListFileLock.unlock();
		}
	}

	private void writeConfigFile() {
		final String configFileName = BASE_PATH + this.runID + "/" + CONFIG_FILE;
		final StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader("config/REALConfig.properties"))) {
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (final IOException e1) {
			e1.printStackTrace();
		}
		try {
			this.logBuffer.put(new LogDatum(configFileName, sb.toString()));
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Subscribe
	public void rcvShutdownEvent(final ShutdownEvent e) {
		LOGGER.debug("Shutdown logdispatcher");
		this.logDispatcher.shutdown();
	}

}
