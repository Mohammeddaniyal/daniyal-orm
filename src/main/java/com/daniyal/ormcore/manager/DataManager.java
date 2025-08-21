package com.daniyal.ormcore.manager;
import java.sql.*;
import com.daniyal.ormcore.config.*;
import com.daniyal.ormcore.connection.*;
import com.daniyal.ormcore.exceptions.*;
import com.daniyal.ormcore.pojo.*;
import java.util.*;
public class DataManager
{
private static DataManager dataManager;
private ConfigLoader configLoader;
private Connection connection;
private Map<String,EntityMeta> entitiesMetaMap;
private DataManager() throws ORMException
{
this.configLoader=new ConfigLoader();
this.connection=null;
this.entitiesMetaMap=null;
populateDataStructures();
}
private void populateDataStructures() throws ORMException
{
this.entitiesMetaMap=EntityScanner.scanBasePackage(this.configLoader.getBasePackage());
}
public static DataManager getDataManager() throws ORMException
{
if(dataManager==null)
{
dataManager=new DataManager();
return dataManager;
}
return dataManager;
}

public void begin() throws ORMException
{
// make the connection
if(dataManager.connection==null)
{
dataManager.connection=ConnectionManager.getConnection(configLoader);
}
}

public void end() throws ORMException
{
try
{
if(dataManager.connection!=null) 
{
dataManager.connection.close();
dataManager.connection=null;
}
}catch(SQLException sqlException)
{
throw new ORMException(sqlException.getMessage());
}
}

public Connection getConnection()
{
return dataManager.connection;
}

}