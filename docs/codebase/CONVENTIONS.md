# Coding Conventions

## Core Sections (Required)

### 1) Naming Rules

| Item | Rule | Example | Evidence |
|------|------|---------|----------|
| Classes | PascalCase | `FeModel`, `ElementQuad2D`, `SolverLDU` | `src/model/FeModel.java`, `src/elem/ElementQuad2D.java` |
| `gener` command classes | lowerCamelCase matching input file keyword | `genquad8`, `connect`, `writemesh` | `src/gener/genquad8.java`; `Class.forName("gener." + name)` |
| Methods | lowerCamelCase | `readData()`, `assembleGSM()`, `stiffnessMatrix()` | `src/model/FeModel.java`, `src/solver/Solver.java` |
| Fields | lowerCamelCase; abbreviations common | `nNod`, `nEl`, `nDim`, `nEq`, `iel`, `idf` | `src/model/FeModelData.java` |
| Static constants | UPPER_SNAKE or short lowerCamelCase | `FE.JFEM`, `FE.bigValue`, `FE.epsPCG` | `src/fea/FE.java` |
| Enum values | lowercase matching config file keywords | `plstrain`, `plstress`, `ldu`, `pcg`, `quad8`, `hex20` | `src/model/FeModelData.java`, `src/elem/Element.java` |

### 2) Formatting and Linting

- Formatter: None detected – no `.editorconfig`, no Checkstyle, no Spotless
- Linter: None detected
- Most relevant enforced rules: None formally enforced; code style is consistent Java 1.5–1.6 era
- Run commands: N/A

### 3) Import and Module Conventions

- Import grouping: standard Java imports; package imports used (`import elem.*`, `import model.*`)
- No wildcard-import suppression; wildcard (`.*`) imports are common throughout
- No path aliases (plain Java)
- No `module-info.java`

### 4) Error and Logging Conventions

- Error strategy: `UTIL.errorMsg(String)` prints `"=== ERROR: " + message` to `System.out` then calls `System.exit(1)` — no exceptions are thrown for domain errors
- Progress logging: `System.out.println` for runtime status messages to console; `PrintWriter` (`PR`) for detailed output to `.lst` files
- No logging framework (no SLF4J, Log4j, java.util.logging)
- No sensitive-data redaction (not applicable to this domain)

### 5) Testing Conventions

- Test file naming/location rule: **No tests exist**
- Mocking strategy norm: [TODO] – not established; will require SecurityManager override or refactor for `System.exit` calls
- Coverage expectation: [TODO] – target ≥ 90% line coverage per problem statement

### 6) Evidence

- `src/util/UTIL.java` – `errorMsg()` / `System.exit(1)`
- `src/fea/FE.java` – constant naming style
- `src/model/FeModelData.java` – field naming style
- Scan output: "No linting or formatting config files found"
