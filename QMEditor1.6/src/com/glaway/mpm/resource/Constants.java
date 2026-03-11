package com.glaway.mpm.resource;

public class Constants {

	// 工艺
	public static final String ROOT_TECH = "/technics";
	// 工艺信息
	public static final String TECH_INFO_PATH = "/technics/MFawTechnicsInfo";
	// 工序
	public static final String STEPS = "/steps";
	// 工序信息
	public static final String STEPS_INFO_PATH = "/steps/QMProcedureInfo";
	// 部件
	public static final String PARTS = "/parts";
	// 部件信息
	public static final String PARTS_INFO_PATH = "/parts/QMPartInfo";
	// 材料
	public static final String MATERIALS = "/materials";
	public static final String MATERIALS_INFO_PATH = "/materials/QMMaterialInfo";
	// 工步
	public static final String PACES = "/paces";
	// 工步信息
	public static final String PACES_INFO_PATH = "/paces/QMProcedureInfo";
	// 工装
	public static final String TOOLS = "/tools";
	public static final String TOOLS_INFO_PATH = "/tools/QMToolInfo";
	// 设备
	public static final String EQUIPS = "/equips";
	public static final String EQUIPS_INFO_PATH = "/equips/QMEquipmentInfo";
	// 简图
	public static final String IMAGES = "/images";
	public static final String IMAGES_INFO_PATH = "/images/DrawingInfo";
	// 模板路径
	public static final String FM_TEMPLATES_PATH = "/templates";
	// 生成路径
	public static final String FM_TEMPLATE_CREO_HTML = "templateCreo.html";
	public static final String FM_TEMPLATE_CORTONA_HTML = "templateCortona.html";

	public static final String FM_TEMPLATE_CREO_JS = "treegrid_Creo";
	public static final String FM_TEMPLATE_CORTONA_JS = "treegrid_Cortona";
	public static final String FM_TEMPLATE_JS = "treegrid.js";

	public static final String FM_BUILD_CREO_HTML = "Creo.html";
	public static final String FM_BUILD_CORTONA_HTML = "Cortona.html";

	public static String USER_HOME = System.getProperty("user.home");

	public static String PUBLISH_HTML = "";

	/**
	 * common
	 */
	public static final String STAR = "*";
	public static final String SURE = "确定";
	public static final String CANCEL = "取消";

	/**
	 * 新增工艺辅件
	 */
	public static final String ADD_ASSIST_PART = "新增工艺辅件";
	public static final String ASSIST_PART_NUMBER = "工艺辅件编号";
	public static final String ASSIST_PART_NAME = "工艺辅件名称";

	/**
	 * commonString
	 */
	public static final String PERSONAL_CS_MAINTAIN = "个人工艺常用语库维护";
	public static final String PERSONAL_CS_FILENAME = "personalTechnicsCs.xml";
	public static final String SELECT_ADD_CSTYPE = "请选择需要新增的常用语类型";
	public static final String TIP = "提示";
	public static final String PERSONAL_CS = "个人工艺常用语";
	public static final String PUBLIC_CS = "个人工艺常用语";
	public static final String CS = "常用语";
	public static final String LEFT_QUOTA = "“";
	public static final String RIGHT_QUOTA = "”";
	public static final String ADD_SUCCESS = "新增成功";
	public static final String SELECT_DELETE_CSTYPE = "请选择需要移除的常用语";

}
