package visual.vtk;

import model.*;
import visual.*;

/**
 * Bridge between the existing Java FEA data pipeline and VTK data structures.
 *
 * Responsibilities (planned):
 * - Take a loaded FeModel + optional displacement/stress results
 * - Extract surface geometry (reusing or adapting SurfaceGeometry / SurfaceSubGeometry)
 * - Populate vtkUnstructuredGrid or vtkPolyData with points, cells (quadratic hex/quad),
 *   and point/cell data (displacements, stresses, principals, equivalent stress).
 * - Apply deformation via vtkWarpVector when requested.
 *
 * This class will be the main integration point between the battle-tested
 * legacy result computation (ResultAtNodes, element extrapolation) and VTK rendering.
 *
 * @see VtkScene
 */
public class VtkDataBridge {

    private final FeModel fem;
    private final double[] displ;   // may be null
    private final VisData.parms parm;

    public VtkDataBridge(FeModel fem, double[] displ, VisData.parms parm) {
        this.fem = fem;
        this.displ = displ;
        this.parm = parm;
    }

    /**
     * Builds the VTK dataset from the current FeModel + results.
     *
     * This method reuses the existing battle-tested pipeline:
     * - SurfaceGeometry / SurfaceSubGeometry for surface extraction
     * - ResultAtNodes for correct nodal scalar values (including principals & equivalent stress)
     *
     * VTK-specific population (vtkPoints, vtkPolyData, point data arrays, warp, etc.)
     * will be added once the VTK dependency is reliably resolvable.
     */
    public void buildVtkDataset() {
        // Preferred public API for consumers
        PreparedSurfaceData data = getPreparedSurfaceData();

        System.out.println("[VtkDataBridge] Prepared surface: " +
                           (data.points.length / 3) + " vertices, " +
                           (data.triangleIndices.length / 3) + " triangles.");

        if (data.pointScalars != null) {
            System.out.println("[VtkDataBridge] Scalar field prepared for parm=" + parm +
                               " (range " + data.scalarMin + " .. " + data.scalarMax + ")");
        }
        if (data.deformed) {
            System.out.println("[VtkDataBridge] Deformed shape included (scale=" + VisData.deformScale + ")");
        }
        if (data.displacementVectors != null) {
            System.out.println("[VtkDataBridge] Displacement vectors prepared for vtkWarpVector");
        }

        // TODO (vtk-9 full):
        // Feed 'data' into VTK:
        // - vtkPoints + normals
        // - vtkCellArray from triangleIndices
        // - point data scalars + lookup table
        // - optional vtkWarpVector using original displ + scale
    }

    // ---------------------------------------------------------------------
    // Public data access for VtkScene (pure Java layer)
    // ---------------------------------------------------------------------

    /**
     * Simple container for data ready to be turned into VTK structures.
     */
    public static class PreparedSurfaceData {
        public final float[] points;           // x,y,z interleaved (already deformed if requested)
        public final int[] triangleIndices;    // 3 per triangle
        public final float[] pointScalars;     // one per vertex (or null)
        public final float[] normals;          // x,y,z per vertex (or null)
        public final float[] displacementVectors; // 3 per vertex (original displ scaled) - for vtkWarpVector
        public final double scalarMin;
        public final double scalarMax;
        public final boolean deformed;

        PreparedSurfaceData(float[] points, int[] triangleIndices, float[] pointScalars,
                            float[] normals, float[] displacementVectors,
                            double scalarMin, double scalarMax, boolean deformed) {
            this.points = points;
            this.triangleIndices = triangleIndices;
            this.pointScalars = pointScalars;
            this.normals = normals;
            this.displacementVectors = displacementVectors;
            this.scalarMin = scalarMin;
            this.scalarMax = scalarMax;
            this.deformed = deformed;
        }
    }

    /**
     * Returns fully prepared data for the current visualization request.
     * This is the main API VtkScene should call.
     */
    public PreparedSurfaceData getPreparedSurfaceData() {
        SurfaceSubGeometry sub = createSurfaceGeometry();
        double[] scalars = prepareScalarsForCurrentParm(sub);

        float[] points = extractSurfacePoints(sub);
        int[] triIndices = extractTriangleIndices(sub);
        float[] pointScalars = (scalars != null) ? mapScalarsToSurfacePoints(sub, scalars) : null;
        float[] normals = extractNormals(sub);
        float[] dispVectors = extractDisplacementVectorsForWarp();

        boolean isDeformed = VisData.showDeformShape && VisData.deformScale > 0;

        return new PreparedSurfaceData(
            points, triIndices, pointScalars, normals, dispVectors,
            sub.fmin, sub.fmax, isDeformed
        );
    }

    // ---------------------------------------------------------------------
    // Data preparation helpers (pure Java — can be developed and tested now)
    // ---------------------------------------------------------------------

    /**
     * Extracts the actual subdivided surface points from SurfaceSubGeometry.
     * Applies deformation scaling if requested in VisData.
     */
    private float[] extractSurfacePoints(SurfaceSubGeometry sub) {
        // SurfaceSubGeometry populates xyzSurface as float[3 * nVertices]
        float[] src = sub.xyzSurface;
        if (src == null) return new float[0];

        float[] points = new float[src.length];

        if (VisData.showDeformShape && VisData.deformScale > 0 && VisData.displ != null) {
            // Apply deformation: we need to map from full nodal displ to surface points.
            // For simplicity in first cut we use the already deformed coords if legacy path did it,
            // otherwise we could do explicit warping here.
            // Current legacy SurfaceGeometry already modifies coordinates when showDeformShape is true.
            System.arraycopy(src, 0, points, 0, src.length);
        } else {
            System.arraycopy(src, 0, points, 0, src.length);
        }
        return points;
    }

    /**
     * Extracts flat triangle index list from the subdivided surface.
     * Since SurfaceSubGeometry stores vertices in strict triangle order (3 per triangle),
     * the indices are simply 0,1,2,3,4,5,...
     */
    private int[] extractTriangleIndices(SurfaceSubGeometry sub) {
        int numTris = sub.nVertices / 3;
        int[] indices = new int[numTris * 3];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }
        return indices;
    }

    private float[] mapScalarsToSurfacePoints(SurfaceSubGeometry sub, double[] scalars) {
        if (scalars == null) return null;

        // The legacy path already computed nodal scalars in sub.fun[] via ResultAtNodes.
        float[] out = new float[scalars.length];
        for (int i = 0; i < scalars.length; i++) {
            out[i] = (float) scalars[i];
        }
        return out;
    }

    private float[] extractNormals(SurfaceSubGeometry sub) {
        float[] src = sub.norSurface;
        if (src == null || src.length == 0) return null;
        float[] n = new float[src.length];
        System.arraycopy(src, 0, n, 0, src.length);
        return n;
    }

    /**
     * Builds displacement vectors (3 components per surface vertex) scaled for vtkWarpVector.
     * This allows clean deformation in VTK without mutating the original FeModel.
     */
    private float[] extractDisplacementVectorsForWarp() {
        if (VisData.displ == null || VisData.displ.length == 0) {
            return null;
        }

        // Conservative size (actual surface vertices may be fewer).
        // For a first working version we allocate for all nodes and let VTK ignore extras.
        int nNodes = VisData.fem.nNod;
        float[] vectors = new float[nNodes * 3];

        double scale = 0.0;
        if (VisData.showDeformShape && VisData.deformScale > 0) {
            // Compute the same scale the legacy code uses
            // (we could also just return raw displ and let the caller scale, but matching legacy is safer)
            double displMax = 0;
            for (int i = 0; i < nNodes; i++) {
                double d = 0;
                for (int j = 0; j < VisData.fem.nDim; j++) {
                    double val = VisData.displ[i * VisData.fem.nDim + j];
                    d += val * val;
                }
                displMax = Math.max(displMax, d);
            }
            displMax = Math.sqrt(displMax);
            if (displMax > 1e-12) {
                // We need sizeMax from SurfaceGeometry. For now use a reasonable default.
                // In a later polish we can expose bounding box info from the bridge.
                double sizeMax = 1.0; // placeholder – will be improved in next data polish
                scale = sizeMax * VisData.deformScale / displMax;
            }
        }

        for (int i = 0; i < nNodes; i++) {
            for (int j = 0; j < VisData.fem.nDim; j++) {
                vectors[i * 3 + j] = (float) (VisData.displ[i * VisData.fem.nDim + j] * scale);
            }
        }
        return vectors;
    }

    /**
     * Creates the subdivided surface geometry (the key reusable legacy component).
     */
    private SurfaceSubGeometry createSurfaceGeometry() {
        return new SurfaceSubGeometry();
    }

    /**
     * Prepares the scalar array for the currently selected VisData.parm.
     * Reuses ResultAtNodes logic where possible for correctness.
     */
    private double[] prepareScalarsForCurrentParm(SurfaceSubGeometry sub) {
        if (parm == VisData.parms.none || VisData.displ == null) {
            return null;
        }

        // ResultAtNodes was already run during VisData.readData() if drawContours was set.
        // For now we return the fun array it populated on the surface.
        // In a fuller implementation we may re-run or convert it explicitly.
        return sub.fun;
    }
}
