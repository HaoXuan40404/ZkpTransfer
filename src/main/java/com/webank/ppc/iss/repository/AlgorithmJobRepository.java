package com.webank.ppc.iss.repository;

import com.webank.ppc.iss.entity.AlgorithmJob;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: caryliao @Date: 2022/2/18 10:35
 */
public interface AlgorithmJobRepository
        extends PagingAndSortingRepository<AlgorithmJob, Long>,
        JpaSpecificationExecutor<AlgorithmJob> {
    @Query("select count(*) from AlgorithmJob where status = ?1")
    long countByStatus(long status);

    @Query("select count(*) from AlgorithmJob where algorithmId = ?1 and jobId = ?2")
    long countExisted(String algorithmId, String jobId);

    AlgorithmJob findFirstByAlgorithmIdAndJobIdAndBlockNumberLessThan(String algorithmId, String jobId,
            Long blockNumber);

    AlgorithmJob findFirstByAlgorithmIdAndJobIdAndBlockNumberGreaterThanEqual(String algorithmId, String jobId,
            Long blockNumber);

    @Modifying
    @Transactional
    @Query(value = "INSERT IGNORE INTO d_algorithm_job (status, algorithm_id, job_id, block_number) " +
            "VALUES (:#{#algorithmJob.status}, :#{#algorithmJob.algorithmId}, :#{#algorithmJob.jobId}, :#{#algorithmJob.blockNumber})", nativeQuery = true)
    void saveIgnore(@Param("algorithmJob") AlgorithmJob algorithmJob);

}
