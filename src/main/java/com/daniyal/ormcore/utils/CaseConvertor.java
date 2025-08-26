package com.daniyal.ormcore.utils;
public class CaseConvertor
{
public static String toSnakeCase(String camelCase)
{
if(camelCase==null || camelCase.isEmpty()) return camelCase;
camelCase=camelCase.trim();
StringBuilder snakeCaseBuilder=new StringBuilder();
snakeCaseBuilder.append(Character.toLowerCase(camelCase.charAt(0)));
char c;
for(int i=1;i<camelCase.length();++i)
{
c=camelCase.charAt(i);
if(Character.isUpperCase(c))
{
snakeCaseBuilder.append("_");
snakeCaseBuilder.append(Character.toLowerCase(c));
}
else
{
snakeCaseBuilder.append(c);
}
}
return snakeCaseBuilder.toString();
}
public static String toCamelCase(String snakeCase)
{
if(snakeCase==null || snakeCase.isEmpty() || !snakeCase.contains("_")) return snakeCase;
snakeCase=snakeCase.trim();
StringBuilder camelCaseBuilder=new StringBuilder();
boolean isNextCharCapital=false;
char c;
for(int i=0;i<snakeCase.length();++i)
{
c=snakeCase.charAt(i);
if(c=='_')
{
isNextCharCapital=true;
}
else
{
if(isNextCharCapital)
{
camelCaseBuilder.append(Character.toUpperCase(c));
}
else
{
camelCaseBuilder.append(c);
}
}
isNextCharCapital=false;
} // loop ends
return camelCaseBuilder.toString();
} //toCamelCase
}// class ends