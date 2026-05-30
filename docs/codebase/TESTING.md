# Testing Patterns

## Core Sections (Required)

### 1) Test Stack and Commands

- Primary test framework: **JUnit 5** (`org.junit.jupiter`)
- Assertion/mocking tools: AssertJ; Mockito is available as a test dependency
- Commands:

    mvn test
    mvn verify   # includes JaCoCo coverage report and checks

### 2) Test Layout

- Test file placement pattern: `src/test/java/<package>/`
- Naming convention: `*Test.java`; shared helpers live under `src/test/java/testutil/`
- Setup files and where they run: tests call `LegacyStateReset.resetAll()` from `@BeforeEach` where legacy static state is involved

### 3) Test Scope Matrix

| Scope | Covered? | Typical target | Notes |
|-------|----------|----------------|-------|
| Unit | Yes | Parsers, Gauss rules, material laws, factories, coordinate helpers | Uses JUnit 5 and AssertJ |
| Integration | Yes | Solver + model round-trip; mesh generator flows | Uses shipped example fixtures and temporary output directories |
| E2E / Regression | Yes | Full example runs compared to golden `.lst.*` / `.mesh` outputs | Numerical values are compared with floating-point tolerances |

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
