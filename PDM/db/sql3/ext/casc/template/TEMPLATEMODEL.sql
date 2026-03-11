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

Date: 2016-02-22 14:50:59
*/


-- ----------------------------
-- Table structure for TEMPLATEMODEL
-- ----------------------------
DROP TABLE "PDM10"."TEMPLATEMODEL";
CREATE TABLE "PDM10"."TEMPLATEMODEL" (
"ID" VARCHAR2(100 BYTE) NOT NULL ,
"CREATE_TIME" VARCHAR2(25 BYTE) NULL ,
"CREATOR" VARCHAR2(100 BYTE) NULL ,
"MODIFY_TIME" VARCHAR2(25 BYTE) NULL ,
"MODIFIER" VARCHAR2(100 BYTE) NULL ,
"TEMPLATE_NAME" VARCHAR2(600 BYTE) NULL ,
"TEMPLATE_STYLE" VARCHAR2(900 BYTE) NULL 
)
LOGGING
NOCOMPRESS
NOCACHE

;
COMMENT ON COLUMN "PDM10"."TEMPLATEMODEL"."ID" IS '主键';
COMMENT ON COLUMN "PDM10"."TEMPLATEMODEL"."CREATE_TIME" IS '创建时间';
COMMENT ON COLUMN "PDM10"."TEMPLATEMODEL"."CREATOR" IS '创建者';
COMMENT ON COLUMN "PDM10"."TEMPLATEMODEL"."MODIFY_TIME" IS '修改时间';
COMMENT ON COLUMN "PDM10"."TEMPLATEMODEL"."MODIFIER" IS '修改者';
COMMENT ON COLUMN "PDM10"."TEMPLATEMODEL"."TEMPLATE_NAME" IS '模板名称';
COMMENT ON COLUMN "PDM10"."TEMPLATEMODEL"."TEMPLATE_STYLE" IS '模板类型';

-- ----------------------------
-- Indexes structure for table TEMPLATEMODEL
-- ----------------------------

-- ----------------------------
-- Checks structure for table TEMPLATEMODEL
-- ----------------------------
ALTER TABLE "PDM10"."TEMPLATEMODEL" ADD CHECK ("ID" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table TEMPLATEMODEL
-- ----------------------------
ALTER TABLE "PDM10"."TEMPLATEMODEL" ADD PRIMARY KEY ("ID");
