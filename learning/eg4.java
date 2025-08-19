import java.sql.*;
import java.io.*;
class eg4psp
{
public static void main(String []args)
{
try
{
Connection connection=DAOConnection.getConnection();

System.out.println("Connected as: " + connection.getMetaData().getUserName());

DatabaseMetaData meta=connection.getMetaData();
ResultSet rs=meta.getTables(connection.getCatalog(),null,"%",new String[]{"TABLE"});
String anyTable=null;
if(rs.next())
{
anyTable=rs.getString("TABLE_NAME");
}
if(anyTable!=null) 
{
ResultSet t=meta.getColumns(connection.getCatalog(),null,anyTable,"%");
ResultSetMetaData md=t.getMetaData();
int n=md.getColumnCount();
System.out.println("Columns returned by getColumns() for table " + anyTable + ":");
for(int i=1;i<=n;i++) {
System.out.println(i + " -> " + md.getColumnName(i));
}

}


connection.close();
}catch(SQLException ex)
{
System.out.println(ex.getMessage());
}
}
}