-- auto-generated definition
create table GL_DOCPARAMETERSLINK
(
  DOCOID          NUMBER default NULL not null,
  PARAMETEROID    NUMBER default NULL not null,
  GISTNUMBER      VARCHAR2(200),
  GISTNAME        VARCHAR2(500),
  TECHNICSNUMBER  VARCHAR2(200),
  PPNUMBER        VARCHAR2(200),
  TECHNICSNAME    VARCHAR2(500),
  TECHNICSVERSION VARCHAR2(50),
  BIANZHIZHE	  VARCHAR2(100),
  TECTYPE		  VARCHAR2(500),
  OBJTYPE         VARCHAR2(100)
)
/

comment on column GL_DOCPARAMETERSLINK.DOCOID
is 'sop工艺文件oid'
/

comment on column GL_DOCPARAMETERSLINK.PARAMETEROID
is 'sop关联对象oid'
/

comment on column GL_DOCPARAMETERSLINK.PARAMETEROID
is '依据文件编号'
/

comment on column GL_DOCPARAMETERSLINK.PARAMETEROID
is '依据文件名称'
/

comment on column GL_DOCPARAMETERSLINK.TECHNICSNUMBER
is 'sop工艺文件流水号'
/

comment on column GL_DOCPARAMETERSLINK.PPNUMBER
is 'sop工艺文件编号'
/

comment on column GL_DOCPARAMETERSLINK.TECHNICSNAME
is 'sop工艺文件名称'
/

comment on column GL_DOCPARAMETERSLINK.TECHNICSVERSION
is 'sop工艺文件大版本'
/

comment on column GL_DOCPARAMETERSLINK.OBJTYPE
is 'sop关联对象类型'
/
comment on column GL_DOCPARAMETERSLINK.TECTYPE
is '工艺类型'
/
comment on column GL_DOCPARAMETERSLINK.BIANZHIZHE
is '编制者'
/

