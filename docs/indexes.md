---

# 📌 What is an Index?

Think of an **index** in a book 📖:

* Instead of flipping through every page to find "Newton," you look at the index at the back → it tells you the exact page numbers.

In a database, an **index** works the same way:

* It’s a **data structure** (usually a B-tree) that makes lookups, joins, and filtering much faster.
* Instead of scanning the whole table, MySQL can **jump directly** to rows using the index.

---

# 📌 Types of Indexes You Saw

### 1. **Primary Key Index**

```text
IDX: PRIMARY on code | unique=true
```

* Every **primary key** automatically creates an index.
* Why? Because MySQL needs to **quickly find rows by PK** and enforce **uniqueness**.
* So in `course(code)`, since `code` is the primary key, you got:

  * Index name: `PRIMARY` (special name)
  * On column: `code`
  * Unique: `true` (no duplicates allowed)

---

### 2. **Unique Index on `title`**

```text
IDX: title on title | unique=true
```

* This comes from either:

  * You defined `title` as `UNIQUE`, or
  * The database enforces it because of a schema rule.
* It ensures no two courses can have the same title.
* MySQL builds a **unique index** to enforce that rule.

---

### 3. **Foreign Key Column Index**

```text
IDX: course_code on course_code | unique=false
```

* This is from your `student` table foreign key:

  ```sql
  FOREIGN KEY (course_code) REFERENCES course(code)
  ```
* MySQL **automatically creates a non-unique index** on every foreign key column.
* Why? Because when you join:

  ```sql
  SELECT * 
  FROM student s 
  JOIN course c ON s.course_code = c.code;
  ```

  MySQL must quickly find all students with a given `course_code`.
* **Non-unique** because many students can be in the same course.

  * Example:

    * `course_code = "CS101"` could appear in 200 rows in `student`.
    * So duplicates are allowed → **non-unique index**.

---

# ✅ In short

* **PK index** = unique index created automatically for the primary key.
* **Unique index** = enforces uniqueness on some column (`title`).
* **Foreign key index** = created automatically for efficient joins, but **non-unique** (since many rows may reference the same parent).

---

👉 So the answer to *"why this index for course\_code on course\_code?"*:
Because MySQL automatically creates an index on foreign key columns, so it can enforce referential integrity and speed up joins. And it’s **non-unique**, since many students can point to the same course.

---
