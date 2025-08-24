# Daniyal ORM Framework

A lightweight Java ORM I built to cut down on JDBC boilerplate when working with MySQL.  
Instead of writing endless SQL and mapping results by hand, you just annotate your classes and let the framework handle the rest.  
It scans your DB, validates that your entities match the schema, and gives you basic CRUD functionality through reflection.

Think of it as a simplified Hibernate — without the heavy learning curve.

---

## ✨ Features (Updated)

- **Annotation Mapping**  
  Use `@Table`, `@Column`, `@PrimaryKey`, `@AutoIncrement`, and `@ForeignKey` annotations to declaratively map your Java classes and their fields to MySQL tables and columns. The framework processes these at runtime via reflection to generate queries and manage data.

- **Schema-Aware Metadata Loading**  
  On startup, Daniyal-ORM scans the connected database schema via JDBC metadata APIs, caching table, column, primary key, and foreign key info. This eliminates redundant manual schema configuration and keeps your entity classes in sync with the database.

- **Entity Validation**  
  Before runtime operations, the framework validates entity class structures against the actual database schema. Checks include primary key presence, foreign key relations, column nullability, and data types. This early validation prevents common runtime errors.

- **Auto-Increment Primary Key Support**  
  Insert operations request generated keys from the database, and Daniyal-ORM automatically retrieves these values, type-converts them, and sets them on the applicable entity fields using reflection. This keeps entity state consistent with database state seamlessly.

- **Simple Session Management**  
  Open and close database connection sessions explicitly with `begin()` and `end()`, ensuring controlled transactional operations.

- **Reflection-Driven CRUD Operations**  
  Reflection with cached field lookups is used to read and write entity attributes efficiently, minimizing overhead and boilerplate code in CRUD methods like `save()`.

- **Config File Based Setup**  
  Database connection parameters, including JDBC driver class, connection URL, username/password, and entity base package, are configured in a `conf.json` file for easy customization without code changes.


---

## 📂 Project Layout

```

├───annotations
│       AutoIncrement.java
│       Column.java
│       ForeignKey.java
│       PrimaryKey.java
│       Table.java
│
├───config
│       ConfigLoader.java
│
├───connection
│       ConnectionManager.java
│
├───exceptions
│       ORMException.java
│
├───generator        # placeholder for future features
│
├───manager
│       DatabaseMetaDataLoader.java
│       DataManager.java
│       EntityScanner.java
│
├───pojo
│       ColumnMetaData.java
│       EntityMeta.java
│       FieldMeta.java
│       ForeignKeyInfo.java
│       TableMetaData.java
│
├───query
│       FieldProcessor.java
│       Query.java
│       QueryBuilder.java
│
├───utils
│       CaseConvertor.java
│       TypeMapper.java
│
└───validation
EntityValidator.java

````

---

## ⚙️ How It Works

1. **Initialization**  
   When you start the app, `DataManager` loads the DB schema, scans your entity package, and matches classes against actual tables.

2. **Session lifecycle**  
   - Call `begin()` → opens a connection  
   - Run CRUD ops (`save()`, `update()`, etc.)  
   - Call `end()` → closes the connection

3. **Reflection under the hood**  
   Entities are read/written using reflection. Fields are cached after the first lookup for efficiency.

4. **Validation & conversion**  
   Before hitting the DB, values are validated (size, nulls, etc.) and converted into proper SQL-friendly types.

5. **Dynamic query building**  
   Queries are built automatically (parameterized). Right now `INSERT` works; `UPDATE`, `DELETE`, and `SELECT` are planned.

---

## 🛠 Configuration (`conf.json`)

Drop a `conf.json` in the project root:

```json
{
  "jdbc-driver": "com.mysql.cj.jdbc.Driver",
  "connection-url": "jdbc:mysql://localhost:3306/tmschooldb",
  "username": "tmschool",
  "password": "tmschool",
  "base-package": "com.daniyal.test.ormcore"
}
````

---

## 🚀 Example Usage

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
			
			// id is AutoIncremented
            Course c = new Course("Java");
            dm.save(c);

            dm.end();
        } catch (ORMException | SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
```

---

## 🏗 Defining an Entity

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

---

## 📌 Notes

* Every mapped field needs a `@Column`.
* `@PrimaryKey`, `@AutoIncrement`, and `@ForeignKey` must be used together with `@Column`.
* Validation runs both at startup (entity vs DB) and before persisting data.
* Reflection calls are cached internally for speed.
* Right now it’s MySQL-only, but adding other databases should be straightforward.

---

## 🔮 Roadmap

* Add support for `UPDATE`, `DELETE`, and `SELECT`.
* Better validation and error messages.
* Connection pooling + improved transaction handling.
* Logging, tests, and CI pipeline.

---

## 🤝 Contributing

If you’d like to improve it, feel free to fork and open a PR.
Bug reports and feature requests are welcome via GitHub Issues.

```

---
