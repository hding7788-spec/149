package com.glaway.mpm.tool;

import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.MPMResourceUtil;
import com.ptc.windchill.mpml.resource.*;
import ext.casc.constants.PDMConfig;
import ext.casc.util.IBAUtility;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import wt.fc.*;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainerRef;
import wt.inf.library.WTLibrary;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.*;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.vc.config.ConfigHelper;
import wt.vc.config.ConfigSpec;

import java.io.*;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author lbzhang
 *
 */
public class ImportMPMResourceTool implements RemoteAccess {

	private static final String CLASSNAME = ImportMPMResourceTool.class
			.getName();

	public static String resourceFilePath = "";
	public static String eqpFilePath = "";
	public static String forckFilePath = "";
	public static String gyflFilePath = "";
	public static String materialFilePath = "C:\\临时文件\\工艺资源导入模板\\材料.xls";
	public static String sortmaterialFilePath = "C:\\临时文件\\工艺资源导入模板\\材料分类.xls";
	public static String wt_temp = "";

	static {
		try {
			WTProperties properties = WTProperties.getLocalProperties();
			wt_temp = properties.getProperty("wt.temp");
			resourceFilePath = wt_temp + File.separator + "sczy.xls";//生产资源
			eqpFilePath = wt_temp + File.separator + "sb.xls";//设备
			forckFilePath = wt_temp + File.separator + "gzzy.xls";//工装资源
			gyflFilePath = wt_temp + File.separator + "gyfl.xls";//工艺辅料
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static WTLibrary getLibraryByName(String name) throws WTException {
		WTLibrary library = null;

		QuerySpec qs = new QuerySpec(WTLibrary.class);
		qs.appendWhere(new SearchCondition(WTLibrary.class, WTLibrary.NAME,
				SearchCondition.EQUAL, name), new int[] { 0 });
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		while (qr.hasMoreElements()) {
			library = (WTLibrary) qr.nextElement();
		}
		return library;
	}

	/**
	 *
	 *
	 * @author zhangdong
	 * @date 2013-5-28
	 * @param library
	 * @param strPEqp
	 * @param strEqp
	 * @param eqpNumber
	 * @param strFolder
	 * @param objectType
	 * @param strPlants
	 * @return MPMTooling
	 *
	 */

	private static MPMTooling createEquipment(WTLibrary library,
			MPMTooling parentEqp, String strEqpName, String strFolder,
			String objectType, String strPlants) {

		MPMTooling cs = null;
		try {
			cs = MPMResourceUtil.createTooling("", strEqpName, library,
					strFolder, objectType, "");

			if (parentEqp != null) {
				// 建立父子设备关联关系
				System.out.println("+++start create +++++");
				CreateCommentDataTool_New.createWTPartUsageLink(parentEqp,
						(WTPartMaster) cs.getMaster());
			}
			// 制造单位关联
			if (strPlants != null && strPlants.length() > 0) {
				String plants[] = strPlants.split("\\|");
				for (String strPlant : plants) {
					MPMPlant plant = CreateCommentDataTool_New
							.getPlantByName(strPlant);
					if (plant != null) {
						CreateCommentDataTool_New.createWTPartUsageLink(plant,
								(WTPartMaster) cs.getMaster());
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return cs;
	}

	/**
	 * 设备-车床
	 *
	 * @author zhangdong
	 * @date 2013-5-28
	 * @param excelFile
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws WTException
	 *
	 */
	public static void importEquipment_CheChuang(Workbook workbook)
			throws FileNotFoundException, IOException, WTException {
		int startRow = 1;
		int startColumn = 1;
		String folder = "/Default/设备/车床";
		String objectType = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|casc.sast.149.车床";
		WTLibrary library = getLibraryByName("工艺资源库");
		Sheet sheet = workbook.getSheet("车床");
		int lastRowNum = sheet.getLastRowNum();
		System.out.println(lastRowNum);
		MPMTooling parentEqp = null;
		for (int i = startRow; i <= lastRowNum; i++) {
			Row row = sheet.getRow(i);
			Cell flagCell = row.getCell(0);
			if (flagCell == null) {
				flagCell = row.createCell(0);
			}
			if ((flagCell.getStringCellValue() != null && flagCell
					.getStringCellValue().equalsIgnoreCase("Y"))) {
				System.out.println("跳过 -- " + getValue(row, 1));
				continue;
			}
			// 父设备名称 子设备名称 设备编号 型号 加工范围 刀架移动 中心距 加工精度 主轴 转速 使用部门 数量 厂家 设备类型
			// 文件夹 使用部门
			// 设备名称 型号 设备编号 加工范围 刀架移动 中心距 加工精度 主轴 转速 数量 厂家 使用部门

			// String value1 = row.getCell(1).getStringCellValue();// 设备名称
			// String value2 = row.getCell(2).getStringCellValue();// 型号
			// String value3 = row.getCell(3) == null ? "" :
			// (row.getCell(3).getCellType() == 0 ? new Integer((int)
			// row.getCell(3).getNumericCellValue()).toString() :
			// row.getCell(3).getStringCellValue()); // 设备编号
			// String value4 = row.getCell(4) == null ? "" : (
			// row.getCell(4).getCellType() == 0 ? new Integer((int)
			// row.getCell(4).getNumericCellValue()).toString() :
			// row.getCell(4).getStringCellValue()); // 加工范围
			// String value5 = row.getCell(5) == null ? "" :
			// (row.getCell(5).getCellType() == 0 ? new Integer((int)
			// row.getCell(5).getNumericCellValue()).toString() :
			// row.getCell(5).getStringCellValue()); // 刀架移动
			// String value6 = row.getCell(6) == null ? "" :
			// (row.getCell(6).getCellType() == 0 ? new Integer((int)
			// row.getCell(6).getNumericCellValue()).toString() :
			// row.getCell(6).getStringCellValue()); // 中心距
			// String value7 = row.getCell(7) == null ? "" :
			// (row.getCell(7).getCellType() == 0 ? new Integer((int)
			// row.getCell(7).getNumericCellValue()).toString() :
			// row.getCell(7).getStringCellValue()); // 加工精度
			// String value8 = row.getCell(8) == null ? "" :
			// (row.getCell(8).getCellType() == 0 ? new Integer((int)
			// row.getCell(8).getNumericCellValue()).toString() :
			// row.getCell(8).getStringCellValue());// 主轴
			// String value9 = row.getCell(9) == null ? "" :
			// (row.getCell(9).getCellType() == 0 ? new Integer((int)
			// row.getCell(9).getNumericCellValue()).toString() :
			// row.getCell(9).getStringCellValue()); // 转速
			// String value10 = row.getCell(10) == null ? "" :
			// (row.getCell(10).getCellType() == 0 ? new Integer((int)
			// row.getCell(10).getNumericCellValue()).toString() :
			// row.getCell(10).getStringCellValue()); // 数量
			// String value11 = row.getCell(11).getStringCellValue(); // 厂家
			// String value12 = row.getCell(12).getStringCellValue(); //
			// 设备类型
			// String value13 = row.getCell(13).getStringCellValue(); // 文件夹
			// String value14 = row.getCell(12).getStringCellValue(); // 制造单位
			Date value15 = row.getCell(13).getDateCellValue();// 启动时间
			// String value16 = row.getCell(14).getStringCellValue(); //分类

			String value1 = getValue(row, 1);
			String value2 = getValue(row, 2);
			String value3 = getValue(row, 3);
			String value4 = getValue(row, 4);
			String value5 = getValue(row, 5);
			String value6 = getValue(row, 6);
			String value7 = getValue(row, 7);
			String value8 = getValue(row, 8);
			String value9 = getValue(row, 9);
			String value10 = getValue(row, 10);
			String value11 = getValue(row, 11);
			String value14 = getValue(row, 12);
			String value16 = getValue(row, 14);
			value1 = value1.trim();
			value2 = value2.trim();
			value3 = value3.trim();
			value4 = value4.trim();
			value5 = value5.trim();
			value6 = value6.trim();
			value7 = value7.trim();
			value8 = value8.trim();
			value9 = value9.trim();
			value10 = value10.trim();
			value11 = value11.trim();
			value14 = value14.trim();
			value16 = value16.trim();
			MPMTooling currentEqp = null;
			System.out.println(value1 + "," + value2 + "," + value3 + ",");
			// + value4 + ",  "
			// + value5 + ",  " + value6 + ",  " + value7 + ",  " + value8 +
			// ",  " + value9 + ",  " + value10 + ",  ");

			if (value3 == null || value3.length() == 0 || "".equals(value3)) {
				// 是个父设备
				System.out.println("创建父设备 --- " + value3);
				parentEqp = createEquipment(library, null, value1, folder,
						objectType, value14);
				currentEqp = parentEqp;
			} else {
				// 创建设备
				System.out.println("创建子设备 --- " + value3);
				System.out.println("父设备为 --- " + parentEqp);
				currentEqp = createEquipment(library, parentEqp, value1,
						folder, objectType, value14);
			}
			// number 编号
			// equipmentNumber 设备编号
			// name 设备名称
			// modelNumber 型号
			// equipmentAmount 设备数量
			// equipmentVender 厂家
			// remark 备注 startTime 启用日期 newcategory 设备分类
			// workingScope 加工范围
			// knifeMove 刀架移动
			// centreSpacing 中心距
			// workPrecision 加工精度
			// chiefAxis 主轴
			// speed 转速

			IBAHelper.setIBAStringValue(currentEqp, "equipmentNumber", value3);
			IBAHelper.setIBAStringValue(currentEqp, "modelNumber", value2);
			IBAHelper.setIBAStringValue(currentEqp, "equipmentAmount", value10);
			IBAHelper.setIBAStringValue(currentEqp, "equipmentVender", value11);
			IBAHelper.setIBAStringValue(currentEqp, "remark", "");
			IBAHelper.setIBAStringValue(currentEqp, "workingScope", value4);
			IBAHelper.setIBAStringValue(currentEqp, "knifeMove", value5);
			IBAHelper.setIBAStringValue(currentEqp, "centreSpacing", value6);
			IBAHelper.setIBAStringValue(currentEqp, "workPrecision", value7);
			IBAHelper.setIBAStringValue(currentEqp, "chiefAxis", value8);
			IBAHelper.setIBAStringValue(currentEqp, "speed", value9);
			if (value15 != null) {
				IBAHelper.setIBATimestampValue(currentEqp, "startTime",
						new Timestamp(value15.getTime()));
			}
			IBAHelper.setIBAStringValue(currentEqp, "category", value16);
			// TODO 将属性设置到设备上
			// currentEqp.setAttribute();
			flagCell.setCellValue("Y");
		}
	}

	public static void setMyIBAStringValue(WTObject object,
			String attributeName, String attributeValue) throws WTException {
		if (attributeValue == null || "".equals(attributeValue.trim())
				|| "null".equals(attributeValue.trim())) {
			return;
		} else {
			IBAHelper.setIBAStringValue(object, attributeName, attributeValue);
		}

	}

	/**
	 * 设备-铣床
	 *
	 * @author zhangdong
	 * @date 2013-5-28
	 * @param excelFile
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws WTException
	 *
	 */
	public static void importEquipment_XiChuang(Workbook workbook)
			throws FileNotFoundException, IOException, WTException {
		int startRow = 1;
		String folder = "/Default/设备/铣床";
		String objectType = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|casc.sast.149.铣床";

		WTLibrary library = getLibraryByName("工艺资源库");
		Sheet sheet = workbook.getSheet("铣床");
		int lastRowNum = sheet.getLastRowNum();
		System.out.println(lastRowNum);
		MPMTooling parentEqp = null;
		for (int i = startRow; i <= lastRowNum; i++) {
			Row row = sheet.getRow(i);
			Cell flagCell = row.getCell(0);
			if (flagCell == null) {
				flagCell = row.createCell(0);
			}
			if ((flagCell.getStringCellValue() != null && flagCell
					.getStringCellValue().equalsIgnoreCase("Y"))) {
				System.out.println("跳过 -- " + getValue(row, 1));
				continue;
			}
			// 设备名称 型号 设备编号 工作台尺寸 T形槽尺寸 主轴到工作台距离 主轴中心到立柱距离 主轴头回转角度
			// 工作台行程(X,Y,Z) 主轴转速 工作精度 数量 厂家 使用部门

			// String value1 = row.getCell(1).getStringCellValue();// 设备名称
			// String value2 = row.getCell(2).getStringCellValue();// 型号
			// String value3 = row.getCell(3) == null ? "" :
			// (row.getCell(3).getCellType() == 0 ? new Integer((int)
			// row.getCell(3).getNumericCellValue()).toString() :
			// row.getCell(3).getStringCellValue()); // 设备编号
			// String value4 = row.getCell(4) == null ? "" :
			// (row.getCell(4).getCellType() == 0 ? new Integer((int)
			// row.getCell(4).getNumericCellValue()).toString() :
			// row.getCell(4).getStringCellValue()); // 工作台尺寸
			// String value5 = row.getCell(5) == null ? "" :
			// (row.getCell(5).getCellType() == 0 ? new Integer((int)
			// row.getCell(5).getNumericCellValue()).toString() :
			// row.getCell(5).getStringCellValue()); // T形槽尺寸
			//
			// String value6 = row.getCell(6) == null ? "" :
			// (row.getCell(6).getCellType() == 0 ? new Integer((int)
			// row.getCell(6).getNumericCellValue()).toString() :
			// row.getCell(6).getStringCellValue()); // 主轴到工作台距离
			// String value7 = row.getCell(7) == null ? "" :
			// (row.getCell(7).getCellType() == 0 ? new Integer((int)
			// row.getCell(7).getNumericCellValue()).toString() :
			// row.getCell(7).getStringCellValue()); // 主轴中心到立柱距离
			// String value8 = row.getCell(8) == null ? "" :
			// (row.getCell(8).getCellType() == 0 ? new Integer((int)
			// row.getCell(8).getNumericCellValue()).toString() :
			// row.getCell(8).getStringCellValue()); // 主轴头回转角度
			// String value9 = row.getCell(9) == null ? "" :
			// (row.getCell(9).getCellType() == 0 ? new Integer((int)
			// row.getCell(9).getNumericCellValue()).toString() :
			// row.getCell(9).getStringCellValue()); // 工作台行程(X,Y,Z)
			// String value10 = row.getCell(10) == null ? "" :
			// (row.getCell(10).getCellType() == 0 ? new Integer((int)
			// row.getCell(10).getNumericCellValue()).toString() :
			// row.getCell(10).getStringCellValue()); // 主轴转速
			// String value11 = row.getCell(11) == null ? "" :
			// (row.getCell(11).getCellType() == 0 ? new Integer((int)
			// row.getCell(11).getNumericCellValue()).toString() :
			// row.getCell(11).getStringCellValue()); // 工作精度
			// String value12 = row.getCell(12) == null ? "" :
			// (row.getCell(12).getCellType() == 0 ? new Integer((int)
			// row.getCell(12).getNumericCellValue()).toString() :
			// row.getCell(12).getStringCellValue()); // 数量
			// String value13 = row.getCell(13) == null ? "" :
			// (row.getCell(13).getStringCellValue()); // 厂家
			// // String value14 = row.getCell(14).getStringCellValue(); // 设备类型
			// // String value15 = row.getCell(15).getStringCellValue(); //文件夹
			// String value14 = row.getCell(14).getStringCellValue(); // 制造单位
			Date value15 = row.getCell(15).getDateCellValue();// 启动时间
			// String value16 = row.getCell(16).getStringCellValue(); //分类

			String value1 = getValue(row, 1);
			String value2 = getValue(row, 2);
			String value3 = getValue(row, 3);
			String value4 = getValue(row, 4);
			String value5 = getValue(row, 5);
			String value6 = getValue(row, 6);
			String value7 = getValue(row, 7);
			String value8 = getValue(row, 8);
			String value9 = getValue(row, 9);
			String value10 = getValue(row, 10);
			String value11 = getValue(row, 11);
			String value12 = getValue(row, 12);
			String value13 = getValue(row, 13);
			String value14 = getValue(row, 14);
			String value16 = getValue(row, 16);

			value1 = value1.trim();
			value2 = value2.trim();
			value3 = value3.trim();
			value4 = value4.trim();
			value5 = value5.trim();
			value6 = value6.trim();
			value7 = value7.trim();
			value8 = value8.trim();
			value9 = value9.trim();
			value10 = value10.trim();
			value11 = value11.trim();
			value12 = value12.trim();
			value13 = value13.trim();
			value14 = value14.trim();
			value16 = value16.trim();
			System.out.println(value1 + "," + value1 + "," + value2);

			MPMTooling currentEqp = null;

			if (value3 == null || value3.length() == 0) {
				// 是个父设备
				parentEqp = createEquipment(library, null, value1, folder,
						objectType, value14);
				currentEqp = parentEqp;
			} else {
				// 创建设备
				currentEqp = createEquipment(library, parentEqp, value1,
						folder, objectType, value14);
			}
			// number 编号
			// equipmentNumber 设备编号
			// name 设备名称
			// modelNumber 型号
			// equipmentAmount 设备数量
			// equipmentVender 厂家
			// remark 备注
			// stagingSize 工作台尺寸
			// Tsize T形槽尺寸
			// stagingDistance 主轴至工作台距离
			// crutchDistance 主轴中心至立柱距离
			// swivellingAngle 主轴头回转角度
			// stagingRunning 工作台行程（X、Y、Z）
			// speed 主轴转速
			// workPrecision 工作精度

			// TODO 将属性设置到设备上
			// currentEqp.setAttribute();
			IBAHelper.setIBAStringValue(currentEqp, "equipmentNumber", value3);
			IBAHelper.setIBAStringValue(currentEqp, "modelNumber", value2);
			IBAHelper.setIBAStringValue(currentEqp, "equipmentAmount", value12);
			IBAHelper.setIBAStringValue(currentEqp, "equipmentVender", value13);
			IBAHelper.setIBAStringValue(currentEqp, "remark", "");
			IBAHelper.setIBAStringValue(currentEqp, "stagingSize", value4);
			IBAHelper.setIBAStringValue(currentEqp, "TSize", value5);
			IBAHelper.setIBAStringValue(currentEqp, "stagingDistance", value6);
			IBAHelper.setIBAStringValue(currentEqp, "crutchDistance", value7);
			IBAHelper.setIBAStringValue(currentEqp, "swivellingAngle", value8);
			IBAHelper.setIBAStringValue(currentEqp, "stagingRunning", value9);
			IBAHelper.setIBAStringValue(currentEqp, "speed", value10);
			IBAHelper.setIBAStringValue(currentEqp, "workPrecision", value11);
			if (value15 != null) {
				IBAHelper.setIBATimestampValue(currentEqp, "startTime",
						new Timestamp(value15.getTime()));
			}
			IBAHelper.setIBAStringValue(currentEqp, "category", value16);
			flagCell.setCellValue("Y");
		}
	}

	/**
	 * 设备-刨床
	 *
	 * @author zhangdong
	 * @date 2013-5-28
	 * @param excelFile
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws WTException
	 *
	 */
	public static void importEquipment_BaoChuang(Workbook workbook)
			throws FileNotFoundException, IOException, WTException {
		int startRow = 1;
		int startColumn = 1;
		String folder = "/Default/设备/刨床";
		String objectType = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|casc.sast.149.刨床";

		WTLibrary library = getLibraryByName("工艺资源库");
		Sheet sheet = workbook.getSheet("刨床");
		int lastRowNum = sheet.getLastRowNum();
		System.out.println(lastRowNum);
		MPMTooling parentEqp = null;
		for (int i = startRow; i <= lastRowNum; i++) {
			Row row = sheet.getRow(i);
			Cell flagCell = row.getCell(0);
			if (flagCell == null) {
				flagCell = row.createCell(0);
			}

			if ((flagCell.getStringCellValue() != null && flagCell
					.getStringCellValue().equalsIgnoreCase("Y"))) {
				System.out.println("跳过 -- " + getValue(row, 1));
				continue;
			}
			// 设备名称 型号 设备编号 工作台尺寸 加工范围 滑枕行程 工作台行程 刀架移动 加工精度 数量 厂家 使用部门

			// String value1 = row.getCell(1).getStringCellValue();// 设备名称
			// String value2 = row.getCell(2).getStringCellValue();// 型号
			// String value3 = row.getCell(3) == null ? "" :
			// (row.getCell(3).getCellType() == 0 ? new Integer((int)
			// row.getCell(3).getNumericCellValue()).toString() :
			// row.getCell(3).getStringCellValue()); // 设备编号
			// String value4 = row.getCell(4) == null ? "" :
			// (row.getCell(4).getCellType() == 0 ? new Integer((int)
			// row.getCell(4).getNumericCellValue()).toString() :
			// row.getCell(4).getStringCellValue()); // 工作台尺寸
			// String value5 = row.getCell(5) == null ? "" :
			// (row.getCell(5).getCellType() == 0 ? new Integer((int)
			// row.getCell(5).getNumericCellValue()).toString() :
			// row.getCell(5).getStringCellValue()); // 加工范围
			// String value6 = row.getCell(6) == null ? "" :
			// (row.getCell(6).getCellType() == 0 ? new Integer((int)
			// row.getCell(6).getNumericCellValue()).toString() :
			// row.getCell(6).getStringCellValue());// 滑枕行程
			// String value7 = row.getCell(7) == null ? "" :
			// (row.getCell(7).getCellType() == 0 ? new Integer((int)
			// row.getCell(7).getNumericCellValue()).toString() :
			// row.getCell(7).getStringCellValue()); // 工作台行程
			// String value8 = row.getCell(8) == null ? "" :
			// (row.getCell(8).getCellType() == 0 ? new Integer((int)
			// row.getCell(8).getNumericCellValue()).toString() :
			// row.getCell(8).getStringCellValue()); // 刀架移动
			// String value9 = row.getCell(9) == null ? "" :
			// (row.getCell(9).getCellType() == 0 ? new Integer((int)
			// row.getCell(9).getNumericCellValue()).toString() :
			// row.getCell(9).getStringCellValue()); // 加工精度
			// String value10 = row.getCell(10) == null ? "" :
			// (row.getCell(10).getCellType() == 0 ? new Integer((int)
			// row.getCell(10).getNumericCellValue()).toString() :
			// row.getCell(10).getStringCellValue()); // 数量
			// String value11 = row.getCell(11) == null ? "" :
			// (row.getCell(11).getCellType() == 0 ? new Integer((int)
			// row.getCell(11).getNumericCellValue()).toString() :
			// row.getCell(11).getStringCellValue()); // 厂家
			// // String value12 = row.getCell(12).getStringCellValue(); //
			// // 设备类型
			// // String value13 = row.getCell(13).getStringCellValue(); // 文件夹
			// String value12 = row.getCell(12) == null ? "" :
			// (row.getCell(12).getStringCellValue()); // 制造单位
			Date value15 = row.getCell(13).getDateCellValue();// 启动时间
			// String value16 = row.getCell(14).getStringCellValue(); //分类

			String value1 = getValue(row, 1);
			String value2 = getValue(row, 2);
			String value3 = getValue(row, 3);
			String value4 = getValue(row, 4);
			String value5 = getValue(row, 5);
			String value6 = getValue(row, 6);
			String value7 = getValue(row, 7);
			String value8 = getValue(row, 8);
			String value9 = getValue(row, 9);
			String value10 = getValue(row, 10);
			String value11 = getValue(row, 11);
			String value12 = getValue(row, 12);

			String value16 = getValue(row, 14);

			value1 = value1.trim();
			value2 = value2.trim();
			value3 = value3.trim();
			value4 = value4.trim();
			value5 = value5.trim();
			value6 = value6.trim();
			value7 = value7.trim();
			value8 = value8.trim();
			value9 = value9.trim();
			value10 = value10.trim();
			value11 = value11.trim();
			value12 = value12.trim();

			value16 = value16.trim();
			System.out.println(value1 + "," + value1 + "," + value2);

			MPMTooling currentEqp = null;

			if (value3 == null || value3.length() == 0) {
				// 是个父设备
				parentEqp = createEquipment(library, null, value1, folder,
						objectType, value12);
				currentEqp = parentEqp;
			} else {
				// 创建设备
				currentEqp = createEquipment(library, parentEqp, value1,
						folder, objectType, value12);
			}

			// TODO 将属性设置到设备上
			// number 编号
			// equipmentNumber 设备编号
			// name 设备名称
			// modelNumber 型号
			// equipmentAmount 设备数量
			// equipmentVender 厂家
			// remark 备注
			// stagingSize 工作台尺寸
			// workingScope 加工范围
			// ramRunning 滑枕行程
			// stagingRunning 工作台行程
			// knifeMove 刀架移动
			// workPrecision 加工精度
			IBAHelper.setIBAStringValue(currentEqp, "equipmentNumber", value3);
			IBAHelper.setIBAStringValue(currentEqp, "modelNumber", value2);
			IBAHelper.setIBAStringValue(currentEqp, "equipmentAmount", value10);
			IBAHelper.setIBAStringValue(currentEqp, "equipmentVender", value11);
			IBAHelper.setIBAStringValue(currentEqp, "remark", "");
			IBAHelper.setIBAStringValue(currentEqp, "stagingSize", value4);
			IBAHelper.setIBAStringValue(currentEqp, "workingScope", value5);
			IBAHelper.setIBAStringValue(currentEqp, "ramRunning", value6);
			IBAHelper.setIBAStringValue(currentEqp, "stagingRunning", value7);
			IBAHelper.setIBAStringValue(currentEqp, "knifeMove", value8);
			IBAHelper.setIBAStringValue(currentEqp, "workPrecision", value9);
			if (value15 != null) {
				IBAHelper.setIBATimestampValue(currentEqp, "startTime",
						new Timestamp(value15.getTime()));
			}
			IBAHelper.setIBAStringValue(currentEqp, "category", value16);
			flagCell.setCellValue("Y");
		}
	}

	/**
	 * 设备-镗床
	 *
	 * @author zhangdong
	 * @date 2013-5-28
	 * @param excelFile
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws WTException
	 *
	 */
	public static void importEquipment_TangChuang(Workbook workbook)
			throws FileNotFoundException, IOException, WTException {
		int startRow = 1;
		int startColumn = 1;
		String folder = "/Default/设备/镗床";
		String objectType = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|casc.sast.149.镗床";

		WTLibrary library = getLibraryByName("工艺资源库");
		Sheet sheet = workbook.getSheet("镗床");
		int lastRowNum = sheet.getLastRowNum();
		System.out.println(lastRowNum);
		MPMTooling parentEqp = null;
		for (int i = startRow; i <= lastRowNum; i++) {
			Row row = sheet.getRow(i);
			Cell flagCell = row.getCell(0);
			if (flagCell == null) {
				flagCell = row.createCell(0);
			}
			if ((flagCell.getStringCellValue() != null && flagCell
					.getStringCellValue().equalsIgnoreCase("Y"))) {
				System.out.println("跳过 -- " + getValue(row, 1));
				continue;
			}
			// 设备名称 型号 设备编号 工作台尺寸 T形槽尺寸 加工范围 主轴最在伸出量 最大镗孔直径 主轴 花盘 数量 厂家 使用部门

			// String value1 = row.getCell(1).getStringCellValue();// 设备名称
			// String value2 = row.getCell(2).getStringCellValue();// 型号
			// String value3 = row.getCell(3) == null ? "" :
			// (row.getCell(3).getCellType() == 0 ? new Integer((int)
			// row.getCell(3).getNumericCellValue()).toString() :
			// row.getCell(3).getStringCellValue()); // 设备编号
			// String value4 = row.getCell(4) == null ? "" :
			// (row.getCell(4).getCellType() == 0 ? new Integer((int)
			// row.getCell(4).getNumericCellValue()).toString() :
			// row.getCell(4).getStringCellValue()); // 工作台尺寸
			// String value5 = row.getCell(5) == null ? "" :
			// (row.getCell(5).getCellType() == 0 ? new Integer((int)
			// row.getCell(5).getNumericCellValue()).toString() :
			// row.getCell(5).getStringCellValue()); // T形槽尺寸
			// String value6 = row.getCell(6) == null ? "" :
			// (row.getCell(6).getCellType() == 0 ? new Integer((int)
			// row.getCell(6).getNumericCellValue()).toString() :
			// row.getCell(6).getStringCellValue()); // 加工范围
			// String value7 = row.getCell(7) == null ? "" :
			// (row.getCell(7).getCellType() == 0 ? new Integer((int)
			// row.getCell(7).getNumericCellValue()).toString() :
			// row.getCell(7).getStringCellValue()); // 主轴最在伸出量
			// String value8 = row.getCell(8) == null ? "" :
			// (row.getCell(8).getCellType() == 0 ? new Integer((int)
			// row.getCell(8).getNumericCellValue()).toString() :
			// row.getCell(8).getStringCellValue()); // 最大镗孔直径
			// String value9 = row.getCell(9) == null ? "" :
			// (row.getCell(9).getCellType() == 0 ? new Integer((int)
			// row.getCell(9).getNumericCellValue()).toString() :
			// row.getCell(9).getStringCellValue()); // 主轴
			// String value10 = row.getCell(10) == null ? "" :
			// (row.getCell(10).getCellType() == 0 ? new Integer((int)
			// row.getCell(10).getNumericCellValue()).toString() :
			// row.getCell(10).getStringCellValue());// 花盘
			// String value11 = row.getCell(11) == null ? "" :
			// (row.getCell(11).getCellType() == 0 ? new Integer((int)
			// row.getCell(11).getNumericCellValue()).toString() :
			// row.getCell(11).getStringCellValue()); // 数量
			// String value12 = row.getCell(12) == null ? "" :
			// (row.getCell(12).getCellType() == 0 ? new Integer((int)
			// row.getCell(12).getNumericCellValue()).toString() :
			// row.getCell(12).getStringCellValue()); // 厂家
			// // String value13 = row.getCell(13).getCellType() == 0 ? new
			// Integer((int) row.getCell(13).getNumericCellValue()).toString() :
			// row.getCell(13).getStringCellValue(); // 设备类型
			// // String value14 = row.getCell(14).getStringCellValue(); // 文件夹
			// String value13 = row.getCell(13) == null ? "" :
			// (row.getCell(13).getStringCellValue()); // 制造单位
			Date value15 = row.getCell(14).getDateCellValue();// 启动时间
			// String value16 = row.getCell(15).getStringCellValue(); //分类

			String value1 = getValue(row, 1);
			String value2 = getValue(row, 2);
			String value3 = getValue(row, 3);
			String value4 = getValue(row, 4);
			String value5 = getValue(row, 5);
			String value6 = getValue(row, 6);
			String value7 = getValue(row, 7);
			String value8 = getValue(row, 8);
			String value9 = getValue(row, 9);
			String value10 = getValue(row, 10);
			String value11 = getValue(row, 11);
			String value12 = getValue(row, 12);
			String value13 = getValue(row, 13);

			String value16 = getValue(row, 15);

			value1 = value1.trim();
			value2 = value2.trim();
			value3 = value3.trim();
			value4 = value4.trim();
			value5 = value5.trim();
			value6 = value6.trim();
			value7 = value7.trim();
			value8 = value8.trim();
			value9 = value9.trim();
			value10 = value10.trim();
			value11 = value11.trim();
			value12 = value12.trim();
			value13 = value13.trim();

			value16 = value16.trim();
			System.out.println(value1 + "," + value1 + "," + value2);

			MPMTooling currentEqp = null;

			if (value3 == null || value3.length() == 0) {
				// 是个父设备
				parentEqp = createEquipment(library, null, value1, folder,
						objectType, value13);
				currentEqp = parentEqp;
			} else {
				// 创建设备
				currentEqp = createEquipment(library, parentEqp, value1,
						folder, objectType, value13);
			}

			// TODO 将属性设置到设备上
			// number 编号
			// equipmentNumber 设备编号
			// name 设备名称
			// modelNumber 型号
			// equipmentAmount 设备数量
			// equipmentVender 厂家
			// workShop 使用部门
			// remark 备注
			// stagingSize 工作台尺寸
			// TSize T形槽尺寸
			// workingScope 加工范围
			// stretchSize 主轴最大伸出量
			// diameter 最大镗孔直径
			// chiefAxis 主轴
			// flowerDisc 花盘
			IBAHelper.setIBAStringValue(currentEqp, "equipmentNumber", value3);
			IBAHelper.setIBAStringValue(currentEqp, "modelNumber", value2);
			IBAHelper.setIBAStringValue(currentEqp, "equipmentAmount", value11);
			IBAHelper.setIBAStringValue(currentEqp, "equipmentVender", value12);
			IBAHelper.setIBAStringValue(currentEqp, "remark", "");
			IBAHelper.setIBAStringValue(currentEqp, "stagingSize", value4);
			IBAHelper.setIBAStringValue(currentEqp, "TSize", value5);
			IBAHelper.setIBAStringValue(currentEqp, "workingScope", value6);
			IBAHelper.setIBAStringValue(currentEqp, "stretchSize", value7);
			IBAHelper.setIBAStringValue(currentEqp, "diameter", value8);
			IBAHelper.setIBAStringValue(currentEqp, "chiefAxis", value9);
			IBAHelper.setIBAStringValue(currentEqp, "flowerDisc", value10);
			if (value15 != null) {
				IBAHelper.setIBATimestampValue(currentEqp, "startTime",
						new Timestamp(value15.getTime()));
			}
			IBAHelper.setIBAStringValue(currentEqp, "category", value16);
			flagCell.setCellValue("Y");
		}
	}

	/**
	 * 设备-磨床
	 *
	 * @author zhangdong
	 * @date 2013-5-28
	 * @param excelFile
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws WTException
	 *
	 */
	public static void importEquipment_MoChuang(Workbook workbook)
			throws FileNotFoundException, IOException, WTException {
		int startRow = 1;
		String folder = "/Default/设备/磨床";
		String objectType = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|casc.sast.149.磨床";

		WTLibrary library = getLibraryByName("工艺资源库");
		Sheet sheet = workbook.getSheet("磨床");
		int lastRowNum = sheet.getLastRowNum();
		System.out.println(lastRowNum);
		MPMTooling parentEqp = null;
		for (int i = startRow; i <= lastRowNum; i++) {
			Row row = sheet.getRow(i);
			Cell flagCell = row.getCell(0);
			if (flagCell == null) {
				flagCell = row.createCell(0);
			}
			if ((flagCell.getStringCellValue() != null && flagCell
					.getStringCellValue().equalsIgnoreCase("Y"))) {
				System.out.println("跳过 -- " + getValue(row, 1));
				continue;
			}
			// 设备名称 型号 设备编号 工作台尺寸 加工范围 其它技术参数 工作台行程 加工精度 工作台速度 数量 厂家 使用部门

			String value1 = row.getCell(1) == null ? "" : row.getCell(1)
					.getStringCellValue();// 设备名称
			String value2 = row.getCell(2) == null ? "" : row.getCell(2)
					.getStringCellValue();// 型号
			String value3 = row.getCell(3) == null ? "" : (row.getCell(3)
					.getCellType() == 0 ? new Integer((int) row.getCell(3)
					.getNumericCellValue()).toString() : row.getCell(3)
					.getStringCellValue()); // 设备编号
			String value4 = row.getCell(4) == null ? "" : (row.getCell(4)
					.getCellType() == 0 ? new Integer((int) row.getCell(4)
					.getNumericCellValue()).toString() : row.getCell(4)
					.getStringCellValue()); // 工作台尺寸
			String value5 = row.getCell(5) == null ? "" : (row.getCell(5)
					.getCellType() == 0 ? new Integer((int) row.getCell(5)
					.getNumericCellValue()).toString() : row.getCell(5)
					.getStringCellValue()); // 加工范围
			String value6 = row.getCell(6) == null ? "" : (row.getCell(6)
					.getCellType() == 0 ? new Integer((int) row.getCell(6)
					.getNumericCellValue()).toString() : row.getCell(6)
					.getStringCellValue()); // 其它技术参数
			String value7 = row.getCell(7) == null ? "" : (row.getCell(7)
					.getCellType() == 0 ? new Integer((int) row.getCell(7)
					.getNumericCellValue()).toString() : row.getCell(7)
					.getStringCellValue()); // 工作台行程
			String value8 = row.getCell(8) == null ? "" : (row.getCell(8)
					.getCellType() == 0 ? new Integer((int) row.getCell(8)
					.getNumericCellValue()).toString() : row.getCell(8)
					.getStringCellValue()); // 加工精度
			String value9 = row.getCell(9) == null ? "" : (row.getCell(9)
					.getCellType() == 0 ? new Integer((int) row.getCell(9)
					.getNumericCellValue()).toString() : row.getCell(9)
					.getStringCellValue()); // 工作台速度
			String value10 = row.getCell(10) == null ? "" : (row.getCell(10)
					.getCellType() == 0 ? new Integer((int) row.getCell(10)
					.getNumericCellValue()).toString() : row.getCell(10)
					.getStringCellValue()); // 数量
			String value11 = row.getCell(11) == null ? "" : (row.getCell(11)
					.getCellType() == 0 ? new Integer((int) row.getCell(11)
					.getNumericCellValue()).toString() : row.getCell(11)
					.getStringCellValue()); // 厂家
			// String value12 = row.getCell(14).getStringCellValue(); //
			// 设备类型
			// String value13 = row.getCell(15).getStringCellValue(); // 文件夹
			String value12 = row.getCell(12) == null ? "" : (row.getCell(12)
					.getStringCellValue()); // 制造单位
			Date value15 = row.getCell(13).getDateCellValue();// 启动时间
			String value16 = row.getCell(14).getStringCellValue(); // 分类

			value1 = value1.trim();
			value2 = value2.trim();
			value3 = value3.trim();
			value4 = value4.trim();
			value5 = value5.trim();
			value6 = value6.trim();
			value7 = value7.trim();
			value8 = value8.trim();
			value9 = value9.trim();
			value10 = value10.trim();
			value11 = value11.trim();
			value12 = value12.trim();

			value16 = value16.trim();
			System.out.println(value1 + "," + value1 + "," + value2);

			MPMTooling currentEqp = null;

			if (value3 == null || value3.length() == 0) {
				// 是个父设备
				parentEqp = createEquipment(library, null, value1, folder,
						objectType, value12);
				currentEqp = parentEqp;
			} else {
				// 创建设备
				currentEqp = createEquipment(library, parentEqp, value1,
						folder, objectType, value12);
			}

			// TODO 将属性设置到设备上
			// number 编号
			// equipmentNumber 设备编号
			// name 设备名称
			// modelNumber 型号
			// equipmentAmount 设备数量
			// equipmentVender 厂家
			// remark 备注
			// stagingSize 工作台尺寸
			// workingScope 加工范围
			// skillParameter 其它技术参数
			// stagingRunning 工作台行程
			// workPrecision 加工精度
			// speed 工作台速度
			IBAHelper.setIBAStringValue(currentEqp, "equipmentNumber", value3);
			IBAHelper.setIBAStringValue(currentEqp, "modelNumber", value2);
			IBAHelper.setIBAStringValue(currentEqp, "equipmentAmount", value10);
			IBAHelper.setIBAStringValue(currentEqp, "equipmentVender", value11);
			IBAHelper.setIBAStringValue(currentEqp, "remark", "");
			IBAHelper.setIBAStringValue(currentEqp, "stagingSize", value4);
			IBAHelper.setIBAStringValue(currentEqp, "workingScope", value5);
			IBAHelper.setIBAStringValue(currentEqp, "skillParameter", value6);
			IBAHelper.setIBAStringValue(currentEqp, "stagingRunning", value7);
			IBAHelper.setIBAStringValue(currentEqp, "workPrecision", value8);
			IBAHelper.setIBAStringValue(currentEqp, "speed", value9);
			if (value15 != null) {
				IBAHelper.setIBATimestampValue(currentEqp, "startTime",
						new Timestamp(value15.getTime()));
			}
			IBAHelper.setIBAStringValue(currentEqp, "category", value16);
			flagCell.setCellValue("Y");
		}
	}

	/**
	 * 设备-钻床
	 *
	 * @author zhangdong
	 * @date 2013-5-28
	 * @param excelFile
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws WTException
	 *
	 */
	public static void importEquipment_ZuanChuang(Workbook workbook)
			throws FileNotFoundException, IOException, WTException {
		int startRow = 1;
		int startColumn = 1;
		String folder = "/Default/设备/钻床";
		String objectType = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|casc.sast.149.钻床";

		WTLibrary library = getLibraryByName("工艺资源库");
		Sheet sheet = workbook.getSheet("钻床");
		int lastRowNum = sheet.getLastRowNum();
		System.out.println(lastRowNum);
		for (int i = startRow; i <= lastRowNum; i++) {
			Row row = sheet.getRow(i);
			Cell flagCell = row.getCell(0);
			if (flagCell == null) {
				flagCell = row.createCell(0);
			}
			if ((flagCell.getStringCellValue() != null && flagCell
					.getStringCellValue().equalsIgnoreCase("Y"))) {
				System.out.println("跳过 -- " + getValue(row, 1));
				continue;
			}
			// 设备名称 型号 设备编号 工作台尺寸 T形槽尺寸 加工范中心线至立柱表面距离围 端面至底座距离 主轴行程 最大钻孔直径
			// 主轴 数量 厂家 使用部门

			String value1 = row.getCell(1) == null ? "" : row.getCell(1)
					.getStringCellValue();// 设备名称
			String value2 = row.getCell(2) == null ? "" : row.getCell(2)
					.getStringCellValue();// 型号
			String value3 = row.getCell(3) == null ? "" : (row.getCell(3)
					.getCellType() == 0 ? new Integer((int) row.getCell(3)
					.getNumericCellValue()).toString() : row.getCell(3)
					.getStringCellValue()); // 设备编号
			String value4 = row.getCell(4) == null ? "" : (row.getCell(4)
					.getCellType() == 0 ? new Integer((int) row.getCell(4)
					.getNumericCellValue()).toString() : row.getCell(4)
					.getStringCellValue()); // 工作台尺寸
			String value5 = row.getCell(5) == null ? "" : (row.getCell(5)
					.getCellType() == 0 ? new Integer((int) row.getCell(5)
					.getNumericCellValue()).toString() : row.getCell(5)
					.getStringCellValue()); // T形槽尺寸
			String value6 = row.getCell(6) == null ? "" : (row.getCell(6)
					.getCellType() == 0 ? new Integer((int) row.getCell(6)
					.getNumericCellValue()).toString() : row.getCell(6)
					.getStringCellValue()); // 中心线至立柱表面距离围
			String value7 = row.getCell(7) == null ? "" : (row.getCell(7)
					.getCellType() == 0 ? new Integer((int) row.getCell(7)
					.getNumericCellValue()).toString() : row.getCell(7)
					.getStringCellValue()); // 端面至底座距离
			String value8 = row.getCell(8) == null ? "" : (row.getCell(8)
					.getCellType() == 0 ? new Integer((int) row.getCell(8)
					.getNumericCellValue()).toString() : row.getCell(8)
					.getStringCellValue()); // 主轴行程
			String value9 = row.getCell(9) == null ? "" : (row.getCell(9)
					.getCellType() == 0 ? new Integer((int) row.getCell(9)
					.getNumericCellValue()).toString() : row.getCell(9)
					.getStringCellValue()); // 最大钻孔直径
			String value10 = row.getCell(10) == null ? "" : (row.getCell(10)
					.getCellType() == 0 ? new Integer((int) row.getCell(10)
					.getNumericCellValue()).toString() : row.getCell(10)
					.getStringCellValue()); // 主轴
			String value11 = row.getCell(11) == null ? "" : (row.getCell(11)
					.getCellType() == 0 ? new Integer((int) row.getCell(11)
					.getNumericCellValue()).toString() : row.getCell(11)
					.getStringCellValue()); // 数量
			String value12 = row.getCell(12) == null ? "" : (row.getCell(12)
					.getCellType() == 0 ? new Integer((int) row.getCell(12)
					.getNumericCellValue()).toString() : row.getCell(12)
					.getStringCellValue()); // 厂家
			// String value13 = row.getCell(16).getStringCellValue(); //
			// 设备类型
			// String value14 = row.getCell(17).getStringCellValue(); // 文件夹
			String value13 = row.getCell(13) == null ? "" : (row.getCell(13)
					.getStringCellValue()); // 制造单位
			Date value15 = row.getCell(14).getDateCellValue();// 启动时间
			String value16 = row.getCell(15).getStringCellValue(); // 分类

			value1 = value1.trim();
			value2 = value2.trim();
			value3 = value3.trim();
			value4 = value4.trim();
			value5 = value5.trim();
			value6 = value6.trim();
			value7 = value7.trim();
			value8 = value8.trim();
			value9 = value9.trim();
			value10 = value10.trim();
			value11 = value11.trim();
			value12 = value12.trim();
			value13 = value13.trim();

			value16 = value16.trim();
			System.out.println(value1 + "," + value1 + "," + value2);
			MPMTooling parentEqp = null;
			MPMTooling currentEqp = null;

			if (value3 == null || value3.length() == 0) {
				// 是个父设备
				parentEqp = createEquipment(library, null, value1, folder,
						objectType, value13);
				currentEqp = parentEqp;
			} else {
				// 创建设备
				currentEqp = createEquipment(library, parentEqp, value1,
						folder, objectType, value13);
			}

			// TODO 将属性设置到设备上
			// number 编号
			// equipmentNumber 设备编号
			// name 设备名称
			// modelNumber 型号
			// equipmentAmount 设备数量
			// equipmentVender 厂家
			// remark 备注
			// stagingSize 工作台尺寸
			// TSize T形槽尺寸
			// crutchDistance 中心线至立柱表面距离
			// baseDistance 端面至底座距离
			// axisRunning 主轴行程
			// diameter 最大钻孔直径
			// chiefAxis 主轴
			IBAHelper.setIBAStringValue(currentEqp, "equipmentNumber", value3);
			IBAHelper.setIBAStringValue(currentEqp, "modelNumber", value2);
			IBAHelper.setIBAStringValue(currentEqp, "equipmentAmount", value11);
			IBAHelper.setIBAStringValue(currentEqp, "equipmentVender", value12);
			IBAHelper.setIBAStringValue(currentEqp, "remark", "");
			IBAHelper.setIBAStringValue(currentEqp, "stagingSize", value4);
			IBAHelper.setIBAStringValue(currentEqp, "TSize", value5);
			IBAHelper.setIBAStringValue(currentEqp, "crutchDistance", value6);
			IBAHelper.setIBAStringValue(currentEqp, "baseDistance", value7);
			IBAHelper.setIBAStringValue(currentEqp, "axisRunning", value8);
			IBAHelper.setIBAStringValue(currentEqp, "diameter", value9);
			IBAHelper.setIBAStringValue(currentEqp, "chiefAxis", value10);
			if (value15 != null) {
				IBAHelper.setIBATimestampValue(currentEqp, "startTime",
						new Timestamp(value15.getTime()));
			}
			IBAHelper.setIBAStringValue(currentEqp, "category", value16);
			flagCell.setCellValue("Y");
		}
	}

	/**
	 * 设备-线切割
	 *
	 * @author zhangdong
	 * @date 2013-5-28
	 * @param excelFile
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws WTException
	 *
	 */
	public static void importEquipment_XianQieGe(Workbook workbook)
			throws FileNotFoundException, IOException, WTException {
		int startRow = 1;
		int startColumn = 1;
		String folder = "/Default/设备/线切割";
		String objectType = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|casc.sast.149.线切割";

		WTLibrary library = getLibraryByName("工艺资源库");
		Sheet sheet = workbook.getSheet("线切割");
		int lastRowNum = sheet.getLastRowNum();
		System.out.println(lastRowNum);
		MPMTooling parentEqp = null;
		for (int i = startRow; i <= lastRowNum; i++) {
			Row row = sheet.getRow(i);
			Cell flagCell = row.getCell(0);
			if (flagCell == null) {
				flagCell = row.createCell(0);
			}
			if ((flagCell.getStringCellValue() != null && flagCell
					.getStringCellValue().equalsIgnoreCase("Y"))) {
				System.out.println("跳过 -- " + getValue(row, 1));
				continue;
			}
			// 设备名称 型号 设备编号 工作台行程 工作台尺寸 最在切割厚度 最大切割锥度 电极丝直径 走丝速度 数控控制轴数
			// 精度及功能 数量 厂家 使用部门

			String value1 = row.getCell(1) == null ? "" : row.getCell(1)
					.getStringCellValue();// 设备名称
			String value2 = row.getCell(2) == null ? "" : row.getCell(2)
					.getStringCellValue();// 型号
			String value3 = row.getCell(3) == null ? "" : (row.getCell(3)
					.getCellType() == 0 ? new Integer((int) row.getCell(3)
					.getNumericCellValue()).toString() : row.getCell(3)
					.getStringCellValue()); // 设备编号
			String value4 = row.getCell(4) == null ? "" : (row.getCell(4)
					.getCellType() == 0 ? new Integer((int) row.getCell(4)
					.getNumericCellValue()).toString() : row.getCell(4)
					.getStringCellValue());// 工作台行程
			String value5 = row.getCell(5) == null ? "" : (row.getCell(5)
					.getCellType() == 0 ? new Integer((int) row.getCell(5)
					.getNumericCellValue()).toString() : row.getCell(5)
					.getStringCellValue()); // 工作台尺寸
			String value6 = row.getCell(6) == null ? "" : (row.getCell(6)
					.getCellType() == 0 ? new Integer((int) row.getCell(6)
					.getNumericCellValue()).toString() : row.getCell(6)
					.getStringCellValue()); // 最在切割厚度
			String value7 = row.getCell(7) == null ? "" : (row.getCell(7)
					.getCellType() == 0 ? new Integer((int) row.getCell(7)
					.getNumericCellValue()).toString() : row.getCell(7)
					.getStringCellValue()); // 最大切割锥度
			String value8 = row.getCell(8) == null ? "" : (row.getCell(8)
					.getCellType() == 0 ? new Integer((int) row.getCell(8)
					.getNumericCellValue()).toString() : row.getCell(8)
					.getStringCellValue()); // 电极丝直径
			String value9 = row.getCell(9) == null ? "" : (row.getCell(9)
					.getCellType() == 0 ? new Integer((int) row.getCell(9)
					.getNumericCellValue()).toString() : row.getCell(9)
					.getStringCellValue()); // 走丝速度
			String value10 = row.getCell(10) == null ? "" : (row.getCell(10)
					.getCellType() == 0 ? new Integer((int) row.getCell(10)
					.getNumericCellValue()).toString() : row.getCell(10)
					.getStringCellValue()); // 数控控制轴数
			String value11 = row.getCell(11) == null ? "" : (row.getCell(11)
					.getCellType() == 0 ? new Integer((int) row.getCell(11)
					.getNumericCellValue()).toString() : row.getCell(11)
					.getStringCellValue()); // 精度及功能
			String value12 = row.getCell(12) == null ? "" : (row.getCell(12)
					.getCellType() == 0 ? new Integer((int) row.getCell(12)
					.getNumericCellValue()).toString() : row.getCell(12)
					.getStringCellValue()); // 数量
			String value13 = row.getCell(13) == null ? "" : (row.getCell(13)
					.getCellType() == 0 ? new Integer((int) row.getCell(13)
					.getNumericCellValue()).toString() : row.getCell(13)
					.getStringCellValue()); // 厂家
			// String value14 = row.getCell(16).getStringCellValue(); //
			// 设备类型
			// String value15 = row.getCell(17).getStringCellValue(); // 文件夹
			String value14 = row.getCell(14) == null ? "" : (row.getCell(14)
					.getStringCellValue()); // 制造单位
			Date value15 = row.getCell(15).getDateCellValue();// 启动时间
			String value16 = row.getCell(16).getStringCellValue(); // 分类

			value1 = value1.trim();
			value2 = value2.trim();
			value3 = value3.trim();
			value4 = value4.trim();
			value5 = value5.trim();
			value6 = value6.trim();
			value7 = value7.trim();
			value8 = value8.trim();
			value9 = value9.trim();
			value10 = value10.trim();
			value11 = value11.trim();
			value12 = value12.trim();
			value13 = value13.trim();
			value14 = value14.trim();
			value16 = value16.trim();
			System.out.println(value1 + "," + value1 + "," + value2);
			MPMTooling currentEqp = null;

			if (value3 == null || value3.length() == 0) {
				// 是个父设备
				parentEqp = createEquipment(library, null, value1, folder,
						objectType, value14);
				currentEqp = parentEqp;
			} else {
				// 创建设备
				currentEqp = createEquipment(library, parentEqp, value1,
						folder, objectType, value14);
			}

			// TODO 将属性设置到设备上
			// number 编号
			// equipmentNumber 设备编号
			// name 设备名称
			// modelNumber 型号
			// equipmentAmount 设备数量
			// equipmentVender 厂家
			// remark 备注
			// stagingRunning 工作台行程
			// stagingSize 工作台尺寸
			// thickness 最大切割厚度
			// taper 最大切割锥度
			// diameter 电极丝直径
			// speed 走丝速度
			// axisAmount 数控控制轴数
			// workPrecision 精度及功能
			IBAHelper.setIBAStringValue(currentEqp, "equipmentNumber", value3);
			IBAHelper.setIBAStringValue(currentEqp, "modelNumber", value2);
			IBAHelper.setIBAStringValue(currentEqp, "equipmentAmount", value12);
			IBAHelper.setIBAStringValue(currentEqp, "equipmentVender", value13);
			IBAHelper.setIBAStringValue(currentEqp, "remark", "");
			IBAHelper.setIBAStringValue(currentEqp, "stagingRunning", value4);
			IBAHelper.setIBAStringValue(currentEqp, "stagingSize", value5);
			IBAHelper.setIBAStringValue(currentEqp, "thickness", value6);
			IBAHelper.setIBAStringValue(currentEqp, "taper", value7);
			IBAHelper.setIBAStringValue(currentEqp, "diameter", value8);
			IBAHelper.setIBAStringValue(currentEqp, "speed", value9);
			IBAHelper.setIBAStringValue(currentEqp, "axisAmount", value10);
			IBAHelper.setIBAStringValue(currentEqp, "workPrecision", value11);
			if (value15 != null) {
				IBAHelper.setIBATimestampValue(currentEqp, "startTime",
						new Timestamp(value15.getTime()));
			}
			IBAHelper.setIBAStringValue(currentEqp, "category", value16);
			flagCell.setCellValue("Y");
		}
	}

	/**
	 * 设备-滚齿机
	 *
	 * @author zhangdong
	 * @date 2013-5-28
	 * @param excelFile
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws WTException
	 *
	 */
	public static void importEquipment_GunChiJi(Workbook workbook)
			throws FileNotFoundException, IOException, WTException {
		int startRow = 1;
		int startColumn = 1;
		String folder = "/Default/设备/滚齿机";
		String objectType = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|casc.sast.149.滚齿机";

		WTLibrary library = getLibraryByName("工艺资源库");
		Sheet sheet = workbook.getSheet("滚齿机");
		int lastRowNum = sheet.getLastRowNum();
		System.out.println(lastRowNum);
		MPMTooling parentEqp = null;
		for (int i = startRow; i <= lastRowNum; i++) {
			Row row = sheet.getRow(i);
			Cell flagCell = row.getCell(0);
			if (flagCell == null) {
				flagCell = row.createCell(0);
			}
			if ((flagCell.getStringCellValue() != null && flagCell
					.getStringCellValue().equalsIgnoreCase("Y"))) {
				System.out.println("跳过 -- "
						+ row.getCell(1).getStringCellValue());
				continue;
			}
			// 设备名称 型号 设备编号 最大加工直径X模数 最小中心距离 齿宽 最大螺旋角 加工齿数 其它技术参数 工作台 加工精度
			// 数量 厂家 使用部门

			String value1 = row.getCell(1) == null ? "" : row.getCell(1)
					.getStringCellValue();// 父设备名称
			String value2 = row.getCell(2) == null ? "" : row.getCell(2)
					.getStringCellValue();// 型号
			String value3 = row.getCell(3) == null ? "" : (row.getCell(3)
					.getCellType() == 0 ? new Integer((int) row.getCell(3)
					.getNumericCellValue()).toString() : row.getCell(3)
					.getStringCellValue()); // 设备编号
			String value4 = row.getCell(4) == null ? "" : (row.getCell(4)
					.getCellType() == 0 ? new Integer((int) row.getCell(4)
					.getNumericCellValue()).toString() : row.getCell(4)
					.getStringCellValue()); // 最大加工直径X模数
			String value5 = row.getCell(5) == null ? "" : (row.getCell(5)
					.getCellType() == 0 ? new Integer((int) row.getCell(5)
					.getNumericCellValue()).toString() : row.getCell(5)
					.getStringCellValue()); // 最小中心距离
			String value6 = row.getCell(6) == null ? "" : (row.getCell(6)
					.getCellType() == 0 ? new Integer((int) row.getCell(6)
					.getNumericCellValue()).toString() : row.getCell(6)
					.getStringCellValue()); // 齿宽
			String value7 = row.getCell(7) == null ? "" : (row.getCell(7)
					.getCellType() == 0 ? new Integer((int) row.getCell(7)
					.getNumericCellValue()).toString() : row.getCell(7)
					.getStringCellValue()); // 最大螺旋角
			String value8 = row.getCell(8) == null ? "" : (row.getCell(8)
					.getCellType() == 0 ? new Integer((int) row.getCell(8)
					.getNumericCellValue()).toString() : row.getCell(8)
					.getStringCellValue()); // 加工齿数
			String value9 = row.getCell(9) == null ? "" : (row.getCell(9)
					.getCellType() == 0 ? new Integer((int) row.getCell(9)
					.getNumericCellValue()).toString() : row.getCell(9)
					.getStringCellValue()); // 其它技术参数
			String value10 = row.getCell(10) == null ? "" : (row.getCell(10)
					.getCellType() == 0 ? new Integer((int) row.getCell(10)
					.getNumericCellValue()).toString() : row.getCell(10)
					.getStringCellValue());// 工作台
			String value11 = row.getCell(11) == null ? "" : (row.getCell(11)
					.getCellType() == 0 ? new Integer((int) row.getCell(11)
					.getNumericCellValue()).toString() : row.getCell(11)
					.getStringCellValue()); // 加工精度
			String value12 = row.getCell(12) == null ? "" : (row.getCell(12)
					.getCellType() == 0 ? new Integer((int) row.getCell(12)
					.getNumericCellValue()).toString() : row.getCell(12)
					.getStringCellValue()); // 数量
			String value13 = row.getCell(13) == null ? "" : (row.getCell(13)
					.getCellType() == 0 ? new Integer((int) row.getCell(13)
					.getNumericCellValue()).toString() : row.getCell(13)
					.getStringCellValue()); // 厂家
			// String value14 = row.getCell(16).getStringCellValue(); //
			// 设备类型
			// String value15 = row.getCell(17).getStringCellValue(); // 文件夹
			String value14 = row.getCell(14).getStringCellValue(); // 制造单位
			Date value15 = row.getCell(15).getDateCellValue();// 启动时间
			String value16 = row.getCell(16).getStringCellValue(); // 分类

			value1 = value1.trim();
			value2 = value2.trim();
			value3 = value3.trim();
			value4 = value4.trim();
			value5 = value5.trim();
			value6 = value6.trim();
			value7 = value7.trim();
			value8 = value8.trim();
			value9 = value9.trim();
			value10 = value10.trim();
			value11 = value11.trim();
			value12 = value12.trim();
			value13 = value13.trim();
			value14 = value14.trim();
			value16 = value16.trim();
			System.out.println(value1 + "," + value1 + "," + value2);
			MPMTooling currentEqp = null;

			if (value3 == null || value3.length() == 0) {
				// 是个父设备
				parentEqp = createEquipment(library, null, value1, folder,
						objectType, value14);
				currentEqp = parentEqp;
			} else {
				// 创建设备
				currentEqp = createEquipment(library, parentEqp, value1,
						folder, objectType, value14);
			}

			// TODO 将属性设置到设备上
			// number 编号
			// equipmentNumber 设备编号
			// name 设备名称
			// modelNumber 型号
			// equipmentAmount 设备数量
			// equipmentVender 厂家
			// remark 备注
			// centreSpacing 最小中心距
			// diameter 最大加工直径X模数
			// toothWidth 齿宽
			// maxHelixAngle 最大螺旋角
			// toothAmount 加工齿数
			// skillParameter 其它技术参数
			// stagingSize 工作台
			// workPrecision 加工精度
			IBAHelper.setIBAStringValue(currentEqp, "equipmentNumber", value3);
			IBAHelper.setIBAStringValue(currentEqp, "modelNumber", value2);
			IBAHelper.setIBAStringValue(currentEqp, "equipmentAmount", value12);
			IBAHelper.setIBAStringValue(currentEqp, "equipmentVender", value13);
			IBAHelper.setIBAStringValue(currentEqp, "remark", "");
			IBAHelper.setIBAStringValue(currentEqp, "diameter", value4);
			IBAHelper.setIBAStringValue(currentEqp, "toothWidth", value5);
			IBAHelper.setIBAStringValue(currentEqp, "thickness", value6);
			IBAHelper.setIBAStringValue(currentEqp, "maxHelixAngle", value7);
			IBAHelper.setIBAStringValue(currentEqp, "toothAmount", value8);
			IBAHelper.setIBAStringValue(currentEqp, "skillParameter", value9);
			IBAHelper.setIBAStringValue(currentEqp, "stagingSize", value10);
			IBAHelper.setIBAStringValue(currentEqp, "workPrecision", value11);
			if (value15 != null) {
				IBAHelper.setIBATimestampValue(currentEqp, "startTime",
						new Timestamp(value15.getTime()));
			}
			IBAHelper.setIBAStringValue(currentEqp, "category", value16);
			flagCell.setCellValue("Y");
		}
	}

	/**
	 * 设备-其它设备 改变了
	 *
	 * @author zhangdong
	 * @date 2013-5-28
	 * @param excelFile
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws WTException
	 *
	 */
	public static void importEquipment_Others(Workbook workbook)
			throws FileNotFoundException, IOException, WTException {

		WTLibrary library = getLibraryByName("工艺资源库");
		List<String> sheetList = new ArrayList<String>();
		sheetList.add("焊接设备");
		// sheetList.add("加工中心");
		sheetList.add("电缆剥皮机");
		// sheetList.add("波峰焊接机");
		sheetList.add("水清洗机");
		sheetList.add("热压机");
		sheetList.add("真空设备");
		sheetList.add("烘箱");
		sheetList.add("表处设备");
		// sheetList.add("化学沉铜生产线");
		// sheetList.add("六轴数控钻床");
		// sheetList.add("平行光曝光机");
		sheetList.add("锯床");
		sheetList.add("冲床");
		sheetList.add("注塑机");
		sheetList.add("印制板");
		sheetList.add("其它设备");

		for (String sheetName : sheetList) {
			int startRow = 1;
			String folder = "/Default/设备/" + sheetName;// 此值会被excel文件里的值取代
			String objectType = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Equipment|casc.sast.149.其它设备";// 此值会被excel里的值替代
			Sheet sheet = workbook.getSheet(sheetName);
			int lastRowNum = sheet.getLastRowNum();
			System.out.println(lastRowNum);
			MPMTooling parentEqp = null;
			for (int i = startRow; i <= lastRowNum; i++) {
				Row row = sheet.getRow(i);
				Cell flagCell = row.getCell(0);
				if (flagCell == null) {
					flagCell = row.createCell(0);
				}
				if ((flagCell.getStringCellValue() != null && flagCell
						.getStringCellValue().equalsIgnoreCase("Y"))) {
					System.out.println("跳过 -- "
							+ row.getCell(1).getStringCellValue());
					continue;
				}
				// 设备名称 型号 设备编号 主要技术参数 数量 厂家 备注 使用部门

				String value1 = row.getCell(1) == null ? "" : row.getCell(1)
						.getStringCellValue();// 设备名称
				String value2 = row.getCell(2) == null ? "" : row.getCell(2)
						.getStringCellValue();// 型号
				String value3 = row.getCell(3) == null ? "" : (row.getCell(3)
						.getCellType() == 0 ? new Integer((int) row.getCell(3)
						.getNumericCellValue()).toString() : row.getCell(3)
						.getStringCellValue()); // 设备编号
				String value4 = row.getCell(4) == null ? "" : (row.getCell(4)
						.getCellType() == 0 ? new Integer((int) row.getCell(4)
						.getNumericCellValue()).toString() : row.getCell(4)
						.getStringCellValue()); // 主要技术参数
				String value5 = row.getCell(5) == null ? "" : (row.getCell(5)
						.getCellType() == 0 ? new Integer((int) row.getCell(5)
						.getNumericCellValue()).toString() : row.getCell(5)
						.getStringCellValue()); // 数量
				String value6 = row.getCell(6) == null ? "" : (row.getCell(6)
						.getCellType() == 0 ? new Integer((int) row.getCell(6)
						.getNumericCellValue()).toString() : row.getCell(6)
						.getStringCellValue()); // 厂家
				String value7 = row.getCell(7) == null ? "" : (row.getCell(7)
						.getCellType() == 0 ? new Integer((int) row.getCell(7)
						.getNumericCellValue()).toString() : row.getCell(7)
						.getStringCellValue()); // 备注
				// String value8 = row.getCell(8).getCellType() == 0 ? new
				// Integer((int)
				// row.getCell(8).getNumericCellValue()).toString() :
				// row.getCell(8).getStringCellValue();// 设备类型
				// String value9 = row.getCell(9).getStringCellValue(); // 文件夹
				String value8 = row.getCell(8) == null ? "" : (row.getCell(8)
						.getStringCellValue()); // 制造单位

				Date value15 = row.getCell(9).getDateCellValue();
				String value16 = row.getCell(10).getStringCellValue(); // 分类

				value1 = value1.trim();
				value2 = value2.trim();
				value3 = value3.trim();
				value4 = value4.trim();
				value5 = value5.trim();
				value6 = value6.trim();
				value7 = value7.trim();
				value8 = value8.trim();
				value16 = value16.trim();

				System.out.println(value1 + "," + value1 + "," + value2
						+ ",  设备编号：" + value3);
				MPMTooling currentEqp = null;

				if (value3 == null || value3.length() == 0) {
					// 是个父设备
					parentEqp = createEquipment(library, null, value1, folder,
							objectType, value8);
					currentEqp = parentEqp;
				} else {
					// 创建设备
					currentEqp = createEquipment(library, parentEqp, value1,
							folder, objectType, value8);
				}

				// TODO 将属性设置到设备上
				// currentEqp.setAttribute();
				// number 编号
				// equipmentNumber 设备编号
				// name 设备名称
				// modelNumber 型号
				// equipmentAmount 设备数量
				// equipmentVender 厂家
				// remark 备注
				// skillParameter 主要技术参数
				IBAHelper.setIBAStringValue(currentEqp, "equipmentNumber",
						value3);
				IBAHelper.setIBAStringValue(currentEqp, "modelNumber", value2);
				IBAHelper.setIBAStringValue(currentEqp, "equipmentAmount",
						value5);
				IBAHelper.setIBAStringValue(currentEqp, "equipmentVender",
						value6);
				IBAHelper.setIBAStringValue(currentEqp, "remark", "");
				IBAHelper.setIBAStringValue(currentEqp, "skillParameter",
						value4);
				if (value15 != null) {
					IBAHelper.setIBATimestampValue(currentEqp, "startTime",
							new Timestamp(value15.getTime()));
				}
				IBAHelper.setIBAStringValue(currentEqp, "category", value16);

				flagCell.setCellValue("Y");
			}

		}

	}

	/**
	 * 生产资源导入开始
	 */

	/**
	 * 工位
	 *
	 * @author zhangdong
	 * @date 2013-5-28
	 * @param excelFile
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 *
	 */
	public static void importWorkCenter(Workbook workbook)
			throws FileNotFoundException, IOException, WTException,
			WTPropertyVetoException {
		int startRow = 1;

		WTLibrary library = getLibraryByName("工艺资源库");
		Sheet sheet = workbook.getSheet("工位");
		if(sheet == null) {
			return ;
		}
		int lastRowNum = sheet.getLastRowNum();
		System.out.println(lastRowNum);
		for (int i = startRow; i <= lastRowNum; i++) {
			Row row = sheet.getRow(i);
			Cell flagCell = row.getCell(0);
			if (flagCell == null) {
				flagCell = row.createCell(0);
			}
			if ((flagCell.getStringCellValue() != null && flagCell
					.getStringCellValue().equalsIgnoreCase("Y"))) {
				System.out.println("跳过 -- "
						+ row.getCell(1).getStringCellValue());
				continue;
			}
			// 工种编码 工种名称 制造单位 工艺常用语 工序名称 类型 文件夹
			String value1 = getValue(row, 1);
			String value2 = getValue(row, 2);
			String value3 = getValue(row, 3);
			String value4 = getValue(row, 4);
			value1 = value1.trim();
			value2 = value2.trim();
			value3 = value3.trim();
			value4 = value4.trim();
			if (value4 == null || "".equals(value4)
					|| !value4.startsWith("/Default")) {
				value4 = "/Default/工位";
			}

			if (value1 != null && value1.length() > 0) {
				MPMWorkCenter workCenter = MPMResourceUtil.createWorkSpace(
						value1, value2, library, value4, "", "");
				// 制造单位关联
				if (value3 != null && value3.length() > 0) {
					String plants[] = value3.split("\\|");
					for (String strPlant : plants) {
						MPMPlant plant = CreateCommentDataTool_New
								.getPlantByName(strPlant);
						if (plant != null) {
							CreateCommentDataTool_New.createWTPartUsageLink(
									plant,
									(WTPartMaster) workCenter.getMaster());
						}
					}
				}
			}
			flagCell.setCellValue("Y");
		}
	}

	/**
	 * 工种
	 *
	 * @author zhangdong
	 * @date 2013-5-28
	 * @param excelFile
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 *
	 */
	public static void importSkill(Workbook workbook)
			throws FileNotFoundException, IOException, WTPropertyVetoException,
			WTException {
		int startRow = 1;

		WTLibrary library = getLibraryByName("工艺资源库");
		Sheet sheet = workbook.getSheet("工种");
		if(sheet == null) {
			return ;
		}
		int lastRowNum = sheet.getLastRowNum();
		System.out.println("----" + lastRowNum);
		for (int i = startRow; i <= lastRowNum; i++) {
			Row row = sheet.getRow(i);
			Cell flagCell = row.getCell(0);
			if (flagCell == null) {
				flagCell = row.createCell(0);
			}
			if ((flagCell.getStringCellValue() != null && flagCell
					.getStringCellValue().equalsIgnoreCase("Y"))) {
				System.out.println("跳过 -- "+getValue(row, 1));
				continue;
			}
			String value1 = getValue(row, 1);// 工种编码
			String value2 = getValue(row, 2);// 工种名称
			String value3 = getValue(row, 3);// 工序名称
			String value4 = getValue(row, 4);// 艺常用语

			value1 = value1.trim();
			value2 = value2.trim();
			value3 = value3.trim();
			value4 = value4.trim();
			if (value4 == null || "".equals(value4)
					|| !value4.startsWith("/Default")) {
				value4 = "/Default/工种";
			}

			if (value1 != null && value1.length() > 0) {
				MPMSkill cs = MPMResourceUtil.getMPMSkill(value1);
				if (cs == null) {
					cs = MPMResourceUtil.createSkill(value1, value2, library,
							value4, "", "");
				}

				// 制造单位关联
				if (value3 != null && value3.length() > 0) {
					String plants[] = value3.split("\\|");
					for (String strPlant : plants) {
						MPMPlant plant = CreateCommentDataTool_New
								.getPlantByName(strPlant);
						if (plant != null) {
							MPMResourceUtil.createWTPartUsageLink(plant,
									(WTPartMaster) cs.getMaster());
						}
					}
				}
			}
			flagCell.setCellValue("Y");
		}
	}

	/**
	 * 制造单位
	 *
	 * @author zhangdong
	 * @date 2013-5-28
	 * @param excelFile
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 *
	 */
	public static void importPlant(Workbook workbook)
			throws FileNotFoundException, IOException, WTException,
			WTPropertyVetoException {
		int startRow = 1;

		System.out.println("import plants -----");
		WTLibrary library = getLibraryByName("工艺资源库");
		Sheet sheet = workbook.getSheet("制造单位");
		if(sheet == null) {
			return ;
		}
		int lastRowNum = sheet.getLastRowNum();
		for (int i = startRow; i <= lastRowNum; i++) {
			Row row = sheet.getRow(i);
			Cell flagCell = row.getCell(0);
			if (flagCell == null) {
				flagCell = row.createCell(0);
			}
			if ((flagCell.getStringCellValue() != null && flagCell
					.getStringCellValue().equalsIgnoreCase("Y"))) {
				System.out.println("跳过 -- " + getValue(row, 1));
				continue;
			}

			String value1 = getValue(row, 1);
			String value2 = getValue(row, 2);
			String folderPath = getValue(row, 3);

			value1 = value1.trim();
			value2 = value2.trim();
			folderPath = folderPath.trim();
			if (folderPath == null || "".equals(folderPath)
					|| !folderPath.startsWith("/Default")) {
				folderPath = "/Default/制造单位";
			}

			if (value1 != null && value1.length() > 0) {
				MPMPlant plant = MPMResourceUtil.createPlant(value1, value2,
						library, folderPath, "", "");
			}
			flagCell.setCellValue("Y");
		}
	}

	/**
	 * 工序名称
	 *
	 * @author zhangdong
	 * @date 2013-5-28
	 * @param excelFile
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 *
	 */
	public static void importProceduce(Workbook workbook)
			throws FileNotFoundException, IOException, WTPropertyVetoException,
			WTException {
		int startRow = 1;

		System.out.println("import 工序名称-----");
		WTLibrary library = getLibraryByName("工艺资源库");
		Sheet sheet = workbook.getSheet("工序名称");
		if(sheet == null) {
			return ;
		}
		int lastRowNum = sheet.getLastRowNum();
		for (int i = startRow; i <= lastRowNum; i++) {
			Row row = sheet.getRow(i);
			Cell flagCell = row.getCell(0);
			if (flagCell == null) {
				flagCell = row.createCell(0);
			}
			if ((flagCell.getStringCellValue() != null && flagCell.getStringCellValue().equalsIgnoreCase("Y"))) {
				System.out.println("跳过 -- " + getValue(row, 1));
				continue;
			}
			String value1 = getValue(row, 1);
			String value2 = getValue(row, 2);
			String remark = getValue(row, 3);
			String value4 = getValue(row, 4);
			String value5 = getValue(row, 5);
			value1 = value1.trim();
			value2 = value2.trim();
			value4 = value4.trim();
			value5 = value5.trim();
			if (value4 == null || "".equals(value4)
					|| !value4.startsWith("/Default")) {
				value4 = "/Default/工序名称";
			}
			String type = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.ProceduceName";
			if (value1 != null && value1.length() > 0) {
				MPMTooling tooling = MPMResourceUtil.getMPMToolingByName(value2, type);
				if (tooling == null) {
					tooling = MPMResourceUtil.createTooling(value1, value2, library, value4, type, "");
				}

				if(tooling!=null){

					IBAUtility iba = new IBAUtility(tooling);
					try {
						iba.setIBAValue("REMARK", remark.trim());
						iba.setIBAValue("GSLX", value5);
						tooling = (MPMTooling) iba.updateAttributeContainer(tooling);
						iba.updateIBAHolder(tooling);
					} catch (WTPropertyVetoException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

			flagCell.setCellValue("Y");
		}

	}

	/**
	 * 工艺常用语
	 *
	 * @author zhangdong
	 * @date 2013-5-28
	 * @param excelFile
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 *
	 */
	public static void importCommonString(Workbook workbook)
			throws FileNotFoundException, IOException, WTPropertyVetoException,
			WTException {
		int startRow = 1;

		System.out.println("import 工艺常用语-----");
		WTLibrary library = getLibraryByName("工艺资源库");
		Sheet sheet = workbook.getSheet("工艺常用语");
		if(sheet == null) {
			return ;
		}
		int lastRowNum = sheet.getLastRowNum();
		System.out.println(lastRowNum);
		for (int i = startRow; i <= lastRowNum; i++) {
			Row row = sheet.getRow(i);
			Cell flagCell = row.getCell(0);
			if (flagCell == null) {
				flagCell = row.createCell(0);
			}
			if ((flagCell.getStringCellValue() != null && flagCell
					.getStringCellValue().equalsIgnoreCase("Y"))) {
				System.out.println("跳过 -- " + getValue(row, 1));
				continue;
			}

			String value1 = getValue(row, 1);
			String value2 = getValue(row, 2);
			String value3 = getValue(row, 3);
			String value4 = getValue(row, 4);
			value1 = value1.trim();
			value2 = value2.trim();
			value3 = value3.trim();
			value4 = value4.trim();
			if (value4 == null || "".equals(value4)
					|| !value4.startsWith("/Default")) {
				value4 = "/Default/工艺常用语";
			}

			String type = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.CommonString";
			if (value1 != null && value1.length() > 0) {
				MPMTooling cs = MPMResourceUtil.createTooling(value1, value2,
						library, value4, type, "");
				if (value3 != null && !"".equals(value3)) {
					if (value3.contains("|")) {
						String[] names = value3.split("\\|");
						for (String name : names) {
							MPMSkill skill = MPMResourceUtil
									.getMPMSkillByName(name);
							if (skill != null) {
								MPMResourceUtil.createWTPartUsageLink(skill,
										(WTPartMaster) cs.getMaster());
							}
						}
					} else {
						MPMSkill skill = MPMResourceUtil
								.getMPMSkillByName(value3);
						if (skill != null) {
							MPMResourceUtil.createWTPartUsageLink(skill,
									(WTPartMaster) cs.getMaster());
						}
					}
				}
			}
			flagCell.setCellValue("Y");
		}
	}

	/**
	 * 生产资源导入结束
	 */

	/**
	 *
	 * 工装资源导入开始点
	 *
	 *
	 */

	/**
	 * 导入 工装资源 改变了
	 *
	 * @author zhangdong
	 * @date 2013-5-30
	 * @param excelFile
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 *
	 */
	public static void importForck(Workbook workbook)
			throws FileNotFoundException, IOException, WTException,
			WTPropertyVetoException {
		String folder = "/Default/工装";
		int startRow = 1;
		int startColumn = 1;
		String objectType = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Frock";

		WTLibrary library = getLibraryByName("工艺资源库");
		Sheet sheet = workbook.getSheet("工装资源");
		int lastRowNum = sheet.getLastRowNum();
		System.out.println(lastRowNum);
		for (int i = startRow; i <= lastRowNum; i++) {
			Row row = sheet.getRow(i);
			Cell flagCell = row.getCell(0);
			if (flagCell == null) {
				flagCell = row.createCell(0);
			}

			if ((flagCell.getStringCellValue() != null && flagCell
					.getStringCellValue().equalsIgnoreCase("Y"))) {
				row.getCell(1).getStringCellValue();
				System.out.println("跳过 -- " + getValue(row, 1));
				continue;
			}
			// 工装编号 工装名称 用于制件的产品代号 用于制件的图号 适用工艺专业 工装卡申请人 工装申请日期 工装库存 常用工装
			// 使用部门

			// String value1 = row.getCell(1).getCellType() == 0 ? new
			// Integer((int) row.getCell(1).getNumericCellValue()).toString() :
			// row.getCell(1).getStringCellValue();// 编号
			// String value2 = row.getCell(2).getStringCellValue();// 工装名称
			// String value3 = row.getCell(3).getCellType() == 0 ? new
			// Integer((int) row.getCell(3).getNumericCellValue()).toString() :
			// row.getCell(3).getStringCellValue(); // 用于制件的产品代号
			// String value4 = row.getCell(4).getCellType() == 0 ? new
			// Integer((int) row.getCell(4).getNumericCellValue()).toString() :
			// row.getCell(4).getStringCellValue(); // 用于制件的图号
			// String value5 = row.getCell(5).getCellType() == 0 ? new
			// Integer((int) row.getCell(5).getNumericCellValue()).toString() :
			// row.getCell(5).getStringCellValue(); // 适用工艺专业
			// String value6 = row.getCell(6).getCellType() == 0 ? new
			// Integer((int) row.getCell(6).getNumericCellValue()).toString() :
			// row.getCell(6).getStringCellValue(); // 工装卡申请人
			// String value7 = row.getCell(7).getCellType() == 0 ? new
			// Integer((int) row.getCell(7).getNumericCellValue()).toString() :
			// row.getCell(7).getStringCellValue(); // 工装申请日期
			// String value8 = row.getCell(8).getCellType() == 0 ? new
			// Integer((int) row.getCell(8).getNumericCellValue()).toString() :
			// row.getCell(8).getStringCellValue(); // 工装库存

			String value1 = getValue(row, 1);
			String value2 = getValue(row, 2);
			String value3 = getValue(row, 3);
			String value4 = getValue(row, 4);
			String value5 = getValue(row, 5);
			String value6 = getValue(row, 6);
			String value7 = getValue(row, 7);
			String value8 = getValue(row, 8);

			value1 = value1.trim();
			value2 = value2.trim();
			value3 = value3.trim();
			value4 = value4.trim();
			value5 = value5.trim();
			value6 = value6.trim();
			value7 = value7.trim();
			value8 = value8.trim();

			MPMTooling frock = null;
			System.out.println(value1 + "   ,   " + value2);

			if (value1 != null && value1.length() > 0 && value2 != null
					&& value2.length() > 0) {
				String number = "G" + value1;
				frock = MPMResourceUtil.createTooling(number, value2, library,
						folder, objectType, "");
				setMyIBAStringValue(frock, "gzCardNumber",
						"G" + value1.replace("-", "."));
				setMyIBAStringValue(frock, "applicant", value3);
				setMyIBAStringValue(frock, "applyDate", value4);
				setMyIBAStringValue(frock, "usageOrg", value5);
				setMyIBAStringValue(frock, "partNumber", value6);
				setMyIBAStringValue(frock, "productNumber", value7);
				setMyIBAStringValue(frock, "remark", value8);
			}

			// 和使用单位建立关联
			// makeUsingLinkWithPlant(value10, frock);

			// TODO 将属性设置上
			// currentEqp.setAttribute();
			flagCell.setCellValue("Y");

		}

	}

	public static void importZYForck(Workbook workbook)
			throws FileNotFoundException, IOException, WTException,
			WTPropertyVetoException {

		int startRow = 1;
		int startColumn = 1;
		String objectType = com.glaway.mpm.mpmresource.TypeNameConstants.GZhuang;

		WTLibrary library = getLibraryByName("工艺资源库");
		Sheet sheet = workbook.getSheet("工装");
		if(sheet == null) {
			return ;
		}
		int lastRowNum = sheet.getLastRowNum();
		System.out.println(lastRowNum);
		for (int i = startRow; i <= lastRowNum; i++) {
			Row row = sheet.getRow(i);
			Cell flagCell = row.getCell(0);
			if (flagCell == null) {
				flagCell = row.createCell(0);
			}

			if ((flagCell.getStringCellValue() != null && flagCell
					.getStringCellValue().equalsIgnoreCase("Y"))) {
				row.getCell(1).getStringCellValue();
				System.out.println("跳过 -- " + getValue(row, 1));
				continue;
			}

			String value1 = getValue(row, 1);
			String value2 = getValue(row, 2);
			String value3 = getValue(row, 3);
			String value4 = getValue(row, 4);

			value1 = value1.trim();
			value2 = value2.trim();
			value3 = value3.trim();
			value4 = value4.trim();

			String folder = "/Default/工装";
			if (value4 != null && !"".equals(value4) && value4.startsWith("/Default")) {
				folder = value4;
			}
			MPMTooling frock = null;
			System.out.println(value1 + "   ,   " + value2 + "   ,   " + value3);

			if (value1 != null && value1.length() > 0 && value2 != null && value2.length() > 0) {
				frock = MPMResourceUtil.createTooling(value1, value2, library, folder, objectType, "");
				if(frock!=null){
					Map<String, String> ibaMap = new HashMap<String, String>();
					ibaMap.put("FROCKTYPE", value3);
					System.out.println("---------ibaMap----" + ibaMap);
					IBAHelper helper = new IBAHelper(frock);
			        helper.setIBAValue(frock, ibaMap);
				}
			}

			flagCell.setCellValue("Y");
		}

	}

	public static void importTYForck(Workbook workbook)
			throws FileNotFoundException, IOException, WTException,
			WTPropertyVetoException {

		int startRow = 1;
		int startColumn = 1;
		String objectType = com.glaway.mpm.mpmresource.TypeNameConstants.CommonGZhuang;

		WTLibrary library = getLibraryByName("工艺资源库");
		Sheet sheet = workbook.getSheet("通用工装");
		int lastRowNum = sheet.getLastRowNum();
		System.out.println(lastRowNum);
		for (int i = startRow; i <= lastRowNum; i++) {
			Row row = sheet.getRow(i);
			Cell flagCell = row.getCell(0);
			if (flagCell == null) {
				flagCell = row.createCell(0);
			}

			if ((flagCell.getStringCellValue() != null && flagCell
					.getStringCellValue().equalsIgnoreCase("Y"))) {
				row.getCell(1).getStringCellValue();
				System.out.println("跳过 -- " + getValue(row, 1));
				continue;
			}

			String value1 = getValue(row, 1);
			String value2 = getValue(row, 2);
			String value3 = getValue(row, 3);
			String value4 = getValue(row, 4);
			String value5 = getValue(row, 5);
			String value6 = getValue(row, 6);
			String value7 = getValue(row, 7);
			String value8 = getValue(row, 8);
			String value9 = getValue(row, 9);
			String value10 = getValue(row, 10);
			String value11 = getValue(row, 11);
			String value12 = getValue(row, 12);
			String value13 = getValue(row, 13);

			value1 = value1.trim();
			value2 = value2.trim();
			value3 = value3.trim();
			value4 = value4.trim();
			value5 = value5.trim();
			value6 = value6.trim();
			value7 = value7.trim();
			value8 = value8.trim();
			value9 = value9.trim();
			value10 = value10.trim();
			value11 = value11.trim();
			value12 = value12.trim();
			value13 = value13.trim();
			String folder = "/Default/工装/通用工装";
			if (value13 != null && !"".equals(value13) && value13.startsWith("/Default")) {
				folder = value13;
			}
			MPMTooling frock = null;

			if (value1 != null && value1.length() > 0 && value2 != null
					&& value2.length() > 0) {
				frock = MPMResourceUtil.createTooling(value1, value2, library, folder, objectType, "");
				setMyIBAStringValue(frock, "TYPENO", value3);
				setMyIBAStringValue(frock, "PINDEX", value4);
				setMyIBAStringValue(frock, "CSIZE", value5);
				setMyIBAStringValue(frock, "LEVEL", value6);
				setMyIBAStringValue(frock, "FACTORY", value7);
				setMyIBAStringValue(frock, "RESOURCE", value8);
				setMyIBAStringValue(frock, "MANUNO", value9);
				setMyIBAStringValue(frock, "MANAGESTATUS", value10);
				setMyIBAStringValue(frock, "QUALITYSTATUS", value11);
				setMyIBAStringValue(frock, "MANAGELEVEL", value12);
			}

			flagCell.setCellValue("Y");

		}

	}

	// private static MPMTooling getMPMToolingByNumber(String number)
	// {
	// MPMTooling mpmTooling = null;
	// QuerySpec querySpec = new QuerySpec(MPMTooling.class);
	// querySpec.appendWhere(new SearchCondition(MPMTooling.class,
	// WTPartHelper.))
	// }

	/**
	 * 工装资源和使用部门之间建立关联关系
	 *
	 * @author zhangdong
	 * @date 2013-5-30
	 * @param plantString
	 * @param tooling
	 * @throws WTException
	 *
	 */
	private static void makeUsingLinkWithPlant(String plantString,
			MPMTooling tooling) throws WTException {
		if (plantString != null && plantString.length() > 0) {
			String plants[] = plantString.split("\\|");
			for (String plant : plants) {
				MPMPlant mpmPlant = CreateCommentDataTool_New
						.getPlantByName(plant);
				if (mpmPlant != null) {
					CreateCommentDataTool_New.createWTPartUsageLink(mpmPlant,
							(WTPartMaster) tooling.getMaster());
				}
			}
		}
	}

	/**
	 * 导入 刀具
	 *
	 * @author zhangdong
	 * @date 2013-5-30
	 * @param excelFile
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 *
	 */
	public static void importKnife(Workbook workbook)
			throws FileNotFoundException, IOException, WTPropertyVetoException,
			WTException {

		int startRow = 1;
		int startColumn = 1;
		String objectType = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Knife";

		WTLibrary library = getLibraryByName("工艺资源库");
		Sheet sheet = workbook.getSheet("刀具");
		if(sheet == null) {
			return ;
		}
		int lastRowNum = sheet.getLastRowNum();
		System.out.println(lastRowNum);
		for (int i = startRow; i <= lastRowNum; i++) {
			Row row = sheet.getRow(i);

			Cell flagCell = row.getCell(0);
			if (flagCell == null) {
				flagCell = row.createCell(0);
			}
			if ((flagCell.getStringCellValue() != null && flagCell
					.getStringCellValue().equalsIgnoreCase("Y"))) {
				System.out.println("跳过 -- " + getValue(row, 1));
				continue;
			}

			String number = getValue(row, 1);
			String name = getValue(row, 2);
			String cmat = getValue(row, 3);
			String rkzj = getValue(row, 4);
			String jczj = getValue(row, 5);
			String rkcd = getValue(row, 6);
			String zcd = getValue(row, 7);
			String gc = getValue(row, 8);
			String zxjgcc = getValue(row, 9);
			String zdjgcc = getValue(row, 10);
			String jgxs = getValue(row, 11);
			String jklx = getValue(row, 12);
			String jsbz = getValue(row, 13);
			String rkyjbj = getValue(row, 14);
			String cs = getValue(row, 15);
			String knifetype = getValue(row, 16);
			String folderpath = getValue(row, 17);

			String folder = "/Default/刀具";
			if (folderpath != null && !"".equals(folderpath) && folderpath.startsWith("/Default")) {
				folder = folderpath;
			}

			System.out.println(number + "," + name);
			MPMTooling knife = null;

			if (number != null && number.length() > 0) {
				knife = MPMResourceUtil.createTooling(number, name, library, folder, objectType, "");
			}

			Map<String, String> ibaMap = new HashMap<String, String>();
			ibaMap.put("CMAT", cmat);
			ibaMap.put("RKZJ", rkzj);
			ibaMap.put("JCZJ", jczj);
			ibaMap.put("RKCD", rkcd);
			ibaMap.put("ZCD", zcd);
			ibaMap.put("GC", gc);
			ibaMap.put("ZXJGCC", zxjgcc);
			ibaMap.put("ZDJGCC", zdjgcc);
			ibaMap.put("JGXS", jgxs);
			ibaMap.put("JKLX", jklx);
			ibaMap.put("JSBZ", jsbz);
			ibaMap.put("RKYJBJ", rkyjbj);
			ibaMap.put("CS", cs);
			ibaMap.put("KNIFETYPE", knifetype);

			IBAHelper helper = new IBAHelper(knife);
	        helper.setIBAValue(knife, ibaMap);

			flagCell.setCellValue("Y");
		}

	}

	/**
	 * 导入 工量具 改变了
	 *
	 * @author zhangdong
	 * @date 2013-5-30
	 * @param excelFile
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 *
	 */
	public static void importMeasures(Workbook workbook)
			throws FileNotFoundException, IOException, WTPropertyVetoException,
			WTException {

		int startRow = 1;
		int startColumn = 1;
		String objectType = com.glaway.mpm.mpmresource.TypeNameConstants.LJ;

		WTLibrary library = getLibraryByName("工艺资源库");
		Sheet sheet = workbook.getSheet("量具");
		if(sheet == null) {
			return ;
		}
		int lastRowNum = sheet.getLastRowNum();
		System.out.println(lastRowNum);
		for (int i = startRow; i <= lastRowNum; i++) {
			Row row = sheet.getRow(i);

			Cell flagCell = row.getCell(0);
			if (flagCell == null) {
				flagCell = row.createCell(0);
			}
			if ((flagCell.getStringCellValue() != null && flagCell
					.getStringCellValue().equalsIgnoreCase("Y"))) {
				System.out.println("跳过 -- " + getValue(row, 1));
				continue;
			}

			String value1 = getValue(row, 1);
			String value2 = getValue(row, 2);
			String value3 = getValue(row, 3);
			String value4 = getValue(row, 4);
			String value5 = getValue(row, 5);

			value1 = value1.trim();
			value2 = value2.trim();
			value3 = value3.trim();
			value4 = value4.trim();
			value5 = value5.trim();
			String folder = "/Default/量具";
			if (value5 != null && !"".equals(value5) && value5.startsWith("/Default")) {
				folder = value5;
			}

			MPMTooling measure = null;

			if (value1 != null && value1.length() > 0) {
				measure = MPMResourceUtil.createTooling(value1, value2, library, folder, objectType, "");

			}

			Map<String, String> ibaMap = new HashMap<String, String>();
			ibaMap.put("CSIZE", value3);
			ibaMap.put("MINDEX", value4);

			IBAHelper helper = new IBAHelper(measure);
	        helper.setIBAValue(measure, ibaMap);

			flagCell.setCellValue("Y");
		}

	}

	public static void importTools(Workbook workbook)
			throws FileNotFoundException, IOException, WTPropertyVetoException,
			WTException {

		int startRow = 1;
		int startColumn = 1;
		String objectType = com.glaway.mpm.mpmresource.TypeNameConstants.GJ;

		WTLibrary library = getLibraryByName("工艺资源库");
		Sheet sheet = workbook.getSheet("工具");
		if(sheet == null) {
			return ;
		}
		int lastRowNum = sheet.getLastRowNum();
		System.out.println(lastRowNum);
		for (int i = startRow; i <= lastRowNum; i++) {
			Row row = sheet.getRow(i);

			Cell flagCell = row.getCell(0);
			if (flagCell == null) {
				flagCell = row.createCell(0);
			}
			if ((flagCell.getStringCellValue() != null && flagCell
					.getStringCellValue().equalsIgnoreCase("Y"))) {
				System.out.println("跳过 -- " + getValue(row, 1));
				continue;
			}

			String value1 = getValue(row, 1);
			String value2 = getValue(row, 2);
			String value3 = getValue(row, 3);
			String value4 = getValue(row, 4);
			String value5 = getValue(row, 5);

			value1 = value1.trim();
			value2 = value2.trim();
			value3 = value3.trim();
			value4 = value4.trim();
			value5 = value5.trim();
			String folder = "/Default/工具";
			if (value5 != null && !"".equals(value5) && value5.startsWith("/Default")) {
				folder = value5;
			}

			MPMTooling measure = null;

			if (value1 != null && value1.length() > 0) {
				measure = MPMResourceUtil.createTooling(value1, value2, library, folder, objectType, "");
			}

			Map<String, String> ibaMap = new HashMap<String, String>();
			ibaMap.put("CSIZE", value3);
			ibaMap.put("MINDEX", value4);

			IBAHelper helper = new IBAHelper(measure);
	        helper.setIBAValue(measure, ibaMap);

			flagCell.setCellValue("Y");
		}

	}

	public static void importEquipments(Workbook workbook)
			throws FileNotFoundException, IOException, WTPropertyVetoException,
			WTException {

		int startRow = 1;
		int startColumn = 1;
		String objectType = com.glaway.mpm.mpmresource.TypeNameConstants.SB;

		WTLibrary library = getLibraryByName("工艺资源库");
		Sheet sheet = workbook.getSheet("设备");
		if(sheet == null) {
			return ;
		}
		int lastRowNum = sheet.getLastRowNum();
		System.out.println(lastRowNum);
		for (int i = startRow; i <= lastRowNum; i++) {
			Row row = sheet.getRow(i);

			Cell flagCell = row.getCell(0);
			if (flagCell == null) {
				flagCell = row.createCell(0);
			}
			if ((flagCell.getStringCellValue() != null && flagCell
					.getStringCellValue().equalsIgnoreCase("Y"))) {
				System.out.println("跳过 -- " + getValue(row, 1));
				continue;
			}

			String value1 = getValue(row, 1);
			String value2 = getValue(row, 2);
			String value3 = getValue(row, 3);
			String value4 = getValue(row, 4);
			String value5 = getValue(row, 5);
			String value6 = getValue(row, 6);

			value1 = value1.trim();
			value2 = value2.trim();
			value3 = value3.trim();
			value4 = value4.trim();
			value5 = value5.trim();
			value6 = value6.trim();
			String folder = "/Default/设备";
			if (value6 != null && !"".equals(value6) && value6.startsWith("/Default")) {
				folder = value6;
			}

			MPMTooling sb = null;

			if (value1 != null && value1.length() > 0) {
				sb = MPMResourceUtil.createTooling(value1, value2, library, folder, objectType, "");
				setMyIBAStringValue(sb, "EQUIPMENTTYPE", value3);
				setMyIBAStringValue(sb, "CSIZE", value4);
				setMyIBAStringValue(sb, "MINDEX", value5);
			}

			flagCell.setCellValue("Y");
		}

	}

	/**
	 * 导入 仪器仪表
	 *
	 * @author zhangdong
	 * @date 2013-5-30
	 * @param excelFile
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 *
	 */
	public static void importDashboards(Workbook workbook)
			throws FileNotFoundException, IOException, WTPropertyVetoException,
			WTException {

		int startRow = 1;
		int startColumn = 1;
		String objectType = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Dashboard";

		WTLibrary library = getLibraryByName("工艺资源库");
		Sheet sheet = workbook.getSheet("仪器仪表");
		if(sheet == null) {
			return ;
		}
		int lastRowNum = sheet.getLastRowNum();
		System.out.println(lastRowNum);
		for (int i = startRow; i <= lastRowNum; i++) {
			Row row = sheet.getRow(i);

			Cell flagCell = row.getCell(0);
			if (flagCell == null) {
				flagCell = row.createCell(0);
			}
			if ((flagCell.getStringCellValue() != null && flagCell
					.getStringCellValue().equalsIgnoreCase("Y"))) {
				System.out.println("跳过 -- " + getValue(row, 1));
				continue;
			}

			String value1 = getValue(row, 1);
			String value2 = getValue(row, 2);
			String value3 = getValue(row, 3);
			String value4 = getValue(row, 4);
			String value5 = getValue(row, 5);

			String folder = "/Default/仪器仪表";
			if (value5 != null && !"".equals(value5) && value5.startsWith("/Default")) {
				folder = value5;
			}

			System.out.println(value1 + "," + value1 + "," + value2);
			MPMTooling dashboard = null;

			if (value1 != null && value1.length() > 0) {
				dashboard = MPMResourceUtil.createTooling(value1, value2, library,
						folder, objectType, "");
			}

			Map<String, String> ibaMap = new HashMap<String, String>();
			ibaMap.put("CSIZE", value3);
			ibaMap.put("MINDEX", value4);

			IBAHelper helper = new IBAHelper(dashboard);
	        helper.setIBAValue(dashboard, ibaMap);

			flagCell.setCellValue("Y");
		}

	}

	public static Map<String, String> getMaterialFolderName() throws FileNotFoundException, IOException {
		File resourceFile = new File(ImportMPMResourceTool.sortmaterialFilePath);
		Workbook workbook = new HSSFWorkbook(new FileInputStream(resourceFile));
		Sheet sheet = workbook.getSheet("材料分类");
		Map<String, String> folderMap = new HashMap<String, String>();
		int startRow = 1;
		int startColumn = 1;
		int lastRowNum = sheet.getLastRowNum();
		for (int i = startRow; i <= lastRowNum; i++) {
			Row row = sheet.getRow(i);


		}
		return folderMap;
	}

	public static void importMPMProcessMaterials(Workbook workbook)
			throws FileNotFoundException, IOException, WTPropertyVetoException,
			WTException {

		int startRow = 1;
		int startColumn = 1;
		String objectType = com.glaway.mpm.mpmresource.TypeNameConstants.GYFL;

		WTLibrary library = getLibraryByName("工艺资源库");
		Sheet sheet = workbook.getSheet("工艺辅料");
		if(sheet == null) {
			return ;
		}
		int lastRowNum = sheet.getLastRowNum();
		System.out.println(lastRowNum);
		for (int i = startRow; i <= lastRowNum; i++) {
			Row row = sheet.getRow(i);

			Cell flagCell = row.getCell(0);
			if (flagCell == null) {
				flagCell = row.createCell(0);
			}
			if ((flagCell.getStringCellValue() != null && flagCell
					.getStringCellValue().equalsIgnoreCase("Y"))) {
				System.out.println("跳过 -- " + getValue(row, 1));
				continue;
			}

			String number = getValue(row, 1);
			String name = getValue(row, 2);
			String csize = getValue(row, 3);
			String mindex = getValue(row, 4);
			String jldw = getValue(row, 5);
			String jstj = getValue(row, 6);
			String fjtj = getValue(row, 7);
			String folderpath = getValue(row, 8);

			String folder = "/Default/工艺辅料";
			if (folderpath != null && !"".equals(folderpath) && folderpath.startsWith("/Default")) {
				folder = folderpath;
			}

			MPMProcessMaterial processMaterial = null;

			if (number != null && number.length() > 0) {
				processMaterial = MPMResourceUtil.createProcessMaterial(number, name, library, folder, objectType, "");
			}

			Map<String, String> ibaMap = new HashMap<String, String>();
			ibaMap.put("CSIZE", csize);
			ibaMap.put("MINDEX", mindex);
			ibaMap.put("JLDW", jldw);
			ibaMap.put("JSTJ", jstj);
			ibaMap.put("FJTJ", fjtj);

			IBAHelper helper = new IBAHelper(processMaterial);
	        helper.setIBAValue(processMaterial, ibaMap);

			flagCell.setCellValue("Y");
		}

	}

	/**
	 * 导入 原材料
	 *
	 * @author zhangdong
	 * @date 2013-5-30
	 * @param excelFile
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 *
	 */
	public static void importMaterials(Workbook workbook)
			throws FileNotFoundException, IOException, WTException,
			WTPropertyVetoException {
		String folder = "/Default/原材料";
		int startRow = 1;
		int startColumn = 1;
		String objectType = "com.ptc.windchill.mpml.resource.MPMProcessMaterial|casc.sast.149.Material";
		WTLibrary library = getLibraryByName("工艺资源库");
		Sheet sheet = workbook.getSheet("原材料");
		int lastRowNum = sheet.getLastRowNum();
		System.out.println(lastRowNum);
		Map<String, String> folderMap = getMaterialFolderName();
		Set<MaterialObject> materialSet = new HashSet<MaterialObject>();
		/**
		 * 1 物资编号 2 名称 3 牌号（型号） 4 规格 5 型号、规格 6 材料密度 7 材料损耗系数 8 定额单位 9 标准号 10
		 * 存货分类编码 11 存货分类名称（类型）
		 */
		for (int i = startRow; i <= lastRowNum; i++) {
			folder = "/Default/工艺辅料";
			Row row = sheet.getRow(i);
			Cell flagCell = row.getCell(0);
			if (flagCell == null) {
				flagCell = row.createCell(0);
			}
			if ((flagCell.getStringCellValue() != null && flagCell
					.getStringCellValue().equalsIgnoreCase("Y"))) {
				System.out.println("跳过 -- " + getValue(row, 1));
				continue;
			}
			// 物资编码 名称 材料牌号 材料标准号 材料类型 材料密度 材料损耗系数 定额单位

			// String value1 = row.getCell(1) == null ? "" :
			// (row.getCell(1).getCellType() == 0 ? new Long((long)
			// row.getCell(1).getNumericCellValue()).toString() :
			// row.getCell(1).getStringCellValue()); //分类编号
			// String value2 = row.getCell(2) == null ? "" :
			// (row.getCell(2).getCellType() == 0 ? new Integer((int)
			// row.getCell(2).getNumericCellValue()).toString() :
			// row.getCell(2).getStringCellValue()); // 分类名称
			String value3 = row.getCell(3) == null ? "" : (row.getCell(3)
					.getCellType() == 0 ? new Integer((int) row.getCell(3)
					.getNumericCellValue()).toString() : row.getCell(3)
					.getStringCellValue()); // 材料牌号
			String value4 = row.getCell(4) == null ? "" : (row.getCell(4)
					.getCellType() == 0 ? new Integer((int) row.getCell(4)
					.getNumericCellValue()).toString() : row.getCell(4)
					.getStringCellValue()); // 规格
			String value5 = row.getCell(5) == null ? "" : (row.getCell(5)
					.getCellType() == 0 ? new Integer((int) row.getCell(5)
					.getNumericCellValue()).toString() : row.getCell(5)
					.getStringCellValue()); // 型号
											// 规格
			Double value6 = (Double) (row.getCell(6) == null ? 0.0D : (row
					.getCell(6).getCellType() == 0 ? new Double((double) row
					.getCell(6).getNumericCellValue()) : 0.0D)); // 材料密度
			Double value7 = (Double) (row.getCell(7) == null ? 0.0D : (row
					.getCell(7).getCellType() == 0 ? new Double((double) row
					.getCell(7).getNumericCellValue()) : 0.0D)); // 材料损耗系数
			String value8 = row.getCell(8) == null ? "" : (row.getCell(8)
					.getCellType() == 0 ? new Integer((int) row.getCell(8)
					.getNumericCellValue()).toString() : row.getCell(8)
					.getStringCellValue()); // 定额单位
			String value9 = row.getCell(9) == null ? "" : (row.getCell(9)
					.getCellType() == 0 ? new Long((int) row.getCell(9)
					.getNumericCellValue()).toString() : row.getCell(9)
					.getStringCellValue()); // 标准号
			String value10 = row.getCell(10) == null ? "" : (row.getCell(10)
					.getCellType() == 0 ? new Long((long) row.getCell(10)
					.getNumericCellValue()).toString() : row.getCell(10)
					.getStringCellValue()); // 存货分类编码
			String value11 = row.getCell(11) == null ? "" : (row.getCell(11)
					.getCellType() == 0 ? new Long((int) row.getCell(11)
					.getNumericCellValue()).toString() : row.getCell(11)
					.getStringCellValue()); // 存货分类名称（类型）
			System.out.println("double value6-----" + value6.toString());

			String value1 = row.getCell(1) == null ? "" : row.getCell(1)
					.getStringCellValue();// 物资编码
			String value2 = row.getCell(2) == null ? "" : row.getCell(2)
					.getStringCellValue();// 名称
			// String value3 = row.getCell(3).getStringCellValue(); // 材料牌号
			// String value4 = row.getCell(4).getStringCellValue(); // 材料标准号
			// String value5 = row.getCell(5).getStringCellValue(); // 材料类型
			// String value6 = row.getCell(6).getStringCellValue(); // 材料密度
			// String value7 = row.getCell(7).getStringCellValue(); // 材料损耗系数
			// String value8 = row.getCell(8).getStringCellValue(); // 定额单位
			value1 = value1.trim();
			value2 = value2.trim();
			value3 = value3.trim();
			value4 = value4.trim();
			value5 = value5.trim();

			value8 = value8.trim();
			value9 = value9.trim();
			value10 = value10.trim();
			value11 = value11.trim();
			// 从第五类里拆分出 型号和规则
			System.out.println("规格 型号---------" + value5);
			int index = value5.indexOf(" ");
			String xingHao = value5;
			String guiGe = "";
			if (index >= 1) {
				xingHao = value5.substring(0, index);
				guiGe = value5.substring(index);
				guiGe = guiGe.trim();
				guiGe = guiGe.replace("≠", "δ");

			}
			if (value10 == null || value10.length() == 0
					|| "null".equals(value10)) {
				folder = folder + "/电子材料";
			} else if (value10.startsWith("12")) {
				folder = folder + "/辅材料";
				String folder2 = value10.substring(0, 4);
				String folderName2 = getFolderName(folderMap, folder2);
				String folder3 = value10.substring(0, 6);
				String folderName3 = getFolderName(folderMap, folder3);
				if (folderName2 != null && folderName2.length() > 0) {
					folder = folder + "/" + folder2 + folderName2;
				}
				if (folderName3 != null && folderName3.length() > 0) {
					folder = folder + "/" + folder3.substring(2) + folderName3;
				}
			} else if (value10.startsWith("11")) {
				folder = folder + "/原材料";
				String folder2 = value10.substring(0, 4);
				String folderName2 = getFolderName(folderMap, folder2);
				String folder3 = value10.substring(0, 6);
				String folderName3 = getFolderName(folderMap, folder3);
				if (folderName2 != null && folderName2.length() > 0) {
					folder = folder + "/" + folder2 + folderName2;
				}
				if (folderName3 != null && folderName3.length() > 0) {
					folder = folder + "/" + folder3.substring(2) + folderName3;
				}
			}
			// MaterialObject currentMaterial = new MaterialObject(value1,
			// value2, Integer.parseInt(value3));
			// addMaterialToSet(materialSet, currentMaterial);
			MPMProcessMaterial material = null;
			if (value1 != null && value1.length() > 0) {
				material = MPMResourceUtil.createProcessMaterial("", value2,
						library, folder, objectType, "");
			}
			setMyIBAStringValue(material, "materialNumber", value1);
			setMyIBAStringValue(material, "materialBrand", xingHao);
			setMyIBAStringValue(material, "materialSpec", guiGe);
			if (value6 > 0.0) {
				IBAHelper.setIBARatioValue(material, "materialDensity", value6);
			}
			if (value7 > 0.0) {
				IBAHelper.setIBARatioValue(material, "attritionRate", value7);
			}
			setMyIBAStringValue(material, "materialUtit", value8);
			setMyIBAStringValue(material, "materialCrision", value9);
			setMyIBAStringValue(material, "materialCategory", value11);
			// TODO 将属性设置上
			// IBAHelper.setIBAStringValue(material, "materialCategory",
			// value5);
			// IBAHelper.setIBAStringValue(material, "materialCrision",
			// str[i][j][3]);
			// IBAHelper.setIBAStringValue(material, "materialCode", );
			// IBAHelper.setIBAStringValue(material, "materialState",
			// str[i][j][5]);
			// IBAHelper.setIBAStringValue(material, "computeType", value5);
			// IBAHelper.setIBAStringValue(material, "materialSpec", value4);
			// IBAHelper.setIBAStringValue(material, "materialQuotiety",
			// value7);
			// IBAHelper.setIBAStringValue(material, "materialDensity", value6);
			// IBAHelper.setIBAStringValue(material, "materialUnit", value8);
			// IBAHelper.setIBAStringValue(material, "materialBrand", value3);
			// currentEqp.setAttribute();
			flagCell.setCellValue("Y");
		}

	}

	private static String getFolderName(Map<String, String> folderMap,
			String folder3) {
		String folderName = folderMap.get(folder3);
		if (folderName != null && folderName.length() > 0) {
			return folderName;
		} else {
			return null;
		}
	}

	private void addMaterialToSet(Set<MaterialObject> materialSet,
			MaterialObject material) {
		Iterator it = materialSet.iterator();
		boolean foundParent = false;
		while (it.hasNext()) {
			MaterialObject aMaterial = (MaterialObject) it.next();
			if (material.getNumber().startsWith(aMaterial.getNumber())
					&& ((material.getLevel() + 1) == aMaterial.getLevel())) {
				aMaterial.addChild(material);
				foundParent = true;
			} else {
				// if()
			}

		}

	}

	//
	// public static void deleteWTPartUsageLink() throws WTException {
	//
	// List<MPMTooling> result = MPMResourceUtil.getAllMPMTooling();
	// for (MPMTooling tooling : result) {
	//
	// QueryResult queryResult = WTPartHelper.service.getUsesWTParts(tooling,
	// WTPartUtil.getConfigSpec());
	// System.out.println(queryResult.hasMoreElements());
	// // for (WTPart childPart : childPartList) {
	// // WTPartUsageLink link = WTPartUtil.getWTPartUsageLink(skill,
	// // (WTPartMaster) childPart.getMaster());
	// // WTPartUtil.deleteWTPartUsageLink(link);
	// // }
	// while (queryResult.hasMoreElements()) {
	// Persistable[] per = (Persistable[]) queryResult.nextElement();
	// WTPartUtil.deleteWTPartUsageLink((WTPartUsageLink) per[0]);
	// }
	// }
	//
	// }

	public static void main(String[] args) throws Exception {

		RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
		methodServer.setUserName("wcadmin");
		methodServer.setPassword(PDMConfig.WCADMIN_PASSWORD);

		// methodServer.invoke("startImport", CLASSNAME, null, new Class[] {},
		// new Object[] {});

		// String index = "1";
		//
		// if (index == null || index.length() == 0) {
		// index = "1";
		// }
		// Integer in = Integer.parseInt(index);
		methodServer.invoke("startImport", CLASSNAME, null, new Class[] {},
				new Object[] {});
		// methodServer.invoke("getFolderCon", CLASSNAME, null, new Class[] {},
		// new Object[] {});

		// new Object[] {});

		// startImport();

	}

	public static void startMaterial() throws FileNotFoundException,
			IOException {

		File resourceFile = new File(ImportMPMResourceTool.resourceFilePath);
		Workbook workbook = new HSSFWorkbook(new FileInputStream(resourceFile));
		try {
			ImportMPMResourceTool.importPlant(workbook);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			workbook.write(new FileOutputStream(new File(
					ImportMPMResourceTool.materialFilePath)));
		}
	}

	/**
	 *
	 * @author zhangdong
	 * @throws Exception
	 * @date 2013-6-1
	 *
	 */

	public static void startImport() throws Exception {

		/**
		 * 导入生产资源
		 */
		File resourceFile = new File(ImportMPMResourceTool.resourceFilePath);
		Workbook workbook = new HSSFWorkbook(new FileInputStream(resourceFile));
		/**
		 * 制造单位 工序名称 工艺常用语 工位 工种
		 */
		try {
			ImportMPMResourceTool.importPlant(workbook); // ok
			ImportMPMResourceTool.importProceduce(workbook); // ok
			ImportMPMResourceTool.importCommonString(workbook); // ok
			ImportMPMResourceTool.importWorkCenter(workbook); // ok
			ImportMPMResourceTool.importSkill(workbook);// ok
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			workbook.write(new FileOutputStream(new File(ImportMPMResourceTool.resourceFilePath)));
		 }

		// /**
		// * 工装资源 刀具 工量具 仪器仪表 原材料
		// */
		File materialFile = new File(ImportMPMResourceTool.forckFilePath);
		workbook = new HSSFWorkbook(new FileInputStream(materialFile));
		try {
			//ImportMPMResourceTool.importTYForck(workbook);
			ImportMPMResourceTool.importZYForck(workbook);
			ImportMPMResourceTool.importKnife(workbook);
			ImportMPMResourceTool.importMeasures(workbook);
			ImportMPMResourceTool.importTools(workbook);
			ImportMPMResourceTool.importDashboards(workbook);

			ImportMPMResourceTool.importDMSBForck(workbook);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			workbook.write(new FileOutputStream(new File(ImportMPMResourceTool.forckFilePath)));
		 }

		/**
		 * 设备
		 */
		File sbFile = new File(ImportMPMResourceTool.eqpFilePath);
		workbook = new HSSFWorkbook(new FileInputStream(sbFile));
		try {
			ImportMPMResourceTool.importEquipments(workbook);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			workbook.write(new FileOutputStream(new File(ImportMPMResourceTool.eqpFilePath)));
		 }

		/**
		 * 工艺辅料
		 */
		File gyflFile = new File(ImportMPMResourceTool.gyflFilePath);
		workbook = new HSSFWorkbook(new FileInputStream(gyflFile));
		try {
			ImportMPMResourceTool.importMPMProcessMaterials(workbook);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			workbook.write(new FileOutputStream(new File(ImportMPMResourceTool.gyflFilePath)));
		 }
	}

	private static void importDMSBForck(Workbook workbook) throws WTException, WTPropertyVetoException, RemoteException {
		int startRow = 1;
		int startColumn = 1;
		String objectType = com.glaway.mpm.mpmresource.TypeNameConstants.DMSB;

		WTLibrary library = getLibraryByName("工艺资源库");
		Sheet sheet = workbook.getSheet("地面设备");
		if(sheet == null) {
			return ;
		}
		int lastRowNum = sheet.getLastRowNum();
		System.out.println(lastRowNum);
		for (int i = startRow; i <= lastRowNum; i++) {
			Row row = sheet.getRow(i);
			Cell flagCell = row.getCell(0);
			if (flagCell == null) {
				flagCell = row.createCell(0);
			}

			if ((flagCell.getStringCellValue() != null && flagCell
					.getStringCellValue().equalsIgnoreCase("Y"))) {
				row.getCell(1).getStringCellValue();
				System.out.println("跳过 -- " + getValue(row, 1));
				continue;
			}

			String value1 = getValue(row, 1);
			String value2 = getValue(row, 2);
			String value3 = getValue(row, 3);
			String value4 = getValue(row, 4);

			value1 = value1.trim();
			value2 = value2.trim();
			value3 = value3.trim();
			value4 = value4.trim();

			String folder = "/Default/地面设备";
			if (value4 != null && !"".equals(value4) && value4.startsWith("/Default")) {
				folder = value4;
			}
			MPMTooling frock = null;
			System.out.println(value1 + "   ,   " + value2 + "   ,   " + value3);

			if (value1 != null && value1.length() > 0 && value2 != null && value2.length() > 0) {
				frock = MPMResourceUtil.createTooling(value1, value2, library, folder, objectType, "");
				//Map<String, String> ibaMap = new HashMap<String, String>();
				//ibaMap.put("FROCKTYPE", value3);
				//System.out.println("---------ibaMap----" + ibaMap);
				//IBAHelper helper = new IBAHelper(frock);
		        //helper.setIBAValue(frock, ibaMap);
			}

			flagCell.setCellValue("Y");
		}


	}

	/**
	 *
	 * @author liuzhaogang
	 * @date 2013-7-6
	 * @param row
	 *            准备取之row
	 * @param index
	 *            用于指定cell
	 * @return cell 值
	 */
	public static String getValue(Row row, int index) {
		Cell cell = row.getCell(index);
		if (cell == null) {
			return "";
		}
		String value = "";
		int flag = cell.getCellType();

		switch (flag) {
		case Cell.CELL_TYPE_STRING:
			value = cell.getStringCellValue();
			break;
		case Cell.CELL_TYPE_NUMERIC:
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				value = cell.getDateCellValue().toString();

			} else {
				value = String.valueOf((long) (cell.getNumericCellValue()))
						.toString();
			}
			break;

		case Cell.CELL_TYPE_BOOLEAN:
			value = String.valueOf(cell.getBooleanCellValue()).toString();
			break;

		default:
			break;
		}
		return value.trim();
	}

	/**
	 * 删除所有关联
	 *
	 * @author qianlong
	 * @date 2013-7-15
	 * @param path
	 * @param library
	 * @throws WTException
	 */
	public static void getFolderCon() throws WTException {
		int i = 0;
		System.out.println("----start get ----");
		WTLibrary library = getLibraryByName("工艺资源库");
		Folder folder = FolderHelper.service.getFolder("/Default/",
				WTContainerRef.newWTContainerRef(library));
		QueryResult qr = FolderHelper.service.findFolderContents(folder);
		// List list = getMPMPlant();
		QueryResult Linkqr = null;
		if (qr != null) {
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (obj instanceof WTPart) {
					WTPart part = (WTPart) obj;
					Linkqr = WTPartHelper.service.getUsesWTParts(part,
							getConfigSpec());
					while (Linkqr.hasMoreElements()) {
						Persistable[] per = (Persistable[]) Linkqr
								.nextElement();
						WTPartUsageLink link = (WTPartUsageLink) per[0];
						WTPart childpart = (WTPart) per[1];
						String type = TypedUtilityServiceHelper.service
								.getTypeIdentifier(childpart).toString();
						if (type != null
								&& type.contains("com.ptc.windchill.mpml.resource.MPMTooling|com.nriet.ProceduceName")) {
							PersistenceServerHelper.manager.remove(link);
							CreateCommentDataTool_New.createWTPartUsageLink(
									childpart, (WTPartMaster) part.getMaster());
							i++;
						}

					}
				}
			}
		}

		System.out.println("---end change---" + i);
	}

	/**
	 * 更改零件类型编号
	 *
	 * @author qianlong
	 * @date 2013-7-16
	 * @param part
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static void changeNum(WTPart part, String number)
			throws WTException, WTPropertyVetoException {
		WTPartMaster master = (WTPartMaster) part.getMaster();
		WTPartMasterIdentity iden = (WTPartMasterIdentity) master
				.getIdentificationObject();
		iden.setNumber(number);
		IdentityHelper.service.changeIdentity(master, iden);
		PersistenceServerHelper.manager.update(part);

	}

	private static List getMPMPlant() throws WTException {
		List<MPMPlant> list = new ArrayList<MPMPlant>();
		WTLibrary library = getLibraryByName("工艺资源库");
		Folder folder = FolderHelper.service.getFolder("/Default/制造单位",
				WTContainerRef.newWTContainerRef(library));
		QueryResult qr = FolderHelper.service.findFolderContents(folder);
		while (qr.hasMoreElements()) {
			MPMPlant plant = (MPMPlant) qr.nextElement();
			list.add(plant);
		}
		return list;
	}

	private static ConfigSpec getConfigSpec() throws WTException {

		return ConfigHelper.service.getDefaultConfigSpecFor(WTPart.class);

	}

	/**
	 * 删除两物件间Link
	 */
	public static void deleteLink(WTPart parent, WTPart child)
			throws WTException {
		WTPartUsageLink link = null;
		link = getLink(parent, child);
		if (link != null) {
			PersistenceServerHelper.manager.remove(link);
		}

	}

	/**
	 * 获得两物件关联
	 *
	 * @author liuzhaogang
	 * @date 2013-7-12
	 * @param parent
	 * @param child
	 * @return link
	 * @throws WTException
	 */

	public static WTPartUsageLink getLink(WTPart parent, WTPart child)
			throws WTException {
		WTPartUsageLink link = null;
		QueryResult qr = WTPartHelper.service.getUsesWTParts(parent,
				ConfigHelper.service.getDefaultConfigSpecFor(WTPart.class));
		while (qr.hasMoreElements()) {
			Persistable[] per = (Persistable[]) qr.nextElement();
			if (per[1].equals(child)) {
				link = (WTPartUsageLink) per[0];
			}
		}
		return link;
	}







}

class MaterialObject {
	private String number;
	private String name;
	private int level;
	private MaterialObject parent = null;
	private Set<MaterialObject> childList = new HashSet<MaterialObject>();

	public MaterialObject(String number, String name, int level) {
		this.number = number;
		this.name = name;
		this.level = level;
	}

	public void addChild(MaterialObject child) {
		this.childList.add(child);
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public MaterialObject getParent() {
		return parent;
	}

	public void setParent(MaterialObject parent) {
		this.parent = parent;
	}

	public Set<MaterialObject> getChildList() {
		return childList;
	}

	public void setChildList(Set<MaterialObject> childList) {
		this.childList = childList;
	}

	public boolean hasChild() {
		if (this.childList == null || this.childList.size() == 0) {
			return false;
		} else {
			return true;
		}
	}

	public boolean hasParent() {
		return !(this.parent == null);
	}
}