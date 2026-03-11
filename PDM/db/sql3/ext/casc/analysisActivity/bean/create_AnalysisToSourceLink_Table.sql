set echo on
REM Creating table AnalysisToSourceLink for ext.casc.analysisActivity.bean.AnalysisToSourceLink
set echo off
CREATE TABLE AnalysisToSourceLink (
   plNumber   VARCHAR2(600),
   classnamekeyroleAObjectRef   VARCHAR2(600),
   idA3A5   NUMBER,
   classnamekeyroleBObjectRef   VARCHAR2(600),
   idA3B5   NUMBER,
   createStampA2   DATE,
   markForDeleteA2   NUMBER NOT NULL,
   modifyStampA2   DATE,
   classnameA2A2   VARCHAR2(600),
   idA2A2   NUMBER NOT NULL,
   updateCountA2   NUMBER,
   updateStampA2   DATE,
 CONSTRAINT PK_AnalysisToSourceLink PRIMARY KEY (idA2A2))
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
ENABLE PRIMARY KEY USING INDEX
 TABLESPACE INDX
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
/
COMMENT ON TABLE AnalysisToSourceLink IS 'Table AnalysisToSourceLink created for ext.casc.analysisActivity.bean.AnalysisToSourceLink'
/
REM @//ext/casc/analysisActivity/bean/AnalysisToSourceLink_UserAdditions
