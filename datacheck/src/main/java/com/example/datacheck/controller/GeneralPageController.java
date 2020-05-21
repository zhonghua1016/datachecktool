package com.example.datacheck.controller;

import com.example.datacheck.ConstantTool;
import com.example.datacheck.service.DataCheckTask;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;

/**
 * created by yuanjunjie on 2019/6/7 6:00 PM
 */
@Controller
public class GeneralPageController {
    @RequestMapping("{module}/{url}.html")
    public String module(@PathVariable("module") String module, @PathVariable("url") String url){
        return module + "/" + url;
    }

    @RequestMapping("{modules}/{module}/{url}.html")
    public String module(@PathVariable("modules") String modules, @PathVariable("module") String module, @PathVariable("url") String url){
        return modules+"/"+module + "/" + url;
    }

    @RequestMapping("{url}.html")
    public String url(@PathVariable("url") String url){
        return url;
    }

    @RequestMapping("/")
    public String index(){
        return "main";
    }
    @GetMapping("/result/file")
    public void getFile(HttpSession session, HttpServletResponse httpServletResponse) throws Throwable{

        File file = new File(DataCheckTask.resultBasePath);

        InputStream ins = new FileInputStream(file);
        /* 设置文件ContentType类型，这样设置，会自动判断下载文件类型 */
        httpServletResponse.setContentType("multipart/form-data");
        /* 设置文件头：最后一个参数是设置下载文件名 */
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename="+file.getName());
        try{
            OutputStream os = httpServletResponse.getOutputStream();
            byte[] b = new byte[1024];
            int len;
            while((len = ins.read(b)) > 0){
                os.write(b,0,len);
            }
            os.flush();
            os.close();
            ins.close();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }

    }

}
