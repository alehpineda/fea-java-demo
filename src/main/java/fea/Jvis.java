package fea;

import visual.*;
import util.*;

import java.applet.Applet;
import com.sun.j3d.utils.applet.MainFrame;

// Main class of the visualizer
//
// @deprecated Legacy Java3D + Applet implementation.
// Use visual.vtk.VtkLauncher instead (activated via -Pvisual-vtk profile).
// See visual/vtk/CLEANUP_PLAN.md for migration status.
public class Jvis extends Applet {

    public static FeScanner RD = null;

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println(
                    "Usage: java fea.Jvis FileIn \n");
            return;
        }
        FE.main = FE.JVIS;

        RD = new FeScanner(args[0]);
        System.out.println("fea.Jvis: Visualization." +
                " Data file: " + args[0]);

        new MainFrame(new Jvis(), 800, 600);
    }

    public Jvis() {

        VisData.readData(RD);

        new J3dScene(this);

    }

}