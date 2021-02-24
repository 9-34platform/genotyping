package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.connection.Remote;
import com.example.demo.connection.SftpDownload;
import com.example.demo.connection.SftpUpload;
import com.example.demo.tools.Bwa;
import com.example.demo.tools.Igvtools;
import com.example.demo.tools.Samtools;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.jws.WebParam;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import static com.example.demo.tools.ReadJson.readJsonFile;

@Controller
@MultipartConfig
public class HelloController extends HttpServlet {
    @ResponseBody

    @RequestMapping("/index")
    public ModelAndView process(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("process");
        return modelAndView;
    }
    //获取json文件
    @RequestMapping("/getJson")
    public ModelAndView getJson(HttpServletRequest request, HttpServletResponse response) throws Exception{
        request.setCharacterEncoding("UTF-8");
        String str = request.getParameter("myJson");
        OutputStream os = new FileOutputStream("src\\main\\resources\\json\\demo.json");
        PrintWriter pw = new PrintWriter(os);
        pw.write(str);
        pw.flush();
        os.close();
        pw.close();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("submitName");
        return modelAndView;
    }
    //连接服务器，根据json文件的指令调用算法
    @RequestMapping("/upload")
    public ModelAndView exe(HttpServletRequest request, HttpServletResponse response) throws Exception {

        //设置请求的编码格式
        request.setCharacterEncoding("UTF-8");
        //获取文件
        Part part1 = request.getPart("file1");
        Part part2 = request.getPart("file2");
        //得到上传文件的文件名
        String fileName1 = part1.getSubmittedFileName();
        String fileName2 = part2.getSubmittedFileName();
        //将文件转为输入流
        InputStream is1 = part1.getInputStream();
        InputStream is2 = part2.getInputStream();
        //上传文件到服务器
        SftpUpload.transfer(is1,fileName1);
        SftpUpload.transfer(is2,fileName2);
        //读取json文件
        String path = "src\\main\\resources\\json\\demo.json";
        String s = readJsonFile(path);
        JSONObject jobj = JSON.parseObject(s);
        //构建json数组
        JSONArray arrs = jobj.getJSONArray("nodeDataArray");
        for (Object arr : arrs) {
            JSONObject key = (JSONObject) arr;
            String category = (String) key.get("category");
            //识别到BWA算法
            if (category != null && category.equals("algorithm") && key.get("text").equals("BWA")) {
                Bwa.bwa(fileName1,fileName2);
                System.out.println("调用了BWA");
            }
            //识别到SAMTools算法
            if (category != null && category.equals("algorithm") && key.get("text").equals("SAMTools")) {
                Samtools.view();
                Samtools.sort();
                Samtools.index();
                System.out.println("调用了SAMTools");
            }
            //识别到IGVTools算法
            if (category != null && category.equals("algorithm") && key.get("text").equals("IGVTools")) {
                Igvtools.igvtools();
                System.out.println("调用了IGVTools");
            }
        }
        //提示上传成功
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("submitCmd");
        modelAndView.addObject("name",fileName1 + "已上传");
        modelAndView.addObject("name1",fileName2 + "已上传");
        return modelAndView;
    }

    //响应localhost:8080/indexDownload，用于下载已经处理后的文件
    @RequestMapping("/indexDownload")
    public ModelAndView directory() throws Exception {

        //查看当前目录可供下载的文件
        ModelAndView modelAndView = new ModelAndView();
        List<String> remoteString = Remote.connect("ls -l | grep ^-");
        modelAndView.setViewName("selectFile");
        modelAndView.addObject("dirFile", remoteString);
        return modelAndView;
    }

    //跳转到下载页面
    @RequestMapping("/turn")
    public ModelAndView directory1() throws Exception{
        //查看当前目录可供下载的文件
        ModelAndView modelAndView = new ModelAndView();
        List<String> remoteString = Remote.connect("ls -l | grep ^-");
        modelAndView.setViewName("selectFile");
        modelAndView.addObject("dirFile", remoteString);
        return modelAndView;
    }
    //响应下载按钮
    @RequestMapping("/download")
    public ModelAndView down(String selectedFile) throws Exception {
        //将文件下载到指定目录
        SftpDownload.transfer("C:\\Users\\27142\\Desktop\\download\\"+selectedFile, "/data/home/vip494/" + selectedFile);
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
    @RequestMapping("/flowchart")
    public ModelAndView flowchart(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("Flowchart");
        return modelAndView;
    }


}
