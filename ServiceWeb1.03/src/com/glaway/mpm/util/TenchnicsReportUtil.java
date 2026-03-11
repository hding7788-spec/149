package com.glaway.mpm.util;

import com.glaway.mpm.pdf.PDFUtil;
import com.glaway.mpm.processplan.helper.ProcessPlanHelper;
import com.glaway.mpm.sjzyk.LoadConfigAttributes;
import com.glaway.mpm.view.NewReportTechnicsBean;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.method.MethodContext;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.pom.WTConnection;
import wt.session.SessionServerHelper;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.*;


public class TenchnicsReportUtil {
	public static String GYWJML ="工艺文件目录";
	public static String GongZhuang ="工艺装备明细表";
	public static String BiaozhunYiQi ="仪器仪表明细表";
	public static String FeiBiaoZhunYiQi ="非标仪器仪表、设备明细表";
	public static String DaoLiangJu ="标准刀量具明细表";
	public static String WaiXieJian ="外协件明细表";
	public static String GuanJianGongXu ="关键工序明细表";
	public static String FuZhuCaiLiao ="辅助材料定额表";
	public static String CaiLiaoHuiZong ="材料消耗工艺定额明细表";
	public static String GongYiLuXian ="工艺路线表";
	public static String Waigoujian ="外购件（元器件、标准件）消耗工艺定额汇总表";
	public static Object[][] getReportDatas(List<String> partOids, String type) throws Exception{
		if ("工艺文件目录".equals(type)) {
			return getGYWJMLDatas(partOids, type);
		} else if ("工艺装备明细表".equals(type)) {
			return getGongZhuang(partOids, type);
		} else if ("仪器仪表明细表".equals(type)) {
			return getBiaozhunYiQi(partOids, type);
		} else if ("非标仪器仪表、设备明细表".equals(type)) {
			return getFeiBiaoZhunYiQi(partOids, type);
		} else if ("标准刀量具明细表".equals(type)) {
			return getDaoLiangJu(partOids, type);
		} else if ("外协件明细表".equals(type)) {
			return getWaiXieJian(partOids, type);
		} else if ("关键工序明细表".equals(type)) {
			return getGuanJianGongXu(partOids, type);
		} else if ("辅助材料定额表".equals(type)) {
			return  getFuZhuCaiLiao(partOids, type);
		} else if ("辅助材料定额汇总表".equals(type)) {
			return getFuZhuCaiLiaoHuiZong(partOids, type);
		} else if ("材料消耗工艺定额明细表".equals(type)) {
			return  getCaiLiaoHuiZong(partOids, type);
		}  else if ("工艺路线表".equals(type)) {
			return  getGongYiLuXian(partOids, type);
		} else if ("外购件（元器件、标准件）消耗工艺定额汇总表".equals(type)) {
			return getWaigoujian(partOids, type);
		}

		return null;
	}

	private static Object[][] getFeiBiaoZhunYiQi(List<String> partOids, String type) throws WTException, FileNotFoundException, PropertyVetoException, DocumentException {
		boolean isTop = false;
		int index = 0;
		int j = 0;
		List<NewReportTechnicsBean> TechnicsList = new ArrayList<NewReportTechnicsBean>();
		for (String oid : partOids) {
			index++;
			if (index == 1) {
				isTop = true;
			} else {
				isTop = false;
			}
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
			WTPart newpart = (WTPart) ProcessPlanHelper.searchLatestIteratedByNumberVersionView(WTPart.class, part.getNumber(), part.getVersionInfo().getIdentifier().getValue(), "Manufacturing");
			QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(newpart, true);
			LatestConfigSpec lcs = new LatestConfigSpec();
			qr = lcs.process(qr);
			while (qr.hasMoreElements()) {
				WTDocument document = (WTDocument) qr.nextElement();
				String typename = TypedUtility.getTypeIdentifier(document).getTypename();
				if (isTop && typename.contains("report") && document.getName().startsWith("工艺文件目录")) {
					continue;
				}
				if (!typename.contains("PROCESS_PLAN")) {
					continue;
				}
				QueryResult qr2 = VersionControlHelper.service.allIterationsOf(document.getMaster());
				if (qr2.hasMoreElements()) {
					// 只获取关联的最新版本
					document = (WTDocument) qr2.nextElement();
					IBAHelper ibaHelper = new IBAHelper(document);
					String PPLANTYPE = ibaHelper.getIBAValue("PPLANTYPE");
					if (!"正式工艺文件".equals(PPLANTYPE)) {
						continue;
					}
					Element rootElement = getElementByDoc(document);
					// 工艺文件信息
					List pplanList = rootElement.selectNodes("//QMFawTechnicsInfo");
					for (Object o : pplanList) {
						Element element = (Element) o;
						List<Element> steps = element.selectNodes("steps/QMProcedureInfo");
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
								bean.setPartNumber(element.attributeValue("CINDEX"));
								bean.setPartName(element.attributeValue("partName"));
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
									bean.setPartNumber(element.attributeValue("CINDEX"));
									bean.setPartName(element.attributeValue("partName"));
									bean.setShiyongchejian(procedure.attributeValue("workShop"));
									bean.setComment(list1.get(k).attributeValue("bz"));
									TechnicsList.add(bean);
								}
							}
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

	private static Object[][] getWaigoujian(List<String> partOids, String type) throws WTException, FileNotFoundException, PropertyVetoException, DocumentException {
		boolean isTop = false;
		int index = 0;
		int j = 0;
		List<NewReportTechnicsBean> TechnicsList = new ArrayList<NewReportTechnicsBean>();
		List bmList = new ArrayList();
		List<WTPart> partList = new ArrayList<WTPart>();
		for (String oid : partOids) {
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
			WTPart newpart = (WTPart) ProcessPlanHelper.searchLatestIteratedByNumberVersionView(WTPart.class, part.getNumber(), part.getVersionInfo().getIdentifier().getValue(), "Manufacturing");
			partList.add(newpart);
		}
		for (WTPart p : partList) {
			index++;
			if (index == 1) {
				isTop = true;
			} else {
				isTop = false;
			}
			QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(p, true);
			LatestConfigSpec lcs = new LatestConfigSpec();
			qr = lcs.process(qr);
			while (qr.hasMoreElements()) {
				WTDocument document = (WTDocument) qr.nextElement();
				String typename = TypedUtility.getTypeIdentifier(document).getTypename();
				if (isTop && typename.contains("report") && document.getName().startsWith("工艺文件目录")) {
					continue;
				}
				if (!typename.contains("PROCESS_PLAN")) {
					continue;
				}
				QueryResult qr2 = VersionControlHelper.service.allIterationsOf(document.getMaster());
				if (qr2.hasMoreElements()) {
					// 只获取关联的最新版本
					document = (WTDocument) qr2.nextElement();
					IBAHelper ibaHelper = new IBAHelper(document);
					String PPLANTYPE = ibaHelper.getIBAValue("PPLANTYPE");
					if (!"正式工艺文件".equals(PPLANTYPE)) {
						continue;
					}
					Element rootElement =  getElementByDoc(document);
					// 工艺文件信息
					List pplanList = rootElement.selectNodes("//QMFawTechnicsInfo");
					for (Object o : pplanList) {
						Element e = (Element) o;
						Element gyde = XmlUtility.getTechnicsDEElement(e);
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

							newParts = XmlUtility.getTechnicsSJZYKGYDEMatchPart(gyde);
							if (newParts != null && !newParts.isEmpty()) {
								for (Element element : newParts) {
									j++;
									NewReportTechnicsBean bean = new NewReportTechnicsBean();
									bean.setIndex(j);
									bean.setMingcheng(element.attributeValue("name"));
									bean.setLeibie("");// 暂时不好判断
									bean.setPaihao(element.attributeValue("ph"));
									bean.setGuige(element.attributeValue("gg"));
									bean.setCscj(element.attributeValue("gys"));
									bean.setJstj(element.attributeValue("bzh"));
									bean.setCailiao("");// 暂时未处理
									bean.setJxxndj(element.attributeValue("jxxndjhyd"));
									bean.setBmcl("");// 暂时未处理
									bean.setDanwei(element.attributeValue("dw"));
									bean.setGyde(element.attributeValue("gysl"));
									bean.setComment("");
									bmList.add(element.attributeValue("partNumber"));
									TechnicsList.add(bean);
								}
							}

							newParts = XmlUtility.getTechnicsSJZYKGYDENewPart(gyde);
							if (newParts != null && !newParts.isEmpty()) {
								for (Element element : newParts) {
									j++;
									NewReportTechnicsBean bean = new NewReportTechnicsBean();
									bean.setIndex(j);
									bean.setMingcheng(element.attributeValue("name"));
									bean.setLeibie("");// 暂时不好判断
									bean.setPaihao(element.attributeValue("ph"));
									bean.setGuige(element.attributeValue("gg"));
									bean.setCscj(element.attributeValue("gys"));
									bean.setJstj(element.attributeValue("bzh"));
									bean.setCailiao("");// 暂时未处理
									bean.setJxxndj(element.attributeValue("jxxndjhyd"));
									bean.setBmcl("");// 暂时未处理
									bean.setDanwei(element.attributeValue("dw"));
									bean.setGyde(element.attributeValue("gysl"));
									bean.setComment("");
									TechnicsList.add(bean);
								}
							}
							ArrayList PBOMlist = new ArrayList();
							for (int k = 0; k < partList.size(); k++) {
								for (int k2 = 0; k2 < bmList.size(); k2++) {
									String number = partList.get(k).getNumber();
									if (number.equals(bmList.get(k2))) {
										PBOMlist.add(partList.get(k));
									}
								}
							}
							partList.removeAll(PBOMlist);

						}

					}
				}
			}
		}
		for (int k = 0; k < partList.size(); k++) {
			j++;
			WTPart part = partList.get(k);
			IBAHelper partIba = new IBAHelper(part);
			NewReportTechnicsBean bean = new NewReportTechnicsBean();
			bean.setIndex(j);
			bean.setMingcheng(part.getName());
			bean.setLeibie("");// 暂时不好判断
			bean.setPaihao(partIba.getIBAValue("MINDEX"));
			bean.setGuige(partIba.getIBAValue("STANDARD"));
			bean.setCscj(partIba.getIBAValue("SUPPLIERS"));
			bean.setJstj(partIba.getIBAValue("USESTANDARD"));
			bean.setCailiao(partIba.getIBAValue("CMAT"));// 暂时未处理
			bean.setJxxndj("");
			bean.setBmcl("");// 暂时未处理
			bean.setDanwei(partIba.getIBAValue("MEASUREUNIT"));
			bean.setGyde("");
			bean.setComment("");
			TechnicsList.add(bean);
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

	private static Object[][] getGongYiLuXian(List<String> partOids, String type) throws WTException, FileNotFoundException, PropertyVetoException, DocumentException {
		boolean isTop = false;
		int index = 0;
		int j = 0;
		List<NewReportTechnicsBean> TechnicsList = new ArrayList<NewReportTechnicsBean>();
		for (String oid : partOids) {
			index++;
			if (index == 1) {
				isTop = true;
			} else {
				isTop = false;
			}
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
			WTPart newpart = (WTPart) ProcessPlanHelper.searchLatestIteratedByNumberVersionView(WTPart.class, part.getNumber(), part.getVersionInfo().getIdentifier().getValue(), "Manufacturing");
			QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(newpart, true);
			LatestConfigSpec lcs = new LatestConfigSpec();
			qr = lcs.process(qr);
			while (qr.hasMoreElements()) {
				WTDocument document = (WTDocument) qr.nextElement();
				String typename = TypedUtility.getTypeIdentifier(document).getTypename();
				if (isTop && typename.contains("report") && document.getName().startsWith("工艺文件目录")) {
					continue;
				}
				if (!typename.contains("PROCESS_PLAN")) {
					continue;
				}
				QueryResult qr2 = VersionControlHelper.service.allIterationsOf(document.getMaster());
				if (qr2.hasMoreElements()) {
					// 只获取关联的最新版本
					document = (WTDocument) qr2.nextElement();
					IBAHelper ibaHelper = new IBAHelper(document);
					String PPLANTYPE = ibaHelper.getIBAValue("PPLANTYPE");
					String ZFFLAG = ibaHelper.getIBAValue("ZFFLAG");
					if (!"正式工艺文件".equals(PPLANTYPE)) {
						continue;
					}
					if ("F".equals(ZFFLAG)) {
						continue;
					}
					Element rootElement =  getElementByDoc(document);
					// 工艺文件信息
					List pplanList = rootElement.selectNodes("//QMFawTechnicsInfo");
					for (Object o : pplanList) {
						Element element = (Element) o;
						List<Element> steps = element.selectNodes("steps/QMProcedureInfo");
						j++;
						NewReportTechnicsBean bean = new NewReportTechnicsBean();
						bean.setIndex(j);
						bean.setDaihao(element.attributeValue("CINDEX"));
						bean.setMingcheng(element.attributeValue("partName"));
						bean.setMeitaochanpingshuliang(element.attributeValue("gysl"));
						bean.setShuliang("");
						bean.setZhuzhibumen(element.attributeValue("ZZCJ"));
						bean.setShijianshuliang("");
						Map<String, String> map = LoadConfigAttributes.getInstance().getAllDesignAttr();
						List<String> deptList = LoadConfigAttributes.getInstance().getDept();
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
//
						bean.setDiyibumen(currentBumenString);
						TechnicsList.add(bean);
					}

				}
			}
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

	private static Object[][] getCaiLiaoHuiZong(List<String> partOids, String type) throws WTException, FileNotFoundException, PropertyVetoException, DocumentException {
		boolean isTop = false;
		int index = 0;
		int j = 0;
		List<NewReportTechnicsBean> TechnicsList = new ArrayList<NewReportTechnicsBean>();
		for (String oid : partOids) {
			index++;
			if (index == 1) {
				isTop = true;
			} else {
				isTop = false;
			}
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
			WTPart newpart = (WTPart) ProcessPlanHelper.searchLatestIteratedByNumberVersionView(WTPart.class, part.getNumber(), part.getVersionInfo().getIdentifier().getValue(), "Manufacturing");
			QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(newpart, true);
			LatestConfigSpec lcs = new LatestConfigSpec();
			qr = lcs.process(qr);
			while (qr.hasMoreElements()) {
				WTDocument document = (WTDocument) qr.nextElement();
				String typename = TypedUtility.getTypeIdentifier(document).getTypename();
				if (isTop && typename.contains("report") && document.getName().startsWith("工艺文件目录")) {
					continue;
				}
				if (!typename.contains("PROCESS_PLAN")) {
					continue;
				}
				QueryResult qr2 = VersionControlHelper.service.allIterationsOf(document.getMaster());
				if (qr2.hasMoreElements()) {
					// 只获取关联的最新版本
					document = (WTDocument) qr2.nextElement();
					IBAHelper ibaHelper = new IBAHelper(document);
					String PPLANTYPE = ibaHelper.getIBAValue("PPLANTYPE");
					if (!"正式工艺文件".equals(PPLANTYPE)) {
						continue;
					}
					Element rootElement =  getElementByDoc(document);
					// 工艺文件信息
					List pplanList = rootElement.selectNodes("//QMFawTechnicsInfo");
					for (Object o : pplanList) {
						Element element = (Element) o;
						Element clde = XmlUtility.getTechnicsCLDEElement(element);
						if (clde == null) {
							return new Object[][] {};
						}
						List<Element> ycl = XmlUtility.getTechnicsYCLDE(clde);
						for (Element procedure : ycl) {
							j++;
							NewReportTechnicsBean bean = new NewReportTechnicsBean();
							bean.setIndex(j);
							bean.setDaihao(element.attributeValue("CINDEX"));
							bean.setPartName(element.attributeValue("partName"));
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

				}
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

	private static Object[][] getFuZhuCaiLiaoHuiZong(List<String> partOids, String type) throws WTException, FileNotFoundException, PropertyVetoException, DocumentException {
		boolean isTop = false;
		int index = 0;
		int j = 0;
		List<NewReportTechnicsBean> TechnicsList = new ArrayList<NewReportTechnicsBean>();
		for (String oid : partOids) {
			index++;
			if (index == 1) {
				isTop = true;
			} else {
				isTop = false;
			}
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
			WTPart newpart = (WTPart) ProcessPlanHelper.searchLatestIteratedByNumberVersionView(WTPart.class, part.getNumber(), part.getVersionInfo().getIdentifier().getValue(), "Manufacturing");
			QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(newpart, true);
			LatestConfigSpec lcs = new LatestConfigSpec();
			qr = lcs.process(qr);
			while (qr.hasMoreElements()) {
				WTDocument document = (WTDocument) qr.nextElement();
				String typename = TypedUtility.getTypeIdentifier(document).getTypename();
				if (isTop && typename.contains("report") && document.getName().startsWith("工艺文件目录")) {
					continue;
				}
				if (!typename.contains("PROCESS_PLAN")) {
					continue;
				}
				QueryResult qr2 = VersionControlHelper.service.allIterationsOf(document.getMaster());
				if (qr2.hasMoreElements()) {
					// 只获取关联的最新版本
					document = (WTDocument) qr2.nextElement();
					IBAHelper ibaHelper = new IBAHelper(document);
					String PPLANTYPE = ibaHelper.getIBAValue("PPLANTYPE");
					if (!"正式工艺文件".equals(PPLANTYPE)) {
						continue;
					}
					Element rootElement =  getElementByDoc(document);
					// 工艺文件信息
					List pplanList = rootElement.selectNodes("//QMFawTechnicsInfo");
					for (Object o : pplanList) {
						Element element = (Element) o;
						List<Element> steps = element.selectNodes("steps/QMProcedureInfo");
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

	private static Object[][] getFuZhuCaiLiao(List<String> partOids, String type) throws WTException, FileNotFoundException, PropertyVetoException, DocumentException {
		boolean isTop = false;
		int index = 0;
		int j = 0;
		List<NewReportTechnicsBean> TechnicsList = new ArrayList<NewReportTechnicsBean>();
		for (String oid : partOids) {
			index++;
			if (index == 1) {
				isTop = true;
			} else {
				isTop = false;
			}
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
			WTPart newpart = (WTPart) ProcessPlanHelper.searchLatestIteratedByNumberVersionView(WTPart.class, part.getNumber(), part.getVersionInfo().getIdentifier().getValue(), "Manufacturing");
			QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(newpart, true);
			LatestConfigSpec lcs = new LatestConfigSpec();
			qr = lcs.process(qr);
			while (qr.hasMoreElements()) {
				WTDocument document = (WTDocument) qr.nextElement();
				String typename = TypedUtility.getTypeIdentifier(document).getTypename();
				if (isTop && typename.contains("report") && document.getName().startsWith("工艺文件目录")) {
					continue;
				}
				if (!typename.contains("PROCESS_PLAN")) {
					continue;
				}
				QueryResult qr2 = VersionControlHelper.service.allIterationsOf(document.getMaster());
				if (qr2.hasMoreElements()) {
					// 只获取关联的最新版本
					document = (WTDocument) qr2.nextElement();
					IBAHelper ibaHelper = new IBAHelper(document);
					String PPLANTYPE = ibaHelper.getIBAValue("PPLANTYPE");
					if (!"正式工艺文件".equals(PPLANTYPE)) {
						continue;
					}
					Element rootElement =  getElementByDoc(document);
					// 工艺文件信息
					List pplanList = rootElement.selectNodes("//QMFawTechnicsInfo");
					for (Object o : pplanList) {
						Element element = (Element) o;
						// 获取所有工装
						List<Element> steps = element.selectNodes("steps/QMProcedureInfo");
						// 开始循环遍历所有工序
						for (Element procedure : steps) {
							// 获取当前工序的工序内容
							Element object = (Element) procedure.selectNodes("materials").get(0);
							List<Element> list = object.elements("QMMaterialInfo");
							for (int k = 0; k < list.size(); k++) {
								j++;
								NewReportTechnicsBean bean = new NewReportTechnicsBean();
								bean.setIndex(j);
								bean.setDaihao(element.attributeValue("CINDEX"));
								bean.setPartName(element.attributeValue("partName"));
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
									bean.setDaihao(element.attributeValue("CINDEX"));
									bean.setPartName(element.attributeValue("partName"));
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

	private static Object[][] getGuanJianGongXu(List<String> partOids, String type)throws WTException, FileNotFoundException, PropertyVetoException, DocumentException {
		boolean isTop = false;
		int index = 0;
		int j = 0;
		List<NewReportTechnicsBean> TechnicsList = new ArrayList<NewReportTechnicsBean>();
		for (String oid : partOids) {
			index++;
			if (index == 1) {
				isTop = true;
			} else {
				isTop = false;
			}
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
			WTPart newpart = (WTPart) ProcessPlanHelper.searchLatestIteratedByNumberVersionView(WTPart.class, part.getNumber(), part.getVersionInfo().getIdentifier().getValue(), "Manufacturing");
			QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(newpart, true);
			LatestConfigSpec lcs = new LatestConfigSpec();
			qr = lcs.process(qr);
			while (qr.hasMoreElements()) {
				WTDocument document = (WTDocument) qr.nextElement();
				String typename = TypedUtility.getTypeIdentifier(document).getTypename();
				if (isTop && typename.contains("report") && document.getName().startsWith("工艺文件目录")) {
					continue;
				}
				if (!typename.contains("PROCESS_PLAN")) {
					continue;
				}
				QueryResult qr2 = VersionControlHelper.service.allIterationsOf(document.getMaster());
				if (qr2.hasMoreElements()) {
					// 只获取关联的最新版本
					document = (WTDocument) qr2.nextElement();
					IBAHelper ibaHelper = new IBAHelper(document);
					String PPLANTYPE = ibaHelper.getIBAValue("PPLANTYPE");
					if (!"正式工艺文件".equals(PPLANTYPE)) {
						continue;
					}
					Element rootElement = getElementByDoc(document);
					// 工艺文件信息
					List pplanList = rootElement.selectNodes("//QMFawTechnicsInfo");
					for (Object o : pplanList) {
						Element element = (Element) o;
						List<Element> list = element.selectNodes("steps/QMProcedureInfo");

						for (int k = 0; k < list.size(); k++) {
							if ("true".equals(list.get(k).attributeValue("isKey"))) {
								j++;
								NewReportTechnicsBean bean = new NewReportTechnicsBean();
								bean.setIndex(j);
								bean.setPartNumber(element.attributeValue("pplanNumber"));
								bean.setPartName(element.attributeValue("partName"));
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
								bean.setZzdw(list.get(k).attributeValue("workShop"));
								bean.setComment(element.attributeValue("bz"));
								bean.setTechnicsNumber(element.attributeValue("technicsNumber"));
								bean.setKznrFlag(PDFUtil.objectToString(kznrContentEle.getTextTrim()));
								TechnicsList.add(bean);
							}
						}
					}

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

			datas[i][6] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getZzdw();
			datas[i][7] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getComment();
			datas[i][8] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getTechnicsNumber();
			datas[i][9] = (String) ((NewReportTechnicsBean) TechnicsList.get(i)).getKznrFlag();
		}
		return datas;
	}

	private static Object[][] getWaiXieJian(List<String> partOids, String type) throws WTException, FileNotFoundException, PropertyVetoException, DocumentException {
		boolean isTop = false;
		int index = 0;
		int j = 0;
		List<NewReportTechnicsBean> TechnicsList = new ArrayList<NewReportTechnicsBean>();
		for (String oid : partOids) {
			index++;
			if (index == 1) {
				isTop = true;
			} else {
				isTop = false;
			}
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
			WTPart newpart = (WTPart) ProcessPlanHelper.searchLatestIteratedByNumberVersionView(WTPart.class, part.getNumber(), part.getVersionInfo().getIdentifier().getValue(), "Manufacturing");
			QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(newpart, true);
			LatestConfigSpec lcs = new LatestConfigSpec();
			qr = lcs.process(qr);
			while (qr.hasMoreElements()) {
				WTDocument document = (WTDocument) qr.nextElement();
				String typename = TypedUtility.getTypeIdentifier(document).getTypename();
				if (isTop && typename.contains("report") && document.getName().startsWith("工艺文件目录")) {
					continue;
				}
				if (!typename.contains("PROCESS_PLAN")) {
					continue;
				}
				QueryResult qr2 = VersionControlHelper.service.allIterationsOf(document.getMaster());
				if (qr2.hasMoreElements()) {
					// 只获取关联的最新版本
					document = (WTDocument) qr2.nextElement();
					IBAHelper ibaHelper = new IBAHelper(document);
					String PPLANTYPE = ibaHelper.getIBAValue("PPLANTYPE");
					if (!"正式工艺文件".equals(PPLANTYPE)) {
						continue;
					}
					Element rootElement = getElementByDoc(document);
					// 工艺文件信息
					List pplanList = rootElement.selectNodes("//QMFawTechnicsInfo");
					for (Object o : pplanList) {
						Element element = (Element) o;
						String isTabular = element.attributeValue("isTabular");
						if ("外协".equals(isTabular)) {
							NewReportTechnicsBean bean = new NewReportTechnicsBean();
							bean.setIndex(j);
							String partNumber = PDFUtil.objectToString(element.attributeValue("CINDEX"));
							bean.setPartNumber(partNumber);
							bean.setPartName(element.attributeValue("partName"));
							String scmat = PDFUtil.getCmatByCLDE(element);
							bean.setCailiao(scmat);
							String useCount = PDFUtil.objectToString(element.attributeValue("useCount"));
							bean.setShuliang(useCount);
							String jsxy = PDFUtil.objectToString(element.attributeValue("jsxy"));
							bean.setJsxy(jsxy);
							String zzdw = PDFUtil.objectToString(element.attributeValue("zzdw"));
							bean.setCzdw(zzdw);

							Element toolElement = element.element(XmlUtility.TOOL_GROUP);
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
							String bz = PDFUtil.objectToString(element.attributeValue("bz"));
							bean.setComment(bz);
							TechnicsList.add(bean);

						}

					}

				}
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

	private static Object[][] getDaoLiangJu(List<String> partOids, String type) throws WTException, FileNotFoundException, PropertyVetoException, DocumentException {
		boolean isTop = false;
		int index = 0;
		int j = 0;
		List<NewReportTechnicsBean> TechnicsList = new ArrayList<NewReportTechnicsBean>();
		for (String oid : partOids) {
			index++;
			if (index == 1) {
				isTop = true;
			} else {
				isTop = false;
			}
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
			WTPart newpart = (WTPart) ProcessPlanHelper.searchLatestIteratedByNumberVersionView(WTPart.class, part.getNumber(), part.getVersionInfo().getIdentifier().getValue(), "Manufacturing");
			QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(newpart, true);
			LatestConfigSpec lcs = new LatestConfigSpec();
			qr = lcs.process(qr);
			while (qr.hasMoreElements()) {
				WTDocument document = (WTDocument) qr.nextElement();
				String typename = TypedUtility.getTypeIdentifier(document).getTypename();
				if (isTop && typename.contains("report") && document.getName().startsWith("工艺文件目录")) {
					continue;
				}
				if (!typename.contains("PROCESS_PLAN")) {
					continue;
				}
				QueryResult qr2 = VersionControlHelper.service.allIterationsOf(document.getMaster());
				if (qr2.hasMoreElements()) {
					// 只获取关联的最新版本
					document = (WTDocument) qr2.nextElement();
					IBAHelper ibaHelper = new IBAHelper(document);
					String PPLANTYPE = ibaHelper.getIBAValue("PPLANTYPE");
					if (!"正式工艺文件".equals(PPLANTYPE)) {
						continue;
					}
					Element rootElement = getElementByDoc(document);
					// 工艺文件信息
					List pplanList = rootElement.selectNodes("//QMFawTechnicsInfo");
					for (Object o : pplanList) {
						Element element = (Element) o;
						List<Element> steps = element.selectNodes("steps/QMProcedureInfo");
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
									// bean.setShuliang(list.get(k).attributeValue("useCount"));
									bean.setShuliang("1");
									bean.setJingdudengji("");
									bean.setPartNumber(element.attributeValue("partNumber"));
									bean.setPartName(element.attributeValue("partName"));
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
										// bean.setShuliang(list1.get(k).attributeValue("useCount"));
										bean.setShuliang("1");
										bean.setJingdudengji("");
										bean.setPartNumber(element.attributeValue("partNumber"));
										bean.setPartName(element.attributeValue("partName"));
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
									// bean.setShuliang(list2.get(k).attributeValue("useCount"));
									bean.setShuliang("1");
									bean.setJingdudengji("");
									bean.setPartNumber(element.attributeValue("partNumber"));
									bean.setPartName(element.attributeValue("partName"));
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
										// bean.setShuliang(list3.get(k).attributeValue("useCount"));
										bean.setShuliang("1");
										bean.setJingdudengji("");
										bean.setPartNumber(element.attributeValue("partNumber"));
										bean.setPartName(element.attributeValue("partName"));
										bean.setShiyongchejian(procedure.attributeValue("workShop"));
										bean.setComment(list3.get(k).attributeValue("bz"));
										TechnicsList.add(bean);
									}
								}

							}
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

	private static Object[][] getBiaozhunYiQi(List<String> partOids, String type) throws WTException, FileNotFoundException, PropertyVetoException, DocumentException {
		boolean isTop = false;
		int index = 0;
		int j = 0;
		List<NewReportTechnicsBean> TechnicsList = new ArrayList<NewReportTechnicsBean>();
		for (String oid : partOids) {
			index++;
			if (index == 1) {
				isTop = true;
			} else {
				isTop = false;
			}
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
			WTPart newpart = (WTPart) ProcessPlanHelper.searchLatestIteratedByNumberVersionView(WTPart.class, part.getNumber(), part.getVersionInfo().getIdentifier().getValue(), "Manufacturing");
			QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(newpart, true);
			LatestConfigSpec lcs = new LatestConfigSpec();
			qr = lcs.process(qr);
			while (qr.hasMoreElements()) {
				WTDocument document = (WTDocument) qr.nextElement();
				String typename = TypedUtility.getTypeIdentifier(document).getTypename();
				if (isTop && typename.contains("report") && document.getName().startsWith("工艺文件目录")) {
					continue;
				}
				if (!typename.contains("PROCESS_PLAN")) {
					continue;
				}
				QueryResult qr2 = VersionControlHelper.service.allIterationsOf(document.getMaster());
				if (qr2.hasMoreElements()) {
					// 只获取关联的最新版本
					document = (WTDocument) qr2.nextElement();
					IBAHelper ibaHelper = new IBAHelper(document);
					String PPLANTYPE = ibaHelper.getIBAValue("PPLANTYPE");
					if (!"正式工艺文件".equals(PPLANTYPE)) {
						continue;
					}
					Element rootElement = getElementByDoc(document);
					 //工艺文件信息
			        List pplanList = rootElement.selectNodes("//QMFawTechnicsInfo");
			        for (Object o : pplanList) {
						Element element = (Element)o;
						// 获取所有工装
						List<Element> steps = element.selectNodes("steps/QMProcedureInfo");
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
								bean.setPartNumber(element.attributeValue("CINDEX"));
								bean.setPartName(element.attributeValue("partName"));
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
									bean.setPartNumber(element.attributeValue("CINDEX"));
									bean.setPartName(element.attributeValue("partName"));
									bean.setShiyongchejian(procedure.attributeValue("workShop"));
									bean.setComment(list1.get(k).attributeValue("bz"));
									TechnicsList.add(bean);
								}
							}
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

	private static Object[][] getGongZhuang(List<String> partOids, String type) throws WTException, FileNotFoundException, PropertyVetoException, DocumentException {
		boolean isTop = false;
		int index = 0;
		int j = 0;
		List<NewReportTechnicsBean> TechnicsList = new ArrayList<NewReportTechnicsBean>();
		for (String oid : partOids) {
			index++;
			if (index == 1) {
				isTop = true;
			} else {
				isTop = false;
			}
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
			WTPart newpart = (WTPart) ProcessPlanHelper.searchLatestIteratedByNumberVersionView(WTPart.class, part.getNumber(), part.getVersionInfo().getIdentifier().getValue(), "Manufacturing");
			QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(newpart, true);
			LatestConfigSpec lcs = new LatestConfigSpec();
			qr = lcs.process(qr);
			while (qr.hasMoreElements()) {
				WTDocument document = (WTDocument) qr.nextElement();
				String typename = TypedUtility.getTypeIdentifier(document).getTypename();
				if (isTop && typename.contains("report") && document.getName().startsWith("工艺文件目录")) {
					continue;
				}
				if (!typename.contains("PROCESS_PLAN")) {
					continue;
				}
				QueryResult qr2 = VersionControlHelper.service.allIterationsOf(document.getMaster());
				if (qr2.hasMoreElements()) {
					// 只获取关联的最新版本
					document = (WTDocument) qr2.nextElement();
					IBAHelper ibaHelper = new IBAHelper(document);
					String PPLANTYPE = ibaHelper.getIBAValue("PPLANTYPE");
					if (!"正式工艺文件".equals(PPLANTYPE)) {
						continue;
					}
					Element rootElement = getElementByDoc(document);
					 //工艺文件信息
			        List pplanList = rootElement.selectNodes("//QMFawTechnicsInfo");
			        for (Object o : pplanList) {
						Element element = (Element)o;
			        	List<Element> steps = element.selectNodes("steps/QMProcedureInfo");
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
								bean.setPartNumber(element.attributeValue("CINDEX"));
								bean.setPartName(element.attributeValue("partName"));
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
									bean.setPartNumber(element.attributeValue("CINDEX"));
									bean.setPartName(element.attributeValue("partName"));
									bean.setShiyongchejian(procedure.attributeValue("workShop"));
									bean.setComment(list1.get(k).attributeValue("bz"));
									TechnicsList.add(bean);
								}
							}

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


	private static Element getElementByDoc(WTDocument document) throws WTException, PropertyVetoException, FileNotFoundException, DocumentException {
		String tempFilePath = PropertiesUtil.getWTHome() + File.separator + "temp" + File.separator
				+  java.util.UUID.randomUUID().toString()+ File.separator;
		String zipFileName = WTDocumentUtil.downloadDocumentPrimaryToTemp(document, tempFilePath);
		String subFileName = zipFileName.substring(0, zipFileName.lastIndexOf("."));
		ApacheZipUtil.decompress(tempFilePath + zipFileName, tempFilePath + subFileName);
		File xmlFile = new File(tempFilePath + subFileName + File.separator + subFileName + ".xml");
		SAXReader reader = new SAXReader();
        Document dom = reader.read(xmlFile);
        Element rootElement = dom.getRootElement();
		FileUtil.deleteFile(new File(tempFilePath));
		return rootElement;
	}

	public static Object[][] getGYWJMLDatas(List<String> partOids, String type)throws WTException {
		boolean isTop = false;
		int index = 0;
		List<NewReportTechnicsBean> technicsList= new ArrayList<NewReportTechnicsBean>();
		for(String oid :partOids){
			index++;
			if(index==1){
				isTop = true;
			}else{
				isTop = false;
			}
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
			WTPart newpart = (WTPart) ProcessPlanHelper.searchLatestIteratedByNumberVersionView(WTPart.class, part.getNumber(), part.getVersionInfo().getIdentifier().getValue(), "Manufacturing");
			QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(newpart, true);
			LatestConfigSpec lcs = new LatestConfigSpec();
			qr = lcs.process(qr);
			while (qr.hasMoreElements()) {
				WTDocument document = (WTDocument) qr.nextElement();
				String typename = TypedUtility.getTypeIdentifier(document).getTypename();
				System.out.println(document.getNumber());
				if(isTop&&typename.contains("report")&&document.getName().startsWith("工艺文件目录")){
					continue;
				}
				if(!typename.contains("PROCESS_PLAN")){
					continue;
				}
				QueryResult qr2 = VersionControlHelper.service.allIterationsOf(document.getMaster());
				if (qr2.hasMoreElements()) {
					// 只获取关联的最新版本
					document = (WTDocument) qr2.nextElement();
					IBAHelper ibaHelper = new IBAHelper(document);
					String PPLANTYPE = ibaHelper.getIBAValue("PPLANTYPE");
					if("临时工艺文件".equals(PPLANTYPE)){
						continue;
					}
					NewReportTechnicsBean bean = new NewReportTechnicsBean();
					bean.setIndex(0);
					bean.setBianhao(ibaHelper.getIBAValue("PPNUMBER"));
					bean.setMingcheng(document.getName().substring(0,  document.getName().indexOf("(")));
					bean.setDaihao(ibaHelper.getIBAValue("CINDEX"));
					bean.setPartName(newpart.getName());
					bean.setShuliang(ibaHelper.getIBAValue("PAGE"));
					bean.setZerenbumen( ibaHelper.getIBAValue("DEPT"));
					bean.setComment("");
					bean.setLifecycle( document.getState().getState().getDisplay(Locale.CHINA));
					bean.setFileNumber(document.getNumber());
					bean.setVersion(document.getVersionIdentifier().getValue()+"."+document.getIterationIdentifier().getValue());
					technicsList.add(bean);
					/*byte[] bytes = null;
					Vector<Object> vector = new Vector<Object>();
					if (null != document) {
						ApplicationData data = WTDocumentUtil.getPrimaryByDocument(document);
						bytes = WTDocumentUtil.applicationDataToByte(data);
						vector.add(data.getFileName());
						vector.add(bytes);
						vector.add(document.getLifeCycleState().getLocalizedMessage(Locale.CHINA));
						vector.add(document.getIterationDisplayIdentifier().toString());
						vector.add(document.getNumber());
						vector.add(document.getName());

					}*/
				}
			}
		}

		String[][] datas = new String[technicsList.size()][11];
		for (int i = 0; i < technicsList.size(); i++) {
			datas[i][0] = String.valueOf(i + 1);
			datas[i][1] = (String) ((NewReportTechnicsBean) technicsList.get(i)).getBianhao();
			datas[i][2] = (String) ((NewReportTechnicsBean) technicsList.get(i)).getMingcheng();
			datas[i][3] = (String) ((NewReportTechnicsBean) technicsList.get(i)).getDaihao();
			datas[i][4] = (String) ((NewReportTechnicsBean) technicsList.get(i)).getPartName();
			datas[i][5] = (String) ((NewReportTechnicsBean) technicsList.get(i)).getShuliang();
			datas[i][6] = (String) ((NewReportTechnicsBean) technicsList.get(i)).getZerenbumen();
			datas[i][7] = (String) ((NewReportTechnicsBean) technicsList.get(i)).getComment();
			datas[i][8] = (String) ((NewReportTechnicsBean) technicsList.get(i)).getLifecycle();
			datas[i][9] = (String) ((NewReportTechnicsBean) technicsList.get(i)).getFileNumber();
			datas[i][10] = (String) ((NewReportTechnicsBean) technicsList.get(i)).getVersion();
		}
		return datas;
	}

	public static void insertTempDatas(String[][] os,String type) {
		PreparedStatement pstmt = null;
		WTConnection wtconnection = null;
		Connection con = null;
		StringBuffer sqlSB = new StringBuffer();
		if(GYWJML.equals(type)){
			sqlSB.append("insert into GWREPORTDATAS(COLUMN1, COLUMN2,COLUMN3,COLUMN4,COLUMN5,COLUMN6,COLUMN7,COLUMN8,COLUMN9,TYPE)");
			sqlSB.append("values(?,?,?,?,?,?,?,?,?,?)");
		}else if(GongZhuang.equals(type)||BiaozhunYiQi.equals(type)||FeiBiaoZhunYiQi.equals(type)){
			sqlSB.append("insert into GWREPORTDATAS(COLUMN1, COLUMN2,COLUMN3,COLUMN4,COLUMN5,COLUMN6,COLUMN7,COLUMN8,COLUMN9,TYPE,COLUMN10,COLUMN11)");
			sqlSB.append("values(?,?,?,?,?,?,?,?,?,?,?,?) ");
		}

		try {
			SessionServerHelper.manager.setAccessEnforced(false);
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
		    con = wtconnection.getConnection();
		    con.setAutoCommit(false);
		    for(String[] o:os){
		    	pstmt = con.prepareStatement(sqlSB.toString());
				pstmt.setString(1,o[0]);
				pstmt.setString(2,o[1]);
				pstmt.setString(3,o[2]);
				pstmt.setString(4,o[3]);
				pstmt.setString(5,o[4]);
				pstmt.setString(6,o[5]);
				pstmt.setString(7,o[6]);
				pstmt.setString(8,o[7]);
				pstmt.setString(9,o[8]);
				pstmt.setString(10,type);
				if(GongZhuang.equals(type)||BiaozhunYiQi.equals(type)||FeiBiaoZhunYiQi.equals(type)){
					pstmt.setString(11,o[9]);
					pstmt.setString(12,o[10]);
				}
				pstmt.executeUpdate();
		    }

			con.commit();
			con =null;
			//wtconnection.releaseAll();
			//wtconnection = null;
		} catch (Exception ex) {
			try {
				con.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ex.printStackTrace();
			SessionServerHelper.manager.setAccessEnforced(true);
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			SessionServerHelper.manager.setAccessEnforced(true);
		}

	}
	public static String[][] getTempDatas(String partOid,String type) {
		Statement ps = null;
		ResultSet resultset = null;
		WTConnection wtconnection = null;
		Connection con = null;
		StringBuffer sqlSB = new StringBuffer();
		sqlSB.append("select * from  GWREPORTDATAS where type='"+type+"'");
		try {
			SessionServerHelper.manager.setAccessEnforced(false);
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
		    con = wtconnection.getConnection();
		    con.setAutoCommit(false);
		    ps = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            resultset = ps.executeQuery(sqlSB.toString());

    		List<String[]> tempDatas = new ArrayList<String[]>();
    		int i=0;
            while (resultset.next()) {
            	i++;

            	String[] datas = new String[9];
            	if(GongZhuang.equals(type)||BiaozhunYiQi.equals(type)||FeiBiaoZhunYiQi.equals(type)){
            		datas = new String[11];
				}
            	datas[0] = String.valueOf(i);
    			datas[1] = resultset.getString(2);
    			datas[2] = resultset.getString(3);
    			datas[3] = resultset.getString(4);
    			datas[4] = resultset.getString(5);
    			datas[5] = resultset.getString(6);
    			datas[6] = resultset.getString(7);
    			datas[7] = resultset.getString(8);
    			datas[8] = resultset.getString(9);
    			if(GongZhuang.equals(type)||BiaozhunYiQi.equals(type)||FeiBiaoZhunYiQi.equals(type)){
    				datas[9] = resultset.getString(10);
        			datas[10] = resultset.getString(11);
    			}

    			tempDatas.add(datas);

            	/*NewReportTechnicsBean bean = new NewReportTechnicsBean();
				bean.setIndex(0);
				bean.setBianhao(resultset.getString(2));
				bean.setMingcheng(resultset.getString(3));
				bean.setDaihao(resultset.getString(4));
				bean.setPartName(resultset.getString(5));
				bean.setShuliang(resultset.getString(6));
				bean.setZerenbumen( resultset.getString(7));
				bean.setComment("");
				bean.setLifecycle( resultset.getString(9));
				technicsList.add(bean);*/
            }

            String[][] resultdatas = new String[tempDatas.size()][9];
            if(GongZhuang.equals(type)||BiaozhunYiQi.equals(type)||FeiBiaoZhunYiQi.equals(type)){
            	resultdatas = new String[tempDatas.size()][11];
			}
            int j = 0;
            for(String[] d:tempDatas){
            	resultdatas[j] = d;
            	j++;
            }

    		return resultdatas;
			//wtconnection.releaseAll();
			//wtconnection = null;
		} catch (Exception ex) {
			try {
				con.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ex.printStackTrace();
			SessionServerHelper.manager.setAccessEnforced(true);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (resultset != null) {
					resultset.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			SessionServerHelper.manager.setAccessEnforced(true);
		}
		return null;

	}

}
