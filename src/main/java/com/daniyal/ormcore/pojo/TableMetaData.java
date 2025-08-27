package com.daniyal.ormcore.pojo;
import java.util.*;		
public class TableMetaData
{
private String tableName;
private Map<String,ColumnMetaData> columnMetaDataMap;
private Set<TableMetaData> childTableMetaDataSet;
public TableMetaData()
{
this.tableName="";
this.columnMetaDataMap=null;
}
public void setTableName(java.lang.String tableName)
{
this.tableName=tableName;
}
public java.lang.String getTableName()
{
return this.tableName;
}
public void setColumnMetaDataMap(java.util.Map<String,ColumnMetaData> columnMetaDataMap)
{
this.columnMetaDataMap=columnMetaDataMap;
}
public java.util.Map<String,ColumnMetaData> getColumnMetaDataMap()
{
return this.columnMetaDataMap;
}
public void setChildTableMetaDataSet(TableMetaData childTableMetaDataSet)
{
	this.childTableMetaDataSet=childTableMetaDataSet;
}
public Set<TableMetaData> getChildTableMetaDataSet()
{
	return this.childTableMetaDataSet;
}
}