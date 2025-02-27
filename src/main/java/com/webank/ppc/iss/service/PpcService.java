package com.webank.ppc.iss.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.ppc.iss.common.PpcCommonUtils;
import com.webank.ppc.iss.common.StatusEnum;
import com.webank.ppc.iss.config.CryptoConfig;
import com.webank.ppc.iss.entity.*;
import com.webank.ppc.iss.message.*;
import com.webank.ppc.iss.message.blockchain.*;
import com.webank.ppc.iss.repository.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@Slf4j
public class PpcService {

    public static BlockingQueue<ChainQueueSet> chainUploadQueueSets = new LinkedBlockingQueue();
    public static BlockingQueue<ChainQueueSet> chainUpdateQueueSets = new LinkedBlockingQueue();
    public static BlockingQueue<ChainQueueSet> chainRemoveQueueSets = new LinkedBlockingQueue();

    public static BlockingQueue<ChainJobEventQueueSet> chainJobEventQueueSets = new LinkedBlockingQueue();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private ChainService chainService;
    @Autowired
    private DatasetRepository datasetRepository;
    @Autowired
    private DatasetAuthorizationRepository datasetAuthorizationRepository;
    @Autowired
    private DatasetAuthorizationRequestRepository datasetAuthorizationRequestRepository;
    @Autowired
    private AlgorithmRepository algorithmRepository;
    @Autowired
    private AlgorithmJobRepository algorithmJobRepository;
    @Autowired
    private DatasetJobRepository datasetJobRepository;
    @Autowired
    private DatasetAvailableRepository datasetAvailableRepository;
    @Autowired
    private JobAuditRepository jobAuditRepository;
    @Autowired
    private AgencyRepository agencyRepository;
    @Autowired
    private BlockChainRepository blockChainRepository;

    @Autowired
    private CryptoConfig cryptoConfig;

    private static boolean checkAuthority(
            String agencyId, String algorithmId, DatasetContent datasetContent) {
        List<DatasetAuthority> datasetAuthorityList = datasetContent.getDatasetAuthority();
        if (datasetContent.getOwnerAgencyId().equals(agencyId)) {
            return true;
        }

        for (DatasetAuthority datasetAuthority : datasetAuthorityList) {
            if (datasetAuthority.getAuthorizedAgencyId().equals(agencyId)) {
                if ("PPC_ALGO_ALL".equals(datasetAuthority.getAlgorithmId())
                        || datasetAuthority.getAlgorithmId().equals(algorithmId)) {
                    if (datasetAuthority.getAuthorizationDate() > System.currentTimeMillis()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void uploadDataset(
            String ownerAgencyId,
            String datasetId,
            String datasetTitle,
            String ownerAgencyName,
            String datasetAlgorithm,
            String dataDetail) {
        chainService.uploadDataSetMetaData(
                ownerAgencyId, datasetId, datasetTitle, ownerAgencyName, datasetAlgorithm, dataDetail);
    }

    public void setDatasetAlgorithmFlag(String ownerAgencyId, String datasetId, String flag) {
        chainService.setDatasetAlgorithmFlag(ownerAgencyId, datasetId, flag);
    }

    public void updateDataset(UpdateDatasetRequest request) {
        chainService.updateDataSetMetaData(request);
    }

    public DatasetMessage getDataset(GetDatasetRequest request) {
        String authorizeAgencyId = request.getAuthorizeAgencyId();
        String ownerAgencyId = request.getOwnerAgencyId();
        String datasetId = request.getDatasetId();
        String datasetTitle = request.getDatasetTitle();
        String ownerAgencyName = request.getOwnerAgencyName();
        String algorithmFlag = request.getAlgorithmFlag();
        String algorithmId = request.getAlgorithmId();
        String datasetAlgorithm = request.getDatasetAlgorithm();
        Boolean showAvailable = request.getShowAvailable();
        long dateRangeStart = request.getDateRangeStart();
        long dateRangeEnd = request.getDateRangeEnd();
        int pageOffset = request.getPageOffset();
        int pageSize = request.getPageSize();
        boolean showAvailableFlag = true;
        if (ownerAgencyName != null || datasetTitle != null)
        {
            showAvailableFlag = false;
        }
        if (showAvailable == true && showAvailableFlag == true) {
            DatasetMessage datasetMessage = new DatasetMessage();

            long datasetCount = datasetAvailableRepository.countByOwnerAgencyIdAndStatus(authorizeAgencyId, algorithmId,
                    System.currentTimeMillis());
            List<Dataset> datasets;
            if(pageSize == 0) {
                datasets = datasetAvailableRepository.findDatasetsWithAvailableAll(authorizeAgencyId,
                algorithmId, System.currentTimeMillis());
            }
            else 
            {
                datasets = datasetAvailableRepository.findDatasetsWithAvailable(authorizeAgencyId,
                algorithmId, System.currentTimeMillis(), pageSize, pageOffset);
            }

            List<DatasetContent> datasetContentList = new ArrayList<>();

            log.info("getDataset, datasets:{}", datasets.size());
            datasets.forEach(dataset -> {
                DatasetContent datasetContent = new DatasetContent();
                datasetContent.setOwnerAgencyId(dataset.getOwnerAgencyId());
                datasetContent.setCreateTime(dataset.getCreateTime());
                datasetContent.setUpdateTime(dataset.getUpdateTime());
                datasetContent.setDatasetId(dataset.getDatasetId());
                datasetContent.setDatasetTitle(dataset.getDatasetTitle());
                datasetContent.setDatasetAlgorithm(dataset.getDatasetAlgorithm());
                datasetContent.setOwnerAgencyName(dataset.getOwnerAgencyName());
                datasetContent.setDataDetail(dataset.getDataDetail());
                datasetContent.setAlgorithmFlag(dataset.getAlgorithmFlag());
                datasetContent.setAuthorized(true);
                datasetContentList.add(datasetContent);
            });
            // // 按照CreateTime对datasetContentList进行排序
            // datasetContentList.sort((o1, o2) ->
            // o2.getCreateTime().compareTo(o1.getCreateTime()));
            datasetMessage.setContent(datasetContentList);

            datasetMessage.setTotal(datasetCount);
            // 根据pageOffset和pageSize来判断是否是最后一页
            boolean isLastPage = (pageOffset + 1) * pageSize >= datasetCount;
            datasetMessage.setLastPage(isLastPage);
            return datasetMessage;
        }
        Specification<Dataset> queryCondition = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(
                    criteriaBuilder.equal(
                            root.get("status"), StatusEnum.Normal.getValue()));
            if (StringUtils.hasLength(ownerAgencyId)) {
                predicateList.add(
                        criteriaBuilder.equal(root.get("ownerAgencyId"), ownerAgencyId));
            }
            if (StringUtils.hasLength(datasetId)) {
                predicateList.add(criteriaBuilder.equal(root.get("datasetId"), datasetId));
            }
            if (StringUtils.hasLength(datasetTitle)) {
                predicateList.add(
                        criteriaBuilder.like(
                                root.get("datasetTitle"),
                                "%" + handleSpecialCharacters(datasetTitle) + "%", '\\'));
            }
            if (StringUtils.hasLength(datasetAlgorithm)) {
                predicateList.add(criteriaBuilder.equal(root.get("datasetAlgorithm"), datasetAlgorithm));
            }
            if (StringUtils.hasLength(algorithmFlag)) {
                predicateList.add(
                        criteriaBuilder.like(
                                root.get("algorithmFlag"),
                                "%" + handleSpecialCharacters(algorithmFlag) + "%", '\\'));
            }
            if (StringUtils.hasLength(ownerAgencyName)) {
                predicateList.add(
                        criteriaBuilder.like(
                                root.get("ownerAgencyName"),
                                "%" + handleSpecialCharacters(ownerAgencyName) + "%", '\\'));
            }
            if (PpcCommonUtils.hashValue(dateRangeStart)) {
                predicateList.add(
                        criteriaBuilder.ge(root.get("createTime"), dateRangeStart));
            }
            if (PpcCommonUtils.hashValue(dateRangeEnd)) {
                predicateList.add(criteriaBuilder.le(root.get("createTime"), dateRangeEnd));
            }
            return criteriaBuilder.and(
                    predicateList.toArray(new Predicate[predicateList.size()]));
        };

        List<Dataset> datasetList;
        long total = 0;
        boolean isLastPage = false;

        if (showAvailable) {
        datasetList =
        datasetRepository.findAll(
        queryCondition, Sort.by(Sort.Direction.DESC, "updateTime"));
        } else {
        Page<Dataset> datasetPage = datasetRepository.findAll(
                queryCondition,
                PageRequest.of(
                        (int) Math.ceil(pageOffset / (double) pageSize),
                        pageSize,
                        Sort.by(Sort.Direction.DESC, "updateTime")));
        datasetList = datasetPage.getContent();
        total = datasetPage.getTotalElements();
        isLastPage = datasetPage.isLast();
        }

        List<DatasetContent> datasetContentList = new ArrayList<>();
        datasetList.forEach(
                dataset -> {
                    DatasetContent datasetContent = new DatasetContent();
                    String queriedOwnerAgencyId = dataset.getOwnerAgencyId();
                    datasetContent.setOwnerAgencyId(queriedOwnerAgencyId);
                    String queriedDatasetId = dataset.getDatasetId();
                    datasetContent.setDatasetId(queriedDatasetId);
                    datasetContent.setDatasetTitle(dataset.getDatasetTitle());
                    datasetContent.setDatasetAlgorithm(dataset.getDatasetAlgorithm());
                    datasetContent.setOwnerAgencyName(dataset.getOwnerAgencyName());
                    datasetContent.setDataDetail(dataset.getDataDetail());
                    datasetContent.setAlgorithmFlag(dataset.getAlgorithmFlag());
                    List<DatasetAuthority> datasetAuthority = new ArrayList<>();
                    List<DatasetAuthority> datasetAuthorityRequest = new ArrayList<>();
                    if (authorizeAgencyId.equals(queriedOwnerAgencyId)) {
                        datasetContent.setAuthorized(true);
                    } else {
                        String PPC_ALL_AUTH_FLAG = "PPC_ALGO_ALL";
                        long authorize_record = datasetAuthorizationRepository.countRecord(
                                StatusEnum.Normal.getValue(),
                                queriedDatasetId,
                                authorizeAgencyId,
                                PPC_ALL_AUTH_FLAG,
                                System.currentTimeMillis());
                        if (authorize_record == 0) {
                            authorize_record = datasetAuthorizationRepository.countRecord(
                                    StatusEnum.Normal.getValue(),
                                    queriedDatasetId,
                                    authorizeAgencyId,
                                    algorithmId,
                                    System.currentTimeMillis());
                        }
                        datasetContent.setAuthorized(authorize_record != 0);
                    }
                    Specification<DatasetAuthorization> queryAuthCondition = (root, criteriaQuery, criteriaBuilder) -> {
                        List<Predicate> predicateList = new ArrayList<>();
                        predicateList.add(
                                criteriaBuilder.equal(
                                        root.get("status"),
                                        StatusEnum.Normal.getValue()));
                        predicateList.add(
                                criteriaBuilder.equal(
                                        root.get("datasetId"), queriedDatasetId));
                        return criteriaBuilder.and(
                                predicateList.toArray(new Predicate[predicateList.size()]));
                    };
                    List<DatasetAuthorization> datasetAuthorizationList = datasetAuthorizationRepository.findAll(
                            queryAuthCondition, Sort.by(Sort.Direction.DESC, "updateTime"));
                    datasetAuthorizationList.forEach(
                            datasetAuthorizationItem -> {
                                DatasetAuthority datasetAuthorityItem = new DatasetAuthority();
                                datasetAuthorityItem.setAuthorizedAgencyId(
                                        datasetAuthorizationItem.getAuthorizedAgencyId());
                                datasetAuthorityItem.setAuthorizationDate(
                                        datasetAuthorizationItem.getExpiredTime());
                                datasetAuthorityItem.setAlgorithmId(
                                        datasetAuthorizationItem.getAlgorithmId());
                                datasetAuthority.add(datasetAuthorityItem);
                            });

                    Specification<DatasetAuthorizationRequest> queryRequestCondition = (root, criteriaQuery,
                            criteriaBuilder) -> {
                        List<Predicate> predicateList = new ArrayList<>();
                        predicateList.add(
                                criteriaBuilder.equal(
                                        root.get("status"),
                                        StatusEnum.Normal.getValue()));
                        predicateList.add(
                                criteriaBuilder.equal(
                                        root.get("datasetId"), queriedDatasetId));
                        return criteriaBuilder.and(
                                predicateList.toArray(new Predicate[predicateList.size()]));
                    };

                    List<DatasetAuthorizationRequest> datasetAuthorizationListRequest = datasetAuthorizationRequestRepository
                            .findAll(
                                    queryRequestCondition,
                                    Sort.by(Sort.Direction.DESC, "updateTime"));
                    datasetAuthorizationListRequest.forEach(
                            datasetAuthorizationItem -> {
                                DatasetAuthority datasetAuthorityItem = new DatasetAuthority();
                                datasetAuthorityItem.setAuthorizedAgencyId(
                                        datasetAuthorizationItem.getAuthorizedAgencyId());
                                datasetAuthorityItem.setAuthorizationDate(
                                        datasetAuthorizationItem.getExpiredTime());
                                datasetAuthorityItem.setAlgorithmId(
                                        datasetAuthorizationItem.getAlgorithmId());
                                datasetAuthorityRequest.add(datasetAuthorityItem);
                            });

                    datasetContent.setDatasetAuthority(datasetAuthority);
                    datasetContent.setDatasetAuthorityRequest(datasetAuthorityRequest);
                    datasetContent.setCreateTime(dataset.getCreateTime());
                    datasetContent.setUpdateTime(dataset.getUpdateTime());

                    if (showAvailable) {
                    if (checkAuthority(authorizeAgencyId, algorithmId, datasetContent)) {
                    datasetContentList.add(datasetContent);
                    }
                    } else {
                    datasetContentList.add(datasetContent);
                    }
                });

        DatasetMessage datasetMessage = new DatasetMessage();
        if (showAvailable) {
        total = datasetContentList.size();
        isLastPage = pageOffset + pageSize >= total;
        for (int i = pageOffset; i < pageOffset + pageSize && i < total; i++) {
        datasetMessage.getContent().add(datasetContentList.get(i));
        }
        } else {
        datasetMessage.setContent(datasetContentList);
        }
        datasetMessage.setTotal(total);
        datasetMessage.setLastPage(isLastPage);
        return datasetMessage;
    }

    private String handleSpecialCharacters(String likeKeyWords) {
        return likeKeyWords.replaceAll("%", "\\\\%").replaceAll("_", "\\\\_");
    }

    public void deleteDataset(String ownerAgencyId, String datasetId) {
        chainService.removeDataset(ownerAgencyId, datasetId);
    }

    public void authorizeDataset(ActiveAuthDatasetRequest request) {
        chainService.authorizeDataset(request);
    }

    public void removeAuthorizeDataset(DeleteAuthDatasetRequest request) {
        chainService.removeAuthorize(request);
    }

    public void authorizeDatasetByRequest(ActiveAuthDatasetByOtherRequest request) {
        chainService.authorizeDatasetByRequest(request);
    }

    public void addAuthorizeRequest(AddAuthDatasetRequest request) {
        chainService.addAuthorizeRequest(request);
    }

    public void uploadAlgorithm(UploadAlgorithmRequest request) {
        chainService.uploadAlgorithm(request);
    }

    public void updateAlgorithm(UpdateAlgorithmRequest request) {
        chainService.updateAlgorithm(request);
    }

    public void deleteAlgorithm(String ownerAgencyId, String algorithmId, String algorithmVersion) {
        chainService.removeAlgorithm(ownerAgencyId, algorithmId, algorithmVersion);
    }

    public AlgorithmMessage getAlgorithm(GetAlgorithmRequest request) {
        String ownerAgencyId = request.getOwnerAgencyId();
        String algorithmId = request.getAlgorithmId();
        String algorithmVersion = request.getAlgorithmVersion();
        String algorithmTitle = request.getAlgorithmTitle();
        String algorithmType = request.getAlgorithmType();
        String algorithmSubtype = request.getAlgorithmSubtype();
        String ownerAgencyName = request.getOwnerAgencyName();
        long dateRangeStart = request.getDateRangeStart();
        long dateRangeEnd = request.getDateRangeEnd();
        int pageOffset = request.getPageOffset();
        int pageSize = request.getPageSize();
        Specification<Algorithm> queryCondition = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(
                    criteriaBuilder.equal(
                            root.get("status"), StatusEnum.Normal.getValue()));
            if (StringUtils.hasLength(ownerAgencyId)) {
                predicateList.add(
                        criteriaBuilder.equal(root.get("ownerAgencyId"), ownerAgencyId));
            }
            if (StringUtils.hasLength(algorithmId)) {
                predicateList.add(
                        criteriaBuilder.equal(root.get("algorithmId"), algorithmId));
            }
            if (StringUtils.hasLength(algorithmVersion)) {
                predicateList.add(
                        criteriaBuilder.equal(
                                root.get("algorithmVersion"), algorithmVersion));
            }
            if (StringUtils.hasLength(algorithmType)) {
                predicateList.add(
                        criteriaBuilder.equal(
                                root.get("algorithmType"), algorithmType));
            }
            if (StringUtils.hasLength(algorithmSubtype)) {
                predicateList.add(
                        criteriaBuilder.equal(
                                root.get("algorithmSubtype"), algorithmSubtype));
            }
            if (StringUtils.hasLength(algorithmTitle)) {
                predicateList.add(
                        criteriaBuilder.like(
                                root.get("algorithmTitle"),
                                "%" + handleSpecialCharacters(algorithmTitle) + "%", '\\'));
            }
            if (StringUtils.hasLength(ownerAgencyName)) {
                predicateList.add(
                        criteriaBuilder.like(
                                root.get("ownerAgencyName"),
                                "%" + handleSpecialCharacters(ownerAgencyName) + "%", '\\'));
            }
            if (PpcCommonUtils.hashValue(dateRangeStart)) {
                predicateList.add(
                        criteriaBuilder.ge(root.get("createTime"), dateRangeStart));
            }
            if (PpcCommonUtils.hashValue(dateRangeEnd)) {
                predicateList.add(criteriaBuilder.le(root.get("createTime"), dateRangeEnd));
            }
            return criteriaBuilder.and(
                    predicateList.toArray(new Predicate[predicateList.size()]));
        };
        pageOffset = (int) Math.ceil(pageOffset / (double) pageSize);
        Page<Algorithm> algorithmPage = algorithmRepository.findAll(
                queryCondition,
                PageRequest.of(
                        pageOffset, pageSize, Sort.by(Sort.Direction.DESC, "updateTime")));
        long total = algorithmPage.getTotalElements();
        boolean isLastPage = algorithmPage.isLast();
        List<Algorithm> algorithmList = algorithmPage.getContent();
        List<AlgorithmInfo> algorithmInfoList = new ArrayList<>();
        algorithmList.forEach(
                algorithm -> {
                    AlgorithmInfo algorithmInfo = new AlgorithmInfo();
                    algorithmInfo.setOwnerAgencyId(algorithm.getOwnerAgencyId());
                    algorithmInfo.setAlgorithmId(algorithm.getAlgorithmId());
                    algorithmInfo.setAlgorithmVersion(algorithm.getAlgorithmVersion());
                    algorithmInfo.setAlgorithmTitle(algorithm.getAlgorithmTitle());
                    algorithmInfo.setOwnerAgencyName(algorithm.getOwnerAgencyName());
                    algorithmInfo.setAlgorithmContent(algorithm.getAlgorithmContent());
                    algorithmInfo.setCreateTime(algorithm.getCreateTime());
                    algorithmInfo.setUpdateTime(algorithm.getUpdateTime());
                    algorithmInfoList.add(algorithmInfo);
                });
        AlgorithmMessage algorithmMessage = new AlgorithmMessage();
        algorithmMessage.setTotal(total);
        algorithmMessage.setLastPage(isLastPage);
        algorithmMessage.setContent(algorithmInfoList);
        return algorithmMessage;
    }

    public JobAuditMessage getJobAudit(GetJobAuditRequest request) {
        String jobId = request.getJobId();
        log.info("getJobAudit, jobId:" + jobId);
        String agencyId = request.getAgencyId();
        log.info("queryCondition start");
        Specification<JobAudit> queryCondition = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(
                    criteriaBuilder.equal(
                            root.get("status"), StatusEnum.Normal.getValue()));
            predicateList.add(criteriaBuilder.equal(root.get("jobId"), jobId));
            if (StringUtils.hasLength(agencyId)) {
                predicateList.add(criteriaBuilder.equal(root.get("agencyId"), agencyId));
            }
            return criteriaBuilder.and(
                    predicateList.toArray(new Predicate[predicateList.size()]));
        };
        log.info("sort by update time");
        List<JobAudit> jobAuditList = jobAuditRepository.findAll(
                queryCondition, Sort.by(Sort.Direction.DESC, "updateTime"));
        List<JobAuditInfo> jobAuditInfoList = new ArrayList<>();
        JobAuditMessage jobAuditMessage = new JobAuditMessage();
        log.info("append with jobAuditList");
        jobAuditList.forEach(
                jobAudit -> {
                    if (jobAuditMessage.getJobTitle() == null) {
                        jobAuditMessage.setJobTitle(jobAudit.getJobTitle());
                    }
                    JobAuditInfo jobAuditInfo = new JobAuditInfo();
                    jobAuditInfo.setAgencyId(jobAudit.getAgencyId());
                    jobAuditInfo.setJobStatus(jobAudit.getJobStatus());
                    jobAuditInfo.setUserName(jobAudit.getUserName());
                    jobAuditInfo.setUserRole(jobAudit.getUserRole());
                    jobAuditInfo.setAuditData(jobAudit.getAuditData());
                    jobAuditInfo.setCreateTime(jobAudit.getCreateTime());
                    jobAuditInfo.setUpdateTime(jobAudit.getUpdateTime());
                    jobAuditInfoList.add(jobAuditInfo);
                });
        jobAuditMessage.setJobId(jobId);
        jobAuditMessage.setJobAuditInfoList(jobAuditInfoList);
        log.info("add jobAuditMessage");
        return jobAuditMessage;
    }

    public void uploadJobAudit(UploadJobAuditRequest request) {
        chainService.uploadJobAudit(request);
    }

    public void scheduleJob(ScheduleJobRequest request) {
        chainService.delegateJob(request);
    }

    public void uploadAgencyInfo(UploadAgencyRequest request) {
        chainService.uploadAgencyInfo(request);
    }

    public void updateAgencyInfo(UpdateAgencyRequest request) {
        chainService.updateAgencyInfo(request);
    }

    public AgencyInfoMessage getAgencyInfo(GetAgencyRequest request) {
        String agencyId = request.getAgencyId();
        String agencyName = request.getAgencyName();
        String agencyPublicKey = request.getAgencyPublicKey();
        String agencyDescription = request.getAgencyDescription();
        Boolean isComputationProvider = request.getIsComputationProvider();
        String gatewayUrl = request.getGatewayUrl();
        String managementUrl = request.getManagementUrl();
        String jobUrl = request.getJobUrl();
        long dateRangeStart = request.getDateRangeStart();
        long dateRangeEnd = request.getDateRangeEnd();
        int pageOffset = request.getPageOffset();
        int pageSize = request.getPageSize();
        Specification<Agency> queryCondition = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(
                    criteriaBuilder.equal(
                            root.get("status"), StatusEnum.Normal.getValue()));
            if (StringUtils.hasLength(agencyId)) {
                predicateList.add(criteriaBuilder.equal(root.get("agencyId"), agencyId));
            }
            if (StringUtils.hasLength(agencyName)) {
                predicateList.add(
                        criteriaBuilder.like(
                                root.get("agencyName"),
                                "%" + handleSpecialCharacters(agencyName) + "%", '\\'));
            }
            if (StringUtils.hasLength(agencyPublicKey)) {
                predicateList.add(
                        criteriaBuilder.like(
                                root.get("agencyPublicKey"),
                                "%" + handleSpecialCharacters(agencyPublicKey) + "%", '\\'));
            }
            if (StringUtils.hasLength(agencyDescription)) {
                predicateList.add(
                        criteriaBuilder.like(
                                root.get("agencyDescription"),
                                "%" + handleSpecialCharacters(agencyDescription) + "%", '\\'));
            }
            if (isComputationProvider != null) {
                predicateList.add(
                        criteriaBuilder.equal(
                                root.get("isComputationProvider"), isComputationProvider));
            }
            if (StringUtils.hasLength(gatewayUrl)) {
                predicateList.add(
                        criteriaBuilder.like(
                                root.get("gatewayUrl"),
                                "%" + handleSpecialCharacters(gatewayUrl) + "%", '\\'));
            }
            if (StringUtils.hasLength(managementUrl)) {
                predicateList.add(
                        criteriaBuilder.like(
                                root.get("managementUrl"),
                                "%" + handleSpecialCharacters(managementUrl) + "%", '\\'));
            }
            if (StringUtils.hasLength(jobUrl)) {
                predicateList.add(
                        criteriaBuilder.like(
                                root.get("jobUrl"),
                                "%" + handleSpecialCharacters(jobUrl) + "%", '\\'));
            }
            if (PpcCommonUtils.hashValue(dateRangeStart)) {
                predicateList.add(
                        criteriaBuilder.ge(root.get("createTime"), dateRangeStart));
            }
            if (PpcCommonUtils.hashValue(dateRangeEnd)) {
                predicateList.add(criteriaBuilder.le(root.get("createTime"), dateRangeEnd));
            }
            return criteriaBuilder.and(
                    predicateList.toArray(new Predicate[predicateList.size()]));
        };
        pageOffset = (int) Math.ceil(pageOffset / (double) pageSize);
        Page<Agency> agencyPage = agencyRepository.findAll(
                queryCondition,
                PageRequest.of(
                        pageOffset, pageSize, Sort.by(Sort.Direction.DESC, "updateTime")));
        long total = agencyPage.getTotalElements();
        boolean isLastPage = agencyPage.isLast();
        List<Agency> agencyList = agencyPage.getContent();
        List<AgencyInfo> agencyInfoList = new ArrayList<>();
        agencyList.forEach(
                agency -> {
                    AgencyInfo agencyInfo = new AgencyInfo();
                    agencyInfo.setAgencyId(agency.getAgencyId());
                    agencyInfo.setAgencyName(agency.getAgencyName());
                    agencyInfo.setAgencyPublicKey(agency.getAgencyPublicKey());
                    agencyInfo.setAgencyDescription(agency.getAgencyDescription());
                    agencyInfo.setComputationProvider(agency.getIsComputationProvider());
                    agencyInfo.setGatewayUrl(agency.getGatewayUrl());
                    agencyInfo.setManagementUrl(agency.getManagementUrl());
                    agencyInfo.setJobUrl(agency.getJobUrl());
                    agencyInfo.setCreateTime(agency.getCreateTime());
                    agencyInfo.setUpdateTime(agency.getUpdateTime());
                    agencyInfoList.add(agencyInfo);
                });
        AgencyInfoMessage agencyInfoMessage = new AgencyInfoMessage();
        agencyInfoMessage.setTotal(total);
        agencyInfoMessage.setLastPage(isLastPage);
        agencyInfoMessage.setContent(agencyInfoList);
        return agencyInfoMessage;
    }

    public void deleteAgencyInfo(String agencyId) {
        chainService.deleteAgencyInfo(agencyId);
    }

    public StatsMessage getStats(String agencyId) {
        long agencyCount = agencyRepository.countByStatus(StatusEnum.Normal.getValue());
        long datasetCount = datasetRepository.countByStatus(StatusEnum.Normal.getValue());
        long myDatasetCount = datasetRepository.countByOwnerAgencyIdAndStatus(
                agencyId, StatusEnum.Normal.getValue());
        long algorithmCount = algorithmRepository.countByStatus(StatusEnum.Normal.getValue());
        long myAlgorithmCount = algorithmRepository.countByOwnerAgencyIdAndStatus(
                agencyId, StatusEnum.Normal.getValue());
        StatsMessage statsMessage = new StatsMessage();
        statsMessage.setAgencyCount(agencyCount);
        statsMessage.setAlgorithmCount(algorithmCount);
        statsMessage.setMyAlgorithmCount(myAlgorithmCount);
        statsMessage.setDatasetCount(datasetCount);
        statsMessage.setMyDatasetCount(myDatasetCount);
        return statsMessage;
    }

    public void uploadJobParticipantDetail(UploadJobParticipantDetailRequest request) {
        chainService.uploadJobParticipantDetail(request);
    }

    public AlgorithmJobMessage getAlgorithmJob(GetAlgorithmJobRequest request) {
        String algorithmId = request.getAlgorithmId();
        String jobId = request.getJobId();
        int pageOffset = request.getPageOffset();
        int pageSize = request.getPageSize();
        Specification<AlgorithmJob> queryCondition = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(
                    criteriaBuilder.equal(
                            root.get("status"), StatusEnum.Normal.getValue()));
            if (StringUtils.hasLength(algorithmId)) {
                predicateList.add(
                        criteriaBuilder.equal(root.get("algorithmId"), algorithmId));
            }
            if (StringUtils.hasLength(jobId)) {
                predicateList.add(criteriaBuilder.equal(root.get("jobId"), jobId));
            }
            return criteriaBuilder.and(
                    predicateList.toArray(new Predicate[predicateList.size()]));
        };
        pageOffset = (int) Math.ceil(pageOffset / (double) pageSize);
        Page<AlgorithmJob> algorithmPage = algorithmJobRepository.findAll(
                queryCondition, PageRequest.of(pageOffset, pageSize));
        long total = algorithmPage.getTotalElements();
        boolean isLastPage = algorithmPage.isLast();
        List<AlgorithmJob> algorithmJobList = algorithmPage.getContent();
        List<AlgorithmJobInfo> algorithmJobInfoList = new ArrayList<>();
        algorithmJobList.forEach(
                algorithmJob -> {
                    AlgorithmJobInfo algorithmJobInfo = new AlgorithmJobInfo();
                    algorithmJobInfo.setJobId(algorithmJob.getJobId());
                    algorithmJobInfoList.add(algorithmJobInfo);
                });
        AlgorithmJobMessage algorithmJobMessage = new AlgorithmJobMessage();
        algorithmJobMessage.setTotal(total);
        algorithmJobMessage.setLastPage(isLastPage);
        algorithmJobMessage.setContent(algorithmJobInfoList);
        return algorithmJobMessage;
    }

    public DatasetJobMessage getDatasetJob(GetDatasetJobRequest request) {
        String datasetId = request.getDatasetId();
        String jobId = request.getJobId();
        int pageOffset = request.getPageOffset();
        int pageSize = request.getPageSize();
        Specification<DatasetJob> queryCondition = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(
                    criteriaBuilder.equal(
                            root.get("status"), StatusEnum.Normal.getValue()));
            if (StringUtils.hasLength(datasetId)) {
                predicateList.add(criteriaBuilder.equal(root.get("datasetId"), datasetId));
            }
            if (StringUtils.hasLength(jobId)) {
                predicateList.add(criteriaBuilder.equal(root.get("jobId"), jobId));
            }
            return criteriaBuilder.and(
                    predicateList.toArray(new Predicate[predicateList.size()]));
        };
        pageOffset = (int) Math.ceil(pageOffset / (double) pageSize);
        Page<DatasetJob> datasetPage = datasetJobRepository.findAll(queryCondition,
                PageRequest.of(pageOffset, pageSize));
        long total = datasetPage.getTotalElements();
        boolean isLastPage = datasetPage.isLast();
        List<DatasetJob> datasetJobList = datasetPage.getContent();
        List<DatasetJobInfo> datasetJobInfoList = new ArrayList<>();
        datasetJobList.forEach(
                datasetJob -> {
                    DatasetJobInfo datasetJobInfo = new DatasetJobInfo();
                    datasetJobInfo.setJobId(datasetJob.getJobId());
                    datasetJobInfoList.add(datasetJobInfo);
                });
        DatasetJobMessage datasetJobMessage = new DatasetJobMessage();
        datasetJobMessage.setTotal(total);
        datasetJobMessage.setLastPage(isLastPage);
        datasetJobMessage.setContent(datasetJobInfoList);
        return datasetJobMessage;
    }

    public void uploadMetaDatabase(String tableName, String metaKey, String metaValueCipher,
            BigInteger getBlockNumber) {
        log.info("uploadMetaDatabase: tableName: {}, metaKey:{}, metaValueCipher:{}, blockNumber:{}", tableName,
                metaKey, metaValueCipher, getBlockNumber);
        String metaValue = null;
        try {
            metaValue = AESUtils.decrypt(metaValueCipher, cryptoConfig.getKey());
            log.info("uploadMetaDatabase: metaValue:{}", metaValue);
        } catch (Exception e) {
            log.error("uploadMetaDatabase decrypt message failed, e: " + e.getMessage());
        }
        switch (tableName) {
            case FiscoBcosClient.AGENCY_TABLE_NAME:
                saveAgency(metaKey, metaValue, getBlockNumber);
                break;
            case FiscoBcosClient.SET_TABLE_NAME:
                saveDataset(metaKey, metaValue, getBlockNumber);
                break;
            case FiscoBcosClient.ALGORITHM_TABLE_NAME:
                saveAlgorithm(metaKey, metaValue, getBlockNumber);
                break;
            case FiscoBcosClient.ALGORITHM_JOB_TABLE_NAME:
                saveAlgorithmJob(metaKey, metaValue, getBlockNumber);
                break;
            case FiscoBcosClient.DATASET_JOB_TABLE_NAME:
                saveDatasetJob(metaKey, metaValue, getBlockNumber);
                break;
            case FiscoBcosClient.AUTHORIZATION_REQUEST_TABLE_NAME:
                saveDatasetAuthRequest(metaKey, metaValue, getBlockNumber);
                break;
            case FiscoBcosClient.AUTHORIZATION_TABLE_NAME:
                saveDatasetAuth(metaKey, metaValue, getBlockNumber);
                break;
            case FiscoBcosClient.JOB_AUDIT_TABLE_NAME:
                saveJobAudit(metaKey, metaValue, getBlockNumber);
                break;
            default:
                log.error("uploadMetaDatabase failed, tableName:" + tableName);
        }
    }

    public BlockChain getEventStatus(String eventType) {
        return blockChainRepository.findFirstByEventType(eventType);
    }

    public void setEventStatus(String eventType, long blockNumber) {
        BlockChain blockChain = blockChainRepository.findFirstByEventType(eventType);
        if (blockChain == null) {
            blockChain = new BlockChain();
        }
        blockChain.setEventType(eventType);
        blockChain.setMaxBlockNumber(blockNumber);
        blockChainRepository.save(blockChain);
    }

    private void saveDataset(String metaKey, String metaValue, BigInteger blockNumber) {
        try {
            log.info("saveDataset: {} with blockNumber {}", metaValue, blockNumber.longValue());
            DatasetInfoChainSet datasetInfo = objectMapper.readValue(metaValue, DatasetInfoChainSet.class);
            Dataset datasetQuery = datasetRepository.findFirstByDatasetIdAndBlockNumberGreaterThanEqual(
                    datasetInfo.getDatasetId(), blockNumber.longValue());
            if (datasetQuery != null) {
                log.info("datasetQuery: {}", datasetQuery);
                log.info("saveDataset" + metaValue + " has already been updated");
                return;
            }

            Dataset dataset = new Dataset();
            dataset.setCreateTime(PpcCommonUtils.getTimeStamp().longValue());
            dataset.setDatasetId(datasetInfo.getDatasetId());
            dataset.setDatasetTitle(datasetInfo.getDatasetTitle());
            dataset.setDatasetAlgorithm(datasetInfo.getDatasetAlgorithm());
            dataset.setDataDetail(datasetInfo.getDataDetail());
            dataset.setBlockNumber(blockNumber.longValue());
            dataset.setAlgorithmFlag(datasetInfo.getAlgorithmFlag());
            dataset.setOwnerAgencyId(datasetInfo.getOwnerAgencyId());
            dataset.setOwnerAgencyName(datasetInfo.getOwnerAgencyName());
            dataset.setUpdateTime(PpcCommonUtils.getTimeStamp().longValue());
            dataset.setStatus(StatusEnum.Normal.getValue());
            log.info("try saveDataset" + metaValue + "start");
            datasetRepository.save(dataset);
            log.info("try saveDataset {} success", metaValue);
        } catch (Exception e) {
            log.error("saveDataset parsed failed" + metaValue);
            log.error(String.valueOf(e));
        }
    }

    private void saveAlgorithm(String metaKey, String metaValue, BigInteger blockNumber) {
        try {
            log.info("saveAlgorithm: {} with blockNumber {}", metaValue, blockNumber.longValue());
            AlgorithmInfoChainSet algorithmInfoChainSet = objectMapper.readValue(metaValue,
                    AlgorithmInfoChainSet.class);

            if (algorithmRepository.findFirstByAlgorithmIdAndBlockNumberGreaterThanEqual(metaValue,
                    blockNumber.longValue()) != null) {
                log.info("saveAlgorithm" + metaValue + " has already been updated");
            }

            Algorithm algorithm = new Algorithm();
            algorithm.setCreateTime(PpcCommonUtils.getTimeStamp().longValue());
            algorithm.setAlgorithmId(algorithmInfoChainSet.getAlgorithmId());
            algorithm.setAlgorithmTitle(algorithmInfoChainSet.getAlgorithmTitle());
            algorithm.setAlgorithmContent(algorithmInfoChainSet.getAlgorithmContent());
            algorithm.setAlgorithmType(algorithmInfoChainSet.getAlgorithmType());
            algorithm.setAlgorithmSubtype(algorithmInfoChainSet.getAlgorithmSubtype());
            algorithm.setBlockNumber(blockNumber.longValue());
            algorithm.setAlgorithmVersion(algorithmInfoChainSet.getAlgorithmVersion());
            algorithm.setOwnerAgencyId(algorithmInfoChainSet.getOwnerAgencyId());
            algorithm.setOwnerAgencyName(algorithmInfoChainSet.getOwnerAgencyName());
            algorithm.setUpdateTime(PpcCommonUtils.getTimeStamp().longValue());
            algorithm.setStatus(StatusEnum.Normal.getValue());

            algorithmRepository.save(algorithm);
            log.info("saveAlgorithm {}, success", metaValue);
        } catch (Exception e) {
            log.error("saveAlgorithm parsed failed" + metaValue);
            log.error(String.valueOf(e));
        }
    }

    private void saveAgency(String metaKey, String metaValue, BigInteger blockNumber) {

        try {
            log.info("saveAgency: {} with blockNumber {}", metaValue, blockNumber.longValue());
            Agency agency = new Agency();
            AgencyInfoChainSet agencyInfoChainSet = objectMapper.readValue(metaValue, AgencyInfoChainSet.class);
            if (agencyRepository.findFirstByAgencyIdAndBlockNumberGreaterThanEqual(metaKey,
                    blockNumber.longValue()) != null) {
                log.info("saveAgency" + metaValue + " has already been updated");
                return;
            }
            agency.setCreateTime(PpcCommonUtils.getTimeStamp().longValue());
            agency.setAgencyId(agencyInfoChainSet.getAgencyId());
            agency.setAgencyName(agencyInfoChainSet.getAgencyName());
            agency.setAgencyPublicKey(agencyInfoChainSet.getAgencyPublicKey());
            agency.setAgencyDescription(agencyInfoChainSet.getAgencyDescription());
            agency.setIsComputationProvider(agencyInfoChainSet.getComputationProvider());
            agency.setGatewayUrl(agencyInfoChainSet.getGatewayUrl());
            agency.setManagementUrl(agencyInfoChainSet.getManagementUrl());
            agency.setJobUrl(agencyInfoChainSet.getJobUrl());
            agency.setStatus(StatusEnum.Normal.getValue());
            agency.setUpdateTime(PpcCommonUtils.getTimeStamp().longValue());
            agency.setBlockNumber(blockNumber.longValue());
            agencyRepository.save(agency);
            log.info("saveAgency {}, success", metaValue);
        } catch (Exception e) {
            log.error("saveAgency parsed failed" + metaValue);
            log.error(String.valueOf(e));
        }
    }

    private void saveAlgorithmJob(String metaKey, String metaValue, BigInteger blockNumber) {
        try {
            log.info("saveAlgorithmJob: {} with blockNumber {}", metaValue, blockNumber.longValue());
            AlgorithmJobChainSet algorithmJobChainSet = objectMapper.readValue(metaValue, AlgorithmJobChainSet.class);
            String jobId = algorithmJobChainSet.getJobId();
            AlgorithmJob algorithmJob = new AlgorithmJob();

            if (algorithmJobRepository.findFirstByAlgorithmIdAndJobIdAndBlockNumberGreaterThanEqual(metaKey, jobId,
                    blockNumber.longValue()) != null) {
                log.info("saveAlgorithmJob" + metaValue + " has already been updated");
                return;
            }
            algorithmJob.setAlgorithmId(metaKey);
            algorithmJob.setJobId(jobId);
            algorithmJob.setBlockNumber(blockNumber.longValue());
            algorithmJob.setStatus(StatusEnum.Normal.getValue());
            algorithmJobRepository.save(algorithmJob);
            log.info("saveAlgorithmJob {}, success", metaValue);

        } catch (Exception e) {
            log.error("saveAlgorithm parsed failed" + metaValue);
            log.error(String.valueOf(e));
        }
    }

    private void saveDatasetJob(String metaKey, String metaValue, BigInteger blockNumber) {
        try {
            log.info("saveDatasetJob: {} with blockNumber {}", metaValue, blockNumber.longValue());
            DatasetJobChainSet datasetJobChainSet = objectMapper.readValue(metaValue, DatasetJobChainSet.class);
            String jobId = datasetJobChainSet.getJobId();

            DatasetJob datasetJob = new DatasetJob();

            if (datasetJobRepository.findFirstByDatasetIdAndJobIdAndBlockNumberGreaterThanEqual(metaKey, jobId,
                    blockNumber.longValue()) != null) {
                log.info("saveDatasetJob" + metaValue + " has already been updated");
                return;
            }
            datasetJob.setDatasetId(metaKey);
            datasetJob.setJobId(jobId);
            datasetJob.setBlockNumber(blockNumber.longValue());
            datasetJob.setStatus(StatusEnum.Normal.getValue());
            datasetJobRepository.save(datasetJob);
            log.info("saveDatasetJob {}, success", metaValue);
        } catch (Exception e) {
            log.error("saveDatasetJob parsed failed" + metaValue);
            log.error(String.valueOf(e));
        }
    }

    private AuthKey getAuthKey(String keys) throws Exception {
        String[] keySet = FiscoBcosClient.getContractKeyList(keys);
        if (keySet.length != 3) {
            log.error("parsed auth key failed, {}", keys);
            throw new Exception("parsed auth key failed");
        }
        return new AuthKey(keySet[0], keySet[1], keySet[2]);
    }

    private JobAuditKey getJobAuditKey(String keys) throws Exception {
        String[] keySet = FiscoBcosClient.getContractKeyList(keys);
        if (keySet.length != 3) {
            log.error("parsed JobAudit key failed, {}", keys);
            throw new Exception("parsed JobAudit failed");
        }
        return new JobAuditKey(keySet[0], keySet[1], keySet[2]);
    }

    private void saveDatasetAuth(String metaKey, String metaValue, BigInteger blockNumber) {
        try {
            log.info("saveDatasetAuth: {} with blockNumber {}", metaValue, blockNumber.longValue());
            AuthKey authKey = getAuthKey(metaKey);

            DatasetAuthorization datasetAuthorization = new DatasetAuthorization();

            if (datasetAuthorizationRepository
                    .findFirstByDatasetIdAndAuthorizedAgencyIdAndAlgorithmIdAndBlockNumberGreaterThanEqual(
                            authKey.getDatasetId(), authKey.agencyId, authKey.algorithmId,
                            blockNumber.longValue()) != null) {
                log.info("saveDatasetAuth" + metaValue + " has already been updated");
                return;
            }

            DatasetAuthorityChainSet datasetAuthorityChainSet = objectMapper.readValue(metaValue,
                    DatasetAuthorityChainSet.class);
            datasetAuthorization.setDatasetId(authKey.getDatasetId());
            datasetAuthorization.setAuthorizedAgencyId(authKey.agencyId);
            datasetAuthorization.setAlgorithmId(authKey.algorithmId);
            datasetAuthorization.setExpiredTime(datasetAuthorityChainSet.getAuthorizationDate());
            datasetAuthorization.setBlockNumber(blockNumber.longValue());
            datasetAuthorization.setStatus(StatusEnum.Normal.getValue());
            datasetAuthorizationRepository.save(datasetAuthorization);
            log.info("saveDatasetAuth {}, success", metaValue);
        } catch (Exception e) {
            log.error("saveDatasetAuth parsed failed, {}", metaValue);
            log.error(String.valueOf(e));
        }
    }

    private void saveDatasetAuthRequest(String metaKey, String metaValue, BigInteger blockNumber) {
        try {
            log.info("saveDatasetAuthRequest: {} with blockNumber {}", metaValue, blockNumber.longValue());
            AuthKey authKey = getAuthKey(metaKey);

            String datasetId = authKey.getDatasetId();
            if (datasetAuthorizationRequestRepository.findFirstByDatasetIdAndAuthorizedAgencyIdAndAlgorithmIdAndBlockNumberGreaterThanEqual(
                    datasetId, authKey.getAgencyId(), authKey.getAlgorithmId(), blockNumber.longValue()) != null) {
                log.info("saveDatasetAuthRequest" + metaValue + " has already been updated");
                return;
            }

            DatasetAuthorizationRequest datasetAuthorizationRequest = new DatasetAuthorizationRequest();

            DatasetAuthorityChainSet datasetAuthorityChainSet = objectMapper.readValue(metaValue,
                    DatasetAuthorityChainSet.class);
            Dataset dataset = datasetRepository.findByDatasetId(datasetId);
            datasetAuthorizationRequest.setDatasetId(datasetId);
            datasetAuthorizationRequest.setDatasetTitle(dataset.getDatasetTitle());
            datasetAuthorizationRequest.setAuthorizedAgencyId(authKey.agencyId);
            datasetAuthorizationRequest.setOwnerAgencyId(dataset.getOwnerAgencyId());
            datasetAuthorizationRequest.setAlgorithmId(authKey.algorithmId);
            datasetAuthorizationRequest.setExpiredTime(datasetAuthorityChainSet.getAuthorizationDate());
            datasetAuthorizationRequest.setBlockNumber(blockNumber.longValue());
            datasetAuthorizationRequest.setStatus(StatusEnum.Normal.getValue());
            long nowTime = System.currentTimeMillis();
            datasetAuthorizationRequest.setCreateTime(nowTime);
            datasetAuthorizationRequest.setUpdateTime(nowTime);
            datasetAuthorizationRequestRepository.save(datasetAuthorizationRequest);
            log.info("saveDatasetAuthRequest {}, success", metaValue);
        } catch (Exception e) {
            log.error("saveDatasetAuth parsed failed, {}", metaValue);
            log.error(String.valueOf(e));
        }
    }

    private void saveJobAudit(String metaKey, String metaValue, BigInteger blockNumber) {
        try {
            log.info("saveJobAudit: {} with blockNumber {}", metaValue, blockNumber.longValue());
            JobAuditKey jobAuditKey = getJobAuditKey(metaKey);

            if (jobAuditRepository.findFirstByJobIdAndAgencyIdAndUserRoleAndBlockNumberGreaterThanEqual(
                    jobAuditKey.getJobId(), jobAuditKey.getAgencyId(), jobAuditKey.getUserRole(),
                    blockNumber.longValue()) != null) {
                log.info("saveJobAudit" + metaValue + " has already been updated");
                return;
            }

            JobAudit jobAudit = new JobAudit();
            jobAudit.setCreateTime(PpcCommonUtils.getTimeStamp().longValue());

            JobAuditChainSet jobAuditChainSet = objectMapper.readValue(metaValue, JobAuditChainSet.class);
            jobAudit.setJobId(jobAuditKey.getJobId());
            jobAudit.setJobTitle(jobAuditChainSet.getJobTitle());
            jobAudit.setUserRole(jobAuditChainSet.getUserRole());
            jobAudit.setAuditData(jobAuditChainSet.getAuditData());
            jobAudit.setJobStatus(jobAuditChainSet.getJobStatus());
            jobAudit.setUserName(jobAuditChainSet.getUserName());
            jobAudit.setBlockNumber(blockNumber.longValue());
            jobAudit.setStatus(StatusEnum.Normal.getValue());
            jobAudit.setAgencyId(jobAuditKey.getAgencyId());
            jobAudit.setCreateTime(jobAuditChainSet.getCreateTime());
            jobAudit.setUpdateTime(jobAuditChainSet.getUpdateTime());
            jobAuditRepository.save(jobAudit);
            log.info("saveJobAudit {}, success", metaValue);
        } catch (Exception e) {
            log.error("saveJobAudit parsed failed" + metaValue);
            log.error(String.valueOf(e));
        }
    }

    public void removeMetaDatabase(String tableName, String metaKey, BigInteger getBlockNumber) {
        log.info("removeMetaDatabase: tableName: {}, metaKey:{}, blockNumber:{}", tableName, metaKey, getBlockNumber);
        switch (tableName) {
            case FiscoBcosClient.AGENCY_TABLE_NAME:
                removeAgency(metaKey, getBlockNumber);
                break;
            case FiscoBcosClient.SET_TABLE_NAME:
                removeDataset(metaKey, getBlockNumber);
                break;
            case FiscoBcosClient.ALGORITHM_TABLE_NAME:
                removeAlgorithm(metaKey, getBlockNumber);
                break;
            case FiscoBcosClient.ALGORITHM_JOB_TABLE_NAME:
                removeAlgorithmJob(metaKey, getBlockNumber);
                break;
            case FiscoBcosClient.DATASET_JOB_TABLE_NAME:
                removeDatasetJob(metaKey, getBlockNumber);
                break;
            case FiscoBcosClient.AUTHORIZATION_REQUEST_TABLE_NAME:
                removeDatasetAuthRequest(metaKey, getBlockNumber);
                break;
            case FiscoBcosClient.AUTHORIZATION_TABLE_NAME:
                removeDatasetAuth(metaKey, getBlockNumber);
                break;
            case FiscoBcosClient.JOB_AUDIT_TABLE_NAME:
                removeJobAudit(metaKey, getBlockNumber);
                break;
            default:
                log.error("removeMetaDatabase failed, tableName:{}, metaKey:{}, getBlockNumber:{}", tableName, metaKey,
                        getBlockNumber);
        }
    }

    private void removeAgency(String metaKey, BigInteger blockNumber) {
        try {
            log.info("removeAgency: {} with blockNumber {}", metaKey, blockNumber.longValue());
            if (agencyRepository.findFirstByAgencyIdAndBlockNumberGreaterThanEqual(metaKey,
                    blockNumber.longValue()) != null) {
                log.info("removeAgency" + metaKey + " has already been updated");
                return;
            }

            // 找到最新
            Agency agency = agencyRepository.findFirstByAgencyIdAndBlockNumberLessThan(metaKey,
                    blockNumber.longValue());
            if (agency == null) {
                log.warn("removeAgency" + metaKey + "not found");
                return;
            }
            agency.setStatus(StatusEnum.Removed.getValue());
            agency.setUpdateTime(PpcCommonUtils.getTimeStamp().longValue());
            agency.setBlockNumber(blockNumber.longValue());
            agencyRepository.save(agency);
            log.info("removeAgency {}, success", metaKey);
        } catch (Exception e) {
            log.error("removeAgency parsed failed");
            log.error(String.valueOf(e));
        }
    }

    private void removeDataset(String metaKey, BigInteger blockNumber) {

        try {
            log.info("removeDataset: {} with blockNumber {}", metaKey, blockNumber.longValue());
            if (datasetRepository.findFirstByDatasetIdAndBlockNumberGreaterThanEqual(metaKey,
                    blockNumber.longValue()) != null) {
                log.info("removeDataset" + metaKey + " has already been updated");
                return;
            }

            Dataset dataset = datasetRepository.findFirstByDatasetIdAndBlockNumberLessThan(metaKey,
                    blockNumber.longValue());
            if (dataset == null) {
                log.warn("removeDataset" + metaKey + "not found");
                return;
            }
            dataset.setStatus(StatusEnum.Removed.getValue());
            dataset.setUpdateTime(PpcCommonUtils.getTimeStamp().longValue());
            dataset.setBlockNumber(blockNumber.longValue());
            datasetRepository.save(dataset);
            log.info("removeDataset {}, success", metaKey);
        } catch (Exception e) {
            log.error("removeDataset parsed failed");
            log.error(String.valueOf(e));
        }
    }

    private void removeDatasetAuthRequest(String metaKey, BigInteger blockNumber) {

        try {
            log.info("removeDatasetAuthRequest: {} with blockNumber {}", metaKey, blockNumber.longValue());
            AuthKey authKey = getAuthKey(metaKey);

            if (datasetAuthorizationRequestRepository
                    .findFirstByDatasetIdAndAuthorizedAgencyIdAndAlgorithmIdAndBlockNumberGreaterThanEqual(
                            authKey.getDatasetId(), authKey.getAgencyId(), authKey.getAlgorithmId(),
                            blockNumber.longValue()) != null) {
                log.info("removeDatasetAuthRequest" + metaKey + " has already been updated");
                return;
            }

            DatasetAuthorizationRequest datasetAuthorizationRequest = datasetAuthorizationRequestRepository
                    .findFirstByDatasetIdAndAuthorizedAgencyIdAndAlgorithmIdAndBlockNumberLessThan(
                            authKey.getDatasetId(), authKey.getAgencyId(), authKey.getAlgorithmId(),
                            blockNumber.longValue());
            if (datasetAuthorizationRequest == null) {
                log.warn("removeDatasetAuthRequest" + metaKey + "not found");
                return;
            }
            datasetAuthorizationRequest.setStatus(StatusEnum.Removed.getValue());
            datasetAuthorizationRequest.setUpdateTime(PpcCommonUtils.getTimeStamp().longValue());
            datasetAuthorizationRequest.setBlockNumber(blockNumber.longValue());
            datasetAuthorizationRequestRepository.save(datasetAuthorizationRequest);
            log.info("removeDatasetAuthRequest {}, success", metaKey);
        } catch (Exception e) {
            log.error("removeDatasetAuthRequest parsed failed");
            log.error(String.valueOf(e));
        }
    }

    private void removeDatasetAuth(String metaKey, BigInteger blockNumber) {

        try {
            log.info("removeDatasetAuth: {} with blockNumber {}", metaKey, blockNumber.longValue());
            AuthKey authKey = getAuthKey(metaKey);
            if (datasetAuthorizationRepository
                    .findFirstByDatasetIdAndAuthorizedAgencyIdAndAlgorithmIdAndBlockNumberGreaterThanEqual(
                            authKey.getDatasetId(), authKey.getAgencyId(), authKey.getAlgorithmId(),
                            blockNumber.longValue()) != null) {
                log.info("removeDatasetAuth" + metaKey + " has already been updated");
                return;
            }

            DatasetAuthorization datasetAuthorization = datasetAuthorizationRepository
                    .findFirstByDatasetIdAndAuthorizedAgencyIdAndAlgorithmIdAndBlockNumberLessThan(
                            authKey.getDatasetId(), authKey.getAgencyId(), authKey.getAlgorithmId(),
                            blockNumber.longValue());
            if (datasetAuthorization == null) {
                log.warn("removeDatasetAuth" + metaKey + "not found");
                return;
            }
            datasetAuthorization.setStatus(StatusEnum.Removed.getValue());
            datasetAuthorization.setUpdateTime(PpcCommonUtils.getTimeStamp().longValue());
            datasetAuthorization.setBlockNumber(blockNumber.longValue());
            datasetAuthorizationRepository.save(datasetAuthorization);
            log.info("removeDatasetAuth {}, success", metaKey);
        } catch (Exception e) {
            log.error("removeDatasetAuth parsed failed");
            log.error(String.valueOf(e));
        }
    }

    private void removeAlgorithm(String metaKey, BigInteger blockNumber) {

        try {
            log.info("removeAlgorithm: {} with blockNumber {}", metaKey, blockNumber.longValue());
            if (algorithmRepository.findFirstByAlgorithmIdAndBlockNumberGreaterThanEqual(metaKey,
                    blockNumber.longValue()) != null) {
                log.info("removeAlgorithm" + metaKey + " has already been updated");
                return;
            }

            Algorithm algorithm = algorithmRepository.findFirstByAlgorithmIdAndBlockNumberLessThan(metaKey,
                    blockNumber.longValue());
            if (algorithm == null) {
                log.warn("removeAlgorithm" + metaKey + "not found");
                return;
            }
            algorithm.setStatus(StatusEnum.Removed.getValue());
            algorithm.setUpdateTime(PpcCommonUtils.getTimeStamp().longValue());
            algorithm.setBlockNumber(blockNumber.longValue());
            algorithmRepository.save(algorithm);
            log.info("removeAlgorithm {}, success", metaKey);
        } catch (Exception e) {
            log.error("removeAlgorithm parsed failed");
            log.error(String.valueOf(e));
        }
    }

    private void removeAlgorithmJob(String metaKey, BigInteger blockNumber) {
        log.error("AlgorithmJob can not removed, algorithmId: {}, blockNumber:{}", metaKey, blockNumber);
    }

    private void removeDatasetJob(String metaKey, BigInteger blockNumber) {
        log.error("DatasetJob can not removed, datasetId: {}, blockNumber:{}", metaKey, blockNumber);
    }

    private void removeJobAudit(String metaKey, BigInteger blockNumber) {
        log.error("JobAudit can not removed, datasetId: {}, blockNumber:{}", metaKey, blockNumber);
    }

    @Data
    final class AuthKey {
        public String datasetId;
        public String agencyId;
        public String algorithmId;

        public AuthKey(String datasetId, String agencyId, String algorithmId) {
            this.datasetId = datasetId;
            this.agencyId = agencyId;
            this.algorithmId = algorithmId;
        }
    }

    @Data
    final class JobAuditKey {
        public String jobId;
        public String agencyId;
        public String userRole;

        public JobAuditKey(String jobId, String agencyId, String userRole) {
            this.jobId = jobId;
            this.agencyId = agencyId;
            this.userRole = userRole;
        }
    }

}
