package solver;

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

@Tag("unit")
@DisplayName("Solver factory")
class SolverFactoryTest {

    @BeforeEach
    void setUp() {
        LegacyStateReset.resetAll();
        FE.main = FE.JFEM;
    }

    @Test
    @DisplayName("creates the profile solver when LDU is selected")
    void createsTheProfileSolverWhenLduIsSelected() {
        Solver.solver = Solver.Solvers.ldu;

        Solver solver = Solver.newSolver(createMinimalModel());

        assertThat(solver).isInstanceOf(SolverLDU.class);
    }

    @Test
    @DisplayName("creates the conjugate-gradient solver when PCG is selected")
    void createsTheConjugateGradientSolverWhenPcgIsSelected() {
        Solver.solver = Solver.Solvers.pcg;

        Solver solver = Solver.newSolver(createMinimalModel());

        assertThat(solver).isInstanceOf(SolverPCG.class);
    }

    private FeModel createMinimalModel() {
        FeModel model = new FeModel(null, new PrintWriter(new StringWriter()));
        model.nDim = 2;
        model.nDf = 2;
        model.nNod = 8;
        model.nEl = 1;
        model.nEq = model.nNod * model.nDf;
        model.newCoordArray();
        Element.fem = model;
        model.elems = new Element[]{Element.newElement("quad8")};
        model.elems[0].setElemConnectivities(new int[]{1, 2, 3, 4, 5, 6, 7, 8});
        return model;
    }
}
