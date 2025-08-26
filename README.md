# Daniyal ORM

![Java](https://img.shields.io/badge/Java-8-blue?logo=openjdk&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green)

> A lightweight Java ORM for MySQL with annotation-based mapping, fluent queries, and a CLI for entity generation.


Hey! I built **Daniyal ORM** to make working with MySQL in Java a lot less painful.
Instead of writing endless SQL and manually mapping results, you just annotate your classes, and the framework handles the rest.

It scans your database, validates your entities, and gives you basic CRUD operations using reflection.

Think of it as a **simplified Hibernate**, without the heavy learning curve.

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

## ⚙️ Configuration (`conf.json`)

```json
{
  "jdbc-driver": "com.mysql.cj.jdbc.Driver",
  "connection-url": "jdbc:mysql://localhost:3306/tmschooldb",
  "username": "tmschool",
  "password": "tmschool",
  "base-package": "com.daniyal.test.ormcore"
}
```

---

## 🏗 Defining an Entity

```java
@Table(name = "courses")
public class Course {
    @PrimaryKey @AutoIncrement @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    public Course() {}  // No-arg constructor

    public Course(String name) { this.name = name; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
```

---

## 🚀 Example Usage

```java
DataManager dm = DataManager.getDataManager();
dm.begin();

try {
    // Insert
    Course course = new Course("Java");
    dm.save(course);

    // Update
    course.setName("JavaScript");
    dm.update(course);

    // Delete
    dm.delete(course);

    // Query all
    List<Course> courses = dm.query(Course.class).list();
    courses.forEach(c -> System.out.println(c.getId() + " | " + c.getName()));

} catch (ORMException e) {
    System.out.println("Error: " + e.getMessage());
} finally {
    dm.end();
}
```

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