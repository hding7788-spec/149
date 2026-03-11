exec WTPK.dropTable('TechNoticeAfterLink')
set echo on
REM Creating table TechNoticeAfterLink for ext.ases.technotice.TechNoticeAfterLink
set echo off
CREATE TABLE TechNoticeAfterLink (
   branchIdA3A5   NUMBER,
   classnamekeyroleAObjectRef   VARCHAR2(600),
   branchIdA3B5   NUMBER,
   classnamekeyroleBObjectRef   VARCHAR2(600),
   createStampA2   DATE,
   markForDeleteA2   NUMBER NOT NULL,
   modifyStampA2   DATE,
   classnameA2A2   VARCHAR2(600),
   idA2A2   NUMBER NOT NULL,
   updateCountA2   NUMBER,
   updateStampA2   DATE,
 CONSTRAINT PK_TechNoticeAfterLink PRIMARY KEY (idA2A2))
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
ENABLE PRIMARY KEY USING INDEX
 TABLESPACE INDX
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
/
COMMENT ON TABLE TechNoticeAfterLink IS 'Table TechNoticeAfterLink created for ext.ases.technotice.TechNoticeAfterLink'
/
REM @ext/ases/technotice/TechNoticeAfterLink_UserAdditions
