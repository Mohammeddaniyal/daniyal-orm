package com.daniyal.ormcore.utils;
public class TypeMapper
{
public static String  getFieldType(String type,int size)
{
if(type.equalsIgnoreCase("INT")) return "int";
if(type.equalsIgnoreCase("BIGINT")) return "long";
if(type.equalsIgnoreCase("TINYINT") && size==1) return "boolean";
if(type.equalsIgnoreCase("TINYINT") && size>1) return "byte";
if(type.equalsIgnoreCase("SMALLINT")) return "short";
if(type.equalsIgnoreCase("FLOAT")) return "float";
if(type.equalsIgnoreCase("double")) return "double";
if(type.equalsIgnoreCase("DECIMAL")) return "BigDecimal";
if(type.equalsIgnoreCase("NUMERIC")) return "BigDecimal";
if(type.equalsIgnoreCase("CHAR") && size==1) return "char";
if(type.equalsIgnoreCase("CHAR") || type.equalsIgnoreCase("VARCHAR") || type.equalsIgnoreCase("TEXT")) return "String";
if(type.equalsIgnoreCase("DATE")) return "Date";
//if(type.equalsIgnoreCase("TIME")) return "Time";
//if(type.equalsIgnoreCase("TIMESTAMP")  || type.equalsIgnoreCase("DATETIME")) return "TimeStamp";
return "Object";
}
public static boolean isCompatible(String entityFieldType,String tableFieldType,int size)
{
switch(tableFieldType.toUpperCase())
{
	case "INT":
	return entityFieldType.equals("int") || entityFieldType.equals("java.lang.Integer");
	case "BIGINT"
	return entityFieldType.equals("long") || entityFieldType.equals("java.lang.Long");
	case "DOUBLE"
	return entityFieldType.equals("double") || entityFieldType.equals("java.lang.Double");
	case "FLOAT":
	return entityFieldType.equals("float") || entityFieldType.equals("java.lang.Float");
	case "BOOLEAN":
	caee "BIT":
	return entityFieldType.equals("boolean") || entityFieldType.equals("java.lang.Boolean");
	
	case "VARCHAR":
	case "TEXT":
	return entityFieldType.equals("java.lang.String");
	case "CHAR":
	if(size==1)
	{
		return entityFieldType.equals("char") || entityFieldType.equals("java.lang.Character");
	}
	else
	{
		return entityFieldType.equals("java.lang.String");
	}
	case "DATE":
	return entityFieldType.equals("java.util.Date") || entityFieldType.equals("java.sql.Date");
	case "TIMESTAMP":
	return entityFieldType.equals("java.sql.TimeStamp") || entityFieldType.equals("java.util.Date");
	case "DECIMAL":
	case "NUMERIC":
	return entityFieldType.equals("double") || entityFieldType.equals("java.lang.Double") || entityFieldType.equals("java.math.BigDecimal");
	default:
	return false;
}
}
}