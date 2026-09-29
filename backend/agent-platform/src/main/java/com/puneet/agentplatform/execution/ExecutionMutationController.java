package com.puneet.agentplatform.execution;

import com.puneet.agentplatform.ai.AgentRuntimeService;
import com.puneet.agentplatform.task.Task;
import com.puneet.agentplatform.task.TaskService;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ExecutionMutationController {

    private final ExecutionService executionService;
    private final TaskService taskService;
    private final AgentRuntimeService agentRuntimeService;

    public ExecutionMutationController(
            ExecutionService executionService,
            TaskService taskService,
            AgentRuntimeService agentRuntimeService) {

        this.executionService = executionService;
        this.taskService = taskService;
        this.agentRuntimeService = agentRuntimeService;
    }

    @MutationMapping
    public Execution startExecution(
            @Argument String taskId) {

        Task task = taskService.findById(taskId);

        if (task == null) {
            throw new IllegalArgumentException(
                    "Task not found: " + taskId);
        }

        Execution execution =
                executionService.createExecution(taskId);

        return agentRuntimeService.execute(
                execution.id(),
                task.prompt());
    }
}