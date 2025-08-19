import java.sql.*;
import java.io.*;
class eg1psp
{
public static void main(String []args)
{
try
{
Connection connection=DAOConnection.getConnection();
System.out.println("Connected as: " + connection.getMetaData().getUserName());
System.out.println("In mysql catalog or schema or database are all same thing");
System.out.println("Catalog/Schema/Database : "+connection.getCatalog());
connection.close();
}catch(SQLException ex)
{
System.out.println(ex.getMessage());
}
}
}