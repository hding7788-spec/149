package ext.casc.report.bomcompare;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;


import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.lifecycle.State;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.config.ConfigHelper;
import wt.vc.config.ConfigSpec;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;

import java.util.Collection;
import java.util.Iterator;


import wt.fc.ObjectVector;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainerRef;
import wt.pds.StatementSpec;
import wt.projmgmt.admin.Project2;
import wt.query.ClassAttribute;
import wt.query.ConstantExpression;
import wt.query.SQLFunction;
import wt.util.WTPropertyVetoException;
import wt.vc.OneOffVersioned;
import wt.vc.VersionControlHelper;
import wt.vc.config.ConfigException;
import wt.vc.views.ViewException;
import wt.vc.views.ViewManageable;
import wt.vc.wip.WorkInProgressHelper;

import ext.casc.part.CSCPart;
import ext.casc.util.IBAHelper;

public class DownloadBomCompareReportUtil {

	private static String templateDir;
	private static String tempDir;

	static {
		try {
			WTProperties pro = WTProperties.getLocalProperties();
			templateDir = pro.getProperty("wt.codebase.location")
					+ File.separator + "ext" + File.separator + "casc"
					+ File.separator + "report" + File.separator + "technics"
					+ File.separator + "templates";

			tempDir = pro.getProperty("wt.temp");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * EBOM和PBOM比较
	 *
	 * @param rootPart
	 *            PBOM的顶层part
	 * @return File xls文件
	 */
	public static File exportEBOMCompareToPBOM(WTPart rootPart) {

		String oPath = templateDir + File.separator + "ebomcomparetopbom.xls";// 报表模板路径
		String tPath = tempDir + File.separator + rootPart.getNumber()
				+ "_EBOMPBOM对比.xls";// 报表输出路径
		File xlsFile = copyTemplate(oPath, tPath);
		// 因属性差异将part放入该list
		List<WTPart> partIBAPrintList = new ArrayList<WTPart>();
		// 因数量差异将part放入该list
		List<WTPart> partQuantityPrintList = new ArrayList<WTPart>();
		Map<WTPart, Double> PartQuantitymap = new HashMap<WTPart, Double>();
		// 因部件PBOM多出将Part放入该list
		List<WTPart> lessPartList = new ArrayList<WTPart>();
		// 因部件PBOM缺少将Part放入该list
		List<WTPart> morepartList = new ArrayList<WTPart>();

		if (xlsFile == null) {
			return null;
		}
		try {
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(
					xlsFile));// 创建xls表
			HSSFWorkbook hssfWorkbook = new HSSFWorkbook(fs);
			HSSFSheet sheet = hssfWorkbook.getSheetAt(0);// 获取第一张sheet
			HSSFRow row = null;
			int index = 2;
			// PBOMList
			List<WTPart> allPart = new ArrayList<WTPart>();
			allPart.add(rootPart);
			// EBOMList
			List<WTPart> epartList = new ArrayList<WTPart>();
			WTPart rootEpart = CSCPart.getPartByNumberAndViewName(
					rootPart.getNumber(), "Design");
			epartList.add(rootEpart);
			List<ExportBomDetailsBean> pts = new ArrayList<ExportBomDetailsBean>();
			List<ExportBomDetailsBean> ets = new ArrayList<ExportBomDetailsBean>();
			Map<String, WTPart> map1 = new HashMap<String, WTPart>();
			Map<String, WTPart> map2 = new HashMap<String, WTPart>();
			// 获得PBOM所有Part
			Map<String, WTPart> PBOMMap = buildPPartBean(rootPart, pts, 1, map2);
			// 获得EBOM所有Part
			Map<String, WTPart> EBOMMap = buildEPartBean(rootEpart, ets, 1,
					map1);
			for (String partNumber : EBOMMap.keySet()) {
				WTPart afterPart = PBOMMap.get(partNumber);
				WTPart beforePart = EBOMMap.get(partNumber);
				if (afterPart == null) {
					if (!lessPartList.contains(beforePart)) {
						lessPartList.add(beforePart);
						for (ExportBomDetailsBean ebean : ets) {
							if (ebean.getChildPart().getNumber()
									.equals(beforePart.getNumber())) {
								PartQuantitymap.put(beforePart,
										ebean.getAmount());
							}
						}
					}
				}
			}
			for (String partNumber : PBOMMap.keySet()) {
				WTPart afterPart = PBOMMap.get(partNumber);
				WTPart beforePart = EBOMMap.get(partNumber);
				// 当EBOM部件存在的时候
				if (beforePart != null) {
					if (afterPart.equals(beforePart)) {
						continue;
					}
					// 得到PBOM和EBOM的结构差异
					Map<String, Map<String, List<WTPartUsageLink>>> map = getBomDifferences(
							rootEpart, rootPart);
					Map<String, List<WTPartUsageLink>> map_add = map.get("add");

					Map<String, List<WTPartUsageLink>> map_rem = map.get("rem");
					// 得到数量变化的部件
					Map<String, List<WTPartUsageLink>> map_cha_bef = map
							.get("cha_bef");
					Map<String, List<WTPartUsageLink>> map_cha_aft = map
							.get("cha_aft");
					// 新增
					for (String str : map_add.keySet()) {
						List<WTPartUsageLink> usageLikList = map_add.get(str);
						for (WTPartUsageLink usageLink : usageLikList) {
							// 得到PBOM新增的部件
							WTPart part1 = getNewVersionPart(usageLink
									.getUses().getNumber(),
									afterPart.getViewName());
							if (!morepartList.contains(part1)) {
								String name = part1.getName();
								System.out.println(name);
								morepartList.add(part1);
								// PartQuantitymap.put(part1,
								// usageLink.getQuantity().getAmount());
								for (ExportBomDetailsBean pbean : pts) {
									if (pbean.getChildPart().getNumber()
											.equals(part1.getNumber())) {
										PartQuantitymap.put(part1,
												pbean.getAmount());
									}
								}
							}
						}
					}
					// 移除
					for (String str : map_rem.keySet()) {
						List<WTPartUsageLink> usageLikList = map_rem.get(str);
						for (WTPartUsageLink usageLink : usageLikList) {
							// 得到删除的部件
							WTPart part1 = getNewVersionPart(usageLink
									.getUses().getNumber(),
									afterPart.getViewName());
							if (!lessPartList.contains(part1)) {
								lessPartList.add(part1);
								PartQuantitymap.put(part1, usageLink
										.getQuantity().getAmount());
							}
						}
					}
					// Quantity变化
					for (String str : map_cha_aft.keySet()) {
						List<WTPartUsageLink> bef_usageLikList = map_cha_bef
								.get(str);
						for (WTPartUsageLink usageLink : bef_usageLikList) {
							WTPart beforeChildPart = null;
							if (EBOMMap.keySet().contains(str)) {
								beforeChildPart = EBOMMap.get(str);
							} else {
								// 得到EBOM有数量差异的part
								beforeChildPart = getNewVersionPart(usageLink
										.getUses().getNumber(),
										afterPart.getViewName());
							}
							if (!partQuantityPrintList
									.contains(beforeChildPart)) {

								partQuantityPrintList.add(beforeChildPart);
								PartQuantitymap.put(beforeChildPart, usageLink
										.getQuantity().getAmount());
							}
						}
						List<WTPartUsageLink> aft_usageLikList = map_cha_aft
								.get(str);
						for (WTPartUsageLink usageLink : aft_usageLikList) {
							// 得到PBOM有数量差异的part
							WTPart afterChildPart = getNewVersionPart(usageLink
									.getUses().getNumber(),
									afterPart.getViewName());
							if (!partQuantityPrintList.contains(afterChildPart)) {
								partQuantityPrintList.add(afterChildPart);
								PartQuantitymap.put(afterChildPart, usageLink
										.getQuantity().getAmount());
							}
						}
					}
				} else {
					// 当EBOM没有该编号的部件时
					// PBOM新增
					Map<WTPart, WTPartUsageLink> afterBOM = collectSingleLevelBOMAndLink(
							afterPart, "Manufacturing");
					if (!afterBOM.isEmpty()) {
						for (WTPart part2 : afterBOM.keySet()) {
							// 获取到PBOM新增的部件
							if (!morepartList.contains(part2)) {
								morepartList.add(part2);
								for (ExportBomDetailsBean pbean : pts) {
									if (pbean.getChildPart().getNumber()
											.equals(afterPart.getNumber())) {
										PartQuantitymap.put(part2,
												pbean.getAmount());
									}
								}
							}
						}
					} else {
						if (!morepartList.contains(afterPart)) {
							morepartList.add(afterPart);
							afterPart.getName();
							for (ExportBomDetailsBean ebean : pts) {
								if (ebean.getChildPart().getNumber()
										.equals(afterPart.getNumber())) {
									PartQuantitymap.put(afterPart,
											ebean.getAmount());
								}
							}
						}
					}
				}
			}
			// 获取PBOM顶层部件的属性列表
			String ppartIBAString = getPpartIBAList(rootPart);
			// 获取EBOM顶层部件的属性列表
			String epartIBAString = getEpartIBAList(rootEpart);
			// 1.对比PBOM和EBOM顶层部件的属性，如果存在差异，将存在差异的Part放入列表中
			if (!ppartIBAString.equals(epartIBAString)) {
				System.out.println("PBOM和EBOM属性存在差异");
				partIBAPrintList.add(rootEpart);
				partIBAPrintList.add(rootPart);
			} else {
				System.out.println("PBOM和EBOM属性相同");
			}
			getAllChildPPart(rootPart, allPart, epartList, partIBAPrintList,
					partQuantityPrintList, PartQuantitymap, lessPartList,
					morepartList);// 获取所有M视图的子部件
			// 遍历属性差异的Part

			for (WTPart part : partIBAPrintList) {
				System.out.println("------part----" + part.getNumber() + "  "
						+ part.getViewName());
				String defaultUnit = part.getDefaultUnit().getDisplay();
				row = sheet.createRow(index++);
				if ("Design".equals(part.getViewName())) {
					// 差异对比
					writeCellValue(row, 0, "EBOM属性");
					// 名称
					writeCellValue(row, 1, part.getName());
					// 编号
					writeCellValue(row, 2, part.getNumber());
					// 单位
					writeCellValue(row, 6, defaultUnit);
					// 材料
					writeCellValue(row, 7,
							IBAHelper.getIBAStringValue(part, "CMAT"));
					// 备注
					writeCellValue(row, 8, "");

				}
				if ("Manufacturing".equals(part.getViewName())) {
					// 差异对比
					writeCellValue(row, 0, "PBOM属性");
					// 名称
					writeCellValue(row, 1, part.getName());
					// 编号
					writeCellValue(row, 2, part.getNumber());
					// 单位
					writeCellValue(row, 6, defaultUnit);
					// 材料
					writeCellValue(row, 7,
							getNotNullParam(IBAHelper.getIBAStringValue(part,
									"CMAT")));
					// 备注
					writeCellValue(row, 8, "");
				}
			}
			// 遍历数量差异的Part
			for (WTPart part : partQuantityPrintList) {
				String defaultUnit = part.getDefaultUnit().getDisplay();
				// 父部件
				QueryResult queryResult = WTPartHelper.service
						.getUsedByWTParts((WTPartMaster) part.getMaster());
				WTPart fatherpart = (WTPart) queryResult.nextElement();
				row = sheet.createRow(index++);
				if ("Design".equals(part.getViewName())) {
					// 差异对比
					writeCellValue(row, 0, "EBOM数量");
					// 名称
					writeCellValue(row, 1, part.getName());
					// 编号
					writeCellValue(row, 2, part.getNumber());
					// 父编号
					writeCellValue(row, 3, fatherpart.getNumber());
					// 父项名称
					writeCellValue(row, 4, fatherpart.getName());
					// 数量
					for (WTPart key : PartQuantitymap.keySet()) {
						System.out.println("key= " + key + " and value= "
								+ PartQuantitymap.get(key));
						if ("Design".equals(key.getViewName()) && key == part) {
							writeCellValue(row, 5, PartQuantitymap.get(key)
									.toString());
						}
					}
					// 单位
					writeCellValue(row, 6, defaultUnit);
					// 材料
					writeCellValue(row, 7,
							IBAHelper.getIBAStringValue(part, "CMAT"));
					// 备注
					writeCellValue(row, 8, "");
				}
				if ("Manufacturing".equals(part.getViewName())) {
					// 差异对比
					writeCellValue(row, 0, "PBOM数量");
					// 名称
					writeCellValue(row, 1, part.getName());
					// 编号
					writeCellValue(row, 2, part.getNumber());
					// 父编号
					writeCellValue(row, 3, fatherpart.getNumber());
					// 父项名称
					writeCellValue(row, 4, fatherpart.getName());
					// 数量
					for (WTPart key : PartQuantitymap.keySet()) {
						System.out.println("key= " + key + " and value= "
								+ PartQuantitymap.get(key));
						if ("Manufacturing".equals(key.getViewName())
								&& key == part) {
							writeCellValue(row, 5, PartQuantitymap.get(key)
									.toString());
						}
					}
					// 单位
					writeCellValue(row, 6, defaultUnit);
					// 材料
					writeCellValue(row, 7,
							getNotNullParam(IBAHelper.getIBAStringValue(part,
									"CMAT")));
					// 备注
					writeCellValue(row, 8, "");
				}
			}
			for (WTPart part : morepartList) {
				row = sheet.createRow(index++);
				String defaultUnit = part.getDefaultUnit().getDisplay();
				// 父部件
				QueryResult queryResult = WTPartHelper.service
						.getUsedByWTParts((WTPartMaster) part.getMaster());
				WTPart fatherpart = (WTPart) queryResult.nextElement();
				if ("Manufacturing".equals(part.getViewName())) {
					// 差异对比
					writeCellValue(row, 0, "PBOM多出");
					// 名称
					writeCellValue(row, 1, part.getName());
					// 编号
					writeCellValue(row, 2, part.getNumber());
					// 父编号
					writeCellValue(row, 3, fatherpart.getNumber());
					// 父项名称
					writeCellValue(row, 4, fatherpart.getName());
					// 数量
					for (WTPart key : PartQuantitymap.keySet()) {
						System.out.println("key= " + key + " and value= "
								+ PartQuantitymap.get(key));
						if ("Manufacturing".equals(key.getViewName())
								&& key == part) {
							writeCellValue(row, 5, PartQuantitymap.get(key)
									.toString());
						}
					}
					// 单位
					writeCellValue(row, 6, defaultUnit);
					// 材料
					writeCellValue(row, 7,
							getNotNullParam(IBAHelper.getIBAStringValue(part,
									"CMAT")));
					// 备注
					writeCellValue(row, 8, "");
				}
			}
			for (WTPart part : lessPartList) {
				row = sheet.createRow(index++);
				String defaultUnit = part.getDefaultUnit().getDisplay();
				// 父部件
				QueryResult queryResult = WTPartHelper.service
						.getUsedByWTParts((WTPartMaster) part.getMaster());
				WTPart fatherpart = (WTPart) queryResult.nextElement();
				if ("Design".equals(part.getViewName())) {
					// 差异对比
					writeCellValue(row, 0, "PBOM缺少");
					// 名称
					writeCellValue(row, 1, part.getName());
					// 编号
					writeCellValue(row, 2, part.getNumber());
					// 父编号
					writeCellValue(row, 3, fatherpart.getNumber());
					// 父项名称
					writeCellValue(row, 4, fatherpart.getName());
					// 数量
					for (WTPart key : PartQuantitymap.keySet()) {
						System.out.println("key= " + key + " and value= "
								+ PartQuantitymap.get(key));
						if ("Design".equals(key.getViewName()) && key == part) {
							writeCellValue(row, 5, PartQuantitymap.get(key)
									.toString());
						}
					}
					// 单位
					writeCellValue(row, 6, defaultUnit);
					// 材料
					writeCellValue(row, 7,
							getNotNullParam(IBAHelper.getIBAStringValue(part,
									"CMAT")));
					// 备注
					writeCellValue(row, 8, "");
				}
			}
			xlsFile = new File(tPath);
			FileOutputStream outputStream = new FileOutputStream(xlsFile);
			hssfWorkbook.write(outputStream);
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}

		return xlsFile;
	}

	private static void writeCellValue(HSSFRow row, int col, String value) {
		HSSFCell cell = row.createCell(col, HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString(value));
	}

	/**
	 * 将获取的模板内容写入到新的文件中
	 *
	 * @param oPath
	 *            报表模板路径
	 * @param tPath
	 *            报表输出路径
	 * @return
	 */
	private static File copyTemplate(String oPath, String tPath) {
		File oFile = new File(oPath);
		if (!oFile.exists()) {
			return null;
		}
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			is = new FileInputStream(oFile);
			File tFile = new File(tPath);
			fos = new FileOutputStream(tFile);
			byte[] bytes = new byte[1024];
			int length = 0;
			while ((length = is.read(bytes)) != -1) {
				fos.write(bytes, 0, length);
			}
			fos.close();
			is.close();

			return tFile;
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
		return null;
	}

	/**
	 * 获取所有M视图D视图的子部件
	 *
	 * @param part
	 *            M视图的顶层部件
	 * @param list
	 *            包含有顶层部件的集合
	 * @throws WTException
	 */
	public static void getAllChildPPart(WTPart part, List<WTPart> list,
			List<WTPart> epartList, List<WTPart> partIBAPrintList,
			List<WTPart> partQuantityPrintList,
			Map<WTPart, Double> PartQuantitymap, List<WTPart> lessPartList,
			List<WTPart> morepartList) throws WTException {
		QueryResult qr = WTPartHelper.service.getUsesWTParts(part,
				getDefaultConfigSpec());// 得到子部件结果集
		WTPart ppart = null;
		WTPart epart = null;
		// 获取D视图的顶层部件
		while (qr.hasMoreElements()) {
			// 获取子部件的link
			Persistable[] persistables = (Persistable[]) qr.nextElement();
			Persistable persistable = (Persistable) persistables[1];
			if (persistable instanceof WTPartMaster) {
				continue;
			}
			WTPart childPart = (WTPart) persistable;
			String viewName = childPart.getViewName();
			if ("Design".equals(viewName)) {
				// 第二层
				ppart = CSCPart.getPartByNumberAndViewName(
						childPart.getNumber(), "Manufacturing");// M视图子部件
				epart = CSCPart.getPartByNumberAndViewName(
						childPart.getNumber(), "Design");// D视图子部件

				List<ExportBomDetailsBean> pts = new ArrayList<ExportBomDetailsBean>();
				List<ExportBomDetailsBean> ets = new ArrayList<ExportBomDetailsBean>();
				Map<String, WTPart> map1 = new HashMap<String, WTPart>();
				Map<String, WTPart> map2 = new HashMap<String, WTPart>();
				// 获得PBOM所有Part
				Map<String, WTPart> PBOMMap = buildPPartBean(ppart, pts, 1,
						map2);
				// 获得EBOM所有Part
				Map<String, WTPart> EBOMMap = buildEPartBean(epart, ets, 1,
						map1);
				for (String partNumber : EBOMMap.keySet()) {
					WTPart afterPart = PBOMMap.get(partNumber);
					WTPart beforePart = EBOMMap.get(partNumber);
					if (afterPart == null) {
						if (!lessPartList.contains(beforePart)) {
							lessPartList.add(beforePart);
							for (ExportBomDetailsBean ebean : ets) {
								if (ebean.getChildPart().getNumber()
										.equals(beforePart.getNumber())) {
									PartQuantitymap.put(beforePart,
											ebean.getAmount());
								}
							}
						}
					}
				}
				for (String partNumber : PBOMMap.keySet()) {
					WTPart afterPart = PBOMMap.get(partNumber);
					WTPart beforePart = EBOMMap.get(partNumber);
					// 当EBOM部件存在的时候
					if (beforePart != null) {
						if (afterPart.equals(beforePart)) {
							continue;
						}
						// 得到PBOM和EBOM的结构差异
						Map<String, Map<String, List<WTPartUsageLink>>> map = getBomDifferences(
								beforePart, afterPart);
						Map<String, List<WTPartUsageLink>> map_add = map
								.get("add");

						Map<String, List<WTPartUsageLink>> map_rem = map
								.get("rem");
						// 得到数量变化的部件
						Map<String, List<WTPartUsageLink>> map_cha_bef = map
								.get("cha_bef");
						Map<String, List<WTPartUsageLink>> map_cha_aft = map
								.get("cha_aft");
						// 新增
						for (String str : map_add.keySet()) {
							List<WTPartUsageLink> usageLikList = map_add
									.get(str);
							for (WTPartUsageLink usageLink : usageLikList) {
								// 得到PBOM新增的部件
								WTPart part1 = getNewVersionPart(usageLink
										.getUses().getNumber(),
										afterPart.getViewName());
								if (!morepartList.contains(part1)) {
									morepartList.add(part1);
									PartQuantitymap.put(part1, usageLink
											.getQuantity().getAmount());
								}
							}
						}

						// 移除
						for (String str : map_rem.keySet()) {
							List<WTPartUsageLink> usageLikList = map_rem
									.get(str);
							for (WTPartUsageLink usageLink : usageLikList) {
								// 得到删除的部件
								WTPart part1 = getNewVersionPart(usageLink
										.getUses().getNumber(),
										afterPart.getViewName());
								String name = part1.getName();
								if (!lessPartList.contains(part1)) {
									lessPartList.add(part1);
									PartQuantitymap.put(part1, usageLink
											.getQuantity().getAmount());
								}
							}
						}
						// Quantity变化
						for (String str : map_cha_aft.keySet()) {
							List<WTPartUsageLink> bef_usageLikList = map_cha_bef
									.get(str);
							for (WTPartUsageLink usageLink : bef_usageLikList) {
								WTPart beforeChildPart = null;
								if (EBOMMap.keySet().contains(str)) {
									beforeChildPart = EBOMMap.get(str);
								} else {
									// 得到EBOM有数量差异的part
									beforeChildPart = getNewVersionPart(
											usageLink.getUses().getNumber(),
											afterPart.getViewName());
								}
								if (!partQuantityPrintList
										.contains(beforeChildPart)) {
									partQuantityPrintList.add(beforeChildPart);
									PartQuantitymap
											.put(beforeChildPart, usageLink
													.getQuantity().getAmount());
								}
							}
							List<WTPartUsageLink> aft_usageLikList = map_cha_aft
									.get(str);
							for (WTPartUsageLink usageLink : aft_usageLikList) {
								// 得到PBOM有数量差异的part
								WTPart afterChildPart = getNewVersionPart(
										usageLink.getUses().getNumber(),
										afterPart.getViewName());
								if (!partQuantityPrintList
										.contains(afterChildPart)) {
									partQuantityPrintList.add(afterChildPart);
									PartQuantitymap
											.put(afterChildPart, usageLink
													.getQuantity().getAmount());
								}
							}
						}
					} else {
						// 当EBOM没有该编号的部件时
						// PBOM新增
						Map<WTPart, WTPartUsageLink> afterBOM = collectSingleLevelBOMAndLink(
								afterPart, "Manufacturing");
						if (!afterBOM.isEmpty()) {
							for (WTPart part2 : afterBOM.keySet()) {
								WTPartUsageLink usageLink = afterBOM.get(part2);
								// 获取到PBOM新增的部件
								if (!morepartList.contains(part2)) {
									morepartList.add(part2);
									PartQuantitymap.put(part2, usageLink
											.getQuantity().getAmount());
								}
							}
						} else {
							if (!morepartList.contains(afterPart)) {
								morepartList.add(afterPart);
								for (ExportBomDetailsBean pbean : pts) {
									if (pbean.getChildPart().getNumber()
											.equals(afterPart.getNumber())) {
										PartQuantitymap.put(afterPart,
												pbean.getAmount());
									}
								}
							}
						}
					}
				}
				// 获取M视图子部件的所有IBA属性字符串
				String ppartIBAString = getPpartIBAList(ppart);
				// 获取D视图子部件的所有IBA属性字符串
				String epartIBAString = getEpartIBAList(epart);
				// 1.如果M视图和D视图子部件的IBA属性存在差异，就将存在差异的两个部件放入partIBAPrintList中
				if (!ppartIBAString.equals(epartIBAString)) {
					System.out.println("PBOM和EBOM属性存在差异");
					partIBAPrintList.add(ppart);
					partIBAPrintList.add(epart);
				} else {
					System.out.println("PBOM和EBOM属性相同");
				}
			}
			if (!list.contains(ppart) && ppart != null && epart != null) {
				list.add(ppart);
				epartList.add(epart);
				getAllChildPPart(ppart, list, epartList, partIBAPrintList,
						partQuantityPrintList, PartQuantitymap, lessPartList,
						morepartList);
			}
		}
	}

	private static Map<String, Map<String, List<WTPartUsageLink>>> getBomDifferences(
			WTPart epart, WTPart ppart) throws WTException {
		Map<String, Map<String, List<WTPartUsageLink>>> map = new HashMap<String, Map<String, List<WTPartUsageLink>>>();
		QueryResult queryResult2 = WTPartHelper.service
				.getUsesWTPartMasters(ppart);
		QueryResult queryResult1 = WTPartHelper.service
				.getUsesWTPartMasters(epart);
		Map<String, List<WTPartUsageLink>> map_add = new HashMap<String, List<WTPartUsageLink>>();
		Map<String, List<WTPartUsageLink>> map_rem = new HashMap<String, List<WTPartUsageLink>>();
		Map<String, List<WTPartUsageLink>> map_cha_bef = new HashMap<String, List<WTPartUsageLink>>();
		Map<String, List<WTPartUsageLink>> map_cha_aft = new HashMap<String, List<WTPartUsageLink>>();
		Map<String, List<WTPartUsageLink>> beforeLinkMap = new HashMap<String, List<WTPartUsageLink>>();
		Map<String, List<WTPartUsageLink>> afterLinkMap = new HashMap<String, List<WTPartUsageLink>>();
		List<WTPartUsageLink> linkList = null;
		while (queryResult1.hasMoreElements()) {
			WTPartUsageLink usageLink = (WTPartUsageLink) queryResult1
					.nextElement();
			if (!beforeLinkMap.containsKey(usageLink.getUses().getNumber())) {
				linkList = new ArrayList<WTPartUsageLink>();
				beforeLinkMap.put(usageLink.getUses().getNumber(), linkList);
			} else {
				linkList = beforeLinkMap.get(usageLink.getUses().getNumber());
			}
			linkList.add(usageLink);
		}
		while (queryResult2.hasMoreElements()) {
			WTPartUsageLink usageLink = (WTPartUsageLink) queryResult2
					.nextElement();
			if (!afterLinkMap.containsKey(usageLink.getUses().getNumber())) {
				linkList = new ArrayList<WTPartUsageLink>();
				afterLinkMap.put(usageLink.getUses().getNumber(), linkList);
			} else {
				linkList = afterLinkMap.get(usageLink.getUses().getNumber());
			}
			linkList.add(usageLink);
		}
		for (String key : afterLinkMap.keySet()) {
			if (beforeLinkMap.get(key) == null) {
				// 新增
				List<WTPartUsageLink> addUsageLinkList = afterLinkMap.get(key);
				map_add.put(key, addUsageLinkList);
			} else {
				List<WTPartUsageLink> beforeLinkList = beforeLinkMap.get(key);
				List<WTPartUsageLink> afterLinkList = afterLinkMap.get(key);
				List<WTPartUsageLink> copyBeforeLinkList = new ArrayList<WTPartUsageLink>();
				copyBeforeLinkList.addAll(beforeLinkList);
				List<WTPartUsageLink> copyAfterLinkList = new ArrayList<WTPartUsageLink>();
				copyAfterLinkList.addAll(afterLinkList);
				for (WTPartUsageLink afterLink : afterLinkList) {
					// 移除相同的link
					for (WTPartUsageLink beforeLink : beforeLinkList) {
						if (afterLink.getQuantity().equals(
								beforeLink.getQuantity())) {
							copyAfterLinkList.remove(afterLink);
							copyBeforeLinkList.remove(beforeLink);
						}
					}
				}
				// PBOM删除了link
				if (copyAfterLinkList.size() == 0
						&& copyBeforeLinkList.size() != 0) {
					map_rem.put(key, copyBeforeLinkList);
				}
				// PBOM新增了link
				if (copyAfterLinkList.size() != 0
						&& copyBeforeLinkList.size() == 0) {
					map_add.put(key, copyAfterLinkList);
				}
				// PBOMlink发送变化
				if (copyAfterLinkList.size() != 0
						&& copyBeforeLinkList.size() != 0) {
					map_cha_bef.put(key, copyBeforeLinkList);
					map_cha_aft.put(key, copyAfterLinkList);
				}
			}
		}
		for (String key : beforeLinkMap.keySet()) {
			if (afterLinkMap.get(key) == null) {
				// 删除
				List<WTPartUsageLink> remUsageLinkList = beforeLinkMap.get(key);
				map_rem.put(key, remUsageLinkList);
			}
		}
		map.put("add", map_add);
		map.put("rem", map_rem);
		map.put("cha_bef", map_cha_bef);
		map.put("cha_aft", map_cha_aft);
		return map;
	}

	public static Map<String, WTPart> buildPPartBean(WTPart ppart,
			List<ExportBomDetailsBean> ts, int i, Map<String, WTPart> map)
			throws WTException {
		// Map<String, WTPart> map = new HashMap<String, WTPart>();
		ExportBomDetailsBean bomDetails;
		Persistable persistable1 = null;
		WTPart countPpart = null;
		WTPart childPpart1 = null;
		if (map.get(ppart.getNumber()) == null) {
			map.put(ppart.getNumber(), ppart);
		}
		QueryResult qr1 = WTPartHelper.service.getUsesWTParts(ppart,
				getDefaultConfigSpec());// 得到子部件结果集
		while (qr1.hasMoreElements()) {
			bomDetails = new ExportBomDetailsBean();
			Persistable[] persistables1 = (Persistable[]) qr1.nextElement();
			WTPartUsageLink usageLink = (WTPartUsageLink) persistables1[0];
			bomDetails.setAmount(usageLink.getQuantity().getAmount());
			persistable1 = (Persistable) persistables1[1];
			if (persistable1 instanceof WTPartMaster) {
				childPpart1 = CSCPart.getPartByNumberAndViewName(
						((WTPartMaster) persistable1).getNumber(),
						"Manufacturing");
			} else if (persistable1 instanceof WTPart) {
				countPpart = (WTPart) persistable1;
				childPpart1 = CSCPart.getPartByNumberAndViewName(
						countPpart.getNumber(), "Manufacturing");// M视图子部件
			}
			String name = childPpart1.getName();
			System.out.println(name);

			bomDetails.setChildPart(childPpart1);
			ts.add(bomDetails);
			if (map.get(childPpart1.getNumber()) == null) {
				map.put(childPpart1.getNumber(), childPpart1);
			}
			buildEPartBean(childPpart1, ts, i, map);
		}
		return map;
	}

	public static Map<String, WTPart> buildEPartBean(WTPart epart,
			List<ExportBomDetailsBean> ts, int i, Map<String, WTPart> map)
			throws WTException {
		ExportBomDetailsBean bomDetails;
		Persistable persistable2 = null;
		WTPart countEpart = null;
		WTPart childEpart1 = null;
		// Map<String, WTPart> map = new HashMap<String, WTPart>();
		String name = epart.getName();
		if (map.get(epart.getNumber()) == null) {
			map.put(epart.getNumber(), epart);
		}
		// EBOM子部件使用数量
		QueryResult qr2 = WTPartHelper.service.getUsesWTParts(epart,
				getDefaultConfigSpec());// 得到子部件结果集
		while (qr2.hasMoreElements()) {
			bomDetails = new ExportBomDetailsBean();
			Persistable[] persistables2 = (Persistable[]) qr2.nextElement();
			WTPartUsageLink usageLink = (WTPartUsageLink) persistables2[0];
			bomDetails.setAmount(usageLink.getQuantity().getAmount());
			persistable2 = (Persistable) persistables2[1];
			if (persistable2 instanceof WTPartMaster) {
				continue;
			}
			countEpart = (WTPart) persistable2;
			childEpart1 = CSCPart.getPartByNumberAndViewName(
					countEpart.getNumber(), "Design");// D视图子部件
			bomDetails.setChildPart(childEpart1);
			ts.add(bomDetails);
			if (map.get(childEpart1.getNumber()) == null) {
				map.put(childEpart1.getNumber(), childEpart1);
			}
			buildEPartBean(childEpart1, ts, i, map);
		}
		return map;
	}

	private static ConfigSpec getDefaultConfigSpec() throws WTException {
		return ConfigHelper.service.getDefaultConfigSpecFor(WTPart.class);
	}

	/**
	 * 获取EBOM属性
	 *
	 * @param part
	 * @param list
	 * @throws WTException
	 */
	public static String getEpartIBAList(WTPart part) throws WTException {

		StringBuffer result = new StringBuffer();
		// 备注:
		String dREMARK = getNotNullParam(IBAHelper.getIBAStringValue(part,
				"REMARK"));
		result.append("REMARK=");
		result.append(dREMARK).append("|");
		// 材料:
		String dCMAT = getNotNullParam(IBAHelper
				.getIBAStringValue(part, "CMAT"));
		result.append("CMAT=");
		result.append(dCMAT).append("|");
		// 材料名称:
		String dPTC_MATERIAL_NAME = getNotNullParam(IBAHelper
				.getIBAStringValue(part, "PTC_MATERIAL_NAME"));
		result.append("PTC_MATERIAL_NAME=");
		result.append(dPTC_MATERIAL_NAME).append("|");
		// 材料上标:
		String dCMAT_UP = getNotNullParam(IBAHelper.getIBAStringValue(part,
				"CMAT_UP"));
		result.append("CMAT_UP=");
		result.append(dCMAT_UP).append("|");
		// 材料下标:
		String dCMAT_DOWN = getNotNullParam(IBAHelper.getIBAStringValue(part,
				"CMAT_DOWN"));
		result.append("CMAT_DOWN=");
		result.append(dCMAT_DOWN).append("|");
		// 规格:
		String dSTANDARD = getNotNullParam(IBAHelper.getIBAStringValue(part,
				"STANDARD"));
		result.append("STANDARD=");
		result.append(dSTANDARD).append("|");
		// 成套件标识:
		String dSETMARK = getNotNullParam(IBAHelper.getIBAStringValue(part,
				"SETMARK"));
		result.append("SETMARK=");
		result.append(dSETMARK).append("|");
		// 零部件分类:
		String dCTYPE = getNotNullParam(IBAHelper.getIBAStringValue(part,
				"CTYPE"));
		result.append("CTYPE=");
		result.append(dCTYPE).append("|");
		// 密级:
		String dSECRET = getNotNullParam(IBAHelper.getIBAStringValue(part,
				"SECRET"));
		result.append("SECRET=");
		result.append(dSECRET).append("|");
		// 所属成品:
		String dENDITEMIN = getNotNullParam(IBAHelper.getIBAStringValue(part,
				"ENDITEMIN"));
		result.append("ENDITEMIN=");
		result.append(dENDITEMIN).append("|");
		// 所属型号:
		String dMINDEX = getNotNullParam(IBAHelper.getIBAStringValue(part,
				"MINDEX"));
		result.append("MINDEX=");
		result.append(dMINDEX).append("|");
		// 图号:
		String dCINDEX = getNotNullParam(IBAHelper.getIBAStringValue(part,
				"CINDEX"));
		result.append("CINDEX=");
		result.append(dCINDEX).append("|");
		// 中文名称:
		String dPTC_COMMON_NAME = getNotNullParam(IBAHelper.getIBAStringValue(
				part, "PTC_COMMON_NAME"));
		result.append("PTC_COMMON_NAME=");
		result.append(dPTC_COMMON_NAME).append("|");
		// 产品代号:
		String dPINDEX = getNotNullParam(IBAHelper.getIBAStringValue(part,
				"PINDEX"));
		result.append("PINDEX=");
		result.append(dPINDEX).append("|");
		// 设计者:
		String dDESIGNER = getNotNullParam(IBAHelper.getIBAStringValue(part,
				"DESIGNER"));
		result.append("DESIGNER=");
		result.append(dDESIGNER).append("|");
		// 设计单位:
		String dCOMPANY = getNotNullParam(IBAHelper.getIBAStringValue(part,
				"COMPANY"));
		result.append("COMPANY=");
		result.append(dCOMPANY).append("|");
		// 工艺路线:
		String dROUTING = getNotNullParam(IBAHelper.getIBAStringValue(part,
				"ROUTING"));
		result.append("ROUTING=");
		result.append(dROUTING).append("|");
		// 工装代号:
		String dPRODUCT_INDEX = getNotNullParam(IBAHelper.getIBAStringValue(
				part, "PRODUCT_INDEX"));
		result.append("PRODUCT_INDEX=");
		result.append(dPRODUCT_INDEX).append("|");
		// 主制车间:
		String dZZCJ = getNotNullParam(IBAHelper
				.getIBAStringValue(part, "ZZCJ"));
		result.append("ZZCJ=");
		result.append(dZZCJ).append("|");
		// 辅制车间:
		String dFZCJ = getNotNullParam(IBAHelper
				.getIBAStringValue(part, "FZCJ"));
		result.append("FZCJ=");
		result.append(dFZCJ).append("|");
		// 零组件生产类型:
		String dMTYPE = getNotNullParam(IBAHelper.getIBAStringValue(part,
				"MTYPE"));
		result.append("MTYPE=");
		result.append(dMTYPE).append("|");
		// 批次:
		String dBATCH = getNotNullParam(IBAHelper.getIBAStringValue(part,
				"BATCH"));
		result.append("BATCH=");
		result.append(dBATCH).append("|");
		// 物资简称:
		String dSHORTNAME = getNotNullParam(IBAHelper.getIBAStringValue(part,
				"SHORTNAME"));
		result.append("SHORTNAME=");
		result.append(dSHORTNAME).append("|");
		// 编码分类:
		String dClassificationNode = getNotNullParam(IBAHelper
				.getIBAStringValue(part, "ClassificationNode"));
		result.append("ClassificationNode=");
		result.append(dClassificationNode).append("|");
		// 编码优选级别:
		String dYXJB = getNotNullParam(IBAHelper
				.getIBAStringValue(part, "YXJB"));
		result.append("YXJB=");
		result.append(dYXJB).append("|");
		// 编码状态:
		String dBMZT = getNotNullParam(IBAHelper
				.getIBAStringValue(part, "BMZT"));
		result.append("BMZT=");
		result.append(dBMZT).append("|");
		// 编码类型:
		String dBMLX = getNotNullParam(IBAHelper
				.getIBAStringValue(part, "BMLX"));
		result.append("BMLX=");
		result.append(dBMLX).append("|");
		// 特殊说明:
		String dSPECIALINSTRUCTION = getNotNullParam(IBAHelper
				.getIBAStringValue(part, "SPECIALINSTRUCTION"));
		result.append("SPECIALINSTRUCTION=");
		result.append(dSPECIALINSTRUCTION).append("|");
		// 是否进口:
		String dISIMPORT = getNotNullParam(IBAHelper.getIBAStringValue(part,
				"ISIMPORT"));
		result.append("ISIMPORT=");
		result.append(dISIMPORT).append("|");
		// 计量单位:
		String dMEASUREUNIT = getNotNullParam(IBAHelper.getIBAStringValue(part,
				"MEASUREUNIT"));
		result.append("MEASUREUNIT=");
		result.append(dMEASUREUNIT).append("|");
		// 材料编号:
		String dNUMBER = getNotNullParam(IBAHelper.getIBAStringValue(part,
				"NUMBER"));
		result.append("NUMBER=");
		result.append(dNUMBER).append("|");
		// 材料类型:
		String dMATTYPE = getNotNullParam(IBAHelper.getIBAStringValue(part,
				"MATTYPE"));
		result.append("MATTYPE=");
		result.append(dMATTYPE).append("|");
		// 牌号:
		String dMARKNUMBER = getNotNullParam(IBAHelper.getIBAStringValue(part,
				"MARKNUMBER"));
		result.append("MARKNUMBER=");
		result.append(dMARKNUMBER).append("|");
		// 供应状态:
		String dSUPPLYSTATE = getNotNullParam(IBAHelper.getIBAStringValue(part,
				"SUPPLYSTATE"));
		result.append("SUPPLYSTATE=");
		result.append(dSUPPLYSTATE).append("|");
		// 采用标准:
		String dUSESTANDARD = getNotNullParam(IBAHelper.getIBAStringValue(part,
				"USESTANDARD"));
		result.append("USESTANDARD=");
		result.append(dUSESTANDARD).append("|");
		// 标准号:
		String dSTANDARDNUMBER = getNotNullParam(IBAHelper.getIBAStringValue(
				part, "STANDARDNUMBER"));
		result.append("STANDARDNUMBER=");
		result.append(dSTANDARDNUMBER).append("|");
		// 机械性能等级或硬度:
		String dMECHANICALPROPERTYORHARDNESS = getNotNullParam(IBAHelper
				.getIBAStringValue(part, "MECHANICALPROPERTYORHARDNESS"));
		result.append("MECHANICALPROPERTYORHARDNESS=");
		result.append(dMECHANICALPROPERTYORHARDNESS).append("|");
		// 表面处理:
		String dSURFACETREATMENT = getNotNullParam(IBAHelper.getIBAStringValue(
				part, "SURFACETREATMENT"));
		result.append("SURFACETREATMENT=");
		result.append(dSURFACETREATMENT).append("|");
		// 热处理:
		String dHEATTREATMENT = getNotNullParam(IBAHelper.getIBAStringValue(
				part, "HEATTREATMENT"));
		result.append("HEATTREATMENT=");
		result.append(dHEATTREATMENT).append("|");
		// 产品型式:
		String dPRODUCTFORM = getNotNullParam(IBAHelper.getIBAStringValue(part,
				"PRODUCTFORM"));
		result.append("PRODUCTFORM=");
		result.append(dPRODUCTFORM).append("|");
		// 产品等级:
		String dPRODUCTLEVEL = getNotNullParam(IBAHelper.getIBAStringValue(
				part, "PRODUCTLEVEL"));
		result.append("PRODUCTLEVEL=");
		result.append(dPRODUCTLEVEL).append("|");
		// 板拧形式:
		String dPLATECSCREWFORM = getNotNullParam(IBAHelper.getIBAStringValue(
				part, "PLATECSCREWFORM"));
		result.append("PLATECSCREWFORM=");
		result.append(dPLATECSCREWFORM).append("|");
		// 型号:
		String dTYPE = getNotNullParam(IBAHelper
				.getIBAStringValue(part, "TYPE"));
		result.append("TYPE=");
		result.append(dTYPE).append("|");
		// 型号规格:
		String dTYPESTANDARD = getNotNullParam(IBAHelper.getIBAStringValue(
				part, "TYPESTANDARD"));
		result.append("TYPESTANDARD=");
		result.append(dTYPESTANDARD).append("|");
		// 质量等级:
		String dQUALITYLEVEL = getNotNullParam(IBAHelper.getIBAStringValue(
				part, "QUALITYLEVEL"));
		result.append("QUALITYLEVEL=");
		result.append(dQUALITYLEVEL).append("|");
		// 总规范:
		String dTOTALSTANDARD = getNotNullParam(IBAHelper.getIBAStringValue(
				part, "TOTALSTANDARD"));
		result.append("TOTALSTANDARD=");
		result.append(dTOTALSTANDARD).append("|");
		// 详细规范:
		String dDETAILSTANDARD = getNotNullParam(IBAHelper.getIBAStringValue(
				part, "DETAILSTANDARD"));
		result.append("DETAILSTANDARD=");
		result.append(dDETAILSTANDARD).append("|");
		// 封装形式:
		String dPACKAGINGFORM = getNotNullParam(IBAHelper.getIBAStringValue(
				part, "PACKAGINGFORM"));
		result.append("PACKAGINGFORM=");
		result.append(dPACKAGINGFORM).append("|");
		// 外形尺寸:
		String dOUTLINESIZE = getNotNullParam(IBAHelper.getIBAStringValue(part,
				"OUTLINESIZE"));
		result.append("OUTLINESIZE=");
		result.append(dOUTLINESIZE).append("|");
		// 专用条件:
		String dSPECIALCONDITION = getNotNullParam(IBAHelper.getIBAStringValue(
				part, "SPECIALCONDITION"));
		result.append("SPECIALCONDITION=");
		result.append(dSPECIALCONDITION).append("|");
		// 附加协议:
		String dEXTRACONDITION = getNotNullParam(IBAHelper.getIBAStringValue(
				part, "EXTRACONDITION"));
		result.append("EXTRACONDITION=");
		result.append(dEXTRACONDITION);
		return result.toString();
	}

	/**
	 * 获取PBOM属性
	 *
	 * @param part
	 * @param list
	 * @throws WTException
	 */
	public static String getPpartIBAList(WTPart ppart) throws WTException {
		StringBuffer result = new StringBuffer();
		// 备注:
		String REMARK = getNotNullParam(IBAHelper.getIBAStringValue(ppart,
				"REMARK"));
		result.append("REMARK=");
		result.append(REMARK).append("|");
		// 材料:
		String CMAT = getNotNullParam(IBAHelper
				.getIBAStringValue(ppart, "CMAT"));
		result.append("CMAT=");
		result.append(CMAT).append("|");
		// 材料名称:
		String PTC_MATERIAL_NAME = getNotNullParam(IBAHelper.getIBAStringValue(
				ppart, "PTC_MATERIAL_NAME"));
		result.append("PTC_MATERIAL_NAME=");
		result.append(PTC_MATERIAL_NAME).append("|");
		// 材料上标:
		String CMAT_UP = getNotNullParam(IBAHelper.getIBAStringValue(ppart,
				"CMAT_UP"));
		result.append("CMAT_UP=");
		result.append(CMAT_UP).append("|");
		// 材料下标:
		String CMAT_DOWN = getNotNullParam(IBAHelper.getIBAStringValue(ppart,
				"CMAT_DOWN"));
		result.append("CMAT_DOWN=");
		result.append(CMAT_DOWN).append("|");
		// 规格:
		String STANDARD = getNotNullParam(IBAHelper.getIBAStringValue(ppart,
				"STANDARD"));
		result.append("STANDARD=");
		result.append(STANDARD).append("|");
		// 成套件标识:
		String SETMARK = getNotNullParam(IBAHelper.getIBAStringValue(ppart,
				"SETMARK"));
		result.append("SETMARK=");
		result.append(SETMARK).append("|");
		// 零部件分类:
		String CTYPE = getNotNullParam(IBAHelper.getIBAStringValue(ppart,
				"CTYPE"));
		result.append("CTYPE=");
		result.append(CTYPE).append("|");
		// 密级:
		String SECRET = getNotNullParam(IBAHelper.getIBAStringValue(ppart,
				"SECRET"));
		result.append("SECRET=");
		result.append(SECRET).append("|");
		// 所属成品:
		String ENDITEMIN = getNotNullParam(IBAHelper.getIBAStringValue(ppart,
				"ENDITEMIN"));
		result.append("ENDITEMIN=");
		result.append(ENDITEMIN).append("|");
		// 所属型号:
		String MINDEX = getNotNullParam(IBAHelper.getIBAStringValue(ppart,
				"MINDEX"));
		result.append("MINDEX=");
		result.append(MINDEX).append("|");
		// 图号:
		String CINDEX = getNotNullParam(IBAHelper.getIBAStringValue(ppart,
				"CINDEX"));
		result.append("CINDEX=");
		result.append(CINDEX).append("|");
		// 中文名称:
		String PTC_COMMON_NAME = getNotNullParam(IBAHelper.getIBAStringValue(
				ppart, "PTC_COMMON_NAME"));
		result.append("PTC_COMMON_NAME=");
		result.append(PTC_COMMON_NAME).append("|");
		// 产品代号:
		String PINDEX = getNotNullParam(IBAHelper.getIBAStringValue(ppart,
				"PINDEX"));
		result.append("PINDEX=");
		result.append(PINDEX).append("|");
		// 设计者:
		String DESIGNER = getNotNullParam(IBAHelper.getIBAStringValue(ppart,
				"DESIGNER"));
		result.append("DESIGNER=");
		result.append(DESIGNER).append("|");
		// 设计单位:
		String COMPANY = getNotNullParam(IBAHelper.getIBAStringValue(ppart,
				"COMPANY"));
		result.append("COMPANY=");
		result.append(COMPANY).append("|");
		// 工艺路线:
		String ROUTING = getNotNullParam(IBAHelper.getIBAStringValue(ppart,
				"ROUTING"));
		result.append("ROUTING=");
		result.append(ROUTING).append("|");
		// 工装代号:
		String PRODUCT_INDEX = getNotNullParam(IBAHelper.getIBAStringValue(
				ppart, "PRODUCT_INDEX"));
		result.append("PRODUCT_INDEX=");
		result.append(PRODUCT_INDEX).append("|");
		// 主制车间:
		String ZZCJ = getNotNullParam(IBAHelper
				.getIBAStringValue(ppart, "ZZCJ"));
		result.append("ZZCJ=");
		result.append(ZZCJ).append("|");
		// 辅制车间:
		String FZCJ = getNotNullParam(IBAHelper
				.getIBAStringValue(ppart, "FZCJ"));
		result.append("FZCJ=");
		result.append(FZCJ).append("|");
		// 零组件生产类型:
		String MTYPE = getNotNullParam(IBAHelper.getIBAStringValue(ppart,
				"MTYPE"));
		result.append("MTYPE=");
		result.append(MTYPE).append("|");
		// 批次:
		String BATCH = getNotNullParam(IBAHelper.getIBAStringValue(ppart,
				"BATCH"));
		result.append("BATCH=");
		result.append(BATCH).append("|");
		// 物资简称:
		String SHORTNAME = getNotNullParam(IBAHelper.getIBAStringValue(ppart,
				"SHORTNAME"));
		result.append("SHORTNAME=");
		result.append(SHORTNAME).append("|");
		// 编码分类:
		String ClassificationNode = getNotNullParam(IBAHelper
				.getIBAStringValue(ppart, "ClassificationNode"));
		result.append("ClassificationNode=");
		result.append(ClassificationNode).append("|");
		// 编码优选级别:
		String YXJB = getNotNullParam(IBAHelper
				.getIBAStringValue(ppart, "YXJB"));
		result.append("YXJB=");
		result.append(YXJB).append("|");
		// 编码状态:
		String BMZT = getNotNullParam(IBAHelper
				.getIBAStringValue(ppart, "BMZT"));
		result.append("BMZT=");
		result.append(BMZT).append("|");
		// 编码类型:
		String BMLX = getNotNullParam(IBAHelper
				.getIBAStringValue(ppart, "BMLX"));
		result.append("BMLX=");
		result.append(BMLX).append("|");
		// 特殊说明:
		String SPECIALINSTRUCTION = getNotNullParam(IBAHelper
				.getIBAStringValue(ppart, "SPECIALINSTRUCTION"));
		result.append("SPECIALINSTRUCTION=");
		result.append(SPECIALINSTRUCTION).append("|");
		// 是否进口:
		String ISIMPORT = getNotNullParam(IBAHelper.getIBAStringValue(ppart,
				"ISIMPORT"));
		result.append("ISIMPORT=");
		result.append(ISIMPORT).append("|");
		// 计量单位:
		String MEASUREUNIT = getNotNullParam(IBAHelper.getIBAStringValue(ppart,
				"MEASUREUNIT"));
		result.append("MEASUREUNIT=");
		result.append(MEASUREUNIT).append("|");
		// 材料编号:
		String NUMBER = getNotNullParam(IBAHelper.getIBAStringValue(ppart,
				"NUMBER"));
		result.append("NUMBER=");
		result.append(NUMBER).append("|");
		// 材料类型:
		String MATTYPE = getNotNullParam(IBAHelper.getIBAStringValue(ppart,
				"MATTYPE"));
		result.append("MATTYPE=");
		result.append(MATTYPE).append("|");
		// 牌号:
		String MARKNUMBER = getNotNullParam(IBAHelper.getIBAStringValue(ppart,
				"MARKNUMBER"));
		result.append("MARKNUMBER=");
		result.append(MARKNUMBER).append("|");
		// 供应状态:
		String SUPPLYSTATE = getNotNullParam(IBAHelper.getIBAStringValue(ppart,
				"SUPPLYSTATE"));
		result.append("SUPPLYSTATE=");
		result.append(SUPPLYSTATE).append("|");
		// 采用标准:
		String USESTANDARD = getNotNullParam(IBAHelper.getIBAStringValue(ppart,
				"USESTANDARD"));
		result.append("USESTANDARD=");
		result.append(USESTANDARD).append("|");
		// 标准号:
		String STANDARDNUMBER = getNotNullParam(IBAHelper.getIBAStringValue(
				ppart, "STANDARDNUMBER"));
		result.append("STANDARDNUMBER=");
		result.append(STANDARDNUMBER).append("|");
		// 机械性能等级或硬度:
		String MECHANICALPROPERTYORHARDNESS = getNotNullParam(IBAHelper
				.getIBAStringValue(ppart, "MECHANICALPROPERTYORHARDNESS"));
		result.append("MECHANICALPROPERTYORHARDNESS=");
		result.append(MECHANICALPROPERTYORHARDNESS).append("|");
		// 表面处理:
		String SURFACETREATMENT = getNotNullParam(IBAHelper.getIBAStringValue(
				ppart, "SURFACETREATMENT"));
		result.append("SURFACETREATMENT=");
		result.append(SURFACETREATMENT).append("|");
		// 热处理:
		String HEATTREATMENT = getNotNullParam(IBAHelper.getIBAStringValue(
				ppart, "HEATTREATMENT"));
		result.append("HEATTREATMENT=");
		result.append(HEATTREATMENT).append("|");
		// 产品型式:
		String PRODUCTFORM = getNotNullParam(IBAHelper.getIBAStringValue(ppart,
				"PRODUCTFORM"));
		result.append("PRODUCTFORM=");
		result.append(PRODUCTFORM).append("|");
		// 产品等级:
		String PRODUCTLEVEL = getNotNullParam(IBAHelper.getIBAStringValue(
				ppart, "PRODUCTLEVEL"));
		result.append("PRODUCTLEVEL=");
		result.append(PRODUCTLEVEL).append("|");
		// 板拧形式:
		String PLATECSCREWFORM = getNotNullParam(IBAHelper.getIBAStringValue(
				ppart, "PLATECSCREWFORM"));
		result.append("PLATECSCREWFORM=");
		result.append(PLATECSCREWFORM).append("|");
		// 型号:
		String TYPE = getNotNullParam(IBAHelper
				.getIBAStringValue(ppart, "TYPE"));
		result.append("TYPE=");
		result.append(TYPE).append("|");
		// 型号规格:
		String TYPESTANDARD = getNotNullParam(IBAHelper.getIBAStringValue(
				ppart, "TYPESTANDARD"));
		result.append("TYPESTANDARD=");
		result.append(TYPESTANDARD).append("|");
		// 质量等级:
		String QUALITYLEVEL = getNotNullParam(IBAHelper.getIBAStringValue(
				ppart, "QUALITYLEVEL"));
		result.append("QUALITYLEVEL=");
		result.append(QUALITYLEVEL).append("|");
		// 总规范:
		String TOTALSTANDARD = getNotNullParam(IBAHelper.getIBAStringValue(
				ppart, "TOTALSTANDARD"));
		result.append("TOTALSTANDARD=");
		result.append(TOTALSTANDARD).append("|");
		// 详细规范:
		String DETAILSTANDARD = getNotNullParam(IBAHelper.getIBAStringValue(
				ppart, "DETAILSTANDARD"));
		result.append("DETAILSTANDARD=");
		result.append(DETAILSTANDARD).append("|");
		// 封装形式:
		String PACKAGINGFORM = getNotNullParam(IBAHelper.getIBAStringValue(
				ppart, "PACKAGINGFORM"));
		result.append("PACKAGINGFORM=");
		result.append(PACKAGINGFORM).append("|");
		// 外形尺寸:
		String OUTLINESIZE = getNotNullParam(IBAHelper.getIBAStringValue(ppart,
				"OUTLINESIZE"));
		result.append("OUTLINESIZE=");
		result.append(OUTLINESIZE).append("|");
		// 专用条件:
		String SPECIALCONDITION = getNotNullParam(IBAHelper.getIBAStringValue(
				ppart, "SPECIALCONDITION"));
		result.append("SPECIALCONDITION=");
		result.append(SPECIALCONDITION).append("|");
		// 附加协议:
		String EXTRACONDITION = getNotNullParam(IBAHelper.getIBAStringValue(
				ppart, "EXTRACONDITION"));
		result.append("EXTRACONDITION=");
		result.append(EXTRACONDITION);
		return result.toString();
	}

	private static String getNotNullParam(Object obj) {
		if (obj == null) {
			return "";
		} else {
			return obj.toString();
		}
	}

	/*
	 * 获取ConfigSpec
	 */
	private static ConfigSpec getConfigSpec() throws WTException {

		return ConfigHelper.service.getDefaultConfigSpecFor(WTPart.class);

	}

	/**
	 *
	 * @param num
	 * @return
	 * @throws WTException
	 */
	public static WTPart getNewVersionPart(String partNumber, String view)
			throws WTException {
		if (partNumber == null || partNumber.length() == 0)
			return null;

		QuerySpec qs = new QuerySpec(WTPart.class);
		SearchCondition scNumber = new SearchCondition(WTPart.class,
				WTPart.NUMBER, SearchCondition.EQUAL, partNumber.toUpperCase());
		SearchCondition scLatestIteration = new SearchCondition(WTPart.class,
				WTAttributeNameIfc.LATEST_ITERATION, SearchCondition.IS_TRUE);
		qs.appendWhere(scNumber);
		qs.appendAnd();
		qs.appendWhere(scLatestIteration);
		if (view != null && view.length() > 0) {
			qs.appendAnd();
			SearchCondition scView = new SearchCondition(WTPart.class,
					"view.key", SearchCondition.EQUAL,
					PersistenceHelper.getObjectIdentifier(ViewHelper.service
							.getView(view)));
			qs.appendWhere(scView);
		}
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr != null && qr.hasMoreElements())
			qr = (new LatestConfigSpec()).process(qr);

		if (qr != null && qr.hasMoreElements())
			return (WTPart) qr.nextElement();
		return null;

	}

	/**
	 * 根据某个部件收集单层BOM
	 *
	 * @param wtPart
	 * @param viewName
	 * @throws WTException
	 */
	public static Map<WTPart, WTPartUsageLink> collectSingleLevelBOMAndLink(
			WTPart wtPart, String viewName) throws WTException {
		Map<WTPart, WTPartUsageLink> cacheBOM = new HashMap<WTPart, WTPartUsageLink>();
		QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(wtPart);
		while (qr.hasMoreElements()) {
			WTPartUsageLink link = (WTPartUsageLink) qr.nextElement();
			WTPartMaster wtPartMaster = (WTPartMaster) link.getUses();
			WTPart childPart = getWTPart(wtPartMaster.getNumber(), viewName);
			if (cacheBOM.get(childPart) == null) {
				cacheBOM.put(childPart, link);
			}
		}
		return cacheBOM;
	}

	/**
	 * 根据编号返回部件指定视图的最新版本
	 *
	 * @param number
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static WTPart getWTPart(String number, String viewName) {
		return getWTPart(number, viewName, null);
	}

	/**
	 * 根据编号返回部件指定视图和生命周期状态最新版
	 *
	 * @param number
	 * @return
	 * @throws WTException
	 * @throws ViewException
	 * @throws WTPropertyVetoException
	 */
	public static WTPart getWTPart(String number, String viewName,
			String stateDisplay) {
		try {
			// 视图
			View view = null;
			if (StringUtils.isNotEmpty(viewName)) {
				view = ViewHelper.service.getView(viewName);
			}

			// 生命周期状态
			State state = null;
			if (StringUtils.isNotEmpty(stateDisplay)) {
				state = State.toState(stateDisplay);
			}

			return getWTPart(number, view, state);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据编号和视图返回Part最新版本
	 *
	 * @param number
	 * @param view
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	@SuppressWarnings("deprecation")
	public static WTPart getWTPart(String number, View view, State state)
			throws WTException, WTPropertyVetoException {
		if (StringUtils.isEmpty(number)) {
			return null;
		}
		WTPart wtpart = null;
		WTPart returnpart = null;
		QuerySpec qs = new QuerySpec(WTPart.class);

		// Latest Iteration，仅查询最新版本的，过滤掉历史版本数据。
		SearchCondition scLatestIteration = new SearchCondition(WTPart.class,
				WTPart.LATEST_ITERATION, SearchCondition.IS_TRUE);
		qs.appendWhere(scLatestIteration, new int[] { 0 });

		ClassAttribute caNumber = new ClassAttribute(WTPart.class,
				WTPart.NUMBER);
		SQLFunction upperNumber = SQLFunction.newSQLFunction("UPPER", caNumber);
		SearchCondition scNumber = new SearchCondition(upperNumber,
				SearchCondition.EQUAL, ConstantExpression.newExpression(number
						.toUpperCase()));
		qs.appendAnd();
		qs.appendWhere(scNumber, new int[] { 0 });
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);

		if (qr.hasMoreElements()) {
			wtpart = (WTPart) qr.nextElement();
		}

		if (wtpart == null) {
			return null;
		}

		QueryResult localQueryResult = null;
		if (WorkInProgressHelper.isWorkingCopy(wtpart)) {
			wtpart = (WTPart) WorkInProgressHelper.service
					.originalCopyOf(wtpart);
		}
		try {
			int i = ((wtpart instanceof OneOffVersioned) && (VersionControlHelper
					.isAOneOff((OneOffVersioned) wtpart))) ? 1 : 0;
			WTContainerRef localWTContainerRef = ((WTContained) wtpart)
					.getContainerReference();

			if ((i != 0)
					&& (Project2.class.isAssignableFrom(localWTContainerRef
							.getReferencedClass()))) {
				localQueryResult = VersionControlHelper.service
						.iterationsOf(wtpart.getIterationInfo().getBranchId());
			} else {
				localQueryResult = VersionControlHelper.service
						.allIterationsOf(wtpart.getMaster(), true);
			}
		} catch (WTException e) {
			e.printStackTrace();
		}

		// 自定义视图过滤方法，防止出现过滤后版本顺序改变的问题
		localQueryResult = process(localQueryResult, view);

		while (localQueryResult != null && localQueryResult.hasMoreElements()) {
			WTPart wtpartTemp = (WTPart) localQueryResult.nextElement();

			// 排除工作副本
			if (WorkInProgressHelper.isWorkingCopy(wtpartTemp)) {
				continue;
			}

			// 排除一次性版本
			if ((wtpartTemp instanceof OneOffVersioned)
					&& VersionControlHelper
							.isAOneOff((OneOffVersioned) wtpartTemp)) {
				continue;
			}

			// 过滤生命周期状态
			if (state != null
					&& !state.equals(wtpartTemp.getState().getState())) {
				continue;
			}

			returnpart = wtpartTemp;
			if (returnpart != null) {
				break;
			}
		}
		return returnpart;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static QueryResult process(QueryResult paramQueryResult, View view)
			throws WTException {
		if ((paramQueryResult == null) || (!paramQueryResult.hasMoreElements())) {
			return new QueryResult();
		}
		ObjectVector localObjectVector = new ObjectVector();
		HashMap localHashMap = new HashMap();
		while (paramQueryResult.hasMoreElements()) {
			Object localObject1 = paramQueryResult.nextElement();
			if (!(localObject1 instanceof ViewManageable)) {
				Object[] localObject2 = new Object[] { "results",
						ViewManageable.class.getName() };
				throw new ConfigException("wt.vc.config.configResource", "1",
						localObject2);
			}

			if (ViewHelper.getView((ViewManageable) localObject1) == null) {
				localObjectVector.addElement(localObject1);
			} else {
				Object localObject2 = ((ViewManageable) localObject1)
						.getMaster();
				if (localHashMap.get(localObject2) == null) {
					localHashMap.put(localObject2, new ArrayList());
				}
				((Collection) localHashMap.get(localObject2))
						.add((ViewManageable) localObject1);
			}
		}
		paramQueryResult.reset();

		for (Object localObject1 = localHashMap.entrySet().iterator(); ((Iterator) localObject1)
				.hasNext();) {
			Object localObject2 = (Map.Entry) ((Iterator) localObject1).next();
			Collection<ViewManageable> localCollection = (Collection<ViewManageable>) ((Map.Entry) localObject2)
					.getValue();

			localCollection = filterByView(localCollection, view);

			for (ViewManageable localViewManageable : localCollection) {
				localObjectVector.addElement(localViewManageable);
			}
		}

		return (QueryResult) (QueryResult) new QueryResult(localObjectVector);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected static Collection<ViewManageable> filterByView(
			Collection<ViewManageable> paramCollection, View paramView)
			throws WTException {
		ArrayList localHashSet = new ArrayList();

		if ((paramCollection == null) || (paramCollection.isEmpty())) {
			return localHashSet;
		}

		if (paramView == null) {
			localHashSet.addAll(paramCollection);
			return localHashSet;
		}

		View localView = paramView;
		while ((localHashSet.isEmpty()) && (localView != null)) {
			for (ViewManageable localViewManageable : paramCollection) {
				if (PersistenceHelper.isEquivalent(localView,
						ViewHelper.getView(localViewManageable))) {

					localHashSet.add(localViewManageable);
				}
			}
			localView = ViewHelper.service.getParent(localView);
		}
		return localHashSet;
	}
}
