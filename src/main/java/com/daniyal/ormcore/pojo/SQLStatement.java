package com.daniyal.ormcore.pojo;
public class SQLStatement
{
	private String insert;
	private String update;
	private String delete;
	private String getAll;
	public void setInsert(String insert)
	{
		this.insert=insert;
	}
	public void setUpdate(String update)
	{
		this.update=update;
	}
	public void setDelete(String delete)
	{
		this.delete=delete;
	}
	public void setGetAll(String getAll)
	{
		this.getAll=getAll;
	}
	public String getInsert()
	{
		return this.insert;
	}
	public String getUpdate()
	{
		return this.update;
	}
	public String getDelete()
	{
		return this.delete;
	}
	public String getGetAll()
	{
		return this.getAll;
	}
}