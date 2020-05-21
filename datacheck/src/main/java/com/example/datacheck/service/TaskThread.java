package com.example.datacheck.service;


import com.example.datacheck.ConstantTool;
import com.example.datacheck.config.ZipCompressorTool;
import com.example.datacheck.controller.CheckController;
import com.example.datacheck.controller.GeneralPageController;
import com.example.datacheck.dto.TaskResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author macbook
 * @title: TaskThread
 * @projectName datacheck
 * @description: TODO
 * @date 2020-05-1913:12
 */
public class TaskThread implements Runnable {
    private List<DataCheckTask> taskList;

    public  TaskThread(List<DataCheckTask> taskList) {
        this.taskList = taskList;
    }



    @Override
    public void run() {
        try {
            startTask();
        }catch (Throwable e){
            System.out.println("check task thread happened:"+e.getMessage());
        }


    }

    public void startTask() throws Throwable{
        List<TaskResult> countResultList = new ArrayList<>();
        List<TaskResult> contentResultList = new ArrayList<>();
        try {
            for (DataCheckTask dataCheckTask : taskList) {
                Map<String, TaskResult> taskRes = dataCheckTask.run();
                TaskResult countRes = taskRes.get(ConstantTool.COUNT_CHECK_FLAG);
                TaskResult contentRes = taskRes.get(ConstantTool.CONTENT_CHECK_FLAG);
                countResultList.add(countRes);
                contentResultList.add(contentRes);
            }
            // 设置测试结果
            CheckController.contentRes.setResultList(contentResultList);
            CheckController.contentRes.setState(ConstantTool.TASK_STATE_END);
            CheckController.countRes.setResultList(countResultList);
            CheckController.countRes.setState(ConstantTool.TASK_STATE_END);
            //对检测结果进行压缩文件夹
            ZipCompressorTool zipCompressorTool = new ZipCompressorTool(DataCheckTask.resultBasePath);
            zipCompressorTool.compress(DataCheckTask.basePath+"/result");

            CheckController.taskstartFalg.set(false);
        }catch(Exception e){
            throw new Exception("check happened exception!");
        }
        finally {
            CheckController.taskstartFalg.set(false);
        }

    }
}
