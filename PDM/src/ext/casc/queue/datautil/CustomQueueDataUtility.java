package ext.casc.queue.datautil;


import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Vector;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.UrlDisplayComponent;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmURLFactoryBean;
import com.ptc.netmarkets.util.misc.NetmarketURL;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;

import ext.ases.changepackaged.ChangePackaged;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.queue.builder.CustomQueueEntryTable;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.ObjectIdentifier;
import wt.fc.PersistInfo;
import wt.fc.WTObject;
import wt.maturity.PromotionNotice;
import wt.org.WTPrincipalReference;
import wt.part.WTPart;
import wt.queue.MethodArgument;
import wt.queue.QueueEntry;
import wt.queue.jca.QueueHyperLinker;
import wt.util.WTException;
import wt.workflow.engine.WfConnector;
import wt.workflow.engine.WfConnectorFunction;
import wt.workflow.engine.WfProcess;
import wt.workflow.robots.WfExpressionRobot;
import wt.workflow.robots.WfInternalMethod;
import wt.workflow.robots.WfTimerActivity;
import wt.workflow.work.WfAssignedActivity;


/**
 * 增加队列条目属性处理
 * @author Jacky
 * @date 2025年9月12日下午2:29:02
 * xconfmanager -t codebase/service.properties -s wt.services/svc/default/com.ptc.core.components.descriptor.DataUtility/CustomQueueDataUtility/java.lang.Object/0=ext.casc.queue.datautil.CustomQueueDataUtility/singleton -p
 */
public class CustomQueueDataUtility extends AbstractDataUtility{

	@Override
	public Object getDataValue(String arg0, Object arg1, ModelContext arg2) throws WTException {
		QueueEntry queueEntry=(QueueEntry)arg1;
		
		PersistInfo persistInfo=queueEntry.getPersistInfo();
		ObjectIdentifier obj=persistInfo.getObjectIdentifier();
		WfProcess wfProcess=getRelatedWfProcess(queueEntry);
		if(wfProcess==null) {
			return "";
		}
		
		if(CustomQueueEntryTable.ATTR_PROCESSNAME.equals(arg0)) {
			return wfProcess.getName();
		} else if(CustomQueueEntryTable.ATTR_PRIMARYOBJECTNUMBER.equals(arg0)) {
			WTObject pbo =(WTObject)wfProcess.getContext().getVariable("primaryBusinessObject").getValue();
			if(pbo==null) {
				return "";
			}
			UrlDisplayComponent urlDisplayComponent=getUrlDisplayComponent(pbo);
			return urlDisplayComponent;
		} else if(CustomQueueEntryTable.ATTR_PRINCIPAL.equals(arg0)) {
			WTPrincipalReference wtPrincipalReference=wfProcess.getCreator();
			if(wtPrincipalReference!=null) {
				return wtPrincipalReference.getFullName();
			}
			
		} else if(CustomQueueEntryTable.ATTR_STARTDATE.equals(arg0)) {
			Timestamp timestamp=wfProcess.getStartTime();
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
			String targetDateStr = dateFormat.format(timestamp);
			return targetDateStr;
		} else if(CustomQueueEntryTable.ATTR_CURRENTNODE.equals(arg0)) {
			Vector vt=queueEntry.getArgs();
			QueueHyperLinker arg7 = new QueueHyperLinker();
			Iterator arg6 = vt.iterator();
			while (arg6.hasNext()) {
				Object art6Obj=arg6.next();
				if(art6Obj instanceof MethodArgument) {
					MethodArgument methodArgument=(MethodArgument)art6Obj;
					Object arg=methodArgument.getArg();
					if(arg instanceof ObjectIdentifier) {
						ObjectIdentifier objectIdentifier=(ObjectIdentifier)arg;
						NmOid nmoid = new NmOid(objectIdentifier);
						Object refObj=nmoid.getRefObject();
						if(refObj instanceof WfConnector) {
							WfConnector wfConnector=(WfConnector)refObj;
							WfConnectorFunction wfConnectorFunction=wfConnector.getConnectorFunction();
							if(wfConnectorFunction!=null) {
								String result=wfConnectorFunction.getDisplay();
								return result;
							}
						} else if(refObj instanceof WfExpressionRobot) {
							WfExpressionRobot wfExpressionRobot=(WfExpressionRobot)refObj;
							String result=wfExpressionRobot.getName();
							return result;
						} else if(refObj instanceof WfInternalMethod) {
							WfInternalMethod WfInternalMethod=(WfInternalMethod)refObj;
							String name=WfInternalMethod.getName();
							return name;
						} else if(refObj instanceof WfAssignedActivity) {
							WfAssignedActivity wfAssignedActivity=(WfAssignedActivity)refObj;
							return wfAssignedActivity.getName();
						} else if(refObj instanceof WfProcess) {
							WfProcess targetProcess=(WfProcess)refObj;
							return targetProcess.getName();
						} else if(refObj instanceof WfTimerActivity) {
							WfTimerActivity wfTimerActivity=(WfTimerActivity)refObj;
							return wfTimerActivity.getName();
						}
					}
				}
			}
		}
		
		return "";
	}

	private  UrlDisplayComponent getUrlDisplayComponent(WTObject wtobject) throws WTException {
		String name = "";
		UrlDisplayComponent urldisplaycomponent = null;
		NmOid nmoid = new NmOid(wtobject.getPersistInfo().getObjectIdentifier());
		NmURLFactoryBean nmurlfactorybean = new NmURLFactoryBean();
		nmurlfactorybean.setRequestURI(NetmarketURL.BASEURL);
		String url = NetmarketURL.buildURL(nmurlfactorybean, "object", "view", nmoid, null);
		name=getPrimaryObjectNumber(wtobject);
		urldisplaycomponent = new UrlDisplayComponent(name, name, url);
		urldisplaycomponent.setTarget("_blank");
		return urldisplaycomponent;
	}

	/** 
	  * @Description: 获取流程主要对象的编号
	  * @date 2025年9月12日下午3:54:15
	  * @author Jacky
	  * @param pbo
	  * @return  
	  * @return 
	*/
	private String getPrimaryObjectNumber(WTObject pbo) {
		if(pbo instanceof WTDocument) {
			return ((WTDocument)pbo).getNumber();
		} else if(pbo instanceof WTPart) {
			return ((WTPart)pbo).getNumber();
		} else if(pbo instanceof EPMDocument) {
			return ((EPMDocument)pbo).getNumber();
		} else if(pbo instanceof MPMProcessPlan) {
			return ((MPMProcessPlan)pbo).getNumber();
		} else if(pbo instanceof MPMOperation) {
			return ((MPMOperation)pbo).getNumber();
		} else if (pbo instanceof ProcessEnvelope) {
			return ((ProcessEnvelope)pbo).getNumber();
		} else if (pbo instanceof WTChangeActivity2) {
			return ((WTChangeActivity2)pbo).getNumber();
		} else if (pbo instanceof WTChangeOrder2) {
			return ((WTChangeOrder2)pbo).getNumber();
		} else if (pbo instanceof WTChangeRequest2) {
			return ((WTChangeRequest2)pbo).getNumber();
		}  else if (pbo instanceof PromotionNotice) {
			return ((PromotionNotice)pbo).getNumber();
		} else if (pbo instanceof ChangePackaged) {
			return ((ChangePackaged)pbo).getNumber();
		}
		
		return "";
	}

	/** 
	  * @Description: 获取队列关联的流程
	  * @date 2025年9月12日下午3:45:42
	  * @author Jacky
	  * @param queueEntry
	  * @return
	  * @throws WTException  
	  * @return 
	*/
	private WfProcess getRelatedWfProcess(QueueEntry queueEntry) throws WTException {
		Vector vt=queueEntry.getArgs();
		QueueHyperLinker arg7 = new QueueHyperLinker();
		Iterator arg6 = vt.iterator();
		while (arg6.hasNext()) {
			Object obj=arg6.next();
			Object arg9 = arg7.isWfProcess(obj);
			if(arg9 instanceof WfProcess) {
				return (WfProcess)arg9;
			}
		}
		return null;
	}

}
