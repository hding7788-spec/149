package ext.casc.process.assign;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.folder.FolderNotFoundException;
import wt.iba.value.IBAHolder;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.inf.library.WTLibrary;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pdmlink.PDMLinkProduct;
import wt.pom.Transaction;
import wt.projmgmt.admin.Project2;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.workflow.engine.WfEngineServerHelper;
import wt.workflow.engine.WfProcess;

import com.glaway.mpm.wcIntf.UserIntf;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.constants.Constants;
import ext.casc.process.ProcessConstants;
import ext.casc.process.ProcessTask;
import ext.casc.process.ProcessTaskItem;
import ext.casc.process.ProcessTaskLink;
import ext.casc.process.util.ProcessUtil;
import ext.casc.util.IBAUtility;
import ext.casc.util.WCUtil;
public class TechnicsReportProProcessor extends DefaultObjectFormProcessor {
    @Override
    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> arg1) throws WTException {
        FormResult form = new FormResult();
        IBAUtility ibaUtility = null;
        StringBuffer msg = new StringBuffer();
        FeedbackMessage message = new FeedbackMessage();
        // System.out.println("---------commandBean:"+commandBean.getMap());
        HttpServletRequest request = commandBean.getRequest();
        HttpSession session = commandBean.getRequest().getSession();
        // System.out.println("---------request:"+request.getParameterMap());
        List<String> oidList = new ArrayList<String>();
        System.out.println("---------oidList:" + oidList);
        String taskType = ProcessConstants.TASK_TYPE_BAOBIAOLEI;
        session.removeAttribute("deleteObject");
        session.removeAttribute("addObject");
        session.removeAttribute("rootParts");
        WTPart part = (WTPart)commandBean.getActionOid().getRefObject();
        FormProcessingStatus formProcessingStatus = FormProcessingStatus.SUCCESS;
        try {
        //新建工艺任务对象
        ProcessTask processTask = ProcessTask.newProcessTask();
        processTask.setName(part.getName());
        processTask.setNumber(part.getNumber());
        processTask.setVersion(part.getIterationDisplayIdentifier().toString());
//        processTask.setZhuzhichejian("zhuzhichejie");
//        processTask.setFuzhichejian("fuzhichejian");
//        processTask.setRenwuyaoqiu("renwuyaoqiu");
//        processTask.setEndDate(Timestamp.valueOf("2015-10-27 00:00:00"));
        processTask.setTaskState(ProcessConstants.TASK_STATE_JINGXINZHONG);
        processTask.setTaskType(taskType);
//        processTask.setRenwuyiju("renwuyiju");
        processTask.setContainer(part.getContainer());

        Folder folder = getFolder("/Default", part.getContainer());
        FolderHelper.assignLocation((FolderEntry) processTask, folder);

        processTask = (ProcessTask)PersistenceHelper.manager.save(processTask);

      //如果是工艺设计任务，则记录顶层part的oid
//        if(ProcessConstants.TASK_TYPE_GONGYISHEJI.equals(taskType)||ProcessConstants.TASK_TYPE_BAOBIAOLEI.equals(taskType)) {
//        	String topOid = (String)session.getAttribute("topOid");
//        	System.out.println("------------topOid-----"+topOid);
//        	ibaUtility = new IBAUtility(processTask);
//        	try {
//				ibaUtility.setIBAValue("TOPOID", topOid);
//			} catch (RemoteException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//        	try {
//				processTask = (ProcessTask) ibaUtility.updateAttributeContainer(processTask);
//			} catch (RemoteException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (ClassNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			ibaUtility.updateIBAHolder(processTask);
//        }

        //创建工艺任务与零部件的关联
        createProcessTaskLink(processTask, part);



        List<String> userInfo = UserIntf.getCurrentUserInfo();

        ProcessTaskItem processTaskItem = ProcessTaskItem.newProcessTaskItem();
        processTaskItem.setProcessTaskId(PersistenceHelper.getObjectIdentifier(processTask).getId());
        processTaskItem.setPartId(PersistenceHelper.getObjectIdentifier(part).getId());
        processTaskItem.setOwner(userInfo.get(0));
        processTaskItem.setName(part.getName());
        processTaskItem.setNumber(part.getNumber());
        processTaskItem.setVersion(part.getIterationDisplayIdentifier().toString());
        processTaskItem.setZhurengongyishi(userInfo.get(0));
        processTaskItem.setChejian("");
        processTaskItem.setExecutorRole(ProcessConstants.ROLE_GONGYIZUZHANG);
        processTaskItem.setIszhuzhi(false);
//        processTaskItem.setRenwuyaoqiu("");
        processTaskItem.setTaskType(taskType);
        processTaskItem.setTaskItemState(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN);
        processTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_BAOBIAOLEIRENWUZHIPAI);
		processTaskItem.setContainer(part.getContainer());
        FolderHelper.assignLocation((FolderEntry) processTaskItem, folder);
        PersistenceHelper.manager.save(processTaskItem);
        msg.append(ProcessConstants.JSP_MSG_SUCCESS);
        } catch (WTPropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
            form.setStatus(formProcessingStatus);
            message.addMessage(msg.toString());
            form.addFeedbackMessage(message);
            form.setNextAction(FormResultAction.NONE);
            form.setNextAction(FormResultAction.REFRESH_OPENER);
        }
        session.removeAttribute("oidList");
		return form;


    }

    public static void createProcessTaskLink(ProcessTask processTask,WTPart part) throws WTException{
        ProcessTaskLink link = ProcessTaskLink.newProcessTaskLink(part, processTask);
        PersistenceHelper.manager.save(link);
    }

    public static WfProcess initiateWfProcess(String templateName, HashMap data, WTContainer container,
            WTObject persistable) throws Exception {
        return initiateWfProcess(templateName, data, WTContainerRef.newWTContainerRef(container), persistable);
    }

    public static WfProcess initiateWfProcess(String templateName, HashMap data, WTContainerRef conref,
            WTObject persistable) throws Exception {
        try {
            wt.workflow.definer.WfProcessDefinition wfProcessDef = wt.workflow.definer.WfDefinerHelper.service
                    .getProcessDefinition(templateName, conref);
            if (wfProcessDef == null) {
                System.out.println("the woflowtemplate named " + templateName + "doesn't exist");
                return null;
            }
            WTContainer container = conref.getReferencedContainer();
            wt.inf.team.ContainerTeam team = null;
            if (container instanceof PDMLinkProduct) {
                team = wt.inf.team.ContainerTeamHelper.service.getContainerTeam((PDMLinkProduct) container);
            } else if (container instanceof WTLibrary) {
                team = wt.inf.team.ContainerTeamHelper.service.getContainerTeam((WTLibrary) container);
            } else if (container instanceof Project2) {
                team = wt.inf.team.ContainerTeamHelper.service.getContainerTeam((Project2) container);
            }
            wt.workflow.engine.WfProcess wfProcess = wt.workflow.engine.WfEngineHelper.service.createProcess(
                    wfProcessDef, team, conref);
            wfProcess.setName(templateName + "_" + System.currentTimeMillis());
            String user = wt.auth.Authentication.getUserName();
            wt.org.WTUser wtuser = wt.org.OrganizationServicesHelper.manager.getAuthenticatedUser(user);
            wt.org.WTPrincipalReference ref = wt.org.WTPrincipalReference.newWTPrincipalReference(wtuser);
            wfProcess.setCreator(ref);
            wfProcess = WfEngineServerHelper.service.setPrimaryBusinessObject(wfProcess, persistable);
            wt.workflow.engine.ProcessData pData = wfProcess.getContext();
            Iterator keys = data.keySet().iterator();
            while (keys.hasNext()) {
                String paramName = (String) keys.next();
                Object paramValue = data.get(paramName);
                pData.setValue(paramName, paramValue);
            }
            wfProcess = wfProcess.start(pData, 0, true);
            Team team2 = (Team)wfProcess.getTeamId().getObject();
            System.out.println("*************Map:"+team2.getRoles());
            return wfProcess;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    private static Folder getFolder(String path, WTContainer con) throws WTException {
        Folder folder = null;
        StringTokenizer tokenizer = new StringTokenizer(path, "/");
        String subPath = "";
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            subPath = subPath + "/" + token;
            if (subPath != null && !subPath.equalsIgnoreCase("")) {
                try {
                    folder = FolderHelper.service.getFolder(subPath, WTContainerRef.newWTContainerRef(con));
                } catch (FolderNotFoundException e) {
                    boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
                    folder = FolderHelper.service.createSubFolder(subPath, WTContainerRef.newWTContainerRef(con));
                    SessionServerHelper.manager.setAccessEnforced(flag);
                }
            }
        }
        return folder;
    }

}

