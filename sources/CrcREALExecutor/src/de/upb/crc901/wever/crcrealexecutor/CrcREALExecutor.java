package de.upb.crc901.wever.crcrealexecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.upb.crc901.wever.crcreal.core.REALManager;
import de.upb.crc901.wever.crcreal.core.control.AbstractControl;
import de.upb.crc901.wever.crcreal.core.control.arraycontrol.ArrayControl;
import de.upb.crc901.wever.crcreal.core.control.chunk.ChunkControl;
import de.upb.crc901.wever.crcreal.core.generator.AbstractGenerator;
import de.upb.crc901.wever.crcreal.core.learner.AbstractActiveLearner;
import de.upb.crc901.wever.crcreal.core.validator.AbstractValidator;
import de.upb.crc901.wever.crcreal.serializer.bestavg.BestAvgStatsSerializer;
import de.upb.crc901.wever.crcrealexecutor.json.JsonReport;
import de.upb.wever.util.chunk.Chunk;
import de.upb.wever.util.rest.RESTHelper;

public class CrcREALExecutor {
	private static Logger LOGGER = LoggerFactory.getLogger(CrcREALExecutor.class);

	private final REALManager manager;
	private AbstractControl controller;

	private CrcREALExecutor() {
		this.manager = new REALManager();

		final BestAvgStatsSerializer stats = new BestAvgStatsSerializer();
		this.manager.registerListenerToEventBus(stats);
	}

	public void listEntities() {
		System.out.println("[Learner]");
		for (final AbstractActiveLearner learner : this.manager.getLearnerRegistry().registeredEntities()) {
			System.out.println(learner.getIdentifier());
		}

		System.out.println("[Validator]");
		for (final AbstractValidator validator : this.manager.getValidatorRegistry().registeredEntities()) {
			System.out.println(validator.getIdentifier());
		}

		System.out.println("[Challenger]");
		for (final AbstractGenerator challenger : this.manager.getGeneratorRegistry().registeredEntities()) {
			System.out.println(challenger.getIdentifier());
		}
	}

	public void startWithChunkController(final Chunk pChunk, final boolean sendResults) {
		this.controller = new ChunkControl(pChunk);
		this.manager.registerControl(this.controller);
		LOGGER.debug("Registered chunk control");
		this.controller.run();
		LOGGER.debug("Controller run has finished");
		if (sendResults) {
			this.sendTaskResultsToCentralServer(pChunk);
		}
	}

	public void startWithArrayController() {
		this.controller = new ArrayControl();
		this.manager.registerControl(this.controller);
		this.controller.run();
	}

	private void sendTaskResultsToCentralServer(final Chunk pChunk) {
		LOGGER.debug("Create REST Helper");
		final RESTHelper rest = new RESTHelper();
		LOGGER.debug("Send task results to central server");
		final JsonReport report = new JsonReport(pChunk);
		LOGGER.debug("Created json report, now send data to central server");
		rest.sendJsonReport(report.toJson());
		LOGGER.debug("Data sent, now shutdown dispatcher");
		rest.shutdownDispatcher();
	}

	public static void main(final String[] args) {
		if (args.length < 1) {
			printCorrectUsage();
			System.exit(0);
		}

		final CrcREALExecutor worker = new CrcREALExecutor();

		switch (args[0]) {
		case "chunk":
			if (args.length < 2) {
				printCorrectUsage();
				System.exit(0);
			}
			boolean sendResults = true;
			if (args.length >= 3) {
				if (args[2].equals("--no-report")) {
					sendResults = false;
				}
			}
			worker.startWithChunkController(Chunk.readFrom(args[1]), sendResults);
			break;
		case "array":
			worker.startWithArrayController();
			break;
		case "ls":
			worker.listEntities();
			break;
		case "sendResults":
			if (args.length < 2) {
				printCorrectUsage();
				System.exit(0);
			}
			worker.sendTaskResultsToCentralServer(Chunk.readFrom(args[1]));
			break;
		default:
			System.out.println("Unknown controller selected.");
			printCorrectUsage();
		}

	}

	private static void printCorrectUsage() {
		System.out.println("Correct usage: java -jar CrcREALWorker.jar controller [workSetFile]");
		System.out.println("Examples:");
		System.out.println("For chunk control: java -jar CrcREALWorker.jar chunk myChunkFile.chunk");
		System.out.println("For array control: java -jar CrcREALWorker.jar array");
	}

}
