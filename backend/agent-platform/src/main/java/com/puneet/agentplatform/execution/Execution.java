package com.puneet.agentplatform.execution;

import java.util.List;

public record Execution(
        String id,
        String taskId,
        ExecutionStatus status,
        String createdAt,
        String startedAt,
        String completedAt,
        String result,
        String errorMessage,
        List<ExecutionStep> steps) {

}
