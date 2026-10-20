
package com.puneet.agentplatform.execution;

import com.puneet.agentplatform.task.Task;
import com.puneet.agentplatform.task.TaskService;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ExecutionQueryController {

    private final ExecutionService executionService;
    private final TaskService taskService;

    public ExecutionQueryController(
            ExecutionService executionService,
            TaskService taskService) {
        this.executionService = executionService;
        this.taskService = taskService;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @QueryMapping
    public List<Execution> executions(
            @Argument String taskId,
            @AuthenticationPrincipal Jwt jwt) {

        Task task = getAuthorizedTask(taskId, jwt);

        if (task == null) {
            return List.of();
        }

        return executionService.findByTaskId(taskId);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @QueryMapping
    public Execution execution(
            @Argument String id,
            @AuthenticationPrincipal Jwt jwt) {

        Execution execution = executionService.findById(id);

        if (execution == null) {
            return null;
        }

        Task task = getAuthorizedTask(execution.taskId(), jwt);

        return task == null ? null : execution;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @SchemaMapping(typeName = "Task", field = "executions")
    public List<Execution> executionsForTask(
            Task task,
            @AuthenticationPrincipal Jwt jwt) {

        Task authorizedTask = getAuthorizedTask(task.id(), jwt);

        if (authorizedTask == null) {
            return List.of();
        }

        return executionService.findByTaskId(task.id());
    }

    private Task getAuthorizedTask(String taskId, Jwt jwt) {
        if (jwt.getClaimAsMap("realm_access") != null) {
            Object rolesObject =
                    jwt.getClaimAsMap("realm_access").get("roles");

            if (rolesObject instanceof List<?> roles
                    && roles.contains("ADMIN")) {
                return taskService.findById(taskId);
            }
        }

        return taskService.findByIdAndOwner(taskId, jwt.getSubject());
    }
}
