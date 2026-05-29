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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@Tag("integration")
@DisplayName("Example 02 regression")
class Example02IntegrationTest {

    private static final Pattern LOADSTEP_B_PATTERN = Pattern.compile(
            "Loadstep B\\s+(\\d+) iterations, Relative residual norm =\\s*([0-9.E+-]+)");

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        LegacyStateReset.resetAll();
    }

    @Test
    @DisplayName("matches the golden elastic-plastic outputs for load steps A and B")
    void matchesTheGoldenElasticPlasticOutputsForLoadStepsAAndB() throws Exception {
        Path outputFile = tempDir.resolve("example02.lst");

        Jfem.main(new String[]{"example02/f.fem", outputFile.toString()});

        String listing = Files.readString(outputFile);
        assertThat(listing).contains("Number of elements    nEl = 2");
        assertThat(listing).contains("Number of nodes      nNod = 32");
        assertThat(listing).contains("Number of dimensions nDim = 3");
        assertThat(listing).contains("Loadstep A");

        Matcher matcher = LOADSTEP_B_PATTERN.matcher(listing);
        assertThat(matcher.find()).isTrue();
        assertThat(Integer.parseInt(matcher.group(1))).isEqualTo(20);
        assertThat(Double.parseDouble(matcher.group(2))).isCloseTo(0.00075, within(1.0e-6));

        ResultFileAssertions.assertNumericContentClose(
                Path.of(outputFile + ".A"),
                Path.of("example02/f.lst.A"),
                1.0e-6);
        ResultFileAssertions.assertNumericContentClose(
                Path.of(outputFile + ".B"),
                Path.of("example02/f.lst.B"),
                1.0e-6);
    }
}
