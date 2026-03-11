set echo on
REM Creating table ChangePackaged for ext.ases.changepackaged.ChangePackaged
set echo off
CREATE TABLE ChangePackaged (
   affectdpage   VARCHAR2(600),
   avidmtype   VARCHAR2(600),
   blob$entrySetadHocAcl   BLOB,
   changeleixing   VARCHAR2(600),
   changereason   VARCHAR2(600),
   changetype   VARCHAR2(600),
   complex   VARCHAR2(600),
   classnamekeycontainerReferen   VARCHAR2(600),
   idA3containerReference   NUMBER,
   cost   VARCHAR2(600),
   classnamekeyA7   VARCHAR2(600),
   idA3A7   NUMBER,
   department   VARCHAR2(600),
   description   VARCHAR2(4000),
   classnamekeydomainRef   VARCHAR2(600),
   idA3domainRef   NUMBER,
   edittime   VARCHAR2(600),
   entrySetadHocAcl   VARCHAR2(4000),
   eventSet   VARCHAR2(4000),
   filenumber   VARCHAR2(600),
   classnamekeyA2folderingInfo   VARCHAR2(600),
   idA3A2folderingInfo   NUMBER,
   classnamekeyB2folderingInfo   VARCHAR2(600),
   idA3B2folderingInfo   NUMBER,
   guancanghao   VARCHAR2(600),
   implement   VARCHAR2(600),
   indexersindexerSet   VARCHAR2(4000),
   inheritedDomain   NUMBER(1),
   name   VARCHAR2(600) NOT NULL,
   ChangePackaged   VARCHAR2(600) NOT NULL,
   classnamekeyorganizationRefe   VARCHAR2(600),
   idA3organizationReference   NUMBER,
   classnamekeyA2ownership   VARCHAR2(600),
   idA3A2ownership   NUMBER,
   phasecode   VARCHAR2(600),
   pindex   VARCHAR2(600),
   profession   VARCHAR2(600),
   remark   VARCHAR2(600),
   requestpriority   VARCHAR2(600),
   requesttime   VARCHAR2(600),
   responsor   VARCHAR2(600),
   secret   VARCHAR2(600),
   securityLabels   VARCHAR2(4000),
   startphasename   VARCHAR2(600),
   atGatestate   NUMBER(1),
   classnamekeyA2state   VARCHAR2(600),
   idA3A2state   NUMBER,
   statestate   VARCHAR2(600) NOT NULL,
   targetphasename   VARCHAR2(600),
   teamIdIsNull   NUMBER(1),
   classnamekeyteamId   VARCHAR2(600),
   idA3teamId   NUMBER,
   teamTemplateIdIsNull   NUMBER(1),
   classnamekeyteamTemplateId   VARCHAR2(600),
   idA3teamTemplateId   NUMBER,
   template   VARCHAR2(600),
   createStampA2   DATE,
   markForDeleteA2   NUMBER NOT NULL,
   modifyStampA2   DATE,
   classnameA2A2   VARCHAR2(600),
   idA2A2   NUMBER NOT NULL,
   updateCountA2   NUMBER,
   updateStampA2   DATE,
   branchIdA2typeDefinitionRefe   NUMBER,
   idA2typeDefinitionReference   NUMBER,
 CONSTRAINT PK_ChangePackaged PRIMARY KEY (idA2A2))
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
LOB ( blob$entrySetadHocAcl ) STORE AS 
 (TABLESPACE BLOBS
    STORAGE ( INITIAL 50k NEXT 50k PCTINCREASE 1 )
             CHUNK 32k)
ENABLE PRIMARY KEY USING INDEX
 TABLESPACE INDX
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
/
COMMENT ON TABLE ChangePackaged IS 'Table ChangePackaged created for ext.ases.changepackaged.ChangePackaged'
/
REM @//ext/ases/changepackaged/ChangePackaged_UserAdditions
