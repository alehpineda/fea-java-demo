package visual.vtk;

import java.util.Locale;

/**
 * Centralized, robust loader for VTK native libraries.
 *
 * This class is the single place that should be called before any VTK Java code runs.
 *
 * Recommended usage (2026):
 * <pre>
 *   if (!VtkNativeLoader.initialize()) {
 *       System.err.println("Failed to load VTK natives. Visualization disabled.");
 *       return;
 *   }
 *   // Now safe to use vtk.* classes
 * </pre>
 *
 * It prefers the easier jzy3d wrapper (`org.jzy3d:vtk-java-all` or similar) when present,
 * falling back to raw VTK native loading.
 *
 * See visual/vtk/NOTES.md for current dependency recommendations.
 */
public final class VtkNativeLoader {

    private static boolean initialized = false;
    private static String loadedVia = null;

    private VtkNativeLoader() {}

    /**
     * Initialize VTK native libraries. Safe to call multiple times.
     *
     * @return true if natives were successfully loaded (or already loaded)
     */
    public static synchronized boolean initialize() {
        if (initialized) {
            return true;
        }

        // Try jzy3d helper first (recommended path)
        if (tryLoadViaJzy3d()) {
            initialized = true;
            loadedVia = "jzy3d";
            System.out.println("[VtkNativeLoader] VTK natives loaded via jzy3d helper.");
            return true;
        }

        // Fall back to raw VTK
        if (tryLoadRawVtk()) {
            initialized = true;
            loadedVia = "raw-vtk";
            System.out.println("[VtkNativeLoader] VTK natives loaded via raw VTK mechanism.");
            return true;
        }

        System.err.println("[VtkNativeLoader] FAILED to load VTK native libraries.");
        System.err.println("  - Make sure either the jzy3d vtk-java-all artifact or raw VTK natives are on the classpath.");
        System.err.println("  - For raw VTK you usually need a PAT for GitHub Packages + platform native JARs.");
        System.err.println("  See src/main/java/visual/vtk/NOTES.md for setup instructions.");
        return false;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static String getLoadedVia() {
        return loadedVia;
    }

    private static boolean tryLoadViaJzy3d() {
        try {
            // The jzy3d wrapper usually provides a convenient static initializer
            Class<?> helper = Class.forName("org.jzy3d.vtk.VtkNativeLibraries");
            // Preferred method in recent jzy3d VTK wrappers
            java.lang.reflect.Method m = helper.getMethod("initialize");
            Boolean result = (Boolean) m.invoke(null);
            return Boolean.TRUE.equals(result);
        } catch (ClassNotFoundException e) {
            // jzy3d not present — normal case when using raw VTK
            return false;
        } catch (Exception e) {
            System.err.println("[VtkNativeLoader] jzy3d native loader failed: " + e.getMessage());
            return false;
        }
    }

    private static boolean tryLoadRawVtk() {
        try {
            // Raw VTK 9.x way
            Class<?> vtkNativeLibrary = Class.forName("vtk.vtkNativeLibrary");
            java.lang.reflect.Method loadAll = vtkNativeLibrary.getMethod("LoadAllNativeLibraries");
            Boolean result = (Boolean) loadAll.invoke(null);
            if (Boolean.TRUE.equals(result)) {
                // Also disable the annoying output window
                java.lang.reflect.Method disable = vtkNativeLibrary.getMethod("DisableOutputWindow", (Class<?>[]) null);
                disable.invoke(null);
                return true;
            }
            return false;
        } catch (ClassNotFoundException e) {
            return false;
        } catch (Exception e) {
            System.err.println("[VtkNativeLoader] Raw VTK native loading failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Quick platform hint (useful for diagnostics).
     */
    public static String getPlatformHint() {
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);
        if (os.contains("win")) return "windows-" + arch;
        if (os.contains("mac")) return "macos-" + arch;
        if (os.contains("linux")) return "linux-" + arch;
        return os + "-" + arch;
    }
}
