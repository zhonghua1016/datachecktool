package com.example.datacheck.config;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * created by yuanjunjie on 2020/5/8 6:41 PM
 */
public class DBConfigTool {
    public static Map<String,List<DBConfig>> readFromXlsx(String path) throws Exception {
        //List<DBConfig> list = new ArrayList<>();
        FileInputStream stream = new FileInputStream(path);
        return readFromXlsx(stream);

    }


    public static Map<String,List<DBConfig>> readFromXlsx(InputStream inputStream) throws Exception {
        List<DBConfig> list = new ArrayList<>();
        Map<String,List<DBConfig>> tasks = new HashMap<>();
        //FileInputStream stream = new FileInputStream(path);
        XSSFWorkbook readWorkbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = readWorkbook.getSheetAt(0);//得到指定名称的Sheet
        String taskId =  DigestUtils.md5Hex(inputStream);
        boolean isFirst = true;
        for (Row row : sheet) {
            if (isFirst) {
                isFirst = false;
                continue;
            }
            DBConfig config = new DBConfig();
            config.setTaskName(getString(row.getCell(0)));
            config.setSrcIp(getString(row.getCell(1)));
            if (isEmptyText(config.getSrcIp())) {
                throw new Exception("任务["+config.getTaskName()+"]源数据库Ip为空");
            }

            try {
                config.setSrcPort(getPort(row.getCell(2)));
            } catch (Exception e) {
                throw new Exception("任务["+config.getTaskName()+"]源数据库"+e.getMessage());
            }
            config.setSrcName(getString(row.getCell(3)));
            if (isEmptyText(config.getSrcName())) {
                throw new Exception("任务["+config.getTaskName()+"]源数据库用户名为空");
            }
            config.setSrcPassword(getString(row.getCell(4)));
            if (isEmptyText(config.getSrcPassword())) {
                throw new Exception("任务["+config.getTaskName()+"]源数据库密码为空");
            }


            config.setDestIp(getString(row.getCell(5)));
            if (isEmptyText(config.getDestIp())) {
                throw new Exception("任务["+config.getTaskName()+"]目标数据库Ip为空");
            }

            try {
                config.setDestPort(getPort(row.getCell(6)));
            } catch (Exception e) {
                throw new Exception("任务["+config.getTaskName()+"]目标数据库"+e.getMessage());
            }
            config.setDestName(getString(row.getCell(7)));
            if (isEmptyText(config.getDestName())) {
                throw new Exception("任务["+config.getTaskName()+"]目标数据库用户名为空");
            }
            //config.setDestPassword("");

            config.setDestPassword(getString(row.getCell(8)));
            if (isEmptyText(config.getDestPassword())) {
                throw new Exception("任务["+config.getTaskName()+"]目标数据库密码为空");
            }
            list.add(config);
        }
        tasks.put(taskId,list);
        inputStream.close();
        return tasks;
    }


    private static Integer getPort(Cell cell) throws Exception {
        String text = getString(cell);
        if (isEmptyText(text)) {
            throw new Exception("端口为空");
        }
        int port = 0;
        try {
            port = Integer.valueOf(text);
        } catch (NumberFormatException e) {
            throw new Exception("端口错误："+text);
        }

        if (port <=0 || port > 65535) {
            throw new Exception("端口范围错误："+text);
        }
        return port;
    }


    private static String getString(Cell cell) {
        String value = null;
        if (cell != null) {
            switch (cell.getCellTypeEnum()) {
                case STRING:
                    value = cell.getStringCellValue();
//                    if (value != null) {
//                        value = value.trim();
//                    }
                    break;
                case NUMERIC:
                    value = String.format("%d", (int)cell.getNumericCellValue());
                    break;
            }
        }
        return value;
    }

    private static boolean isEmptyText(String text) {
        return text == null || text.length() == 0;
    }


    public static void tt(String[] args) {
       /* try {
            List<DBConfig> list = readFromXlsx("/Users/yuanjunjie/works/项目文档/腾云忆想/项目文件/DB割接辅助工具/db-verify_v1.2.0/批量创建任务&导出任务详情模版.xlsx");
            System.out.println("Read==>"+list);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}
