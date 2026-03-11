package ext.casc.workflow.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.epm.build.EPMBuildRule;
import wt.fc.ObjectReference;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;
import wt.workflow.engine.WfActivity;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changepackaged.ChangePackagedResultLink;
import ext.ases.changerequest.ChangeRequest;
import ext.ases.changerequest.ChangeRequestAffectLink;
import ext.ases.envelope.EnvelopeHelper;
import ext.ases.envelope.ProcessEnvelope;

public class WorkflowHelper {
    public static String HTML_LINK_AFTER = "</a>";
	public static WorkflowService service = new WorkflowServiceFwd();


	@SuppressWarnings("unchecked")
	public static void setWTObjectLifecycle(ProcessEnvelope pe,String state) throws WTException{
		List<Object> wtObjList = null;
		wtObjList = EnvelopeHelper.service.getAllMembers(pe);
		for(int i = 0 ; i < wtObjList.size() ; i++){
			Object obj = wtObjList.get(i);
			if(!WorkInProgressHelper.isCheckedOut((Workable)obj)){

            	String tmpState = ((LifeCycleManaged)obj).getState().getState().toString();
            	//已批准状态不允许再设置状态
            	if("APPROVED".equals(tmpState)){
            		continue;
            	}

            	if(obj instanceof RevisionControlled){
					RevisionControlled version = (RevisionControlled)obj;
					if("F".equals(version.getVersionIdentifier().getValue())){
						continue;
					}
				}
				LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)obj, State.toState(state));
				if (obj instanceof EPMDocument) {
					// 查找EPM关联的部件
					EPMDocument reEpm = (EPMDocument) obj;
					QueryResult qr = PersistenceHelper.manager.navigate(reEpm,
							EPMBuildRule.BUILD_TARGET_ROLE, EPMBuildRule.class,
							true);
					if (qr.hasMoreElements()) {
						WTPart part = (WTPart) qr.nextElement();
						if (!WorkInProgressHelper.isCheckedOut(part)) {
							LifeCycleHelper.service.setLifeCycleState(part,
									State.toState(state));
						}
					}
				}
			}else{
				System.out.println(((LifeCycleManaged)obj).getIdentity() +"已被检出，无法在"+pe.getNumber()+"流程中自动设置状态。");
			}
		}
	}

		public static void setWTObjectLifecycle(ChangePackaged changePackaged,String state) throws WTException{
		List<Object> wtObjList = new ArrayList<Object>();
		QueryResult qr = PersistenceHelper.manager.navigate(changePackaged, ChangePackagedResultLink.ROLE_BOBJECT_ROLE,
                ChangePackagedResultLink.class, true);
        while (qr.hasMoreElements()) {
            wtObjList.add(qr.nextElement());
        }
		for(int i = 0 ; i < wtObjList.size() ; i++){
			Object obj = wtObjList.get(i);
			if(!WorkInProgressHelper.isCheckedOut((Workable)obj)){

				String tmpState = ((LifeCycleManaged)obj).getState().getState().toString();
            	//已批准状态不允许再设置状态
            	if("APPROVED".equals(tmpState)){
            		continue;
            	}
            	if(obj instanceof RevisionControlled){
					RevisionControlled version = (RevisionControlled)obj;
					if("F".equals(version.getVersionIdentifier().getValue())){
						continue;
					}
				}
				LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)obj, State.toState(state));
				if (obj instanceof EPMDocument) {
					// 查找EPM关联的部件
					EPMDocument reEpm = (EPMDocument) obj;
					QueryResult qr2 = PersistenceHelper.manager.navigate(reEpm,
							EPMBuildRule.BUILD_TARGET_ROLE, EPMBuildRule.class,
							true);
					if (qr2.hasMoreElements()) {
						WTPart part = (WTPart) qr2.nextElement();
						if (!WorkInProgressHelper.isCheckedOut(part)) {
							LifeCycleHelper.service.setLifeCycleState(part,
									State.toState(state));
						}
					}
				}
			}else{
				System.out.println(((LifeCycleManaged)obj).getIdentity() +"已被检出，无法在"+changePackaged.getNumber()+"流程中自动设置状态。");
			}
		}
	}

	public static void setWTObjectLifecycle(WTObject obj,String state) throws WTException{
		if(obj instanceof ChangePackaged){
			setWTObjectLifecycle((ChangePackaged)obj,state);
		}else if(obj instanceof ProcessEnvelope){
			setWTObjectLifecycle((ProcessEnvelope)obj,state);
		}else if(obj instanceof ChangeRequest){
			ChangeRequest cr = (ChangeRequest)obj;
			QueryResult qr = PersistenceHelper.manager.navigate(cr, ChangeRequestAffectLink.ROLE_BOBJECT_ROLE,
					ChangeRequestAffectLink.class, true);
			while (qr.hasMoreElements()) {
				Object item = qr.nextElement();
				if(!WorkInProgressHelper.isCheckedOut((Workable)item)){
					LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)item, State.toState(state));
				}
			}
		}

    }

	  /**获取发放单位变量 selectUnitLink
     * @param primaryBusinessObject
     * @param ref
     * @param type
     * @return
     * @throws WTException
     */
    public static String getSelectUnitLink (ObjectReference ref) throws WTException {
        //800会签流程
        String link = "<a href=\"#\" onclick=\"window.open('netmarkets/jsp/ext/workflow/setUnitLink.jsp','设置电子会签单位','height=600, width=500, top=150, left=300')\">" + "设置电子会签单位" + HTML_LINK_AFTER;
        return link;
    }


    /**获取发放单位变量 getSelectUsersLink
     * @param primaryBusinessObject
     * @param ref
     * @param type
     * @return
     * @throws WTException
     */
    public static String getSelectUsersLink (ObjectReference ref) throws WTException {
        //800会签流程
        String link = "<a href=\"#\" onclick=\"window.open('netmarkets/jsp/ext/workflow/getSelectUsersLink.jsp','设置电子会签人员','height=600, width=500, top=150, left=300')\">" + "设置电子会签人员" + HTML_LINK_AFTER;
        return link;
    }

}
