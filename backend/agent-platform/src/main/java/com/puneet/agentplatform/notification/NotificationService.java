package com.puneet.agentplatform.notification;

import com.puneet.agentplatform.execution.ExecutionEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import org.springframework.context.event.EventListener;

@Service
public class NotificationService {

      private final Sinks.Many<ExecutionEvent> eventSink =
        Sinks.many()
                .multicast()
                .onBackpressureBuffer(256, false);

        @EventListener
        public void handleExecutionEvent(ExecutionEvent event) {
                eventSink.tryEmitNext(event);
        }

        public Flux<ExecutionEvent> subscribe(String taskId) {

                return eventSink.asFlux()
                                .filter(event -> event.taskId().equals(taskId));
        }

}