package com.puneet.agentplatform.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AssistantService {

    private final ChatClient chatClient;

    public AssistantService(ChatClient.Builder builder, DateTimeTools dateTimeTools) {
        this.chatClient = builder
                .defaultSystem("""
                        You are the AI assistant for the AI Agent Platform.

                        Be accurate and concise.
                        Do not invent facts.
                        When a term is ambiguous, indicate that
                        clarification may be required.

                        Return the requested information in the
                        specified structured format.
                        """)
                .defaultTools(dateTimeTools)
                .build();
    }

    public TechnicalAnswer answer(String message) {

        return chatClient
                .prompt()
                .user(message)
                .call()
                .entity(
                        TechnicalAnswer.class,
                        spec -> spec
                                .useProviderStructuredOutput()
                                .validateSchema());
    }

    public String chat(String message) {
        return chatClient
                .prompt()
                .user(message)
                .call()
                .content();
    }
}