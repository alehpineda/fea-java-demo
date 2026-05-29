package fea;

import model.FeModel;
import util.*;

import java.io.*;
import java.util.HashMap;

// Main class of the mesh generator
public class Jmgen {

    public static FeScanner RD;
    public static PrintWriter PR;
    public static HashMap<String, FeModel> blocks;

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println(
                "Usage: java fea.Jmgen FileIn [FileOut]\n");
            return;
        }
        FE.main = FE.JMGEN;

        try {
            RD = new FeScanner(args[0]);

            String fileOut = (args.length == 1) ?
                    args[0]+".lst" : args[1];
            PR = new FePrintWriter().getPrinter(fileOut);

            PR.println("fea.Jmgen: Mesh generator. Data file: "
                    + args[0]);
            System.out.println("fea.Jmgen: Mesh generator. "
                    + "Data file: " + args[0]);

            new Jmgen();
        } catch (FeaException e) {
            System.out.println("=== ERROR: " + e.getMessage());
            if (PR != null) {
                PR.println("=== ERROR: " + e.getMessage());
            }
            System.exit(1);
        } finally {
            if (RD != null) {
                RD.close();
            }
            if (PR != null) {
                PR.close();
            }
        }
    }

    Jmgen() {

        UTIL.printDate(PR);

        // Hash table for storing mesh blocks
        blocks = new HashMap<>();

        while (RD.hasNext()) {

            String name = RD.next().toLowerCase();
            if (name.equals("#")) { RD.nextLine(); continue; }
            PR.println("------------------------------------");

            Runnable command = JmgenRegistry.command(name);
            if (command == null) {
                UTIL.errorMsg("Class name not found: "+name);
            }
            command.run();
        }
    }

}
