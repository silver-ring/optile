package com.optila.taskrunner.api.executor;

import com.optila.taskrunner.api.utils.MessageSender;
import org.springframework.stereotype.Service;

@Service
public class ExecutorService {

    private final MessageSender messageSender;

    public ExecutorService(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public void sendExecuteMessage(RunCommandRequest runCommandRequest) {
        messageSender.sendWorkerMessage(runCommandRequest.getExecutionCommand());
    }

}
