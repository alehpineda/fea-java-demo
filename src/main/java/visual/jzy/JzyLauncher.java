package visual.jzy;

import fea.FE;
import util.FeScanner;
import visual.VisData;

/**
 * Modern Jzy3D-based launcher for the visualization module.
 *
 * This will eventually replace `visual.vtk.VtkLauncher` (and the legacy `fea.Jvis`).
 * Command line usage should remain compatible with existing `.vis` files.
 */
public class JzyLauncher {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java visual.jzy.JzyLauncher FileIn.vis");
            return;
        }

        FE.main = FE.JVIS;

        try {
            FeScanner rd = new FeScanner(args[0]);
            System.out.println("Jzy3D Visualizer: Data file = " + args[0]);

            VisData.readData(rd);

            // Jzy3D native initialization is usually handled automatically when
            // creating charts, but we can add explicit loader calls here later.

            JzyScene scene = new JzyScene();
            scene.show();

            System.out.println("[JzyLauncher] Jzy3D visualization session started for " + args[0]);

        } catch (Exception e) {
            System.err.println("Failed to start Jzy3D visualizer: " + e.getMessage());
            e.printStackTrace();
        }
    }
}