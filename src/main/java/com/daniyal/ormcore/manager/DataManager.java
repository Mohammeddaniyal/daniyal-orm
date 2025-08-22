package com.daniyal.ormcore.manager;
import java.sql.*;
import com.daniyal.ormcore.config.*;
import com.daniyal.ormcore.connection.*;
import com.daniyal.ormcore.exceptions.*;
import com.daniyal.ormcore.pojo.*;
import java.util.*;
public class DataManager
{
private static DataManager dataManager;
private ConfigLoader configLoader;
private Connection connection;
//private Map<String,EntityMeta> entitiesMetaMap;
private Map<String,TableMetaData> tablesMetaMap;
private DataManager() throws ORMException
{
this.configLoader=new ConfigLoader();
this.connection=null;
//this.entitiesMetaMap=null;
populateDataStructures();
}
private void printData(Map<String,TableMetaData> tablesMetaMap)
{
for (Map.Entry<String, TableMetaData> entry : tablesMetaMap.entrySet()) {
    String tableKey = entry.getKey();
    TableMetaData table = entry.getValue();

    System.out.println("Table Key: " + tableKey);
    System.out.println("Table Name: " + table.getTableName());

    List<ColumnMetaData> columns = table.getColumns();
    if (columns != null) {
        for (ColumnMetaData column : columns) {
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
private void populateDataStructures() throws ORMException
{
//this.entitiesMetaMap=EntityScanner.scanBasePackage(this.configLoader.getBasePackage());
this.tablesMetaMap=DatabaseMetaDataLoader.loadTableMetaData(ConnectionManager.getConnection(configLoader));
printData(tablesMetaMap);
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

}