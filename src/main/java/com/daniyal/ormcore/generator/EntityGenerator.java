package com.daniyal.ormcore.generator;
import com.daniyal.ormcore.pojo.ForeignKeyMetaData;
import java.sql.*;
import java.io.*;
import java.util.*;
class EntityGenerator
{

public static void main(String []args)
{
try
{

java -cp daniyal-orm.jar;. com.daniyal.ormcore.generator.EntityGenerator --package=com.anis.customer.entities 
(optional)--output=src/main/java (optional)--table=student,course 
(optional only if user is calling from the conf.json dir)--config=path/to/conf.json
	
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
Connection connection=DAOConnection.getConnection();

DatabaseMetaData meta=connection.getMetaData();
ResultSet tables=meta.getTables(connection.getCatalog(),null,"%",new String[]{"TABLE"});

File file;
RandomAccessFile randomAccessFile;
Set<String> primaryKeyColumns=new HashSet<>();
List<String> importLines=new ArrayList<>();
Map<String,ForeignKeyInfo> foreignKeyColumnsMap=new HashMap<>();
ForeignKeyInfo foreignKeyInfo;
while(tables.next())
{
String tableName=tables.getString("TABLE_NAME");

System.out.println("\n=== " + tableName + " ===");

String classSourceCode="@Table(name=\""+tableName+"\")\r\n";
classSourceCode=classSourceCode+"public class ";
String className=tableName.substring(0,1).toUpperCase()+tableName.substring(1);
classSourceCode=classSourceCode+className+"\r\n{\r\n";

file=new File(className+".java");
if(file==null)
{
System.out.println("Unable to create file for "+className);
return;
}


randomAccessFile=new RandomAccessFile(file,"rw");
if(randomAccessFile==null)
{
System.out.println("Unable to create file for "+className);
return;
}



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
foreignKeyInfo=new ForeignKeyInfo();
foreignKeyInfo.fkColumnName=fkCol;
foreignKeyInfo.parentTable=pkTbl;
foreignKeyInfo.parentColumnName=pkCol;
foreignKeyColumnsMap.put(fkCol,foreignKeyInfo);
System.out.println(" FK: " + fkCol + " -> " + pkTbl + "(" + pkCol + ")");

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
String fieldName=columnName;
if(columnName.contains("_"))
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
}
classSourceCode=classSourceCode+"@Column(name=\""+columnName+"\")\r\n";

if(isPrimaryKey)
{
classSourceCode=classSourceCode+"@PrimaryKey\r\n";
}

if(autoIncrement.equalsIgnoreCase("YES"))
{
classSourceCode=classSourceCode+"@AutoIncrement\r\n";
}

if(foreignKeyColumnsMap.containsKey(columnName))
{
foreignKeyInfo=foreignKeyColumnsMap.get(columnName);
classSourceCode=classSourceCode+"@ForeignKey(parent=\"" + foreignKeyInfo.parentTable + "\",column=\"" + foreignKeyInfo.parentColumnName + "\")\r\n" ;
}

// check which type

String fieldType=getFieldType(type,Integer.parseInt(size));
if(fieldType.equalsIgnoreCase("Date")) importLines.add("import java.util.Date;");
else if(fieldType.equalsIgnoreCase("BigDecimal")) importLines.add("import java.math.BigDecimal");

classSourceCode=classSourceCode+"public "+fieldType+" "+fieldName+";"+"\r\n\r\n";





System.out.println(" - " + columnName + " : " + type + "(" + size + ")" +" | nullable=" + isNull +" | autoincrement=" + autoIncrement);

}
columns.close();

classSourceCode=classSourceCode+"}";


ResultSet idx=meta.getIndexInfo(connection.getCatalog(), null, tableName, false, false);
while(idx.next()) 
{
String idxName = idx.getString("INDEX_NAME");
String colName = idx.getString("COLUMN_NAME");
boolean nonUnique = idx.getBoolean("NON_UNIQUE");
//System.out.println(" IDX: " + idxName + " on " + colName + " | unique=" + !nonUnique);
}
idx.close();

for(String importLine:importLines)
{
randomAccessFile.writeBytes(importLine+"\r\n");
}

randomAccessFile.writeBytes(classSourceCode);
randomAccessFile.close();
primaryKeyColumns.clear();
foreignKeyColumnsMap.clear();
}

tables.close();
connection.close();
}catch(IOException | SQLException ex)
{
System.out.println(ex.getMessage());
}
}
}