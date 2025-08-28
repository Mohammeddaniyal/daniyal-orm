package com.daniyal.ormcore.manager;
import com.daniyal.ormcore.pojo.*;
import java.util.*;
class SQLStatementGenerator
{
	public static Map<Class,SQLStatement> generateSQLStatementMap(Map<Class,EntityMetaData> entityMetaDataMap)
	{
		Map<Class,SQLStatement> sqlStatementMap=new HashMap<>();
		EntityMetaData entityMetaData;
		Class entityClass;
		FieldMetaDataMap<String,FieldMetaData> fieldMetaDataMap;
		QueryBuilder queryBuilder;
		SQLStatement sqlStatement;
		String insertSQL;
		for(Map.Entry<Class,EntityMetaData> entityMetaDataEntry:entityMetaDataMap)
		{
			entityMetaData=entityMetaDataEntry.getValue();
			entityClass=entityMetaDataEntry.getKey();
			queryBuilder=new QueryBuilder(entityMetaData.getTableName(),entityMetaData.getFieldMetaDataMap());
		}
		return sqlStatementMap;
	}
}