package com.glaway.mpm.constants;

import java.lang.reflect.Field;

public class WorkflowConstants {
	// 流程名称
	public static final String gzCardAuditWorkflowTemplateName = "工装申请卡审核流程";
	public static final String gzDesignAuditorkflowTemplateName = "工装设计审核流程";
	public static final String gyPaiGongTemplateName = "工艺派工流程";
	public static final String SANJIGONGYIQIANSHENLIUCHENG = "三级工艺文件签审流程";
	public static final String SANJIGONGYIGENGGAILIUCHENG = "三级工艺文件更改流程";
	public static final String SANJIBAOBIAOLEIGONGYIQIANSHENLIUCHENG = "三级报表类工艺文件签审流程";
	public static final String WUJIGONGYIQIANSHENLIUCHENG = "五级工艺文件签审流程";
	public static final String GONGYIGENGGAIDANQIANSHENLIUCHENG = "工艺更改单签审流程";
	public static final String WUJIBAOBIAOLEIGONGYIQIANSHENLIUCHENG = "五级报表类工艺文件签审流程";
	public static final String CAILIAODINGELIUCHENG = "材料定额流程";
	public static final String PBOMRELEASEDNOTICE = "PBOM发布通知流程";

	// 流程主物件的变量名称
	public static final String primaryBusinessObject = "primaryBusinessObject";

	// 流程活动名称
	public static final String TechnicInworkRevise = "工艺拟制修订";
	public static final String TechnicShenhe = "工艺审核";
	public static final String TechnicPizhun = "工艺批准";
	public static final String TempTechnicShenhe = "临时工艺审核流程（审核）";
	public static final String TempTechnicBohui = "临时工艺审核流程（驳回）";
	public static final String ReworkTechnicPizhun = "工艺返工流程审核流程（工艺主师批准）";
	public static final String ReworkTechnicShenhe = "工艺返工流程审核流程（科技部计划员审核）";

	public static final String GZCardLiZhi = "工装申请卡审核流程（拟制）";
	public static final String GZCardShenHe = "工装申请卡审核流程（专业组长审核）";
	public static final String GZCardHuiQian = "工装申请卡审核流程（工艺主师会签）";
	public static final String GZCardPiZun = "工装申请卡审核流程（部领导批准）";
	public static final String WAIXIEJISHUXIEYIQIANSHENLIUCHENG="外协技术协议签审流程";
	// 流程路由
	public static final String TongGuo = "通过";
	public static final String BoHui = "驳回";

	public static String getAttributeName(String str) {
		String returnStr = "";
		try {
			Class c = Class.forName(TypeNameConstants.class.getName());
			Field field = c.getField(str);
			returnStr = (String) field.get(c);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnStr;
	}

}
