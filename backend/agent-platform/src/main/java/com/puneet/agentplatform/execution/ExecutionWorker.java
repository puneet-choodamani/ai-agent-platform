package com.puneet.agentplatform.execution;

import com.puneet.agentplatform.ai.AgentRuntimeService;
import com.puneet.agentplatform.task.Task;
import com.puneet.agentplatform.task.TaskService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ExecutionWorker {

    private final TaskService taskService;
    private final AgentRuntimeService agentRuntimeService;

    public ExecutionWorker(
            TaskService taskService,
            AgentRuntimeService agentRuntimeService) {

        this.taskService = taskService;
        this.agentRuntimeService = agentRuntimeService;
    }

    @KafkaListener(
            topics = "execution-requests",
            groupId = "agent-platform-execution-worker")
    public void process(ExecutionRequested request) {

        Task task = taskService.findById(request.taskId());

        if (task == null) {
            throw new IllegalArgumentException(
                    "Task not found: " + request.taskId());
        }

        agentRuntimeService.execute(
                request.executionId(),
                task.prompt());
    }
}