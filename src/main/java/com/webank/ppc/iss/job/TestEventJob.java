package com.webank.ppc.iss.job;

import com.webank.ppc.iss.message.ChainJobEventQueueSet;
import com.webank.ppc.iss.service.JobEventQueueService;
import com.webank.ppc.iss.service.PpcService;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author caryliao
 * 用于本地测试
 * @date 2024/4/21
 */
//@Component
@Slf4j
public class TestEventJob {

    @Autowired
    private JobEventQueueService jobEventQueueService;

    @Scheduled(initialDelay = 30, fixedDelay = 1000 * 3600 * 24)
    public void scheduledEnqueueJob() {
        System.out.println( "scheduledEnqueueJob:" + Thread.currentThread().getName());
        for (int i = 1; i <= 10; i++) {
            System.out.println("收到链上job" + i);
            ChainJobEventQueueSet chainJobEventQueueSet = new ChainJobEventQueueSet();
            chainJobEventQueueSet.setJobId("jobId" + i);
            chainJobEventQueueSet.setJobEvent("jobEvent" + i);
            PpcService.chainJobEventQueueSets.add(chainJobEventQueueSet);
        }
    }
}
