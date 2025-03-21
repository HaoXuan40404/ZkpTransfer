
CREATE TABLE t_account (
    `id`          VARCHAR(256) NOT NULL COMMENT '银行id',
    `address`     VARCHAR(256) NOT NULL COMMENT '银行公钥地址',
    `name`        VARCHAR(256) NOT NULL COMMENT '银行名称',
    `url`        VARCHAR(256) NOT NULL COMMENT   '连接银行URL',
    `status`      INT          NOT NULL COMMENT '状态 0删除 1正常',
    `create_time` TIMESTAMP    NOT NULL COMMENT '创建时间',
    `update_time` TIMESTAMP    NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    INDEX `idx_address` (`address`)  -- 添加普通索引
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='银行账户信息表';