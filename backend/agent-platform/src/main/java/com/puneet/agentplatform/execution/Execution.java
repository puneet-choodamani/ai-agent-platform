package com.puneet.agentplatform.execution;

import java.util.List;

public record Execution(
        String id,
        String taskId,
        ExecutionStatus status,
        String createdAt,
        List<ExecutionStep> steps) {

}
