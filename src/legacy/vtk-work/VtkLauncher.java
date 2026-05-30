package visual.vtk;

import fea.FE;
import util.FeScanner;
import visual.VisData;

/**
 * Modern replacement launcher for the visualization module.
 *
 * Replaces the old Applet-based Jvis.java + MainFrame.
 *
 * Usage remains compatible:
 *   java ... visual.vtk.VtkLauncher hole3d.vis
 */
public class VtkLauncher {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java visual.vtk.VtkLauncher FileIn.vis");
            return;
        }

        FE.main = FE.JVIS;   // keep for compatibility if anything checks it

        try {
            FeScanner rd = new FeScanner(args[0]);
            System.out.println("fea.VtkLauncher (VTK): Visualization data file: " + args[0]);

            VisData.readData(rd);

            // Initialize natives early
            if (!VtkNativeLoader.initialize()) {
                System.err.println("VTK visualization cannot start — native libraries failed to load.");
                System.exit(1);
            }

            VtkDataBridge bridge = new VtkDataBridge(VisData.fem, VisData.displ, VisData.parm);
            bridge.buildVtkDataset();

            VtkScene scene = new VtkScene(bridge);
            scene.show();

            System.out.println("[VtkLauncher] Visualization session started for " + args[0]);

        } catch (Exception e) {
            System.err.println("Error starting VTK visualizer: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
