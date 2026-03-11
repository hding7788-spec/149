set echo on
REM Creating table ChangePackagedAffectLink for ext.ases.changepackaged.ChangePackagedAffectLink
set echo off
CREATE TABLE ChangePackagedAffectLink (
   description   VARCHAR2(600),
   classnamekeyroleAObjectRef   VARCHAR2(600),
   idA3A5   NUMBER,
   branchIdA3B5   NUMBER,
   classnamekeyroleBObjectRef   VARCHAR2(600),
   theChangePackaged   VARCHAR2(600),
   createStampA2   DATE,
   markForDeleteA2   NUMBER NOT NULL,
   modifyStampA2   DATE,
   classnameA2A2   VARCHAR2(600),
   idA2A2   NUMBER NOT NULL,
   updateCountA2   NUMBER,
   updateStampA2   DATE,
 CONSTRAINT PK_ChangePackagedAffectLink PRIMARY KEY (idA2A2))
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
ENABLE PRIMARY KEY USING INDEX
 TABLESPACE INDX
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
/
COMMENT ON TABLE ChangePackagedAffectLink IS 'Table ChangePackagedAffectLink created for ext.ases.changepackaged.ChangePackagedAffectLink'
/
REM @//ext/ases/changepackaged/ChangePackagedAffectLink_UserAdditions
