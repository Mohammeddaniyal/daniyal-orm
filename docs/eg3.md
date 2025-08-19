
### Key part of your code

```java
DatabaseMetaData meta = connection.getMetaData();
ResultSet rs = meta.getTables(connection.getCatalog(), null, "%", new String[]{"TABLE"});

while (rs.next()) {
    String tableName = rs.getString("TABLE_NAME");
    System.out.println("Table : " + tableName);
}
```

---

## 1. What `getTables(...)` is doing

We’ve already seen this in `eg2psp`, but quick recap:

* **Arguments passed**:

  * `connection.getCatalog()` → current database name in MySQL.
  * `null` → schema pattern (ignored in MySQL).
  * `"%"` → all table names.
  * `new String[]{"TABLE"}` → only user-defined **base tables** (exclude views/system tables).

So the `ResultSet` (`rs`) will contain **one row per table** in your current database.

---

## 2. Looping over the result set

```java
while (rs.next()) {
    String tableName = rs.getString("TABLE_NAME");
    System.out.println("Table : " + tableName);
}
```

* `rs.next()` → moves the cursor to the next row in the metadata result. Each row = one table.
* `rs.getString("TABLE_NAME")` → fetches the value from the **`TABLE_NAME` column** (which is guaranteed by the JDBC spec to be present).
* This gives you the **actual name of each table** in your database.
* Then you print it.

---

## 3. Example output

Suppose your database `shopdb` has three tables:

* `customers`
* `orders`
* `products`

Your output will be:

```
Table : customers
Table : orders
Table : products
```

---

## 4. Important notes

* You don’t need `ResultSetMetaData` here because you already know which column you want (`TABLE_NAME`).

* If you wanted more detail, you could also pull:

  * `rs.getString("TABLE_CAT")` → the catalog/database name
  * `rs.getString("TABLE_TYPE")` → `"TABLE"` or `"VIEW"`
  * `rs.getString("REMARKS")` → comment/description

* Since you filtered with `"TABLE"`, you won’t see views. If you wanted **both tables and views**, you could pass:

  ```java
  new String[]{"TABLE", "VIEW"}
  ```

---

✅ **Summary of this example (`eg3psp`)**

* You ask JDBC: “List all tables in my current database.”
* JDBC returns a `ResultSet` with one row per table.
* You loop through rows, grab the `TABLE_NAME` column, and print it.
* End result = a list of table names in your database.

---


