package com.fortytwotalents.fotogallery.service;

import com.fortytwotalents.fotogallery.model.CsvReadResult;
import com.fortytwotalents.fotogallery.model.GalleryEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class DirectoryStructureGeneratorService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryStructureGeneratorService.class);

	private static final String PORTRAITS_DIR = "portraits";

	public void generate(CsvReadResult csvReadResult, Path baseOutputPath) throws IOException {
		String eventName = csvReadResult.eventName();

		if (eventName.isBlank()) {
			throw new IllegalArgumentException("Event name is missing from the CSV file.");
		}

		Path eventDir = baseOutputPath.resolve(sanitize(eventName));
		Path portraitsDir = eventDir.resolve(PORTRAITS_DIR);

		Files.createDirectories(portraitsDir);
		LOGGER.info("Created directory: {}", portraitsDir.toAbsolutePath());

		for (GalleryEntry entry : csvReadResult.entries()) {
			Path idDir = portraitsDir.resolve(entry.code());
			Files.createDirectories(idDir);
			LOGGER.info("Created directory: {}", idDir.toAbsolutePath());
		}

		LOGGER.info("Directory structure generated under: {}", eventDir.toAbsolutePath());
	}

	private String sanitize(String name) {
		return name.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
	}

}
