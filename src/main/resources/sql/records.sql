create table t_{account_address}_records 
( 
    `id`            bigint(11) not null auto_increment, 
      `index`					varchar(32) not null comment '派生索引', 
    `value`       	int not null comment '承诺金额', 
    `sercet_key`    varchar(256) not null comment '私钥',
      `block_ number` int(11) not null comment '同步区块块高', 
    `status`   int not null comment '状态 1未花费 0已花费', 
    `create_time`   timestamp not null comment '创建的时间', 
    `update_time`   timestamp not null comment '修改的时间', 
) 
engine = InnoDB
default charset = utf8 
comment = '密文承诺信息表'; 