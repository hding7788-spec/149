package com.glaway.mpm.view;

import com.glaway.mpm.pdf.PDFUtil;
import com.glaway.mpm.sjzyk.LoadConfigAttributes;
import com.glaway.mpm.util.XmlUtility;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * author:chenming date:2015-9-21
 *
 * */
public class NewTechnicsReportUtil {

	private static final String Element = null;
	private static final String String = null;

	public static Object[][] getGongZhuang(ArrayList<XWTreeNode> technicsList) {
		ArrayList TechnicsList = new ArrayList();
		int j = 0;
		for (int i = 0; i < technicsList.size(); i++) {
			XWTreeNode xwTreeNode = technicsList.get(i);
			Element element2 = xwTreeNode.getObject().getTreeCellData();
			// 获取所有工装
			List<Element> steps = element2.selectNodes("steps/QMProcedureInfo");
			// 开始循环遍历所有工序
			for (Element procedure : steps) {
				// 获取当前工序的工序内容
				Element object = (Element) procedure.selectNodes("tools").get(0);
				List<Element> list = object.elements("QMToolInfo");
				for (int k = 0; k < list.size(); k++) {
					j++;
					NewReportTechnicsBean bean = new NewReportTechnicsBean();
					bean.setIndex(j);
					bean.setBianhao(list.get(k).attributeValue("toolNum"));
					bean.setMingcheng(list.get(k).attributeValue("toolName"));
					bean.setGuige(list.get(k).attributeValue("csize"));
					bean.setLeibie(list.get(k).attributeValue("frockType"));
					bean.setShuliang(list.get(k).attributeValue("useCount"));
					bean.setJingdudengji("");
					bean.setPartNumber(element2.attributeValue("CINDEX"));
					bean.setPartName(element2.attributeValue("partName"));
					bean.setShiyongchejian(procedure.attributeValue("workShop"));
					bean.setComment(list.get(k).attributeValue("bz"));
					TechnicsList.add(bean);
				}
				List<Element> selectNodes = procedure.selectNodes("paces/QMProcedureInfo");
				for (Element paceElement : selectNodes) {
					Element object1 = (Element) paceElement.selectNodes("tools").get(0);
					List<Element> list1 = object1.elements("QMToolInfo");
					for (int k = 0; k < list1.size(); k++) {
						j++;
						NewReportTechnicsBean bean = new NewReportTechnicsBean();
						bean.setIndex(j);
						bean.setBianhao(list1.get(k).attributeValue("toolNum"));
						bean.setMingcheng(list1.get(k).attributeValue("toolName"));
						bean.setGuige(list1.get(k).attributeValue("csize"));
						bean.setLeibie(list1.get(k).attributeValue("frockType"));
						bean.setShuliang(list1.get(k).attributeValue("useCount"));
						bean.setJingdudengji("");
						bean.setPartNumber(element2.attributeValue("CINDEX"));
						bean.setPartName(element2.attributeValue("partName"));
						bean.setShiyongchejian(procedure.attributeValue("workShop"));
						bean.setComment(list1.get(k).attributeValue("bz"));
						TechnicsList.add(bean);
					}
				}

			}
		}
		String[][] datas = new String[TechnicsList.size()][11];
		for (int i = 0; i < TechnicsList.size(); i++) {
			datas[i][0] = String.valueOf(i + 1);
			datas[i][1] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getBianhao();
			datas[i][2] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getMingcheng();
			datas[i][3] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getGuige();
			datas[i][4] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getLeibie();
			datas[i][5] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getShuliang();
			datas[i][6] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getJingdudengji();
			datas[i][7] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getPartNumber();
			datas[i][8] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getPartName();
			datas[i][9] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getShiyongchejian();
			datas[i][10] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getComment();
		}
		return datas;
	}

	public static Object[][] getBiaozhunYiQi(ArrayList<XWTreeNode> technicsList) {
		ArrayList TechnicsList = new ArrayList();
		int j = 0;
		for (int i = 0; i < technicsList.size(); i++) {
			XWTreeNode xwTreeNode = technicsList.get(i);
			Element element2 = xwTreeNode.getObject().getTreeCellData();
			// 获取所有工装
			List<Element> steps = element2.selectNodes("steps/QMProcedureInfo");
			// 开始循环遍历所有工序
			for (Element procedure : steps) {
				// 获取当前工序的工序内容
				Element object = (Element) procedure.selectNodes("sdashboard").get(0);
				List<Element> list = object.elements("QMSDashboardInfo");
				for (int k = 0; k < list.size(); k++) {
					j++;
					NewReportTechnicsBean bean = new NewReportTechnicsBean();
					bean.setIndex(j);
					bean.setBianhao(list.get(k).attributeValue("number"));
					bean.setMingcheng(list.get(k).attributeValue("name"));
					bean.setGuige(list.get(k).attributeValue("csize"));
					bean.setLeibie(list.get(k).attributeValue("equipmentType"));
					bean.setShuliang(list.get(k).attributeValue("useCount"));
					bean.setJingdudengji("");
					bean.setPartNumber(element2.attributeValue("CINDEX"));
					bean.setPartName(element2.attributeValue("partName"));
					bean.setShiyongchejian(procedure.attributeValue("workShop"));
					bean.setComment(list.get(k).attributeValue("bz"));
					TechnicsList.add(bean);
				}
				List<Element> selectNodes = procedure.selectNodes("paces/QMProcedureInfo");
				for (Element paceElement : selectNodes) {
					Element object1 = (Element) paceElement.selectNodes("sdashboard").get(0);
					List<Element> list1 = object1.elements("QMSDashboardInfo");
					for (int k = 0; k < list1.size(); k++) {
						j++;
						NewReportTechnicsBean bean = new NewReportTechnicsBean();
						bean.setIndex(j);
						bean.setBianhao(list1.get(k).attributeValue("number"));
						bean.setMingcheng(list1.get(k).attributeValue("name"));
						bean.setGuige(list1.get(k).attributeValue("csize"));
						bean.setLeibie(list1.get(k).attributeValue("equipmentType"));
						bean.setShuliang(list1.get(k).attributeValue("useCount"));
						bean.setJingdudengji("");
						bean.setPartNumber(element2.attributeValue("CINDEX"));
						bean.setPartName(element2.attributeValue("partName"));
						bean.setShiyongchejian(procedure.attributeValue("workShop"));
						bean.setComment(list1.get(k).attributeValue("bz"));
						TechnicsList.add(bean);
					}
				}
			}
		}

		String[][] datas = new String[TechnicsList.size()][11];
		for (int i = 0; i < TechnicsList.size(); i++) {
			datas[i][0] = String.valueOf(i + 1);
			datas[i][1] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getBianhao();
			datas[i][2] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getMingcheng();
			datas[i][3] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getGuige();
			datas[i][4] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getLeibie();
			datas[i][5] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getShuliang();
			datas[i][6] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getJingdudengji();
			datas[i][7] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getPartNumber();
			datas[i][8] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getPartName();
			datas[i][9] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getShiyongchejian();
			datas[i][10] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getComment();
		}
		return datas;
	}

	public static Object[][] getFeiBiaoZhunYiQi(ArrayList<XWTreeNode> technicsList) {

		ArrayList TechnicsList = new ArrayList();
		int j = 0;
		for (int i = 0; i < technicsList.size(); i++) {
			XWTreeNode xwTreeNode = technicsList.get(i);
			Element element2 = xwTreeNode.getObject().getTreeCellData();
			// 获取所有工装
			List<Element> steps = element2.selectNodes("steps/QMProcedureInfo");
			// 开始循环遍历所有工序
			for (Element procedure : steps) {
				// 获取当前工序的工序内容
				Element object = (Element) procedure.selectNodes("unsdashboard").get(0);
				List<Element> list = object.elements("QMUnSDashboardInfo");
				for (int k = 0; k < list.size(); k++) {
					j++;
					NewReportTechnicsBean bean = new NewReportTechnicsBean();
					bean.setIndex(j);
					bean.setBianhao(list.get(k).attributeValue("number"));
					bean.setMingcheng(list.get(k).attributeValue("name"));
					bean.setGuige(list.get(k).attributeValue("csize"));
					bean.setLeibie(list.get(k).attributeValue("equipmentType"));
					bean.setShuliang(list.get(k).attributeValue("useCount"));
					bean.setJingdudengji("");
					bean.setPartNumber(element2.attributeValue("CINDEX"));
					bean.setPartName(element2.attributeValue("partName"));
					bean.setShiyongchejian(procedure.attributeValue("workShop"));
					bean.setComment(list.get(k).attributeValue("bz"));
					TechnicsList.add(bean);
				}
				List<Element> selectNodes = procedure.selectNodes("paces/QMProcedureInfo");
				for (Element paceElement : selectNodes) {
					Element object1 = (Element) paceElement.selectNodes("unsdashboard").get(0);
					List<Element> list1 = object1.elements("QMUnSDashboardInfo");
					for (int k = 0; k < list1.size(); k++) {
						j++;
						NewReportTechnicsBean bean = new NewReportTechnicsBean();
						bean.setIndex(j);
						bean.setBianhao(list1.get(k).attributeValue("number"));
						bean.setMingcheng(list1.get(k).attributeValue("name"));
						bean.setGuige(list1.get(k).attributeValue("csize"));
						bean.setLeibie(list1.get(k).attributeValue("equipmentType"));
						bean.setShuliang(list1.get(k).attributeValue("useCount"));
						bean.setJingdudengji("");
						bean.setPartNumber(element2.attributeValue("CINDEX"));
						bean.setPartName(element2.attributeValue("partName"));
						bean.setShiyongchejian(procedure.attributeValue("workShop"));
						bean.setComment(list1.get(k).attributeValue("bz"));
						TechnicsList.add(bean);
					}
				}
			}
		}
		String[][] datas = new String[TechnicsList.size()][11];
		for (int i = 0; i < TechnicsList.size(); i++) {
			datas[i][0] = String.valueOf(i + 1);
			datas[i][1] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getBianhao();
			datas[i][2] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getMingcheng();
			datas[i][3] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getGuige();
			datas[i][4] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getLeibie();
			datas[i][5] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getShuliang();
			datas[i][6] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getJingdudengji();
			datas[i][7] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getPartNumber();
			datas[i][8] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getPartName();
			datas[i][9] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getShiyongchejian();
			datas[i][10] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getComment();
		}
		return datas;

	}

	public static Object[][] getDaoLiangJu(ArrayList<XWTreeNode> technicsList) {
		ArrayList TechnicsList = new ArrayList();
		int j = 0;
		for (int i = 0; i < technicsList.size(); i++) {
			XWTreeNode xwTreeNode = technicsList.get(i);
			Element element2 = xwTreeNode.getObject().getTreeCellData();
			// 获取所有工装
			List<Element> steps = element2.selectNodes("steps/QMProcedureInfo");
			// 开始循环遍历所有工序
			for (Element procedure : steps) {
				// 获取当前工序的工序内容
				if (procedure.selectNodes("knifeTools").size() > 0) {
					Element object = (Element) procedure.selectNodes("knifeTools").get(0);
					List<Element> list = object.elements("QMKnifeToolInfo");
					for (int k = 0; k < list.size(); k++) {
						j++;
						NewReportTechnicsBean bean = new NewReportTechnicsBean();
						bean.setIndex(j);
						bean.setBianhao(list.get(k).attributeValue("toolNum"));
						bean.setMingcheng(list.get(k).attributeValue("toolName"));
						bean.setGuige(list.get(k).attributeValue("rkzj"));
						bean.setLeibie(list.get(k).attributeValue("knifetype"));
//						bean.setShuliang(list.get(k).attributeValue("useCount"));
						bean.setShuliang("1");
						bean.setJingdudengji("");
						bean.setPartNumber(element2.attributeValue("partNumber"));
						bean.setPartName(element2.attributeValue("partName"));
						bean.setShiyongchejian(procedure.attributeValue("workShop"));
						bean.setComment(list.get(k).attributeValue("bz"));
						TechnicsList.add(bean);
					}
				}

				List<Element> selectNodes = procedure.selectNodes("paces/QMProcedureInfo");
				for (Element paceElement : selectNodes) {
					if (paceElement.selectNodes("knifeTools").size() > 0) {
						Element object1 = (Element) paceElement.selectNodes("knifeTools").get(0);
						List<Element> list1 = object1.elements("QMKnifeToolInfo");
						for (int k = 0; k < list1.size(); k++) {
							j++;
							NewReportTechnicsBean bean = new NewReportTechnicsBean();
							bean.setIndex(j);
							bean.setBianhao(list1.get(k).attributeValue("toolNum"));
							bean.setMingcheng(list1.get(k).attributeValue("toolName"));
							bean.setGuige(list1.get(k).attributeValue("rkzj"));
							bean.setLeibie(list1.get(k).attributeValue("knifetype"));
//							bean.setShuliang(list1.get(k).attributeValue("useCount"));
							bean.setShuliang("1");
							bean.setJingdudengji("");
							bean.setPartNumber(element2.attributeValue("partNumber"));
							bean.setPartName(element2.attributeValue("partName"));
							bean.setShiyongchejian(procedure.attributeValue("workShop"));
							bean.setComment(list1.get(k).attributeValue("bz"));
							TechnicsList.add(bean);
						}
					}

				}
				if (!procedure.selectNodes("measures").isEmpty()) {
					Element object12 = (Element) procedure.selectNodes("measures").get(0);
					List<Element> list2 = object12.elements("QMMeasureInfo");
					for (int k = 0; k < list2.size(); k++) {
						j++;
						NewReportTechnicsBean bean = new NewReportTechnicsBean();
						bean.setIndex(j);
						bean.setBianhao(list2.get(k).attributeValue("number"));
						bean.setMingcheng(list2.get(k).attributeValue("name"));
						bean.setGuige(list2.get(k).attributeValue("csize"));
						bean.setLeibie(list2.get(k).attributeValue("pindex"));
//						bean.setShuliang(list2.get(k).attributeValue("useCount"));
						bean.setShuliang("1");
						bean.setJingdudengji("");
						bean.setPartNumber(element2.attributeValue("partNumber"));
						bean.setPartName(element2.attributeValue("partName"));
						bean.setShiyongchejian(procedure.attributeValue("workShop"));
						bean.setComment(list2.get(k).attributeValue("bz"));
						TechnicsList.add(bean);
					}
				}

				List<Element> selectNodes1 = procedure.selectNodes("paces/QMProcedureInfo");
				for (Element paceElement1 : selectNodes1) {
					if (!paceElement1.selectNodes("measures").isEmpty()) {
						Element object13 = (Element) paceElement1.selectNodes("measures").get(0);
						List<Element> list3 = object13.elements("QMMeasureInfo");
						for (int k = 0; k < list3.size(); k++) {
							j++;
							NewReportTechnicsBean bean = new NewReportTechnicsBean();
							bean.setIndex(j);
							bean.setBianhao(list3.get(k).attributeValue("number"));
							bean.setMingcheng(list3.get(k).attributeValue("name"));
							bean.setGuige(list3.get(k).attributeValue("csize"));
							bean.setLeibie(list3.get(k).attributeValue("pindex"));
//							bean.setShuliang(list3.get(k).attributeValue("useCount"));
							bean.setShuliang("1");
							bean.setJingdudengji("");
							bean.setPartNumber(element2.attributeValue("partNumber"));
							bean.setPartName(element2.attributeValue("partName"));
							bean.setShiyongchejian(procedure.attributeValue("workShop"));
							bean.setComment(list3.get(k).attributeValue("bz"));
							TechnicsList.add(bean);
						}
					}

				}
			}
		}
		String[][] datas = new String[TechnicsList.size()][11];
		for (int i = 0; i < TechnicsList.size(); i++) {
			datas[i][0] = String.valueOf(i + 1);
			datas[i][1] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getBianhao();
			datas[i][2] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getMingcheng();
			datas[i][3] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getGuige();
			datas[i][4] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getLeibie();
			datas[i][5] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getShuliang();
			datas[i][6] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getJingdudengji();
			datas[i][7] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getPartNumber();
			datas[i][8] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getPartName();
			datas[i][9] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getShiyongchejian();
			datas[i][10] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getComment();
		}
		return datas;
	}

	public static Object[][] getWaiXieJian(ArrayList<XWTreeNode> technicsList) {
		ArrayList TechnicsList = new ArrayList();
		int j = 0;
		for (int i = 0; i < technicsList.size(); i++) {
			XWTreeNode xwTreeNode = technicsList.get(i);
			Element element2 = xwTreeNode.getObject().getTreeCellData();
			String isTabular = element2.attributeValue("isTabular");
			if ("外协".equals(isTabular)) {
				NewReportTechnicsBean bean = new NewReportTechnicsBean();
				bean.setIndex(j);
				String partNumber = PDFUtil.objectToString(element2.attributeValue("CINDEX"));
				bean.setPartNumber(partNumber);
				bean.setPartName(element2.attributeValue("partName"));
				String scmat = PDFUtil.getCmatByCLDE(element2);
				bean.setCailiao(scmat);
				String useCount = PDFUtil.objectToString(element2.attributeValue("useCount"));
				bean.setShuliang(useCount);
				String jsxy = PDFUtil.objectToString(element2.attributeValue("jsxy"));
				bean.setJsxy(jsxy);
				String zzdw = PDFUtil.objectToString(element2.attributeValue("zzdw"));
				bean.setCzdw(zzdw);

				Element toolElement = element2.element(XmlUtility.TOOL_GROUP);
				String gyzb = "";
				if (toolElement != null) {
					for (Iterator<Element> it = toolElement.elementIterator(XmlUtility.TOOL_TAG); it.hasNext();) {
						Element ele = it.next();
						if (gyzb != null && !"".equals(gyzb)) {
							gyzb = gyzb + ";" + ele.attributeValue("toolNum");
						} else {
							gyzb = ele.attributeValue("toolNum");
						}
					}
				}

				bean.setGyzb(gyzb);
				String bz = PDFUtil.objectToString(element2.attributeValue("bz"));
				bean.setComment(bz);
				TechnicsList.add(bean);

			}
		}
		String[][] datas = new String[TechnicsList.size()][9];
		for (int i = 0; i < TechnicsList.size(); i++) {
			datas[i][0] = String.valueOf(i + 1);
			datas[i][1] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getPartNumber();
			datas[i][2] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getPartName();
			datas[i][3] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getCailiao();
			datas[i][4] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getShuliang();
			datas[i][5] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getJsxy();
			datas[i][6] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getCzdw();
			datas[i][7] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getGyzb();
			datas[i][8] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getComment();
		}
		return datas;
	}

	public static Object[][] getGuanJianGongXu(ArrayList<XWTreeNode> technicsList) {

		ArrayList TechnicsList = new ArrayList();
		int j = 0;
		for (int i = 0; i < technicsList.size(); i++) {
			XWTreeNode xwTreeNode = technicsList.get(i);
			Element element2 = xwTreeNode.getObject().getTreeCellData();
			List<Element> list = element2.selectNodes("steps/QMProcedureInfo");
			for (int k = 0; k < list.size(); k++) {
				if ("true".equals(list.get(k).attributeValue("isKey"))) {
					j++;
					NewReportTechnicsBean bean = new NewReportTechnicsBean();
					bean.setIndex(j);
					bean.setPartNumber(element2.attributeValue("pplanNumber"));
					bean.setPartName(element2.attributeValue("partName"));
					bean.setGongxuhao(list.get(k).attributeValue("stepNumber"));
					bean.setGongxumingcheng(list.get(k).attributeValue("stepName"));
					Element kznrContentEle = list.get(k).element("kznrContent");
					String kznr = PDFUtil.objectToString(kznrContentEle.getTextTrim());
					// kznr=RemoveHTML.getTextFromHtml(kznr);
					kznr = kznr.replace("<html>", "").replace("<head>", "").replace("</html>", "").replace("</head>", "").replace("<body>", "").replace("</body>", "")
							.replace("<p style='margin-top:5'>", "").replace("</p>", "").trim();
					if (kznr != null && !"".equals(kznr)) {

						bean.setKongzhineirong("Y");
					} else {
						bean.setKongzhineirong("N");
					}
					bean.setNeikongbiaozhun("");
					bean.setComment(element2.attributeValue("bz"));
					bean.setTechnicsNumber(element2.attributeValue("technicsNumber"));
					bean.setKznrFlag(PDFUtil.objectToString(kznrContentEle.getTextTrim()));
					TechnicsList.add(bean);
				}
			}
		}

		String[][] datas = new String[TechnicsList.size()][10];
		for (int i = 0; i < TechnicsList.size(); i++) {
			datas[i][0] = String.valueOf(i + 1);
			datas[i][1] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getPartNumber();
			datas[i][2] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getPartName();
			datas[i][3] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getGongxuhao();
			datas[i][4] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getGongxumingcheng();
			datas[i][5] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getKongzhineirong();
			// String kongzhineirong = ((NewReportTechnicsBean)
			// TechnicsList.get(i)).getKongzhineirong();
			// if
			// ("<html> <head> </head> <body> </body> </html>".equals(kongzhineirong))
			// {
			// datas[i][5]="N";
			// // bean.setKongzhineirong("N");
			// }else{
			// datas[i][5]="Y";
			// // bean.setKongzhineirong("Y");
			// }

			datas[i][6] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getNeikongbiaozhun();
			datas[i][7] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getComment();
			datas[i][8] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getTechnicsNumber();
			datas[i][9] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getKznrFlag();
		}
		return datas;
	}

	public static Object[][] getFuZhuCaiLiao(ArrayList<XWTreeNode> technicsList) {

		ArrayList TechnicsList = new ArrayList();
		int j = 0;
		for (int i = 0; i < technicsList.size(); i++) {
			XWTreeNode xwTreeNode = technicsList.get(i);
			Element element2 = xwTreeNode.getObject().getTreeCellData();
			// 获取所有工装
			List<Element> steps = element2.selectNodes("steps/QMProcedureInfo");
			// 开始循环遍历所有工序
			for (Element procedure : steps) {
				// 获取当前工序的工序内容
				Element object = (Element) procedure.selectNodes("materials").get(0);
				List<Element> list = object.elements("QMMaterialInfo");
				for (int k = 0; k < list.size(); k++) {
					j++;
					NewReportTechnicsBean bean = new NewReportTechnicsBean();
					bean.setIndex(j);
					bean.setDaihao(element2.attributeValue("CINDEX"));
					bean.setPartName(element2.attributeValue("partName"));
					bean.setCailiao(list.get(k).attributeValue("materialName"));
					bean.setPaihao(list.get(k).attributeValue("mindex"));
					bean.setGuige(list.get(k).attributeValue("csize"));
					bean.setJsxy(list.get(k).attributeValue("jstj"));
					bean.setPingzhongguige(list.get(k).attributeValue("clgg"));
					bean.setDanwei(list.get(k).attributeValue("jldw"));
					bean.setShuliang(list.get(k).attributeValue("sl"));
					bean.setShiyongchejian(procedure.attributeValue("workShop"));
					bean.setComment(list.get(k).attributeValue("bz"));
					TechnicsList.add(bean);
				}
				List<Element> selectNodes = procedure.selectNodes("paces/QMProcedureInfo");
				for (Element paceElement : selectNodes) {
					Element object1 = (Element) paceElement.selectNodes("materials").get(0);
					List<Element> list1 = object1.elements("QMMaterialInfo");
					for (int k = 0; k < list1.size(); k++) {
						j++;
						NewReportTechnicsBean bean = new NewReportTechnicsBean();
						bean.setIndex(j);
						bean.setDaihao(element2.attributeValue("CINDEX"));
						bean.setPartName(element2.attributeValue("partName"));
						bean.setCailiao(list1.get(k).attributeValue("materialName"));
						bean.setPaihao(list1.get(k).attributeValue("mindex"));
						bean.setGuige(list1.get(k).attributeValue("csize"));
						bean.setJsxy(list1.get(k).attributeValue("jstj"));
						bean.setPingzhongguige(list1.get(k).attributeValue("clgg"));
						bean.setDanwei(list1.get(k).attributeValue("jldw"));
						bean.setShuliang(list1.get(k).attributeValue("sl"));
						bean.setShiyongchejian(procedure.attributeValue("workShop"));
						bean.setComment(list1.get(k).attributeValue("bz"));
						TechnicsList.add(bean);

					}

				}
			}
		}
		String[][] datas = new String[TechnicsList.size()][12];
		for (int i = 0; i < TechnicsList.size(); i++) {
			datas[i][0] = String.valueOf(i + 1);
			datas[i][1] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getDaihao();
			datas[i][2] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getPartName();
			datas[i][3] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getCailiao();
			datas[i][4] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getPaihao();
			datas[i][5] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getGuige();
			datas[i][6] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getJsxy();
			datas[i][7] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getPingzhongguige();
			datas[i][8] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getDanwei();
			datas[i][9] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getShuliang();
			datas[i][10] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getShiyongchejian();
			datas[i][11] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getComment();
		}
		return datas;
	}

	public static Object[][] getFuZhuCaiLiaoHuiZong(ArrayList<XWTreeNode> technicsList) {

		ArrayList TechnicsList = new ArrayList();
		int j = 0;
		for (int i = 0; i < technicsList.size(); i++) {
			XWTreeNode xwTreeNode = technicsList.get(i);
			Element element2 = xwTreeNode.getObject().getTreeCellData();
			// 获取所有工装
			List<Element> steps = element2.selectNodes("steps/QMProcedureInfo");
			// 开始循环遍历所有工序
			for (Element procedure : steps) {
				// 获取当前工序的工序内容
				Element object = (Element) procedure.selectNodes("materials").get(0);
				List<Element> list = object.elements("QMMaterialInfo");
				for (int k = 0; k < list.size(); k++) {
					j++;
					NewReportTechnicsBean bean = new NewReportTechnicsBean();
					bean.setIndex(j);
					bean.setCailiao(list.get(k).attributeValue("materialName"));
					bean.setPaihao(list.get(k).attributeValue("mindex"));
					bean.setGuige(list.get(k).attributeValue("csize"));
					bean.setJsxy(list.get(k).attributeValue("jstj"));
					bean.setDanwei(list.get(k).attributeValue("jldw"));
					bean.setShuliang(list.get(k).attributeValue("sl"));
					bean.setComment(list.get(k).attributeValue("bz"));
					TechnicsList.add(bean);
				}
				List<Element> selectNodes = procedure.selectNodes("paces/QMProcedureInfo");
				for (Element paceElement : selectNodes) {
					Element object1 = (Element) paceElement.selectNodes("materials").get(0);
					List<Element> list1 = object1.elements("QMMaterialInfo");
					for (int k = 0; k < list1.size(); k++) {
						j++;
						NewReportTechnicsBean bean = new NewReportTechnicsBean();
						bean.setIndex(j);
						bean.setCailiao(list1.get(k).attributeValue("materialName"));
						bean.setPaihao(list1.get(k).attributeValue("mindex"));
						bean.setGuige(list1.get(k).attributeValue("csize"));
						bean.setJsxy(list1.get(k).attributeValue("jstj"));
						bean.setDanwei(list1.get(k).attributeValue("jldw"));
						bean.setShuliang(list1.get(k).attributeValue("sl"));
						bean.setComment(list1.get(k).attributeValue("bz"));
						TechnicsList.add(bean);

					}

				}

			}
		}
		String[][] datas = new String[TechnicsList.size()][8];
		for (int i = 0; i < TechnicsList.size(); i++) {
			datas[i][0] = String.valueOf(i + 1);
			datas[i][1] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getCailiao();
			datas[i][2] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getPaihao();
			datas[i][3] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getGuige();
			datas[i][4] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getJsxy();
			datas[i][5] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getDanwei();
			datas[i][6] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getShuliang();
			datas[i][7] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getComment();
		}
		return datas;

	}

	public static Object[][] getCaiLiaoHuiZong(ArrayList<XWTreeNode> technicsList) {
		ArrayList TechnicsList = new ArrayList();
		int j = 0;
		for (int i = 0; i < technicsList.size(); i++) {
			XWTreeNode xwTreeNode = technicsList.get(i);
			Element element2 = xwTreeNode.getObject().getTreeCellData();
			Element clde = XmlUtility.getTechnicsCLDEElement(element2);
			if (clde == null) {
				return new Object[][] {};
			}
			List<Element> ycl = XmlUtility.getTechnicsYCLDE(clde);
			for (Element procedure : ycl) {
				j++;
				NewReportTechnicsBean bean = new NewReportTechnicsBean();
				bean.setIndex(j);
				bean.setDaihao(element2.attributeValue("CINDEX"));
				bean.setPartName(element2.attributeValue("partName"));
				bean.setMeitaochanpingshuliang("");
				bean.setCailiao(procedure.attributeValue("chmc"));
				bean.setPaihao(procedure.attributeValue("xhph"));
				bean.setGuige(procedure.attributeValue("gg"));
				bean.setJsxy(procedure.attributeValue("jstj"));
				bean.setXialiaochicun(procedure.attributeValue("xlcc"));
				bean.setKezhijianshu(procedure.attributeValue("kzjs"));
				bean.setDanwei(procedure.attributeValue("dw"));
				bean.setLingjianmaozhong("");
				bean.setLingjiangongyidinger("");
				bean.setGongyimaozhong("");
				bean.setGongyidinger("");
				bean.setShijianshuliang(procedure.attributeValue("sl"));
				bean.setShijianmaopichicun(procedure.attributeValue("sjcc"));
				bean.setComment(procedure.attributeValue("comment"));
				bean.setGyztrcl(procedure.attributeValue("gyztrcl"));
				TechnicsList.add(bean);
			}
		}
		String[][] datas = new String[TechnicsList.size()][19];
		for (int i = 0; i < TechnicsList.size(); i++) {
			datas[i][0] = String.valueOf(i + 1);
			datas[i][1] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getDaihao();
			datas[i][2] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getPartName();
			datas[i][3] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getMeitaochanpingshuliang();
			datas[i][4] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getCailiao();
			datas[i][5] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getPaihao();
			datas[i][6] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getGuige();
			datas[i][7] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getJsxy();
			datas[i][8] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getXialiaochicun();
			datas[i][9] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getKezhijianshu();
			datas[i][10] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getDanwei();
			datas[i][11] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getLingjianmaozhong();
			datas[i][12] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getLingjiangongyidinger();
			datas[i][13] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getGongyimaozhong();
			datas[i][14] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getGongyidinger();
			datas[i][15] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getShijianshuliang();
			datas[i][16] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getShijianmaopichicun();
			datas[i][17] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getGyztrcl();
			datas[i][18] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getComment();
		}
		return datas;
	}

	public static Object[][] getGonyiMulu(ArrayList<XWTreeNode> technicsList) {
		ArrayList TechnicsList = new ArrayList();
		List<String> ppNumberList = new ArrayList<String>();
		int j = 0;
		for (int i = 0; i < technicsList.size(); i++) {
			XWTreeNode xwTreeNode = technicsList.get(i);
			Element element2 = xwTreeNode.getObject().getTreeCellData();
			j++;
			String ppNumber = element2.attributeValue("pplanNumber");
			if(ppNumberList.contains(ppNumber)){
				continue;
			}else{
				ppNumberList.add(ppNumber);
			}
			NewReportTechnicsBean bean = new NewReportTechnicsBean();
			bean.setIndex(j);
			bean.setBianhao(element2.attributeValue("pplanNumber"));
			bean.setMingcheng(element2.attributeValue("pplanName"));
			bean.setDaihao(element2.attributeValue("CINDEX"));
			bean.setPartName(element2.attributeValue("partName"));
			bean.setShuliang(element2.attributeValue("pageSize"));
			bean.setZerenbumen(element2.attributeValue("DEPT"));
			bean.setComment("");
			bean.setLifecycle(element2.attributeValue("lifecycle"));
			bean.setFileNumber(element2.attributeValue("filenumber"));
			bean.setVersion(element2.attributeValue("version"));
			TechnicsList.add(bean);

		}
		String[][] datas = new String[TechnicsList.size()][11];
		for (int i = 0; i < TechnicsList.size(); i++) {
			datas[i][0] = String.valueOf(i + 1);
			datas[i][1] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getBianhao();
			datas[i][2] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getMingcheng();
			datas[i][3] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getDaihao();
			datas[i][4] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getPartName();
			datas[i][5] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getShuliang();
			datas[i][6] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getZerenbumen();
			datas[i][7] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getComment();
			datas[i][8] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getLifecycle();
			datas[i][9] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getFileNumber();
			datas[i][10] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getVersion();

		}
		return datas;
	}

	public static Object[][] getGongYiLuXian(ArrayList<XWTreeNode> technicsList) {
		ArrayList TechnicsList = new ArrayList();
		int j = 0;
		for (int i = 0; i < technicsList.size(); i++) {
			XWTreeNode xwTreeNode = technicsList.get(i);
			Element element2 = xwTreeNode.getObject().getTreeCellData();
			List<Element> steps = element2.selectNodes("steps/QMProcedureInfo");
			j++;
			NewReportTechnicsBean bean = new NewReportTechnicsBean();
			bean.setIndex(j);
			bean.setDaihao(element2.attributeValue("CINDEX"));
			bean.setMingcheng(element2.attributeValue("partName"));
			bean.setMeitaochanpingshuliang(element2.attributeValue("gysl"));
			bean.setShuliang("");
			bean.setZhuzhibumen(element2.attributeValue("ZZCJ"));
			bean.setShijianshuliang("");
			String name = "";
			Map<String, String> map = LoadConfigAttributes.getInstance().getAllDesignAttr();
			List<String> deptList = LoadConfigAttributes.getInstance().getDept();
			String gongyibumen = "";
			ArrayList<String[]> list = new ArrayList<String[]>();
			for (int k = 0; k < steps.size(); k++) {
				String[] arg = new String[2];
				String workShop = steps.get(k).attributeValue("workShop");
				if(deptList.contains(workShop)){
					workShop = "";
				}
				String stepName = steps.get(k).attributeValue("stepName");
				for (String key : map.keySet()) {
					arg[0] = workShop;
					if (key.equals(stepName)) {
						if ("/".equals(map.get(key))) {
							arg[1] = "";
						} else {
							arg[1] = map.get(key);
						}
					}

				}
				if (arg[1] == null) {
					arg[1] = "无";
				}
				list.add(arg);
			}
			String currentBumenString = "";
			for(int k = list.size() - 1;k >= 0; k--){
				if("".equals(list.get(k)[0]) || "".equals(list.get(k)[1]) || "无".equals(list.get(k)[1])){
					list.remove(k);
				}
			}
			for(int n = 0; n < list.size(); n++){
				if (n >= 1 && !"".equals(currentBumenString)) {
					if (list.get(n - 1)[0].equals(list.get(n)[0])) {
						currentBumenString = currentBumenString + list.get(n)[1];
					} else {
						currentBumenString = currentBumenString + "-" + list.get(n)[0] + list.get(n)[1];
					}
				} else {
					currentBumenString = currentBumenString + list.get(n)[0] + list.get(n)[1];
				}
				}
//			if (list.size() > 0 && !"无".equals(list.get(0)[1])) {
//				currentBumenString = list.get(0)[0] + list.get(0)[1];
//			}
//			for (int k = 1; k < list.size(); k++) {
//				if(!"无".equals(list.get(k)[1])){
//
//				if (k >= 1) {
//					if (list.get(k)[0].equals(list.get(k - 1)[0])) {
//						if("".equals(currentBumenString)){
//							currentBumenString = currentBumenString + list.get(k)[0] + list.get(k)[1];
//						}else{
//							currentBumenString = currentBumenString + list.get(k)[1];
//						}
//					} else {
//						if(k == 1 && "".equals(list.get(0)[0])){
//							currentBumenString = currentBumenString + list.get(k)[0] + list.get(k)[1];
//						}else{
//							currentBumenString = currentBumenString + "-" + list.get(k)[0] + list.get(k)[1];
//						}
//					}
//				}
//				}
//			}
			bean.setDiyibumen(currentBumenString);
			TechnicsList.add(bean);
		}
		String[][] datas = new String[TechnicsList.size()][9];
		for (int i = 0; i < TechnicsList.size(); i++) {
			datas[i][0] = String.valueOf(i + 1);
			datas[i][1] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getDaihao();
			datas[i][2] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getMingcheng();
			datas[i][3] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getMeitaochanpingshuliang();
			datas[i][4] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getShuliang();
			datas[i][5] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getShijianshuliang();
			datas[i][6] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getZhuzhibumen();
			datas[i][7] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getDiyibumen();
			datas[i][8] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getComment();
		}
		return datas;
	}

	public static Object[][] getWaigoujian(ArrayList<XWTreeNode> technicsList, ArrayList<XWTreeNode> partList) {
		ArrayList TechnicsList = new ArrayList();
		ArrayList bmList = new ArrayList();
		int j = 0;
		for (int i = 0; i < technicsList.size(); i++) {
			XWTreeNode xwTreeNode = technicsList.get(i);
			Element element2 = xwTreeNode.getObject().getTreeCellData();
			Element gyde = XmlUtility.getTechnicsDEElement(element2);
			if (gyde != null) {
				// ERP集成获取的数据
				List<Element> newParts = XmlUtility.getTechnicsGYDENewPart(gyde);
				if (newParts != null && !newParts.isEmpty()) {
					for (Element element : newParts) {
						j++;
						NewReportTechnicsBean bean = new NewReportTechnicsBean();
						bean.setIndex(j);
						bean.setMingcheng(element.attributeValue("chmc"));
						bean.setLeibie("");// 暂时不好判断
						bean.setPaihao(element.attributeValue("xhph"));
						bean.setGuige(element.attributeValue("gg"));
						bean.setCscj(element.attributeValue("sccj"));
						bean.setJstj(element.attributeValue("jstj"));
						bean.setCailiao("");// 暂时未处理
						bean.setJxxndj(element.attributeValue("jxxndj"));
						bean.setBmcl("");// 暂时未处理
						bean.setDanwei(element.attributeValue("dw2"));
						bean.setGyde(element.attributeValue("sl"));
						bean.setComment("");
						TechnicsList.add(bean);
					}
				}
				newParts = XmlUtility.getTechnicsGYDEMatchPart(gyde);
				if (newParts != null && !newParts.isEmpty()) {
					for (Element element : newParts) {
						j++;
						NewReportTechnicsBean bean = new NewReportTechnicsBean();
						bean.setIndex(j);
						bean.setMingcheng(element.attributeValue("chmc"));
						bean.setLeibie("");// 暂时不好判断
						bean.setPaihao(element.attributeValue("xhph"));
						bean.setGuige(element.attributeValue("gg"));
						bean.setCscj(element.attributeValue("sccj"));
						bean.setJstj(element.attributeValue("jstj"));
						bean.setCailiao("");// 暂时未处理
						bean.setJxxndj(element.attributeValue("jxxndj"));
						bean.setBmcl("");// 暂时未处理
						bean.setDanwei(element.attributeValue("dw2"));
						bean.setGyde(element.attributeValue("sl"));
						bean.setComment("");
						bmList.add(element.attributeValue("partNumber"));
						TechnicsList.add(bean);
					}
				}
				ArrayList PBOMlist = new ArrayList();
				for (int k = 0; k < partList.size(); k++) {
					for (int k2 = 0; k2 < bmList.size(); k2++) {
						String number = partList.get(k).getObject().getTreeCellData().attributeValue("partNumber");
						if (number.equals(bmList.get(k2))) {
							PBOMlist.add(partList.get(k));
						}
					}
				}
				partList.removeAll(PBOMlist);
				for (int k = 0; k < partList.size(); k++) {
					j++;
					Element element3 = partList.get(k).getObject().getTreeCellData();
					NewReportTechnicsBean bean = new NewReportTechnicsBean();
					bean.setIndex(j);
					bean.setMingcheng(element3.attributeValue("partName"));
					bean.setLeibie("");// 暂时不好判断
					bean.setPaihao(element3.attributeValue("XHPH"));
					bean.setGuige(element3.attributeValue("GG"));
					bean.setCscj(element3.attributeValue("SCCJ"));
					bean.setJstj(element3.attributeValue("JSTJ"));
					bean.setCailiao(element3.attributeValue("materialName"));// 暂时未处理
					bean.setJxxndj("");
					bean.setBmcl("");// 暂时未处理
					bean.setDanwei(element3.attributeValue("JLDWMC"));
					bean.setGyde(element3.attributeValue("gysl"));
					bean.setComment("");
					TechnicsList.add(bean);
				}

			}
		}
		String[][] datas = new String[TechnicsList.size()][13];
		for (int m = 0; m < TechnicsList.size(); m++) {
			datas[m][0] = String.valueOf(m + 1);
			datas[m][1] = (String) ((NewReportTechnicsBean) TechnicsList.get(m)).getMingcheng();
			datas[m][2] = (String) ((NewReportTechnicsBean) TechnicsList.get(m)).getLeibie();
			datas[m][3] = (String) ((NewReportTechnicsBean) TechnicsList.get(m)).getPaihao();
			datas[m][4] = (String) ((NewReportTechnicsBean) TechnicsList.get(m)).getGuige();
			datas[m][5] = (String) ((NewReportTechnicsBean) TechnicsList.get(m)).getCscj();
			datas[m][6] = (String) ((NewReportTechnicsBean) TechnicsList.get(m)).getJstj();
			datas[m][7] = (String) ((NewReportTechnicsBean) TechnicsList.get(m)).getCailiao();
			datas[m][8] = (String) ((NewReportTechnicsBean) TechnicsList.get(m)).getJxxndj();
			datas[m][9] = (String) ((NewReportTechnicsBean) TechnicsList.get(m)).getBmcl();
			datas[m][10] = (String) ((NewReportTechnicsBean) TechnicsList.get(m)).getDanwei();
			datas[m][11] = (String) ((NewReportTechnicsBean) TechnicsList.get(m)).getGyde();
			datas[m][12] = (String) ((NewReportTechnicsBean) TechnicsList.get(m)).getComment();
		}
		return datas;

	}



}
