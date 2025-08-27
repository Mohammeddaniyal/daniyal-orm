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
ForeignKeyMetaData foreignKeyMetaData;
DatabaseMetaData databaseMetaData=connection.getMetaData();
Set<String> primaryKeyColumns=new HashSet<>();
Map<String,ForeignKeyMetaData> foreignKeyColumnsMap=new HashMap<>();

ResultSet tablesResultSet=databaseMetaData.getTables(connection.getCatalog(),null,"%",new String[]{"TABLE"});
String tableName;
ResultSet keysResultSet;
String fkTbl;
String fkCol;
String pkTbl;
String pkCol;
ResultSet columnsResultSet;
String columnName;
String type;
int size;
int scale;
String isNullable;
String autoIncrement;
boolean isPrimaryKey;
Map<String,ColumnMetaData> columnMetaDataMap;
while(tablesResultSet.next())
{

tableName=tablesResultSet.getString("TABLE_NAME");
tableMetaData=tableMetaDataMap.get(tableName);
if(tableMetaData==null) tableMetaData=new TableMetaData();

tableMetaData.setTableName(tableName);

keysResultSet=databaseMetaData.getPrimaryKeys(connection.getCatalog(),null,tableName);
while(keysResultSet.next())
{
primaryKeyColumns.add(keysResultSet.getString("COLUMN_NAME"));
} 
keysResultSet.close();

fkTbl=tableName;
keysResultSet=databaseMetaData.getImportedKeys(connection.getCatalog(),null,tableName);
while(keysResultSet.next())
{
fkCol= keysResultSet.getString("FKCOLUMN_NAME");
pkTbl=keysResultSet.getString("PKTABLE_NAME");
pkCol=keysResultSet.getString("PKCOLUMN_NAME");
foreignKeyMetaData=new ForeignKeyMetaData();
foreignKeyMetaData.setFKTable(fkTbl);
foreignKeyMetaData.setFKColumn(fkCol);
foreignKeyMetaData.setPKTable(pkTbl);
foreignKeyMetaData.setPKColumn(pkCol);
foreignKeyColumnsMap.put(fkCol,foreignKeyMetaData);

// now check is parent TableMetaData exists
TableMetaData parentTableMetaData=tableMetaDataMap.get(pkTbl);
List<ForeignKeyMetaData> referenceByList;
Set<TableMetaData> childs;
if(parentTableMetaData!=null)
{
	referenceByList=parentTableMetaData.getReferenceByList();
	referenceByList.add(foreignKeyMetaData);
}
else
{
	parentTableMetaData=new TableMetaData();
	referenceByList=parentTableMetaData.getReferenceByList();
	referenceByList.add(foreignKeyMetaData);
	tableMetaDataMap.put(pkTbl,parentTableMetaData);
}


//System.out.println(" FK: " + fkCol + " -> " + pkTbl + "(" + pkCol + ")");
} // on foregn keys loop ends
keysResultSet.close();

columnsResultSet=databaseMetaData.getColumns(connection.getCatalog(),null,tableName,"%");
columnMetaDataMap=new HashMap<>();
while(columnsResultSet.next())
{
columnMetaData=new ColumnMetaData();
columnName=columnsResultSet.getString("COLUMN_NAME");
type=columnsResultSet.getString("TYPE_NAME");
size=columnsResultSet.getInt("COLUMN_SIZE");
scale=columnsResultSet.getInt("DECIMAL_DIGITS");
isNullable=columnsResultSet.getString("IS_NULLABLE");
autoIncrement=columnsResultSet.getString("IS_AUTOINCREMENT");
isPrimaryKey=primaryKeyColumns.contains(columnName);
if(foreignKeyColumnsMap.containsKey(columnName))
{
foreignKeyMetaData=foreignKeyColumnsMap.get(columnName);
columnMetaData.setForeignKey(true);
columnMetaData.setForeignKeyMetaData(foreignKeyMetaData);
}
columnMetaData.setColumnName(columnName);
columnMetaData.setDataType(type);
columnMetaData.setSize(size);
columnMetaData.setScale(scale);
columnMetaData.setPrimaryKey(isPrimaryKey);
columnMetaData.setAutoIncrement(autoIncrement.equalsIgnoreCase("YES"));
columnMetaData.setNullable(autoIncrement.equalsIgnoreCase("YES"));
columnMetaDataMap.put(columnName,columnMetaData);
} // on columns loop ends

columnsResultSet.close();
tableMetaData.setColumnMetaDataMap(columnMetaDataMap);
tableMetaDataMap.put(tableName,tableMetaData);


primaryKeyColumns.clear();
foreignKeyColumnsMap.clear();
} // on tables loop ends

tablesResultSet.close();
connection.close();
return tableMetaDataMap;
}catch(SQLException sqlException)
{
throw new ORMException(sqlException.getMessage());
}

} // statuic function ends
}// class ends