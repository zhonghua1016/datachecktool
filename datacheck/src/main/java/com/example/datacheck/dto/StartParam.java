package com.example.datacheck.dto;


import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * created by yuanjunjie on 2020/5/18 9:45 PM
 */
@Data
@ToString
public class StartParam {
    private Integer contentPer;
    private Integer countPer;
    private Integer limitCount;
    private List<Integer> selectedIdList;
   // private Boolean isSelectedAll;
}
