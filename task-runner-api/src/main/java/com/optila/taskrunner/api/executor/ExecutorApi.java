package com.optila.taskrunner.api.executor;

import com.optila.taskrunner.api.exceptions.MissingParameterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExecutorApi {

    private final ExecutorService executorService;

    @Autowired
    public ExecutorApi(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @PostMapping("/executor")
    public void executeCommand(@RequestBody RunCommandRequest runCommandRequest) {
        if (runCommandRequest.getExecutionCommand().isEmpty()) {
            throw new MissingParameterException("executionCommand");
        }
        executorService.sendExecuteMessage(runCommandRequest);
    }

}
