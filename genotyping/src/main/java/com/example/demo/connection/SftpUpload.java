package com.example.demo.connection;

import com.example.demo.connection.Remote;
import com.jcraft.jsch.*;

import java.io.InputStream;
import java.util.Properties;

public class SftpUpload {
    public static void transfer(InputStream is, String fileName) throws SftpException, JSchException {
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
        channel.put(is, "/data/home/vip494/" + fileName);
    }
}
