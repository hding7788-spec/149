package com.glaway.mpm.mesDataSearch;

import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.mesParameter.MesParameterProcessor;
import com.glaway.mpm.model.TempObject;
import com.glaway.mpm.parameter.GWParameterTableTypeManager;
import com.glaway.mpm.parameter.constants.ParameterConstants;
import com.glaway.mpm.parameter.helper.MPMParameterProcessor;
import com.glaway.mpm.parameter.model.GWOperationToParamTableLink;
import com.glaway.mpm.parameter.model.GWParamTableTypeMaster;
import com.glaway.mpm.parameter.model.GWParameterTableColumn;
import com.glaway.mpm.parameter.model.GWParameterTableType;
import com.glaway.mpm.parameter.model.data.CmParamTableType;
import com.glaway.mpm.parameter.model.data.CmParameterTableColumn;
import com.glaway.mpm.parameter.model.data.CmParameterTablePackage;
import com.glaway.mpm.parameter.service.gwpersistable.GwPersistenceHelper;
import com.glaway.mpm.parameter.service.gwpersistable.GwQueryResult;
import com.glaway.mpm.parameter.service.gwpersistable.GwQuerySpec;
import com.glaway.mpm.util.DBConnUtil;
import com.glaway.mpm.util.IBAHelper;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MesDataSearchProcesser {

	private static VaLogger logger = VaLogger.getLogger(MesDataSearchProcesser.class.getName());
	private static int index[] = { 0 };

	public static List<TempObject> getTechnicsNumberList(String lukahao, String productNumber, String batch, String tuhao){
			List<TempObject> technicsNumberList = new ArrayList<TempObject>();
			DBConnUtil conn = null;
			TempObject tempObj = null;
			try {
				conn= new DBConnUtil();
				String sql = "select DISTINCT TECHNICSNUMBER,LUKAHAO,PRODUCTNUMBER,ISZF from MESCOMMONPARAMTABLE where PRODUCTNUMBER like '%"+ productNumber +"%'";
				ResultSet rs = conn.executeQuery(sql);
				while(rs.next()){
					String rsTechnicsNumber = rs.getString("TECHNICSNUMBER");
					String rsLukahao = rs.getString("LUKAHAO");
					String rsProductNumber = rs.getString("PRODUCTNUMBER");
					String rsIsZF = rs.getString("ISZF");
					tempObj = new TempObject();
					tempObj.setDocNumber(rsTechnicsNumber);
					WTDocument document = getDocumentByNumber(rsTechnicsNumber);
					String zzFlag = IBAHelper.getIBAValue(document, "ZFFLAG");
					tempObj.setName(zzFlag);
					tempObj.setType(rsProductNumber);
					tempObj.setVersion(rsLukahao);
					tempObj.setLifecycle(rsIsZF);
					technicsNumberList.add(tempObj);
				}
				conn.commit();
			} catch (Exception e) {
				logger.error(e);
			} finally {
				try {
					if (conn != null) {
						conn.close();
					}
				} catch (SQLException e) {
					logger.error(e);
				}
 			}
		return technicsNumberList;
	}
	public static WTDocument getDocumentByNumber(String number) throws WTException, RemoteException {
		WTDocument document = null;
		QuerySpec qs = new QuerySpec(WTDocument.class);
		qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number), index);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		qr = new LatestConfigSpec().process(qr);
		if (qr.hasMoreElements()) {
			document = (WTDocument) qr.nextElement();
		}
		return document;
	}
	public static CmParamTableType getCommonParamTableTypeForDataSearch(String technicsNumber, String productNumber, String lukahao){
		CmParamTableType cmParamTableType = null;
		GWParameterTableType gwParamTableType = null;
		GWParamTableTypeMaster master = null;
		try {
			//获取该对象关联参数表的所有Link
			List<GWOperationToParamTableLink> links = new ArrayList<GWOperationToParamTableLink>();
			if(technicsNumber.endsWith("_ZF")){
				links = getGwOperationToParamTableLink(technicsNumber.substring(0, technicsNumber.indexOf("_ZF")), links);
			}else{
				links = getGwOperationToParamTableLink(technicsNumber, links);
			}
			if (links != null && !links.isEmpty()) {
				for (GWOperationToParamTableLink link : links) {
					String tableId = link.getParametertableid();
					gwParamTableType = queryGwParameterTableType(tableId);
					//获取参数表定义信息
					if (gwParamTableType != null) {
						String masterId = gwParamTableType.getTabletypemasterid();
						master = queryParamTableTypeMasterById(masterId);
						if ("通用检查项定义".equals(gwParamTableType.getTechnicstype())) {
							break;
						}
					}
				}
			} else {
				return null;
//				master = MesParameterTypeManager.queryParamTableTypeMaster("CommonParamTable");
//				gwParamTableType = MesParameterTypeManager.getLatestTableType(master.getGwKey());
//				createParamTableLink(technicsNumber, objType, objNumber, gwParamTableType.getGwKey());
			}
			cmParamTableType = GWParameterTableTypeManager.convertToCmParamTableType(gwParamTableType);
			if (cmParamTableType != null) {
				//查询参数
				List<CmParameterTableColumn> tableColumns = cmParamTableType.getTableColumns();
				String columnEnames = "";
				for(CmParameterTableColumn tableColumn : tableColumns){
					String columnEname = tableColumn.getEnName();
					if(columnEnames == ""){
						columnEnames = columnEname;
					}else{
						columnEnames = columnEnames +","+ columnEname;
					}
				}
//				Vector<Vector<Object>> paramVectors = MesDataSearchManager.queryParamsByTableId(master.getName(), gwParamTableType.getGwKey(), productNumber, technicsNumber, "", "", lukahao, "", columnEnames);
				Vector<Vector<Object>> paramVectors = MesDataSearchManager.queryParamsByTableId2(master.getName(), gwParamTableType.getGwKey(), productNumber, technicsNumber, lukahao, "", columnEnames);
//				if (paramVectors != null && !paramVectors.isEmpty()) {
					//Collections.sort(paramVectors, new ParamsComparator());
//				}
				cmParamTableType.setParameters(paramVectors);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return cmParamTableType;
	}
	public static List<CmParamTableType> getMesDataSearchParamTableTypes(String technicsNumber, String productNumber, String lukahao){
		List<CmParamTableType> paramTableTypes = new ArrayList<CmParamTableType>();
		List<String> masterNameList = new ArrayList<String>();
		try {
			//获取该对象关联参数表的所有Link
			List<GWOperationToParamTableLink> links = new ArrayList<GWOperationToParamTableLink>();
			if(technicsNumber.endsWith("_ZF")){
				links = getGwOperationToParamTableLink(technicsNumber.substring(0, technicsNumber.indexOf("_ZF")), links);
				List<String[]> fzTechnics = MesParameterProcessor.getFzTechnicsNumberList(technicsNumber.substring(0, technicsNumber.indexOf("_ZF")));
				for(String[] strs : fzTechnics){
					String fzTechnicsNumber = strs[0];
					links = getGwOperationToParamTableLink(fzTechnicsNumber, links);
				}
			}else{
				links = getGwOperationToParamTableLink(technicsNumber, links);
			}

			if (links != null && !links.isEmpty()) {
				for (GWOperationToParamTableLink link : links) {
					String tableId = link.getParametertableid();
					GWParameterTableType gwParamTableType = queryGwParameterTableType(tableId);
					//获取参数表定义信息
					if (gwParamTableType != null) {
						if (!"通用检查项定义".equals(gwParamTableType.getTechnicstype())) {
							String masterId = gwParamTableType.getTabletypemasterid();
							GWParamTableTypeMaster master = queryParamTableTypeMasterById(masterId);
							System.out.println("speTableName=====>>>>>" + master.getChinaname());
							if(masterNameList.contains(master.getName())){
								continue;
							}
							masterNameList.add(master.getName());
							CmParamTableType cmParamTableType = null;
//							if (!isApproved) {
//								deleteParamTableLink(technicsNumber, objType, objNumber, gwParamTableType.getGwKey());
//								gwParamTableType = MesParameterTypeManager.getLatestTableType(masterId);
//								createParamTableLink(technicsNumber, objType, objNumber, gwParamTableType.getGwKey());
//							}
							cmParamTableType = convertToCmParamTableType(gwParamTableType);
							if (cmParamTableType != null) {
								//查询参数
								List<CmParameterTableColumn> tableColumns = cmParamTableType.getTableColumns();
								String columnEnames = "";
								for(CmParameterTableColumn tableColumn : tableColumns){
									String columnEname = tableColumn.getEnName();
									if(columnEnames == ""){
										columnEnames = columnEname;
									}else{
										columnEnames = columnEnames +","+ columnEname;
									}
								}
								Vector<Vector<Object>> paramVectors = MesDataSearchManager.queryParamsByTableId2(master.getName(), gwParamTableType.getGwKey(), productNumber, technicsNumber, lukahao, "", columnEnames);
								if (paramVectors != null && !paramVectors.isEmpty()) {
//									Collections.sort(paramVectors, new ParamsComparator());
								}
								cmParamTableType.setParameters(paramVectors);
								paramTableTypes.add(cmParamTableType);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return paramTableTypes;
	}

	public static CmParamTableType convertToCmParamTableType(GWParameterTableType gwParameterTableType) throws Exception {
		CmParamTableType paramTableType = new CmParamTableType();
		paramTableType.setTechnicsType(gwParameterTableType.getTechnicstype());
		paramTableType.setIsUsed(gwParameterTableType.getIsUsed());
		paramTableType.setVersion(gwParameterTableType.getVersion());
		paramTableType.setOid(Long.valueOf(gwParameterTableType.getGwKey()));

		GWParamTableTypeMaster master = queryParamTableTypeMasterById(gwParameterTableType.getTabletypemasterid());
		paramTableType.setEnName(master.getName());
		paramTableType.setName(master.getChinaname() + "("+gwParameterTableType.getTechnicstype().subSequence(0, 2)+")");
		paramTableType.setTableTypeMasterId(master.getGwKey());

		List<CmParameterTableColumn> tableColumns = queryCmParameterTableColumns(gwParameterTableType);
		paramTableType.setTableColumns(tableColumns);

		return paramTableType;
	}
	private static List<CmParameterTableColumn> queryCmParameterTableColumns(GWParameterTableType parameterTableType) throws Exception {
		List<GWParameterTableColumn> tableColumns = queryGWParameterTableColumns(parameterTableType);

		List<CmParameterTableColumn> list = new ArrayList<CmParameterTableColumn>();
		if (tableColumns != null && !tableColumns.isEmpty()) {
			for (GWParameterTableColumn gwParameterTableColumn : tableColumns) {
				list.add(convertToCmParamTableColumn(gwParameterTableColumn));
			}
		}

		return list;
	}
	public static CmParameterTableColumn convertToCmParamTableColumn(GWParameterTableColumn gwParameterTableColumn) {
		CmParameterTableColumn tableColumn = new CmParameterTableColumn();

		tableColumn.setOid(Long.valueOf(gwParameterTableColumn.getGwKey()));
		tableColumn.setOrderno(gwParameterTableColumn.getOrderno());
		tableColumn.setParametertabletypeid(gwParameterTableColumn.getParametertabletypeid());
		tableColumn.setName(gwParameterTableColumn.getChinaname());
		tableColumn.setEnName(gwParameterTableColumn.getName());
		tableColumn.setDatatype(gwParameterTableColumn.getDatatype());
		tableColumn.setMaxlong(gwParameterTableColumn.getMaxlong());
		tableColumn.setIsfromparam(gwParameterTableColumn.getIsfromparam());
		tableColumn.setIsrecord(gwParameterTableColumn.getIsrecord());
		tableColumn.setValueRange(gwParameterTableColumn.getValuerange());
		tableColumn.setStatus(gwParameterTableColumn.getStatus());
		tableColumn.setShow(ParameterConstants.isShow(gwParameterTableColumn.getName()));
		tableColumn.setEditable(ParameterConstants.isEditable(gwParameterTableColumn.getName()));
		tableColumn.setVisiless(gwParameterTableColumn.getVisiless());
		tableColumn.setVisilessInMes(gwParameterTableColumn.getVisilessinmes());

		return tableColumn;
	}
	/**
	 * 查询指定参数表定义的列信息
	 *
	 * @param parameterTableType
	 * @return
	 * @throws Exception
	 */
	private static List<GWParameterTableColumn> queryGWParameterTableColumns(GWParameterTableType parameterTableType) throws Exception {
		GwQuerySpec qs = new GwQuerySpec(GWParameterTableColumn.class);
		qs.appendWhere(GWParameterTableColumn.PARAMETERTABLETYPEID, GwQuerySpec.EQUAL, parameterTableType.getGwKey());
		qs.appendOrderBy("ORDERNO", false);
		GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
		List<GWParameterTableColumn> columns = new ArrayList<GWParameterTableColumn>();
		GWParameterTableColumn column = null;
		while (qr.hasNext()) {
			column = (GWParameterTableColumn) qr.next();
			columns.add(column);
		}

		return columns;
	}

	public static File exportSearchData(String technicsNumber, String processNumber, String productNumber, String filePath){
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFCellStyle style = workbook.createCellStyle(); // 样式对象
		style.setVerticalAlignment(VerticalAlignment.CENTER); // 使用枚举值
		style.setAlignment(HorizontalAlignment.CENTER);
		/**通用表的数据*/
		CmParamTableType paramTableType = getCommonParamTableTypeForDataSearch(technicsNumber, processNumber, "");
		CmParameterTablePackage tablePackage = MPMParameterProcessor.getParameterTablePackage(paramTableType, "", "");
		String[] tableColumnName = tablePackage.getMesDataSearchTableColumnName();
		int[] notShowColumns = tablePackage.getNotShowDataSearchColumns();
		List<String> showColumnNames = new ArrayList<String>();
		List<String> notshowColumnNames = new ArrayList<String>();
		for(int n = 0; n < notShowColumns.length; n++){
			notshowColumnNames.add(String.valueOf(notShowColumns[n]));
		}
		for(int i = 0; i < tableColumnName.length; i++){
			if(!notshowColumnNames.contains(String.valueOf(i))){
				showColumnNames.add(tableColumnName[i]);
			}
		}
		HSSFSheet sheet = workbook.createSheet("质量记录表");
		Vector<Vector<Object>> dataVector = paramTableType.getParameters();
		HSSFRow row0 = sheet.createRow(0);
		HSSFCell cell0 = row0.createCell(0);
		cell0.setCellValue("过程编号");
		cell0.setCellStyle(style);
		HSSFCell cell1 = row0.createCell(1);
		cell1.setCellValue(processNumber);
		cell1.setCellStyle(style);
		HSSFCell cell2 = row0.createCell(3);
		cell2.setCellValue("产品编号");
		cell2.setCellStyle(style);
		HSSFCell cell3 = row0.createCell(4);
		cell3.setCellValue(productNumber);
		cell3.setCellStyle(style);
		sheet.autoSizeColumn(0);
		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4);
		HSSFRow row = sheet.createRow(1);
		for(int n = 0; n < showColumnNames.size(); n++){
			HSSFCell cell = row.createCell(n);
			cell.setCellValue(showColumnNames.get(n));
			cell.setCellStyle(style);
		}
		for(int i = 0; i < dataVector.size(); i++){
			row = sheet.createRow(i + 2);
			int count = 0;
			for(int j = 0; j < dataVector.get(i).size(); j++){
				if(!notshowColumnNames.contains(String.valueOf(j))){
					HSSFCell cell = row.createCell(count);
					String cellValue = String.valueOf(dataVector.get(i).get(j));
					if(cellValue.contains("已选图片数量") || cellValue.contains("img")){
						cellValue = "";
					}
					if(cellValue.contains("<html>") && cellValue.contains("<head>") && cellValue.contains("<body>")){
						cellValue = getBlobStr(cellValue);
					}
					if(cellValue.equals("true")){
						cellValue = "是";
					}
					if(cellValue.equals("false")){
						cellValue = "否";
					}
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					cell.setCellValue(cellValue);
					cell.setCellStyle(style);
					count++;
				}
			}
		}
		/**特殊表的数据*/
		List<CmParamTableType> list = MesDataSearchProcesser.getMesDataSearchParamTableTypes(technicsNumber, processNumber, "");
		if (list != null) {
			for (CmParamTableType paramTableType2 : list) {
				CmParameterTablePackage tablePackage2 = MPMParameterProcessor.getParameterTablePackage(paramTableType2, "", "");
				String[] tableColumnName2 = tablePackage2.getMesDataSearchTableColumnName();
				int[] notShowColumns2 = tablePackage2.getNotShowDataSearchColumns();
				List<String> showColumnNames2 = new ArrayList<String>();
				List<String> notshowColumnNames2 = new ArrayList<String>();
				for(int n = 0; n < notShowColumns2.length; n++){
					notshowColumnNames2.add(String.valueOf(notShowColumns2[n]));
				}
				for(int i = 0; i < tableColumnName2.length; i++){
					if(!notshowColumnNames2.contains(String.valueOf(i))){
						showColumnNames2.add(tableColumnName2[i]);
					}
				}
				HSSFSheet sheet2 = workbook.createSheet(paramTableType2.getName());
				Vector<Vector<Object>> dataVector2 = paramTableType2.getParameters();
				HSSFRow row00 = sheet2.createRow(0);
				HSSFCell cell00 = row00.createCell(0);
				cell00.setCellValue("过程编号");
				cell00.setCellStyle(style);
				HSSFCell cell01 = row00.createCell(1);
				cell01.setCellValue(processNumber);
				cell01.setCellStyle(style);
				HSSFCell cell02 = row00.createCell(3);
				cell02.setCellValue("产品编号");
				cell02.setCellStyle(style);
				HSSFCell cell03 = row00.createCell(4);
				cell03.setCellValue(productNumber);
				cell03.setCellStyle(style);
				sheet.autoSizeColumn(0);
				sheet.autoSizeColumn(1);
				sheet.autoSizeColumn(3);
				sheet.autoSizeColumn(4);
				HSSFRow row2 = sheet2.createRow(1);
				for(int n = 0; n < showColumnNames2.size(); n++){
					HSSFCell cell = row2.createCell(n);
					cell.setCellValue(showColumnNames2.get(n));
					cell.setCellStyle(style);
				}
				for(int i = 0; i < dataVector2.size(); i++){
					row2 = sheet2.createRow(i + 2);
					int count = 0;
					for(int j = 0; j < dataVector2.get(i).size(); j++){
						if(!notshowColumnNames2.contains(String.valueOf(j))){
							HSSFCell cell = row2.createCell(count);
							sheet2.autoSizeColumn(j, true);
							String cellValue = String.valueOf(dataVector2.get(i).get(j));
							if(cellValue.contains("已选图片数量") || cellValue.contains("img")){
								cellValue = "";
							}
							if(cellValue.contains("<html>") && cellValue.contains("<head>") && cellValue.contains("<body>")){
								cellValue = getBlobStr(cellValue);
							}
							if(cellValue.equals("true")){
								cellValue = "是";
							}
							if(cellValue.equals("false")){
								cellValue = "否";
							}
							cell.setCellType(HSSFCell.CELL_TYPE_STRING);
							cell.setCellValue(cellValue);
							cell.setCellStyle(style);
							count++;
						}
					}
				}
			}
		}
		FileOutputStream writeFile = null;
		try {
			writeFile = new FileOutputStream(filePath);
			workbook.write(writeFile);
			writeFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new File(filePath);
	}
	/**
	 * 查询对象使用到的参数表
	 *
	 * @param object
	 * @param tableName 参数表表名
	 * @param dataType 数据记录类型
	 * @return
	 * @throws Exception
	 */
	public static List<GWOperationToParamTableLink> getGwOperationToParamTableLink(String technicsNumber, List<GWOperationToParamTableLink> links) throws Exception {
		GwQuerySpec qs = new GwQuerySpec(GWOperationToParamTableLink.class);
		qs.appendWhere(GWOperationToParamTableLink.TECHNICSNUMBER, GwQuerySpec.EQUAL, technicsNumber);
		GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
		GWOperationToParamTableLink link = null;
		while (qr.hasNext()) {
			link = (GWOperationToParamTableLink)qr.next();
			links.add(link);
		}
		return links;
	}
	protected static GWParameterTableType queryGwParameterTableType(String gwkey) {
		GWParameterTableType parameterTableType = null;
		try {
			GwQuerySpec qs = new GwQuerySpec(GWParameterTableType.class);
			qs.appendWhere(GWParameterTableType.KEY_ID, GwQuerySpec.EQUAL, gwkey);
			GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
			if (qr.hasNext()) {
				parameterTableType = (GWParameterTableType)qr.next();
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return parameterTableType;
	}
	protected static GWParamTableTypeMaster queryParamTableTypeMasterById(String gwkey) {
		GWParamTableTypeMaster master = null;
		try {
			GwQuerySpec qs = new GwQuerySpec(GWParamTableTypeMaster.class);
			qs.appendWhere(GWParamTableTypeMaster.KEY_ID, GwQuerySpec.EQUAL, gwkey);
			GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
			if (qr.hasNext()) {
				master = (GWParamTableTypeMaster) qr.next();
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return master;
	}
	public static String getBlobStr(String str){
		str = str.replace("\r\n", "");
		str = str.replace("<html>", "");
		str = str.replace("<head>", "");
		str = str.replace("</head>", "");
		str = str.replace("<body>", "");
		str = str.replace("<p style='margin-top:5'>", "");
		str = str.replace("</p>", "");
		str = str.replace("</body>", "");
		str = str.replace("</html>", "");
		return str.trim();
	}
}
