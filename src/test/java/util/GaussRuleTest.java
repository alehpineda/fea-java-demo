package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for GaussRule (numerical integration points/weights).
 * Uses parameterized for multiple (nGauss, nDim) combinations per java-junit.
 */
@DisplayName("GaussRule - Gauss quadrature")
class GaussRuleTest {

    @Test
    @DisplayName("1-point 1D rule has 1 point, weight 2.0 at 0")
    void onePoint1D_correct() {
        GaussRule r = new GaussRule(1, 1);
        assertThat(r.nIntPoints).isEqualTo(1);
        assertThat(r.xii[0]).isEqualTo(0.0);
        assertThat(r.wi[0]).isEqualTo(2.0);
    }

    @ParameterizedTest(name = "nGauss={0}, nDim={1} -> {2} points")
    @CsvSource({
        "1,1,1",
        "2,1,2",
        "3,1,3",
        "2,2,4",
        "3,2,9",
        "1,3,1",
        "14,3,14"
    })
    @DisplayName("various Gauss rules produce expected point counts (data-driven)")
    void pointCounts(int nGauss, int nDim, int expectedPoints) {
        GaussRule r = new GaussRule(nGauss, nDim);
        assertThat(r.nIntPoints).isEqualTo(expectedPoints);
        assertThat(r.xii).hasSize(expectedPoints);
        assertThat(r.wi).hasSize(expectedPoints);
    }

    @Test
    @DisplayName("invalid nGauss throws via UTIL.errorMsg (now exception)")
    void invalidNGauss_throws() {
        assertThatThrownBy(() -> new GaussRule(4, 2))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("invalid nDim throws")
    void invalidNDim_throws() {
        assertThatThrownBy(() -> new GaussRule(2, 4))
            .isInstanceOf(IllegalStateException.class);
    }
}
