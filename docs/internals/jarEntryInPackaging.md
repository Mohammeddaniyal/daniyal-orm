# Understanding JarEntry and Writing Class Files into a JAR

## What is a JAR?

- A JAR (Java ARchive) file is essentially a ZIP file that packages many `.class` files and resources.
- It allows Java applications to bundle all necessary classes into one file, which can be executed or added to classpaths.

---

## What is a JarEntry?

- A **`JarEntry`** represents a single file or directory inside the JAR archive.
- It contains metadata about the entry like its name (path inside JAR), size, and modification time.
- When creating a JAR programmatically with `JarOutputStream`, you create one `JarEntry` for each file you want to add.

---

## Why do we create a JarEntry for every class file?

- Each compiled class file (`.class`) needs a corresponding JAR entry so that:
  - The JAR has a **map of all files it contains**.
  - Java classloaders can locate classes by their **package and class name** inside the JAR.
- The entry name must reflect the package path, for example:  
  `com/daniyal/entities/MyEntity.class`

---

## How do we create the JarEntry name from the class file?

- When compiling classes, they’re stored in nested directories matching package names (e.g., `bin/classes/com/daniyal/entities/...`).
- We need the **relative path inside the root classes folder**, not the full absolute path.
  
- This is done by:
  - Getting the absolute path of the `.class` file.
  - Removing the portion corresponding to the root compiled classes directory (using `substring`).
  - Replacing Windows file separators (`\`) with `/` because JAR specifications require forward slashes.
  
Example:

```
Absolute file path: C:\project\bin\classes\com\daniyal\entities\MyEntity.class
Root classes path: C:\project\bin\classes\
Relative path: com/daniyal/entities/MyEntity.class  <-- to be used as JarEntry name
```

---

## Writing files into the JAR

The general process to write a `.class` to the JAR:

1. **Create a `JarEntry`** using the relative path:  
   ```
   JarEntry jarEntry = new JarEntry(entryName);
   ```

2. **Set metadata** like modification time:  
   ```
   jarEntry.setTime(source.lastModified());
   ```

3. **Put the entry into the `JarOutputStream`**, signalling the start of writing this file:  
   ```
   jarOutputStream.putNextEntry(jarEntry);
   ```

4. **Write the file content bytes** to the stream, typically by reading the `.class` file as a buffered stream and writing chunks of bytes.

5. **Close the entry** to indicate the end of this file:  
   ```
   jarOutputStream.closeEntry();
   ```

6. Repeat for all `.class` files.

---

## Why is this important?

- Maintaining the **correct relative path** and forward slash separators ensures the JAR is compatible across different operating systems.
- Each `JarEntry` clearly defines a distinct file inside the archive.
- Proper opening and closing of entries make the JAR readable by standard Java tools and runtimes.

---

## Summary

- **JarEntry**: A record inside the JAR corresponding to one file (class/resource).
- **Entry name**: The relative package path inside the JAR (not absolute file system path).
- **Writing content**: Read the class file bytes and write to the open JarEntry stream.
- **Close entry**: Marks end of each JAR file section.
- This process is repeated for each class to build a complete JAR.

---