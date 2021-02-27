package com.example.demo.Controller;

import com.example.demo.Remote;
import com.example.demo.SftpDownload;
import com.example.demo.SftpUpload;
import com.example.demo.tools.*;
import com.jcraft.jsch.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

@Controller
@MultipartConfig
public class HelloController extends HttpServlet {
    @ResponseBody
    //Home page
    @RequestMapping("/home")
    public ModelAndView homePageFunc() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("homePage");
        return modelAndView;
    }

    //响应localhost:8080/indexUpload,上传两个fq文件
    @RequestMapping("/indexUpload")
    public ModelAndView uploadPageFunc() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("uploadRequest");
        return modelAndView;
    }

    //响应submit按钮，上传文件到服务器，并运行指令
    @RequestMapping("/upload")
    public ModelAndView uploadFunc(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //由于将本计算机代替Spring服务器执行算法工具类，故此省略从客户端上传文件到Spring服务器的部分代码
        //设置请求的编码格式
        request.setCharacterEncoding("UTF-8");
        //获取文件
        Part part1 = request.getPart("file1");
        Part part2 = request.getPart("file2");
        //获取文件的文件名
        String fileName1 = part1.getSubmittedFileName();
        String fileName2 = part2.getSubmittedFileName();
        //提示上传成功
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("submitCmd");
        modelAndView.addObject("name", fileName1+"已上传");
        modelAndView.addObject("name1", fileName2+"已上传");

        //调用算法在Spring服务器上拆分fastq文件
        String springPath = "E:\\Cache\\";
        SampleSplitAlgo.split(springPath+"fq\\", springPath+"name.txt", springPath+fileName1, springPath+fileName2);
        //connect to a channel
        ChannelSftp channel = SftpUpload.connect();
        for (int i = 0; i < 96; i++) {
            //将文件转为输入流
            fileName1 = i + "_1.fq";
            fileName2 = i + "_2.fq";
            File file1 = new File(springPath + "fq\\" + fileName1);
            File file2 = new File(springPath + "fq\\" + fileName2);
            InputStream is1 = new FileInputStream(file1);
            InputStream is2 = new FileInputStream(file2);
            //上传文件到服务器
            SftpUpload.transfer(channel, is1, fileName1);
            SftpUpload.transfer(channel, is2, fileName2);
            //在服务器上依次运行bwa,samtools,igvtools模块，得到wig文件
            Bam.pipe(fileName1, fileName2, i);
            Bam.index(i);
            Igvtools.count(i);
        }
        //disconnect from channel
        SftpUpload.disconnect(channel);
        return modelAndView;
    }

    //响应localhost:8080/indexDownload，用于下载已经处理后的文件
    @RequestMapping("/indexDownload")
    public ModelAndView downloadPageFunc() throws Exception {
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
    public ModelAndView downloadFunc(String selectedFile) throws Exception {
        //将文件下载到指定目录
        SftpDownload.transfer("C:\\Users\\27142\\Desktop\\download\\"+selectedFile, "/data/home/vip494/" + selectedFile);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("done");
        modelAndView.addObject("none", "开始下载...");
        return modelAndView;
    }
}
