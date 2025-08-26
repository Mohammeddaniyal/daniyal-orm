# Daniyal-ORM Phase 7: Update & Delete Functionality

## Overview

This phase focused on building out robust data modification operations in Daniyal-ORM, handling updates and deletes both at the query builder and ORM manager levels.

***

## What Was Done

- **Update Query Support:**
    - Added `updateQueryBuilder()` to dynamically generate parameterized `UPDATE` SQL using entity metadata and provided new values.
    - Implemented `update()` in `DataManager` to apply updates to persisted entities in the database.

- **Delete Query Support:**
    - Added `deleteQueryBuilder()` for generating `DELETE FROM ... WHERE PK = ?` queries.
    - Implemented `delete()` in `DataManager` to safely remove entities by primary key.

- **Entity Integrity:**  
  Both features validate PK presence, use prepared statements for safety, and update entity state after execution.

***

## Next Focus

- Begin development on flexible `query()` APIs for:
    - `query()` → fetch all entities from a table.
    - `query().where()` and additional filtering (e.g., `.gt(35)`) for custom retrieval.

- Start designing the standalone CLI entity class generator.
