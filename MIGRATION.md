# Migration summary

## Changes completed

### Phase 1 — Maven build and Java 17 baseline

- Added a Maven build initially targeting Java 17.
- Copied production sources into `src/main/java/` and created `src/test/java/`.
- Replaced raw collections with parameterized generics in the main model/load/generator code paths.
- Replaced deprecated reflective dispatch in `Jmgen` with `fea.JmgenRegistry`.
- Replaced `System.exit()` in `UTIL.errorMsg()` with `util.FeaException`.
- Modernized `UTIL.printDate()` to `java.time.LocalDateTime`.
- Added JUnit 5 unit and regression tests for scanners, rules, materials, factories, solver creation, solver examples, and generator flows (32 tests).
- Added JaCoCo reporting and a 90 % package-level line coverage gate for the `util` and `material` packages.

### Phase 2 — Java 25 and JavaFX 3D visualization

- Upgraded Maven compiler target to Java 25 (`maven.compiler.release=25`).
- Upgraded JaCoCo to 0.8.14 (minimum version that supports Java 25 class file format, version 69).
- Migrated the entire `visual/` package from `Applet` + Java 3D (`javax.media.j3d.*`, `javax.vecmath.*`) to JavaFX 3D:
  - `VisData`: replaced `javax.vecmath.Color3f` with `javafx.scene.paint.Color`.
  - `Lights`: `BranchGroup`/`DirectionalLight` (Java 3D) → `Group`/`AmbientLight`/`DirectionalLight` (JavaFX) with rotation via cross-product transform.
  - `MouseInteraction`: Java 3D behaviors → JavaFX `SubScene` event handlers (orbit, pan, zoom).
  - `ColorScale`: `Texture2D`/`TextureLoader` → `WritableImage`/`PixelWriter`; pure-math color logic preserved.
  - `SurfaceGeometry`: fixed CRLF line endings; `LinkedList` → `LinkedList<int[]>`; fixed a bug where `sizeMax` was computed as `xyzmax[i] - xyzmax[i]` (always 0) instead of `xyzmax[i] - xyzmin[i]`.
  - `SurfaceSubGeometry`: `TriangleArray` → `TriangleMesh`/`MeshView`; `LineArray` → cylinder `Group`; `PointArray` → sphere `Group`.
  - `J3dScene`: `Canvas3D`/`SimpleUniverse` → `SubScene`/`PerspectiveCamera`/`Group`; `PhongMaterial` with optional `WritableImage` diffuse map for contour rendering.
- Rewrote `Jvis.java` from `extends Applet` to `extends javafx.application.Application`.
- Removed `<excludes>` for `visual/**` and `fea/Jvis.java` from the Maven compiler configuration.
- Added GitHub Actions CI workflow (`.github/workflows/ci.yml`) targeting Java 25 Temurin.

See `VISUALIZATION.md` for full JavaFX migration details.

## Known limitations

- Static mutable state still exists in several legacy classes; tests reset it before execution, but a deeper architectural refactor remains open.
- The numerical kernels were intentionally preserved, so the code still reflects legacy structure in element and solver classes.
- JaCoCo enforcement is scoped to the `util` and `material` packages; broader solver/generator behavior is protected through regression tests.
- Visual package tests require a display and are excluded from the JaCoCo coverage gate; no automated tests for the JavaFX visualization code.

