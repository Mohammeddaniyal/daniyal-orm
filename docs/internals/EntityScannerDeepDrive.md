# EntityScanner Full Deep-Dive — Daniyal ORM

This is a fully detailed, continuous explanation of how your `EntityScanner` works, including directories, JARs, class loading, annotation handling, metadata building, and common pitfalls.

---

## 1. Why scanning is needed in an ORM

In an annotation-based ORM like Daniyal ORM, scanning is necessary to:

1. Discover **entity classes** under a user-provided base package (e.g., `com.app.entities`).
2. Load their **annotations** (`@Table`, `@Column`, `@PrimaryKey`, etc.) via reflection.
3. Validate that these declarations **match the database schema** (table names, columns, PK/FK, types).
4. Build **metadata maps** used by query builders and entity managers.

`EntityScanner` handles steps 1–2 and triggers step 3 to assemble step 4.

---

## 2. Key classes, annotations, and data structures

### Classes & utilities

* `EntityScanner` — main class performing scanning and reflection.
* `TypeMapper` — checks Java↔SQL type compatibility.
* `CaseConvertor` — converts CamelCase class names to snake\_case (fallback table names).
* `ORMException` — throws precise errors on mismatches or violations.

### Annotations

* `@Table(name = "...")` — marks an entity and specifies table name.
* `@Column(name = "...")` — maps a field to a database column.
* `@PrimaryKey` — marks the primary key field.
* `@AutoIncrement` — marks auto-incremented fields.
* `@ForeignKey(parent = "...", column = "...")` — describes a foreign key relationship.

### Metadata objects

* `EntityMetaData` — per-entity metadata:

  * `Class<?> entityClass`
  * `Constructor<?> entityNoArgConstructor`
  * `String tableName`
  * `Map<String, FieldMetaData> fieldMetaDataMap` keyed by column name
  * `FieldMetaData primaryKeyFieldMetaData`
  * `FieldMetaData autoIncrementFieldMetaData`

* `FieldMetaData` — per-field mapping:

  * `Field field`
  * `String columnName`
  * `boolean isPrimaryKey, isAutoIncrement, isForeignKey`
  * `ForeignKeyMetaData foreignKeyMetaData` (optional)

* `TableMetaData` — database table schema:

  * `String tableName`
  * `Map<String, ColumnMetaData> columnMetaDataMap`

* `ColumnMetaData` — DB column schema: name, SQL type, size, PK/FK flags, auto-increment, etc.

* `ForeignKeyMetaData` — FK structure: `fkColumn`, `pkTable`, `pkColumn`

### Global maps

* `Map<Class<?>, EntityMetaData> entitiesMetaMap` — main entity registry.
* `Map<String, TableMetaData> tableMetaDataMap` — database schema cache.
* `Map<String, Class<?>> tableNameToClassMap` — reverse lookup: table → entity class.

---

## 3. High-level scanning flow

```
scanBasePackage(basePackage, tableMetaDataMap, tableNameToClassMap)
  ├─ convert package to resource path: basePackage.replace('.', '/')
  ├─ enumerate resources via ClassLoader
  │   ├─ if resource protocol == "file" → scanDirectory(...)
  │   └─ if resource protocol == "jar"  → scanJar(resource)
  ├─ iterate every JAR in java.class.path (to catch JARs missing directory entries)
  └─ return entitiesMetaMap
```

**Why iterate both resource URLs and classpath JARs?**

* Some JARs do not expose directory entries. Relying solely on `ClassLoader.getResources(path)` may miss classes. Scanning all JARs ensures nothing is skipped.

---

## 4. Package to resource path conversion

Always convert package names to **forward-slash resource paths**:

```java
String path = basePackage.replace('.', '/');
```

* Works in both directory scanning and JAR entry scanning.
* Using `File.separator` is **wrong** for JAR entries on Windows (`\` vs `/`).

---

## 5. Scanning directories (`file:` protocol)

1. Resolve the `file:` URL to a `File` object.
2. Recursively traverse directories.
3. Select `.class` files.
4. Build FQCN: `packageName + "." + simpleClassName`
5. Load the class: `Class.forName(className)`.
6. Call `handleClassMetaData(clazz, ...)` to validate annotations and populate metadata.

---

## 6. Scanning JARs

### A) Using `jar:` URLs

* Some JARs expose a directory entry, returning `jar:` URLs.
* Open the `JarFile`, iterate entries, filter `.class` files, convert to class names, load classes.
* Limitations: Not all JARs expose package directories.

### B) Scanning all classpath JARs (robust)

* Read `System.getProperty("java.class.path")`.
* Split by `File.pathSeparator`.
* For each `.jar`, open with `JarFile`, iterate entries, filter by package path, load classes.
* Guarantees discovery of all classes on the classpath.

---

## 7. Handling annotations and building metadata (`handleClassMetaData`)

For each loaded class:

1. Check `@Table`. Skip if missing.
2. Determine table name: annotation or `snake_case` fallback.
3. Verify table exists in `tableMetaDataMap`.
4. Require a no-arg constructor.
5. Collect fields (consider switching `getDeclaredFields()` to include private fields).
6. For each `@Column` field:

   * Verify column exists in DB metadata.
   * Validate PK, AI, FK annotations against DB.
   * Validate type compatibility using `TypeMapper`.
7. Track PK and AI fields.
8. Ensure all table columns have corresponding fields.
9. Build `FieldMetaData` for each field.
10. Build `EntityMetaData` for the class and register in `entitiesMetaMap` and `tableNameToClassMap`.

---

## 8. Field-level validation

* **Primary Key**: Field annotated with `@PrimaryKey` must match DB PK.
* **Auto Increment**: Field annotated with `@AutoIncrement` must match DB AI column.
* **Foreign Key**: Field annotated with `@ForeignKey` must match DB FK metadata.
* **Type compatibility**: Java type must be compatible with DB type.
* **Completeness**: All columns must have corresponding fields; else throw `ORMException`.

---

## 9. Common pitfalls and fixes

| Pitfall                    | Fix                                                          |
| -------------------------- | ------------------------------------------------------------ |
| Wrong path separator       | Always use `/` for resource paths.                           |
| URL decoding issues        | Use `URLDecoder.decode(..., "UTF-8")` or `JarURLConnection`. |
| Not finding JARs           | Also iterate `java.class.path`.                              |
| Using `getFields()` only   | Use `getDeclaredFields()` for private/protected fields.      |
| Missing no-arg constructor | Add a public or protected no-arg constructor.                |
| Entity ↔ DB mismatch       | Ensure annotations match table schema precisely.             |

---

## 10. Runtime example walkthrough

**Given:** `Course` entity class in `com.daniyal.test.ormcore.entity`:

1. Scanner finds `Course.class` in `dist/pojo.jar`.
2. `handleClassMetaData` sees `@Table(name="course")`.
3. Retrieves `TableMetaData` for `course` from `tableMetaDataMap`.
4. Iterates fields:

   * `@Column(name="id") @PrimaryKey @AutoIncrement int id` → validated.
   * `@Column(name="title") String title` → validated.
5. Builds `FieldMetaData` for each field.
6. Builds `EntityMetaData` for `Course`.
7. Registers `entitiesMetaMap.put(Course.class, entityMetaData)`.

Result: ORM can now map `Course` instances to `course` table.

---

## 11. Best practices and future improvements

* Cache scan results to avoid repeated scanning.
* Parallelize classpath JAR scanning for large projects.
* Add allow/deny filters to exclude test or irrelevant classes.
* Support nested/fat JARs if using uber JARs.
* Support module path introspection (Java 9+) for JPMS.
* Allow pluggable type mappers for custom SQL types.

---

## 12. TL;DR

* Convert base package to `/`-separated path.
* Scan resources and all JARs on the classpath.
* For each class, validate annotations against DB schema.
* Build `EntityMetaData` and `FieldMetaData` objects.
* Handle PK, AI, FK, type compatibility, and completeness.
* Cache results; fix common pitfalls.

This ensures the ORM robustly discovers entities from both **directories** and **JARs**, fully validated against the database.
