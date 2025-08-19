import java.sql.*;
import java.io.*;
class DAOConnection
{
private DAOConnection(){}

public static Connection getConnection()
{
Connection connection=null;
String driver="";
String connectionURL="";
String username="";
String password="";

try
{
File file=new File("conf.json");
RandomAccessFile randomAccessFile=new RandomAccessFile(file,"r");
if(randomAccessFile==null)
{
System.out.println("File not found\n");
return connection	;
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
driver=value;
}else if(key.equalsIgnoreCase("connection-url"))
{
connectionURL=value;
}else if(key.equalsIgnoreCase("username"))
{
username=value;
}else if(key.equalsIgnoreCase("password"))
{
password=value;
}
}
}catch(IOException ex)
{
System.out.println(ex.getMessage());
}
try
{
Class.forName(driver);
connection=DriverManager.getConnection(connectionURL,username,password);
}catch(ClassNotFoundException | SQLException ex)
{
System.out.println(ex.getMessage());
}
return connection;
}
}
