package integration;

import fea.Jfem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import testutil.LegacyStateReset;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@DisplayName("Example 01 regression")
class Example01IntegrationTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        LegacyStateReset.resetAll();
    }

    @Test
    @DisplayName("matches the golden solver output for load step 1")
    void matchesTheGoldenSolverOutputForLoadStep1() throws Exception {
        Path outputFile = tempDir.resolve("example01.lst");

        Jfem.main(new String[]{"example01/f.fem", outputFile.toString()});

        String listing = Files.readString(outputFile);
        assertThat(listing).contains("Number of elements    nEl = 2");
        assertThat(listing).contains("Number of nodes      nNod = 13");
        assertThat(listing).contains("Number of dimensions nDim = 2");
        assertThat(listing).contains("Loadstep 1");

        ResultFileAssertions.assertNumericContentClose(
                Path.of(outputFile + ".1"),
                Path.of("example01/f.lst.1"),
                1.0e-6);
    }
}
