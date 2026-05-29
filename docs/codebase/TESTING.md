# Testing Patterns

## Core Sections (Required)

### 1) Test Stack and Commands

- Primary test framework: **None** – no test framework present
- Assertion/mocking tools: None
- Commands:

```bash
# No test commands exist yet
# Target state (after modernization):
mvn test
mvn verify   # includes JaCoCo coverage report
```

### 2) Test Layout

- Test file placement pattern: **None** – no `src/test/` or equivalent directory
- Naming convention: [TODO] – to be established; target: `*Test.java` in `src/test/java/<package>/`
- Setup files and where they run: [TODO]

### 3) Test Scope Matrix

| Scope | Covered? | Typical target | Notes |
|-------|----------|----------------|-------|
| Unit | No | Parsers, Gauss rules, shape functions, material laws | Blocked by static shared state and `System.exit` |
| Integration | No | Solver + model round-trip | Requires example `.fem` files as fixtures |
| E2E / Regression | No | Full example runs compared to golden `.lst.1` / `.mesh` | Highest value: validates numerical behavior |

### 4) Mocking and Isolation Strategy

- Main mocking approach: [TODO] – not yet established
- Key blocker: `UTIL.errorMsg()` calls `System.exit(1)`, which terminates the JVM in tests. Must be replaced with a proper exception before unit tests can run.
- Static shared state in `Element`, `FeModelData`, `FeLoadData` must be reset between tests or converted to instance state to avoid test interference.

### 5) Coverage and Quality Signals

- Coverage tool: [TODO] – JaCoCo targeted
- Coverage threshold: ≥ 90% line coverage (per problem statement)
- Current reported coverage: 0% (no tests)
- Known gaps: entire codebase; visualization package will require special treatment (UI/3D rendering)

### 6) Evidence

- Scan output: "No performance testing configs detected" / "No CI/CD pipelines detected"
- `src/util/UTIL.java` – `System.exit(1)` in error path blocks testability
- `src/elem/Element.java` – static `fem`, `kmat`, `evec` fields are test pollution sources
- `example01/f.lst.1`, `example02/f.lst.A`, `example04/hole3d.lst.1` – golden output files suitable for regression tests
