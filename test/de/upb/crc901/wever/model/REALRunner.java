package de.upb.crc901.wever.model;

import java.util.HashMap;
import java.util.Map;

import org.aeonbits.owner.ConfigCache;

import de.upb.crc901.wever.crcreal.core.REALManager;
import de.upb.crc901.wever.crcreal.core.control.arraycontrol.ArrayControl;
import de.upb.crc901.wever.crcreal.serializer.bestavg.BestAvgStatsSerializerDB;
import jaicore.basic.SQLAdapter;
import jaicore.experiments.ExperimentDBEntry;
import jaicore.experiments.ExperimentRunner;
import jaicore.experiments.IExperimentIntermediateResultProcessor;
import jaicore.experiments.IExperimentSetConfig;
import jaicore.experiments.IExperimentSetEvaluator;

public class REALRunner implements IExperimentSetEvaluator {

	private static final ExperimentRunnerConfig CONFIG = ConfigCache.getOrCreate(ExperimentRunnerConfig.class);

	public static SQLAdapter ADAPTER;

	@Override
	public IExperimentSetConfig getConfig() {
		return CONFIG;
	}

	@Override
	public void evaluate(final ExperimentDBEntry experimentEntry, final SQLAdapter adapter,
			final IExperimentIntermediateResultProcessor processor) throws Exception {
		REALRunner.ADAPTER = adapter;
		Map<String, String> entries = experimentEntry.getExperiment().getValuesOfKeyFields();
		System.out.println(entries);
		CONFIG.setProperty(ExperimentRunnerConfig.RUN_ID, experimentEntry.getId() + "");
		CONFIG.setProperty(ExperimentRunnerConfig.SUPPLIER, entries.get("supplier"));
		CONFIG.setProperty(ExperimentRunnerConfig.SEED, entries.get("seed"));
		CONFIG.setProperty(ExperimentRunnerConfig.SUPPLIER_REQUEST_SET, entries.get("request"));
		CONFIG.setProperty(ExperimentRunnerConfig.ACC_MEASURE, entries.get("accmeasure"));

		REALManager realMan = new REALManager();
		ArrayControl control = new ArrayControl();
		realMan.registerControl(control);
		realMan.registerListenerToEventBus(new BestAvgStatsSerializerDB(adapter));
		control.run();

		Map<String, Object> results = new HashMap<>();
		results.put("testAccuracy", "0.0");
		processor.processResults(results);

	}

	public static void main(final String[] args) throws Exception {
		/* create and start experiment runner */
		ExperimentRunner runner = new ExperimentRunner(new REALRunner());
		System.out.println("Get Experiment");
		runner.randomlyConductExperiments(1, false);
		System.out.println("Done");
	}
}
