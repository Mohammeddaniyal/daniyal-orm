package com.daniyal.ormcore.query;
public class Condition
{
	private final QueryBuilder parent;
	private final String column;
	
	Condition(QueryBuilder parent,String column)
	{
		this.parent=parent;
		this.column=column;
	}
	public QueryBuilder eq(Object value)
	{
		parent.addCondition(column+" =?",value);
		return parent;
	}
	public QueryBuilder gt(Object value)
	{
		parent.addCondition(column+" > ?",value);
		return parent;
	}
	public QueryBuilder lt(Object value)
	{
		parent.addCondition(column+" < ?",value);
		return parent;
	}
	public QueryBuilder ge(Object value)
	{
		parent.addCondition(column+" >=?",value);
		return parent;
	}
	public QueryBuilder le(Object value)
	{
		parent.addCondition(column+" <=?",value);
		return parent;
	}
	public QueryBuilder like(Object value)
	{
		parent.addCondition(column+" LIKE ?",value);
		return parent;
	}
}