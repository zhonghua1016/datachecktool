package com.example.datacheck.config;


import lombok.Data;
import lombok.ToString;

/**
 * created by yuanjunjie on 2020/5/8 6:41 PM
 */
@ToString
@Data
public class DBConfig {
    private Integer id;
    private String taskName;
    private String srcIp;
    private Integer srcPort;
    private String srcName;
    private String srcPassword;

    private String destIp;
    private Integer destPort;
    private String destName;
    private String destPassword;
}
