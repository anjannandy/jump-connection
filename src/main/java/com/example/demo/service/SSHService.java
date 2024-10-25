package com.example.demo.service;

import com.example.demo.util.SSHUtil;
import com.example.demo.util.TelnetUtil;
import org.springframework.stereotype.Service;

@Service
public class SSHService {

    public String runCommand(String host, int port, String user, String password, String command) {
        try {
            return SSHUtil.executeCommandWithJump(host, port, user, password, command);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    public String runCommandForTelnet(String host, int port, String user, String password, String command) {
        try {
            return TelnetUtil.executeTelnetWithJump(host, port, user, password, command);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}