package com.puneet.agentplatform.ai;

import com.puneet.agentplatform.execution.Execution;
import com.puneet.agentplatform.execution.ExecutionService;
import com.puneet.agentplatform.execution.ExecutionStep;
import com.puneet.agentplatform.execution.ExecutionStepStatus;
import com.puneet.agentplatform.execution.ExecutionStepType;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.Duration;
import java.util.UUID;

@Service
public class AgentRuntimeService {

        private final ExecutionService executionService;
        private final AssistantService assistantService;

        public AgentRuntimeService(
                        ExecutionService executionService,
                        AssistantService assistantService) {

                this.executionService = executionService;
                this.assistantService = assistantService;
        }

        public Execution execute(
                        String executionId,
                        String prompt) {

                executionService.markInProgress(executionId);

                int sequence = 1;

                try {

                        String startedAt = OffsetDateTime.now().toString();

                        ExecutionStep llmStep = new ExecutionStep(
                                        UUID.randomUUID().toString(),
                                        executionId,
                                        sequence,
                                        ExecutionStepType.LLM,
                                        ExecutionStepStatus.IN_PROGRESS,
                                        startedAt,
                                        null,
                                        null);

                        executionService.addStep(
                                        executionId,
                                        llmStep);

                        String result = assistantService.chat(prompt);

                        String completedAt = OffsetDateTime.now().toString();

                        long durationMs = Duration.between(
                                        OffsetDateTime.parse(startedAt),
                                        OffsetDateTime.parse(completedAt))
                                        .toMillis();

                        executionService.updateStep(
                                        executionId,
                                        llmStep.id(),
                                        ExecutionStepStatus.COMPLETED,
                                        completedAt,
                                        durationMs);

                        return executionService.markCompleted(
                                        executionId,
                                        result);

                } catch (Exception exception) {

                        executionService.markFailed(
                                        executionId,
                                        exception.getMessage());

                        throw exception;
                }
        }
}