package com.daniyal.ormcore.generator;
import com.daniyal.ormcore.pojo.ForeignKeyMetaData;
import com.daniyal.ormcore.connection.*;
import com.daniyal.ormcore.utils.*;
import com.daniyal.ormcore.config.*;
import com.daniyal.ormcore.exceptions.*;
import java.sql.*;
import java.io.*;
import java.util.*;
import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.util.jar.*;
class EntityGenerator
{
public static void main(String []args)
{
try
{
/* 
java -cp daniyal-orm.jar;. com.daniyal.ormcore.generator.EntityGenerator --package=com.anis.customer.entities 
(optional)--output=src/main/java (optional)--table=student,course 
(optional only if user is calling from the conf.json dir)--config=path/to/conf.json */
	
	if(args.length<2)
	{
		System.out.println("Usage: java -cp daniyal-orm.jar;. com.daniyal.ormcore.generator.EntityGenerator " +
                   "--package=<your.package.name> [--output=<output/directory>] " +
                   "[--tables=table1,table2,...] [--config=path/to/conf.json]");
		System.exit(1);
	}
	String packageName=null;
	String tablesArg=null;
	String outputDir=null;
	String config=null;
	for(String arg:args)
	{
		if(arg.startsWith("--package="))
		{
			packageName=arg.substring("--package=".length());
		}else if(arg.startsWith("--output="))
		{
			outputDir=arg.substring("--output=".length());
		}else if(arg.startsWith("--tables="))
		{
			tablesArg=arg.substring("--tables=".length());
		}else if(arg.startsWith("--config="))
		{
			config=arg.substring("--config=".length());
		}
	}
	if(packageName==null)
	{
		System.out.println("--package= required as command line argument");
		System.exit(1);
	}
	if(outputDir==null)
	{
		System.out.println("--output= required as command line argument");
		System.exit(1);
	}
	
	String packagePath=packageName.replace(".",File.separator);
	
	
	ConfigLoader configLoader=null;
	try
	{	
	if(config==null) config="conf.json";
	configLoader=new ConfigLoader(config);
	}catch(ORMException exception)
	{
		System.out.println(exception.getMessage());
		System.exit(1);
	}
	
	File targetDir=new File(outputDir,packagePath);
	if(!targetDir.exists())
	{
		if(!targetDir.mkdirs())
		{
			System.out.println("Failed to create output directory: "+targetDir.getAbsolutePath());
			System.exit(1);
		}
	}
	boolean allTables=false;
	Set<String> tableSet=null;
	if(tablesArg==null)
	{
		allTables=true;
	}else if(tablesArg.equals("*"))
	{
		allTables=true;
	}
	else
	{
		try
		{
		tableSet=new HashSet<>(Arrays.asList(tablesArg.split(",")));
		}catch(Exception e)
		{
			System.out.println("Invalid '--tables=' argument");
			System.exit(1);
		}
	}
Connection connection=ConnectionManager.getConnection(configLoader);

DatabaseMetaData meta=connection.getMetaData();
ResultSet tables=meta.getTables(connection.getCatalog(),null,"%",new String[]{"TABLE"});

List<File> javaFiles=new ArrayList<>();
File file;
RandomAccessFile randomAccessFile;
Set<String> primaryKeyColumns=new HashSet<>();
List<String> importLines=new ArrayList<>();
Map<String,ForeignKeyMetaData> foreignKeyMetaDataMap=new HashMap<>();
ForeignKeyMetaData foreignKeyMetaData;
StringBuilder classBuilder=new StringBuilder();
StringBuilder constructorBuilder=new StringBuilder();
StringBuilder setterGetterBuilder=new StringBuilder();
while(tables.next())
{
String tableName=tables.getString("TABLE_NAME");

if(!allTables)
{
	if(!tableSet.contains(tableName)) continue;
}

String className=tableName.substring(0,1).toUpperCase()+tableName.substring(1);

classBuilder.append("@Table(name=\"" + tableName + "\")\r\n");
classBuilder.append("\r\npublic class ");
classBuilder.append(className+"\r\n{\r\n");

constructorBuilder.append("public "+className+"()\r\n{");
file=new File(targetDir,className+".java");
if(file.exists()) file.delete();
randomAccessFile=new RandomAccessFile(file,"rw");




ResultSet k=meta.getPrimaryKeys(connection.getCatalog(),null,tableName);
while(k.next())
{
primaryKeyColumns.add(k.getString("COLUMN_NAME"));
}
k.close();

k=meta.getImportedKeys(connection.getCatalog(),null,tableName);
while(k.next())
{
String fkCol= k.getString("FKCOLUMN_NAME");
String pkTbl=k.getString("PKTABLE_NAME");
String pkCol=k.getString("PKCOLUMN_NAME");
foreignKeyMetaData=new ForeignKeyMetaData();
foreignKeyMetaData.setFKColumn(fkCol);
foreignKeyMetaData.setPKTable(pkTbl);
foreignKeyMetaData.setPKColumn(pkCol);
foreignKeyMetaDataMap.put(fkCol,foreignKeyMetaData);
}

k.close();



ResultSet columns=meta.getColumns(connection.getCatalog(),null,tableName,"%");



while(columns.next())
{
String columnName=columns.getString("COLUMN_NAME");
String type=columns.getString("TYPE_NAME");
String size=columns.getString("COLUMN_SIZE");
String isNull=columns.getString("IS_NULLABLE");
String autoIncrement=columns.getString("IS_AUTOINCREMENT");
boolean isPrimaryKey=primaryKeyColumns.contains(columnName);
classBuilder.append("@Column(name=\""+columnName+"\")\r\n");

if(isPrimaryKey)
{
	classBuilder.append("@PrimaryKey\r\n");
}

if(autoIncrement.equalsIgnoreCase("YES"))
{
	classBuilder.append("@AutoIncrement\r\n");
}

if(foreignKeyMetaDataMap.containsKey(columnName))
{
foreignKeyMetaData=foreignKeyMetaDataMap.get(columnName);
String fkCol= foreignKeyMetaData.getFKColumn();
String pkTbl=foreignKeyMetaData.getPKTable();
String pkCol=foreignKeyMetaData.getPKColumn();
classBuilder.append("@ForeignKey(parent=\"" + pkTbl + "\",column=\"" + pkCol + "\")\r\n");
}

// check which type

String fieldType=TypeMapper.getFieldType(type,Integer.parseInt(size));
String fieldName=CaseConvertor.toCamelCase(columnName);
if(fieldType.equalsIgnoreCase("Date")) fieldType="java.util.Date";
else if(fieldType.equalsIgnoreCase("BigDecimal")) fieldType="java.math.BigDecimal";

classBuilder.append("public "+fieldType+" "+fieldName+";"+"\r\n");
String capitalizeFieldName=fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);

// adding this field into constructor
constructorBuilder.append("\r\nthis."+fieldName+"="+TypeMapper.getDefaultValue(fieldType)+";");

// generating setter
setterGetterBuilder.append("public void set"+capitalizeFieldName+"("+fieldType+" "+fieldName+")\r\n");
setterGetterBuilder.append("{\r\n");
setterGetterBuilder.append("this."+fieldName+"="+fieldName+";\r\n");
setterGetterBuilder.append("}\r\n");

// generating getter
setterGetterBuilder.append("public "+fieldType+" get"+capitalizeFieldName+"()\r\n");
setterGetterBuilder.append("{\r\n");
setterGetterBuilder.append("return this."+fieldName+";\r\n");
setterGetterBuilder.append("}\r\n");


}
columns.close();
constructorBuilder.append("\r\n}\r\n");
randomAccessFile.writeBytes("package "+packageName+";\r\n");
randomAccessFile.writeBytes("import com.daniyal.ormcore.annotations.*;\r\n");
randomAccessFile.writeBytes(classBuilder.toString());
randomAccessFile.writeBytes(constructorBuilder.toString());
randomAccessFile.writeBytes(setterGetterBuilder.toString());
randomAccessFile.writeBytes("}");
randomAccessFile.close();
primaryKeyColumns.clear();
foreignKeyMetaDataMap.clear();
classBuilder.setLength(0);
constructorBuilder.setLength(0);
setterGetterBuilder.setLength(0);
javaFiles.add(file);
/* ResultSet idx=meta.getIndexInfo(connection.getCatalog(), null, tableName, false, false);
while(idx.next()) 
{
String idxName = idx.getString("INDEX_NAME");
String colName = idx.getString("COLUMN_NAME");
boolean nonUnique = idx.getBoolean("NON_UNIQUE");
//System.out.println(" IDX: " + idxName + " on " + colName + " | unique=" + !nonUnique);
}
idx.close();
 */

} // loops on column ends here
tables.close();
connection.close();

// compile files
	compileJavaFiles(javaFiles);
// create jar
	createJar();
}catch(IOException | SQLException ex)
{
System.out.println(ex.getMessage());
}
}

private static  void compileJavaFiles(List<File> javaFiles) throws IOException 
{
	deleteDirectory(new File("bin"));
	// getting the system Java compiler 
	// which requires (JDK not JRE)
	JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();
	if(compiler==null)
	{
		System.out.println("Java compiler not available. Run with JDK.");
	}
	List<String> options=Arrays.asList("-d","bin/classes");
	// Get a standard file manager to handle JavaFileObjects
	try(StandardJavaFileManager fileManager=compiler.getStandardFileManager(null,null,null))
	{
		// converting java.io.File list to compilation units
		Iterable<? extends javax.tools.JavaFileObject> compilationUnits=fileManager.getJavaFileObjectsFromFiles(javaFiles);
		// creating the compilation task with default options(null params, means we won't default behaviour )
		JavaCompiler.CompilationTask task=compiler.getTask(null,fileManager,null,options,null,compilationUnits);
		
		// running the compilation tasl
		boolean success=task.call();
		if(!success)
		{
			System.out.println("Compilation failed.");
			System.exit(1);
		}
	}
}

private static void createJar() throws IOException
{
	File classesDir=new File("bin/classes");
	File jarFile=new File("dist/pojo.jar");
	if(jarFile.exists())
	{
		if(!jarFile.delete())
		{
			System.out.println("Failed to delete exisitng jar file: "+jarFile.getAbsolutePath());
			System.exit(1);
		}
	}
	File parentDir=jarFile.getParentFile();
	if(parentDir!=null && !parentDir.exists())
	{
		if(!parentDir.mkdirs())
		{
			System.out.println("Unable to create directory : "+parentDir.getAbsolutePath());
			System.exit(1);
		}
	}
	// create output stream for the jar file
	try(JarOutputStream jarOutputStream=new JarOutputStream(new FileOutputStream(jarFile)))
	{
		// recursively adding all .class files which are in classesDir to the jar
		addFilesToJar(jarOutputStream,classesDir,classesDir.getAbsolutePath().length()+1);
	}
	deleteDirectory(new File("bin"));
}

private static void addFilesToJar(JarOutputStream jarOutputStream,File source,int prefixLength) throws IOException
{
	if(source.isDirectory())
	{
		for(File file:source.listFiles())
		{
			addFilesToJar(jarOutputStream,file,prefixLength);
		}
		return;
	}

	String entryName=source.getAbsolutePath().substring(prefixLength).replace("\\","/");
	System.out.println(entryName);
	JarEntry jarEntry=new JarEntry(entryName);
	
	jarEntry.setTime(source.lastModified());
	jarOutputStream.putNextEntry(jarEntry);
	
	// writing the file content into the jar entryName
	try(BufferedInputStream bufferedInputStream=new BufferedInputStream(new FileInputStream(source)))
	{
		byte buffer[]=new byte[1024];
		int bytesRead;
		while((bytesRead=bufferedInputStream.read(buffer))!=-1)
		{
			jarOutputStream.write(buffer,0,bytesRead);
		}
	}
	jarOutputStream.closeEntry();
	
}
private static void deleteDirectory(File directory)
{
	if(!directory.exists()) return;
	File[] files=directory.listFiles();
	if(files!=null)
	{
		for(File file:files)
		{
			if(file.isDirectory())
			{
				deleteDirectory(file);
			}else{
				file.delete();
			}
		}
	}
	directory.delete();		
}
}