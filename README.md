# Daniyal ORM Framework

A lightweight Java ORM framework that simplifies database interaction by using annotations to map your Java classes to MySQL tables.  
It automatically loads database metadata, validates mappings, and provides core data management functionalities with reflection and JDBC under the hood.

***

## ✨ Features

- **Annotation-Based Mapping:** Define your entities with `@Table` and annotate fields with `@Column`, `@PrimaryKey`, `@AutoIncrement`, and `@ForeignKey`.  
- **Automatic Metadata Loading:** Scans database schema via JDBC and caches table/column metadata for runtime efficiency.  
- **Entity Scanning & Validation:** Automatically finds entity classes in a user-defined base package, validates consistency with the database schema, and builds metadata mappings.  
- **Transaction Management:** Start and end database connection sessions easily through `DataManager`.  
- **Reflection-Driven CRUD:** Uses reflection with optimized access to read and write entity fields.  
- **Configurable via JSON:** Externalize configurations like JDBC connection details and base package in a `conf.json` file.

***

## 📂 Project Structure

- **annotations/** → Java annotations like `@Table`, `@Column`, `@PrimaryKey`, etc.  
- **config/** → `ConfigLoader` loads settings from `conf.json`.  
- **connection/** → Manages database connections via `ConnectionManager`.  
- **exceptions/** → Custom exception types such as `ORMException`.  
- **manager/** → Core management classes:  
  - `DataManager` – Singleton managing lifecycle, connection, and metadata.  
  - `EntityScanner` – Scans the base package for entity classes and validates schema.  
  - `DatabaseMetaDataLoader` – Loads and caches schema metadata from MySQL.  
- **pojo/** → Metadata containers (`EntityMeta`, `FieldMeta`, `TableMetaData`, etc.).  
- **utils/** → Helpers like case converters and type mappers.

***

## ⚙️ How It Works

### 1. Initialization (`DataManager`)

- Loads DB schema metadata via `DatabaseMetaDataLoader` on startup.  
- Scans the user’s classes in the configured base package for entities.  
- Validates entity fields (annotations vs DB columns) with detailed checks on primary keys, foreign keys, and auto-increment fields.  
- Builds in-memory metadata maps representing entities and their DB counterparts.

### 2. Transaction Lifecycle

- Call `begin()` to open a DB connection.  
- Use `save()`, `update()`, or other CRUD operations (future) on entity objects.  
- Call `end()` to close the connection gracefully.

### 3. Reflection-Based Data Access

- Reflection is used to get/set entity field values efficiently via cached accessible `Field` objects.

***

## 🛠 Configuration (`conf.json`)

Create a JSON file named `conf.json` in your project root or working directory:

```json
{
  "jdbc-driver": "com.mysql.cj.jdbc.Driver",
  "connection-url": "jdbc:mysql://localhost:3306/tmschooldb",
  "username": "tmschool",
  "password": "tmschool",
  "base-package": "com.daniyal.test.ormcore"
}
```

**Config Fields:**

- `jdbc-driver`: Fully qualified class name of your JDBC driver.  
- `connection-url`: JDBC URL for your database.  
- `username`: DB username.  
- `password`: DB password.  
- `base-package`: Java package where all entity classes reside (framework scans this package).

***

## 🚀 Usage Example

Minimal example (`com.daniyal.test.ormcore`):

```java
package com.daniyal.test.ormcore;

import com.daniyal.ormcore.manager.DataManager;
import com.daniyal.ormcore.exceptions.ORMException;
import java.sql.Connection;
import java.sql.SQLException;

public class Eg1 {
    public static void main(String[] args) {
        try {
            DataManager dm = DataManager.getDataManager();
            dm.begin();

            Connection connection = dm.getConnection();
            System.out.println("Connected as: " + connection.getMetaData().getUserName());

            Course c = new Course(1, "Java");
            dm.save(c);

            dm.end();
        } catch (ORMException | SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
```

***

## 🏗 How to Define Entities

Example of an entity class:

```java
import com.daniyal.ormcore.annotations.*;

@Table(name = "courses")
public class Course {
    @PrimaryKey
    @AutoIncrement
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    // Constructors, getters, setters
}
```

***

## 📌 Key Notes

- Entity fields must be annotated with `@Column` to be mapped.  
- `@PrimaryKey`, `@AutoIncrement`, and `@ForeignKey` require `@Column`.  
- The framework verifies each entity's class structure against actual DB tables and raises `ORMException` on mismatch.  
- Reflection uses `setAccessible(true)` once on entity fields to optimize get/set calls.  
- Metadata loading scans the database schema dynamically at startup—no manual syncing needed beyond annotations.  
- Currently supports MySQL; extension to other DBs is possible with minor changes.

***

## 🔮 Next Steps

- Implement save, update, delete, and query operations.  
- Add advanced validation: type compatibility, nullability.  
- Expand transaction management and connection pooling.  
- Add logging and improved error messages.

***

## 🤝 Contribution

Feel free to clone, extend, and customize. For bug reports or feature requests, open issues or pull requests on the repository.
