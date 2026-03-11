package ext.casc.sop.task;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;
import ext.casc.constants.Constants;
import ext.casc.process.ProcessConstants;
import ext.casc.process.ProcessTask;
import ext.casc.process.ProcessTaskItem;
import ext.casc.process.ProcessTaskLink;
import ext.casc.process.util.ProcessUtil;
import ext.casc.sop.constants.SopConstants;
import ext.casc.util.IBAUtility;
import ext.casc.util.WCUtil;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
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
import wt.util.WTPropertyVetoException;
import wt.workflow.engine.WfEngineServerHelper;
import wt.workflow.engine.WfProcess;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.*;

public class SopTaskProcessor{


    /**
     * sop任务分工任务
     * @param commandBean
     * @return
     * @throws WTException
     */
    public static FormResult createSopTask(NmCommandBean commandBean) throws WTException {
        FormResult form = new FormResult();
        HttpServletRequest request = commandBean.getRequest();
        HttpSession session = commandBean.getRequest().getSession();
        List<String> oidList = new ArrayList<String>();
        String taskType = (String)session.getAttribute("TaskType");
        WTUser currentUser = (WTUser)SessionHelper.getPrincipal();

        //工艺员
        Map<String, String> gyyMap = new HashMap<String, String>();
        //用于存储各零件的任务计划完成时间
        Map<String, String> jhwcsjMap = new HashMap<String, String>();
        //用于存储各零件的任务要求
        Map<String, String> rwyqMap = new HashMap<String, String>();


        Map map = request.getParameterMap();
        Iterator iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = String.valueOf(iterator.next());
            if (key.endsWith("_renwuyaoqiu___textbox")) {// 任务要求
                String oidValue = key.substring(key.lastIndexOf("$"), key.length());
                oidValue = oidValue.substring(4, oidValue.indexOf("_renwuyaoqiu___textbox"));
                if(!oidList.contains(oidValue)){
                    oidList.add(oidValue);
                }

                String value = request.getParameter(key);
                rwyqMap.put(oidValue, value);
            } else if (key.endsWith("_jihuawanchengshijian___textbox")) {// 计划完成时间
                String oidValue = key.substring(key.lastIndexOf("$"), key.length());
                oidValue = oidValue.substring(4, oidValue.indexOf("_jihuawanchengshijian"));
                if (oidValue.contains("_col_")) {
                    oidValue = oidValue.substring(oidValue.indexOf("_col_")+5, oidValue.length());
                }
                String value = request.getParameter(key);
                if (value != null && !"".equals(value)) {
                    value = value.replaceAll("/", "-")+" "+"00:00:00";
                }

                jhwcsjMap.put(oidValue, value);
            } else if (key.endsWith("_gongyiyuan___combobox")) {// 工艺员
                String oidValue = key.substring(key.lastIndexOf("$"), key.length());
                oidValue = oidValue.substring(4, oidValue.indexOf("_gongyiyuan___combobox"));
                String value = request.getParameter(key);
                gyyMap.put(oidValue, value);
            }

        }

        FeedbackMessage message = new FeedbackMessage();
        StringBuffer msg = new StringBuffer();
        FormProcessingStatus formProcessingStatus = FormProcessingStatus.SUCCESS;
        Transaction tx = new Transaction();
        try {
            tx.start();
            if (oidList != null) {
                IBAUtility ibaUtility = null;
                for (String oid : oidList) {
                    WTPart part = (WTPart) WCUtil.getPersistable(oid);

                    String gyy = gyyMap.get(oid);
                    if (gyy==null) {
                        gyy = "";
                    }
                    String jhwcsj = jhwcsjMap.get(oid);
                    if (jhwcsj==null) {
                        jhwcsj = "";
                    }
                    String rwyq = rwyqMap.get(oid);
                    if (rwyq==null) {
                        rwyq = "";
                    }
                    ibaUtility = new IBAUtility(part);
                    String dept = ibaUtility.getIBAValue("Department");
                    //新建工艺任务对象
                    ProcessTask processTask = ProcessTask.newProcessTask();
                    processTask.setName(part.getName());
                    processTask.setNumber(part.getNumber());
                    processTask.setVersion(part.getIterationDisplayIdentifier().toString());
                    processTask.setZhuzhichejian(dept);
                    processTask.setFuzhichejian("");
                    processTask.setRenwuyaoqiu(rwyq);
                    processTask.setEndDate(Timestamp.valueOf(jhwcsj));

                    processTask.setTaskState(ProcessConstants.TASK_STATE_JINGXINZHONG);
                    processTask.setTaskType(taskType);
                    processTask.setContainer(part.getContainer());

                    Folder folder = getFolder("/Default", part.getContainer());
                    FolderHelper.assignLocation((FolderEntry) processTask, folder);

                    processTask = (ProcessTask)PersistenceHelper.manager.save(processTask);

                    //创建工艺任务与零部件的关联
                    createProcessTaskLink(processTask, part);

                    //创建工艺任务活动条目
                    ProcessTaskItem processTaskItem = ProcessTaskItem.newProcessTaskItem();
                    processTaskItem.setProcessTaskId(PersistenceHelper.getObjectIdentifier(processTask).getId());
                    processTaskItem.setPartId(PersistenceHelper.getObjectIdentifier(part).getId());
                    processTaskItem.setOwner(gyy);
                    processTaskItem.setName(part.getName());
                    processTaskItem.setNumber(part.getNumber());
                    processTaskItem.setVersion(part.getIterationDisplayIdentifier().toString());
                    processTaskItem.setZhurengongyishi(currentUser.getName());
                    processTaskItem.setChejian(dept);
                    processTaskItem.setExecutorRole(ProcessConstants.ROLE_GONGYIYUAN);
                    processTaskItem.setIszhuzhi(true);
                    processTaskItem.setRenwuyaoqiu(rwyq);
                    processTaskItem.setTaskType(taskType);
                    processTaskItem.setGongyiyuan(gyy);
                    processTaskItem.setTaskItemState(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN);
                    processTaskItem.setEndDate(Timestamp.valueOf(jhwcsj));
                    processTaskItem.setTaskItemName(SopConstants.SOP_TASK_TASKITEMNAME);

                    processTaskItem.setContainer(part.getContainer());
                    FolderHelper.assignLocation((FolderEntry) processTaskItem, folder);
                    PersistenceHelper.manager.save(processTaskItem);

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
        session.removeAttribute("oidList");
        return form;
    }
    /**
     * sop任务分工任务
     * @param commandBean
     * @return
     * @throws WTException
     */
    public static FormResult createSopChangeTask(NmCommandBean commandBean) throws WTException {
        FormResult form = new FormResult();
        HttpServletRequest request = commandBean.getRequest();
        HttpSession session = commandBean.getRequest().getSession();
        List<String> oidList = new ArrayList<String>();
        String taskType = (String)session.getAttribute("TaskType");
        WTUser currentUser = (WTUser)SessionHelper.getPrincipal();

        //工艺员
        Map<String, String> gyyMap = new HashMap<String, String>();
        //用于存储各零件的任务计划完成时间
        Map<String, String> jhwcsjMap = new HashMap<String, String>();
        //用于存储各零件的任务要求
        Map<String, String> rwyqMap = new HashMap<String, String>();


        Map map = request.getParameterMap();
        Iterator iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = String.valueOf(iterator.next());
            if (key.endsWith("_renwuyaoqiu___textbox")) {// 任务要求
                String oidValue = key.substring(key.lastIndexOf("$"), key.length());
                oidValue = oidValue.substring(4, oidValue.indexOf("_renwuyaoqiu___textbox"));
                if(!oidList.contains(oidValue)){
                    oidList.add(oidValue);
                }

                String value = request.getParameter(key);
                rwyqMap.put(oidValue, value);
            } else if (key.endsWith("_jihuawanchengshijian___textbox")) {// 计划完成时间
                String oidValue = key.substring(key.lastIndexOf("$"), key.length());
                oidValue = oidValue.substring(4, oidValue.indexOf("_jihuawanchengshijian"));
                if (oidValue.contains("_col_")) {
                    oidValue = oidValue.substring(oidValue.indexOf("_col_")+5, oidValue.length());
                }
                String value = request.getParameter(key);
                if (value != null && !"".equals(value)) {
                    value = value.replaceAll("/", "-")+" "+"00:00:00";
                }

                jhwcsjMap.put(oidValue, value);
            } else if (key.endsWith("_gongyiyuan___combobox")) {// 工艺员
                String oidValue = key.substring(key.lastIndexOf("$"), key.length());
                oidValue = oidValue.substring(4, oidValue.indexOf("_gongyiyuan___combobox"));
                String value = request.getParameter(key);
                gyyMap.put(oidValue, value);
            }

        }

        FeedbackMessage message = new FeedbackMessage();
        StringBuffer msg = new StringBuffer();
        FormProcessingStatus formProcessingStatus = FormProcessingStatus.SUCCESS;
        if(gyyMap.size() != jhwcsjMap.size()){
            formProcessingStatus = FormProcessingStatus.FAILURE;
            msg.append(ProcessConstants.JSP_MSG_FAILD);
            return form;
        }
        Transaction tx = new Transaction();
        try {
            tx.start();
            if (oidList != null) {
                IBAUtility ibaUtility = null;
                for (String oid : oidList) {
                    WTPart part = (WTPart) WCUtil.getPersistable(oid);

                    String gyy = gyyMap.get(oid);
                    if (gyy==null) {
                        gyy = "";
                    }
                    String jhwcsj = jhwcsjMap.get(oid);
                    if (jhwcsj==null) {
                        jhwcsj = "";
                    }
                    String rwyq = rwyqMap.get(oid);
                    if (rwyq==null) {
                        rwyq = "";
                    }
                    ibaUtility = new IBAUtility(part);
                    String dept = ibaUtility.getIBAValue("Department");
                    //新建工艺任务对象
                    ProcessTask processTask = ProcessTask.newProcessTask();
                    processTask.setName(part.getName());
                    processTask.setNumber(part.getNumber());
                    processTask.setVersion(part.getIterationDisplayIdentifier().toString());
                    processTask.setZhuzhichejian(dept);
                    processTask.setFuzhichejian("");
                    processTask.setRenwuyaoqiu(rwyq);
                    processTask.setEndDate(Timestamp.valueOf(jhwcsj));

                    processTask.setTaskState(ProcessConstants.TASK_STATE_JINGXINZHONG);
                    processTask.setTaskType(taskType);
                    processTask.setContainer(part.getContainer());

                    Folder folder = getFolder("/Default", part.getContainer());
                    FolderHelper.assignLocation((FolderEntry) processTask, folder);

                    processTask = (ProcessTask)PersistenceHelper.manager.save(processTask);

                    //创建工艺任务与零部件的关联
                    createProcessTaskLink(processTask, part);

                    //创建工艺任务活动条目
                    ProcessTaskItem processTaskItem = ProcessTaskItem.newProcessTaskItem();
                    processTaskItem.setProcessTaskId(PersistenceHelper.getObjectIdentifier(processTask).getId());
                    processTaskItem.setPartId(PersistenceHelper.getObjectIdentifier(part).getId());
                    processTaskItem.setOwner(gyy);
                    processTaskItem.setName(part.getName());
                    processTaskItem.setNumber(part.getNumber());
                    processTaskItem.setVersion(part.getIterationDisplayIdentifier().toString());
                    processTaskItem.setZhurengongyishi(currentUser.getName());
                    processTaskItem.setChejian(dept);
                    processTaskItem.setExecutorRole(ProcessConstants.ROLE_GONGYIYUAN);
                    processTaskItem.setIszhuzhi(true);
                    processTaskItem.setRenwuyaoqiu(rwyq);
                    processTaskItem.setTaskType(taskType);
                    processTaskItem.setGongyiyuan(gyy);
                    processTaskItem.setTaskItemState(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN);
                    processTaskItem.setEndDate(Timestamp.valueOf(jhwcsj));
                    processTaskItem.setTaskItemName(SopConstants.SOP_TASK_CHANGETASKITEMNAME);

                    processTaskItem.setContainer(part.getContainer());
                    FolderHelper.assignLocation((FolderEntry) processTaskItem, folder);
                    PersistenceHelper.manager.save(processTaskItem);

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
        session.removeAttribute("oidList");
        return form;
    }


    /**
     * 拒绝任务
     * @param commandBean
     * @return
     * @throws Exception
     */
    public static FormResult rejectTask(NmCommandBean commandBean) {
        FormResult form = new FormResult();
        FormProcessingStatus formProcessingStatus = FormProcessingStatus.SUCCESS;
        StringBuilder msg = new StringBuilder();
        FeedbackMessage message = new FeedbackMessage();
        Transaction tx = null;
        try {
            tx = new Transaction();
            tx.start();
            Object refObject = commandBean.getPageOid().getRefObject();
            WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
            HttpServletRequest request = commandBean.getRequest();
            String beizhu = commandBean.getTextParameter("beizhi");
            if(refObject instanceof ProcessTaskItem){
                ProcessTaskItem taskItem = (ProcessTaskItem) refObject;

                taskItem.setCompletedBy(currentUser.getFullName());
                taskItem.setRouteSelect(ProcessConstants.TASKITEM_ROUTESELECT_BOHUI);
                taskItem.setTaskItemState(ProcessConstants.TASKITEM_STATE_YIWANCHENG);
                taskItem.setDescription(beizhu);
                taskItem = (ProcessTaskItem) PersistenceHelper.manager.save(taskItem);

                ProcessTaskItem newTaskItem = new ProcessTaskItem();
                newTaskItem.setOwner(taskItem.getCreator().getName());

                newTaskItem.setProcessTaskId(taskItem.getProcessTaskId());
                newTaskItem.setPartId(taskItem.getPartId());
                newTaskItem.setName(taskItem.getName());
                newTaskItem.setNumber(taskItem.getNumber());
                newTaskItem.setVersion(taskItem.getVersion());
                newTaskItem.setZhurengongyishi(taskItem.getZhurengongyishi());
                newTaskItem.setRenwuyaoqiu(taskItem.getRenwuyaoqiu());
                newTaskItem.setRenwuyiju(taskItem.getRenwuyiju());
                newTaskItem.setTaskType(taskItem.getTaskType());
                newTaskItem.setTaskItemState(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN);
                newTaskItem.setEndDate(taskItem.getEndDate());
                newTaskItem.setChejian(taskItem.getChejian());
                newTaskItem.setIszhuzhi(taskItem.getIszhuzhi());
                newTaskItem.setExecutorRole(SopConstants.SOP_ROLE_BZHS);
                if(SopConstants.SOP_TASK_TASKTYPE_CHANGE.equals(taskItem.getTaskType())){
                    newTaskItem.setTaskItemName(SopConstants.SOP_TASK_CHANGETASKZP);
                }else if(SopConstants.SOP_TASK_TASKTYPE.equals(taskItem.getTaskType())){
                    newTaskItem.setTaskItemName(SopConstants.SOP_TASK_ZP);
                }

                newTaskItem.setContainer(taskItem.getContainer());
                Folder folder = getFolder("/Default", taskItem.getContainer());
                FolderHelper.assignLocation((FolderEntry) newTaskItem, folder);

                PersistenceHelper.manager.save(newTaskItem);
            }
            msg.append(SopConstants.SOP_MSG_REJECT_SUCCESS);
            tx.commit();
            tx = null;
        } catch (Exception e) {
            formProcessingStatus = FormProcessingStatus.FAILURE;
            msg.append(SopConstants.SOP_MSG_REJECT_FAIL);
            e.printStackTrace();
        } finally {
            if(tx != null){
                tx.rollback();
            }
            form.setStatus(formProcessingStatus);
            message.addMessage(msg.toString());
            form.addFeedbackMessage(message);
            form.setNextAction(FormResultAction.NONE);
        }
        return form;
    }

    /**
     * 作废SOP任务
     * @param commandBean
     * @return
     */
    public static FormResult deleteTask(NmCommandBean commandBean) {
        FormResult form = new FormResult();
        FormProcessingStatus formProcessingStatus = FormProcessingStatus.SUCCESS;
        StringBuffer msg = new StringBuffer();
        FeedbackMessage message = new FeedbackMessage();
        Transaction tx = null;
        try {
            tx = new Transaction();
            tx.start();
            Object refObject = commandBean.getPageOid().getRefObject();
            WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
            if(refObject instanceof ProcessTaskItem){
                ProcessTaskItem taskItem = (ProcessTaskItem) refObject;
                ProcessTask processTask = ProcessUtil.getProcessTask(taskItem.getProcessTaskId());
                if(processTask != null){
                    QueryResult queryResult = ProcessUtil.getAllProcessTaskItemByPTask(taskItem.getProcessTaskId());
                    while(queryResult.hasMoreElements()){
                        ProcessTaskItem processTaskItem = (ProcessTaskItem) queryResult.nextElement();
                        processTaskItem.setTaskItemState(ProcessConstants.TASK_STATE_YIZUOFEI);
                        processTaskItem.setCompletedBy(currentUser.getFullName());
                        PersistenceHelper.manager.save(processTaskItem);
                    }
                    processTask.setTaskState(ProcessConstants.TASK_STATE_YIZUOFEI);
                    PersistenceHelper.manager.save(processTask);
                }
            }
            msg.append(SopConstants.SOP_MSG_DELETE_SUCCESS);
            tx.commit();
            tx = null;
        } catch (Exception e) {
            formProcessingStatus = FormProcessingStatus.FAILURE;
            msg.append(SopConstants.SOP_MSG_DELETE_FAIL);
            e.printStackTrace();
        } finally {
            if(tx != null){
                tx.rollback();
            }
            form.setNextAction(FormResultAction.NONE);
        }
        return form;
    }

    /**
     * 完成SOP任务
     * @param commandBean
     * @return
     */
    public static FormResult completeTask(NmCommandBean commandBean) {
        FormResult form = new FormResult();
        FormProcessingStatus formProcessingStatus = FormProcessingStatus.SUCCESS;
        StringBuffer msg = new StringBuffer();
        FeedbackMessage message = new FeedbackMessage();
        Transaction tx = null;
        try {
            tx = new Transaction();
            tx.start();
            String selectUser = commandBean.getTextParameter("selectSopUser");
            String userName = selectUser.substring(0,selectUser.indexOf("("));
            Object refObject = commandBean.getPageOid().getRefObject();
            WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
            String beizhu = commandBean.getTextParameter("beizhi");
            if(refObject instanceof ProcessTaskItem){
                ProcessTaskItem taskItem = (ProcessTaskItem) refObject;
                taskItem.setRouteSelect(ProcessConstants.TASKITEM_ROUTESELECT_WANCHENGRENWU);
                taskItem.setCompletedBy(currentUser.getFullName());
                taskItem.setTaskItemState(ProcessConstants.TASKITEM_STATE_YIWANCHENG);
                taskItem.setDescription(beizhu);
                taskItem = (ProcessTaskItem) PersistenceHelper.manager.save(taskItem);

                ProcessTaskItem newTaskItem = ProcessTaskItem.newProcessTaskItem();
                newTaskItem.setProcessTaskId(taskItem.getProcessTaskId());
                newTaskItem.setPartId(taskItem.getPartId());
                newTaskItem.setName(taskItem.getName());
                newTaskItem.setNumber(taskItem.getNumber());
                newTaskItem.setVersion(taskItem.getVersion());
                newTaskItem.setZhurengongyishi(taskItem.getZhurengongyishi());
                newTaskItem.setChejian(taskItem.getChejian());
                newTaskItem.setIszhuzhi(taskItem.getIszhuzhi());
                newTaskItem.setRenwuyaoqiu(taskItem.getRenwuyaoqiu());
                newTaskItem.setRenwuyiju(taskItem.getRenwuyiju());
                newTaskItem.setTaskType(taskItem.getTaskType());
                newTaskItem.setTaskItemState(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN);
                newTaskItem.setEndDate(taskItem.getEndDate());
                newTaskItem.setContainer(taskItem.getContainer());
                Folder folder = getFolder("/Default", taskItem.getContainer());
                FolderHelper.assignLocation((FolderEntry) newTaskItem, folder);
                if(SopConstants.SOP_TASK_TASKTYPE_CHANGE.equals(taskItem.getTaskType())){
                    newTaskItem.setTaskItemName(SopConstants.SOP_TASK_CHANGETASKITEMNAME);
                }else{
                    newTaskItem.setTaskItemName(SopConstants.SOP_TASK_TASKITEMNAME);
                }
                newTaskItem.setExecutorRole(SopConstants.SOP_ROLE_GYY);
                newTaskItem.setOwner(userName);
                PersistenceHelper.manager.save(newTaskItem);
            }
            msg.append(SopConstants.SOP_MSG_COMPLETE_SUCCESS);
            tx.commit();
            tx = null;
        } catch (Exception e) {
            formProcessingStatus = FormProcessingStatus.FAILURE;
            msg.append(SopConstants.SOP_MSG_COMPLETE_FAIL);
            e.printStackTrace();
        } finally {
            if(tx != null){
                tx.rollback();
            }
            form.setStatus(formProcessingStatus);
            message.addMessage(msg.toString());
            form.addFeedbackMessage(message);
            form.setNextAction(FormResultAction.NONE);
            form.setNextAction(FormResultAction.REFRESH_OPENER);
        }
        return form;
    }




    private static void createProcessTaskLink(ProcessTask processTask,WTPart part) throws WTException{
        ProcessTaskLink link = ProcessTaskLink.newProcessTaskLink(part, processTask);
        PersistenceHelper.manager.save(link);
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
