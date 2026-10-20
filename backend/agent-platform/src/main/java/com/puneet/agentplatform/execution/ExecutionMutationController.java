package com.puneet.agentplatform.execution;

import com.puneet.agentplatform.ai.AgentRuntimeService;
import com.puneet.agentplatform.task.Task;
import com.puneet.agentplatform.task.TaskService;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

@Controller
public class ExecutionMutationController {

    private final ExecutionService executionService;
    private final TaskService taskService;
    private final ExecutionRequestProducer executionRequestProducer;

    public ExecutionMutationController(
            ExecutionService executionService,
            TaskService taskService,
            ExecutionRequestProducer executionRequestProducer) {

        this.executionService = executionService;
        this.taskService = taskService;
        this.executionRequestProducer = executionRequestProducer;
    }

    
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @MutationMapping
    public Execution startExecution(
            @Argument String taskId,
            @AuthenticationPrincipal Jwt jwt) {

        Task task = taskService.findById(taskId);

        if (task == null) {
            throw new IllegalArgumentException(
                    "Task not found: " + taskId);
        }

        boolean isAdmin = jwt.getClaimAsMap("realm_access") != null
                && jwt.getClaimAsMap("realm_access").get("roles") instanceof java.util.List<?> roles
                && roles.contains("ADMIN");

        if (!isAdmin && !task.ownerId().equals(jwt.getSubject())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You are not authorized to execute this task");
        }

        Execution execution = executionService.createExecution(taskId);
        Execution queuedExecution = executionService.markQueued(execution.id());

        executionRequestProducer.publish(
                new ExecutionRequested(execution.id(), taskId));

        return queuedExecution;
    }

}