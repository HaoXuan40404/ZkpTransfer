// package com.webank.wedpr.zktransfer.job;

// import com.webank.wedpr.zktransfer.common.EnumResponseStatus;
// import com.webank.wedpr.zktransfer.common.PpcCommonUtils;
// import com.webank.wedpr.zktransfer.common.PpcException;
// import com.webank.wedpr.zktransfer.config.DataWriterService;
// import com.webank.wedpr.zktransfer.config.ServiceConfig;
// import com.webank.wedpr.zktransfer.entity.JobEventQueueEntity;
// import com.webank.wedpr.zktransfer.message.UploadJobEventHttpRequest;
// import com.webank.wedpr.zktransfer.message.UploadJobEventHttpResponse;
// import com.webank.wedpr.zktransfer.repository.JobDetailRepository;
// import com.webank.wedpr.zktransfer.service.JobEventQueueService;
// import lombok.extern.slf4j.Slf4j;
// import org.fisco.bcos.sdk.v3.utils.ObjectMapperFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
// import org.springframework.retry.annotation.Backoff;
// import org.springframework.retry.annotation.Retryable;
// import org.springframework.scheduling.annotation.Async;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;

// import java.time.LocalDateTime;

// /**
//  * @author caryliao
//  * @date 2024/4/21
//  */
// @Component
// @ConditionalOnExpression("${service.job-event-enabled} && ${shedlock.enabled}")
// @Slf4j
// public class DequeueJob {

//     @Autowired
//     private JobEventQueueService jobEventQueueService;

//     @Autowired
//     private JobDetailRepository jobDetailRepository;

//     @Autowired
//     private ServiceConfig serviceConfig;

//     @Autowired
//     private DataWriterService dataWriterService;

//     @Async("asyncExecutor")
//     @Scheduled(cron = "${app.jobQueue.cron}")
// //    @SchedulerLock(name = "dequeueJob", lockAtLeastFor = "${app.shedlock.lockAtLeastFor}", lockAtMostFor = "${app.shedlock.lockAtMostFor}")
//     public void scheduledDequeueJob() {
//         // 有运行中的任务，则跳过
//         try {
//             if (!dataWriterService.isLocker()) {
//                 log.debug("未抢到锁, 不执行任务下发");
//                 return;
//             }
//             if (jobDetailRepository.hasExistRunningJobWithConcurrency(serviceConfig.getJobMaxConcurrency())) {
//                 log.info("dequeueJob 存在运行中的任务, 当前并发度: {}", serviceConfig.getJobMaxConcurrency());
//                 return;
//             }
//             JobEventQueueEntity jobEventQueueEntity = jobEventQueueService.dequeueJob();
//             log.debug("jobEventQueueEntity: {}", jobEventQueueEntity);
//             // 队列中没有任务，则跳过
//             if (jobEventQueueEntity == null) {
//                 log.debug("dequeueJob 队列表中没有任务");
//                 return;
//             }
//             // 没有运行中的任务，且队列中有任务，则下发任务
//             String jobId = jobEventQueueEntity.getJobId();
//             String jobEvent = jobEventQueueEntity.getJobEvent();
//             log.info("下发任务, jobId:{}, jobEvent:{}, at:{}", jobId, jobEvent, LocalDateTime.now());
// //        System.out.println("下发任务，jobId: " + jobId +", jobEvent:" + jobEvent + " at:" + LocalDateTime.now());
//             sendJobEventRequest(jobId, jobEvent);
//         } catch (Exception e) {
//             log.error("", e);
//         }

//     }

//     @Retryable(maxAttempts = PpcCommonUtils.MAX_ATTEMPTS, backoff = @Backoff(delay = PpcCommonUtils.DELAY), value = {PpcException.class})
//     public void sendJobEventRequest(String jobId, String jobEvent) {
//         UploadJobEventHttpRequest uploadJobEventHttpRequest = new UploadJobEventHttpRequest();
//         uploadJobEventHttpRequest.setJobId(jobId);
//         uploadJobEventHttpRequest.setJobEvent(jobEvent);
//         String url = serviceConfig.getJobScheduler() + PpcCommonUtils.PSS_API;
//         try {
//             String uploadJobEventHttpResponseContent =
//                     PpcCommonUtils.sendRequest(url, uploadJobEventHttpRequest, serviceConfig.getJobEventTimeout());
//             UploadJobEventHttpResponse uploadJobEventHttpResponse =
//                     ObjectMapperFactory.getObjectMapper()
//                             .readValue(
//                                     uploadJobEventHttpResponseContent,
//                                     UploadJobEventHttpResponse.class);
//             int responseCode = uploadJobEventHttpResponse.getErrorCode();
//             String responseMsg = uploadJobEventHttpResponse.getMessage();
//             if (responseCode == 0) {
//                 log.info("下发任务成功, jobId:{}", jobId);
//             } else {
//                 log.error("下发任务失败, jobId:{}, 错误信息:{}", jobId, responseMsg);
//                 throw new PpcException(EnumResponseStatus.FAILURE.getErrorCode(), "下发任务失败:" + responseMsg);
//             }
//         } catch (Exception e) {
//             log.error("下发任务失败, jobId:{}, 错误信息: {}", jobId, e.getMessage(), e);
//             throw new PpcException(EnumResponseStatus.FAILURE.getErrorCode(), "下发任务失败:" + e.getMessage());
//         }
//     }
// }
