package com.example.datacheck.controller;

import com.example.datacheck.ConstantTool;
import com.example.datacheck.config.DBConfig;
import com.example.datacheck.config.DBConfigTool;
import com.example.datacheck.constant.CommonConstants;
import com.example.datacheck.dto.*;
import com.example.datacheck.service.DataCheckTask;
import com.example.datacheck.service.TaskThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author macbook
 * @title: CheckController
 * @projectName datacheck
 * @description: TODO
 * @date 2020-05-1814:28
 */
@RestController
public class CheckController {
    private static final Logger logger = LoggerFactory.getLogger(CheckController.class);
    // 任务提交的列表
    public static List<DBConfig> dbConfigs = null;
    // 执行任务内容检测的结果
    public static ResultInfo contentRes = new ResultInfo();
    // 执行任务数量检测的结果
    public static ResultInfo countRes = new ResultInfo();
    //有任务正在执行表示，整个任务跑完修改值为true
    public static AtomicBoolean taskstartFalg = new AtomicBoolean(false);


    @PostMapping("/start")
    public R startCheck(@RequestBody StartParam startParam,HttpSession session) throws Throwable{
        logger.info("startParam:{}", startParam);
        int countPer = startParam.getCountPer()==null?100:startParam.getCountPer();
        int contentPer = startParam.getContentPer()==null?100:startParam.getContentPer();
        int limmitCount = startParam.getLimitCount()==null?10000:startParam.getLimitCount();
        if (countPer>100||contentPer>100||limmitCount<1){
            return R.error("param is abnormal,countPer:"+contentPer+",contentPer:"+contentPer
            +",limmitCount:"+limmitCount);
        }
        List<Integer>selectedTasks =startParam.getSelectedIdList();
        if(selectedTasks==null||selectedTasks.size()<1)
            return R.error(CommonConstants.FAIL,"selected tasks is not null");

        List<DBConfig> realTasks = new ArrayList<>();
        List<DataCheckTask> dataCheckTasks = new ArrayList<>();
        List<DBConfig>taskList = dbConfigs;
        if(taskList==null||taskList.size()<1)
            return R.error(CommonConstants.FAIL,"before start check task,please upload tasks");
        if(taskstartFalg.compareAndSet(false,true)){
            for(Integer integer:selectedTasks){
                for(DBConfig dbConfig:taskList){
                    if(integer.equals(dbConfig.getId())){
                        realTasks.add(dbConfig);
                    }

                }
            }
            for(DBConfig dbConfig:realTasks){
                DataCheckTask dataCheckTask = new DataCheckTask(dbConfig,String.valueOf(contentPer/100f),String.valueOf(countPer/100f),String.valueOf(limmitCount));
                dataCheckTasks.add(dataCheckTask);

            }
            // 清除上次的结果
            if(contentRes.getResultList()!=null)
            contentRes.getResultList().clear();
            if(countRes.getResultList()!=null)
            countRes.getResultList().clear();
            String resultBasePath = new StringBuilder(DataCheckTask.basePath).append("/")
                    .append(ConstantTool.CHECK_RESULT_PATH).toString();
            DataCheckTask.deleteDir(new File(resultBasePath));
            String dbTemp = new StringBuilder(DataCheckTask.basePath).append("/")
                    .append("db").toString();
            DataCheckTask.deleteDir(new File(dbTemp));
            //开启本次任务的检测线层
            new Thread(new TaskThread(dataCheckTasks)).start();
            return R.ok();
        }else{
            return R.error(CommonConstants.STARTING,"task is already starting!");

        }

       // return R.ok();
    }

   @GetMapping("/count/result")
   public  R getCountRes(){
        if(taskstartFalg.get()==true){
            countRes.setState(ConstantTool.TASK_STATE_STARTING);
            return R.ok(countRes);
        }
       countRes.setState(ConstantTool.TASK_STATE_END);
      return R.ok(countRes);
    }

    @GetMapping("/content/result")
    public  R getContentRes(){
        if(taskstartFalg.get()==true){
            contentRes.setState(ConstantTool.TASK_STATE_STARTING);
            return R.ok(contentRes);
        }
        contentRes.setState(ConstantTool.TASK_STATE_END);
        return R.ok(contentRes);
    }

    public float stringToFloat(String str) throws Exception {
        float toFloat = 0;
        try {
            toFloat = Float.valueOf(str);
        } catch (NumberFormatException e) {
            System.out.println("ERROR: table.percentage.percentage[" + str + "] error.");
            throw new Exception("str to float is exception,str:+" + str + "\n" + e.getMessage());
        }
        return toFloat;

    }





    @PostMapping(value = "/upload")
    public R upload(@RequestParam("file") MultipartFile file,
                                    HttpSession session) throws Exception {
        if(taskstartFalg.get()==true){
            return R.error(CommonConstants.STARTING,"task is already starting,please waiting!");
        }
        //dbConfigs.clear();
        if (!file.isEmpty()) {
            try {
                /*File saveFilePath = new File(ConstantTool.TASK_BASE_PATH);
                // 重新创建文件夹 ,为了将之前的数据删除
                if (saveFilePath.exists()) {
                    saveFilePath.delete();
                }
                saveFilePath.mkdirs();*/
                /*String path = ConstantTool.TASK_BASE_PATH + "/" + file.getOriginalFilename();
                byte[] bytes = file.getBytes();*/
               /* BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(path));
                FileInputStream fileInputStream =  new FileInputStream(file.getInputStream()) ;
*/

                List<DBConfig> dbConfigList = DBConfigTool.readFromXlsx(file.getInputStream());
                dbConfigs = dbConfigList;
                //setTaskFileName(path);
                /*stream.write(bytes);
                stream.close();*/
                //session.setAttribute("dbConfigs", dbConfigList);
                return R.ok();
            } catch (Exception e) {
                throw new Exception("upload task happened exception:" + e.getMessage());
            }
        } else {
            throw new Exception("upload task is null");
        }
    }


    @GetMapping("/config/list")
    public R<List<DBConfig>> configList(HttpSession session) {
        //List<DBConfig> list = (List<DBConfig>) session.getAttribute("dbConfigs");
        return R.ok(dbConfigs);
    }





    @GetMapping("/result")
    public R<ResultInfo> taskResult() {
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setState(0);
        List<TaskResult> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            TaskResult result = new TaskResult();
            result.setTaskName("task-1");
            result.setResult(i%2);
            result.setDbName("test-"+i);
            result.setDownloadUrl("/122/222");
            result.setFailedTableNum(i);
            result.setTotalTableNum(100);
            result.setSuccTableNum(51);
            list.add(result);
        }
        resultInfo.setResultList(list);

        return R.ok(resultInfo);
    }


}
