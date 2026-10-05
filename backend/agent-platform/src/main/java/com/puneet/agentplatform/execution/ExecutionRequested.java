package com.puneet.agentplatform.execution;

public record ExecutionRequested(
        String executionId,
        String taskId
) {
}
