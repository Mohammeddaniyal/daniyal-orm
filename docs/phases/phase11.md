# Phase 11: Save Validation & Enhanced Data Integrity Checks

## What was added

In this phase, the Daniyal ORM got smarter about checking data before saving, updating, or deleting. This helped catch problems early and made the ORM more reliable and user-friendly.

***

## Save Operation Validation

- Before saving a new entity, the ORM now ensures:
  - All foreign key references actually exist in their parent tables.
  - Data types and constraints align with the database schema.
- If a foreign key value is invalid or missing, the ORM throws a helpful `ORMException` to explain the problem.
- After inserting, it retrieves any auto-generated primary key values like IDs and updates the entity object — no more guesswork about what ID was created!

***

## Update Operation Validation Recap

- Checks that the record to update exists in the database.
- Validates foreign key references exist in parent tables.
- Prevents updating to invalid foreign key values.
- Throws clear exceptions with helpful messages on failures.

***

## Delete Operation Validation Recap

- Ensures the record exists before attempting deletion.
- Checks if the record is referenced by any child table via foreign keys.
- Blocks deletion if such references exist, protecting data integrity.
- Provides clear messages about why a delete was blocked.

***

## How Metadata Helps

- The ORM tracks parent-child relationships dynamically in `TableMetaData`.
- This allows quick access to foreign key constraints and dependent tables.
- Makes validation easy to implement and fast to run.

***

## Why This Matters

- Stops invalid data from sneaking in or breaking your database.
- Saves you from confusing SQL errors by checking things first.
- Makes debugging easy with meaningful error messages.
- Great for beginners learning ORM concepts who want immediate feedback.

***

## Example Save Method (Simplified)

```java
public Object save(Object entity) throws ORMException {
  // Validates foreign keys exist...
  // Builds and executes insert statement...
  // Fetches generated keys and sets them on entity...
  // Throws ORMException with meaningful error messages if fails.
}
```

***

## What’s Next?

- Add logging for validation failures.
- Implement more complex queries and cascading operations.
- Support other databases.
- Enhance fluent API with more features.

***