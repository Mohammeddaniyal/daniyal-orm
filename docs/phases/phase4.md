# Daniyal-ORM Phase 4: Dynamic SQL Query Generation

## Overview

In this phase, we built the core dynamic query generation capabilities of Daniyal-ORM. This allows the ORM to programmatically create SQL statements tailored to entity metadata and data, supporting flexible data persistence.

---

## What We Did

### 1. Query and QueryBuilder Classes  
- Created a `Query` class that encapsulates the generated SQL string and the ordered list of parameters corresponding to placeholders.  
- Designed a `QueryBuilder` class that accepts `EntityMeta` and an entity instance to dynamically build queries such as INSERT.

### 2. Common Field Processing Helper  
- Implemented a reusable internal method in `QueryBuilder` to traverse all fields via `FieldMeta`.  
- Integrated validation and conversion calls (`EntityValidator`) during traversal to ensure all values are safe and compliant before building queries.

### 3. Flexible Query Part Construction Using Functional Interface  
- Created a `FieldProcessor` functional interface.  
- Utilized different implementations of this interface to build varied SQL parts (columns, placeholders, conditions) based on query type (INSERT, UPDATE, etc.).  
- This design greatly reduces boilerplate code and allows easy extensibility.

### 4. Separation of Concerns  
- Separated SQL generation logic into `QueryBuilder`, isolating it from database connection and execution logic (handled by `DataManager`).  
- Maintained clean and modular architecture, improving testability and maintainability.

---

## Why This Phase is Important

- Enables dynamic generation of SQL tailored to entity structure and current data.  
- Supports extensibility to cover various SQL operations uniformly.  
- Ensures query building respects validation and type conversion, thus safe and reliable.  
- Dramatically reduces repetitive code by abstracting common field processing logic.

---

## New Learnings and Skills Acquired

- Using functional interfaces/lambdas to customize behavior in a reusable traversal method.  
- Dynamic SQL statement construction with proper parameter binding.  
- Integrating validation seamlessly within query building.  
- Architecting clean separation of concerns for ORM internals.

---

## Next Steps

- Implement support for UPDATE, DELETE, and SELECT queries in `QueryBuilder`.  
- Enhance query features such as conditional updates and batch operations.  
- Improve error handling and add comprehensive unit tests for query generation.

---

This phase marks a major step towards a flexible, maintainable, and reliable ORM capable of handling real-world persistence scenarios.
