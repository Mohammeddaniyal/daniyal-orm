package com.daniyal.ormcore.query;
public class Condition<T>
{
	private final QueryBuilder<T> parent;
	private final String column;
	
	Condition(QueryBuilder<T> parent,String column)
	{
		this.parent=parent;
		this.column=column;
	}
	public QueryBuilder<T> eq(Object value)
	{
		parent.addCondition(column+" =?",value);
		return parent;
	}
	public QueryBuilder<T> gt(Object value)
	{
		parent.addCondition(column+" > ?",value);
		return parent;
	}
	public QueryBuilder<T> lt(Object value)
	{
		parent.addCondition(column+" < ?",value);
		return parent;
	}
	public QueryBuilder<T> ge(Object value)
	{
		parent.addCondition(column+" >=?",value);
		return parent;
	}
	public QueryBuilder<T> le(Object value)
	{
		parent.addCondition(column+" <=?",value);
		return parent;
	}
	public QueryBuilder<T> like(Object value)
	{
		parent.addCondition(column+" LIKE ?",value);
		return parent;
	}
}