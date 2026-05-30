package visual;

import model.*;
import util.*;
import elem.Element;

import javax.vecmath.*;

/**
 * Central configuration and data holder for the FEA visualization module.
 *
 * <p>This class is responsible for:
 * <ul>
 *   <li>Parsing the legacy <code>.vis</code> control file (exact format preserved for compatibility)</li>
 *   <li>Loading the mesh (<code>.mesh</code>) and optional results (<code>.res</code> / listing)</li>
 *   <li>Exposing visualization parameters (which scalar field to contour, deformation scale, etc.)</li>
 * </ul>
 *
 * <p><b>Important:</b> The public static fields and the exact keyword matching behavior
 * in <code>.vis</code> files are part of the external contract and must remain compatible
 * with all existing example files (example03, example04, etc.).
 *
 * @since Visualization migration to VTK (Java 25)
 */
public class VisData {

    /** The loaded finite element model (populated by {@link #readData}). */
    static FeModel fem;

    /** Global vector of nodal displacements (length = nNod * nDf). Populated when resultFile is present. */
    static double displ[];

    // ---------------------------------------------------------------------
    // .vis file keywords (exact strings must continue to work)
    // ---------------------------------------------------------------------

    /**
     * Supported top-level keywords in a <code>.vis</code> control file.
     * The parser performs case-insensitive matching via {@code valueOf(toLowerCase())}.
     */
    enum vars {
        /** Path to the mesh file (required). */
        meshfile,
        /** Optional path to results file (displacements + stresses). */
        resultfile,
        /** Scalar field to visualize (see {@link parms}). */
        parm,
        /** Whether to draw element edges (y/n). */
        showedges,
        /** Whether to draw nodes as points (y/n). */
        shownodes,
        /** Minimum subdivision level per edge for curvature/contour quality. */
        ndivmin,
        /** Maximum subdivision level per edge. */
        ndivmax,
        /** Minimum value for contouring (optional). */
        fmin,
        /** Maximum value for contouring (optional). */
        fmax,
        /** Number of contour bands / colors. */
        ncontours,
        /** Scale factor for deformed shape display (0 = no deformation). */
        deformscale,
        /** End of input marker. */
        end
    }

    /**
     * Scalar result parameters that can be contoured on the model surface.
     * These correspond to the nodal values computed by {@link ResultAtNodes}.
     */
    enum parms {
        /** Displacement in X */
        ux,
        /** Displacement in Y */
        uy,
        /** Displacement in Z (3D only) */
        uz,
        /** Normal stress XX */
        sx, sy, sz,
        /** Shear stresses */
        sxy, syz, szx,
        /** Principal stresses */
        s1, s2, s3,
        /** Equivalent (von Mises) stress */
        si,
        /** Another equivalent stress measure (historical) */
        s13,
        /** No field selected (mesh only visualization) */
        none
    }

    // ---------------------------------------------------------------------
    // Public configuration state (legacy contract — do not rename or remove)
    // ---------------------------------------------------------------------

    static String meshFile = null, resultFile = null;
    static parms parm = parms.none;
    static boolean showEdges = true, showNodes = false,
        showDeformShape = false, drawContours = false;
    static double deformScale = 0.0;
    static int nDivMin = 2, nDivMax = 16;
    static double fMin = 0, fMax = 0;
    static int nContours = 256;

    /** Polygon offset used for face rendering (legacy Java3D artifact, kept for compatibility). */
    static float offset = 500.0f;
    static float offsetFactor = 1.0f;

    // Default colors (kept for backward compatibility with old .vis behavior expectations)
    static Color3f bgColor     = new Color3f(1.0f, 1.0f, 1.0f);
    static Color3f modelColor  = new Color3f(0.5f, 0.5f, 0.9f);
    static Color3f surTexColor = new Color3f(0.8f, 0.8f, 0.8f);
    static Color3f edgeColor   = new Color3f(0.2f, 0.2f, 0.2f);
    static Color3f nodeColor   = new Color3f(0.2f, 0.2f, 0.2f);

    /** Size of the 1D color texture used for contouring (legacy). */
    static int textureSize = 256;

    /** Curvature subdivision coefficient: n = 1 + Csub * ro */
    static double Csub = 15;

    /** Contour gradient subdivision coefficient: n = 1 + Fsub * |df| / deltaf */
    static double Fsub = 20;

    /**
     * Main entry point for visualization data loading.
     *
     * <p>Reads the <code>.vis</code> control file, then loads the referenced mesh
     * and (optionally) result file. Populates all static fields used by the
     * VTK-based viewer.
     *
     * @param RD scanner positioned at the start of a <code>.vis</code> file
     */
    public static void readData(FeScanner RD) {

        readDataFile(RD);

        FeScanner fes = new FeScanner(meshFile);
        fem = new FeModel(fes, null);
        Element.fem = fem;
        fem.readData();

        if (resultFile != null) {
            displ = new double[fem.nNod * fem.nDf];
            FeStress stress = new FeStress(fem);
            stress.readResults(resultFile, displ);
            if (deformScale > 0) showDeformShape = true;
            drawContours = VisData.parm != VisData.parms.none;
        }
    }

    /**
     * Parses the <code>.vis</code> control file.
     *
     * <p>This method must preserve exact backward compatibility with all existing
     * <code>.vis</code> files in the repository (example03, example04, etc.).
     *
     * <p>Unknown keywords cause a fatal error via {@link UTIL#errorMsg}.
     */
    static void readDataFile(FeScanner RD) {

        vars name = null;

        while (RD.hasNext()) {

            String varName = RD.next();
            String varNameLower = varName.toLowerCase();
            if (varName.equals("#")) {
                RD.nextLine();
                continue;
            }
            try {
                name = vars.valueOf(varNameLower);
            } catch (Exception e) {
                UTIL.errorMsg("Variable name is not found: " + varName);
            }

            switch (name) {

                case meshfile:
                    meshFile = RD.next();
                    break;
                case resultfile:
                    resultFile = RD.next();
                    break;
                case parm:
                    try {
                        varName = RD.next();
                        parm = parms.valueOf(varName.toLowerCase());
                    } catch (Exception e) {
                        UTIL.errorMsg("No such result parameter: " + varName);
                    }
                    break;
                case showedges:
                    showEdges = RD.next().equalsIgnoreCase("y");
                    break;
                case shownodes:
                    showNodes = RD.next().equalsIgnoreCase("y");
                    break;
                case ndivmin:
                    nDivMin = RD.readInt();
                    break;
                case ndivmax:
                    nDivMax = RD.readInt();
                    break;
                case fmin:
                    fMin = RD.readDouble();
                    break;
                case fmax:
                    fMax = RD.readDouble();
                    break;
                case ncontours:
                    nContours = RD.readInt();
                    break;
                case deformscale:
                    deformScale = RD.readDouble();
                    break;
                case end:
                    return;
            }
        }
    }

}