package com.daniyal.ormcore.query;
public interface Condition<T>
{
	public Queryable<T> eq(Object value);
	public Queryable<T> gt(Object value);
	public Queryable<T> lt(Object value);
	public Queryable<T> ge(Object value);
	public Queryable<T> le(Object value);
	public Queryable<T> like(Object value);
}