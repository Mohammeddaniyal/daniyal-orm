# Phase 10: Validation Enhancements for Update and Delete Operations

## Overview

This phase focused on strengthening the integrity and correctness of the ORM’s update and delete operations by implementing pre-validation checks to avoid database errors and improve user feedback.

***

## Key Features Implemented

### 1. Existence Check Before Update and Delete

- Before performing an **update** or **delete** operation, the ORM now verifies if the target record exists in the database.
- Uses efficient, lightweight SQL queries:
  
  ```sql
  SELECT 1 FROM <table> WHERE <primary_key_column> = ?;
  ```
  
- If the record does not exist, an `ORMException` is thrown with a clear message specifying the missing record details.

### 2. Foreign Key Constraint Validation on Update

- During **update**, for each foreign key field in the entity, a validation check ensures that the referenced parent record exists.
- This prevents violating referential integrity by updating to non-existent foreign key values.
- Throws detailed exceptions indicating which parent table and column caused the violation.

### 3. Foreign Key Constraint Validation on Delete

- Before deleting a record, the ORM checks if the record is referenced by any child tables through foreign key relationships.
- The `TableMetaData` class was extended with a `referenceByList` property tracking the child tables that reference the current table.
- If dependent child rows exist, deletion is blocked and a descriptive exception is raised to inform the user about the constraint.

### 4. `TableMetaData` Enhancement with Parent-Child Relationship Tracking

- During metadata scanning, the ORM builds bi-directional foreign key mappings:
  - For each foreign key, both the parent and child table metadata are updated.
  - Enables efficient lookup of dependencies during validation.

### 5. Clear Exception Messages

- Exception messages were improved throughout the update and delete flows to provide detailed, actionable information:
  - Target table, primary key column, and value.
  - Foreign key relationships involved in validation failures.
  - Differentiation between missing records and constraint violations.

***

## Benefits

- Prevents silent failures or costly database exceptions by proactively validating constraints.
- Facilitates easier debugging and error handling through detailed exception messages.
- Enforces database integrity at the ORM layer, adding a safety layer beyond the database engine.
- Enables the application layer to communicate specific reasons for failure back to users or UI.

***

## Sample Exception Messages

- Update record missing:
  ```
  No record found in table 'student' for primary key 'id' with value '123'.
  ```
- Foreign key violation on update:
  ```
  Foreign key violation: No matching record in parent table 'course' for primary key column 'code' with value 'CS101' (for field 'courseCode').
  ```
- Delete blocked due to child dependency:
  ```
  Cannot delete from table 'course': Record with primary key value 'CS101' is referenced by child table 'enrollments' via foreign key column 'course_id'. Deletion would violate referential integrity.
  ```

***

## Next Steps

- Add logging around validation failures for audit and monitoring.
- Implement unit and integration tests covering validation scenarios.
- Explore supporting cascade deletes and soft deletes with appropriate validations.
- Refactor and optimize query executions for batch operations if needed.

***

This phase significantly improves ORM robustness and reliability in multi-table relational data scenarios.

***
