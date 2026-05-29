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
@DisplayName("Readmesh/copy/transform integration")
class JmgenReadmeshTransformIntegrationTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        LegacyStateReset.resetAll();
    }

    @Test
    @DisplayName("reads, copies, transforms, and writes a mesh with relative paths")
    void readsCopiesTransformsAndWritesAMeshWithRelativePaths() throws Exception {
        Files.copy(Path.of("example02/f.mesh"), tempDir.resolve("base.mesh"));
        Path script = tempDir.resolve("transform.gen");
        Files.writeString(script, """
                readmesh base base.mesh
                copy base shifted
                transform shifted
                  translate y 1.5
                  scale z 2.0
                  rotate z 90.0
                  mirror x 0.0
                end
                writemesh shifted out.mesh
                """);
        Path listing = tempDir.resolve("transform.lst");

        Jmgen.main(new String[]{script.toString(), listing.toString()});

        String outputMesh = Files.readString(tempDir.resolve("out.mesh"));
        assertThat(Files.readString(listing)).contains("Mesh shifted: nEl = 2  nNod = 32");
        assertThat(outputMesh).contains("nNod =    32");
        assertThat(outputMesh).contains("nEl =     2");
        assertThat(outputMesh).contains("         1.500000000         0.000000000         0.000000000");
        assertThat(outputMesh).contains("         1.500000000         0.000000000         1.000000000");
        assertThat(outputMesh).contains("hex20    mat");
    }
}
