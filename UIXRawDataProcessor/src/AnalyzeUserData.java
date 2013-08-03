/*
 * @author: Sahithi Reddigari
 * MSc Cognitive and Decision Sciences
 * Fall 2013
 * 
 * */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class AnalyzeUserData {

	private static final String DEVICE_2 = "mobile";
	private static final String DEVICE_1 = "prototype";
	private static final String HIGH_RISK_FAST = "HighRiskFast";
	private static final Object LOW_RISK_FAST = "LowRiskFast";
	private static final Object HIGH_RISK_ACCURATE = "HighRiskAccurate";
	private static final Object LOW_RISK_ACCURATE = "LowRiskAccurate";

	private static List HRF_expected = null;
	private static List LRF_expected = null;
	private static List HRA_expcted = null;
	private static List LRA_expected = null;

	private static final String DATA_PATH = "C:\\Users\\Sahithi\\Google Drive\\UCL\\Thesis Project\\data\\user_interaction_tests\\";

	private static final String EXPECTED_DIR = DATA_PATH + "expected\\";

	private static final String[] folders = { "LowRiskFast\\",
			"HighRiskFast\\", "LowRiskAccurate\\", "HighRiskAccurate\\" };
	private static final double TWO_DIG_ERROR_MARGIN = 4;
	private static final double THREE_DIG_ERROR_MARGIN = 4;

	private static List<String> twoDig;
	private static List<String> threeDig;

	public static void main(String[] args) throws IOException {

		processRawData();

		//reformatFileForRepeatedMeasuresTesting();
	}

	private static void processRawData() throws IOException,
			FileNotFoundException {
		cleanDiffDirectory(DATA_PATH + "\\actual\\time-diff\\");

		findTimeDiffsFromRawDataFiles(DEVICE_1);
		findTimeDiffsFromRawDataFiles(DEVICE_2);

		calculateAverageDosageEntryTimes();
	}

	private static List initializeExpectedValues(String filename)
			throws IOException {

		List prescriptions = new ArrayList();

		BufferedReader inputReader = new BufferedReader(new FileReader(
				EXPECTED_DIR + filename));

		String nextLine = null;
		while ((nextLine = inputReader.readLine()) != null) {
			StringTokenizer str = new StringTokenizer(nextLine, ",");
			String volume = str.nextToken();
			String rate = str.nextToken();
			String time = str.nextToken();
			PrescriptionScenario rx = new PrescriptionScenario(
					Float.parseFloat(volume), Float.parseFloat(rate),
					Integer.parseInt(time));

			prescriptions.add(rx);
		}

		return prescriptions;

	}

	private static void calculateAverageDosageEntryTimes()
			throws IOException {
		File folder_prototype;
		boolean type = false;

		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
				DATA_PATH + "AverageTimes.csv")));

		String QUALIFIED_PATH = DATA_PATH + "actual\\time-diff\\";

		folder_prototype = new File(QUALIFIED_PATH);
		File[] listOfFiles = folder_prototype.listFiles();
		Arrays.sort(listOfFiles);

		for (int i = 0; i < listOfFiles.length; i++) {
			// for each csv file in the folder

			if (listOfFiles[i].isFile()
					&& listOfFiles[i].getName().startsWith("TimeDiff")) {

				String filename = listOfFiles[i].getName();
				String condition = findConditionFromFilename(filename);

				RandomAccessFile f = new RandomAccessFile(QUALIFIED_PATH
						+ filename, "r");
				BufferedReader inputReader = new BufferedReader(new FileReader(
						QUALIFIED_PATH + filename));

				// read the file contents into a String:
				String nextLine;

				List<String> twoDig = new ArrayList();
				List<String> threeDig = new ArrayList();

				List<String> times = new ArrayList<String>();
				while ((nextLine = inputReader.readLine()) != null) {

					StringTokenizer tok = new StringTokenizer(nextLine, ",");

					String timeInterval = tok.nextToken();
					String number = tok.nextToken();

					if (!number.contains(".")) {
						if (number.trim().length() == 3) {
							threeDig.add(timeInterval);
						} else if (number.trim().length() == 2) {
							twoDig.add(timeInterval);
						}
					}

				}

				float avgTime2 = getTotal(twoDig,TWO_DIG_ERROR_MARGIN);
				float avgTime3 = getTotal(threeDig,THREE_DIG_ERROR_MARGIN);

				reset();

				String[] info = extractPrescriptionInfoFromFilename(filename);

				writer.write(info[0] + "," + info[2] + "," + info[1] + ",");
				writer.write(Float.toString(avgTime2) + ",");
				writer.write(Float.toString(avgTime3));
				writer.newLine();

			}

		}
		writer.close();

	}

	private static String findConditionFromFilename(String filename) {
		StringTokenizer str = new StringTokenizer(filename, "-");

		str.nextToken();
		str.nextToken();

		return str.nextToken();
	}

	private static String[] extractPrescriptionInfoFromFilename(String filename) {
		String[] info = new String[3];

		StringTokenizer tok1 = new StringTokenizer(filename);
		String firstPart = tok1.nextToken();
		String[] firstString = firstPart.split("-");
		
		String secondPart = tok1.nextToken();

		StringTokenizer tok2 = new StringTokenizer(secondPart, "-");
		String partId = tok2.nextToken();

		String thirdPart = tok2.nextToken();

		StringTokenizer tok3 = new StringTokenizer(thirdPart, ".");
		String condition = tok3.nextToken();

		info[0] = partId;
		info[1] = condition;
		info[2] = firstString[1];
		
		return info;
	}

	private static void reset() {

		twoDig = new ArrayList();
		threeDig = new ArrayList();
	}

	private static float getTotal(List<String> times, double error_margin) {

		Iterator<String> itr = times.iterator();
		int size = 0;
		float total = 0;
		while (itr.hasNext()) {
			String num = itr.next();
			float float_number = Float.parseFloat(num);
			if (float_number < error_margin) {
				total += float_number;
				size++;
			}
		}

		return size > 0 ? (total / size) : 0;
	}

	private static void reformatFileForRepeatedMeasuresTesting()
			throws IOException {

		BufferedReader reader = new BufferedReader(new FileReader(DATA_PATH
				+ "AverageTimes.csv"));

		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
				DATA_PATH + "RepeatedMeasuresColumnData.csv")));
		writer.write("SubjectID,DeviceType,HRF_2_Dig,LRF_2_Dig,HRA_2_Dig,LRA_2_Dig,"
				+ "HRF_3_Dig,LRF_3_Dig,HRA_3_Dig,LRA_3_Dig");
		writer.newLine();

		int subject_index = 4;
		String newLine = "";
		RepeatedMeasuresRow row = new RepeatedMeasuresRow();
		String deviceType = null;
		while (null != (newLine = reader.readLine())) {
			StringTokenizer tok = new StringTokenizer(newLine, ",");

			int subjectId = Integer.parseInt(tok.nextToken());
			deviceType = tok.nextToken();
			String condition = tok.nextToken();
			float twoDig = Float.parseFloat(tok.nextToken());
			float threeDig = Float.parseFloat(tok.nextToken());

			if (subjectId == subject_index) {
				assign2and3DigitColumnValue(row, condition, twoDig, threeDig);

			} else {

				writer.write(subject_index + "," + deviceType + ","
						+ row.getHRF_Val_2() + "," + row.getLRF_Val_2() + ","
						+ row.getHRA_Val_2() + "," + row.getLRA_Val_2() + ","
						+ row.getHRF_Val_3() + "," + row.getLRF_Val_3() + ","
						+ row.getHRA_Val_3() + "," + row.getLRA_Val_3());
				writer.newLine();

				row = new RepeatedMeasuresRow();
				assign2and3DigitColumnValue(row, condition, twoDig, threeDig);
				subject_index = subjectId;

			}

		}

		writer.write(subject_index + "," + deviceType + ","
				+ row.getHRF_Val_2() + "," + row.getLRF_Val_2() + ","
				+ row.getHRA_Val_2() + "," + row.getLRA_Val_2() + ","
				+ row.getHRF_Val_3() + "," + row.getLRF_Val_3() + ","
				+ row.getHRA_Val_3() + "," + row.getLRA_Val_3());

		writer.close();
	}

	private static void assign2and3DigitColumnValue(RepeatedMeasuresRow row,
			String condition, float twoDig, float threeDig) {
		if (condition.trim().equals("HighRiskAccurate")) {
			row.setHRA_Val_2(twoDig);
			row.setHRA_Val_3(threeDig);
		} else if (condition.trim().equals("HighRiskFast")) {
			row.setHRF_Val_2(twoDig);
			row.setHRF_Val_3(threeDig);
		} else if (condition.trim().equals("LowRiskAccurate")) {
			row.setLRA_Val_2(twoDig);
			row.setLRA_Val_3(threeDig);
		} else if (condition.trim().equals("LowRiskFast")) {
			row.setLRF_Val_2(twoDig);
			row.setLRF_Val_3(threeDig);
		}
	}

	private static void findErrorsFromTimeDiffs(String pathname,
			String deviceType) throws IOException {
		File folder_name;
		boolean type = false;

		BufferedWriter diffWriter = new BufferedWriter(new FileWriter(new File(
				pathname + "\\Errors-" + deviceType + ".csv")));

		for (int j = 0; j < folders.length; j++) {
			// for each folder in the directory

			String QUALIFIED_PATH = pathname + folders[j];
			folder_name = new File(QUALIFIED_PATH + "\\diff\\");
			File[] listOfFiles = folder_name.listFiles();
			Arrays.sort(listOfFiles);

			for (int i = 0; i < listOfFiles.length; i++) {
				// for each diff file in the folder

				if (listOfFiles[i].isFile()
						&& listOfFiles[i].getName().startsWith("TimeDiff")) {

					String filename = listOfFiles[i].getName();

					BufferedReader inputReader = new BufferedReader(
							new FileReader(QUALIFIED_PATH + filename));

					String newLine = null;

					String[] info = extractPrescriptionInfoFromFilename(filename);

					String participantId = info[0];
					String condition = info[1];
					int entryIndex = 1;

					List actual_HRF = new ArrayList();
					List actual_LRF = new ArrayList();
					List actual_HRA = new ArrayList();
					List actual_LRA = new ArrayList();

					while (null != (newLine = inputReader.readLine())) {
						StringTokenizer tok = new StringTokenizer(newLine, ",");

						float time = Float.parseFloat(tok.nextToken());
						float amount = Float.parseFloat(tok.nextToken());
						int numEntries = Integer.parseInt(tok.nextToken());

						List expectedList = null;
						List actualList = null;

						if (condition.equals(Condition.HighRiskFast)) {
							expectedList = HRF_expected;
							actualList = actual_HRF;
						} else if (condition.equals(Condition.HighRiskAccurate)) {
							expectedList = HRA_expcted;
							actualList = actual_HRA;
						} else if (condition.equals(Condition.LowRiskFast)) {
							expectedList = LRF_expected;
							actualList = actual_LRF;
						} else if (condition.equals(Condition.LowRiskAccurate)) {
							expectedList = LRA_expected;
							actualList = actual_LRA;
						}

						if (numEntries == 2) {
							if (entryIndex == 2) {
								entryIndex = 1;
							} else {
								entryIndex++;
							}

						} else { // if 3 values
							if (entryIndex == 3) {
								entryIndex = 1;
							} else {
								entryIndex++;
							}
						}

					}

					// System.out.println(participantId + " " + condition + " "+
					// deviceType);

				}
			}
		}

		diffWriter.close();

	}

	private static void findTimeDiffsFromRawDataFiles(String deviceType)
			throws FileNotFoundException, IOException {
		File folder_prototype;
		boolean type = false;

		for (int j = 0; j < folders.length; j++) {
			// for each folder in the directory
			String pathname = DATA_PATH + "actual\\" + deviceType + "\\valid\\";

			String QUALIFIED_PATH = pathname + folders[j];
			
			folder_prototype = new File(QUALIFIED_PATH);
			File[] listOfFiles = folder_prototype.listFiles();
			Arrays.sort(listOfFiles);

			for (int i = 0; i < listOfFiles.length; i++) {
				// for each csv file in the folder

				if (listOfFiles[i].isFile()
						&& listOfFiles[i].getName().startsWith("results")) {

					String filename = listOfFiles[i].getName();
					BufferedWriter diffWriter = new BufferedWriter(
							new FileWriter(new File(DATA_PATH
									+ "actual\\time-diff\\" + "TimeDiff-"
									+ deviceType + "-" + filename)));

					RandomAccessFile f = new RandomAccessFile(QUALIFIED_PATH
							+ filename, "r");
					BufferedReader inputReader = new BufferedReader(
							new FileReader(QUALIFIED_PATH + filename));
					// System.out.println(QUALIFIED_PATH+filename);
					// read the file contents into a String:
					String nextLine;

					String previousFieldType = "#vtbi";

					List allTimes = new ArrayList();
					List<String> times = new ArrayList<String>();
					while ((nextLine = inputReader.readLine()) != null) {

						if ((nextLine.contains("#vtbi")
								|| nextLine.contains("#time") || nextLine
									.contains("#rate"))) {
							StringTokenizer tok = new StringTokenizer(nextLine,
									",");

							String drugName = tok.nextToken();
							String numVals = tok.nextToken();
							String currentFieldType = tok.nextToken();
							String keyPress = tok.nextToken();

							String currentNumber = tok.nextToken();
							String timing = tok.nextToken();

							if (previousFieldType.equals(currentFieldType)
									&& !currentNumber.contains(".")
									&& isNum(keyPress)) {

								times.add(timing + "," + currentNumber + ","
										+ currentFieldType);

							} else if (!previousFieldType
									.equals(currentFieldType)
									|| keyPress.equals("Enter")) { // start
																	// fresh
								previousFieldType = currentFieldType;

								if (times.size() > 0) {

									String first = times.get(0);
									String last = times.get(times.size() - 1);

									StringTokenizer firstTok = new StringTokenizer(
											first, ",");
									String first_time = firstTok.nextToken();

									StringTokenizer secondTok = new StringTokenizer(
											last, ",");
									String second_time = secondTok.nextToken();
									String doseEntered = secondTok.nextToken();

									float time1 = Float.parseFloat(first_time);
									float time2 = Float.parseFloat(second_time);

									if (!doseEntered.contains(".")) {

										diffWriter.write(time2 - time1 + ","
												+ doseEntered + "," + numVals);
										diffWriter.newLine();
									}

									times = new ArrayList<String>();
									// System.out.println("---------------------");
									if (!keyPress.trim().equals("Enter"))
										times.add(timing + "," + currentNumber
												+ "," + currentFieldType);

								}

							}

						}

					}
					diffWriter.close();
				}

			}

		}

	}

	private static boolean isNum(String keyPress) {

		try {
			Integer.parseInt(keyPress);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private static void cleanDiffDirectory(String QUALIFIED_PATH)
			throws IOException {
		File diff_directory = new File(QUALIFIED_PATH);
		if (!diff_directory.exists())
			diff_directory.mkdir();
		for (File file : (diff_directory.listFiles()))
			if (file.getName().startsWith("TimeDiff"))
				file.delete();
	}
}
