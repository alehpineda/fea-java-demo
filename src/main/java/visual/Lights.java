package visual;

import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.DirectionalLight;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

/**
 * Adds ambient and directional lights to the JavaFX 3D scene.
 *
 * <p>Mirrors the original Java 3D lighting setup:
 * one white ambient light and two directional lights from different
 * angles to give depth cues on the FE surface.</p>
 */
public class Lights {

    /**
     * Adds lighting nodes to {@code root}.
     *
     * @param root the group that receives the light nodes
     */
    public static void setLights(Group root) {

        AmbientLight ambient = new AmbientLight(Color.color(0.5, 0.5, 0.5));
        root.getChildren().add(ambient);

        root.getChildren().add(makeDirectionalLight(
                Color.color(0.6, 0.6, 0.6),  4f, -7f, -12f));
        root.getChildren().add(makeDirectionalLight(
                Color.color(0.4, 0.4, 0.4), -5f, -3f,  -1f));
    }

    /**
     * Creates a {@link DirectionalLight} aimed in the given direction.
     * JavaFX directional lights default to the +Z axis; we rotate the
     * node to the requested direction.
     *
     * @param color     light colour
     * @param x         x-component of the direction vector (need not be normalised)
     * @param y         y-component
     * @param z         z-component
     * @return a {@link Group} containing the rotated light node
     */
    private static Group makeDirectionalLight(Color color,
                                              double x, double y, double z) {
        DirectionalLight light = new DirectionalLight(color);
        double len = Math.sqrt(x * x + y * y + z * z);
        if (len < 1e-10) {
            return new Group(light);
        }
        double nx = x / len;
        double ny = y / len;
        double nz = z / len;
        // Default direction of DirectionalLight in JavaFX is (0, 0, 1).
        // We need to rotate (0, 0, 1) onto (nx, ny, nz).
        double cosA = nz;  // dot product with (0,0,1)
        if (Math.abs(cosA - 1.0) < 1e-10) {
            return new Group(light);  // already aligned
        }
        double angle;
        Point3D axis;
        if (Math.abs(cosA + 1.0) < 1e-10) {
            // Opposite direction: rotate 180° around X
            angle = 180.0;
            axis = Rotate.X_AXIS;
        } else {
            angle = Math.toDegrees(Math.acos(cosA));
            // Axis = (0,0,1) × (nx,ny,nz) = (-ny, nx, 0)
            axis = new Point3D(-ny, nx, 0).normalize();
        }
        light.getTransforms().add(new Rotate(angle, axis));
        return new Group(light);
    }
}

