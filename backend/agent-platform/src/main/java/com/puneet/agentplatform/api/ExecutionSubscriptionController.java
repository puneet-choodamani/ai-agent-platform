
package com.puneet.agentplatform.api;

import com.puneet.agentplatform.execution.ExecutionEvent;
import com.puneet.agentplatform.notification.NotificationService;
import com.puneet.agentplatform.task.TaskService;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;

import reactor.core.publisher.Flux;

@Controller
public class ExecutionSubscriptionController {

    private final NotificationService notificationService;
    private final TaskService taskService;

    public ExecutionSubscriptionController(
            NotificationService notificationService,
            TaskService taskService) {
        this.notificationService = notificationService;
        this.taskService = taskService;
    }

    @SubscriptionMapping
    public Flux<ExecutionUpdate> executionUpdates(
            @Argument String taskId,
            @AuthenticationPrincipal Jwt jwt) {

        // Verify task ownership before subscribing to its events.
        taskService.findByIdAndOwner(taskId, jwt.getSubject());

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
