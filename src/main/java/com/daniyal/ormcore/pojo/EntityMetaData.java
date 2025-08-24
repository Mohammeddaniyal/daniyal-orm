package com.daniyal.ormcore.pojo;
import java.util.*;
public class EntityMetaData
{
private Class entityClass;
private String tableName;
private Map<String,FieldMetaData> fieldMetaDataMap;

public EntityMetaData()
{
this.entityClass=null;
this.tableName="";
this.fieldMetaDataMap=null;
}

public void setEntityClass(Class entityClass)
{
this.entityClass=entityClass;
}
public void setTableName(String tableName)
{
this.tableName=tableName;
}
public void setFieldMetaDataMap(Map<String,FieldMetaData> fieldMetaDataMap)
{
this.fieldMetaDataMap=fieldMetaDataMap;
}
public Class getEntityClass()
{
return this.entityClass;
}
public String getTableName()
{
return this.tableName;
}
public Map<String,FieldMetaData> getFieldMetaDataMap()
{
return this.fieldMetaDataMap;
}
}