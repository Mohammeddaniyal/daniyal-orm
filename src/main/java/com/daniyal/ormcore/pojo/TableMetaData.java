package com.daniyal.ormcore.pojo;
import java.util.*;		
public class TableMetaData
{
private String tableName;
private Map<String,ColumnMetaData> columnMetaDataMap;
private List<ForeignKeyMetaData> referenceByList;
private List<ForiegnKetMetaData> foreignKeyList;
public TableMetaData()
{
this.tableName="";
this.columnMetaDataMap=null;
this.referenceByList=new ArrayList<>();
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
public void setReferenceByList(List<ForeignKeyMetaData> referenceByList)
{
	this.referenceByList=referenceByList;
}
public List<ForeignKeyMetaData> getReferenceByList()
{
	return this.referenceByList;
}
public void setForeignKeyList(List<ForeignKeyMetaData> foreignKeyList)
{
	this.foreignKeyList=foreignKeyList;
}
public List<ForeignKeyMetaData> getForeignKeyList()
{
	return this.foreignKeyList;
}
}