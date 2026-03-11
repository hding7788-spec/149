create table PF_DOC
(
  ID     NUMBER,
  NAME   VARCHAR2(200),
  DOCID  VARCHAR2(100),
  PARTID VARCHAR2(100),
  PID    NUMBER
)
/

comment on column PF_DOC.ID
is '编号'
/

comment on column PF_DOC.NAME
is '名称'
/

comment on column PF_DOC.DOCID
is '文档编号'
/

comment on column PF_DOC.PARTID
is '部件oid'
/

comment on column PF_DOC.PID
is '产品id'
/

