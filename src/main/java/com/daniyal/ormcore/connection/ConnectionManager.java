package com.daniyal.ormcore.connection;
import java.sql.*;
import java.io.*;
import com.daniyal.ormcore.exceptions.*;
import com.daniyal.ormcore.config.*;
public class ConnectionManager
{
private ConnectionManager(){}

public static Connection getConnection(ConfigLoader configLoader)
{
Connection connection=null;
String driver=configLoader.getDriver();
String connectionURL=configLoader.getConnectionURL();
String username=configLoader.getUsername();
String password=configLoader.getPassword();

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
