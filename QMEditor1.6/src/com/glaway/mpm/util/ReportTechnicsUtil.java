package com.glaway.mpm.util;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.TechnicsMessageTreeObject;
import com.glaway.mpm.view.XWPartTreeObject;
import com.glaway.mpm.view.XWTreeNode;
import com.glaway.mpm.view.XWTreeObject;
import com.glaway.mpm.wcIntf.TechnicsIntf;

public class ReportTechnicsUtil {

	public static void processAttributes(Element techElement, NewTechnicsPart frame, List<List<String>> list, String type) {
		XWTreeNode partNode = frame.xwPartTreePanel.getSelectedTreeNode();
		List<XWTreeNode> partList = new ArrayList<XWTreeNode>();
		partList.add(partNode);
		getAllChildPart(partList, partNode);
		if("仪器仪表明细表".equals(type)) {
			processYiqiyibiao(techElement, frame, list, partList);
		} else if ("工艺装备明细表".equals(type)) {
			processGongZhuang(techElement, frame, list, partList);
		} else if ("非标仪器仪表、设备明细表".equals(type)) {
			processFeiBiaoZhunYiqiyibiao(techElement, frame, list, partList);
		} else if ("标准刀量具明细表".equals(type)) {
			processDaoJu(techElement, frame, list, partList);
		}
	}

	public static void processAttributes2(Element techElement, NewTechnicsPart frame, List<List<String>> list, String type) {
		XWTreeNode partNode = frame.xwPartTreePanel.getSelectedTreeNode();
		List<XWTreeNode> partList = new ArrayList<XWTreeNode>();
		partList.add(partNode);
		getAllChildPart(partList, partNode);
		try {
			if("仪器仪表明细表".equals(type)) {
				processYiqiyibiao2(techElement, list);
			} else if ("工艺装备明细表".equals(type)) {
				processGongZhuang2(techElement, list);
			} else if ("非标仪器仪表、设备明细表".equals(type)) {
				processFeiBiaoZhunYiqiyibiao2(techElement, list);
			} else if ("标准刀量具明细表".equals(type)) {
				processDaoJu2(techElement, list);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 处理仪器仪表信息，数据来源于工艺编辑器中PBOM树中工艺节点对象。
	 *
	 * @param reportTechElement
	 * @param frame
	 * @param attrList
	 * @param partList
	 */
	private static void processYiqiyibiao(Element reportTechElement, NewTechnicsPart frame, List<List<String>> attrList, List<XWTreeNode> partList) {
		Element data = XmlUtility.getChildElements(reportTechElement, "data");
		Enumeration num = null;
		XWTreeNode child = null;
		XWTreeObject obj = null;
		Element techEle = null;
		Element dataEle = null;
		for (XWTreeNode xwTreeNode : partList) {
			num = xwTreeNode.children();
			while(num.hasMoreElements()) {
				child = (XWTreeNode)num.nextElement();
				obj = child.getObject();
				if(obj instanceof TechnicsMessageTreeObject) {
					techEle = obj.getTreeCellData();

					List<Element> steps = techEle.selectNodes("steps/QMProcedureInfo");
					//开始循环遍历所有工序
					for (Element procedure : steps) {
						//获取当前工序的所有标准仪器仪表元素
						List<Element> dlist = procedure.selectNodes("sdashboard/QMSDashboardInfo");
    					if(dlist != null) {
    						for (Element element : dlist) {
    							dataEle = DocumentHelper.createElement("dataItem");
    							for(List<String> list: attrList) {
    								String attriName = list.get(0);
    								String dataFrom = list.get(2);
    								if("partNumber".equals(attriName)) {
    									XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partNumber"));
    								} else if ("partName".equals(attriName)) {
    									XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partName"));
    								} else if ("pbomState".equals(attriName)) {
    									XmlUtility.setAttributeValue(dataEle, attriName, "");
    								} else {
    									if("technics".equals(dataFrom)) {
    										XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue(attriName));
    									} else if ("procedure".equals(dataFrom)) {
    										XmlUtility.setAttributeValue(dataEle, attriName, procedure.attributeValue(attriName));
    									} else if ("element".equals(dataFrom)) {
    										XmlUtility.setAttributeValue(dataEle, attriName, element.attributeValue(attriName));
    									}
    								}
    							}
    							data.add(dataEle);
							}
    					}

    					//获取当前工序的所有工步的标准仪器仪表元素
    					List<Element> stepList = procedure.selectNodes("paces/QMProcedureInfo/sdashboard/QMSDashboardInfo");
    					for(int i = 0;i < stepList.size();i++) {
    						Element element = stepList.get(i);
    						dataEle = DocumentHelper.createElement("dataItem");
							for(List<String> list: attrList) {
								String attriName = list.get(0);
								String dataFrom = list.get(2);
								if("partNumber".equals(attriName)) {
									XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partNumber"));
								} else if ("partName".equals(attriName)) {
									XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partName"));
								} else if ("pbomState".equals(attriName)) {
									XmlUtility.setAttributeValue(dataEle, attriName, "");
								} else {
									if("technics".equals(dataFrom)) {
										XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue(attriName));
									} else if ("procedure".equals(dataFrom)) {
										XmlUtility.setAttributeValue(dataEle, attriName, procedure.attributeValue(attriName));
									} else if ("element".equals(dataFrom)) {
										XmlUtility.setAttributeValue(dataEle, attriName, element.attributeValue(attriName));
									}
								}
							}
							data.add(dataEle);
    					}
					}
				}
			}
		}

	}

	/**
	 * 处理仪器仪表信息，数据来源于PDM系统。
	 *
	 * @param reportTechElement
	 * @param frame
	 * @param attrList
	 * @param partList
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	private static void processYiqiyibiao2(Element reportTechElement, List<List<String>> attrList)
			throws RemoteException, InvocationTargetException {
		String partOid = reportTechElement.attributeValue("partOid");
		partOid = "OR:wt.part.WTPart:"+partOid;
		List<Element> technicsList = TechnicsIntf.getAllTechnicsElementByPart(partOid);
		Element data = XmlUtility.getChildElements(reportTechElement, "data");
		Element dataEle = null;
		for(Element techEle : technicsList) {
			List<Element> steps = techEle.selectNodes("steps/QMProcedureInfo");
			//开始循环遍历所有工序
			for (Element procedure : steps) {
				//获取当前工序的所有标准仪器仪表元素
				List<Element> dlist = procedure.selectNodes("sdashboard/QMSDashboardInfo");
				if(dlist != null) {
					for (Element element : dlist) {
						dataEle = DocumentHelper.createElement("dataItem");
						for(List<String> list: attrList) {
							String attriName = list.get(0);
							String dataFrom = list.get(2);
							if("partNumber".equals(attriName)) {
								XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partNumber"));
							} else if ("partName".equals(attriName)) {
								XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partName"));
							} else if ("pbomState".equals(attriName)) {
								XmlUtility.setAttributeValue(dataEle, attriName, "");
							} else {
								if("technics".equals(dataFrom)) {
									XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue(attriName));
								} else if ("procedure".equals(dataFrom)) {
									XmlUtility.setAttributeValue(dataEle, attriName, procedure.attributeValue(attriName));
								} else if ("element".equals(dataFrom)) {
									XmlUtility.setAttributeValue(dataEle, attriName, element.attributeValue(attriName));
								}
							}
						}
						data.add(dataEle);
					}
				}

				//获取当前工序的所有工步的标准仪器仪表元素
				List<Element> stepList = procedure.selectNodes("paces/QMProcedureInfo/sdashboard/QMSDashboardInfo");
				for(int i = 0;i < stepList.size();i++) {
					Element element = stepList.get(i);
					dataEle = DocumentHelper.createElement("dataItem");
					for(List<String> list: attrList) {
						String attriName = list.get(0);
						String dataFrom = list.get(2);
						if("partNumber".equals(attriName)) {
							XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partNumber"));
						} else if ("partName".equals(attriName)) {
							XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partName"));
						} else if ("pbomState".equals(attriName)) {
							XmlUtility.setAttributeValue(dataEle, attriName, "");
						} else {
							if("technics".equals(dataFrom)) {
								XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue(attriName));
							} else if ("procedure".equals(dataFrom)) {
								XmlUtility.setAttributeValue(dataEle, attriName, procedure.attributeValue(attriName));
							} else if ("element".equals(dataFrom)) {
								XmlUtility.setAttributeValue(dataEle, attriName, element.attributeValue(attriName));
							}
						}
					}
					data.add(dataEle);
				}
			}

		}

	}

	/**
	 * 处理率非标准仪器仪表，数据来源于工艺编辑器PBOM树中的工艺节点对象。
	 *
	 * @param reportTechElement
	 * @param frame
	 * @param attrList
	 * @param partList
	 */
	private static void processFeiBiaoZhunYiqiyibiao(Element reportTechElement, NewTechnicsPart frame, List<List<String>> attrList, List<XWTreeNode> partList) {
		Element data = XmlUtility.getChildElements(reportTechElement, "data");
		Enumeration num = null;
		XWTreeNode child = null;
		XWTreeObject obj = null;
		Element techEle = null;
		Element dataEle = null;
		for (XWTreeNode xwTreeNode : partList) {
			num = xwTreeNode.children();
			while(num.hasMoreElements()) {
				child = (XWTreeNode)num.nextElement();
				obj = child.getObject();
				if(obj instanceof TechnicsMessageTreeObject) {
					techEle = obj.getTreeCellData();

					List<Element> steps = techEle.selectNodes("steps/QMProcedureInfo");
					//开始循环遍历所有工序
					for (Element procedure : steps) {
						//获取当前工序的所有标准仪器仪表元素
						List<Element> dlist = procedure.selectNodes("unsdashboard/QMUnSDashboardInfo");
    					if(dlist != null) {
    						for (Element element : dlist) {
    							dataEle = DocumentHelper.createElement("dataItem");
    							for(List<String> list: attrList) {
    								String attriName = list.get(0);
    								String dataFrom = list.get(2);
    								if("partNumber".equals(attriName)) {
    									XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partNumber"));
    								} else if ("partName".equals(attriName)) {
    									XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partName"));
    								} else if ("pbomState".equals(attriName)) {
    									XmlUtility.setAttributeValue(dataEle, attriName, "");
    								} else {
    									if("technics".equals(dataFrom)) {
    										XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue(attriName));
    									} else if ("procedure".equals(dataFrom)) {
    										XmlUtility.setAttributeValue(dataEle, attriName, procedure.attributeValue(attriName));
    									} else if ("element".equals(dataFrom)) {
    										XmlUtility.setAttributeValue(dataEle, attriName, element.attributeValue(attriName));
    									}
    								}
    							}
    							data.add(dataEle);
							}
    					}

    					//获取当前工序的所有工步的标准仪器仪表元素
    					List<Element> stepList = procedure.selectNodes("paces/QMProcedureInfo/unsdashboard/QMUnSDashboardInfo");
    					for(int i = 0;i < stepList.size();i++) {
    						Element element = stepList.get(i);
    						dataEle = DocumentHelper.createElement("dataItem");
							for(List<String> list: attrList) {
								String attriName = list.get(0);
								String dataFrom = list.get(2);
								if("partNumber".equals(attriName)) {
									XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partNumber"));
								} else if ("partName".equals(attriName)) {
									XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partName"));
								} else if ("pbomState".equals(attriName)) {
									XmlUtility.setAttributeValue(dataEle, attriName, "");
								} else {
									if("technics".equals(dataFrom)) {
										XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue(attriName));
									} else if ("procedure".equals(dataFrom)) {
										XmlUtility.setAttributeValue(dataEle, attriName, procedure.attributeValue(attriName));
									} else if ("element".equals(dataFrom)) {
										XmlUtility.setAttributeValue(dataEle, attriName, element.attributeValue(attriName));
									}
								}
							}
							data.add(dataEle);
    					}
					}
				}
			}
		}
	}

	/**
	 * 处理非标准仪器仪表信息，数据来源于PDM系统。
	 *
	 * @param reportTechElement
	 * @param frame
	 * @param attrList
	 * @param partList
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	private static void processFeiBiaoZhunYiqiyibiao2(Element reportTechElement, List<List<String>> attrList)
			throws RemoteException, InvocationTargetException {
		String partOid = reportTechElement.attributeValue("partOid");
		partOid = "OR:wt.part.WTPart:"+partOid;
		List<Element> technicsList = TechnicsIntf.getAllTechnicsElementByPart(partOid);
		Element data = XmlUtility.getChildElements(reportTechElement, "data");
		Element dataEle = null;
		for(Element techEle : technicsList) {
			List<Element> steps = techEle.selectNodes("steps/QMProcedureInfo");
			//开始循环遍历所有工序
			for (Element procedure : steps) {
				//获取当前工序的所有标准仪器仪表元素
				List<Element> dlist = procedure.selectNodes("unsdashboard/QMUnSDashboardInfo");
				if(dlist != null) {
					for (Element element : dlist) {
						dataEle = DocumentHelper.createElement("dataItem");
						for(List<String> list: attrList) {
							String attriName = list.get(0);
							String dataFrom = list.get(2);
							if("partNumber".equals(attriName)) {
								XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partNumber"));
							} else if ("partName".equals(attriName)) {
								XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partName"));
							} else if ("pbomState".equals(attriName)) {
								XmlUtility.setAttributeValue(dataEle, attriName, "");
							} else {
								if("technics".equals(dataFrom)) {
									XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue(attriName));
								} else if ("procedure".equals(dataFrom)) {
									XmlUtility.setAttributeValue(dataEle, attriName, procedure.attributeValue(attriName));
								} else if ("element".equals(dataFrom)) {
									XmlUtility.setAttributeValue(dataEle, attriName, element.attributeValue(attriName));
								}
							}
						}
						data.add(dataEle);
					}
				}

				//获取当前工序的所有工步的标准仪器仪表元素
				List<Element> stepList = procedure.selectNodes("paces/QMProcedureInfo/unsdashboard/QMUnSDashboardInfo");
				for(int i = 0;i < stepList.size();i++) {
					Element element = stepList.get(i);
					dataEle = DocumentHelper.createElement("dataItem");
					for(List<String> list: attrList) {
						String attriName = list.get(0);
						String dataFrom = list.get(2);
						if("partNumber".equals(attriName)) {
							XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partNumber"));
						} else if ("partName".equals(attriName)) {
							XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partName"));
						} else if ("pbomState".equals(attriName)) {
							XmlUtility.setAttributeValue(dataEle, attriName, "");
						} else {
							if("technics".equals(dataFrom)) {
								XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue(attriName));
							} else if ("procedure".equals(dataFrom)) {
								XmlUtility.setAttributeValue(dataEle, attriName, procedure.attributeValue(attriName));
							} else if ("element".equals(dataFrom)) {
								XmlUtility.setAttributeValue(dataEle, attriName, element.attributeValue(attriName));
							}
						}
					}
					data.add(dataEle);
				}
			}
		}
	}

	/**
	 * 处理工艺装备信息，数据来源于工艺编辑器PBOM树中工艺节点对象。
	 *
	 * @param reportTechElement
	 * @param frame
	 * @param attrList
	 * @param partList
	 */
	private static void processGongZhuang(Element reportTechElement, NewTechnicsPart frame, List<List<String>> attrList, List<XWTreeNode> partList) {
		Element data = XmlUtility.getChildElements(reportTechElement, "data");
		Enumeration num = null;
		XWTreeNode child = null;
		XWTreeObject obj = null;
		Element techEle = null;
		Element dataEle = null;
		for (XWTreeNode xwTreeNode : partList) {
			num = xwTreeNode.children();
			while(num.hasMoreElements()) {
				child = (XWTreeNode)num.nextElement();
				obj = child.getObject();
				if(obj instanceof TechnicsMessageTreeObject) {
					techEle = obj.getTreeCellData();

					List<Element> steps = techEle.selectNodes("steps/QMProcedureInfo");
					//开始循环遍历所有工序
					for (Element procedure : steps) {
						//获取当前工序的所有标准仪器仪表元素
						List<Element> dlist = procedure.selectNodes("equips/QMEquipmentInfo");
    					if(dlist != null) {
    						for (Element element : dlist) {
    							dataEle = DocumentHelper.createElement("dataItem");
    							for(List<String> list: attrList) {
    								String attriName = list.get(0);
    								String dataFrom = list.get(2);
    								if("partNumber".equals(attriName)) {
    									XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partNumber"));
    								} else if ("partName".equals(attriName)) {
    									XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partName"));
    								} else if ("pbomState".equals(attriName)) {
    									XmlUtility.setAttributeValue(dataEle, attriName, "");
    								} else {
    									if("technics".equals(dataFrom)) {
    										XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue(attriName));
    									} else if ("procedure".equals(dataFrom)) {
    										XmlUtility.setAttributeValue(dataEle, attriName, procedure.attributeValue(attriName));
    									} else if ("element".equals(dataFrom)) {
    										XmlUtility.setAttributeValue(dataEle, attriName, element.attributeValue(attriName));
    									}
    								}
    							}
    							data.add(dataEle);
							}
    					}

    					//获取当前工序的所有工步的标准仪器仪表元素
    					List<Element> stepList = procedure.selectNodes("paces/QMProcedureInfo/equips/QMEquipmentInfo");
    					for(int i = 0;i < stepList.size();i++) {
    						Element element = stepList.get(i);
    						dataEle = DocumentHelper.createElement("dataItem");
							for(List<String> list: attrList) {
								String attriName = list.get(0);
								String dataFrom = list.get(2);
								if("partNumber".equals(attriName)) {
									XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partNumber"));
								} else if ("partName".equals(attriName)) {
									XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partName"));
								} else if ("pbomState".equals(attriName)) {
									XmlUtility.setAttributeValue(dataEle, attriName, "");
								} else {
									if("technics".equals(dataFrom)) {
										XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue(attriName));
									} else if ("procedure".equals(dataFrom)) {
										XmlUtility.setAttributeValue(dataEle, attriName, procedure.attributeValue(attriName));
									} else if ("element".equals(dataFrom)) {
										XmlUtility.setAttributeValue(dataEle, attriName, element.attributeValue(attriName));
									}
								}
							}
							data.add(dataEle);
    					}
					}
				}
			}
		}
	}

	/**
	 * 处理工艺装备信息，数据来源于PDM系统。
	 *
	 * @param reportTechElement
	 * @param frame
	 * @param attrList
	 * @param partList
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	private static void processGongZhuang2(Element reportTechElement,List<List<String>> attrList)
			throws RemoteException, InvocationTargetException {
		String partOid = reportTechElement.attributeValue("partOid");
		partOid = "OR:wt.part.WTPart:"+partOid;
		List<Element> technicsList = TechnicsIntf.getAllTechnicsElementByPart(partOid);
		Element data = XmlUtility.getChildElements(reportTechElement, "data");
		Element dataEle = null;
		for(Element techEle : technicsList) {
			List<Element> steps = techEle.selectNodes("steps/QMProcedureInfo");
			//开始循环遍历所有工序
			for (Element procedure : steps) {
				//获取当前工序的所有标准仪器仪表元素
				List<Element> dlist = procedure.selectNodes("equips/QMEquipmentInfo");
				if(dlist != null) {
					for (Element element : dlist) {
						dataEle = DocumentHelper.createElement("dataItem");
						for(List<String> list: attrList) {
							String attriName = list.get(0);
							String dataFrom = list.get(2);
							if("partNumber".equals(attriName)) {
								XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partNumber"));
							} else if ("partName".equals(attriName)) {
								XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partName"));
							} else if ("pbomState".equals(attriName)) {
								XmlUtility.setAttributeValue(dataEle, attriName, "");
							} else {
								if("technics".equals(dataFrom)) {
									XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue(attriName));
								} else if ("procedure".equals(dataFrom)) {
									XmlUtility.setAttributeValue(dataEle, attriName, procedure.attributeValue(attriName));
								} else if ("element".equals(dataFrom)) {
									XmlUtility.setAttributeValue(dataEle, attriName, element.attributeValue(attriName));
								}
							}
						}
						data.add(dataEle);
					}
				}

				//获取当前工序的所有工步的标准仪器仪表元素
				List<Element> stepList = procedure.selectNodes("paces/QMProcedureInfo/equips/QMEquipmentInfo");
				for(int i = 0;i < stepList.size();i++) {
					Element element = stepList.get(i);
					dataEle = DocumentHelper.createElement("dataItem");
					for(List<String> list: attrList) {
						String attriName = list.get(0);
						String dataFrom = list.get(2);
						if("partNumber".equals(attriName)) {
							XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partNumber"));
						} else if ("partName".equals(attriName)) {
							XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partName"));
						} else if ("pbomState".equals(attriName)) {
							XmlUtility.setAttributeValue(dataEle, attriName, "");
						} else {
							if("technics".equals(dataFrom)) {
								XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue(attriName));
							} else if ("procedure".equals(dataFrom)) {
								XmlUtility.setAttributeValue(dataEle, attriName, procedure.attributeValue(attriName));
							} else if ("element".equals(dataFrom)) {
								XmlUtility.setAttributeValue(dataEle, attriName, element.attributeValue(attriName));
							}
						}
					}
					data.add(dataEle);
				}
			}
		}
	}

	/**
	 * 处理刀具信息，数据来源于工艺编辑器PBOM树中工艺节点对象。
	 *
	 * @param reportTechElement
	 * @param frame
	 * @param attrList
	 * @param partList
	 */
	private static void processDaoJu(Element reportTechElement, NewTechnicsPart frame, List<List<String>> attrList, List<XWTreeNode> partList) {
		Element data = XmlUtility.getChildElements(reportTechElement, "data");
		Enumeration num = null;
		XWTreeNode child = null;
		XWTreeObject obj = null;
		Element techEle = null;
		Element dataEle = null;
		for (XWTreeNode xwTreeNode : partList) {
			num = xwTreeNode.children();
			while(num.hasMoreElements()) {
				child = (XWTreeNode)num.nextElement();
				obj = child.getObject();
				if(obj instanceof TechnicsMessageTreeObject) {
					techEle = obj.getTreeCellData();

					List<Element> steps = techEle.selectNodes("steps/QMProcedureInfo");
					//开始循环遍历所有工序
					for (Element procedure : steps) {
						//获取当前工序的所有标准仪器仪表元素
						List<Element> dlist = procedure.selectNodes("knifeTools/QMKnifeToolInfo");
    					if(dlist != null) {
    						for (Element element : dlist) {
    							dataEle = DocumentHelper.createElement("dataItem");
    							for(List<String> list: attrList) {
    								String attriName = list.get(0);
    								String dataFrom = list.get(2);
    								if("partNumber".equals(attriName)) {
    									XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partNumber"));
    								} else if ("partName".equals(attriName)) {
    									XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partName"));
    								} else if ("pbomState".equals(attriName)) {
    									XmlUtility.setAttributeValue(dataEle, attriName, "");
    								} else {
    									if("technics".equals(dataFrom)) {
    										XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue(attriName));
    									} else if ("procedure".equals(dataFrom)) {
    										XmlUtility.setAttributeValue(dataEle, attriName, procedure.attributeValue(attriName));
    									} else if ("element".equals(dataFrom)) {
    										XmlUtility.setAttributeValue(dataEle, attriName, element.attributeValue(attriName));
    									}
    								}
    							}
    							data.add(dataEle);
							}
    					}

    					//获取当前工序的所有工步的标准仪器仪表元素
    					List<Element> stepList = procedure.selectNodes("paces/QMProcedureInfo/knifeTools/QMKnifeToolInfo");
    					for(int i = 0;i < stepList.size();i++) {
    						Element element = stepList.get(i);
    						dataEle = DocumentHelper.createElement("dataItem");
							for(List<String> list: attrList) {
								String attriName = list.get(0);
								String dataFrom = list.get(2);
								if("partNumber".equals(attriName)) {
									XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partNumber"));
								} else if ("partName".equals(attriName)) {
									XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partName"));
								} else if ("pbomState".equals(attriName)) {
									XmlUtility.setAttributeValue(dataEle, attriName, "");
								} else {
									if("technics".equals(dataFrom)) {
										XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue(attriName));
									} else if ("procedure".equals(dataFrom)) {
										XmlUtility.setAttributeValue(dataEle, attriName, procedure.attributeValue(attriName));
									} else if ("element".equals(dataFrom)) {
										XmlUtility.setAttributeValue(dataEle, attriName, element.attributeValue(attriName));
									}
								}
							}
							data.add(dataEle);
    					}
					}
				}
			}
		}
	}

	/**
	 * 处理刀具信息，数据来源于PDM系统。
	 *
	 * @param reportTechElement
	 * @param frame
	 * @param attrList
	 * @param partList
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	private static void processDaoJu2(Element reportTechElement, List<List<String>> attrList)
			throws RemoteException, InvocationTargetException {
		String partOid = reportTechElement.attributeValue("partOid");
		partOid = "OR:wt.part.WTPart:"+partOid;
		List<Element> technicsList = TechnicsIntf.getAllTechnicsElementByPart(partOid);
		Element data = XmlUtility.getChildElements(reportTechElement, "data");
		Element dataEle = null;
		for(Element techEle : technicsList) {
			List<Element> steps = techEle.selectNodes("steps/QMProcedureInfo");
			//开始循环遍历所有工序
			for (Element procedure : steps) {
				//获取当前工序的所有标准仪器仪表元素
				List<Element> dlist = procedure.selectNodes("knifeTools/QMKnifeToolInfo");
				if(dlist != null) {
					for (Element element : dlist) {
						dataEle = DocumentHelper.createElement("dataItem");
						for(List<String> list: attrList) {
							String attriName = list.get(0);
							String dataFrom = list.get(2);
							if("partNumber".equals(attriName)) {
								XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partNumber"));
							} else if ("partName".equals(attriName)) {
								XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partName"));
							} else if ("pbomState".equals(attriName)) {
								XmlUtility.setAttributeValue(dataEle, attriName, "");
							} else {
								if("technics".equals(dataFrom)) {
									XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue(attriName));
								} else if ("procedure".equals(dataFrom)) {
									XmlUtility.setAttributeValue(dataEle, attriName, procedure.attributeValue(attriName));
								} else if ("element".equals(dataFrom)) {
									XmlUtility.setAttributeValue(dataEle, attriName, element.attributeValue(attriName));
								}
							}
						}
						data.add(dataEle);
					}
				}

				//获取当前工序的所有工步的标准仪器仪表元素
				List<Element> stepList = procedure.selectNodes("paces/QMProcedureInfo/knifeTools/QMKnifeToolInfo");
				for(int i = 0;i < stepList.size();i++) {
					Element element = stepList.get(i);
					dataEle = DocumentHelper.createElement("dataItem");
					for(List<String> list: attrList) {
						String attriName = list.get(0);
						String dataFrom = list.get(2);
						if("partNumber".equals(attriName)) {
							XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partNumber"));
						} else if ("partName".equals(attriName)) {
							XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue("partName"));
						} else if ("pbomState".equals(attriName)) {
							XmlUtility.setAttributeValue(dataEle, attriName, "");
						} else {
							if("technics".equals(dataFrom)) {
								XmlUtility.setAttributeValue(dataEle, attriName, techEle.attributeValue(attriName));
							} else if ("procedure".equals(dataFrom)) {
								XmlUtility.setAttributeValue(dataEle, attriName, procedure.attributeValue(attriName));
							} else if ("element".equals(dataFrom)) {
								XmlUtility.setAttributeValue(dataEle, attriName, element.attributeValue(attriName));
							}
						}
					}
					data.add(dataEle);
				}
			}
		}
	}

	private static void getAllChildPart(List<XWTreeNode> list, XWTreeNode currentPart) {
		Enumeration num = currentPart.children();
		XWTreeNode child = null;
		XWTreeObject obj = null;
		while(num.hasMoreElements()) {
			child = (XWTreeNode)num.nextElement();
			obj = child.getObject();
			if(obj instanceof XWPartTreeObject) {
				list.add(child);
				getAllChildPart(list, child);
			}
		}
	}
}
