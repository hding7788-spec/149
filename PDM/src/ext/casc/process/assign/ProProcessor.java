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

import com.glaway.mpm.util.IBAHelper;
import wt.fc.PersistenceHelper;
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

public class ProProcessor extends DefaultObjectFormProcessor {

    @Override
    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> arg1) throws WTException {
        FormResult form = new FormResult();
        // System.out.println("---------commandBean:"+commandBean.getMap());
        HttpServletRequest request = commandBean.getRequest();
        HttpSession session = commandBean.getRequest().getSession();
        // System.out.println("---------request:"+request.getParameterMap());
        List<String> oidList = new ArrayList<String>();
        System.out.println("---------oidList:" + oidList);

        session.removeAttribute("deleteObject");
        session.removeAttribute("addObject");
        session.removeAttribute("rootParts");

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
        //用于存储各零件的任务主制车间
        Map<String, String> zzcjMap = new HashMap<String, String>();
        //用于存储各零件的任务辅制车间
        Map<String, String> fzcjMap = new HashMap<String, String>();

      //用于存储各零件的任务依据
        Map<String, String> renwuyijuMap = new HashMap<String, String>();

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
            }else if(key.endsWith("_cldePlanTime___textbox")){ //材料定额计划完成时间
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

            } else if (key.endsWith("_zhuzhichejian___combobox")) {// 主制车间
                //System.out.println("-------key:" + key);
                String oidValue = key.substring(key.lastIndexOf("$"), key.length());
                oidValue = oidValue.substring(4, oidValue.indexOf("_zhuzhichejian___combobox"));
                //System.out.println("-------oidValue:" + oidValue);
                String value = request.getParameter(key);
                //System.out.println("-------主制车间:" + value);
                zzcjMap.put(oidValue, value);
            } else if (key.indexOf("_fuzhichejian")!=-1) {// 辅制车间
            	String[] values = request.getParameterValues(key);
                //System.out.println("-------key:" + key);
                String oidValue = key.substring(0,key.lastIndexOf("_"));
                String value = "";
                for (String v : values) {
                	value = value+v+"&";
                }
               if(!"".equals(value)){
            	   value = value.substring(0, value.length()-1);
               }
                fzcjMap.put(oidValue, value);

            }else if (key.endsWith("_renwuyiju___combobox")) {// 主制车间
                //System.out.println("-------key:" + key);
                String oidValue = key.substring(key.lastIndexOf("$"), key.length());
                oidValue = oidValue.substring(4, oidValue.indexOf("_renwuyiju___combobox"));
                //System.out.println("-------oidValue:" + oidValue);
                String value = request.getParameter(key);
                //System.out.println("-------主制车间:" + value);
                renwuyijuMap.put(oidValue, value);
            }

        }

        System.out.println("-------rwyqMap:" + rwyqMap);
        System.out.println("-------wcsjMap:" + wcsjMap);
        System.out.println("-------cldeWcsjMap:" + cldeWcsjMap);
        System.out.println("-------zzcjMap:" + zzcjMap);
        System.out.println("-------fzcjMap:" + fzcjMap);

        Map<String, List<WTUser>> gongYiZuZhangmap = ProcessUtil.getGroupAndUsersInOrgContainer();
        WTContainer container = commandBean.getContainer();
        List<WTUser> xmbUsers = ProcessUtil.getRoleUsersByWTContainer("XIANGMUBUXINGHAOZHUGUANG", container);
        gongYiZuZhangmap.put(Constants.ROLE_XIANGMUBUXINGHAOZHUGUAN, xmbUsers);
        System.out.println("-------gongYiZuZhangmap:" + gongYiZuZhangmap);

        HashMap<String, WTPart> data1 = new HashMap<String, WTPart>();
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
                    //WTContainer container = part.getContainer();
                    //System.out.println("-------part:" + part);
                    //initiateWfProcess(Constants.WF_GONGYIRENWUFENGONG, data1, container, part);

                    String zhuzhichejian = zzcjMap.get(oid);
                    String fuzhichejian = fzcjMap.get(oid);
                    String renwuyaoqiu = rwyqMap.get(oid);
                    String wanchengshijian = wcsjMap.get(oid);
                    String cldePlanTime = cldeWcsjMap.get(oid);
                    String renwuyiju = renwuyijuMap.get(oid);
                    if (zhuzhichejian==null) {
                        zhuzhichejian = "";
                    }
                    if (fuzhichejian==null) {
                        fuzhichejian = "";
                    }
                    if (renwuyaoqiu==null) {
                        renwuyaoqiu = "";
                    }
                    if (wanchengshijian==null) {
                        wanchengshijian = "";
                    }
                    if (cldePlanTime==null) {
                        cldePlanTime = "";
                    }
                    System.out.println("-------zhuzhichejian:" + zhuzhichejian);
                    System.out.println("-------fuzhichejian:" + fuzhichejian);
                    System.out.println("-------renwuyaoqiu:" + renwuyaoqiu);
                    System.out.println("-------wanchengshijian:" + wanchengshijian);
                    System.out.println("-------cldePlanTime:" + cldePlanTime);

                    //更新part属性"主制车间"、"辅制车间"的值
                    ibaUtility = new IBAUtility(part);
                    ibaUtility.setIBAValue("ZZCJ", zhuzhichejian);
                    ibaUtility.setIBAValue("FZCJ", fuzhichejian.replaceAll("&", "-"));
                    part = (WTPart)ibaUtility.updateAttributeContainer(part);
                    ibaUtility.updateIBAHolder(part);
                    part = (WTPart)PersistenceHelper.manager.refresh(part);

                    //新建工艺任务对象
                    ProcessTask processTask = ProcessTask.newProcessTask();
                    processTask.setName(part.getName());
                    processTask.setNumber(part.getNumber());
                    processTask.setVersion(part.getIterationDisplayIdentifier().toString());
                    processTask.setZhuzhichejian(zhuzhichejian);
                    processTask.setFuzhichejian(fuzhichejian);
                    processTask.setRenwuyaoqiu(renwuyaoqiu);
                    processTask.setEndDate(Timestamp.valueOf(wanchengshijian));

                    processTask.setTaskState(ProcessConstants.TASK_STATE_JINGXINZHONG);
                    processTask.setTaskType(taskType);
                    processTask.setRenwuyiju(renwuyiju);
                    processTask.setContainer(part.getContainer());

                    Folder folder = getFolder("/Default", part.getContainer());
                    FolderHelper.assignLocation((FolderEntry) processTask, folder);

                    processTask = (ProcessTask)PersistenceHelper.manager.save(processTask);

                    ibaUtility = new IBAUtility(processTask);
                    ibaUtility.setIBAValue("cldePlanTime", cldePlanTime);
                    processTask = (ProcessTask) ibaUtility.updateAttributeContainer(processTask);
                    ibaUtility.updateIBAHolder(processTask);

                  //如果是工艺设计任务，则记录顶层part的oid
                    if(ProcessConstants.TASK_TYPE_GONGYISHEJI.equals(taskType)||ProcessConstants.TASK_TYPE_BAOBIAOLEI.equals(taskType)) {
                    	String topOid = (String)session.getAttribute("topOid");
                    	System.out.println("------------topOid-----"+topOid);
                    	ibaUtility = new IBAUtility(processTask);
                    	ibaUtility.setIBAValue("TOPOID", topOid);
                    	processTask = (ProcessTask) ibaUtility.updateAttributeContainer(processTask);
        				ibaUtility.updateIBAHolder(processTask);
                    }

                    //创建工艺任务与零部件的关联
                    createProcessTaskLink(processTask, part);

                    //创建工艺任务活动条目,针对每一个车间工艺组长创建一个活动条目
                    List<WTUser> gongYiZuZhangList = gongYiZuZhangmap.get(zhuzhichejian);
                    for (WTUser wtUser : gongYiZuZhangList) {
                        ProcessTaskItem processTaskItem = ProcessTaskItem.newProcessTaskItem();
                        processTaskItem.setProcessTaskId(PersistenceHelper.getObjectIdentifier(processTask).getId());
                        processTaskItem.setPartId(PersistenceHelper.getObjectIdentifier(part).getId());
                        processTaskItem.setOwner(wtUser.getName());
                        processTaskItem.setName(part.getName());
                        processTaskItem.setNumber(part.getNumber());
                        processTaskItem.setVersion(part.getIterationDisplayIdentifier().toString());
                        processTaskItem.setZhurengongyishi(currentUser.getName());
                        processTaskItem.setChejian(zhuzhichejian);
                        processTaskItem.setExecutorRole(ProcessConstants.ROLE_GONGYIZUZHANG);
                        processTaskItem.setIszhuzhi(true);
                        processTaskItem.setRenwuyaoqiu(renwuyaoqiu);
                        processTaskItem.setRenwuyiju(renwuyiju);
                        processTaskItem.setTaskType(taskType);
                        processTaskItem.setTaskItemState(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN);
                        processTaskItem.setEndDate(Timestamp.valueOf(wanchengshijian));

                        if (taskType.equals(ProcessConstants.TASK_TYPE_LINSHIGONGYI)) {
                            processTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_LINSHIGONGYIRENWUZHIPAI);
                        } else if (taskType.equals(ProcessConstants.TASK_TYPE_GONGYISHEJI)) {
                            processTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_ZHIPAIGONGYIYUAN);
                        } else if (taskType.equals(ProcessConstants.TASK_TYPE_GONGYIGENGGAI)) {
                            processTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_GONGYIGENGGAIRENWUZHIPAI);
                        }else if (taskType.equals(ProcessConstants.TASK_TYPE_BAOBIAOLEI)) {
                        	processTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_BAOBIAOLEIRENWUZHIPAI);
						}

                        processTaskItem.setContainer(part.getContainer());
                        FolderHelper.assignLocation((FolderEntry) processTaskItem, folder);

                        PersistenceHelper.manager.save(processTaskItem);

                        //设置材料定额计划完成时间 start
                        ibaUtility = new IBAUtility(processTaskItem);
                        ibaUtility.setIBAValue("cldePlanTime", cldePlanTime);
                        processTaskItem = (ProcessTaskItem) ibaUtility.updateAttributeContainer(processTaskItem);
                        ibaUtility.updateIBAHolder(processTaskItem);
                        //设置材料定额计划完成时间 end
                    }
                    if (fuzhichejian.contains("&")) {
                        String[] chenjian = fuzhichejian.split("&");
                        for (String str : chenjian) {
                            List<WTUser> fzZuZhang = gongYiZuZhangmap.get(str);
                            for (WTUser wtUser : fzZuZhang) {
                                ProcessTaskItem processTaskItem = ProcessTaskItem.newProcessTaskItem();
                                processTaskItem.setProcessTaskId(PersistenceHelper.getObjectIdentifier(processTask).getId());
                                processTaskItem.setPartId(PersistenceHelper.getObjectIdentifier(part).getId());
                                processTaskItem.setOwner(wtUser.getName());
                                processTaskItem.setName(part.getName());
                                processTaskItem.setNumber(part.getNumber());
                                processTaskItem.setVersion(part.getIterationDisplayIdentifier().toString());
                                processTaskItem.setZhurengongyishi(currentUser.getName());
                                processTaskItem.setChejian(str);
                                processTaskItem.setExecutorRole(ProcessConstants.ROLE_GONGYIZUZHANG);
                                processTaskItem.setIszhuzhi(false);
                                processTaskItem.setRenwuyaoqiu(renwuyaoqiu);
                                processTaskItem.setTaskType(taskType);
                                processTaskItem.setTaskItemState(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN);
                                processTaskItem.setEndDate(Timestamp.valueOf(wanchengshijian));

                                if (taskType.equals(ProcessConstants.TASK_TYPE_LINSHIGONGYI)) {
                                    processTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_LINSHIGONGYIRENWUZHIPAI);
                                } else if (taskType.equals(ProcessConstants.TASK_TYPE_GONGYISHEJI)) {
                                    processTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_ZHIPAIGONGYIYUAN);
                                } else if (taskType.equals(ProcessConstants.TASK_TYPE_GONGYIGENGGAI)) {
                                    processTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_GONGYIGENGGAIRENWUZHIPAI);
                                }else if (taskType.equals(ProcessConstants.TASK_TYPE_BAOBIAOLEI)) {
                                	processTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_BAOBIAOLEIRENWUZHIPAI);
        						}

                                processTaskItem.setContainer(part.getContainer());
                                FolderHelper.assignLocation((FolderEntry) processTaskItem, folder);

                                PersistenceHelper.manager.save(processTaskItem);
                                //设置材料定额计划完成时间 start
                                ibaUtility = new IBAUtility(processTaskItem);
                                ibaUtility.setIBAValue("cldePlanTime", cldePlanTime);
                                processTaskItem = (ProcessTaskItem) ibaUtility.updateAttributeContainer(processTaskItem);
                                ibaUtility.updateIBAHolder(processTaskItem);
                                //设置材料定额计划完成时间 end
                            }
                        }
                    }else {
                        if (!"".equals(fuzhichejian)) {
                            List<WTUser> fzZuZhang = gongYiZuZhangmap.get(fuzhichejian);
                            if (fzZuZhang != null) {
                                for (WTUser wtUser : fzZuZhang) {
                                    ProcessTaskItem processTaskItem = ProcessTaskItem.newProcessTaskItem();
                                    processTaskItem.setProcessTaskId(PersistenceHelper.getObjectIdentifier(processTask).getId());
                                    processTaskItem.setPartId(PersistenceHelper.getObjectIdentifier(part).getId());
                                    processTaskItem.setOwner(wtUser.getName());
                                    processTaskItem.setName(part.getName());
                                    processTaskItem.setNumber(part.getNumber());
                                    processTaskItem.setVersion(part.getIterationDisplayIdentifier().toString());
                                    processTaskItem.setZhurengongyishi(currentUser.getName());
                                    processTaskItem.setChejian(fuzhichejian);
                                    processTaskItem.setExecutorRole(ProcessConstants.ROLE_GONGYIZUZHANG);
                                    processTaskItem.setIszhuzhi(false);
                                    processTaskItem.setRenwuyaoqiu(renwuyaoqiu);
                                    processTaskItem.setTaskType(taskType);
                                    processTaskItem.setTaskItemState(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN);
                                    processTaskItem.setEndDate(Timestamp.valueOf(wanchengshijian));

                                    //设置材料定额计划完成时间 end
                                    if (taskType.equals(ProcessConstants.TASK_TYPE_LINSHIGONGYI)) {
                                        processTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_LINSHIGONGYIRENWUZHIPAI);
                                    } else if (taskType.equals(ProcessConstants.TASK_TYPE_GONGYISHEJI)) {
                                        processTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_ZHIPAIGONGYIYUAN);
                                    } else if (taskType.equals(ProcessConstants.TASK_TYPE_GONGYIGENGGAI)) {
                                        processTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_GONGYIGENGGAIRENWUZHIPAI);
                                    }else if (taskType.equals(ProcessConstants.TASK_TYPE_BAOBIAOLEI)) {
                                    	processTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_BAOBIAOLEIRENWUZHIPAI);
            						}
                                    processTaskItem.setContainer(part.getContainer());
                                    FolderHelper.assignLocation((FolderEntry) processTaskItem, folder);

                                    PersistenceHelper.manager.save(processTaskItem);
                                    //设置材料定额计划完成时间 start
                                    ibaUtility = new IBAUtility(processTaskItem);
                                    ibaUtility.setIBAValue("cldePlanTime", cldePlanTime);
                                    processTaskItem = (ProcessTaskItem) ibaUtility.updateAttributeContainer(processTaskItem);
                                    ibaUtility.updateIBAHolder(processTaskItem);
                                }
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
