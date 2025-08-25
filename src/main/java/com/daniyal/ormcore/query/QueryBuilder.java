package com.daniyal.ormcore.query;
import com.daniyal.ormcore.pojo.*;
import com.daniyal.ormcore.exceptions.*;
import com.daniyal.ormcore.validation.*;
import java.util.*;
import java.lang.reflect.*;
import java.sql.*;
public class QueryBuilder
{
	private final Connection connection;
	private final String tableName;
	private final Map<String,FieldMetaData> fieldMetaDataMap;
	private final Map<String,ColumnMetaData> columnMetaDataMap;
	private final Class entityClass;
	private final Object entityInstance;
	private final Constructor entityNoArgConstructor;
	private final List<String> conditions;
	private final List<Object> params;
	public QueryBuilder(Connection connection,Class entityClass,Constructor entityNoArgConstructor,String tableName)
	{
		this.connection=connection;
		this.entityClass=entityClass;
		this.entityNoArgConstructor;
		this.tableName=tableName;
		this.conditions=new ArrayList<>();
		this.params=new ArrayList<>();
		this.fieldMetaDataMap=null;
		this.columnMetaDataMap=null;
		this.entityInstance=null;
	}
	public QueryBuilder(Object entityInstance,String tableName,Map<String,FieldMetaData> fieldMetaDataDataMap,Map<String,ColumnMetaData> columnMetaDataMap)
	{
		this.entityInstance=entityInstance;
		this.tableName=tableName;
		this.fieldMetaDataMap=fieldMetaDataDataMap;
		this.columnMetaDataMap=columnMetaDataMap;
		this.conditions=null;
		this.params=null;
		this.connection=null;
		this.entityClass=null;
		this.entityNoArgConstructor=null;
		}
	private void processFields(FieldProcessor processor,List<String> columns,List<Object> params,StringBuilder placeholders) throws ORMException
	{
		
			FieldMetaData fieldMetaData;
			Field field;
			Object rawValue=null;
			Object validatedValue;
			ColumnMetaData columnMetaData;
			for(Map.Entry<String,FieldMetaData> entry:fieldMetaDataMap.entrySet())
			{
				fieldMetaData=entry.getValue();
				field=fieldMetaData.getField();
				try
				{
				rawValue=field.get(this.entityInstance);
				}catch(IllegalAccessException exception)
				{
					throw new ORMException("Cannot read value of field '" + field.getName() +"' in entity '" + entityInstance.getClass().getSimpleName() +"'. Ensure the field is accessible (e.g., use @Column and allow access).");		
				}
				validatedValue=EntityValidator.validateAndConvert(rawValue,fieldMetaData,columnMetaDataMap.get(fieldMetaData.getColumnName()));
				processor.process(fieldMetaData,validatedValue,columns,params,placeholders);
			}
	}
	public Query buildInsertQuery(FieldMetaData fieldMetaDataWithAutoIncrementOnField) throws ORMException
	{
		List<String> columns=new ArrayList<>();
		List<Object> params=new ArrayList<>();
		StringBuilder placeholders=new StringBuilder();
		
		FieldProcessor insertProcessor=(fieldMetaData,validatedValue,cols,paramList,ph)->{
			if(fieldMetaData.isAutoIncrement()) 
			{
				fieldMetaDataWithAutoIncrementOnField.setField(fieldMetaData.getField());
				return;
			}
			columns.add(fieldMetaData.getColumnName());
			params.add(validatedValue);
			if(ph.length()>0) 
				ph.append(",");
			ph.append("?");
		};
		processFields(insertProcessor,columns,params,placeholders);
		
		String sql="INSERT INTO "+this.tableName+" ("+String.join(",",columns)+") VALUES ("+placeholders.toString()+ ")";
		return new Query(sql,params);
	}
	public Query buildUpdateQuery() throws ORMException
	{
		List<String> setClauses=new ArrayList<>();
		List<Object> params=new ArrayList<>();
		StringBuilder dummy=new StringBuilder();
		final String[] primaryKeyColumn={null};
		final Object[] primaryKeyValue={null};
		FieldProcessor updateProcessor=(fieldMetaData,validatedValue,cols,paramList,ph)->{
			if(fieldMetaData.isPrimaryKey())
			{
				primaryKeyColumn[0]=fieldMetaData.getColumnName();
				primaryKeyValue[0]=validatedValue;
				return;
			}
			setClauses.add(fieldMetaData.getColumnName()+"=?");
			params.add(validatedValue);
		};
		processFields(updateProcessor,setClauses,params,dummy);
		String sql="UPDATE "+tableName+" SET "+String.join(",",setClauses)+" WHERE "+primaryKeyColumn[0]+"=?";
		params.add(primaryKeyValue[0]);
		return new Query(sql,params);
	}
	public Query buildDeleteQuery()throws ORMException
	{
		final String[] primaryKeyColumn={null};
		final Object[] primaryKeyValue={null};
		FieldProcessor deleteProcessor=(fieldMetaData,validatedValue,cols,paramList,ph)->{
			if(fieldMetaData.isPrimaryKey())
			{
				primaryKeyColumn[0]=fieldMetaData.getColumnName();
				primaryKeyValue[0]=validatedValue;
			}
		};
		processFields(deleteProcessor,null,null,null);
		String sql="DELETE FROM "+this.tableName+" WHERE "+primaryKeyColumn[0]+"=?";
		List<Object> params=Collections.singletonList(primaryKeyValue[0]);
		return new Query(sql,params);
	}
	public void addCondition(String condition,Object value)
	{
		this.conditions.add(condition);
		this.params.add(value);
	}
	public void addCondition(String condition)
	{
		this.conditions.add(condition);
	}
	public Condition where(String column)
	{
		this.conditions.add(" WHERE ");
		return new Condition(this,column);
	}
	public Condition or(String column)
	{
		this.conditions.add(" OR ");
		return new Condition(this,column);
	}
	public Condition and(String column)
	{
		this.conditions.add(" AND ");
		return new Condition(this,column);
	}
	public List<Object> list()
	{
		StringBuilder sqlBuilder=new StringBuilder("SELECT * FROM "+this.tableName);
		if(!this.conditions.isEmpty())
		{
			for(int i=0;i<this.conditions.size();i++)
			{
				sqlBuilder.append(this.conditions.get(i));
			}
		}
		System.out.println("SQL Query generated : "+sqlBuilder.toString());
		List<?> entityList=new ArrayList<>();
		Object entityInstance;
		try
		{
			Statement statement=connection.createStatement();
			ResultSet resultSet=statement.executeQuery(sqlBuilder.toString());
			while(resultSet.next())
			{
				
				entityInstance=this.entityClass.newInstance();
				
			}
			
		}catch(SQLException sqlException)
			{
					throw new ORMException(sqlException.getMessage());
			}
		return new ArrayList<>();
	}
}