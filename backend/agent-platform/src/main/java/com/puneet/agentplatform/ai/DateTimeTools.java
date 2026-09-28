package com.puneet.agentplatform.ai;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class DateTimeTools {

    @Tool(description = "Get the current date and time")
    public String currentDateTime() {
        return OffsetDateTime.now().toString();
    }
}
