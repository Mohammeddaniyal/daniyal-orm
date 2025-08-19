import java.sql.*;
import java.io.*;
class eg6psp
{
public static void main(String []args)
{
try
{
Connection connection=DAOConnection.getConnection();

DatabaseMetaData meta=connection.getMetaData();
ResultSet tables=meta.getTables(connection.getCatalog(),null,"%",new String[]{"TABLE"});



while(tables.next())
{
String tableName=tables.getString("TABLE_NAME");
//System.out.println("Table : "+tableName);
System.out.println("\n=== " + tableName + " ===");
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
columns.close();

ResultSet k=meta.getPrimaryKeys(connection.getCatalog(),null,tableName);
while(k.next())
{
System.out.println(" PK: " + k.getString("COLUMN_NAME"));
}
k.close();
k=meta.getImportedKeys(connection.getCatalog(),null,tableName);
while(k.next())
{
String fkCol= k.getString("FKCOLUMN_NAME");
String pkTbl=k.getString("PKTABLE_NAME");
String pkCol=k.getString("PKCOLUMN_NAME");
System.out.println(" FK: " + fkCol + " -> " + pkTbl + "(" + pkCol + ")");
}

k.close();

ResultSet idx=meta.getIndexInfo(connection.getCatalog(), null, tableName, false, false);
while(idx.next()) 
{
String idxName = idx.getString("INDEX_NAME");
String colName = idx.getString("COLUMN_NAME");
boolean nonUnique = idx.getBoolean("NON_UNIQUE");
System.out.println(" IDX: " + idxName + " on " + colName + " | unique=" + !nonUnique);
}
idx.close();
}

tables.close();
connection.close();
}catch(SQLException ex)
{
System.out.println(ex.getMessage());
}
}
}