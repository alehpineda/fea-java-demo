package elem;

import fea.FE;
import model.FeModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import testutil.LegacyStateReset;
import util.FeaException;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("unit")
@DisplayName("Element factory")
class ElementFactoryTest {

    @BeforeEach
    void setUp() {
        LegacyStateReset.resetAll();
        FE.main = FE.JFEM;
    }

    @Test
    @DisplayName("creates quad8 elements")
    void createsQuad8Elements() {
        Element.fem = createModel(2);

        Element element = Element.newElement("quad8");

        assertThat(element).isInstanceOf(ElementQuad2D.class);
    }

    @Test
    @DisplayName("creates hex20 elements")
    void createsHex20Elements() {
        Element.fem = createModel(3);

        Element element = Element.newElement("hex20");

        assertThat(element).isInstanceOf(ElementQuad3D.class);
    }

    @Test
    @DisplayName("rejects unknown element names")
    void rejectsUnknownElementNames() {
        Element.fem = createModel(2);

        assertThatThrownBy(() -> Element.newElement("bogus"))
                .isInstanceOf(FeaException.class)
                .hasMessageContaining("Incorrect element type");
    }

    private FeModel createModel(int nDim) {
        FeModel model = new FeModel(null, new PrintWriter(new StringWriter()));
        model.nDim = nDim;
        return model;
    }
}
