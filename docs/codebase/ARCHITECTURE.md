# Architecture

## Core Sections (Required)

### 1) Architectural Style

- Primary style: **Layered procedural / batch-processing pipeline** with thin object wrappers
- Why this classification: Data flows in a single pass from file input through model building, assembly, solve, and output. Classes wrap numeric arrays rather than encapsulate behavior. Heavy use of static fields creates a globally shared mutable state.
- Primary constraints:
  1. Numerical fidelity – algorithms are transcribed from the reference textbook and must not drift
  2. Legacy Java 1.6 idioms – raw collections, `System.exit`, reflection-based dispatch
  3. File-driven I/O – every run begins with a plain-text `.fem`/`.gen`/`.vis` file and ends with a `.lst`/`.mesh`/`.res` file

### 2) System Flow

**Solver (Jfem):**
```text
CLI args (*.fem file)
  -> FeScanner (tokenized I/O)
  -> FeModel.readData() (builds node/element/material/BC data)
  -> Solver.newSolver() + assembleGSM() (stiffness matrix)
  -> FeLoad.readData() loop per load step
     -> load.assembleRHS()
     -> solver.solve(RHS) -> displacements
     -> FeStress.computeIncrement() + equilibrium check
     -> FeStress.accumulate() + writeResults()
  -> *.lst file output via PrintWriter
```

**Mesh Generator (Jmgen):**
```text
CLI args (*.gen file)
  -> FeScanner reads command tokens
  -> Class.forName("gener." + token).newInstance() dispatch
     -> Each genXxx class reads its parameters and mutates Jmgen.blocks (HashMap)
  -> writemesh writes *.mesh file
```

**Visualizer (Jvis):**
```text
CLI args (*.vis file)
  -> VisData.readData() (reads mesh + results)
  -> J3dScene (constructs Java 3D scene graph)
  -> Java AWT/Applet window
```

### 3) Layer/Module Responsibilities

| Layer or module | Owns | Must not own | Evidence |
|-----------------|------|--------------|----------|
| `fea` | CLI entry, global flags (`FE.main`, `FE.bigValue`) | Domain logic | `src/fea/FE.java`, `src/fea/Jfem.java` |
| `model` | FE model state (nodes, elements, materials, BCs), load steps, stress accumulation | Solver algorithm, element numerics | `src/model/FeModel.java`, `src/model/FeLoad.java`, `src/model/FeStress.java` |
| `elem` | Element-level stiffness, shape functions, face loads, stress extrapolation | Model I/O, global assembly | `src/elem/Element.java`, `src/elem/ElementQuad2D.java` |
| `material` | Constitutive laws (elastic, elastic-plastic) | Geometry, assembly | `src/material/ElasticMaterial.java`, `src/material/ElasticPlasticMaterial.java` |
| `solver` | Global stiffness matrix storage and factored solve | Element logic | `src/solver/SolverLDU.java`, `src/solver/SolverPCG.java` |
| `util` | Tokenizing file I/O, Gauss quadrature rules, date/error printing | Domain logic | `src/util/FeScanner.java`, `src/util/GaussRule.java` |
| `gener` | Parameterized mesh block generation; result stored in `Jmgen.blocks` | Solving, visualization | `src/gener/genquad8.java`, `src/gener/connect.java` |
| `visual` | Java 3D scene construction, color mapping, deformed shape display | Solving, mesh generation | `src/visual/J3dScene.java`, `src/visual/VisData.java` |

### 4) Reused Patterns

| Pattern | Where found | Why it exists |
|---------|-------------|---------------|
| Static shared mutable state | `Element.fem`, `Element.kmat`, `FeModelData.RD/PR`, `FeLoadData.RHS`, `Jmgen.blocks`, `FE.main` | Legacy design: avoids parameter passing in deeply nested calls |
| Enum-based factory | `Element.elements`, `Solver.Solvers`, `Material.newMaterial` | Type-safe element/solver selection from config strings |
| Reflection-based dispatch | `Class.forName("gener." + name).newInstance()` in `Jmgen` | Extensible command set without explicit registration |
| Template method | `Element` abstract base with `stiffnessMatrix()`, `thermalVector()`, etc. overridden in `ElementQuad2D`/`ElementQuad3D` | Polymorphic element behavior |
| Data superclass/subclass split | `FeModelData` / `FeModel`, `FeLoadData` / `FeLoad` | Separates field declarations from read/compute logic |

### 5) Known Architectural Risks

- **Java 3D / Applet removal**: `java.applet.Applet` and `javax.media.j3d` are not available in Java 11+ without third-party installations. The entire `visual` package and `Jvis` entry point are broken on modern JDKs.
- **Static shared state prevents testability**: `Element.fem`, `Element.kmat`, `FeLoadData.RHS`, etc., are mutable class-level statics. Running two tests in the same JVM may corrupt state.
- **`System.exit(1)` in utility code**: `UTIL.errorMsg()` calls `System.exit`, making error paths impossible to test with JUnit without additional work.
- **Reflection dispatch in mesh generator**: `Class.forName(...).newInstance()` uses deprecated no-arg constructor reflection; fails on Java 9+ modules without `--add-opens`.

### 6) Evidence

- `src/fea/Jvis.java` – `extends Applet`, `import com.sun.j3d.utils.applet.MainFrame`
- `src/visual/J3dScene.java` – `import javax.media.j3d.*`, `import com.sun.j3d.utils.universe.SimpleUniverse`
- `src/elem/Element.java` – `public static FeModel fem`, `public static double kmat[][]`
- `src/fea/Jmgen.java` – `Class.forName("gener." + name).newInstance()`
- `src/util/UTIL.java` – `System.exit(1)` in `errorMsg()`
