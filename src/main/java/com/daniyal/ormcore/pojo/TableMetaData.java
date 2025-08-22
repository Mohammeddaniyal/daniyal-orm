package com.daniyal.ormcore.pojo;
import java.util.*;		
public class TableMetaData
{
private String tableName;
private List<ColumnMetaData> columns;
public TableMetaData()
{
this.tableName="";
this.columns=null;
}
public void setTableName(java.lang.String tableName)
{
this.tableName=tableName;
}
public java.lang.String getTableName()
{
return this.tableName;
}
public void setColumns(java.util.List columns)
{
this.columns=columns;
}
public java.util.List getColumns()
{
return this.columns;
}

}