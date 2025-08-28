 package com.daniyal.ormcore.metadata;
public class ColumnMetaData
{
private String columnName;
private boolean isPrimaryKey;
private boolean isAutoIncrement;
private boolean isNullable;
private boolean isForeignKey;
private ForeignKeyMetaData foreignKeyMetaData;
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
this.foreignKeyMetaData=null;
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
public void setPrimaryKey(boolean isPrimaryKey)
{
this.isPrimaryKey=isPrimaryKey;
}
public boolean isPrimaryKey()
{
return this.isPrimaryKey;
}
public void setAutoIncrement(boolean isAutoIncrement)
{
this.isAutoIncrement=isAutoIncrement;
}
public boolean isAutoIncrement()
{
return this.isAutoIncrement;
}
public void setNullable(boolean isNullable)
{
this.isNullable=isNullable;
}
public boolean isNullable()
{
return this.isNullable;
}
public void setForeignKey(boolean isForeignKey)
{
this.isForeignKey=isForeignKey;
}
public boolean isForeignKey()
{
return this.isForeignKey;
}
public void setForeignKeyMetaData(ForeignKeyMetaData foreignKeyMetaData)
{
this.foreignKeyMetaData=foreignKeyMetaData;
}
public ForeignKeyMetaData getForeignKeyMetaData()
{
return this.foreignKeyMetaData;
}
public void setDataType(java.lang.String dataType)
{
this.dataType=dataType;
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
