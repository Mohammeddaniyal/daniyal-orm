# Mini ORM (Java)

This is a mini ORM playground project. It introspects your SQL database schema using JDBC `DatabaseMetaData`, then generates Java entity classes with appropriate annotations (like `@Table`, `@Column`, `@PrimaryKey`, `@AutoIncrement`, `@ForeignKey`, etc.).  
**Purpose:** To explore the foundational building blocks behind how ORMs generate and map database tables to Java classes, including basic relationship metadata.

## Features

- Reads DB connection config from `conf.json`
- Connects to any JDBC-compatible RDBMS
- Scans all tables, columns, primary keys, foreign keys, and indexes
- Auto-maps SQL types to Java types with custom mapping logic
- Outputs `.java` classes with detailed annotation scaffolding including foreign keys
- Includes mini example programs to experiment with JDBC metadata in `learning/`
- Personal notes and explanations maintained in `docs/`

## Project Structure

```text
root/
│
├── learning/                 # Mini example files for JDBC metadata exploration
│     ├── eg1psp.java
│     ├── eg2psp.java
│     └── ... (other learning examples)
│
├── docs/                     # Explanations, doubts, and notes
│     ├── explanation.md
│     └── doubts.md
│
├── DAOConnection.java         # Core JDBC connection handler
├── EntityGenerator.java       # Main entity code generator using metadata
├── conf.json                  # Database connection configuration
└── README.md                  # Project documentation (this file)



## Usage (Current Stage)

1. Add your DB connection info into `conf.json`  
2. Run the code generator (`eg2psp.java` or main class)  
3. Generated entity classes with annotations will be created in your project folder

## Status

**WIP / Prototype:**  
- No runtime CRUD or query support yet  
- Generated annotations are placeholders; custom annotation classes need to be defined and integrated  
- Code currently tested in a single folder, packaging planned future work  
- Focus on learning JDBC metadata and code generation fundamentals

---

Daniyal (github.com/Mohammeddaniyal)
