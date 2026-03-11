create table PF_PRODUCT
(
  ID         NUMBER,
  PINDEX     VARCHAR2(50),
  PNAME      VARCHAR2(50),
  PHASE      VARCHAR2(20),
  TEMPLATEID NUMBER,
  ERROR      VARCHAR2(200)
)
/

comment on column PF_PRODUCT.ID
is '编号'
/

comment on column PF_PRODUCT.PINDEX
is '产品型号'
/

comment on column PF_PRODUCT.PNAME
is '产品名称'
/

comment on column PF_PRODUCT.PHASE
is '研制阶段'
/

comment on column PF_PRODUCT.TEMPLATEID
is '模板id'
/

comment on column PF_PRODUCT.ERROR
is '错误信息'
/


