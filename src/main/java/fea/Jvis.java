package fea;

import visual.*;
import util.*;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main class of the FEA visualizer.
 *
 * <p>Reads a visualizer input file (specifying mesh, optional results file and
 * display options), then opens an interactive JavaFX 3D window rendered by
 * {@link visual.J3dScene}.  Mouse controls: left-drag to orbit, right-drag to
 * pan, scroll-wheel to zoom.</p>
 *
 * <p>Usage:
 * <pre>  java fea.Jvis &lt;input-file&gt;</pre>
 * or via Maven:
 * <pre>  mvn javafx:run -Djavafx.args=example03/cube.vis</pre>
 * </p>
 */
public class Jvis extends Application {

    /** Input-file path forwarded from {@link #main(String[])}. */
    private static String inputFile;

    /**
     * Entry point.  Stores the input-file path and launches the
     * JavaFX application lifecycle.
     *
     * @param args command-line arguments; args[0] is the vis input file
     */
    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Usage: java fea.Jvis <input-file>");
            return;
        }
        FE.main = FE.JVIS;
        inputFile = args[0];
        System.out.println("fea.Jvis: Visualization. Data file: " + inputFile);
        Application.launch(args);
    }

    /**
     * JavaFX start callback — loads the model data and creates the 3D scene.
     *
     * @param stage the primary stage provided by the JavaFX runtime
     */
    @Override
    public void start(Stage stage) {

        FeScanner rd = new FeScanner(inputFile);
        try {
            VisData.readData(rd);
        } finally {
            rd.close();
        }
        new J3dScene(stage);
    }
}
