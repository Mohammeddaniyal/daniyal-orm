 package com.daniyal.ormcore.pojo;
public class ColumnMetaData
{
private String columnName;
private boolean isPrimaryKey;
private boolean isAutoIncrement;
private boolean isNullable;
private boolean isForeignKey;
private ForeignKeyInfo foreignKeyInfo;
private String dataType;
private int size;
private int scale;
public ColumnMetaData()
{
this.columnName="";
this.isPrimaryKey=false;
this.isAutoIncrement=false;
this.isNullable=false;
this.isForeignKey=false;
this.foreignKeyInfo=null;
this.dataType="";
this.size=0;
this.scale=0;
}
public void setColumnName(java.lang.String columnName)
{
this.columnName=columnName;
}
public java.lang.String getColumnName()
{
return this.columnName;
}
public void setIsPrimaryKey(boolean isPrimaryKey)
{
this.isPrimaryKey=isPrimaryKey;
}
public boolean getIsPrimaryKey()
{
return this.isPrimaryKey;
}
public void setIsAutoIncrement(boolean isAutoIncrement)
{
this.isAutoIncrement=isAutoIncrement;
}
public boolean getIsAutoIncrement()
{
return this.isAutoIncrement;
}
public void setisNullable(boolean isNullable)
{
this.isNullable=isNullable;
}
public boolean getisNullable()
{
return this.isNullable;
}
public void setIsForeignKey(boolean isForeignKey)
{
this.isForeignKey=isForeignKey;
}
public boolean getIsForeignKey()
{
return this.isForeignKey;
}
public void setForeignKeyInfo(ForeignKeyInfo foreignKeyInfo)
{
this.foreignKeyInfo=foreignKeyInfo;
}
public ForeignKeyInfo getForeignKeyInfo()
{
return this.foreignKeyInfo;
}
public void setDataType(java.lang.String dataType)
{
this.dataTypeype=dataType;
}
public java.lang.String getDataType()
{
return this.dataType;
}
public void setSize(int size)
{
this.size=size;
}
public int getSize()
{
return this.size;
}

public void setScale(int Scale)
{
	this.scale=scale;
}
public int getScale()
{
	return this.scale;
}

}
