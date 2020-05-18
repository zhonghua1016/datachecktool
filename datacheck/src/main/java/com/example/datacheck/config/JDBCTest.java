package com.example.datacheck.config;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;

/**
 * created by yuanjunjie on 2020/5/9 11:57 AM
 */
public class JDBCTest {

    public static void tt(String[] args) throws Exception {
        Connection conn = null;
        try {
            //1. JDBC连接MYSQL的代码很标准。
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:mysql://localhost/poc?user=root&password=zhonghua111002");

            //2. 下面就是获取表的信息。
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tableRet = metaData.getTables(null, "%",
                    "%", new String[]{"TABLE"});
            /*其中"%"就是表示*的意思，也就是任意所有的意思。其中m_TableName就是要获取的数据表的名字，如果想获取所有的表的名字，就可以使用"%"来作为参数了。*/

            //3. 提取表的名字。
            while (tableRet.next()) {
            /*通过getString("TABLE_NAME")，就可以获取表的名字了。
从这里可以看出，前面通过getTables的接口的返回，JDBC是将其所有的结果，保存在一个类似table的内存结构中，
而其中TABLE_NAME这个名字的字段就是每个表的名字。*/
                String tableName = tableRet.getString("TABLE_NAME");
                System.out.println("\n-----------------------------------");
                System.out.println("TABLE_NAME:"+tableName);

                ResultSet primaryKeyResultSet = metaData.getPrimaryKeys(null,null,tableName);
                while(primaryKeyResultSet.next()){
                    String primaryKeyColumnName = primaryKeyResultSet.getString("COLUMN_NAME");
                    System.out.println("主键:"+primaryKeyColumnName);
                }

                //4. 提取表内的字段的名字和类型
                String columnName;
                String columnType;
                ResultSet colRet = metaData.getColumns(null, "%", tableName, "%");
                while (colRet.next()) {
                    columnName = colRet.getString("COLUMN_NAME");
                    columnType = colRet.getString("TYPE_NAME");
                    int datasize = colRet.getInt("COLUMN_SIZE");
                    int digits = colRet.getInt("DECIMAL_DIGITS");
                    int nullable = colRet.getInt("NULLABLE");
                    System.out.println(columnName + " " + columnType + " " + datasize + " " + digits + " " + nullable);
                }


            }

        } finally {
            if (conn != null) {
                conn.close();
            }
        }




/*JDBC里面通过getColumns的接口，实现对字段的查询。跟getTables一样，"%"表示所有任意的（字段），而m_TableName就是数据表的名字。

getColumns的返回也是将所有的字段放到一个类似的内存中的表，而COLUMN_NAME就是字段的名字，TYPE_NAME就是数据类型，比如"int","int unsigned"等等，COLUMN_SIZE返回整数，就是字段的长度，比如定义的int(8)的字段，返回就是8，最后NULLABLE，返回1就表示可以是Null,而0就表示Not Null。*/
    }
}
