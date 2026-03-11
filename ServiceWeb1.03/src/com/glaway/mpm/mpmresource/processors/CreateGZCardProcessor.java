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

import wt.part.WTPart;
import wt.pom.Transaction;
import wt.util.WTException;

import com.glaway.mpm.constants.AttributeConstants;
import com.glaway.mpm.constants.Constants;
import com.glaway.mpm.mpmresource.gzcard.GZCardHelper;
import com.glaway.mpm.mpmresource.gznumber.number.GZNumberManager;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class CreateGZCardProcessor extends CustomerObjectFormProcessor {

	@SuppressWarnings("unchecked")
	@Override
	public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> list) throws WTException {
		FormResult result = new FormResult(FormProcessingStatus.SUCCESS);
		InputStream inputStream = null;

		Transaction transaction = new Transaction();
		try {
			Map comboBoxMap = commandBean.getComboBox();
			Map textMap = commandBean.getText();
			Map textAreaMap = commandBean.getTextArea();
			String number = ((ArrayList) comboBoxMap.get(AttributeConstants.number)).get(0).toString().split(
					"\\" + Constants.gzNameSplitStr)[0];
			ArrayList insertPart = (ArrayList) comboBoxMap.get(AttributeConstants.insertPart);
			ArrayList isReview = (ArrayList) comboBoxMap.get(AttributeConstants.isReview);
			ArrayList isCommonTools = (ArrayList) comboBoxMap.get(AttributeConstants.isCommonTools);
			ArrayList isTestPart = (ArrayList) comboBoxMap.get(AttributeConstants.isTestPart);
			ArrayList workShop = (ArrayList) comboBoxMap.get(AttributeConstants.workShop);
			ArrayList isRegularlyTools = (ArrayList) comboBoxMap.get(AttributeConstants.isRegularlyTools);

			String name = (String) textMap.get(AttributeConstants.name);
			String productNumber = (String) textMap.get(AttributeConstants.productNumber);
			String productionNumber = (String) textMap.get(AttributeConstants.productionNumber);
			String partNumber = (String) textMap.get(AttributeConstants.partNumber);
			String wholePartNumber = "";

			String toolingRequirements = (String) textAreaMap.get("toolingRequirements");

			// 判断输入的零件是否存在，存在就找出上皆的零件编号，放到整件编号（wholePartNumber）属性中
			if (partNumber != null && !"".equals(partNumber.trim())) {
				partNumber = partNumber.trim().toUpperCase();
				boolean tag = true;
				Set<String> parentPartNumber = new HashSet<String>();
				for (String str : partNumber.split(";")) {
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
					feedbackMessage.addMessage("输入的零件编号不存在!");
					result.addFeedbackMessage(feedbackMessage);
					return result;
				}
			}

			transaction.start();
			Map<String, String> ibaMap = new HashMap<String, String>();
			ibaMap.put(AttributeConstants.insertPart, (String) insertPart.get(0));
			ibaMap.put(AttributeConstants.isReview, (String) isReview.get(0));
			ibaMap.put(AttributeConstants.isCommonTools, (String) isCommonTools.get(0));
			ibaMap.put(AttributeConstants.isTestPart, (String) isTestPart.get(0));
			ibaMap.put(AttributeConstants.workShop, (String) workShop.get(0));
			ibaMap.put(AttributeConstants.productNumber, productNumber);
			ibaMap.put(AttributeConstants.productionNumber, productionNumber);
			ibaMap.put(AttributeConstants.partNumber, partNumber);
			ibaMap.put(AttributeConstants.wholePartNumber, wholePartNumber);
			ibaMap.put(AttributeConstants.toolingRequirements, toolingRequirements);
			ibaMap.put(AttributeConstants.isRegularlyTools, (String) isRegularlyTools.get(0));
			String fileName = "";
			Map<String, InputStream> fileMap = new HashMap<String, InputStream>();
			Map<String,String[]> parameterMap = commandBean.getRequest().getParameterMap();
			Map<String, File> fileUploadMap = (Map<String, File>) commandBean.getMap().get("fileUploadMap");
			for (String key : parameterMap.keySet()) {
				if (key.contains("hiddenfile")) {
					String filePath = ((String[]) parameterMap.get(key))[0];
					if (filePath != null && !"".equals(filePath)) {
						fileName = filePath.substring(filePath.lastIndexOf("\\") + 1, filePath.length());
						fileMap.put(fileName, new FileInputStream(fileUploadMap.get(key.replace("hidden", ""))));
					}
				} else if (key.contains("hiddensecondaryfile")) {
					String filePath = ((String[]) parameterMap.get(key))[0];
					if (filePath != null && !"".equals(filePath)) {
						fileName = filePath.substring(filePath.lastIndexOf("\\") + 1, filePath.length());
						fileMap.put("secondaryfile-"+fileName, new FileInputStream(fileUploadMap.get(key.replace("hidden", ""))));
					}
				}
			}
			// 创建工装申请卡
			GZCardHelper.createGZCard(number, name, ibaMap, fileMap);
			// 创建工装资源
			Map<String, String> ibaMap1 = new HashMap<String, String>();
			ibaMap1.put(AttributeConstants.productNumber, productNumber);
			ibaMap1.put(AttributeConstants.partNumber, partNumber);
			ibaMap1.put(AttributeConstants.gzCardNumber, number);
			ibaMap1.put(AttributeConstants.isRegularlyTools, (String) isRegularlyTools.get(0));
			ibaMap1.put(AttributeConstants.workShop, (String) workShop.get(0));
			GZCardHelper.createGZResource(number.replace(".", "-"), name, ibaMap1);
			// 设定工装编号为已使用
			GZNumberManager.setNumberUsed(number);
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
