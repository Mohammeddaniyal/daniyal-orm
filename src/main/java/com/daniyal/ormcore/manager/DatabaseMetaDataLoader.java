package com.daniyal.ormcore.manager;
import com.daniyal.ormcore.exceptions.*;
import com.daniyal.ormcore.pojo.*;
import java.util.*;
import java.sql.*;
public class DatabaseMetaDataLoader
{
private static Map<String,ColumnMetaData> getColumnMetaDataMapForView(Connection connection,DatabaseMetaData databaseMetaData,String viewName)
{
ResultSet columnsResultSet=databaseMetaData.getColumns(connection.getCatalog(),null,viewName,"%");
Map<String,ColumnMetaData> columnMetaDataMap=new HashMap<>();
ColumnMetaData columnMetaData;
String columnName;
String type;
int size;
int scale;
String isNullable;
while(columnsResultSet.next())
{
columnMetaData=new ColumnMetaData();
columnName=columnsResultSet.getString("COLUMN_NAME");
type=columnsResultSet.getString("TYPE_NAME");
size=columnsResultSet.getInt("COLUMN_SIZE");
scale=columnsResultSet.getInt("DECIMAL_DIGITS");
isNullable=columnsResultSet.getString("IS_NULLABLE");
columnMetaData.setColumnName(columnName);
columnMetaData.setDataType(type);
columnMetaData.setSize(size);
columnMetaData.setScale(scale);
columnMetaData.setNullable(autoIncrement.equalsIgnoreCase("YES"));
columnMetaDataMap.put(columnName,columnMetaData);
} // on columns loop ends

columnsResultSet.close();
return columnMetaDataMap;
}
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

ResultSet tablesResultSet=databaseMetaData.getTables(connection.getCatalog(),null,"%",new String[]{"TABLE","VIEW"});
String tableName;
String tableType;
boolean isView;
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
List<ForeignKeyMetaData> foreignKeyList;
while(tablesResultSet.next())
{

tableName=tablesResultSet.getString("TABLE_NAME");
tableType=tableResultSet.getString("TABLE_TYPE");
isView="VIEW".equalsIgnoreCase(tableType);
// doing so, gettting from map the reason is in case if a current tableMetaData is pointing to a table which is parent
// constraint foreign key, so in this case if the TableMetaData object is not in the Map so we created the instance
// and put it in the map and also updated the value for referenceByList ArrayList<> which hold ForeignKeyMetaData
// for those tables which are childs
tableMetaData=tableMetaDataMap.get(tableName);
if(tableMetaData==null) tableMetaData=new TableMetaData();
foreignKeyList=tableMetaData.getForeignKeyList();
tableMetaData.setTableName(tableName);
tableMetaData.setView(isView);
if(isView)
{
	columnMetaDataMap=getColumnMetaDataMapForView(connection,databaseMetaData,tableName);
	tableMetaData.setColumnMetaDataMap(columnMetaDataMap);
	tableMetaDataMap.put(tableName,tableMetaData); 
	continue;
}


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

// update the foreignKeyList for the table
foreignKeyList.add(foreignKeyMetaData);

// now check is parent TableMetaData exists
// updating the parent table referenceByList(which hold the ForeignKeyMetaData) pointing or meta data about
// the childs of the table
TableMetaData parentTableMetaData=tableMetaDataMap.get(pkTbl);
List<ForeignKeyMetaData> referenceByList;

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
columnMetaData.setNullable(isNullable.equalsIgnoreCase("YES"));
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