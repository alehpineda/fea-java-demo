package integration;

import fea.Jmgen;
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
@DisplayName("Example 04 mesh-generation regression")
class JmgenExample04IntegrationTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        LegacyStateReset.resetAll();
    }

    @Test
    @DisplayName("matches the golden mesh generated for the hole plate example")
    void matchesTheGoldenMeshGeneratedForTheHolePlateExample() throws Exception {
        Path input = tempDir.resolve("hole3d.gen");
        String content = Files.readString(Path.of("example04/hole3d.gen"))
                .replace("writemesh b3 hole3d.mesh", "writemesh b3 generated.mesh");
        Files.writeString(input, content);
        Path listing = tempDir.resolve("hole3d.gen.lst");

        Jmgen.main(new String[]{input.toString(), listing.toString()});

        assertThat(Files.readString(listing)).contains("Mesh b3: nEl = 148  nNod = 908");
        ResultFileAssertions.assertNumericContentClose(
                tempDir.resolve("generated.mesh"),
                Path.of("example04/hole3d.mesh"),
                1.0e-6);
    }
}
