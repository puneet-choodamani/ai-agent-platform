package com.puneet.agentplatform.execution;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

public record ExecutionEvent(
        String id,
        String executionId,
        String taskId,
        ExecutionEventType eventType,
        String occurredAt,
        String traceId,
        Map<String, Object> metadata) {

            
    public ExecutionEvent(
            String executionId,
            String taskId,
            ExecutionEventType type,
            String traceId,
            Map<String, Object> metadata) {

        this(
                UUID.randomUUID().toString(),
                executionId,
                taskId,
                type,
                OffsetDateTime.now().toString(),
                traceId,
                metadata);
    }

}
