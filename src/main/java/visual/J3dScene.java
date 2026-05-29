package visual;

import javafx.scene.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.*;
import javafx.stage.Stage;

/**
 * JavaFX 3D scene for finite element visualization.
 *
 * <p>Replaces the former Java 3D / Applet-based scene.  Responsibilities:</p>
 * <ol>
 *   <li>Trigger surface subdivision via {@link SurfaceSubGeometry}.</li>
 *   <li>Build the JavaFX scene graph (world group, lights, camera, sub-scene).</li>
 *   <li>Configure the {@link PhongMaterial} for solid or contour rendering.</li>
 *   <li>Attach mouse interaction handlers.</li>
 *   <li>Show the {@link Stage}.</li>
 * </ol>
 */
public class J3dScene {

    /**
     * Constructs the scene and shows it in the supplied stage.
     *
     * @param stage the primary JavaFX stage
     */
    public J3dScene(Stage stage) {

        SurfaceSubGeometry sub = new SurfaceSubGeometry();

        // Transforms applied to the world group for interactive manipulation
        Scale     scaleT     = new Scale(sub.getScale(), sub.getScale(), sub.getScale());
        Rotate    rotX       = new Rotate(20,  Rotate.X_AXIS);
        Rotate    rotY       = new Rotate(-30, Rotate.Y_AXIS);
        Translate translateT = new Translate();

        Group world = new Group();
        // Apply transforms in order: translate → rotY → rotX → scale
        world.getTransforms().addAll(translateT, rotY, rotX, scaleT);

        Lights.setLights(world);

        // Element faces
        MeshView facesView = sub.getModelMesh();
        configureMaterial(facesView, sub);
        world.getChildren().add(facesView);

        // Mesh edges
        if (VisData.showEdges) {
            world.getChildren().add(sub.getModelEdges());
        }

        // Surface nodes
        if (VisData.showNodes) {
            world.getChildren().add(sub.getModelNodes());
        }

        // Camera positioned along -Z looking at origin
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.001);
        camera.setFarClip(100.0);
        camera.setTranslateZ(-2.4);

        SubScene subScene = new SubScene(world, 800, 600, true, SceneAntialiasing.BALANCED);
        subScene.setFill(VisData.bgColor);
        subScene.setCamera(camera);

        MouseInteraction.attachHandlers(subScene, rotX, rotY, translateT, scaleT);

        StackPane root = new StackPane(subScene);
        Scene scene = new Scene(root, 800, 600);

        // Keep the 3D sub-scene filling the window
        subScene.widthProperty().bind(root.widthProperty());
        subScene.heightProperty().bind(root.heightProperty());

        stage.setTitle("FEA Visualization");
        stage.setScene(scene);
        stage.show();

        printInfo(sub);
    }

    /** Applies a {@link PhongMaterial} to the mesh: plain colour or contour texture. */
    private void configureMaterial(MeshView facesView, SurfaceSubGeometry sub) {

        PhongMaterial mat = new PhongMaterial();
        if (VisData.drawContours) {
            ColorScale scale = new ColorScale();
            mat.setDiffuseMap(scale.getImage());
            mat.setDiffuseColor(VisData.surTexColor);
        } else {
            mat.setDiffuseColor(VisData.modelColor);
            mat.setSpecularColor(Color.color(0.9, 0.9, 0.9));
            mat.setSpecularPower(16.0);
        }
        facesView.setMaterial(mat);
        facesView.setDrawMode(DrawMode.FILL);
    }

    /** Prints a short summary of the rendered scene to standard output. */
    private void printInfo(SurfaceSubGeometry sub) {

        System.out.println(" Number of polygons = " + sub.nVertices / 3);
        if (VisData.showDeformShape) {
            System.out.printf(" Deformed shape: max displacement = %4.2f max size%n",
                    VisData.deformScale);
        }
        if (VisData.drawContours) {
            System.out.printf(" Contours: %d colors (Magenta-Blue-Cyan-Green-Yellow-Red)%n",
                    VisData.nContours);
            System.out.printf(" %s: Fmin = %10.4e, Fmax = %10.4e%n",
                    VisData.parm, sub.fmin, sub.fmax);
        }
    }
}
