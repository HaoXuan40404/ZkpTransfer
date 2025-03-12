CREATE TABLE transaction_history (
    id INT NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    biz_seq VARCHAR(64) NOT NULL COMMENT '业务流水号',
    commitment VARCHAR(256) COMMENT '承诺',
    value INT NOT NULL COMMENT '交易金额',
    owner VARCHAR(100) NOT NULL COMMENT '交易归属方 机构名称',
    trans_type INT NOT NULL COMMENT '交易类型（1:deposit 2:withdraw 3:transfer 等，按业务约定）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易历史记录表';