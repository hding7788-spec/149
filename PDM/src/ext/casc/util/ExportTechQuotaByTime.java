package ext.casc.util;

import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.SWXMLUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import com.ptc.core.meta.common.TypeIdentifier;
import ext.casc.integrate.util.ZipUtil;
import ext.casc.nc.NCMaterialHelper;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jdom.Element;
import wt.content.ApplicationData;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.log4j.LogR;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.pds.StatementSpec;
import wt.query.*;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.util.WTProperties;
import wt.vc.wip.WorkInProgressHelper;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: SAST-149-PDM
 * @description: 按产品导出工艺定额
 * @author: MChen
 * @create: 2020-12-22 10:07
 */
public class ExportTechQuotaByTime implements RemoteAccess {
    private static Logger logger = LogR.getLogger(ExportTechQuotaByTime.class.getName());

	public static void main(String[] args) {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		String username = null;
		String passwd = null;
		String startTime = null;
		String endTime = null;

		if (args.length >= 2) {
			username = args[0];
			passwd = args[1];
			startTime = args[2];
			endTime = args[3];

			if (username == null)
				username = "wcadmin";

			if (passwd == null)
				passwd = "Admin@149.941";
		}
		System.out.println("------user:" + username + "    password:" + passwd);
		String method = "exportExcel";
		Class[] types = { String.class, String.class };
		Object[] vals = { startTime, endTime };

		rms.setUserName(username);
		rms.setPassword(passwd);
		try {
			rms.invoke(method, ExportTechQuotaByTime.class.getName(), null, types, vals);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// exportExcel(startTime,endTime);

	}

	/**
	 * 导出excel
	 */
	public static void exportExcel(String startTime, String endTime) {

		if (!RemoteMethodServer.ServerFlag) {
			String method = "exportExcel";
			Class[] types = { String.class, String.class };
			Object[] vals = { startTime, endTime };

			RemoteMethodServer rms = RemoteMethodServer.getDefault();
			try {
				rms.invoke(method, ExportTechQuotaByTime.class.getName(), null, types, vals);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			InputStream inputStream = null;
			SWXMLUtil xmlUtil = null;
			try {

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Timestamp date1 = new Timestamp(dateFormat.parse(startTime).getTime());
				Timestamp date2 = new Timestamp(dateFormat.parse(endTime).getTime());

				WTProperties pro = WTProperties.getLocalProperties();
				String tec_temp_dir = pro.getProperty("wt.temp");
				ArrayList<TypeIdentifier> list = SoftTypeUtil.getChildTypes("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN", null);
				String containName = startTime + "~" + endTime;
				// 创建Excel文件薄
				XSSFWorkbook workbook = new XSSFWorkbook();
				// 创建工作表sheeet
				Sheet sheet = workbook.createSheet(containName);
				// 创建第一行
				Row row = sheet.createRow(0);
				String[] title = { "工艺文件OID", "工艺编号", "工艺文件编号", "工艺名称", "版本", "创建者", "部门", "工艺类型", "工艺类别", "存货编码(wzbm)", "存货名称(wzmc)", "型号牌号(xhph)", "规格(gg)", "技术条件(jstj)", "生产厂家(sccj)",
						"主计量单位(zjldw)", "附加条件(fjtj)", "供应状态/热处理(gyztrcl)", "质量等级(zldj)", "单位(dw)", "封装形式(fzxs)", "精度等级(jddj)", "螺纹规格/公称尺寸(lwgg)", "机械性能等级(jxxndj)", "电参考特选要求(dcstxyq)", "备注(comment)" };
				Cell cell = null;
				for (int i = 0; i < title.length; i++) {
					cell = row.createCell(i);
					cell.setCellValue(title[i]);
				}
				int count = 1;
				containName = new String(containName.getBytes("GBK"), System.getProperty("sun.jnu.encoding"));
				long[] types = new long[list.size()];
				for (int i = 0; i < list.size(); i++) {
					String type = list.get(i).toString().substring(7);
					TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference(type);
					long typeId = 0;
					if (tdr != null) {
						typeId = tdr.getKey().getBranchId();
					}
					types[i] = typeId;
				}

				QuerySpec qs = new QuerySpec(WTDocument.class);

				qs.appendWhere(new SearchCondition(new ClassAttribute(WTDocument.class, "typeDefinitionReference.key.branchId"), SearchCondition.IN, new ArrayExpression(types)), new int[] { 0 });
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.LATEST_ITERATION, SearchCondition.IS_TRUE), new int[] { 0 });
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.LIFE_CYCLE_STATE, SearchCondition.EQUAL, "APPROVED", true), new int[] { 0 });
				qs.appendAnd();
				Object rangeExpress = new RangeExpression(new AttributeRange(date1, date2));
				qs.appendWhere(new SearchCondition(new ClassAttribute(WTDocument.class, WTDocument.MODIFY_TIMESTAMP), SearchCondition.BETWEEN, ((RelationalExpression) (rangeExpress))),
						new int[] { 0 });

				QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
				while (qr.hasMoreElements()) {
					WTDocument document = (WTDocument) qr.nextElement();
					boolean checkedOut = WorkInProgressHelper.isCheckedOut(document);
					if (!checkedOut) {
						try {
							String tecNumber = document.getNumber();
							logger.info(tecNumber);
							// 定义字段
							String techOid = document.getPersistInfo().getObjectIdentifier().getStringValue();
							String technicsNum = "";
							String techName = "";
							String version = "";
							String creator = "";
							String dept = "";
							String techLx = "";
							String techLb = "";
							ApplicationData data = WTDocumentUtil.getPrimaryByDocument(document);
							if (data == null) {
								continue;
							}
							String tempPath = java.util.UUID.randomUUID().toString();
							String tecFilePath = tec_temp_dir + File.separator + tempPath + File.separator + tecNumber;
							byte[] bytes = WTDocumentUtil.applicationDataToByte(data);
							ZipUtil.unZip(bytes, tecFilePath);
							String xmlPath = tec_temp_dir + File.separator + tempPath + File.separator + tecNumber + File.separator + tecNumber + ".xml";
							File xmlFile = new File(xmlPath);
							if (xmlFile.exists()) {
								inputStream = new FileInputStream(xmlFile);
								xmlUtil = new SWXMLUtil(inputStream);
								Element rootElement = xmlUtil.getRootElement();
								List<Element> qmFawTechnicsInfo = rootElement.getChildren("QMFawTechnicsInfo");
								if (qmFawTechnicsInfo != null && qmFawTechnicsInfo.size() > 0) {
									Element o = qmFawTechnicsInfo.get(0);
									creator = NCMaterialHelper.getStringValue(o.getAttributeValue("creatorDisplay"));
									technicsNum = NCMaterialHelper.getStringValue(o.getAttributeValue("pplanNumber"));
									techName = NCMaterialHelper.getStringValue(o.getAttributeValue("technicsName"));
									version = NCMaterialHelper.getStringValue(o.getAttributeValue("version"));
									dept = NCMaterialHelper.getStringValue(o.getAttributeValue("DEPT"));
									techLx = NCMaterialHelper.getStringValue(o.getAttributeValue("technicsType"));
									techLb = NCMaterialHelper.getStringValue(o.getAttributeValue("PPLANTYPE"));
								}
								for (Element ele : (List<Element>) rootElement.getChildren("QMFawTechnicsInfo")) {
									for (Element cldeEle : (List<Element>) ele.getChildren("CLDE")) {
										// 原材料定额
										for (Element ycldeEle : (List<Element>) cldeEle.getChildren("YCLDE")) {
											for (Element ycldeRecordEle : (List<Element>) ycldeEle.getChildren("ycldeRecord")) {
												String wzbm = NCMaterialHelper.getStringValue(ycldeRecordEle.getAttributeValue("chbm"));
												String wzmc = NCMaterialHelper.getStringValue(ycldeRecordEle.getAttributeValue("chmc"));
												String xhph = NCMaterialHelper.getStringValue(ycldeRecordEle.getAttributeValue("xhph"));
												String gg = NCMaterialHelper.getStringValue(ycldeRecordEle.getAttributeValue("gg"));
												String jstj = NCMaterialHelper.getStringValue(ycldeRecordEle.getAttributeValue("jstj"));
												String sccj = NCMaterialHelper.getStringValue(ycldeRecordEle.getAttributeValue("sccj"));
												String zjldw = NCMaterialHelper.getStringValue(ycldeRecordEle.getAttributeValue("zjldw"));
												String fjtj = NCMaterialHelper.getStringValue(ycldeRecordEle.getAttributeValue("fjtj"));
												String gyztrcl = NCMaterialHelper.getStringValue(ycldeRecordEle.getAttributeValue("gyztrcl"));
												String zldj = NCMaterialHelper.getStringValue(ycldeRecordEle.getAttributeValue("zldj"));
												String dw = NCMaterialHelper.getStringValue(ycldeRecordEle.getAttributeValue("dw"));
												String fzxs = NCMaterialHelper.getStringValue(ycldeRecordEle.getAttributeValue("fzxs"));
												String jddj = NCMaterialHelper.getStringValue(ycldeRecordEle.getAttributeValue("jddj"));
												String lwgg = NCMaterialHelper.getStringValue(ycldeRecordEle.getAttributeValue("lwgg"));
												String jxxndj = NCMaterialHelper.getStringValue(ycldeRecordEle.getAttributeValue("jxxndj"));
												String dcstxyq = NCMaterialHelper.getStringValue(ycldeRecordEle.getAttributeValue("dcstxyq"));
												String comment = NCMaterialHelper.getStringValue(ycldeRecordEle.getAttributeValue("comment"));
												String[] rowDatas = new String[] { techOid, tecNumber, technicsNum, techName, version, creator, dept, techLx, techLb, wzbm, wzmc, xhph, gg, jstj, sccj,
														zjldw, fjtj, gyztrcl, zldj, dw, fzxs, jddj, lwgg, jxxndj, dcstxyq, comment };
												Row nextrow = sheet.createRow(count);
												for (int i = 0; i < title.length; i++) {
													cell = nextrow.createCell(i);
													cell.setCellValue(rowDatas[i]);
												}
												count++;
											}
										}
										// 原材料定额
										for (Element zycldeEle : (List<Element>) cldeEle.getChildren("ZYCLDE")) {
											for (Element zycldeRecordEle : (List<Element>) zycldeEle.getChildren("zycldeRecord")) {
												String wzbm = NCMaterialHelper.getStringValue(zycldeRecordEle.getAttributeValue("chbm"));
												String wzmc = NCMaterialHelper.getStringValue(zycldeRecordEle.getAttributeValue("chmc"));
												String xhph = NCMaterialHelper.getStringValue(zycldeRecordEle.getAttributeValue("xhph"));
												String gg = NCMaterialHelper.getStringValue(zycldeRecordEle.getAttributeValue("gg"));
												String jstj = NCMaterialHelper.getStringValue(zycldeRecordEle.getAttributeValue("jstj"));
												String sccj = NCMaterialHelper.getStringValue(zycldeRecordEle.getAttributeValue("sccj"));
												String zjldw = NCMaterialHelper.getStringValue(zycldeRecordEle.getAttributeValue("zjldw"));
												String fjtj = NCMaterialHelper.getStringValue(zycldeRecordEle.getAttributeValue("fjtj"));
												String gyztrcl = NCMaterialHelper.getStringValue(zycldeRecordEle.getAttributeValue("gyztrcl"));
												String zldj = NCMaterialHelper.getStringValue(zycldeRecordEle.getAttributeValue("zldj"));
												String dw = NCMaterialHelper.getStringValue(zycldeRecordEle.getAttributeValue("dw"));
												String fzxs = NCMaterialHelper.getStringValue(zycldeRecordEle.getAttributeValue("fzxs"));
												String jddj = NCMaterialHelper.getStringValue(zycldeRecordEle.getAttributeValue("jddj"));
												String lwgg = NCMaterialHelper.getStringValue(zycldeRecordEle.getAttributeValue("lwgg"));
												String jxxndj = NCMaterialHelper.getStringValue(zycldeRecordEle.getAttributeValue("jxxndj"));
												String dcstxyq = NCMaterialHelper.getStringValue(zycldeRecordEle.getAttributeValue("dcstxyq"));
												String comment = NCMaterialHelper.getStringValue(zycldeRecordEle.getAttributeValue("comment"));
												String[] rowDatas = new String[] { techOid, tecNumber, technicsNum, techName, version, creator, dept, techLx, techLb, wzbm, wzmc, xhph, gg, jstj, sccj,
														zjldw, fjtj, gyztrcl, zldj, dw, fzxs, jddj, lwgg, jxxndj, dcstxyq, comment };
												Row nextrow = sheet.createRow(count);
												for (int i = 0; i < title.length; i++) {
													cell = nextrow.createCell(i);
													cell.setCellValue(rowDatas[i]);
												}
												count++;
											}
										}
										// 试件原材料
										for (Element sjycldeEle : (List<Element>) cldeEle.getChildren("SJYCLDE")) {
											for (Element sjycldeRecordEle : (List<Element>) sjycldeEle.getChildren("sjycldeRecord")) {
												String wzbm = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("chbm"));
												String wzmc = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("chmc"));
												String xhph = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("xhph"));
												String gg = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("gg"));
												String jstj = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("jstj"));
												String sccj = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("sccj"));
												String zjldw = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("zjldw"));
												String fjtj = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("fjtj"));
												String gyztrcl = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("gyztrcl"));
												String zldj = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("zldj"));
												String dw = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("dw"));
												String fzxs = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("fzxs"));
												String jddj = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("jddj"));
												String lwgg = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("lwgg"));
												String jxxndj = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("jxxndj"));
												String dcstxyq = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("dcstxyq"));
												String comment = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("comment"));
												String[] rowDatas = new String[] { techOid, tecNumber, technicsNum, techName, version, creator, dept, techLx, techLb, wzbm, wzmc, xhph, gg, jstj, sccj,
														zjldw, fjtj, gyztrcl, zldj, dw, fzxs, jddj, lwgg, jxxndj, dcstxyq, comment };
												Row nextrow = sheet.createRow(count);
												for (int i = 0; i < title.length; i++) {
													cell = nextrow.createCell(i);
													cell.setCellValue(rowDatas[i]);
												}
												count++;
											}
										}
									}

									// 工艺定额
									for (Element gydeEle : (List<Element>) ele.getChildren("GYDE")) {
										// 主要材料定额
										for (Element zycldeEle : (List<Element>) gydeEle.getChildren("ZYCLDE")) {
											for (Element zyclderecordEle : (List<Element>) zycldeEle.getChildren("zycldeRecord")) {
												String wzbm = NCMaterialHelper.getStringValue(zyclderecordEle.getAttributeValue("chbm"));
												String wzmc = NCMaterialHelper.getStringValue(zyclderecordEle.getAttributeValue("chmc"));
												String xhph = NCMaterialHelper.getStringValue(zyclderecordEle.getAttributeValue("xhph"));
												String gg = NCMaterialHelper.getStringValue(zyclderecordEle.getAttributeValue("gg"));
												String jstj = NCMaterialHelper.getStringValue(zyclderecordEle.getAttributeValue("jstj"));
												String sccj = NCMaterialHelper.getStringValue(zyclderecordEle.getAttributeValue("sccj"));
												String zjldw = NCMaterialHelper.getStringValue(zyclderecordEle.getAttributeValue("zjldw"));
												String fjtj = NCMaterialHelper.getStringValue(zyclderecordEle.getAttributeValue("fjtj"));
												String gyztrcl = NCMaterialHelper.getStringValue(zyclderecordEle.getAttributeValue("gyztrcl"));
												String zldj = NCMaterialHelper.getStringValue(zyclderecordEle.getAttributeValue("zldj"));
												String dw = NCMaterialHelper.getStringValue(zyclderecordEle.getAttributeValue("dw"));
												String fzxs = NCMaterialHelper.getStringValue(zyclderecordEle.getAttributeValue("fzxs"));
												String jddj = NCMaterialHelper.getStringValue(zyclderecordEle.getAttributeValue("jddj"));
												String lwgg = NCMaterialHelper.getStringValue(zyclderecordEle.getAttributeValue("lwgg"));
												String jxxndj = NCMaterialHelper.getStringValue(zyclderecordEle.getAttributeValue("jxxndj"));
												String dcstxyq = NCMaterialHelper.getStringValue(zyclderecordEle.getAttributeValue("dcstxyq"));
												String comment = NCMaterialHelper.getStringValue(zyclderecordEle.getAttributeValue("comment"));
												String[] rowDatas = new String[] { techOid, tecNumber, technicsNum, techName, version, creator, dept, techLx, techLb, wzbm, wzmc, xhph, gg, jstj, sccj,
														zjldw, fjtj, gyztrcl, zldj, dw, fzxs, jddj, lwgg, jxxndj, dcstxyq, comment };
												Row nextrow = sheet.createRow(count);
												for (int i = 0; i < title.length; i++) {
													cell = nextrow.createCell(i);
													cell.setCellValue(rowDatas[i]);
												}
												count++;
											}
										}
										// 匹配
										for (Element matchpartEle : (List<Element>) gydeEle.getChildren("MATCHPART")) {
											for (Element matchpartRecordEle : (List<Element>) matchpartEle.getChildren("MatchPart")) {
												String wzbm = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("chbm"));
												String wzmc = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("chmc"));
												String xhph = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("xhph"));
												String gg = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("gg"));
												String jstj = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("jstj"));
												String sccj = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("sccj"));
												String zjldw = "";
												String fjtj = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("fjtj"));
												String gyztrcl = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("gyztrcl"));
												String zldj = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("zldj"));
												String dw = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("dw"));
												String fzxs = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("fzxs"));
												String jddj = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("jddj"));
												String lwgggccc = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("lwgggccc"));
												String jxxndj = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("jxxndj"));
												String dcstxyq = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("dcstxyq"));
												String comment = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("comment"));
												String[] rowDatas = new String[] { techOid, tecNumber, technicsNum, techName, version, creator, dept, techLx, techLb, wzbm, wzmc, xhph, gg, jstj, sccj,
														zjldw, fjtj, gyztrcl, zldj, dw, fzxs, jddj, lwgggccc, jxxndj, dcstxyq, comment };
												Row nextrow = sheet.createRow(count);
												for (int i = 0; i < title.length; i++) {
													cell = nextrow.createCell(i);
													cell.setCellValue(rowDatas[i]);
												}
												count++;
											}
										}
										// 新增
										for (Element newpartEle : (List<Element>) gydeEle.getChildren("NEWPART")) {
											for (Element newpartRecordEle : (List<Element>) newpartEle.getChildren("NewPart")) {
												String wzbm = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("chbm"));
												String wzmc = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("chmc"));
												String xhph = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("xhph"));
												String gg = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("gg"));
												String jstj = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("jstj"));
												String sccj = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("sccj"));
												String zjldw = "";
												String fjtj = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("fjtj"));
												String gyztrcl = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("gyztrcl"));
												String zldj = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("zldj"));
												String dw = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("dw"));
												String fzxs = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("fzxs"));
												String jddj = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("jddj"));
												String lwgggccc = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("lwgggccc"));
												String jxxndj = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("jxxndj"));
												String dcstxyq = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("dcstxyq"));
												String comment = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("comment"));
												String[] rowDatas = new String[] { techOid, tecNumber, technicsNum, techName, version, creator, dept, techLx, techLb, wzbm, wzmc, xhph, gg, jstj, sccj,
														zjldw, fjtj, gyztrcl, zldj, dw, fzxs, jddj, lwgggccc, jxxndj, dcstxyq, comment };
												Row nextrow = sheet.createRow(count);
												for (int i = 0; i < title.length; i++) {
													cell = nextrow.createCell(i);
													cell.setCellValue(rowDatas[i]);
												}
												count++;
											}
										}

										// 试件原材料
										for (Element sjycldeEle : (List<Element>) gydeEle.getChildren("SJYCLDE")) {
											for (Element sjycldeRecordEle : (List<Element>) sjycldeEle.getChildren("sjycldeRecord")) {
												String wzbm = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("chbm"));
												String wzmc = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("chmc"));
												String xhph = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("xhph"));
												String gg = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("gg"));
												String jstj = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("jstj"));
												String sccj = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("sccj"));
												String zjldw = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("zjldw"));
												String fjtj = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("fjtj"));
												String gyztrcl = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("gyztrcl"));
												String zldj = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("zldj"));
												String dw = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("dw"));
												String fzxs = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("fzxs"));
												String jddj = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("jddj"));
												String lwgg = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("lwgg"));
												String jxxndj = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("jxxndj"));
												String dcstxyq = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("dcstxyq"));
												String comment = NCMaterialHelper.getStringValue(sjycldeRecordEle.getAttributeValue("comment"));
												String[] rowDatas = new String[] { techOid, tecNumber, technicsNum, techName, version, creator, dept, techLx, techLb, wzbm, wzmc, xhph, gg, jstj, sccj,
														zjldw, fjtj, gyztrcl, zldj, dw, fzxs, jddj, lwgg, jxxndj, dcstxyq, comment };
												Row nextrow = sheet.createRow(count);
												for (int i = 0; i < title.length; i++) {
													cell = nextrow.createCell(i);
													cell.setCellValue(rowDatas[i]);
												}
												count++;
											}
										}

										// 资源库匹配
										for (Element matchpartEle : (List<Element>) gydeEle.getChildren("SJZYKMATCHPART")) {
											for (Element matchpartRecordEle : (List<Element>) matchpartEle.getChildren("SjzykMatchPart")) {
												String wzbm = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("sjbm"));
												String wzmc = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("name"));
												String xhph = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("xhph"));
												String gg = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("gg"));
												String jstj = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("jstj"));
												String sccj = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("sccj"));
												String zjldw = "";
												String fjtj = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("fjtj"));
												String gyztrcl = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("gyztrcl"));
												String zldj = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("zldj"));
												String dw = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("dw"));
												String fzxs = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("fzxs"));
												String jddj = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("jddj"));
												String lwgggccc = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("lwgggccc"));
												String jxxndj = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("jxxndj"));
												String dcstxyq = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("dcstxyq"));
												String comment = NCMaterialHelper.getStringValue(matchpartRecordEle.getAttributeValue("comment"));
												String[] rowDatas = new String[] { techOid, tecNumber, technicsNum, techName, version, creator, dept, techLx, techLb, wzbm, wzmc, xhph, gg, jstj, sccj,
														zjldw, fjtj, gyztrcl, zldj, dw, fzxs, jddj, lwgggccc, jxxndj, dcstxyq, comment };
												Row nextrow = sheet.createRow(count);
												for (int i = 0; i < title.length; i++) {
													cell = nextrow.createCell(i);
													cell.setCellValue(rowDatas[i]);
												}
												count++;
											}
										}
										// 资源库新增
										for (Element newpartEle : (List<Element>) gydeEle.getChildren("SJZYKNEWPART")) {
											for (Element newpartRecordEle : (List<Element>) newpartEle.getChildren("SjzykNewPart")) {
												String wzbm = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("sjbm"));
												String wzmc = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("name"));
												String xhph = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("ph"));
												String gg = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("gg"));
												String jstj = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("bzh"));
												String sccj = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("gys"));
												String zjldw = "";
												String fjtj = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("fjtj"));
												String gyztrcl = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("rcl"));
												String zldj = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("zldj"));
												String dw = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("dw"));
												String fzxs = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("fzxs"));
												String jddj = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("jddj"));
												String lwgggccc = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("lwgggccc"));
												String jxxndj = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("jxxndj"));
												String dcstxyq = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("dcstxyq"));
												String comment = NCMaterialHelper.getStringValue(newpartRecordEle.getAttributeValue("comment"));
												String[] rowDatas = new String[] { techOid, tecNumber, technicsNum, techName, version, creator, dept, techLx, techLb, wzbm, wzmc, xhph, gg, jstj, sccj,
														zjldw, fjtj, gyztrcl, zldj, dw, fzxs, jddj, lwgggccc, jxxndj, dcstxyq, comment };
												Row nextrow = sheet.createRow(count);
												for (int i = 0; i < title.length; i++) {
													cell = nextrow.createCell(i);
													cell.setCellValue(rowDatas[i]);
												}
												count++;
											}
										}

									}
								}

							}

						} catch (Exception e2) {
							e2.printStackTrace();
						}
					}
				}

				// 创建一个文件
				File file = new File(PropertiesUtil.getWTHome() + File.separator + "exportTechQuota" + File.separator + "exportTechQuota_" + containName + ".xlsx");
				file.createNewFile();
				FileOutputStream stream = FileUtils.openOutputStream(file);
				workbook.write(stream);
				stream.close();

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}

		}
	}

}