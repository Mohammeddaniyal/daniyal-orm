package com.daniyal.ormcore.config;
import com.daniyal.ormcore.exceptions.*;
import java.io.*;
public class ConfigLoader
{
private String driver;
private String connectionURL;
private String username;
private String password;
private String basePackage;
public ConfigLoader() throws ORMException
{
try
{
File file=new File("conf.json");
RandomAccessFile randomAccessFile=new RandomAccessFile(file,"r");
if(randomAccessFile==null)
{
throw new ORMException("Configuration file not found\n");
}
String json="";
while(randomAccessFile.getFilePointer()<randomAccessFile.length())
{
json=json+randomAccessFile.readLine();
}

String content=json.trim().replaceAll("[\\{\\}\"]","");

String pairs[]=content.split(",");


for(String pair:pairs)
{
String[] kv=pair.split(":",2);
String key=kv[0];
String value=kv[1];
if(key.equalsIgnoreCase("jdbc-driver"))
{
this.driver=value;
}else if(key.equalsIgnoreCase("connection-url"))
{
this.connectionURL=value;
}else if(key.equalsIgnoreCase("username"))
{
this.username=value;
}else if(key.equalsIgnoreCase("password"))
{
this.password=value;
}else if(key.equalsIgnoreCase("base-package"))
{
this.basePackage=value.replace(".","/");
}
}
randomAccessFile.close();
}catch(IOException ex)
{
throw new ORMException(ex.getMessage());
}
}

public String getDriver()
{
return this.driver;
}
public String getConnectionURL()
{
return this.connectionURL;
}
public String getUsername()
{
return this.username;
}
public String getPassword()
{
return this.password;
}
public String getBasePackage()
{
return this.basePackage;
}
}