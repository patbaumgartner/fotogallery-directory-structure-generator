[![CI](https://github.com/patbaumgartner/foto-gallery-directory-structure-generator/actions/workflows/ci.yml/badge.svg)](https://github.com/patbaumgartner/foto-gallery-directory-structure-generator/actions/workflows/ci.yml) [![Release](https://github.com/patbaumgartner/foto-gallery-directory-structure-generator/actions/workflows/release.yml/badge.svg)](https://github.com/patbaumgartner/foto-gallery-directory-structure-generator/actions/workflows/release.yml) [![Java](https://img.shields.io/badge/Java-17-blue?logo=openjdk)](https://openjdk.org/) [![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-6DB33F?logo=spring-boot)](https://spring.io/projects/spring-boot) [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

# Foto Gallery Directory Structure Generator

A Spring Boot command-line tool that reads a gallery CSV file (produced by [foto-gallery-qrcode-generator](https://github.com/patbaumgartner/foto-gallery-qrcode-generator)) and generates a ready-to-use directory structure for organising school portrait photos.

---

## Table of Contents

* [Features](#features)
* [Prerequisites](#prerequisites)
* [Getting Started](#getting-started)
  * [Download a release](#download-a-release)
  * [Build from source](#build-from-source)
* [Usage](#usage)
  * [Convenience scripts](#convenience-scripts)
  * [Direct invocation](#direct-invocation)
* [Generated directory structure](#generated-directory-structure)
* [Configuration](#configuration)
* [Contributing](#contributing)
* [Code of Conduct](#code-of-conduct)
* [License](#license)

---

## Features

* 📂 **Directory structure generation** — creates an event folder with `portraits/` and `klassenfoto/` subdirectories from a gallery CSV
* 🗂️ **Per-code portrait folders** — one subdirectory per gallery code inside `portraits/`
* 📄 **File organisation** — automatically moves the source CSV and its matching PDF into the event folder
* 🖥️ **Interactive script** — prompts you to select a CSV when multiple events are present in `schulfotos/`

---

## Prerequisites

| Requirement | Version |
|---|---|
| Java (JDK) | 17+ |
| Maven | 3.9+ |

> **Tip:** A Maven wrapper (`./mvnw` / `mvnw.cmd`) is included, so you only need Java installed.

---

## Getting Started

### Download a release

Download the pre-built JAR from the [Releases](https://github.com/patbaumgartner/foto-gallery-directory-structure-generator/releases) page.

### Build from source

```bash
# Clone the repository
git clone https://github.com/patbaumgartner/foto-gallery-directory-structure-generator.git
cd foto-gallery-directory-structure-generator

# Build (skipping tests for speed)
./mvnw clean package -DskipTests

# Build including tests
./mvnw clean verify
```

The fat JAR is produced at `target/fotogallery-directory-structure-generator-*.jar`.

---

## Usage

### Convenience scripts

Two wrapper scripts in the project root scan `schulfotos/` for CSV files, prompt you to pick one, and run the application automatically.

**Linux / macOS:**

```bash
./generate-structure.sh
```

**Windows:**

```cmd
generate-structure.bat
```

Example session:

```
Available CSV files:
  1) GS1c-BA-codes.csv
  2) GS1d-BA-codes.csv

Select a CSV file [1-2]: 1

Using: GS1c-BA-codes.csv
Output directory: /path/to/schulfotos
```

### Direct invocation

**Using Maven:**

```bash
./mvnw spring-boot:run \
  -Dspring-boot.run.jvmArguments="-Dapp.csv-input-path=schulfotos/GS1c-BA-codes.csv -Dapp.output-path=schulfotos"
```

**Using JAR:**

```bash
java -Dapp.csv-input-path=schulfotos/GS1c-BA-codes.csv \
     -Dapp.output-path=schulfotos \
     -jar target/fotogallery-directory-structure-generator-*.jar
```

---

## Generated directory structure

For each event found in the CSV the tool creates:

```
schulfotos/
└── GS1c BA/
    ├── portraits/
    │   ├── 59UN-FSQJ-T1OE/
    │   ├── 59UN-0ARU-GKUL/
    │   └── ...
    ├── klassenfoto/
    ├── GS1c-BA-codes.csv
    └── GS1c-BA-qr-codes.pdf
```

The source CSV and its matching PDF (same filename prefix) are **moved** into the event folder.

---

## Configuration

All options can be set via `application.properties`, environment variables, or JVM system properties.

| Property             | Default     | Description                                     |
|----------------------|-------------|-------------------------------------------------|
| `app.csv-input-path` | `codes.csv` | Path to the input CSV file                      |
| `app.output-path`    | `.`         | Base directory where the structure is generated |

---

## Contributing

Contributions are welcome! Please read [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines on how to open issues, suggest improvements, and submit pull requests.

---

## Code of Conduct

This project follows the [Contributor Covenant Code of Conduct](CODE_OF_CONDUCT.md). By participating you agree to uphold it.

---

## License

This project is licensed under the [MIT License](LICENSE).
