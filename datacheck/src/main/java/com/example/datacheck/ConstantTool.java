package com.example.datacheck;

/**
 * @author macbook
 * @title: ConstantTool
 * @projectName datacheck
 * @description: TODO
 * @date 2020-05-0822:08
 */
public class ConstantTool {
    public static final String JDBC_PRE = "jdbc:mysql://";
    public static final String JDBC_SUFFIX="?useSSL=false&useUnicode=true&characterEncoding=utf-8&autoReconnect=true&serverTimezone=GMT%2B8";
    public  static final String ALL_TABLES = "select table_schema, table_name, engine from information_schema.tables  where upper(table_schema) <> 'MYSQL'  and upper(table_schema) <> 'INFORMATION_SCHEMA'  and upper(table_schema) <> 'PERFORMANCE_SCHEMA'  and upper(table_schema) <> 'SYS'";
    public static final String COUNT_SQL ="select count(1) from ";

    public static final String SELECT_SQL ="select * from ";

    public static final String SELECT_SQL_SUFFIX ="select * from ";
    public static final String ORDER_BY_TYPE_ASC ="asc";
    public static final String ORDER_BY_TYPE_DRSC="desc";
    public static final String CONTENT_CHECK_FLAG="content_check";
    public static final String COUNT_CHECK_FLAG="count_check";

    public static final int CHECK_SUCCESS_RES=1;

    public static final int CHECK_FAILED_RES=-1;

    public static final String COUNT_KEY= "count";
    public static final String MD5_KEY= "md5";
    public static final String PRINARY= "primary";
    public static final String SPLIT= "--";
    public static final String DATA_CHANNEL_SOURCE="source";
    public static final String DATA_CHANNEL_DEST="dest";
    public static final String DATABASE_AND_SPLIT="     ";
    public static final Integer TASK_STATE_STARTING = 0;
    public static final Integer TASK_STATE_END = 1;

    public static final String CHECK_RESULT_PATH="result";
    public static final String RESULT_URL="/dbcheck/result/file";




}
