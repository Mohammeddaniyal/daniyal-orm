# Daniyal-ORM Phase 6: Handling Auto-Increment Generated Keys

## Overview

In this phase, we implemented the crucial feature of retrieving and setting auto-generated keys (such as auto-increment primary keys) after insert operations. This ensures that the Java entity objects reflect the primary key values assigned by the database upon insertion.

---

## What We Did

### 1. Modified Insert Query Building  
- Updated `buildInsertQuery` method to track the `FieldMetaData` that represents the auto-increment column.  
- During query building, if a field is auto-increment, it is excluded from the list of insert columns and parameters but stored for later use.

### 2. Prepared Statement Execution with Generated Keys  
- Executed the insert statement prepared with `Statement.RETURN_GENERATED_KEYS` flag to request generated keys automatically.

### 3. Retrieving Generated Keys and Setting Entity Fields  
- After successful execution, obtained the generated keys `ResultSet` using `preparedStatement.getGeneratedKeys()`.  
- Checked if there is at least one key in the result set.  
- Retrieved the generated key using `getObject()` with precise type detection based on the type of the auto-incremented entity field (int, long, double, etc.).  
- Set the generated key value back into the Java entity field via reflection after making the field accessible.

### 4. Robust Type Handling  
- Covered all common Java types for generated keys including primitive wrappers (`Integer`, `Long`, etc.), `BigDecimal`, `String`, and `java.util.Date` (converted from SQL timestamp).  
- Threw meaningful exceptions (`ORMException`) when reflection access fails, guiding users to ensure fields are accessible.

---

## Code Snippet Highlight

```java
PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
int x = 1;
for (Object param : params) {
    preparedStatement.setObject(x++, param);
}
int affectedRow = preparedStatement.executeUpdate();
if (affectedRow == 0) {
    throw new ORMException("Save failed");
}
ResultSet generatedKeysResultSet = preparedStatement.getGeneratedKeys();
if (generatedKeysResultSet.next()) {
    Field fieldWithAutoIncrement = fieldMetaData.getField();
    Class fieldType = fieldWithAutoIncrement.getType();
    try {
        // type-specific getObject and set on entity field
        // ...
    } catch (IllegalAccessException e) {
        throw new ORMException("Cannot write value of field " + fieldWithAutoIncrement.getName());
    }
}
```

---

## Why This Is Important

- Ensures entity primary key fields are synchronized post-insert with database-assigned auto-generated values.  
- Enables further operations like updates or deletes on newly created entities.  
- Keeps the ORM’s entity state coherent with the persistent data store.

---

## Next Steps

- Extend this approach to support multiple generated keys if needed (e.g., composite keys with generated parts).  
- Add more comprehensive error handling and verbose logging.  
- Finalize update and delete query builders using metadata enhancements made.

---

This phase solidifies Daniyal-ORM as a functional ORM capable of reliable persistence and primary key management.
