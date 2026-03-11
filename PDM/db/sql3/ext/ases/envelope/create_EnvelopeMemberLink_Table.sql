set echo on
REM Creating table EnvelopeMemberLink for ext.ases.envelope.EnvelopeMemberLink
set echo off
CREATE TABLE EnvelopeMemberLink (
   description   VARCHAR2(600),
   implementadvise   VARCHAR2(600),
   classnamekeyroleAObjectRef   VARCHAR2(600),
   idA3A5   NUMBER,
   branchIdA3B5   NUMBER,
   classnamekeyroleBObjectRef   VARCHAR2(600),
   createStampA2   DATE,
   markForDeleteA2   NUMBER NOT NULL,
   modifyStampA2   DATE,
   classnameA2A2   VARCHAR2(600),
   idA2A2   NUMBER NOT NULL,
   updateCountA2   NUMBER,
   updateStampA2   DATE,
 CONSTRAINT PK_EnvelopeMemberLink PRIMARY KEY (idA2A2))
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
ENABLE PRIMARY KEY USING INDEX
 TABLESPACE INDX
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
/
COMMENT ON TABLE EnvelopeMemberLink IS 'Table EnvelopeMemberLink created for ext.ases.envelope.EnvelopeMemberLink'
/
REM @//ext/ases/envelope/EnvelopeMemberLink_UserAdditions
