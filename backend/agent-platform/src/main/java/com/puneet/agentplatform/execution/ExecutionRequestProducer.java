package com.puneet.agentplatform.execution;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ExecutionRequestProducer {

    private static final String TOPIC = "execution-requests";

    private final KafkaTemplate<String, ExecutionRequested> kafkaTemplate;

    public ExecutionRequestProducer(
            KafkaTemplate<String, ExecutionRequested> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(ExecutionRequested request) {

        kafkaTemplate.send(
                TOPIC,
                request.executionId(),
                request);
    }
}