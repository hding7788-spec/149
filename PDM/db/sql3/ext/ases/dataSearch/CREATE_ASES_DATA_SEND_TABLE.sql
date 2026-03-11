set echo on
REM Creating table ASES_DATA_SEND_TABLE for ext.ases.dataSearch.ASES_DATA_SEND_TABLE
set echo off 
CREATE TABLE ASES_DATA_SEND_TABLE(
   PACKAGE_NUM varchar2(50) not null,
   PACKAGE_NAME varchar2(255) not null,   
   PACKAGE_OID varchar2(100) not null,
   OBJ_NUMBER varchar2(50) not null,
   OBJ_NAME varchar2(255) not null,  
   OBJ_VERSION varchar2(30) ,
   OBJ_OID varchar2(100) not null,
   PRODUCT_CODE varchar2(255),
   SEND_TYPE varchar2(30),
   SEND_DATE date,
   UNDERREVIEW varchar2(10),
   containerOID varchar2(60),
   containerName varchar2(30),
   constraint ASES_DATA_SEND_TABLE PRIMARY KEY( PACKAGE_OID,OBJ_OID,SEND_TYPE)
)
STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
/
COMMENT ON TABLE ASES_DATA_SEND_TABLE IS 'Table ASES_DATA_SEND_TABLE created for ext.ases.dataSearch.ASES_DATA_SEND_TABLE'
/
REM @ext/ases/dataSearch/ASES_DATA_SEND_TABLE_UserAdditions
	