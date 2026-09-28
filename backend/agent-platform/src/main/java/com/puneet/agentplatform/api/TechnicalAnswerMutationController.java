package com.puneet.agentplatform.api;

import com.puneet.agentplatform.ai.AssistantService;
import com.puneet.agentplatform.ai.TechnicalAnswer;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class TechnicalAnswerMutationController {

    private final AssistantService assistantService;

    public TechnicalAnswerMutationController(
            AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @MutationMapping
    public TechnicalAnswer technicalAnswer(
            @Argument ChatInput input) {

        return assistantService.answer(input.message());
    }

    public record ChatInput(String message) {
    }
}