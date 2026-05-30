package visual.vtk;

import vtk.*;

/**
 * Minimal VTK Java "hello world" for the FEA visualization migration.
 *
 * This class proves:
 * - VTK native library loading works in our environment
 * - Basic pipeline (source → mapper → actor → renderer → window) can be constructed
 *
 * Real FEA viewer code will live in sibling classes (VtkScene, VtkDataBridge, etc.)
 * and will consume the existing modernized Java pipeline:
 *   FeModel + SurfaceGeometry + ResultAtNodes + VisData
 *
 * References (2026):
 * - VTK 9.6 Java wrappers (org.vtk:vtk-java11 from Kitware GitHub Packages)
 * - vtkNativeLibrary loading pattern (official docs + 9.6 release notes)
 *
 * Run (after configuring GitHub Packages auth + native JARs):
 *   mvn -Pvisual-vtk compile exec:java -Dexec.mainClass="visual.vtk.VtkHello"
 */
public class VtkHello {

    static {
        if (!VtkNativeLoader.initialize()) {
            throw new UnsatisfiedLinkError("VTK native libraries could not be loaded. " +
                    "See VtkNativeLoader and NOTES.md for setup help.");
        }
    }

    public static void main(String[] args) {
        // Classic VTK "cone" example — proves the Java bindings and rendering pipeline work.
        vtkConeSource cone = new vtkConeSource();
        cone.SetResolution(8);

        vtkPolyDataMapper coneMapper = new vtkPolyDataMapper();
        coneMapper.SetInputConnection(cone.GetOutputPort());

        vtkActor coneActor = new vtkActor();
        coneActor.SetMapper(coneMapper);

        vtkRenderer ren = new vtkRenderer();
        ren.AddActor(coneActor);
        ren.SetBackground(0.1, 0.2, 0.4);

        vtkRenderWindow renWin = new vtkRenderWindow();
        renWin.AddRenderer(ren);
        renWin.SetSize(600, 480);
        renWin.SetWindowName("FEA Java Demo - VTK Hello (migration prototype)");

        vtkRenderWindowInteractor iren = new vtkRenderWindowInteractor();
        iren.SetRenderWindow(renWin);

        // Simple interactor style (trackball/rotate + zoom + pan)
        vtkInteractorStyleTrackballCamera style = new vtkInteractorStyleTrackballCamera();
        iren.SetInteractorStyle(style);

        renWin.Render();
        iren.Initialize();
        iren.Start();

        // Cleanup (important for native resources)
        iren.Delete();
        renWin.Delete();
        ren.Delete();
        coneActor.Delete();
        coneMapper.Delete();
        cone.Delete();
    }
}
