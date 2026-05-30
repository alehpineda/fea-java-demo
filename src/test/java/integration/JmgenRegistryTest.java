package integration;

import fea.JmgenRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import testutil.LegacyStateReset;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@DisplayName("Jmgen command registry")
class JmgenRegistryTest {

    @BeforeEach
    void setUp() {
        LegacyStateReset.resetAll();
    }

    @Test
    @DisplayName("registers every supported generator command")
    void registersEverySupportedGeneratorCommand() {
        assertThat(JmgenRegistry.commands())
                .containsKeys("genquad8", "connect", "copy", "readmesh", "rectangle", "sweep", "transform", "writemesh");
    }
}
