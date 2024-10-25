package com.example.demo.controller;

import com.example.demo.service.SSHService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SSHController {

    private final SSHService sshService;

    @GetMapping("/execute")
    public String executeSshCommand(@RequestParam String host, @RequestParam int port, @RequestParam String user, @RequestParam String password, @RequestParam String command) {
        return sshService.runCommand(host, port, user, password, command);
    }

    @GetMapping("/executeTelnet")
    public String executeTelnetCommand(@RequestParam String host, @RequestParam int port, @RequestParam String user, @RequestParam String password, @RequestParam String command) {
        return sshService.runCommandForTelnet(host, port, user, password, command);
    }
}
