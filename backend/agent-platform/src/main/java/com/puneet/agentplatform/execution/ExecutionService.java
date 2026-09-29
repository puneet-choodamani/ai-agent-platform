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
                                OffsetDateTime.now().toString(), null, null, null, null,
                                List.of());
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

        private Execution getRequired(String executionId) {

                Execution execution = executions.get(executionId);

                if (execution == null) {
                        throw new IllegalArgumentException(
                                        "Execution not found: " + executionId);
                }

                return execution;
        }

        public Execution markInProgress(String executionId) {

                Execution current = getRequired(executionId);

                Execution updated = new Execution(
                                current.id(),
                                current.taskId(),
                                ExecutionStatus.IN_PROGRESS,
                                current.createdAt(),
                                OffsetDateTime.now().toString(),
                                null,
                                null,
                                null,
                                current.steps());

                executions.put(executionId, updated);

                return updated;
        }

        public Execution markCompleted(
                        String executionId,
                        String result) {

                Execution current = getRequired(executionId);

                Execution updated = new Execution(
                                current.id(),
                                current.taskId(),
                                ExecutionStatus.COMPLETED,
                                current.createdAt(),
                                current.startedAt(),
                                OffsetDateTime.now().toString(),
                                result,
                                null,
                                current.steps());

                executions.put(executionId, updated);

                return updated;
        }

        public Execution markFailed(
                        String executionId,
                        String errorMessage) {

                Execution current = getRequired(executionId);

                Execution updated = new Execution(
                                current.id(),
                                current.taskId(),
                                ExecutionStatus.FAILED,
                                current.createdAt(),
                                current.startedAt(),
                                OffsetDateTime.now().toString(),
                                null,
                                errorMessage,
                                current.steps());

                executions.put(executionId, updated);

                return updated;
        }

        public Execution addStep(
                        String executionId,
                        ExecutionStep step) {

                Execution current = getRequired(executionId);

                List<ExecutionStep> steps = new ArrayList<>(current.steps());

                steps.add(step);

                Execution updated = new Execution(
                                current.id(),
                                current.taskId(),
                                current.status(),
                                current.createdAt(),
                                current.startedAt(),
                                current.completedAt(),
                                current.result(),
                                current.errorMessage(),
                                List.copyOf(steps));

                executions.put(executionId, updated);

                return updated;
        }

        public Execution updateStep(
                        String executionId,
                        String stepId,
                        ExecutionStepStatus status,
                        String completedAt,
                        Long durationMs) {

                Execution current = getRequired(executionId);

                List<ExecutionStep> steps = current.steps()
                                .stream()
                                .map(step -> step.id().equals(stepId)
                                                ? new ExecutionStep(
                                                                step.id(),
                                                                step.executionId(),
                                                                step.sequence(),
                                                                step.type(),
                                                                status,
                                                                step.startedAt(),
                                                                completedAt,
                                                                durationMs)
                                                : step)
                                .toList();

                Execution updated = new Execution(
                                current.id(),
                                current.taskId(),
                                current.status(),
                                current.createdAt(),
                                current.startedAt(),
                                current.completedAt(),
                                current.result(),
                                current.errorMessage(),
                                steps);

                executions.put(executionId, updated);

                return updated;
        }
}
