package com.example.demo;

import com.jcraft.jsch.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Remote {

    public final String user = "vip494";
    public final String host = "118.24.216.223";
    public final int port = 22;
    public final String password = "pd6704";

    public static List<String> remoteExecute(Session session, String command) throws JSchException {
        //return String.format(">> %s", command);
        ChannelExec channel = null;
        List<String> resultLines = new ArrayList<>();
        try {
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            InputStream input = channel.getInputStream();
            channel.connect(5000);
            try {
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(input));
                String inputLine;
                while ((inputLine = inputReader.readLine()) != null) {
                    resultLines.add(inputLine);
                    //return String.format("  %s", inputLine);
                }
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (Exception e) {
                        System.out.println(String.format("JSch inputStream close error: %s", e));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(String.format("IOException: %s", e));
        } finally {
            if (channel != null) {
                try {
                    channel.disconnect();
                } catch (Exception e) {
                    System.out.println(String.format("JSch inputStream close error: %s", e));
                }
            }
        }
        return resultLines;
    }

    public static List<String> connect(String cmd) throws Exception {

        JSch jSch = new JSch();
        Remote remote = new Remote();

        Session session = jSch.getSession(remote.user, remote.host, remote.port);
        session.setPassword(remote.password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(5000);
        if (session.isConnected()) {
            return remoteExecute(session, cmd);
            //return String.format("Host(%s) connected", remote.host);
        }

        session.disconnect();

        List<String> emptyLines = new ArrayList<>();
        return emptyLines;
    }
}