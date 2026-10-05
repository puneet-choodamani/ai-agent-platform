package com.puneet.agentplatform.execution;

import org.springframework.context.ApplicationEventPublisher;
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
        private final ApplicationEventPublisher eventPublisher;

        public ExecutionService(ApplicationEventPublisher eventPublisher) {
                this.eventPublisher = eventPublisher;
        }

        public Execution createExecution(String taskId) {
                String id = UUID.randomUUID().toString();
                Execution execution = new Execution(id, taskId, ExecutionStatus.CREATED,
                                OffsetDateTime.now().toString(), null, null, null, null,
                                List.of());
                executions.put(id, execution);
                eventPublisher.publishEvent(
                                new ExecutionEvent(
                                                execution.id(),
                                                execution.taskId(),
                                                ExecutionEventType.EXECUTION_CREATED,
                                                null,
                                                Map.of()));
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
                eventPublisher.publishEvent(
                                new ExecutionEvent(
                                                updated.id(),
                                                updated.taskId(),
                                                ExecutionEventType.EXECUTION_STARTED,
                                                null,
                                                Map.of()));

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
                eventPublisher.publishEvent(
                                new ExecutionEvent(
                                                updated.id(),
                                                updated.taskId(),
                                                ExecutionEventType.EXECUTION_COMPLETED,
                                                null,
                                                Map.of(
                                                                "resultLength",
                                                                result == null ? 0 : result.length())));

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

                eventPublisher.publishEvent(
                                new ExecutionEvent(
                                                updated.id(),
                                                updated.taskId(),
                                                ExecutionEventType.EXECUTION_FAILED,
                                                null,
                                                Map.of(
                                                                "error",
                                                                errorMessage == null ? "" : errorMessage)));

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

                eventPublisher.publishEvent(
                                new ExecutionEvent(
                                                updated.id(),
                                                updated.taskId(),
                                                ExecutionEventType.STEP_STARTED,
                                                null,
                                                Map.of(
                                                                "stepId", step.id(),
                                                                "stepType", step.type().name(),
                                                                "sequence", step.sequence())));

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

                if (status == ExecutionStepStatus.COMPLETED) {

                        eventPublisher.publishEvent(
                                        new ExecutionEvent(
                                                        updated.id(),
                                                        updated.taskId(),
                                                        ExecutionEventType.STEP_COMPLETED,
                                                        null,
                                                        Map.of(
                                                                        "stepId", stepId,
                                                                        "stepType",
                                                                        findStepType(updated, stepId).name(),
                                                                        "durationMs",
                                                                        durationMs == null ? 0 : durationMs)));
                }

                return updated;
        }

        private ExecutionStepType findStepType(

                        Execution execution,
                        String stepId) {

                return execution.steps()
                                .stream()
                                .filter(step -> step.id().equals(stepId))
                                .map(ExecutionStep::type)
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Step not found: " + stepId));
        }

        public Execution markQueued(String executionId) {
                Execution current = getRequired(executionId);

                Execution updated = new Execution(
                                current.id(),
                                current.taskId(),
                                ExecutionStatus.QUEUED,
                                current.createdAt(),
                                current.startedAt(),
                                current.completedAt(),
                                current.result(),
                                current.errorMessage(),
                                current.steps());

                executions.put(executionId, updated);

                eventPublisher.publishEvent(
                                new ExecutionEvent(
                                                updated.id(),
                                                updated.taskId(),
                                                ExecutionEventType.EXECUTION_QUEUED,
                                                null,
                                                Map.of()));

                return updated;
        }
}
