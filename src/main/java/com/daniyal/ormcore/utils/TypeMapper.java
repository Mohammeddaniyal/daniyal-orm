package com.daniyal.ormcore.utils;
class TypeMapper
{
private static String  getFieldType(String type,int size)
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
}