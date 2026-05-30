package material;

import elem.Element;
import fea.FE;
import model.FeModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import testutil.LegacyStateReset;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@Tag("unit")
@DisplayName("ElasticMaterial")
class ElasticMaterialTest {

    @BeforeEach
    void setUp() {
        LegacyStateReset.resetAll();
    }

    @Test
    @DisplayName("computes Lame constants for three-dimensional elasticity")
    void computesLameConstantsForThreeDimensionalElasticity() {
        ElasticMaterial material = new ElasticMaterial("threed");
        material.setElasticProp(1.0, 0.3, 0.0);

        assertThat(material.getLambda()).isCloseTo(0.5769230769230769, within(1.0e-12));
        assertThat(material.getMu()).isCloseTo(0.3846153846153846, within(1.0e-12));
    }

    @Test
    @DisplayName("converts uniaxial strain to stress in plane stress")
    void convertsUniaxialStrainToStressInPlaneStress() {
        ElasticMaterial material = new ElasticMaterial("plstress");
        material.setElasticProp(1.0, 0.3, 0.0);
        StubElement element = createStubElement(2, new double[]{1.0, 0.0, 0.0, 0.0});

        material.strainToStress(element, 0);

        assertThat(element.str[0].dStress[0]).isCloseTo(1.098901098901099, within(1.0e-9));
        assertThat(element.str[0].dStress[1]).isCloseTo(0.32967032967032966, within(1.0e-9));
        assertThat(element.str[0].dStress[2]).isCloseTo(0.0, within(1.0e-9));
        assertThat(element.str[0].dStress[3]).isCloseTo(0.0, within(1.0e-9));
    }

    @Test
    @DisplayName("converts uniaxial strain to stress in plane strain")
    void convertsUniaxialStrainToStressInPlaneStrain() {
        ElasticMaterial material = new ElasticMaterial("plstrain");
        material.setElasticProp(1.0, 0.3, 0.0);
        StubElement element = createStubElement(2, new double[]{1.0, 0.0, 0.0, 0.0});

        material.strainToStress(element, 0);

        assertThat(element.str[0].dStress[0]).isCloseTo(1.346153846153846, within(1.0e-9));
        assertThat(element.str[0].dStress[1]).isCloseTo(0.5769230769230769, within(1.0e-9));
        assertThat(element.str[0].dStress[2]).isCloseTo(0.0, within(1.0e-9));
        assertThat(element.str[0].dStress[3]).isCloseTo(0.5769230769230769, within(1.0e-9));
    }

    @Test
    @DisplayName("converts uniaxial strain to stress in three dimensions")
    void convertsUniaxialStrainToStressInThreeDimensions() {
        ElasticMaterial material = new ElasticMaterial("threed");
        material.setElasticProp(1.0, 0.3, 0.0);
        StubElement element = createStubElement(3, new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0});

        material.strainToStress(element, 0);

        assertThat(element.str[0].dStress[0]).isCloseTo(1.346153846153846, within(1.0e-9));
        assertThat(element.str[0].dStress[1]).isCloseTo(0.5769230769230769, within(1.0e-9));
        assertThat(element.str[0].dStress[2]).isCloseTo(0.5769230769230769, within(1.0e-9));
        assertThat(element.str[0].dStress[3]).isCloseTo(0.0, within(1.0e-9));
        assertThat(element.str[0].dStress[4]).isCloseTo(0.0, within(1.0e-9));
        assertThat(element.str[0].dStress[5]).isCloseTo(0.0, within(1.0e-9));
    }

    private StubElement createStubElement(int nDim, double[] strains) {
        FeModel model = new FeModel(null, new PrintWriter(new StringWriter()));
        model.nDim = nDim;
        Element.fem = model;
        FE.main = FE.JFEM;
        return new StubElement(strains);
    }

    private static final class StubElement extends Element {

        private final double[] strains;

        private StubElement(double[] strains) {
            super("stub", 1, 1);
            this.strains = strains;
        }

        @Override
        public double[] getStrainsAtIntPoint(int intPoint) {
            return strains.clone();
        }
    }
}
