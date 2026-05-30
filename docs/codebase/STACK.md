# Technology Stack

## Core Sections (Required)

### 1) Runtime Summary

| Area | Value | Evidence |
|------|-------|----------|
| Primary language | Java | `src/**/*.java` |
| Runtime + version | Java 25 | `pom.xml` (`maven.compiler.release=25`) |
| Package manager | Maven | `pom.xml` |
| Module/build system | Maven 3 | `pom.xml` |

### 2) Production Frameworks and Dependencies

| Dependency | Version | Role in system | Evidence |
|------------|---------|----------------|----------|
| Java SE standard library | 25 | Core runtime, file I/O, Scanner, collections | All `src/**/*.java` |
| JavaFX Controls (`javafx-controls`) | 21.0.3 | UI controls for visualization window | `pom.xml`, `src/fea/Jvis.java` |
| JavaFX Graphics (`javafx-graphics`) | 21.0.3 | 3D scene rendering and interactive window | `pom.xml`, `src/visual/J3dScene.java` |

### 3) Development Toolchain

| Tool | Purpose | Evidence |
|------|---------|----------|
| Maven (`mvn`) | Build, test, package | `pom.xml` |
| JUnit Jupiter | Unit / regression test runner | `pom.xml` (`junit-jupiter 5.10.2`) |
| AssertJ | Fluent test assertions | `pom.xml` (`assertj-core 3.25.3`) |
| Mockito | Test mocking | `pom.xml` (`mockito-core 5.11.0`) |
| JaCoCo | Code coverage reporting and enforcement | `pom.xml` (`jacoco-maven-plugin 0.8.14`) |
| javafx-maven-plugin | Launch interactive visualizer | `pom.xml` (`javafx-maven-plugin 0.0.8`) |

### 4) Key Commands

```bash
# Build and run all tests
mvn test

# Build, test, and enforce coverage thresholds
mvn verify

# Run solver
mvn exec:java -Dexec.mainClass=fea.Jfem -Dexec.args="<input-file> [output-file]"

# Run mesh generator
mvn exec:java -Dexec.mainClass=fea.Jmgen -Dexec.args="<input-file> [output-file]"

# Run visualizer (interactive JavaFX window)
mvn javafx:run
```

### 5) Environment and Config

- Config sources: `.fem`, `.gen`, `.vis` text files passed as CLI arguments
- Required env vars: None – fully file-driven
- Deployment/runtime constraints: Java 25 JDK required; JavaFX 21 is resolved automatically by Maven

### 6) Evidence

- `pom.xml` – Maven build descriptor (replaces legacy `jc.bat`)
- `src/fea/Jvis.java` – JavaFX 3D entry point
- `src/visual/J3dScene.java` – JavaFX 3D scene setup
- `src/test/java/` – JUnit 5 test suite
