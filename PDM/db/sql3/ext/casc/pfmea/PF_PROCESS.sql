create table PF_PROCESS
(
  ID           NUMBER,
  STEPNUMBER   VARCHAR2(50),
  STEPNAME     VARCHAR2(50),
  REQUIREMENTS VARCHAR2(1000),
  PARTID       VARCHAR2(50),
  PID          NUMBER,
  DOCID          VARCHAR2(50)
)
/

comment on column PF_PROCESS.ID
is '编号'
/

comment on column PF_PROCESS.STEPNUMBER
is '工序编号'
/

comment on column PF_PROCESS.STEPNAME
is '工序名称'
/

comment on column PF_PROCESS.REQUIREMENTS
is '工序要求'
/

comment on column PF_PROCESS.PARTID
is '图号ID'
/

comment on column PF_PROCESS.PID
is '产品型号id'
/


