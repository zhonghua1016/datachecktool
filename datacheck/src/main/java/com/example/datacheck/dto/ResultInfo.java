package com.example.datacheck.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * created by yuanjunjie on 2020/5/19 1:52 PM
 */
@ToString
@Data
public class ResultInfo {
    private Integer state;
    private List<TaskResult> resultList;

}
