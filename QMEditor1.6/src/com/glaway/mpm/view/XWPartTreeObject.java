package com.glaway.mpm.view;


import com.glaway.mpm.util.*;
import com.glaway.mpm.wcIntf.PBomIntf;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;
import java.util.List;

public class XWPartTreeObject implements XWTreeObject {
	private Element partElement;
	private boolean isAllowed = false;

	public XWPartTreeObject(Element partElement) {
		this.partElement = partElement;
	}

	public Vector expand() throws Exception {
		Vector vec = new Vector();

		String partNumber = XmlUtility.getAttributeValue(this.partElement,"partNumber");
		String partOid = XmlUtility.getAttributeValue(this.partElement,"oid");
		HashMap map = new HashMap();
		map.put("oid", partOid);
		map.put("partNumber",partNumber);
		map.put("category", "normal");

		String reportTechnics1="";
		if (NewTechnicsPart.reportTechnics) {
			reportTechnics1="report";
		}
        map.put("reportTechnics", reportTechnics1);
		//当前可编辑的零部件需要把其所有的工艺文件下载下来. NewTechnicsPart.childPartOid就是当前可编辑零部件的OID
//		if(NewTechnicsPart.childPartOid.equals(partOid)) {
			map.put("isEditable", "true");
//		} else {
//			map.put("isEditable", "false");
//		}
		List<Vector> vector = TechnicsIntf.getTechnicsByPart(map);
		if("SOP".equals(com.glaway.mpm.EditorConfig.startType)){
			if (vector.size() > 0) {
				for (Vector result : vector) {
					String technichsNumber = (String) result.get(4);
					if (WorkSpaceUtil.isExistTechnics(technichsNumber)) {
						String type = "";
						String technicsVersion = null;
						String filepath = WorkSpaceUtil.getTechnicsPath(technichsNumber);
						Document doucment = XmlUtil.getDocument(filepath);
						if (doucment != null) {
							Element technicsElement = doucment.getRootElement().element("QMFawTechnicsInfo");
							if (technicsElement != null) {
								type = technicsElement.attributeValue("technicsType");
								String technicsCategory = technicsElement.attributeValue("technicsCategory");
								technicsVersion = technicsElement.attributeValue("version");
								String batch = technicsElement.attributeValue("PCNO");
								String pplanNumber = technicsElement.attributeValue("pplanNumber");
								String isZhuZhi = technicsElement.attributeValue("ZFFLAG");
								String PPLANTYPE = technicsElement.attributeValue("PPLANTYPE");

								TechnicsMessageTreeObject partObject = new TechnicsMessageTreeObject(partNumber, technichsNumber,
										technicsElement.attributeValue("pplanName"), type, technicsVersion, technicsCategory, batch, pplanNumber, isZhuZhi, PPLANTYPE);
								vec.add(partObject);
							} else {
								technicsElement = doucment.getRootElement().element("XWReportTechnicsInfo");
								if (technicsElement != null) {
									ReportTechnicsTreeObject treeObj = new ReportTechnicsTreeObject(technicsElement.attributeValue("partNumber"),
											technicsElement.attributeValue("pplanNumber"),
											technicsElement.attributeValue("technicsNumber"),
											technicsElement.attributeValue("version"),
											technicsElement.attributeValue("technicsType"),
											technicsElement.attributeValue("batch"),
											"normal",
											technicsElement.attributeValue("pplanName"),
											technicsElement.attributeValue("technicsName"));
									vec.add(treeObj);
								}
							}
						}
					}else{
						String fileName = (String) result.get(0);
						byte[] data = (byte[]) result.get(1);
						if(data != null && data.length > 0){
							if (fileName.toLowerCase().endsWith(".zip")) {
								fileName = fileName.substring(0, fileName.length() - 4);
							}
							String lifecycle = (String) result.get(2);
							String version = (String) result.get(3);
							File f = new File(WorkSpaceUtil.getCommonTechnicsRootPath() + "\\" + fileName);
							if (f.exists()) {
								WorkSpaceUtil.delete(f);
							}

							String filepath = WorkSpaceUtil.createTechnicsDirectory(fileName);
							TechnicsReleaseUtil.unZip(data, filepath);
							String technicsNumber = WorkSpaceUtil.getTechnicsNumber(fileName);
							Document doc = WorkSpaceUtil.getTechnicsDocumentByTechnicsNumber(technicsNumber);
							Element technicsElement = XmlUtility.getTechnicsElement(doc);
							XmlUtility.setAttributeValue(technicsElement, "version", version);
							XmlUtility.setAttributeValue(technicsElement, "partOid", partOid);
							XmlUtility.setAttributeValue(technicsElement, "lifecycle", lifecycle);
							firstSaveProcess(technicsElement);
							String type = technicsElement.attributeValue("technicsType");
							String technicsCategory = technicsElement.attributeValue("technicsCategory");
							String technicsVersion = technicsElement.attributeValue("version");
							String batch = technicsElement.attributeValue("PCNO");
							String pplanNumber = technicsElement.attributeValue("pplanNumber");
							String isZhuZhi = technicsElement.attributeValue("ZFFLAG");
							String PPLANTYPE = technicsElement.attributeValue("PPLANTYPE");
							TechnicsMessageTreeObject messageTreeObject = new TechnicsMessageTreeObject(partNumber, technichsNumber,
									technicsElement.attributeValue("pplanName"), type, technicsVersion, technicsCategory, batch, pplanNumber, isZhuZhi, PPLANTYPE);
							vec.add(messageTreeObject);
						}
					}
				}
			}
		}else{
			if (vector.size() > 0) {
				for (Vector result : vector) {
					String technichsNumber = (String) result.get(4);
					if (WorkSpaceUtil.isExistTechnics(technichsNumber)) {
						String type = "";
						String technicsVersion = null;
						String filepath = WorkSpaceUtil.getTechnicsPath(technichsNumber);
						Document doucment = XmlUtil.getDocument(filepath);
						if (doucment != null) {
							Element technicsElement = doucment.getRootElement().element("QMFawTechnicsInfo");
							if (technicsElement != null) {
								type = technicsElement.attributeValue("technicsType");
								String technicsCategory = technicsElement.attributeValue("technicsCategory");
								technicsVersion = technicsElement.attributeValue("version");
								String batch = technicsElement.attributeValue("PCNO");
								String pplanNumber = technicsElement.attributeValue("pplanNumber");
								String isZhuZhi = technicsElement.attributeValue("ZFFLAG");
								String PPLANTYPE = technicsElement.attributeValue("PPLANTYPE");

								TechnicsMessageTreeObject partObject = new TechnicsMessageTreeObject(partNumber, technichsNumber,
										technicsElement.attributeValue("pplanName"), type, technicsVersion, technicsCategory, batch, pplanNumber, isZhuZhi, PPLANTYPE);
								vec.add(partObject);
							} else {
								technicsElement = doucment.getRootElement().element("XWReportTechnicsInfo");
								if (technicsElement != null) {
									ReportTechnicsTreeObject treeObj = new ReportTechnicsTreeObject(technicsElement.attributeValue("partNumber"),
											technicsElement.attributeValue("pplanNumber"),
											technicsElement.attributeValue("technicsNumber"),
											technicsElement.attributeValue("version"),
											technicsElement.attributeValue("technicsType"),
											technicsElement.attributeValue("batch"),
											"normal",
											technicsElement.attributeValue("pplanName"),
											technicsElement.attributeValue("technicsName"));
									vec.add(treeObj);
								}
							}

						}
					}
				}
			}

			//判断该零部件是有保存PBOM XML，如果有，则读取其XML的的结构,如果没有，则直接读取其子元素
			String hasXml = XmlUtility.getAttributeValue(this.partElement, "isHasXml");
			if ("true".equals(hasXml)) {
				//如果当前节点不是PBOM树的第一个part节点
				String oid = this.partElement.attributeValue("oid");
				if (!oid.equals(NewTechnicsPart.getPbomOid())) {
					String[] str = {oid};
//				byte[] childBytes = PBomIntf.getPBomXML(oid);
					byte[] childBytes = PbomUtil.generatePbomBytes(str);
					if (childBytes != null) {
						Document doc = BomXMLUtil.getDocument(childBytes);
						Element productElement = BomXMLUtil.getProduct(doc);
						Element mainPart = BomXMLUtil.getMainPart(productElement);

						List list = BomXMLUtil.getChildProducts(mainPart);
						if ((list != null) && (!list.isEmpty())) {
							for (int i = 0; i < list.size(); i++) {
								Element part = (Element) list.get(i);
								XWPartTreeObject partObject = new XWPartTreeObject(part);
								vec.add(partObject);
							}
						}
						downloadTechnics(oid, partElement);
						//updateTechnics(childBytes);
					}
				} else {
//				byte[] childBytes = PBomIntf.getPBomXML(oid);
					String oid1 = this.partElement.attributeValue("partNumber");
					List list = BomXMLUtil.getChildProducts(this.partElement);
					if ((list != null) && (!list.isEmpty())) {
						for (int i = 0; i < list.size(); i++) {
							Element part = (Element) list.get(i);
							XWPartTreeObject partObject = new XWPartTreeObject(part);
							vec.add(partObject);
						}
					}
					downloadTechnics(oid1, partElement);
//				updateTechnics(childBytes);
				}

			} else {
				List list = BomXMLUtil.getChildProducts(this.partElement);
				String oid = this.partElement.attributeValue("partNumber");
				if ((list != null) && (!list.isEmpty())) {
					for (int i = 0; i < list.size(); i++) {
						Element part = (Element) list.get(i);
						XWPartTreeObject partObject = new XWPartTreeObject(part);
						vec.add(partObject);
					}
				}
				downloadTechnics(oid, partElement);
//			 updateTechnics(childBytes);
			}

		}

		return vec;
	}
	/**
     * 下载普通工艺
     *
     * @param partNumber
     */
    public void downloadTechnics(String partNumber, Element part) {
        if ( (partNumber != null) && (partNumber.trim().length() > 0)) {
            String techXMLPath = null;
            try {
                String filepath = null;
                String oid=part.attributeValue("oid");
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("partNumber", partNumber);
                map.put("oid", oid);
                map.put("category", "normal");
                String reportTechnics1="";
                if(NewTechnicsPart.reportTechnics){
					reportTechnics1 = "report";
				}
                map.put("reportTechnics", reportTechnics1);
                //当前可编辑的零部件需要把其所有的工艺文件下载下来. NewTechnicsPart.childPartOid就是当前可编辑零部件的OID
                map.put("isEditable", "true");

                List<Vector> list = TechnicsIntf.getTechnicsByPart(map);
                if (list.size() > 0) {
                    for(int i=0;i<list.size();i++) {
                        Vector result = list.get(i);
                        if ((result != null) && (result.size() == 6)) {
                            String fileName = (String) result.get(0);
                            byte[] data = (byte[]) result.get(1);
                            if ((data == null) || (data.length <= 0)) {
                                return;
                            }
                            if (fileName.toLowerCase().endsWith(".zip")) {
                                fileName = fileName.substring(0, fileName.length() - 4);
                            }
                            String lifecycle = (String) result.get(2);
                            String version = (String) result.get(3);
                            File f = new File(WorkSpaceUtil.getCommonTechnicsRootPath() + "\\" + fileName);
                                if (f.exists()) {
                                    WorkSpaceUtil.delete(f);
                            }

                            filepath = WorkSpaceUtil.createTechnicsDirectory(fileName);
                            TechnicsReleaseUtil.unZip(data, filepath);
                            String technicsNumber = WorkSpaceUtil.getTechnicsNumber(fileName);
                            Document doc = WorkSpaceUtil.getTechnicsDocumentByTechnicsNumber(technicsNumber);
                            Element ele = XmlUtility.getTechnicsElement(doc);
                            XmlUtility.setAttributeValue(ele, "version", version);
                            XmlUtility.setAttributeValue(ele, "partOid", part.attributeValue("oid"));
                            XmlUtility.setAttributeValue(ele, "lifecycle", lifecycle);
                            firstSaveProcess(ele);
                            techXMLPath = WorkSpaceUtil.getTechnicsPath(technicsNumber);
                            File file = new File(techXMLPath);
                            if (!file.exists()) {
//                                JOptionPane.showMessageDialog(NewTechnicsPa, "下载工艺出现错误！", "提示", 1);
                                File technDIR = new File(filepath);
                                if ((technDIR != null) && (technDIR.exists()))
                                    WorkSpaceUtil.delete(technDIR);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (techXMLPath != null) {
                    File file = new File(techXMLPath);
                    if (!file.exists())
                        try {
                            WorkSpaceUtil.delete(file);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                }
            }
        }
    }

	/**
	 * 根据PBOM xml 更新工艺（下载工艺压缩包）
	 *
	 * @param bytes
	 * @return
	 */
	private Set<String> updateTechnics(byte[] bytes) {
		Set<String> oids = null;
		String techXMLPath = null;
		try {
			if (bytes != null) {
				Document doc = BomXMLUtil.getDocument(bytes);
				if ((doc != null) && (NewTechnicsPart.creatorOid != null) && (NewTechnicsPart.creatorOid.trim().length() > 0)) {
					HashMap allPartOid = new HashMap();
					Element productElement = BomXMLUtil.getProduct(doc);
					Element mainPart = BomXMLUtil.getMainPart(productElement);
//					System.out.println(mainPart.asXML());

					//查询当前PBOM下所有的部件OID哪些是当前用户需要编制工艺的
					List<String> allOid = new ArrayList<String>();
					BomXMLUtil.getAllPartOid(mainPart, allOid);
//					allOid = TechnicsIntf.getCurrentUserTaskPartOidInfo(allOid);
					if(NewTechnicsPart.childPartOid != null && !"".equals(NewTechnicsPart.childPartOid)) {
						allOid.remove(NewTechnicsPart.childPartOid);
					}

					BomXMLUtil.filterPBOM(mainPart, NewTechnicsPart.creatorOid, allPartOid);

					oids = allPartOid.keySet();
					if ((allPartOid != null) && (allPartOid.size() > 0)) {
						Set set = allPartOid.keySet();
						if (set != null) {
							Iterator it = set.iterator();
							if (it != null) {
								while (it.hasNext()) {
									String partOid = (String) it.next();
									if ((partOid != null) && (partOid.trim().length() > 0)) {

//										if(allOid.contains(partOid)) {
//											continue;
//										}

										String partNumber = (String) allPartOid.get(partOid);

										if (partNumber != null) {
											HashMap map = new HashMap();
											map.put("oid", partOid);
											map.put("partNumber", partNumber);
											map.put("category", "normal");


											String reportTechnics1="";
											if (NewTechnicsPart.reportTechnics) {
												reportTechnics1="report";
											}
									        map.put("reportTechnics", reportTechnics1);
											//当前可编辑的零部件需要把其所有的工艺文件下载下来. NewTechnicsPart.childPartOid就是当前可编辑零部件的OID
//											if(NewTechnicsPart.childPartOid.equals(partOid)) {
												map.put("isEditable", "true");
//											} else {
//												map.put("isEditable", "false");
//											}

											List<Vector> vector = TechnicsIntf.getTechnicsByPart(map);
											if (vector.size() > 0) {
												for (Vector result : vector) {
													if ((result != null) && (result.size() == 6)) {
														String technicsNumber = (String) result.get(4);
														String filePath = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
														boolean flag = false;
														if (filePath != null) {
//															if (SwingUtil.showConfirmDialog(partNumber+":2当前工艺数据已存在，是否覆盖？",
//																			Constants.TIP, 2) == JOptionPane.YES_OPTION) {
																WorkSpaceUtil.delete(new File(filePath));
																flag = true;
//															}
														} else {
															flag = true;
														}
														if (flag) {
															String fileName = (String) result.get(0);
															byte[] data = (byte[]) result.get(1);
															if ((data != null) && (data.length > 0)) {
																if (fileName.toLowerCase().endsWith(".zip")) {
																	fileName = fileName.substring(0, fileName.length() - 4);
																}
																String lifecycle = (String) result.get(2);
																String version = (String) result.get(3);
																String filepath = WorkSpaceUtil.createTechnicsDirectory(fileName);

																techXMLPath = filepath;
																TechnicsReleaseUtil.unZip(data, filepath);
																Document document = WorkSpaceUtil.getTechnicsDocumentByTechnicsNumber(technicsNumber);
																Element ele = XmlUtility.getTechnicsElement(document);
																XmlUtility.setAttributeValue(ele, "version", version);
																XmlUtility.setAttributeValue(ele, "lifecycle", lifecycle);
																firstSaveProcess(ele);
																String temp = WorkSpaceUtil.getTechnicsPath(technicsNumber);
																if(temp != null && temp.length() > 0) {
																	File file = new File(temp);
																	if (!file.exists()) {
																		File technDIR = new File(techXMLPath);
																		if ((technDIR != null) && (technDIR.exists()))
																			WorkSpaceUtil.delete(technDIR);
																	} else {
																		techXMLPath = null;
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (techXMLPath != null) {
				File technDIR = new File(techXMLPath);
				if ((technDIR != null) && (technDIR.exists()))
					try {
						WorkSpaceUtil.delete(technDIR);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
			}
		}
		return oids;
	}

	public void firstSaveProcess(Element newone) {
		if (newone == null)
			return;
		Document doc = newone.getDocument();
		if (doc != null) {
			try {
				Element techele = XmlUtility.getTechnicsElement(doc);
				XmlUtility.hasKeyStepInTechnics(techele);
				String technicsNumber = XmlUtility.getAttributeValue(techele, "technicsNumber");
				String technicsName = XmlUtility.getAttributeValue(techele, "technicsName");
				String technicsCategory = XmlUtility.getAttributeValue(techele, "technicsCategory");
				String s = XmlUtility.getCurrentTime();
				XmlUtility.setAttributeValue(techele, "modifyTime", s);
				//NewTechnicsPart.technicsMasterJPanel.setCreatTimeJLabel(s);
				RecentSaveUtil.addRecent(technicsNumber);
				OutputFormat format = OutputFormat.createPrettyPrint();
				format.setTrimText(false);
				format.setEncoding("GBK");
				String path;
				if ("rework".equals(technicsCategory)) {
					path = WorkSpaceUtil.getReWorkTechnicsPathByTechnicsName(technicsNumber, technicsName);
				} else if ("temp".equals(technicsCategory)) {
					path = WorkSpaceUtil.getTempTechnicsPathByTechnicsName(technicsNumber, technicsName);
				} else {
					path = WorkSpaceUtil.getTechnicsPath(technicsNumber);
				}
				XMLWriter writer = new XMLWriter(new FileOutputStream(path), format);
				writer.write(doc);
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Image getCloseImage() {
		return getOpenImage();
	}

	public String getDisplayName() {
		return BomXMLUtil.getPartIdentufy(this.partElement);
	}

	public Image getOpenImage() {
		String type = XmlUtility.getAttributeValue(this.partElement, "partType");
		String iconName = "/images/part.gif";
		if ((type != null) && (type.trim().length() > 0)) {
			if (type.equalsIgnoreCase("middle2")) {//工艺中间件
				iconName = "/images/middle.gif";
			}
			if (type.equalsIgnoreCase("assistant")) {
				iconName = "/images/fujian.gif";
			}
			if (type.equalsIgnoreCase("zuhe")) {//工艺组合件
				iconName = "/images/assist.gif";
			}
			if (type.equalsIgnoreCase("mp")) {//毛坯件
				iconName = "/images/fujian.gif";
			}
		}
		ImageIcon icon = new ImageIcon(getClass().getResource(iconName));
		return icon.getImage();
	}

	public String getTipNoteText() {
		return BomXMLUtil.getPartIdentufy(this.partElement);
	}

	public Element getTreeCellData() {
		return this.partElement;
	}

	public int compareTo(Object arg0) {
		if (!(arg0 instanceof XWPartTreeObject)) {
			throw new RuntimeException("树节点类型不匹配！");
		}
		XWPartTreeObject temp = (XWPartTreeObject) arg0;
		String number = temp.getTreeCellData().attributeValue("partNumber");
		String me = this.partElement.attributeValue("partNumber");
		return number.compareTo(me);
	}

	public void setTreeCellData(Element data) {
		this.partElement = data;
	}

	public void setAllowed(boolean isAllowed) {
		this.isAllowed = isAllowed;
	}

	public boolean isAllowed() {
		return this.isAllowed;
	}

	@Override
	public String getEqualsName() {
		// TODO Auto-generated method stub
		return this.getDisplayName();
	}
}
