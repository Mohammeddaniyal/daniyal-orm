package com.daniyal.ormcore.manager;
import com.daniyal.ormcore.metadata.*;
import com.daniyal.ormcore.exceptions.*;
import com.daniyal.ormcore.query.QueryBuilder;
import java.util.*;
class SQLStatementGenerator
{
	public static Map<Class,SQLStatement> generateSQLStatementMap(Map<Class,EntityMetaData> entityMetaDataMap) throws ORMException
	{
		Map<Class,SQLStatement> sqlStatementMap=new HashMap<>();
		EntityMetaData entityMetaData;
		Class entityClass;
		Map<String,FieldMetaData> fieldMetaDataMap;
		QueryBuilder queryBuilder;
		SQLStatement sqlStatement;
		String insertSQL;
		String updateSQL;
		String deleteSQL;
		String selectAllSQL;
		Map<String,String> existsByColumnSQLMap;
		FieldMetaData primaryKeyFieldMetaData;
		String primaryKeyColumn;
		
		for(Map.Entry<Class,EntityMetaData> entityMetaDataEntry:entityMetaDataMap.entrySet())
		{
			entityMetaData=entityMetaDataEntry.getValue();
			if(entityMetaData.isView()) continue;
			entityClass=entityMetaDataEntry.getKey();
			primaryKeyFieldMetaData=entityMetaData.getPrimaryKeyFieldMetaData();
			primaryKeyColumn=primaryKeyFieldMetaData.getColumnName();
			queryBuilder=new QueryBuilder(entityMetaData.getTableName(),entityMetaData.getFieldMetaDataMap());
			insertSQL=queryBuilder.buildInsertSQL();
			updateSQL=queryBuilder.buildUpdateSQL(primaryKeyColumn);
			deleteSQL=queryBuilder.buildDeleteSQL(primaryKeyColumn);
			selectAllSQL=queryBuilder.buildSelectAllSQL();
			existsByColumnSQLMap=queryBuilder.buildExistsByColumnSQLMap();
			sqlStatement=new SQLStatement(insertSQL,updateSQL,deleteSQL,selectAllSQL,existsByColumnSQLMap);
			sqlStatementMap.put(entityClass,sqlStatement);
		}
		return sqlStatementMap;
	}
}