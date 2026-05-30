package visual.vtk;

import visual.VisData;

/**
 * Helper for creating a professional scalar bar / color legend using VTK.
 *
 * Reproduces the classic rainbow used in the original Java3D viewer:
 * Magenta → Blue → Cyan → Green → Yellow → Red (6 control points).
 *
 * This replaces the old texture-based {@code ColorScale} hack.
 */
public class VtkScalarBar {

    /**
     * Creates a VTK scalar bar actor (vtkScalarBarActor) ready to be added to the renderer.
     *
     * Call this after you have a mapper with scalar range set.
     */
    public static Object createScalarBar() {
        // When VTK is available under the profile:
        //
        // vtkScalarBarActor scalarBar = new vtkScalarBarActor();
        // scalarBar.SetLookupTable( (vtkLookupTable) createClassicRainbowLookupTable(0, 1) );
        // scalarBar.SetTitle(VisData.parm.name().toUpperCase());
        // scalarBar.SetNumberOfLabels(5);
        // scalarBar.SetPosition(0.85, 0.1);
        // scalarBar.SetWidth(0.08);
        // scalarBar.SetHeight(0.7);
        //
        // return scalarBar;

        System.out.println("[VtkScalarBar] Scalar bar requested for " + VisData.nContours + " contours, parm=" + VisData.parm);
        return null;
    }

    /**
     * Builds the classic FEA 6-color rainbow lookup table used by the original viewer.
     *
     * Colors (in order from low to high):
     * Magenta (1,0,1) → Blue (0,0,1) → Cyan (0,1,1) → Green (0,1,0) → Yellow (1,1,0) → Red (1,0,0)
     */
    public static Object createClassicRainbowLookupTable(double min, double max) {
        // When VTK is on the classpath:
        //
        // vtkLookupTable lut = new vtkLookupTable();
        // lut.SetRange(min, max);
        // lut.SetNumberOfColors(256);
        //
        // // Define the 6 control points
        // double[][] colors = {
        //     {1.0, 0.0, 1.0}, // Magenta
        //     {0.0, 0.0, 1.0}, // Blue
        //     {0.0, 1.0, 1.0}, // Cyan
        //     {0.0, 1.0, 0.0}, // Green
        //     {1.0, 1.0, 0.0}, // Yellow
        //     {1.0, 0.0, 0.0}  // Red
        // };
        //
        // lut.Build();
        // // Interpolate between the 6 points
        // for (int i = 0; i < 256; i++) {
        //     double t = i / 255.0;
        //     // linear interpolation across the 5 segments
        //     int seg = (int)(t * 5);
        //     double localT = (t * 5) - seg;
        //     if (seg >= 5) { seg = 4; localT = 1.0; }
        //
        //     double r = colors[seg][0] * (1-localT) + colors[seg+1][0] * localT;
        //     double g = colors[seg][1] * (1-localT) + colors[seg+1][1] * localT;
        //     double b = colors[seg][2] * (1-localT) + colors[seg+1][2] * localT;
        //
        //     lut.SetTableValue(i, r, g, b, 1.0);
        // }
        //
        // return lut;

        System.out.println("[VtkScalarBar] Would create classic rainbow LUT [" + min + ", " + max + "]");
        return null;
    }
}
