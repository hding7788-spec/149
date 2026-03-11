set echo on
REM Creating table ASESHuiqianSignature for ext.ases.part.ASESHuiqianSignature
set echo off
CREATE TABLE ASESHuiqianSignature (
   activity   VARCHAR2(600),
   conclusion   VARCHAR2(600),
   implementadvise   VARCHAR2(600),
   opinion   VARCHAR2(600),
   signature   VARCHAR2(600),
   createStampA2   DATE,
   markForDeleteA2   NUMBER NOT NULL,
   modifyStampA2   DATE,
   classnameA2A2   VARCHAR2(600),
   idA2A2   NUMBER NOT NULL,
   updateCountA2   NUMBER,
   updateStampA2   DATE,
   updatestate   VARCHAR2(600),
 CONSTRAINT PK_ASESHuiqianSignature PRIMARY KEY (idA2A2))
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
ENABLE PRIMARY KEY USING INDEX
 TABLESPACE INDX
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
/
COMMENT ON TABLE ASESHuiqianSignature IS 'Table ASESHuiqianSignature created for ext.ases.part.ASESHuiqianSignature'
/
REM @//ext/ases/part/ASESHuiqianSignature_UserAdditions
