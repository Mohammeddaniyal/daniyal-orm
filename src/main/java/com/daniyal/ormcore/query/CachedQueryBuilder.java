package com.daniyal.ormcore.query;
import com.daniyal.ormcore.metadata.*;
import java.lang.reflect.*;
import com.daniyal.ormcore.exceptions.*;
import java.util.*;

public class CachedQueryBuilder<T> implements Queryable<T>
{
	private List<T> list;
	private Class entityClass;
	private Constructor<T> constructor;
	private Map<String,FieldMetaData> fieldMetaDataMap;
	private String currentField=null;
	private List<Condition> conditions=new ArrayList<>();
	public enum OPERATION{EQ,GT,ET,GE,LE,LIKE};
	
	public CachedQueryBuilder(Class entityClass,Constructor<T> constructor, Map<String,FieldMetaData> fieldMetaDataMap,List<T> list)
	{
		this.entityClass=entityClass;
		this.constructor=constructor;
		this.fieldMetaDataMap=fieldMetaDataMap;
		this.list=list;
	}
	public List<T> list() throws ORMException
	{
		return cloneList();
	}
	private List<T> cloneList()throws ORMException	
	{
	List<T> clonedList=new ArrayList<>();
	for(T instance:list)
	{
		clonedList.add(cloneInstance(instance));
	}
	return clonedList;
	}
private T cloneInstance(T instance) throws ORMException
{
	try{
		T cloneInstance=this.constructor.newInstance();
	for(Map.Entry<String,FieldMetaData> entry:this.fieldMetaDataMap.entrySet())
	{
		FieldMetaData fieldMetaData=entry.getValue();
		Field field=fieldMetaData.getField();
		Object value=field.get(instance);
		field.set(cloneInstance,value);
	}
	return cloneInstance;
	}catch(IllegalAccessException | InstantiationException | InvocationTargetException ex)
	{
		throw new ORMException(ex.getMessage());
	}
	
}
	@Deprecated
	public Condition<T> where(String column)
	{
		throw new UnsupportedOperationException("Not yet implemented"); 
	}
	@Deprecated
	public Condition<T> or(String column)
	{
		throw new UnsupportedOperationException("Not yet implemented");
	}
	@Deprecated
	public Condition<T> and(String column)
	{
		throw new UnsupportedOperationException("Not yet implemented"); 
	}
	@Deprecated
	public void addCondition(String column,Object value)
	{
		throw new UnsupportedOperationException("Not yet implemented"); 
	}
}