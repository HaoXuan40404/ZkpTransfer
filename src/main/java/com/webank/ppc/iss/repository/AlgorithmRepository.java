package com.webank.ppc.iss.repository;

import com.webank.ppc.iss.entity.Algorithm;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: caryliao @Date: 2022/2/18 10:35
 */
public interface AlgorithmRepository
        extends PagingAndSortingRepository<Algorithm, Long>, JpaSpecificationExecutor<Algorithm> {
    @Query("select count(*) from Algorithm where ownerAgencyId = ?1 and status = ?2")
    long countByOwnerAgencyIdAndStatus(String agencyId, long status);

    @Query("select count(*) from Algorithm where status = ?1")
    long countByStatus(long status);

    @Query("select count(*) from Algorithm where algorithmId = ?1")
    long countExisted(String algorithmId);

    Algorithm findFirstByAlgorithmIdAndBlockNumberLessThan(String algorithmId, Long blockNumber);

    Algorithm findFirstByAlgorithmIdAndBlockNumberGreaterThanEqual(String algorithmId, Long blockNumber);

    @Modifying
    @Transactional
    @Query(value = "INSERT IGNORE INTO d_algorithm (status, owner_agency_id, algorithm_id, algorithm_version, algorithm_title, owner_agency_name, algorithm_type, algorithm_subtype, algorithm_content, create_time, update_time, block_number) "
            +
            "VALUES (:#{#algorithm.status}, :#{#algorithm.ownerAgencyId}, :#{#algorithm.algorithmId}, :#{#algorithm.algorithmVersion}, :#{#algorithm.algorithmTitle}, :#{#algorithm.ownerAgencyName}, :#{#algorithm.algorithmType}, :#{#algorithm.algorithmSubtype}, :#{#algorithm.algorithmContent}, :#{#algorithm.createTime}, :#{#algorithm.updateTime}, :#{#algorithm.blockNumber})", nativeQuery = true)
    void saveIgnore(@Param("algorithm") Algorithm algorithm);

}
