# Phase 9: CLI Entity Generator Implementation

## Overview

- Implemented CLI tool for generating Java entity classes from DB schema.
- Accepts arguments: `--package=`, `--output=`, `--tables=`, `--config=`.
- Dynamically creates package directory structure inside the output dir.
- Uses ORM metadata loaders to generate entity source with annotations.
- Validates input arguments and handles error reporting.

## Usage Examples

- See README.md usage section.

## Code Details

- Argument parsing with simple prefix checks.
- Config loaded from user-specified or default `conf.json`.
- DB connection via `ConnectionManager.getConnection`.
- Metadata queries: tables, columns, PKs, FKs.
- Generates Java source files using `RandomAccessFile`.
- Annotation-based Java code generation.
- Supports filtering specific tables or all.
- Handles output directory creation and file writing.

## Next Steps

- Add tests and error handling improvements.
- Consider using CLI libraries like Picocli for parsing.
- Implement more flexible config options.
- Add support for incremental generation or updates.
