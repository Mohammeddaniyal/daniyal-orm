package com.daniyal.ormcore.query;
import com.daniyal.ormcore.pojo.*;
import com.daniyal.ormcore.exceptions.*;
import com.daniyal.ormcore.validation.*;
import java.util.*;
import java.lang.reflect.*;
public class QueryBuilder
{
	private final String tableName;
	private final Map<String,FieldMetaData> fieldMetaDataDataMap;
	private final Map<String,ColumnMetaData> columnMetaDataMap;
	private final Object entityInstance;
	public QueryBuilder(Object entityInstance,String tableName,Map<String,FieldMetaData> fieldMetaDataDataMap,Map<String,ColumnMetaData> columnMetaDataMap)
	{
		this.entityInstance=entityInstance;
		this.tableName=tableName;
		this.fieldMetaDataDataMap=fieldMetaDataDataMap;
		this.columnMetaDataMap=columnMetaDataMap;
	}
	private void processFields(FieldProcessor processor,List<String> columns,List<Object> params,StringBuilder placeholders) throws ORMException
	{
		
			FieldMetaData fieldMetaData;
			Field field;
			Object rawValue=null;
			Object validatedValue;
			ColumnMetaData columnMetaData;
			for(Map.Entry<String,FieldMetaData> entry:fieldMetaDataDataMap.entrySet())
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
	public Query buildInsertQuery() throws ORMException
	{
		List<String> columns=new ArrayList<>();
		List<Object> params=new ArrayList<>();
		StringBuilder placeholders=new StringBuilder();
		FieldProcessor insertProcessor=(fieldMetaData,validatedValue,cols,paramList,ph)->{
			if(fieldMetaData.isAutoIncrement()) 
				return;
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
}