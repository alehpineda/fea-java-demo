package visual;

import javafx.scene.SubScene;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

/**
 * Attaches mouse-driven rotate / zoom / translate handlers to the 3D sub-scene.
 *
 * <ul>
 *   <li>Left-button drag → orbit (rotate around X and Y axes)</li>
 *   <li>Right-button drag → pan (translate in X / Y)</li>
 *   <li>Scroll wheel → zoom (uniform scale)</li>
 * </ul>
 */
public class MouseInteraction {

    private static final double ROTATE_SPEED   = 0.3;
    private static final double TRANSLATE_SPEED = 0.002;
    private static final double ZOOM_FACTOR     = 1.1;

    /**
     * Registers all mouse handlers on {@code subScene}.
     * The supplied transforms are applied directly to the world group.
     *
     * @param subScene   the 3D sub-scene receiving mouse events
     * @param rotX       rotation around the X axis
     * @param rotY       rotation around the Y axis
     * @param translateT pan translation
     * @param scaleT     zoom scale
     */
    public static void attachHandlers(SubScene subScene,
                                      Rotate rotX, Rotate rotY,
                                      Translate translateT, Scale scaleT) {

        double[] lastX = {0};
        double[] lastY = {0};

        subScene.setOnMousePressed(e -> {
            lastX[0] = e.getSceneX();
            lastY[0] = e.getSceneY();
        });

        subScene.setOnMouseDragged(e -> {
            double dx = e.getSceneX() - lastX[0];
            double dy = e.getSceneY() - lastY[0];

            if (e.isPrimaryButtonDown()) {
                // Orbit: drag up/down tilts, drag left/right spins
                rotX.setAngle(rotX.getAngle() - dy * ROTATE_SPEED);
                rotY.setAngle(rotY.getAngle() + dx * ROTATE_SPEED);
            } else if (e.isSecondaryButtonDown()) {
                // Pan
                translateT.setX(translateT.getX() + dx * TRANSLATE_SPEED);
                translateT.setY(translateT.getY() + dy * TRANSLATE_SPEED);
            }

            lastX[0] = e.getSceneX();
            lastY[0] = e.getSceneY();
        });

        subScene.setOnScroll(e -> {
            double factor = e.getDeltaY() > 0 ? ZOOM_FACTOR : 1.0 / ZOOM_FACTOR;
            scaleT.setX(scaleT.getX() * factor);
            scaleT.setY(scaleT.getY() * factor);
            scaleT.setZ(scaleT.getZ() * factor);
        });
    }
}

