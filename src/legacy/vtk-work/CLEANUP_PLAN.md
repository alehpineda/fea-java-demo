# Cleanup Plan for Legacy Java3D Visualization Code

## Decision (2026)

We have decided to **permanently archive and remove** the old Java3D-based visualization code once the VTK implementation reaches feature parity.

### Files to Archive / Remove
- `visual/J3dScene.java`
- `visual/ColorScale.java`
- `visual/Lights.java`
- `visual/MouseInteraction.java`
- `visual/SurfaceSubGeometry.java` (the Java3D-dependent parts)
- `fea/Jvis.java` (the Applet version — already annotated @deprecated)
- All `javax.media.j3d` and `com.sun.j3d` imports

Reusable pure-Java classes (`VisData`, `ResultAtNodes`, `SurfaceGeometry`, `FaceSubdivision`) will be kept or moved to a neutral package (`visual.data` or similar) as they are used by the new VTK path.

### Recommended Approach
1. Create `src/legacy/visual-java3d/` and move the pure Java3D files there (for git history + reference).
2. Update `pom.xml` compiler and JaCoCo excludes to remove all references to the old `visual/` package.
3. Delete the old files from `src/main/java/visual/` (except reusable data classes).
4. Remove the `visual-vtk` profile's special treatment once everything is cleaned.

### Current State (after this session)
- `fea/Jvis.java` has been annotated `@deprecated`.
- `CLEANUP_PLAN.md` exists.
- New code lives cleanly in `visual/vtk/`.

**Next concrete actions** (tracked in todo vtk-11):
- Create the legacy directory structure.
- Move the first batch of dead Java3D files.
- Update excludes in pom.xml.

This cleanup should happen before the next release that ships the VTK visualizer.
