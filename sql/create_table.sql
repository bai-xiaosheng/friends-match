create table user
(
    id           bigint auto_increment comment '主键'
        primary key,
    userAccount  varchar(1024)                       null comment '账户',
    userName     varchar(1024)                       null comment '账户名称',
    userUrl      varchar(512)                        null comment '头像',
    gender       tinyint                             null comment '性别 0-女 1-男',
    userPassword varchar(512)                        null comment '密码',
    userStatus   tinyint   default 0                 null comment '状态 0-正常',
    createTime   timestamp default CURRENT_TIMESTAMP null,
    updateTime   timestamp default CURRENT_TIMESTAMP null,
    isDelete     int       default 0                 null comment '是否删除（逻辑删除） 0-正常 1-删除',
    userRole     int       default 0                 not null comment '用户权限 0-普通用户 1-管理员',
    plantId      varchar(512)                        null comment '星球id-用户校验',
    tags         varchar(1024)                       null comment '标签 json 列表',
    profile      varchar(512)                        null comment '个人简介',
    phone        varchar(128)                        null comment '电话',
    email        varchar(512)                        null comment '邮箱'
)
    comment '用户';

create table team
(
    id          bigint auto_increment comment 'id'
        primary key,
    name        varchar(256)                       not null comment '队伍名称',
    description varchar(1024)                      null comment '描述',
    maxNum      int      default 1                 not null comment '最大人数',
    expireTime  datetime                           null comment '过期时间',
    userId      bigint comment '用户id',
    status      int      default 0                 not null comment '0 - 公开，1 - 私有，2 - 加密',
    password    varchar(512)                       null comment '密码',

    createTime  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete    tinyint  default 0                 not null comment '是否删除'
)
    comment '队伍';

create table user_team
(
    id         bigint auto_increment comment 'id'
        primary key,
    userId     bigint comment '用户id',
    teamId     bigint comment '队伍id',
    joinTime   datetime                           null comment '加入时间',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete   tinyint  default 0                 not null comment '是否删除'
)
    comment '用户队伍关系';