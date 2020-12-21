package com.optila.taskrunner.api.scheduler;

import lombok.Data;
import org.quartz.Trigger;

@Data
public class CreateSchedulerRequest {

    private String executionCommand;
    private String cronExpression;
    private int priority = Trigger.DEFAULT_PRIORITY;

}
