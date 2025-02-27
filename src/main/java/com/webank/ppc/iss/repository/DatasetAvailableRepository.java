package com.webank.ppc.iss.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.webank.ppc.iss.entity.Dataset;

import java.util.List;

public interface DatasetAvailableRepository
                extends PagingAndSortingRepository<Dataset, Long>, JpaSpecificationExecutor<Dataset> {

        @Query(value = "SELECT DISTINCT d.* FROM d_dataset d " +
                        "LEFT JOIN d_authorization da ON (d.dataset_id = da.dataset_id AND d.status = 0) " +
                        "WHERE (da.status = 0 AND da.authorized_agency_id = ?1 " +
                        "AND (da.algorithm_id = ?2 OR da.algorithm_id = 'PPC_ALGO_ALL') AND ?3 < da.expired_time) " +
                        "OR (d.owner_agency_id = ?1 AND d.status = 0)" +
                        "ORDER BY d.update_time DESC " +
                        "LIMIT ?4 " +
                        "OFFSET ?5", nativeQuery = true)
        List<Dataset> findDatasetsWithAvailable(String authorizedAgencyId, String algorithmId, Long currentTime,
                        int pageSize, int pageOffset);

        @Query(value = "SELECT DISTINCT d.* FROM d_dataset d " +
                        "LEFT JOIN d_authorization da ON (d.dataset_id = da.dataset_id AND d.status = 0) " +
                        "WHERE (da.status = 0 AND da.authorized_agency_id = ?1 " +
                        "AND (da.algorithm_id = ?2 OR da.algorithm_id = 'PPC_ALGO_ALL') AND ?3 < da.expired_time) " +
                        "OR (d.owner_agency_id = ?1 AND d.status = 0)" +
                        "ORDER BY d.update_time DESC", nativeQuery = true)
        List<Dataset> findDatasetsWithAvailableAll(String authorizedAgencyId, String algorithmId, Long currentTime);

        @Query(value = "SELECT COUNT(*) FROM(SELECT DISTINCT d.* FROM d_dataset d " +
                        "LEFT JOIN d_authorization da ON (d.dataset_id = da.dataset_id AND d.status = 0) " +
                        "WHERE (da.status = 0 AND da.authorized_agency_id = ?1 " +
                        "AND (da.algorithm_id = ?2 OR da.algorithm_id = 'PPC_ALGO_ALL') AND ?3 < da.expired_time) " +
                        "OR (d.owner_agency_id = ?1 AND d.status = 0)) AS subquery", nativeQuery = true)
        long countByOwnerAgencyIdAndStatus(String authorizedAgencyId, String algorithmId, Long currentTime);

}
