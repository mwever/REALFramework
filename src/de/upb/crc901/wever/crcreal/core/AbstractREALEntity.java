package de.upb.crc901.wever.crcreal.core;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import de.upb.crc901.wever.crcreal.model.events.TaskDefinitionEvent;
import de.upb.crc901.wever.crcreal.util.chunk.Task;
import de.upb.crc901.wever.crcreal.util.rand.IRandomGenerator;

public abstract class AbstractREALEntity {

	private final String identifier;
	private EventBus eventBus;
	private final IRandomGenerator prg;

	private Task task;

	private long startTime;

	protected AbstractREALEntity(final String pIdentifier, final IRandomGenerator pPRG) {
		this.identifier = pIdentifier;
		this.prg = pPRG;
	}

	@Subscribe
	public void rcvTaskDefinitionEvent(final TaskDefinitionEvent e) {
		this.task = e.getTask();
	}

	public void setEventBus(final EventBus pEventBus) {
		this.eventBus = pEventBus;
	}

	public String getIdentifier() {
		return this.identifier;
	}

	protected EventBus getEventBus() {
		return this.eventBus;
	}

	protected IRandomGenerator getPRG() {
		return this.prg;
	}

	protected Task getTask() {
		return this.task;
	}

	protected void startTimeMeasure() {
		this.startTime = System.currentTimeMillis();
	}

	protected long getTimeMeasure() {
		return (System.currentTimeMillis() - this.startTime);
	}
}
