package com.puneet.agentplatform.api;

import com.puneet.agentplatform.execution.ExecutionEvent;
import com.puneet.agentplatform.notification.NotificationService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
public class ExecutionSubscriptionController {

        private final NotificationService notificationService;

        public ExecutionSubscriptionController(
                        NotificationService notificationService) {
                this.notificationService = notificationService;
        }

        @SubscriptionMapping
        public Flux<ExecutionUpdate> executionUpdates(
                        @Argument String taskId) {

                System.out.println(
                                "SUBSCRIPTION CREATED FOR TASK: " + taskId);

                return notificationService.subscribe(taskId)
                                .map(event -> new ExecutionUpdate(
                                                event.id(),
                                                event.executionId(),
                                                event.taskId(),
                                                event.eventType().name(),
                                                event.occurredAt()));
        }

        public record ExecutionUpdate(
                        String eventId,
                        String executionId,
                        String taskId,
                        String type,
                        String occurredAt) {
        }
}