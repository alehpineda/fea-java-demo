# Technology Stack

## Core Sections (Required)

### 1) Runtime Summary

| Area | Value | Evidence |
|------|-------|----------|
| Primary language | Java | `src/**/*.java` (44 source files) |
| Runtime + version | Java 1.6+ (legacy, documented); Java 17 available in environment | `README.txt`, `jc.bat` |
| Package manager | None (manual javac invocation) | `jc.bat` |
| Module/build system | None (hand-crafted `jc.bat` batch script) | `jc.bat` |

### 2) Production Frameworks and Dependencies

| Dependency | Version | Role in system | Evidence |
|------------|---------|----------------|----------|
| Java SE standard library | 1.6+ | Core runtime, file I/O, Scanner, collections | All `src/**/*.java` |
| Java 3D (`javax.media.j3d`) | Unknown (legacy) | 3D scene rendering in visualizer | `src/visual/J3dScene.java`, `src/visual/Lights.java` |
| Java 3D utils (`com.sun.j3d.utils.*`) | Unknown (legacy) | Applet integration and universe setup | `src/fea/Jvis.java`, `src/visual/J3dScene.java` |
| Java Applet (`java.applet.Applet`) | JDK legacy | Hosts the visualization window | `src/fea/Jvis.java` |
| `javax.vecmath` | Bundled with Java 3D | 3D math (Point3f, Color3f, etc.) | `src/visual/VisData.java`, `src/visual/SurfaceGeometry.java` |

### 3) Development Toolchain

| Tool | Purpose | Evidence |
|------|---------|----------|
| `javac` | Compile Java sources | `jc.bat` |
| None | No test runner, linter, or formatter detected | Scan output |

### 4) Key Commands

```bash
# Current (legacy) – compile
javac -verbose -sourcepath src -d classes src/fea/*.java
javac -verbose -sourcepath src -d classes src/gener/*.java

# Run solver
java -cp classes fea.Jfem <input-file> [output-file]

# Run mesh generator
java -cp classes fea.Jmgen <input-file> [output-file]

# Run visualizer (requires Java 3D on classpath)
java -cp classes fea.Jvis <input-file>
```

### 5) Environment and Config

- Config sources: `.fem`, `.gen`, `.vis` text files passed as CLI arguments
- Required env vars: None – fully file-driven
- Deployment/runtime constraints: Java 3D must be installed for visualizer; Applet API removed in Java 11+

### 6) Evidence

- `jc.bat` – only build script
- `src/fea/Jvis.java` – Java 3D / Applet entry point
- `src/visual/J3dScene.java` – Java 3D scene setup
- Scan output: "No recognized manifest files found in project root"
