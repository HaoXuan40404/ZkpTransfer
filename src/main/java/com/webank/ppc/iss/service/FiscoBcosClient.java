package com.webank.ppc.iss.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.ppc.iss.common.PpcCommonUtils;
import com.webank.ppc.iss.config.CryptoConfig;
import com.webank.ppc.iss.contracts.PpcContractController;
import com.webank.ppc.iss.message.*;
import com.webank.ppc.iss.message.blockchain.*;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class FiscoBcosClient {

    public static final String SET_TABLE_NAME = "d_dataset";
    public static final String AUTHORIZATION_TABLE_NAME = "d_authorization";
    public static final String AUTHORIZATION_REQUEST_TABLE_NAME = "d_authorization_request";
    public static final String ALGORITHM_TABLE_NAME = "d_algorithm";
    public static final String JOB_AUDIT_TABLE_NAME = "d_job_audit";
    public static final String AGENCY_TABLE_NAME = "d_agency";
    public static final String DATASET_JOB_TABLE_NAME = "d_dataset_job";
    public static final String ALGORITHM_JOB_TABLE_NAME = "d_algorithm_job";
    public static final String CONTRACT_KEY_REG = "===";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private Client client;
    @Autowired
    private CryptoConfig cryptoConfig;
    private final PpcContractController ppcContract;

    public FiscoBcosClient(PpcContractController ppcContract) {
        this.ppcContract = ppcContract;
    }

    public static String[] getContractKeyList(String key) {
        return Arrays.asList(key.split(CONTRACT_KEY_REG)).stream().filter((s -> !s.isEmpty())).collect(Collectors.toList()).toArray(new String[0]);
    }

    private static boolean isTransactionSucceeded(TransactionReceipt transactionReceipt) {
        return 0 == transactionReceipt.getStatus();
    }

    private static void checkTransactionReceipt(TransactionReceipt transactionReceipt)
            throws ContractException {
        if (!isTransactionSucceeded(transactionReceipt)) {
            throw new ContractException(
                    transactionReceipt.getStatus() + transactionReceipt.getMessage());
        }
    }

    public String makeContractKey(String... keys) {
        String finalKey = "";
        for (String key : keys) {
            finalKey += CONTRACT_KEY_REG + key;
        }
        return finalKey;
    }

    private String encryptObject(Object metaDataPlanet) throws Exception {
        String metaValue = AESUtils.encrypt(objectMapper.writeValueAsString(metaDataPlanet), cryptoConfig.getKey());
        return metaValue;
    }

    public void uploadDataSetMetaData(
            String ownerAgencyId,
            String datasetId,
            String datasetTitle,
            String ownerAgencyName,
            String datasetAlgorithm,
            String dataDetail)
            throws Exception {

        DatasetInfoChainSet datasetInfo = new DatasetInfoChainSet();
        datasetInfo.setDatasetId(datasetId);
        datasetInfo.setOwnerAgencyName(ownerAgencyName);
        datasetInfo.setOwnerAgencyId(ownerAgencyId);
        datasetInfo.setDatasetTitle(datasetTitle);
        datasetInfo.setDatasetAlgorithm(datasetAlgorithm);
        datasetInfo.setDataDetail(dataDetail);
        datasetInfo.setCreateTime(PpcCommonUtils.getTimeStamp().longValue());

        TransactionReceipt transactionReceipt =
                ppcContract.uploadMeta(
                        SET_TABLE_NAME,
                        datasetId,
                        encryptObject(datasetInfo)
                );
        checkTransactionReceipt(transactionReceipt);
    }

    public void setDatasetAlgorithmFlag(String ownerAgencyId, String datasetId, String flag)
            throws Exception {
        String jsonObjectStringRaw = ppcContract.queryMeta(SET_TABLE_NAME, datasetId);
        String jsonObjectString = AESUtils.decrypt(jsonObjectStringRaw, cryptoConfig.getKey());

        if (!jsonObjectString.equals("")) {
            DatasetInfoChainSet datasetInfo = objectMapper.readValue(jsonObjectString, DatasetInfoChainSet.class);
            datasetInfo.setAlgorithmFlag(flag);
            TransactionReceipt transactionReceipt =
                    ppcContract.uploadMeta(SET_TABLE_NAME, datasetId, encryptObject(datasetInfo));
            checkTransactionReceipt(transactionReceipt);
        } else {
            throw new ContractException(
                    "datasetId:" + datasetId + " not found");
        }
    }

    public void updateDataSetMetaData(UpdateDatasetRequest request) throws Exception {
        String datasetId = request.getDatasetId();
        String jsonObjectStringRaw = ppcContract.queryMeta(SET_TABLE_NAME, datasetId);
        String jsonObjectString = AESUtils.decrypt(jsonObjectStringRaw, cryptoConfig.getKey());

        if (!jsonObjectString.equals("")) {
            DatasetInfoChainSet datasetInfo = objectMapper.readValue(jsonObjectString, DatasetInfoChainSet.class);
            datasetInfo.setDatasetTitle(request.getDatasetTitle());
            datasetInfo.setOwnerAgencyId(request.getOwnerAgencyId());
            datasetInfo.setDataDetail(request.getDataDetail());
            datasetInfo.setUpdateTime(PpcCommonUtils.getTimeStamp().longValue());
            TransactionReceipt transactionReceipt =
                    ppcContract.updateMeta(SET_TABLE_NAME, datasetId, encryptObject(datasetInfo));
            checkTransactionReceipt(transactionReceipt);
        } else {
            throw new ContractException(
                    "datasetId:" + datasetId + " not found");
        }
    }

    public void removeDataset(String ownerAgencyId, String datasetId) throws ContractException {
        TransactionReceipt transactionReceipt = ppcContract.removeMeta(SET_TABLE_NAME, datasetId);
        checkTransactionReceipt(transactionReceipt);
    }

    public void authorizeDataset(ActiveAuthDatasetRequest request) throws Exception {
        String datasetId = request.getDatasetId();
        List<Authority> authorityList = request.getAuthorizedAlgoList();
        List<String> keyList = new ArrayList<>();
        List<String> datasetAuthorityChainSetListStr = new ArrayList<>();

        for (Authority authority : authorityList) {
            String authorizeDatasetKey = makeContractKey(datasetId, authority.getAgencyId(), authority.getAlgorithmId());
            DatasetAuthorityChainSet datasetAuthorityChainSet = new DatasetAuthorityChainSet();
            datasetAuthorityChainSet.setAuthorizationDate(authority.getExpiredTime());
            keyList.add(authorizeDatasetKey);
            datasetAuthorityChainSetListStr.add(encryptObject(datasetAuthorityChainSet));
        }
        TransactionReceipt transactionReceipt =
                ppcContract.updateMetaWithMultiKey(
                        AUTHORIZATION_TABLE_NAME,
                        keyList,
                        datasetAuthorityChainSetListStr);
        checkTransactionReceipt(transactionReceipt);
    }


    public void removeAuthorize(DeleteAuthDatasetRequest request) throws ContractException, JsonProcessingException {
        String datasetId = request.getDatasetId();
        List<AuthorityRemove> authorityRemoveList = request.getAuthorityRemoveList();
        List<String> removeKeyList = new ArrayList<>();

        for (AuthorityRemove authorityRemove : authorityRemoveList) {
            String authorizeDatasetKey = makeContractKey(datasetId, authorityRemove.getAgencyId(), authorityRemove.getAlgorithmId());
            removeKeyList.add(authorizeDatasetKey);

        }

        TransactionReceipt transactionReceipt =
                ppcContract.removeMetaWithMultiKey(
                        AUTHORIZATION_TABLE_NAME,
                        removeKeyList);
        checkTransactionReceipt(transactionReceipt);

    }

    public void uploadAlgorithm(UploadAlgorithmRequest request) throws Exception {
        String ownerAgencyId = request.getOwnerAgencyId();
        String algorithmId = request.getAlgorithmId();
        String algorithmVersion = request.getAlgorithmVersion();
        String algorithmTitle = request.getAlgorithmTitle();
        String ownerAgencyName = request.getOwnerAgencyName();
        String algorithmContent = request.getAlgorithmContent();
        String algorithmType = request.getAlgorithmType();
        String algorithmSubtype = request.getAlgorithmSubtype();
        AlgorithmInfoChainSet algorithmInfoChainSet = new AlgorithmInfoChainSet();
        algorithmInfoChainSet.setAlgorithmId(algorithmId);
        algorithmInfoChainSet.setAlgorithmContent(algorithmContent);
        algorithmInfoChainSet.setAlgorithmTitle(algorithmTitle);
        algorithmInfoChainSet.setAlgorithmVersion(algorithmVersion);
        algorithmInfoChainSet.setOwnerAgencyName(ownerAgencyName);
        algorithmInfoChainSet.setOwnerAgencyId(ownerAgencyId);
        algorithmInfoChainSet.setAlgorithmType(algorithmType);
        algorithmInfoChainSet.setAlgorithmSubtype(algorithmSubtype);
        algorithmInfoChainSet.setCreateTime(System.currentTimeMillis());

        TransactionReceipt transactionReceipt = ppcContract.uploadMeta(ALGORITHM_TABLE_NAME, algorithmId, encryptObject(algorithmInfoChainSet));

        checkTransactionReceipt(transactionReceipt);
    }

    public void updateAlgorithm(UpdateAlgorithmRequest request) throws Exception {
//        String ownerAgencyId = request.getOwnerAgencyId();
        String algorithmId = request.getAlgorithmId();
        String algorithmVersion = request.getAlgorithmVersion();
        String algorithmTitle = request.getAlgorithmTitle();
        String algorithmContent = request.getAlgorithmContent();

        String jsonObjectStringRaw = ppcContract.queryMeta(ALGORITHM_TABLE_NAME, algorithmId);
        String jsonObjectString = AESUtils.decrypt(jsonObjectStringRaw, cryptoConfig.getKey());
        if (!jsonObjectString.equals("")) {
            AlgorithmInfoChainSet algorithmInfoChainSet = objectMapper.readValue(jsonObjectString, AlgorithmInfoChainSet.class);
            algorithmInfoChainSet.setAlgorithmTitle(algorithmTitle);
            algorithmInfoChainSet.setAlgorithmContent(algorithmContent);
            algorithmInfoChainSet.setAlgorithmVersion(algorithmVersion);
            algorithmInfoChainSet.setUpdateTime(PpcCommonUtils.getTimeStamp().longValue());
            TransactionReceipt transactionReceipt =
                    ppcContract.updateMeta(ALGORITHM_TABLE_NAME, algorithmId, encryptObject(algorithmInfoChainSet));
            checkTransactionReceipt(transactionReceipt);
        } else {
            throw new ContractException(
                    "algorithmId:" + algorithmId + " not found");
        }

    }

    public void removeAlgorithm(String ownerAgencyId, String algorithmId, String algorithmVersion)
            throws ContractException {
        TransactionReceipt transactionReceipt =
                ppcContract.removeMeta(ALGORITHM_TABLE_NAME, algorithmId);
        checkTransactionReceipt(transactionReceipt);
    }

    public void addAuthorizeRequest(AddAuthDatasetRequest request) throws Exception {
        String datasetId = request.getDatasetId();
        List<Authority> authorityList = request.getAuthorizedAlgoList();
        List<String> authKeyList = new ArrayList<>();
        List<String> setList = new ArrayList<>();
        for (Authority authority : authorityList) {
            String authorizeDatasetKey = makeContractKey(datasetId, authority.getAgencyId(), authority.getAlgorithmId());
            DatasetAuthorityChainSet datasetAuthorityChainSet = new DatasetAuthorityChainSet();
            datasetAuthorityChainSet.setAuthorizationDate(authority.getExpiredTime());
            authKeyList.add(authorizeDatasetKey);
            setList.add(encryptObject(datasetAuthorityChainSet));
        }

        TransactionReceipt transactionReceipt =
                ppcContract.updateMetaWithMultiKey(
                        AUTHORIZATION_REQUEST_TABLE_NAME,
                        authKeyList,
                        setList
                );
        checkTransactionReceipt(transactionReceipt);
    }

    public void authorizeDatasetByRequest(ActiveAuthDatasetByOtherRequest request)
            throws Exception {
        String datasetId = request.getDatasetId();
        List<Authority> authorityList = request.getAuthorizedAlgoList();

        List<String> authKeyList = new ArrayList<>();
        List<String> setList = new ArrayList<>();
        for (Authority authority : authorityList) {
            String authorizeDatasetKey = makeContractKey(datasetId, authority.getAgencyId(), authority.getAlgorithmId());
            DatasetAuthorityChainSet datasetAuthorityChainSet = new DatasetAuthorityChainSet();
            datasetAuthorityChainSet.setAuthorizationDate(authority.getExpiredTime());
            authKeyList.add(authorizeDatasetKey);
            setList.add(encryptObject(datasetAuthorityChainSet));
        }

        TransactionReceipt transactionReceipt =
                ppcContract.updateAndRemoveMultiMetaWithoutCheck(
                        AUTHORIZATION_TABLE_NAME,
                        AUTHORIZATION_REQUEST_TABLE_NAME,
                        authKeyList,
                        setList);
        checkTransactionReceipt(transactionReceipt);
    }

    public void uploadJobAudit(UploadJobAuditRequest request) throws Exception {
        String jobId = request.getJobId();
        String jobTitle = request.getJobTitle();
        String agencyId = request.getAgencyId();
        String userName = request.getUserName();
        String userRole = request.getUserRole();
        String jobStatus = request.getJobStatus();
        String auditData = request.getAuditData();

        // TODO: check upload or update info omit
        // upload twice check

        JobAuditChainSet jobAuditChainSet = new JobAuditChainSet();
        jobAuditChainSet.setAgencyId(agencyId);
        jobAuditChainSet.setJobStatus(jobStatus);
        jobAuditChainSet.setUserName(userName);
        jobAuditChainSet.setUserRole(userRole);
        jobAuditChainSet.setAuditData(auditData);
        jobAuditChainSet.setJobTitle(jobTitle);
        jobAuditChainSet.setCreateTime(PpcCommonUtils.getTimeStamp().longValue());

        TransactionReceipt transactionReceipt =
                ppcContract.uploadMeta(
                        JOB_AUDIT_TABLE_NAME,
                        makeContractKey(jobId, agencyId, userRole),
                        encryptObject(jobAuditChainSet));
        checkTransactionReceipt(transactionReceipt);
    }

    public void delegateJob(ScheduleJobRequest request) throws ContractException {
        TransactionReceipt transactionReceipt =
                ppcContract.delegateJob(request.getJobId(), request.getJobEvent());
        checkTransactionReceipt(transactionReceipt);
    }

    public void uploadAgencyInfo(UploadAgencyRequest request) throws Exception {
        AgencyInfoChainSet agencyInfoChainSet = new AgencyInfoChainSet();
        agencyInfoChainSet.setAgencyId(request.getAgencyId());
        agencyInfoChainSet.setAgencyName(request.getAgencyName());
        agencyInfoChainSet.setAgencyPublicKey(request.getAgencyPublicKey());
        agencyInfoChainSet.setComputationProvider(request.getIsComputationProvider());
        agencyInfoChainSet.setAgencyDescription(request.getAgencyDescription());
        agencyInfoChainSet.setGatewayUrl(request.getGatewayUrl());
        agencyInfoChainSet.setManagementUrl(request.getManagementUrl());
        agencyInfoChainSet.setJobUrl(request.getJobUrl());
        agencyInfoChainSet.setCreateTime(PpcCommonUtils.getTimeStamp().longValue());

        TransactionReceipt transactionReceipt =
                ppcContract.uploadMeta(AGENCY_TABLE_NAME, request.getAgencyId(), encryptObject(agencyInfoChainSet));
        checkTransactionReceipt(transactionReceipt);
    }

    public void updateAgencyInfo(UpdateAgencyRequest request) throws Exception {
        String jsonObjectStringRaw = ppcContract.queryMeta(AGENCY_TABLE_NAME, request.getAgencyId());
        String jsonObjectString = AESUtils.decrypt(jsonObjectStringRaw, cryptoConfig.getKey());

        if (jsonObjectString.equals("")) {
            throw new ContractException(
                    "agencyId:" + request.getAgencyId() + " not found");
        }
        AgencyInfoChainSet agencyInfoChainSet = objectMapper.readValue(jsonObjectString, AgencyInfoChainSet.class);
        agencyInfoChainSet.setAgencyName(request.getAgencyName());
        agencyInfoChainSet.setAgencyPublicKey(request.getAgencyPublicKey());
        agencyInfoChainSet.setAgencyDescription(request.getAgencyDescription());
        agencyInfoChainSet.setComputationProvider(request.getIsComputationProvider());
        agencyInfoChainSet.setGatewayUrl(request.getGatewayUrl());
        agencyInfoChainSet.setManagementUrl(request.getManagementUrl());
        agencyInfoChainSet.setJobUrl(request.getJobUrl());
        agencyInfoChainSet.setUpdateTime(PpcCommonUtils.getTimeStamp().longValue());

        TransactionReceipt transactionReceipt =
                ppcContract.updateMeta(
                        AGENCY_TABLE_NAME,
                        request.getAgencyId(),
                        encryptObject(agencyInfoChainSet));
        checkTransactionReceipt(transactionReceipt);
    }

    public void deleteAgencyInfo(String agencyId) throws ContractException {
        TransactionReceipt transactionReceipt = ppcContract.removeMeta(AGENCY_TABLE_NAME, agencyId);
        checkTransactionReceipt(transactionReceipt);
    }

    public void uploadJobParticipantDetail(UploadJobParticipantDetailRequest request)
            throws Exception {
        List<String> algorithmList = new ArrayList<>();
        algorithmList.add(request.getAlgorithmId());
        AlgorithmJobChainSet algorithmJobChainSet = new AlgorithmJobChainSet();
        DatasetJobChainSet datasetJobChainSet = new DatasetJobChainSet();
        algorithmJobChainSet.setJobId(request.getJobId());
        datasetJobChainSet.setJobId(request.getJobId());
        TransactionReceipt transactionReceipt =
                ppcContract.updateTwoMetaWithMultiKey(
                        DATASET_JOB_TABLE_NAME, ALGORITHM_JOB_TABLE_NAME, request.getDatasetIdList(), algorithmList, encryptObject(datasetJobChainSet), encryptObject(algorithmJobChainSet));
        checkTransactionReceipt(transactionReceipt);
    }
}
