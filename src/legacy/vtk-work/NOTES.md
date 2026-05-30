# VTK Visualization Migration - Implementation Notes

## Current Status (as of this commit)

### Completed
- Maven profile `-Pvisual-vtk` defined (uses Kitware GitHub Packages for VTK 9.6+)
- Package structure under `visual/vtk/`
- `VtkHello.java` - minimal working VTK pipeline prototype (cone + trackball interactor)
- `VisData.java` modernized (Javadoc + small robustness improvements) while preserving 100% .vis file compatibility
- `VtkDataBridge.java` skeleton created (the key integration point)

### Priority 1 Completed: vecmath Dependency Tackled
- Added `org.jogamp.java3d:vecmath:1.8.0` (modern maintained fork of `javax.vecmath`) **exclusively** inside the `-Pvisual-vtk` profile.
- This allows the reusable legacy data classes (VisData, etc.) to compile when visualization is enabled.
- Note: Because the profile declares the Kitware GitHub Packages repo, *all* dependencies in the profile currently require a PAT (including vecmath until we improve the repo setup in Priority 3).

**Long-term cleanup**: During implementation of VtkDataBridge and VtkScene we will replace `Color3f` usage with standard Java types where possible to eventually remove this dependency.

### Priority 3 + 14: Dependency & Packaging Story
**Recommended path (2026)**: Prefer `org.jzy3d:vtk-java-all` over raw Kitware packages when possible.

**Packaging recommendation for end users**:
- Use the jzy3d `vtk-java-all` artifact (it handles native selection reasonably well).
- Document clearly in README that users need either:
  - The jzy3d helper, **or**
  - Raw VTK natives + proper `java.library.path` or the VtkNativeLoader.

Full guidance lives in this NOTES.md and the visualization section of the root README.

2. **VTK dependency resolution**
   - GitHub Packages requires a PAT with `read:packages` scope.
   - Native libraries must be provided for the target platform.
   - Until this is solved in CI / developer machines, VTK-dependent code cannot be compiled in normal builds.

### Next Priorities
- Resolve or work around the vecmath issue so reusable data classes can be compiled and tested by default.
- Implement `VtkDataBridge.buildVtkDataset()` (feed existing `ResultAtNodes` + surface data into VTK).
- Build the actual `VtkScene` viewer on top of the bridge.
- Full regression on the shipped example .vis files.

## Architectural Principle
We are deliberately maximizing reuse of the existing, battle-tested Java data pipeline
(`FeModel`, `ResultAtNodes`, element extrapolation, surface extraction) and using VTK
only for what it is world-class at: rendering, warping, contouring, and interaction.

## Verification
See `VERIFICATION.md` (in this directory) for the complete acceptance checklist, including:
- Build & profile activation
- Numeric correctness (scalars must match `ResultAtNodes` + legacy extrapolation)
- Manual regression on `example03/f.vis` and `example04/*.vis`
- Packaging & native loading
- Sign-off criteria

This document is the final gate before considering the VTK visualizer ready for release.
