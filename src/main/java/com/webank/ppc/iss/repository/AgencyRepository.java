package com.webank.ppc.iss.repository;

import com.webank.ppc.iss.entity.Agency;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: caryliao @Date: 2022/2/18 10:35
 */
public interface AgencyRepository
        extends PagingAndSortingRepository<Agency, Long>, JpaSpecificationExecutor<Agency> {
    @Query("select count(*) from Agency where status = ?1")
    long countByStatus(long status);

    @Query("select count(*) from Agency where agencyId = ?1")
    long countExisted(String algorithmId);

    Agency findFirstByAgencyIdAndBlockNumberLessThan(String agencyId, Long blockNumber);

    Agency findFirstByAgencyIdAndBlockNumberGreaterThanEqual(String agencyId, Long blockNumber);

    @Modifying
    @Transactional
    @Query(value = "INSERT IGNORE INTO d_agency (agency_id, status, agency_name, agency_public_key, agency_description, is_computation_provider, gateway_url, management_url, job_url, create_time, update_time, block_number) "
            +
            "VALUES (:#{#agency.agencyId}, :#{#agency.status}, :#{#agency.agencyName}, :#{#agency.agencyPublicKey}, :#{#agency.agencyDescription}, :#{#agency.isComputationProvider}, :#{#agency.gatewayUrl}, :#{#agency.managementUrl}, :#{#agency.jobUrl}, :#{#agency.createTime}, :#{#agency.updateTime}, :#{#agency.blockNumber})", nativeQuery = true)
    void saveIgnore(@Param("agency") Agency agency);

}
