package visual.jzy;

/**
 * Interaction controllers for the Jzy3D viewer.
 *
 * Replaces previous VTK and Java3D interaction code.
 */
public class JzyInteraction {

    /**
     * Attaches standard mouse/keyboard controllers (orbit, zoom, pan) to a Jzy3D chart or canvas.
     */
    public static void attachDefaultControllers(Object chartOrCanvas) {
        // When Jzy3D is active:
        // chart.addController(new CameraMouseController());
        // chart.addController(new CameraKeyboardController());

        System.out.println("[JzyInteraction] Default controllers would be attached here.");
    }
}