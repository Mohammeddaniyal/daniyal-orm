package com.daniyal.ormcore.manager;
import java.lang.reflect.*;
import java.util.*;
import com.daniyal.ormcore.pojo.*;
import java.io.*;
import java.net.URL;
class EntityScanner
{
public static Map<String,EntityMeta> scanBasePackage(String basePackage) throws ORMException
{
try
{
Map<String,EntityMeta> entitiesMetaMap=new HashMap<>();
ClassLoader classLoader=Thread.currentThread().getContextClassLoader();
URL resource=classLoader.getResource(basePackage);
if(resource==null)
{
throw new ORMException("Invalid base-package : "+basePackage);
}
String fullPath=resource.getPath();
System.out.println("Full path : " +fullPath);
return entitiesMetaMap;
}catch(IOException ioException)
{
}
}
}
