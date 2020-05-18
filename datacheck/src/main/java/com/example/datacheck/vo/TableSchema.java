package com.example.datacheck.vo;

import java.io.Serializable;

/**
 * @author macbook
 * @title: TableSchema
 * @projectName datacheck
 * @description: TODO
 * @date 2020-05-1815:35
 */
public class TableSchema implements Serializable {

    private String id;
    private String dataBaseName;
    private String table;
    // -1  检测不通过通过，1检测通过，0是未检测
    private int flag;

    public String getDataBaseName() {
        return dataBaseName;
    }

    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "TableSchema{" +
                "id='" + id + '\'' +
                ", dataBaseName='" + dataBaseName + '\'' +
                ", table='" + table + '\'' +
                ", flag=" + flag +
                '}';
    }
}
