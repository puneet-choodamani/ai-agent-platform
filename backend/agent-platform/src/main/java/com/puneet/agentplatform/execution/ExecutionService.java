package com.puneet.agentplatform.execution;

import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExecutionService {

    private final Map<String, Execution> executions = new ConcurrentHashMap<>();

    public Execution createExecution(String taskId) {
        String id = UUID.randomUUID().toString();
        Execution execution = new Execution(id, taskId, ExecutionStatus.CREATED,
                OffsetDateTime.now().toString(), List.of());
        executions.put(id, execution);
        return execution;

    }

    public Execution findById(String id) {
        return executions.get(id);
    }

    public List<Execution> findByTaskId(String taskId) {
        return executions.values().stream()
                .filter(execution -> execution.taskId().equals(taskId))
                .toList();
    }

    public List<Execution> findAll() {
        return new ArrayList<>(executions.values());
    }

    public Execution startExecution(String taskId) {

        String id = UUID.randomUUID().toString();

        Execution execution = new Execution(
                id,
                taskId,
                ExecutionStatus.QUEUED,
                OffsetDateTime.now().toString(),
                List.of());

        executions.put(id, execution);

        return execution;
    }
}
