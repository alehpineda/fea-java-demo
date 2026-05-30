# External Integrations

## Core Sections (Required)

### 1) Integration Inventory

| System | Type | Purpose | Auth model | Criticality | Evidence |
|--------|------|---------|------------|-------------|----------|
| Local filesystem | File I/O | Read `.fem`/`.gen`/`.vis` input; write `.lst`/`.mesh`/`.res` output | None – local file path only | High (only I/O mechanism) | `src/util/FeScanner.java`, `src/util/FePrintWriter.java` |
| JavaFX (`org.openjfx`) | UI/runtime library | Interactive 3D visualization of FE results | None | Low (visualization only) | `pom.xml`, `src/fea/Jvis.java`, `src/visual/J3dScene.java` |

### 2) Data Stores

| Store | Role | Access layer | Key risk | Evidence |
|-------|------|--------------|----------|----------|
| Plain-text input files (`.fem`, `.gen`, `.vis`) | FE model definition, mesh generation commands, visualization config | `util.FeScanner` (token scanner over `java.util.Scanner`) | File not found → `System.exit(1)` | `src/util/FeScanner.java` |
| Plain-text output files (`.lst`, `.mesh`, `.res`) | Solver output, mesh data, result data | `util.FePrintWriter` (wraps `PrintWriter`) | Cannot open → `System.exit(1)` | `src/util/FePrintWriter.java` |

### 3) Secrets and Credentials Handling

- No credentials, tokens, or secrets are used
- No `.env` file, no environment variable reads detected

### 4) Reliability and Failure Behavior

- Retry/backoff: None – any I/O error immediately calls `System.exit(1)`
- Timeout: None configured
- Circuit-breaker: None

### 5) Observability for Integrations

- Logging around file operations: minimal – file-not-found error printed to stdout before exit
- Metrics/tracing: None
- Missing visibility gaps: no structured error reporting; no stack traces surfaced to user

### 6) Evidence

- `src/util/FeScanner.java` – wraps `java.util.Scanner(new File(fileIn))`
- `src/util/FePrintWriter.java` – wraps `PrintWriter(new BufferedWriter(new FileWriter(fileOut)))`
- `src/util/UTIL.java` – `errorMsg()` exits on any I/O or domain error
