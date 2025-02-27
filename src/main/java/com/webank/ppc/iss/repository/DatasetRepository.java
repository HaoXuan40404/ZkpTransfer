package com.webank.ppc.iss.repository;

import com.webank.ppc.iss.entity.Dataset;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: caryliao @Date: 2022/2/18 10:35
 */
public interface DatasetRepository
        extends PagingAndSortingRepository<Dataset, Long>, JpaSpecificationExecutor<Dataset> {
    @Query("select count(*) from Dataset where ownerAgencyId = ?1 and status = ?2")
    long countByOwnerAgencyIdAndStatus(String agencyId, long status);

    @Query("select count(*) from Dataset where status = ?1")
    long countByStatus(long status);

    @Query("select count(*) from Dataset where datasetId = ?1")
    long countExisted(String datasetId);

    Dataset findFirstByDatasetIdAndBlockNumberLessThan(String datasetId, Long blockNumber);

    Dataset findFirstByDatasetIdAndBlockNumberGreaterThanEqual(String datasetId, Long blockNumber);

    Dataset findByDatasetId(String datasetId);

    @Query(value = "INSERT IGNORE INTO d_dataset (dataset_id, dataset_title, dataset_algorithm, data_detail, block_number, algorithm_flag, owner_agency_id, owner_agency_name, update_time, create_time, status) "
            +
            "VALUES (:#{#dataset.datasetId}, :#{#dataset.datasetTitle}, :#{#dataset.datasetAlgorithm}, :#{#dataset.dataDetail}, :#{#dataset.blockNumber}, :#{#dataset.algorithmFlag}, :#{#dataset.ownerAgencyId}, :#{#dataset.ownerAgencyName}, :#{#dataset.updateTime}, :#{#dataset.createTime}, :#{#dataset.status})", nativeQuery = true)
    @Modifying
    @Transactional
    void saveIgnore(@Param("dataset") Dataset dataset);
}
