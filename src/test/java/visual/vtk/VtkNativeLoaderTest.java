package visual.vtk;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Basic tests for the VTK native loader.
 * These can run even when full VTK is not available.
 */
@DisplayName("VtkNativeLoader")
class VtkNativeLoaderTest {

    @Test
    @DisplayName("platform hint is never null or empty")
    void platformHintIsValid() {
        String hint = VtkNativeLoader.getPlatformHint();
        assertThat(hint).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("initialize is idempotent and safe")
    void initializeIsSafe() {
        boolean first = VtkNativeLoader.initialize();
        boolean second = VtkNativeLoader.initialize();
        assertThat(second).isEqualTo(first);
    }
}
