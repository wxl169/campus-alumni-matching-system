/*
 Navicat Premium Data Transfer

 Source Server         : wxl
 Source Server Type    : MySQL
 Source Server Version : 80034 (8.0.34)
 Source Host           : localhost:3306
 Source Schema         : campus_alumni_matching_system

 Target Server Type    : MySQL
 Target Server Version : 80034 (8.0.34)
 File Encoding         : 65001

 Date: 27/09/2023 20:58:43
*/



SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_message_team
-- ----------------------------
DROP TABLE IF EXISTS `tb_message_team`;
CREATE TABLE `tb_message_team`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '群聊消息主键',
  `content` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '消息内容',
  `send_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
  `send_user_id` bigint UNSIGNED NOT NULL COMMENT '发送用户id',
  `team_id` bigint UNSIGNED NOT NULL COMMENT '接收群id',
  `message_show` tinyint NOT NULL DEFAULT 0 COMMENT '消息是否清除（0-未清除，1-清除）',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除（0-未删除，1-删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 91 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_message_team_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_message_team_user`;
CREATE TABLE `tb_message_team_user`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '群消息关联表',
  `receive_user_id` bigint UNSIGNED NOT NULL COMMENT '接收用户id',
  `message_team_id` bigint UNSIGNED NOT NULL COMMENT '群消息id',
  `status` int NOT NULL DEFAULT 0 COMMENT '接收状态(0-未接收，1-接收）',
  `send_time` datetime NULL DEFAULT NULL COMMENT '接收时间',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除（0-未删除，1-删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 119 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_message_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_message_user`;
CREATE TABLE `tb_message_user`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '消息主键',
  `content` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '消息内容',
  `status` int NOT NULL DEFAULT 0 COMMENT '消息接收状态（0-未接收，1-接收）',
  `send_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `send_user_id` bigint UNSIGNED NOT NULL COMMENT '发送用户id',
  `receive_user_id` bigint UNSIGNED NOT NULL COMMENT '接收用户id',
  `message_show` int NOT NULL DEFAULT 0 COMMENT '展示对象(0-两位都展示，id-是谁的id就展示谁)',
  `is_system` int NOT NULL DEFAULT 0 COMMENT '是否为系统消息 （0-不是，1——是）',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除（0-未删除，1-删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 97 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_tag
-- ----------------------------
DROP TABLE IF EXISTS `tb_tag`;
CREATE TABLE `tb_tag`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tag_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '标签名称',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户id',
  `parent_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '父标签id',
  `is_parent` tinyint UNSIGNED NULL DEFAULT 0 COMMENT '是否为父标签（0——不是，1——父标签）',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `is_delete` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除（0——没删，1——删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_tagName`(`tag_name` ASC) USING BTREE,
  INDEX `idx_userId`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 45 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_team
-- ----------------------------
DROP TABLE IF EXISTS `tb_team`;
CREATE TABLE `tb_team`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '队伍主键',
  `leader_id` bigint UNSIGNED NOT NULL COMMENT '队长id',
  `team_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '队伍名称',
  `avatar_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '群头像，默认是队长的头像',
  `description` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '队伍描述',
  `max_num` int NOT NULL DEFAULT 1 COMMENT '最大人数',
  `password` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '队伍密码',
  `status` int NOT NULL DEFAULT 0 COMMENT '0-公开，1-私有，2-加密',
  `expire_time` datetime NULL DEFAULT NULL COMMENT '过期时间',
  `create_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 42 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户主键',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户昵称',
  `user_account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '账号',
  `avatar_url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '用户头像',
  `gender` tinyint NULL DEFAULT NULL COMMENT '性别',
  `user_password` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '密码',
  `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '电话',
  `email` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '邮箱',
  `profile` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '个人简介',
  `user_status` int NULL DEFAULT 0 COMMENT '状态 0——正常',
  `create_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `user_role` int NULL DEFAULT 0 COMMENT '用户角色 0——普通用户，1——管理员',
  `tags` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '标签列表',
  `friends` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '好友列表',
  `is_delete` tinyint NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 405022 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_user_team
-- ----------------------------
DROP TABLE IF EXISTS `tb_user_team`;
CREATE TABLE `tb_user_team`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户队伍关系主键',
  `user_id` bigint UNSIGNED NOT NULL COMMENT '用户id',
  `team_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '队伍id',
  `join_time` datetime NULL DEFAULT NULL COMMENT '加入时间',
  `create_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 109 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
