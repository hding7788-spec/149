set echo on
REM Creating table Preview for ext.casc.preview.Preview
set echo off
CREATE TABLE Preview (
   administrativeLockIsNull   NUMBER(1),
   typeadministrativeLock   VARCHAR2(150),
   blob$entrySetadHocAcl   BLOB,
   classnamekeycontainerReferen   VARCHAR2(600),
   idA3containerReference   NUMBER,
   classnamekeyA7   VARCHAR2(600),
   idA3A7   NUMBER,
   description   VARCHAR2(600),
   designCompany   VARCHAR2(600),
   designer   VARCHAR2(600),
   classnamekeydomainRef   VARCHAR2(600),
   idA3domainRef   NUMBER,
   entrySetadHocAcl   VARCHAR2(4000),
   eventSet   VARCHAR2(4000),
   classnamekeyA2folderingInfo   VARCHAR2(600),
   idA3A2folderingInfo   NUMBER,
   classnamekeyB2folderingInfo   VARCHAR2(600),
   idA3B2folderingInfo   NUMBER,
   classnamekeyformat   VARCHAR2(600),
   idA3format   NUMBER,
   indexersindexerSet   VARCHAR2(4000),
   inheritedDomain   NUMBER(1),
   name   VARCHAR2(600) NOT NULL,
   PreviewNumber   VARCHAR2(600) NOT NULL,
   classnamekeyorganizationRefe   VARCHAR2(600),
   idA3organizationReference   NUMBER,
   classnamekeyA2ownership   VARCHAR2(600),
   idA3A2ownership   NUMBER,
   securityLabels   VARCHAR2(4000),
   atGatestate   NUMBER(1),
   classnamekeyA2state   VARCHAR2(600),
   idA3A2state   NUMBER,
   statestate   VARCHAR2(600) NOT NULL,
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
 CONSTRAINT PK_Preview PRIMARY KEY (idA2A2))
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
LOB ( blob$entrySetadHocAcl ) STORE AS 
 (TABLESPACE BLOBS
    STORAGE ( INITIAL 50k NEXT 50k PCTINCREASE 1 )
             CHUNK 32k)
ENABLE PRIMARY KEY USING INDEX
 TABLESPACE INDX
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
/
COMMENT ON TABLE Preview IS 'Table Preview created for ext.casc.preview.Preview'
/
REM @//ext/casc/preview/Preview_UserAdditions
