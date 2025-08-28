package com.daniyal.ormcore.manager;
import com.daniyal.ormcore.metadata.*;
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
	private static void scanClasspathJars(String basePackage,Map<Class,EntityMetaData> entitiesMetaMap,Map<String,TableMetaData> tableMetaDataMap,Map<String,Class> tableNameToClassMap) throws Exception 
	{
		String classpath=System.getProperty("java.class.path");
		String[] entries=classpath.split(File.pathSeparator);
		String packagePath=basePackage.replace(".","/");
		for(String entry:entries)
		{
			File file=new File(entry);
			if(file.isFile() && file.getName().endsWith(".jar"))
			{
				try(JarFile jarFile=new JarFile(file))
				{
							// going through every entry in the jar file
					for(JarEntry jarEntry:java.util.Collections.list(jarFile.entries()))	
					{
						String name=jarEntry.getName();

						// only picking .class files inside our package 
						if(name.startsWith(packagePath) && name.endsWith(".class"))
						{
							String className=name.replace("/",".").replace(".class","");
							//System.out.println("(Jar)Discovered class : "+className);	

							Class clazz=Class.forName(className);
							//System.out.println("Class loaded : "+clazz.getName());
							handleClassMetaData(clazz,entitiesMetaMap,tableMetaDataMap,tableNameToClassMap);
						}
					} // for loop on jar entries ends here

				}
			}
		}
	}
private static void scanDirectory(File directory,String packageName,Map<Class,EntityMetaData> entitiesMetaMap,Map<String,TableMetaData> tableMetaDataMap,Map<String,Class> tableNameToClassMap) throws Exception 
{
for(File file:directory.listFiles())
{
if(file.isDirectory())
{
scanDirectory(file,packageName+"."+file.getName(),entitiesMetaMap,tableMetaDataMap,tableNameToClassMap);
}
else if(file.getName().endsWith(".class"))
{
String className=packageName+"."+file.getName().replace(".class","");
//System.out.println("Discovered class : "+className);	

Class clazz=Class.forName(className);
//System.out.println("Class loaded : "+clazz.getName());

handleClassMetaData(clazz,entitiesMetaMap,tableMetaDataMap,tableNameToClassMap);
}
}// for loop ends on directory files list

}

public static Map<Class,EntityMetaData> scanBasePackage(String basePackage,Map<String,TableMetaData> tableMetaDataMap,Map<String,Class> tableNameToClassMap) throws ORMException
{
Map<Class,EntityMetaData> entitiesMetaMap=new HashMap<>();
ClassLoader classLoader=Thread.currentThread().getContextClassLoader();
//System.out.println("Base package "+basePackage);


try
{
String path=basePackage.replace(".","/");
//System.out.println("Path :"+path);
// Get all resources(directory or jar) for basePackage
Enumeration<URL> resources=classLoader.getResources(path);
URL resource;
boolean inDisk=false;
// Loop over every resource(can either be folder or jar)
while(resources.hasMoreElements())
{
resource=resources.nextElement();
//System.out.println("Found resource : "+resource);
//System.out.println("Resource protocol: " + resource.getProtocol() + " -> " + resource);

// if it's a folder
if(resource.getProtocol().equals("file"))
{
File directory=new File(resource.toURI());

scanDirectory(directory,basePackage,entitiesMetaMap,tableMetaDataMap,tableNameToClassMap);

}// folder on disk condition ends

}// loop ends on resources

	scanClasspathJars(basePackage, entitiesMetaMap, tableMetaDataMap, tableNameToClassMap);
}catch(ClassNotFoundException | IOException | URISyntaxException exception)
{
throw new ORMException(exception.getMessage());
}catch(Exception exception)
{
throw new ORMException(exception.getMessage());
}
return entitiesMetaMap;

}// function ends


private static void handleClassMetaData(Class clazz,Map<Class,EntityMetaData> entitiesMetaMap,Map<String,TableMetaData> tableMetaDataMap,Map<String,Class> tableNameToClassMap) throws ORMException
{
Table tableAnnotation=(Table)clazz.getAnnotation(Table.class);
View viewAnnotation=(View)clazz.getAnnotation(View.class);
boolean isView=false;
if(tableAnnotation!=null && viewAnnotation!=null)
{
	throw new ORMException(
    "Configuration error: Class " + clazz.getName() +
    " cannot be annotated with both @Table and @View. " +
    "Only one annotation is allowed. Use @Table for entity mappings or @View for database views."
);
}
if(tableAnnotation==null && viewAnnotation==null)
{
	return;
}
if(tableAnnotation==null) 
{
	isView=true;
}

String tableName;
if(isView)
	tableName=viewAnnotation.name().trim();
else
	tableName=tableAnnotation.name().trim();
if(tableName==null || tableName.length()==0)
{
tableName=clazz.getSimpleName();
tableName=CaseConvertor.toSnakeCase(tableName);
}

TableMetaData tableMetaData=tableMetaDataMap.get(tableName);
if(tableMetaData==null)
{
if(isView)
	throw new ORMException("No view exists with name "+tableName);
else
	throw new ORMException("No table exists with name "+tableName);
}	
Constructor entityNoArgConstructor=null;
try
{
	entityNoArgConstructor=clazz.getDeclaredConstructor();
	entityNoArgConstructor.setAccessible(true);
}catch(NoSuchMethodException exp)
	{
		throw new ORMException(clazz.getName()+" must have a no-arg constructor.");
	}
//System.out.println(" Checking for : "+tableName+" table exists in mysql or not");
// we'll use map of TableMetaData
Field []fields=clazz.getDeclaredFields();
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

ForeignKeyMetaData foreignKeyMetaData;
String fkColumn;
String pkTable;
String pkColumn;

EntityMetaData entityMetaData;
FieldMetaData fieldMetaData;
String columnName;
boolean isPrimaryKey;
boolean isAutoIncrement;
boolean isForeignKey;
FieldMetaData primaryKeyFieldMetaData=null;
FieldMetaData autoIncrementFieldMetaData=null;
ForeignKeyMetaData foreignKeyMetaData1=null;

Map<String,FieldMetaData> fieldMetaDataMap=new HashMap<>();

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

if(isPrimaryKey && columnMetaData.isPrimaryKey()==false)
{
throw new ORMException("Entity class " + clazz.getSimpleName() +" property '" + field.getName() + "' is annotated @PrimaryKey but corresponding column '" +columnAnnotationValue + "' is not a primary key column in table '" + tableName + "'");
}

if(!isPrimaryKey && columnMetaData.isPrimaryKey())
{
throw new ORMException("Entity class " + clazz.getSimpleName() +" property '" + field.getName() + "' is missing @PrimaryKey annotation but column '" +columnAnnotationValue + "' is a primary key column in table '" + tableName + "'");
}

isAutoIncrement=field.isAnnotationPresent(AutoIncrement.class);

if(isAutoIncrement && columnMetaData.isAutoIncrement()==false)
{
throw new ORMException("Entity class " + clazz.getSimpleName() +" property '" + field.getName() + "' annotated @AutoIncrement but column '" +columnAnnotationValue + "' is not auto-increment in table '" + tableName + "'");
}

if(!isAutoIncrement && columnMetaData.isAutoIncrement())
{
throw new ORMException("Entity class " + clazz.getSimpleName() +" property '" + field.getName() + "' missing @AutoIncrement annotation but column '" +columnAnnotationValue + "' is auto-increment in table '" + tableName + "'");
}

isForeignKey=field.isAnnotationPresent(ForeignKey.class);

if(isForeignKey)
{
if(columnMetaData.isForeignKey()==false)
{
throw new ORMException("Entity class " + clazz.getSimpleName() +" property '" + field.getName() + "' annotated @ForeignKey but column '" +columnAnnotationValue + "' is not a foreign key in table '" + tableName + "'");
}
foreignKeyAnnotation=(ForeignKey)field.getAnnotation(ForeignKey.class);
foreignKeyAnnotationFKColumn=columnAnnotationValue;
foreignKeyAnnotationPKTable=foreignKeyAnnotation.parent();
foreignKeyAnnotationPKColumn=foreignKeyAnnotation.column();

foreignKeyMetaData=columnMetaData.getForeignKeyMetaData();

fkColumn=foreignKeyMetaData.getFKColumn();
pkTable=foreignKeyMetaData.getPKTable();
pkColumn=foreignKeyMetaData.getPKColumn();
if(fkColumn.equals(foreignKeyAnnotationFKColumn)==false)
{
throw new ORMException("Entity class " + clazz.getSimpleName() +" property '" + field.getName() + "' @ForeignKey FK column mismatch: annotation '" +foreignKeyAnnotationFKColumn + "' vs DB '" + foreignKeyMetaData.getFKColumn() + "'");
}
if(pkTable.equals(foreignKeyAnnotationPKTable)==false)
{
throw new ORMException("Entity class " + clazz.getSimpleName() +" property '" + field.getName() + "' @ForeignKey parent table mismatch: annotation '" +foreignKeyAnnotationPKTable + "' vs DB '" + foreignKeyMetaData.getPKTable() + "'");
}
if(pkColumn.equals(foreignKeyAnnotationPKColumn)==false)
{
throw new ORMException("Entity class " + clazz.getSimpleName() +" property '" + field.getName() + "' @ForeignKey parent column mismatch: annotation '" +foreignKeyAnnotationPKColumn + "' vs DB '" + foreignKeyMetaData.getPKColumn() + "'");
}
foreignKeyMetaData1=new ForeignKeyMetaData();
foreignKeyMetaData1.setFKColumn(foreignKeyAnnotationFKColumn);
foreignKeyMetaData1.setPKTable(foreignKeyAnnotationPKTable);
foreignKeyMetaData1.setPKColumn(foreignKeyAnnotationPKColumn);
}

if(!isForeignKey && columnMetaData.isForeignKey())
{
throw new ORMException("Entity class " + clazz.getSimpleName() +" property '" + field.getName() + "' missing @ForeignKey annotation but column '" +columnAnnotationValue + "' is a foreign key in table '" + tableName + "'");
}

 //[DONE] TODO: Add data type compatibility validation here (future improvement).

if(!TypeMapper.isCompatible(field.getType(),columnMetaData.getDataType(),columnMetaData.getSize()))
{
	 throw new ORMException("Type mismatch for field '" + field.getName() +"' in entity '" + clazz.getSimpleName() +
        "': Java type '" + field.getType().getSimpleName() +
        "' is not compatible with SQL type '" + columnMetaData.getDataType() + "'");
}


field.setAccessible(true);

fieldMetaData=new FieldMetaData();
fieldMetaData.setField(field);
fieldMetaData.setColumnName(columnName);
fieldMetaData.setPrimaryKey(isPrimaryKey);
fieldMetaData.setAutoIncrement(isAutoIncrement);
fieldMetaData.setForeignKey(isForeignKey);
if(isForeignKey)
{
fieldMetaData.setForeignKeyMetaData(foreignKeyMetaData1);
}
if(isPrimaryKey)
{
	primaryKeyFieldMetaData=fieldMetaData;
}
if(isAutoIncrement)
{
	autoIncrementFieldMetaData=fieldMetaData;
}
fieldMetaDataMap.put(columnName,fieldMetaData);
} // for loop ends
// means there are missing fields to that didn't represents some table columns
if(fieldsWithColumnAnnotation!=columnMetaDataMapSize)
{
fieldMetaDataMap.clear();
System.out.println(fieldsWithColumnAnnotation);
System.out.println(columnMetaDataMapSize);
throw new ORMException("Entity class " + clazz.getSimpleName() +" has missing fields for some columns in the table '" + tableName + "'");
}

entityMetaData=new EntityMetaData();
entityMetaData.setEntityClass(clazz);
entityMetaData.setEntityNoArgConstructor(entityNoArgConstructor);
entityMetaData.setTableName(tableName);
entityMetaData.setPrimaryKeyFieldMetaData(primaryKeyFieldMetaData);
entityMetaData.setAutoIncrementFieldMetaData(autoIncrementFieldMetaData);
entityMetaData.setFieldMetaDataMap(fieldMetaDataMap);
entityMetaData.setView(isView);
entitiesMetaMap.put(clazz,entityMetaData);
tableNameToClassMap.put(tableName,clazz);

}// function ends

}// class ends 

