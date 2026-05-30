# Codebase Structure

## Core Sections (Required)

### 1) Top-Level Map

| Path | Purpose | Evidence |
|------|---------|----------|
| `src/` | All Java source code, organized by package | `jc.bat` |
| `src/fea/` | Top-level application entry points and global constants | `src/fea/Jfem.java`, `src/fea/Jmgen.java`, `src/fea/Jvis.java`, `src/fea/FE.java` |
| `src/elem/` | Finite element type definitions and shape functions | `src/elem/Element.java`, `src/elem/ElementQuad2D.java`, `src/elem/ElementQuad3D.java` |
| `src/model/` | FE model data, load data, and stress computation | `src/model/FeModel.java`, `src/model/FeLoad.java`, `src/model/FeStress.java` |
| `src/material/` | Material constitutive laws (elastic, elastic-plastic) | `src/material/Material.java`, `src/material/ElasticMaterial.java`, `src/material/ElasticPlasticMaterial.java` |
| `src/solver/` | Linear equation solvers (LDU profile, PCG iterative) | `src/solver/Solver.java`, `src/solver/SolverLDU.java`, `src/solver/SolverPCG.java` |
| `src/util/` | Utilities: file scanner, print writer, Gauss rules, misc | `src/util/FeScanner.java`, `src/util/GaussRule.java`, `src/util/UTIL.java` |
| `src/gener/` | Mesh generation commands (reflect-dispatched by name) | `src/gener/genquad8.java`, `src/gener/connect.java`, `src/gener/sweep.java` |
| `src/visual/` | Visualization subsystem (Java 3D / Applet) | `src/visual/J3dScene.java`, `src/visual/VisData.java` |
| `example01/` | 2-element plane-stress thermal + surface load example | `example01/f.fem`, `example01/f.lst.1` |
| `example02/` | Elastic-plastic 3D tension example | `example02/f.fem`, `example02/f.lst.A`, `example02/f.lst.B` |
| `example03/` | Visualization-only example (mesh + results display) | `example03/f.vis`, `example03/f.mesh`, `example03/f.res` |
| `example04/` | 3D plate-with-hole: mesh generation + solve + visualize | `example04/hole3d.gen`, `example04/hole3d.fem`, `example04/hole3d.lst.1` |
| `context/` | PDF reference book ("Programming Finite Elements in Java") | `context/fea.pdf` |
| `docs/` | Generated documentation (this directory) | Created by codebase scan |

### 2) Entry Points

- Main runtime entry: `src/fea/Jfem.java` – FE solver (`fea.Jfem`)
- Secondary entry points:
  - `src/fea/Jmgen.java` – mesh generator (`fea.Jmgen`)
  - `src/fea/Jvis.java` – visualizer (`fea.Jvis`, extends `Applet`)
- How entry is selected: separate CLI invocations; `FE.main` flag (`JFEM=0`, `JMGEN=1`, `JVIS=2`) distinguishes runtime context in shared code

### 3) Module Boundaries

| Boundary | What belongs here | What must not be here |
|----------|-------------------|------------------------|
| `fea` | CLI entry, global constants (`FE`) | Business logic, data structures |
| `model` | FE model state, load state, stress state | Element numerics, solver algorithms |
| `elem` | Element definitions, shape functions, assembly | Model I/O, solver |
| `material` | Constitutive laws (stress from strain) | Model data, element geometry |
| `solver` | GSM assembly and linear solve | Element/material logic |
| `util` | File I/O helpers, Gauss rules, print utilities | Domain logic |
| `gener` | Mesh-generation commands (each class = one command) | Solver or visualization |
| `visual` | 3D scene rendering, result display (Java 3D / Applet) | Solver or mesh generation |

### 4) Naming and Organization Rules

- File naming: PascalCase for class files (e.g., `FeModel.java`, `ElementQuad2D.java`); lower camelCase for `gener` command classes (e.g., `genquad8.java`, `connect.java`, `writemesh.java`)
- Directory organization: by layer/package (not by feature)
- Import style: standard Java imports; no path aliases
- `gener` classes are loaded reflectively by name: `Class.forName("gener." + commandName)` – class name must match the command keyword in the input file

### 5) Evidence

- `jc.bat` – shows source layout and package names
- `src/fea/Jmgen.java` – `Class.forName("gener." + name).newInstance()`
- `src/fea/FE.java` – global constants and `main` flag
- `example01/x.bat`, `example04/g.bat` – show CLI invocations
