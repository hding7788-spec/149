package ext.casc.workflow.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import ext.casc.doc.CSCDoc;
import ext.casc.util.CSCIBA;
import ext.casc.util.IBAUtility;

import wt.doc.WTDocument;
import wt.fc.ReferenceFactory;
import wt.iba.value.IBAHolder;
import wt.inf.container.WTContainerRef;
import wt.method.RemoteMethodServer;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardDocService extends StandardManager implements
DocService, Serializable {
	
	public static StandardDocService newStandardDocService()
	throws WTException {
		StandardDocService instance = new StandardDocService();
		instance.initialize();
		return instance;
	}

	public Object createDoc(String number, String name, String desc, HashMap attributes, HashMap<String, Object> softAttr, WTContainerRef containerRef)
			throws WTException {
		WTDocument wtd = CSCDoc.createDoc(number,name,desc,attributes,containerRef);
		CSCIBA iba = new CSCIBA(wtd);
		IBAUtility ibaUtility = new IBAUtility(wtd);
		try {
			for (String key:softAttr.keySet()){
				Object obj = softAttr.get(key);
				if (obj instanceof String)
					CSCIBA.setIBAStringValue(wtd, key, (String)obj);
			}
			iba.updateIBAHolder((IBAHolder)wtd);
		} catch (WTException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return wtd;
	}

}
