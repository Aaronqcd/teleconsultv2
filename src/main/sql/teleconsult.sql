/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 50727
 Source Host           : localhost:3306
 Source Schema         : teleconsult

 Target Server Type    : MySQL
 Target Server Version : 50727
 File Encoding         : 65001

 Date: 08/03/2020 16:48:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for attachment
-- ----------------------------
DROP TABLE IF EXISTS `attachment`;
CREATE TABLE `attachment`  (
  `id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `source` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `fileName` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `sysName` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `createTime` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `path` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `share_user` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `is_share` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `dcmURL` varchar(4096) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '当文件是dicom文件时候，保存dcm4chee上的文件url路径'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of attachment
-- ----------------------------
INSERT INTO `attachment` VALUES ('45ef3b555b9c4ffaa55067090b08f565', 'applyMeeting', '00000001.dcm', '00000001.dcm', '2020-03-08 11:48:50', 'D:\\vsapp\\eclipsews\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\teleconsult\\webapp/images/headimg/', NULL, NULL, 'null');
INSERT INTO `attachment` VALUES ('84e887d43348417188cd26762850986f', 'applyMeeting', '00000001.dcm', '00000001.dcm', '2020-03-08 11:55:23', 'D:\\vsapp\\eclipsews\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\teleconsult\\webapp/images/headimg/', NULL, NULL, 'null');
INSERT INTO `attachment` VALUES ('0d3049305f3d4f85b484288238b796e6', 'applyMeeting', '00000001.dcm', '00000001.dcm', '2020-03-08 16:22:30', 'D:\\vsapp\\eclipsews\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\teleconsult\\webapp/images/headimg/', NULL, NULL, 'http://localhost:7070/oviyam2/dcmviewer.html?patientID=4&studyUID=234');
INSERT INTO `attachment` VALUES ('55deb532eb514573a13f8ae91ffa61fe', 'applyMeeting', '00000001', '00000001', '2020-03-08 16:23:57', 'D:\\vsapp\\eclipsews\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\teleconsult\\webapp/images/headimg/', NULL, NULL, '');
INSERT INTO `attachment` VALUES ('ce2bf30c0ba0405b97b8f147c1ff3803', 'applyMeeting', '00000001.dcm', '00000001.dcm', '2020-03-08 16:24:53', 'D:\\vsapp\\eclipsews\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\teleconsult\\webapp/images/headimg/', NULL, NULL, 'http://localhost:7070/oviyam2/dcmviewer.html?patientID=0000&studyUID=1333');
INSERT INTO `attachment` VALUES ('b36962acee6546ad875555c9eadbb591', 'applyMeeting', '00000001.dcm', '00000001.dcm', '2020-03-08 16:32:54', 'D:\\vsapp\\eclipsews\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\teleconsult\\webapp/images/headimg/', NULL, NULL, 'http://localhost:7070/oviyam2/dcmviewer.html?patientID=0000&studyID=1333');
INSERT INTO `attachment` VALUES ('70811beec702423e8d5d42ada1180e78', 'applyMeeting', '00000001.dcm', '00000001.dcm', '2020-03-08 16:35:52', 'D:\\vsapp\\eclipsews\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\teleconsult\\webapp/images/headimg/', NULL, NULL, 'http://localhost:7070/oviyam2/dcmviewer.html?patientID=0000&studyID=1333');

-- ----------------------------
-- Table structure for device_message
-- ----------------------------
DROP TABLE IF EXISTS `device_message`;
CREATE TABLE `device_message`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `deviceId` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '设备编码，全局唯一标识设备',
  `channel` int(11) NULL DEFAULT NULL COMMENT '视频卡端口',
  `userId` int(11) NULL DEFAULT NULL COMMENT '下令人ID',
  `meetingId` int(11) NULL DEFAULT NULL COMMENT '关联会诊ID',
  `sendCommand` int(11) NULL DEFAULT NULL COMMENT '命令',
  `sendBody` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '命令参数',
  `sendTime` datetime(0) NULL DEFAULT NULL COMMENT '下令时间',
  `isReply` bit(1) NULL DEFAULT NULL COMMENT '是否已响应',
  `replyCode` int(11) NULL DEFAULT NULL COMMENT '响应代码。0=成功 其余=错误代码',
  `replyBody` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '响应内容',
  `replyTime` datetime(0) NULL DEFAULT NULL COMMENT '相应时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dict
-- ----------------------------
DROP TABLE IF EXISTS `dict`;
CREATE TABLE `dict`  (
  `did` int(11) NOT NULL AUTO_INCREMENT,
  `key` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `desc` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`did`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dict
-- ----------------------------
INSERT INTO `dict` VALUES (1, 'bool', '真假', '真假选择');
INSERT INTO `dict` VALUES (2, 'sex', '性别', '用户性别');
INSERT INTO `dict` VALUES (3, 'meetingstatus', '会诊状态', '会诊状态');
INSERT INTO `dict` VALUES (4, 'meetingtype', '会诊类型', '会诊类型');

-- ----------------------------
-- Table structure for dictitem
-- ----------------------------
DROP TABLE IF EXISTS `dictitem`;
CREATE TABLE `dictitem`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pid` int(11) NOT NULL DEFAULT 0,
  `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `key` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `value` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `sort` int(4) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dictitem
-- ----------------------------
INSERT INTO `dictitem` VALUES (1, 0, 'bool', '0', '否', 1);
INSERT INTO `dictitem` VALUES (2, 0, 'bool', '1', '是', 0);
INSERT INTO `dictitem` VALUES (3, 0, 'sex', '0', '男', 0);
INSERT INTO `dictitem` VALUES (4, 0, 'sex', '1', '女', 0);
INSERT INTO `dictitem` VALUES (5, 0, 'meetingstatus', '4', '审核拒绝', 0);
INSERT INTO `dictitem` VALUES (6, 0, 'meetingstatus', '2', '已结束', 0);
INSERT INTO `dictitem` VALUES (7, 0, 'meetingstatus', '1', '待审核', 0);
INSERT INTO `dictitem` VALUES (8, 0, 'meetingstatus', '3', '正在会诊', 0);
INSERT INTO `dictitem` VALUES (9, 0, 'meetingstatus', '7', '待主持', 0);
INSERT INTO `dictitem` VALUES (10, 0, 'meetingstatus', '5', '待参加', 0);
INSERT INTO `dictitem` VALUES (11, 0, 'meetingstatus', '6', '已取消', 0);
INSERT INTO `dictitem` VALUES (12, 0, 'meetingtype', '1', '会诊', 0);
INSERT INTO `dictitem` VALUES (13, 0, 'meetingtype', '2', '直播', 0);

-- ----------------------------
-- Table structure for disk_file
-- ----------------------------
DROP TABLE IF EXISTS `disk_file`;
CREATE TABLE `disk_file`  (
  `file_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `file_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件名',
  `file_user_id` int(11) NOT NULL COMMENT '文件归属人人id',
  `file_size` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件大小',
  `file_parent_folder` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件上级目录',
  `file_creation_date` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件创建时间',
  `file_creator` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建人',
  `file_path` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件地址',
  `file_source_meeting` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`file_id`) USING BTREE,
  INDEX `file_index`(`file_id`, `file_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '云盘文件' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of disk_file
-- ----------------------------
INSERT INTO `disk_file` VALUES ('04a06e89-3c87-402c-b8d4-320c54be8625', '00000103.dcm', 2, '1049650', '4750e58f-e726-4009-8dde-97d1d50b3945', '2020年03月07日', '李医生', 'file_69327ead8c1a4ad5b8472a57f61b3306.block', NULL);
INSERT INTO `disk_file` VALUES ('0a779e57-eaa2-4159-a38b-742502bb4b99', '00000012', 2, '1049598', '4750e58f-e726-4009-8dde-97d1d50b3945', '2020年03月07日', '李医生', 'file_865d423e8c534141a07e52394f115c21.block', NULL);
INSERT INTO `disk_file` VALUES ('152ebe91-5f38-4a57-b9b8-535d6957b5d7', '00000001.dcm', 2, '132736', '4750e58f-e726-4009-8dde-97d1d50b3945', '2020年03月08日', '李医生', 'file_5794a9de95a149aba637770ef1881bcc.block', NULL);
INSERT INTO `disk_file` VALUES ('53fe96ac-6f12-498c-893c-e8939bf0494c', '00000001.dcm', 2, '132736', '4750e58f-e726-4009-8dde-97d1d50b3945', '2020年03月07日', '李医生', 'file_a1d651c387134f14bbb13a385deea5ce.block', NULL);
INSERT INTO `disk_file` VALUES ('706a97bb-879e-4de7-8bb8-ce56c12a3efb', '00000001', 2, '132574', '4750e58f-e726-4009-8dde-97d1d50b3945', '2020年03月08日', '李医生', 'file_ab0ec0d9cd3347cb943429f81530916b.block', NULL);
INSERT INTO `disk_file` VALUES ('9f9fe617-89d2-45b9-99e4-720f88426c32', '00000012', 3, '1049598', '8c602bc6-5960-4aca-9318-8f0474e8f529', '2020年03月07日', '任小非', 'file_101acfa99aae4cb597b62d097cc426b4.block', '3b0eab49990944b3a055a20c39f83cf4');
INSERT INTO `disk_file` VALUES ('a245aa78-c0e4-486f-9f14-f75040c37a9f', '00000001.dcm', 2, '132736', '4750e58f-e726-4009-8dde-97d1d50b3945', '2020年03月08日', '李医生', 'file_51e118c3a0894d5fbb0e0dc33c8ed5a1.block', NULL);
INSERT INTO `disk_file` VALUES ('a2fc3607-e4fd-41d8-a1bf-a9e4b31731ce', '00000001.dcm', 2, '132574', '4750e58f-e726-4009-8dde-97d1d50b3945', '2020年03月08日', '李医生', 'file_037f16043106441a864aa999d7f95144.block', NULL);
INSERT INTO `disk_file` VALUES ('b6427e72-12cd-47e5-ad55-af7665202542', '00000001.dcm', 2, '132736', '4750e58f-e726-4009-8dde-97d1d50b3945', '2020年03月08日', '李医生', 'file_77cdcc67d64e43b3b094a5d646ff27e8.block', NULL);
INSERT INTO `disk_file` VALUES ('c72a54de-4423-43e1-9443-adc7edbdb04b', '00000001.dcm', 2, '132736', '4750e58f-e726-4009-8dde-97d1d50b3945', '2020年03月07日', '李医生', 'file_5b4536f1f0954c8ca347b6496b43cc1b.block', NULL);
INSERT INTO `disk_file` VALUES ('e3d6b41d-e148-4b55-9c46-915c6a8dfb1e', '00000001.dcm', 2, '132574', '4750e58f-e726-4009-8dde-97d1d50b3945', '2020年03月08日', '李医生', 'file_a01aee887c894ebd889a56bedd217d6f.block', NULL);
INSERT INTO `disk_file` VALUES ('fcbff981-4d84-48a9-a5a8-3a54367d31c0', '00000001.dcm', 2, '132574', '4750e58f-e726-4009-8dde-97d1d50b3945', '2020年03月08日', '李医生', 'file_d34abfa529f34b719386374cae942183.block', NULL);

-- ----------------------------
-- Table structure for disk_folder
-- ----------------------------
DROP TABLE IF EXISTS `disk_folder`;
CREATE TABLE `disk_folder`  (
  `folder_id` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '目录id',
  `folder_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '目录名',
  `folder_user_id` int(11) NOT NULL COMMENT '目录用户id',
  `folder_creation_date` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '目录创建时间',
  `folder_creator` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '目录创建人',
  `folder_parent` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '上级目录',
  `folder_constraint` int(11) NULL DEFAULT NULL COMMENT '目录约束',
  `folder_size` bigint(20) NULL DEFAULT 0 COMMENT '文件夹容量大小(单位kb)',
  PRIMARY KEY (`folder_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '云盘目录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of disk_folder
-- ----------------------------
INSERT INTO `disk_folder` VALUES ('358404c4-9cbc-45ca-812e-bee5245a1046', 'M20181214153714', 3, '2020年03月07日', 'test02', '6945f057-9d30-4674-9f6e-2a1c7bc1f20a', 0, 0);
INSERT INTO `disk_folder` VALUES ('431b99b8-3827-436b-9f44-286fcd888c9a', 'root', 4, '2020年03月04日', 'wei', 'null', 0, 0);
INSERT INTO `disk_folder` VALUES ('4750e58f-e726-4009-8dde-97d1d50b3945', 'M20200303192928', 2, '2020年03月03日', 'test01', '6180879d-76d8-46c6-9998-8cbd5f8096e8', 0, 0);
INSERT INTO `disk_folder` VALUES ('4c408fea-6c77-407f-8494-15feecc5f25a', '测试', 7, '2019年06月25日', 'rao6', 'd122bca3-5bf0-46f6-8423-c968aa01d7b5', 0, 0);
INSERT INTO `disk_folder` VALUES ('4d1ff564-c798-4ea4-ac9a-496930e31ecb', 'M20200303192928', 3, '2020年03月07日', 'test02', '6945f057-9d30-4674-9f6e-2a1c7bc1f20a', 0, 0);
INSERT INTO `disk_folder` VALUES ('60375c03-a8f0-4ecc-8b3f-51cde794edb5', '文档', 7, '2019年06月25日', 'rao6', 'd122bca3-5bf0-46f6-8423-c968aa01d7b5', 0, 0);
INSERT INTO `disk_folder` VALUES ('6180879d-76d8-46c6-9998-8cbd5f8096e8', 'root', 2, '2020年03月03日', 'test01', 'null', 0, 0);
INSERT INTO `disk_folder` VALUES ('66389ac4-faf1-47d4-96da-fa29a5c1531f', 'M20181214154252', 3, '2019年06月25日', 'userid_100006615684_2', '6945f057-9d30-4674-9f6e-2a1c7bc1f20a', 0, 0);
INSERT INTO `disk_folder` VALUES ('6945f057-9d30-4674-9f6e-2a1c7bc1f20a', 'root', 3, '2019年06月25日', 'userid_100006615684_2', 'null', 0, 0);
INSERT INTO `disk_folder` VALUES ('7df5372c-d5d1-48ad-ac0b-4b2b03c31350', 'root', 7, '2019年06月25日', 'rao6', 'null', 0, 0);
INSERT INTO `disk_folder` VALUES ('7e062c93-448c-4caf-84d9-1bf34325b188', '资源', 7, '2019年06月25日', 'rao6', 'd122bca3-5bf0-46f6-8423-c968aa01d7b5', 0, 0);
INSERT INTO `disk_folder` VALUES ('85dfba69-f1e3-4f00-9dd4-3d20903cddb0', 'M20200308101159', 2, '2020年03月08日', 'test01', '6180879d-76d8-46c6-9998-8cbd5f8096e8', 0, 0);
INSERT INTO `disk_folder` VALUES ('8c602bc6-5960-4aca-9318-8f0474e8f529', '共享', 3, '2020年03月07日', 'test02', '4d1ff564-c798-4ea4-ac9a-496930e31ecb', 0, 0);
INSERT INTO `disk_folder` VALUES ('bd6a9846-b06f-400e-9fc3-a833bf79517f', 'M20200304160724', 4, '2020年03月04日', 'wei', '431b99b8-3827-436b-9f44-286fcd888c9a', 0, 0);
INSERT INTO `disk_folder` VALUES ('bdb24403-9088-4b99-8f34-6ffea0ddd409', 'M20200304160442', 4, '2020年03月04日', 'wei', '431b99b8-3827-436b-9f44-286fcd888c9a', 0, 0);
INSERT INTO `disk_folder` VALUES ('cceb25e5-0107-4eac-9694-b52de4638649', '资源', 3, '2019年06月25日', 'userid_100006615684_2', 'ec7644b7-076b-4f33-ae7a-4c7304ccb2b2', 0, 0);
INSERT INTO `disk_folder` VALUES ('d122bca3-5bf0-46f6-8423-c968aa01d7b5', 'M20181214154252', 7, '2019年06月25日', 'rao6', '7df5372c-d5d1-48ad-ac0b-4b2b03c31350', 0, 0);
INSERT INTO `disk_folder` VALUES ('e9a22f34-d37b-4f36-8c89-f9e6a619d2eb', '文档', 3, '2019年06月25日', 'userid_100006615684_2', 'cceb25e5-0107-4eac-9694-b52de4638649', 0, 0);
INSERT INTO `disk_folder` VALUES ('ec7644b7-076b-4f33-ae7a-4c7304ccb2b2', 'M20190620210412', 3, '2019年06月25日', 'userid_100006615684_2', '6945f057-9d30-4674-9f6e-2a1c7bc1f20a', 0, 0);
INSERT INTO `disk_folder` VALUES ('f6202733-5ce1-406b-b4e6-bd2fb0aeec45', 'M20181214153714', 2, '2020年03月08日', 'test01', '6180879d-76d8-46c6-9998-8cbd5f8096e8', 0, 0);

-- ----------------------------
-- Table structure for drug_presc_detail
-- ----------------------------
DROP TABLE IF EXISTS `drug_presc_detail`;
CREATE TABLE `drug_presc_detail`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `presc_date` datetime(0) NULL DEFAULT NULL,
  `presc_no` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `item_no` int(11) NULL DEFAULT NULL,
  `drug_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `drug_spec` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `drug_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `drug_grade` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `firm_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `package_spec` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `package_units` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `quantity` decimal(6, 2) NULL DEFAULT NULL,
  `price` decimal(10, 4) NULL DEFAULT NULL,
  `costs` decimal(10, 4) NULL DEFAULT NULL,
  `payments` decimal(10, 4) NULL DEFAULT NULL,
  `order_no` int(11) NULL DEFAULT NULL,
  `order_sub_no` int(11) NULL DEFAULT NULL,
  `administration` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `flag` int(11) NULL DEFAULT NULL,
  `dosage_each` decimal(10, 4) NULL DEFAULT NULL,
  `dosage_units` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `frequency` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `freq_detail` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `batch_no` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `inventory` decimal(12, 2) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for drug_presc_master
-- ----------------------------
DROP TABLE IF EXISTS `drug_presc_master`;
CREATE TABLE `drug_presc_master`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `patient_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `presc_date` datetime(0) NULL DEFAULT NULL,
  `presc_no` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `dispensary` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `presc_type` int(11) NULL DEFAULT NULL,
  `presc_attr` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `presc_source` int(11) NULL DEFAULT NULL,
  `repetition` int(11) NULL DEFAULT NULL,
  `costs` decimal(10, 4) NULL DEFAULT NULL,
  `payments` decimal(10, 4) NULL DEFAULT NULL,
  `ordered_by` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `prescribed_by` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `entered_by` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `dispensing_provider` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `count_per_repetition` int(11) NULL DEFAULT NULL,
  `entered_datetime` datetime(0) NULL DEFAULT NULL,
  `dispensing_datetime` datetime(0) NULL DEFAULT NULL,
  `memo` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `sub_storage` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `flag` int(11) NULL DEFAULT NULL,
  `doctor_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `verify_by` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `discharge_taking_indicator` int(11) NULL DEFAULT NULL,
  `visit_id` int(11) NULL DEFAULT NULL,
  `decoction` int(11) NULL DEFAULT NULL,
  `rcpt_no` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `original_presc_date` date NULL DEFAULT NULL,
  `original_presc_no` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `return_visit_no1` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `return_visit_date1` date NULL DEFAULT NULL,
  `clinic_no` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `batch_provide_no` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for exam_report
-- ----------------------------
DROP TABLE IF EXISTS `exam_report`;
CREATE TABLE `exam_report`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `exam_no` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `patient_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `exam_date` datetime(0) NULL DEFAULT NULL,
  `exam_room` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `exam_body` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `sound_no` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `exam_doctor` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `exam_para` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `description` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `impression` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `recommendation` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `is_abnormal` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `device` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `img_all_url` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `memo` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for group
-- ----------------------------
DROP TABLE IF EXISTS `group`;
CREATE TABLE `group`  (
  `gid` int(11) NOT NULL AUTO_INCREMENT,
  `key` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `groupname` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `config` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`gid`) USING BTREE,
  UNIQUE INDEX `key`(`key`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of group
-- ----------------------------
INSERT INTO `group` VALUES (1, 'admins', '超级管理员', '');
INSERT INTO `group` VALUES (2, 'managers', '机构管理员', '');
INSERT INTO `group` VALUES (3, 'users', '普通用户', '');

-- ----------------------------
-- Table structure for his
-- ----------------------------
DROP TABLE IF EXISTS `his`;
CREATE TABLE `his`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `servername` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `serverip` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `dbport` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `datesource` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `loginname` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `loginkey` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `orgid` int(11) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `rhis_orgid`(`orgid`) USING BTREE,
  CONSTRAINT `rhis_orgid` FOREIGN KEY (`orgid`) REFERENCES `organization` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for lab_result_detail
-- ----------------------------
DROP TABLE IF EXISTS `lab_result_detail`;
CREATE TABLE `lab_result_detail`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `test_no` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `item_no` int(11) NULL DEFAULT NULL,
  `print_order` int(11) NULL DEFAULT NULL,
  `report_item_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `report_item_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `result` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `units` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `abnormal_indicator` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `normal_value` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `min_max_value` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `instrument_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `result_date_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for lab_result_master
-- ----------------------------
DROP TABLE IF EXISTS `lab_result_master`;
CREATE TABLE `lab_result_master`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `test_no` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `patient_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `test_date` datetime(0) NULL DEFAULT NULL,
  `test_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for lis
-- ----------------------------
DROP TABLE IF EXISTS `lis`;
CREATE TABLE `lis`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `servername` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `serverip` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `dbport` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `datesource` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `loginname` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `loginkey` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `orgid` int(11) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `rhis_orgid`(`orgid`) USING BTREE,
  CONSTRAINT `lis_ibfk_1` FOREIGN KEY (`orgid`) REFERENCES `organization` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for live_video
-- ----------------------------
DROP TABLE IF EXISTS `live_video`;
CREATE TABLE `live_video`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NULL DEFAULT NULL COMMENT '主持人ID',
  `meetingId` int(11) NULL DEFAULT NULL COMMENT '会诊ID',
  `deviceId` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '设备ID',
  `channel` int(11) NULL DEFAULT NULL COMMENT '视频卡端口',
  `pushUrl` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '推流地址',
  `playUrl` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '播放地址',
  `status` int(11) NULL DEFAULT NULL COMMENT '视频状态 0=已关闭 1=正在打开 2=已打开 3=正在关闭',
  `openTime` datetime(0) NULL DEFAULT NULL COMMENT '视频打开时间',
  `closeTime` datetime(0) NULL DEFAULT NULL COMMENT '视频关闭时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for log
-- ----------------------------
DROP TABLE IF EXISTS `log`;
CREATE TABLE `log`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user` int(11) NOT NULL,
  `time` datetime(0) NOT NULL,
  `context` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of log
-- ----------------------------
INSERT INTO `log` VALUES (6, 3, '2018-12-14 16:22:46', '登录');
INSERT INTO `log` VALUES (7, 3, '2018-12-14 16:23:15', '退出');
INSERT INTO `log` VALUES (8, 1, '2018-12-14 16:23:20', '登录');
INSERT INTO `log` VALUES (9, 1, '2018-12-14 16:25:16', '退出');


-- ----------------------------
-- Table structure for meeting
-- ----------------------------
DROP TABLE IF EXISTS `meeting`;
CREATE TABLE `meeting`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `no` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `user` int(11) NOT NULL,
  `stime` datetime(0) NOT NULL COMMENT '会诊开始时间',
  `etime` datetime(0) NOT NULL COMMENT '会诊结束时间',
  `topic` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `type` int(5) NOT NULL,
  `attends` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `absentee` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '缺席人',
  `conclusion` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '会诊结论',
  `attendaccepts` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `status` int(4) NOT NULL DEFAULT 1,
  `attaId` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `is_del_metting` bigint(20) NULL DEFAULT 0 COMMENT '是否删除会诊标识',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `no`(`no`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of meeting
-- ----------------------------
INSERT INTO `meeting` VALUES (27, 'M20181214153714', 3, '2018-12-07 00:00:00', '2019-01-06 00:00:00', 'wef', 1, '1,2', '', NULL, '2,3', 3, NULL, 0);
INSERT INTO `meeting` VALUES (30, 'M20181214154252', 3, '2018-12-15 00:00:00', '2019-01-15 00:00:00', 'wefe', 1, '1,2', '', NULL, '', 2, NULL, 0);
INSERT INTO `meeting` VALUES (31, 'M20181217215236', 185, '2018-12-11 00:00:00', '2018-12-11 00:00:00', '会更好', 1, '103,29,28,27,26,25,24', '', NULL, '', 2, NULL, 0);
INSERT INTO `meeting` VALUES (32, 'M20181219142505', 186, '2018-12-11 00:00:00', '2018-12-12 00:00:00', '222', 1, '26,25,27,29,103,128,185', '', NULL, '', 1, NULL, 0);
INSERT INTO `meeting` VALUES (33, 'M20181219142608', 186, '2018-12-05 00:00:00', '2018-12-08 00:00:00', '333', 2, '24,19', '', NULL, '', 1, NULL, 0);
INSERT INTO `meeting` VALUES (34, 'M20200303192928', 2, '2020-03-01 00:00:00', '2020-04-02 00:00:00', '脑外科', 1, '3,4', '', NULL, '2,3', 3, '45ef3b555b9c4ffaa55067090b08f565,84e887d43348417188cd26762850986f,0d3049305f3d4f85b484288238b796e6,55deb532eb514573a13f8ae91ffa61fe,ce2bf30c0ba0405b97b8f147c1ff3803,b36962acee6546ad875555c9eadbb591,70811beec702423e8d5d42ada1180e78,', 0);
INSERT INTO `meeting` VALUES (35, 'M20200304160442', 4, '2020-04-09 00:00:00', '2020-04-17 00:00:00', '开会了', 1, '2,3', '', NULL, '4', 4, '', 0);
INSERT INTO `meeting` VALUES (36, 'M20200304160724', 4, '2020-04-03 00:00:00', '2020-04-10 00:00:00', 'chulai', 1, '3', '', NULL, '4', 3, '', 0);
INSERT INTO `meeting` VALUES (37, 'M20200308101159', 2, '2020-03-08 00:00:00', '2020-03-09 00:00:00', '测试', 1, '3,4', '', NULL, '2', 3, '', 0);

-- ----------------------------
-- Table structure for meeting_appends
-- ----------------------------
DROP TABLE IF EXISTS `meeting_appends`;
CREATE TABLE `meeting_appends`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `meeting` int(11) NOT NULL,
  `file` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for `meet_count`
-- ----------------------------
DROP TABLE IF EXISTS `meet_count`;
CREATE TABLE `meet_count` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `stear_time` datetime NOT NULL,
  `end_time` datetime DEFAULT NULL,
  `people` int(11) DEFAULT NULL,
  `duration` int(11) DEFAULT NULL,
  `duration_count` int(11) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `remark1` varchar(255) DEFAULT NULL,
  `remark2` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;



-- ----------------------------
-- Table structure for meeting_search_status
-- ----------------------------
DROP TABLE IF EXISTS `meeting_search_status`;
CREATE TABLE `meeting_search_status`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` int(4) NOT NULL DEFAULT 0,
  `key` int(11) NOT NULL,
  `value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of meeting_search_status
-- ----------------------------
INSERT INTO `meeting_search_status` VALUES (1, 0, 1, '待审核');
INSERT INTO `meeting_search_status` VALUES (2, 1, 2, '审核通过');
INSERT INTO `meeting_search_status` VALUES (3, 2, 2, '待主持');
INSERT INTO `meeting_search_status` VALUES (4, 3, 2, '待参加');
INSERT INTO `meeting_search_status` VALUES (5, 0, 3, '正在会诊');
INSERT INTO `meeting_search_status` VALUES (6, 0, 4, '已结束');
INSERT INTO `meeting_search_status` VALUES (7, 0, 5, '已取消');
INSERT INTO `meeting_search_status` VALUES (8, 0, 6, '审核拒绝');

-- ----------------------------
-- Table structure for meeting_status
-- ----------------------------
DROP TABLE IF EXISTS `meeting_status`;
CREATE TABLE `meeting_status`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `adminname` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `applyname` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `attendname` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `adminbutton` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `applybutton` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `attendbutton` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of meeting_status
-- ----------------------------
INSERT INTO `meeting_status` VALUES (1, '待审核', '待审核', '待审核', '<a class=\"layui-btn layui-btn-xs layui-btn-radius tc-bg-main\" lay-event=\"check\">同意</a>\n    <a class=\"layui-btn layui-btn-xs layui-btn-warm layui-btn-radius\" lay-event=\"reject\">拒绝</a>', '<a class=\"layui-btn layui-btn-xs layui-btn-warm layui-btn-radius\" lay-event=\"cancle\">取消</a>', '');
INSERT INTO `meeting_status` VALUES (2, '审核通过', '待主持', '待参加', '', '<a class=\"layui-btn layui-btn-normal layui-btn-xs layui-btn-radius\" lay-event=\"start\">发起</a>\n    <a class=\"layui-btn layui-btn-xs layui-btn-warm layui-btn-radius\" lay-event=\"cancle\">取消</a>', '<a class=\"layui-btn layui-btn-xs layui-btn-radius tc-bg-main\" lay-event=\"check\">加入</a>');
INSERT INTO `meeting_status` VALUES (3, '正在会诊', '正在会诊', '正在会诊', '<a class=\"layui-btn layui-btn-xs layui-btn-danger layui-btn-radius\" lay-event=\"stop\">中断</a>', '<a class=\"layui-btn layui-btn-normal layui-btn-xs layui-btn-radius\" lay-event=\"entry\">进入</a>', '<a class=\"layui-btn layui-btn-normal layui-btn-xs layui-btn-radius\" lay-event=\"entry\">进入</a>');
INSERT INTO `meeting_status` VALUES (4, '已结束', '已结束', '已结束', '', '', '');
INSERT INTO `meeting_status` VALUES (5, '已取消', '已取消', '已取消', '', '', '');
INSERT INTO `meeting_status` VALUES (6, '审核拒绝', '审核拒绝', '审核拒绝', '', '<a class=\"layui-btn layui-btn-xs layui-btn-warm layui-btn-radius\" lay-event=\"recheck\">重新申请</a>', '');

-- ----------------------------
-- Table structure for operateroomtype
-- ----------------------------
DROP TABLE IF EXISTS `operateroomtype`;
CREATE TABLE `operateroomtype`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `roomtype` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `engname` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of operateroomtype
-- ----------------------------
INSERT INTO `operateroomtype` VALUES (1, '示教手术室', 'r1');
INSERT INTO `operateroomtype` VALUES (2, '数字化手术室', 'r2');
INSERT INTO `operateroomtype` VALUES (3, 'ICU', 'r3');

-- ----------------------------
-- Table structure for organization
-- ----------------------------
DROP TABLE IF EXISTS `organization`;
CREATE TABLE `organization`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pid` int(11) NOT NULL DEFAULT 0,
  `type` int(4) NOT NULL DEFAULT 0,
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '机构' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of organization
-- ----------------------------
INSERT INTO `organization` VALUES (1, 0, 0, '福田医联体');
INSERT INTO `organization` VALUES (2, 1, 1, '南山医院');
INSERT INTO `organization` VALUES (3, 1, 1, '西丽医院');
INSERT INTO `organization` VALUES (5, 2, 2, '田夏社康');
INSERT INTO `organization` VALUES (6, 2, 2, '阳光社康');
INSERT INTO `organization` VALUES (7, 3, 2, '留仙社康');
INSERT INTO `organization` VALUES (9, 3, 2, 'sdfe');

-- ----------------------------
-- Table structure for pacs
-- ----------------------------
DROP TABLE IF EXISTS `pacs`;
CREATE TABLE `pacs`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `server` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `ipaddress` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `port` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `zaet` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `baet` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `orgid` int(11) NOT NULL,
  `dbip` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `dbport` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `dbname` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `dbuser` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `dbpwd` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `rhis_orgid`(`orgid`) USING BTREE,
  CONSTRAINT `pacs_ibfk_1` FOREIGN KEY (`orgid`) REFERENCES `organization` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for patient
-- ----------------------------
DROP TABLE IF EXISTS `patient`;
CREATE TABLE `patient`  (
  `patient_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `inp_no` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `name_phonetic` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `sex` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `date_of_birth` datetime(0) NULL DEFAULT NULL,
  `birth_place` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `citizenship` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `nation` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `id_no` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `identity` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `height` int(11) NULL DEFAULT NULL,
  `weight` int(11) NULL DEFAULT NULL,
  `charge_type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `deposit` decimal(10, 4) NULL DEFAULT NULL,
  `unit_in_contract` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `mailing_address` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `zip_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `phone_number_home` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `phone_number_business` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `next_of_kin` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `relationship` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `next_of_kin_addr` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `next_of_kin_zip_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `next_of_kin_phone` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `last_visit_date` datetime(0) NULL DEFAULT NULL,
  `vip_indicator` int(11) NULL DEFAULT NULL,
  `create_date` datetime(0) NULL DEFAULT NULL,
  `operator` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `service_agency` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `business_zip_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `photo` blob NULL,
  `patient_class` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `degree` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `race` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `religion` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `mother_language` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `foreign_language` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `id_type` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `vip_no` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `e_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`patient_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for resource
-- ----------------------------
DROP TABLE IF EXISTS `resource`;
CREATE TABLE `resource`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `resourcetype` varchar(15) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `typeeng` varchar(15) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of resource
-- ----------------------------
INSERT INTO `resource` VALUES (1, 'HIS', 'HIS');
INSERT INTO `resource` VALUES (2, 'LIS', 'LIS');
INSERT INTO `resource` VALUES (3, 'PACS', 'PACS');
INSERT INTO `resource` VALUES (4, 'RIS', 'RIS');
INSERT INTO `resource` VALUES (5, '视频源', 'VIDEO');

-- ----------------------------
-- Table structure for ris
-- ----------------------------
DROP TABLE IF EXISTS `ris`;
CREATE TABLE `ris`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `server` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `ipaddress` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `port` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `zaet` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `baet` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `orgid` int(11) NOT NULL,
  `dbip` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `dbport` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `dbname` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `dbuser` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `dbpwd` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `rhis_orgid`(`orgid`) USING BTREE,
  CONSTRAINT `ris_ibfk_1` FOREIGN KEY (`orgid`) REFERENCES `organization` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_conf
-- ----------------------------
DROP TABLE IF EXISTS `sys_conf`;
CREATE TABLE `sys_conf`  (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `conf_key` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '系统配置的key',
  `conf_value` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '系统配置的value',
  `remark` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_conf
-- ----------------------------
INSERT INTO `sys_conf` VALUES (1, 'DISK_SIZE', '2147483648', '个人云盘最大容量,默认2GB(单位字节)');
INSERT INTO `sys_conf` VALUES (2, 'MAX_UPLOAD_SIZE', '31457280', '最大文件上传大小,默认500M(单位字节)');
INSERT INTO `sys_conf` VALUES (3, 'CONF_TEUDBORAD', '0', '配置白板开放功能：0为未开通，1为已开通');
INSERT INTO `sys_conf` VALUES (4, 'USER_SIZE', '30', '添加和批量导入用户的最大限制');
INSERT INTO `sys_conf` VALUES (5, 'EMAIL_HOSPITAL_NAME', '中山大医院', '邮件模板要替换的医院名称');
INSERT INTO `sys_conf` VALUES (6, 'EMAIL_HOSPITAL_URL', 'http://localhost/teleconsult/Login', '邮件模板要替换的医院访问地址');
INSERT INTO `sys_conf` VALUES (7, 'EMAIL_ADDRESS', '727470665@qq.com', '系统邮箱地址');
INSERT INTO `sys_conf` VALUES (8, 'EMAIL_AUTHORIZATION_CODE', 'vmhawxjllcptbdhi', '系统邮箱授权码');
INSERT INTO `sys_conf` VALUES (9, 'MEETING_CYCLE', '180', '会诊数据存储周期，单位(天)');
INSERT INTO `sys_conf` VALUES (10, 'SYS_LOGO', '20160706172512559.jpg', '医院LOGO上传（尺寸319x50）');
INSERT INTO `sys_conf` (`id`, `conf_key`, `conf_value`, `remark`) VALUES ('100', 'SMS_APPID', '1400334674', '短信appId');
INSERT INTO `sys_conf` (`id`, `conf_key`, `conf_value`, `remark`) VALUES ('101', 'SMS_APPKEY', '313d29c03fc5d1169d6ee75326927457', '短信appkey');
INSERT INTO `sys_conf` (`id`, `conf_key`, `conf_value`, `remark`) VALUES ('102', 'SMS_SIGN', '威视爱普', '短信签名');
INSERT INTO `sys_conf` (`id`, `conf_key`, `conf_value`, `remark`) VALUES ('103', 'SMS_TEMP_1', '557838', '短信内容：已删除编号为{1}会诊');
INSERT INTO `sys_conf` (`id`, `conf_key`, `conf_value`, `remark`) VALUES ('104', 'SMS_TEMP_2', '558016', '短信内容：会诊管理系统，重置密码成功！新密码为：{1}');
INSERT INTO `sys_conf` (`id`, `conf_key`, `conf_value`, `remark`) VALUES ('105', 'SMS_TEMP_3', '558017', '短信内容：您的验证码为：{1}');
INSERT INTO `sys_conf` (`id`, `conf_key`, `conf_value`, `remark`) VALUES ('106', 'SMS_TEMP_4', '558018', '短信内容：恭喜您成为了会诊系统用户！你的登录账户是:{1}，初始密码是:{2}，为了您的账户安全，敬请修改您的初始密码');
INSERT INTO `sys_conf` (`id`, `conf_key`, `conf_value`, `remark`) VALUES ('107', 'SMS_TEMP_5', '558019', '短信内容：亲爱的用户您好，[{1}]主持的会诊[{2}]将在[{3}]至[{4}]进行，请各位参会人员务必准时到场，参加人[{5}]谢谢配合！');
INSERT INTO `sys_conf` (`id`, `conf_key`, `conf_value`, `remark`) VALUES ('108', 'SMS_TEMP_6', '558021', '短信内容：会诊主题:{1}，会诊时间：{2}，与会人：{3}，缺席人：{4}，会诊结论：{5}');
INSERT INTO `sys_conf` (`id`, `conf_key`, `conf_value`, `remark`) VALUES ('109', 'SMS_TEMP_7', '558022', '短信内容：亲爱的用户[{1}]您好，[{2}]主持的会诊[{3}]诚挚邀请您参加，谢谢！');
INSERT INTO `sys_conf` (`id`, `conf_key`, `conf_value`, `remark`) VALUES ('110', 'SMS_TEMP_8', '558023', '短信内容：亲爱的用户[{1}]您好，[{2}]主持的会诊被管理员中断！');
INSERT INTO `sys_conf` (`id`, `conf_key`, `conf_value`, `remark`) VALUES ('111', 'SMS_TEMP_9', '558025', '短信内容：亲爱的用户[{1}]您好，您已经登录远程会诊系统，如非本人操作，请及时联系后台管理员！');
INSERT INTO `sys_conf` (`id`, `conf_key`, `conf_value`, `remark`) VALUES ('112', 'SMS_TEMP_10', '558027', '短信内容：亲爱的用户[{1}]您好，您远程会诊系统的密码被修改，如非本人操作，请及时联系后台管理员！');
INSERT INTO `sys_conf` (`id`, `conf_key`, `conf_value`, `remark`) VALUES ('113', 'SMS_TEMP_11', '558028', '短信内容：亲爱的用户[{1}]您好，您申请的远程会诊{2}被拒绝，请联系后台管理员！');
INSERT INTO `sys_conf` (`id`, `conf_key`, `conf_value`, `remark`) VALUES ('114', 'SMS_TEMP_12', '559209', '短信内容：[{1}]申请一个会议，请及时审核，谢谢。');
INSERT INTO `sys_conf` (`id`, `conf_key`, `conf_value`, `remark`) VALUES ('115', 'RECORD_CALLBACK_URL', 'https://ychz.szseyy.com/tencent-video-api/record/callback', '视频录制回调地址');

-- ----------------------------
-- Table structure for tbl_room
-- ----------------------------
DROP TABLE IF EXISTS `tbl_room`;
CREATE TABLE `tbl_room`  (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '音视频房间号',
  `roomName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '房间名称',
  `meetingID` bigint(11) NULL DEFAULT NULL COMMENT '会诊ID',
  `createTime` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `endTime` timestamp(0) NULL DEFAULT NULL COMMENT '结束时间',
  `actionTime` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `operator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作人',
  `groupID` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT 'IM群组id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for template
-- ----------------------------
DROP TABLE IF EXISTS `template`;
CREATE TABLE `template`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `template_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `template_title` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `template_context` varchar(4000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `template_code`(`template_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of template
-- ----------------------------
INSERT INTO `template` VALUES (1, 'mail_user_warn', '注册提醒', '恭喜您成为了${hospitalName}远程会诊系统用户！你的登录账户是:${user},初始密码是:${password},为了您的账户安全，敬请修改您的初始密码,登录地址为:${sysUrl}');
INSERT INTO `template` VALUES (2, 'mail_meeting_notify', '【会诊通知】${topic}', '亲爱的用户您们好，【${proposer}】主持的会诊【${topic}】将在${stime}至${etime}进行，请各位参会人员务必准时到场，参加人【${partInPerson}】谢谢配合！');
INSERT INTO `template` VALUES (3, 'mail_share_meetingsInfo', '【会诊纪要】${meetingTitle}', '会诊主题:${meetingTitle} \n会诊时间：${meetingTime} \n与会人：${meetingAttends} \n缺席人：${meetingAbsentee} \n会诊结论：${meetingConclusion}');
INSERT INTO `template` VALUES (4, 'mail_meeting_invite_expert', '【会诊通知】', '亲爱的用户【${userName}】您好，【${proposer}】主持的会诊【${topic}】诚挚邀请您参加，谢谢！');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `group` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `email` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `belong` int(11) NOT NULL,
  `avatar` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `sex` int(2) NOT NULL DEFAULT 0,
  `job` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `special` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `phone` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `videoRole` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'user169' COMMENT '会诊视频角色名称，与腾讯后台对应',
  `last_login_time` datetime(0) DEFAULT NULL,
  `logintype` int(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user`(`user`) USING BTREE,
  UNIQUE INDEX `email`(`email`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', 'admins', '1@qq.com', 1, '', 'admin', 0, 'doctor', '111', '22222', 'user169', '2020-05-07 12:34:06', 0);
INSERT INTO `user` VALUES (2, 'root', 'e10adc3949ba59abbe56e057f20f883e', 'admins', '0@qq.com', 1, '', 'root', 0, 'doctor', '11', '22', 'user169', '2020-05-07 12:09:07', 0);
INSERT INTO `user` VALUES (3, 'test02', 'e10adc3949ba59abbe56e057f20f883e', 'users', '727470665@qq.com', 2, '', '任小非', 1, '主治医师', '心脑外科', '15013825509', 'user169', '2020-05-07 12:30:32', 0);
INSERT INTO `user` VALUES (4, 'wei', '46c157f9b773b3f27226e3d5a1b6d2e4', 'users', '1191250110@qq.com', 2, '', '韦医生', 0, '韦医生', '韦医生', '15778711929', 'user169', '2020-03-04 16:06:27', 0);
INSERT INTO `user` VALUES (6, 'w1234', '1843d3f01fefa313f6c3b67b709396fc', 'users', '862815462@qq.com', 1, '', 'xc1', 1, '1', '1', '17920bf8051cc6c8703d7e53c090e1da', 'user169', NULL, 1);

-- ----------------------------
-- Table structure for video
-- ----------------------------
DROP TABLE IF EXISTS `video`;
CREATE TABLE `video`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `roomname` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `roomip` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `roomtype` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `v1name` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `v1add` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `v2name` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `v2add` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `v3name` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `v3add` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `v4name` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `v4add` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `v5name` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `v5add` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `v6name` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `v6add` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `v7name` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `v7add` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `v8name` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `v8add` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `orgid` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `rhis_orgid`(`orgid`) USING BTREE,
  CONSTRAINT `video_ibfk_1` FOREIGN KEY (`orgid`) REFERENCES `organization` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;


DROP TABLE IF EXISTS `api_config`;
CREATE TABLE `api_config` (
  `id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `hosp_url` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;



SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- 录制请求记录表
-- ----------------------------
DROP TABLE IF EXISTS `record_request`;
CREATE TABLE `record_request`  (
  `id` INT NOT NULL AUTO_INCREMENT,
  `command` VARCHAR(100) NULL COMMENT '请求命令',
  `request` TEXT NULL COMMENT '请求参数',
  `response` TEXT NULL COMMENT '响应结果',
  `taskId` VARCHAR(200) NULL COMMENT '录制任务ID',
  `requestId` VARCHAR(200) NULL COMMENT '录制请求ID',
  `inDate` DATETIME NULL COMMENT '请求时间',
  PRIMARY KEY (`id`)
);

-- ----------------------------
-- 录制任务表
-- ----------------------------
DROP TABLE IF EXISTS `record_task`;
CREATE TABLE `record_task`  (
  `id` INT NOT NULL AUTO_INCREMENT,
  `meetingId` INT NULL COMMENT '所属会诊ID',
  `taskId` VARCHAR(200) NULL COMMENT '录制任务ID',
  `requestId` VARCHAR(200) NULL COMMENT '开始录制请求ID',
  `status` VARCHAR(50) NULL COMMENT '录制任务状态',
  `finishReason` VARCHAR(50) NULL COMMENT '录制结束原因',
  `roomId` INT COMMENT '房间号', 
  `groupId` VARCHAR(200) COMMENT '白板的群组 Id', 
  `userId` VARCHAR(200) NULL COMMENT '录制用户Id',
  `startTime` DATETIME NULL COMMENT '实际开始录制时间，Unix 时间戳，单位秒',
  `stopTime` DATETIME NULL COMMENT '实际停止录制时间，Unix 时间戳，单位秒',
  `totalTime` INT NULL COMMENT '回放视频总时长（单位：毫秒）' ,
  `inDate` DATETIME NULL COMMENT '请求时间',  
  PRIMARY KEY (`id`)
);

-- ----------------------------
-- 录制视频表
-- ----------------------------
DROP TABLE IF EXISTS `record_video`;
CREATE TABLE `record_video`  (
  `id` INT NOT NULL AUTO_INCREMENT,
  `meetingId` INT NULL COMMENT '所属会诊ID',
  `name` VARCHAR(50) NULL COMMENT '视频名称',
  `taskId` VARCHAR(200) NULL COMMENT '录制任务ID',
  `inDate` DATETIME NULL COMMENT '请求时间',  
  `videoPlayTime` INT NULL COMMENT '视频开始播放的时间（单位：毫秒',
  `videoSize` INT NULL COMMENT '视频大小（字节）',
  `videoFormat`	VARCHAR(50) NULL COMMENT '视频格式',
  `videoDuration` INT NULL COMMENT '视频播放时长（单位：毫秒）',
  `videoUrl` VARCHAR(500) NULL COMMENT '视频文件URL',
  `videoId` VARCHAR(200) NULL COMMENT '视频文件Id',
  `videoType` INT NULL COMMENT '视频流类型 0：摄像头视频 1：屏幕分享视频（仅课后录制支持）2：白板视频 3：混流视频 4：纯音频（mp3)',
  `userId` VARCHAR(200) NULL COMMENT '摄像头/屏幕分享视频所属用户的 Id（白板视频为空、混流视频tic_mixstream_房间号_混流布局类型）',
  `attends` VARCHAR(100) NULL,
  PRIMARY KEY (`id`)
);

-- ----------------------------
-- 4S 测试数据
-- ----------------------------
-- 病人
insert into patient (patient_id, inp_no, `name`, name_phonetic, sex, date_of_birth, birth_place, citizenship,
	nation, id_no, identity, height, weight, charge_type, deposit, unit_in_contract, mailing_address, zip_code, 
	phone_number_home, phone_number_business, next_of_kin, relationship, next_of_kin_addr, 
	next_of_kin_zip_code, next_of_kin_phone, last_visit_date, vip_indicator, create_date,
	operator, service_agency, business_zip_code, photo, patient_class, degree, race, religion,
	mother_language, foreign_language, id_type, vip_no, e_name) 
values
	('P100080210', 'H903820183', '刘强', 'LIU QIANG', '男', '1992-5-8', '河北省石家庄', '中国',
	'汉族', '103948858472847', 'I1039842', 180, 135, '自费', 308.5, 'C998392183', '河北省石家庄南山路103号', '201932',
	'133803842', null, '刘奎', '父子', '河北省石家庄南山路103号',
	'201932', '1389383905', null, 1, '2018-10-8', 
	'张丽', null, '201933', null, '门诊', '本科', '汉族', '无',
	'汉语', null, '身份证', 'M109482934', null),
	('P100039324', 'H73290944', '罗刚', 'LUO GANG', '男', '1995-8-13', '湖北省武汉市', '中国',
	'汉族', '40293948882492', 'I9309234', 175, 125, '公费', 588.2, 'C420852934', '湖北省武汉市名湖山庄19号', '593036',
	'130302934', null, '郭巧儿', '夫妻', '湖北省武汉市名湖山庄19号',
	'593036', '134023848', null, 1, '2019-3-21', 
	'张丽', null, '593037', null, '急诊', '大专', '汉族', '无',
	'汉语', null, '身份证', 'M18448234', null),
	('P10323573', 'H73290944', '韩小云', 'HAN XIAO YUN', '女', '1994-10-2', '河南省郑州市', '中国',
	'回族', '309239248845', 'I058429324', 165, 95, '自费', 101.4, 'C04832944', '河南省郑州市晟利嘉园1824号', '301032',
	'140293446', null, '韩志远', '父女', '河南省郑州市晟利嘉园1824号',
	'301032', '158992933', null, 1, '2019-2-18', 
	'张丽', null, '301033', null, '转入', '大专', '汉族', '无',
	'汉语', null, '身份证', 'M0439234', null),
	('P10007753', 'H64592930', '肖立红', 'XIAO LI HONG', '男', '1993-7-17', '北京市', '中国',
	'汉族', '503943912843', 'I84832939', 185, 142, '公费', 218.5, 'C84932050', '北京市海淀紫竹院10号', '100012',
	'130302934', null, '肖立刚', '兄弟', '北京市海淀紫竹院10号',
	'100012', '138033284', null, 1, '2018-5-21', 
	'张丽', null, '100012', null, '急诊', '本科', '汉族', '无',
	'汉语', null, '身份证', 'M0238954', null);

-- HIS 主记录
insert into drug_presc_master(id, presc_date, presc_no, dispensary, patient_id,
	presc_type,presc_attr,presc_source,repetition,costs,payments,ordered_by,prescribed_by,entered_by,
	dispensing_provider, count_per_repetition, entered_datetime, dispensing_datetime, memo, sub_storage, flag,
	doctor_user, verify_by, discharge_taking_indicator,visit_id,decoction, rcpt_no, original_presc_date,original_presc_no,
	return_visit_date1, return_visit_no1, clinic_no, batch_Provide_no)
values 
	(1, '2019-5-21 10:32:24', '10001', '北京医药局', 'P10323573', 
	 0, 'GZY', 0, 3, 89.5, 78.8, '皮肤科', '乔志明', '乔立人',
	'乔立人', 1, '2019-5-21 10:32:24','2019-5-21 11:42:24', '无', 'S01', 1,
	'乔志明', '乔立人', 1, 3, 1, 'R081833K2', '2019-5-21', '10001',
	NULL, NULL, NULL, NULL),
	(2, '2019-5-21 11:18:11', '10003', '河北医药局', 'P100080210',
	1, 'MZY', 1, 1, 39.9, 39.9, '眼科', '苗红叶', '乔立人',
	'乔立人', 1, '2019-5-21 11:18:11','2019-5-21 12:14:39', '无', 'S01', 1,
	'苗红叶', '乔立人', 1, 3, 1, 'R081833K2', '2019-5-21', '10001',
	NULL, NULL, NULL, NULL),
	(3, '2019-5-23 9:42:24', '10011', '山西医药局', 'P100039324',
	0, 'GZY', 0, 3, 89.5, 78.8, '耳鼻科', '张晓军', '乔立人',
	'乔立人', 1, '2019-5-23 10:32:24','2019-5-23 11:42:24', '无', 'S01', 1,
	'张晓军', '乔立人', 1, 3, 1, 'R081833K2', '2019-5-21', '10001',
	NULL, NULL, NULL, NULL);

-- HIS 子记录
insert into drug_presc_detail(id, presc_date, presc_no, item_no, drug_code, drug_spec, drug_name, drug_grade, firm_id,
	package_spec, package_units, quantity, price, costs, payments, order_no, order_sub_no, administration, flag,
	dosage_each,dosage_units, frequency, freq_detail, batch_no, inventory)
values
	(1, '2019-5-21 10:32:24', '10001', 1, 'D0101', 'X103', '舒肤止痒酊', '1级', 'F010393',
	'0.25*30', '包', 14, 2.4, 38.9, 36.8, 1001, 2, '外敷, 忌水', 1, 
	5.2, '克', '每日3次', NULL, NULL, 100),
	(2, '2019-5-21 10:32:24', '10001', 2, 'D0102', 'X103', '黑加白', '2级', 'F010393',
	'0.25*30', '包', 1, 42.5, 42.5, 37.6, 1002, 2, '一日三次，每次3片', 1, 
	3.8, '克', '每日3次', NULL, NULL, 100),
	(3, '2019-5-21 10:32:24', '10001', 3, 'D0103', 'X103', '复方门冬维甘滴眼剂', 'I', 'F010393',
	'0.25*30', '包', 2, 18.9, 37.8, 30.2, 1003, 2, '每次1-2滴，每日3次', 1, 
	3, '滴', '每日3次', NULL, NULL, 100);

-- LIS
insert into lab_result_master(id, test_no, patient_id, test_date, test_name)
values
	(1, 'T102039424', 'P100080210', '2019-5-21 12:12:42', '腹腔'),
	(2, 'T938038494', 'P10323573', '2019-5-28 09:42:22', '肺部'),
	(3, 'T430193437', 'P100039324', '2019-5-29 16:09:18', '肾脏');

insert into lab_result_detail(id,test_no,item_no,print_order,
	report_item_name,report_item_code,result,units,abnormal_indicator,
	normal_value,min_max_value,instrument_id,result_date_time)
values 
	(1, 'T102039424', 1, 1,
	'*ALY#', 'ALY#', '0.00', NULL, 'N',
	'0.0 - 0.3', '-0.9 - 1.2', 'T0001', '2019-5-21 12:14:21'),
	(2, 'T102039424', 2, 2,
	'血红蛋白', 'XHDB', '127.00', 'e/l', 'N',
	'102 - 135', '91 - 162', 'T0002', '2019-5-21 12:14:21'),
	(3, 'T102039424', 3, 3,
	'肺炎支原体', 'FYZYT', '阴性', NULL, 'N',
	NULL, NULL, 'T0003', '2019-5-21 12:14:21'), 
	(4, 'T102039424', 4, 4,
	'*ALY%', 'ALY%', '0.00', NULL, 'N',
	'0.0 - 0.3', '-0.9 - 1.2', 'T0001', '2019-5-21 12:14:21'), 
	(5, 'T102039424', 5, 5,
	'单核细胞', 'DHXB', '0.86', NULL, 'H',
	'0.3 - 1.56', '0.1 - 1.94', 'T0001', '2019-5-21 12:14:21'),
	(6, 'T102039424', 6, 6,
	'中性细胞数', 'ZXXS', '7.15', '10~9/L', 'N',
	'5.2 - 8.6', '3.3 - 11.4', 'T0001', '2019-5-21 12:14:21'),
	(7, 'T102039424', 7, 7,
	'血小板分布宽度', 'XXBFBKD', '16.70', 'fL', 'N',
	'12.4 - 19.5', '10.1 - 22.5', 'T0001', '2019-5-21 12:14:21'),
	(8, 'T102039424', 8, 8,
	'平均血小板分布宽度', 'PJXXB', '10.20', 'fL', 'N',
	'7.6 - 12.9', '5.4 - 14.5', 'T0001', '2019-5-21 12:14:21'),
	(9, 'T102039424', 9, 9,
	'*ALC%', 'ALC', '1.2', NULL, 'N',
	NULL, NULL, 'T0001', '2019-5-21 12:14:21'),
	(10, 'T102039424', 10, 10,
	'平均血红蛋白量', 'PJXHDB', '26.10', 'pg', 'N',
	NULL, NULL, 'T0001', '2019-5-21 12:14:21'),
	(11, 'T102039424', 11, 11,
	'嗜酸性粒细胞比率', 'SXXXB', '0.70', '%', 'N',
	NULL, NULL, 'T0001', '2019-5-21 12:14:21'),
	(12, 'T102039424', 12, 12,
	'嗜酸性粒细胞', 'SXXB', '0.07', NULL, 'N',
	NULL, NULL, 'T0001', '2019-5-21 12:14:21'),
	(13, 'T102039424', 10, 10,
	'RDW-CV', 'RDW', '25.37', NULL, 'H',
	NULL, NULL, 'T0001', '2019-5-21 12:14:21'),
	(14, 'T102039424', 10, 10,
	'C反应蛋白', 'PJXHDB', '41.08', 'mg/l', 'N',
	NULL, NULL, 'T0001', '2019-5-21 12:14:21');

-- PACS/RIS
insert into exam_report (id, patient_id, exam_no, exam_date, exam_para,
	exam_room, exam_body, sound_no, exam_doctor,  
	description, impression, recommendation, is_abnormal, device, img_all_url, memo)
values 
	(1, 'P10323573', 'E00001', '2019-5-21 12:14:21', NULL, 
	'妇科手术室', '子宫', 'S10893293', '王医生',
	'子宫位置: 后位。子宫大小: 长径42mm, 左右径42mm。前后径: 37mm。子宫形态: 规则。子宫回声: 欠均匀。',
	NULL, '双卵巢见多个小卵泡', 0, '内窥镜', 'img001.png,img002.png,img003.png,img004.png', NULL);