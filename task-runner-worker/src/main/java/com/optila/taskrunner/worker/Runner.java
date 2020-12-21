package com.optila.taskrunner.worker;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class Runner {

    final String WORKER_TOPIC = "worker";

    @KafkaListener(topics = WORKER_TOPIC)
    public void buildJobTrigger(String executionCommand) throws IOException {
        // execute command
        Process process = Runtime.getRuntime().exec(executionCommand);
        printResults(process);
    }

    private void printResults(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }

}
