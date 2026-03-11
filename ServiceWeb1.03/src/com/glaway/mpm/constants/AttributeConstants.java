package com.glaway.mpm.constants;

import java.lang.reflect.Field;

public class AttributeConstants {
	/**
	 * 通用的属性
	 */
	public static final String number = "number";
	public static final String name = "name";
	public static final String state = "state";

	/**
	 * PBOM 属性
	 */
	public static final String iskey = "iskey";// 关键件
	// public static final String isSpecial = "isSpecial";//特殊件
	// public static final String workShop = "workShop";//主制单位
	// public static final String remark = "remark";//备注
	// public static final String materialType = "materialType";//物料类型
	// public static final String backupRate = "backupRate";//备份比例
	// public static final String maxBackupCount = "maxBackupCount";//最大备份数
	// public static final String backupReason = "backupReason";//备份原因
	public static final String outsourcingUnits = "outsourcingUnits";// 建议外协单位
	// 工艺辅件
	public static final String productionRatio = "productionRatio";// 投产比例
	public static final String productionQuantity = "productionQuantity";// 投产数量
	/**
	 * 工装申请卡属性
	 */
	public static final String productNumber = "productNumber";// 产品代号
	public static final String insertPart = "insertPart";// 镶件
	public static final String productionNumber = "productionNumber";// 投产付数
	public static final String isReview = "isReview";// 是否评审
	public static final String isCommonTools = "isCommonTools";// 共用工装
	public static final String isTestPart = "isTestPart";// 试模件
	public static final String partNumber = "partNumber";// 零件编号
	public static final String wholePartNumber = "wholePartNumber";// 整件编号
	public static final String workShop = "workShop";// 主制单位
	public static final String toolingRequirements = "toolingRequirements";// 工装设计要求
	public static final String isRegularlyTools = "isRegularlyTools";// 常用工装
	public static final String partOid = "partOid";// 零件Oid

	/**
	 * 工装资源属性
	 */
	// public static final String productNumber = "productNumber";// 产品代号
	// public static final String partNumber = "partNumber";// 零件编号
	public static final String usageOrg = "usageOrg"; // 适用工艺专业
	public static final String applicant = "applicant"; // 工装卡申请人
	public static final String applyDate = "applyDate"; // 工装卡申请日期
	public static final String stock = "stock"; // 工装库存
	public static final String gzCardNumber = "gzCardNumber"; // 工装申请卡编号
	// public static final String isRegularlyTools = "isRegularlyTools"; // 常用工装
	// public static final String workShop = "workShop";// 主制单位

	/**
	 * 工艺辅料属性
	 */
	public static final String materialNumber = "materialNumber";// 材料编码
	public static final String materialBrand = "materialBrand";// 材料牌号
	public static final String materialCrision = "materialCrision";// 材料标准号
	public static final String materialCategory = "materialCategory";// 材料类型
	public static final String materialDensity = "materialDensity";// 材料密度
	public static final String attritionRate = "attritionRate";// 材料损耗系数
	public static final String materialUnit = "materialUnit";// 定额单位
	public static final String materialSpec = "materialSpec";// 材料规格

	/**
	 * 零件工艺，装配工艺属性
	 */
	// public static final String productNumber = "productNumber";// 产品编号
	public static final String productName = "productName";// 产品名称
	public static final String parentPartNumber = "parentPartNumber";// 整件图号
	public static final String partName = "partName";// 零件名称
	// public static final String partNumber = "partNumber";// 零件图号
	public static final String backupReason = "backupReason";// 备份原因
	public static final String maxBackupCount = "maxBackupCount";// 最大备份数
	public static final String backupRate = "backupRate";// 工艺备份比例
	public static final String materialType = "materialType";// 物料类型
	public static final String isSpecial = "isSpecial";// 特殊件
	public static final String isKey = "isKey";// 关键件
	public static final String isRework = "isRework";// 是否返工
	// public static final String workShop = "workShop";// 主制单位
	public static final String remark = "remark";// 备注
	public static final String onBuildNumber = "onBuildNumber";// 在制令号

	/**
	 *
	 */
	public static final String workcenter = "workcenter";// 工作中心
	/**
	 * 设备属性
	 */
	public static final String equipmentNumber = "equipmentNumber";// 设备编号
	public static final String modelNumber = "modelNumber";// 型号
	public static final String equipmentAmount = "equipmentAmount";// 设备数量
	public static final String equipmentVender = "equipmentVender";// 设备厂家
	// public static final String remark = "remark";
	public static final String stagingSize = "stagingSize";// 工作台尺寸
	public static final String TSize = "TSize";// T形槽尺寸
	public static final String stagingDistance = "stagingDistance";// 主轴至工作台距离
	public static final String crutchDistance = "crutchDistance";//
	public static final String swivellingAngle = "swivellingAngle";//
	public static final String stagingRunning = "stagingRunning";//
	public static final String speed = "speed";//
	public static final String workPrecision = "workPrecision";//
	public static final String workingScope = "workingScope";//
	public static final String knifeMove = "knifeMove";//
	public static final String centreSpacing = "centreSpacing";//
	public static final String chiefAxis = "chiefAxis";//
	public static final String ramRunning = "ramRunning";//
	public static final String stretchSize = "stretchSize";//
	public static final String diameter = "diameter";//
	public static final String flowerDisc = "flowerDisc";//
	public static final String baseDistance = "baseDistance";//
	public static final String axisRunning = "axisRunning";//
	public static final String skillParameter = "skillParameter";//
	public static final String thickness = "thickness";//
	public static final String taper = "taper";//
	public static final String axisAmount = "axisAmount";//
	public static final String toothWidth = "toothWidth";//
	public static final String maxHelixAngle = "maxHelixAngle";//
	public static final String toothAmount = "toothAmount";//

	// 工序工步属性
	// public static final String isKey = "isKey";// 检验工步
	public static final String description = "description";// 内容描述
	// public static final String workShop = "workShop";// 主制部门
	public static final String workType = "workType";// 工种
	public static final String workSpace = "workSpace";// 工位
	public static final String PrepareWorkHours = "PrepareWorkHours";// 准备工时
	public static final String TaktTime = "TaktTime";// 单间工时
	public static final String NumberOfGroup = "NumberOfGroup";// 每组数量（个）

	// 工艺文件临时更改通知单
	public static final String changeReason = "changeReason";// 更改原因
	// public static final String productNumber="productNumber";//产品代号
	// public static final String parentPartNumber="parentPartNumber";//整件编号
	// public static final String partName="partName";//零件名称
	// public static final String partNumber="partNumber";//零件编号
	public static final String isRealChange = "isRealChange";// 是否正式更改
	public static final String changeContent = "changeContent";// 更改内容

	// 工艺更改单
	// public static final String changeReason = "changeReason";
	// public static final String changeContent = "changeContent";
	public static final String isChangeKeySeq = "isChangeKeySeq";// 原材料变动
	public static final String isChangeMaterial = "isChangeMaterial";// 关键工序变动
	public static final String changeTask = "changeTask";// 变更任务

	// 工序工步与资源的链接
	public static final String materialQuota = "materialQuota";// 材料定额
	public static final String useSize = "useSize";// 下料尺寸
	public static final String singleSize = "singleSize";// 单件毛坯尺寸
	public static final String partUnit = "partUnit";// 零件数量

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
