package com.daniyal.ormcore.pojo;
import java.lang.reflect.Constructor;
import java.util.*;
public class EntityMetaData
{
private Class entityClass;
private Constructor entityNoArgConstructor;
private String tableName;
private FieldMetaData primaryKeyFieldMetaData;
private FieldMetaData autoIncrementFieldMetaData;
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

public void setPrimaryKeyFieldMetaData(FieldMetaData primaryKeyFieldMetaData)
{
	this.primaryKeyFieldMetaData=primaryKeyFieldMetaData;
}
public void setAutoIncrementFieldMetaData(FieldMetaData autoIncrementFieldMetaData)
{
	this.autoIncrementFieldMetaData=autoIncrementFieldMetaData;
}
public FieldMetaData getPrimaryKeyFieldMetaData()
{
	return this.primaryKeyFieldMetaData;
}
public FieldMetaData getAutoIncrementFieldMetaData()
{
	return this.autoIncrementFieldMetaData;
}
public Map<String,FieldMetaData> getFieldMetaDataMap()
{
return this.fieldMetaDataMap;
}
public void setEntityNoArgConstructor(Constructor entityNoArgConstructor)
{
	this.entityNoArgConstructor=entityNoArgConstructor;
}
public Constructor getEntityNoArgConstructor()
{
	return entityNoArgConstructor;
}
}