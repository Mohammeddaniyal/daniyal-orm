import java.sql.*;
import java.io.*;
class eg2psp
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
ResultSetMetaData md=rs.getMetaData();
int n=md.getColumnCount();
String cat = rs.getString("TABLE_CAT");
String name = rs.getString("TABLE_NAME");
String type = rs.getString("TABLE_TYPE");
System.out.println(cat + " :: " + name + " (" + type + ")");
System.out.println("Columns returned by getTables():");
for (int i = 1;i <= n;i++) 
{
System.out.println(i + " -> " + md.getColumnName(i));
}

}
rs.close();
connection.close();
}catch(SQLException ex)
{
System.out.println(ex.getMessage());
}
}
}