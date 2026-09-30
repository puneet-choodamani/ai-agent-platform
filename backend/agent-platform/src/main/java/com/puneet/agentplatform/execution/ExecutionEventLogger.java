package com.puneet.agentplatform.execution;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ExecutionEventLogger {

    private static final Logger log = LoggerFactory.getLogger(ExecutionEventLogger.class);

    @EventListener
    public void handleExecutionEvent(ExecutionEvent event) {
           log.info(
                "execution_event eventId={} executionId={} taskId={} type={} occurredAt={} metadata={}",
                event.id(),
                event.executionId(),
                event.taskId(),
                event.eventType(),
                event.occurredAt(),
                event.metadata());
    }

}
