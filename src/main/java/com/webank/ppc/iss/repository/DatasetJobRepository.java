package com.webank.ppc.iss.repository;

import com.webank.ppc.iss.entity.DatasetJob;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: caryliao @Date: 2022/2/18 10:35
 */
public interface DatasetJobRepository
        extends PagingAndSortingRepository<DatasetJob, Long>, JpaSpecificationExecutor<DatasetJob> {
    @Query("select count(*) from DatasetJob where status = ?1")
    long countByStatus(long status);

    @Query("select count(*) from DatasetJob where datasetId = ?1 and jobId = ?2")
    long countExisted(String datasetId, String jobId);

    DatasetJob findFirstByDatasetIdAndJobIdAndBlockNumberLessThan(String datasetId, String jobId, Long blockNumber);

    DatasetJob findFirstByDatasetIdAndJobIdAndBlockNumberGreaterThanEqual(String datasetId, String jobId, Long blockNumber);

    @Modifying
    @Transactional
    @Query(value = "INSERT IGNORE INTO d_dataset_job (status, dataset_id, job_id, block_number) " +
            "VALUES (:#{#datasetJob.status}, :#{#datasetJob.datasetId}, :#{#datasetJob.jobId}, :#{#datasetJob.blockNumber})", nativeQuery = true)
    void saveIgnore(@Param("datasetJob") DatasetJob datasetJob);

}
