package util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import testutil.LegacyStateReset;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("unit")
@DisplayName("UTIL")
class UTILTest {

    @BeforeEach
    void setUp() {
        LegacyStateReset.resetAll();
    }

    @Test
    @DisplayName("maps text directions to solver directions")
    void mapsTextDirectionsToSolverDirections() {
        assertThat(UTIL.direction("x")).isEqualTo(1);
        assertThat(UTIL.direction("y")).isEqualTo(2);
        assertThat(UTIL.direction("z")).isEqualTo(3);
        assertThat(UTIL.direction("n")).isEqualTo(0);
        assertThat(UTIL.direction("bad")).isEqualTo(-1);
    }

    @Test
    @DisplayName("formats the current date and time for listing files")
    void formatsTheCurrentDateAndTimeForListingFiles() {
        StringWriter writer = new StringWriter();

        UTIL.printDate(new PrintWriter(writer));

        assertThat(writer.toString())
                .matches("Date: \\d{4}-\\d{2}-\\d{2}  Time: \\d{2}:\\d{2}:\\d{2}\\n");
    }

    @Test
    @DisplayName("throws a testable exception for error messages")
    void throwsATestableExceptionForErrorMessages() {
        assertThatThrownBy(() -> UTIL.errorMsg("boom"))
                .isInstanceOf(FeaException.class)
                .hasMessage("boom");
    }
}
