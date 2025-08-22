package com.daniyal.ormcore.pojo;
import java.util.*;
public class EntityMeta
{
private Class entityClass;
private String tableName;
private Map<String,FieldMeta> fields;

public EntityMeta()
{
this.entityClass=null;
this.tableName="";
this.fields=null;
}

public void setEntityClass(Class entityClass)
{
this.entityClass=entityClass;
}
public void setTableName(String tableName)
{
this.tableName=tableName;
}
public void setFields(Map<String,FieldMeta> fields)
{
this.fields=fields;
}
public Class getEntityClass()
{
return this.entityClass;
}
public String getTableName()
{
return this.tableName;
}
public Map<String,FieldMeta> getFields()
{
return this.fields;
}
}