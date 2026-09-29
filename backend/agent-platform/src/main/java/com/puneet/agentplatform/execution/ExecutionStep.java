package com.puneet.agentplatform.execution;




public record ExecutionStep(
        String id,
        String executionId,
        int sequence,
        ExecutionStepType type,
        ExecutionStepStatus status,
        String startedAt,
        String completedAt,
        Long durationMs) {
}
