package com.daniyal.ormcore.pojo;
import java.util.*;
public class EntityMeta
{
private Class entityClass;
private String tableName;
private List<FieldMeta> fields;

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
public void setFields(List<FieldMeta> fields)
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
public List<FieldMeta> getFields()
{
return this.fields;
}
}