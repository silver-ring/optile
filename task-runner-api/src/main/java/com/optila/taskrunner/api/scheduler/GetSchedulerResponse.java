package com.optila.taskrunner.api.scheduler;

import lombok.Data;
import org.quartz.Trigger;

@Data
public class GetSchedulerResponse {

    private String jobId;
    private String executionCommand;
    private String nextFiringTime;
    private int priority;
    private Trigger.TriggerState state;

}
