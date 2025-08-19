# 📌 New code in `eg6psp`

```java
ResultSet k=meta.getPrimaryKeys(connection.getCatalog(),null,tableName);
while(k.next())
{
    System.out.println(" PK: " + k.getString("COLUMN_NAME"));
}
k.close();

k=meta.getImportedKeys(connection.getCatalog(),null,tableName);
while(k.next())
{
    String fkCol= k.getString("FKCOLUMN_NAME");
    String pkTbl=k.getString("PKTABLE_NAME");
    String pkCol=k.getString("PKCOLUMN_NAME");
    System.out.println(" FK: " + fkCol + " -> " + pkTbl + "(" + pkCol + ")");
}
k.close();

ResultSet idx=meta.getIndexInfo(connection.getCatalog(), null, tableName, false, false);
while(idx.next()) 
{
    String idxName = idx.getString("INDEX_NAME");
    String colName = idx.getString("COLUMN_NAME");
    boolean nonUnique = idx.getBoolean("NON_UNIQUE");
    System.out.println(" IDX: " + idxName + " on " + colName + " | unique=" + !nonUnique);
}
idx.close();
```

---

## 🔎 Step 1. Primary Keys

```java
ResultSet k = meta.getPrimaryKeys(connection.getCatalog(), null, tableName);
while(k.next()) {
    System.out.println(" PK: " + k.getString("COLUMN_NAME"));
}
```

* **`getPrimaryKeys`** → asks JDBC: *“Which columns are the primary key of this table?”*
* Each row in the result = one primary key column.
* `COLUMN_NAME` → actual PK column name (`id`, `user_id`, etc.).
* Output example:

  ```
  === customers ===
   PK: id
  ```

---

## 🔎 Step 2. Foreign Keys (Imported Keys)

```java
k = meta.getImportedKeys(connection.getCatalog(), null, tableName);
while(k.next()) {
    String fkCol = k.getString("FKCOLUMN_NAME");
    String pkTbl = k.getString("PKTABLE_NAME");
    String pkCol = k.getString("PKCOLUMN_NAME");
    System.out.println(" FK: " + fkCol + " -> " + pkTbl + "(" + pkCol + ")");
}
```

* **`getImportedKeys`** → gives all foreign keys in this table (columns that reference primary keys in another table).
* Fields:

  * `FKCOLUMN_NAME` → foreign key column in *this* table.
  * `PKTABLE_NAME` → the parent table it references.
  * `PKCOLUMN_NAME` → the referenced primary key column.
* Output example:

  ```
  === orders ===
   PK: order_id
   FK: customer_id -> customers(id)
  ```

---

## 🔎 Step 3. Indexes

```java
ResultSet idx = meta.getIndexInfo(connection.getCatalog(), null, tableName, false, false);
while(idx.next()) {
    String idxName = idx.getString("INDEX_NAME");
    String colName = idx.getString("COLUMN_NAME");
    boolean nonUnique = idx.getBoolean("NON_UNIQUE");
    System.out.println(" IDX: " + idxName + " on " + colName + " | unique=" + !nonUnique);
}
```

* **`getIndexInfo`** → returns all indexes on the table.
* Fields:

  * `INDEX_NAME` → the index’s name (`PRIMARY`, `idx_customer_email`, etc.).
  * `COLUMN_NAME` → the column indexed.
  * `NON_UNIQUE` → whether duplicates are allowed (`false` means **unique index**).
* Output example:

  ```
  === customers ===
   PK: id
   IDX: PRIMARY on id | unique=true
   IDX: idx_email on email | unique=true
  ```

---

# ✅ In short (new code meaning)

For each table:

1. **`getPrimaryKeys`** → prints its **primary key columns**.
2. **`getImportedKeys`** → prints its **foreign key relationships**.
3. **`getIndexInfo`** → prints its **indexes** (with uniqueness).

---

👉 Together, this gives you not just the structure of tables, but the **relational design**:

* PKs = how the table identifies rows
* FKs = how tables link together
* Indexes = how queries are optimized

This is like programmatically generating a **database ERD (Entity Relationship Diagram)** from JDBC metadata.

---

