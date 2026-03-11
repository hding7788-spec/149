set echo on
REM Creating table GLSupply for ext.sast.supply.GLSupply
set echo off
CREATE TABLE GLSupply (
   address   VARCHAR2(600),
   bc   VARCHAR2(600),
   classgrade   VARCHAR2(600),
   code   VARCHAR2(600),
   cym   VARCHAR2(600),
   cz   VARCHAR2(600),
   email   VARCHAR2(600),
   jc   VARCHAR2(600),
   lxdh   VARCHAR2(600),
   lxr   VARCHAR2(600),
   GLSupplyName   VARCHAR2(600) NOT NULL,
   GLSupplyNumber   VARCHAR2(600) NOT NULL,
   qyxz   VARCHAR2(600),
   rdcp   VARCHAR2(600),
   remark   VARCHAR2(600),
   state   VARCHAR2(600) NOT NULL,
   createStampA2   DATE,
   markForDeleteA2   NUMBER NOT NULL,
   modifyStampA2   DATE,
   classnameA2A2   VARCHAR2(600),
   idA2A2   NUMBER NOT NULL,
   updateCountA2   NUMBER,
   updateStampA2   DATE,
   wzlb   VARCHAR2(600) NOT NULL,
   zipcode   VARCHAR2(600),
 CONSTRAINT PK_GLSupply PRIMARY KEY (idA2A2))
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
ENABLE PRIMARY KEY USING INDEX
 TABLESPACE INDX
 STORAGE ( INITIAL 20k NEXT 20k PCTINCREASE 0 )
/
COMMENT ON TABLE GLSupply IS 'Table GLSupply created for ext.sast.supply.GLSupply'
/
REM @//ext/sast/supply/GLSupply_UserAdditions
