package ext.casc.report;

import com.glaway.mpm.util.DateUtil;
import com.glaway.mpm.util.PropertiesUtil;
import ext.casc.integrate.util.BomUtil;
import ext.casc.util.IBAHelper;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import wt.fc.*;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pdmlink.PDMLinkProduct;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.vc.VersionControlHelper;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BOMReprotsService {
	public static Connection conn = null;
	public static int[] index = { 0 };
	static {
		try {
			conn = wt.pds.oracle81.OracleDataSource.getOracleDataSource().getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Object getBOMReport(String productName) {
		System.out.println(conn);
		List<BOMReport> result = new ArrayList<BOMReport>();
		result = getPartByProductName(productName);
		return result;
	}

	public static List<BOMReport> getPartByProductName(String productName) {
		List<BOMReport> result = new ArrayList<BOMReport>();
		try {
			PDMLinkProduct product = null;
			QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
			if (productName == null || "".equals(productName)) {
				return null;
			}
			qs.appendWhere(new SearchCondition(PDMLinkProduct.class, PDMLinkProduct.NAME, SearchCondition.EQUAL, productName));
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				product = (PDMLinkProduct) qr.nextElement();
			}
			if (product != null) {
				WTPartMaster partMaster = null;
				ReferenceFactory ref = new ReferenceFactory();
				WTReference wtf = ref.getReference(product);
				String oid = ref.getReferenceString(wtf);
				oid = oid.replace("OR:wt.pdmlink.PDMLinkProduct:", "");
				QuerySpec qs1 = new QuerySpec(WTPartMaster.class);
				qs1.appendWhere(new SearchCondition(WTPartMaster.class, WTPartMaster.CONTAINER_ID, SearchCondition.EQUAL, Long.valueOf(oid).longValue()));
				QueryResult qr1 = PersistenceHelper.manager.find(qs1);
				while (qr1.hasMoreElements()) {
					partMaster = (WTPartMaster) qr1.nextElement();
					QueryResult parts = VersionControlHelper.service.allIterationsOf(partMaster);
					if (parts != null && parts.hasMoreElements()) {
						WTPart part = (WTPart) parts.nextElement();
						if (!isHasParent(part) && isHasChild(part)) {
							BOMReport bomReport = new BOMReport();
							bomReport.setPartName(part.getName());
							bomReport.setView(part.getIdentity());
							bomReport.setPartNumber(part.getNumber());
							bomReport.setCreator(part.getCreatorName());
							ReferenceFactory ref1 = new ReferenceFactory();
							bomReport.setPartOid(ref1.getReferenceString((Persistable) part));
							bomReport.setView(getView(part));
							String time = part.getCreateTimestamp().toString();
							time = time.substring(0, time.length() - 2);
							bomReport.setCreateTime(time);
							result.add(bomReport);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String getView(WTPart part) {
		String version = part.getVersionIdentifier().getValue();
		String number = part.getIterationIdentifier().getValue();
		String viewName = part.getViewName();
		String view = version + "." + number + " (" + viewName + ")";
		return view;
	}

	public static boolean isHasParent(WTPart part) {
		WTPartMaster master = (WTPartMaster) part.getMaster();
		try {
			QueryResult qr = WTPartHelper.service.getUsedByWTParts(master);
			if (qr == null) {
				return false;
			}
			if (qr.hasMoreElements()) {
				return true;
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isHasChild(WTPart part) {
		try {
			QueryResult result = WTPartHelper.service.getUsesWTPartMasters(part);
			if (result == null) {
				return false;
			}
			if (result.hasMoreElements()) {
				return true;
			}
		} catch (WTException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	public static List<String> getProduct() {
		List<String> productNames = new ArrayList<String>();
		PDMLinkProduct product = null;
		try {
			QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
			qs.appendWhere(new SearchCondition(PDMLinkProduct.class, PDMLinkProduct.CONTAINER, SearchCondition.EQUAL, "Site"));
			QueryResult qr = PersistenceHelper.manager.find(qs);
			if (qr != null) {
				while (qr.hasMoreElements()) {
					product = (PDMLinkProduct) qr.nextElement();
					productNames.add(product.getName());
				}
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return productNames;
	}

	/**
	 * ebom导出
	 * 
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static File ebomExport(WTPart part) {
		List<String> titles = getEbomExportTitles();
		List<EBOMExport> ebomBeanList = new ArrayList<EBOMExport>();
		String path = PropertiesUtil.getTempPath() + File.separator + "EBOM_Export.xls";
		FileOutputStream writeFile = null;
		HSSFWorkbook workbook = null;
		HSSFCellStyle style = null;
		HSSFSheet sheet = null;
		HSSFRow titleRow = null;
		try {
			ebomBeanList.add(getEbomExportBean(part));
			getEBOMBeanList(part, ebomBeanList,1);
			workbook = new HSSFWorkbook();
			style = workbook.createCellStyle(); // 样式对象
			style.setVerticalAlignment(VerticalAlignment.CENTER);
			style.setAlignment(HorizontalAlignment.CENTER);
			sheet = workbook.createSheet("sheet1");
			for (int i = 0; i < ebomBeanList.size() + 1; i++) {
				titleRow = sheet.createRow(i);
				if (i == 0) {
					for (int j = 0; j < titles.size(); j++) {
						titleRow.createCell(j).setCellValue(titles.get(j));
					}
				} else {
					titleRow.createCell(0).setCellValue(ebomBeanList.get(i - 1).getPartNumber());
					titleRow.createCell(1).setCellValue(ebomBeanList.get(i - 1).getPartName());
					titleRow.createCell(2).setCellValue(ebomBeanList.get(i - 1).getContainerName());
					titleRow.createCell(3).setCellValue(ebomBeanList.get(i - 1).getVersion());
					titleRow.createCell(4).setCellValue(ebomBeanList.get(i - 1).getState());
					titleRow.createCell(5).setCellValue(ebomBeanList.get(i - 1).getCreateTime());
					titleRow.createCell(6).setCellValue(ebomBeanList.get(i - 1).getModifyTime());
					titleRow.createCell(7).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("SETMARK")));
					titleRow.createCell(8).setCellValue(ebomBeanList.get(i - 1).getfPartNumber());
					titleRow.createCell(9).setCellValue(ebomBeanList.get(i - 1).getfPartName());
					titleRow.createCell(10).setCellValue(ebomBeanList.get(i - 1).getAmount());
					titleRow.createCell(11).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("changeType")));
					titleRow.createCell(12).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("PLATECSCREWFORM")));
					titleRow.createCell(13).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("REMARK")));
					titleRow.createCell(14).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("BMLX")));
					titleRow.createCell(15).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("BMZT")));
					titleRow.createCell(16).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("STANDARDNUMBER")));
					titleRow.createCell(17).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("SURFACETREATMENT")));
					titleRow.createCell(18).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("CMAT")));
					titleRow.createCell(19).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("NUMBER")));
					titleRow.createCell(20).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("CLDW")));
					titleRow.createCell(21).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("MATTYPE")));
					titleRow.createCell(22).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("PTC_MATERIAL_NAME")));
					titleRow.createCell(23).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("MATERIAL")));
					titleRow.createCell(24).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("CMAT_UP")));
					titleRow.createCell(25).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("CMAT_DOWN")));
					titleRow.createCell(26).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("PINDEX")));
					titleRow.createCell(27).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("PRODUCTLEVEL")));
					titleRow.createCell(28).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("PRODUCTFORM")));
					titleRow.createCell(29).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("PHASE_CODE")));
					titleRow.createCell(30).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("SIZE")));
					titleRow.createCell(31).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("PACKAGINGFORM")));
					titleRow.createCell(32).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("FZCJ")));
					titleRow.createCell(33).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("EXTRACONDITION")));
					titleRow.createCell(34).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("ROUTING")));
					titleRow.createCell(35).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("PRODUCT_INDEX")));
					titleRow.createCell(36).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("SUPPLIERS")));
					titleRow.createCell(37).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("KEYCOMPONENT")));
					titleRow.createCell(38).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("CSIZE")));
					titleRow.createCell(39).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("MECHANICALPROPERTYORHARDNESS")));
					titleRow.createCell(40).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("JBCLMC")));
					titleRow.createCell(41).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("MEASUREUNIT")));
					titleRow.createCell(42).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("JSTJ")));
					titleRow.createCell(43).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("JSTJBZH")));
					titleRow.createCell(44).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("JDDJ")));
					titleRow.createCell(45).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("CTYPE")));
					titleRow.createCell(46).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("MTYPE")));
					titleRow.createCell(47).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("SECRET")));
					titleRow.createCell(48).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("name")));//无
					titleRow.createCell(49).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("BATCH")));
					titleRow.createCell(50).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("PZGGBZH")));
					titleRow.createCell(51).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("HEATTREATMENT")));
					titleRow.createCell(52).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("COMPANY")));
					titleRow.createCell(53).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("DESIGNER")));
					titleRow.createCell(54).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("ISIMPORT")));
					titleRow.createCell(55).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("SCOPE")));
					titleRow.createCell(56).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("ENDITEMIN")));
					titleRow.createCell(57).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("MINDEX")));
					titleRow.createCell(58).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("SPECIALINSTRUCTION")));
					titleRow.createCell(59).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("CINDEX")));
					titleRow.createCell(60).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("OUTLINESIZE")));
					titleRow.createCell(61).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("SHORTNAME")));
					titleRow.createCell(62).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("DETAILSTANDARD")));
					titleRow.createCell(63).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("TYPE")));
					titleRow.createCell(64).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("TYPESTANDARD")));
					titleRow.createCell(65).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("XHPH")));
					titleRow.createCell(66).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("ZQCLBZH")));
					titleRow.createCell(67).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("ZQCLMC")));
					titleRow.createCell(68).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("QUALITYLEVEL")));
					titleRow.createCell(69).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("ZLDJ")));
					titleRow.createCell(70).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("PTC_COMMON_NAME")));
					titleRow.createCell(71).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("ZZCJ")));
					titleRow.createCell(72).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("SPECIALCONDITION")));
					titleRow.createCell(73).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("TOTALSTANDARD")));
					titleRow.createCell(74).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("ClassificationNode")));
					titleRow.createCell(75).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("USESTANDARD")));
					titleRow.createCell(76).setCellValue(objToString(ebomBeanList.get(i - 1).getIbaMap().get("MARKNUMBER")));
				}
			}
			writeFile = new FileOutputStream(path);
			workbook.write(writeFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} finally {
			if (writeFile != null) {
				try {
					writeFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return new File(path);
	}

	public static String objToString(Object obj) {
		if (obj == null) {
			return "";
		} else {
			return obj.toString();
		}
	}

	private static List<String> getEbomExportTitles() {
		List<String> titles = new ArrayList<String>();
		titles.add("零部件编号");
		titles.add("零部件名称");
		titles.add("所在产品库");
		titles.add("版本");
		titles.add("生命周期状态");
		titles.add("创建时间");
		titles.add("上次修改时间");
		titles.add("成套件标识");
		titles.add("父件编号");
		titles.add("父件名称");
		titles.add("装配数量");
		titles.add("八部更改标识");
		titles.add("板拧形式");
		titles.add("备注");
		titles.add("编码类型");
		titles.add("编码状态");
		titles.add("标准号");
		titles.add("表面处理");
		titles.add("材料");
		titles.add("材料编号");
		titles.add("材料单位");
		titles.add("材料类型");
		titles.add("材料名称");
		titles.add("材料牌号");
		titles.add("材料上标");
		titles.add("材料下标");
		titles.add("产品代号");
		titles.add("产品等级");
		titles.add("产品型式");
		titles.add("当前阶段");
		titles.add("二维工程图图幅");
		titles.add("封装形式");
		titles.add("辅制车间");
		titles.add("附加协议");
		titles.add("工艺路线");
		titles.add("工装代号");
		titles.add("供应商");
		titles.add("关重件标识");
		titles.add("规格");
		titles.add("机械性能等级或硬度");
		titles.add("基体材料名称");
		titles.add("计量单位");
		titles.add("技术条件");
		titles.add("技术条件标准号");
		titles.add("精度等级");
		titles.add("零部件分类");
		titles.add("零组件生产类型");
		titles.add("密级");
		titles.add("名称");
		titles.add("批次");
		titles.add("品种规格标准号");
		titles.add("热处理");
		titles.add("设计单位");
		titles.add("设计者");
		titles.add("是否进口");
		titles.add("适用范围");
		titles.add("所属成品");
		titles.add("所属型号");
		titles.add("特殊说明");
		titles.add("图号");
		titles.add("外形尺寸");
		titles.add("物资简称");
		titles.add("详细规范");
		titles.add("型号");
		titles.add("型号规格");
		titles.add("型号牌号");
		titles.add("增强材料标准号");
		titles.add("增强材料名称");
		titles.add("质量等级");
		titles.add("质量等级（旧）");
		titles.add("中文名称");
		titles.add("主制车间");
		titles.add("专用条件");
		titles.add("总规范");
		titles.add("分类");
		titles.add("采用标准");
		titles.add("牌号");
		return titles;
	}

	public static void getEBOMBeanList(WTPart part, List<EBOMExport> ebomBeanList,double i) {
		WTPart childPart;
		try {
			if (part != null) {
				QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(part);
				while (qr.hasMoreElements()) {
					WTPartUsageLink link = (WTPartUsageLink) qr.nextElement();
					WTPartMaster master = link.getUses();
					childPart = BomUtil.getLatestPartByView(master, "Design");
					EBOMExport ebomExport = getEbomExportBean(childPart);
					ebomExport.setfPartNumber(part.getNumber());
					ebomExport.setfPartName(part.getName());
					double aMount = link.getQuantity().getAmount()*i;
					ebomExport.setAmount(aMount);
					ebomBeanList.add(ebomExport);
					getEBOMBeanList(childPart, ebomBeanList,aMount);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	public static EBOMExport getEbomExportBean(WTPart part) throws WTException {
		EBOMExport ebomExport = null;
		if (part != null) {
			ebomExport = new EBOMExport();
			String partNumber = part.getNumber();
			String partName = part.getName();
			String containerName = part.getContainerName();
			String state = part.getState().getState().getDisplay(Locale.CHINA);
			String version = part.getIterationDisplayIdentifier().toString();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String createTime = sdf.format(part.getCreateTimestamp());
			String modifyTime = sdf.format(part.getModifyTimestamp());
			Hashtable table = IBAHelper.getAllIBAValues(part);
			ebomExport.setPartNumber(partNumber);
			ebomExport.setPartName(partName);
			ebomExport.setContainerName(containerName);
			ebomExport.setState(state);
			ebomExport.setVersion(version);
			ebomExport.setCreateTime(createTime);
			ebomExport.setModifyTime(modifyTime);
			ebomExport.setAmount(1);
			ebomExport.setIbaMap(table);
		}
		return ebomExport;
	}

	//发次BOM
	public static File faciBomExport(WTPart part) {
		List<EBOMExport> faciBomBeanList = new ArrayList<EBOMExport>();
		FileInputStream is = null;
		FileOutputStream writeFile = null;
		XSSFWorkbook workbook = null;
		XSSFCellStyle style = null;
		XSSFSheet sheet = null;
		XSSFRow row = null;
		File xlsFile = null;
		try {
			String path = PropertiesUtil.getLocalCodeBase() + File.separator + "ext" + File.separator + "casc" + File.separator + "part" + File.separator + "template" + File.separator + "facibom.xlsx";
			File file = new File(path);
			String tPath = PropertiesUtil.getTempPath() + File.separator + UUID.randomUUID() + "_发次BOM导出.xlsx";
			FileUtils.copyFile(file, new File(tPath));
			xlsFile = new File(tPath);
			if(xlsFile == null) {
				return null;
			}
			is = new FileInputStream(file);
			faciBomBeanList.add(getEbomExportBean(part));
			getFaciBOMBeanList(part, faciBomBeanList, 1);

			workbook = new XSSFWorkbook(is);
			style = workbook.createCellStyle(); // 样式对象
			style.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直
			style.setAlignment(HorizontalAlignment.CENTER);// 水平
			sheet = workbook.getSheetAt(0);

			row = sheet.createRow(0);
			writeCellValue(row, 0, IBAHelper.getIBAStringValue(part, "SECRET"),style);
			row = sheet.createRow(2);
			writeCellValue(row, 0, part.getNumber(), style);
			writeCellValue(row, 3, part.getName(), style);
			writeCellValue(row, 6, "导出时间", style);
			writeCellValue(row, 12, DateUtil.getTodayDate("yyyy-MM-dd HH:mm:ss"), style);

			int count = 4;
			for(int i = 0; i < faciBomBeanList.size(); i++) {
				EBOMExport export = faciBomBeanList.get(i);
				row = sheet.createRow(count++);
				writeCellValue(row, 0, String.valueOf(i + 1), style);//序号
				writeCellValue(row, 1, export.getPartNumber(), style);//编号
				writeCellValue(row, 2, objToString(export.getIbaMap().get("PINDEX")), style);//产品代号
				writeCellValue(row, 3, export.getPartName(), style);//名称
				writeCellValue(row, 4, objToString(export.getIbaMap().get("CTYPE")), style);//类别
				if("分系统".equals(export.getIbaMap().get("CTYPE"))) {
					writeCellValue(row, 5, objToString("全箭"), style);//所属系统 分系统写死全箭
				} else {
					writeCellValue(row, 5, objToString(export.getfPartNumber()), style);//所属系统
				}
				writeCellValue(row, 6, objToString(export.getIbaMap().get("FIRSTSTAGENUM")), style);//一子级主份数量
				writeCellValue(row, 7, objToString(export.getIbaMap().get("SECONDSTAGENUM")), style);//二子级主份数量
				writeCellValue(row, 8, objToString(export.getIbaMap().get("PAYLOADFAIRINGSNUM")), style);//星罩主份数量
				writeCellValue(row, 9, objToString(export.getAmount()), style);//单机数量（主件数量）
				writeCellValue(row, 10, objToString(export.getIbaMap().get("SPARECOUNT")), style);//备件数量
				writeCellValue(row, 11, objToString(export.getIbaMap().get("MINDEX")), style);//所属型号
				writeCellValue(row, 12, objToString(export.getIbaMap().get("PHASE_CODE")), style);//研制阶段
				writeCellValue(row, 13, objToString(export.getIbaMap().get("MATCHUNIT")), style);//配套单位
				writeCellValue(row, 14, objToString(export.getIbaMap().get("COMPANY")), style);//设计单位
				writeCellValue(row, 15, objToString(export.getIbaMap().get("FAWANGDANWEI")), style);//生产单位
				writeCellValue(row, 16, objToString(export.getIbaMap().get("ACCEPTANCETYPE")), style);//验收类型
				writeCellValue(row, 17, objToString(export.getIbaMap().get("REMARK")), style);//备注
			}
			writeFile = new FileOutputStream(xlsFile);
			workbook.write(writeFile);
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		} catch(WTException e) {
			e.printStackTrace();
		} finally {
			if(writeFile != null) {
				try {
					writeFile.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
		return xlsFile;
	}


	private static List<String> getFaciExportTitles() {
		List<String> titles = new ArrayList<String>();
		titles.add("零部件编号");
		titles.add("零部件名称");
		titles.add("所在产品库");
		titles.add("版本");
		titles.add("生命周期状态");
		titles.add("创建时间");
		titles.add("上次修改时间");

		titles.add("父件编号");
		titles.add("父件名称");
		titles.add("装配数量");

		titles.add("产品代号");
		titles.add("状态简号");
		titles.add("密级");
		titles.add("当前阶段");

		titles.add("零部件类别");
		titles.add("系统号");
		titles.add("编制部门");

		titles.add("是否归档");
		titles.add("发射时间");
		titles.add("发射状态");

		titles.add("型号代号");
		titles.add("配套单位");
		titles.add("设计单位");
		titles.add("生产单位");
		titles.add("验收类型");
		titles.add("一子级主份数量");
		titles.add("二子级主份数量");
		titles.add("星罩主份数量");
		titles.add("备件数量");
		titles.add("备注");
		return titles;
	}

	public static void getFaciBOMBeanList(WTPart part, List<EBOMExport> faciBomBeanList,double i) {
		WTPart childPart;
		try {
			if (part != null) {
				QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(part);
				while (qr.hasMoreElements()) {
					WTPartUsageLink link = (WTPartUsageLink) qr.nextElement();
					WTPartMaster master = link.getUses();
					childPart = BomUtil.getLatestPartByView(master, "Design");


					EBOMExport ebomExport = getEbomExportBean(childPart);
					ebomExport.setfPartNumber(part.getNumber());
					ebomExport.setfPartName(part.getName());
					double aMount = link.getQuantity().getAmount()*i;
					ebomExport.setAmount(aMount);
					faciBomBeanList.add(ebomExport);
					getFaciBOMBeanList(childPart, faciBomBeanList,aMount);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	public static void writeCellValue(Row row, int col, String value, XSSFCellStyle style) {
		Cell cell = row.createCell(col, CellType.STRING);
		cell.setCellStyle(style);
		cell.setCellValue(new XSSFRichTextString(value));
	}


}
