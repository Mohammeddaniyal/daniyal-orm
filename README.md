# Daniyal ORM Framework

A lightweight Java ORM I built to cut down on JDBC boilerplate when working with MySQL.  
Instead of writing endless SQL and mapping results by hand, you just annotate your classes and let the framework handle the rest.  
It scans your DB, validates that your entities match the schema, and gives you basic CRUD functionality through reflection.

Think of it as a simplified Hibernate — without the heavy learning curve.

---

## ✨ Features
- **Annotation Mapping**  
  Use `@Table`, `@Column`, `@PrimaryKey`, `@AutoIncrement`, and `@ForeignKey` annotations to declaratively map your Java classes and fields to MySQL tables. The framework processes these at runtime via reflection to generate SQL and manage persistence.

- **Schema-Aware Metadata Loading**  
  On startup, Daniyal-ORM scans your database schema via JDBC metadata APIs, caching table, column, primary key, and foreign key information. This eliminates redundant manual configuration and keeps entities in sync with your DB.

- **Entity Validation**  
  Validates entity classes against the real DB schema: checks for PKs, FKs, nullability, and type mismatches. Early validation blocks common errors and ensures model reliability.

- **Auto-Increment Primary Key Support**  
  When saving entities, Daniyal-ORM fetches auto-generated primary keys, converts types as needed, and sets PK fields in your objects using reflection. Entity state always matches the DB, even for new inserts.

- **Update and Delete Operations**  
  Update and delete support is now fully integrated:  
  - Call `update(entity)` to update existing rows, automatically mapping provided values to the correct columns and using the primary key for the `WHERE` clause.  
  - Call `delete(entity)` to remove rows by primary key—just provide an entity instance with its PK set.

- **Flexible Querying with Fluent API**  
  Introduced `query(Class<T> entityClass)` returning a fluent `QueryBuilder<T>` to fetch entities. Supports dynamic filtering with `where()`, `and()`, `or()`, and SQL operators like `eq`, `gt`, `lt`, `le`, `ge`, and `like`. Enables building complex queries naturally and safely.

- **Simple Session Management**  
  Explicit connection sessions: use `begin()` to start, perform CRUD, then `end()` to close safely. No hidden transactions.

- **Reflection-Driven CRUD Operations**  
  CRUD (`save`, `update`, `delete`, `query`) uses reflection with field caching, reducing boilerplate and keeping operations efficient.

- **Config File Based Setup**  
  All DB connection parameters and base package for entities are defined in a single `conf.json` file, making configuration simple and code-free.

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
│       EntityMetaData.java
│       FieldMetaData.java
│       ForeignKeyMetaData.java
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

import com.daniyal.test.ormcore.entity.*;
import com.daniyal.ormcore.manager.DataManager;
import com.daniyal.ormcore.exceptions.ORMException;
import java.util.List;
public class ExampleUsage {
    public static void main(String[] args) {
        try {
            DataManager dm = DataManager.getDataManager();
            dm.begin();
            
            // Save (Insert) Example
            Course toInsert = new Course("Java");
            dm.save(toInsert);
            System.out.println("Saved course with code: " + toInsert.getCode());

            // Update Example
			Course toUpdate=new Course(toInsert.getCode(),"JavaScript");
			dm.update(c);
            System.out.println("Updated course with code: " + toUpdate.getCode());

            // Delete Example
            Course toDelete = new Course(10);  // constructor with code only
            dm.delete(toDelete);
            System.out.println("Deleted course with code: " + toDelete.getCode());
			
			// Query Example - fetch all courses
            List<Course> courseList = dm.query(Course.class).list();
            System.out.println("Code  |  Title");
            for (Course c : courseList) {
                System.out.println(c.getCode() + "   " + c.getTitle());
            }

            dm.end();
        } catch (ORMException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
```
---

## 🏗 Defining an Entity

```java
package com.daniyal.test.ormcore.entity;

import com.daniyal.ormcore.annotations.*;

@Table(name = "courses")
public class Course {
    @PrimaryKey
    @AutoIncrement
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;
	
	Course() // no arg constructor is must
	{
	}

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

* Add support for `SELECT`.
* Better validation and error messages.
* Connection pooling + improved transaction handling.
* Logging, tests, and CI pipeline.

---

## 🤝 Contributing

If you’d like to improve it, feel free to fork and open a PR.
Bug reports and feature requests are welcome via GitHub Issues.

```
