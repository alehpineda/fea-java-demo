package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import testutil.LegacyStateReset;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
@DisplayName("FeModelData")
class FeModelDataTest {

    @BeforeEach
    void setUp() {
        LegacyStateReset.resetAll();
    }

    @Test
    @DisplayName("stores and reads nodal coordinates")
    void storesAndReadsNodalCoordinates() {
        FeModelData model = new FeModelData();
        model.nDim = 3;
        model.nNod = 2;
        model.newCoordArray();

        model.setNodeCoords(0, new double[]{1.0, 2.0, 3.0});
        model.setNodeCoord(1, 0, 4.0);
        model.setNodeCoord(1, 1, 5.0);
        model.setNodeCoord(1, 2, 6.0);

        assertThat(model.getNodeCoords(0)).containsExactly(1.0, 2.0, 3.0);
        assertThat(model.getNodeCoord(1, 0)).isEqualTo(4.0);
        assertThat(model.getNodeCoord(1, 1)).isEqualTo(5.0);
        assertThat(model.getNodeCoord(1, 2)).isEqualTo(6.0);
    }

    @Test
    @DisplayName("returns defensive copies of coordinate arrays")
    void returnsDefensiveCopiesOfCoordinateArrays() {
        FeModelData model = new FeModelData();
        model.nDim = 2;
        model.nNod = 1;
        model.newCoordArray();
        model.setNodeCoords(0, new double[]{7.0, 8.0});

        double[] coordinates = model.getNodeCoords(0);
        coordinates[0] = -1.0;

        assertThat(model.getNodeCoords(0)).containsExactly(7.0, 8.0);
    }
}
