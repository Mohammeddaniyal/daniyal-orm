# 📌 New code in `eg4psp`

```java
String anyTable=null;
if(rs.next())
{
    anyTable=rs.getString("TABLE_NAME");
}
if(anyTable!=null) 
{
    ResultSet t=meta.getColumns(connection.getCatalog(),null,anyTable,"%");
    ResultSetMetaData md=t.getMetaData();
    int n=md.getColumnCount();
    System.out.println("Columns returned by getColumns() for table " + anyTable + ":");
    for(int i=1;i<=n;i++) {
        System.out.println(i + " -> " + md.getColumnName(i));
    }
}
```

---

## 1. Picking any one table
```java
if(rs.next()) {
    anyTable=rs.getString("TABLE_NAME");
}
```
- `rs` here is the result of `getTables(...)`.  
- `rs.next()` moves to the **first row** in the list of tables.  
- `getString("TABLE_NAME")` fetches that table’s name (let’s say `"customers"`).  
- So now `anyTable` holds the name of the first table in the DB.  

---

## 2. Getting column info
```java
ResultSet t = meta.getColumns(connection.getCatalog(), null, anyTable, "%");
```

### 🔹 `getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern)`
This is just like `getTables`, but for **columns** inside a table.  

- **catalog** → which database (using `connection.getCatalog()` again).  
- **schemaPattern** → ignored in MySQL (so `null`).  
- **tableNamePattern** → the specific table you want (`anyTable`).  
- **columnNamePattern** → `"%"` means all columns (could also filter, e.g. `"id"` or `"name%"`).  

👉 This will return a `ResultSet` where:  
- **Each row** = one column in the table.  
- **Each column** (in the ResultSet) = metadata about that column (name, type, size, nullable, etc.).

---

## 3. Inspecting the metadata
```java
ResultSetMetaData md = t.getMetaData();
int n = md.getColumnCount();
for(int i=1;i<=n;i++) {
    System.out.println(i + " -> " + md.getColumnName(i));
}
```

- This doesn’t print the actual column names of your table yet.  
- Instead, it prints the **metadata column names** of the `ResultSet` returned by `getColumns`.  
- In other words: it tells you what pieces of information JDBC provides **about each column**.  

---

## 4. Typical output
When you run this, you’ll likely see something like:

```
Columns returned by getColumns() for table customers:
1 -> TABLE_CAT
2 -> TABLE_SCHEM
3 -> TABLE_NAME
4 -> COLUMN_NAME
5 -> DATA_TYPE
6 -> TYPE_NAME
7 -> COLUMN_SIZE
8 -> BUFFER_LENGTH
9 -> DECIMAL_DIGITS
10 -> NUM_PREC_RADIX
11 -> NULLABLE
12 -> REMARKS
13 -> COLUMN_DEF
14 -> SQL_DATA_TYPE
15 -> SQL_DATETIME_SUB
16 -> CHAR_OCTET_LENGTH
17 -> ORDINAL_POSITION
18 -> IS_NULLABLE
...
```

---

## 5. Meaning of these columns
Each **row** in this ResultSet = one column of your table.  
Each **column** in the ResultSet = information about that table column.  

For example:
- `TABLE_NAME` → name of the table  
- `COLUMN_NAME` → name of the column (`id`, `name`, etc.)  
- `DATA_TYPE` → SQL type code (e.g., `4` for `INTEGER`, `12` for `VARCHAR`)  
- `TYPE_NAME` → actual SQL type name (`INT`, `VARCHAR`)  
- `COLUMN_SIZE` → max size (e.g., 50 for `VARCHAR(50)`)  
- `NULLABLE` → whether it can be NULL  
- `ORDINAL_POSITION` → position of the column in the table (1 = first column, 2 = second, etc.)  

---

✅ **In short (eg4psp new code)**:
- You grab the first table from your DB.  
- You ask JDBC: *“Give me metadata for every column in this table.”*  
- JDBC returns a ResultSet where each row = one column of the table, and each column of the ResultSet = a property of that column.  
- You printed the list of these metadata fields, so you know what’s available.  

---
