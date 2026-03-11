set echo on
REM Creating table GLCatalog for ext.sast.catalog.GLCatalog
set echo off
CREATE TABLE GLCatalog (
   GLCatalogNumber   VARCHAR2(600) NOT NULL,
   GLCatalogName   VARCHAR2(600) NOT NULL,
   catalogtype   VARCHAR2(600),
   createunit   VARCHAR2(600),
   creator   VARCHAR2(600),
   grade   VARCHAR2(600),
   modifier   VARCHAR2(600),
   remark   VARCHAR2(600),
   scope   VARCHAR2(600),
   state   VARCHAR2(600),
   createStampA2   DATE,
   markForDeleteA2   NUMBER NOT NULL,
   modifyStampA2   DATE,
   classnameA2A2   VARCHAR2(600),
   idA2A2   NUMBER NOT NULL,
   updateCountA2   NUMBER,
   updateStampA2   DATE,
 CONSTRAINT PK_GLCatalog PRIMARY KEY (idA2A2))
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
ENABLE PRIMARY KEY USING INDEX
 TABLESPACE INDX
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
/
COMMENT ON TABLE GLCatalog IS 'Table GLCatalog created for ext.sast.catalog.GLCatalog'
/
REM @//ext/sast/catalog/GLCatalog_UserAdditions
