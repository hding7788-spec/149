package com.glaway.mpm.print.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.CellFormat;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import com.glaway.mpm.print.data.CmPrintInfoBean;
import com.glaway.mpm.print.helper.MPMPrintHelper;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.FileChooserTool;
import com.glaway.mpm.util.FileUtil;

public class PrintExportExcelUtil {
	//统一打印模块导出Excel_jiangyixing_20170331
		public static void printExcel(List<CmPrintInfoBean> cmPrintInfoBeans){
			File directory = FileChooserTool.getDirectory(null);
			if(!directory.exists()){
				if(!directory.isDirectory()){
					directory.mkdirs();
					CommonUIUtil.showMessageDialog(null, "请选择文件夹！");
					return ;
				}
			}
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
			String dateString = dateFormat.format(date);
			String filePath = directory.getAbsolutePath()+File.separator+"统一打印表格数据导出"+dateString+".xls";
			new PrintExportExcelUtil().getFileExcelPath(filePath);
			File file = new File(filePath);
			if(file.exists()){
				getExcelFileReader(file,filePath, cmPrintInfoBeans);
			}

		}
		public void getFileExcelPath(String path){
			try {
				InputStream is = this.getClass().getResourceAsStream("/d800/print/util/excelFileModel/printModel.xls");
				FileUtil.copyFile(is, path);
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public static void getExcelFileReader(File file,String path,List<CmPrintInfoBean> cmPrintInfoBeans){
			try {
//				file.createNewFile();
				Workbook rwb = new PrintExportExcelUtil().getExcelWorkbookTemplate(path);
				WritableWorkbook wwb = Workbook.createWorkbook(file, rwb);
				WritableSheet sheet = wwb.getSheet(0);
				//WritableSheet sheetHome = wwb.importSheet("统一打印管理", 0, sheet);
				int index = 1;
				for (CmPrintInfoBean cmPrintInfoBean : cmPrintInfoBeans) {
					Object qrCodeNumber = cmPrintInfoBean.getQrCode();
					//获取分发情况
					List<CmPrintInfoBean> cmPrintInfoBeansList = MPMPrintHelper.getDistributeAndRecoverInfo(String.valueOf(qrCodeNumber));
					String dept = CommonUtil.objectToString(cmPrintInfoBean.getDistributeDeptAndCount());
					String[] depts = dept.split(",");
					//每个分发部门单独显示
					Map<String, String> deptMap = new HashMap<String, String>();
					for (String deptOne : depts) {
						String[] deptones = deptOne.split("-");
						deptMap.put(deptones[0], deptOne);
					}

					if(cmPrintInfoBeansList!=null&&cmPrintInfoBeansList.size()>0){
						for (CmPrintInfoBean cmPrintInfoBean2 : cmPrintInfoBeansList) {
							cmPrintInfoBean.setReceipter(cmPrintInfoBean2.getReceipter());//领取人
							cmPrintInfoBean.setReceiptDate(cmPrintInfoBean2.getReceiptDate());//领取日期
							cmPrintInfoBean.setReceiptDept(cmPrintInfoBean2.getReceiptDept());//领取部门
							cmPrintInfoBean.setRecoverPerson(cmPrintInfoBean2.getRecoverPerson());//退回人
							cmPrintInfoBean.setRecoverDate(cmPrintInfoBean2.getRecoverDate());//退回日期
							cmPrintInfoBean.setRecoverDept(cmPrintInfoBean2.getRecoverDept());//退回部门
							cmPrintInfoBean.setRecoverRemark(cmPrintInfoBean2.getRecoverRemark());//退回备注
							cmPrintInfoBean.setReceivePerson(cmPrintInfoBean2.getReceivePerson());//收件人
							cmPrintInfoBean.setReceiveDate(cmPrintInfoBean2.getReceiveDate());//收件日期
							cmPrintInfoBean.setReceiveCount(cmPrintInfoBean2.getReceiveCount());//回收份数


							cmPrintInfoBean.setDistributeDeptAndCount(deptMap.get(cmPrintInfoBean2.getReceiptDept()));
							deptMap.remove(cmPrintInfoBean2.getReceiptDept());

							setSheetCenterContent(wwb, sheet, cmPrintInfoBean, index);
							index++;
						}
						cmPrintInfoBean.setReceipter("");//领取人
						cmPrintInfoBean.setReceiptDate("");//领取日期
						cmPrintInfoBean.setReceiptDept("");//领取部门
						cmPrintInfoBean.setRecoverPerson("");//退回人
						cmPrintInfoBean.setRecoverDate("");//退回日期
						cmPrintInfoBean.setRecoverDept("");//退回部门
						cmPrintInfoBean.setRecoverRemark("");//退回备注
						cmPrintInfoBean.setReceivePerson("");//收件人
						cmPrintInfoBean.setReceiveDate("");//收件日期
						cmPrintInfoBean.setReceiveCount("");//回收份数
					}
					/*else{
						setSheetCenterContent(wwb, sheet, cmPrintInfoBean, index);
						index++;
					}*/
					if(deptMap.size()>0){
						Iterator iterator = deptMap.entrySet().iterator();
						while (iterator.hasNext()) {
							Map.Entry<String, String> entry = (Entry<String, String>) iterator.next();
							String deptAndCount = entry.getValue();

							cmPrintInfoBean.setDistributeDeptAndCount(deptAndCount);
							setSheetCenterContent(wwb, sheet, cmPrintInfoBean, index);
							index++;
						}
					}
				}
				wwb.write();
				wwb.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		public static void setSheetCenterContent(WritableWorkbook wwb, WritableSheet singleWorkTime, CmPrintInfoBean bean, int i) throws RowsExceededException, WriteException{
			int y = 1 + i;
			singleWorkTime.addCell(getLabelCell(0, y, i+"", singleWorkTime));//序号
			singleWorkTime.addCell(getLabelCell(1, y,  CommonUtil.objectToString(bean.getFileNumber()), singleWorkTime));//编号
			singleWorkTime.addCell(getLabelCell(2, y, CommonUtil.objectToString(bean.getFileName()), singleWorkTime));//名称
			singleWorkTime.addCell(getLabelCell(3, y, CommonUtil.objectToString(bean.getPindex()), singleWorkTime));//产品代号
			singleWorkTime.addCell(getLabelCell(4, y, CommonUtil.objectToString(bean.getVersion()), singleWorkTime));//版本
			singleWorkTime.addCell(getLabelCell(5, y, CommonUtil.objectToString(bean.getPhaseCode()), singleWorkTime));//阶段标记
			singleWorkTime.addCell(getLabelCell(6, y, CommonUtil.objectToString(bean.getFileType()), singleWorkTime));//文件类型
			singleWorkTime.addCell(getLabelCell(7, y, CommonUtil.objectToString(bean.getBaseline()), singleWorkTime));//技术状态基线
			singleWorkTime.addCell(getLabelCell(8, y, CommonUtil.objectToString(bean.getPageCount()), singleWorkTime));//页数
			String isBlueCard = "";
			if(bean.isBlueCard()){
				isBlueCard = "是";
			}else{
				isBlueCard = "否";
			}
			singleWorkTime.addCell(getLabelCell(9, y, isBlueCard, singleWorkTime));//是否蓝卡
			singleWorkTime.addCell(getLabelCell(10, y, CommonUtil.objectToString(bean.getSecret()), singleWorkTime));//密级
			singleWorkTime.addCell(getLabelCell(11, y, CommonUtil.objectToString(bean.getEcnNumber()), singleWorkTime));//更改单号
			singleWorkTime.addCell(getLabelCell(12, y, CommonUtil.objectToString(bean.getPrintDescription()), singleWorkTime));//打印要求
			singleWorkTime.addCell(getLabelCell(13, y, CommonUtil.objectToString(bean.getDistributeDeptAndCount()), singleWorkTime));//分发不部门及分数
			singleWorkTime.addCell(getLabelCell(14, y, CommonUtil.objectToString(bean.getQrCode()), singleWorkTime));//二维码
			singleWorkTime.addCell(getLabelCell(15, y, CommonUtil.objectToString(bean.getApplier()), singleWorkTime));//申请人
			singleWorkTime.addCell(getLabelCell(16, y, CommonUtil.objectToString(bean.getApplyDate()), singleWorkTime));//申请时间
			singleWorkTime.addCell(getLabelCell(17, y, CommonUtil.objectToString(bean.getPrinter()), singleWorkTime));//打印人
			singleWorkTime.addCell(getLabelCell(18, y, CommonUtil.objectToString(bean.getPrintDate()), singleWorkTime));//打印时间

			singleWorkTime.addCell(getLabelCell(19, y, CommonUtil.objectToString(bean.getReceipter()), singleWorkTime));//领取人
			singleWorkTime.addCell(getLabelCell(20, y, CommonUtil.objectToString(bean.getReceiptDate()), singleWorkTime));//领取日期
			singleWorkTime.addCell(getLabelCell(21, y, CommonUtil.objectToString(bean.getReceiptDept()), singleWorkTime));//领取部门
			singleWorkTime.addCell(getLabelCell(22, y, CommonUtil.objectToString(bean.getRecoverPerson()), singleWorkTime));//退回人
			singleWorkTime.addCell(getLabelCell(23, y, CommonUtil.objectToString(bean.getRecoverDate()), singleWorkTime));//退回日期
			singleWorkTime.addCell(getLabelCell(24, y, CommonUtil.objectToString(bean.getRecoverDept()), singleWorkTime));//退回部门
			singleWorkTime.addCell(getLabelCell(25, y, CommonUtil.objectToString(bean.getRecoverRemark()), singleWorkTime));//退回备注
			singleWorkTime.addCell(getLabelCell(26, y, CommonUtil.objectToString(bean.getReceivePerson()), singleWorkTime));//收件人
			singleWorkTime.addCell(getLabelCell(27, y, CommonUtil.objectToString(bean.getReceiveDate()), singleWorkTime));//收件日期
			singleWorkTime.addCell(getLabelCell(28, y, CommonUtil.objectToString(bean.getReceiveCount()), singleWorkTime));//回收份数


		}
		public static Label getLabelCell(int cols, int row, String value, WritableSheet sheet) throws WriteException {
			Label label = new Label(cols, row, value);
			CellFormat format = sheet.getCell(cols, row).getCellFormat();
			if (format != null) {
				label.setCellFormat(format);
			}
			return label;
		}
		public  Workbook getExcelWorkbookTemplate(String fileFullName)
				throws Exception {
			FileInputStream fileinput = new FileInputStream(fileFullName);
//			InputStream is = this.getClass().getResourceAsStream("/d800/print/util/excelFileModel/printModel.xls");

			try {
				WorkbookSettings wbs = new WorkbookSettings();
				wbs.setGCDisabled(true);
				wbs.setSuppressWarnings(true);
				Workbook rwb = Workbook.getWorkbook(fileinput, wbs);
				return rwb;
			} finally {
				fileinput.close();
			}
		}
}
