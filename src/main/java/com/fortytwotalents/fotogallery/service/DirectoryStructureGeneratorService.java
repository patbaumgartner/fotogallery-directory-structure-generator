package com.fortytwotalents.fotogallery.service;

import com.fortytwotalents.fotogallery.model.CsvReadResult;
import com.fortytwotalents.fotogallery.model.GalleryEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
public class DirectoryStructureGeneratorService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryStructureGeneratorService.class);

	private static final String PORTRAITS_DIR = "portraits";

	private static final String KLASSENFOTO_DIR = "klassenfoto";

	public void generate(CsvReadResult csvReadResult, Path csvInputPath, Path baseOutputPath) throws IOException {
		String eventName = csvReadResult.eventName();

		if (eventName.isBlank()) {
			throw new IllegalArgumentException("Event name is missing from the CSV file.");
		}

		Path eventDir = baseOutputPath.resolve(sanitize(eventName));
		Path portraitsDir = eventDir.resolve(PORTRAITS_DIR);
		Path klassenFotoDir = eventDir.resolve(KLASSENFOTO_DIR);

		Files.createDirectories(portraitsDir);
		LOGGER.info("Created directory: {}", portraitsDir.toAbsolutePath());

		Files.createDirectories(klassenFotoDir);
		LOGGER.info("Created directory: {}", klassenFotoDir.toAbsolutePath());

		for (GalleryEntry entry : csvReadResult.entries()) {
			Path idDir = portraitsDir.resolve(entry.code());
			Files.createDirectories(idDir);
			LOGGER.info("Created directory: {}", idDir.toAbsolutePath());
		}

		moveRelatedFiles(csvInputPath, eventDir);

		LOGGER.info("Directory structure generated under: {}", eventDir.toAbsolutePath());
	}

	private void moveRelatedFiles(Path csvInputPath, Path eventDir) throws IOException {
		Path sourceDir = csvInputPath.toAbsolutePath().getParent();
		String csvFilename = csvInputPath.getFileName().toString();
		String prefix = csvFilename.replaceAll("-codes\\.csv$", "");

		try (Stream<Path> files = Files.list(sourceDir)) {
			files.filter(p -> {
				String name = p.getFileName().toString();
				return name.startsWith(prefix) && (name.endsWith(".csv") || name.endsWith(".pdf"));
			}).forEach(source -> {
				Path target = eventDir.resolve(source.getFileName());
				try {
					Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
					LOGGER.info("Moved file: {} -> {}", source.getFileName(), target.toAbsolutePath());
				} catch (IOException e) {
					LOGGER.error("Failed to move file: {}", source, e);
				}
			});
		}
	}

	private String sanitize(String name) {
		return name.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
	}

}

