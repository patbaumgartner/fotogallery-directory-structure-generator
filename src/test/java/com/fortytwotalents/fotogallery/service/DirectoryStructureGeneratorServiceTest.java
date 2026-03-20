package com.fortytwotalents.fotogallery.service;

import com.fortytwotalents.fotogallery.model.CsvReadResult;
import com.fortytwotalents.fotogallery.model.GalleryEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DirectoryStructureGeneratorServiceTest {

	private final DirectoryStructureGeneratorService service = new DirectoryStructureGeneratorService();

	@Test
	void generate_createsEventAndPortraitsDirectory(@TempDir Path tempDir) throws IOException {
		List<GalleryEntry> entries = List.of(new GalleryEntry(1, "ABCD-EFGH-IJKL", "MyEvent"),
				new GalleryEntry(2, "MNOP-QRST-UVWX", "MyEvent"));
		CsvReadResult result = new CsvReadResult(entries, "MyEvent");

		service.generate(result, tempDir);

		assertThat(tempDir.resolve("MyEvent")).isDirectory();
		assertThat(tempDir.resolve("MyEvent/portraits")).isDirectory();
		assertThat(tempDir.resolve("MyEvent/portraits/ABCD-EFGH-IJKL")).isDirectory();
		assertThat(tempDir.resolve("MyEvent/portraits/MNOP-QRST-UVWX")).isDirectory();
	}

	@Test
	void generate_sanitizesEventNameWithSpecialChars(@TempDir Path tempDir) throws IOException {
		List<GalleryEntry> entries = List.of(new GalleryEntry(1, "ABCD-EFGH-IJKL", "My:Event/2024"));
		CsvReadResult result = new CsvReadResult(entries, "My:Event/2024");

		service.generate(result, tempDir);

		assertThat(tempDir.resolve("My_Event_2024")).isDirectory();
		assertThat(tempDir.resolve("My_Event_2024/portraits")).isDirectory();
		assertThat(tempDir.resolve("My_Event_2024/portraits/ABCD-EFGH-IJKL")).isDirectory();
	}

	@Test
	void generate_throwsWhenEventNameIsBlank(@TempDir Path tempDir) {
		List<GalleryEntry> entries = List.of(new GalleryEntry(1, "ABCD-EFGH-IJKL", ""));
		CsvReadResult result = new CsvReadResult(entries, "");

		assertThatThrownBy(() -> service.generate(result, tempDir)).isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Event name is missing");
	}

	@Test
	void generate_createsNestedDirectoriesWhenBasePathDoesNotExist(@TempDir Path tempDir) throws IOException {
		Path nestedBase = tempDir.resolve("a/b/c");
		List<GalleryEntry> entries = List.of(new GalleryEntry(1, "CODE-1234-ABCD", "TestEvent"));
		CsvReadResult result = new CsvReadResult(entries, "TestEvent");

		service.generate(result, nestedBase);

		assertThat(nestedBase.resolve("TestEvent/portraits/CODE-1234-ABCD")).isDirectory();
	}

	@Test
	void generate_handlesEmptyEntryList(@TempDir Path tempDir) throws IOException {
		CsvReadResult result = new CsvReadResult(List.of(), "EmptyEvent");

		service.generate(result, tempDir);

		assertThat(tempDir.resolve("EmptyEvent/portraits")).isDirectory();
	}

	@Test
	void generate_isIdempotentWhenRunTwice(@TempDir Path tempDir) throws IOException {
		List<GalleryEntry> entries = List.of(new GalleryEntry(1, "ABCD-EFGH-IJKL", "MyEvent"));
		CsvReadResult result = new CsvReadResult(entries, "MyEvent");

		service.generate(result, tempDir);
		service.generate(result, tempDir);

		assertThat(tempDir.resolve("MyEvent/portraits/ABCD-EFGH-IJKL")).isDirectory();
	}

}
