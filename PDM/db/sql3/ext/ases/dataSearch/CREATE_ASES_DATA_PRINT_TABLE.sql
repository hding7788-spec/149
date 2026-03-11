set echo on
REM Creating table ASES_DATA_PRINT_TABLE for ext.ases.dataSearch.ASES_DATA_PRINT_TABLE
set echo off 
CREATE TABLE ASES_DATA_PRINT_TABLE(
   OBJ_NUMBER varchar2(50) not null,
   OBJ_NAME varchar2(255) not null,  
   OBJ_VERSION varchar2(30) ,
   OBJ_OID varchar2(100) not null,
   DEPARTMENT varchar2(100) not null,
   AMOUNT varchar2(255) not null,
   PRODUCT_NAME varchar2(255),
   PRINT_DATE date
)
STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
/
COMMENT ON TABLE ASES_DATA_PRINT_TABLE IS 'Table ASES_DATA_PRINT_TABLE created for ext.ases.dataSearch.ASES_DATA_PRINT_TABLE'
/
REM @ext/ases/dataSearch/ASES_DATA_PRINT_TABLE_UserAdditions
	