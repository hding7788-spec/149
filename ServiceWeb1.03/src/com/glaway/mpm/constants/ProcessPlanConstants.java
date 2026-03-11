package com.glaway.mpm.constants;

public class ProcessPlanConstants {

	public static final String technicsFolderPath = "/Default/02工艺文件/03工艺规程";
	public static final String technicsTyFolderPath = "/Default/通用工艺/工艺规程";
	public static final String technicsYhtyFolderPath = "/Default/宇航通用工艺";
	public static final String defaultFolderPath = "/Default";
	public static final String PRODUCT_LS = "临时产品";
	public static final String LIBRARY_TECHNICSRESOURCE = "工艺资源库";
	public static final String LIBRARY_TECHNICSKNOWLEDGE = "工艺知识库";
	public static final String WORKFLOW_TECHNICS_SUBMIT = "工艺规程签审流程";
	public static final String WORKFLOW_TECHNICS_CHANGE = "工艺更改签审流程";
	public static final String WORKFLOW_TECHNICS_INSTANCECHANGE = "实例化工艺更改流程";
	public static final String WORKFLOW_TECHNICSSTATUS_SUBMIT = "工艺状态表签审流程";
	public static final String WORKFLOW_TECHNICSSTATUS_CHANGE = "工艺状态表更改签审流程";
	public static final String WORKFLOW_TYPICALTECHNIC_SUBMIT = "典型工艺入库签审流程";

	public static final String SUCCESS = "success";
	public static final String FAILED = "failed";

	public static String ATTRIBUTE_NAME = "NAME";
	public static String ATTRIBUTE_NUMBER = "NUMBER";
	public static String ATTRIBUTE_DESIGN = "Design";
	public static String ATTRIBUTE_MANUFACTURING = "Manufacturing";
	public static String ATTRIBUTE_PROCESSTASKOID = "PROCESSTASKOID";
	public static final String ATTRIBUTE_PARTOID = "partOid";
	public static final String MBA_PARTNUMBER = "PARTNUMBER";
	public static final String MBA_PARTNAME = "PARTNAME";
	public static final String MBA_PROCESSCATEGORY = "PROCESSCATEGORY";
	public static final String MBA_PROCESSTYPE = "PROCESSTYPE";
	public static final String MBA_SECRET = "SECRET";
	public static final String MBA_KEYCOMPONENT = "KEYCOMPONENT";
	public static final String MBA_PINDEX = "PINDEX";
	public static final String MBA_PHASECODE = "PHASE_CODE";
	public static final String MBA_DEPT = "DEPT";
	public static final String MBA_ASSISTANTDEPT = "ASSISTANTDEPT";
	public static final String MBA_PROCESSNUMBER = "PROCESSNUMBER";

	/**型号代号*/
	public static final String MBA_MINDEX = "MINDEX";
	public static final String MBA_CINDEX = "CINDEX";
	public static final String MBA_ISCAPPIMPORT = "ISCAPPIMPORT";
	public static final String IBA_PRODUCT_PROCESSIDENTIFY = "PROCESSIDENTIFY";
	public static String ATTRIBUTE_STATE = "STATE";

	public static final String DOCTYPE_ZS = "正式工艺";
	public static final String DOCTYPE_TY = "通用工艺";
	public static final String DOCTYPE_DX = "典型工艺";
	public static final String DOCTYPE_YHTY = "宇航通用工艺";
	public static final String DOCTYPE_ZSTY = "战术通用工艺";
	public static final String DOCTYPE_LS = "临时工艺";
	public static final String DOCTYPE_SLY = "实例化工艺";
	public static final String DOCTYPE_GYZTB = "工艺状态表";

	public static final String TABLE_ORDER_NAME = "TECHNICSORDERNUMBER";
	public static final String TABLE_ORDER_PARTNUMBER = "PARTNUMBER";
	public static final String TABLE_ORDER_PROCESSIDENTIFY = "PROCESSIDENTIFY";
	public static final String TABLE_ORDER_TECHNICSCODENUMBER = "TECHNICSCODENUMBER";
	public static final String TABLE_ORDER_DEPARTCODENUMBER = "DEPARTMENTCODENUMBER";
	public static final String TABLE_ORDER_STATUSORDERNUMBER = "STATUSORDERNUMBER";
	public static final String TABLE_ORDER_TECHNICSCODE = "TECHNICSCODE";
	public static final String TABLE_ORDER_DEPARTCODE = "DEPARTMENTCODE";
	public static final String TABLE_COLUMN_PREFIX = "PREFIX";
	public static final String TABLE_COLUMN_SN = "SN";
	public static final String TABLE_COLUMN_PHASECODE = "PHASECODE";

	public static final String FILE_ROUTE_JPG = "technics_route.jpg";
	public static final String FILE_ROUTE_XML = "technics_route.xml";

	public static final String ATTACHMENT_DRAWING = "DRAWING";
	public static final String ATTACHMENT_ATTACHMENT = "ATTACHMENT";
	public static final String ATTACHMENT_COMMON = "COMMON";
	public static final String ATTACHMENT_SPECIALWORD = "SPECIALWORD";
	public static final String ATTACHMENT_SCHEDULE = "SCHEDULE";
	public static final String ATTACHMENT_DRAWINGTYPE_STATUS = "TechnicStatusDrawing";
	public static final String ATTACHMENT_DRAWINGTYPE_SUGGESTION = "WorkShopSuggestionDrawing";

	public static final String ZIP = ".zip";
	public static final String XML = ".xml";
	public static final String PDF = ".pdf";

	public static final String LIFECYCLE_OBSOLESCENCE = "已作废";
	public static final String LIFECYCLE_APPROVE = "已批准";
	public static final String LIFECYCLE_INWORK = "正在工作";
	public static final String LIFECYCLE_MODIFY = "修改中";
	public static final String LIFECYCLE_TECHNICSSTATUS_XIEZUO = "协作编制中";// add by liangbo

	public static final String DATETYPE_CREATE = "CREATE";
	public static final String DATETYPE_APPROVE = "APPROVE";
	public static final String LIFECYCLE_EN_INWORK = "INWORK";
	public static final String LIFECYCLE_EN_APPROVED = "APPROVED";
	public static final String LIFECYCLE_EN_OBSOLESCENCE = "OBSOLESCENCE";

	public static final String ERROR_LIFECYCLE_APPROVE = "已批准，不允许保存！";
	public static final String ERROR_MESSAGE_NOTEXIST = "不存在！";
	public static final String ERROR_LIFECYCLE_INPROCESS = "正在流程签审中，不允许保存！";

	public static final String SOFT_PROCESSSTATUSTABLE = "PROCESSSTATUSTABLE";
	public static final String SOFT_PROCESSECFORM = "PROCESSECFORM";
	public static final String SOFT_INSTANCEPROCESSECFORM = "INSTANCEPROCESSECFORM";
	public static final String SOFT_PROCESSPHASECHANGEECFORM = "PROCESSPHASECHANGEECFORM";
	public static final String OID_TYPE_PROCESSPLAN = "OR:com.ptc.windchill.mpml.processplan.MPMProcessPlan:";

	//用于定版工艺修改
	public static final String PROCESSPLAN_MODIFY_GROUP = "定版工艺修改组";

	public static final String PROCESSPLAN_800GYY_GROUP = "800所工艺员组";
}
