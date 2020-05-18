package com.example.datacheck.vo;

import java.io.Serializable;

/**
 * @author macbook
 * @title: TaskVo
 * @projectName datacheck
 * @description: TODO
 * @date 2020-05-1821:42
 */
public class TaskVo implements Serializable {
    //一批任务的id
    private String id ;
    private String taskName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public String toString() {
        return "TaskVo{" +
                "id='" + id + '\'' +
                ", taskName='" + taskName + '\'' +
                '}';
    }
}
