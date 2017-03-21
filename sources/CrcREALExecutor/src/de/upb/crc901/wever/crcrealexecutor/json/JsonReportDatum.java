package de.upb.crc901.wever.crcrealexecutor.json;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import de.upb.wever.util.MapUtil;
import de.upb.wever.util.MathUtil;

public class JsonReportDatum {

	private final int experimentID;
	private final int generation;
	private final int round;

	private final double realBest;
	private final double realAvg;
	private final double heurBest;
	private final double heurAvg;

	private final String objectiveID;

	public JsonReportDatum(final int pExperimentID, final int pGeneration, final int pRound, final double realBest, final double realAvg, final double heurBest,
			final double heurAvg, final String pObjectiveID) {
		this.experimentID = pExperimentID;
		this.generation = pGeneration;
		this.round = pRound;

		this.realBest = realBest;
		this.realAvg = realAvg;
		this.heurBest = heurBest;
		this.heurAvg = heurAvg;

		this.objectiveID = pObjectiveID;
	}

	public int getExperimentID() {
		return this.experimentID;
	}

	public int getGeneration() {
		return this.generation;
	}

	public int getRound() {
		return this.round;
	}

	public double getRealBest() {
		return this.realBest;
	}

	public double getRealAvg() {
		return this.realAvg;
	}

	public double getHeurBest() {
		return this.heurBest;
	}

	public double getHeurAvg() {
		return this.heurAvg;
	}

	public String getObjectiveID() {
		return this.objectiveID;
	}

	public JsonObjectBuilder toJson() {
		return Json.createObjectBuilder().add("experimentID", this.experimentID).add("generation", this.generation).add("round", this.round).add("objectiveID", this.objectiveID)
				.add("realBest", MathUtil.round(this.realBest, 8)).add("realAvg", MathUtil.round(this.realAvg, 8)).add("heurBest", MathUtil.round(this.heurBest, 8))
				.add("heurAvg", MathUtil.round(this.heurAvg, 8));
	}

	public static List<JsonReportDatum> readFromLog(final int pExperimentID, final String pLogEntry) {
		final List<JsonReportDatum> resultDataList = new LinkedList<>();
		final String[] valuePairs = pLogEntry.split("&");

		int round = 0;
		int generation = 0;
		final Map<String, Map<String, Double>> objectiveValueMap = new HashMap<>();

		for (final String keyValue : valuePairs) {
			final String[] keyValueSplit = keyValue.split("=");

			switch (keyValueSplit[0]) {
			case "r":
				round = Integer.valueOf(keyValueSplit[1]);
				break;

			case "g":
				generation = Integer.valueOf(keyValueSplit[1]);
				break;
			default:
				if (keyValueSplit[0].startsWith("heur-") || keyValueSplit[0].startsWith("real-")) {
					final String[] objectiveSplit = keyValueSplit[0].split("-");
					final String[] objectiveValueSplit = keyValueSplit[1].split(";");
					MapUtil.safePutToMapInMap(objectiveValueMap, objectiveSplit[1], objectiveSplit[0] + "Best", Double.valueOf(objectiveValueSplit[0]));
					MapUtil.safePutToMapInMap(objectiveValueMap, objectiveSplit[1], objectiveSplit[0] + "Avg", Double.valueOf(objectiveValueSplit[1]));
				}
			}
		}

		final String[] valueTypes = { "realBest", "realAvg", "heurBest", "heurAvg" };
		final List<String> valueTypeList = Arrays.asList(valueTypes);

		for (final String objectiveID : objectiveValueMap.keySet()) {
			final double[] values = new double[valueTypeList.size()];
			for (final String valueType : valueTypeList) {
				values[valueTypeList.indexOf(valueType)] = getObjectiveValue(objectiveValueMap, objectiveID, valueType);

			}
			resultDataList.add(new JsonReportDatum(pExperimentID, generation, round, values[valueTypeList.indexOf("realBest")], values[valueTypeList.indexOf("realAvg")],
					values[valueTypeList.indexOf("heurBest")], values[valueTypeList.indexOf("heurAvg")], objectiveID));
		}

		return resultDataList;
	}

	private static double getObjectiveValue(final Map<String, Map<String, Double>> objectiveValueMap, final String pObjectiveID, final String valueType) {
		if (objectiveValueMap.containsKey(pObjectiveID)) {
			final Double value = objectiveValueMap.get(pObjectiveID).get(valueType);
			return (value == null) ? 0.0 : value;
		}
		return 0.0;
	}

}
