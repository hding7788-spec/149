package com.glaway.mpm.mpmresource;

import java.lang.reflect.Field;

import wt.method.RemoteAccess;

public class TypeNameConstants implements RemoteAccess {


	public static final String PART_PROCESSPLAN_TYPE_NAME = "com.ptc.windchill.mpml.processplan.MPMProcessPlan|com.nriet.PartProcessPlan";
	public static final String ASSEMBLE_PROCESSPLAN_TYPE_NAME = "com.ptc.windchill.mpml.processplan.MPMProcessPlan|com.nriet.AssembleProcessPlan";
	public static final String OPERATION_TYPE_NAME = "com.ptc.windchill.mpml.processplan.operation.MPMOperation|casc.sast.149.Operation";
	public static final String SUBOPERATION_TYPE_NAME = "com.ptc.windchill.mpml.processplan.operation.MPMOperation|casc.sast.149.SubOperation";

	// 资源类型
	public static final String DJ = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Knife";
	public static final String SB = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment";
	public static final String GZhuang = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Frock";
	public static final String SpecGZhuang = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Frock|casc.sast.149.SpecFrog";
	public static final String CommonGZhuang = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Frock|casc.sast.149.CommonFrog";
	public static final String GZCommonFrog = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Frock|casc.sast.149.CommonFrog";
	public static final String GZSpecFrog = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Frock|casc.sast.149.SpecFrog";
	public static final String GJ = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Tool";
	public static final String GXMC = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.ProceduceName";
	public static final String GYCYY = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.CommonString";
	public static final String DMSB = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.DIMIANSHEBEI";
	//新增sop资源类型
	public static final String CSXM = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Parameters";
	public static final String DZQY = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.CustomArea";
	public static final String CZGW = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.OperationJob";
	public static final String ZYLB = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.SpecializedType";
	public static final String CSXMMC = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.ParametersName";
	public static final String WZLB = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.MaterialCategory";


	public static final String ZZDW = "com.ptc.windchill.mpml.resource.MPMPlant";
	public static final String GW = "com.ptc.windchill.mpml.resource.MPMWorkCenter";
	public static final String GZhong = "com.ptc.windchill.mpml.resource.MPMSkill";
	public static final String GYFL = "com.ptc.windchill.mpml.resource.MPMProcessMaterial";
	public static final String LJ = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Measure";
	public static final String YQYB = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Dashboard";

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
	public static final String ReYaGuanTypeName = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|com.nriet.热压罐";
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
	public static final String gzChangeNoticeTypeName = "com.nriet.ToolingChangeNotice";// 工装设计更改通知单
	public static final String processChangeDocTypeName = "com.nriet.ProcessChangeDoc";// 工艺更改单
	public static final String tempProcessChangeDocTypeName = "com.nriet.TempProcessChangeDoc";// 工艺临时更改通知单
	public static final String pbomChangeDocTypeName = "casc.sast.149.PbomChangeDoc";// PBOM更改单
	public static final String processChangeActivityTypeName = "casc.sast.149.ProcessChangeActivity";// 工艺更改任务
	public static final String processChangeNoticeTypeName = "casc.sast.149.ProcessChangeNotice";// 工艺更改通知
//	public static final String assembleProcessDocTypeName = "casc.sast.149.装配工艺";
//	public static final String partProcessDocTypeName = "casc.sast.149.零件工艺";
	public static final String pbomDocTypeName = "casc.sast.149.PBOM";
	public static final String partProcessDocTypeName = "casc.sast.149.零件工艺";



	public static String getTypeName(String str) {
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
