CREATE TABLE t_commitment
(
    `commitment`    VARCHAR(256) NOT NULL COMMENT '密文承诺',
    `kdf_index`           INT NOT NULL UNIQUE COMMENT '派生索引 自增', -- 修改字段名并添加自增
    `commitment_value`         INT NOT NULL COMMENT '承诺金额',
    `status`        INT NOT NULL COMMENT '状态 1未花费 0已花费',
    `create_time`   TIMESTAMP NOT NULL COMMENT '创建时间',
    `update_time`   TIMESTAMP NOT NULL COMMENT '修改时间', -- 删除末尾逗号
    PRIMARY KEY (`commitment`) -- 正确声明主键
)
ENGINE = InnoDB
DEFAULT CHARSET = utf8mb4 -- 建议使用 utf8mb4 以支持更全字符集
COMMENT '密文承诺信息表';