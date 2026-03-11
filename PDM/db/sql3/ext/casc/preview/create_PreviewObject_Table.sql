set echo on
REM Creating table PreviewObject for ext.casc.preview.PreviewObject
set echo off
CREATE TABLE PreviewObject (
   description   VARCHAR2(600),
   designCompany   VARCHAR2(600),
   designer   VARCHAR2(600),
   maturityReason   VARCHAR2(600),
   modelMaturity   VARCHAR2(600),
   name   VARCHAR2(600) NOT NULL,
   PreOjbNumber   VARCHAR2(600) NOT NULL,
   objType   VARCHAR2(600),
   objVer   VARCHAR2(600),
   createStampA2   DATE,
   markForDeleteA2   NUMBER NOT NULL,
   modifyStampA2   DATE,
   classnameA2A2   VARCHAR2(600),
   idA2A2   NUMBER NOT NULL,
   updateCountA2   NUMBER,
   updateStampA2   DATE,
 CONSTRAINT PK_PreviewObject PRIMARY KEY (idA2A2))
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
ENABLE PRIMARY KEY USING INDEX
 TABLESPACE INDX
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
/
COMMENT ON TABLE PreviewObject IS 'Table PreviewObject created for ext.casc.preview.PreviewObject'
/
REM @//ext/casc/preview/PreviewObject_UserAdditions
