package com.optila.taskrunner.api.utils;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageSender {

    private final KafkaTemplate kafkaTemplate;

    public MessageSender(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendWorkerMessage(String executionCommand) {
        String WORKER_TOPIC = "worker";
        kafkaTemplate.send(WORKER_TOPIC, executionCommand);
    }

}
