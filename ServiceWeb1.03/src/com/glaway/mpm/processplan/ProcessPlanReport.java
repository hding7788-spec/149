package com.glaway.mpm.processplan;

import java.beans.PropertyVetoException;
import java.io.File;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import wt.doc.WTDocument;
import wt.inf.container.WTContainer;
import wt.util.WTException;

import com.glaway.mpm.util.GLLogger;
import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.WTContainerUtil;
import com.glaway.mpm.util.WTDocumentUtil;

public class ProcessPlanReport {
	private static final String CLASSNAME = ProcessPlanReport.class.getCanonicalName();
	private static String tempPath = PropertiesUtil.getTempPath() + "/processPlanReport/"
			+ String.valueOf(new Date().getTime()) + File.separator;

	public Vector<String> getPrimaryFileByDocument(String type, String productName) {
		GLLogger.debug(CLASSNAME, "type：" + type + "productName:" + productName);
		Vector<String> vector = new Vector<String>();
		try {
			WTContainer container = WTContainerUtil.getContainerByName(productName);
			List<WTDocument> list = WTDocumentUtil.getDocumentByTypeAndConatiner(container, type);
			for (WTDocument document : list) {
				GLLogger.debug(CLASSNAME, "document：" + document.getName());
				String fileName = WTDocumentUtil.downloadDocumentPrimaryToTemp(document, tempPath);
				if (fileName != null) {
					vector.add(tempPath + fileName);
				}

			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}

		GLLogger.debug(CLASSNAME, "vector：" + vector);
		return vector;
	}
}
