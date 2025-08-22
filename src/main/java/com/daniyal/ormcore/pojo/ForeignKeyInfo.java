package com.daniyal.ormcore.pojo;
public class ForeignKeyInfo
{
private String fkColumnName;
private  String parentTable;
private String parentColumnName;
public ForeignKeyInfo()
{
this.fkColumnName="";
this.parentTable="";
this.parentColumnName="";
}
public void setFkColumnName(java.lang.String fkColumnName)
{
this.fkColumnName=fkColumnName;
}
public java.lang.String getFkColumnName()
{
return this.fkColumnName;
}
public void setParentTable(java.lang.String parentTable)
{
this.parentTable=parentTable;
}
public java.lang.String getParentTable()
{
return this.parentTable;
}
public void setParentColumnName(java.lang.String parentColumnName)
{
this.parentColumnName=parentColumnName;
}
public java.lang.String getParentColumnName()
{
return this.parentColumnName;
}
}