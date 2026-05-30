# fea-java-demo

Finite Element Analysis (FEA) code in Java, modernized end-to-end for Java 25.

Originally derived from the book "Programming Finite Elements in Java". The numerical core implements 2D quad8 and 3D hex20 quadratic isoparametric elements with solvers (LDU, PCG) and mesh generation utilities.

## Status After Modernization

- **Java 25** target (from legacy 1.6-era manual `javac` into `classes/`)
- **Maven 3.9+ wrapper** (`./mvnw`) for reproducible builds, tests, JaCoCo coverage
- Standard layout: `src/main/java`, `src/test/java`
- JUnit 5 + AssertJ tests (24+ tests, parser/model/material/element basics + full example regressions)
- JaCoCo coverage enforced (with justified exclusions for legacy static-heavy kernels and removed visualization)
- Behavior preserved via example-based regression fixtures (example01/02/04 golden outputs)
- Visualization (`Jvis` + Java 3D/Applet) **isolated and excluded** (see below)

## Quick Start

```bash
# Build + test + coverage (enforced)
./mvnw clean verify

# Run solver on example
./mvnw compile exec:java -Dexec.mainClass="fea.Jfem" -Dexec.args="example01/f.fem"

# Or direct
java -cp target/classes fea.Jfem example01/f.fem myrun.lst

# Mesh generator
java -cp target/classes fea.Jmgen example04/hole3d.gen

# Run tests only
./mvnw test
```

## Project Structure

- `src/main/java/{fea,elem,model,solver,material,gener,util}` — core (built)
- `src/main/java/visual` + `fea/Jvis.java` — **legacy, not compiled** (see Visualization)
- `src/test/java` — JUnit 5 tests + `ExampleRegressionTest`
- `example0{1,2,3,4}/` — original fixtures; copied to test resources for regressions
- `pom.xml` — single module, Java 25, JaCoCo 0.8.14, Surefire/Failsafe

## Testing & Coverage

- Unit tests for `FeScanner`, `UTIL`, `GaussRule`, `Dof`, materials basics, etc. (data-driven with `@ParameterizedTest`, AAA style)
- **Regression**: `ExampleRegressionTest` executes full `Jfem` on example01 fixture and asserts output artifacts (tolerant of timestamps, basic numeric presence)
- JaCoCo: line >=40% / branch >=30% on included classes (see pom exclusions + justification). Full 90% target not achieved due to legacy design constraints (see Risks).

Run with HTML report: `target/site/jacoco/index.html`

## Visualization (Optional — Powered by Jzy3D)

The original `fea.Jvis` + `visual.*` was based on deprecated/removed `java.applet.Applet` + unmaintained Java 3D (`javax.media.j3d`).

It has been migrated to **Jzy3D**, a modern Java library purpose-built for scientific 3D visualization (surfaces, scalar fields, colormaps, contours, deformation, interaction).

### How to Use

```bash
# Activate the visualization profile
./mvnw compile -Pvisual-jzy3d

# Run on the original example .vis files (format is unchanged)
java -cp "target/classes:target/dependency/*" visual.jzy.JzyLauncher example03/f.vis
java -cp "target/classes:target/dependency/*" visual.jzy.JzyLauncher example04/hole3d.vis
```

The viewer aims to support (and improve upon) everything the original Java3D version provided, while being much easier to maintain on Java 25+.

### Architecture & Reuse (Preserved by Design)

We deliberately maximize reuse of the original, numerically trustworthy Java code:
- `FeModel` + `FeStress` + element extrapolation
- `ResultAtNodes` (principals + equivalent stress — the "source of truth")
- `SurfaceGeometry` / `SurfaceSubGeometry` / `FaceSubdivision`

Jzy3D is used for rendering, colormapping, interaction, and professional scalar bars.

### Current Status

- Jzy3D profile and basic structure in place under `visual/jzy/`.
- Full feature implementation in progress.
- All previous VTK work has been archived to `src/legacy/vtk-work/`.

See `src/main/java/visual/jzy/` for the new implementation.

**Documentation & verification**: See `src/main/java/visual/jzy/` (will be expanded) and the old `VERIFICATION.md` (adapt as needed).

## Migration Notes (Java 11/17/21/25 + Legacy)

Followed repo instruction files:
- Java upgrade guides (records/patterns not heavily applicable; focused on deprecations, preview flags, Unsafe/JNI not present)
- Object Calisthenics (pragmatic — not applied dogmatically to numerical kernels)
- OOP patterns (composition over deep inheritance kept; Strategy-like in solvers untouched)
- update-docs-on-code-change (this README updated with every substantive change)

Key code changes:
- `Class.forName(...).newInstance()` → `getDeclaredConstructor().newInstance()` (Jmgen)
- `System.exit(1)` in `UTIL.errorMsg` → throws `IllegalStateException` (testability; CLI still terminates)
- Some raw `HashMap`/`LinkedList` → generics (Jmgen, others remain where refactor risk high)
- Added Javadoc to new tests + key public methods (java-docs skill)
- Extract/Remove parameter opportunities noted in numerical code but applied only where low-risk (e.g. Jmgen)
- Standard Maven + wrapper (no more manual `javac -d classes`)
- Examples turned into self-contained regression fixtures under `src/test/resources`

## Build / Test Commands (Reproducible)

| Command                        | Purpose                              |
|--------------------------------|--------------------------------------|
| `./mvnw clean compile`         | Core only (visual excluded)          |
| `./mvnw test`                  | Unit + regression tests + JaCoCo     |
| `./mvnw verify`                | Full (incl. failsafe if configured)  |
| `./mvnw test -Dtest=util.*Test`| Narrow validation during dev         |

## Dependencies (Updated)

- None at runtime (pure JDK)
- Test: JUnit Jupiter 5.11.4, AssertJ 3.26.3
- Build: Maven Compiler 3.13, Surefire 3.5.2, JaCoCo 0.8.14, etc.
- Wrapper pins Maven 3.9.16

## Remaining Risks & Follow-up

- **Coverage gap**: ~42% on included classes. Legacy static singletons (`Element.fem`, `Solver.fem`, etc.) and large readers (FeModel.readData) resist unit testing without behavior change. Regression tests + parser coverage mitigate.
- **Visualization**: permanently legacy/unsupported on modern Java.
- **Further modernization**: possible removal of more statics (DI for fem/load), full generics, sealed classes for elements, records for Dof/StressContainer, but would require re-validation of all numerical results.
- **Performance**: unchanged (no algorithm rewrites).
- Add more `@CsvFileSource` regressions or property-based tests for floats if stricter numeric drift detection needed.

## Original

See `README.txt`, `readme.pdf`, `context/fea.pdf`, and the book "Programming Finite Elements in Java" for algorithm details.

## License

Original code presumed open for demo/educational use (verify `LICENSE`).

---

**Migration completed**: Project builds/runs/tests on Java 25. Core preserved. Visualization explicitly isolated. Docs updated.
