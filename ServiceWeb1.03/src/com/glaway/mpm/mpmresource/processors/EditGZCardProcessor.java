package com.glaway.mpm.mpmresource.processors;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.util.WTException;

import com.glaway.mpm.constants.AttributeConstants;
import com.glaway.mpm.mpmresource.gzcard.GZCardHelper;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.ReferenceFactory;
import com.glaway.mpm.util.Util;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class EditGZCardProcessor extends CustomerObjectFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> list) throws WTException {
		FormResult result = new FormResult(FormProcessingStatus.SUCCESS);
		Map newComboBoxMap = commandBean.getComboBox();
		Map oldComboBoxMap = commandBean.getOldComboBox();
		Map newTextMap = commandBean.getText();
		Map oldTextMap = commandBean.getOldText();
		Map newTextAreaMap = commandBean.getTextArea();
		Map oldTextAreaMap = commandBean.getOldTextArea();

		ArrayList newInsertPart = (ArrayList) newComboBoxMap.get(AttributeConstants.insertPart);
		ArrayList newIsReview = (ArrayList) newComboBoxMap.get(AttributeConstants.isReview);
		ArrayList newIsCommonTools = (ArrayList) newComboBoxMap.get(AttributeConstants.isCommonTools);
		ArrayList newIsTestPart = (ArrayList) newComboBoxMap.get(AttributeConstants.isTestPart);
		ArrayList newWorkShop = (ArrayList) newComboBoxMap.get(AttributeConstants.workShop);
		ArrayList newIsRegularlyTools = (ArrayList) newComboBoxMap.get(AttributeConstants.isRegularlyTools);
		ArrayList oldInsertPart = (ArrayList) oldComboBoxMap.get(AttributeConstants.insertPart);
		ArrayList oldIsReview = (ArrayList) oldComboBoxMap.get(AttributeConstants.isReview);
		ArrayList oldIsCommonTools = (ArrayList) oldComboBoxMap.get(AttributeConstants.isCommonTools);
		ArrayList oldIsTestPart = (ArrayList) oldComboBoxMap.get(AttributeConstants.isTestPart);
		ArrayList oldWorkShop = (ArrayList) oldComboBoxMap.get(AttributeConstants.workShop);
		ArrayList oldIsRegularlyTools = (ArrayList) oldComboBoxMap.get(AttributeConstants.isRegularlyTools);

		String newProductNumber = (String) newTextMap.get(AttributeConstants.productNumber);
		String newProductionNumber = (String) newTextMap.get(AttributeConstants.productionNumber);
		String newPartNumber = (String) newTextMap.get(AttributeConstants.partNumber);
		String oldProductNumber = (String) oldTextMap.get(AttributeConstants.productNumber);
		String oldProductionNumber = (String) oldTextMap.get(AttributeConstants.productionNumber);
		String oldPartNumber = (String) oldTextMap.get(AttributeConstants.partNumber);

		String wholePartNumber = "";

		String newToolingRequirements = (String) newTextAreaMap.get("toolingRequirements");
		String oldToolingRequirements = (String) oldTextAreaMap.get("toolingRequirements");

		// 判断输入的零件是否存在，存在就找出上皆的零件编号，放到整件编号（wholePartNumber）属性中
		if (!newPartNumber.equals(oldPartNumber)) {
			if (newPartNumber != null && !"".equals(newPartNumber.trim())) {
				newPartNumber = newPartNumber.trim().toUpperCase();
				boolean tag = true;
				Set<String> parentPartNumber = new HashSet<String>();
				for (String str : newPartNumber.split(";")) {
					WTPart part = WTPartUtil.getLatestPartByPartNumber(str);
					if (part == null) {
						tag = false;
					} else {
						List<WTPart> parentPart = WTPartUtil.getParentPart(part);
						for (WTPart p : parentPart) {
							parentPartNumber.add(p.getNumber());
						}
					}
				}
				for (String str : parentPartNumber) {
					if ("".equals(wholePartNumber)) {
						wholePartNumber = str;
					} else {
						wholePartNumber = wholePartNumber + ";" + str;
					}
				}
				if (!tag) {
					result = new FormResult(FormProcessingStatus.FAILURE);
					FeedbackMessage feedbackMessage = new FeedbackMessage();
					feedbackMessage.addMessage("输入的零件编号不存在");
					result.addFeedbackMessage(feedbackMessage);
					return result;
				}
			}
		}

		InputStream inputStream = null;

		Transaction transaction = new Transaction();
		try {
			transaction.start();
			NmOid actionOid = commandBean.getActionOid();
			Object object = actionOid.getRefObject();
			WTDocument document = GZCardHelper.getGZCardDocument(object);
			IBAHelper helper = new IBAHelper(document);
			Map<String, String> ibaMap = new HashMap<String, String>();
			if (!newInsertPart.equals(oldInsertPart)) {
				ibaMap.put(AttributeConstants.insertPart, (String) newInsertPart.get(0));
			}
			if (!newIsReview.equals(oldIsReview)) {
				ibaMap.put(AttributeConstants.isReview, (String) newIsReview.get(0));
			}
			if (!newIsCommonTools.equals(oldIsCommonTools)) {
				ibaMap.put(AttributeConstants.isCommonTools, (String) newIsCommonTools.get(0));
			}
			if (!newIsTestPart.equals(oldIsTestPart)) {
				ibaMap.put(AttributeConstants.isTestPart, (String) newIsTestPart.get(0));
			}
			if (!newWorkShop.equals(oldWorkShop)) {
				ibaMap.put(AttributeConstants.workShop, (String) newWorkShop.get(0));
			}
			if (!newProductNumber.equals(oldProductNumber)) {
				ibaMap.put(AttributeConstants.productNumber, newProductNumber);
			}
			if (!newProductionNumber.equals(oldProductionNumber)) {
				ibaMap.put(AttributeConstants.productionNumber, newProductionNumber);
			}
			if (!newPartNumber.equals(oldPartNumber)) {
				ibaMap.put(AttributeConstants.partNumber, newPartNumber);
				ibaMap.put(AttributeConstants.wholePartNumber, wholePartNumber);
			}
			if (!newToolingRequirements.equals(oldToolingRequirements)) {
				ibaMap.put(AttributeConstants.toolingRequirements, newToolingRequirements);
			}
			if (!newIsRegularlyTools.equals(oldIsRegularlyTools)) {
				ibaMap.put(AttributeConstants.isRegularlyTools, (String) newIsRegularlyTools.get(0));
			}
			helper.setIBAValue(document, ibaMap);
			// // String[] value = (String[])
			// // commandBean.getParameterMap().get("doc");
			// String value = commandBean.getTextParameter("hiddenDoc");
			// System.out.println("value---" + value);
			// if (value != null && !"".equals(value)) {
			// String fileName = value.substring(value.lastIndexOf("\\") + 1,
			// value.length());
			// File file = null;
			// Map fileUploadMap = (Map)
			// commandBean.getMap().get("fileUploadMap");
			// if (fileUploadMap != null) {
			// file = (File) fileUploadMap.get("doc");
			// }
			// if (file != null) {
			// inputStream = new FileInputStream(file);
			// document = (WTDocument)
			// PersistenceHelper.manager.refresh(document);
			// ApplicationData appData = (ApplicationData)
			// ContentHelper.service.getPrimary(document);
			// if (appData != null) {
			// PersistenceHelper.manager.delete(appData);
			// PersistenceServerHelper.manager.update(document);
			// }
			// appData = ApplicationData.newApplicationData(document);
			// // 主物件和附件
			// // ContentRoleType.SECONDARY 表示 附件
			// // ContentRoleType.PRIMARY 表示主物件
			// appData.setRole(ContentRoleType.PRIMARY);
			// // 设置文件名称
			// appData.setFileName(fileName);
			// appData =
			// ContentServerHelper.service.updateContent((ContentHolder)
			// document, appData, inputStream); // 更新内容
			// PersistenceServerHelper.manager.update(document);
			// document = (WTDocument) ContentServerHelper.service
			// .updateHolderFormat((FormatContentHolder) document); // 更新格式
			// }
			// }
			document = (WTDocument) PersistenceHelper.manager.refresh(document);
			String deleteUploadedFile = commandBean.getTextParameter("deleteUploadedFile");
			if (!"".equals(deleteUploadedFile)) {
				String deleteUploadedFiles[] = deleteUploadedFile.split(";");
				for (String str : deleteUploadedFiles) {
					System.out.println("fileUploadMap:" + str);
					ApplicationData data = (ApplicationData) ReferenceFactory.getObjectbyOid(str);
					PersistenceHelper.manager.delete(data);
				}
			}
			Map<String,String[]> parameterMap = commandBean.getRequest().getParameterMap();
			Map<String, File> fileUploadMap = (Map<String, File>) commandBean.getMap().get("fileUploadMap");
			System.out.println("fileUploadMap:" + fileUploadMap);
			for (String key : parameterMap.keySet()) {
				String fileName = "";
				if (key.contains("hiddenfile")) {
					String filePath = ((String[]) parameterMap.get(key))[0];
					if (filePath != null && !"".equals(filePath)) {
						fileName = filePath.substring(filePath.lastIndexOf("\\") + 1, filePath.length());
					}
				} else if (key.contains("hiddensecondaryfile")) {
					String filePath = ((String[]) parameterMap.get(key))[0];
					if (filePath != null && !"".equals(filePath)) {
						fileName = "secondaryfile-"
								+ filePath.substring(filePath.lastIndexOf("\\") + 1, filePath.length());
					}
				}
				if (!"".equals(fileName)) {
					System.out.println("key:" + key);
					System.out.println("fileName:" + fileName);
					System.out.println("fileName:" + key.replace("hidden", ""));
					System.out.println("fileName:" + fileUploadMap.get(key.replace("hidden", "")));
					inputStream = new FileInputStream(fileUploadMap.get(key.replace("hidden", "")));
					ApplicationData appData = ApplicationData.newApplicationData(document);
					appData.setRole(ContentRoleType.SECONDARY);
					appData.setFileName(fileName);
					appData = ContentServerHelper.service.updateContent((ContentHolder) document, appData, inputStream); // 更新内容
				}
			}
			PersistenceServerHelper.manager.update(document);
			document = (WTDocument) ContentServerHelper.service.updateHolderFormat((FormatContentHolder) document); // 更新格式

			transaction.commit();
			transaction = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (transaction != null) {
				transaction.rollback();
			}
		}
		return result;
	}
}
