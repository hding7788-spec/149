package com.glaway.mpm.print.constants;

public class PrintServerConstants {

	public static final String FILETYPE_GYWJML = "工艺文件目录";
	public static final String FILETYPE_GYGC = "工艺规程";
	public static final String FILETYPE_ZSGY = "正式工艺";
	public static final String FILETYPE_TYGY = "通用工艺";
	public static final String FILETYPE_YHTYGY = "宇航通用工艺";
	public static final String FILETYPE_ZSTYGY = "战术通用工艺";
	public static final String FILETYPE_GYZTB = "工艺状态表";
	public static final String FILETYPE_DXGY = "典型工艺";
	public static final String FILETYPE_SLHGY = "实例化工艺";
	public static final String FILETYPE_LSGY = "临时工艺";
	public static final String FILETYPE_GYGGD = "工艺更改单";
	public static final String FILETYPE_GYZFA = "工艺总方案";
	public static final String FILETYPE_FYZFA = "工艺分方案";
	public static final String FILETYPE_QT = "其他";
	public static final String FILETYPE_GYJSTZD = "工艺技术通知单";
	public static final String FILETYPE_GYJSXY = "工艺技术协议";
	public static final String FILETYPE_GYTZD = "工艺通知单";
	public static final String FILETYPE_QTBG = "其它报告";
	public static final String FILETYPE_BZHDG = "标准化大纲";
	public static final String FILETYPE_BZHSCBG = "标准化审查报告";
	public static final String FILETYPE_BZHZHYQ = "标准化综合要求";
	public static final String FILETYPE_SOPGYGC = "SOP标准操作规程";

	public static final String PRINTSTATUS_WDY = "未打印";
	public static final String PRINTSTATUS_YDY = "已打印";
	public static final String PRINTSTATUS_YXF = "已下发";

	public static final String FILESTATUS_WFF = "未分发";
	public static final String FILESTATUS_YFF = "已分发";
	public static final String FILESTATUS_FFZ = "分发中";

	public static final String ISBLUECARD = "true";
	public static final String NOTBLUECARD = "false";
	public static final String REJECTSTATUS_WBH = "未驳回";
	public static final String REJECTSTATUS_YBH = "已驳回";
	public static final String TABLETYPE_DYSQBH = "打印申请驳回";
	//编码标识
	public static final String NUMBER_OBJTYPE_PRINTAPPLY = "PRINTAPPLYRECORD";
	public static final String NUMBER_OBJTYPE_PRINTAPPLY_PREFIX = "A";
	public static final String NUMBER_OBJTYPE_PRINTDISTRIBUTE = "PRINTDISTRIBUTERECORD";
	public static final String NUMBER_OBJTYPE_PRINTDISTRIBUTE_PREFIX = "D";
	public static final String NUMBER_OBJTYPE_PRINTPRINTRECOVER = "PRINTRECOVERRECORD";
	public static final String NUMBER_OBJTYPE_PRINTPRINTRECOVER_PREFIX = "R";
	public static final String SOFTTYPE_PROCESSPRINTDOC = "PROCESSPRINTDOC";

	public static final String WORKFLOWNAME_PRINTAPPLY = "工艺文件打印申请签审流程";
	public static final String WORKFLOWNAME_RECOVERAPPLY = "工艺文件打印回收（销毁）签审流程";

	//打印流程
	public static final String WORKFLOWNAME_PRINTOFFSET = "打印申请补打流程";

	public static final String OID_MPMPROCESSPLAN = "OR:com.ptc.windchill.mpml.processplan.MPMProcessPlan:";
	public static final String OID_WTCHANGEORDER2 = "OR:wt.change2.WTChangeOrder2:";
	public static final String OID_WTDOCUMENT = "OR:wt.doc.WTDocument:";
	public static final String OID_WTGROUP = "wt.org.WTGroup:";
	public static final String OID_WTUSER = "wt.org.WTUser:";

	//临时文件夹结构
	public static final String FOLDER_PRINT = "print";
	public static final String FOLDER_SIGNPDF = "signpdf";
	public static final String FOLDER_PRINT_QRCODE = "QRCode";
	public static final String FOLDER_PRINT_PDF = "pdf";
	public static final String FOLDER_PRINT_IMAGE = "image";

	public static final String JPG = ".jpg";

	public static final String PROCESSPRINTDOC_LOCATION = "Default/02工艺文件/90其他文档";
	public static final String OBJTYPE_PROCESSPRINTDOC = "casc.sast.800.PROCESSPRINTDOC";
	public static final String OBJTYPE_PROCESSPRINTDOC_DISPLAY = "文件打印单";
	public static final String OBJTYPE_PROCESSPRINTDOC_RECOVER_DISPLAY = "文件退回申请单";
	public static final String GROUP_ZLY_XXDAC = "资料员_信息档案处";
	public static final String GROUP_XXDAC = "信息档案处";

	public static final String SEAL_SHIYAN = "试验";
	public static final String SEAL_FANXIU = "返(工)修";

	public static final String QRCODEMANAGE_TITLE = "人员二维码信息维护";
	public static final String QRCODEMANAGE_SEARCH_TITLE = "用户查询";
	public static final String QRCODEMANAGE_LABEL_NAME = "用户名：";
	public static final String QRCODEMANAGE_LABEL_FULLNAME = "全名：";
	public static final String QRCODEMANAGE_BUTTON_SEARCH = "查询";
	public static final String QRCODEMANAGE_BUTTON_SAVE = "保存";

	public static final String TRANSFERAPPLY_LOCATION = "Default/转移申请单";
	public static final String OBJTYPE_TRANSFERAPPLY = "casc.sast.149.TRANSFERAPPLYRECORD";
	public static final String WORKFLOWNAME_TRANSFERAPPLY = "工艺文件转移流程";

}
