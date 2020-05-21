package com.example.datacheck.dto;

import lombok.Data;
import lombok.ToString;

/**
 * created by yuanjunjie on 2020/5/19 1:53 PM
 */
@ToString
@Data
public class TaskResult {
    private String taskName;
    //数据库连接地址
    private String dbName;
    private String downloadUrl;
    private int totalTableNum;
    private int succTableNum;
    private int failedTableNum;
    // -1 失败，1 通过
    private int result =1;
}