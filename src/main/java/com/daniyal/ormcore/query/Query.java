package com.daniyal.ormcore.query;
import com.daniyal.ormcore.pojo.*;
import com.daniyal.ormcore.exceptions.*;
import java.util.*;
public class Query
{
	private String sql;
	private List<Object> parameters;
	public Query()
	{
		this.sql="";
		this.parameters=null;
	}
	public Query(String sql,List<Object> parameters)
	{
		this.sql=sql;
		this.parameters=parameters;
	}
	public void setSQL(String sql)
	{
		this.sql=sql;
	}
	public void setParameters(List<Object> parameters)
	{
		this.parameters=parameters;
	}
	public String getSQL()
	{
		return this.sql;
	}
	public List<Object> getParameters()
	{
		return this.parameters;
	}
}