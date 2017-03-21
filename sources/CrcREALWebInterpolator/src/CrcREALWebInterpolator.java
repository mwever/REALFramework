import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import de.upb.wever.util.MapUtil;
import de.upb.wever.util.MathUtil;

public class CrcREALWebInterpolator {

	private static final int NUMBER_SIMULTANEOUSLY_PROCESSED_EXPERIMENTS = 20;

	private static final String DB_HOST = "localhost";
	private static final String DB_PORT = "3306";
	private static final String DB_USER = "root";
	private static final String DB_PASS = "";
	private static final String DB_DATABASE = "crcreal";

	private static final long SLEEP_TIME = 1000 * 60 * 5;

	private class WorkerThread extends Thread {

		private Connection con = null;
		private boolean keepRunning = true;
		private final Semaphore waiter = new Semaphore(0);

		public WorkerThread() {
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

		public String getExperimentList(final List<ExperimentTbl> experimentIDs) {
			final StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (final ExperimentTbl eID : experimentIDs) {
				if (first) {
					first = false;
				} else {
					sb.append(",");
				}
				sb.append(eID.getID());
			}
			return sb.toString();
		}

		public boolean interpolateData() {
			long startTime;
			final long overallTime = System.currentTimeMillis();
			Statement query;
			try {
				System.out.print(new Date() + ": Retrieve experiments that have result data but have not been used yet...");
				query = this.con.createStatement();
				startTime = System.currentTimeMillis();
				final String unusedExperimentsQuery = "SELECT * FROM experiment WHERE instanceSetupID=11 AND used=0 AND resultdata=1";
				final ResultSet unusedExperimentsRes = query.executeQuery(unusedExperimentsQuery);
				final List<ExperimentTbl> experimentIDs = new LinkedList<>();
				while (unusedExperimentsRes.next()) {
					experimentIDs.add(
							new ExperimentTbl(unusedExperimentsRes.getString("id"), unusedExperimentsRes.getString("setupID"), unusedExperimentsRes.getString("instanceSetupID")));
				}
				query.close();
				System.out.println((System.currentTimeMillis() - startTime) + "ms...DONE");
				System.out.println("Experiments : " + experimentIDs.size());

				if (experimentIDs.isEmpty()) {
					return false;
				}

				System.out.print(new Date() + ": Read experiment result data...");
				startTime = System.currentTimeMillis();
				final String allExperimentResultQuery = "SELECT * FROM experimentresults2 WHERE experimentID IN (" + this.getExperimentList(experimentIDs) + ")";
				query = this.con.createStatement();
				final ResultSet experimentresultRes = query.executeQuery(allExperimentResultQuery);

				final String[] values = { "realbest", "realavg", "heurbest", "heuravg" };
				final Map<DataMapKey, Map<String, List<Double>>> dataMap = new HashMap<>();
				while (experimentresultRes.next()) {
					final ExperimentTbl experiment = this.getExperimentByID(experimentresultRes.getString("experimentID"), experimentIDs);
					if (experiment == null) {
						continue;
					}

					for (final String value : values) {
						final DataMapKey key = new DataMapKey(experiment.getSetupID(), experiment.getInstanceSetupID(), experimentresultRes.getString("round"),
								experimentresultRes.getString("generation"), experimentresultRes.getString("objectiveID"));
						Map<String, List<Double>> dataListMap = dataMap.get(key);
						if (dataListMap == null) {
							dataListMap = new HashMap<>();
							dataMap.put(key, dataListMap);
						}
						MapUtil.safeAddToListInMap(dataListMap, value, Double.valueOf(experimentresultRes.getString(value)));
					}
				}
				query.close();
				System.out.println((System.currentTimeMillis() - startTime) + "ms...DONE.");
				System.exit(0);

				System.out.print(new Date() + ": Interpolate and update data...");
				for (final DataMapKey key : dataMap.keySet()) {
					final Map<String, List<Double>> dataListMap = dataMap.get(key);

					startTime = System.currentTimeMillis();
					query = this.con.createStatement();
					final String checkForInterpolatedDataQuery = "SELECT * FROM interpolatedresults WHERE setupID=" + key.getSetupID() + " AND instanceSetupID="
							+ key.getInstanceSetupID() + " AND round=" + key.getRound() + " AND generation=" + key.getGeneration() + " AND objectiveID=" + key.getObjectiveID()
							+ " LIMIT 1";
					final ResultSet alreadyInterpolatedData = query.executeQuery(checkForInterpolatedDataQuery);
					System.out.print("Select took " + (System.currentTimeMillis() - startTime) + "ms...");

					if (!alreadyInterpolatedData.next()) {
						int numberOfSamples = 0;
						String insertInterpolatedDataQuery = "INSERT INTO interpolatedresults (setupID,instanceSetupID,round,generation,";
						for (final String valueType : values) {
							insertInterpolatedDataQuery += valueType + ",";
						}
						insertInterpolatedDataQuery += "samples,objectiveID) VALUES (" + key.getSetupID() + "," + key.getInstanceSetupID() + "," + key.getRound() + ","
								+ key.getGeneration() + ",";
						for (final String valueType : values) {
							try {
								insertInterpolatedDataQuery += "" + MathUtil.round(dataListMap.get(valueType).stream().mapToDouble(x -> (Double) x).average().getAsDouble(), 8);
								numberOfSamples = Math.max(numberOfSamples, dataListMap.get(valueType).size());
							} catch (final Exception e) {
								System.out.println("ups 1");
								insertInterpolatedDataQuery += "0.0";
							}
							insertInterpolatedDataQuery += ",";
						}
						insertInterpolatedDataQuery += numberOfSamples + "," + key.getObjectiveID() + ");";
						final Statement query2 = this.con.createStatement();
						query2.execute(insertInterpolatedDataQuery);
						query2.close();
					} else {
						String updateInterpolatedDataQuery = "UPDATE interpolatedresults SET ";
						int numberOfSamples = 0;
						for (final String valueType : values) {
							updateInterpolatedDataQuery += valueType + "=";
							try {
								updateInterpolatedDataQuery += "" + this.accumulateAverageValue(alreadyInterpolatedData.getString(valueType),
										alreadyInterpolatedData.getString("samples"), dataListMap.get(valueType));
								numberOfSamples = Math.max(numberOfSamples, Integer.valueOf(alreadyInterpolatedData.getString("samples")) + dataListMap.get(valueType).size());
							} catch (final Exception e) {
								System.out.println("ups 2");
								updateInterpolatedDataQuery += "0.0";
							}
							updateInterpolatedDataQuery += ",";
						}

						updateInterpolatedDataQuery += "samples=" + numberOfSamples + " WHERE setupID=" + key.getSetupID() + " AND instanceSetupID=" + key.getInstanceSetupID()
								+ " AND objectiveID=" + key.getObjectiveID() + " AND round=" + key.getRound() + " AND generation=" + key.getGeneration() + " LIMIT 1";

						final Statement query2 = this.con.createStatement();
						query2.execute(updateInterpolatedDataQuery);
						query2.close();
					}
				}
				query.close();
				System.out.println("DONE.");

				// Update experiments, set used to true
				System.out.print(new Date() + ": Update experiments to state that they have already been used...");
				startTime = System.currentTimeMillis();
				final String experimentUpdateQuery = "UPDATE experiment SET used=1 WHERE id IN (" + this.getExperimentList(experimentIDs) + ") LIMIT " + experimentIDs.size();
				query = this.con.createStatement();
				query.execute(experimentUpdateQuery);
				query.close();
				System.out.println((System.currentTimeMillis() - startTime) + "ms...DONE.");

				// Archive all the experiment results used
				System.out.print(new Date() + ": Archive experiment results...");
				startTime = System.currentTimeMillis();
				query = this.con.createStatement();
				final String archiveDataQuery = "INSERT resultsarchive SELECT * FROM experimentresults WHERE experimentID IN (" + this.getExperimentList(experimentIDs) + ")";
				query.execute(archiveDataQuery);
				query.close();
				System.out.println((System.currentTimeMillis() - startTime) + "ms...DONE.");

				System.out.print(new Date() + ": Remove used experiment results from original table...");
				startTime = System.currentTimeMillis();
				query = this.con.createStatement();
				final String removeUsedExperimentResultsQuery = "DELETE FROM experimentresults WHERE experimentID IN (" + this.getExperimentList(experimentIDs) + ")";
				query.execute(removeUsedExperimentResultsQuery);
				query.close();
				System.out.println((System.currentTimeMillis() - startTime) + "ms...DONE.");

			} catch (final SQLException e) {
				e.printStackTrace();
			}
			System.out.println("Overall time: " + (System.currentTimeMillis() - overallTime) + "ms");
			return true;
		}

		private double accumulateAverageValue(final String pInterpolationValue, final String numberOfSamples, final List<Double> newValues) {
			final Double currentAvg = Double.valueOf(pInterpolationValue);
			final int currentN = Integer.valueOf(numberOfSamples);
			return MathUtil.round((currentAvg * currentN + newValues.stream().mapToDouble(x -> x).sum()) / (currentN + newValues.size()), 8);
		}

		private ExperimentTbl getExperimentByID(final String seekedForID, final List<ExperimentTbl> experimentIDs) {
			for (final ExperimentTbl e : experimentIDs) {
				if (e.getID().equals(seekedForID)) {
					return e;
				}
			}
			return null;
		}

		public void shutdown() {
			this.keepRunning = false;
		}
	}

	private boolean keepMainRunning = true;
	private WorkerThread worker;

	public void run() {
		try (Scanner commandReader = new Scanner(System.in)) {
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
		final CrcREALWebInterpolator inter = new CrcREALWebInterpolator();
		inter.run();
	}
}
