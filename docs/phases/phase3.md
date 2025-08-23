# Daniyal-ORM Phase 3: Entity Value Validation and Runtime Conversion

## Overview

This phase focused on integrating runtime validation and conversion of entity field values just before persisting them into the database. It ensures that the data adheres to the schema constraints and is correctly formatted for JDBC operations.

---

## What We Did

### 1. Runtime Validation of Field Values  
- Used the `EntityValidator` utility to check each entity field’s value at save-time.  
- Validated:  
  - Nullability according to database schema constraints.  
  - String lengths against maximum column sizes to prevent data truncation.  
  - Character length restrictions for single-character columns.  

### 2. Conversion of Java Types for Persistence  
- Converted `java.util.Date` to appropriate SQL date/time types (`java.sql.Date` or `java.sql.Timestamp`) based on the column’s data type.  
- Adjusted field values where necessary to make them compatible with JDBC APIs, ensuring smooth parameter binding.

### 3. Enforcement of Validation Before Query Execution  
- Integrated validation within the persistence workflow, ensuring invalid or mismatched data is caught early by throwing precise exceptions.  
- Prevented potential runtime SQL errors related to invalid data inputs.

---

## Why This Phase is Important

- Validates data integrity before database interaction, avoiding costly rollback and debugging from failed transactions.  
- Ensures persistence-layer robustness by combining static metadata checks with runtime validation.  
- Streamlines the save operation, making sure only correct, well-formed data reaches the database.

---

## New Learnings and Skills Acquired

- Applying layered validation: combining static metadata with runtime data checks.  
- Deepening knowledge of JDBC data binding expectations and type conversions.  
- Handling special cases and edge conditions during object-to-relational mapping.  
- Designing clear error handling strategies for data validation failures.

---

## Next Steps

- Develop the dynamic query generation component that leverages the metadata and validated data.  
- Reduce boilerplate in query building by abstracting common traversal and validation steps.

---

This phase makes Daniyal-ORM more resilient and prepares the groundwork for dynamic SQL generation and execution.
