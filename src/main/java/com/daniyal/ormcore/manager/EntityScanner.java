package com.daniyal.ormcore.manager;
import com.daniyal.ormcore.pojo.*;
import com.daniyal.ormcore.utils.*;
import com.daniyal.ormcore.exceptions.*;
import com.daniyal.ormcore.annotations.*;
import java.lang.reflect.*;
import java.lang.annotation.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.util.jar.*;
class EntityScanner
{
private static void scanDirectory(File directory,String packageName,Map<Class,EntityMeta> entitiesMetaMap,Map<String,TableMetaData> tableMetaDataMap) throws Exception 
{
for(File file:directory.listFiles())
{
if(file.isDirectory())
{
scanDirectory(file,packageName+"."+file.getName(),entitiesMetaMap,tableMetaDataMap);
}
else if(file.getName().endsWith(".class"))
{
String className=packageName+"."+file.getName().replace(".class","");
System.out.println("Discovered class : "+className);	

Class clazz=Class.forName(className);
System.out.println("Class loaded : "+clazz.getName());

handleClassMetaData(clazz,entitiesMetaMap,tableMetaDataMap);
}
}// for loop ends on directory files list

}

public static Map<Class,EntityMeta> scanBasePackage(String basePackage,Map<String,TableMetaData> tableMetaDataMap) throws ORMException
{
Map<Class,EntityMeta> entitiesMetaMap=new HashMap<>();
ClassLoader classLoader=Thread.currentThread().getContextClassLoader();
System.out.println("Base package "+basePackage);


try
{
String path=basePackage.replace(".",File.separator);
System.out.println("Path :"+path);
// Get all resources(directory or jar) for basePackage
Enumeration<URL> resources=classLoader.getResources(path);
URL resource;

// Loop over every resource(can either be folder or jar)
while(resources.hasMoreElements())
{
resource=resources.nextElement();
System.out.println("Found resource : "+resource);

// if it's a folder
if(resource.getProtocol().equals("file"))
{
File directory=new File(resource.toURI());
scanDirectory(directory,basePackage,entitiesMetaMap,tableMetaDataMap);

}// folder on disk condition ends
else if(resource.getProtocol().equals("jar"))
{
// this how path looks 
//jar:file:/C:/libs/myLib.jar!/com/example/MyClass.class
// because of this below line it will become 
// C:/libs/myLib.jar
String jarPath=resource.getPath().substring("file:/".length(),resource.getPath().indexOf("!"));
JarFile jarFile=new JarFile(jarPath);

// going through every entry in the jar file
for(JarEntry entry:java.util.Collections.list(jarFile.entries()))
{
String name=entry.getName();

// only picking .class files inside our package 
if(name.startsWith(path) && name.endsWith(".class"))
{
String className=name.replace("/",".").replace(".class","");
System.out.println("Discovered class : "+className);	

Class clazz=Class.forName(className);
System.out.println("Class loaded : "+clazz.getName());
handleClassMetaData(clazz,entitiesMetaMap,tableMetaDataMap);
}
} // for loop on jar entries ends here

}// jar part ends here

}// loop ends on resources
}catch(ClassNotFoundException | IOException | URISyntaxException exception)
{
throw new ORMException(exception.getMessage());
}catch(Exception exception)
{
throw new ORMException(exception.getMessage());
}
return entitiesMetaMap;

}// function ends


private static void handleClassMetaData(Class clazz,Map<Class,EntityMeta> entitiesMetaMap,Map<String,TableMetaData> tableMetaDataMap) throws ORMException
{
Table tableAnnotation=(Table)clazz.getAnnotation(Table.class);
if(tableAnnotation==null) return;

String tableName=tableAnnotation.name().trim();
if(tableName==null || tableName.length()==0)
{
tableName=clazz.getSimpleName();
tableName=CaseConvertor.toSnakeCase(tableName);
}

TableMetaData tableMetaData=tableMetaDataMap.get(tableName);
if(tableMetaData==null)
{
throw new ORMException("No table exists with name "+tableName);
}

System.out.println(" Checking for : "+tableName+" table exists in mysql or not");
// we'll use map of TableMetaData
Field []fields=clazz.getFields();
Map<String,ColumnMetaData> columnMetaDataMap=tableMetaData.getColumnMetaDataMap();
int columnMetaDataMapSize=columnMetaDataMap.size();
int fieldsSize=fields.length;
int fieldsWithColumnAnnotation=fields.length;
AutoIncrement autoIncrementAnnotation;
Column columnAnnotation;
ForeignKey foreignKeyAnnotation;
PrimaryKey primaryKeyAnnotation;

String columnAnnotationValue;
ColumnMetaData columnMetaData;
String foreignKeyAnnotationFKColumn;
String foreignKeyAnnotationPKTable;
String foreignKeyAnnotationPKColumn;
int columnVarSize;

ForeignKeyInfo foreignKeyInfo;
String fkColumn;
String pkTable;
String pkColumn;

EntityMeta entityMeta;
FieldMeta fieldMeta;
String columnName;
boolean isPrimaryKey;
boolean isAutoIncrement;
boolean isForeignKey;
ForeignKeyInfo foreignKeyInfo1=null;

Map<String,FieldMeta> fieldMetaMap=new HashMap<>();
for(Field field:fields)
{
columnAnnotation=(Column)field.getAnnotation(Column.class);
if(columnAnnotation==null)
{
// not part of sql table column so ignore this

if(field.isAnnotationPresent(PrimaryKey.class) || field.isAnnotationPresent(AutoIncrement.class) || field.isAnnotationPresent(ForeignKey.class))
{
throw new ORMException("Entity class " + clazz.getSimpleName() +" property " + field.getName() +" has @PrimaryKey/@AutoIncrement/@ForeignKey annotation but is missing @Column");
}
fieldsWithColumnAnnotation--;
continue;
}
columnAnnotationValue=columnAnnotation.name();
columnMetaData=columnMetaDataMap.get(columnAnnotationValue);
columnName=columnAnnotationValue;
if(columnMetaData==null)
{
throw new ORMException("Entity class " + clazz.getSimpleName() +" property " + field.getName() +" declares @Column(name=\"" + columnName + "\") but no such column exists in table '" + tableName + "'");
}

isPrimaryKey=field.isAnnotationPresent(PrimaryKey.class);

if(isPrimaryKey && columnMetaData.getIsPrimaryKey()==false)
{
throw new ORMException("Entity class " + clazz.getSimpleName() +" property '" + field.getName() + "' is annotated @PrimaryKey but corresponding column '" +columnAnnotationValue + "' is not a primary key column in table '" + tableName + "'");
}

if(!isPrimaryKey && columnMetaData.getIsPrimaryKey())
{
throw new ORMException("Entity class " + clazz.getSimpleName() +" property '" + field.getName() + "' is missing @PrimaryKey annotation but column '" +columnAnnotationValue + "' is a primary key column in table '" + tableName + "'");
}

isAutoIncrement=field.isAnnotationPresent(AutoIncrement.class);

if(isAutoIncrement && columnMetaData.getIsAutoIncrement()==false)
{
throw new ORMException("Entity class " + clazz.getSimpleName() +" property '" + field.getName() + "' annotated @AutoIncrement but column '" +columnAnnotationValue + "' is not auto-increment in table '" + tableName + "'");
}

if(!isAutoIncrement && columnMetaData.getIsAutoIncrement())
{
throw new ORMException("Entity class " + clazz.getSimpleName() +" property '" + field.getName() + "' missing @AutoIncrement annotation but column '" +columnAnnotationValue + "' is auto-increment in table '" + tableName + "'");
}

isForeignKey=field.isAnnotationPresent(ForeignKey.class);

if(isForeignKey)
{
if(columnMetaData.getIsForeignKey()==false)
{
throw new ORMException("Entity class " + clazz.getSimpleName() +" property '" + field.getName() + "' annotated @ForeignKey but column '" +columnAnnotationValue + "' is not a foreign key in table '" + tableName + "'");
}
foreignKeyAnnotation=(ForeignKey)field.getAnnotation(ForeignKey.class);
foreignKeyAnnotationFKColumn=columnAnnotationValue;
foreignKeyAnnotationPKTable=foreignKeyAnnotation.parent();
foreignKeyAnnotationPKColumn=foreignKeyAnnotation.column();

foreignKeyInfo=columnMetaData.getForeignKeyInfo();

fkColumn=foreignKeyInfo.getFKColumn();
pkTable=foreignKeyInfo.getPKTable();
pkColumn=foreignKeyInfo.getPKColumn();
if(fkColumn.equals(foreignKeyAnnotationFKColumn)==false)
{
throw new ORMException("Entity class " + clazz.getSimpleName() +" property '" + field.getName() + "' @ForeignKey FK column mismatch: annotation '" +foreignKeyAnnotationFKColumn + "' vs DB '" + foreignKeyInfo.getFKColumn() + "'");
}
if(pkTable.equals(foreignKeyAnnotationPKTable)==false)
{
throw new ORMException("Entity class " + clazz.getSimpleName() +" property '" + field.getName() + "' @ForeignKey parent table mismatch: annotation '" +foreignKeyAnnotationPKTable + "' vs DB '" + foreignKeyInfo.getPKTable() + "'");
}
if(pkColumn.equals(foreignKeyAnnotationPKColumn)==false)
{
throw new ORMException("Entity class " + clazz.getSimpleName() +" property '" + field.getName() + "' @ForeignKey parent column mismatch: annotation '" +foreignKeyAnnotationPKColumn + "' vs DB '" + foreignKeyInfo.getPKColumn() + "'");
}
foreignKeyInfo1=new ForeignKeyInfo();
foreignKeyInfo1.setFKColumn(foreignKeyAnnotationFKColumn);
foreignKeyInfo1.setPKTable(foreignKeyAnnotationPKTable);
foreignKeyInfo1.setPKColumn(foreignKeyAnnotationPKColumn);
}

if(!isForeignKey && columnMetaData.getIsForeignKey())
{
throw new ORMException("Entity class " + clazz.getSimpleName() +" property '" + field.getName() + "' missing @ForeignKey annotation but column '" +columnAnnotationValue + "' is a foreign key in table '" + tableName + "'");
}

 // TODO: Add data type compatibility validation here (future improvement).

if(!TypeMapper.isCompatible(field.getType(),columnMetaData.getType(),columnMetaData.getSize()))
{
	 throw new ORMException("Type mismatch for field '" + field.getName() +"' in entity '" + clazz.getSimpleName() +
        "': Java type '" + field.getType().getSimpleName() +
        "' is not compatible with SQL type '" + columnMetaData.getType() + "'");
}


field.setAccessible(true);

fieldMeta=new FieldMeta();
fieldMeta.setField(field);
fieldMeta.setColumnName(columnName);
fieldMeta.setIsPrimaryKey(isPrimaryKey);
fieldMeta.setIsAutoIncrement(isAutoIncrement);
fieldMeta.setIsForeignKey(isForeignKey);
if(isForeignKey)
{
fieldMeta.setForeignKeyInfo(foreignKeyInfo1);
}

fieldMetaMap.put(columnName,fieldMeta);
} // for loop ends
// means there are missing fields to that didn't represents some table columns
if(fieldsWithColumnAnnotation!=columnMetaDataMapSize)
{
fieldMetaMap.clear();
throw new ORMException("Entity class " + clazz.getSimpleName() +" has missing fields for some columns in the table '" + tableName + "'");
}

entityMeta=new EntityMeta();
entityMeta.setEntityClass(clazz);
entityMeta.setTableName(tableName);
entityMeta.setFields(fieldMetaMap);
entitiesMetaMap.put(clazz,entityMeta);
}// function ends

}// class ends 

