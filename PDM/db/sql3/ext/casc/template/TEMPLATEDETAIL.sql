/*
Navicat Oracle Data Transfer
Oracle Client Version : 10.2.0.5.0

Source Server         : pdm
Source Server Version : 110200
Source Host           : localhost:1521
Source Schema         : PDM10

Target Server Type    : ORACLE
Target Server Version : 110200
File Encoding         : 65001

Date: 2016-02-22 14:50:47
*/


-- ----------------------------
-- Table structure for TEMPLATEDETAIL
-- ----------------------------
DROP TABLE "PDM10"."TEMPLATEDETAIL";
CREATE TABLE "PDM10"."TEMPLATEDETAIL" (
"ROLE_NAME" VARCHAR2(600 BYTE) NULL ,
"ROLE_FULL_NAME" VARCHAR2(600 BYTE) NULL ,
"USER" VARCHAR2(600 BYTE) NULL ,
"CONTACT_ID" VARCHAR2(100 BYTE) NULL 
)
LOGGING
NOCOMPRESS
NOCACHE

;
COMMENT ON COLUMN "PDM10"."TEMPLATEDETAIL"."ROLE_NAME" IS ' 角色';
COMMENT ON COLUMN "PDM10"."TEMPLATEDETAIL"."ROLE_FULL_NAME" IS '角色英文名';
COMMENT ON COLUMN "PDM10"."TEMPLATEDETAIL"."USER" IS '用户';
COMMENT ON COLUMN "PDM10"."TEMPLATEDETAIL"."CONTACT_ID" IS '模板相关的主键';
