# Visualization status

## Why visualization is isolated

The legacy visualization code uses:
- `java.applet.Applet`
- Java 3D APIs under `javax.media.j3d.*`

Those APIs are not part of a normal Java 17 runtime baseline for this project, so compiling them in the main Maven module would block modernization of the solver and generator.

## Current build behavior

The repository keeps the visualization sources in place under `src/main/java/visual/` and `src/main/java/fea/Jvis.java`, but the Maven compiler excludes them:
- `visual/**`
- `fea/Jvis.java`

This preserves the legacy code for reference while allowing `mvn clean verify` to succeed on Java 17.

## What still works

- `fea.Jfem` builds and runs on Java 17
- `fea.Jmgen` builds and runs on Java 17
- unit and regression tests execute under Maven

## Future options

To restore visualization support, create a dedicated follow-up effort to either:
- move the old visualization code into a separate compatibility module with its own dependencies, or
- replace the applet/Java 3D stack with a modern supported rendering approach
