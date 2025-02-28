create table t_{account_address}_unspent_commitment 
( 
    `id`            varchar(256) not null comment '密文承诺', 
      `index`					varchar(32) not null comment '派生索引', 
    `value`       	int not null comment '承诺金额', 
      `block_number` int(11) not null comment '同步区块块高',  // event里面去解析 不清理链上receipt
    `status`   int not null comment '状态 1未花费 0已花费', 
    `create_time`   timestamp not null comment '创建的时间', 
    `update_time`   timestamp not null comment '修改的时间', 
) 
engine = InnoDB
default charset = utf8 
comment = '密文承诺信息表'; 