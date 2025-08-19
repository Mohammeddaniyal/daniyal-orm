---

# 📌 Functions We Used

| Function | What it Does | Why We Use It |
|----------|--------------|---------------|
| **`connection.getMetaData()`** | Returns a `DatabaseMetaData` object for the current connection. | This is the main entry point to query metadata about the database, tables, columns, keys, indexes, etc. |
| **`meta.getUserName()`** | Gets the name of the user connected to the database. | To confirm which database account is being used for the connection. |
| **`meta.getTables(catalog, schemaPattern, tableNamePattern, types)`** | Returns all tables (and optionally views, system tables, etc.) that match the given filters. Each row = one table. | To list tables in the database. <br> Example: `new String[]{"TABLE"}` → only user-defined tables. |
| **`meta.getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern)`** | Returns all columns of a given table, with details (name, type, size, nullability, auto-increment, etc.). Each row = one column. | To get table structure programmatically (like `DESCRIBE table`). |
| **`meta.getPrimaryKeys(catalog, schema, table)`** | Returns all primary key columns of a given table. Each row = one PK column. | To identify which columns uniquely identify rows. |
| **`meta.getImportedKeys(catalog, schema, table)`** | Returns all foreign key columns of a given table, along with their parent table/column. Each row = one FK relationship. | To understand relationships (joins) between tables. |
| **`meta.getIndexInfo(catalog, schema, table, unique, approximate)`** | Returns all indexes on a given table. Each row = one indexed column. | To see how the DB optimizes lookups and enforces uniqueness. Includes primary key indexes, unique indexes, and indexes on foreign keys. |
| **`rs.getString("COLUMN_NAME")`** (or other column names like `"TABLE_NAME"`) | Extracts the actual metadata value from a row in the `ResultSet`. | This is how we read the values returned by `DatabaseMetaData` (e.g., table names, column types, etc.). |
| **`ResultSetMetaData` (`rs.getMetaData()`)** | Returns metadata *about the ResultSet itself* (like number of columns, their names, types). | To inspect which fields the `DatabaseMetaData` functions provide (e.g., TABLE_NAME, TYPE_NAME, COLUMN_SIZE, etc.). |

---

# 📌 Flow of What We Built

1. **eg1psp** → connect + get username (basic metadata).  
2. **eg2psp** → used `getTables` and `ResultSetMetaData` to see *what info about tables is available*.  
3. **eg3psp** → used `getTables` to actually list table names.  
4. **eg4psp** → used `getColumns` for a single table, and `ResultSetMetaData` to see what info about columns is available.  
5. **eg5psp** → listed **all tables with all columns**, including type, size, nullability, auto-increment.  
6. **eg6psp** → added **primary keys, foreign keys, and indexes** to describe relationships and optimizations.  

---

# ✅ In short
All these functions from `DatabaseMetaData` let you **query the database schema itself** — not the data.  
- `getTables` → what tables exist  
- `getColumns` → what columns they have  
- `getPrimaryKeys` → how rows are uniquely identified  
- `getImportedKeys` → how tables connect  
- `getIndexInfo` → how lookups are optimized  

---

