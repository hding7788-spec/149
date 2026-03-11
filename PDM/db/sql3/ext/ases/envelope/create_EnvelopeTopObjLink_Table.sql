set echo on
REM Creating table EnvelopeTopObjLink for ext.ases.envelope.EnvelopeTopObjLink
set echo off
CREATE TABLE EnvelopeTopObjLink (
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
 CONSTRAINT PK_EnvelopeTopObjLink PRIMARY KEY (idA2A2))
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
ENABLE PRIMARY KEY USING INDEX
 TABLESPACE INDX
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
/
COMMENT ON TABLE EnvelopeTopObjLink IS 'Table EnvelopeTopObjLink created for ext.ases.envelope.EnvelopeTopObjLink'
/
REM @//ext/ases/envelope/EnvelopeTopObjLink_UserAdditions
