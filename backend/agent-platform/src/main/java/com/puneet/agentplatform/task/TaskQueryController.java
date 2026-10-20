
package com.puneet.agentplatform.task;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Controller
public class TaskQueryController {

    private final TaskService taskService;

    public TaskQueryController(TaskService taskService) {
        this.taskService = taskService;
    }

    @QueryMapping
    public String health() {
        return "AI Agent Platform GraphQL API is running";
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @QueryMapping
    public List<Task> tasks(@AuthenticationPrincipal Jwt jwt) {
        if (isAdmin(jwt)) {
            return taskService.findAll();
        }

        return taskService.findAllByOwner(jwt.getSubject());
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @QueryMapping
    public Task task(
            @Argument String id,
            @AuthenticationPrincipal Jwt jwt) {

        if (isAdmin(jwt)) {
            return taskService.findById(id);
        }

        return taskService.findByIdAndOwner(id, jwt.getSubject());
    }

    private boolean isAdmin(Jwt jwt) {
        Map<String, Object> realmAccess =
                jwt.getClaimAsMap("realm_access");

        if (realmAccess == null) {
            return false;
        }

        Object rolesObject = realmAccess.get("roles");

        if (!(rolesObject instanceof Collection<?> roles)) {
            return false;
        }

        return roles.contains("ADMIN");
    }
}
