exec WTPK.dropTable('TechNoticeBeforeLink')
set echo on
REM Creating table TechNoticeBeforeLink for ext.ases.technotice.TechNoticeBeforeLink
set echo off
CREATE TABLE TechNoticeBeforeLink (
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
 CONSTRAINT PK_TechNoticeBeforeLink PRIMARY KEY (idA2A2))
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
ENABLE PRIMARY KEY USING INDEX
 TABLESPACE INDX
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
/
COMMENT ON TABLE TechNoticeBeforeLink IS 'Table TechNoticeBeforeLink created for ext.ases.technotice.TechNoticeBeforeLink'
/
REM @ext/ases/technotice/TechNoticeBeforeLink_UserAdditions
