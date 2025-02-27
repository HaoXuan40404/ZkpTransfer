package com.webank.ppc.iss.service;

import com.webank.ppc.iss.common.EnumResponseStatus;
import com.webank.ppc.iss.common.PpcCommonUtils;
import com.webank.ppc.iss.common.PpcException;
import com.webank.ppc.iss.message.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChainService {
    @Autowired
    private FiscoBcosClient fiscoBcosClient;


    @Retryable(
            value = {PpcException.class},
            maxAttempts = PpcCommonUtils.MAX_ATTEMPTS,
            backoff =
            @Backoff(
                    delay = PpcCommonUtils.DELAY,
                    multiplier = PpcCommonUtils.MULTIPLIER,
                    random = true))
    public void uploadDataSetMetaData(
            String ownerAgencyId,
            String datasetId,
            String datasetTitle,
            String ownerAgencyName,
            String datasetAlgorithm,
            String dataDetail) {
        try {
            fiscoBcosClient.uploadDataSetMetaData(
                    ownerAgencyId, datasetId, datasetTitle, ownerAgencyName, datasetAlgorithm, dataDetail);
        } catch (Exception e) {
            log.error("uploadDataSetMetaData failed:" + e.getMessage());
            throw new PpcException(
                    EnumResponseStatus.FAILURE.getErrorCode(),
                    "uploadDataSetMetaData failed:" + e.getMessage());
        }
    }

    @Retryable(
            value = {PpcException.class},
            maxAttempts = PpcCommonUtils.MAX_ATTEMPTS,
            backoff =
            @Backoff(
                    delay = PpcCommonUtils.DELAY,
                    multiplier = PpcCommonUtils.MULTIPLIER,
                    random = true))
    public void setDatasetAlgorithmFlag(String ownerAgencyId, String datasetId, String flag) {
        try {
            fiscoBcosClient.setDatasetAlgorithmFlag(ownerAgencyId, datasetId, flag);
        } catch (Exception e) {
            log.error("setPreprocessingAysDataset failed:" + e.getMessage());
            throw new PpcException(
                    EnumResponseStatus.FAILURE.getErrorCode(),
                    "setPreprocessingAysDataset failed:" + e.getMessage());
        }
    }

    @Retryable(
            value = {PpcException.class},
            maxAttempts = PpcCommonUtils.MAX_ATTEMPTS,
            backoff =
            @Backoff(
                    delay = PpcCommonUtils.DELAY,
                    multiplier = PpcCommonUtils.MULTIPLIER,
                    random = true))
    public void updateDataSetMetaData(UpdateDatasetRequest request) {
        try {
            fiscoBcosClient.updateDataSetMetaData(request);
        } catch (Exception e) {
            log.error("updateDataSetMetaData failed:" + e.getMessage());
            throw new PpcException(
                    EnumResponseStatus.FAILURE.getErrorCode(),
                    "updateDataSetMetaData failed:" + e.getMessage());
        }
    }


    @Retryable(
            value = {PpcException.class},
            maxAttempts = PpcCommonUtils.MAX_ATTEMPTS,
            backoff =
            @Backoff(
                    delay = PpcCommonUtils.DELAY,
                    multiplier = PpcCommonUtils.MULTIPLIER,
                    random = true))
    public void removeDataset(String ownerAgencyId, String datasetId) {
        try {
            fiscoBcosClient.removeDataset(ownerAgencyId, datasetId);
        } catch (Exception e) {
            log.error("removeDataset failed:" + e.getMessage());
            throw new PpcException(
                    EnumResponseStatus.FAILURE.getErrorCode(),
                    "removeDataset failed:" + e.getMessage());
        }
    }

    @Retryable(
            value = {PpcException.class},
            maxAttempts = PpcCommonUtils.MAX_ATTEMPTS,
            backoff =
            @Backoff(
                    delay = PpcCommonUtils.DELAY,
                    multiplier = PpcCommonUtils.MULTIPLIER,
                    random = true))
    public void authorizeDataset(ActiveAuthDatasetRequest request) {
        try {
            fiscoBcosClient.authorizeDataset(request);
        } catch (Exception e) {
            log.error("activeAuthDataset failed:" + e.getMessage());
            throw new PpcException(
                    EnumResponseStatus.FAILURE.getErrorCode(),
                    "activeAuthDataset failed:" + e.getMessage());
        }
    }

    @Retryable(
            value = {PpcException.class},
            maxAttempts = PpcCommonUtils.MAX_ATTEMPTS,
            backoff =
            @Backoff(
                    delay = PpcCommonUtils.DELAY,
                    multiplier = PpcCommonUtils.MULTIPLIER,
                    random = true))
    public void removeAuthorize(DeleteAuthDatasetRequest request) {
        try {
            fiscoBcosClient.removeAuthorize(request);
        } catch (Exception e) {
            log.error("removeAuthorize failed:" + e.getMessage());
            throw new PpcException(
                    EnumResponseStatus.FAILURE.getErrorCode(),
                    "removeAuthorize failed:" + e.getMessage());
        }
    }

    @Retryable(
            value = {PpcException.class},
            maxAttempts = PpcCommonUtils.MAX_ATTEMPTS,
            backoff =
            @Backoff(
                    delay = PpcCommonUtils.DELAY,
                    multiplier = PpcCommonUtils.MULTIPLIER,
                    random = true))
    public void addAuthorizeRequest(AddAuthDatasetRequest request) {
        try {
            fiscoBcosClient.addAuthorizeRequest(request);
        } catch (Exception e) {
            log.error("addAuthorizeRequest failed:" + e.getMessage());
            throw new PpcException(
                    EnumResponseStatus.FAILURE.getErrorCode(),
                    "addAuthorizeRequest failed:" + e.getMessage());
        }
    }

    @Retryable(
            value = {PpcException.class},
            maxAttempts = PpcCommonUtils.MAX_ATTEMPTS,
            backoff =
            @Backoff(
                    delay = PpcCommonUtils.DELAY,
                    multiplier = PpcCommonUtils.MULTIPLIER,
                    random = true))
    public void authorizeDatasetByRequest(ActiveAuthDatasetByOtherRequest request) {
        try {
            fiscoBcosClient.authorizeDatasetByRequest(request);
        } catch (Exception e) {
            log.error("authorizeDatasetByRequest failed:" + e.getMessage());
            throw new PpcException(
                    EnumResponseStatus.FAILURE.getErrorCode(),
                    "authorizeDatasetByRequest failed:" + e.getMessage());
        }
    }


    @Retryable(
            value = {PpcException.class},
            maxAttempts = PpcCommonUtils.MAX_ATTEMPTS,
            backoff =
            @Backoff(
                    delay = PpcCommonUtils.DELAY,
                    multiplier = PpcCommonUtils.MULTIPLIER,
                    random = true))
    public void uploadAlgorithm(UploadAlgorithmRequest request) {
        try {
            fiscoBcosClient.uploadAlgorithm(request);
        } catch (Exception e) {
            log.error("uploadAlgorithm failed:" + e.getMessage());
            throw new PpcException(
                    EnumResponseStatus.FAILURE.getErrorCode(),
                    "uploadAlgorithm failed:" + e.getMessage());
        }
    }

    @Retryable(
            value = {PpcException.class},
            maxAttempts = PpcCommonUtils.MAX_ATTEMPTS,
            backoff =
            @Backoff(
                    delay = PpcCommonUtils.DELAY,
                    multiplier = PpcCommonUtils.MULTIPLIER,
                    random = true))
    public void updateAlgorithm(UpdateAlgorithmRequest request) {
        try {
            fiscoBcosClient.updateAlgorithm(request);
        } catch (Exception e) {
            log.error("updateAlgorithm failed:" + e.getMessage());
            throw new PpcException(
                    EnumResponseStatus.FAILURE.getErrorCode(),
                    "updateAlgorithm failed:" + e.getMessage());
        }
    }

    @Retryable(
            value = {PpcException.class},
            maxAttempts = PpcCommonUtils.MAX_ATTEMPTS,
            backoff =
            @Backoff(
                    delay = PpcCommonUtils.DELAY,
                    multiplier = PpcCommonUtils.MULTIPLIER,
                    random = true))
    public void updateAlgorithm(
            String ownerAgencyId,
            String algorithmId,
            String algorithmVersion,
            String algorithmContent) {
        try {
            //            fiscoBcosClient.updateAlgorithm(
            //                    ownerAgencyId, algorithmId, algorithmVersion, algorithmContent);
        } catch (Exception e) {
            log.error("updateAlgorithm failed:" + e.getMessage());
            throw new PpcException(
                    EnumResponseStatus.FAILURE.getErrorCode(),
                    "updateAlgorithm failed:" + e.getMessage());
        }
    }

    @Retryable(
            value = {PpcException.class},
            maxAttempts = PpcCommonUtils.MAX_ATTEMPTS,
            backoff =
            @Backoff(
                    delay = PpcCommonUtils.DELAY,
                    multiplier = PpcCommonUtils.MULTIPLIER,
                    random = true))
    public void removeAlgorithm(String ownerAgencyId, String algorithmId, String algorithmVersion) {
        try {
            fiscoBcosClient.removeAlgorithm(ownerAgencyId, algorithmId, algorithmVersion);
        } catch (Exception e) {
            log.error("removeAlgorithm failed:" + e.getMessage());
            throw new PpcException(
                    EnumResponseStatus.FAILURE.getErrorCode(),
                    "removeAlgorithm failed:" + e.getMessage());
        }
    }


    @Retryable(
            value = {PpcException.class},
            maxAttempts = PpcCommonUtils.MAX_ATTEMPTS,
            backoff =
            @Backoff(
                    delay = PpcCommonUtils.DELAY,
                    multiplier = PpcCommonUtils.MULTIPLIER,
                    random = true))
    public void uploadJobAudit(UploadJobAuditRequest request) {
        try {
            fiscoBcosClient.uploadJobAudit(request);
        } catch (Exception e) {
            log.error("uploadJobAudit failed:" + e.getMessage());
            throw new PpcException(
                    EnumResponseStatus.FAILURE.getErrorCode(),
                    "uploadJobAudit failed:" + e.getMessage());
        }
    }

    @Retryable(
            value = {PpcException.class},
            maxAttempts = PpcCommonUtils.MAX_ATTEMPTS,
            backoff =
            @Backoff(
                    delay = PpcCommonUtils.DELAY,
                    multiplier = PpcCommonUtils.MULTIPLIER,
                    random = true))
    public void delegateJob(ScheduleJobRequest request) {
        try {
            fiscoBcosClient.delegateJob(request);
        } catch (Exception e) {
            log.error("delegateJob failed:" + e.getMessage());
            throw new PpcException(
                    EnumResponseStatus.FAILURE.getErrorCode(),
                    "delegateJob failed:" + e.getMessage());
        }
    }

    @Retryable(
            value = {PpcException.class},
            maxAttempts = PpcCommonUtils.MAX_ATTEMPTS,
            backoff =
            @Backoff(
                    delay = PpcCommonUtils.DELAY,
                    multiplier = PpcCommonUtils.MULTIPLIER,
                    random = true))
    public void uploadAgencyInfo(UploadAgencyRequest request) {
        try {
            fiscoBcosClient.uploadAgencyInfo(request);
        } catch (Exception e) {
            log.error("uploadAgencyInfo failed:" + e.getMessage());
            throw new PpcException(
                    EnumResponseStatus.FAILURE.getErrorCode(),
                    "uploadAgencyInfo failed:" + e.getMessage());
        }
    }

    @Retryable(
            value = {PpcException.class},
            maxAttempts = PpcCommonUtils.MAX_ATTEMPTS,
            backoff =
            @Backoff(
                    delay = PpcCommonUtils.DELAY,
                    multiplier = PpcCommonUtils.MULTIPLIER,
                    random = true))
    public void updateAgencyInfo(UpdateAgencyRequest request) {
        try {
            fiscoBcosClient.updateAgencyInfo(request);
        } catch (Exception e) {
            log.error("updateAgencyInfo failed:" + e.getMessage());
            throw new PpcException(
                    EnumResponseStatus.FAILURE.getErrorCode(),
                    "updateAgencyInfo failed:" + e.getMessage());
        }
    }

    @Retryable(
            value = {PpcException.class},
            maxAttempts = PpcCommonUtils.MAX_ATTEMPTS,
            backoff =
            @Backoff(
                    delay = PpcCommonUtils.DELAY,
                    multiplier = PpcCommonUtils.MULTIPLIER,
                    random = true))
    public void deleteAgencyInfo(String agencyId) {
        try {
            fiscoBcosClient.deleteAgencyInfo(agencyId);
        } catch (Exception e) {
            log.error("deleteAgencyInfo failed:" + e.getMessage());
            throw new PpcException(
                    EnumResponseStatus.FAILURE.getErrorCode(),
                    "deleteAgencyInfo failed:" + e.getMessage());
        }
    }

    @Retryable(
            value = {PpcException.class},
            maxAttempts = PpcCommonUtils.MAX_ATTEMPTS,
            backoff =
            @Backoff(
                    delay = PpcCommonUtils.DELAY,
                    multiplier = PpcCommonUtils.MULTIPLIER,
                    random = true))
    public void uploadJobParticipantDetail(UploadJobParticipantDetailRequest request) {
        try {
            fiscoBcosClient.uploadJobParticipantDetail(request);
        } catch (Exception e) {
            log.error("uploadJobParticipantDetail failed:" + e.getMessage());
            throw new PpcException(
                    EnumResponseStatus.FAILURE.getErrorCode(),
                    "uploadJobParticipantDetail failed:" + e.getMessage());
        }
    }
}
