package crc.real.client;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class CrcREALClient {

	private boolean exit = false;
	private WorkerStarterThread workerThread = null;

	public CrcREALClient() {
		try (Scanner consoleInput = new Scanner(System.in)) {
			while (!this.exit) {
				final String command = consoleInput.nextLine();
				switch (command) {
				case "exit":
					if (this.workerThread != null) {
						this.workerThread.shutdown();
					}
					this.exit = true;
					break;
				case "run":
					if (this.workerThread == null || !this.workerThread.isAlive()) {
						this.workerThread = new WorkerStarterThread();
						this.workerThread.start();
					} else {
						System.out.println("Worker thread is already runnning.");
					}
					break;
				case "stop":
					if (this.workerThread != null && this.workerThread.isWorking()) {
						this.workerThread.shutdown();
					} else {
						System.out.println("Worker thread not running");
					}
					break;
				case "stop -f":
					if (this.workerThread != null) {
						this.workerThread.forceShutdown();
					}
				}
			}
			if (this.workerThread != null) {
				this.workerThread.join();
			}
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}

	}

	public static void main(final String[] args) {
		try (FileWriter writer = new FileWriter("chunkstate.txt")) {

		} catch (final IOException e) {
		}
		new CrcREALClient();
	}

}
