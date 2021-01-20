package com.example.demo;

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
        Session session = jSch.getSession(remote.user, remote.host, remote.port);
        session.setPassword(remote.password);
        Properties sshConfig = new Properties();
        sshConfig.put("StrictHostKeyChecking", "no");
        session.setConfig(sshConfig);
        session.connect();
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
        //download file
        OutputStream outputStream = new FileOutputStream(localPath);
        channel.get(remoteFile, outputStream);
        //关闭流
        outputStream.close();
    }
}
