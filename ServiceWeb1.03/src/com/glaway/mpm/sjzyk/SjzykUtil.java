package com.glaway.mpm.sjzyk;

import java.rmi.RemoteException;

import wt.part.WTPart;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;

import com.glaway.mpm.util.IBAHelper;

public class SjzykUtil {

	public static SjzykBean createBeanByPart(WTPart part) throws WTException, RemoteException {
		SjzykBean bean = new SjzykBean();
		bean.setNumber(part.getNumber());
		bean.setName(part.getName());
		IBAHelper ibaHelper = new IBAHelper(part);
		String mtype = object2String(ibaHelper.getIBAValue("MTYPE"));
		if("标准件".equals(mtype)) {
			bean.setWzjc(object2String(ibaHelper.getIBAValue("SHORTNAME")));//物资简称
			bean.setBzh(object2String(ibaHelper.getIBAValue("STANDARDNUMBER")));//标准号
			bean.setGg(object2String(ibaHelper.getIBAValue("STANDARD")));//规格
			bean.setCl(object2String(ibaHelper.getIBAValue("MATERIAL")));//材料
			bean.setJxxndjhyd(object2String(ibaHelper.getIBAValue("MECHANICALPROPERTYORHARDNESS")));//机械性能等级或硬度
			bean.setBmcl(object2String(ibaHelper.getIBAValue("SURFACETREATMENT")));//表面处理
			bean.setRcl(object2String(ibaHelper.getIBAValue("HEATTREATMENT")));//热处理
			bean.setCpxs(object2String(ibaHelper.getIBAValue("PRODUCTFORM")));//产品型式
			bean.setCpdj(object2String(ibaHelper.getIBAValue("PRODUCTLEVEL")));//产品等级
			bean.setBnxs(object2String(ibaHelper.getIBAValue("PLATECSCREWFORM")));//板拧形式
			bean.setSfjk(object2String(ibaHelper.getIBAValue("ISIMPORT")));//是否进口
			bean.setTssm(object2String(ibaHelper.getIBAValue("SPECIALINSTRUCTION")));//特殊说明
			bean.setJldw(object2String(ibaHelper.getIBAValue("MEASUREUNIT")));//计量单位
		} else if ("元器件".equals(mtype)) {
			bean.setWzjc(object2String(ibaHelper.getIBAValue("SHORTNAME")));//物资简称
			bean.setXh(object2String(ibaHelper.getIBAValue("TYPE")));//型号
			bean.setXhgg(object2String(ibaHelper.getIBAValue("TYPESTANDARD")));//型号规格
			bean.setZldj(object2String(ibaHelper.getIBAValue("QUALITYLEVEL")));//质量等级
			bean.setZgf(object2String(ibaHelper.getIBAValue("TOTALSTANDARD")));//总规范
			bean.setXxgf(object2String(ibaHelper.getIBAValue("DETAILSTANDARD")));//详细规范
			bean.setFzxs(object2String(ibaHelper.getIBAValue("PACKAGINGFORM")));//封装形式
			bean.setWxcc(object2String(ibaHelper.getIBAValue("OUTLINESIZE")));//外形尺寸
			bean.setZytj(object2String(ibaHelper.getIBAValue("SPECIALCONDITION")));//专用条件
			bean.setFjxy(object2String(ibaHelper.getIBAValue("EXTRACONDITION")));//附加协议
			bean.setSfjk(object2String(ibaHelper.getIBAValue("ISIMPORT")));//是否进口
			bean.setTssm(object2String(ibaHelper.getIBAValue("SPECIALINSTRUCTION")));//特殊说明
			bean.setJldw(object2String(ibaHelper.getIBAValue("MEASUREUNIT")));//计量单位
		} else {
			String softType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(part);
			if(softType.contains("JSCLPart")) {//金属材料
				bean.setWzjc(object2String(ibaHelper.getIBAValue("SHORTNAME")));//物资简称
				bean.setCllx(mtype);//材料类型
				bean.setHsl(object2String(ibaHelper.getIBAValue("RATEOFCONVERSION")));//换算率
				bean.setXs(object2String(ibaHelper.getIBAValue("RATIO")));//系数
				bean.setPh(object2String(ibaHelper.getIBAValue("MARKNUMBER")));//牌号
				bean.setGg(object2String(ibaHelper.getIBAValue("STANDARD")));//规格
				bean.setCybz(object2String(ibaHelper.getIBAValue("USESTANDARD")));//采用标准
				bean.setSfjk(object2String(ibaHelper.getIBAValue("ISIMPORT")));//是否进口
				bean.setTssm(object2String(ibaHelper.getIBAValue("SPECIALINSTRUCTION")));//特殊说明
				bean.setJldw(object2String(ibaHelper.getIBAValue("MEASUREUNIT")));//计量单位
				bean.setJd(object2String(ibaHelper.getIBAValue("PRECISION")));//精度
				bean.setZltz(object2String(ibaHelper.getIBAValue("QUALITYCHARACTER")));//质量特征
				bean.setPzggbz(object2String(ibaHelper.getIBAValue("VARIETYSTANDARD")));//品种规格标准
				bean.setGyzt(object2String(ibaHelper.getIBAValue("SUPPLYSTATE")));//供应状态
			} else if (softType.contains("FJSCLPart")) {//非金属材料
				bean.setWzjc(object2String(ibaHelper.getIBAValue("SHORTNAME")));//物资简称
				bean.setCllx(mtype);//材料类型
				bean.setHsl(object2String(ibaHelper.getIBAValue("RATEOFCONVERSION")));//换算率
				bean.setXs(object2String(ibaHelper.getIBAValue("RATIO")));//系数
				bean.setPh(object2String(ibaHelper.getIBAValue("MARKNUMBER")));//牌号
				bean.setGg(object2String(ibaHelper.getIBAValue("STANDARD")));//规格
				bean.setCybz(object2String(ibaHelper.getIBAValue("USESTANDARD")));//采用标准
				bean.setSfjk(object2String(ibaHelper.getIBAValue("ISIMPORT")));//是否进口
				bean.setTssm(object2String(ibaHelper.getIBAValue("SPECIALINSTRUCTION")));//特殊说明
				bean.setJldw(object2String(ibaHelper.getIBAValue("MEASUREUNIT")));//计量单位
			} else if (softType.contains("FHCLPart")) {//复合材料
				bean.setWzjc(object2String(ibaHelper.getIBAValue("SHORTNAME")));//物资简称
				bean.setCllx(mtype);//材料类型
				bean.setHsl(object2String(ibaHelper.getIBAValue("RATEOFCONVERSION")));//换算率
				bean.setXs(object2String(ibaHelper.getIBAValue("RATIO")));//系数
				bean.setPh(object2String(ibaHelper.getIBAValue("MARKNUMBER")));//牌号
				bean.setGg(object2String(ibaHelper.getIBAValue("STANDARD")));//规格
				bean.setCybz(object2String(ibaHelper.getIBAValue("USESTANDARD")));//采用标准
				bean.setSfjk(object2String(ibaHelper.getIBAValue("ISIMPORT")));//是否进口
				bean.setTssm(object2String(ibaHelper.getIBAValue("SPECIALINSTRUCTION")));//特殊说明
				bean.setJldw(object2String(ibaHelper.getIBAValue("MEASUREUNIT")));//计量单位
			}
		}

		return bean;
	}

	public static String object2String(Object obj) {
		if(obj == null) {
			return "";
		}
		return String.valueOf(obj);
	}

}
