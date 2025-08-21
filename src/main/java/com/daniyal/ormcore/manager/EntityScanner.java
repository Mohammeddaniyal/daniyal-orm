package com.daniyal.ormcore.manager;
import com.daniyal.ormcore.pojo.*;
import com.daniyal.ormcore.exceptions.*;
import java.lang.reflect.*;
import java.util.*;
import java.io.*;
import java.net.URL;

class EntityScanner
{
public static Map<String,EntityMeta> scanBasePackage(String basePackage) throws ORMException
{
Map<String,EntityMeta> entitiesMetaMap=new HashMap<>();
ClassLoader classLoader=Thread.currentThread().getContextClassLoader();
System.out.println("Base package "+basePackage);
URL resource=classLoader.getResource(basePackage);
if(resource==null)
{
throw new ORMException("Invalid base-package : "+basePackage);
}
String fullPath=resource.getPath().substring(1);
System.out.println("Full path : " +fullPath);
return entitiesMetaMap;
}
}

