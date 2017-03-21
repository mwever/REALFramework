package de.upb.crc901.wever.crcreal.core;

import java.util.List;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

import de.upb.crc901.wever.crcreal.core.learner.objective.model.AllTestExampleObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.model.CountSinkStatesObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.model.ModelSizeObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.model.NegativeTestExampleObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.model.PositiveTestExampleObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.model.RelevantPartObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.test.DisagreementObjective;
import de.upb.crc901.wever.crcreal.core.learner.objective.test.MinTestLengthObjective;

import org.aeonbits.owner.Mutable;

@Sources({ "file:config/REALConfig.properties" })
public interface REALConfig extends Config, Mutable {

	public final static String KEY_PREFIX = "crc.real.";

	// parallelism configuraiton
	public static final String SEED = KEY_PREFIX + "basicconfig.seed";
	public static final String CORE_THREADS = KEY_PREFIX + "basicconfig.corethreads";
	public static final String ADDITIONAL_THREADS = KEY_PREFIX + "basicconfig.additionalthreads";

	// Algorithm config prefixes
	public static final String BL_PRE = KEY_PREFIX + "bl.";
	public static final String SBL_PRE = KEY_PREFIX + "sbl.";
	public static final String SVRH_PRE = KEY_PREFIX + "svrh.";
	public static final String AVRH_PRE = KEY_PREFIX + "avrh.";
	public static final String AVRH4_PRE = KEY_PREFIX + "avrh4.";

	// Algorithm config properties
	public static final String MODEL_OBJECTIVES = "modelObjectives";
	public static final String PMODEL_OBJECTIVES = "passiveModelObjectives";
	public static final String TEST_OBJECTIVES = "testObjectives";

	@Key(SEED)
	@DefaultValue("123456789")
	public long randomnessSeed();

	/* START Parallelism Configuraiton */
	@Key(CORE_THREADS)
	@DefaultValue("1")
	public int numberCoreThreads();

	@Key(ADDITIONAL_THREADS)
	@DefaultValue("0")
	public int numberAdditionalThreads();
	/* END Parallelism Configuraiton */

	@Key(BL_PRE + MODEL_OBJECTIVES)
	@DefaultValue(AllTestExampleObjective.ID + "," + RelevantPartObjective.ID)
	public List<String> blModelObjectives();

	@Key(BL_PRE + PMODEL_OBJECTIVES)
	@DefaultValue("")
	public List<String> blPassiveModelObjectives();

	@Key(BL_PRE + TEST_OBJECTIVES)
	@DefaultValue(DisagreementObjective.ID)
	public List<String> blTestObjectives();

	// //////////////////////////////////////////////////////////////////////////////////
	/* Active Van Rooijen Hamann V4 Algorithm Configuration */
	@Key(AVRH4_PRE + MODEL_OBJECTIVES)
	@DefaultValue(AllTestExampleObjective.ID + "," + ModelSizeObjective.ID)
	public List<String> avrh4ModelObjectives();

	@Key(AVRH4_PRE + PMODEL_OBJECTIVES)
	@DefaultValue("")
	public List<String> avrh4PassiveModelObjectives();

	@Key(AVRH4_PRE + TEST_OBJECTIVES)
	@DefaultValue(DisagreementObjective.ID + "," + MinTestLengthObjective.ID)
	public List<String> avrh4TestObjectives();

	// //////////////////////////////////////////////////////////////////////////////////
	/* Simulated Bongard Lipson (for Comparison only) */
	@Key(SBL_PRE + MODEL_OBJECTIVES)
	@DefaultValue(AllTestExampleObjective.ID)
	public List<String> sblModelObjectives();

	@Key(SBL_PRE + PMODEL_OBJECTIVES)
	@DefaultValue("")
	public List<String> sblPassiveModelObjectives();

	// //////////////////////////////////////////////////////////////////////////////////
	/* Simulated Van Rooijen Hamann (for Comparison only) */
	@Key(SVRH_PRE + MODEL_OBJECTIVES)
	@DefaultValue(PositiveTestExampleObjective.ID + "," + NegativeTestExampleObjective.ID + "," + RelevantPartObjective.ID + "," + CountSinkStatesObjective.ID)
	public List<String> svrhModelObjectives();

	@Key(SVRH_PRE + PMODEL_OBJECTIVES)
	@DefaultValue("")
	public List<String> svrhPassiveModelObjectives();

}
