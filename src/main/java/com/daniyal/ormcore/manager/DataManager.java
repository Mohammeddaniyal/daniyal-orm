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
public class DataManager
{
private static DataManager dataManager;
private ConfigLoader configLoader;
private Connection connection;
private Map<Class,EntityMeta> entitiesMetaMap;
private Map<String,TableMetaData> tablesMetaMap;
private DataManager() throws ORMException
{
this.configLoader=new ConfigLoader();
this.connection=null;
this.entitiesMetaMap=null;
populateDataStructures();
}
private void printTableMetaData(Map<String,TableMetaData> tablesMetaMap)
{
for (Map.Entry<String, TableMetaData> entry : tablesMetaMap.entrySet()) {
    String tableKey = entry.getKey();
    TableMetaData table = entry.getValue();

    System.out.println("Table Key: " + tableKey);
    System.out.println("Table Name: " + table.getTableName());

    Map<String, ColumnMetaData> columns = table.getColumnMetaDataMap();
    if (columns != null) {
        for (Map.Entry<String, ColumnMetaData> colEntry : columns.entrySet()) {
            String columnKey = colEntry.getKey();
            ColumnMetaData column = colEntry.getValue();

            System.out.println("   Column Key: " + columnKey);
            System.out.println("   Column Name: " + column.getColumnName());
            System.out.println("   Type: " + column.getType() + "(" + column.getSize() + ")");
            System.out.println("   Primary Key: " + column.getIsPrimaryKey());
            System.out.println("   Auto Increment: " + column.getIsAutoIncrement());
            System.out.println("   Nullable: " + column.getIsNull());
            System.out.println("   Foreign Key: " + column.getIsForeignKey());

            if (column.getIsForeignKey() && column.getForeignKeyInfo() != null) {
                ForeignKeyInfo fk = column.getForeignKeyInfo();
                System.out.println("      FK Column: " + fk.getFKColumn());
                System.out.println("      References Table: " + fk.getPKTable());
                System.out.println("      References Column: " + fk.getPKColumn());
            }
            System.out.println();
        }
    }
    System.out.println("-------------------------------------");
}

}

private void printEntityMetaData()
{
System.out.println("----------------------------------------------------------------------------------");
System.out.println("EntityMetaData");
for (Map.Entry<Class, EntityMeta> entry : entitiesMetaMap.entrySet()) {
    Class entityClass = entry.getKey();
    EntityMeta entityMeta = entry.getValue();

    System.out.println("Entity Class: " + entityClass.getName());
    System.out.println("Table Name: " + entityMeta.getTableName());

    Map<String, FieldMeta> fields = entityMeta.getFields();
    if (fields != null) {
        for (Map.Entry<String, FieldMeta> fieldEntry : fields.entrySet()) {
            String fieldKey = fieldEntry.getKey();
            FieldMeta fieldMeta = fieldEntry.getValue();

            System.out.println("   Field Key: " + fieldKey);
            System.out.println("   Field Name: " + fieldMeta.getField().getName());
            System.out.println("   Column Name: " + fieldMeta.getColumnName());
            System.out.println("   Primary Key: " + fieldMeta.getIsPrimaryKey());
            System.out.println("   Auto Increment: " + fieldMeta.getIsAutoIncrement());
            System.out.println("   Foreign Key: " + fieldMeta.getIsForeignKey());

            if (fieldMeta.getIsForeignKey() && fieldMeta.getForeignKeyInfo() != null) {
                ForeignKeyInfo fk = fieldMeta.getForeignKeyInfo();
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
this.entitiesMetaMap=EntityScanner.scanBasePackage(this.configLoader.getBasePackage(),tablesMetaMap);
//printTableMetaData(tablesMetaMap);
//printEntityMetaData();
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

EntityMeta entityMeta=entitiesMetaMap.get(entityClass);
if(entityMeta==null)
{
throw new ORMException("Entity class '" + entityClass.getName() + "' is not registered. " +"Make sure it is annotated with @Table and included in the base package defined in conf.json.");
}

String tableName=entityMeta.getTableName();
Map<String,FieldMeta> fieldMetaMap=entityMeta.getFields();
TableMetaData tableMetaData=tablesMetaMap.get(tableName);
Map<String,ColumnMetaData> columnMetaDataMap=tableMetaData.getColumnMetaDataMap();
List<Object> params=new ArrayList<>();
String sql;
QueryBuilder queryBuilder=new QueryBuilder(entity,tableName,fieldMetaMap,columnMetaDataMap);
Query query=queryBuilder.buildInsertQuery();
params=query.getParameters();
sql=query.getSQL();
System.out.println("SQL statement for insert : "+sql);
try
{
PreparedStatement preparedStatement=connection.prepareStatement(sql);
int x=1;
System.out.printf("%10s\n","VALUES");
for(Object param:params)
{
//System.out.println(v);
preparedStatement.setObject(x++,param);
}
preparedStatement.executeUpdate();
preparedStatement.close();
}catch(SQLException sqlException)
{
throw new ORMException(sqlException.getMessage());
}
return entity;
}


}