package com.glaway.mpm.pbom.datautilities;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.dom4j.DocumentException;

import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.util.WTException;
import wt.workflow.definer.UserEventVector;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfVotingEventAudit;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

import com.glaway.mpm.util.IBAHelper;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.workflow.NmWorkflowHelper;

import ext.casc.integrate.util.CldeUtil;
import ext.casc.util.Deserialize;

public class PbomTechnicsReportDataUtility extends AbstractDataUtility {

	@Override
	public Object getDataValue(String componentId, Object obj, ModelContext modelcontext)
			throws WTException {
		if(obj instanceof WTDocument) {
			IBAHelper ibaHelper = new IBAHelper((WTDocument)obj);
			if("gyNumber".equals(componentId)) {
				return ibaHelper.getIBAValue("PPNUMBER");
			} else if ("zgycldezt".equals(componentId)) {
				return ibaHelper.getIBAValue("CLDEZT");
			} else if ("gyState".equals(componentId)) {
				return ((WTDocument)obj).getState().getState().getDisplay(Locale.CHINA);
			} else if ("gyCreator".equals(componentId)) {
				return ((WTDocument)obj).getCreatorFullName();
			} else if ("sfclde".equals(componentId)) {
				String isCLDE = ibaHelper.getIBAValue("isCLDE");
				if(isCLDE==null||"".equals(isCLDE)){
					try {
						boolean flag = CldeUtil.isClde((WTDocument)obj);
						if(flag) {
							return "是";
						} else {
							return "否";
						}
					} catch (DocumentException e) {
						e.printStackTrace();
					}
				}else{
					return isCLDE;
				}

			}else if("proState".equals(componentId)){
                 return getProState(obj);
			}
		}
		return "";
	}

	public static String getProState(Object obj){
		String prostate = null;
		WfProcess proc = null;
		WTDocument document = (WTDocument) obj;
		String lifestate = document.getLifeCycleState().toString();
		try {
				QueryResult qrProcs = WfEngineHelper.service
						.getAssociatedProcesses(document, null, null);
				if (qrProcs.hasMoreElements()) {
					proc = (WfProcess) qrProcs.nextElement();
				}
				if (proc != null) {
					NmOid nmoid = new NmOid(proc);
					QueryResult qr = NmWorkflowHelper.service.getRoutingStatusData(nmoid);
					while (qr.hasMoreElements()) {
						Object obj1 = qr.nextElement();
						if (obj1 instanceof WorkItem) {
							WorkItem item = (WorkItem) obj1;
							if(!item.isComplete()){
							WfAssignedActivity wfaa = (WfAssignedActivity) item
									.getSource().getObject();
							String activityName = wfaa.getName();
							String assignee = item.getOwnership().getOwner()
									.getFullName();
							prostate = activityName + "(" + assignee + ")";
							}
						}
					}
				}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return prostate;
	}

}
