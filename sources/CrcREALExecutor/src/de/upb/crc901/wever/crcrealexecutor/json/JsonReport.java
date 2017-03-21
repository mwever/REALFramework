package de.upb.crc901.wever.crcrealexecutor.json;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import de.upb.wever.util.chunk.Chunk;
import de.upb.wever.util.chunk.Task;

public class JsonReport {
	private static final String BASE_DIR = "evalresult/";

	private final List<JsonReportDatum> reportList;

	public JsonReport(final Chunk pChunk) {
		final String chunkDir = BASE_DIR + "chunk" + pChunk.getChunkID() + "/";
		this.reportList = this.readInReportData(chunkDir, pChunk);
	}

	private List<JsonReportDatum> readInReportData(final String pChunkDir, final Chunk pChunk) {
		final List<JsonReportDatum> resultList = new LinkedList<>();
		for (final Task task : pChunk) {
			resultList.addAll(this.readInTaskLog(pChunkDir, task));
		}
		return resultList;
	}

	private List<JsonReportDatum> readInTaskLog(final String pChunkDir, final Task task) {
		final List<JsonReportDatum> taskReportList = new LinkedList<>();

		try (final BufferedReader br = new BufferedReader(new FileReader(pChunkDir + "task" + task.getTaskID() + ".log"))) {
			String line;
			while ((line = br.readLine()) != null) {
				taskReportList.addAll(JsonReportDatum.readFromLog(task.getTaskID(), line));
			}
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return taskReportList;
	}

	public JsonReport(final List<JsonReportDatum> pReportList) {
		this.reportList = pReportList;
	}

	public JsonObjectBuilder toJson() {
		final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		for (final JsonReportDatum datum : this.reportList) {
			arrayBuilder.add(datum.toJson());
		}
		return Json.createObjectBuilder().add("resultArray", arrayBuilder);
	}

}
