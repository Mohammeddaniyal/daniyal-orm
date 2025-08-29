package com.daniyal.ormcore.query;
public interface Queryable<T> 
{
	java.util.List<T> list() throws com.daniyal.ormcore.exceptions.ORMException;
	public void addCondition(String column,Object value);
	public Condition<T> where(String column);
	public Condition<T> or(String column);
	public Condition<T> and(String column);
}