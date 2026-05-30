package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Basic tests for Dof value object.
 */
@DisplayName("Dof - degree of freedom")
class DofTest {

    @Test
    @DisplayName("constructor stores dofNum and value")
    void constructor_storesFields() {
        Dof d = new Dof(42, 3.14);
        assertThat(d.dofNum).isEqualTo(42);
        assertThat(d.value).isEqualTo(3.14);
    }
}
