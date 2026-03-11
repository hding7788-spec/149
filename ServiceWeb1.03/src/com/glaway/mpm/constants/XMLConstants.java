package com.glaway.mpm.constants;

import java.lang.reflect.Field;

public class XMLConstants {
	/**
	 * PBOM XML
	 */
	public static final String Product = "Product";// 产品节点
	public static final String parts = "parts";// 第一皆零件节点
	public static final String QMPartInfo = "QMPartInfo";// 零件属性节点
	public static final String childs = "childs";// 子皆零件节点

	/**
	 *ProcessPlan XML
	 */
	public static final String technics = "technics";// 工艺规程节点
	public static final String QMFawTechnicsInfo = "QMFawTechnicsInfo";// 工艺规程属性节点
	public static final String XWReportTechnicsInfo = "XWReportTechnicsInfo";// 工艺规程属性节点
	public static final String steps = "steps";// 工序节点
	public static final String paces = "paces";// 工步节点
	public static final String IBAAttibutes = "IBAAttibutes";// 软属性节点
	public static final String QMProcedureInfo = "QMProcedureInfo";// 工序工步属性节点
	public static final String attribute = "attribute";// 软属性节点
	public static final String procedureContent = "procedureContent";// 工序内容、工步内容节点
	// public static final String parts = "parts";// 参装件节点
	// public static final String QMPartInfo = "QMPartInfo";// 参装件属性节点是
	public static final String equips = "equips";// 设备节点
	public static final String QMEquipmentInfo = "QMEquipmentInfo";// 设备属性节点
	public static final String tools = "tools";// 设备、工量具、刀具节点
	public static final String QMToolInfo = "QMToolInfo";// 设备、工量具、刀具属性节点
	public static final String materials = "materials";// 材料节点
	public static final String QMMaterialInfo = "QMMaterialInfo";// 设材料属性节点
	public static final String images = "images";// 简图节点
	public static final String PDrawingInfo = "PDrawingInfo";// 简图属性节点
	public static final String attachs = "attachs";// 附件节点
	public static final String PAttachInfo = "PAttachInfo";// 附件属性节点

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
