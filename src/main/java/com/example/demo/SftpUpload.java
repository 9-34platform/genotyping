package com.example.demo;

import com.jcraft.jsch.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SftpUpload {

    public static ChannelSftp connect() throws JSchException {
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
        return channel;
    }

    public static void transfer(ChannelSftp channel, InputStream is, String fileName) throws SftpException, JSchException, IOException {
        //Directory for storing Fastq files
        String fqDir = "/data/home/vip494/user0/fq/";
        //upload file stream
        channel.put(is, fqDir + fileName);
        is.close();
    }

    public static void disconnect(ChannelSftp channel) {
        channel.disconnect();
    }

}
