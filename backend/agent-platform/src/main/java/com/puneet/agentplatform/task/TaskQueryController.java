package com.puneet.agentplatform.task;

import org.springframework.stereotype.Controller;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;

import com.puneet.agentplatform.task.*;
import java.util.List;

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

    @QueryMapping
    public List<Task> tasks() {
        return taskService.findAll();
    }

    @QueryMapping
    public Task task(@Argument String id) {
        return taskService.findById(id);
    }

}
