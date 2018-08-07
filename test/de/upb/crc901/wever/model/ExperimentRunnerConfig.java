package de.upb.crc901.wever.model;

import org.aeonbits.owner.Config.Sources;

import jaicore.experiments.IExperimentSetConfig;

@Sources({ "file:real.properties" })
public interface ExperimentRunnerConfig extends IExperimentSetConfig {

	public static final String SUPPLIER = "real.supplier";
	public static final String SEED = "real.seed";
	public static final String RUN_ID = "real.runID";
	public static final String SUPPLIER_REQUEST_SET = "real.supplierRequestSet";
	public static final String ACC_MEASURE = "real.accmeasure";

	@Key(SUPPLIER)
	public String supplier();

	@Key(SEED)
	public long seed();

	@Key(RUN_ID)
	public int runID();

	@Key(SUPPLIER_REQUEST_SET)
	public String requestSet();

	@Key(ACC_MEASURE)
	public String accMeasure();

}
