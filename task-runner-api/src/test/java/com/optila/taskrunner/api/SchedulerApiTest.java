package com.optila.taskrunner.api;

import com.optila.taskrunner.api.scheduler.utils.JobParameters;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SchedulerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private Scheduler scheduler;

    @Test
    public void createSchedulerTest_success() throws Exception {

    }

    @Test
    public void getSchedulerTest_success() throws Exception {
        String jobId = UUID.randomUUID().toString().replace("-", "");
        JobKey jobKey = JobKey.jobKey(jobId);
        TriggerKey triggerKey = TriggerKey.triggerKey(jobId);
        String url = String.format("/scheduler/%s", jobId);
        Trigger triggerMock = mock(Trigger.class);
        Date date = new Date();
        JobDetail jobDetail = mock(JobDetail.class);
        JobDataMap jobDataMap = mock(JobDataMap.class);
        String executionCommand = "ping www.google.com";
        int priority = 6;
        Trigger.TriggerState triggerState = Trigger.TriggerState.COMPLETE;
        try (MockedStatic<JobKey> jobKeyMock = mockStatic(JobKey.class);
             MockedStatic<TriggerKey> triggerKeyMock = mockStatic(TriggerKey.class)) {
            jobKeyMock.when(() -> JobKey.jobKey(eq(jobId))).thenReturn(jobKey);
            triggerKeyMock.when(() -> TriggerKey.triggerKey(eq(jobId))).thenReturn(triggerKey);
            when(triggerMock.getNextFireTime()).thenReturn(date);
            when(triggerMock.getJobKey()).thenReturn(jobKey);
            when(triggerMock.getPriority()).thenReturn(priority);
            when(triggerMock.getPriority()).thenReturn(priority);
            when(scheduler.checkExists(triggerKey)).thenReturn(true);
            when(scheduler.getTrigger(triggerKey)).thenReturn(triggerMock);
            when(scheduler.getJobDetail(jobKey)).thenReturn(jobDetail);
            when(scheduler.getTriggerState(triggerKey)).thenReturn(triggerState);
            when(jobDetail.getJobDataMap()).thenReturn(jobDataMap);
            when(jobDataMap.getString(JobParameters.EXECUTION_COMMAND)).thenReturn(executionCommand);
            String result = this.mockMvc.perform(get(url))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResult = String.format("{\"jobId\":\"%s\",\"executionCommand\":\"%s\",\"nextFiringTime\":\"%s\",\"priority\":%d,\"state\":\"%s\"}",
                    jobId, executionCommand, date.toString(), priority, triggerState);

            Assertions.assertEquals(expectedResult, result);

            verify(scheduler, times(1)).checkExists(triggerKey);
            verify(triggerMock, times(1)).getNextFireTime();
            verify(triggerMock, times(1)).getJobKey();
            verify(scheduler, times(1)).checkExists(triggerKey);
            verify(scheduler, times(1)).getTrigger(triggerKey);
            verify(scheduler, times(1)).getTriggerState(triggerKey);
            verify(scheduler, times(1)).getJobDetail(jobKey);
            verify(jobDetail, times(1)).getJobDataMap();
            verify(jobDataMap, times(1)).getString(JobParameters.EXECUTION_COMMAND);

        }
    }

    @Test
    public void deleteSchedulerTest_success() throws Exception {

        String jobId = UUID.randomUUID().toString().replace("-", "");
        JobKey jobKey = JobKey.jobKey(jobId);
        String url = String.format("/scheduler/%s", jobId);

        try (MockedStatic<JobKey> jobKeyMock = mockStatic(JobKey.class)) {
            jobKeyMock.when(() -> JobKey.jobKey(eq(jobId))).thenReturn(jobKey);
            when(scheduler.checkExists(jobKey)).thenReturn(true);
            this.mockMvc.perform(delete(url)).andExpect(status().isOk());
            verify(scheduler, times(1)).checkExists(jobKey);
            verify(scheduler, times(1)).deleteJob(jobKey);
        }
    }

    @Test
    public void resumeSchedulerTest_success() throws Exception {
        String jobId = UUID.randomUUID().toString().replace("-", "");
        JobKey jobKey = JobKey.jobKey(jobId);
        String url = String.format("/scheduler/resume/%s", jobId);

        try (MockedStatic<JobKey> jobKeyMock = mockStatic(JobKey.class)) {
            jobKeyMock.when(() -> JobKey.jobKey(eq(jobId))).thenReturn(jobKey);
            when(scheduler.checkExists(jobKey)).thenReturn(true);
            this.mockMvc.perform(patch(url)).andExpect(status().isOk());
            verify(scheduler, times(1)).checkExists(jobKey);
            verify(scheduler, times(1)).resumeJob(jobKey);
        }
    }

    @Test
    public void pauseSchedulerTest_success() throws Exception {
        String jobId = UUID.randomUUID().toString().replace("-", "");
        JobKey jobKey = JobKey.jobKey(jobId);
        String url = String.format("/scheduler/pause/%s", jobId);

        try (MockedStatic<JobKey> jobKeyMock = mockStatic(JobKey.class)) {
            jobKeyMock.when(() -> JobKey.jobKey(eq(jobId))).thenReturn(jobKey);
            when(scheduler.checkExists(jobKey)).thenReturn(true);
            this.mockMvc.perform(patch(url)).andExpect(status().isOk());
            verify(scheduler, times(1)).checkExists(jobKey);
            verify(scheduler, times(1)).pauseJob(jobKey);
        }
    }

    @Test
    public void pauseSchedulerTest_jobNotExist() throws Exception {
        String jobId = UUID.randomUUID().toString().replace("-", "");
        JobKey jobKey = JobKey.jobKey(jobId);
        String url = String.format("/scheduler/pause/%s", jobId);
        when(scheduler.checkExists(jobKey)).thenReturn(false);
        String response = this.mockMvc.perform(patch(url))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();
        String expectedMessage = "{\"message\":\"Job not found\"}";
        Assertions.assertEquals(response, expectedMessage);
        verify(scheduler, times(1)).checkExists(jobKey);
        verify(scheduler, times(0)).pauseJob(jobKey);
    }

}
