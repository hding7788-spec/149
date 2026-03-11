package ext.casc.process.assign;

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
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.folder.FolderNotFoundException;
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
import wt.workflow.engine.WfEngineServerHelper;
import wt.workflow.engine.WfProcess;

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
import ext.casc.workflow.signtrue.zp.SignatureService;

public class ProGYZZProcessor extends DefaultObjectFormProcessor {

    @Override
    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> arg1) throws WTException {
        FormResult form = new FormResult();
        // System.out.println("---------commandBean:"+commandBean.getMap());
        HttpServletRequest request = commandBean.getRequest();
        HttpSession session = commandBean.getRequest().getSession();
        // System.out.println("---------request:"+request.getParameterMap());
        List<String> oidList = new ArrayList<String>();
        System.out.println("---------oidList:" + oidList);

        String taskType = (String)session.getAttribute("TaskType");
        System.out.println("---------taskType:" + taskType);
        WTUser currentUser = (WTUser)SessionHelper.getPrincipal();
        System.out.println("---------currentUser:" + currentUser.getFullName());

        //用于存储各零件的任务要求
        Map<String, String> rwyqMap = new HashMap<String, String>();
        //用于存储各零件的任务计划完成时间
        Map<String, String> wcsjMap = new HashMap<String, String>();
      //用于存储各零件的材料定额计划完成时间
        Map<String, String> cldeWcsjMap = new HashMap<String, String>();

        Map<String, String> gongyiyuan = new HashMap<String, String>();

        Map map = request.getParameterMap();
        Iterator iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = String.valueOf(iterator.next());
            //System.out.println("-------key:"+key);
            //System.out.println("-------value:"+request.getParameter(key));
            if (key.endsWith("_renwuyaoqiu___textbox")) {// 任务要求
                //System.out.println("-------key:" + key);
                String oidValue = key.substring(key.lastIndexOf("$"), key.length());
                oidValue = oidValue.substring(4, oidValue.indexOf("_renwuyaoqiu___textbox"));
                if(!oidList.contains(oidValue)){
                	  oidList.add(oidValue);
                }

                //System.out.println("-------oidValue:" + oidValue);
                String value = request.getParameter(key);
                //System.out.println("-------任务要求:" + value);
                rwyqMap.put(oidValue, value);
            } else if (key.endsWith("_jihuawanchengshijian___textbox")) {// 计划完成时间
                //System.out.println("-------key:" + key);
                String oidValue = key.substring(key.lastIndexOf("$"), key.length());
                oidValue = oidValue.substring(4, oidValue.indexOf("_jihuawanchengshijian"));
                if (oidValue.contains("_col_")) {
                    oidValue = oidValue.substring(oidValue.indexOf("_col_")+5, oidValue.length());
                }
                //System.out.println("-------oidValue:" + oidValue);
                String value = request.getParameter(key);
                //System.out.println("-------计划完成时间:" + value);
                if (value != null && !"".equals(value)) {
                    value = value.replaceAll("/", "-")+" "+"00:00:00";
                }

                wcsjMap.put(oidValue, value);
            } else if(key.endsWith("_cldePlanTime___textbox")){ //材料定额计划完成时间
                String oidValue = key.substring(key.lastIndexOf("$"), key.length());
                oidValue = oidValue.substring(4, oidValue.indexOf("_cldePlanTime"));
                if (oidValue.contains("_col_")) {
                    oidValue = oidValue.substring(oidValue.indexOf("_col_")+5, oidValue.length());
                }
                String value = request.getParameter(key);
                if (value != null && !"".equals(value)) {
                    value = value.replaceAll("/", "-")+" "+"00:00:00";
                }
                cldeWcsjMap.put(oidValue, value);

            } else if (key.indexOf("sign_person_value")!=-1) {
            	String value = request.getParameter(key);
            	if (value != null && !"".equals(value)) {
                    String oidValue = key.substring(0,key.indexOf("_"));
                    gongyiyuan.put(oidValue, value);
            	}
            }
        }

        System.out.println("-------cldeWcsjMap:" + cldeWcsjMap);

        FeedbackMessage message = new FeedbackMessage();
        StringBuffer msg = new StringBuffer();
        FormProcessingStatus formProcessingStatus = FormProcessingStatus.SUCCESS;
        Transaction tx = new Transaction();
        if(gongyiyuan.size()>0&&oidList.size()==gongyiyuan.size()){//如果是工艺组长进行分工

            try {
                tx.start();
                if (oidList != null) {
                	IBAUtility ibaUtility = null;
                    for (String oid : oidList) {
                        WTPart part = (WTPart) WCUtil.getPersistable(oid);
                        //WTContainer container = part.getContainer();
                        //System.out.println("-------part:" + part);
                        //initiateWfProcess(Constants.WF_GONGYIRENWUFENGONG, data1, container, part);


                        String renwuyaoqiu = rwyqMap.get(oid);
                        String wanchengshijian = wcsjMap.get(oid);
                        String cldePlanTime = cldeWcsjMap.get(oid);

                        if (renwuyaoqiu==null) {
                            renwuyaoqiu = "";
                        }
                        if (wanchengshijian==null) {
                            wanchengshijian = "";
                        }
                        if (cldePlanTime==null) {
                            cldePlanTime = "";
                        }

                        System.out.println("-------renwuyaoqiu:" + renwuyaoqiu);
                        System.out.println("-------wanchengshijian:" + wanchengshijian);
                        System.out.println("-------cldePlanTime:" + cldePlanTime);

                        //更新part属性"主制车间"、"辅制车间"的值

                        //新建工艺任务对象
                        ProcessTask processTask = ProcessTask.newProcessTask();
                        processTask.setName(part.getName());
                        processTask.setNumber(part.getNumber());
                        processTask.setVersion(part.getIterationDisplayIdentifier().toString());

                        processTask.setRenwuyaoqiu(renwuyaoqiu);
                        processTask.setEndDate(Timestamp.valueOf(wanchengshijian));
                        processTask.setTaskState(ProcessConstants.TASK_STATE_JINGXINZHONG);
                        processTask.setTaskType(taskType);

                        processTask.setContainer(part.getContainer());

                        Folder folder = getFolder("/Default", part.getContainer());
                        FolderHelper.assignLocation((FolderEntry) processTask, folder);

                        processTask = (ProcessTask)PersistenceHelper.manager.save(processTask);

                        ibaUtility = new IBAUtility(processTask);
                        ibaUtility.setIBAValue("cldePlanTime", cldePlanTime);
                        processTask = (ProcessTask) ibaUtility.updateAttributeContainer(processTask);
                        ibaUtility.updateIBAHolder(processTask);

                        //创建工艺任务与零部件的关联
                        createProcessTaskLink(processTask, part);

                        //创建工艺任务活动条目,针对每一个工艺员创建一个活动条目
                        String gongyiyuans = gongyiyuan.get(oid);
                        ReferenceFactory rf = new ReferenceFactory();
                        if (gongyiyuans != null && !"".equals(gongyiyuans)) {
                            String[] hqPerson = gongyiyuans.split(";");
                            for (String sp : hqPerson) {
                                if (!"".equals(sp.trim())) {
                                    WTUser wtUser = (WTUser) rf.getReference(sp).getObject();
                                    ProcessTaskItem processTaskItem = ProcessTaskItem.newProcessTaskItem();
                                    processTaskItem.setProcessTaskId(PersistenceHelper.getObjectIdentifier(processTask).getId());
                                    processTaskItem.setPartId(PersistenceHelper.getObjectIdentifier(part).getId());
                                    processTaskItem.setOwner(wtUser.getName());
                                    processTaskItem.setName(part.getName());
                                    processTaskItem.setNumber(part.getNumber());
                                    processTaskItem.setVersion(part.getIterationDisplayIdentifier().toString());
                                   // processTaskItem.setZhurengongyishi(currentUser.getName());
                                    processTaskItem.setChejian(SignatureService.getCheJianOrXiangMuNumByUser(part.getContainer()));
                                    processTaskItem.setExecutorRole(ProcessConstants.ROLE_GONGYIYUAN);
                                   // processTaskItem.setIszhuzhi(true);
                                    processTaskItem.setRenwuyaoqiu(renwuyaoqiu);
                                    processTaskItem.setTaskType(taskType);
                                    processTaskItem.setTaskItemState(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN);
                                    processTaskItem.setEndDate(Timestamp.valueOf(wanchengshijian));
                                    if (taskType.equals(ProcessConstants.TASK_TYPE_LINSHIGONGYI)) {
                                        processTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_LINSHIGONGYIRENWU);
                                    } else if (taskType.equals(ProcessConstants.TASK_TYPE_GONGYIGENGGAI)) {
                                        processTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_GONGYIGENGGAIRENWU);
                                    }

                                    processTaskItem.setContainer(part.getContainer());
                                    FolderHelper.assignLocation((FolderEntry) processTaskItem, folder);

                                    PersistenceHelper.manager.save(processTaskItem);

                                    ProcessTaskItem s = (ProcessTaskItem) PersistenceHelper.manager.save(processTaskItem);
                                    ibaUtility = new IBAUtility(s);
                                    ibaUtility.setIBAValue("GONGYIZUZHANG", currentUser.getName());
                                    ibaUtility.setIBAValue("cldePlanTime", cldePlanTime);
                                    s = (ProcessTaskItem) ibaUtility.updateAttributeContainer(s);
                                    IBAUtility.updateIBAHolder(s);
                                    PersistenceHelper.manager.refresh(s);

                                }
                            }


                        }

                    }
                }
                msg.append(ProcessConstants.JSP_MSG_SUCCESS);
                tx.commit();
                tx = null;
            } catch (Exception e) {
                formProcessingStatus = FormProcessingStatus.FAILURE;
                msg.append(ProcessConstants.JSP_MSG_FAILD);
                e.printStackTrace();
            } finally {
                if (tx != null) {
                    tx.rollback();
                }
                form.setStatus(formProcessingStatus);
                message.addMessage(msg.toString());
                form.addFeedbackMessage(message);
                form.setNextAction(FormResultAction.NONE);
                form.setNextAction(FormResultAction.REFRESH_OPENER);
            }

        }else{
        	 formProcessingStatus = FormProcessingStatus.FAILURE;
             msg.append(ProcessConstants.JSP_MSG_FAILD);
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
