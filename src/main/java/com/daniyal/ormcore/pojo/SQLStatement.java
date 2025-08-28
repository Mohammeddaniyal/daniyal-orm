package com.daniyal.ormcore.pojo;
import java.util.*;
public class SQLStatement
{
	private String insertSQL;
	private String updateSQL;
	private String deleteSQL;
	private String selectAllSQL;
	private Map<String,String> existsByColumnSQLMap;
	public SQLStatement(){}
	public SQLStatement(String insertSQL,String updateSQL,String deleteSQL,String selectAllSQL,Map<String,String> existsByColumnSQLMap)
	{
		this.insertSQL=insertSQL;
		this.updateSQL=updateSQL;
		this.deleteSQL=deleteSQL;
		this.selectAllSQL=selectAllSQL;
		this.existsByColumnSQLMap=existsByColumnSQLMap;
	}
	public void setInsertSQL(String insertSQL)
	{
		this.insertSQL=insertSQL;
	}
	public void setUpdateSQL(String updateSQL)
	{
		this.updateSQL=updateSQL;
	}
	public void setDeleteSQL(String deleteSQL)
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
	public String getDeleteSQL()
	{
		return this.deleteSQL;
	}
	public String getSelectAllSQL()
	{
		return this.selectAllSQL;
	}
	public void setExistsByColumnSQLMap(Map<String,String> existsByColumnSQLMap)
	{
		this.existsByColumnSQLMap=existsByColumnSQLMap;
	}
	public Map<String,String> getExistsByColumnSQLMap()
	{
		return this.existsByColumnSQLMap;
	}
}