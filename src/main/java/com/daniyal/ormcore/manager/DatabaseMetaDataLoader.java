package com.daniyal.ormcore.manager;
import com.daniyal.ormcore.exceptions.*;
import com.daniyal.ormcore.pojo.*;
import java.util.*;
import java.sql.*;
public class DatabaseMetaDataLoader
{
public static Map<String,TableMetaData> loadTableMetaData(Connection connection) throws ORMException
{
try
{
Map<String,TableMetaData> tableMetaDataMap=new HashMap<>();
TableMetaData tableMetaData;
ColumnMetaData columnMetaData;
DatabaseMetaData databaseMetaData=connection.getMetaData();

ResultSet tablesResultSet=databaseMetaData.getTables(connection.getCatalog(),null,"%",new String[]{"TABLE"});

while(tablesResultSet.next())
{
tableMetaData=new TableMetaData();
}






}catch(SQLException sqlException)
{
throw new ORMException(sqlException.getMessage());
}
}
}