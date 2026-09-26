package com.puneet.agentplatform.api;

import com.puneet.agentplatform.task.Task;
import com.puneet.agentplatform.task.TaskService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class TaskMutationController {

    private final TaskService taskService;

    public TaskMutationController(TaskService taskService) {
        this.taskService = taskService;
    }

    @MutationMapping
    public Task createTask(@Argument CreateTaskInput input) {
        return taskService.createTask(input.title(), input.prompt());
    }

    public record CreateTaskInput(
            String title,
            String prompt) {
    }

}
