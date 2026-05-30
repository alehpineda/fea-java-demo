package util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import testutil.LegacyStateReset;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("unit")
@DisplayName("FePrintWriter")
class FePrintWriterTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        LegacyStateReset.resetAll();
    }

    @Test
    @DisplayName("creates printers for writable files")
    void createsPrintersForWritableFiles() throws Exception {
        Path output = tempDir.resolve("listing.lst");

        PrintWriter writer = new FePrintWriter().getPrinter(output.toString());
        writer.println("hello");
        writer.close();

        assertThat(Files.readString(output)).contains("hello");
    }

    @Test
    @DisplayName("throws an exception when the output file cannot be opened")
    void throwsAnExceptionWhenTheOutputFileCannotBeOpened() throws Exception {
        Path directory = Files.createDirectory(tempDir.resolve("listing-dir"));

        assertThatThrownBy(() -> new FePrintWriter().getPrinter(directory.toString()))
                .isInstanceOf(FeaException.class)
                .hasMessageContaining("Cannot open output file");
    }
}
