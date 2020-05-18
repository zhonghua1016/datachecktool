package com.example.datacheck.controller;

import com.example.datacheck.ConstantTool;
import com.example.datacheck.config.DBConfig;
import com.example.datacheck.config.DBConfigTool;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
    public static List<DBConfig> dbConfigs = null;

    @GetMapping("/dbcheck")
    public String homePage() {
        return "home.html";
    }


    @PostMapping("/start")
    @ResponseBody
    public void startCheck(@RequestParam(value = "contentPer") String contentPer,
                           @RequestParam(value = "countPer") String countPer,
                           @RequestParam(value = "limitCount") String limitCount,
                           @RequestBody List<DBConfig> taskList) {
        if(taskList!=null&&taskList.size()>0){

        }


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


    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public List<DBConfig> getTaskList(@RequestParam("name") String name,
                            @RequestParam("file") MultipartFile file)throws Exception {
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
                return dbConfigList;
            } catch (Exception e) {
                throw  new Exception("upload task happened exception:"+e.getMessage());
            }
        } else {
            throw  new Exception("upload task is null");
        }
    }



}
