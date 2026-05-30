# Verification Plan for VTK Visualization Migration

**Status**: This document is the authoritative checklist for accepting the new VTK-based visualizer.

**Goal**: Ensure the new implementation (`visual.vtk.*`) delivers at least the functionality and numeric correctness of the original Java3D viewer, while being maintainable on Java 25+.

---

## 1. Prerequisites

- Java 25+
- Maven wrapper available
- GitHub Packages PAT configured with `read:packages` (or use the recommended `org.jzy3d:vtk-java-all` helper)
- Run with profile:
  ```bash
  ./mvnw clean compile -Pvisual-vtk
  ```

**Recommended dependency path (2026)**: Use `org.jzy3d:vtk-java-all` when possible (see `NOTES.md`).

---

## 2. Build & Compile Verification

- [ ] `./mvnw clean compile -Pvisual-vtk` succeeds with no errors related to `visual/vtk/`
- [ ] `./mvnw clean compile` (without profile) still succeeds (old visualization remains excluded)
- [ ] No new warnings introduced in the VTK code
- [ ] `VtkNativeLoader` and data bridge classes compile cleanly

---

## 3. Unit / Smoke Tests

Run:
```bash
./mvnw test -Pvisual-vtk -Dtest="visual.vtk.*Test"
```

- [ ] `VtkNativeLoaderTest` passes
- [ ] `VtkDataBridgeTest` passes (basic smoke)
- [ ] Any additional tests for `PreparedSurfaceData` extraction pass

---

## 4. Manual Regression on Shipped Examples

**Primary fixtures** (must all work):

1. **example03/f.vis** (2D plate, ux contours)
2. **example04/hole3d-mesh.vis** (pure 3D mesh, no results)
3. **example04/hole3d.vis** (3D plate with hole + Sy contours + deformation)

### Commands to run
```bash
# Recommended
java -cp "target/classes:target/dependency/*" visual.vtk.VtkLauncher example03/f.vis

java -cp "target/classes:target/dependency/*" visual.vtk.VtkLauncher example04/hole3d-mesh.vis

java -cp "target/classes:target/dependency/*" visual.vtk.VtkLauncher example04/hole3d.vis
```

### Checklist per example

**General**
- [ ] Window opens without crash
- [ ] Model is visible and correctly scaled/centered
- [ ] Mouse interaction works (orbit with left, pan with right, zoom with wheel/middle)
- [ ] Background is white (matching original behavior)
- [ ] Console prints reasonable stats (vertices, triangles, parm, range, deformed flag)

**Scalar / Contour behavior**
- [ ] Correct scalar field is shown (matches `parm` in .vis file)
- [ ] Color range matches `fmin`/`fmax` (or auto-computed from data)
- [ ] Classic rainbow (Magenta→Blue→Cyan→Green→Yellow→Red) is used
- [ ] Scalar bar / legend is visible and labeled with the parameter name

**Deformation**
- [ ] When `deformScale > 0`, the model is visibly deformed
- [ ] Deformation scale is consistent with the old Java3D viewer (within visual tolerance)

**Toggles (future UI or console commands)**
- [ ] Edges visibility works
- [ ] Nodes visibility works
- [ ] Surface shading looks reasonable (lighting present)

**Numeric Correctness Gate** (Critical)
- The scalar values displayed must match what the legacy `ResultAtNodes` + element extrapolation produced.
- **How to verify**:
  1. Run the old Java3D visualizer (if still possible on a Java 8 machine) on the same `.vis` file and note key values.
  2. Or compare against the nodal values printed/written by the solver for the same `parm`.
  3. For the new viewer, the min/max shown in the scalar bar + spot values on the model should be consistent with `VisData.fmin`/`fmax` and the `fun[]` array populated by `ResultAtNodes`.

---

## 5. Edge Cases & Robustness

- [ ] Pure mesh visualization (no `resultFile`, `parm = none`)
- [ ] 2D vs 3D models
- [ ] Very small / large models (example04 is a good mid-size test)
- [ ] Missing or invalid `.vis` keywords (graceful error handling)
- [ ] VTK natives not present → clear, helpful error from `VtkNativeLoader`

---

## 6. Packaging & Distribution

- [ ] `VtkNativeLoader` works with both raw VTK and the recommended jzy3d helper
- [ ] Clear instructions exist in `README.md` and `NOTES.md` for end users
- [ ] No hard dependency on VTK in the default build (core `Jfem` / `Jmgen` unaffected)

---

## 7. Documentation

- [ ] `README.md` has an up-to-date "Visualization (Optional — Powered by VTK)" section
- [ ] `visual/vtk/NOTES.md` reflects current dependency recommendations
- [ ] `visual/vtk/CLEANUP_PLAN.md` is current
- [ ] Old `fea/Jvis.java` is properly deprecated

---

## 8. Sign-off Criteria (for release)

Before considering the VTK visualizer "done":

1. All items in sections 2–6 above are green.
2. The three primary shipped examples render correctly and produce scalar values consistent with the legacy `ResultAtNodes` pipeline.
3. A normal `./mvnw clean verify` (no profile) still passes.
4. The old Java3D code has been moved to `src/legacy/visual-java3d/` (or deleted) and excludes have been cleaned up.
5. At least one person has successfully run the viewer on their machine using the documented dependency path.

---

## 9. Stretch Goals (nice to have)

- Ability to export the current view as `.vtk`/`.vtu` (for ParaView)
- Basic animation of time steps (if result files support it)
- Node/element picking with value display
- Cutting planes

---

**Owner**: Visualization migration team  
**Last updated**: During the VTK port (2026)

This document should be updated as implementation progresses and used as the final acceptance checklist before shipping the new visualizer.