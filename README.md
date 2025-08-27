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


---

## 📂 Project Layout

```
com.daniyal.ormcore
├───annotations       # @Table, @Column, @PrimaryKey, @AutoIncrement, @ForeignKey
├───config            # ConfigLoader.java
├───connection        # ConnectionManager.java
├───exceptions        # ORMException.java
├───generator         # CLI EntityGenerator
├───manager           # DataManager, EntityScanner, DatabaseMetaDataLoader
├───pojo              # Metadata classes
├───query             # Query, QueryBuilder, FieldProcessor
├───utils             # CaseConvertor, TypeMapper
└───validation        # EntityValidator.java
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

## 💻 Command Line Interface (CLI) Entity Generator

Generate Java entity classes from your database schema using the **CLI tool** included in the ORM JAR.

### Command Syntax

```bash
java -cp daniyal-orm.jar com.daniyal.ormcore.generator.EntityGenerator \
--package=com.example.entities \
--output=src/main/java \
--tables=student,course \
--config=path/to/conf.json
```

### Arguments

* `--package=` (**required**) – Java package for generated entities
* `--output=` (**required**) – Directory to create entity files
* `--tables=` (optional) – Comma-separated list of table names, or `*` for all tables
* `--config=` (optional) – Path to `conf.json` (defaults to current directory)

### Examples

Generate `student` and `course` entities:

```bash
java -cp daniyal-orm.jar com.daniyal.ormcore.generator.EntityGenerator \
--package=com.example.entities --output=. --tables=student,course
```

Generate all tables with a custom config:

```bash
java -cp daniyal-orm.jar com.daniyal.ormcore.generator.EntityGenerator \
--package=com.example.entities --output=src/main/java --config=../myproject/conf.json
```

### How It Works

* Loads DB config from `conf.json`
* Connects to the database
* Reads schema metadata
* Generates annotated Java entity classes

### Notes

* Always specify `--package` and `--output`
* Paths can be relative or absolute
* Run without arguments for help

---

## 📌 Notes

* Every mapped field needs `@Column`.
* `@PrimaryKey`, `@AutoIncrement`, and `@ForeignKey` must be used with `@Column`.
* Validation runs at startup and before saving data.
* Reflection calls are cached internally for speed.
* Currently MySQL-only; other DBs can be added in the future.

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