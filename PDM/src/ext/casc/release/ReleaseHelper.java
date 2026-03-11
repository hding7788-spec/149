package ext.casc.release;

import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changepackaged.ChangePackagedResultLink;
import ext.ases.envelope.EnvelopeHelper;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.change.ChangeHelper;
import ext.casc.preview.Preview;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.fc.*;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;

import java.util.ArrayList;
import java.util.List;

public class ReleaseHelper {

	public static String HTML_DOWNLOAD_PRINT_APPLY_LINK_PRE = "<a href=\"netmarkets/jsp/ext/ases/document/downloadPrintApplyElecFile.jsp?oid=";
	public static String HTML_DOWNLOAD_CAPP_PRINT_PRE = "<a href=\"netmarkets/jsp/ext/ases/document/downloadCappPrintFile.jsp?oid=";
	public static String HTML_LINK_MID = "\">";
	public static String HTML_LINK_BLANK_MID = "\" target=\"_blank\">";
	public static String HTML_LINK_AFTER = "</a>";

	public static String HTML_VIEWYQJOUTLINK_PRE = "<a href=\"netmarkets/jsp/ext/sast/center/workflow/viewChaoMuluMemberLink.jsp?oid=OR:";

	public static String getViewYqjOutLink(WTObject pbo, ObjectReference self) {
		String objOid = PersistenceHelper.getObjectIdentifier(pbo)
				.toString();
		String link = HTML_VIEWYQJOUTLINK_PRE + objOid+ HTML_LINK_BLANK_MID
				+ "元器件目录送审单" + HTML_LINK_AFTER;
		return link;
	}

	public static String getPrintApplyDocDownloadLink(WTObject pbo,
			ObjectReference self) {
		WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
		try {
			WfProcess proc = wfa.getParentProcess();
			String processOid = PersistenceHelper.getObjectIdentifier(proc)
					.toString();
			String objOid = PersistenceHelper.getObjectIdentifier(pbo)
					.toString();
			String link = HTML_DOWNLOAD_PRINT_APPLY_LINK_PRE + objOid
					+ "&processOid=" + processOid + HTML_LINK_BLANK_MID
					+ "下载文件" + HTML_LINK_AFTER;
			return link;
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public static String getPreviewDownloadLink(WTObject pbo,
			ObjectReference self) {
		WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
		try {
			WfProcess proc = wfa.getParentProcess();
			String processOid = PersistenceHelper.getObjectIdentifier(proc)
					.toString();
			String objOid = PersistenceHelper.getObjectIdentifier(pbo)
					.toString();
			String link =  "<a href=\"netmarkets/jsp/ext/casc/preview/downloadPreviewFile.jsp?oid=" + objOid
					+ "&processOid=" + processOid + HTML_LINK_BLANK_MID
					+ "下载文件" + HTML_LINK_AFTER;
			return link;
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	public static String getCAPPPrintDownloadLink(WTObject pbo,
			ObjectReference self) {
		WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
		try {
			WfProcess proc = wfa.getParentProcess();
			String processOid = PersistenceHelper.getObjectIdentifier(proc)
					.toString();
			String objOid = PersistenceHelper.getObjectIdentifier(pbo)
					.toString();
			String link = HTML_DOWNLOAD_CAPP_PRINT_PRE + objOid
					+ "&processOid=" + processOid + HTML_LINK_BLANK_MID
					+ "下载文件" + HTML_LINK_AFTER;
			return link;
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * @param oid
	 * @return
	 * @throws WTException
	 * @throws WTRuntimeException
	 */
	public static List<String> getPrintApplyRelatedDoc(String oid) throws WTRuntimeException, WTException {
	    String newOid="";
		List<String> oidList = new ArrayList<String>();
		wt.fc.ReferenceFactory factory = new wt.fc.ReferenceFactory();
	    Persistable persistable1 = factory.getReference(oid).getObject();
	    if (persistable1 instanceof WTDocument) {
	        WTDocument doc = (WTDocument) persistable1;
	        WTDocument verDoc = (WTDocument)VersionControlHelper.service.getLatestIteration(doc, true);
	                oidList.add(PersistenceHelper.getObjectIdentifier(
                            (WTObject) verDoc).getStringValue());
	    }
		ReferenceFactory rf = new ReferenceFactory();
		try {
			Persistable persistable = rf.getReference(oid).getObject();
			if (persistable instanceof ProcessEnvelope) {
				ProcessEnvelope proen = (ProcessEnvelope) persistable;
				oidList.add(PersistenceHelper.getObjectIdentifier(proen).getStringValue());
				List<Object> objList = new ArrayList<Object>();
				objList = EnvelopeHelper.service.getAllMembers(proen);
				for (Object obj : objList) {
					if (!(obj instanceof WTPart)) {
						oidList.add(PersistenceHelper.getObjectIdentifier(
								(WTObject) obj).getStringValue());
					}
				}
			} else if (persistable instanceof WTChangeOrder2) {
				WTChangeOrder2 changeOrder2 = (WTChangeOrder2) persistable;
				ArrayList list = ChangeHelper.getChangeResultItem(changeOrder2);
				if (list != null) {
					for (Object obj : list) {
						if (!(obj instanceof WTPart)) {
							oidList.add(PersistenceHelper.getObjectIdentifier(
									(WTObject) obj).getStringValue());
						}
					}
				}
			} else if (persistable instanceof ChangePackaged) {
				ChangePackaged change = (ChangePackaged) persistable;
				oidList.add(PersistenceHelper.getObjectIdentifier(change).getStringValue());
				QueryResult qr = PersistenceHelper.manager.navigate(change,
						"theRevisionControlled",
						ChangePackagedResultLink.class, true);
				while (qr.hasMoreElements()) {
					RevisionControlled revision = (RevisionControlled) qr
							.nextElement();
					if (!(revision instanceof WTPart)) {
						oidList.add(PersistenceHelper.getObjectIdentifier(
								revision).getStringValue());
					}
				}
			} else if(persistable instanceof Preview) {
				Preview preview = (Preview) persistable;
				oidList.add(PersistenceHelper.getObjectIdentifier(preview).getStringValue());
			}

		} catch (WTRuntimeException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return oidList;
	}

}
