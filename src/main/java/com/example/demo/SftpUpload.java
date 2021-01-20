package com.example.demo;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.Properties;

public class SftpUpload {
    public static void transfer(String pathStr, String nameStr) throws Exception {
        Remote remote = new Remote();
        JSch jSch = new JSch();
        //建立一个Linux session
        Session session = jSch.getSession(remote.user, remote.host, remote.port);
        session.setPassword(remote.password);
        //关闭key的检验
        Properties sshConfig = new Properties();
        sshConfig.put("StrictHostKeyChecking", "no");
        session.setConfig(sshConfig);
        //连接Linux
        session.connect();
        //通过sftp的方式连接
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
        //upload file
        File file = new File(pathStr);
        InputStream inputStream = new FileInputStream(file);
        channel.put(inputStream, "/data/home/vip494/" + nameStr);
        /*
         //download file
         *OutputStream outputStream = new FileOutputStream("本地路径")
         *channel.get("服务器路径", outputStream);
         */
        //关闭流
        inputStream.close();
        //outputStream.close();
    }
}