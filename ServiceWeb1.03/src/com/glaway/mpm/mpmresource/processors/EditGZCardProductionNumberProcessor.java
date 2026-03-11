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
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class EditGZCardProductionNumberProcessor extends CustomerObjectFormProcessor {

	@Override
	public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> list) throws WTException {
		FormResult result = new FormResult(FormProcessingStatus.SUCCESS);
		Map newTextMap = commandBean.getText();
		Map oldTextMap = commandBean.getOldText();

		String newProductionNumber = (String) newTextMap.get(AttributeConstants.productionNumber);
		String oldProductionNumber = (String) oldTextMap.get(AttributeConstants.productionNumber);

		InputStream inputStream = null;

		Transaction transaction = new Transaction();
		try {
			transaction.start();
			NmOid actionOid = commandBean.getActionOid();
			Object object = actionOid.getRefObject();
			WTDocument document = GZCardHelper.getGZCardDocument(object);
			IBAHelper helper = new IBAHelper(document);
			Map<String, String> ibaMap = new HashMap<String, String>();
			if (!newProductionNumber.equals(oldProductionNumber)) {
				ibaMap.put(AttributeConstants.productionNumber, newProductionNumber);
			}
			helper.setIBAValue(document, ibaMap);
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
