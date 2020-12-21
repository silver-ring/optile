package com.optila.taskrunner.api.scheduler;

import com.optila.taskrunner.api.scheduler.utils.JobParameters;
import com.optila.taskrunner.api.utils.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SchedulerJobBean extends QuartzJobBean {

    private final MessageSender messageSender;

    public SchedulerJobBean(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        String executionCommand =
                (String) jobExecutionContext.getJobDetail().getJobDataMap().get(JobParameters.EXECUTION_COMMAND);
        log.info(String.format("fire job schedule >>> %s", executionCommand));
        messageSender.sendWorkerMessage(executionCommand);
    }

}
