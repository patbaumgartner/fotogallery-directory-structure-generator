package com.fortytwotalents.fotogallery.runner;

import com.fortytwotalents.fotogallery.config.AppProperties;
import com.fortytwotalents.fotogallery.model.CsvReadResult;
import com.fortytwotalents.fotogallery.service.CsvReaderService;
import com.fortytwotalents.fotogallery.service.DirectoryStructureGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
public class DirectoryStructureRunner implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryStructureRunner.class);

	private final CsvReaderService csvReaderService;

	private final DirectoryStructureGeneratorService directoryStructureGeneratorService;

	private final AppProperties appProperties;

	public DirectoryStructureRunner(CsvReaderService csvReaderService,
			DirectoryStructureGeneratorService directoryStructureGeneratorService, AppProperties appProperties) {
		this.csvReaderService = csvReaderService;
		this.directoryStructureGeneratorService = directoryStructureGeneratorService;
		this.appProperties = appProperties;
	}

	@Override
	public void run(String... args) throws Exception {
		Path csvInputPath = Path.of(appProperties.csvInputPath());
		Path outputPath = Path.of(appProperties.outputPath());

		LOGGER.info("Reading gallery entries from: {}", csvInputPath.toAbsolutePath());

		CsvReadResult result = csvReaderService.readEntries(csvInputPath);

		if (result.entries().isEmpty()) {
			LOGGER.warn("No gallery entries found in CSV file: {}", csvInputPath.toAbsolutePath());
			return;
		}

		LOGGER.info("Found {} gallery entries for event '{}'", result.entries().size(), result.eventName());

		directoryStructureGeneratorService.generate(result, csvInputPath, outputPath);
	}

}
