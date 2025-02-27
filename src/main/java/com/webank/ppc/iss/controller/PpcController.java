package com.webank.ppc.iss.controller;

import com.webank.ppc.iss.common.EnumResponseStatus;
import com.webank.ppc.iss.message.*;
import com.webank.ppc.iss.service.PpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v3/ppc-management/iss/")
public class PpcController {

    @Autowired private PpcService ppcService;

    private void setSuccessMsg(BaseResponse response) {
        response.setErrorCode(EnumResponseStatus.SUCCESS.getErrorCode());
        response.setMessage(EnumResponseStatus.SUCCESS.getMessage());
    }

    @GetMapping("/stats/{agencyId}")
    public GetStatsResponse getStats(@PathVariable("agencyId") String agencyId) {
        StatsMessage statsMessage = ppcService.getStats(agencyId);
        GetStatsResponse getStatsResponse = new GetStatsResponse();
        getStatsResponse.setData(statsMessage);
        setSuccessMsg(getStatsResponse);
        return getStatsResponse;
    }

    @PostMapping("/data")
    public UploadDatasetResponse uploadDataset(
            @Validated @RequestBody UploadDatasetRequest request) {
        String ownerAgencyId = request.getOwnerAgencyId();
        String datasetId = request.getDatasetId();
        String datasetTitle = request.getDatasetTitle();
        String ownerAgencyName = request.getOwnerAgencyName();
        String datasetAlgorithm = request.getDatasetAlgorithm();
        String dataDetail = request.getDataDetail();
        ppcService.uploadDataset(
                ownerAgencyId, datasetId, datasetTitle, ownerAgencyName, datasetAlgorithm, dataDetail);
        UploadDatasetResponse uploadDatasetResponse = new UploadDatasetResponse();
        setSuccessMsg(uploadDatasetResponse);
        return uploadDatasetResponse;
    }

    @PatchMapping("/data-flag")
    public SetDatasetAlgorithmFlagResponse setDatasetAlgorithmFlag(
            @Validated @RequestBody SetDatasetAlgorithmFlagRequest request) {
        String ownerAgencyId = request.getOwnerAgencyId();
        String datasetId = request.getDatasetId();
        String flag = request.getFlag();
        ppcService.setDatasetAlgorithmFlag(ownerAgencyId, datasetId, flag);
        SetDatasetAlgorithmFlagResponse setDatasetAlgorithmFlagResponse =
                new SetDatasetAlgorithmFlagResponse();
        setSuccessMsg(setDatasetAlgorithmFlagResponse);
        return setDatasetAlgorithmFlagResponse;
    }

    @PatchMapping("/data")
    public UpdateDatasetResponse updateDataset(
            @Validated @RequestBody UpdateDatasetRequest request) {
        ppcService.updateDataset(request);
        UpdateDatasetResponse updateDatasetResponse = new UpdateDatasetResponse();
        setSuccessMsg(updateDatasetResponse);
        return updateDatasetResponse;
    }

    @GetMapping("/data")
    public GetDatasetResponse getDataset(@Validated GetDatasetRequest request) {
        DatasetMessage datasetMessage = ppcService.getDataset(request);
        GetDatasetResponse getDatasetResponse = new GetDatasetResponse();
        getDatasetResponse.setData(datasetMessage);
        setSuccessMsg(getDatasetResponse);
        return getDatasetResponse;
    }

    @DeleteMapping("/data")
    public DeleteDatasetResponse deleteDataset(
            @Validated @RequestBody DeleteDatasetRequest request) {
        String ownerAgencyId = request.getOwnerAgencyId();
        String datasetId = request.getDatasetId();
        ppcService.deleteDataset(ownerAgencyId, datasetId);
        DeleteDatasetResponse deleteDatasetResponse = new DeleteDatasetResponse();
        setSuccessMsg(deleteDatasetResponse);
        return deleteDatasetResponse;
    }

    @PostMapping("/data-ops/auth")
    public ActiveAuthDatasetResponse authorizeDataset(
            @Validated @RequestBody ActiveAuthDatasetRequest request) {
        ppcService.authorizeDataset(request);
        ActiveAuthDatasetResponse activeAuthDatasetResponse = new ActiveAuthDatasetResponse();
        setSuccessMsg(activeAuthDatasetResponse);
        return activeAuthDatasetResponse;
    }

    @DeleteMapping("/data-ops/auth")
    public DeleteAuthDatasetResponse deleteAuthDataset(
            @Validated @RequestBody DeleteAuthDatasetRequest request) {
        ppcService.removeAuthorizeDataset(request);
        DeleteAuthDatasetResponse deleteAuthDatasetResponse = new DeleteAuthDatasetResponse();
        setSuccessMsg(deleteAuthDatasetResponse);
        return deleteAuthDatasetResponse;
    }

    @PostMapping("/data-request/add")
    public AddAuthDatasetResponse addAuthorizeRequest(
            @Validated @RequestBody AddAuthDatasetRequest request) {
        ppcService.addAuthorizeRequest(request);
        AddAuthDatasetResponse addAuthDatasetResponse = new AddAuthDatasetResponse();
        setSuccessMsg(addAuthDatasetResponse);
        return addAuthDatasetResponse;
    }

    @PostMapping("/data-request/auth")
    public ActiveAuthDatasetResponse authorizeDatasetByRequest(
            @Validated @RequestBody ActiveAuthDatasetByOtherRequest request) {
        ppcService.authorizeDatasetByRequest(request);
        ActiveAuthDatasetResponse activeAuthDatasetResponse = new ActiveAuthDatasetResponse();
        setSuccessMsg(activeAuthDatasetResponse);
        return activeAuthDatasetResponse;
    }

    @PostMapping("/algorithms")
    public UploadAlgorithmResponse uploadAlgorithm(
            @Validated @RequestBody UploadAlgorithmRequest request) {
        ppcService.uploadAlgorithm(request);
        UploadAlgorithmResponse uploadAlgorithmResponse = new UploadAlgorithmResponse();
        setSuccessMsg(uploadAlgorithmResponse);
        return uploadAlgorithmResponse;
    }

    @PatchMapping("/algorithms")
    public UploadAlgorithmResponse updateAlgorithm(
            @Validated @RequestBody UpdateAlgorithmRequest request) {
        ppcService.updateAlgorithm(request);
        UploadAlgorithmResponse uploadAlgorithmResponse = new UploadAlgorithmResponse();
        setSuccessMsg(uploadAlgorithmResponse);
        return uploadAlgorithmResponse;
    }

    @DeleteMapping("/algorithms")
    public DeleteAlgorithmResponse deleteAlgorithm(
            @Validated @RequestBody DeleteAlgorithmRequest request) {
        String ownerAgencyId = request.getOwnerAgencyId();
        String algorithmId = request.getAlgorithmId();
        String algorithmVersion = request.getAlgorithmVersion();
        ppcService.deleteAlgorithm(ownerAgencyId, algorithmId, algorithmVersion);
        DeleteAlgorithmResponse deleteAlgorithmResponse = new DeleteAlgorithmResponse();
        setSuccessMsg(deleteAlgorithmResponse);
        return deleteAlgorithmResponse;
    }

    @GetMapping("/algorithms")
    public GetAlgorithmResponse getAlgorithm(@Validated GetAlgorithmRequest request) {
        AlgorithmMessage algorithmMessage = ppcService.getAlgorithm(request);
        GetAlgorithmResponse getAlgorithmResponse = new GetAlgorithmResponse();
        getAlgorithmResponse.setData(algorithmMessage);
        setSuccessMsg(getAlgorithmResponse);
        return getAlgorithmResponse;
    }

    @PostMapping("/jobs-audit")
    public UploadJobAuditResponse uploadJobAudit(
            @Validated @RequestBody UploadJobAuditRequest request) {
        ppcService.uploadJobAudit(request);
        UploadJobAuditResponse uploadJobAuditResponse = new UploadJobAuditResponse();
        setSuccessMsg(uploadJobAuditResponse);
        return uploadJobAuditResponse;
    }

    @PatchMapping("/jobs-audit")
    public UploadJobAuditResponse updateJobAudit(
            @Validated @RequestBody UploadJobAuditRequest request) {
        return uploadJobAudit(request);
    }

    @GetMapping("/jobs-audit")
    public GetJobAuditResponse getJobAudit(@Validated GetJobAuditRequest request) {
        JobAuditMessage jobAuditMessage = ppcService.getJobAudit(request);
        GetJobAuditResponse getJobAuthResponse = new GetJobAuditResponse();
        getJobAuthResponse.setData(jobAuditMessage);
        setSuccessMsg(getJobAuthResponse);
        return getJobAuthResponse;
    }

    @PostMapping("/agency")
    public BaseResponse uploadAgencyInfo(@Validated @RequestBody UploadAgencyRequest request) {
        ppcService.uploadAgencyInfo(request);
        BaseResponse baseResponse = new BaseResponse();
        setSuccessMsg(baseResponse);
        return baseResponse;
    }

    @PatchMapping("/agency")
    public BaseResponse updateAgencyInfo(@Validated @RequestBody UpdateAgencyRequest request) {
        ppcService.updateAgencyInfo(request);
        BaseResponse baseResponse = new BaseResponse();
        setSuccessMsg(baseResponse);
        return baseResponse;
    }

    @GetMapping("/agency")
    public GetAgencyInfoResponse getAgencyInfo(@Validated GetAgencyRequest request) {
        AgencyInfoMessage agencyInfoMessage = ppcService.getAgencyInfo(request);
        GetAgencyInfoResponse getAgencyInfoResponse = new GetAgencyInfoResponse();
        getAgencyInfoResponse.setData(agencyInfoMessage);
        setSuccessMsg(getAgencyInfoResponse);
        return getAgencyInfoResponse;
    }

    @DeleteMapping("/agency/{agencyId}")
    public BaseResponse deleteAgencyInfo(@PathVariable("agencyId") String agencyId) {
        ppcService.deleteAgencyInfo(agencyId);
        BaseResponse baseResponse = new BaseResponse();
        setSuccessMsg(baseResponse);
        return baseResponse;
    }

    @PostMapping("/schedule-job")
    public ScheduleJobResponse scheduleJob(@Validated @RequestBody ScheduleJobRequest request) {
        ppcService.scheduleJob(request);
        ScheduleJobResponse scheduleJobResponse = new ScheduleJobResponse();
        setSuccessMsg(scheduleJobResponse);
        return scheduleJobResponse;
    }

    @PostMapping("/job-participant-detail")
    public BaseResponse uploadJobParticipantDetail(
            @Validated @RequestBody UploadJobParticipantDetailRequest request) {
        ppcService.uploadJobParticipantDetail(request);
        BaseResponse baseResponse = new BaseResponse();
        setSuccessMsg(baseResponse);
        return baseResponse;
    }

    @GetMapping("/algorithms-job")
    public GetAlgorithmJobResponse getAlgorithmJob(@Validated GetAlgorithmJobRequest request) {
        AlgorithmJobMessage algorithmJobMessage = ppcService.getAlgorithmJob(request);
        GetAlgorithmJobResponse getAlgorithmJobResponse = new GetAlgorithmJobResponse();
        getAlgorithmJobResponse.setData(algorithmJobMessage);
        setSuccessMsg(getAlgorithmJobResponse);
        return getAlgorithmJobResponse;
    }

    @GetMapping("/dataset-job")
    public GetDatasetJobResponse getDatasetJob(@Validated GetDatasetJobRequest request) {
        DatasetJobMessage datasetJobMessage = ppcService.getDatasetJob(request);
        GetDatasetJobResponse getDatasetJobResponse = new GetDatasetJobResponse();
        getDatasetJobResponse.setData(datasetJobMessage);
        setSuccessMsg(getDatasetJobResponse);
        return getDatasetJobResponse;
    }
}
