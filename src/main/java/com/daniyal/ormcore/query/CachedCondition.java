package com.daniyal.ormcore.query;
class CachedCondition<T> implements Condition
{
	private final Queryable<T> parent;
	private final String column;
	
	CachedCondition(Queryable<T> parent,String column)
	{
		this.parent=parent;
		this.column=column;
	}
	public Queryable<T> eq(Object value)
	{
		parent.addCondition(column+" =?",value);
		return parent;
	}
	public Queryable<T> gt(Object value)
	{
		parent.addCondition(column+" > ?",value);
		return parent;
	}
	public Queryable<T> lt(Object value)
	{
		parent.addCondition(column+" < ?",value);
		return parent;
	}
	public Queryable<T> ge(Object value)
	{
		parent.addCondition(column+" >=?",value);
		return parent;
	}
	public Queryable<T> le(Object value)
	{
		parent.addCondition(column+" <=?",value);
		return parent;
	}
	public Queryable<T> like(Object value)
	{
		parent.addCondition(column+" LIKE ?",value);
		return parent;
	}
}