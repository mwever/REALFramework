package crc.real.client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;

import crc.real.client.cmd.CommandLineHelper;
import de.upb.wever.util.chunk.Chunk;
import de.upb.wever.util.rest.RESTHelper;

public class WorkerStarterThread extends Thread {

	private static final long SLEEP_TIME = 1000 * 60 * 2;
	private static final long PROCESS_TIMEOUT = 1000 * 60 * 60 * 2;

	private final RESTHelper rest;
	private boolean keepRunning = true;
	private Chunk currentChunk;
	private ProcessState processState;
	private Process workerProcess;

	private String chunkStatus = "";
	private long lastChunkStatusSent = -1;

	private enum ProcessState {
		READY, RUNNING, DONE;
	}

	public WorkerStarterThread() {
		super();
		this.rest = new RESTHelper();
		this.processState = ProcessState.READY;
		this.lastChunkStatusSent = System.currentTimeMillis();
	}

	public void shutdown() {
		this.keepRunning = false;
	}

	public void forceShutdown() {
		this.keepRunning = false;
		this.workerProcess.destroyForcibly();
	}

	public boolean isWorking() {
		return this.keepRunning;
	}

	@Override
	public void run() {
		while (this.keepRunning || this.processState == ProcessState.RUNNING) {
			try {
				System.out.println(new Date().toString() + ": Send heartbeat");
				this.rest.sendHeartbeat();
				this.sendChunkStatus();

				switch (this.processState) {
				case READY:
					System.out.println(new Date().toString() + ": READY");
					switch (this.checkForNewChunk()) {
					case 1:
						this.startProcessExecution();
						break;
					case 0:
						System.out.println(new Date().toString() + ": Error in confirmation");
						continue;
					default:
						break;
					}
					break;
				case DONE:
					System.out.println(new Date().toString() + ": Processing of Chunk #" + this.currentChunk.getChunkID() + " completed.");
					this.rest.sendChunkDone(this.currentChunk.getChunkID());
					this.processState = ProcessState.READY;
					break;
				case RUNNING:
					System.out.println(new Date().toString() + ": RUNNING");
					this.updateProcessState();
					break;
				}
				Thread.sleep(SLEEP_TIME);

			} catch (final IOException e) {
				e.printStackTrace();
			} catch (final ParseException e) {
				e.printStackTrace();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.rest.shutdownDispatcher();
		System.out.println(new Date().toString() + ": Worker Thread terminated.");
	}

	private void startProcessExecution() {
		try (final FileWriter writer = new FileWriter("chunk." + this.currentChunk.getChunkID())) {
			writer.write(this.currentChunk.getTextualRepresentation());
		} catch (final IOException e) {
			e.printStackTrace();
		}

		System.out.println(new Date().toString() + ": Start processing of Chunk #" + this.currentChunk.getChunkID());
		this.workerProcess = CommandLineHelper.executeWorkerJar("chunk." + this.currentChunk.getChunkID());
		this.processState = ProcessState.RUNNING;
	}

	private void updateProcessState() {
		if (!this.workerProcess.isAlive() && this.processState == ProcessState.RUNNING) {
			this.processState = ProcessState.DONE;
		} else {
			System.out.println(new Date().toString() + ": Still running");
			if (System.currentTimeMillis() - this.lastChunkStatusSent > PROCESS_TIMEOUT) {
				System.out.println(new Date().toString() + ": Process timed out. Something seems to be gone wrong.");
				this.workerProcess.destroy();
				this.processState = ProcessState.READY;
			}
		}
	}

	private void sendChunkStatus() {
		try (BufferedReader br = new BufferedReader(new FileReader("chunkstate.txt"))) {
			final String stateLine = br.readLine();
			if (stateLine != null && !stateLine.equals(this.chunkStatus)) {
				final String[] lineSplit = stateLine.split("&");
				int chunkID = 0;
				String state = "";

				for (final String keyValuePair : lineSplit) {
					final String[] keyValue = keyValuePair.split("=");
					switch (keyValue[0]) {
					case "chunkID":
						chunkID = Integer.valueOf(keyValue[1]);
						break;
					case "state":
						state = keyValue[1];
						break;
					}
				}

				System.out.println(new Date().toString() + ": Send chunk status");
				this.lastChunkStatusSent = System.currentTimeMillis();
				this.chunkStatus = stateLine;
				this.rest.sendChunkState(chunkID, state);
			}
		} catch (final FileNotFoundException e) {
			// no chunk state file yet
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private int checkForNewChunk() throws ClientProtocolException, IOException {
		String chunkData = this.rest.getNewChunk();
		switch (chunkData) {
		case "false":
			return -1;
		case "error":
			return 0;
		default:
			chunkData = chunkData.substring(1, chunkData.length() - 1);
			final Chunk receivedChunk = Chunk.readFromString(chunkData.replaceAll("#", "\n"));
			if (this.rest.sendChunkConfirmation(receivedChunk.getChunkID()).endsWith("true")) {
				this.currentChunk = receivedChunk;
				System.out.println(new Date().toString() + ": Received new chunk with ID " + this.currentChunk.getChunkID());
				return 1;
			} else {
				return 0;
			}
		}
	}

}
