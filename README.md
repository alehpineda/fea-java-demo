# FEA Java Demo

A legacy finite element analysis (FEA) solver, mesh generator, and visualization codebase modernized to build on Java 17 with Maven.

## Project description

The repository contains three legacy entry points:
- `fea.Jfem` - finite element solver
- `fea.Jmgen` - mesh generator
- `fea.Jvis` - legacy visualization applet (kept in source, excluded from the Java 17 build)

The modernization keeps the original numerical behavior while adding:
- a Maven build targeting Java 17
- JUnit 5 regression and unit tests
- JaCoCo coverage reporting and enforcement for the modernized utility/material packages
- generic collections, safer exception handling, and explicit generator command registration

## Requirements

- Java 17+
- Maven 3.9+

## Repository layout

- `src/main/java/` - Maven production sources
- `src/test/java/` - JUnit 5 test suite
- `src/main/resources/` - Maven resources
- `src/` - retained legacy source layout for reference during migration
- `example01/` to `example04/` - regression examples and golden outputs

## Build

```bash
mvn clean verify
```

## Test

```bash
mvn test
```

## Coverage report

After a successful build, open:

```text
target/site/jacoco/index.html
```

## Running examples

Compile first:

```bash
mvn -q -DskipTests compile
```

Run the solver on example 01:

```bash
java -cp target/classes fea.Jfem example01/f.fem example01/out.lst
```

Run the solver on example 02:

```bash
java -cp target/classes fea.Jfem example02/f.fem example02/out.lst
```

Run the mesh generator on example 04:

```bash
java -cp target/classes fea.Jmgen example04/hole3d.gen example04/hole3d.gen.lst
```

The generator now resolves included and generated files relative to the input script location, which makes Maven/Surefire-based execution reliable.

## Migration notes

- The Maven build targets Java 17 today.
- Java 25 follow-up work is documented in `MIGRATION.md`.
- The legacy visualization stack (`Applet` + Java 3D) remains in the repository but is excluded from compilation; see `VISUALIZATION.md`.
- `UTIL.errorMsg()` now throws `util.FeaException` so tests can exercise failure paths without terminating the JVM.
- `Jmgen` now uses an explicit command registry instead of deprecated reflective construction.
- Legacy numerical kernels were preserved; behavior is guarded with regression tests against the shipped example outputs.

## Breaking changes from legacy

- The supported build path is now Maven rather than `jc.bat`.
- Solver and generator failures now raise `FeaException` internally and only exit at the CLI boundary.
- Visualization is not part of the Java 17 build.
- Source files are compiled from `src/main/java/`; the original `src/` tree is retained only as a reference copy.
