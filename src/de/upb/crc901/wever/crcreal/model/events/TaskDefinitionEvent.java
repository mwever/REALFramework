package de.upb.crc901.wever.crcreal.model.events;

import de.upb.crc901.wever.crcreal.util.chunk.REALTask;

public class TaskDefinitionEvent {

	private final REALTask task;

	public TaskDefinitionEvent(final REALTask pTask) {
		this.task = pTask;
	}

	public REALTask getTask() {
		return this.task;
	}

}
