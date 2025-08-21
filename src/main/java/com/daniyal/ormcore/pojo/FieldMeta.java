package com.daniyal.ormcore.pojo;
import java.lang.reflect.*;
public class FieldMeta
{
private Field field;
private String columnName;
private Method setterMethod;
private Method getterMethod;
private boolean isPrimaryKey;
private boolean isAutoIncrement;
private boolean isForeignKey;
private String parentTableName;
private String parentColumnName;
public FieldMeta()
{
this.field=null;
this.columnName="";
this.setterMethod=null;
this.getterMethod=null;
this.isPrimaryKey=false;
this.isAutoIncrement=false;
this.isForeignKey=false;
this.parentTableName="";
this.parentColumnName="";
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
public void setIsForeignKey(boolean isForeignKey)
{
this.isForeignKey=isForeignKey;
}
public boolean getIsForeignKey()
{
return this.isForeignKey;
}
public void setParentTableName(java.lang.String parentTableName)
{
this.parentTableName=parentTableName;
}
public java.lang.String getParentTableName()
{
return this.parentTableName;
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