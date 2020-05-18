package com.example.datacheck.controller;

import com.example.datacheck.config.DBConfig;
import com.example.datacheck.config.DBConfigTool;
import com.example.datacheck.vo.TaskVo;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author macbook
 * @title: CheckController
 * @projectName datacheck
 * @description: TODO
 * @date 2020-05-1814:28
 */
@Controller
@Component
public class CheckController {
    // key 上传文件配置的MD5值,value 对用的任务列表
    public static Map<String, List<DBConfig>> tasks = new HashMap<>();

    @GetMapping("/dbcheck")
    public String homePage() {
        return "home.html";
    }


    @PostMapping("/start")
    @ResponseBody
    public void startCheck(@RequestParam(value = "contentPer") String contentPer,
                           @RequestParam(value = "countPer") String countPer,
                           @RequestParam(value = "limitCount") String limitCount,
                           @RequestBody(required = true)List<TaskVo> taskList) throws Exception {
        if (taskList.size() > 0) {
            float contentPerFloat = contentPer != null ? Float.valueOf(contentPer) : 1;
            float countPerFloat = countPer != null ? Float.valueOf(countPer) : 1;
            float limitFloat = limitCount != null ? Float.valueOf(countPer) : 10000;
            if (contentPerFloat > 1 || countPerFloat > 1 || limitFloat < 1 || taskList == null) {
                throw new Exception("param is abnormal contentPer:" + contentPer + ",countPer:" + countPer
                        + ",limitCount:" + limitCount);
            }




        }


    }

    public List<DBConfig> getDBconfigByIdAndName(List<TaskVo> taskList) {


    }


    public float stringToFloat(String str) throws Exception {
        float toFloat = 0;
        if (str == null) {
            return 1;
        }

        try {
            toFloat = Float.valueOf(str);
        } catch (NumberFormatException e) {
            System.out.println("ERROR: table.percentage.percentage[" + str + "] error.");
            throw new Exception("str to float is exception,str:+" + str + "\n" + e.getMessage());
        }
        return toFloat;

    }


    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public List<TaskVo> getTaskList(@RequestParam("name") String name,
                                    @RequestParam("file") MultipartFile file) throws Exception {
        //dbConfigs.clear();
        List<TaskVo> taskVos = new ArrayList<>();
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
                List<DBConfig> dbConfigList = null;
                String taskId = DigestUtils.md5Hex(file.getInputStream());
                if (tasks.containsKey(taskId)) {
                    dbConfigList = tasks.get(taskId);
                } else {
                    Map<String, List<DBConfig>> dbConfigMap = DBConfigTool.readFromXlsx(file.getInputStream());
                    for (Map.Entry<String, List<DBConfig>> entry : dbConfigMap.entrySet()) {
                        dbConfigList = entry.getValue();
                        tasks.put(entry.getKey(), entry.getValue());
                    }

                }

                for(DBConfig dbConfig:dbConfigList){
                    TaskVo taskVo = new TaskVo();
                    taskVo.setTaskName(dbConfig.getTaskName());
                    taskVo.setId(taskId);
                    taskVos.add(taskVo);
                }


                return taskVos;

                //setTaskFileName(path);
                /*stream.write(bytes);
                stream.close();*/

            } catch (Exception e) {
                throw new Exception("upload task happened exception:" + e.getMessage());
            }
        } else {
            throw new Exception("upload task is null");
        }
    }


}
