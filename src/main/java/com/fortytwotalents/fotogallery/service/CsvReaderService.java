package com.fortytwotalents.fotogallery.service;

import com.fortytwotalents.fotogallery.model.CsvReadResult;
import com.fortytwotalents.fotogallery.model.GalleryEntry;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvReaderService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CsvReaderService.class);

	public CsvReadResult readEntries(Path csvFile) throws IOException {
		if (!Files.exists(csvFile)) {
			throw new IOException("CSV file not found: " + csvFile.toAbsolutePath());
		}

		List<GalleryEntry> entries = new ArrayList<>();
		String eventName = "";

		CSVFormat format = CSVFormat.DEFAULT.builder()
			.setHeader()
			.setSkipHeaderRecord(true)
			.setIgnoreEmptyLines(true)
			.setTrim(true)
			.get();

		try (Reader reader = Files.newBufferedReader(csvFile, StandardCharsets.UTF_8);
				CSVParser parser = format.parse(reader)) {

			for (CSVRecord record : parser) {
				if (record.size() == 0) {
					continue;
				}

				String numberStr = record.get(0).trim();
				// Strip BOM if present on first record
				if (numberStr.startsWith("\uFEFF")) {
					numberStr = numberStr.substring(1);
				}

				if (numberStr.isBlank()) {
					continue;
				}

				int number;
				try {
					number = Integer.parseInt(numberStr);
				}
				catch (NumberFormatException e) {
					LOGGER.warn("Skipping record with invalid number at line {}: '{}'", record.getRecordNumber(),
							numberStr);
					continue;
				}

				String code = record.size() > 1 ? record.get(1).trim() : "";
				String recordEventName = record.size() > 3 ? record.get(3).trim() : "";

				if (eventName.isEmpty() && !recordEventName.isEmpty()) {
					eventName = recordEventName;
				}

				entries.add(new GalleryEntry(number, code, recordEventName));
			}
		}

		LOGGER.info("Read {} gallery entries from {}", entries.size(), csvFile);
		return new CsvReadResult(entries, eventName);
	}

}
