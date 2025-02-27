package com.webank.ppc.iss.repository;

import com.webank.ppc.iss.entity.JobAudit;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: caryliao @Date: 2022/2/18 10:35
 */
public interface JobAuditRepository
        extends PagingAndSortingRepository<JobAudit, Long>, JpaSpecificationExecutor<JobAudit> {

    @Query("select count(*) from JobAudit where jobId = ?1 and agencyId =?2 and userRole =?3")
    long countExisted(String jobId, String agencyId, String userRole);

    JobAudit findFirstByJobIdAndAgencyIdAndUserRoleAndBlockNumberLessThan(String jobId, String agencyId,
            String userRole, Long blockNumber);

    JobAudit findFirstByJobIdAndAgencyIdAndUserRoleAndBlockNumberGreaterThanEqual(String jobId, String agencyId,
            String userRole, Long blockNumber);

    @Modifying
    @Transactional
    @Query(value = "INSERT IGNORE INTO d_job_audit (status, job_id, job_title, agency_id, user_name, user_role, job_status, audit_data, create_time, update_time, block_number) "
            +
            "VALUES (:#{#jobAudit.status}, :#{#jobAudit.jobId}, :#{#jobAudit.jobTitle}, :#{#jobAudit.agencyId}, :#{#jobAudit.userName}, :#{#jobAudit.userRole}, :#{#jobAudit.jobStatus}, :#{#jobAudit.auditData}, :#{#jobAudit.createTime}, :#{#jobAudit.updateTime}, :#{#jobAudit.blockNumber})", nativeQuery = true)
    void saveIgnore(@Param("jobAudit") JobAudit jobAudit);

}
