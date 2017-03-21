package de.upb.crc901.wever.crcreal.core.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.upb.crc901.wever.crcreal.core.AbstractREALEntity;
import de.upb.crc901.wever.crcreal.util.rand.IRandomGenerator;
import de.upb.wever.util.MathUtil;

public abstract class AbstractControl extends AbstractREALEntity {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractControl.class);

	private int jobCounter;
	private int numberOfJobs;

	protected AbstractControl(final String pIdentifier) {
		super(pIdentifier, null);
	}

	@Override
	protected IRandomGenerator getPRG() {
		throw new UnsupportedOperationException("This method is not supported for controls.");
	}

	protected void setNumberOfJobs(final int pNumberOfJobs) {
		this.numberOfJobs = pNumberOfJobs;
	}

	protected void jobDone() {
		this.jobCounter++;
		this.printJobStatus();
	}

	protected double getPercentageStatus() {
		return MathUtil.round(((double) this.jobCounter / this.numberOfJobs) * 100, 2);
	}

	private void printJobStatus() {
		LOGGER.info("Completed job {} / {} in {}ms. Current status: {}%", this.jobCounter, this.numberOfJobs, this.getTimeMeasure(), this.getPercentageStatus());
	}

	public abstract void run();

}
