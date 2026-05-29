package material;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import testutil.LegacyStateReset;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
@DisplayName("Material factory")
class MaterialTest {

    @BeforeEach
    void setUp() {
        LegacyStateReset.resetAll();
    }

    @Test
    @DisplayName("creates elastic materials")
    void createsElasticMaterials() {
        Material material = Material.newMaterial("elastic", "plstress");

        assertThat(material).isInstanceOf(ElasticMaterial.class);
    }

    @Test
    @DisplayName("creates elastic-plastic materials")
    void createsElasticPlasticMaterials() {
        Material material = Material.newMaterial("elplastic", "threed");

        assertThat(material).isInstanceOf(ElasticPlasticMaterial.class);
    }

    @Test
    @DisplayName("returns configured elastic properties")
    void returnsConfiguredElasticProperties() {
        Material material = Material.newMaterial("elastic", "threed");
        material.setElasticProp(10.0, 0.25, 1.2e-5);

        assertThat(material.getNu()).isEqualTo(0.25);
        assertThat(material.getAlpha()).isEqualTo(1.2e-5);
    }
}
