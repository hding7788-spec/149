package com.glaway.mpm.mpmresource.processors;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.query.ClassAttribute;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;

import com.glaway.mpm.mpmresource.Constants;
import com.glaway.mpm.mpmresource.TypeNameConstants;
import com.glaway.mpm.util.GLLogger;
import com.glaway.mpm.util.TypeUtil;
import com.glaway.mpm.util.Util;
import com.glaway.mpm.util.WorkflowUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class StartGZWorkFlowProcessor extends DefaultObjectFormProcessor implements RemoteAccess {

	@SuppressWarnings("deprecation")
	@Override
	public FormResult doOperation(NmCommandBean cb, List<ObjectBean> list) throws WTException {
		FormResult formResult = new FormResult(FormProcessingStatus.SUCCESS);
		GLLogger.debug("=====StartGZWorkFlowProcessor====");
		NmOid nmoid = cb.getActionOid();
		Object obj = nmoid.getRef();
		if (!(obj instanceof WTPart)) {
			formResult.setStatus(FormProcessingStatus.FAILURE);
			formResult.addException(new Exception("The select is not Part!"));
			return formResult;
		}
		if (obj instanceof WTPart) {
			WTPart part = (WTPart) obj;
			if (!checkChildPart(part, part.getNumber())) {
				formResult.setStatus(FormProcessingStatus.FAILURE);
				formResult.addException(new Exception("该工装成品中存在不属于该成品的工装自制件！"));
			} else {
				// GMToolingDesignTask task =
				// GZCardHelper.getGMToolingDesignTaskByPartNumber(part.getNumber());
				// if (null == task) {
				// formResult.setStatus(FormProcessingStatus.FAILURE);
				// formResult.addException(new Exception("该工装零件对应的工装设计任务不存在！"));
				// return formResult;
				// } else if
				// (!Constants.RELEASED.equals(task.getState().getState())) {
				// formResult.setStatus(FormProcessingStatus.FAILURE);
				// formResult.addException(new
				// Exception("该工装零件对应的工装设计任务没有完成！"));
				// return formResult;
				// } else {
				WorkflowUtil.startProcess(part, Constants.gzDesignAuditorkflowTemplateName,
						Constants.gzDesignAuditorkflowTemplateName + "_" + part.getNumber() + "_" + part.getName());
				formResult.setNextAction(FormResultAction.JAVASCRIPT);
				formResult.setJavascript("alert(\"" + Constants.gzDesignAuditorkflowTemplateName + "启动!\");");
			}
		}
		return formResult;
	}

	private static boolean checkChildPart(WTPart parentPart, String partNumber) {
		boolean tag = true;
		try {
			QuerySpec querySpec = new QuerySpec(WTPart.class);
			querySpec.setAdvancedQueryEnabled(true);
			ClassAttribute caId = new ClassAttribute(WTPart.class, "masterReference.key.id");
			TypeUtil.getTypeQuery(WTPart.class, TypeNameConstants.gzToolingPartTypeName, querySpec);
			querySpec.appendAnd();
			querySpec.appendOpenParen();
			SubSelectExpression subSelectExpression = getChildPartMasterQuery(parentPart);
			querySpec.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), new int[] { 0 });
			querySpec.appendCloseParen();
			QueryResult queryResult = PersistenceHelper.manager.find(querySpec);
			queryResult = new LatestConfigSpec().process(queryResult);
			while (queryResult.hasMoreElements()) {
				WTPart part = (WTPart) queryResult.nextElement();
				if (!part.getNumber().contains(partNumber)) {
					tag = false;
					return tag;
				} else {
					checkChildPart(part, partNumber);
				}
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return tag;
	}

	private static SubSelectExpression getChildPartMasterQuery(WTPart parentPart) throws QueryException {
		QuerySpec querySpec = new QuerySpec();
		int partMasterIndex = querySpec.addClassList(WTPartMaster.class, false);
		int linkIndex = querySpec.addClassList(WTPartUsageLink.class, false);

		querySpec.appendSelect(new ClassAttribute(WTPartMaster.class, "thePersistInfo.theObjectIdentifier.id"),
				partMasterIndex, false);

		querySpec.appendWhere(new SearchCondition(WTPartUsageLink.class, "roleAObjectRef.key.id",
				SearchCondition.EQUAL, Util.getLongOid(parentPart)), linkIndex);
		querySpec.appendAnd();
		querySpec.appendWhere(new SearchCondition(WTPartUsageLink.class, "roleBObjectRef.key.id", WTPartMaster.class,
				"thePersistInfo.theObjectIdentifier.id"), linkIndex, partMasterIndex);
		return new SubSelectExpression(querySpec);
	}

	public static void test() throws RemoteException, WTException {
		WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, "1492013");
		checkChildPart(part, "");
	}

	public static void main(String[] args) throws WTException, RemoteException, InvocationTargetException {
		RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
		methodServer.setUserName("wcadmin");
		methodServer.setPassword("wcadmin");
		methodServer.invoke("test", StartGZWorkFlowProcessor.class.getName(), null, null, null);
	}
}
