package com.webank.ppc.iss.repository;

import com.webank.ppc.iss.entity.JobDetailEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface JobDetailRepository extends CrudRepository<JobDetailEntity, String> {
    /**
     * 查询是否有运行中的任务
     *
     * @return
     */
    @Query("select count(jd.jobId) > 0 from JobDetailEntity jd WHERE jd.jobStatus = 'RUNNING'")
    boolean hasExistRunningJob();

    @Query("select count(jd.jobId) > ?1 from JobDetailEntity jd WHERE jd.jobStatus = 'RUNNING'")
    boolean hasExistRunningJobWithConcurrency(long concurrency);

    JobDetailEntity findFirstByJobId(String jobId);

}