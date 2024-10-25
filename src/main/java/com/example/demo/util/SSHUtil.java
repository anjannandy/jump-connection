package com.example.demo.util;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;

public class SSHUtil {

    public static String executeCommand(String host, int port, String user, String password, String command) throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(user, host, port);
        session.setPassword(password);

        // Avoid asking for key confirmation
        session.setConfig("StrictHostKeyChecking", "no");

        session.connect();

        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);

        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
        channel.setOutputStream(responseStream);

        channel.connect();

        while (channel.isConnected()) {
            Thread.sleep(100);
        }

        String response = new String(responseStream.toByteArray());
        channel.disconnect();
        session.disconnect();

        return response;
    }

    public static Session createJumpSession(String jumpHost, int jumpPort, String jumpUser, String jumpPassword) throws Exception {
        JSch jsch = new JSch();
        Session jumpSession = jsch.getSession(jumpUser, jumpHost, jumpPort);
        jumpSession.setPassword(jumpPassword);
        jumpSession.setConfig("StrictHostKeyChecking", "no");
        jumpSession.connect();
        return jumpSession;
    }

    public static String executeCommandWithJump(String destHost, int destPort, String destUser, String destPassword, String command) throws Exception {
        JSch jsch = new JSch();
        Session jumpSession = createJumpSession("localhost", 2225, "jumpuser", "password");

        int assignedPort = jumpSession.setPortForwardingL(4501, destHost, destPort);

        Session destSession = jsch.getSession(destUser, "localhost", assignedPort);
        destSession.setPassword(destPassword);
        destSession.setConfig("StrictHostKeyChecking", "no");
        destSession.connect();

        ChannelExec channel = (ChannelExec) destSession.openChannel("exec");
        channel.setCommand(command);

        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
        channel.setOutputStream(responseStream);

        channel.connect();

        while (channel.isConnected()) {
            Thread.sleep(100);
        }

        String response = new String(responseStream.toByteArray());
        channel.disconnect();
        destSession.disconnect();
        jumpSession.disconnect();

        return response;
    }
}
