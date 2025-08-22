package com.daniyal.ormcore.pojo;
public class ForeignKeyInfo
{
private String fkColumn;
private  String pkTable;
private String pkColumn;
public ForeignKeyInfo()
{
this.fkColumn="";
this.pkTable="";
this.fkColumn="";
}
public void setFKColumn(java.lang.String fkColumn)
{
this.fkColumn=fkColumn;
}
public java.lang.String getFKColumn()
{
return this.fkColumn;
}
public void setParentTable(java.lang.String pkTable)
{
this.pkTable=pkTable;
}
public java.lang.String getPKTable()
{
return this.pkTable;
}
public void setPKColumn(java.lang.String pkColumn)
{
this.pkColumn=pkColumn;
}
public java.lang.String getPKColumn()
{
return this.pkColumn;
}
}