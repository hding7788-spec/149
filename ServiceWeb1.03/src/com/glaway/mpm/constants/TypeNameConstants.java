package com.glaway.mpm.constants;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import wt.method.RemoteAccess;

public class TypeNameConstants implements RemoteAccess {

	public static final String PART_PROCESSPLAN_TYPE_NAME = "com.ptc.windchill.mpml.processplan.MPMProcessPlan|casc.sast.149.PartProcessPlan";
	public static final String ASSEMBLE_PROCESSPLAN_TYPE_NAME = "com.ptc.windchill.mpml.processplan.MPMProcessPlan|casc.sast.149.AssembleProcessPlan";
	public static final String OPERATION_TYPE_NAME = "com.ptc.windchill.mpml.processplan.operation.MPMOperation|casc.sast.149.Operation";
	public static final String SUBOPERATION_TYPE_NAME = "com.ptc.windchill.mpml.processplan.operation.MPMOperation|casc.sast.149.SubOperation";


	public static final String DJ = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Knife";
	public static final String SB = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment";
	public static final String GZhuang = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Frock";
	public static final String GJ = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Tool";
	public static final String LJ = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Measure";
	public static final String GXMC = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.ProceduceName";
	public static final String GYCYY = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.CommonString";
	public static final String DMSB = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.DIMIANSHEBEI";
	public static final String GWWH = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.MPMWorkMaintain";

	public static final String CSXM = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Parameters";
	public static final String DZQY = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.CustomArea";
	public static final String CZGW = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.OperationJob";
	public static final String ZYLB = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.SpecializedType";
	public static final String CSXMMC = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.ParametersName";
	public static final String WZLB = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.MaterialCategory";
	public static final String CZMC = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.OperationName";


	public static final String YQYB = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Dashboard";
	public static final String BZYQYB = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Dashboard|casc.sast.149.StandardDashboard";
	public static final String FBZYQYB = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Dashboard|casc.sast.149.UnStandardDashboard";

	public static final String GZFloder = "/Default/工装";
	public static final String DMSBFloder = "/Default/地面设备";
    public static final Map<String,String> ResourceMap=new HashMap<String,String>();

	public static final String ZZDW = "com.ptc.windchill.mpml.resource.MPMPlant";
	public static final String GW = "com.ptc.windchill.mpml.resource.MPMWorkCenter";
	public static final String GZhong = "com.ptc.windchill.mpml.resource.MPMSkill";
	public static final String GYFL = "com.ptc.windchill.mpml.resource.MPMProcessMaterial";

	// 设备子类型
	public static final String XianChuangTypeName = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|casc.sast.149.铣床";
	public static final String CheChuangTypeName = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|casc.sast.149.车床";
	public static final String MoChuangTypeName = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|casc.sast.149.磨床";
	public static final String PaoChuangTypeName = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|casc.sast.149.刨床";
	public static final String TangChuangTypeName = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|casc.sast.149.镗床";
	public static final String ZuanChuangTypeName = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|casc.sast.149.钻床";
	public static final String XianQieGeTypeName = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|casc.sast.149.线切割";
	public static final String GunChiJiTypeName = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|casc.sast.149.滚齿机";
	public static final String HanJieSheBeiTypeName = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|casc.sast.149.焊接设备";
	public static final String JiaGongZXTypeName = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|casc.sast.149.加工中心";
	public static final String DianLanBPJTypeName = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|casc.sast.149.电缆剥皮机";
	public static final String BoFengHJTypeName = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|casc.sast.149.波峰焊机";
	public static final String ShuiQingXJTypeName = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|casc.sast.149.水清洗机";
	public static final String ReYaJiTypeName = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|casc.sast.149.热压机";
	public static final String ZhenKongSBTypeName = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|casc.sast.149.真空设备";
	public static final String HongXiangTypeName = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|casc.sast.149.烘箱";
	public static final String BiaoChuSBTypeName = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|casc.sast.149.表处设备";
	public static final String HuaXueCTSCXTypeName = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|casc.sast.149.化学沉铜生产线";
	public static final String LiuZhouSKZCTypeName = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|casc.sast.149.六轴数控钻床";
	public static final String PingXingPGJTypeName = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|casc.sast.149.平行光曝光机";
	public static final String ReYaGuanTypeName = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|casc.sast.149.热压罐";
	public static final String QiTaSBTypeName = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|casc.sast.149.其它设备";

	// 类型名称
	public static final String gzCardTypeName = "casc.sast.149.工装申请卡";
	public static final String gzRootPartTypeName = "casc.sast.149.ToolingProduct";// 工装成品
	public static final String gzToolingPartTypeName = "casc.sast.149.ToolingPart";// 工装自制件

	public static final String PartTypeName = "casc.sast.149.Part";// 自制件
	public static final String ALKPartTypeName = "casc.sast.149.ALKPart";// 科研件
	public static final String changeIssueTypeName = "wt.change2.WTChangeIssue";// 问题报告
	public static final String changeReportTypeName = "wt.change2.WTChangeRequest2";// 变更请求
	public static final String changeNoticeTypeName = "wt.change2.WTChangeOrder2";// 变更通知
	public static final String gzIssuesNoticeTypeName = "casc.sast.149.ToolingIssuesNotice";// 工装问题通知单
	public static final String gzChangeNoticeTypeName = "casc.sast.149.ToolingChangeNotice";// 工装设计更改通知单
	public static final String processChangeDocTypeName = "casc.sast.149.ProcessChangeDoc";// 工艺更改单
	public static final String tempProcessChangeDocTypeName = "casc.sast.149.TempProcessChangeDoc";// 工艺临时更改通知单
	public static final String pbomChangeDocTypeName = "casc.sast.149.PbomChangeDoc";// PBOM更改单
	public static final String processChangeActivityTypeName = "casc.sast.149.ProcessChangeActivity";// 工艺更改任务
	public static final String processChangeNoticeTypeName = "casc.sast.149.ProcessChangeNotice";// 工艺更改通知
	public static final String assembleProcessDocTypeName = "casc.sast.149.装配工艺";
	public static final String partProcessDocTypeName = "casc.sast.149.零件工艺";



	public static final String errorMessage = "主任工艺师无法操作此工艺资源!";

	public static String getTypeName(String str) {
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

	static {
		ResourceMap.put("6201", "/Default/设备/机床类");
		ResourceMap.put("6501", "/Default/设备/工业专用设备");
		ResourceMap.put("6511", "/Default/设备/工业专用设备");
		ResourceMap.put("6513", "/Default/设备/工业专用设备");
		ResourceMap.put("6514", "/Default/设备/航天专用设备");
		ResourceMap.put("6203", "/Default/设备/机械压力机");
		ResourceMap.put("6523", "/Default/设备/木工机床");
		ResourceMap.put("7905", "/Default/设备/其他设备");
		ResourceMap.put("6999", "/Default/设备/其他设备");
		ResourceMap.put("6299", "/Default/设备/其他设备");
		ResourceMap.put("6213", "/Default/设备/其他设备");
		ResourceMap.put("7999", "/Default/设备/其他设备");
		ResourceMap.put("6329", "/Default/设备/通用设备");
		ResourceMap.put("6321", "/Default/设备/通用设备");
		ResourceMap.put("8707", "/Default/仪器仪表/电工仪器仪表");
		ResourceMap.put("8706", "/Default/仪器仪表/电工仪器仪表");
		ResourceMap.put("8731", "/Default/仪器仪表/电子和通信测量仪器");
		ResourceMap.put("8716", "/Default/仪器仪表/分析仪器仪表");
		ResourceMap.put("8711", "/Default/仪器仪表/光学仪器仪表");
		ResourceMap.put("8728", "/Default/仪器仪表/量仪");
		ResourceMap.put("8726", "/Default/仪器仪表/实验室仪器及装置");
		ResourceMap.put("8721", "/Default/仪器仪表/试验机");
		ResourceMap.put("8736", "/Default/仪器仪表/钟表及定时仪器");
		ResourceMap.put("8701", "/Default/仪器仪表/自动化仪器仪表");
		ResourceMap.put("8301", "/Default/仪器仪表/自动化仪器仪表");
		ResourceMap.put("8746", "/Default/非标准仪器仪表/专用仪器仪表");
		ResourceMap.put("8801", "/Default/量具/长度计量标准器具");
		ResourceMap.put("8807", "/Default/量具/电磁学计量标准器具");
		ResourceMap.put("8845", "/Default/量具/衡器");
		ResourceMap.put("8805", "/Default/量具/力学计量标准器具");
		ResourceMap.put("8803", "/Default/量具/热学计量标准器具");
	}

}
