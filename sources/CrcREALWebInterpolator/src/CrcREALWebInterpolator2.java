import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import de.upb.wever.util.MapUtil;
import de.upb.wever.util.MathUtil;

public class CrcREALWebInterpolator2 {

	private static final boolean DEV_MODE = false;

	private static final String DB_HOST = "localhost";
	private static final String DB_PORT = "3306";
	private static String DB_USER = "crcreal";
	private static String DB_PASS = "crcrealwever2016";
	private static final String DB_DATABASE = "crcreal";

	private static final long SLEEP_TIME = 1000 * 60 * 5;

	private class WorkerThread extends Thread {

		private Connection con = null;
		private boolean keepRunning = true;
		private final Semaphore waiter = new Semaphore(0);

		public WorkerThread() {
			if (DEV_MODE) {
				DB_USER = "root";
				DB_PASS = "";
			}

			try {
				Class.forName("com.mysql.jdbc.Driver");
				this.con = DriverManager.getConnection("jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_DATABASE + "?user=" + DB_USER + "&password=" + DB_PASS);
			} catch (final ClassNotFoundException e) {
				System.out.println("Driver could not be found");
			} catch (final SQLException e) {
				System.out.println("Verbindung nicht moglich");
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
			}
		}

		@Override
		public void run() {
			while (this.keepRunning) {
				if (!this.interpolateData()) {
					try {
						this.waiter.tryAcquire(SLEEP_TIME, TimeUnit.MILLISECONDS);
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			System.out.println(new Date() + ": Worker thread shut down.");
		}

		private void loadDataFromTableIntoMap(final String table, final Map<Integer, ExperimentTbl> experiments, final Map<DataMapKey, Map<String, List<Double>>> allDataMap,
				final List<Integer> experimentResultIDs) {
			long startTime;
			final long overallTime = System.currentTimeMillis();
			final int pageSize;
			if (DEV_MODE) {
				pageSize = 50000;
			} else {
				pageSize = 100000;
			}

			final String[] values = { "realbest", "realavg", "heurbest", "heuravg" };
			Statement query;
			try {
				System.out.print(new Date() + ": Read experiment result data...");
				startTime = System.currentTimeMillis();
				query = this.con.createStatement();
				final ResultSet experimentresultRes = query.executeQuery("SELECT COUNT(*) as c FROM " + table);
				experimentresultRes.next();
				final int numberOfExperimentResults = Integer.valueOf(experimentresultRes.getString("c"));
				query.close();

				final int pages = (int) Math.ceil(((double) numberOfExperimentResults / pageSize));

				System.out.println();
				System.out.println("Number of experiment results: " + numberOfExperimentResults + " Pages: " + pages + " Pagesize: " + pageSize);
				System.out.println((System.currentTimeMillis() - startTime) + "ms");

				startTime = System.currentTimeMillis();
				for (int currentPage = 1; currentPage <= pages; currentPage++) {
					final long startTime2 = System.currentTimeMillis();
					final int offset = (currentPage - 1) * pageSize;

					final String resultsPageQuery = "SELECT * FROM " + table + " ORDER BY id LIMIT " + offset + "," + pageSize + ";";
					query = this.con.createStatement();
					final ResultSet resultsPageRes = query.executeQuery(resultsPageQuery);

					while (resultsPageRes.next()) {
						final ExperimentTbl experiment = experiments.get(Integer.valueOf(resultsPageRes.getString("experimentID")));
						if (experiment == null) {
							continue;
						}

						experimentResultIDs.add(resultsPageRes.getInt("id"));
						for (final String value : values) {
							final DataMapKey key = new DataMapKey(experiment.getSetupID(), experiment.getInstanceSetupID(), resultsPageRes.getString("round"),
									resultsPageRes.getString("generation"), resultsPageRes.getString("objectiveID"));
							Map<String, List<Double>> dataListMap = allDataMap.get(key);
							if (dataListMap == null) {
								dataListMap = new HashMap<>();
								allDataMap.put(key, dataListMap);
							}
							MapUtil.safeAddToListInMap(dataListMap, value, Double.valueOf(resultsPageRes.getString(value)));
						}
					}

					System.out
							.println(currentPage + " / " + pages + " HashMap Size: " + allDataMap.size() + " " + (System.currentTimeMillis() - startTime2) + "ms needed for page.");
					query.close();

					if (offset + pageSize >= 100000) {
						break;
					}
				}

				System.out.println("Needed " + (System.currentTimeMillis() - startTime) + "ms to get all the stuff");
				System.out.println("Map contains now " + allDataMap.size() + " many keys");

			} catch (final SQLException e) {
				e.printStackTrace();
			}
			System.out.println("Overall time: " + (System.currentTimeMillis() - overallTime) + "ms");

		}

		private Map<DataMapKey, Map<String, AveragedValue>> interpolateDataMap(final Map<DataMapKey, Map<String, List<Double>>> allDataMap, final String[] values) {
			final Map<DataMapKey, Map<String, AveragedValue>> interpolatedDataMap = new HashMap<>();
			for (final DataMapKey key : allDataMap.keySet()) {
				final Map<String, List<Double>> dataListMap = allDataMap.get(key);

				final Map<String, AveragedValue> interpolatedData = new HashMap<>();
				for (final String valueType : values) {
					try {
						final double interpolatedValue = MathUtil.round(dataListMap.get(valueType).stream().mapToDouble(x -> (Double) x).average().getAsDouble(), 8);
						interpolatedData.put(valueType, new AveragedValue(dataListMap.get(valueType).size(), interpolatedValue));
					} catch (final Exception e) {
						System.out.println("ups 1");
						interpolatedData.put(valueType, new AveragedValue(0, 0.0));
					}
				}
				interpolatedDataMap.put(key, interpolatedData);
			}
			return interpolatedDataMap;
		}

		/**
		 * @return
		 */
		public boolean interpolateData() {
			long startTime;
			final long overallTime = System.currentTimeMillis();
			Statement query;
			try {
				System.out.print(new Date() + ": Retrieve experiments that have result data but have not been used yet...");
				query = this.con.createStatement();
				startTime = System.currentTimeMillis();
				final ResultSet experimentsRes = query.executeQuery("SELECT * FROM experiment");
				final Map<Integer, ExperimentTbl> experiments = new HashMap<>();
				final Map<Integer, Map<Integer, ExperimentTbl>> setupExperiments = new HashMap<>();
				while (experimentsRes.next()) {
					final ExperimentTbl tbl = new ExperimentTbl(experimentsRes.getString("id"), experimentsRes.getString("setupID"), experimentsRes.getString("instanceSetupID"));
					experiments.put(Integer.valueOf(experimentsRes.getString("id")), tbl);
					MapUtil.safePutToMapInMap(setupExperiments, Integer.valueOf(tbl.getInstanceSetupID()), Integer.valueOf(tbl.getSetupID()), tbl);
				}
				query.close();
				System.out.println((System.currentTimeMillis() - startTime) + "ms...DONE.");

				final Map<DataMapKey, Map<String, List<Double>>> dataBufferMap = new HashMap<>();
				final String[] values = { "realbest", "realavg", "heurbest", "heuravg" };

				final List<Integer> experimentResultIDs = new LinkedList<>();

				// XXX table
				final String resultsTable = "experimentresults";
				final String resultsArchiveTable = "experimentresultsarchive";
				final String interpolationTable = "interpolatedresults2";
				final String interpolatedresultsarchive = "interpolatedresultsarchive";

				System.out.println();
				System.out.print(new Date() + ": Load data from " + resultsTable + " table...");
				startTime = System.currentTimeMillis();
				this.loadDataFromTableIntoMap(resultsTable, experiments, dataBufferMap, experimentResultIDs);
				System.out.println(new Date() + ": ..." + (System.currentTimeMillis() - startTime) + "ms...DONE.");
				System.out.println();

				System.out.print(new Date() + ": Interpolate data...");
				startTime = System.currentTimeMillis();
				final Map<DataMapKey, Map<String, AveragedValue>> interpolatedValues = this.interpolateDataMap(dataBufferMap, values);
				dataBufferMap.clear();
				System.out.println((System.currentTimeMillis() - startTime) + "ms...DONE.");

				System.out.print(new Date() + ": Load already interpolated data...");
				startTime = System.currentTimeMillis();
				final Map<DataMapKey, Map<String, AveragedValue>> interpolatedValuesArchive = new HashMap<>();
				query = this.con.createStatement();
				final ResultSet alreadyInterpolatedRes = query.executeQuery("SELECT * FROM " + interpolationTable);
				while (alreadyInterpolatedRes.next()) {
					final ExperimentTbl exp = setupExperiments.get(alreadyInterpolatedRes.getInt("instanceSetupID")).get(alreadyInterpolatedRes.getInt("setupID"));
					final DataMapKey mapKey = new DataMapKey(exp.getSetupID(), exp.getInstanceSetupID(), alreadyInterpolatedRes.getString("round"),
							alreadyInterpolatedRes.getString("generation"), alreadyInterpolatedRes.getString("objectiveID"));

					final Map<String, AveragedValue> avgValueMap = new HashMap<>();
					for (final String valueType : values) {
						avgValueMap.put(valueType, new AveragedValue(alreadyInterpolatedRes.getInt("samples"), alreadyInterpolatedRes.getDouble(valueType)));
					}
					interpolatedValuesArchive.put(mapKey, avgValueMap);
				}
				query.close();
				System.out.println((System.currentTimeMillis() - startTime) + "ms...DONE.");

				// XXX Mixing already interpolated with new interpolated data
				System.out.print(new Date() + ": Interpolate new data into already interpolated data...");
				startTime = System.currentTimeMillis();
				final Map<DataMapKey, Map<String, AveragedValue>> interpolatedDataMap = new HashMap<>();

				final Set<DataMapKey> allDataKeys = new HashSet<>(interpolatedValues.keySet());
				allDataKeys.addAll(interpolatedValuesArchive.keySet());

				for (final DataMapKey key : allDataKeys) {
					final Map<String, AveragedValue> firstValues = interpolatedValues.get(key);
					final Map<String, AveragedValue> archiveValues = interpolatedValuesArchive.get(key);
					if (firstValues == null) {
						interpolatedDataMap.put(key, archiveValues);
					} else if (archiveValues == null) {
						interpolatedDataMap.put(key, firstValues);
					} else {
						final Map<String, AveragedValue> combinedMap = new HashMap<>();
						for (final String valueType : values) {
							final AveragedValue av = firstValues.get(valueType);
							final AveragedValue archiveValue = archiveValues.get(valueType);
							double interpolatedValue = av.getAverageValue() * av.getSamples() + archiveValue.getAverageValue() * archiveValue.getSamples();
							interpolatedValue /= (archiveValue.getSamples() + av.getSamples());
							interpolatedValue = MathUtil.round(interpolatedValue, 8);

							combinedMap.put(valueType, new AveragedValue(av.getSamples() + archiveValue.getSamples(), interpolatedValue));
							interpolatedDataMap.put(key, combinedMap);
						}
					}
				}
				dataBufferMap.clear();
				System.out.println((System.currentTimeMillis() - startTime) + "ms...DONE.");

				// XXX data is interpolated
				// Now move all the used experiment results to archive table (Insert and Delete)
				// Backup all the already interpolated data into archive interpolated results table
				// Insert freshly interpolated data into interpolated results table

				System.out.print(new Date() + ": Move used experimentresults to archive...");
				Collections.sort(experimentResultIDs);
				boolean range = true;
				for (int i = 0; i < experimentResultIDs.size() - 1; i++) {
					if (experimentResultIDs.get(i) + 1 != experimentResultIDs.get(i + 1)) {
						range = false;
						break;
					}
				}
				query = this.con.createStatement();
				final String transferQuery = "INSERT INTO " + resultsArchiveTable + " SELECT * FROM experimentresults WHERE ";
				String whereClause = "";
				if (range) {
					whereClause += "id>=" + experimentResultIDs.get(0) + " AND id<=" + experimentResultIDs.get(experimentResultIDs.size() - 1);
					System.out.print("RANGE (" + (System.currentTimeMillis() - startTime) + "ms)...");
				} else {
					startTime = System.currentTimeMillis();
					final StringBuilder sb = new StringBuilder();
					boolean first = true;
					for (final Integer id : experimentResultIDs) {
						if (first) {
							first = false;
						} else {
							sb.append(",");
						}
						sb.append(id);
					}
					whereClause += "id IN (" + sb.toString() + ")";
					System.out.print("IN ARRAY (" + (System.currentTimeMillis() - startTime) + "ms)...");
				}
				startTime = System.currentTimeMillis();
				query.executeUpdate(transferQuery + whereClause);
				query.close();
				System.out.println((System.currentTimeMillis() - startTime) + "ms...DONE.");

				System.out.print(new Date() + ": Clean experimentresults from archived values...");
				query = this.con.createStatement();
				final String deleteQuery = "DELETE FROM " + resultsTable + " WHERE " + whereClause;
				query.executeUpdate(deleteQuery);
				query.close();
				System.out.println("DONE.");

				final StringBuilder globalInsert = new StringBuilder();
				globalInsert.append("INSERT INTO " + interpolationTable + " (setupID,instanceSetupID,round,generation,");
				for (final String valueType : values) {
					globalInsert.append(valueType + ",");
				}
				globalInsert.append("samples,objectiveID) VALUES ");
				boolean doGlobalInsert = false;

				// XXX Backup interpolated data
				System.out.print(new Date() + ": Backup already interpolated data...");
				startTime = System.currentTimeMillis();

				long startTime2 = System.currentTimeMillis();
				query = this.con.createStatement();
				query.execute("TRUNCATE TABLE " + interpolatedresultsarchive);
				System.out.print("CLEANED ARCHIVE (" + (System.currentTimeMillis() - startTime2) + "ms)...");

				startTime2 = System.currentTimeMillis();
				query.execute("INSERT INTO " + interpolatedresultsarchive + " SELECT * FROM " + interpolationTable);
				System.out.print("MOVED (" + (System.currentTimeMillis() - startTime2) + "ms)...");
				System.out.println((System.currentTimeMillis() - startTime) + "ms...DONE.");
				// Backup done

				// XXX Insert fresh interpolated data
				System.out.print(new Date() + ": Collect INSERT query...");
				System.out.print(interpolatedDataMap.keySet().size() + " key...");
				startTime = System.currentTimeMillis();
				for (final DataMapKey key : interpolatedDataMap.keySet()) {
					final Map<String, AveragedValue> dataListMap = interpolatedDataMap.get(key);

					if (!doGlobalInsert) {
						doGlobalInsert = true;
					} else {
						globalInsert.append(",");
					}
					int numberOfSamples = 0;
					globalInsert.append("(" + key.getSetupID() + "," + key.getInstanceSetupID() + "," + key.getRound() + "," + key.getGeneration() + ",");
					for (final String valueType : values) {
						try {
							globalInsert.append(dataListMap.get(valueType).getAverageValue());
							globalInsert.append(",");
							numberOfSamples = Math.max(numberOfSamples, dataListMap.get(valueType).getSamples());
						} catch (final Exception e) {
							System.out.println("ups 1");
							globalInsert.append("0.0,");
						}
					}
					globalInsert.append(numberOfSamples + "," + key.getObjectiveID() + ")");

				}
				globalInsert.append(";");
				System.out.println((System.currentTimeMillis() - startTime) + "ms...DONE;");

				System.out.print("Execute insert on database...");
				startTime = System.currentTimeMillis();
				if (doGlobalInsert) {
					query = this.con.createStatement();
					query.execute("TRUNCATE TABLE " + interpolationTable);
					query.close();

					final Statement query2 = this.con.createStatement();
					query2.execute(globalInsert + ";");
					query2.close();
				}
				System.out.println((System.currentTimeMillis() - startTime) + "ms...DONE.");

			} catch (final SQLException e) {
				e.printStackTrace();
			}
			System.out.println("Overall time: " + (System.currentTimeMillis() - overallTime) + "ms");
			return true;
		}

		public void shutdown() {
			this.keepRunning = false;
		}
	}

	private boolean keepMainRunning = true;
	private WorkerThread worker;

	public void run() {
		try (Scanner commandReader = new Scanner(System.in)) {
			/* */
			while (this.keepMainRunning || (this.worker != null && this.worker.isAlive())) {
				final String command = commandReader.nextLine();

				switch (command) {
				case "exit":
					System.out.println(new Date() + ": Going to shutdown interpolator.");
					this.keepMainRunning = false;
					this.worker.shutdown();
					break;
				case "run":
					this.worker = new WorkerThread();
					this.worker.start();
					break;
				case "stop":
					this.worker.shutdown();
					break;
				}

			}
		}
		System.out.println("Interpolator shut down.");
	}

	public static void main(final String[] args) {
		final CrcREALWebInterpolator2 inter = new CrcREALWebInterpolator2();
		inter.run();
	}
}
