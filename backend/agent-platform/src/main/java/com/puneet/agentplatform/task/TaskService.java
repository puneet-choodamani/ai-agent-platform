
package com.puneet.agentplatform.task;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TaskService {

    private final Map<String, Task> tasks = new ConcurrentHashMap<>();

    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    public List<Task> findAllByOwner(String ownerId) {
        return tasks.values().stream()
                .filter(task -> ownerId.equals(task.ownerId()))
                .toList();
    }

    public Task findById(String id) {
        return tasks.get(id);
    }

    public Task findByIdAndOwner(String id, String ownerId) {
        Task task = tasks.get(id);

        if (task == null || !ownerId.equals(task.ownerId())) {
            return null;
        }

        return task;
    }

    public Task createTask(String title, String prompt, String ownerId) {
        String id = UUID.randomUUID().toString();

        Task task = new Task(
                id,
                title,
                prompt,
                TaskStatus.CREATED,
                ownerId);

        tasks.put(id, task);
        return task;
    }
}
