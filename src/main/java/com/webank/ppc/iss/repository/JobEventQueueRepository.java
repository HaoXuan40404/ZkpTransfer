package com.webank.ppc.iss.repository;

import com.webank.ppc.iss.entity.JobEventQueueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface JobEventQueueRepository extends JpaRepository<JobEventQueueEntity, String> {
    @Transactional
    @Modifying
    @Query(value = "insert ignore into t_job_event_queue (job_id, job_event) values (:jobId, :jobEvent)", nativeQuery = true)
    int saveJobEventWithInsertIgnore(@Param("jobId") String jobId, @Param("jobEvent") String jobEvent);

    @Transactional
    @Modifying
    @Query(value = "insert into t_job_event_queue (job_id, job_event) values (:jobId, :jobEvent)", nativeQuery = true)
    int saveJobEvent(@Param("jobId") String jobId, @Param("jobEvent") String jobEvent);

    JobEventQueueEntity findTop1ByOrderByCreatedDateAscJobIdAsc();

    JobEventQueueEntity findFirstByJobIdOrderByCreatedDateAscJobIdAsc(String jobId);

//    JobEventQueueEntity findTopByJobIdA(String jobId);
}
