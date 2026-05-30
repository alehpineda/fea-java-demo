package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * JUnit 5 tests for UTIL (per java-junit skill: parameterized, AAA, descriptive names).
 * Covers direction mapping and error handling modernization.
 */
@DisplayName("UTIL - miscellaneous utilities")
class UTILTest {

    @Test
    @DisplayName("direction(x) should return 1")
    void direction_x_returnsOne() {
        // Arrange / Act / Assert
        assertThat(UTIL.direction("x")).isEqualTo(1);
    }

    @ParameterizedTest(name = "direction({0}) = {1}")
    @CsvSource({
        "x, 1",
        "y, 2",
        "z, 3",
        "n, 0",
        "X, -1",
        "foo, -1"
    })
    @DisplayName("direction maps valid dir strings and returns -1 for invalid (data-driven, per java-junit)")
    void direction_mapsCorrectly(String input, int expected) {
        assertThat(UTIL.direction(input)).isEqualTo(expected);
    }

    @Test
    @DisplayName("errorMsg throws IllegalStateException (modernized, enables testing vs original System.exit)")
    void errorMsg_throwsException() {
        assertThatThrownBy(() -> UTIL.errorMsg("test error"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("test error");
    }
}
