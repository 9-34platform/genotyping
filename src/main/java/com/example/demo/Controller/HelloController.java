package com.example.demo.Controller;

import com.example.demo.Remote;
import com.example.demo.SftpDownload;
import com.example.demo.SftpUpload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import java.util.List;

@Controller
public class HelloController {
    @ResponseBody
    @RequestMapping("/indexUpload")
    public ModelAndView submit() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("submitName");
        return modelAndView;
    }
    @RequestMapping("/upload")
    public ModelAndView exe(String nameStr) throws Exception {
        SftpUpload.transfer("E:\\Cache\\" + nameStr, nameStr);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("submitCmd");
        modelAndView.addObject("name",nameStr + "已上传");
        String cmd = "/data/home/vip494/bwa-0.7.17/bwa index " + nameStr;
        List<String> remoteString = Remote.connect(cmd);
        System.out.println(remoteString);
        return modelAndView;
    }
    @RequestMapping("/indexDownload")
    public ModelAndView directory() throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        List<String> remoteString = Remote.connect("ls -l | grep ^-");
        modelAndView.setViewName("selectFile");
        modelAndView.addObject("dirFile", remoteString);
        return modelAndView;
    }
    @RequestMapping("/download")
    public ModelAndView down(String selectedFile) throws Exception {
        System.out.println(selectedFile);
        SftpDownload.transfer("E:\\Cache\\", "/data/home/vip494/" + selectedFile);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("done");
        modelAndView.addObject("none", "开始下载...");
        return modelAndView;
    }
    @RequestMapping("/return")
    public ModelAndView service(String cmd) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        List<String> remoteString = Remote.connect(cmd);
        modelAndView.setViewName("command");
        modelAndView.addObject("results", remoteString);
        System.out.println(remoteString);
        return modelAndView;
    }
}
