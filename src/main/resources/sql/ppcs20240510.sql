CREATE TABLE IF NOT EXISTS `t_job_event_queue` (
    job_id varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '任务id',
    job_event text COLLATE utf8mb4_bin NOT NULL COMMENT '任务事件信息',
    created_date timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建日期',
    last_modified_date timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '最后修改日期',
    PRIMARY KEY(`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='队列表';


CREATE TABLE IF NOT EXISTS `shedlock` (
    name varchar(64) NOT NULL,
    lock_until timestamp(3) NOT NULL,
    locked_at timestamp(3) NOT NULL,
    locked_by varchar(255) NOT NULL,
    PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

