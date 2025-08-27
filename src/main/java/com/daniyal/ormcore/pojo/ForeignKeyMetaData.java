package com.daniyal.ormcore.pojo;
public class ForeignKeyMetaData
{
private String fkTable;
private String fkColumn;
private  String pkTable;
private String pkColumn;
public ForeignKeyMetaData()
{
this.fkTable="";
this.fkColumn="";
this.pkTable="";
this.fkColumn="";
}
public void setFKTable(java.lang.String fkTable)
{
this.fkTable=fkTable;
}
public java.lang.String getFKTable()
{
return this.fkTable;
}
public void setFKColumn(java.lang.String fkColumn)
{
this.fkColumn=fkColumn;
}
public java.lang.String getFKColumn()
{
return this.fkColumn;
}
public void setPKTable(java.lang.String pkTable)
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