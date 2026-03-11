set echo on
REM Creating table ASES_BATCHES_TABLE for MANAGER BATCHE
set echo off
CREATE TABLE ASES_BATCHES_TABLE(
   oid varchar2(255) not null,
   productoid varchar2(255) not null,
   name varchar2(50) not null,
   constraint ASES_BATCHES_TABLE PRIMARY KEY( oid )
);
