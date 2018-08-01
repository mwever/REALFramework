package de.upb.crc901.wever.crcreal.core.control.arraycontrol;

import java.util.stream.IntStream;

import org.aeonbits.owner.ConfigCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.upb.crc901.wever.crcreal.core.REALManager;
import de.upb.crc901.wever.crcreal.core.control.AbstractControl;
import de.upb.crc901.wever.crcreal.core.control.chunk.ChunkControl;
import de.upb.crc901.wever.crcreal.serializer.bestavg.BestAvgStatsSerializer;
import de.upb.crc901.wever.crcreal.util.chunk.Chunk;
import de.upb.crc901.wever.crcreal.util.chunk.EEvaluationCycle;
import de.upb.crc901.wever.crcreal.util.chunk.EEvaluationType;
import de.upb.crc901.wever.crcreal.util.chunk.TaskBuilder;

public class ArrayControl extends AbstractControl {
	private final static Logger LOGGER = LoggerFactory.getLogger(ArrayControl.class);
	private static final String ID = "crc.real.control.array";

	private final ArrayControlConfig config;
	private Chunk arrayChunk;

	public ArrayControl() {
		super(ID);
		this.config = ConfigCache.getOrCreate(ArrayControlConfig.class);
	}

	@Override
	public void run() {
		LOGGER.trace("Run array control controller");
		this.arrayChunk = this.createChunkFromConfig();
		final ChunkControl chunkControl = new ChunkControl(this.arrayChunk);
		chunkControl.setEventBus(this.getEventBus());
		chunkControl.run();
	}

	private Chunk createChunkFromConfig() {
		LOGGER.trace("Create chunk out from array control config");
		final Chunk chunkCompiledFromConfig = new Chunk();

		this.config.numberOfStates().stream().forEach(numberOfStates -> {
			this.config.sizeOfAlphabet().stream().forEach(sizeOfAlphabet -> {
				this.config.sizeOfTrainingSet().forEach(sizeOfTrainingSet -> {
					this.config.sizeOfPopulation().forEach(sizeOfPopulation -> {
						this.config.numberOfGenerations().forEach(numberOfGenerations -> {
							this.config.algorithms().stream().forEach(algorithmID -> {
								this.config.numberOfRounds().stream().forEach(numberOfRounds -> {
									this.config.suppliers().stream().forEach(supplier -> {
										IntStream.range(0, this.config.numberOfSamples()).forEach(sampleNumber -> {
											chunkCompiledFromConfig.add(TaskBuilder.getInstance()
													.setNumberOfGenerations(numberOfGenerations)
													.setSizeOfPopulation(sizeOfPopulation)
													.setSizeOfAlphabet(sizeOfAlphabet)
													.setSizeOfTrainingSet(sizeOfTrainingSet).setAlgorithmID(algorithmID)
													.setNumberOfRounds(numberOfRounds).setTaskID(sampleNumber)
													.setNumberOfStates(numberOfStates).setSupplierID(supplier)
													.setGeneratorID("crc.real.generator.randomUniformGenerator")
													.setValidatorID("crc.real.validator.explorationvalidator")
													.setEvaluationBound(this.config.evalBound())
													.setEvaluationCycle(EEvaluationCycle.ROUND)
													.setEvaluationType(EEvaluationType.ALL)
													.setMaxTestLength(this.config.maxTestLength())
													.setOracleID("crc.real.oracle.10honestoracle").toTask());
										});
									});
								});
							});
						});
					});
				});
			});
		});
		return chunkCompiledFromConfig;
	}

	public static void main(final String[] args) {
		System.out.println("Start real run");
		REALManager realMan = new REALManager();
		realMan.registerListenerToEventBus(new BestAvgStatsSerializer());

		ArrayControl ac = new ArrayControl();
		realMan.registerControl(ac);
		ac.run();
	}

}
