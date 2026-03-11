set echo on
REM Creating table GLSupplyDescLink for ext.sast.supply.GLSupplyDescLink
set echo off
CREATE TABLE GLSupplyDescLink (
   description   VARCHAR2(600),
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
 CONSTRAINT PK_GLSupplyDescLink PRIMARY KEY (idA2A2))
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
ENABLE PRIMARY KEY USING INDEX
 TABLESPACE INDX
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
/
COMMENT ON TABLE GLSupplyDescLink IS 'Table GLSupplyDescLink created for ext.sast.supply.GLSupplyDescLink'
/
REM @//ext/sast/supply/GLSupplyDescLink_UserAdditions
