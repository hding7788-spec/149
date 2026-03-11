package com.glaway.mpm.mpmresource.processors;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import wt.doc.WTDocument;
import wt.iba.value.IBAHolder;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.util.IBAUtility;

public class EditPrintFlagProcessor extends CustomerObjectFormProcessor {
    @Override
    public FormResult doOperation(NmCommandBean arg0, List<ObjectBean> arg1) throws WTException {
        FormResult formResult = new FormResult();
        HttpServletRequest request = arg0.getRequest();
        Map map = request.getParameterMap();
        String[] pflag = (String[])map.get("Print");
        IBAHolder actionObj = (IBAHolder)arg0.getActionOid().getRefObject();
        changePrintFlag(pflag[0],actionObj);

        return super.doOperation(arg0, arg1);
    }

    private void changePrintFlag(String print,IBAHolder doc) throws WTException {
    	IBAUtility iba = new IBAUtility((IBAHolder)doc);
		try {
			iba.setIBAValue( "PrintFlag", print);
			doc = iba.updateAttributeContainer(doc);
			iba.updateIBAHolder(doc);
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
