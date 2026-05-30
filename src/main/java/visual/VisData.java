package visual;

import model.*;
import util.*;
import elem.Element;
import javafx.scene.paint.Color;

/**
 * Global configuration and shared data for the FEA visualizer.
 *
 * <p>All fields are package-private statics so that the visualizer
 * sub-classes ({@link J3dScene}, {@link SurfaceSubGeometry}, etc.) can
 * read them without additional getter boilerplate.  The class is never
 * instantiated; it is a pure data namespace.</p>
 */
public class VisData {

    /** The loaded finite-element model. */
    static FeModel fem;
    /** Global vector of nodal displacements (size = nNod * nDf). */
    static double[] displ;

    /** Keywords accepted in the visualizer input file. */
    enum vars {
        meshfile, resultfile, parm, showedges, shownodes,
        ndivmin, ndivmax, fmin, fmax, ncontours, deformscale,
        end
    }

    /**
     * Result quantities that can be contour-plotted.
     * Displacements: ux, uy, uz.  Stresses: sx … szx.
     * Principal / equivalent: s1, s2, s3, si, s13.
     */
    enum parms {
        ux, uy, uz, sx, sy, sz, sxy, syz, szx,
        s1, s2, s3, si, s13, none
    }

    static String meshFile = null, resultFile = null;
    static parms parm = parms.none;
    static boolean showEdges = true, showNodes = false,
        showDeformShape = false, drawContours = false;
    static double deformScale = 0.0;
    static int nDivMin = 2, nDivMax = 16;
    static double fMin = 0, fMax = 0;
    static int nContours = 256;

    /** Number of pixels in the 1-D colour-scale texture strip. */
    static int textureSize = 256;
    /** Coefficient for curvature-based subdivision: n = 1 + C*ro. */
    static double Csub = 15;
    /** Coefficient for result-based subdivision: n = 1 + F*|df|/Δf. */
    static double Fsub = 20;

    /** Background colour of the 3D sub-scene. */
    static Color bgColor     = Color.WHITE;
    /** Solid face colour (used when contours are disabled). */
    static Color modelColor  = Color.color(0.5, 0.5, 0.9);
    /** Base colour modulated with the contour texture. */
    static Color surTexColor = Color.color(0.8, 0.8, 0.8);
    /** Colour of mesh edges. */
    static Color edgeColor   = Color.color(0.2, 0.2, 0.2);
    /** Colour of surface nodes. */
    static Color nodeColor   = Color.color(0.2, 0.2, 0.2);

    /**
     * Reads the visualizer input file, loads the mesh and (optionally)
     * the result file.
     *
     * @param rd scanner positioned at the start of the vis input file
     */
    public static void readData(FeScanner rd) {

        readDataFile(rd);

        FeScanner fes = new FeScanner(meshFile);
        try {
            fem = new FeModel(fes, null);
            Element.fem = fem;
            fem.readData();
        } finally {
            fes.close();
        }

        if (resultFile != null) {
            displ = new double[fem.nNod * fem.nDf];
            FeStress stress = new FeStress(fem);
            stress.readResults(resultFile, displ);
            if (deformScale > 0) showDeformShape = true;
            drawContours = VisData.parm != VisData.parms.none;
        }
    }

    private static void readDataFile(FeScanner rd) {

        vars name = null;

        while (rd.hasNext()) {

            String varName = rd.next();
            String varNameLower = varName.toLowerCase();
            if (varName.equals("#")) {
                rd.nextLine();
                continue;
            }
            try {
                name = vars.valueOf(varNameLower);
            } catch (Exception e) {
                UTIL.errorMsg("Variable name is not found: " + varName);
            }

            switch (name) {
            case meshfile:
                meshFile = rd.next();
                break;
            case resultfile:
                resultFile = rd.next();
                break;
            case parm:
                try {
                    varName = rd.next();
                    parm = parms.valueOf(varName.toLowerCase());
                } catch (Exception e) {
                    UTIL.errorMsg("No such result parameter: " + varName);
                }
                break;
            case showedges:
                showEdges = rd.next().equalsIgnoreCase("y");
                break;
            case shownodes:
                showNodes = rd.next().equalsIgnoreCase("y");
                break;
            case ndivmin:
                nDivMin = rd.readInt();
                break;
            case ndivmax:
                nDivMax = rd.readInt();
                break;
            case fmin:
                fMin = rd.readDouble();
                break;
            case fmax:
                fMax = rd.readDouble();
                break;
            case ncontours:
                nContours = rd.readInt();
                break;
            case deformscale:
                deformScale = rd.readDouble();
                break;
            case end:
                return;
            default:
                break;
            }
        }
    }
}
