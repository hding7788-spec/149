package com.glaway.mpm.util;

import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.FormatContentHolder;
import wt.epm.EPMDocument;
import wt.epm._EPMDocument;
import wt.epm.structure.EPMMemberLink;
import wt.epm.structure.EPMReferenceLink;
import wt.epm.structure._EPMReferenceLink;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.pds.StatementSpec;
import wt.pom.PersistenceException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.vc.Iterated;
import wt.vc.Mastered;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;
import wt.vc.struct._IteratedUsageLink;
import ext.casc.util.CommonUtil;

import com.glaway.mpm.model.data.CmAttachment;
import com.glaway.mpm.parameter.util.PersistableUtil;

public class EPMDocUtil {

	public static QueryResult getEPMDocumentLikeNumber(String number) throws WTException {
		int[] index = { 0 };
		QuerySpec qs = new QuerySpec(EPMDocument.class);
		qs.appendWhere(new SearchCondition(EPMDocument.class, _EPMDocument.NUMBER, SearchCondition.LIKE, number, false),
				index);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		qr = new LatestConfigSpec().process(qr);
		return qr;
	}

	public static EPMDocument getEPMDocumentByNumber(String number) throws WTException {
		int[] index = { 0 };
		QuerySpec qs = new QuerySpec(EPMDocument.class);
		qs.appendWhere(new SearchCondition(EPMDocument.class, _EPMDocument.NUMBER, SearchCondition.EQUAL, number, false),
				index);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		qr = new LatestConfigSpec().process(qr);
		if(qr.hasMoreElements()){
			EPMDocument epm = (EPMDocument) qr.nextElement();
			return epm;
		}
		return null;
	}

	public static EPMDocument getEpmByOid(long oid) throws Exception {
		QuerySpec qSpec = new QuerySpec(EPMDocument.class);
		int[] index = { 0 };
		SearchCondition sCondition = new SearchCondition(EPMDocument.class,
				"thePersistInfo.theObjectIdentifier.id", SearchCondition.EQUAL,
				oid);
		qSpec.appendWhere(sCondition, index);
		QueryResult qResult = PersistenceHelper.manager
				.find((StatementSpec) qSpec);
		EPMDocument epm = null;
		if (qResult.hasMoreElements()) {
			epm = (EPMDocument) qResult.nextElement();
		}
		return epm;
	}

    public static List<EPMDocument> getMemberEPMDoc(EPMDocument epm3d) throws WTException, RemoteException, InvocationTargetException{
    	List<EPMDocument> childList = new ArrayList<EPMDocument>();
		QueryResult qr = PersistenceHelper.manager.navigate(epm3d, _IteratedUsageLink.USES_ROLE, EPMMemberLink.class, false);
		while (qr.hasMoreElements()) {
		    EPMMemberLink link = (EPMMemberLink) qr.nextElement();
		    // 过滤掉隐含成员
		    if (link.isSuppressed() == true) {
		        continue;
		    }
		    EPMDocument latestChild = (EPMDocument) getIteratedByMaster(link.getUses());
		    childList.add(latestChild);
		}
		return childList;
}

    public static List<EPMDocument> getReferenceEPMDoc(EPMDocument epm3d) throws WTException, RemoteException, InvocationTargetException{
    	List<EPMDocument> childList = new ArrayList<EPMDocument>();
    	QueryResult qr = PersistenceHelper.manager.navigate(epm3d.getMaster(), _EPMReferenceLink.REFERENCED_BY_ROLE, EPMReferenceLink.class, false);
		while (qr.hasMoreElements()) {
		    EPMReferenceLink link = (EPMReferenceLink) qr.nextElement();
		    EPMDocument latestChild =  link.getReferencedBy();
		    childList.add(latestChild);
		}
		return childList;
}

    /**
     * 通过master查询对象的最新版本
     *
     * @param mst
     *            Mastered
     * @return Iterated
     * @throws RemoteException
     * @throws InvocationTargetException
     */
    public static Iterated getIteratedByMaster(Mastered mst) throws RemoteException, InvocationTargetException {

        Iterated itr = null;

        if (mst != null) {
            QueryResult qr;
            try {
                qr = VersionControlHelper.service.allIterationsOf(mst);
                if (qr.hasMoreElements()) {
                    itr = (Iterated) qr.nextElement();
                }
            } catch (PersistenceException e) {
                e.printStackTrace();
            } catch (WTException e) {
                e.printStackTrace();
            }

        }
        return itr;
    }

    public static ApplicationData getPrimaryContent(FormatContentHolder holder) {
    	ApplicationData appData = null;
		try {
			appData = (ApplicationData) ContentHelper.service.getPrimary(holder);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
    	return appData;
    }

    public static CmAttachment getAnotationFile(EPMDocument epmDoc) throws WTException {
    	CmAttachment attachment = null;
    	QueryResult qr = ContentHelper.service.getContentsByRole(epmDoc, ContentRoleType.SECONDARY);

    	String version = PersistableUtil.getVersion(epmDoc);
    	while (qr.hasMoreElements()) {
    		ApplicationData appData = (ApplicationData) qr.nextElement();
    		String fileName = appData.getFileName();
    		if (fileName.endsWith(".xml") && fileName.contains(version)) {
    			attachment = new CmAttachment();
    			attachment.setFileName(fileName);
    			attachment.setBytes(CommonUtil.applicationDataToByte(appData));
    		}
    	}
    	return attachment;
    }
}
