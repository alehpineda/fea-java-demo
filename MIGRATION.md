# Migration summary

## Changes completed

- Added a Maven build targeting Java 17.
- Copied production sources into `src/main/java/` and created `src/test/java/`.
- Excluded `visual/**` and `fea/Jvis.java` from Java 17 compilation.
- Replaced raw collections with parameterized generics in the main model/load/generator code paths.
- Replaced deprecated reflective dispatch in `Jmgen` with `fea.JmgenRegistry`.
- Replaced `System.exit()` from `UTIL.errorMsg()` with `util.FeaException`.
- Modernized `UTIL.printDate()` to `java.time.LocalDateTime`.
- Added JUnit 5 unit and regression tests for scanners, rules, materials, factories, solver creation, solver examples, and generator flows.
- Added JaCoCo reporting and a 90% package-level line coverage gate for the modernized `util` and `material` packages.

## Visualization decision

The visualization code depends on `java.applet.Applet` and Java 3D (`javax.media.j3d.*`), both of which are unsuitable for a Java 17 baseline. The source has been preserved for reference, but the Maven build excludes it from compilation. This keeps solver and generator modernization unblocked while leaving a clear seam for a later visualization rewrite.

See `VISUALIZATION.md` for details.

## Remaining work for Java 25

To move beyond Java 17 toward Java 25:
- replace the legacy visualization layer with a supported UI/3D stack before considering a full Java 25 migration
- evaluate Stream Gatherers for custom stream-heavy post-processing pipelines
- review preview/modern language features only where they improve clarity without destabilizing the preserved solver logic
- evaluate runtime tuning under newer GC defaults such as generational ZGC for larger models
- incrementally modernize documentation comments and API surfaces once the legacy compatibility layer is no longer the main constraint

## Known limitations

- `Jvis` and the `visual` package are intentionally excluded from compilation.
- Static mutable state still exists in several legacy classes; tests reset it before execution, but a deeper architectural refactor is still open.
- The numerical kernels were intentionally preserved, so the code still reflects legacy structure in many element and solver classes.
- JaCoCo enforcement is scoped to the modernized utility/material packages, while broader solver/generator behavior is protected primarily through regression tests.
