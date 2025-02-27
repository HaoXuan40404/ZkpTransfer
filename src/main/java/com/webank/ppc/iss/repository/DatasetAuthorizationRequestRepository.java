package com.webank.ppc.iss.repository;

import com.webank.ppc.iss.entity.DatasetAuthorizationRequest;
import com.webank.ppc.iss.entity.DatasetJob;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author: caryliao @Date: 2022/2/18 10:35
 */
public interface DatasetAuthorizationRequestRepository
                extends PagingAndSortingRepository<DatasetAuthorizationRequest, Long>,
                JpaSpecificationExecutor<DatasetAuthorizationRequest> {

        boolean existsByDatasetIdAndAuthorizedAgencyIdAndExpiredTimeGreaterThan(
                        String datasetId, String authorizedAgencyId, Long currentTime);

        List<DatasetAuthorizationRequest> findByDatasetIdAndExpiredTimeGreaterThan(
                        String datasetId, Long currentTime);

        @Query("select count(*) from DatasetAuthorizationRequest where status = ?1 and datasetId =?2 and authorizedAgencyId =?3 and algorithmId = ?4")
        long countRecord(long status, String datasetId, String authorizedAgencyId, String algorithmId);

        @Query("select count(*) from DatasetAuthorizationRequest where datasetId = ?1 and authorizedAgencyId = ?2 and algorithmId = ?3")
        long countExisted(String datasetId, String authorizedAgencyId, String algorithmId);

        DatasetAuthorizationRequest findFirstByDatasetIdAndAuthorizedAgencyIdAndAlgorithmIdAndBlockNumberLessThan(
                        String datasetId, String authorizedAgencyId, String algorithmId, Long blockNumber);

        DatasetAuthorizationRequest findFirstByDatasetIdAndAuthorizedAgencyIdAndAlgorithmIdAndBlockNumberGreaterThanEqual(
                        String datasetId, String authorizedAgencyId, String algorithmId, Long blockNumber);

        @Modifying
        @Transactional
        @Query(value = "INSERT IGNORE INTO d_authorization_request (status, dataset_id, dataset_title, authorized_agency_id, owner_agency_id, algorithm_id, expired_time, create_time, update_time, block_number) "
                        +
                        "VALUES (:#{#datasetAuthorizationRequest.status}, :#{#datasetAuthorizationRequest.datasetId}, :#{#datasetAuthorizationRequest.datasetTitle}, :#{#datasetAuthorizationRequest.authorizedAgencyId}, :#{#datasetAuthorizationRequest.ownerAgencyId}, :#{#datasetAuthorizationRequest.algorithmId}, :#{#datasetAuthorizationRequest.expiredTime}, :#{#datasetAuthorizationRequest.createTime}, :#{#datasetAuthorizationRequest.updateTime}, :#{#datasetAuthorizationRequest.blockNumber})", nativeQuery = true)
        void saveIgnore(@Param("datasetAuthorizationRequest") DatasetAuthorizationRequest datasetAuthorizationRequest);

}
