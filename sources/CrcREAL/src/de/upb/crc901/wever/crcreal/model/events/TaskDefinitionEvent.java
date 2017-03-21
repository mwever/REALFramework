package de.upb.crc901.wever.crcreal.model.events;

import de.upb.wever.util.chunk.Task;

public class TaskDefinitionEvent {

	private final Task task;

	public TaskDefinitionEvent(final Task pTask) {
		this.task = pTask;
	}

	public Task getTask() {
		return this.task;
	}

}
