create table user
(
    id           bigint auto_increment comment '主键'
        primary key,
    userAccount  varchar(1024)                          null comment '账户',
    userName     varchar(1024)                          null comment '账户名称',
    userUrl      varchar(512)                           null comment '头像',
    gender       tinyint                                null comment '性别 0-女 1-男',
    userPassword varchar(512)                           null comment '密码',
    userStatus   tinyint      default 0                 null comment '状态 0-正常',
    createTime   timestamp    default CURRENT_TIMESTAMP null,
    updateTime   timestamp    default CURRENT_TIMESTAMP null,
    isDelete     int          default 0                 null comment '是否删除（逻辑删除） 0-正常 1-删除',
    userRole     int          default 0                 not null comment '用户权限 0-普通用户 1-管理员',
    vipState     varchar(512) default '0'               null comment '会员 0-普通用户 1-vip 2-svip',
    tags         varchar(1024)                          null comment '标签 json 列表',
    profile      varchar(512)                           null comment '个人简介',
    phone        varchar(128)                           null comment '电话',
    email        varchar(512)                           null comment '邮箱',
    lastTime     timestamp    default CURRENT_TIMESTAMP null comment '用户最后登录时间',
    friendsIds   varchar(512)                           null comment '添加的好友'
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
    userId      bigint                             null comment '用户id',
    status      int      default 0                 not null comment '0 - 公开，1 - 私有，2 - 加密',
    password    varchar(512)                       null comment '密码',
    createTime  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete    tinyint  default 0                 not null comment '是否删除',
    teamUrl     varchar(512)                       null comment '队伍头像'
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


create table chat
(
    id           bigint auto_increment comment '聊天记录id'
        primary key,
    sendUserId   bigint                                   not null comment '发送消息id',
    recUserId    bigint                                   null comment '接收消息id',
    content      varchar(1024) collate utf8mb4_unicode_ci null comment '聊天内容',
    chatType     tinyint                                  not null comment '聊天类型 1-私聊 2-群聊 3-大厅聊天 4-智能聊天机器人',
    isRead       tinyint  default 0                       null comment '是否已读 1-已读 0-未读',
    sendTime     datetime default CURRENT_TIMESTAMP       null comment '创建时间',
    deleteTime   datetime                                 null comment '删除时间',
    teamId       bigint                                   null comment '要发送信息的队伍id',
    isDelete     tinyint  default 0                       null comment '逻辑删除 0-正常 1-删除',
    sendUserName varchar(1024)                            null
)
    comment '聊天消息表' row_format = COMPACT;



create table friends
(
    id         bigint auto_increment comment '好友申请id'
        primary key,
    fromId     bigint                             not null comment '发送申请的用户id',
    receiveId  bigint                             null comment '接收申请的用户id ',
    isRead     tinyint  default 0                 not null comment '是否已读(0-未读 1-已读)',
    status     tinyint  default 0                 not null comment '申请状态 默认0 （0-未通过 1-已同意 2-已过期 3-已撤销）',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null,
    isDelete   tinyint  default 0                 not null comment '是否删除',
    remark     varchar(214)                       null comment '好友申请备注信息'
)
    comment '好友申请管理表' charset = utf8mb4;