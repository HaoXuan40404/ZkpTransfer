package com.webank.ppc.iss.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.webank.ppc.iss.common.*;
import com.webank.ppc.iss.config.ServiceConfig;
import com.webank.ppc.iss.entity.JobDetailEntity;
import com.webank.ppc.iss.entity.JobEventQueueEntity;
import com.webank.ppc.iss.message.BaseResponse;
import com.webank.ppc.iss.message.KilledJobHttpRequest;
import com.webank.ppc.iss.repository.JobDetailRepository;
import com.webank.ppc.iss.repository.JobEventQueueRepository;
import com.webank.ppc.iss.utils.PpcProtoUtils;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.v3.utils.ObjectMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ppc.proto.Ppc;

import java.util.Base64;

@Service
@Slf4j
public class JobEventQueueService {

    @Autowired
    private JobEventQueueRepository jobEventQueueRepository;

    @Autowired
    JobDetailRepository jobDetailRepository;

    @Autowired
    private ServiceConfig serviceConfig;

    @Value("${shedlock.enabled}")
    private Boolean shedLockEnabled;

    @Retryable(
            value = {PpcException.class},
            maxAttempts = PpcCommonUtils.MAX_ATTEMPTS,
            backoff =
            @Backoff(
                    delay = PpcCommonUtils.DELAY,
                    multiplier = PpcCommonUtils.MULTIPLIER,
                    random = true))
    @Transactional(rollbackFor = Exception.class)
    public void enqueueJob(String jobId, String jobEvent) {
        try {
            int result;
            log.info("shedLockEnabled:{}", shedLockEnabled);
            Ppc.JobEvent jobEventProto = PpcProtoUtils.jobEventFromString(jobEvent);
            String jobType = jobEventProto.getJobType();
            String algorithmType = jobEventProto.getJobAlgorithmType();
            log.info("jobId:{}, 任务类型: {}, algorithmType: {}", jobEventProto.getJobId(), jobType, algorithmType);
            log.info("jobEventProto:{}", jobEventProto);
            // 判断db如果有就delete掉，如果有（已经下发）就向同步服务发起kill请求
            if (jobType.equals(JobTypeEnum.KILLED.name())) {
                // 终止任务
                executeKilledJob(jobId, algorithmType);
                return;
            }

            // 如果不是自己的任务需要跳过
            if (!checkParticipateByRecord(jobId, serviceConfig.getAgencyId(), jobEventProto.getJobDatasetProviderListPb())) {
                log.info("不是自己的任务, 跳过jobId: {}, agencyId: {}", jobId, serviceConfig.getAgencyId());
                return;
            }

            // 若不为killed类型 则check，并打印，原逻辑类型可能为None
            boolean checkResult = checkJobType(jobType);
            if (!checkResult) {
                throw new PpcException(EnumResponseStatus.CHECK_JOB_TYPE_FAILED.getErrorCode(), EnumResponseStatus.CHECK_JOB_TYPE_FAILED.getMessage());
            }
            // 解析任务，插入任务jobDetail表
            JobDetailEntity jobDetailEntity = jobDetailRepository.findFirstByJobId(jobId);
            if (jobDetailEntity == null) {
                jobDetailEntity = PpcProtoUtils.mapJobEventToJobDetailEntity(jobEventProto);
                jobDetailEntity.setParticipateAgencyId(serviceConfig.getAgencyId());
                jobDetailEntity.setJobStatus(JobStatusEnum.WAITING.name());
            } else {
                String jobStatus = jobDetailEntity.getJobStatus();
                log.info("查询到任务状态: jobId:{}, 任务状态: {}", jobId, jobStatus);
                if (jobStatus.equals(JobStatusEnum.FAILED.name())) {
                    jobDetailEntity.setJobStatus(JobStatusEnum.RETRY.name());
                } else if (jobStatus.equals(JobStatusEnum.WAITING.name())) {
                    // TODO: 创建任务时 任务发起方会写任务
                    log.info("任务已被写入db, 任务: {} 当前状态: {}", jobId, jobStatus);
                    jobDetailEntity.setJobStatus(JobStatusEnum.WAITING.name());
                } else {
                    // 不应该有此状态的任务
                    log.warn("任务已成功执行, 任务: {} 当前状态: {}", jobId, jobStatus);
                    return;
                }
            }

            // 1. 查询表，如果任务已存在，且状态为running/success/WAITING则跳过，若为failed则修改状态为待重试状态
            // 2. 如果任务不存在，则插入一条新纪录，状态为waiting
            jobDetailRepository.save(jobDetailEntity);
            log.info("jobDetailRepository写DB成功, jobDetailEntity: {}", jobDetailEntity);

            if (shedLockEnabled) {
                result = jobEventQueueRepository.saveJobEventWithInsertIgnore(jobId, jobEvent);
            } else {
                result = jobEventQueueRepository.saveJobEvent(jobId, jobEvent);
            }
            if (result == 1) {
                log.info("本实例插入job队列成功, jobId:{}", jobId);
            } else {
                log.info("其他实例插入job队列成功, jobId:{}", jobId);
            }

        } catch (Exception e) {
            log.error("本实例插入enqueueJob， jobId:{}, jobEvent: {}", jobId, jobEvent);
            log.error(e.getMessage());
            throw new PpcException(EnumResponseStatus.FAILURE.getErrorCode(), "本实例插入job到队列失败" + e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public JobEventQueueEntity dequeueJob() {
        // 查询队列中最早的任务
//        log.debug("dequeueJob");
        JobEventQueueEntity jobEventQueueEntity = jobEventQueueRepository.findTop1ByOrderByCreatedDateAscJobIdAsc();
        if (jobEventQueueEntity != null) {
            // 获取任务后，删除该任务
            jobEventQueueRepository.delete(jobEventQueueEntity);
            log.info("任务队列表出队列, jobId:{}", jobEventQueueEntity.getJobId());
//            return jobEventQueueEntity;
        }
        return jobEventQueueEntity;
    }

    private void executeKilledJob(String jobId, String jobAlgorithmType) {
        // 1. 查询任务详情jobDetailRepository 如果没有异常 否则设置为kill
        // 2. 判断任务队列db里是否有这条数据，如果有则删掉
        // 3. 发送kill请求给调度服务
        log.info("executeKilledJob: {}", jobId);
        JobDetailEntity jobDetailEntity = jobDetailRepository.findFirstByJobId(jobId);
        if (jobDetailEntity == null) {
            // 如果不为空则异常, 成功下发的任务一定在detail中
            log.warn("jobId: {} 在JobDetailEntity中未找到", jobId);
            throw new PpcException(EnumResponseStatus.KILLED_JOB_NOT_FOUND.getErrorCode(), EnumResponseStatus.KILLED_JOB_NOT_FOUND.getMessage());
        }
        JobEventQueueEntity jobEventQueueEntity = jobEventQueueRepository.findFirstByJobIdOrderByCreatedDateAscJobIdAsc(jobId);
        if (jobEventQueueEntity != null) {
            // 获取任务后，删除该任务
            jobEventQueueRepository.delete(jobEventQueueEntity);
            log.info("任务队列表不为空, 删除, jobId:{}", jobEventQueueEntity.getJobId());
        }
        String jobStatus = jobDetailEntity.getJobStatus();
        log.info("任务详情不为空, 设置为killed, jobId: {}, 原状态: {}", jobId, jobStatus);
        if (jobStatus.equals(JobStatusEnum.SUCCEED.name()) || jobStatus.equals(JobStatusEnum.FAILED.name())) {
            log.info("任务无需kill, 当前状态: {}", jobStatus);
            return;
        }
        jobDetailEntity.setJobStatus(JobStatusEnum.FAILED.name());
        jobDetailRepository.save(jobDetailEntity);
        sendJobKilledRequest(jobId, jobAlgorithmType);
    }

    @Retryable(maxAttempts = PpcCommonUtils.MAX_ATTEMPTS, backoff = @Backoff(delay = PpcCommonUtils.DELAY), value = {PpcException.class})
    private void sendJobKilledRequest(String jobId, String jobAlgorithmType) {
        log.info("发送停止任务请求: {}, {}", jobId, jobAlgorithmType);
        KilledJobHttpRequest killedJobHttpRequest = new KilledJobHttpRequest();
        killedJobHttpRequest.setJobId(jobId);
        killedJobHttpRequest.setJobAlgorithmType(jobAlgorithmType);
        String url = serviceConfig.getJobScheduler() + PpcCommonUtils.PSS_KILL_API;
        try {
            log.info("发送kill请求, url: {}, jobId: {}, jobAlgorithmType: {}", url, jobId, jobAlgorithmType);
            String killedResponseStr =
                    PpcCommonUtils.sendRequest(url, killedJobHttpRequest, serviceConfig.getJobEventTimeout());
            log.info("发送kill请求, killedResponseStr: {}", killedResponseStr);
            BaseResponse killedResponse =
                    ObjectMapperFactory.getObjectMapper()
                            .readValue(
                                    killedResponseStr,
                                    BaseResponse.class);
            int responseCode = killedResponse.getErrorCode();
            String responseMsg = killedResponse.getMessage();
            if (responseCode == 0) {
                log.info("停止任务成功, jobId:{}", jobId);
            } else {
                log.error("停止任务失败，jobId:{}, 错误信息:{}", jobId, responseMsg);
                throw new PpcException(EnumResponseStatus.KILLED_JOB_FAILED.getErrorCode(), "停止任务失败:" + responseMsg);
            }
        } catch (Exception e) {
            log.error("下发任务失败, jobId:{}, 错误信息: {}", jobId, e.getMessage(), e);
            throw new PpcException(EnumResponseStatus.KILLED_JOB_FAILED.getErrorCode(), "停止任务失败:" + e.getMessage());
        }
    }

    private boolean checkJobType(String jobType) {
        if (jobType == null || jobType.equals(JobTypeEnum.CREATE.name())) {
            log.info("checkJobType成功, 任务类型: {}", jobType);
            return true;
        }
        log.warn("checkJobType失败, 任务类型: {}", jobType);
        return false;
    }

    public static boolean checkParticipateByRecord(String jobId, String myAgencyId, String jobDatasetProviderPbStr) {
        try {
            byte[] protoBytes = Base64.getDecoder().decode(jobDatasetProviderPbStr);
            Ppc.jobDatasetProviderList ppcProto = Ppc.jobDatasetProviderList.parseFrom(protoBytes);
            log.info("检查任务{} 参与详情, 任务参与方List: {}", jobId, ppcProto.getProviderList());
            for (Ppc.jobDatasetProvider datasetProvider : ppcProto.getProviderList()) {
                log.info("任务id: {}, 自身id: {}, 参与方id: {}", jobId, myAgencyId, datasetProvider.getAgencyId());
                if (myAgencyId.equals(datasetProvider.getAgencyId())) {
                    return true;
                }
            }
            return false;
        } catch (InvalidProtocolBufferException e) {
            throw new PpcException(EnumResponseStatus.PROTO_DECODED_FAILED.getErrorCode(), e.getMessage());
        }

    }

}