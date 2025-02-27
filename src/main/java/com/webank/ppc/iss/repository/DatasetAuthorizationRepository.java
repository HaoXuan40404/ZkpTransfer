package com.webank.ppc.iss.repository;

import com.webank.ppc.iss.entity.DatasetAuthorization;
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
public interface DatasetAuthorizationRepository
                extends PagingAndSortingRepository<DatasetAuthorization, Long>,
                JpaSpecificationExecutor<DatasetAuthorization> {


        @Query("select count(*) from DatasetAuthorization where status = ?1 and datasetId =?2 and authorizedAgencyId =?3 and algorithmId = ?4 and expiredTime >= ?5")
        long countRecord(
                        long status,
                        String datasetId,
                        String authorizedAgencyId,
                        String algorithmId,
                        Long currentTime);

        List<DatasetAuthorization> findByDatasetIdAndExpiredTimeGreaterThan(
                        String datasetId, Long currentTime);

        @Query("select count(*) from DatasetAuthorization where datasetId = ?1 and authorizedAgencyId = ?2 and algorithmId = ?3")
        long countExisted(String datasetId, String authorizedAgencyId, String algorithmId);

        DatasetAuthorization findFirstByDatasetIdAndAuthorizedAgencyIdAndAlgorithmIdAndBlockNumberLessThan(
                        String datasetId, String authorizedAgencyId, String algorithmId, Long blockNumber);

        DatasetAuthorization findFirstByDatasetIdAndAuthorizedAgencyIdAndAlgorithmIdAndBlockNumberGreaterThanEqual(
                        String datasetId, String authorizedAgencyId, String algorithmId, Long blockNumber);

        @Modifying
        @Transactional
        @Query(value = "INSERT IGNORE INTO d_authorization (status, dataset_id, authorized_agency_id, algorithm_id, expired_time, create_time, update_time, block_number) "
                        +
                        "VALUES (:#{#datasetAuthorization.status}, :#{#datasetAuthorization.datasetId}, :#{#datasetAuthorization.authorizedAgencyId}, :#{#datasetAuthorization.algorithmId}, :#{#datasetAuthorization.expiredTime}, :#{#datasetAuthorization.createTime}, :#{#datasetAuthorization.updateTime}, :#{#datasetAuthorization.blockNumber})", nativeQuery = true)
        void saveIgnore(@Param("datasetAuthorization") DatasetAuthorization datasetAuthorization);
}
