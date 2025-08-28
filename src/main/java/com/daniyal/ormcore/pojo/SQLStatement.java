package com.daniyal.ormcore.pojo;
public class SQLStatement
{
	private String insertSQL;
	private String updateSQL;
	private String deleteSQL;
	private String selectAllSQL;
	public SQLStatement(){}
	public SQLStatement(String insertSQL,String updateSQL,String deleteSQL,String selectAllSQL)
	{
		this.insertSQL=insertSQL;
		this.updateSQL=updateSQL;
		this.deleteSQL=deleteSQL;
		this.selectAllSQL=selectAllSQL;
	}
	public void setInsertSQL(String insertSQL)
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
	public void setSelectAllSQL(String selectAllSQL)
	{
		this.selectAllSQL=selectAllSQL;
	}
	public String getInsertSQL()
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
	public String getSelectAllSQL()
	{
		return this.selectAllSQL;
	}
}