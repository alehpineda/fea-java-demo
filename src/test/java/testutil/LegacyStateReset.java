package testutil;

import elem.Element;
import fea.FE;
import fea.Jfem;
import fea.Jmgen;
import material.ElasticMaterial;
import model.FeLoadData;
import model.FeModelData;
import model.FeStress;
import solver.Solver;

import java.lang.reflect.Field;

public final class LegacyStateReset {

    private LegacyStateReset() {
    }

    public static void resetAll() {
        FE.main = FE.JFEM;
        FE.bigValue = 1.0e64;
        FE.tunedSolver = true;
        FE.maxRow2D = 21;
        FE.maxRow3D = 117;
        FE.maxIterPcg = 10000;
        FE.epIntegrationTANGENT = false;

        Element.fem = null;
        Element.load = null;
        Solver.solver = Solver.Solvers.ldu;
        Solver.lengthOfGSM = 0;
        FeStress.relResidNorm = 0.0;

        Jmgen.RD = null;
        Jmgen.PR = null;
        Jmgen.blocks = null;

        setStaticField(Jfem.class, "RD", null);
        setStaticField(Jfem.class, "PR", null);
        Jfem.fileOut = null;

        setStaticField(FeModelData.class, "RD", null);
        setStaticField(FeModelData.class, "PR", null);
        setStaticField(FeModelData.class, "varName", null);
        setStaticField(FeModelData.class, "stressState", FeModelData.StrStates.threed);

        setStaticField(FeLoadData.class, "loadStepName", null);
        setStaticField(FeLoadData.class, "residTolerance", 0.01d);
        setStaticField(FeLoadData.class, "maxIterNumber", 100);
        setStaticField(FeLoadData.class, "dtemp", null);
        setStaticField(FeLoadData.class, "dpLoad", null);
        setStaticField(FeLoadData.class, "spLoad", null);
        setStaticField(FeLoadData.class, "dhLoad", null);
        setStaticField(FeLoadData.class, "dDispl", null);
        setStaticField(FeLoadData.class, "sDispl", null);
        setStaticField(FeLoadData.class, "RHS", null);

        setStaticField(ElasticMaterial.class, "lv", 0);
    }

    private static void setStaticField(Class<?> type, String name, Object value) {
        try {
            Field field = type.getDeclaredField(name);
            field.setAccessible(true);
            field.set(null, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to reset field " + type.getName() + '.' + name, e);
        }
    }
}
