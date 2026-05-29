# Visualization

## Current state: JavaFX 3D (Java 25)

The visualization subsystem has been fully migrated from `Applet` + Java 3D to
JavaFX 3D and is compiled as part of the main Maven build.

## Architecture

| File | Role |
|---|---|
| `fea/Jvis.java` | Entry point; `extends Application`; loads `VisData`, creates `J3dScene` |
| `visual/J3dScene.java` | JavaFX scene coordinator; `SubScene` + `PerspectiveCamera` |
| `visual/SurfaceSubGeometry.java` | Converts FE mesh to `TriangleMesh`, edge cylinders, node spheres |
| `visual/SurfaceGeometry.java` | Computes bounding box, surface topology |
| `visual/ColorScale.java` | Maps scalar results to colours via `WritableImage`/`PixelWriter` |
| `visual/MouseInteraction.java` | Orbit (left-drag), pan (right-drag), zoom (scroll) |
| `visual/Lights.java` | Ambient + directional lights with cross-product orientation |
| `visual/VisData.java` | Global visualization configuration (colours, flags, scale) |
| `visual/FaceSubdivision.java` | Pure-math face subdivision (unchanged from legacy) |
| `visual/ResultAtNodes.java` | Pure-math result averaging (unchanged from legacy) |

## Running the visualizer

Launch via Maven for automatic module-path setup:

```bash
mvn javafx:run -Djavafx.args=example03/cube.vis
```

Or directly with the JavaFX SDK on the module path:

```bash
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.graphics \
     -cp target/classes fea.Jvis example03/cube.vis
```

## Mouse controls

| Gesture | Action |
|---|---|
| Left drag | Orbit (rotate around model) |
| Right drag | Pan |
| Scroll | Zoom |

## Migration from Java 3D

| Legacy | Replacement |
|---|---|
| `java.applet.Applet` | `javafx.application.Application` |
| `Canvas3D` / `SimpleUniverse` | `SubScene` / `PerspectiveCamera` |
| `BranchGroup` / `TransformGroup` | `Group` with `Rotate` / `Translate` / `Scale` |
| `javax.media.j3d.DirectionalLight` | `javafx.scene.PointLight` / `AmbientLight` |
| `TriangleArray` / `LineArray` | `TriangleMesh` / cylinder `Group` / sphere `Group` |
| `Texture2D` / `TextureLoader` | `WritableImage` / `PixelWriter` |
| `javax.vecmath.Color3f` | `javafx.scene.paint.Color` |
| `MouseRotate` / `MouseZoom` / `MouseTranslate` behaviors | `SubScene` mouse event handlers |

## Bug fixed during migration

`SurfaceGeometry.setBoundingBox()` had a silent bug in the legacy code:

```java
// Legacy (wrong): s is always 0
double s = xyzmax[i] - xyzmax[i];

// Fixed:
double s = xyzmax[i] - xyzmin[i];
```

This caused `sizeMax` to always be 0, breaking camera placement and cylinder/sphere sizing.

## Headless CI note

The visualizer is excluded from automated test coverage because it requires an active display. No visualization tests run in GitHub Actions CI.

