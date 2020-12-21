package com.optila.taskrunner.api.scheduler;

import com.optila.taskrunner.api.exceptions.JobNotFoundException;
import com.optila.taskrunner.api.exceptions.MissingParameterException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SchedulerApi {

    private final SchedulerService schedulerService;
    private final Scheduler scheduler;

    @Autowired
    public SchedulerApi(Scheduler scheduler, SchedulerService schedulerService) {
        this.scheduler = scheduler;
        this.schedulerService = schedulerService;
    }

    @GetMapping("/scheduler")
    @ResponseBody
    public List<GetSchedulerResponse> getAllScheduler() throws SchedulerException {
        return schedulerService.getAllJobs();
    }

    @PostMapping("/scheduler")
    public String createScheduler(@RequestBody CreateSchedulerRequest createSchedulerRequest) throws SchedulerException {
        if (createSchedulerRequest.getExecutionCommand().isEmpty()) {
            throw new MissingParameterException("executionCommand");
        }
        if (createSchedulerRequest.getCronExpression().isEmpty()) {
            throw new MissingParameterException("cronExpression");
        }
        return schedulerService.scheduleJob(createSchedulerRequest);
    }

    @PutMapping("/scheduler/{jobId}")
    public void updateScheduler(@PathVariable String jobId, @RequestBody UpdateSchedulerRequest updateSchedulerRequest) throws SchedulerException {
        if (updateSchedulerRequest.getExecutionCommand().isEmpty()) {
            throw new MissingParameterException("executionCommand");
        }
        if (updateSchedulerRequest.getCronExpression().isEmpty()) {
            throw new MissingParameterException("cronExpression");
        }
        schedulerService.updateJob(jobId, updateSchedulerRequest);
    }

    @GetMapping("/scheduler/{jobId}")
    @ResponseBody
    public GetSchedulerResponse getScheduler(@PathVariable String jobId) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobId);
        if (!scheduler.checkExists(triggerKey)) {
            throw new JobNotFoundException();
        }
        return schedulerService.getJob(triggerKey);
    }

    @DeleteMapping("/scheduler/{jobId}")
    public void deleteScheduler(@PathVariable String jobId) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobId);
        if (!scheduler.checkExists(jobKey)) {
            throw new JobNotFoundException();
        }
        schedulerService.deleteJob(jobKey);
    }

    @PatchMapping("/scheduler/resume/{jobId}")
    public void resumeScheduler(@PathVariable String jobId) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobId);
        if (!scheduler.checkExists(jobKey)) {
            throw new JobNotFoundException();
        }
        schedulerService.resumeJob(jobKey);
    }

    @PatchMapping("/scheduler/pause/{jobId}")
    public void pauseScheduler(@PathVariable String jobId) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobId);
        if (!scheduler.checkExists(jobKey)) {
            throw new JobNotFoundException();
        }
        schedulerService.pauseJob(jobKey);
    }

}
