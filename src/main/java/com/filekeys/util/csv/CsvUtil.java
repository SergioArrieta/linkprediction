package com.filekeys.util.csv;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class CsvUtil {

	private static final char FIELD_SEPARATOR = ';';
    public static final List<String> DEFAULT_HEADERS = List.of("Tecnica de LP","Threshold", "Recall", "Precision");

	/**
	 * Saves a list of the objects to a CSV file.
	 */
	public static void write(List<String[]> objectList, List<String> csvHeaders, String filePath) {

		try (CsvWriter writer = new CsvWriter(filePath)) {
			writer.setDelimiter(FIELD_SEPARATOR);
			writeHeader(writer, csvHeaders);
			writeCsv(writer, objectList);
			log.info("CSV file saved correctly to: '{}'", filePath);
		} catch (Exception e) {
			log.error("Error writing to CSV, path = {}", e);
		}	
	}

	public static List<String[]> read(String filePath) throws IOException {
		CsvReader reader = null;

		try {
			log.info("Loading data from file...");
			reader = new CsvReader(new FileReader(filePath), FIELD_SEPARATOR);

			return csvReaderToListOfStrings(reader);
			
		} finally {
			if (Objects.nonNull(reader)) {
				reader.close();
			}
		}
	}
	
	private static List<String[]> csvReaderToListOfStrings(CsvReader reader) throws IOException {
		List<String[]> rowList = new ArrayList<>();
		reader.readRecord(); // skips the header
		while (reader.readRecord()) {
			rowList.add(reader.getValues());
		}
		log.info("Loading data from file finished successfully. Total rows: {}", rowList.size());
		return rowList;
	}

	/**
	 * Writes the results of the query to the CSV file
	 */
	private static void writeCsv(CsvWriter writer, List<String[]> objectList) {

		objectList.forEach(row -> {
			try {
				// There are 11 columns of data
				for (int i = 0; i < row.length; i++) {
					writer.write(Objects.toString(row[i], ""));
				}
				writer.endRecord();
				writer.flush();
			} catch (IOException e) {
				log.error("There was a problem writing the information to the CSV file: {}", e);
			}
		});
	}

	/**
	 * Writes the headers to the CSV file
	 */
	private static void writeHeader(CsvWriter writer, List<String> csvHeaders) throws IOException {
		for (String header : csvHeaders) {
			writer.write(header);
		}
		writer.endRecord();
	}

}
