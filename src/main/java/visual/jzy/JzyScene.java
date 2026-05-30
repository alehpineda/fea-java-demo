package visual.jzy;

import visual.SurfaceSubGeometry;
import visual.VisData;

/**
 * Jzy3D-based 3D viewer scene for FEA results.
 *
 * This is the replacement for the previous VTK-based implementation
 * (visual.vtk.VtkScene) and the original Java3D viewer.
 *
 * Responsibilities:
 * - Consume data prepared from the reusable pipeline (VisData, ResultAtNodes,
 *   SurfaceSubGeometry, etc.)
 * - Render surfaces with scalar coloring (contours)
 * - Support deformed shape
 * - Provide interaction, edges/nodes, and colorbar
 *
 * Jzy3D strengths for this use case: excellent colormapping, surfaces, and
 * relatively lightweight native handling compared to raw VTK.
 */
public class JzyScene {

    private SurfaceSubGeometry surfaceData;

    public JzyScene() {
        // Data will be loaded when show() is called or via explicit load method
    }

    /**
     * Loads / refreshes the visualization data from the current VisData state.
     * This reuses the existing battle-tested SurfaceSubGeometry + ResultAtNodes logic.
     */
    public void loadData() {
        this.surfaceData = new SurfaceSubGeometry();
        // At this point surfaceData.xyzSurface, norSurface, and scalar data (via fun[] or tex)
        // are populated and ready for Jzy3D.
    }

    /**
     * Main entry point to display the viewer.
     * Should be called after native libraries are initialized (Jzy3D handles most of this).
     */
    public void show() {
        loadData();

        if (surfaceData == null || surfaceData.xyzSurface == null) {
            System.err.println("[JzyScene] No surface data available.");
            return;
        }

        // ============================================================
        // Jzy3D rendering (activated when profile brings in dependencies)
        // ============================================================
        //
        // Example structure (to be filled when Jzy3D is on classpath):
        //
        // Chart chart = new Chart(new Quality(), "swing");
        // Surface surface = new Surface();
        // surface.setData(...); // from surfaceData
        //
        // ColorMapper mapper = (ColorMapper) JzyColorMapper.createScalarColorMapper(
        //         surfaceData.fmin, surfaceData.fmax);
        // surface.setColorMapper(mapper);
        //
        // chart.getScene().getGraph().add(surface);
        // JzyInteraction.attachDefaultControllers(chart);
        // chart.add(JzyColorBar.createColorBar(mapper));
        //
        // JFrame frame = new JFrame("FEA Jzy3D Viewer");
        // frame.setContentPane((Component) chart.getCanvas());
        // frame.pack();
        // frame.setVisible(true);

        System.out.println("[JzyScene] Jzy3D viewer show() called.");
        System.out.println("             Surface vertices: " + (surfaceData.xyzSurface.length / 3));
        System.out.println("             (Full Jzy3D rendering not yet wired in this skeleton)");
    }

    // Control methods (to be implemented)
    // public void setShowEdges(boolean show) { ... }
    // public void setShowNodes(boolean show) { ... }
    // public void setDeformScale(double scale) { ... }
}