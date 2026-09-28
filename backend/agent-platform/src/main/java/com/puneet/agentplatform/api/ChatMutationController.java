package com.puneet.agentplatform.api;

import com.puneet.agentplatform.ai.AssistantService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ChatMutationController {

    private final AssistantService assistantService;

    public ChatMutationController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @MutationMapping
    public ChatResponse chat(@Argument ChatInput input) {
        String response = assistantService.chat(input.message());
        return new ChatResponse(response);
    }

    public record ChatInput(String message) {
    }

    public record ChatResponse(String message) {
    }
}