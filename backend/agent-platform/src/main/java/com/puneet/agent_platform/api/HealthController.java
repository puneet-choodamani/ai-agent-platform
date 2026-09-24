package com.puneet.agent_platform.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "AI Agent Platform is running";
    }

     @GetMapping("/")
    public String home() {
        return "AI Agent Platform API";
    }
}