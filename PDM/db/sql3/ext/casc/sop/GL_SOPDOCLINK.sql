-- auto-generated definition
create table GL_SOPDOCLINK
(
  DOCNUMBER     VARCHAR2(100),
  DOCName       VARCHAR2(100),
  DOCCONTAINER  VARCHAR2(100),
  DOCVERSION    VARCHAR2(50),
  DOCSTEPNUMBER VARCHAR2(50),
  DOCSTEPNAME   VARCHAR2(50),
  DOCSTEPBSOID  VARCHAR2(100),
  SOPNUMBER     VARCHAR2(100),
  SOPNUM        VARCHAR2(100),
  SOPVERSION    VARCHAR2(50)
)
/

comment on column GL_SOPDOCLINK.DOCNUMBER
is '专用工艺流水号'
/

comment on column GL_SOPDOCLINK.DOCName
is '专用工艺名称'
/

comment on column GL_SOPDOCLINK.DOCCONTAINER
is '专用工艺上下文'
/

comment on column GL_SOPDOCLINK.DOCVERSION
is '专用工艺大版本'
/

comment on column GL_SOPDOCLINK.DOCSTEPNUMBER
is '专用工艺工序号'
/

comment on column GL_SOPDOCLINK.DOCSTEPNAME
is '专用工艺工序名称'
/

comment on column GL_SOPDOCLINK.DOCSTEPBSOID
is '专用工艺工序BSOID属性'
/

comment on column GL_SOPDOCLINK.DOCNUMBER
is 'SOP工艺编号'
/

comment on column GL_SOPDOCLINK.SOPNUM
is 'SOP工艺流水号'
/

comment on column GL_SOPDOCLINK.SOPVERSION
is 'SOP工艺大版本号'
/


