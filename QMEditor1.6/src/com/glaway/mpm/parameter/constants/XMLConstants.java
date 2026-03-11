package com.glaway.mpm.parameter.constants;

public class XMLConstants {

	/** 公共属性 */
	public static String ATTRIBUTE_OID = "OID";
	public static String ATTRIBUTE_MASTEROID = "MASTEROID";
	public static String ATTRIBUTE_NAME = "NAME";
	public static String ATTRIBUTE_NUMBER = "NUMBER";
	public static String ATTRIBUTE_VERSION = "VERSION";
	public static String ATTRIBUTE_STATE = "STATE";
	public static String ATTRIBUTE_MODIFIERTIME = "MODIFIERTIME";
	public static String ATTRIBUTE_CREATETIME = "CREATETIME";

	/** 部件属性 */
	public static String ATTRIBUTE_USECOUNT = "USECOUNT";
	public static String ATTRIBUTE_OCCPATH = "OCCPATH";
	public static String ATTRIBUTE_ISRECORD = "ISRECORD";

	/** 附件属性 */
	public static String ATTRIBUTE_TYPE = "TYPE";
	public static String ATTRIBUTE_SIZE = "SIZE";
	public static String ATTRIBUTE_PATH = "PATH";
	public static String ATTRIBUTE_TEMPLATETYPE = "TEMPLATETYPE";
	public static final String ATTRIBUTE_DRAWING = "DRAWING";
	public static final String ATTRIBUTE_ATTACHMENT = "ATTACHMENT";
	public static final String ATTRIBUTE_SCHEDULE = "SCHEDULE";
	public static final String ATTRIBUTE_ATTACHMENTTYPE = "ATTACHMENTTYPE";
	public static final String ATTRIBUTE_COMMON = "COMMON";
	public static final String ATTRIBUTE_SPECIALWORD = "SPECIALWORD";

	/** 工序属性 */
	public static String ATTRIBUTE_PROCEDURELABEL = "stepNumber";
	public static String ATTRIBUTE_PROCEDURENAME = "stepName";
	public static String ATTRIBUTE_ORDERNO = "ORDERNO";
	public static String ATTRIBUTE_RISKTYPE = "RISKTYPE";
	public static String ATTRIBUTE_CARDTYPE = "CARDTYPE";
	public static String ATTRIBUTE_DEPT = "DEPT";
	public static String ATTRIBUTE_WORKCENTER = "workShop";
	public static String ATTRIBUTE_ISKEY = "isKey";
	public static String ATTRIBUTE_ISASSISTANT = "ISASSISTANT";
	public static String ATTRIBUTE_ISCHECK = "ISCHECK";
	public static String ATTRIBUTE_PSIZE = "PSIZE";
	public static String ATTRIBUTE_PROCESSPLANOID = "PROCESSPLANOID";
	public static String ATTRIBUTE_PROCESSTYPE = "PROCESSTYPE";
	public static String ATTRIBUTE_HANDLER = "HANDLER";
	public static String ATTRIBUTE_ENVIRONMENTALPAMAMETER = "ENVIRONMENTALPAMAMETER";
	public static String ATTRIBUTE_CONTROLCHART = "CONTROLCHART";
	public static String ATTRIBUTE_WORKSHOPSUGGESTION = "WORKSHOPSUGGESTION";
	public static String ATTRIBUTE_ASSISTANTTYPE = "ASSISTANTTYPE";
	public static String ATTRIBUTE_SHORTDESCRIPTION = "SHORTDESCRIPTION";
	public static String ATTRIBUTE_JOINREQUIRE = "JOINREQUIRE";
	public static String ATTRIBUTE_BSOID = "bsoID";
	public static String ATTRIBUTE_PREBSOID = "preBsoID";
	public static String ATTRIBUTE_NEXTBSOID = "nextBsoID";
	public static String ATTRIBUTE_TECHNICMETHOD = "TECHNICMETHOD";
	public static String ATTRIBUTE_SECONDDANJIAN = "SECONDDANJIAN";
	public static String ATTRIBUTE_SECONDZHUNJIE = "SECONDZHUNJIE";
	public static String ATTRIBUTE_ISSUBMITEDTASK = "ISSUBMITEDTASK";
	public static String ATTRIBUTE_PVSNAME = "PVSNAME";
	public static String ATTRIBUTE_REMARK = "REMARK";
	public static String ATTRIBUTE_ASSISTECHNICSNUMBER = "ASSISTECHNICSNUMBER";
	public static String ATTRIBUTE_ASSISTECHNICSSTATE = "ASSISTECHNICSSTATE";
	public static String ATTRIBUTE_ASSISTECHNICSNAME = "ASSISTECHNICSNAME";
	public static String ATTRIBUTE_ASSISTECHNICSPINDEX = "ASSISTECHNICSPINDEX";
	public static String ATTRIBUTE_ASSISTECHNICSPHASECODE = "ASSISTECHNICSPHASECODE";
	public static String ATTRIBUTE_PREPROCEDURE = "PREPROCEDURE";
	/** 工序MES端执行的状态 */
	public static String EXECUTESTATE = "EXECUTESTATE";

	/** 工艺属性 */
	public static String ATTRIBUTE_XMLKEY_PARTNUMBER = "partNumber";
	public static String ATTRIBUTE_XMLKEY_PARTNAME = "partName";
	public static String ATTRIBUTE_PARTNUMBER = "PARTNUMBER";
	public static String ATTRIBUTE_PARTNAME = "PARTNAME";
	public static String ATTRIBUTE_PARTOID = "partOid";
	public static String ATTRIBUTE_PROCESSCATEGORY = "PROCESSCATEGORY";
	public static String ATTRIBUTE_SECRET = "SECRET";
	public static String ATTRIBUTE_KEYCOMPONENT = "KEYCOMPONENT";
	public static String ATTRIBUTE_PINDEX = "PINDEX";
	public static String ATTRIBUTE_PHASECODE = "PHASE_CODE";
	public static String ATTRIBUTE_PROCESSNUMBER = "PROCESSNUMBER";
	public static String ATTRIBUTE_PPLANNUMBER = "PPLANNUMBER";
	public static String ATTRIBUTE_PRODUCTNAME = "PRODUCTNAME";
	public static String ATTRIBUTE_PRODUCTOID = "PRODUCTOID";
	public static String ATTRIBUTE_PROCESSTASKOID = "PROCESSTASKOID";
	public static String ATTRIBUTE_APPROVETIME = "APPROVETIME";
	public static String ATTRIBUTE_STATUS = "STATUS";
	public static String ATTRIBUTE_MINDEX = "MINDEX";
	public static String ATTRIBUTE_CINDEX = "CINDEX";

	/** 签审人员名称 */
	public static String ATTRIBUTE_BIANZHI = "BIANZHIZHE";
	public static String ATTRIBUTE_JIAODUI = "JIAODUIZHE";
	public static String ATTRIBUTE_SHENHE = "SHENHEZHE";
	public static String ATTRIBUTE_BIAOSHEN = "BIAOSHENZHE";
	public static String ATTRIBUTE_APPROVER = "APPROVER";
	public static String ATTRIBUTE_NEIBUHUIQIAN = "NEIBUHUIQIANZHE";

	/** 工步属性 */
	public static String ATTRIBUTE_SEQUENCELABEL = "stepNumber";
	public static String ATTRIBUTE_SEQUENCENAME = "stepName";
	public static String ATTRIBUTE_PROCEDUREOID = "PROCEDUREOID";

	/** 用户属性 */
	public static String ATTRIBUTE_CREATOROID = "CREATOROID";
	public static String ATTRIBUTE_CREATORNAME = "CREATORNAME";
	public static String ATTRIBUTE_CREATORFULLNAME = "CREATORFULLNAME";
	public static String ATTRIBUTE_CREATOREMAIL = "CREATOREMAIL";
	public static String ATTRIBUTE_CREATORDEPT = "CREATORDEPT";

	public static String ATTRIBUTE_MODIFIEROID = "MODIFIEROID";
	public static String ATTRIBUTE_MODIFIERNAME = "MODIFIERNAME";
	public static String ATTRIBUTE_MODIFIERFULLNAME = "MODIFIERFULLNAME";
	public static String ATTRIBUTE_MODIFIEREMAIL = "MODIFIEREMAIL";
	public static String ATTRIBUTE_MODIFIERDEPT = "MODIFIERDEPT";

	/** 材料属性 */
	public static String ATTRIBUTE_PTC_MATERIAL_NAME = "PTC_MATERIAL_NAME";
	public static String ATTRIBUTE_BLANKSIZE = "BLANKSIZE";
	public static String ATTRIBUTE_MARKNUMBER = "MARKNUMBER";
	public static String ATTRIBUTE_CSIZE = "CSIZE";
	public static String ATTRIBUTE_USESTANDARD = "USESTANDARD";
	public static String ATTRIBUTE_INVCODE = "INVCODE";
	public static String ATTRIBUTE_INVNAME = "INVNAME";
	public static String ATTRIBUTE_SUPPLYSTATE = "SUPPLYSTATE";
	public static String ATTRIBUTE_MEASUREUNIT = "MEASUREUNIT";
	public static String ATTRIBUTE_MATQUOTA = "MATQUOTA";
	public static String ATTRIBUTE_NUMPERBLANK = "NUMPERBLANK";

	/** 工艺资源属性 */
	/** 类别 **/
	public static String ATTRIBUTE_S_TYPEID = "S_MPMRESOURCETYPEID";
	/** 特殊要求 **/
	public static String ATTRIBUTE_S_SPECIAL = "S_SPECIAL";
	/** 规格 **/
	public static String ATTRIBUTE_S_SPEC = "S_SPEC";
	/** 型号 **/
	public static String ATTRIBUTE_S_TYPE = "S_TYPE";
	/** 资源类型 **/
	public static String ATTRIBUTE_RESOURCETYPE = "RESOURCETYPE";
	/** 属性分类 **/
	public static String ATTRIBUTE_RESOURCEUSEDTYPE = "RESOURCEUSEDTYPE";

	/** 工艺参数属性 */
	public static String ATTRIBUTE_CHINANAME = "CHINANAME";
	public static String ATTRIBUTE_TECHNICSTYPE = "TECHNICSTYPE";
	public static String ATTRIBUTE_OBJECTTYPE = "OBJECTTYPE";
	public static String ATTRIBUTE_ISCOMMON = "ISCOMMON";
	public static String ATTRIBUTE_TABLETYPE = "TABLETYPE";
	public static String ATTRIBUTE_COLUMNNAME = "COLUMNNAME";

	/**工艺状态表**/
	public static String ATTRIBUTE_MBA_ASSISTANTDEPT = "ASSISTANTDEPT";
	public static String ATTRIBUTE_USEDEPT = "USEDEPT";

	/** 配套明细属性 */
	public static String ATTRIBUTE_PEITAODETAIL_GWKEYID = "PEITAODETAIL_GWKEYID";
	public static String ATTRIBUTE_PEITAODETAIL_PROCESSPLANOID = "PEITAODETAIL_PROCESSPLANOID";
	public static String ATTRIBUTE_PEITAODETAIL_INDEX = "PEITAODETAIL_INDEX";
	public static String ATTRIBUTE_PEITAODETAIL_CODE = "PEITAODETAIL_CODE";
	public static String ATTRIBUTE_PEITAODETAIL_NAME = "PEITAODETAIL_NAME";
	public static String ATTRIBUTE_PEITAODETAIL_SPEC = "PEITAODETAIL_SPEC";
	public static String ATTRIBUTE_PEITAODETAIL_PAIHAO = "PEITAODETAIL_PAIHAO";
	public static String ATTRIBUTE_PEITAODETAIL_SURFACESTATE = "PEITAODETAIL_SURFACESTATE";
	public static String ATTRIBUTE_PEITAODETAIL_PERFORMANCELEVEL = "PEITAODETAIL_PERFORMANCELEVEL";
	public static String ATTRIBUTE_PEITAODETAIL_UNIT = "PEITAODETAIL_UNIT";
	public static String ATTRIBUTE_PEITAODETAIL_QUANTITY = "PEITAODETAIL_QUANTITY";
	public static String ATTRIBUTE_PEITAODETAIL_FROMWHERE = "PEITAODETAIL_FROMWHERE";
	public static String ATTRIBUTE_PEITAODETAIL_CATEGORY = "PEITAODETAIL_CATEGORY";
	public static String ATTRIBUTE_PEITAODETAIL_MEMO = "PEITAODETAIL_MEMO";


	/** 检验记录表属性 */
	public static String TABLE_ENNAME = "ENNAME";
	public static String TABLE_CHINANAME = "CHINANAME";
	public static String TABLE_OID = "OID";
	public static String TABLE_VERSION = "VERSION";
	public static String TABLE_TECHNICSTYPE = "TECHNICSTYPE";
}
