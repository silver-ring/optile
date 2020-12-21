package com.optila.taskrunner.api.scheduler;

import com.optila.taskrunner.api.scheduler.utils.JobParameters;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class SchedulerService {

    private final Scheduler scheduler;

    @Autowired
    public SchedulerService(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public String scheduleJob(CreateSchedulerRequest createSchedulerRequest) throws SchedulerException {
        String jobId = UUID.randomUUID().toString().replace("-", "");
        JobDetail orderJobDetail = buildJobDetail(jobId, createSchedulerRequest.getExecutionCommand());
        TriggerKey triggerKey = TriggerKey.triggerKey(jobId);
        Trigger jobDetail = buildTrigger(triggerKey, orderJobDetail, createSchedulerRequest.getCronExpression(), createSchedulerRequest.getPriority());
        scheduler.scheduleJob(orderJobDetail, jobDetail);
        return jobId;
    }

    public void updateJob(String jobId, UpdateSchedulerRequest updateSchedulerRequest) throws SchedulerException {
        JobDetail orderJobDetail = buildJobDetail(jobId, updateSchedulerRequest.getExecutionCommand());
        TriggerKey triggerKey = TriggerKey.triggerKey(jobId);
        Trigger jobDetail = buildTrigger(triggerKey, orderJobDetail, updateSchedulerRequest.getCronExpression(), updateSchedulerRequest.getPriority());
        scheduler.rescheduleJob(triggerKey, jobDetail);
    }

    private JobDetail buildJobDetail(String jobId, String executionCommand) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(JobParameters.EXECUTION_COMMAND, executionCommand);
        return JobBuilder.newJob(SchedulerJobBean.class)
                .withIdentity(jobId)
                .usingJobData(jobDataMap)
                .build();
    }

    private Trigger buildTrigger(TriggerKey triggerKey, JobDetail jobDetail, String cronExpression, int priority) {
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression)
                .withMisfireHandlingInstructionDoNothing();
        return TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .withSchedule(cronScheduleBuilder)
                .forJob(jobDetail)
                .withPriority(priority)
                .build();
    }

    public GetSchedulerResponse getJob(TriggerKey triggerKey) throws SchedulerException {
        Trigger trigger = scheduler.getTrigger(triggerKey);
        String nextFireTime = trigger.getNextFireTime().toString();
        JobDetail jobDetail = scheduler.getJobDetail(trigger.getJobKey());
        Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        String executionCommand = jobDataMap.getString(JobParameters.EXECUTION_COMMAND);

        GetSchedulerResponse getSchedulerResponse = new GetSchedulerResponse();
        getSchedulerResponse.setExecutionCommand(executionCommand);
        getSchedulerResponse.setJobId(triggerKey.getName());
        getSchedulerResponse.setNextFiringTime(nextFireTime);
        getSchedulerResponse.setPriority(trigger.getPriority());
        getSchedulerResponse.setState(triggerState);
        return getSchedulerResponse;
    }

    public List<GetSchedulerResponse> getAllJobs() throws SchedulerException {
        GroupMatcher<TriggerKey> groupMatcher = GroupMatcher.anyTriggerGroup();
        Set<TriggerKey> jobKeys = scheduler.getTriggerKeys(groupMatcher);
        List<GetSchedulerResponse> result = new ArrayList<>();
        for (TriggerKey triggerKey : jobKeys) {
            result.add(getJob(triggerKey));
        }
        return result;
    }

    public void deleteJob(JobKey jobKey) throws SchedulerException {
        scheduler.deleteJob(jobKey);
    }

    public void resumeJob(JobKey jobKey) throws SchedulerException {
        scheduler.resumeJob(jobKey);
    }

    public void pauseJob(JobKey jobKey) throws SchedulerException {
        scheduler.pauseJob(jobKey);
    }

}
