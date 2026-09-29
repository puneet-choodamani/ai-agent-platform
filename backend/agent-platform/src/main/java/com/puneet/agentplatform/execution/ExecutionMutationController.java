package com.puneet.agentplatform.execution;

import com.puneet.agentplatform.task.Task;
import com.puneet.agentplatform.task.TaskService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ExecutionMutationController {

    private final ExecutionService executionService;
    private final TaskService taskService;

    public ExecutionMutationController(
            ExecutionService executionService,
            TaskService taskService) {
        this.executionService = executionService;
        this.taskService = taskService;
    }

    @MutationMapping
    public Execution startExecution(@Argument String taskId) {

        Task task = taskService.findById(taskId);

        if (task == null) {
            throw new IllegalArgumentException(
                    "Task not found: " + taskId);
        }

        return executionService.startExecution(taskId);
    }
}