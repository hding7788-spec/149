set echo on
REM Creating table TechnicsMaterialEntries for ext.ases.techMaterial.TechnicsMaterialEntries
set echo off
CREATE TABLE TechnicsMaterialEntries (
   administrativeLockIsNull   NUMBER(1),
   typeadministrativeLock   VARCHAR2(150),
   blob$entrySetadHocAcl   BLOB,
   classnamekeycontainerReferen   VARCHAR2(600),
   idA3containerReference   NUMBER,
   classnamekeyA7   VARCHAR2(600),
   idA3A7   NUMBER,
   description   VARCHAR2(4000),
   classnamekeydomainRef   VARCHAR2(600),
   idA3domainRef   NUMBER,
   entrySetadHocAcl   VARCHAR2(4000),
   eventSet   VARCHAR2(4000),
   classnamekeyA2folderingInfo   VARCHAR2(600),
   idA3A2folderingInfo   NUMBER,
   classnamekeyB2folderingInfo   VARCHAR2(600),
   idA3B2folderingInfo   NUMBER,
   indexersindexerSet   VARCHAR2(4000),
   inheritedDomain   NUMBER(1),
   TechnicsMaterialEntriesName   VARCHAR2(600) NOT NULL,
   TechnicsMaterialEntriesNumbe   VARCHAR2(600) NOT NULL,
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
 CONSTRAINT PK_TechnicsMaterialEntries PRIMARY KEY (idA2A2))
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
LOB ( blob$entrySetadHocAcl ) STORE AS 
 (TABLESPACE BLOBS
    STORAGE ( INITIAL 50k NEXT 50k PCTINCREASE 1 )
             CHUNK 32k)
ENABLE PRIMARY KEY USING INDEX
 TABLESPACE INDX
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
/
COMMENT ON TABLE TechnicsMaterialEntries IS 'Table TechnicsMaterialEntries created for ext.ases.techMaterial.TechnicsMaterialEntries'
/
REM @//ext/ases/techMaterial/TechnicsMaterialEntries_UserAdditions
