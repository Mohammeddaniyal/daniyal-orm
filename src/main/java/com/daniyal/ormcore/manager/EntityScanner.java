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
String absolutePath=resource.getPath();
if(absolutePath==null)
{
throw new ORMException("Invalid base-package : "+basePackage);
}
absolutePath=absolutePath.substring(1);
try
{
File rootFolder=new File(absolutePath);
if(!rootFolder.exists() || !root.isDirectory())
{
throw new ORMException("Invalid base-package : "+basePackage);
}
loadMeta(rootFolder);
// now we have the absolutePath of the base package
// now we'll traverse on all directories and it's sub folder
// and identity each file and analyze classes using reflectionApi
// and classes with @Entity tag will become the part of datastructure
}catch(IOException ioException)
{
throw new ORMException(ioExeption.getMessage());
}

private void loadMeta(File rootFolder)
{

}


return entitiesMetaMap;
}
}

