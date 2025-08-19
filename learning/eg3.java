import java.sql.*;
import java.io.*;
class eg3psp
{
public static void main(String []args)
{
try
{
Connection connection=DAOConnection.getConnection();


DatabaseMetaData meta=connection.getMetaData();
ResultSet rs=meta.getTables(connection.getCatalog(),null,"%",new String[]{"TABLE"});

while(rs.next())
{
String tableName=rs.getString("TABLE_NAME");
System.out.println("Table : "+tableName);
}
connection.close();
}catch(SQLException ex)
{
System.out.println(ex.getMessage());
}
}
}