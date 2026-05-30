# FEA Java Demo

A legacy finite element analysis (FEA) solver, mesh generator, and visualization
codebase fully modernized to Java 25 with Maven.

## Project description

Three executable entry points:

| Entry point | Purpose |
|---|---|
| `fea.Jfem` | Finite element solver |
| `fea.Jmgen` | Mesh generator |
| `fea.Jvis` | Interactive 3D visualization (JavaFX) |

Numerical core: second-order quadratic isoparametric elements — quad8 (2D) and hex20 (3D).

## Requirements

- Java 25 (JDK 25+)
- Maven 3.9+

## Build & test

```bash
# Compile, run all 32 JUnit 5 tests, enforce JaCoCo coverage, produce report
mvn clean verify
```

Coverage report (line coverage ≥ 90 % for `util` and `material` packages):

```text
target/site/jacoco/index.html
```

## Running examples

```bash
# Compile (skip tests for speed)
mvn -q -DskipTests compile

# Finite element solver — example 01 (plane stress, 2 elements)
java -cp target/classes fea.Jfem example01/f.fem example01/out.lst

# Finite element solver — example 02 (elastic-plastic)
java -cp target/classes fea.Jfem example02/f.fem example02/out.lst

# Mesh generator — example 04 (3D plate with central hole)
java -cp target/classes fea.Jmgen example04/hole3d.gen example04/hole3d.gen.lst
```

## Interactive 3D visualization

The visualizer requires a display and the JavaFX runtime. Launch via Maven for automatic module-path setup:

```bash
# example03 provides a pre-solved cube mesh for visualization
mvn javafx:run -Djavafx.args=example03/cube.vis
```

Or directly (JavaFX must be on the module path):

```bash
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.graphics \
     -cp target/classes fea.Jvis example03/cube.vis
```

Mouse controls: **left-drag** to orbit, **right-drag** to pan, **scroll** to zoom.

## Repository layout

```
src/main/java/        Maven production sources (all packages, including visual/)
src/test/java/        JUnit 5 test suite
example01/–example04/ Regression fixtures and golden outputs
docs/codebase/        Architecture, stack, testing and concerns documentation
```

## Migration notes

- Build targets Java 25 (`maven.compiler.release=25`).
- Visualization migrated from `Applet` + Java 3D → `javafx.application.Application` + JavaFX 3D.
- `UTIL.errorMsg()` throws `util.FeaException` instead of calling `System.exit()`.
- `Jmgen` uses an explicit `JmgenRegistry` instead of deprecated reflective construction.
- All numerical kernels are preserved; behavior is guarded by regression tests against the shipped examples.
- See `MIGRATION.md` for the full change history and `VISUALIZATION.md` for JavaFX migration details.

## CI

GitHub Actions runs `mvn --no-transfer-progress clean verify` on every push and pull request using Java 25 (Eclipse Temurin). See `.github/workflows/ci.yml`.

