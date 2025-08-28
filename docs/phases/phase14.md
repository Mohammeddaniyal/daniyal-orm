# Phase 14 Summary: Database Views Integration and Read-Only Entity Generation

## Overview

In this phase, the key focus was on extending the system to **scan and generate entities for database views**, alongside existing support for tables. Special handling was implemented to respect the read-only nature of views in SQL generation and ORM entity lifecycle.

***

## Major Enhancements

### 1. Views Scanning Alongside Tables

- The entity scanning process now **names and identifies views along with tables** during the metadata extraction phase.
- Views are distinguished using a flag `isView` within `TableMetaData`.
- Scanning for views intentionally excludes:
  - Foreign keys
  - Primary key detection
  - Auto-increment properties
- Only essential column metadata (name, type, size, nullability) is extracted for views.

### 2. SQL Generation Strategy for Views

- For views, **only SELECT queries are generated** since views are read-only.
- Insert, update, and delete SQL generation steps are **skipped** for views.
- Due to the query chaining mechanism (`query().where().eq().and().gt().list()`), **static caching of SELECT SQL for views was not implemented**, as dynamic query generation better supports flexible filtering and aggregation.

### 3. CLI Option to Generate View Entities

- Added CLI support with a new option `--views` to allow users to specify which views should have POJOs generated.
- This approach enables selective generation of view entities, preventing unnecessary code for unwanted views.
- View entities are generated only when explicitly requested via this option.

### 4. Annotation and Representation of View POJOs

- Generated POJOs for views are annotated using a custom annotation:

  ```java
  @View(name = "view_name")
  ```

- This clearly flags these classes as entities mapped to database views.
- This distinction ensures ORM or runtime layers treat these as **read-only entities**.
- View POJOs **do not have primary key (`@Id`) or auto-increment annotations**, reflecting their read-only status.

***

## Architectural and Design Benefits

- **Clarity and Consistency:** Views and tables coexist smoothly in metadata, yet receive tailored handling.
- **Performance:** Avoids unnecessary SQL caching for views, leveraging dynamic query composition.
- **User Control:** CLI option empowers users to manage entity generation explicitly.
- **Maintainability:** Clear annotations and flagging help future developers understand read-only nature of view entities.

***

## Next Steps Recommendations

- Consider enhancing the ORM runtime to enforce immutability of view entities.
- Document CLI options thoroughly for user convenience.
- Explore performance improvements by selectively caching parts of frequently used dynamic view queries if needed.

***

This phase marks a significant step forward in handling diverse database structures with flexibility and clarity.
