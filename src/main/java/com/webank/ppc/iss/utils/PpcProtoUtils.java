package com.webank.ppc.iss.utils;

import com.google.protobuf.InvalidProtocolBufferException;
import com.webank.ppc.iss.common.EnumResponseStatus;
import com.webank.ppc.iss.common.PpcException;
import com.webank.ppc.iss.entity.JobDetailEntity;
import ppc.proto.Ppc;

import java.util.Base64;

/**
 * @author asher
 * @date 2024/6/24
 */
public class PpcProtoUtils {

    public static Ppc.JobEvent jobEventFromString(String protoStr) throws PpcException {
        try {
            byte[] protoBytes = Base64.getDecoder().decode(protoStr);
            Ppc.JobEvent ppcProto = Ppc.JobEvent.parseFrom(protoBytes);
            return ppcProto;
        } catch (InvalidProtocolBufferException e) {
            throw new PpcException(EnumResponseStatus.PROTO_DECODED_FAILED.getErrorCode(), e.getMessage());
        }
    }

    public static JobDetailEntity mapJobEventToJobDetailEntity(Ppc.JobEvent jobEvent) {
        JobDetailEntity jobDetailEntity = new JobDetailEntity();
        jobDetailEntity.setJobId(jobEvent.getJobId());
        jobDetailEntity.setJobTitle(jobEvent.getJobTitle());
        jobDetailEntity.setJobDescription(jobEvent.getJobDescription());
        jobDetailEntity.setJobPriority((int) jobEvent.getJobPriority());
        jobDetailEntity.setJobCreator(jobEvent.getJobCreator());
        jobDetailEntity.setInitiatorAgencyId(jobEvent.getInitiatorAgencyId());
        jobDetailEntity.setInitiatorAgencyName(jobEvent.getInitiatorAgencyName());
        jobDetailEntity.setJobAlgorithmId(jobEvent.getJobAlgorithmId());
        jobDetailEntity.setJobAlgorithmTitle(jobEvent.getJobAlgorithmTitle());
        jobDetailEntity.setJobAlgorithmVersion(jobEvent.getJobAlgorithmVersion());
        jobDetailEntity.setJobAlgorithmType(jobEvent.getJobAlgorithmType());
        jobDetailEntity.setJobDatasetProviderListPb(jobEvent.getJobDatasetProviderListPb());
        jobDetailEntity.setJobResultReceiverListPb(jobEvent.getJobResultReceiverListPb());
        jobDetailEntity.setCreateTime(jobEvent.getCreateTime());
        jobDetailEntity.setPsiFields(jobEvent.getPsiFields());
        jobDetailEntity.setJobAlgorithmSubtype(jobEvent.getJobAlgorithmSubtype());
        jobDetailEntity.setMatchFieldsStr(jobEvent.getMatchFields());
        jobDetailEntity.setTagProviderAgencyId(jobEvent.getTagProviderAgencyId());
        return jobDetailEntity;
    }
}
