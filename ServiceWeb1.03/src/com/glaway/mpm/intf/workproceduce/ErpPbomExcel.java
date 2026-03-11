package com.glaway.mpm.intf.workproceduce;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.AutoCloseInputStream;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.inf.container.WTContainer;
import wt.util.WTException;

import com.glaway.mpm.pbom.db.Wzk;
import com.glaway.mpm.util.GLLogger;
import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.Util;
import com.glaway.mpm.util.WTDocumentUtil;

public class ErpPbomExcel {
	private static String WORKPROCEDECES_DOCNAME = "pbom_template";
	private static String WORKPROCEDECES_DOC_CONTAINERNAME = "工艺资源库";

	/**
	 * 写Pbom Excel
	 * @param fileName
	 * @param dataList
	 * @return
	 * @throws WTException
	 * @throws PropertyVetoException
	 * @throws IOException
	 * @throws ParsePropertyException
	 * @throws InvalidFormatException
	 */
	public static File pbomWriteExcel(String fileName,List<Wzk> dataList) throws WTException, PropertyVetoException, IOException, ParsePropertyException, InvalidFormatException{
		WTContainer lib = Util.getContainerByName(WORKPROCEDECES_DOC_CONTAINERNAME);
		GLLogger.debug("lib name=" + lib.getContainerName());
		WTDocument wp_doc = WTDocumentUtil.getWrokproceduceDoc(WORKPROCEDECES_DOCNAME, lib);
		ContentItem item = ContentHelper.service.getPrimary(wp_doc);
		ApplicationData ad = (ApplicationData) item;
		InputStream ins = new AutoCloseInputStream(ContentServerHelper.service.findContentStream(ad));
		XLSTransformer transformer = new XLSTransformer();
		Map<String,List<Wzk>> beanParams = new HashMap<String,List<Wzk>>();
		beanParams.put("wzks", dataList);
		Workbook excel =   transformer.transformXLS(ins, beanParams);
		File floder = new File(PropertiesUtil.getTempPath()+File.separator+"pbom");
		if(!floder.exists()){
			floder.mkdirs();
		}
		File file = new File(floder,fileName+".xls");
		OutputStream os = new FileOutputStream(file);
		try{
			excel.write(os);
		}finally{
			os.close();
		}

		return file;
	}
	/**
	 * 读Pbom Excel
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static List<Wzk> pbomReadExcel(InputStream is) throws IOException{
		List<Wzk> dataList = new ArrayList<Wzk>();
		HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
		HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
		HSSFRow row = null;
		HSSFRow titleRow = null;
		for (int i = 2; i < sheet.getLastRowNum() + 1; i++) {
			Wzk wzk = new Wzk();
			titleRow = sheet.getRow(1);
			row = sheet.getRow(i);
			if (row != null) {
				if(row.getCell((short) 1)==null|| row.getCell((short) 2)==null){
					continue;
				}
				for (int j = 1; j < row.getLastCellNum(); j++) {
					HSSFCell cell = row.getCell((short) j);
					HSSFCell titlecell = titleRow.getCell((short) j);
					if (cell!=null) {
						if (titlecell.toString().equals("公司编码")) {
							wzk.setGsdm(getValueByCell(cell));
						} else if (titlecell.toString().equals("工厂编码")) {
							wzk.setGcbm(getValueByCell(cell));
						} else if (titlecell.toString().equals("产品编码")) {
							wzk.setCpbm(getValueByCell(cell));
						} else if (titlecell.toString().equals("版本号")) {
							wzk.setVersion(getValueByCell(cell));
						}else if (titlecell.toString().equals("父项数量")) {
							wzk.setFxsl(getValueByCell(cell));
						}else if (titlecell.toString().equals("上级图号")) {
							wzk.setSjth(getValueByCell(cell));
						}else if (titlecell.toString().equals("上级图号分类编码")) {
							wzk.setSjthflbm(getValueByCell(cell));
						}else if (titlecell.toString().equals("上级图号名称")) {
							wzk.setSjthmc(getValueByCell(cell));
						}else if (titlecell.toString().equals("上级图号所属型号")) {
							wzk.setSjthssxh(getValueByCell(cell));
						}else if (titlecell.toString().equals("上级图号研制阶段")) {
							wzk.setSjthyzjd(getValueByCell(cell));
						}else if (titlecell.toString().equals("上级图号计量单位名称")) {
							wzk.setSjthjldwmc(getValueByCell(cell));
						}else if (titlecell.toString().equals("是否默认")) {
							wzk.setSfmr(getValueByCell(cell));
						}else if (titlecell.toString().equals("材料编码")) {
							wzk.setInvcode(getValueByCell(cell));
						}else if (titlecell.toString().equals("材料分类编码")) {
							wzk.setClflbm(getValueByCell(cell));
							wzk.setInvclasscode(getValueByCell(cell));
						}else if (titlecell.toString().equals("子项数量")) {
							wzk.setZxsl(getValueByCell(cell));
						}else if (titlecell.toString().equals("图号")) {
							wzk.setTh(getValueByCell(cell));
						}else if (titlecell.toString().equals("图号名称")) {
							wzk.setThmc(getValueByCell(cell));
						}else if (titlecell.toString().equals("所属型号")) {
							wzk.setSsxh(getValueByCell(cell));
						}else if (titlecell.toString().equals("研制阶段")) {
							wzk.setYzjd(getValueByCell(cell));
						}else if (titlecell.toString().equals("材料名称")) {
							wzk.setClmc(getValueByCell(cell));
						}else if (titlecell.toString().equals("型号/牌号/材料")) {
							wzk.setInvtype(getValueByCell(cell));
						}else if (titlecell.toString().equals("规格")) {
							wzk.setInvspec(getValueByCell(cell));
						}else if (titlecell.toString().equals("总规范/技术条件")) {
							wzk.setDef2(getValueByCell(cell));
						}else if (titlecell.toString().equals("详细规范/技术条件")) {
							wzk.setDef3(getValueByCell(cell));
						}else if (titlecell.toString().equals("计量单位名称")) {
							wzk.setMeasname(getValueByCell(cell));
						}else if (titlecell.toString().equals("单机工艺定额")) {
							wzk.setDjgyde(getValueByCell(cell));
						}else if (titlecell.toString().equals("下料尺寸")) {
							wzk.setMpcc(getValueByCell(cell));
						}else if (titlecell.toString().equals("毛坯可制件数")) {
							wzk.setMpkzjs(getValueByCell(cell));
						}else if (titlecell.toString().equals("表体备注")) {
							wzk.setBtbz(getValueByCell(cell));
						}else if (titlecell.toString().equals("计量单位")) {
							wzk.setMeasname(getValueByCell(cell));
						}else if (titlecell.toString().equals("密度")) {
							wzk.setMd(getValueByCell(cell));
						}else if (titlecell.toString().equals("物资自由项1")) {
							wzk.setWzzyx1(getValueByCell(cell));
						}else if (titlecell.toString().equals("物资自由项2")) {
							wzk.setWzzyx2(getValueByCell(cell));
						}else if (titlecell.toString().equals("物资自由项3")) {
							wzk.setWzzyx3(getValueByCell(cell));
						}else if (titlecell.toString().equals("质量等级")) {
							wzk.setDef4(getValueByCell(cell));
						}

					}
				}
			}
			if(wzk.getTh()==null||"".equals(wzk.getTh().trim())){

			}else{
				dataList.add(wzk);
			}

		}
		return dataList;
	}

	private static String getValueByCell(HSSFCell cell){
		String value = null;
		if(cell == null){
			return "";
		}

		int type = cell.getCellType();
		if(type == HSSFCell.CELL_TYPE_STRING){
			value = cell.getStringCellValue();
		}
//		else if (HSSFDateUtil.isCellDateFormatted(cell)) {
//	        double d = cell.getNumericCellValue();
//	        Date date = HSSFDateUtil.getJavaDate(d);
//	        SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
//	        value = dateFormat.format(date);
//		}

		else if(type == HSSFCell.CELL_TYPE_NUMERIC){
			double dvalue = cell.getNumericCellValue();
			if(isIntegerNum(String.valueOf(dvalue))){
				value = String.valueOf((int)dvalue);
			}else{

				value = String.valueOf(dvalue);
			}
		}else if(type == HSSFCell.CELL_TYPE_BOOLEAN){
			value = cell.getBooleanCellValue() + "";
		}else if(type == HSSFCell.CELL_TYPE_BLANK){
			value = "";
		}else{
			value = cell.getStringCellValue();
		}

		if (value == null){
			value = "";
		}
		value = value.trim();
		return value;
	}



	private static boolean isIntegerNum(String s) {
		String[] strNum = s.split("\\.");
		try{
			if(strNum[1].length()==1&&Integer.parseInt(strNum[1]) == 0){
				return true;
			}
		}catch(ArrayIndexOutOfBoundsException ex){
			return true;
		}
		return false;
	}



	public static byte[] savePbomToExcel(String fileName,List<Wzk> dataList) throws WTException, PropertyVetoException, IOException, ParsePropertyException, InvalidFormatException{
		WTContainer lib = Util.getContainerByName(WORKPROCEDECES_DOC_CONTAINERNAME);
		GLLogger.debug("lib name=" + lib.getContainerName());
		WTDocument wp_doc = WTDocumentUtil.getWrokproceduceDoc(WORKPROCEDECES_DOCNAME, lib);
		ContentItem item = ContentHelper.service.getPrimary(wp_doc);
		ApplicationData ad = (ApplicationData) item;
		InputStream ins = new AutoCloseInputStream(ContentServerHelper.service.findContentStream(ad));
		XLSTransformer transformer = new XLSTransformer();
		Map<String,List<Wzk>> beanParams = new HashMap<String,List<Wzk>>();
		beanParams.put("wzks", dataList);
		Workbook excel =   transformer.transformXLS(ins, beanParams);
		File floder = new File(PropertiesUtil.getTempPath()+File.separator+"pbom");
		if(!floder.exists()){
			floder.mkdirs();
		}
		File file = new File(floder,fileName+".xls");
		OutputStream os = new FileOutputStream(file);
		try{
			excel.write(os);
		}finally{
			os.close();
		}
		InputStream is = new  AutoCloseInputStream(new FileInputStream(file));
		byte b[] =  IOUtils.toByteArray(is);
		file.delete();
		return b;
	}

}
