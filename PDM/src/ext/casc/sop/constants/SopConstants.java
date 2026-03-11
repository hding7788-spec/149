package ext.casc.sop.constants;

public class SopConstants {


    /**SOP体系BOM*/
    public static String SOP_TYPE_SOPPART = "casc.sast.149.SOPPart";
    /**SOP标准操作规程-doc*/
    public static String SOP_TYPE_SOPDOC = "casc.sast.149.SOPDoc";
    /**SOP标准操作规程-mpmprocessplan*/
    public static String SOP_TYPE_SOPPROCESSPLAN = "casc.sast.149.SOPProcessPlan";
    /**SOP标准操作规程-mpmprocessplan full*/
    public static String SOP_TYPE_SOPPROCESSPLAN_FULL = "com.ptc.windchill.mpml.processplan.MPMProcessPlan|casc.sast.149.SOPProcessPlan";
    /**MPMTooling*/
    public static String SOP_TYPE_MPMTOOLING = "com.ptc.windchill.mpml.resource.MPMTooling";
    /**专业类别*/
    public static String SOP_TYPE_SPECIALIZEDTYPE = "casc.sast.149.SpecializedType";
    /**工序名称*/
    public static String SOP_TYPE_PROCEDUCENAME = "casc.sast.149.ProceduceName";
    /**参数项目名称*/
    public static String SOP_TYPE_PARAMETERSNAME = "casc.sast.149.ParametersName";
    /**参数项目类型*/
    public static String SOP_TYPE_PARAMETERS = "casc.sast.149.Parameters";
    /**定制区域*/
    public static String SOP_TYPE_CUSTOMAREA = "casc.sast.149.CustomArea";
    /**操作岗位*/
    public static String SOP_TYPE_OPERATIONJOB = "casc.sast.149.OperationJob";
    /**物资类别*/
    public static String SOP_TYPE_MATERIALCATEGORY = "casc.sast.149.MaterialCategory";
    /**操作名称*/
    public static String SOP_TYPE_OPERATIONNAME = "casc.sast.149.OperationName";
    /**国家标准*/
    public static String SOP_TYPE_GUOJIABIAOZHUN = "casc.sast.149.GUOJIABIAOZHUN";

    public static String SOP_WORKFLOW_SOPPROCESS = "SOP工艺文件签审流程";
    /**工艺知识库*/
    public static String SOP_CONTAINER_GYZSK = "工艺知识库";
    /**工艺资源库*/
    public static String SOP_CONTAINER_GYZYK = "工艺资源库";

    /**SOP体系BOM存储路径*/
    public static String SOP_FOLDOR_BOM = "/Default/SOP体系BOM/";
    /**SOP工艺文件存储路径*/
    public static String SOP_FOLDOR_PROCESS = "/Default/SOP规程/";
    /**参数项目名称*/
    public static String SOP_FOLDOR_PARAMETERSNAME = "/Default/SOP资源/参数项目名称";
    /**参数项目名称*/
    public static String SOP_FOLDOR_PARAMETERS = "/Default/SOP资源/工艺参数项目";
    /**参数项目名称*/
    public static String SOP_FOLDOR_SPECIALIZEDTYPE = "/Default/SOP资源/专业类别";
    /**参数项目名称*/
    public static String SOP_FOLDOR_PROCEDUCENAME = "/Default/SOP资源/工序名称";
    /**参数项目名称*/
    public static String SOP_FOLDOR_MATERIALCATEGORY = "/Default/SOP资源/物资类别";
    /**参数项目名称*/
    public static String SOP_FOLDOR_CUSTOMAREA = "/Default/SOP资源/定制区域";
    /**参数项目名称*/
    public static String SOP_FOLDOR_OPERATIONJOB = "/Default/SOP资源/操作岗位";
    /**参数项目名称*/
    public static String SOP_FOLDOR_OPERATIONNAME = "/Default/SOP资源/操作名称";
    /**编号*/
    public static String SOP_ATTR_NUMBER = "number";
    /**名称*/
    public static String SOP_ATTR_NAME = "name";
    /**说明*/
    public static String SOP_ATTR_REMARK = "REMARK";
    /**备注*/
	public static String SOP_IBA_REMARK = "REMARK";
	/**主制车间*/
	public static String SOP_IBA_ZZCJ = "ZZCJ";
	/**英文名*/
	public static String SOP_IBA_ENGLISHNAME = "EnglishName";
    /**密级*/
    public static String SOP_IBA_SECRET = "SECRET";
    /**期限*/
    public static String SOP_IBA_TERM = "Term";
    /**专业类别*/
    public static String SOP_IBA_SPECIALIZEDTYPE = "SpecializedType";
    /**工序名称*/
    public static String SOP_IBA_PROCEDUCENAME = "ProceduceName";
    /**专业代号*/
    public static String SOP_IBA_PROFESSIONALCODE = "ProfessionalCode";
    /**工序简号*/
    public static String SOP_IBA_GONGXUJIANHAO = "GONGXUJIANHAO";
    /**部门*/
    public static String SOP_IBA_DEPARTMENT = "Department";
    /**参数项目名称*/
    public static String SOP_IBA_PARAMETERSNAME = "ParametersName";
    /**参数值*/
    public static String SOP_IBA_CANSHUZHI = "CANSHUZHI";
    /**物资类别*/
	public static String SOP_IBA_MATERIALCATEGORY = "MaterialCategory";
    /**操作名称*/
  	public static String SOP_IBA_OPERATIONNAME = "OperationName";
	/**操作岗位*/
    public static String SOP_IBA_OPERATIONJOB = "OperationJob";
    /**定制区域*/
    public static String SOP_IBA_CUSTOMAREA = "CustomArea";
    /**参数项目*/
	public static String SOP_IBA_PARAMETERS = "Parameters";
	/**SOP文件编号*/
	public static String SOP_IBA_SOPNUMBER = "SopNumber";

    /**SOP设计任务分工*/
    public static String SOP_TASK_TASKTYPE= "SOP文件设计任务";
    /**SOP设计任务分工*/
    public static String SOP_TASK_TASKTYPE_CHANGE= "SOP更改设计任务";
    /**SOP工艺任务*/
    public static String SOP_TASK_TASKITEMNAME= "SOP编制任务";
    /**SOP工艺任务指派*/
    public static String SOP_TASK_ZP= "SOP编制任务指派";
    /**SOP工艺更改任务*/
    public static String SOP_TASK_CHANGETASKITEMNAME= "SOP工艺更改任务";
    /**SOP工艺更改任务指派*/
    public static String SOP_TASK_CHANGETASKZP= "SOP工艺更改任务指派";
    /**SOP工艺任务_拒绝*/
    public static String SOP_TASKITEMNAME_REFUSED= "SOP工艺任务_拒绝";
    /**日期校验提示信息*/
    public static String SOP_MSG_DATAINFO= "请选择今天以后的日期";
    /**任务驳回失败*/
    public static String SOP_MSG_REJECT_FAIL= "任务驳回失败!";
    /**任务驳回成功*/
    public static String SOP_MSG_REJECT_SUCCESS= "任务驳回成功!";
    /**任务作废成功*/
    public static String SOP_MSG_DELETE_SUCCESS= "任务作废成功!";
    /**任务作废失败*/
    public static String SOP_MSG_DELETE_FAIL= "任务作废失败!";
    /**任务已完成*/
    public static String SOP_MSG_COMPLETE_SUCCESS= "任务已完成!";
    /**完成任务失败*/
    public static String SOP_MSG_COMPLETE_FAIL= "完成任务失败!";
    /**新建SOP部件*/
    public static String SOP_MSG_CREATESOPPART= "新建SOP零部件";
    /**公开*/
	public static String SOP_MSG_GONGKAI = "公开";
	/**商密*/
	public static String SOP_MSG_SHANGMI = "商密";
	/**内部*/
	public static String SOP_MSG_NEIBU = "内部";
	/**秘密*/
	public static String SOP_MSG_MIMI = "秘密";
	/**机密*/
	public static String SOP_MSG_JIMI = "机密";
    /**SOP资源导入*/
    public static String SOP_MSG_SOPRESOURCEIMPORT= "SOP资源导入!";
    public static String SOP_MSG_SOPRESOURCEIMPORT_1= "在执行导入前，请选择相应的SOP资源文件：!";
    public static String SOP_MSG_SOPRESOURCEIMPORT_2= "*请选择EXCEL表文件：";

    /**角色-标准化师*/
    public static String SOP_ROLE_BZHS = "标准化师";
    /**角色-工艺员*/
    public static String SOP_ROLE_GYY = "工艺员";
    /**PBOM-VIEW*/
    public static String PBOM_VIEW = "Manufacturing";

    public static String SOP_STR_GXMC = "GXMC";
    public static String SOP_STR_CSXM = "CSXM";
    public static String SOP_STR_CZGW = "CZGW";
    public static String SOP_STR_ZYLB = "ZYLB";
    public static String SOP_STR_CSXMMC = "CSXMMC";
    public static String SOP_STR_WZLB = "WZLB";
    public static String SOP_STR_DZQY = "DZQY";
    public static String SOP_STR_CZMC = "CZMC";

    public static String JSP_SEARCH_TECHNICSNUMBER = "工艺文件编号";
    public static String JSP_SEARCH_TECHNICSNAME = "工艺文件名称";
    public static String JSP_SEARCH_SOPTECHNICSNUMBER = "SOP文件编号";
    public static String JSP_SEARCH_SOPTECHNICSNAME = "SOP文件名称";
    public static String JSP_SEARCH_TECHNICSTYPE = "工艺类型";
    public static String JSP_SEARCH_GISTTECHNICS = "专用工艺";
    public static String JSP_SEARCH_SOPTECHNICS = "SOP工艺";
    public static String JSP_SEARCH_GISTNUMBER = "依据文件编号";
    public static String JSP_SEARCH_GISTNAME = "依据文件名称";
}
