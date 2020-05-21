package com.example.datacheck.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author macbook
 * @title: TableSchemaRes
 * @projectName datacheck
 * @description: TODO
 * @date 2020-05-1911:18
 */
@Data
@ToString
public class TableSchemaRes implements Serializable {
    private String id;
    private String dataBaseName;
    private String table;
    // -1  检测不通过通过，1检测通过，
    private int flag;
}
