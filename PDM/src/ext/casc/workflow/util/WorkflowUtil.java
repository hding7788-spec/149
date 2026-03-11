package ext.casc.workflow.util;

import com.ptc.core.meta.common.TypeIdentifierHelper;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.ases.envelope.EnvelopeHelper;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.change.ChangeHelper;
import ext.casc.doc.DocumentUtil;
import ext.casc.part.CSCPart;
import ext.casc.part.PartHelper;
import ext.casc.workflow.CSCWorkflowException;
import wt.change2.WTChangeOrder2;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.fc.*;
import wt.inf.container.WTContainer;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.pom.PersistenceException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.representation.Representation;
import wt.representation.RepresentationHelper;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.vc.Mastered;
import wt.vc.VersionControlHelper;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "deprecation"})
public class WorkflowUtil {
	/**
	 * 
	 * @param activity
	 * @param variable
	 * @return
	 */
	public static Object getActivityVariableValue(WfActivity activity, String variable) {
		try {
			ProcessData pd = activity.getContext();
			return pd.getValue(variable);
		} catch (Exception e) {
			return null;
		}
	}
  
   /**
    * 
    * @param cb
    * @return
    * @throws WTException
    */
   public static WorkItem getWorkItem(NmCommandBean cb) throws WTException
   {
      WorkItem ret = null;

      NmOid oid = cb.getPageOid() == null ? cb.getPrimaryOid() == null ? null : cb.getPrimaryOid() : cb.getPageOid();
      if (oid != null && oid.getRef() instanceof WorkItem)
      {
         ret = (WorkItem) oid.getRef();
      }
      return ret;
   }
   
	/**
	 * 根据用户名查询用户，返回WTUser，用户名是唯一的
	 * 
	 * @param userName
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings("deprecation")
	public static WTUser getWTUserByName(String userName) throws WTException {
		WTUser user = null;
		QuerySpec qs = new QuerySpec(WTUser.class);
		qs.appendWhere(new SearchCondition(WTUser.class, WTUser.NAME,
				SearchCondition.EQUAL, userName));
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		while (qr.hasMoreElements()) {
			user = (WTUser) qr.nextElement();
		}
		return user;
	}
	   
	public static List<String> getEndPartList(String oid){
		List<String> endPartList = null;
		ReferenceFactory rf = new ReferenceFactory();
		WorkItem wi = null;
		try {
			wi = (WorkItem) rf.getReference(oid).getObject();
		} catch (WTRuntimeException e) {
			CSCWorkflowException e1 = new CSCWorkflowException("通过oid实例化对象出错!",e);
			e1.printMessage();
		} catch (WTException e) {
			CSCWorkflowException e1 = new CSCWorkflowException("通过oid实例化对象出错!",e);
			e1.printMessage();
		}
		
		if(wi != null){
			WTContainer wtc = null;
			WfActivity wfAct = (WfActivity) wi.getSource().getObject();
			Persistable pbo = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");
			
			if(pbo instanceof WTPart){
				WTPart part = (WTPart) pbo;
				wtc = part.getContainer();
			}else if(pbo instanceof WTDocument){
				WTDocument doc = (WTDocument)pbo;
				wtc = doc.getContainer();
				}if(pbo instanceof ProcessEnvelope){
				ProcessEnvelope proen = (ProcessEnvelope)pbo;
				wtc = proen.getContainer();
			}
			
			String cOid = PersistenceHelper.getObjectIdentifier(wtc).toString();
			
			try {
				endPartList = PartHelper.service.getEndPartList(cOid);
			} catch (WTException e) {
				CSCWorkflowException e1 = new CSCWorkflowException("读取成品数据出错!",e);
				e1.printMessage();
			}
			if (endPartList == null)
				return new ArrayList();
		}

		return endPartList;
	}
	
	public static List<WTPart> getEndItemList(String oid){
		List<WTPart> endPartList = null;
		ReferenceFactory rf = new ReferenceFactory();
		WorkItem wi = null;
		try {
			wi = (WorkItem) rf.getReference(oid).getObject();
		} catch (WTRuntimeException e) {
			CSCWorkflowException e1 = new CSCWorkflowException("通过oid实例化对象出错!",e);
			e1.printMessage();
		} catch (WTException e) {
			CSCWorkflowException e1 = new CSCWorkflowException("通过oid实例化对象出错!",e);
			e1.printMessage();
		}
		
		if(wi != null){
			WTContainer wtc = null;
			WfActivity wfAct = (WfActivity) wi.getSource().getObject();
			Persistable pbo = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");
			
			if(pbo instanceof WTPart){
				WTPart part = (WTPart) pbo;
				wtc = part.getContainer();
			}else if(pbo instanceof WTDocument){
				WTDocument doc = (WTDocument)pbo;
				wtc = doc.getContainer();
				}if(pbo instanceof ProcessEnvelope){
				ProcessEnvelope proen = (ProcessEnvelope)pbo;
				wtc = proen.getContainer();
			}
			
			String cOid = PersistenceHelper.getObjectIdentifier(wtc).toString();
			endPartList = CSCPart.getEndItemPartByContext(cOid);
			
			if (endPartList == null)
				return new ArrayList();
		}

		return endPartList;
	}
		/**
	 * 是否所有的WTObject在指定的生命周期里
	 * @return
	 */
	public static String isAllWTObjectAtAssignedState(List wtObject, String errorMsg, State... states) {
		if (wtObject == null || states == null) return "指定对象为空！";
		StringBuffer returnMsg = new StringBuffer("");
		for (Object obj : wtObject) {
			returnMsg.append(isWTObjectAtAssignedState((WTObject) obj, errorMsg, states) + "\n");
		}
		String rs = returnMsg.toString();
		return "".equals(rs) ? WorkflowValidationUtil.VALIDATE_OK : rs;
	}
	
	public static String isAllWTObjectAtAssignedStateNullSafe(List wtObject, String errorMsg, State... states) {
		if (wtObject == null || states == null) return "指定对象为空！";
		StringBuffer returnMsg = new StringBuffer("");
		for (Object obj : wtObject) {
			String checkStr = isWTObjectAtAssignedState((WTObject) obj, errorMsg, states);
			if (!WorkflowValidationUtil.VALIDATE_OK.equals(checkStr)) {
				returnMsg.append(checkStr + "\n");
			}
		}
		String rs = returnMsg.toString();
		return "".equals(rs) ? WorkflowValidationUtil.VALIDATE_OK : rs;
	}
	
	public static String isWTObjectAtAssignedState(WTObject wtObject, String errorMsg, State... states) {
		String objId = ((WTObject) wtObject).getIdentity();
		if (LifeCycleManaged.class.isAssignableFrom(wtObject.getClass())) {
			LifeCycleManaged lifecylceOjb = (LifeCycleManaged) wtObject;
			boolean inState = false;
			for (State state : states) {
				inState |= lifecylceOjb.getLifeCycleState().equals(state);
				if (inState) break;
			}
			if (!inState) return objId + errorMsg;
		} else {
			return objId + "不可判断生命周期！";
		}
		return WorkflowValidationUtil.VALIDATE_OK;
	}
	
	public static boolean isWTObjectAtAssignedState(WTObject wtObject, State... states) {
		String objId = ((WTObject) wtObject).getIdentity();
		if (LifeCycleManaged.class.isAssignableFrom(wtObject.getClass())) {
			LifeCycleManaged lifecylceOjb = (LifeCycleManaged) wtObject;
			boolean inState = false;
			for (State state : states) {
				inState |= lifecylceOjb.getLifeCycleState().equals(state);
				if (inState) break;
			}
			return inState;
		} else {
			return false;
		}
	}
	
	public static WTObject getLatestVersionOfWTObject(Mastered mastered) throws PersistenceException, WTException {
		QueryResult allVersionsQs = VersionControlHelper.service.allVersionsOf(mastered);
		// 最新的在第一个元素
		WTObject childLatest = null;
		if (allVersionsQs.hasMoreElements()) {
			childLatest = (WTObject) allVersionsQs.getEnumeration().nextElement();
		} else {
			// 找不到最新版本，系统错误
			throw new WTException(mastered.getIdentity() + " 无法找到最新的版本！");
		}
		return childLatest;
	}
	
	
	/**
	 * 检测图档是否有同名称的用于打印的pdf文件
	 * @return
	 */
	public static boolean isDOC_TUDANG_HasPDF(WTDocument wtDoc) {
		if (TypeIdentifierHelper.getType(wtDoc).toExternalForm().contains("DRAWING_DOC")) {
			Map docRelatedObjMap = DocumentUtil.getAttachmentsFromDocument(wtDoc, ContentRoleType.SECONDARY);
			boolean hasSameNamePDF = false;
			for (Object key : docRelatedObjMap.keySet()) {
				// 检查是否有与WTDocument相同名字的pdf附件
				if ((wtDoc.getName() + ".pdf").equals(key.toString())) {
					hasSameNamePDF = true;
					break;
				}
			}
			if (!hasSameNamePDF) return false;
		}
		return false;
	}
	
	
	/**
	 * 设置ecn的改前/改后数据状态
	 * @param ecn
	 * @param state
	 * @param beforeOrAfter
	 * @throws WTException 
	 */
	public static void setECNAfterWTObjectStatus(WTChangeOrder2 ecn, String state, String scope) throws WTException {
		List itemList = new ArrayList();
		if ("before".equals(scope)) {
			itemList = ChangeHelper.getChangeAffectItem(ecn);
		} else if ("after".equals(scope)) {
			itemList = ChangeHelper.getChangeResultItem(ecn);
		} else if ("all".equals(scope)) {
			itemList.addAll(ChangeHelper.getChangeAffectItem(ecn));
			itemList.addAll(ChangeHelper.getChangeResultItem(ecn));
		} else {
			throw new WTException("必须是 ECNConstant　下 before/after/all中的一种");
		}
		for (Object itemObj : itemList) {
			if (itemObj instanceof LifeCycleManaged && !WorkInProgressHelper.isCheckedOut((Workable)itemObj) ) {
				LifeCycleManaged lifeManagedObj = (LifeCycleManaged) itemObj;
				LifeCycleHelper.service.setLifeCycleState(lifeManagedObj, State.toState(state));
			}
		}
	}
	
	public static String getTypeIdentify(Object obj){
//		CSCDebug.outDebugInfo("Now you enter getTypeIdentify function!!!");
		String type = TypeIdentifierHelper.getType(obj).toString();
		return type;
	}

	public static boolean isPackagedPart(String oid) throws WTRuntimeException, WTException{
		boolean flag = false;
		ReferenceFactory rf = new ReferenceFactory();
		WorkItem wi = (WorkItem) rf.getReference(oid).getObject();
		WfActivity wfAct = (WfActivity) wi.getSource().getObject();
		Persistable pbo = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");
		if(pbo instanceof ProcessEnvelope){
			ProcessEnvelope pe = (ProcessEnvelope)pbo;
			RevisionControlled rc = EnvelopeHelper.service.getTopObject(pe);
			if(rc != null){
				flag = true;
			}
		}
		return flag;
	}

	public static boolean isHasRepresentation(WTObject pbo) throws WTException {
		if(pbo instanceof WTDocument){
			WTDocument document = (WTDocument) pbo;
			Representation representation = RepresentationHelper.service.getDefaultRepresentation(document);
			if(representation != null){
				return true;
			}
		}
		return false;
	}

	//工艺更改单不再校验附件 针对老流程直接通过 不校验
	public static boolean checkChangeOrderAttach(Object pbo){
		return true;
	}

	public static WfProcess getWfProcessByName(Persistable persistable, String wfName) throws WTException {
		WfProcess process = null;
		QueryResult qr = WfEngineHelper.service.getAssociatedProcesses(persistable, null, null);
		while(qr.hasMoreElements()) {
			process = (WfProcess)qr.nextElement();
			if (process.getName().equals(wfName)) {
				return process;
			}
		}
		return null;
	}

	public static WfProcess getWfProcessLikeName(Persistable persistable, String wfName) throws WTException {
		WfProcess process = null;
		QueryResult qr = WfEngineHelper.service.getAssociatedProcesses(persistable, null, null);
		while(qr.hasMoreElements()) {
			process = (WfProcess)qr.nextElement();
			if (process.getName().contains(wfName)) {
				return process;
			}
		}
		return null;
	}
}
