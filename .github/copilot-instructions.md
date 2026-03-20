# Copilot Instructions for Foto Gallery Directory Structure Generator

## Project Overview

This is a **Spring Boot CLI application** (Java 17) that reads a gallery CSV file (produced by [foto-gallery-qrcode-generator](https://github.com/patbaumgartner/foto-gallery-qrcode-generator)) and generates a directory structure for organising school portrait photos.

For each event found in the CSV the tool creates:

```
{Event Name}/
├── portraits/
│   └── {code}/   ← one directory per gallery code
├── klassenfoto/
├── {prefix}-codes.csv       ← moved from the source folder
└── {prefix}-qr-codes.pdf   ← moved from the source folder
```

## Technology Stack

- **Language**: Java 17
- **Framework**: Spring Boot 3.x (using `CommandLineRunner`)
- **Build Tool**: Maven (use `./mvnw` wrapper)
- **Key Libraries**:
  - Apache Commons CSV (CSV reading)
  - Spring Boot Test / JUnit 5 (testing)

## Project Structure

```
src/main/java/com/fortytwotalents/fotogallery/
├── config/   - AppProperties (@ConfigurationProperties)
├── model/    - GalleryEntry, CsvReadResult (domain model)
├── runner/   - DirectoryStructureRunner (CLI entry point)
└── service/  - CsvReaderService, DirectoryStructureGeneratorService

src/test/java/com/fortytwotalents/fotogallery/
└── service/  - CsvReaderServiceTest, DirectoryStructureGeneratorServiceTest
```

## Build & Test Commands

```bash
# Build JAR (skip tests)
./mvnw clean package -DskipTests

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=ClassName
```

## Running the Application

```bash
# Via Maven
./mvnw spring-boot:run \
  -Dspring-boot.run.jvmArguments="-Dapp.csv-input-path=schulfotos/GS1c-BA-codes.csv -Dapp.output-path=schulfotos"

# Via JAR
java -Dapp.csv-input-path=schulfotos/GS1c-BA-codes.csv \
     -Dapp.output-path=schulfotos \
     -jar target/fotogallery-directory-structure-generator-*.jar
```

### Convenience Scripts

Two wrapper scripts in the project root let you pick a CSV interactively:

```bash
./generate-structure.sh    # Linux / macOS
generate-structure.bat     # Windows
```

They scan the `schulfotos/` folder for `*.csv` files, prompt you to select one,
then run the app and output the directory structure under `schulfotos/`.

## Code Conventions

- **Package**: `com.fortytwotalents.fotogallery`
- **Configuration**: All app settings live in `AppProperties` bound via `@ConfigurationProperties(prefix = "app")`
- **Services**: Stateless Spring `@Service` beans; use constructor injection
- **Testing**: JUnit 5 with `@TempDir`; AssertJ for assertions
- **No REST layer**: This is a CLI tool only — do not add web/REST endpoints

## Git Conventions

This project uses **Conventional Commits** (<https://www.conventionalcommits.org>).

### Commit message format

```
<type>(<scope>): <short summary>

[optional body]

[optional footer(s)]
```

**Types:**

| Type       | Use when…                                                  |
|------------|------------------------------------------------------------|
| `feat`     | Adding or extending a feature                              |
| `fix`      | Fixing a bug                                              |
| `test`     | Adding or updating tests (no production code change)       |
| `refactor` | Restructuring code without changing observable behaviour   |
| `docs`     | Documentation only changes                                |
| `chore`    | Build system, tooling, CI, dependency updates             |
| `ci`       | Changes to GitHub Actions or other CI configuration       |
| `style`    | Formatting / whitespace (no logic change)                 |

**Scopes** (optional but encouraged): `config`, `model`, `service`, `runner`, `scripts`

### Software craftsmanship rules

- **Small, focused commits** — each commit should do exactly one thing and be independently understandable
- **Test commits travel with feature commits** — add `test(…)` commit immediately after the `feat(…)` it covers
- **Never commit a broken build** — all tests must pass before committing
- **Commit message must explain *why*, not just *what*** — if the summary alone is insufficient, add a body
- Prefer many small PRs over one large one; squash only when truly warranted

### Examples

```
feat(service): add klassenfoto directory to generated event structure
test(service): add unit tests for klassenfoto directory creation
fix(service): sanitize event name before using as directory path
docs(github): update copilot instructions with conventional commit guidelines
ci: add GitHub Actions CI workflow for build and test
chore: add Maven wrapper scripts and properties
```



| Property            | Default    | Description                                      |
|---------------------|------------|--------------------------------------------------|
| `app.csv-input-path` | `codes.csv` | Path to the input CSV file                      |
| `app.output-path`   | `.`        | Base directory where the structure is generated  |
