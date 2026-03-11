set echo on
REM Creating table ProcessTaskItem for ext.casc.process.ProcessTaskItem
set echo off
CREATE TABLE ProcessTaskItem (
   ProcessTaskId   NUMBER,
   blob$entrySetadHocAcl   BLOB,
   chejian   VARCHAR2(600),
   completedBy   VARCHAR2(600),
   classnamekeycontainerReferen   VARCHAR2(600),
   idA3containerReference   NUMBER,
   classnamekeyA7   VARCHAR2(600),
   idA3A7   NUMBER,
   description   VARCHAR2(4000),
   classnamekeydomainRef   VARCHAR2(600),
   idA3domainRef   NUMBER,
   endDate   DATE,
   entrySetadHocAcl   VARCHAR2(4000),
   eventSet   VARCHAR2(4000),
   executorRole   VARCHAR2(600),
   classnamekeyA2folderingInfo   VARCHAR2(600),
   idA3A2folderingInfo   NUMBER,
   classnamekeyB2folderingInfo   VARCHAR2(600),
   idA3B2folderingInfo   NUMBER,
   fuzhichejian   VARCHAR2(600),
   gongyiyuan   VARCHAR2(600),
   indexersindexerSet   VARCHAR2(4000),
   inheritedDomain   NUMBER(1),
   iszhuzhi   NUMBER(1),
   name   VARCHAR2(600) NOT NULL,
   ProcessTaskItemNumber   VARCHAR2(600) NOT NULL,
   classnamekeyorganizationRefe   VARCHAR2(600),
   idA3organizationReference   NUMBER,
   owner   VARCHAR2(600),
   classnamekeyA2ownership   VARCHAR2(600),
   idA3A2ownership   NUMBER,
   partId   NUMBER,
   renwuyaoqiu   VARCHAR2(4000),
   renwuyiju   VARCHAR2(600),
   routeSelect   VARCHAR2(600),
   securityLabels   VARCHAR2(4000),
   atGatestate   NUMBER(1),
   classnamekeyA2state   VARCHAR2(600),
   idA3A2state   NUMBER,
   statestate   VARCHAR2(600) NOT NULL,
   taskItemName   VARCHAR2(600),
   taskItemState   VARCHAR2(600),
   taskType   VARCHAR2(600),
   teamIdIsNull   NUMBER(1),
   classnamekeyteamId   VARCHAR2(600),
   idA3teamId   NUMBER,
   teamTemplateIdIsNull   NUMBER(1),
   classnamekeyteamTemplateId   VARCHAR2(600),
   idA3teamTemplateId   NUMBER,
   createStampA2   DATE,
   markForDeleteA2   NUMBER NOT NULL,
   modifyStampA2   DATE,
   classnameA2A2   VARCHAR2(600),
   idA2A2   NUMBER NOT NULL,
   updateCountA2   NUMBER,
   updateStampA2   DATE,
   branchIdA2typeDefinitionRefe   NUMBER,
   idA2typeDefinitionReference   NUMBER,
   version   VARCHAR2(600),
   zhurengongyishi   VARCHAR2(600),
   zhuzhichejian   VARCHAR2(600),
 CONSTRAINT PK_ProcessTaskItem PRIMARY KEY (idA2A2))
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
LOB ( blob$entrySetadHocAcl ) STORE AS 
 (TABLESPACE BLOBS
    STORAGE ( INITIAL 50k NEXT 50k PCTINCREASE 1 )
             CHUNK 32k)
ENABLE PRIMARY KEY USING INDEX
 TABLESPACE INDX
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
/
COMMENT ON TABLE ProcessTaskItem IS 'Table ProcessTaskItem created for ext.casc.process.ProcessTaskItem'
/
REM @//ext/casc/process/ProcessTaskItem_UserAdditions
