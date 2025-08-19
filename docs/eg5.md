import java.sql.*;
import java.io.*;
class eg5psp
{
public static void main(String []args)
{
try
{
Connection connection=DAOConnection.getConnection();

System.out.println("Connected as: " + connection.getMetaData().getUserName());

DatabaseMetaData meta=connection.getMetaData();
ResultSet rs=meta.getTables(connection.getCatalog(),null,"%",new String[]{"TABLE"});

while(rs.next())
{
String tableName=rs.getString("TABLE_NAME");
System.out.println("Table : "+tableName);

ResultSet columns=meta.getColumns(connection.getCatalog(),null,tableName,"%");

while(columns.next())
{
String columnName=columns.getString("COLUMN_NAME");
String type=columns.getString("TYPE_NAME");
String size=columns.getString("COLUMN_SIZE");
String isNull=columns.getString("IS_NULLABLE");
String autoIncrement=columns.getString("IS_AUTOINCREMENT");
System.out.println(" - " + columnName + " : " + type + "(" + size + ")" +" | nullable=" + isNull +" | autoincrement=" + autoIncrement);
}


}


connection.close();
}catch(SQLException ex)
{
System.out.println(ex.getMessage());
}
}
}