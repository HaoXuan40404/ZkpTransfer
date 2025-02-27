package com.webank.ppc.iss.job;

import com.webank.ppc.iss.common.EventEnum;
import com.webank.ppc.iss.config.DataWriterService;
import com.webank.ppc.iss.message.ChainJobEventQueueSet;
import com.webank.ppc.iss.service.JobEventQueueService;
import com.webank.ppc.iss.service.PpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * @author caryliao
 * @date 2024/4/21
 */
@ConditionalOnProperty(name = "service.job-event-enabled", havingValue = "true")
@Component
@Slf4j
public class EnqueueJob {

    @Autowired
    private JobEventQueueService jobEventQueueService;

    @Autowired
    private PpcService ppcService;

    @Autowired
    DataWriterService dataWriterService;

    @Async("asyncExecutor")
    @Scheduled(cron = "${app.jobQueue.cron}")
    public void scheduledEnqueueJob() {
        ChainJobEventQueueSet chainJobEventQueueSet = PpcService.chainJobEventQueueSets.poll();
        log.debug("isLocker:{}, enqueueJob run at:{}", dataWriterService.isLocker(), LocalDateTime.now());
        if (dataWriterService.isLocker()) {
            if (chainJobEventQueueSet != null) {
                String jobId = chainJobEventQueueSet.getJobId();
                String jobEvent = chainJobEventQueueSet.getJobEvent();
                BigInteger blockNumber = chainJobEventQueueSet.getBlockNumber();
                log.info("从内存队列获取jobEvent, jobId:{}", jobId);
                jobEventQueueService.enqueueJob(jobId, jobEvent);
                // 记录job处理的最新区块
                ppcService.setEventStatus(EventEnum.JOB.getValue(), blockNumber.longValue());
            }
        }

    }
}
