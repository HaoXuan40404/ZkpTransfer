package com.webank.ppc.iss;

import com.webank.ppc.iss.entity.JobEventQueueEntity;
import com.webank.ppc.iss.repository.JobEventQueueRepository;
import com.webank.ppc.iss.service.JobEventQueueService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(classes = PpcIssApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = {"classpath:application.properties"})
public class JobEventQueueServiceTest {

    @Autowired
    private JobEventQueueService jobEventQueueService;

    @Autowired
    private JobEventQueueRepository jobEventQueueRepository;

    @Test
    public void testEnqueueJobAndDequeueJob() {
        // 入队列
        for (int i = 1; i <= 3; i++) {
            String jobId = "job" + i;
            String jobEvent = "jobEvent"+ i;
            jobEventQueueService.enqueueJob(jobId, jobEvent);
        }
        // 出队列
        for (int i = 1; i <= 3; i++) {
            String jobId = "job" + i;
            String jobEvent = "jobEvent"+ i;
            JobEventQueueEntity jobEventQueueEntity = jobEventQueueService.dequeueJob();
            assertEquals(jobId, jobEventQueueEntity.getJobId());
            assertEquals(jobEvent, jobEventQueueEntity.getJobEvent());
        }
        // 继续出队列
        JobEventQueueEntity jobEventQueueEntity = jobEventQueueService.dequeueJob();
        assertNull(jobEventQueueEntity);
    }
}