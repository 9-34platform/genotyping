package com.example.demo.connection;

import com.example.demo.connection.Remote;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

public class SftpDownload {
    public static void transfer(String localPath, String remoteFile) throws Exception {
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
        //通过sftp方式连接
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
        //download file
        OutputStream outputStream = new FileOutputStream(localPath);
        channel.get(remoteFile, outputStream);
        //关闭流
        outputStream.close();
    }
}
