create table PF_BOM
(
  ID          NUMBER,
  PARTNUMBER  VARCHAR2(100),
  PARTID      VARCHAR2(50),
  PARTVERSION VARCHAR2(50),
  PARTNAME    VARCHAR2(100),
  FPARTNUMBER VARCHAR2(50),
  FPARTID     VARCHAR2(50),
  PID         NUMBER
)
/

comment on column PF_BOM.ID
is '编号'
/

comment on column PF_BOM.PARTNUMBER
is '产品图号'
/

comment on column PF_BOM.PARTID
is '图号ID'
/

comment on column PF_BOM.PARTVERSION
is 'PBOM版本'
/

comment on column PF_BOM.PARTNAME
is '产品名称'
/

comment on column PF_BOM.FPARTNUMBER
is '父级图号'
/

comment on column PF_BOM.FPARTID
is '父图号ID'
/

comment on column PF_BOM.PID
is '产品型号id'
/


