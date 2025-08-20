package com.daniyal.ormcore.pojo;
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
}