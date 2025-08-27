package com.daniyal.ormcore.manager;
import java.sql.*;
import com.daniyal.ormcore.config.*;
import com.daniyal.ormcore.connection.*;
import com.daniyal.ormcore.exceptions.*;
import com.daniyal.ormcore.validation.*;
import com.daniyal.ormcore.pojo.*;
import com.daniyal.ormcore.query.*;
import java.util.*;
import java.lang.reflect.*;
import java.math.BigDecimal;
public class DataManager
{
private static DataManager dataManager;
private ConfigLoader configLoader;
private Connection connection;
private Map<Class,EntityMetaData> entityMetaDataMap;
private Map<String,TableMetaData> tablesMetaMap;

private DataManager() throws ORMException
{
this.configLoader=new ConfigLoader("conf.json");
this.connection=null;
this.entityMetaDataMap=null;

populateDataStructures();
}

private void printTableMetaData(Map<String, TableMetaData> tablesMetaMap) {
    for (Map.Entry<String, TableMetaData> entry : tablesMetaMap.entrySet()) {
        String tableKey = entry.getKey();
        TableMetaData table = entry.getValue();

        System.out.println("Table Key: " + tableKey);
        System.out.println("Table Name: " + table.getTableName());

        // Print column metadata
        Map<String, ColumnMetaData> columns = table.getColumnMetaDataMap();
        if (columns != null) {
            for (Map.Entry<String, ColumnMetaData> colEntry : columns.entrySet()) {
                String columnKey = colEntry.getKey();
                ColumnMetaData columnMetaData = colEntry.getValue();

                System.out.println("   Column Key: " + columnKey);
                System.out.println("   Column Name: " + columnMetaData.getColumnName());
                System.out.println("   Type: " + columnMetaData.getDataType() + "(" + columnMetaData.getSize() + ")");
                System.out.println("   Primary Key: " + columnMetaData.isPrimaryKey());
                System.out.println("   Auto Increment: " + columnMetaData.isAutoIncrement());
                System.out.println("   Nullable: " + columnMetaData.isNullable());
                System.out.println("   Foreign Key: " + columnMetaData.isForeignKey());

                if (columnMetaData.isForeignKey() && columnMetaData.getForeignKeyMetaData() != null) {
                    ForeignKeyMetaData fk = columnMetaData.getForeignKeyMetaData();
                    System.out.println("      FK Column: " + fk.getFKColumn());
                    System.out.println("      References Table: " + fk.getPKTable());
                    System.out.println("      References Column: " + fk.getPKColumn());
                }
                System.out.println();
            }
        }

        // Print tables that reference this table
        List<ForeignKeyMetaData> referenceByList = table.getReferenceByList();
        if (referenceByList != null && !referenceByList.isEmpty()) {
            System.out.println("   Referenced By:");
            for (ForeignKeyMetaData ref : referenceByList) {
                System.out.println("      Table: " + ref.getFKTable() + ", Column: " + ref.getFKColumn());
            }
        }

        System.out.println("-------------------------------------");
    }
}

private void printEntityMetaDataData()
{
System.out.println("----------------------------------------------------------------------------------");
System.out.println("EntityMetaDataData");
for (Map.Entry<Class, EntityMetaData> entry : entityMetaDataMap.entrySet()) {
    Class entityClass = entry.getKey();
    EntityMetaData entityMetaData = entry.getValue();

    System.out.println("Entity Class: " + entityClass.getName());
    System.out.println("Table Name: " + entityMetaData.getTableName());

    Map<String, FieldMetaData> fields = entityMetaData.getFieldMetaDataMap();
    if (fields != null) {
        for (Map.Entry<String, FieldMetaData> fieldEntry : fields.entrySet()) {
            String fieldKey = fieldEntry.getKey();
            FieldMetaData fieldMetaData = fieldEntry.getValue();

            System.out.println("   Field Key: " + fieldKey);
            System.out.println("   Field Name: " + fieldMetaData.getField().getName());
            System.out.println("   Column Name: " + fieldMetaData.getColumnName());
            System.out.println("   Primary Key: " + fieldMetaData.isPrimaryKey());
            System.out.println("   Auto Increment: " + fieldMetaData.isAutoIncrement());
            System.out.println("   Foreign Key: " + fieldMetaData.isForeignKey());

            if (fieldMetaData.isForeignKey() && fieldMetaData.getForeignKeyMetaData() != null) {
                ForeignKeyMetaData fk = fieldMetaData.getForeignKeyMetaData();
                System.out.println("      FK Column: " + fk.getFKColumn());
                System.out.println("      References Table: " + fk.getPKTable());
                System.out.println("      References Column: " + fk.getPKColumn());
            }
            System.out.println();
        }
    }
    System.out.println("-------------------------------------");
}
System.out.println("--------------------------------------------------------------------------");
}
private void populateDataStructures() throws ORMException
{
this.tablesMetaMap=DatabaseMetaDataLoader.loadTableMetaData(ConnectionManager.getConnection(configLoader));
this.entityMetaDataMap=EntityScanner.scanBasePackage(this.configLoader.getBasePackage(),tablesMetaMap);
//printTableMetaData(tablesMetaMap);
//printEntityMetaDataData();
}
public static DataManager getDataManager() throws ORMException
{
if(dataManager==null)
{
dataManager=new DataManager();
return dataManager;
}
return dataManager;
}

public void begin() throws ORMException
{
// make the connection
if(dataManager.connection==null)
{
dataManager.connection=ConnectionManager.getConnection(configLoader);
}
}

public void end() throws ORMException
{
try
{
if(dataManager.connection!=null) 
{
dataManager.connection.close();
dataManager.connection=null;
}
}catch(SQLException sqlException)
{
throw new ORMException(sqlException.getMessage());
}
}

public Connection getConnection()
{
return dataManager.connection;
}


public Object save(Object entity)throws ORMException
{
if(connection==null)
{
throw new ORMException("Connection is closed, can't perform save");
}
Class entityClass=entity.getClass();

EntityMetaData entityMetaData=entityMetaDataMap.get(entityClass);
if(entityMetaData==null)
{
throw new ORMException("Entity class '" + entityClass.getName() + "' is not registered. " +"Make sure it is annotated with @Table and included in the base package defined in conf.json.");
}

String tableName=entityMetaData.getTableName();
Map<String,FieldMetaData> fieldMetaDataMap=entityMetaData.getFieldMetaDataMap();
TableMetaData tableMetaData=tablesMetaMap.get(tableName);
Map<String,ColumnMetaData> columnMetaDataMap=tableMetaData.getColumnMetaDataMap();
List<Object> params=new ArrayList<>();
Object []paramValue={null};
// now in case of foreign key check if the value exists in parent table
FieldMetaData fieldMetaData;
ForeignKeyMetaData foreignKeyMetaData;
String fkCol;
String pkTbl;
String pkCol;
String colNm;
for(Map.Entry<String,FieldMetaData> entry:fieldMetaDataMap.entrySet())
{
	fieldMetaData=entry.getValue();
	colNm=entry.getKey();
	if(fieldMetaData.isForeignKey())
	{
		foreignKeyMetaData=columnMetaDataMap.get(colNm).getForeignKeyMetaData();
		fkCol=foreignKeyMetaData.getFKColumn();
		pkTbl=foreignKeyMetaData.getPKTable();
		pkCol=foreignKeyMetaData.getPKColumn();
		if(!recordExists(entity,pkTbl,pkCol,fieldMetaData,columnMetaDataMap,paramValue))
		{
			throw new ORMException("RECORD NOT EXISTS in parent table "+pkTbl+" for column "+fkCol+" value "+paramValue[0]);
		}
		//select 1 from parent table (course) where code=?
		
	}
}
fieldMetaData=new FieldMetaData();
String sql;
QueryBuilder queryBuilder=new QueryBuilder(entity,tableName,fieldMetaDataMap,columnMetaDataMap);
Query query=queryBuilder.buildInsertQuery(fieldMetaData);
params=query.getParameters();
sql=query.getSQL();
try
{
PreparedStatement preparedStatement=connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
int x=1;

for(Object param:params)
{

preparedStatement.setObject(x++,param);
}
int affectedRow=preparedStatement.executeUpdate();

if(affectedRow==0)
{
	throw new ORMException("Save failed");
}

// set generated keys in user object
ResultSet generatedKeysResultSet=preparedStatement.getGeneratedKeys();
if(generatedKeysResultSet.next())
{
	Field fieldWithAutoIncrement=fieldMetaData.getField();
	
	Class fieldType=fieldWithAutoIncrement.getType();
	try
	{
		if(fieldType==int.class || fieldType==Integer.class)
		{
			Integer generatedKey=generatedKeysResultSet.getObject(1,Integer.class);
			fieldWithAutoIncrement.set(entity,generatedKey);
		}else if(fieldType==long.class || fieldType==Long.class)
		{
			Long generatedKey=generatedKeysResultSet.getObject(1,Long.class);
			fieldWithAutoIncrement.set(entity,generatedKey);
		}else if(fieldType==double.class || fieldType==Double.class)
		{
			Double generatedKey=generatedKeysResultSet.getObject(1,Double.class);
			fieldWithAutoIncrement.set(entity,generatedKey);
		}else if(fieldType==float.class || fieldType==Float.class)
		{
			Float generatedKey=generatedKeysResultSet.getObject(1,Float.class);
			fieldWithAutoIncrement.set(entity,generatedKey);
		}else if(fieldType==boolean.class || fieldType==Boolean.class)
		{
			Boolean generatedKey=generatedKeysResultSet.getObject(1,Boolean.class);
			fieldWithAutoIncrement.set(entity,generatedKey);
		}else if(fieldType==BigDecimal.class)
		{
			BigDecimal generatedKey=generatedKeysResultSet.getObject(1,BigDecimal.class);
			fieldWithAutoIncrement.set(entity,generatedKey);
		}else if(fieldType==String.class)
		{
			String generatedKey=generatedKeysResultSet.getObject(1,String.class);
			fieldWithAutoIncrement.set(entity,generatedKey);
		}else if(fieldType==java.util.Date.class)
		{
			java.sql.Timestamp generatedKey=generatedKeysResultSet.getObject(1,java.sql.Timestamp.class);
			java.util.Date dateVal= generatedKey==null?null:new java.util.Date(generatedKey.getTime()); 
			fieldWithAutoIncrement.set(entity,dateVal);
		}else
		{
			Object generatedKey=generatedKeysResultSet.getObject(1);
			fieldWithAutoIncrement.set(entity,generatedKey);
		}
	}catch(IllegalAccessException exception)
	{
		throw new ORMException("Cannot write value of field " + fieldWithAutoIncrement.getName() +"' in entity '" + entity.getClass().getSimpleName() +"'. Ensure the field is accessible (e.g., use @Column and allow access).");
	}
}
generatedKeysResultSet.close();
preparedStatement.close();
}catch(SQLException sqlException)
{
throw new ORMException(sqlException.getMessage());
}
return entity;
}

private boolean recordExists(Object entityInstance,String tableName,String pkCol,FieldMetaData fieldMetaData,Map<String,ColumnMetaData> columnMetaDataMap,Object []param) throws ORMException
{
Query query=QueryBuilder.buildSelectQueryForIsExists(entityInstance,tableName,pkCol,fieldMetaData,columnMetaDataMap);
String sql=query.getSQL();
List<Object> params=query.getParameters();
param[0]=params.get(0);

try
{
PreparedStatement preparedStatement=connection.prepareStatement(sql);
preparedStatement.setObject(1,param[0]);
ResultSet resultSet=preparedStatement.executeQuery();
boolean exists=resultSet.next();
resultSet.close();
preparedStatement.close();
return exists;
}catch(SQLException exception)
{
	throw new ORMException(exception.getMessage());
}
}

public void update(Object entity)throws ORMException
{
if(connection==null)
{
throw new ORMException("Connection is closed, can't perform save");
}
Class entityClass=entity.getClass();

EntityMetaData entityMetaData=entityMetaDataMap.get(entityClass);
if(entityMetaData==null)
{
throw new ORMException("Entity class '" + entityClass.getName() + "' is not registered. " +"Make sure it is annotated with @Table and included in the base package defined in conf.json.");
}

String tableName=entityMetaData.getTableName();
Map<String,FieldMetaData> fieldMetaDataMap=entityMetaData.getFieldMetaDataMap();
TableMetaData tableMetaData=tablesMetaMap.get(tableName);
Map<String,ColumnMetaData> columnMetaDataMap=tableMetaData.getColumnMetaDataMap();
List<Object> params=null;
String sql;
Object paramValue[]={null};
// first check if the record exists or not 
FieldMetaData primaryKeyFieldMetaData=entityMetaData.getPrimaryKeyFieldMetaData();

try
{
if(!recordExists(entity,tableName,null,primaryKeyFieldMetaData,columnMetaDataMap,paramValue))
{
	throw new ORMException("RECORD NOT EXISTS with for primary key value "+paramValue[0]);
}

// now in case of foreign key check if the value exists in parent table
FieldMetaData fieldMetaData;
ForeignKeyMetaData foreignKeyMetaData;
String fkCol;
String pkTbl;
String pkCol;
String colNm;
for(Map.Entry<String,FieldMetaData> entry:fieldMetaDataMap.entrySet())
{
	fieldMetaData=entry.getValue();
	colNm=entry.getKey();
	if(fieldMetaData.isForeignKey())
	{
		foreignKeyMetaData=columnMetaDataMap.get(colNm).getForeignKeyMetaData();
		fkCol=foreignKeyMetaData.getFKColumn();
		pkTbl=foreignKeyMetaData.getPKTable();
		pkCol=foreignKeyMetaData.getPKColumn();
		if(!recordExists(entity,pkTbl,pkCol,fieldMetaData,columnMetaDataMap,paramValue))
		{
			throw new ORMException("RECORD NOT EXISTS in parent table "+pkTbl+" for column "+fkCol+" value "+paramValue[0]);
		}
		//select 1 from parent table (course) where code=?
		
	}
}

QueryBuilder queryBuilder=new QueryBuilder(entity,tableName,fieldMetaDataMap,columnMetaDataMap);
Query query=queryBuilder.buildUpdateQuery();
params=query.getParameters();
sql=query.getSQL();

PreparedStatement preparedStatement=connection.prepareStatement(sql);
int x=1;

for(Object param:params)
{
preparedStatement.setObject(x++,param);
}
int affectedRow=preparedStatement.executeUpdate();

if(affectedRow==0)
{
	throw new ORMException("Update failed");
}
preparedStatement.close();
}catch(SQLException sqlException)
{
throw new ORMException(sqlException.getMessage());
}
}

public void delete(Object entity) throws ORMException
{
if(connection==null)
{
throw new ORMException("Connection is closed, can't perform save");
}
Class entityClass=entity.getClass();

EntityMetaData entityMetaData=entityMetaDataMap.get(entityClass);
if(entityMetaData==null)
{
throw new ORMException("Entity class '" + entityClass.getName() + "' is not registered. " +"Make sure it is annotated with @Table and included in the base package defined in conf.json.");
}

String tableName=entityMetaData.getTableName();
Map<String,FieldMetaData> fieldMetaDataMap=entityMetaData.getFieldMetaDataMap();
TableMetaData tableMetaData=tablesMetaMap.get(tableName);
Map<String,ColumnMetaData> columnMetaDataMap=tableMetaData.getColumnMetaDataMap();
FieldMetaData primaryKeyFieldMetaData=entityMetaData.getPrimaryKeyFieldMetaData();
List<Object> params=new ArrayList<>();
Object paramValue[]={null};
if(!recordExists(entity,tableName,null,primaryKeyFieldMetaData,columnMetaDataMap,paramValue))
{
	throw new ORMException("RECORD NOT EXISTS with for primary key value "+paramValue[0]);
}


// before deleting the record, check for foriegn key constraint is its value on any child table
// check if value exists on any child table before deleting ensure if yes then don't delete

for(ForeignKeyMetaData foreignKeyMetaData:tableMetaData.getReferenceByList())
{
	String fkTbl=foreignKeyMetaData.getFKTable();
	String fkCol=foreignKeyMetaData.getFKColumn();
	FieldMetaData fieldMetaData=fieldMetaDataMap.get(foreignKeyMetaData.getPKColumn());	
		if(recordExists(entity,fkTbl,fkCol,fieldMetaData,columnMetaDataMap,paramValue))
		{
			throw new ORMException("Foreign Key Constraint on child table "+fkTbl+" for value "+paramValue[0]+ "cannot delete");
		}
}
String sql;
QueryBuilder queryBuilder=new QueryBuilder(entity,tableName,fieldMetaDataMap,columnMetaDataMap);
Query query=queryBuilder.buildDeleteQuery();
params=query.getParameters();
sql=query.getSQL();

try
{
PreparedStatement preparedStatement=connection.prepareStatement(sql);
preparedStatement.setObject(1,params.get(0));
int affectedRow=preparedStatement.executeUpdate();

if(affectedRow==0)
{
	throw new ORMException("Deletion failed");
}
preparedStatement.close();
}catch(SQLException sqlException)
{
throw new ORMException(sqlException.getMessage());
}
}

public <T> QueryBuilder<T> query(Class<T> entityClass) throws ORMException
{
if(connection==null)
{
throw new ORMException("Connection is closed, can't perform save");
}


EntityMetaData entityMetaData=entityMetaDataMap.get(entityClass);
if(entityMetaData==null)
{
throw new ORMException("Entity class '" + entityClass.getName() + "' is not registered. " +"Make sure it is annotated with @Table and included in the base package defined in conf.json.");
}
String tableName=entityMetaData.getTableName();
QueryBuilder<T> queryBuilder=new QueryBuilder(connection,entityClass,entityMetaData.getEntityNoArgConstructor(),entityMetaData.getFieldMetaDataMap(),tableName);
return queryBuilder;
}


}