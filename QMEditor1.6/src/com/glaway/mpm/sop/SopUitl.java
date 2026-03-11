package com.glaway.mpm.sop;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.glaway.mpm.parameter.service.ProcessParameterToWCIntf;
import org.dom4j.Document;
import org.dom4j.Element;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.doc.WTDocument;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;

import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.qmIntf.technics.TechnicsPreview;
import com.glaway.mpm.sop.intf.SopIntf;
import com.glaway.mpm.util.GLLogger;
import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.util.TechnicsReleaseUtil;
import com.glaway.mpm.util.Util;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtility;

import ext.casc.sop.constants.SopConstants;

public class SopUitl {

	private static VaLogger logger = VaLogger.getLogger(SopUitl.class.getName());
	private static String localCodeBase = PropertiesUtil.getLocalCodeBase();

	public static Element getTechnics(String sopOid) throws Exception {
		Element techElement = null;
		try {
			logger.debug("开始下载SOP工艺文件数据包...");
			long startTime0 = System.currentTimeMillis();
			List<Vector<Object>> list = SopIntf.downloadTechnics(sopOid);
			long endTime0 = System.currentTimeMillis();
			logger.debug("下载SOP工艺文件数据包耗时：" + (endTime0 - startTime0) + " ms");
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Vector<Object> result = list.get(i);
					if ((result != null) && (result.size() == 6)) {
						String fileName = (String) result.get(0);
						byte[] data = (byte[]) result.get(1);
						if ((data == null) || (data.length <= 0)) {
							return null;
						}
						if (fileName.toLowerCase().endsWith(".zip")) {
							fileName = fileName.substring(0, fileName.length() - 4);
						}
						String technicsNumber = WorkSpaceUtil.getTechnicsNumber(fileName);
						File f = new File(WorkSpaceUtil.getCommonTechnicsRootPath() + "\\" + fileName);
						if (f.exists()) {
							WorkSpaceUtil.delete(f);
						}
						String filepath = WorkSpaceUtil.createTechnicsDirectory(fileName);

						logger.debug("开始解压下载SOP工艺文件数据包,数据包大小=" + data.length / 1024 + " KB");
						long startTime = System.currentTimeMillis();
						TechnicsReleaseUtil.unZip(data, filepath);
						long endTime = System.currentTimeMillis();
						logger.debug("解压下载SOP工艺文件数据包耗时：" + (endTime - startTime) + " ms");

						Document doc = WorkSpaceUtil.getTechnicsDocumentByTechnicsNumber(technicsNumber);
						techElement = XmlUtility.getTechnicsElement(doc);
					}
				}
			}
		} catch (Exception e) {
			SwingUtil.showMessageDialog("获取SOP标准规程ZIP数据包时出错！", "提示", 2);
			throw e;
		}
		return techElement;
	}

	public static void useSop(Element currentStepElement, String sopOid) throws Exception {
		Element techElement = getTechnics(sopOid);
		if (techElement != null) {
			List<Element> steps = XmlUtility.getAllSteps(techElement);
			if (steps != null && steps.size() > 0) {
				// 1.获取SOP标准规程下所有工序的资源
				Vector<Element> equipVec = new Vector<Element>();
				Vector<Element> measureVec = new Vector<Element>();
				Vector<Element> materialVec = new Vector<Element>();
				Vector<Element> knifeToolVec = new Vector<Element>();
				Vector<Element> sDashboardVec = new Vector<Element>();
				Vector<Element> unsDashboardVec = new Vector<Element>();
				Vector<Element> toolVec = new Vector<Element>();
				Vector<Element> danyuanTableVector = new Vector<Element>();
				Vector<Element> baiyuTableVector = new Vector<Element>();
				for (Element stepElement : steps) {
					equipVec.addAll(getAllEquipmentElements(stepElement));
					measureVec.addAll(getAllMeasureElements(stepElement));
					materialVec.addAll(getAllMaterialElements(stepElement));
					knifeToolVec.addAll(getAllKnifeToolElements(stepElement));
					sDashboardVec.addAll(getAllSDashboardElements(stepElement));
					unsDashboardVec.addAll(getAllUnsDashboardElements(stepElement));
					toolVec.addAll(getAllToolElements(stepElement));
					danyuanTableVector.addAll(getAllDanYuanTableElements(stepElement));
					baiyuTableVector.addAll(getAllBaiyuTableElements(stepElement));

					List<Element> paces  =  XmlUtility.getAllPaces(stepElement);
					for (Element paceElement : paces) {
						equipVec.addAll(getAllEquipmentElements(paceElement));
						measureVec.addAll(getAllMeasureElements(paceElement));
						materialVec.addAll(getAllMaterialElements(paceElement));
						knifeToolVec.addAll(getAllKnifeToolElements(paceElement));
						sDashboardVec.addAll(getAllSDashboardElements(paceElement));
						unsDashboardVec.addAll(getAllUnsDashboardElements(paceElement));
						toolVec.addAll(getAllToolElements(paceElement));
						danyuanTableVector.addAll(getAllDanYuanTableElements(paceElement));
						baiyuTableVector.addAll(getAllBaiyuTableElements(paceElement));
					}
				}
				// 2.获取当前工序下所有的资源
				Vector<Element> currentEquipVec = getAllEquipmentElements(currentStepElement);
				Vector<Element> currentMeasureVec = getAllMeasureElements(currentStepElement);
				Vector<Element> currentMaterialVec = getAllMaterialElements(currentStepElement);
				Vector<Element> currentKnifeToolVec = getAllKnifeToolElements(currentStepElement);
				Vector<Element> currentSDashboardVec = getAllSDashboardElements(currentStepElement);
				Vector<Element> currentUnsDashboardVec = getAllUnsDashboardElements(currentStepElement);
				Vector<Element> currentToolVec = getAllToolElements(currentStepElement);
				// 2.复用SOP标准规程的资源
				// 复用设备
				reuse(currentStepElement, currentEquipVec, equipVec, XmlUtility.EQUIP_GROUP);
				// 复用量具
				reuse(currentStepElement, currentMeasureVec, measureVec, XmlUtility.MEASURE_GROUP);
				// 复用工艺辅料
				reuse(currentStepElement, currentMaterialVec, materialVec, XmlUtility.MATERIAL_GROUP);
				// 复用刀具
				reuse(currentStepElement, currentKnifeToolVec, knifeToolVec, XmlUtility.KNIFETOOL_GROUP);
				// 复用标准仪器仪表
				reuse(currentStepElement, currentSDashboardVec, sDashboardVec, XmlUtility.SDASHBOARD_GROUP);
				// 复用非标准仪器仪表
				reuse(currentStepElement, currentUnsDashboardVec, unsDashboardVec, XmlUtility.UNSDASHBOARD_GROUP);
				// 复用工装
				reuse(currentStepElement, currentToolVec, toolVec, XmlUtility.TOOL_GROUP);

				// 3.复用单元表数据
				//Vector<Element> currentDanYuanVec = getAllDanYuanTableElements(currentStepElement);
				reuseDanYuanTableParameter(currentStepElement, danyuanTableVector);

				// 4.复用白羽数据
				resueBaiyuParameterTable(currentStepElement,baiyuTableVector);

			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void resueSpecialParameterTable(Element stepElement, Vector<Element> currentVector, Vector<Element> vector) {
		if (vector == null || vector.isEmpty()) {
			return;
		}
		Element commonParamTablesElement = stepElement.element(XmlUtility.SPECIALPARAMTABLES);
		if (commonParamTablesElement != null) {
			stepElement.addElement(XmlUtility.SPECIALPARAMTABLES);
		}
		if (currentVector != null && currentVector.size() > 0) {
			for (Iterator<Element> it1 = vector.iterator(); it1.hasNext();) {
				Element element1 = it1.next();
				String tableName1 = XmlUtility.getAttributeValue(element1, "ENNAME");
				boolean flag = false;
				for (Iterator<Element> it2 = currentVector.iterator(); it1.hasNext();) {
					Element element2 = it2.next();
					String tableName2 = XmlUtility.getAttributeValue(element2, "ENNAME");
					// 如果该表在当前工序中已经存在，则合并表中的参数条目
					if(tableName1.equals(tableName2)) {
						flag = true;
						for (Iterator<Element> it3 = element1.elementIterator(XmlUtility.PARAMETER); it3.hasNext();) {
							element2.add(it3.next().createCopy());
						}
					}
				}

				// 如果该表在当前工序中不存在，则直接添加到该工序
				if (!flag) {
					commonParamTablesElement.add(element1.createCopy());
				}
			}
		} else {
			for (Iterator<Element> it = vector.iterator(); it.hasNext();) {
				Element element = it.next();
				commonParamTablesElement.add(element.createCopy());
			}
		}
	}

	private static void reuseCommonParameter(Element stepElement, Vector<Element> currentVector, Vector<Element> vector) {
		if (vector == null || vector.isEmpty()) {
			return;
		}
		Element commonParamTablesElement = stepElement.element(XmlUtility.COMMONPARAMTABLES);
		if (commonParamTablesElement != null) {
			stepElement.addElement(XmlUtility.COMMONPARAMTABLES);
		}
		Element parameterTableElement = stepElement.element(XmlUtility.PARAMETERTABLE);
		if (parameterTableElement != null) {
			parameterTableElement.addElement(XmlUtility.PARAMETERTABLE);
		}

		for (Iterator<Element> it = vector.iterator(); it.hasNext();) {
			Element element = it.next();
			parameterTableElement.add(element.createCopy());
		}
	}
	private static void reuseDanYuanTableParameter(Element stepElement,  Vector<Element> vector) {
		if (vector == null || vector.isEmpty()) {
			return;
		}
		Element commonParamTablesElement = stepElement.element(XmlUtility.CHECKRECORDTABLES);
		if (commonParamTablesElement == null) {
			stepElement.addElement(XmlUtility.CHECKRECORDTABLES);
			commonParamTablesElement = stepElement.element(XmlUtility.CHECKRECORDTABLES);
		}
		for (Iterator<Element> it = vector.iterator(); it.hasNext();) {
			Element element = it.next();
			commonParamTablesElement.add(element.createCopy());
		}
	}

	private static void resueBaiyuParameterTable(Element stepElement,  Vector<Element> vector) {
		if (vector == null || vector.isEmpty()) {
			return;
		}
		String stepName = stepElement.attributeValue("stepName");
		boolean isGB = false;
		if("".equals(stepName)){
			isGB = true;
		}

		if(isGB){
			Element baiyuTableElement = stepElement.element(XmlUtility.SCHEMA_DATA);
			if (baiyuTableElement == null) {
				stepElement.addElement(XmlUtility.SCHEMA_DATA);
				baiyuTableElement = stepElement.element(XmlUtility.SCHEMA_DATA);
			}
			ArrayList<ArrayList<String>> lists = new ArrayList<ArrayList<String>>();
			for (Iterator<Element> it = vector.iterator(); it.hasNext();) {
				Element element = it.next();
				ArrayList<String> list = new ArrayList<String>();
				list.add(element.attributeValue("id"));
				list.add(element.attributeValue("id"));
				list.add(element.attributeValue("name"));
				list.add(element.attributeValue("version"));
				lists.add(list);
			}

			String paceNumber =stepElement.attributeValue("stepNumber");
			Element parentStepElement = stepElement.getParent().getParent();
			Element techElement = parentStepElement.getParent().getParent();
			String technicsNumber =techElement.attributeValue("technicsNumber");
			String stepNumber =parentStepElement.attributeValue("stepNumber");
			try {
				ArrayList<ArrayList<String>> allList = ProcessParameterToWCIntf.quoteBaiyuTemplate(lists,technicsNumber,stepNumber,paceNumber);
				for (ArrayList<String> list : allList) {
					Element schemaEle = baiyuTableElement.addElement(XmlUtility.SCHEMA_TAG);
					XmlUtility.setAttributeValue(schemaEle,"order",list.get(0));
					XmlUtility.setAttributeValue(schemaEle,"id",list.get(1));
					XmlUtility.setAttributeValue(schemaEle,"name",list.get(2));
					XmlUtility.setAttributeValue(schemaEle,"version",list.get(3));
					XmlUtility.setAttributeValue(schemaEle,"creator",list.get(4));
					XmlUtility.setAttributeValue(schemaEle,"mofitier",list.get(5));
					XmlUtility.setAttributeValue(schemaEle,"createTime",list.get(6));
					XmlUtility.setAttributeValue(schemaEle,"mofityTime",list.get(7));
					XmlUtility.setAttributeValue(schemaEle,"docNumber",list.get(8));
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	private static void reuse(Element stepElement, Vector<Element> currentVector, Vector<Element> vector, String name) {
		if (vector == null || vector.isEmpty()) {
			return;
		}
		Element element = stepElement.element(name);
		if(element == null) {
			element = stepElement.addElement(name);
		}
		if (currentVector != null && currentVector.size() > 0) {
			List<String> oidList= new ArrayList<String>();
			for(Element ele : currentVector){
				oidList.add(XmlUtility.getAttributeValue(ele, "oid"));
			}
			for (Iterator<Element> it1 = vector.iterator(); it1.hasNext();) {
				Element element1 = it1.next();
				String oid = XmlUtility.getAttributeValue(element1, "oid");
				if(!oidList.contains(oid)){
					element.add(element1.createCopy());
				}
			}
		} else {
			for (Iterator<Element> it = vector.iterator(); it.hasNext();) {
				element.add(it.next().createCopy());
			}
		}
	}

	private static boolean isEqulElement(Element element1, Element element2) {
		boolean flag = false;
		String oid1 = XmlUtility.getAttributeValue(element1, "oid");
		String oid2 = XmlUtility.getAttributeValue(element2, "oid");
		if (oid1 != null && oid1.length()>0 ) {
			if (oid1.equals(oid2)) {
				return true;
			}
		}
		return flag;
	}

	/**
	 * 获取工序下所有设备资源
	 *
	 * @param stepElement
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Vector<Element> getAllEquipmentElements(Element stepElement) {
		Element equipElement = stepElement.element(XmlUtility.EQUIP_GROUP);
		if(equipElement == null){
			equipElement = stepElement.addElement(XmlUtility.EQUIP_GROUP);
		}
		Vector<Element> equipVec = new Vector<Element>();
		for (Iterator<Element> it = equipElement.elementIterator(XmlUtility.EQUIP_TAG); it.hasNext();) {
			equipVec.add(it.next());
		}
		return equipVec;
	}

	/**
	 * 获取工序下所有量具资源
	 *
	 * @param stepElement
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Vector<Element> getAllMeasureElements(Element stepElement) {
		Element measureElement = stepElement.element(XmlUtility.MEASURE_GROUP);
		if(measureElement == null){
			measureElement = stepElement.addElement(XmlUtility.MEASURE_GROUP);
		}
		Vector<Element> measureVec = new Vector<Element>();
		for (Iterator<Element> it = measureElement.elementIterator(XmlUtility.MEASURE_TAG); it.hasNext();) {
			measureVec.add(it.next());
		}
		return measureVec;
	}

	/**
	 * 获取工序下所有工艺辅料资源
	 *
	 * @param stepElement
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Vector<Element> getAllMaterialElements(Element stepElement) {
		Element materialElement = stepElement.element(XmlUtility.MATERIAL_GROUP);
		if(materialElement == null){
			materialElement = stepElement.addElement(XmlUtility.MATERIAL_GROUP);
		}
		Vector<Element> materialVec = new Vector<Element>();
		for (Iterator<Element> it = materialElement.elementIterator(XmlUtility.MATERIAL_TAG); it.hasNext();) {
			materialVec.add(it.next());
		}
		return materialVec;
	}

	/**
	 * 获取工序下所有刀具资源
	 *
	 * @param stepElement
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Vector<Element> getAllKnifeToolElements(Element stepElement) {
		Element knifeToolElement = stepElement.element(XmlUtility.KNIFETOOL_GROUP);
		if(knifeToolElement == null){
			knifeToolElement = stepElement.addElement(XmlUtility.KNIFETOOL_GROUP);
		}
		Vector<Element> knifeToolVec = new Vector<Element>();
		for (Iterator<Element> it = knifeToolElement.elementIterator(XmlUtility.KNIFETOOL_TAG); it.hasNext();) {
			knifeToolVec.add(it.next());
		}
		return knifeToolVec;
	}

	/**
	 * 获取工序下所有标准仪器仪表资源
	 *
	 * @param stepElement
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Vector<Element> getAllSDashboardElements(Element stepElement) {
		Element sDashboardElement = stepElement.element(XmlUtility.SDASHBOARD_GROUP);
		if(sDashboardElement == null){
			sDashboardElement = stepElement.addElement(XmlUtility.SDASHBOARD_GROUP);
		}
		Vector<Element> sDashboardVec = new Vector<Element>();
		for (Iterator<Element> it = sDashboardElement.elementIterator(XmlUtility.SDASHBOARD_TAG); it.hasNext();) {
			sDashboardVec.add(it.next());
		}
		return sDashboardVec;
	}

	/**
	 * 获取工序下所有非标准仪器仪表资源
	 *
	 * @param stepElement
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Vector<Element> getAllUnsDashboardElements(Element stepElement) {
		Element unsDashboardElement = stepElement.element(XmlUtility.UNSDASHBOARD_GROUP);
		if(unsDashboardElement == null){
			unsDashboardElement = stepElement.addElement(XmlUtility.UNSDASHBOARD_GROUP);
		}
		Vector<Element> unsDashboardVec = new Vector<Element>();
		for (Iterator<Element> it = unsDashboardElement.elementIterator(XmlUtility.UNSDASHBOARD_TAG); it.hasNext();) {
			unsDashboardVec.add(it.next());
		}
		return unsDashboardVec;
	}

	/**
	 * 获取工序下所有工具资源
	 *
	 * @param stepElement
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Vector<Element> getAllToolElements(Element stepElement) {
		Element toolElement = stepElement.element(XmlUtility.TOOL_GROUP);
		if(toolElement == null){
			toolElement = stepElement.addElement(XmlUtility.TOOL_GROUP);
		}
		Vector<Element> toolVec = new Vector<Element>();
		for (Iterator<Element> it = toolElement.elementIterator(XmlUtility.TOOL_TAG); it.hasNext();) {
			toolVec.add(it.next());
		}
		return toolVec;
	}

	/**
	 * 获取质量记录表中的参数元素集合
	 *
	 * @param stepElement
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Vector<Element> getAllCommonParameterElements(Element stepElement) {
		Vector<Element> vector = new Vector<Element>();
		Element commonParamTablesElement = stepElement.element(XmlUtility.COMMONPARAMTABLES);
		if (commonParamTablesElement != null) {
			Element parameterTableElement = stepElement.element(XmlUtility.PARAMETERTABLE);
			if (parameterTableElement != null) {
				for (Iterator<Element> it = parameterTableElement.elementIterator(XmlUtility.PARAMETER); it.hasNext();) {
					vector.add(it.next());
				}
			}
		}
		return vector;
	}

	/**
	 * 获取特殊记录表中的表元素集合
	 *
	 * @param stepElement
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Vector<Element> getAllSpecialParameterTableElements(Element stepElement) {
		Vector<Element> vector = new Vector<Element>();
		Element commonParamTablesElement = stepElement.element(XmlUtility.SPECIALPARAMTABLES);
		if(commonParamTablesElement == null){
			commonParamTablesElement = stepElement.addElement(XmlUtility.SPECIALPARAMTABLES);
		}
		if (commonParamTablesElement != null) {
			for (Iterator<Element> it = commonParamTablesElement.elementIterator(XmlUtility.PARAMETERTABLE); it.hasNext();) {
				vector.add(it.next());
			}
		}
		return vector;
	}

	/**
	 * 获取质单元表元素集合
	 *
	 * @param stepElement
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Vector<Element> getAllDanYuanTableElements(Element stepElement) {
		Vector<Element> vector = new Vector<Element>();
		Element commonParamTablesElement = stepElement.element(XmlUtility.CHECKRECORDTABLES);
		if (commonParamTablesElement != null) {
			for (Iterator<Element> it = commonParamTablesElement.elementIterator(XmlUtility.PARAMETERTABLE); it.hasNext();) {
				vector.add(it.next());
			}
		}
		return vector;
	}

	/**
	 * 获取白羽表元素集合
	 *
	 * @param stepElement
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Vector<Element> getAllBaiyuTableElements(Element stepElement) {
		Vector<Element> vector = new Vector<Element>();
		Element commonParamTablesElement = stepElement.element(XmlUtility.SCHEMA_DATA);
		if (commonParamTablesElement != null) {
			for (Iterator<Element> it = commonParamTablesElement.elementIterator(XmlUtility.SCHEMA_TAG); it.hasNext();) {
				vector.add(it.next());
			}
		}
		return vector;
	}

	public static String previewSOP(String oid) {
		String url = "";
		try {
			Object obj = Util.getObjectByOid(WTDocument.class, oid);
			GLLogger.debug("publish obj===>" + obj.getClass());
			if (obj instanceof WTDocument) {
				WTDocument doc = (WTDocument) obj;
				String path = localCodeBase + File.separator + "temp" + File.separator + "publish";
				File file = new File(path);
				if(!file.exists()){
					file.mkdirs();
				}
				String fileName = getPartAttachment(doc, path);
				String fileNameSub = fileName.replaceAll(".zip", "");
				GLLogger.debug("fileName===>" + fileName);
				GLLogger.debug("path======>" + path);
				String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(doc);
				if (docType.contains(SopConstants.SOP_TYPE_SOPDOC)) {
					url = TechnicsPreview.previewSop(path + File.separator + fileName, path + File.separator + fileNameSub);
				}else{
					url = TechnicsPreview.preview(path + File.separator + fileName, path + File.separator + fileNameSub);
				}
				GLLogger.debug("url====11>" + url);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return url;
	}

	/**
	 * 获取零件的附件
	 *
	 * @author lbzhang
	 * @date 2013-7-10
	 * @param doc
	 * @param path
	 * @return
	 *
	 */
	@SuppressWarnings("deprecation")
	public static String getPartAttachment(WTDocument doc, String path) {
		String fileName = "";
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			if (doc == null) {
				GLLogger.debug("the task of doc===>" + doc);
				return "";
			}
			GLLogger.debug("doc>>>>>" + doc.getNumber() + "   " + doc.getName() + "  "
					+ doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());
			FormatContentHolder holder = (FormatContentHolder) ContentHelper.service.getContents(doc);
			ApplicationData currdata = (ApplicationData) ContentHelper.service.getPrimary(holder);

			is = ContentServerHelper.service.findContentStream(currdata);
			fileName = currdata.getFileName();
			GLLogger.debug("the part Attachment file name:" + fileName);
			fos = new FileOutputStream(new File(path + File.separator + fileName));
			int i = 0;
			byte abyte[] = new byte[8192];
			while ((i = is.read(abyte, 0, abyte.length)) >= 0) {
				fos.write(abyte, 0, i);
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return fileName;
	}

}