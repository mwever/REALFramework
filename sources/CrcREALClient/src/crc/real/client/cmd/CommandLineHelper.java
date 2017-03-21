package crc.real.client.cmd;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CommandLineHelper {

	private static final boolean EXECUTE_LINUX_COMMANDS = false;

	public static void executeSendNotificationMail() {
		try {
			if (EXECUTE_LINUX_COMMANDS) {
				final Process p = Runtime.getRuntime().exec("./sendMail.sh");
				p.waitFor();
				p.destroy();
			}
		} catch (final InterruptedException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public static Process executeWorkerJar(final String chunkFile) {
		System.out.println("Execute jar with chunk");
		Process p = null;
		try (BufferedReader br = new BufferedReader(new FileReader("command.conf"))) {
			final String line = br.readLine();
			System.out.println(line + " chunk " + chunkFile);
			p = Runtime.getRuntime().exec(line + " chunk " + chunkFile);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return p;
	}

}
