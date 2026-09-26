package com.puneet.agentplatform.task;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TaskService {

    private final Map<String, Task> tasks = new ConcurrentHashMap<>();

    public TaskService() {

        createTask(
                "Build AI Agent Platform",
                "Build a general-purpose AI agent platform using Java and React");

    }

    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    public Task findById(String id) {
        return tasks.get(id);
    }

    public Task createTask(String title, String prompt) {
        String id = UUID.randomUUID().toString();

        Task task = new Task(
                id,
                title,
                prompt,
                TaskStatus.CREATED);

        tasks.put(id, task);

        return task;
    }

}
