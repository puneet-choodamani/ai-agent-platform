package com.puneet.agentplatform.execution;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import com.puneet.agentplatform.task.Task;

import java.util.List;

@Controller
public class ExecutionQueryController {
    private final ExecutionService executionService;

    public ExecutionQueryController(ExecutionService executionService) {
        this.executionService = executionService;
    }

    @QueryMapping
    public List<Execution> executions(@Argument String taskId) {
        return executionService.findTaskById(taskId);
    }

    @QueryMapping
    public Execution execution(@Argument String id) {
        return executionService.findById(id);
    }

    @SchemaMapping(typeName = "Task", field = "executions")
    public List<Execution> executionsForTask(Task task) {
        return executionService.findTaskById(task.id());
    }
}
