package com.example.datacheck;

import com.example.datacheck.config.DBConfig;
import com.example.datacheck.config.DBConfigTool;
import com.example.datacheck.service.DataCheckTask;
import com.mysql.cj.util.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.util.List;


@SpringBootApplication
public class DatacheckApplication {
    public static final String URL = "jdbc:mysql://localhost:3306/" + ConstantTool.JDBC_SUFFIX;
    public static final String USER = "root";
    public static final String PASSWORD = "zhonghua111002";


    private static boolean isEmpty(String str) {
        return str == null ? true : (str.trim().length() == 0);
    }

    public static final String userSql = "select table_schema, table_name, engine from information_schema.tables  where upper(table_schema) <> 'MYSQL'  and upper(table_schema) <> 'INFORMATION_SCHEMA'  and upper(table_schema) <> 'PERFORMANCE_SCHEMA'  and upper(table_schema) <> 'SYS'";
    public static final String sql = "select * from poc.t_order";


    public static void main(String[] args) throws Throwable {


       /* String taskPath = System.getProperty("task.path");
        if (taskPath == null || (taskPath.trim().length() == 0)) {
            System.out.println("task path is abnormal!task path:" + taskPath);
            return;
        }*/

        //Class.forName("com.mysql.cj.jdbc.Driver");
        // String path ="/Users/macbook/Desktop/translate/test.xlsx";
        SpringApplication.run(DatacheckApplication.class, args);


    }


    private static void doCheck(String[] args) throws Throwable  {
        String taskPath = System.getProperty("task.path");

        if (StringUtils.isNullOrEmpty(taskPath)) {
            System.out.println("ERROR: please input db config path, -Dtask.path=?");
            return;
        } else {
            System.out.println("task.path:"+taskPath);
        }
        String percentageContentString = System.getProperty("table.content.percentage");
        if (StringUtils.isNullOrEmpty(percentageContentString)) {
            System.out.println("table.content.percentage:all[default]");
        } else {
            System.out.println("table.content.percentage:"+percentageContentString);
            float content;
            try {
                content = Float.valueOf(percentageContentString);
            } catch (NumberFormatException e) {
                System.out.println("ERROR: table.content.percentage["+percentageContentString+"] error.");
                return;
            }
            if (content <= 0.0f) {
                System.out.println("ERROR: table.content.percentage must > 0.");
                return;
            }
        }
        String percentageCountString = System.getProperty("table.count.percentage");
        if (StringUtils.isNullOrEmpty(percentageCountString)) {
            System.out.println("table.count.percentage:all[default]");
        } else {
            System.out.println("table.count.percentage:"+percentageCountString);
            float count;
            try {
                count = Float.valueOf(percentageCountString);
            } catch (NumberFormatException e) {
                System.out.println("ERROR: table.percentage.percentage["+percentageCountString+"] error.");
                return;
            }
            if (count <= 0.0f) {
                System.out.println("ERROR: table.percentage.percentage must > 0.");
                return;
            }
        }

        String limitCount = System.getProperty("table.limit.count");
        if (StringUtils.isNullOrEmpty(limitCount)) {
            System.out.println("table.limit.count:10000[default]");
        } else {
            System.out.println("table.limit.count:"+limitCount);
            int count;
            try {
                count = Integer.valueOf(limitCount);
            } catch (NumberFormatException e) {
                System.out.println("ERROR: table.limit.count["+limitCount+"] error.");
                return;
            }
            if (count <= 0) {
                System.out.println("ERROR: table.limit.count must > 0.");
                return;
            }
        }



//        // ExecutorService pool = Executors.newFixedThreadPool(4);
//        List<DBConfig> dbConfigList = DBConfigTool.readFromXlsx(taskPath);
//        for (DBConfig dbConfig : dbConfigList) {
//            System.out.println("---------------------------------------");
//            new DataCheckTask(dbConfig).run();
//
//        }
    }

}
