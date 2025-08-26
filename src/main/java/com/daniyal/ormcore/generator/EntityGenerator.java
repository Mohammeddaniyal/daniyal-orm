package com.daniyal.ormcore.generator;
import com.daniyal.ormcore.pojo.ForeignKeyMetaData;
import com.daniyal.ormcore.connection.*;
import com.daniyal.ormcore.connection.CaseConvertor;
import java.sql.*;
import java.io.*;
import java.util.*;
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
	
	if(args<1)
	{
		System.out.println("Usage [java -cp daniyal-orm.jar;. com.daniyal.ormcore.generator.EntityGenerator 
		--package=com.anis.customer.entities 
		(optional)--output=src/main/java (optional)--table=student,course 
		(optional)--config=path/to/conf.json]");
		System.exit(1);
	}
	String packageName=null;
	String tables=null;
	String outputDir=null;
	String config=null;
	for(String arg:args)
	{
		if(arg.startsWith("package--="))
		{
			packageName=arg.substring("package--=".length());
		}else if(arg.startsWith("output--="))
		{
			outputDir=arg.substring("output--=".length());
		}else if(arg.startsWith("--table="))
		{
			tables=arg.substring("tables--=".length());
		}else if(arg.startWith("--config="))
		{
			config=arg.substring("config--=".length());
		}
	}
	if(packageName==null)
	{
		System.out.println("--package= required as command line argument");
		System.exit(1);
	}
	if(output==null)
	{
		System.out.println("--output= required as command line argument");
		System.exit(1);
	}
	
	String packagePath=packageName.replace(".",File.separator);
	
	
	ConfigLoader configLoader=null;
	try
	{	
	configLoader=new ConfigLoader((config==null):"conf.json"?config);
	}catch(ORMException exception)
	{
		System.out.println(exception.getMessage());
		System.exit(1);
	}
	
	File targetDir=new File(output,packagePath);
	if(!targetDir.exists())
	{
		if(!targetDir.mkdir())
		{
			System.out.println("Failed to create output directory: "+targetDir.getAbsolutePath());
			System.exit(1);
		}
	}
	boolean allTables=false;
	Set<String> tableSet=null;
	if(tables==null)
	{
		allTables=true;
	}else if(tables.equals("*")
	{
		allTables=true;
	}
	else
	{
		try
		{
		tableSet=new HashMap<>(Array.asList(tables.split(",")));
		}catch(Exception e)
		{
			System.out.println("Invalid '--tables=' argument");
			System.exit(1);
		}
	}
	
Connection connection=ConnectionManager.getConnection(configLoader);

DatabaseMetaData meta=connection.getMetaData();
ResultSet tables=meta.getTables(connection.getCatalog(),null,"%",new String[]{"TABLE"});

File file;
RandomAccessFile randomAccessFile;
Set<String> primaryKeyColumns=new HashSet<>();
List<String> importLines=new ArrayList<>();
Map<String,ForeignKeyMetaData> foreignKeyMetaDataMap=new HashMap<>();
ForeignKeyMetaData foreignKeyMetaData;
String classBuilder=new StringBuilder();
String setterGetterBuilder=new StringBuilder();
while(tables.next())
{
String tableName=tables.getString("TABLE_NAME");

if(!allTables)
{
	if(!tableSet.contains(tableName)) continue;
}

System.out.println("\n=== " + tableName + " ===");
String className=tableName.substring(0,1).toUpperCase()+tableName.substring(1);

classBuilder.append(@Table(name=\""+tableName+"\")\r\n");
classBuilder.append("public class ");
classBuilder.append(className+"\r\n{\r\n");
//String classSourceCode="@Table(name=\""+tableName+"\")\r\n";
//classSourceCode=classSourceCode+"public class ";

//classSourceCode=classSourceCode+className+"\r\n{\r\n";

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
//System.out.println(" FK: " + fkCol + " -> " + pkTbl + "(" + pkCol + ")");

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
/* if(columnName.contains("_"))
{
// remove _ for class field name
// roll_number -> rollNumber
String[] parts=columnName.split("_");

fieldName=parts[0];
for(int i=1;i<parts.length;++i)
{
parts[i]=parts[i].substring(0,1).toUpperCase()+parts[i].substring(1);
fieldName=fieldName+parts[i];
}
} */
classBuilder.append("@Column(name=\""+columnName+"\")\r\n");
//classSourceCode=classSourceCode+"@Column(name=\""+columnName+"\")\r\n";

if(isPrimaryKey)
{
	classBuilder.append("@PrimaryKey\r\n");
//classSourceCode=classSourceCode+"@PrimaryKey\r\n";
}

if(autoIncrement.equalsIgnoreCase("YES"))
{
	classBuildera.append("@AutoIncrement\r\n");
//classSourceCode=classSourceCode+"@AutoIncrement\r\n";
}

if(foreignKeyMetaDataMap.containsKey(columnName))
{
foreignKeyMetaData=foreignKeyMetaDataMap.get(columnName);
String fkCol= foreignKeyMetaData.getFKColumn();
String pkTbl=foreignKeyMetaData.getPKTable();
String pkCol=foreignKeyMetaData.getPKColumn();
classBuilder.append("@ForeignKey(parent=\"" + pkTable + "\",column=\"" + pkCol + "\")\r\n");
//classSourceCode=classSourceCode+"@ForeignKey(parent=\"" + foreignKeyMetaData.parentTable + "\",column=\"" + foreignKeyMetaData.parentColumnName + "\")\r\n" ;
}

// check which type

String fieldType=getFieldType(type,Integer.parseInt(size));
String fieldName=CaseConvertor.toCamelCase(columnName);
if(fieldType.equalsIgnoreCase("Date") fieldType="java.util.Date";
else if(fieldType.equalsIgnoreCase("BigDecimal")) fieldType="java.math.BigDecimal";

classBuilder.append("public "+fieldType+" "+fieldName+";"+"\r\n");
//classSourceCode=classSourceCode+"public "+fieldType+" "+fieldName+";"+"\r\n\r\n";
String capitalizeFieldName=fieldName.charAt(0)+fieldName.substring(1);
// generating setter
setterGetterBuilder.append("public void set"+capitalizeFieldName+"("+fieldType+" "+fieldName+")\r\n");
setterGetterBuilder.append("{\r\n");
setterGetterBuilder.append("this."fieldName+"="+fieldName+";\r\n");
setterGetterBuilder.append("}\r\n");

// generating getter
setterGetterBuilder.append("public "+fieldType+"get"+capitalizeFieldName+"()\r\n");
setterGetterBuilder.append("{\r\n");
setterGetterBuilder.append("return this."fieldName+";\r\n");
setterGetterBuilder.append("}\r\n");




/* System.out.println(" - " + columnName + " : " + type + "(" + size + ")" +" | nullable=" + isNull +" | autoincrement=" + autoIncrement); */

}
columns.close();
randomAccessFile.writeBytes(packageName+"\r\n");
randomAccessFile.writeBytes(classBuilder.toString());
randomAccessFile.writeBytes(setterGetterBuilder.toString());
randomAccessFile.writeBytes("}");
randomAccessFile.close();
primaryKeyColumns.clear();
foreignKeyMetaDataMap.clear();
classBuilder.setLength(0);
setterGetterBuilder.setLength(0);
/* classSourceCode=classSourceCode+"}"; */


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
/* for(String importLine:importLines)
{
randomAccessFile.writeBytes(importLine+"\r\n");
} */

}

tables.close();
connection.close();
}catch(IOException | SQLException ex)
{
System.out.println(ex.getMessage());
}
}
}