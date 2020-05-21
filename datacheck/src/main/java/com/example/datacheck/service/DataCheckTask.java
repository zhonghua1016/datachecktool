package com.example.datacheck.service;

import com.example.datacheck.ConstantTool;
import com.example.datacheck.config.DBConfig;
import com.example.datacheck.dto.TableSchemaRes;
import com.example.datacheck.dto.TaskResult;
import org.apache.commons.codec.digest.DigestUtils;
import sun.security.pkcs11.Secmod;


import java.io.*;

import java.sql.*;
import java.util.*;

/**
 * @author macbook
 * @title: DataCheckTask
 * @projectName datacheck
 * @description: TODO
 * @date 2020-05-0821:28
 */
public class DataCheckTask  {
   public static final String basePath = System.getProperty("user.dir");
   public static String resultBasePath = basePath+"/"+ "result.zip";

    static String sampleCount = System.getProperty("table.limit.count","10000");

    private String contentPer ;
    //数量检测比例
    private String countPer;
    //每种表前后各取的数量
    private String  limit;


    //数据库中的表，key为tableName,value为数据库名


    private DBConfig dbConfig;

    public DataCheckTask() {

    }

    public DataCheckTask(DBConfig dbConfig,String contentPer,String countPer,String limit) {

        this.dbConfig = dbConfig;
        this.countPer =countPer;
        this.contentPer = contentPer;
        this.limit =limit;

    }


    public  Map<String,TaskResult> run() throws Throwable {
        Map<String,TaskResult> taskRes = new HashMap<>();
        TaskResult taskConentResult =new TaskResult();
        TaskResult taskCountResult =new TaskResult();
        try {
            taskConentResult.setDbName(dbConfig.getSrcIp());
            taskCountResult.setDbName(dbConfig.getSrcIp());

            taskCountResult.setDownloadUrl(ConstantTool.RESULT_URL);
            taskConentResult.setDownloadUrl(ConstantTool.RESULT_URL);


            taskConentResult.setTaskName(dbConfig.getTaskName());
            taskCountResult.setTaskName(dbConfig.getTaskName());

            Map<String,TaskResult> checkRes= new HashMap<>();

            System.out.println(getTaskName() + "begin run....................");
            Map<String, String> sourceAllTables = getAllTables(dbConfig.getSrcIp(), dbConfig.getSrcPort().toString(), dbConfig.getSrcPassword(), dbConfig.getSrcName());
            //随机选取的内容检测表
            Map<String, String> selectedSourceContTables = new HashMap<>();
            //随机数量的内容检测表
            Map<String, String> selectedSourceCountTables = new HashMap<>();

            String percentageContentString = getContentPer();
            String percentageCountString = getCountPer();
            float percentageContent = isEmpty(percentageContentString) ? 1f : Float.valueOf(percentageContentString.trim());
            float percentageCount = isEmpty(percentageCountString) ? 1f : Float.valueOf(percentageCountString.trim());
            if (percentageContent > 1 || percentageCount > 1) {
                System.out.println(getTaskName() + "percentage is abnormal percentage:" + percentageContent + ";" + percentageCount);
                throw new Exception(getTaskName() + "percentage is abnormal percentage");
            }
            //表的总个数和内容检测百分比、数量百分比
           /* StringBuilder checkParam = new StringBuilder(getTaskName()).append("all table:")
                    .append(sourceAllTables.size()).append(", percentageContent:").append(percentageContent)
                    .append(", percentageCount:").append(percentageCount);
            System.out.println(checkParam.toString());*/
            if (sourceAllTables != null && sourceAllTables.size() > 0) {

                selectedSourceContTables = selectRandomTableByPer(sourceAllTables, percentageContent);
                taskConentResult.setTotalTableNum(selectedSourceContTables.size());
                selectedSourceCountTables = selectRandomTableByPer(sourceAllTables, percentageCount);
                taskCountResult.setTotalTableNum(selectedSourceCountTables.size());

            } else {
                System.out.println(getTaskName() + "source  have no tables!");
                return checkRes;
            }


            /*StringBuilder tableDescribe = new StringBuilder(getTaskName()).append("content check, ")
                    .append("all table count:")
                    .append(sourceAllTables.size())
                    .append(", checked table count:")
                    .append(selectedSourceContTables.size());

            StringBuilder tableDescribe1 = new StringBuilder(getTaskName()).append("count check, ")
                    .append("all table count:")
                    .append(sourceAllTables.size())
                    .append(", checked table count:")
                    .append(selectedSourceCountTables.size());

            System.out.println(tableDescribe.toString());
            System.out.println(tableDescribe1.toString());*/
            int contentFailedCount =0;
            int countFailedCount =0;
            Map<String, Boolean> checkTableContentRes = checkTablesContent(selectedSourceContTables);
            Map<String, Boolean> checkTableRowCountRes = checkTableRowCount(selectedSourceCountTables);
            taskConentResult.setSuccTableNum(staticCount(checkTableContentRes,true));
            contentFailedCount =staticCount(checkTableContentRes,false);
            taskConentResult.setFailedTableNum(contentFailedCount);

            countFailedCount = staticCount(checkTableRowCountRes,false);

            taskCountResult.setSuccTableNum(staticCount(checkTableRowCountRes,true));
            taskCountResult.setFailedTableNum(countFailedCount);
            if(countFailedCount>0)
                taskCountResult.setResult(ConstantTool.CHECK_FAILED_RES);
            if(contentFailedCount>0)
                taskConentResult.setResult(ConstantTool.CHECK_FAILED_RES);

            taskRes.put(ConstantTool.COUNT_CHECK_FLAG,taskCountResult);
            taskRes.put(ConstantTool.CONTENT_CHECK_FLAG,taskConentResult);




            /*for (Map.Entry<String, Boolean> entry : checkTableContentRes.entrySet()) {
                System.out.println("content:" + entry.getKey() + ":" + entry.getValue());

            }*/


            //结果存储路径
            StringBuilder resContentPath = new StringBuilder(basePath).append("/").append(ConstantTool.CHECK_RESULT_PATH)
                    .append("/").append(dbConfig.getTaskName()).append("/")
                    .append(ConstantTool.CONTENT_CHECK_FLAG);
            StringBuilder resCountPath = new StringBuilder(basePath).append("/").append(ConstantTool.CHECK_RESULT_PATH).append("/")
                    .append(dbConfig.getTaskName()).append("/")
                    .append(ConstantTool.COUNT_CHECK_FLAG);
            saveMapAsFile(getCheckFailedMap(checkTableContentRes), resContentPath.toString());
            saveMapAsFile(getCheckFailedMap(checkTableRowCountRes), resCountPath.toString());

            showResponse(checkTableContentRes,getTaskName()+"==>content check");
            showResponse(checkTableRowCountRes,getTaskName()+"==>count check");
            System.out.println(getTaskName() + "end....................");


        } catch (Throwable e) {
            System.out.println(getTaskName()+"failed" + e.getMessage());
        }
        return taskRes;


    }

    public Map<String, Boolean> getCheckFailedMap(Map<String, Boolean> sourceMap){
        Map<String, Boolean> failedMap = new HashMap<>();
        for(Map.Entry<String, Boolean>entry:sourceMap.entrySet()){
            if(entry.getValue()==false)
                failedMap.put(entry.getKey(),entry.getValue());
        }
        return failedMap;

    }

/**
 * 根据 flag 统计数量
 */
   public  int staticCount(Map<String, Boolean> maps,Boolean flag){
       int count =0;
        for(Map.Entry<String, Boolean>entry:maps.entrySet()){
            if(entry.getValue().equals(flag)){
                count+=1;
            }

        }
        return count;

    }

   /* public List<TaskResult>  mapResToTableSchemaRes(Map<String, Boolean> res1){
        List<TableSchemaRes> res = new ArrayList<>();
        for(Map.Entry<String, Boolean> entry:res1.entrySet()){
            TableSchemaRes tableSchemaRes = new TableSchemaRes();
            String[]database_table = entry.getKey().split(ConstantTool.DATABASE_AND_SPLIT);
            tableSchemaRes.setDataBaseName(database_table[0]);
            tableSchemaRes.setTable(database_table[1]);

        }

    }
*/



    private void showResponse(Map<String, Boolean> res, String type) {
        int successCount = 0;
        int failedCount = 0;
        for (Map.Entry<String, Boolean> entry : res.entrySet()) {
            if (entry.getValue() != null) {
                if ( entry.getValue() == true) {
                    successCount += 1;
                } else {
                    failedCount += 1;
                }
            }else {
                failedCount += 1;
            }
        }
        StringBuilder showRes = new StringBuilder();

        showRes.append(type)
                .append(", ")
                .append("success:")
                .append(successCount)
                .append(", failed:")
                .append(failedCount);
        System.out.println(showRes.toString());

    }

    private String getTaskName() {
        return dbConfig.getTaskName() + ":";
    }

    /**
     * 对表的内容检测
     * 返回结果，key,为DataBase+tableName,value 校验结果
     *
     * @return
     */

    private Map<String, Boolean> checkTablesContent(Map<String, String> tables) throws Throwable {
        System.out.println(getTaskName() + "==>begin checkTablesContent....................");
        Map<String, Boolean> tablesCheckResponse = new LinkedHashMap<>();
        try {

            for (Map.Entry<String, String> entry : tables.entrySet()) {
                Map<List<String>, Map<String, String>> sourceTable = new HashMap<>();
                Map<List<String>, Map<String, String>> destTable = new HashMap<>();
                //按表的某一字段进行记录
                String primary = "";
                String realTable[] = entry.getKey().split(ConstantTool.DATABASE_AND_SPLIT);
                String tableName = realTable[1];
                sourceTable = getSampleDataByTables(dbConfig.getSrcIp(), dbConfig.getSrcPort().toString(), tableName, entry.getValue(),
                        ConstantTool.SELECT_SQL, primary, dbConfig.getSrcPassword(), dbConfig.getSrcName(), ConstantTool.DATA_CHANNEL_SOURCE);
                destTable = getSampleDataByTables(dbConfig.getDestIp(), dbConfig.getDestPort().toString(), tableName, entry.getValue(),
                        ConstantTool.SELECT_SQL, primary, dbConfig.getDestPassword(), dbConfig.getDestName(), ConstantTool.DATA_CHANNEL_DEST);
                String sourceContentMD5 = null;
                String destContentMD5 = null;
                List<String> sourceList = null;
                List<String> destList = null;
                for (Map.Entry<List<String>, Map<String, String>> entry1 : sourceTable.entrySet()) {
                    sourceContentMD5 = (entry1.getValue()).get(ConstantTool.MD5_KEY);
                    primary = (entry1.getValue()).get(ConstantTool.PRINARY);
                    sourceList = entry1.getKey();
                }
                for (Map.Entry<List<String>, Map<String, String>> entry2 : destTable.entrySet()) {
                    destContentMD5 = (entry2.getValue()).get(ConstantTool.MD5_KEY);
                    destList = entry2.getKey();
                }
                if (destContentMD5.equals(sourceContentMD5)) {
                    tablesCheckResponse.put(entry.getKey(), true);

                } else {
                    tablesCheckResponse.put(entry.getKey(), false);
                    StringBuilder stringBuilder = new StringBuilder();
                    //下面形成一个/根目录/database/table/ip/error/table.txt
                    stringBuilder.append(basePath).append("/").append(ConstantTool.CHECK_RESULT_PATH).append("/").append(dbConfig.getTaskName()).append("/").append("error").append("/");
                    stringBuilder.append("/").append(entry.getValue()).append("/").append(tableName).append("/")
                            .append(ConstantTool.CONTENT_CHECK_FLAG).append("/").append(dbConfig.getSrcIp()).append("/");

                    File file = new File(stringBuilder.toString());
                    // 重新创建文件夹 ,为了将之前的数据删除
                    if (file.exists()) {
                        file.delete();
                    }
                    file.mkdirs();
                    saveAbnormalRowAsFile(sourceList, stringBuilder.toString() + tableName + ".txt", primary);

                }

            }
            dbConfig.getDestIp();
        } catch (Throwable e) {
            System.out.println(e);
            throw e;

        }


        System.out.println(getTaskName()+"==>checkTablesContent end....................");
        return tablesCheckResponse;

    }

    /**
     * 删除文件夹
     * @param dir
     * @return
     */

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        //删除空文件夹
        return dir.delete();
    }



    /**
     * 检测表的数据总数
     *
     * @param allTables
     * @return 返回数据表的总数检测的结果，key为表名
     * @throws Throwable
     */


    private Map<String, Boolean> checkTableRowCount(Map<String, String> allTables) throws Throwable {

        System.out.println(getTaskName() + "==>begin checkTableRowCount ....................");
        Map<String, Boolean> tablesCheckResponse = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : allTables.entrySet()) {
            String realTable[] = entry.getKey().split(ConstantTool.DATABASE_AND_SPLIT);
            String tableName = realTable[1];
            int sourceCount = checkByCount(dbConfig.getSrcIp(), dbConfig.getSrcPort().toString(), entry.getValue(),
                    ConstantTool.COUNT_SQL + tableName, dbConfig.getSrcPassword(), dbConfig.getSrcName());

            int destCount = checkByCount(dbConfig.getDestIp(), dbConfig.getDestPort().toString(), entry.getValue(),
                    ConstantTool.COUNT_SQL + tableName, dbConfig.getDestPassword(), dbConfig.getDestName());
            if (sourceCount != destCount) {
                tablesCheckResponse.put(entry.getKey(), false);
            } else {
                tablesCheckResponse.put(entry.getKey(), true);

            }
        }
        System.out.println(getTaskName() + "==>checkTableRowCount end....................");
        return tablesCheckResponse;


    }

    /**
     * 根据百分比随机选取要检测的表
     *
     * @param allTables  源数据
     * @param percentage 百分比
     * @return
     */

    private Map<String, String> selectRandomTableByPer(Map<String, String> allTables, float percentage) {
        Map<String, String> selectedCheckTables = new HashMap<>();
        if (percentage == 1f)
            return allTables;
        if (allTables != null && allTables.size() > 0) {
            int size = allTables.size();
            Random random = new Random(System.currentTimeMillis());
            //计算出校验表的个数
            Double realCheckCount = Math.ceil(size * percentage);
            //随机选的表集合的下标
            List<Integer> countArrays = new ArrayList<>(realCheckCount.intValue());
            //
            boolean[] bool = new boolean[allTables.size()];
            int randInt = 0;//新建变量用于临时存储产生的随机数
            for (int i = 0; i < realCheckCount; i++) {
                do {
                    randInt = random.nextInt(allTables.size());//产生一个随机数
                    //randInt = (int) (Math.random() * 10);
                } while (bool[randInt]);
                bool[randInt] = true;
                countArrays.add(randInt);
                //System.out.println(randInt);
            }
            int index = 0;
            for (Map.Entry<String, String> table : allTables.entrySet()) {

                if (countArrays.contains(index)) {
                    selectedCheckTables.put(table.getKey(), table.getValue());
                }
                index++;
            }
            return selectedCheckTables;
        }
        return selectedCheckTables;

    }


    /**
     * 保存list结果到文件中去
     *
     * @param list
     * @param filePath
     * @throws Exception
     */

    private void saveAbnormalRowAsFile(List<String> list, String filePath, String primary) throws Exception {
        FileWriter fileWriter = new FileWriter(filePath);
        fileWriter.write(primary + "\n");
        for (String string : list) {
            fileWriter.write(string + "\n");
        }
        fileWriter.flush();
        fileWriter.close();

    }

    /**
     * 保存内容、数量检测结果到文件中
     *
     * @param map
     * @param path 所要将结果保存的根路径
     * @throws Exception
     */
    private void saveMapAsFile(Map<String, Boolean> map, String path) throws Exception {
        File file = new File(path);
        // 重新创建文件夹 ,为了将之前的数据删除
        if (file.exists()) {
            file.delete();
        }
        file.mkdirs();
        String resTxt = path + "/" + dbConfig.getTaskName() + ".txt";
        FileWriter fileWriter = new FileWriter(resTxt);

        for (Map.Entry<String, Boolean> entry : map.entrySet()) {
            StringBuilder row = new StringBuilder();
            row.append(entry.getKey()).append("    ").append(entry.getValue());
            fileWriter.write(row.toString() + "\n");
            //System.out.println("content:" + entry.getKey() + ":" + entry.getValue());


        }
        fileWriter.flush();
        fileWriter.close();



    }


    /**
     * 获取连接
     *
     * @param ip
     * @param port
     * @param dataBaseName
     * @return
     * @throws Throwable
     */

    public Connection getConnection(String ip, String port, String dataBaseName, String password, String user) throws Throwable {

        String jdbcUrl = ConstantTool.JDBC_PRE + ip + ":" + port + "/" + dataBaseName.trim() + ConstantTool.JDBC_SUFFIX;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(jdbcUrl, user, password);
        } catch (Throwable e) {
            System.out.println("get Connection excepition ip:" + ip + ":" + port + "/" + dataBaseName + e.getMessage());
        }
        return conn;
    }

    /**
     * 获取source连接中的所有库中的表
     *
     * @return key databaseName+分隔符+tablsName 避免多个库中有相同表名，value为库名
     * @throws Throwable
     */
    private Map<String, String> getAllTables(String ip, String port, String password, String user) throws Throwable {
        Map<String, String> allTables = new HashMap<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection(ip, port, "", password, user);
            // getConnection(dbConfig, "");

            if (conn == null) {
                throw new Exception("get connection exception");
            }
            stmt = conn.createStatement();
            rs = stmt.executeQuery(ConstantTool.ALL_TABLES);
            //如果有数据，rs.next()返回true
            while (rs.next()) {
                String databaseName = rs.getString("table_schema");
                String tablesname = rs.getString("table_name");
                //避免多个库中有相同的表名
                allTables.put(databaseName+ConstantTool.DATABASE_AND_SPLIT+tablesname, rs.getString("table_schema"));
            }
            rs.close();
            stmt.close();
            conn.close();
        } finally {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
            if (conn != null)
                conn.close();
        }
        return allTables;

    }


    public static void tt(String[] args) throws Throwable {


        Integer integer = new Integer(35);
        float f = integer/100f;
        System.out.println(f);


        /*DBConfig dbConfig = new DBConfig();
        DataCheckTask dataCheckTask = new DataCheckTask(dbConfig);
       *//* dbConfig.setTaskName("test");
        List<String> list = new ArrayList<>();
        list.add("中华");
        list.add("华");*//*
        System.out.println("table.percentage:" + System.getProperty("table.percentage"));
        String percentageString = System.getProperty("table.percentage");
        float percentage = dataCheckTask.isEmpty(percentageString) ? 0f : Float.valueOf(percentageString.trim());

        System.out.println("percentage:" + percentage);

        //dataCheckTask.saveListAsFile(list, new File(basePath + "/" + "cai.txt"));
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < 30; i++) {
            map.put(String.valueOf(i), String.valueOf(i));
        }
        Map<String, String> res = dataCheckTask.selectRandomTableByPer(map, 0.4f);
        for (Map.Entry<String, String> entry : res.entrySet()) {
            System.out.println(entry.getKey());
        }*/
    }


    /**
     * 对指定的表进行前后一万条数据写入文件，并对文件进行MD5检验，并把MD5值返回，将primary字段返回list
     *
     * @param tableName
     * @param dataBaseName
     * @param sqlPre
     * @return list为primary字段返回list，string为查询tableName结果对文件的MD5值
     */

    private Map<List<String>, Map<String, String>> getSampleDataByTables(String ip, String port, String tableName, String dataBaseName, String sqlPre, String primary, String password, String user, String dataChannelType) throws Throwable {
        HashMap<List<String>, Map<String, String>> response = new HashMap<>();
        Map<String, String> primaryAndMd5 = new HashMap<>();
        Connection conn = getConnection(ip, port, dataBaseName, password, user);
        Statement stmt = null;
        Statement stmt1 = null;
        ResultSet primaryKeyResultSet = null;
        ResultSet colRet = null;
        ResultSet rsPre = null;
        ResultSet rsSuffix = null;
        ResultSetMetaData rsmd = null;
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            //根据表名获取主键
            primaryKeyResultSet = metaData.getPrimaryKeys(dataBaseName, dataBaseName, tableName);
            //1. 提取表内的字段的名字和类型
            colRet = metaData.getColumns(dataBaseName, dataBaseName, tableName, "%");
            Map<String, String> columnNameAndType = new LinkedHashMap<>();
            String columnName;
            String columnType;
            //如果表没有主键，查询排序是按表中所有的字段进行排序

            StringBuilder allCol = new StringBuilder();
            //记录一个表中最后一字段的名字
            String lastName=null;
            //按某一字段排序
            String recordCol = null;
            //获取所有的字段name和类型
            while (colRet.next()) {
                columnName = colRet.getString("COLUMN_NAME");
                columnType = colRet.getString("TYPE_NAME");
                lastName = columnName;
                allCol.append(columnName).append(",");
                columnNameAndType.put(columnName, columnType);

            }
            if(primaryKeyResultSet!=null) {
                while (primaryKeyResultSet.next()) {
                    String primaryKeyColumnName = primaryKeyResultSet.getString("COLUMN_NAME");
                    primary = primaryKeyColumnName;
                    if (primary != null) {
                        break;
                    }
                }
            }

            rsPre = null;
            rsSuffix = null;
            stmt = conn.createStatement();
            stmt1 = conn.createStatement();
            if (isEmpty(primary)) {
                String allColString  = allCol.toString();
                recordCol =   allColString.substring(0,allColString.length()-1);
                primary = lastName;

            }else {
                recordCol = primary;
            }
            primaryAndMd5.put(ConstantTool.PRINARY, primary);
            int limit= Integer.valueOf(getLimit());
            if(limit<1){
                throw new Exception("table.limit.count is abnormal,value:"+limit);
            }
            //前一万条
            String sql = sqlPre + tableName + " order by " + recordCol + " " + "asc" + " limit "+getLimit();
            //后一万条
            String sql1 = sqlPre + tableName + " order by " + recordCol + " " + "desc" + " limit "+getLimit();
            rsPre = stmt.executeQuery(sql);
            rsSuffix = stmt1.executeQuery(sql1);
            rsmd = (ResultSetMetaData) rsPre.getMetaData();
            //按照某一字段记录所抽样数据
            List<String> recordList = new ArrayList<>(10000);
            //在当前工程目录下创建文件夹
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(basePath).append("/").append("db").append("/").append(dbConfig.getTaskName());
            stringBuilder.append("/").append(dataBaseName).append("/").append(tableName).append("/")
                    .append(dataChannelType+"_"+ip);
            File file = new File(stringBuilder.toString());
            // 重新创建文件夹 ,为了将之前的数据删除
            if (file.exists()) {
                file.delete();
            }
            file.mkdirs();
            String filePath = stringBuilder.toString() + "/" + tableName + ".txt";
            FileOutputStream outputStream = new FileOutputStream(filePath);
            DataOutputStream objectOutputStream = new DataOutputStream(outputStream);
            //往指定文件写入前一万条数据
            saveFile(objectOutputStream, rsmd, rsPre, recordList, primary, columnNameAndType);
            //往指定文件写入后一万条数据
            saveFile(objectOutputStream, rsmd, rsSuffix, recordList, primary, columnNameAndType);
            /*Set result = new HashSet(recordList);
            recordList = new ArrayList<>(result);*/

            String md51 = DigestUtils.md5Hex(new FileInputStream(filePath));
            primaryAndMd5.put(ConstantTool.MD5_KEY, md51);

            objectOutputStream.close();
            outputStream.close();
            response.put(recordList, primaryAndMd5);
            colRet.close();
            rsSuffix.close();
            primaryKeyResultSet.close();
            rsPre.close();
            stmt.close();
            stmt1.close();
            conn.close();

        } finally {
            if (colRet != null)
                colRet.close();
            if (rsPre != null)
                rsPre.close();
            if (rsSuffix != null)
                rsSuffix.close();
            if (primaryKeyResultSet != null)
                primaryKeyResultSet.close();
            if (stmt != null)
                stmt.close();
            if (stmt1 != null)
                stmt1.close();
            if (conn != null)
                conn.close();
        }
        return response;
    }

    /**
     * 将查询结果输入指定的文件中
     *
     * @param dataOutputStream
     * @param rsmd
     * @param rs
     * @param primaryList
     * @param primary
     * @param columnNameAndType
     * @throws Throwable
     */


    private void saveFile(DataOutputStream dataOutputStream, ResultSetMetaData rsmd, ResultSet rs, List<String> primaryList, String primary, Map<String, String> columnNameAndType) throws Throwable {
        while (rs.next()) {
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                if (!primary.isEmpty() && primary.equals(rsmd.getColumnLabel(i)) && primaryList != null) {
                    primaryList.add(rs.getString(i));
                }
                String type = columnNameAndType.get(rsmd.getColumnLabel((i)));
                if (type.equals("DOUBLE")) {
                    dataOutputStream.writeDouble(rs.getDouble(i));
                }
                if (rs.getString(i) != null) {
                    dataOutputStream.writeBytes(rs.getString(i));
                }
                //dataOutputStream.writeBytes(rs.getString(i));
            }
        }

    }

    private boolean isEmpty(String str) {
        return str == null ? true : (str.trim().length() == 0);
    }

    private int checkByCount(String ip, String port, String dataBaseName, String sql, String password, String user) throws Throwable {
        Connection conn = null;
        ResultSet rs = null;
        Statement stmt = null;
        int count = 0;
        try {
            conn = getConnection(ip, port, dataBaseName, password, user);

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                count = rs.getInt("count(1)");
            }
        } catch (Throwable e) {
            System.out.println(getTaskName() + dataBaseName + "happened exception:" + e.getMessage());
            throw new Exception(e);

        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return count;
    }

    public String getContentPer() {
        return contentPer;
    }

    public void setContentPer(String contentPer) {
        this.contentPer = contentPer;
    }

    public String getCountPer() {
        return countPer;
    }

    public void setCountPer(String countPer) {
        this.countPer = countPer;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }
}
