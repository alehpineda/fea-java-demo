package visual.vtk;

/**
 * Interaction styles for the VTK viewer.
 *
 * Direct modern replacement for the old {@code MouseInteraction.java}.
 */
public class VtkInteraction {

    /**
     * Returns the recommended interaction style for FEA model exploration.
     *
     * Uses vtkInteractorStyleTrackballCamera (orbit with left button,
     * pan with right button, zoom with middle or wheel).
     */
    public static Object getDefaultInteractorStyle() {
        // When VTK is available:
        // vtkInteractorStyleTrackballCamera style = new vtkInteractorStyleTrackballCamera();
        // return style;

        System.out.println("[VtkInteraction] Default style: TrackballCamera (orbit/zoom/pan)");
        return null;
    }

    /**
     * Returns a style suitable for more precise manipulation (if needed later).
     */
    public static Object getTrackballActorStyle() {
        // vtkInteractorStyleTrackballActor style = new vtkInteractorStyleTrackballActor();
        // return style;
        return null;
    }
}
