# Daniyal-ORM Phase 8: Query with Filters and Fetch All

## Overview

This phase introduced flexible querying capabilities in the ORM allowing users to:

- Retrieve all records of a given entity type.
- Add dynamic filtering conditions with fluent API methods like `where()`, `and()`, `or()`, and conditional operators (`eq`, `gt`, `lt`, `ge`, `le`, `like`).

***

## What Was Done

### 1. Query Method on DataManager

- Added generic method `query(Class<T> entityClass)` returning a `QueryBuilder<T>` pre-initialized with connection, metadata, and context.
- Validates connection status and entity registration upfront.

### 2. QueryBuilder Enhancements

- Supports fluent condition addition:
    - `where(String column)`, `and(String column)`, `or(String column)` methods initialize a `Condition<T>` helper.
    - Conditional operators (`eq`, `gt`, `lt`, `ge`, `le`, `like`) build parameterized SQL conditions.

- Conditions are stored and combined to form the `WHERE` clause dynamically.

### 3. Executing the Query - `list()`

- Constructs SQL string dynamically starting with `SELECT * FROM table`.
- Appends any accumulated conditions and their parameters.
- Uses `PreparedStatement` for safety.
- Maps result set rows to entity instances using reflection and cached metadata.
- Returns a populated list of entity objects.

### 4. Condition Helper Class

- Encapsulates condition construction on columns.
- Maintains reference back to parent `QueryBuilder` for chaining.
- Supports common SQL operators as methods returning `QueryBuilder` to enable fluent chaining.

***

## Sample Usage

```java
List<Course> courses = dm.query(Course.class).list();
System.out.println("Code  |  Title");
for (Course c : courses) {
    System.out.println(c.getCode() + "   " + c.getTitle());
}

List<Course> filteredCourses = dm.query(Course.class)
    .where("credits").gt(3)
    .and("department").eq("CS")
    .list();
```

***

## Importance

- Provides users a powerful yet simple API for retrieving data with or without filters.
- Enables building complex SQL `WHERE` clauses with ease through method chaining.
- Ensures queries are safe via parameterization.
- Keeps API elegant and consistent with modern ORM design practices.

***

## Next Steps

- Further extend query builder with support for joins, ordering, and pagination.
- Develop CLI-based entity class generator for seamless DB integration.
