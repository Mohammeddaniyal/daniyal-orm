# Daniyal ORM

![Java](https://img.shields.io/badge/Java-8-blue?logo=openjdk&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green)

> A lightweight Java ORM for MySQL with annotation-based mapping, fluent queries, and a CLI for entity generation.


Hey! I built **Daniyal ORM** to make working with MySQL in Java a lot less painful.
Instead of writing endless SQL and manually mapping results, you just annotate your classes, and the framework handles the rest.

It scans your database, validates your entities, and gives you basic CRUD operations using reflection.

Think of it as a **simplified Hibernate**, without the heavy learning curve.

---

### Why Use This?

- **Cut Boilerplate:** Avoid writing repetitive JDBC code and manual result mapping.  
- **Annotation-Based Mapping:** Map Java classes to tables with simple annotations — no XML or complex setup.  
- **Fluent Queries:** Build readable queries using the intuitive fluent API instead of raw SQL.  
- **CLI Entity Generator:** Quickly generate entity classes from your database schema.  
- **Lightweight & Focused:** Perfect for learning and small-to-medium projects. It’s simple, fast, and easy to understand.  
- **Learning-Friendly:** Great for students and developers who want to explore ORM concepts without the overhead of big frameworks.

---

### Why I Built This

I built **Daniyal ORM** as a **learning project** to understand how frameworks work under the hood. Some motivations behind it:  

- To **explore ORM internals**: How entities, metadata, and reflection can interact with a database.  
- To **practice framework-style programming**: Building a reusable, structured library from scratch.  
- To **experiment with modern Java features** like reflection, annotations, and fluent APIs.  
- To **have a personal, lightweight ORM** for small projects or prototypes.  
- To **improve my coding skills** by solving real problems: entity validation, session management, query building, and CLI tooling.  

> This project isn’t meant to compete with Hibernate or other full-scale ORMs — it’s a learning-focused, lightweight solution that’s easy to understand, extend, and experiment with.  

---



## ⚡ Quick Start

```java
DataManager dm = DataManager.getDataManager();
dm.begin();

// Save a new course
Course course = new Course("Java");
dm.save(course);
System.out.println("Saved course with ID: " + course.getId());

// Fetch all courses
List<Course> courses = dm.query(Course.class).list();
courses.forEach(c -> System.out.println(c.getId() + " | " + c.getName()));

dm.end();
```

It’s that simple.

---

## ✨ Features


* **Annotation Mapping**
  Use `@Table`, `@Column`, `@PrimaryKey`, `@AutoIncrement`, and `@ForeignKey` to map your classes and fields to DB tables. Reflection does all the heavy lifting.

* **Schema-Aware Metadata**
  On startup, Daniyal ORM scans your database and caches table info, columns, PKs, and FKs. Keeps entities in sync without extra setup.

* **Entity Validation**
  Checks your classes against the real DB schema: primary keys, foreign keys, nullability, and type mismatches. Catch issues early.

* **Validation & Data Integrity**  
  Ensures foreign keys exist, prevents invalid updates/deletes, and gives meaningful error messages.

* **Auto-Increment Support**
  Inserts automatically fetch generated keys and update your object fields via reflection. No more manual syncing.

* **Update & Delete Made Easy**

  * `update(entity)` updates rows using the primary key.
  * `delete(entity)` removes rows using the primary key.

* **Flexible Fluent API** 

  ```java
  List<Course> javaCourses = dm.query(Course.class)
                               .where("name").eq("Java")
                               .list();
  ```

  Supports `where()`, `and()`, `or()`, and operators like `eq`, `gt`, `lt`, `le`, `ge`, `like`. Build complex queries naturally.

* **Simple Session Management**
  Open a session with `begin()`, do CRUD, then `end()` to close safely. No hidden transactions.

* **Reflection-Powered CRUD**
  Field lookups are cached internally. Fast and zero boilerplate.

* **Config File Setup**
  All DB connection info and base package are defined in `conf.json`. No code changes needed.

* **Automatic Compilation & JAR Packaging**
  After generating entity classes, the CLI can **compile them into `.class` files** and **package them into a ready-to-use JAR (`dist/pojo.jar`)**. No manual `javac` or `jar` commands needed.

* **SQL Statement Caching for Faster CRUD**
  SQL statements for each entity are **generated once at metadata load** and cached internally. This eliminates repeated SQL string generation during runtime, improving performance for CRUD operations.

* **Views Support & Read-Only Entities**
  Daniyal ORM can now generate entities for **database views**. 
  - Use the new CLI `--views` option to generate view POJOs.
  - View entities are annotated with `@View(name="view_name")`.
  - Read-only: no primary key or auto-increment; insert/update/delete are disabled.
  - Supports dynamic SELECT queries with fluent API.

* **Entity Caching with @Cacheable**  
  - Mark an entity with `@Cacheable` to preload its data into memory at startup.  
  - The ORM automatically fetches all rows of that entity during scanning and stores them in a centralized cache.  
  - Queries on cached entities are served directly from memory, reducing DB calls.  
  - Safe usage: cached lists are deep-cloned before being returned, preventing accidental state corruption.  

* **Unified Query Abstraction**  
  - A common `Queryable<T>` API hides the difference between **database-backed queries** and **in-memory cached queries**.  
  - If the entity is cacheable, `query()` returns a `CachedQueryBuilder`; otherwise, it returns the standard `QueryBuilder`.  
  - This gives a **consistent query experience** whether working with DB or cache.

---

## 📂 Project Layout

```
com.daniyal.ormcore
├───annotations        # ORM annotations (@Table, @Column, @PrimaryKey, @AutoIncrement, @ForeignKey, @Cacheable, @View)
│       AutoIncrement.java
│       Cacheable.java
│       Column.java
│       ForeignKey.java
│       PrimaryKey.java
│       Table.java
│       View.java
│
├───config             # Configuration loading
│       ConfigLoader.java
│
├───connection         # Database connection handling
│       ConnectionManager.java
│
├───exceptions         # Custom ORM exceptions
│       ORMException.java
│
├───generator          # CLI: Entity generation from DB schema
│       EntityGenerator.java
│
├───manager            # Core managers (scanning, metadata, SQL, data orchestration)
│       DatabaseMetaDataLoader.java
│       DataManager.java
│       EntityScanner.java
│       SQLStatementGenerator.java
│
├───metadata           # Metadata models (table/column/entity/SQL)
│       ColumnMetaData.java
│       EntityMetaData.java
│       FieldMetaData.java
│       ForeignKeyMetaData.java
│       SQLStatement.java
│       TableMetaData.java
│
├───query              # Query abstraction (DB vs Cache)
│       CachedCondition.java
│       CachedQueryBuilder.java
│       Condition.java
│       FieldProcessor.java
│       Query.java
│       Queryable.java
│       QueryBuilder.java
│       QueryCondition.java
│
├───utils              # Helper utilities
│       CaseConvertor.java
│       TypeMapper.java
│
└───validation         # Validation of entities/annotations
        EntityValidator.java
```

---

### ⚙️ Configuration (`conf.json`)

This file contains **database connection info** and the **base package for your entity classes**. Daniyal ORM reads this at startup to know **where to connect** and **which classes to manage**.

> **Placement:** Put this `conf.json` in the **root folder** of your project — the same location from which you will run **your application that uses Daniyal ORM**.

```json
{
  "jdbc-driver": "com.mysql.cj.jdbc.Driver",
  "connection-url": "jdbc:mysql://localhost:3306/tmschooldb",
  "username": "tmschool",
  "password": "tmschool",
  "base-package": "com.daniyal.test.ormcore"
}
```

> This file contains the database connection details and the base package for generated or scanned entities.

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

    // No-arg constructor is required for ORM
    public Course() {}

    // Constructor with full fields
    public Course(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Constructor for insert (without ID)
    public Course(String name) {
        this.name = name;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
```

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
            System.out.println("Saved course with code: " + toInsert.getId());

            // Update Example
            Course toUpdate = new Course(toInsert.getId(), "JavaScript");
            dm.update(toUpdate);
            System.out.println("Updated course with code: " + toUpdate.getId());

            // Delete Example
            Course toDelete = new Course(10);  // constructor with ID only
            dm.delete(toDelete);
            System.out.println("Deleted course with code: " + toDelete.getId());

            // Query Example - fetch all courses
            List<Course> courseList = dm.query(Course.class).list();
				System.out.println("ID  |  Name");
            for (Course c : courseList) {
                System.out.println(c.getId() + "   " + c.getName());
            }

            dm.end();
        } catch (ORMException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
```

> This example shows how to perform **all CRUD operations** with Daniyal ORM in a clear and practical way.

---

## 💻 Command Line Interface (CLI) – Entity Generator & JAR Builder

Daniyal ORM includes a **CLI tool** that can:

* Generate annotated Java entity classes from your database schema
* Automatically **compile** the generated `.java` files
* **Package compiled classes into a JAR** (`dist/pojo.jar`) ready to use in your project

This removes the need for manual `javac` and `jar` commands, giving you a smooth, automated workflow.

---

### Command Syntax

```bash
java -cp daniyal-orm.jar com.daniyal.ormcore.generator.EntityGenerator \
--package=com.example.entities \
--output=src/main/java \
--tables=student,course \
--views=student_summary,course_stats \
--config=path/to/conf.json
```

### Arguments

* `--package=` (**required**) – Java package for generated entities
* `--output=` (**required**) – Directory where entity files will be created
* `--tables=` (optional) – Comma-separated list of table names, or `*` for all tables
* `--views=` (optional) – Comma-separated list of view names to generate, or `*` for all views
* `--config=` (optional) – Path to `conf.json` (defaults to current directory)

**Phase 13 Additions:**

* After generation, the entities are **compiled automatically**
* Compiled `.class` files are **packaged into a JAR** (`dist/pojo.jar`)
* Temporary compilation files are **cleaned up**

---

### Examples

**Generate and package entities for specific tables (`student` and `course`):**

```bash
java -cp daniyal-orm.jar com.daniyal.ormcore.generator.EntityGenerator \
--package=com.example.entities --output=. --tables=student,course
```

**Generate all tables with a custom config and package into a JAR:**

```bash
java -cp daniyal-orm.jar com.daniyal.ormcore.generator.EntityGenerator \
--package=com.example.entities --output=src/main/java --config=../myproject/conf.json
```
**Generate tables and views (`student`, `course` + `student_summary`, `course_stats`):**

```bash
java -cp daniyal-orm.jar com.daniyal.ormcore.generator.EntityGenerator \
--package=com.example.entities --output=. --tables=student,course --views=student_summary,course_stats
```

> The resulting JAR (`dist/pojo.jar`) can be added directly to your **project’s classpath**.

---

### What Happens Internally

1. **Load DB config** from `conf.json`
2. **Scan schema metadata** from the database
3. **Generate Java entity classes** with proper annotations (`@Table`, `@Column`, etc.)
4. **Compile `.java` files** programmatically using the Java Compiler API
5. **Package compiled `.class` files into a JAR** (`dist/pojo.jar`)
6. **Clean up** temporary compilation files
7. Optionally **generate POJOs for views** marked with `@View` when `--views` is specified.
8. **Preload cache for `@Cacheable` entities**  
   - During metadata scanning, entities marked with `@Cacheable` are fetched completely from the DB.  
   - Their rows are stored in `entityCacheMap`.  
   - Queries on these entities are resolved from the in-memory cache instead of hitting the DB.

> You now get fully compiled, packaged entities with **zero manual effort**.

---

### Notes

* Always specify `--package` and `--output`.
* By default, the CLI creates a `dist/` folder for the JAR.
* Temporary compilation files are **removed automatically** after packaging.
* The generated JAR can be **added directly to your classpath** for usage in your projects.

---



## 📌 Notes

* Every mapped field needs `@Column`.
* `@PrimaryKey`, `@AutoIncrement`, and `@ForeignKey` must be used with `@Column`.
* Validation runs at startup and before saving data.
* Reflection calls are cached internally for speed.
* Currently MySQL-only; other DBs can be added in the future.
* View entities are **read-only**; insert, update, delete operations are not supported.
* Use `@View` annotation to clearly distinguish view POJOs from table entities.
* Currently, cached entities only support `.list()` retrieval.  
* Advanced filtering (`where()`, `eq()`, etc.) on cached lists will be supported in future phases.  

---

## 🔮 Roadmap

* Full `SELECT` support with advanced queries
* Better validation and error messages
* Connection pooling & transaction handling
* Logging, unit tests, and CI/CD pipeline

---

## 🤝 Contributing

Feel free to fork, open a PR, or report issues. Bug reports and feature requests are welcome!

---