package com.daniyal.ormcore.pojo;
import java.lang.reflect.*;
public class FieldMetaData
{
private Field field;
private String columnName;
private Method setterMethod;
private Method getterMethod;
private boolean isPrimaryKey;
private boolean isAutoIncrement;
private boolean isForeignKey;
private ForeignKeyInfo foreignKeyInfo;
public FieldMetaData()
{
this.field=null;
this.columnName="";
this.setterMethod=null;
this.getterMethod=null;
this.isPrimaryKey=false;
this.isAutoIncrement=false;
this.isForeignKey=false;
this.foreignKeyInfo=null;
}
public void setField(java.lang.reflect.Field field)
{
this.field=field;
}
public java.lang.reflect.Field getField()
{
return this.field;
}
public void setColumnName(java.lang.String columnName)
{
this.columnName=columnName;
}
public java.lang.String getColumnName()
{
return this.columnName;
}
public void setSetterMethod(java.lang.reflect.Method setterMethod)
{
this.setterMethod=setterMethod;
}
public java.lang.reflect.Method getSetterMethod()
{
return this.setterMethod;
}
public void setGetterMethod(java.lang.reflect.Method getterMethod)
{
this.getterMethod=getterMethod;
}
public java.lang.reflect.Method getGetterMethod()
{
return this.getterMethod;
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
public void setForeignKey(boolean isForeignKey)
{
this.isForeignKey=isForeignKey;
}
public boolean isForeignKey()
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

}