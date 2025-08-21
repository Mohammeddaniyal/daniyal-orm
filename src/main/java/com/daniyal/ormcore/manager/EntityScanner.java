package com.daniyal.ormcore.manager;
import com.daniyal.ormcore.pojo.*;
import com.daniyal.ormcore.exceptions.*;
import java.lang.reflect.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.util.jar.*;
class EntityScanner
{
public static Map<String,EntityMeta> scanBasePackage(String basePackage) throws ORMException
{
Map<String,EntityMeta> entitiesMetaMap=new HashMap<>();
ClassLoader classLoader=Thread.currentThread().getContextClassLoader();
System.out.println("Base package "+basePackage);

try
{
String path=basePackage.replace(".",File.separator);
System.out.println("Path :"+path);
// Get all resources(directory or jar) for basePackage
Enumeration<URL> resources=classLoader.getResources(path);
URL resource;

// Loop over every resource(can either be folder or jar)
while(resources.hasMoreElements())
{
resource=resources.nextElement();
System.out.println("Found resource : "+resource);

// if it's a folder
if(resource.getProtocol().equals("file"))
{
File directory=new File(resource.toURI());
for(File file:directory.listFiles())
{
if(file.getName().endsWith(".class"))
{
String className=basePackage+"."+file.getName().replace(".class","");
System.out.println("Discovered class : "+className);	
}
}// for loop ends on directory files list
}// folder on disk condition ends
else if(resource.getProtocol().equals("jar"))
{
// this how path looks 
//jar:file:/C:/libs/myLib.jar!/com/example/MyClass.class
// because of this below line it will become 
// C:/libs/myLib.jar
String jarPath=resource.getPath().substring("file:/".length(),resource.getPath().indexOf("!"));
JarFile jarFile=new JarFile(jarPath);

// going through every entry in the jar file
for(JarEntry entry:java.util.Collections.list(jarFile.entries()))
{
String name=entry.getName();

// only picking .class files inside our package 
if(name.startsWith(path) && name.endsWith(".class"))
{
String className=name.replace("/",".").replace(".class","");
System.out.println("Discovered class : "+className);	
}
} // for loop on jar entries ends here

}// jar part ends here

}// loop ends on resources
}catch(IOException | URISyntaxException exception)
{
throw new ORMException(exception.getMessage());
}
return entitiesMetaMap;

}// function ends


}

