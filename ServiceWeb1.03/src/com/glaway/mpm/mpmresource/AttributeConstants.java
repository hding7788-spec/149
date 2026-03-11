package com.glaway.mpm.mpmresource;

import java.lang.reflect.Field;

public class AttributeConstants {
	/**
	 * 通用的属性
	 */
	public static final String number = "number";
	public static final String name = "name";
	public static final String state = "state";
	public static final String zhizaodanwei = "zhizaodanwei";
	public static final String des = "description";
	public static final String remarkKey = "REMARK";
	public static final String ZZCJ = "ZZCJ";
	public static final String SFSBGS = "IsSheBeiGS"; //是否设备工时
	public static final String ENGLISHNAME = "EnglishName";

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
	//812 材料属性
	public static final String  MATERIAL_CLPH= "CLPH";// 材料牌号
	public static final String MATERIAL_CLGG = "CLGG";// 材料规格
	public static final String MATERIAL_CLBZ = "CLBZ ";//CLBZ
	public static final String MATERIAL_JLDW = "JLDW";// JLDW


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
	public static final String isSpecial = "isSpecial";// 特殊键
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
	public static final String pindex = "PINDEX";// 型号
	public static final String equipmentType = "EQUIPMENTTYPE";// 设备类别
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
	public static final String typeno = "TYPENO";//
	public static final String level = "LEVEL";//

    public static final String enableddate = "ENABLEDDATE";//
    public static final String lateststatus = "LATESTSTATUS";//
    public static final String applyno = "APPLYNO";//
    public static final String applyname = "APPLYNAME";//
    public static final String applydate = "APPLYDATE";//
    public static final String qty = "QTY";//

    public static final String mindex = "MINDEX";//
    public static final String csize = "CSIZE";//
    public static final String factory = "FACTORY";//
    public static final String resource = "RESOURCE";//
    public static final String manuno = "MANUNO";//
    public static final String managestatus = "MANAGESTATUS";//
    public static final String qualitystatus = "QUALITYSTATUS";//
    public static final String managelevel = "MANAGELEVEL";//

    //新增刀具IBA属性
    public static final String cmat = "CMAT";//
    public static final String rkzj = "RKZJ";//
    public static final String jczj = "JCZJ";//
    public static final String rkcd = "RKCD";//
    public static final String zcd = "ZCD";//
    public static final String gc = "GC";//
    public static final String zxjgcc = "ZXJGCC";//
    public static final String zdjgcc = "ZDJGCC";//
    public static final String jgxs = "JGXS";//
    public static final String jklx = "JKLX";//
    public static final String jsbz = "JSBZ";//
    public static final String rkyjbj = "RKYJBJ";//
    public static final String cs = "CS";//

    //工艺辅料IBA属性
    public static final String jstj = "JSTJ";//
    public static final String fjtj = "FJTJ";//

    public static final String clph = "CLPH";//
    public static final String clgg = "CLGG";//
    public static final String clbz = "CLBZ";//
    public static final String jldw = "JLDW";//
    public static final String frocktype = "FROCKTYPE";// 工装类别
    public static final String knifetype = "KNIFETYPE";// 刀具类别
    public static final String workplace = "WORKPLACE";// 工位位置

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

	// 工序工步与资源的链接
	public static final String materialQuota = "materialQuota";// 材料定额
	public static final String useSize = "useSize";// 下料尺寸
	public static final String singleSize = "singleSize";// 单件毛坯尺寸
	public static final String partUnit = "partUnit";// 零件数量

	//工序参数项目关联属性        参数项目名称   物资类别
	public static final String SpecializedType = "SpecializedType";// 专业类别
	public static final String ProceduceName = "ProceduceName";// 工序名称
	public static final String CANSHUZHI = "CANSHUZHI";// 参数值
	public static final String ParametersName = "ParametersName";// 参数项目名称
	public static final String MaterialCategory = "MaterialCategory";// 物资类别
	//专业类别
	public static final String ProfessionalCode = "ProfessionalCode"; //专业代号
	//工艺名称
	public static final String GONGXUJIANHAO = "GONGXUJIANHAO"; //工序简号





	public static String getAttributeName(String str) {
		String returnStr = "";
		try {
			Class c = Class.forName(TypeNameConstants.class.getName());
			Field field = c.getField(str);
			returnStr = (String) field.get(c);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return returnStr;
	}

}
