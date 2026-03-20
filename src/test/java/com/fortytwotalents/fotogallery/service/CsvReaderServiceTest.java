package com.fortytwotalents.fotogallery.service;

import com.fortytwotalents.fotogallery.model.CsvReadResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CsvReaderServiceTest {

	private final CsvReaderService service = new CsvReaderService();

	@Test
	void readEntries_parsesAllRows(@TempDir Path tempDir) throws IOException {
		Path csvFile = tempDir.resolve("codes.csv");
		Files.writeString(csvFile,
				"Number,Code,Password,Event Name,URL\n"
						+ "1,ABCD-EFGH-IJKL,pass1,SchoolEvent2024,https://example.com/ABCD-EFGH-IJKL\n"
						+ "2,MNOP-QRST-UVWX,pass2,SchoolEvent2024,https://example.com/MNOP-QRST-UVWX\n",
				StandardCharsets.UTF_8);

		CsvReadResult result = service.readEntries(csvFile);

		assertThat(result.eventName()).isEqualTo("SchoolEvent2024");
		assertThat(result.entries()).hasSize(2);
		assertThat(result.entries().get(0).number()).isEqualTo(1);
		assertThat(result.entries().get(0).code()).isEqualTo("ABCD-EFGH-IJKL");
		assertThat(result.entries().get(1).number()).isEqualTo(2);
		assertThat(result.entries().get(1).code()).isEqualTo("MNOP-QRST-UVWX");
	}

	@Test
	void readEntries_skipsEmptyLines(@TempDir Path tempDir) throws IOException {
		Path csvFile = tempDir.resolve("codes.csv");
		Files.writeString(csvFile,
				"Number,Code,Password,Event Name,URL\n" + "1,ABCD-EFGH-IJKL,pass1,MyEvent,https://example.com/1\n"
						+ "\n" + "2,MNOP-QRST-UVWX,pass2,MyEvent,https://example.com/2\n",
				StandardCharsets.UTF_8);

		CsvReadResult result = service.readEntries(csvFile);

		assertThat(result.entries()).hasSize(2);
	}

	@Test
	void readEntries_skipsRowsWithInvalidNumber(@TempDir Path tempDir) throws IOException {
		Path csvFile = tempDir.resolve("codes.csv");
		Files.writeString(csvFile,
				"Number,Code,Password,Event Name,URL\n" + "1,ABCD-EFGH-IJKL,pass1,MyEvent,https://example.com/1\n"
						+ "BAD,MNOP-QRST-UVWX,pass2,MyEvent,https://example.com/2\n",
				StandardCharsets.UTF_8);

		CsvReadResult result = service.readEntries(csvFile);

		assertThat(result.entries()).hasSize(1);
		assertThat(result.entries().get(0).number()).isEqualTo(1);
	}

	@Test
	void readEntries_throwsWhenFileNotFound(@TempDir Path tempDir) {
		Path missingFile = tempDir.resolve("nonexistent.csv");

		assertThatThrownBy(() -> service.readEntries(missingFile)).isInstanceOf(IOException.class)
			.hasMessageContaining("CSV file not found");
	}

	@Test
	void readEntries_extractsEventNameFromFirstDataRow(@TempDir Path tempDir) throws IOException {
		Path csvFile = tempDir.resolve("codes.csv");
		Files.writeString(csvFile,
				"Number,Code,Password,Event Name,URL\n" + "1,ABCD-EFGH-IJKL,pass1,Graduation2025,https://x.com/1\n"
						+ "2,MNOP-QRST-UVWX,pass2,Graduation2025,https://x.com/2\n",
				StandardCharsets.UTF_8);

		CsvReadResult result = service.readEntries(csvFile);

		assertThat(result.eventName()).isEqualTo("Graduation2025");
	}

	@Test
	void readEntries_handlesBomCharacter(@TempDir Path tempDir) throws IOException {
		Path csvFile = tempDir.resolve("codes.csv");
		// Write with BOM
		byte[] bom = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
		byte[] content = "Number,Code,Password,Event Name,URL\n1,ABCD-EFGH-IJKL,pass1,BomEvent,https://x.com/1\n"
			.getBytes(StandardCharsets.UTF_8);
		byte[] withBom = new byte[bom.length + content.length];
		System.arraycopy(bom, 0, withBom, 0, bom.length);
		System.arraycopy(content, 0, withBom, bom.length, content.length);
		Files.write(csvFile, withBom);

		CsvReadResult result = service.readEntries(csvFile);

		assertThat(result.entries()).hasSize(1);
		assertThat(result.entries().get(0).number()).isEqualTo(1);
	}

	@Test
	void readEntries_returnsEmptyListForEmptyCsv(@TempDir Path tempDir) throws IOException {
		Path csvFile = tempDir.resolve("codes.csv");
		Files.writeString(csvFile, "Number,Code,Password,Event Name,URL\n", StandardCharsets.UTF_8);

		CsvReadResult result = service.readEntries(csvFile);

		assertThat(result.entries()).isEmpty();
		assertThat(result.eventName()).isEmpty();
	}

	@Test
	void readEntries_usesColumnPositionsWhenColumnsMissing(@TempDir Path tempDir) throws IOException {
		Path csvFile = tempDir.resolve("codes.csv");
		// Minimal CSV with only Number and Code columns
		Files.writeString(csvFile, "Number,Code,Password,Event Name,URL\n1,MY-CODE-HERE,pw,SomeEvent,https://x.com\n",
				StandardCharsets.UTF_8);

		CsvReadResult result = service.readEntries(csvFile);

		assertThat(result.entries()).hasSize(1);
		assertThat(result.entries().get(0).code()).isEqualTo("MY-CODE-HERE");
		assertThat(result.eventName()).isEqualTo("SomeEvent");
	}

}
