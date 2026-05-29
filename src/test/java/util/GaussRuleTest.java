package util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import testutil.LegacyStateReset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@Tag("unit")
@DisplayName("GaussRule")
class GaussRuleTest {

    @BeforeEach
    void setUp() {
        LegacyStateReset.resetAll();
    }

    @Test
    @DisplayName("creates the standard one-dimensional rules")
    void createsTheStandardOneDimensionalRules() {
        GaussRule onePoint = new GaussRule(1, 1);
        GaussRule twoPoint = new GaussRule(2, 1);
        GaussRule threePoint = new GaussRule(3, 1);

        assertThat(onePoint.nIntPoints).isEqualTo(1);
        assertThat(onePoint.xii[0]).isCloseTo(0.0, within(1.0e-12));
        assertThat(onePoint.wi[0]).isCloseTo(2.0, within(1.0e-12));

        double oneOverSqrt3 = 1.0 / Math.sqrt(3.0);
        assertThat(twoPoint.nIntPoints).isEqualTo(2);
        assertThat(twoPoint.xii).containsExactly(-oneOverSqrt3, oneOverSqrt3);
        assertThat(twoPoint.wi).containsExactly(1.0, 1.0);

        assertThat(sum(threePoint.wi)).isCloseTo(2.0, within(1.0e-12));
        assertThat(threePoint.xii[0]).isCloseTo(-threePoint.xii[2], within(1.0e-12));
    }

    @Test
    @DisplayName("creates the tensor-product two-dimensional and three-dimensional rules")
    void createsTheTensorProductTwoDimensionalAndThreeDimensionalRules() {
        GaussRule twoByTwo = new GaussRule(2, 2);
        GaussRule threeByThreeByThree = new GaussRule(3, 3);

        assertThat(twoByTwo.nIntPoints).isEqualTo(4);
        assertThat(sum(twoByTwo.wi)).isCloseTo(4.0, within(1.0e-12));
        assertThat(twoByTwo.xii).containsExactly(
                -1.0 / Math.sqrt(3.0),
                -1.0 / Math.sqrt(3.0),
                1.0 / Math.sqrt(3.0),
                1.0 / Math.sqrt(3.0));
        assertThat(twoByTwo.eti).containsExactly(
                -1.0 / Math.sqrt(3.0),
                1.0 / Math.sqrt(3.0),
                -1.0 / Math.sqrt(3.0),
                1.0 / Math.sqrt(3.0));

        assertThat(threeByThreeByThree.nIntPoints).isEqualTo(27);
        assertThat(sum(threeByThreeByThree.wi)).isCloseTo(8.0, within(1.0e-12));
    }

    @Test
    @DisplayName("creates the fourteen-point three-dimensional rule")
    void createsTheFourteenPointThreeDimensionalRule() {
        GaussRule fourteenPoint = new GaussRule(14, 3);

        assertThat(fourteenPoint.nIntPoints).isEqualTo(14);
        assertThat(sum(fourteenPoint.wi)).isCloseTo(8.0, within(1.0e-12));
        assertThat(maxAbs(fourteenPoint.xii)).isCloseTo(maxAbs(fourteenPoint.eti), within(1.0e-12));
        assertThat(maxAbs(fourteenPoint.xii)).isCloseTo(maxAbs(fourteenPoint.zei), within(1.0e-12));
    }

    private double sum(double[] values) {
        double sum = 0.0;
        for (double value : values) {
            sum += value;
        }
        return sum;
    }

    private double maxAbs(double[] values) {
        double max = 0.0;
        for (double value : values) {
            max = Math.max(max, Math.abs(value));
        }
        return max;
    }
}
