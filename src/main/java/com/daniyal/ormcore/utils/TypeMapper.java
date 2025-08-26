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
public static boolean isCompatible(Class entityFieldType,String tableFieldType,int size)
{
switch(tableFieldType.toUpperCase())
{
	case "INT":
	return entityFieldType==int.class || entityFieldType.equals(Integer.class);
	case "BIGINT":
	return entityFieldType==long.class || entityFieldType.equals(Long.class);
	case "DOUBLE":
	return entityFieldType==double.class || entityFieldType.equals(Double.class);
	case "FLOAT":
	return entityFieldType==float.class || entityFieldType.equals(Float.class);
	case "BOOLEAN":
	case "BIT":
	return entityFieldType==boolean.class || entityFieldType.equals(Boolean.class);
	
	case "VARCHAR":
	case "TEXT":
	return entityFieldType.equals(String.class);
	case "CHAR":
	if(size==1)
	{
		return entityFieldType==char.class || entityFieldType.equals(Character.class);
	}
	else
	{
		return entityFieldType.equals(String.class);
	}
	case "DATE":
	return entityFieldType.equals(java.util.Date.class) || entityFieldType.equals(java.sql.Date.class);
	case "TIMESTAMP":
	return entityFieldType.equals(java.sql.Timestamp.class) || entityFieldType.equals(java.util.Date.class);
	case "DECIMAL":
	case "NUMERIC":
	return entityFieldType==double.class || entityFieldType.equals(java.lang.Double.class) || entityFieldType.equals(java.math.BigDecimal.class);
	default:
	return false;
}
}

public static String  getDefaultValue(String type)
{
if(type.equalsIgnoreCase("int")) return "0";
if(type.equalsIgnoreCase("long")) return "0";
if(type.equalsIgnoreCase("boolean")) return "false";
if(type.equalsIgnoreCase("byte") ) return "0";
if(type.equalsIgnoreCase("short")) return "0";
if(type.equalsIgnoreCase("float")) return "0.0f";
if(type.equalsIgnoreCase("double")) return "0.0";
if(type.equalsIgnoreCase("java.math.BigDecimal")) return "null";

if(type.equalsIgnoreCase("char")) return "' '";
if(type.equalsIgnoreCase("String")) return "\"\"";
if(type.equalsIgnoreCase("java.util.Date")) return "null";
return "null";
}

}