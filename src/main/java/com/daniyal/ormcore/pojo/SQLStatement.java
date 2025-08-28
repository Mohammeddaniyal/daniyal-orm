package com.daniyal.ormcore.pojo;
public class SQLStatement
{
	private String insertSQL;
	private String updateSQL;
	private String deleteSQL;
	private String getAllSQL;
	public void setInsert(String insertSQL)
	{
		this.insertSQL=insertSQL;
	}
	public void setUpdateSQL(String updateSQL)
	{
		this.updateSQL=updateSQL;
	}
	public void setDelete(String deleteSQL)
	{
		this.deleteSQL=deleteSQL;
	}
	public void setGetAllSQL(String getAllSQL)
	{
		this.getAllSQL=getAllSQL;
	}
	public String getInsert()
	{
		return this.insertSQL;
	}
	public String getUpdateSQL()
	{
		return this.updateSQL;
	}
	public String getDelete()
	{
		return this.deleteSQL;
	}
	public String getGetAllSQL()
	{
		return this.getAllSQL;
	}
}