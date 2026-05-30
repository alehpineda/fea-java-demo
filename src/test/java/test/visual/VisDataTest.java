package test.visual;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import util.FeScanner;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Smoke tests for the visualization data loading pipeline (VisData + FeModel + ResultAtNodes).
 *
 * These tests protect the contract that existing .vis files continue to parse correctly
 * after the VTK migration. They exercise the code that will later feed VTK.
 */
@DisplayName("VisData - visualization control file loading")
class VisDataTest {

    private static final String BASE = "src/test/resources/examples/";

    @ParameterizedTest(name = "loads {0} correctly")
    @CsvSource({
        "example03/f.vis, f.mesh, f.res, ux, true, false, 8",
        "example04/hole3d-mesh.vis, hole3d.mesh, , none, false, false, 256",
        "example04/hole3d.vis, hole3d.mesh, hole3d.lst.1, Sy, true, true, 10"
    })
    @DisplayName("parses real example .vis files and sets expected fields")
    void parsesRealVisFiles(String visPath, String expectedMesh,
                            String expectedResult, String expectedParm,
                            boolean expectDeform, boolean expectContours,
                            int expectedNContours) throws Exception {

        // Reset static state (legacy design)
        resetVisData();

        File visFile = new File(BASE + visPath);
        assertThat(visFile).exists();

        FeScanner rd = new FeScanner(visFile.getAbsolutePath());
        VisData.readData(rd);

        assertThat(VisData.meshFile).endsWith(expectedMesh);
        if (expectedResult != null && !expectedResult.isBlank()) {
            assertThat(VisData.resultFile).endsWith(expectedResult);
        }
        assertThat(VisData.parm.name()).isEqualToIgnoringCase(expectedParm);
        assertThat(VisData.nContours).isEqualTo(expectedNContours);
        assertThat(VisData.showDeformShape).isEqualTo(expectDeform);
        assertThat(VisData.drawContours).isEqualTo(expectContours);

        // Basic sanity on loaded model
        assertThat(VisData.fem).isNotNull();
        assertThat(VisData.fem.nEl).isPositive();
        assertThat(VisData.fem.nNod).isPositive();
    }

    @Test
    @DisplayName("sets drawContours when a result parameter is requested")
    void drawContoursFlagIsSetWhenParmIsActive() throws Exception {
        resetVisData();

        File visFile = new File(BASE + "example03/f.vis");
        FeScanner rd = new FeScanner(visFile.getAbsolutePath());
        VisData.readData(rd);

        assertThat(VisData.drawContours).isTrue();
        assertThat(VisData.parm).isEqualTo(VisData.parms.ux);
    }

    private void resetVisData() {
        VisData.meshFile = null;
        VisData.resultFile = null;
        VisData.parm = VisData.parms.none;
        VisData.showEdges = true;
        VisData.showNodes = false;
        VisData.showDeformShape = false;
        VisData.drawContours = false;
        VisData.deformScale = 0.0;
        VisData.nDivMin = 2;
        VisData.nDivMax = 16;
        VisData.fMin = 0;
        VisData.fMax = 0;
        VisData.nContours = 256;
        VisData.fem = null;
        VisData.displ = null;
    }
}
