create table tb_user
(
    id            bigint auto_increment comment '用户主键'
        primary key,
    username      varchar(255)      null comment '用户昵称',
    user_account  varchar(255)      null comment '账号',
    avatar_url    varchar(1024)     null comment '用户头像',
    gender        tinyint           null comment '性别',
    user_password varchar(512)      not null comment '密码',
    phone         varchar(128)      null comment '电话',
    email         varchar(512)      null comment '邮箱',
    profile       varchar(512)      null comment '个人简介',
    user_status   int     default 0 null comment '状态 0——正常',
    create_time   datetime          null on update CURRENT_TIMESTAMP comment '创建时间',
    update_time   datetime          null on update CURRENT_TIMESTAMP comment '修改时间',
    user_role     int     default 0 null comment '用户角色 0——普通用户，1——管理员',
    tags          varchar(1024)     null comment '标签列表',
    friends       varchar(1024)     null comment '好友列表',
    is_delete     tinyint default 0 null comment '是否删除'
);

create table tb_user_team
(
    id          bigint unsigned auto_increment comment '用户队伍关系主键'
        primary key,
    user_id     bigint unsigned   not null comment '用户id',
    team_id     bigint unsigned   null comment '队伍id',
    join_time   datetime          null comment '加入时间',
    create_time datetime          null on update CURRENT_TIMESTAMP comment '创建时间',
    update_time datetime          null on update CURRENT_TIMESTAMP comment '修改时间',
    is_delete   tinyint default 0 not null comment '是否删除'
);

create table tb_team
(
    id          bigint unsigned auto_increment comment '队伍主键'
        primary key,
    leader_id   bigint unsigned   not null comment '队长id',
    team_name   varchar(256)      not null comment '队伍名称',
    avatar_url  varchar(1024)     null comment '群头像，默认是队长的头像',
    description varchar(1024)     null comment '队伍描述',
    max_num     int     default 1 not null comment '最大人数',
    password    varchar(256)      null comment '队伍密码',
    status      int     default 0 not null comment '0-公开，1-私有，2-加密',
    expire_time datetime          null comment '过期时间',
    create_time datetime          null on update CURRENT_TIMESTAMP comment '创建时间',
    update_time datetime          null on update CURRENT_TIMESTAMP comment '修改时间',
    is_delete   tinyint default 0 not null comment '是否删除'
);

create table tb_tag
(
    id          bigint auto_increment comment '主键'
        primary key,
    tag_name    varchar(50)                  not null comment '标签名称',
    user_id     bigint                       null comment '用户id',
    parent_id   bigint unsigned              null comment '父标签id',
    is_parent   tinyint unsigned default '0' null comment '是否为父标签（0——不是，1——父标签）',
    create_time datetime                     null comment '创建时间',
    update_time datetime                     null comment '修改时间',
    is_delete   tinyint unsigned default '0' not null comment '是否删除（0——没删，1——删除）',
    constraint unique_tagName
        unique (tag_name)
);

create index idx_userId
    on tb_tag (user_id);

create table tb_message_user
(
    id              bigint unsigned auto_increment comment '消息主键'
        primary key,
    content         varchar(1024)     null comment '消息内容',
    status          int     default 0 not null comment '消息接收状态（0-未接收，1-接收）',
    send_time       datetime          null comment '发送时间',
    send_user_id    bigint unsigned   not null comment '发送用户id',
    receive_user_id bigint unsigned   not null comment '接收用户id',
    message_show    int     default 0 not null comment '展示对象(0-两位都展示，id-是谁的id就展示谁)',
    is_delete       tinyint default 0 not null comment '是否删除（0-未删除，1-删除）'
);

create table tb_message_team_user
(
    id              bigint            not null comment '群消息关联表'
        primary key,
    receive_user_id bigint unsigned   not null comment '接收用户id',
    message_team_id bigint unsigned   not null comment '群消息id',
    status          int     default 0 not null comment '接收状态(0-未接收，1-接收）',
    send_time       datetime          null comment '接收时间',
    is_delete       tinyint default 0 not null comment '是否删除（0-未删除，1-删除）'
);

create table tb_message_team
(
    id           bigint            not null comment '群聊消息主键'
        primary key,
    content      varchar(1024)     null comment '消息内容',
    send_time    datetime          null comment '发送时间',
    send_user_id bigint unsigned   not null comment '发送用户id',
    team_id      bigint unsigned   not null comment '接收群id',
    is_delete    tinyint default 0 not null comment '是否删除（0-未删除，1-删除）'
);

