package com.example.demo.util;

import com.jcraft.jsch.Session;
import org.apache.commons.net.telnet.TelnetClient;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

public class TelnetUtil {

    public static String executeCommand(String host, int port, String user, String password, String command) throws Exception {
        TelnetClient telnet = new TelnetClient();
        telnet.connect(host, port);

        InputStream in = telnet.getInputStream();
        OutputStream out = telnet.getOutputStream();

        // Wait for login prompt
        System.out.println("Waiting for login prompt...");
        waitForPrompt(in, "login: ");
        System.out.println("Sending username...");
        write(out, user);

        // Wait for password prompt
        System.out.println("Waiting for password prompt...");
        waitForPrompt(in, "Password:");
        System.out.println("Sending password...");
        write(out, password);

        // Wait for command prompt
        System.out.println("Waiting for command prompt...");
        waitForPrompt(in, "$ "); // Assuming the command prompt ends with "$ "

        // Execute command
        System.out.println("Executing command...");
        write(out, command);

        // Wait for command output
        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
        waitForPrompt(in, "$ ", responseStream); // Capture the command output

        telnet.disconnect();

        // Clean up the response to remove terminal control characters
        String response = responseStream.toString();
        response = response.replaceAll("\\e\\[[\\d;]*[^\\d;]", ""); // Remove ANSI escape codes

        return response;
    }

    private static void write(OutputStream out, String value) throws Exception {
        out.write((value + "\n").getBytes());
        out.flush();
    }

    private static void waitForPrompt(InputStream in, String prompt) throws Exception {
        waitForPrompt(in, prompt, null);
    }

    private static void waitForPrompt(InputStream in, String prompt, ByteArrayOutputStream responseStream) throws Exception {
        byte[] buffer = new byte[1024];
        int bytesRead;
        StringBuilder sb = new StringBuilder();

        while ((bytesRead = in.read(buffer)) != -1) {
            String received = new String(buffer, 0, bytesRead);
            sb.append(received);
            if (responseStream != null) {
                responseStream.write(buffer, 0, bytesRead);
            }
            System.out.println("Received: " + sb.toString());
            if (sb.toString().contains(prompt)) {
                break;
            }
        }
    }

    public static String executeTelnetWithJump(
                                               String telnetHost, int telnetPort, String telnetUser, String telnetPassword,
                                               String command) throws Exception {
        // Create a jump session
        Session jumpSession = SSHUtil.createJumpSession("localhost", 2225, "jumpuser", "password");

        // Forward a local port to the telnet server through the jump server
        int assignedPort = jumpSession.setPortForwardingL(4561, telnetHost, telnetPort);

        // Execute the telnet command through the forwarded port
        String response = executeTelnet("localhost", assignedPort, telnetUser, telnetPassword, command);

        // Disconnect the jump session
        jumpSession.disconnect();

        return response;
    }

    private static String executeTelnet(String host, int port, String user, String password, String command) throws Exception {
        TelnetClient telnet = new TelnetClient();
        telnet.connect(host, port);

        InputStream in = telnet.getInputStream();
        OutputStream out = telnet.getOutputStream();

        // Wait for login prompt
        waitForPrompt(in, "login: ");
        write(out, user);

        // Wait for password prompt
        waitForPrompt(in, "Password:");
        write(out, password);

        // Wait for command prompt
        waitForPrompt(in, "$ "); // Assuming the command prompt ends with "$ "

        // Execute command
        write(out, command);

        // Wait for command output
        ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
        waitForPrompt(in, "$ ", responseStream); // Capture the command output

        telnet.disconnect();

        // Clean up the response to remove terminal control characters
        String response = responseStream.toString();
        response = response.replaceAll("\\e\\[[\\d;]*[^\\d;]", ""); // Remove ANSI escape codes

        return response;
    }
}