package com.glaway.mpm.print.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.glaway.mpm.intf.PrintToWCIntfRMI;
import com.glaway.mpm.print.data.CmImportBean;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class OutsideFileImport extends DefaultObjectFormProcessor{


	public static void importInfoToDB(List<CmImportBean> list) throws RemoteException, InvocationTargetException{
		PrintToWCIntfRMI.outsideImportInfoToDB(list);
	}
	public static List<CmImportBean> getExcelInfo(File file) throws WTException{
		List<CmImportBean> listBean = new ArrayList<CmImportBean>();
		Workbook workbook;
		try {
			workbook = Workbook.getWorkbook(file);
			Sheet sheet = workbook.getSheet("Sheet1");
			int rows = sheet.getRows();
			for (int i = 1; i < rows; i++) {
				if("图纸".equals(sheet.getCell(0, i).getContents()) || "文件".equals(sheet.getCell(0, i).getContents()) || "光盘".equals(sheet.getCell(0, i).getContents()) || "通知单".equals(sheet.getCell(0, i).getContents())){
					CmImportBean bean = new CmImportBean();
					bean.setFileType(sheet.getCell(0, i).getContents());
					bean.setFileNumber(sheet.getCell(1, i).getContents());
					bean.setFileName(sheet.getCell(2, i).getContents());
					bean.setVersion(sheet.getCell(3, i).getContents());
					bean.setDistributeDept(sheet.getCell(4, i).getContents());
					bean.setDistributeQuantity(sheet.getCell(5, i).getContents());
					bean.setOutDept(sheet.getCell(6, i).getContents());
					listBean.add(bean);
				}
			}
		} catch (BiffException e) {
			throw new WTException("所选择的文件类型不符合规范！");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return listBean;
	}
	@Override
	public FormResult doOperation(NmCommandBean nmCommandBean, List<ObjectBean> listBean) throws WTException {
		List<CmImportBean> list = new ArrayList<CmImportBean>();
		FormResult result = new FormResult(FormProcessingStatus.SUCCESS);
		Object fileMap = nmCommandBean.getMap().get("fileUploadMap");
		if (fileMap != null) {
			Map<?, ?> map = (Map<?, ?>) fileMap;
			Object fileObj = (File) map.get("xlsFile");
			File file = null;
			if (fileObj instanceof File) {
				file = (File) fileObj;
				list = getExcelInfo(file);
				if(list != null && list.size() > 0){
					try {
						importInfoToDB(list);
					} catch (RemoteException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}else{
					result.addFeedbackMessage(new FeedbackMessage(FeedbackType.FAILURE, SessionHelper.getLocale(), "", null, "导入失败！"));
					result.setURL("app/#ptc1/homepage");
					return result;
				}

			}
		}
//		NmURL nmUrl = new NmURL();
//		nmUrl.setType("homepage");
//		String urlStr = nmUrl.toString2(nmCommandBean.getUrlFactoryBean());
//		result.setNextAction(FormResultAction.FORWARD);
		result.addFeedbackMessage(new FeedbackMessage(FeedbackType.SUCCESS, SessionHelper.getLocale(), "", null, "导入成功！"));
		result.setURL("app/#ptc1/homepage");
//		result.setForcedUrl(urlStr);

//		FeedbackMessage msg;
//		msg = new FeedbackMessage(FeedbackType.SUCCESS, null, message, null);
//		//result.setNextAction(FormResultAction.FORWARD);
//		NmURL url = new NmURL();
//		url.setType("homepage");
//		String urlStr = url.toString2(nmCommandBean.getUrlFactoryBean());
//		result.addFeedbackMessage(msg);
//		result.setForcedUrl(urlStr);
		return result;
	}

}
