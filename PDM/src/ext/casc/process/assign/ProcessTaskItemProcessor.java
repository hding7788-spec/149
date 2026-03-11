package ext.casc.process.assign;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import com.glaway.mpm.constants.Constants;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.WTDocumentUtil;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;
import ext.casc.analysisActivity.helper.AnalysisConstant;
import ext.casc.constants.PDMConfig;
import ext.casc.process.ProcessConstants;
import ext.casc.process.ProcessTask;
import ext.casc.process.ProcessTaskItem;
import ext.casc.process.util.ProcessUtil;
import ext.casc.sop.constants.SopConstants;
import ext.casc.util.IBAUtility;
import ext.casc.util.WCUtil;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.json.JSONArray;
import org.json.JSONObject;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.folder.FolderNotFoundException;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.pom.Transaction;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import java.net.URL;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.*;

public class ProcessTaskItemProcessor {

    /**
     * 用于主任工艺师作废工艺任务，同时作废该工艺任务下的所有工艺任务活动条目
     *
     * @param commandBean
     * @return
     * @throws WTException
     */
    public static FormResult deleteProcessTask(NmCommandBean commandBean) throws WTException {
        FormResult formResult = new FormResult();
        FeedbackMessage message = new FeedbackMessage();
        StringBuffer msg = new StringBuffer();
        FormProcessingStatus formProcessingStatus = FormProcessingStatus.SUCCESS;
        Transaction tx = new Transaction();
        try {
            tx.start();
            List<NmOid> allTask = new ArrayList<NmOid>();

            NmOid nmOid = commandBean.getPageOid();
            if(nmOid != null) {
            	allTask.add(nmOid);
            } else {
            	List<NmContext> list = commandBean.getSelected();
            	for (NmContext nmContext : list) {
            		nmOid = nmContext.getTargetOid();
                	allTask.add(nmOid);
				}
            }

            processTask(allTask);

            msg.append(ProcessConstants.JSP_ACTIONS_ZUOFEI_SUCCESS);
            tx.commit();
            tx = null;
        } catch (WTPropertyVetoException e) {
            formProcessingStatus = FormProcessingStatus.FAILURE;
            msg.append(ProcessConstants.JSP_ACTIONS_ZUOFEI_FAILED);
            e.printStackTrace();
        } finally {
            if (tx != null) {
                tx.rollback();
            }
            formResult.setStatus(formProcessingStatus);
            message.addMessage(msg.toString());
            formResult.addFeedbackMessage(message);
            formResult.setNextAction(FormResultAction.NONE);
            formResult.setNextAction(FormResultAction.REFRESH_OPENER);
        }
        return formResult;
    }

    private static void processTask(List<NmOid> allTask) throws WTException, WTPropertyVetoException {
    	WTUser currentUser = (WTUser)SessionHelper.getPrincipal();
    	QueryResult qResult = null;
        ProcessTask processTask = null;
        String taskState = "";
    	for (NmOid nmOid : allTask) {
    		Object object = nmOid.getRefObject();
    		if (object instanceof ProcessTask) {
                processTask = (ProcessTask)object;
                taskState = processTask.getTaskState();
                if(!ProcessConstants.TASK_STATE_JINGXINZHONG.equals(taskState)) {
                	continue;
                }
                long longId = PersistenceHelper.getObjectIdentifier(processTask).getId();
                qResult = ProcessUtil.getAllProcessTaskItemByPTask(longId);
            }else if (object instanceof ProcessTaskItem) {
                ProcessTaskItem taskItem = (ProcessTaskItem)object;
                processTask = ProcessUtil.getProcessTask(taskItem.getProcessTaskId());
                taskState = processTask.getTaskState();
//                if(!ProcessConstants.TASK_STATE_JINGXINZHONG.equals(taskState)) {
//                	continue;
//                }
                long longId = PersistenceHelper.getObjectIdentifier(processTask).getId();
                qResult = ProcessUtil.getAllProcessTaskItemByPTask(longId);
            }

            if (qResult != null) {
                while(qResult.hasMoreElements()){//作废所有的工艺任务活动条目
                    ProcessTaskItem taskItem = (ProcessTaskItem)qResult.nextElement();
                    if (taskItem.getTaskItemState().equals(ProcessConstants.TASK_STATE_JINGXINZHONG)) {
                        taskItem.setTaskItemState(ProcessConstants.TASK_STATE_YIZUOFEI);
                        taskItem.setCompletedBy(currentUser.getFullName());
//                        taskItem.setEndDate(Timestamp.valueOf(null));
//                        taskItem.setTaskItemName(ProcessConstants.TASK_NAME_BAOBIAOLEIRENWUZUOFEI);
                        if ("报表类工艺任务".equals(taskItem.getTaskType())) {
                            taskItem.setTaskItemName(taskItem.getTaskType()+"_作废");
                        }
                        taskItem = (ProcessTaskItem) PersistenceHelper.manager.save(taskItem);
                    }
                }
            }

            if (processTask != null) {//作废工艺任务
                processTask.setTaskState(ProcessConstants.TASK_STATE_YIZUOFEI);
                processTask = (ProcessTask) PersistenceHelper.manager.save(processTask);
            }
		}
    }

    /**
     * 用于主任工艺师删除指定的工艺任务活动条目
     *
     * @param commandBean
     * @return
     * @throws WTException
     */
    public static FormResult deleteTask(NmCommandBean commandBean) throws WTException {
        FormResult formResult = new FormResult();
        FeedbackMessage message = new FeedbackMessage();
        StringBuffer msg = new StringBuffer();
        FormProcessingStatus formProcessingStatus = FormProcessingStatus.SUCCESS;
        WTUser currentUser = (WTUser)SessionHelper.getPrincipal();
        Transaction tx = new Transaction();
        try {
            tx.start();
            List list = commandBean.getSelected();
            //System.out.println("---------list:"+list);
            if (list == null || list.isEmpty()) {
                NmOid nmOid = commandBean.getActionOid();
                list.add(nmOid);
            }
            for (Object obj : list) {
                Object object = null;
                String nmoid = null;
                if (obj instanceof String) {
                    nmoid = String.valueOf(obj);
                    object = WCUtil.getPersistable(nmoid);
                }else if (obj instanceof NmOid) {
                    NmOid nmOid = (NmOid) obj;
                    object = nmOid.getRefObject();
                }else if (obj instanceof NmContext) {
                    NmContext context = (NmContext) obj;
                    object = context.getTargetOid().getRefObject();
                }

                if (object instanceof ProcessTaskItem) {
                    ProcessTaskItem taskItem = (ProcessTaskItem)object;
                    if (ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN.equals(taskItem.getTaskItemState())) {
                        taskItem.setTaskItemState(ProcessConstants.TASKITEM_STATE_CANCLE);
                        taskItem.setCompletedBy(currentUser.getFullName());
                        taskItem = (ProcessTaskItem) PersistenceHelper.manager.save(taskItem);
                        msg.append(ProcessConstants.JSP_ACTIONS_CANCLE_SUCCESS);
                    }
                }
            }

            tx.commit();
            tx = null;
        } catch (WTPropertyVetoException e) {
            formProcessingStatus = FormProcessingStatus.FAILURE;
            msg.append(ProcessConstants.JSP_ACTIONS_CANCLE_FAILED);
            e.printStackTrace();
        } finally {
            if (tx != null) {
                tx.rollback();
            }
            formResult.setStatus(formProcessingStatus);
            message.addMessage(msg.toString());
            formResult.addFeedbackMessage(message);
            formResult.setNextAction(FormResultAction.NONE);
            formResult.setNextAction(FormResultAction.REFRESH_OPENER);
        }
        return formResult;
    }

    /**
     * 重新选定的分配任务到指定的人
     *
     * @param commandBean
     * @return
     * @throws WTException
     */
    public static FormResult reassignTaskItem(NmCommandBean commandBean) throws WTException {
        FormResult formResult = new FormResult();
        FeedbackMessage message = new FeedbackMessage();
        StringBuffer msg = new StringBuffer();
        FormProcessingStatus formProcessingStatus = FormProcessingStatus.SUCCESS;
        Transaction tx = new Transaction();
        try {
            tx.start();
            List list = commandBean.getSelectedOidForPopup();
            NmOid nmOid = commandBean.getPrimaryOid();
            HttpServletRequest request = commandBean.getRequest();
            String userOid = request.getParameter("null___assignTo___combobox");
            WTUser user = (WTUser) ProcessUtil.getPersistable(userOid);
            String userName = user.getName();
            if("sysadmin".equals(userName) || "securityadmin".equals(userName)
                    || "auditadmin".equals(userName) || "Administrator".equals(userName)) {
                msg.append(ProcessConstants.JSP_ACTIONS_ADMIN_ERROR);
            }else {
                boolean flag = false;
                if (nmOid != null) {
                    ProcessTaskItem taskItem = (ProcessTaskItem) nmOid.getRefObject();
                    if (ProcessConstants.TASKITEM_STATE_YIWANCHENG.equals(taskItem.getTaskItemState())) {
                        flag = true;
                    }else {
                        taskItem.setOwner(user.getName());
                        PersistenceHelper.manager.save(taskItem);
                    }
                }
                if (list != null) {
                    for (Object object : list) {
                        if (object instanceof NmOid) {
                            NmOid oid = (NmOid) object;
                            ProcessTaskItem taskItem = (ProcessTaskItem) oid.getRefObject();
                            if (ProcessConstants.TASKITEM_STATE_YIWANCHENG.equals(taskItem.getTaskItemState())) {
                                flag = true;
                                break;
                            }else {
                                taskItem.setOwner(user.getName());
                                PersistenceHelper.manager.save(taskItem);
                            }
                        }
                    }
                }
                if (flag) {
                    msg.append(ProcessConstants.JSP_ACTIONS_REASSIGN_ERROR);
                }else {
                    msg.append(ProcessConstants.JSP_REASSIGN_MSG_SUCCESS);
                }
            }
            tx.commit();
            tx = null;
        } catch (WTPropertyVetoException e) {
            formProcessingStatus = FormProcessingStatus.FAILURE;
            msg.append(ProcessConstants.JSP_REASSIGN_MSG_FAILED);
            e.printStackTrace();
        } finally {
            if (tx != null) {
                tx.rollback();
            }
            formResult.setStatus(formProcessingStatus);
            message.addMessage(msg.toString());
            formResult.addFeedbackMessage(message);
            formResult.setNextAction(FormResultAction.NONE);
            formResult.setNextAction(FormResultAction.REFRESH_OPENER);
        }
        return formResult;
    }

    /**
     * 驳回任务
     *
     * @param commandBean
     * @return
     * @throws WTException
     */
    public static FormResult rejectTask(NmCommandBean commandBean) throws WTException {
        List<Object> list = commandBean.getSelected();
        if (list != null && !list.isEmpty()) {// 对选择的任务活动校验
            for (Object object : list) {
                if (object instanceof NmContext) {
                    NmContext nmContext = (NmContext) object;
                    Object tempObject = nmContext.getTargetOid().getRefObject();
                    if (tempObject instanceof ProcessTaskItem) {
                        ProcessTaskItem taskItem = (ProcessTaskItem) tempObject;
                        String taskName = taskItem.getTaskItemName();
                        if (ProcessConstants.TASKITEM_STATE_YIWANCHENG.equals(taskItem.getTaskItemState())) {// 只能对正在进行的工艺任务进行完成
                            FormResult formResult = new FormResult();
                            FeedbackMessage message = new FeedbackMessage();
                            formResult.setStatus(FormProcessingStatus.SUCCESS);
                            message.addMessage(ProcessConstants.JSP_ACTIONS_COMPLETED_ERROR);
                            formResult.addFeedbackMessage(message);
                            formResult.setNextAction(FormResultAction.NONE);
                            formResult.setNextAction(FormResultAction.REFRESH_OPENER);
                            return formResult;
                        }
                        if (taskName.equals(ProcessConstants.TASK_NAME_ZHIPAIGONGYIZUZHANG)
                                || taskName.equals(ProcessConstants.TASK_NAME_ZHIPAIGONGYIZUZHANG_JUJUE)) {//指派工艺组长任务活动不能被拒绝
                            FormResult formResult = new FormResult();
                            FeedbackMessage message = new FeedbackMessage();
                            formResult.setStatus(FormProcessingStatus.SUCCESS);
                            message.addMessage(ProcessConstants.JSP_ACTIONS_COMPLETED_CANNOTZHIPAI_ERROR);
                            formResult.addFeedbackMessage(message);
                            formResult.setNextAction(FormResultAction.NONE);
                            formResult.setNextAction(FormResultAction.REFRESH_OPENER);
                            return formResult;
                        }
                    }
                }
            }
        }


        if (list == null || list.isEmpty()) {
            NmOid nmOid = commandBean.getActionOid();
            list.add(nmOid);
        }
        for (Object obj : list) {
            Object object = null;
            if (obj instanceof String) {
              String  nmoid1 = String.valueOf(obj);
                object = WCUtil.getPersistable(nmoid1);
            } else if (obj instanceof NmOid) {
                NmOid nmOid = (NmOid) obj;
                object = nmOid.getRefObject();
            } else if (obj instanceof NmContext) {
                NmContext context = (NmContext) obj;
                object = context.getTargetOid().getRefObject();
            }
            if ((object != null) && (object instanceof ProcessTaskItem)) {
                ProcessTaskItem taskItem = (ProcessTaskItem) object;
//                if (taskItem.getTaskType().equals(ProcessConstants.TASK_TYPE_BAOBIAOLEI)) {
                if (taskItem.getTaskItemName().equals(ProcessConstants.TASK_NAME_BAOBIAOLEIRENWUZHIPAI)) {
                	try {
						taskItem.setTaskItemName(ProcessConstants.TASK_NAME_BAOBIAOLEIRENWUZUOFEI);
					} catch (WTPropertyVetoException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	return deleteProcessTask(commandBean);
                }
                 }
        }

        return execute(commandBean, list, "rejectTask");
    }

    /**
     * 完成任务
     *
     * @param commandBean
     * @return
     * @throws WTException
     */
    public static FormResult completeTask(NmCommandBean commandBean) throws WTException {
        List<Object> list = commandBean.getSelected();
        if (list != null && !list.isEmpty()) {// 对选择的任务活动校验
            for (Object object : list) {
                if (object instanceof NmContext) {
                    NmContext nmContext = (NmContext) object;
                    Object tempObject = nmContext.getTargetOid().getRefObject();
                    if (tempObject instanceof ProcessTaskItem) {
                        ProcessTaskItem taskItem = (ProcessTaskItem) tempObject;
                        String taskName = taskItem.getTaskItemName();
                        if (ProcessConstants.TASKITEM_STATE_YIWANCHENG.equals(taskItem.getTaskItemState())) {// 只能对正在进行的工艺任务进行完成
                            FormResult formResult = new FormResult();
                            FeedbackMessage message = new FeedbackMessage();
                            formResult.setStatus(FormProcessingStatus.FAILURE);
                            message.addMessage(ProcessConstants.JSP_ACTIONS_COMPLETED_ERROR);
                            formResult.addFeedbackMessage(message);
                            formResult.setNextAction(FormResultAction.NONE);
                            formResult.setNextAction(FormResultAction.REFRESH_OPENER);
                            return formResult;
                        }
                        if (!taskName.equals(ProcessConstants.TASK_NAME_BIANZHIGONGYI)
                                && !taskName.equals(ProcessConstants.TASK_NAME_GONGYIGENGGAIRENWU)
                                && !taskName.equals(ProcessConstants.TASK_NAME_LINSHIGONGYIRENWU)
                                && !taskName.equals(ProcessConstants.TASK_NAME_LINGBUJIANGONGYIRENWU)) {// 指派任务活动不能直接完成
                            FormResult formResult = new FormResult();
                            FeedbackMessage message = new FeedbackMessage();
                            formResult.setStatus(FormProcessingStatus.FAILURE);
                            message.addMessage(ProcessConstants.JSP_ACTIONS_COMPLETED_TYPE_ERROR);
                            formResult.addFeedbackMessage(message);
                            formResult.setNextAction(FormResultAction.NONE);
                            formResult.setNextAction(FormResultAction.REFRESH_OPENER);
                            return formResult;
                        }
                    }
                }
            }
        }
        if (list == null || list.isEmpty()) {
            NmOid nmOid = commandBean.getActionOid();
            list.add(nmOid);
        }

        for(Object object : list) {
            ProcessTaskItem taskItem = null;
            if (object instanceof NmContext) {
                NmContext nmContext = (NmContext) object;
                Object tempObject = nmContext.getTargetOid().getRefObject();
                if (tempObject instanceof ProcessTaskItem) {
                    taskItem = (ProcessTaskItem) tempObject;
                }
            } else if (object instanceof NmOid) {
                NmOid nmOid = (NmOid) object;
                if(nmOid.getRefObject() instanceof ProcessTaskItem) {
                    taskItem = (ProcessTaskItem) nmOid.getRefObject();
                }
            }
            if(taskItem != null) {
                //制品返修任务 校验是否关联了工艺文件
                if((AnalysisConstant.RENWUYIJV_YIZHIPIN.equals(taskItem.getRenwuyiju()) || AnalysisConstant.RENWUYIJV_ZAIZHIPIN.equals(taskItem.getRenwuyiju()))
                        && ProcessConstants.TASK_NAME_LINSHIGONGYIRENWU.equals(taskItem.getTaskItemName())) {
                    String processdocNum = IBAHelper.getIBAValue(taskItem, "PROCESSDOCNUM");
                    if(StrUtil.isEmpty(processdocNum)){
                        FormResult formResult = new FormResult();
                        FeedbackMessage message = new FeedbackMessage();
                        formResult.setStatus(FormProcessingStatus.FAILURE);
                        message.addMessage("操作失败，请关联返修工艺后再完成任务！");
                        formResult.addFeedbackMessage(message);
                        formResult.setNextAction(FormResultAction.NONE);
                        formResult.setNextAction(FormResultAction.REFRESH_OPENER);
                        return formResult;
                    }
                }
            }
        }

        boolean b = checkUser(commandBean, list, "completeTask");
        if(!b) {
        	FormResult formResult = new FormResult();
            FeedbackMessage message = new FeedbackMessage();
            formResult.setStatus(FormProcessingStatus.FAILURE);
            message.addMessage(ProcessConstants.JSP_ACTIONS_COMPLETED_NOSELECTUSER_ERROR);
            formResult.addFeedbackMessage(message);
            formResult.setNextAction(FormResultAction.NONE);
            formResult.setNextAction(FormResultAction.REFRESH_OPENER);
            return formResult;
        }


        /*String smessage =  checkReposrtProcessAllCompleted(commandBean, list);
        if(!"".equals(smessage)) {
        	FormResult formResult = new FormResult();
            FeedbackMessage message = new FeedbackMessage();
            formResult.setStatus(FormProcessingStatus.FAILURE);
            message.addMessage(smessage);
            formResult.addFeedbackMessage(message);
            formResult.setNextAction(FormResultAction.NONE);
            formResult.setNextAction(FormResultAction.REFRESH_OPENER);
            return formResult;
        }*/
        return execute(commandBean, list, "completeTask");
    }
    private static String checkReposrtProcessAllCompleted(NmCommandBean commandBean, List<Object> list) throws WTException{
    	String nmoid = "";
    	for (Object obj : list) {
            Object object = null;
            if (obj instanceof String) {
                nmoid = String.valueOf(obj);
                object = WCUtil.getPersistable(nmoid);
            } else if (obj instanceof NmOid) {
                NmOid nmOid = (NmOid) obj;
                object = nmOid.getRefObject();
            } else if (obj instanceof NmContext) {
                NmContext context = (NmContext) obj;
                object = context.getTargetOid().getRefObject();
            }
            if ((object != null) && (object instanceof ProcessTaskItem)) {
                ProcessTaskItem taskItem = (ProcessTaskItem) object;
                if(ProcessConstants.TASK_TYPE_BAOBIAOLEI.equals(taskItem.getTaskType())){
                	boolean hasReport = false;
                	WTPart part = ProcessUtil.getWtPart(taskItem.getPartId());
                    if (part != null) {
                    	WTPart lpart = (WTPart)VersionControlHelper.service.getLatestIteration(part, false);
                    	QueryResult qr2 = WTPartHelper.service.getDescribedByWTDocuments(lpart, true);
            			LatestConfigSpec lcs = new LatestConfigSpec();
            			qr2 = lcs.process(qr2);
            			while (qr2.hasMoreElements()) {
            				WTDocument document = (WTDocument) qr2.nextElement();
            				String typeName = TypedUtility.getTypeIdentifier(document).getTypename();
            				System.out.println("typeName="+typeName);

            				if (typeName.contains("reportTechnics")) {
            					hasReport = true;
            					document = (WTDocument)VersionControlHelper.service.getLatestIteration(document, true);
            					String state = document.getState().getState().getDisplay(Locale.CHINA);
            	                if (!Constants.STATE_YIPIZHUN.equals(state)) {
            	                	return "操作失败，该任务下的所有报表类工艺必须已批准才能完成该任务";
            	                }
            				}
            			}
            			//"操作失败，该任务下的所有报表类工艺必须已批准才能完成该任务！"
            			if(!hasReport){
            				return "操作失败，该任务下的还未创建报表类工艺，不能完成该任务！";
            			}

                    }

                }else{
                	return "";
                }
            }
    	}

    	return "";
	}

	/**
     * 完成报表类任务
     *
     * @param commandBean
     * @return
     * @throws WTException
     * @throws WTPropertyVetoException
     */
    public static FormResult completeReportTask(NmCommandBean commandBean) throws WTException, WTPropertyVetoException {
		NmOid nmOid = commandBean.getActionOid();
		Object object = nmOid.getRefObject();
		String mes ="";
		if (object instanceof ProcessTaskItem) {
			ProcessTaskItem taskItem = (ProcessTaskItem) object;
			mes = checkYiwancheng(taskItem);
			if ("".equals(mes)) {
				taskItem.setTaskItemState(ProcessConstants.TASKITEM_STATE_YIWANCHENG);
				taskItem = (ProcessTaskItem) PersistenceHelper.manager.save(taskItem);

				ProcessTask processTask = ProcessUtil.getProcessTask(taskItem.getProcessTaskId());
				if(processTask!=null){
					processTask.setTaskState(ProcessConstants.TASK_STATE_YIWANGONG);
					PersistenceHelper.manager.save(processTask);
				}
				FormResult formResult = new FormResult();
				FeedbackMessage message = new FeedbackMessage();
				formResult.setStatus(FormProcessingStatus.SUCCESS);
				message.addMessage(ProcessConstants.REPORT_SUCCESS_MES);
				formResult.addFeedbackMessage(message);
				formResult.setNextAction(FormResultAction.NONE);
				formResult.setNextAction(FormResultAction.REFRESH_OPENER);
				return formResult;
			}
		}
		FormResult formResult = new FormResult();
		FeedbackMessage message = new FeedbackMessage();
		formResult.setStatus(FormProcessingStatus.FAILURE);
		message.addMessage(mes);
		formResult.addFeedbackMessage(message);
		formResult.setNextAction(FormResultAction.NONE);
		formResult.setNextAction(FormResultAction.REFRESH_OPENER);
		return formResult;
    }


    private static boolean checkUser(NmCommandBean commandBean,List<Object> list,String routeSelect) throws WTException {
    	String nmoid = "";
    	String executorRole = "";
    	for (Object obj : list) {
            Object object = null;
            if (obj instanceof String) {
                nmoid = String.valueOf(obj);
                object = WCUtil.getPersistable(nmoid);
            } else if (obj instanceof NmOid) {
                NmOid nmOid = (NmOid) obj;
                object = nmOid.getRefObject();
            } else if (obj instanceof NmContext) {
                NmContext context = (NmContext) obj;
                object = context.getTargetOid().getRefObject();
            }
            if ((object != null) && (object instanceof ProcessTaskItem)) {
                ProcessTaskItem taskItem = (ProcessTaskItem) object;
                executorRole = taskItem.getExecutorRole();
            }
    	}

    	if (routeSelect.equals("completeTask")) {
            if (ProcessConstants.ROLE_GONGYIZUZHANG.equals(executorRole)) {
            	HttpServletRequest request = commandBean.getRequest();
                Map requestmap = request.getParameterMap();
                Iterator iterator = requestmap.keySet().iterator();
                String gongyiyuanValue = null;
                while (iterator.hasNext()) {
                    String key = String.valueOf(iterator.next());
                    if ("selectedUser".equals(key)||"selectedUser1".equals(key)) {
                        gongyiyuanValue = request.getParameter(key);
                        break;
                    }
                }
                if(gongyiyuanValue != null && !"".equals(gongyiyuanValue)) {
                	return true;
                } else {
                	return false;
                }
            }
    	}
    	return true;
    }

    private static FormResult execute(NmCommandBean commandBean, List<Object> list, String routeSelect)
            throws WTException {
    	String TechnicsReportStyle = null;
        WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
        Map<String, List<WTUser>> map = ProcessUtil.getGroupAndUsersInOrgContainer();
        FormResult formResult = new FormResult();
        FeedbackMessage message = new FeedbackMessage();
        StringBuffer msg = new StringBuffer();
        FormProcessingStatus formProcessingStatus = FormProcessingStatus.SUCCESS;
        Transaction transaction = new Transaction();
        try {
            transaction.start();
            String nmoid = null;
            for (Object obj : list) {
                Object object = null;
                if (obj instanceof String) {
                    nmoid = String.valueOf(obj);
                    object = WCUtil.getPersistable(nmoid);
                } else if (obj instanceof NmOid) {
                    NmOid nmOid = (NmOid) obj;
                    object = nmOid.getRefObject();
                } else if (obj instanceof NmContext) {
                    NmContext context = (NmContext) obj;
                    object = context.getTargetOid().getRefObject();
                }
                if ((object != null) && (object instanceof ProcessTaskItem)) {
                    ProcessTaskItem taskItem = (ProcessTaskItem) object;
                	String beizhu = commandBean.getTextParameter("beizhi");
                	if (SopConstants.SOP_TASK_TASKTYPE_CHANGE.equals(taskItem.getTaskType())
                			|| SopConstants.SOP_TASK_TASKTYPE.equals(taskItem.getTaskType())) {
						try {
							if (routeSelect.equals("rejectTask")) {
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
								if (SopConstants.SOP_TASK_TASKTYPE_CHANGE.equals(taskItem.getTaskType())) {
									newTaskItem.setTaskItemName(SopConstants.SOP_TASK_CHANGETASKZP);
								} else if (SopConstants.SOP_TASK_TASKTYPE.equals(taskItem.getTaskType())) {
									newTaskItem.setTaskItemName(SopConstants.SOP_TASK_ZP);
								}

								newTaskItem.setContainer(taskItem.getContainer());
								Folder folder = getFolder("/Default", taskItem.getContainer());
								FolderHelper.assignLocation((FolderEntry) newTaskItem, folder);

								PersistenceHelper.manager.save(newTaskItem);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						List<WTUser> xmbUsers = ProcessUtil.getRoleUsersByWTContainer("XIANGMUBUXINGHAOZHUGUANG", taskItem.getContainer());
						map.put(ext.casc.constants.Constants.ROLE_XIANGMUBUXINGHAOZHUGUAN, xmbUsers);
						// 获取jsp页面提交的信息
						String[] gongyiyuanValues = null;
						String gongyiyuanValue = null;
						String zhuzhichejianValue = "";
						String fuzhichejianValue = "";
						HttpServletRequest request = commandBean.getRequest();
						Map requestmap = request.getParameterMap();
						Iterator iterator = requestmap.keySet().iterator();
						while (iterator.hasNext()) {
							String key = String.valueOf(iterator.next());
							//System.out.println("---------key:" + key);
							if ("Print".equals(key)) {
								String flag = request.getParameter(key);
								TechnicsReportStyle=flag;

							}
							if ("beizhi".equals(key)) {
								String beizhiValue = request.getParameter(key);
								if (beizhiValue != null) {
									taskItem.setDescription(beizhiValue);
								} else {
									taskItem.setDescription("");
								}
							} else if ("gongyiyuan".equals(key)) {
								gongyiyuanValues = request.getParameterValues(key);
								//System.out.println("-------gongyiyuanValues:" + gongyiyuanValues);
								String gyyString = "";
								if (gongyiyuanValues != null) {
									for (String userName : gongyiyuanValues) {
										if ("".equals(gyyString)) {
											gyyString = userName;
										} else {
											gyyString = gyyString + ";" + userName;
										}

									}
									taskItem.setGongyiyuan(gyyString);
								}
							} else if ("selectedUser".equals(key)||"selectedUser1".equals(key)) {
								gongyiyuanValue = request.getParameter(key);
								System.out.println("-------gongyiyuanValue:" + gongyiyuanValue);
								taskItem.setGongyiyuan(gongyiyuanValue);
							} else if ("zhuzhichejian".equals(key)) {
								zhuzhichejianValue = request.getParameter(key);
								if (zhuzhichejianValue != null && !"kongzhi".equals(zhuzhichejianValue)) {
									taskItem.setZhuzhichejian(zhuzhichejianValue);
								} else {
									taskItem.setZhuzhichejian("");
								}
								//System.out.println("---------zhuzhichejianValue:" + zhuzhichejianValue);
							} else if ("fuzhichejian".equals(key)) {
								fuzhichejianValue = request.getParameter(key);
								if (fuzhichejianValue != null && !"kongzhi".equals(fuzhichejianValue)) {
									taskItem.setFuzhichejian(fuzhichejianValue);
								} else {
									taskItem.setFuzhichejian("");
								}
								//System.out.println("---------fuzhichejianValue:" + fuzhichejianValue);
							}else if("null___endDate_col_endDate___textbox".equals(key)){
								String	endDate = request.getParameter(key);
								if (endDate != null && !"".equals(endDate)) {
									endDate = endDate.replaceAll("/", "-")+" "+"00:00:00";
								}
								taskItem.setEndDate(Timestamp.valueOf(endDate));
							}
						}
						// 更改当前任务活动条目的状态
						taskItem.setCompletedBy(currentUser.getFullName());
						if (routeSelect.equals("rejectTask")) {// 驳回
							taskItem.setRouteSelect(ProcessConstants.TASKITEM_ROUTESELECT_BOHUI);
							taskItem.setTaskItemState(ProcessConstants.TASKITEM_STATE_YIWANCHENG);
						} else if (routeSelect.equals("completeTask")) {// 完成任务
							taskItem.setRouteSelect(ProcessConstants.TASKITEM_ROUTESELECT_WANCHENGRENWU);
							taskItem.setTaskItemState(ProcessConstants.TASKITEM_STATE_YIWANCHENG);
						}

						taskItem = (ProcessTaskItem) PersistenceHelper.manager.save(taskItem);

						String executorRole = taskItem.getExecutorRole();

						//如果是工艺组长驳回，则需要从相应的工艺任务的辅制车间里面删除该工艺组长所在的车间号
						if (ProcessConstants.ROLE_GONGYIZUZHANG.equals(executorRole)) {
							if (routeSelect.equals("rejectTask")) {// 驳回
								String chejian = taskItem.getChejian();
								ProcessTask processTask = ProcessUtil.getProcessTask(taskItem.getProcessTaskId());
								String fuzhichejian = processTask.getFuzhichejian();
								if (fuzhichejian!=null && !"".equals(fuzhichejian)) {
									if (fuzhichejian.contains("&")) {
										String[] strings = fuzhichejian.split("&");
										String newFuzhichejian = "";
										for (String string : strings) {
											if (!chejian.equals(string)) {
												if ("".equals(newFuzhichejian)) {
													newFuzhichejian = string;
												}else {
													newFuzhichejian = newFuzhichejian+"&"+string;
												}
											}
										}
										processTask.setFuzhichejian(newFuzhichejian);
									}else {
										if (chejian.equals(fuzhichejian)) {
											processTask.setFuzhichejian("");
										}
									}
								}
								processTask = (ProcessTask) PersistenceHelper.manager.save(processTask);
							}
						}


						//System.out.println("----------executorRole:" + executorRole);
						// 如果是驳回任务，则需要创建新的任务条目ProcessTaskItem
						if (routeSelect.equals("rejectTask")) {
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


							if ("报表类工艺编制".equals(taskItem.getTaskItemName())) {
								newTaskItem.setOwner(taskItem.getZhurengongyishi());
								newTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_BAOBIAOLEIRENWUZHIPAI_REFUSE);
								if (ProcessConstants.ROLE_GONGYIYUAN.equals(executorRole)) {
									newTaskItem.setExecutorRole(ProcessConstants.ROLE_GONGYIYUAN);
								}else if(ProcessConstants.ROLE_GONGYIZUZHANG.equals(executorRole)){
									newTaskItem.setExecutorRole(ProcessConstants.ROLE_GONGYIZUZHANG);
								}
							}else{
								if (ProcessConstants.ROLE_GONGYIYUAN.equals(executorRole)) {
									// 如果是工艺员驳回，则给其所在车间的工艺组长新建一条任务活动
									List<WTUser> allList = map.get(taskItem.getChejian());
									if (allList != null && !allList.isEmpty()) {
										WTUser user = allList.get(0);
										System.out.println("-------for user:" + user.getFullName() + " new ProcessTaskItem");
										newTaskItem.setOwner(user.getName());
									}else{
										newTaskItem.setOwner(taskItem.getCreatorName());
									}
									if (taskItem.getTaskType().equals(ProcessConstants.TASK_TYPE_LINSHIGONGYI)) {
										newTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_LINSHIGONGYIRENWUZHIPAI_JUJUE);
									} else if (taskItem.getTaskType().equals(ProcessConstants.TASK_TYPE_GONGYISHEJI)) {
										newTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_ZHIPAIGONGYIYUAN_JUJUE);
									} else if (taskItem.getTaskType().equals(ProcessConstants.TASK_TYPE_GONGYIGENGGAI)) {
										newTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_GONGYIGENGGAIRENWUZHIPAI_JUJUE);
									}else if (taskItem.getTaskType().equals(
											ProcessConstants.TASK_TYPE_BAOBIAOLEI)) {
										newTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_BAOBIAOLEIRENWUZHIPAI_REFUSE);
									}else if(ProcessConstants.TASK_TYPE_FEIGONGYISHEJI.equals(taskItem.getTaskType())){
										newTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_ZHIPAIGONGYIYUAN_JUJUE);
									}
									newTaskItem.setExecutorRole(ProcessConstants.ROLE_GONGYIZUZHANG);
								} else if (ProcessConstants.ROLE_GONGYIZUZHANG.equals(executorRole)) {
									// 如果是工艺组长驳回，则给相应的主任工艺师新建一条任务活动
									newTaskItem.setOwner(taskItem.getZhurengongyishi());
									newTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_ZHIPAIGONGYIZUZHANG_JUJUE);
									if(ProcessConstants.TASK_TYPE_FEIGONGYISHEJI.equals(taskItem.getTaskType())){
										newTaskItem.setTaskItemState(ProcessConstants.TASK_STATE_FEIGONGYIZUZHANGJUJUE);
									}
									IBAUtility ibaUtility = new IBAUtility(taskItem);
									String fuzhiType = ibaUtility.getIBAValue("PPTASKTYPE");
									if(fuzhiType!=null&&fuzhiType.contains("辅制")){
										newTaskItem.setExecutorRole(ProcessConstants.ROLE_GONGYIYUAN);
									}else{
										newTaskItem.setExecutorRole(ProcessConstants.ROLE_ZHURENGONGYISHI);
									}

									System.out.println("-------for user:" + taskItem.getZhurengongyishi()
											+ " new ProcessTaskItem");
								}
							}
                            if(StrUtil.isNotEmpty(taskItem.getOwner())){
                                newTaskItem = (ProcessTaskItem) PersistenceHelper.manager.save(newTaskItem);
                                IBAUtility ibaUtility = new IBAUtility(taskItem);
                                String zzgynumber = ibaUtility.getIBAValue("PPNUMBER");
                                if(zzgynumber == null) {
                                    zzgynumber = "";
                                }
                                String zzgyname = ibaUtility.getIBAValue("PPNAME");
                                if(zzgyname == null) {
                                    zzgyname = "";
                                }
                                String zftype = ibaUtility.getIBAValue("PPTASKTYPE");
                                if(zftype == null) {
                                    zftype = "";
                                }
                                String cldePlanTime = ibaUtility.getIBAValue("cldePlanTime");
                                if(cldePlanTime == null) {
                                    cldePlanTime = "";
                                }
                                String cldeEndTime = ibaUtility.getIBAValue("cldeEndTime");
                                if(cldeEndTime == null) {
                                    cldeEndTime = "";
                                }
                                String analysisnumber = ibaUtility.getIBAValue("ANALYSISNUMBER");
                                if(analysisnumber == null) {
                                    analysisnumber = "";
                                }
                                Map ibaMap = new HashMap();
                                ibaMap.put("PPNUMBER",zzgynumber);
                                ibaMap.put("PPNAME", zzgyname);
                                ibaMap.put("PPTASKTYPE", zftype);
                                ibaMap.put("cldePlanTime", cldePlanTime);
                                ibaMap.put("cldeEndTime", cldeEndTime);
                                ibaMap.put("ANALYSISNUMBER", analysisnumber);
                                IBAHelper ibaHelper = new IBAHelper(newTaskItem);
                                ibaHelper.setIBAValue(newTaskItem, ibaMap);
                            }
						}
						// 如果是工艺组长完成的任务活动，则需要给选定的工艺员创建新的任务活动
						// 如果是主任工艺师完成的任务，则需要给指定的工艺组长创建新的活动
						// 如果是工艺员完成的任务，检查当前工艺任务活动的执行情况，如果所有任务条目都已经完成，则将当前工艺任务的状态设置为"已完工"
						if (routeSelect.equals("completeTask")) {
							if (ProcessConstants.ROLE_GONGYIZUZHANG.equals(executorRole)) {
								if (gongyiyuanValues != null) {
									for (String userName : gongyiyuanValues) {
										//System.out.println("--------userName:" + userName);
										if (!"".equals(userName)) {
											userName = userName.substring(0, userName.indexOf("("));
											//System.out.println("--------userName:" + userName);
											ProcessTaskItem newTaskItem = ProcessTaskItem.newProcessTaskItem();
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
											newTaskItem.setExecutorRole(ProcessConstants.ROLE_GONGYIYUAN);
											newTaskItem.setDescription(taskItem.getDescription());
											newTaskItem.setOwner(userName);


											if (taskItem.getTaskType().equals(ProcessConstants.TASK_TYPE_LINSHIGONGYI)) {
												newTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_LINSHIGONGYIRENWU);
											} else if (taskItem.getTaskType()
													.equals(ProcessConstants.TASK_TYPE_GONGYISHEJI)) {
												newTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_BIANZHIGONGYI);
											} else if (taskItem.getTaskType().equals(
													ProcessConstants.TASK_TYPE_GONGYIGENGGAI)) {
												newTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_GONGYIGENGGAIRENWU);
											} else if (taskItem.getTaskType().equals(
													ProcessConstants.TASK_TYPE_BAOBIAOLEI)) {
												newTaskItem.setTaskItemName(ProcessConstants.REPORT_TASK_TYPE_GONGYIGENGGAI);
												ProcessTask processTask = ProcessUtil.getProcessTask(taskItem.getProcessTaskId());
												processTask.setEndDate(taskItem.getEndDate());
											}else if(taskItem.getTaskType().equals(
													ProcessConstants.TASK_TYPE_FEIGONGYISHEJI)){
												newTaskItem.setTaskItemName(ProcessConstants.TASK_TYPE_FEIGONGYISHEJI);
												ProcessTask processTask = ProcessUtil.getProcessTask(taskItem.getProcessTaskId());
												processTask.setTaskState(ProcessConstants.TASK_STATE_FEIGONGZHENGZAIJINXING);
												System.out.println("---------ProcessTask " + processTask.getNumber() + " completed!");
												PersistenceHelper.manager.save(processTask);

											}

											newTaskItem.setContainer(taskItem.getContainer());
											Folder folder = getFolder("/Default", taskItem.getContainer());
											FolderHelper.assignLocation((FolderEntry) newTaskItem, folder);

											newTaskItem = (ProcessTaskItem) PersistenceHelper.manager.save(newTaskItem);

											System.out.println("--------TechnicsReportStyle-------"+TechnicsReportStyle);
											IBAUtility ibaUtility1 = new IBAUtility(newTaskItem);
											IBAUtility ibaUtility2 = new IBAUtility(taskItem);
											if (!"".equals(TechnicsReportStyle)&&!"null".equals(TechnicsReportStyle)&&TechnicsReportStyle!=null) {
												ibaUtility1.setIBAValue("TECHNICSREPORTSTYLE", TechnicsReportStyle);
												ibaUtility2.setIBAValue("TECHNICSREPORTSTYLE", TechnicsReportStyle);
											}
											String cldePlanTime = ibaUtility2.getIBAValue("cldePlanTime");
											if(cldePlanTime == null){
												cldePlanTime = "";
											}
											String cldeEndTime = ibaUtility2.getIBAValue("cldeEndTime");
											if(cldeEndTime == null){
												cldeEndTime = "";
											}
                                            String analysisnumber = ibaUtility2.getIBAValue("ANALYSISNUMBER");
                                            if(analysisnumber == null){
                                                analysisnumber = "";
                                            }
											ibaUtility1.setIBAValue("cldePlanTime",cldePlanTime);
											ibaUtility1.setIBAValue("cldeEndTime",cldeEndTime);
                                            //继承更改影响分析编号属性
                                            ibaUtility1.setIBAValue("ANALYSISNUMBER", analysisnumber);
											newTaskItem = (ProcessTaskItem) ibaUtility1.updateAttributeContainer(newTaskItem);
											ibaUtility1.updateIBAHolder(newTaskItem);
											taskItem = (ProcessTaskItem) ibaUtility2.updateAttributeContainer(taskItem);
											ibaUtility2.updateIBAHolder(taskItem);
											System.out.println("-------for user:" + userName + " new ProcessTaskItem");
										}
									}

								}  else if (gongyiyuanValue != null) {
									String[] gyy_ayy = gongyiyuanValue.split(";");
									IBAUtility ibaUti = new IBAUtility(taskItem);
									String taskSubmitor = ProcessUtil.objtect2String(ibaUti.getIBAValue("PPTASKCREATOR"));
									for (String userName : gyy_ayy) {
										//System.out.println("--------userName:" + userName);
										if (!"".equals(userName)) {
											userName = userName.substring(0, userName.indexOf("("));
											//System.out.println("--------userName:" + userName);
											ProcessTaskItem newTaskItem = ProcessTaskItem.newProcessTaskItem();
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
											newTaskItem.setExecutorRole(ProcessConstants.ROLE_GONGYIYUAN);
											newTaskItem.setOwner(userName);

											if (taskItem.getTaskType().equals(ProcessConstants.TASK_TYPE_LINSHIGONGYI)) {
												if(taskItem.getIszhuzhi()==null||taskItem.getIszhuzhi()) {
													newTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_LINSHIGONGYIRENWU);
												} else {
													newTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_FUZHILINSHIGONGYIRENWU);
												}
											} else if (taskItem.getTaskType().equals(ProcessConstants.TASK_TYPE_GONGYISHEJI)) {
												if(taskItem.getIszhuzhi()==null||taskItem.getIszhuzhi()) {
													newTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_BIANZHIGONGYI);
												} else {
													newTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_FUZHIBIANZHIGONGYI);
												}
											} else if (taskItem.getTaskType().equals(ProcessConstants.TASK_TYPE_GONGYIGENGGAI)) {
												if(taskItem.getIszhuzhi()==null||taskItem.getIszhuzhi()) {
													newTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_GONGYIGENGGAIRENWU);
												} else {
													newTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_FUZHIGONGYIGENGGAIRENWU);
												}
											}else if (taskItem.getTaskType().equals(
													ProcessConstants.TASK_TYPE_BAOBIAOLEI)) {
												newTaskItem.setTaskItemName(ProcessConstants.REPORT_TASK_TYPE_GONGYIGENGGAI);
												ProcessTask processTask = ProcessUtil.getProcessTask(taskItem.getProcessTaskId());
												processTask.setEndDate(taskItem.getEndDate());
												PersistenceHelper.manager.save(processTask);
											}else if(taskItem.getTaskType().equals(
													ProcessConstants.TASK_TYPE_FEIGONGYISHEJI)){
												newTaskItem.setTaskItemName(ProcessConstants.TASK_TYPE_FEIGONGYISHEJI);
												ProcessTask processTask = ProcessUtil.getProcessTask(taskItem.getProcessTaskId());
												processTask.setTaskState(ProcessConstants.TASK_STATE_FEIGONGZHENGZAIJINXING);
												System.out.println("---------ProcessTask " + processTask.getNumber() + " completed!");
												PersistenceHelper.manager.save(processTask);
											}

											newTaskItem.setContainer(taskItem.getContainer());
											Folder folder = getFolder("/Default", taskItem.getContainer());
											FolderHelper.assignLocation((FolderEntry) newTaskItem, folder);

											newTaskItem = (ProcessTaskItem) PersistenceHelper.manager.save(newTaskItem);

											IBAUtility ibaUtility1 = new IBAUtility(newTaskItem);
											IBAUtility ibaUtility2 = new IBAUtility(taskItem);
											if (!"".equals(TechnicsReportStyle)&&!"null".equals(TechnicsReportStyle)&&TechnicsReportStyle!=null) {
												ibaUtility1.setIBAValue("TECHNICSREPORTSTYLE", TechnicsReportStyle);
												ibaUtility2.setIBAValue("TECHNICSREPORTSTYLE", TechnicsReportStyle);

											}
											String cldePlanTime = ibaUtility2.getIBAValue("cldePlanTime");
											if(cldePlanTime == null){
												cldePlanTime = "";
											}
											String cldeEndTime = ibaUtility2.getIBAValue("cldeEndTime");
											if(cldeEndTime == null){
												cldeEndTime = "";
											}
                                            String analysisnumber = ibaUtility2.getIBAValue("ANALYSISNUMBER");
                                            if(analysisnumber == null){
                                                analysisnumber = "";
                                            }
											ibaUtility1.setIBAValue("cldePlanTime",cldePlanTime);
											ibaUtility1.setIBAValue("cldeEndTime",cldeEndTime);
                                            //继承更改影响分析编号属性
                                            ibaUtility1.setIBAValue("ANALYSISNUMBER", analysisnumber);
											newTaskItem = (ProcessTaskItem) ibaUtility1.updateAttributeContainer(newTaskItem);
											ibaUtility1.updateIBAHolder(newTaskItem);
											taskItem = (ProcessTaskItem) ibaUtility2.updateAttributeContainer(taskItem);
											ibaUtility2.updateIBAHolder(taskItem);


											System.out.println("-------for user:" + userName + " new ProcessTaskItem");

											try {
												IBAUtility ibaUtility = new IBAUtility(newTaskItem);
												ibaUtility.setIBAValue("PPNUMBER", ProcessUtil.objtect2String(ibaUti.getIBAValue("PPNUMBER")));//主制工艺文件编号
												ibaUtility.setIBAValue("PPNAME", ProcessUtil.objtect2String(ibaUti.getIBAValue("PPNAME")));//主制工艺文件名称
												if(!taskItem.getTaskType().equals(
														ProcessConstants.TASK_TYPE_FEIGONGYISHEJI)){
													ibaUtility.setIBAValue("PPTASKTYPE", ProcessUtil.objtect2String(ibaUti.getIBAValue("PPTASKTYPE")));//此工艺任务为辅制工艺任务
												}
												ibaUtility.setIBAValue("PPTASKCREATOR", taskSubmitor);//此工艺任务的提交者
												newTaskItem = (ProcessTaskItem) ibaUtility.updateAttributeContainer(newTaskItem);
												ibaUtility.updateIBAHolder(newTaskItem);
											} catch (RemoteException e) {
												e.printStackTrace();
											} catch (ClassNotFoundException e) {
												e.printStackTrace();
											}
										}
									}
								} else {
									formProcessingStatus = FormProcessingStatus.FAILURE;
									msg.append("操作失败，请选中工艺员！");
								}
							} else if (ProcessConstants.ROLE_ZHURENGONGYISHI.equals(executorRole)) {
								// 处理主制车间
								if (zhuzhichejianValue != null && !"".equals(zhuzhichejianValue)) {
									IBAUtility ibaUti = new IBAUtility(taskItem);
									String taskSubmitor = ProcessUtil.objtect2String(ibaUti.getIBAValue("PPTASKCREATOR"));
									List<WTUser> users = map.get(zhuzhichejianValue);
									for (WTUser wtUser : users) {
										ProcessTaskItem newTaskItem = ProcessTaskItem.newProcessTaskItem();
										newTaskItem.setProcessTaskId(taskItem.getProcessTaskId());
										newTaskItem.setPartId(taskItem.getPartId());
										newTaskItem.setName(taskItem.getName());
										newTaskItem.setNumber(taskItem.getNumber());
										newTaskItem.setVersion(taskItem.getVersion());
										newTaskItem.setZhurengongyishi(taskItem.getZhurengongyishi());
										newTaskItem.setRenwuyaoqiu(taskItem.getRenwuyaoqiu());
										newTaskItem.setRenwuyiju(taskItem.getRenwuyiju());
										newTaskItem.setTaskType(taskItem.getTaskType());

										if (ProcessConstants.TASK_TYPE_LINSHIGONGYI.equals(taskItem.getTaskType())) {
											newTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_LINSHIGONGYIRENWUZHIPAI);
										} else if (ProcessConstants.TASK_TYPE_GONGYISHEJI.equals(taskItem.getTaskType())) {
											newTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_ZHIPAIGONGYIYUAN);
										} else if (ProcessConstants.TASK_TYPE_GONGYIGENGGAI.equals(taskItem.getTaskType())) {
											newTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_GONGYIGENGGAIRENWUZHIPAI);
										} else if(ProcessConstants.TASK_TYPE_FEIGONGYISHEJI.equals(taskItem.getTaskType())){
											newTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_FEIGONGYISHEJIZHIPAI);
										}

										newTaskItem.setTaskItemState(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN);
										if(ProcessConstants.TASK_TYPE_FEIGONGYISHEJI.equals(taskItem.getTaskType())){
											newTaskItem.setTaskItemState(ProcessConstants.TASK_STATE_FEIGONGZHIPAIZHONG);
										}
										newTaskItem.setEndDate(taskItem.getEndDate());
										newTaskItem.setExecutorRole(ProcessConstants.ROLE_GONGYIZUZHANG);
										newTaskItem.setChejian(zhuzhichejianValue);
										if(!ProcessConstants.TASK_TYPE_FEIGONGYISHEJI.equals(taskItem.getTaskType())){
											newTaskItem.setIszhuzhi(true);
										}
										newTaskItem.setOwner(wtUser.getName());

										newTaskItem.setContainer(taskItem.getContainer());
										Folder folder = getFolder("/Default", taskItem.getContainer());
										FolderHelper.assignLocation((FolderEntry) newTaskItem, folder);

										newTaskItem = (ProcessTaskItem) PersistenceHelper.manager.save(newTaskItem);
										System.out.println("-------for user:" + wtUser.getName() + " new ProcessTaskItem");

										try {
											IBAUtility ibaUtility = new IBAUtility(newTaskItem);
											ibaUtility.setIBAValue("PPNUMBER", ProcessUtil.objtect2String(ibaUti.getIBAValue("PPNUMBER")));//主制工艺文件编号
											ibaUtility.setIBAValue("PPNAME", ProcessUtil.objtect2String(ibaUti.getIBAValue("PPNAME")));//主制工艺文件名称
											ibaUtility.setIBAValue("PPTASKTYPE", ProcessUtil.objtect2String(ibaUti.getIBAValue("PPTASKTYPE")));//此工艺任务为辅制工艺任务
											ibaUtility.setIBAValue("PPTASKCREATOR",taskSubmitor);//此工艺任务的提交者

											String cldePlanTime = ibaUti.getIBAValue("cldePlanTime");
											if(cldePlanTime == null){
												cldePlanTime = "";
											}
											String cldeEndTime = ibaUti.getIBAValue("cldeEndTime");
											if(cldeEndTime == null){
												cldeEndTime = "";
											}
                                            String analysisnumber = ibaUti.getIBAValue("ANALYSISNUMBER");
                                            if(analysisnumber == null){
                                                analysisnumber = "";
                                            }
											ibaUtility.setIBAValue("cldePlanTime",cldePlanTime);
											ibaUtility.setIBAValue("cldeEndTime",cldeEndTime);
											ibaUtility.setIBAValue("ANALYSISNUMBER",analysisnumber);

											newTaskItem = (ProcessTaskItem) ibaUtility.updateAttributeContainer(newTaskItem);
											ibaUtility.updateIBAHolder(newTaskItem);
										} catch (RemoteException e) {
											e.printStackTrace();
										} catch (ClassNotFoundException e) {
											e.printStackTrace();
										}
									}
								}
								// 处理辅制车间
								if (fuzhichejianValue != null && !"".equals(fuzhichejianValue)) {
									if (fuzhichejianValue.contains("&")) {
										String[] fuzhi = fuzhichejianValue.split("&");
										for (String string : fuzhi) {
											List<WTUser> users = map.get(string);
											for (WTUser wtUser : users) {
												ProcessTaskItem newTaskItem = ProcessTaskItem.newProcessTaskItem();
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
												newTaskItem.setChejian(string);
												newTaskItem.setIszhuzhi(false);
												newTaskItem.setExecutorRole(ProcessConstants.ROLE_GONGYIZUZHANG);
												newTaskItem.setOwner(wtUser.getName());
												if (taskItem.getTaskType().equals(ProcessConstants.TASK_TYPE_LINSHIGONGYI)) {
													newTaskItem
													.setTaskItemName(ProcessConstants.TASK_NAME_LINSHIGONGYIRENWUZHIPAI);
												} else if (taskItem.getTaskType().equals(
														ProcessConstants.TASK_TYPE_GONGYISHEJI)) {
													newTaskItem
													.setTaskItemName(ProcessConstants.TASK_NAME_ZHIPAIGONGYIYUAN);
												} else if (taskItem.getTaskType().equals(
														ProcessConstants.TASK_TYPE_GONGYIGENGGAI)) {
													newTaskItem
													.setTaskItemName(ProcessConstants.TASK_NAME_GONGYIGENGGAIRENWUZHIPAI);
												}

												newTaskItem.setContainer(taskItem.getContainer());
												Folder folder = getFolder("/Default", taskItem.getContainer());
												FolderHelper.assignLocation((FolderEntry) newTaskItem, folder);

												newTaskItem = (ProcessTaskItem) PersistenceHelper.manager.save(newTaskItem);
												System.out.println("-------for user:" + wtUser.getName()
														+ " new ProcessTaskItem");
											}
										}
									} else {
										List<WTUser> users = map.get(fuzhichejianValue);
										for (WTUser wtUser : users) {
											ProcessTaskItem newTaskItem = ProcessTaskItem.newProcessTaskItem();
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
											newTaskItem.setChejian(fuzhichejianValue);
											newTaskItem.setIszhuzhi(false);
											newTaskItem.setExecutorRole(ProcessConstants.ROLE_GONGYIZUZHANG);
											newTaskItem.setOwner(wtUser.getName());
											if (taskItem.getTaskType().equals(ProcessConstants.TASK_TYPE_LINSHIGONGYI)) {
												newTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_LINSHIGONGYIRENWUZHIPAI);
											} else if (taskItem.getTaskType().equals(ProcessConstants.TASK_TYPE_GONGYISHEJI)) {
												newTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_ZHIPAIGONGYIYUAN);
											} else if (taskItem.getTaskType().equals(ProcessConstants.TASK_TYPE_GONGYIGENGGAI)) {
												newTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_GONGYIGENGGAIRENWUZHIPAI);
											}

											newTaskItem.setContainer(taskItem.getContainer());
											Folder folder = getFolder("/Default", taskItem.getContainer());
											FolderHelper.assignLocation((FolderEntry) newTaskItem, folder);

											newTaskItem = (ProcessTaskItem) PersistenceHelper.manager.save(newTaskItem);
											System.out.println("-------for user:" + wtUser.getName() + " new ProcessTaskItem");
										}
									}
								}


							} else {// 工艺员点击"完成任务"后需要判断整个工艺任务的状态

								if(ProcessConstants.TASK_TYPE_FEIGONGYISHEJI.equals(taskItem.getTaskType())){
									IBAUtility ibaUti = new IBAUtility(taskItem);
									String taskSubmitor = currentUser.getName();

									ProcessTaskItem newTaskItem = ProcessTaskItem.newProcessTaskItem();
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
									newTaskItem.setExecutorRole(ProcessConstants.ROLE_ZHURENGONGYISHI);
//                                    newTaskItem.setDescription(taskItem.getDescription());
									newTaskItem.setOwner(taskItem.getZhurengongyishi());


									if (taskItem.getTaskType().equals(
											ProcessConstants.TASK_TYPE_FEIGONGYISHEJI)) {
										newTaskItem.setTaskItemName(ProcessConstants.TASK_TYPE_FEIGONGYISHEJI);
									}

									newTaskItem.setContainer(taskItem.getContainer());
									Folder folder = getFolder("/Default", taskItem.getContainer());
									FolderHelper.assignLocation((FolderEntry) newTaskItem, folder);

									newTaskItem = (ProcessTaskItem) PersistenceHelper.manager.save(newTaskItem);

									try {
										IBAUtility ibaUtility = new IBAUtility(newTaskItem);
										ibaUtility.setIBAValue("PPNUMBER", ProcessUtil.objtect2String(ibaUti.getIBAValue("PPNUMBER")));//主制工艺文件编号
										ibaUtility.setIBAValue("PPNAME", ProcessUtil.objtect2String(ibaUti.getIBAValue("PPNAME")));//主制工艺文件名称
										ibaUtility.setIBAValue("PPTASKTYPE", ProcessUtil.objtect2String(ibaUti.getIBAValue("PPTASKTYPE")));//此工艺任务为辅制工艺任务
										ibaUtility.setIBAValue("PPTASKCREATOR",taskSubmitor);//此工艺任务的提交者

										String cldePlanTime = ibaUti.getIBAValue("cldePlanTime");
										if(cldePlanTime == null){
											cldePlanTime = "";
										}
										String cldeEndTime = ibaUti.getIBAValue("cldeEndTime");
										if(cldeEndTime == null){
											cldeEndTime = "";
										}
										ibaUtility.setIBAValue("cldePlanTime",cldePlanTime);
										ibaUtility.setIBAValue("cldeEndTime",cldeEndTime);

										newTaskItem = (ProcessTaskItem) ibaUtility.updateAttributeContainer(newTaskItem);
										ibaUtility.updateIBAHolder(newTaskItem);
									} catch (RemoteException e) {
										e.printStackTrace();
									} catch (ClassNotFoundException e) {
										e.printStackTrace();
									}

								}else{
									QueryResult qResult = ProcessUtil.getAllProcessTaskItemByPTask(taskItem.getProcessTaskId());
									boolean flag = true;
									while (qResult.hasMoreElements()) {
										ProcessTaskItem tempTaskItem = (ProcessTaskItem) qResult.nextElement();
										if (ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN.equals(tempTaskItem
												.getTaskItemState())) {
											flag = false;
											break;
										}
									}
									if (flag) {
										ProcessTask processTask = ProcessUtil.getProcessTask(taskItem.getProcessTaskId());
										processTask.setTaskState(ProcessConstants.TASK_STATE_YIWANGONG);
										System.out.println("---------ProcessTask " + processTask.getNumber() + " completed!");
										PersistenceHelper.manager.save(processTask);
									}

                                    //如果是制品返修任务，完成之后，通知NC/MES
                                    if(AnalysisConstant.RENWUYIJV_YIZHIPIN.equals(taskItem.getRenwuyiju()) || AnalysisConstant.RENWUYIJV_ZAIZHIPIN.equals(taskItem.getRenwuyiju())
                                            && ProcessConstants.TASK_NAME_LINSHIGONGYIRENWU.equals(taskItem.getTaskItemName())) {
                                        ReferenceFactory rf = new ReferenceFactory();
                                        String processdocNum = IBAHelper.getIBAValue(taskItem, "PROCESSDOCNUM");
                                        String analysisNumber = IBAHelper.getIBAValue(taskItem, "ANALYSISNUMBER");
                                        WTPart part = ProcessUtil.getWtPart(taskItem.getPartId());
                                        if(StrUtil.isNotEmpty(processdocNum) && StrUtil.isNotEmpty(analysisNumber) && part != null) {
                                            long vrOid = part.getBranchIdentifier();
                                            String partId = WTPart.class.getName() + ":" + vrOid;
                                            WTDocument document = WTDocumentUtil.getLatestDocumentByNumber(processdocNum);
                                            if(document != null) {
                                                String version = document.getIterationDisplayIdentifier().toString();
                                                String ppNumber = IBAHelper.getIBAValue(document, "PPNUMBER");
                                                if(AnalysisConstant.RENWUYIJV_YIZHIPIN.equals(taskItem.getRenwuyiju())) {
                                                    String url = "http://10.125.237.4:80/service/PdmtoNCservlet";
                                                    if(!PDMConfig.isZS){
                                                        url = "http://10.125.237.23:8099/service/PdmtoNCservlet";
                                                    }
                                                    JSONArray array = new JSONArray();
                                                    JSONObject json = new JSONObject();
                                                    json.put("yxfxcode", analysisNumber);//影响分析编号
                                                    json.put("partid", partId);//部件ID
                                                    json.put("fxgycode", ppNumber);//返修工艺编号
                                                    json.put("fxgyversion", version);//返修工艺版本
                                                    array.put(json);
                                                    try {
                                                        String body = HttpRequest.post(url).body(array.toString(), "application/json").execute().body();
                                                        JSONObject result = new JSONObject(body);
                                                        System.out.println("制品返修工艺推送结果 result: " + result);
                                                    } catch(Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                } else if(AnalysisConstant.RENWUYIJV_ZAIZHIPIN.equals(taskItem.getRenwuyiju())) {
                                                    JSONObject json = new JSONObject();
                                                    json.put("WORKFLOW_SNID", document.getNumber());
                                                    json.put("WORKFLOWNAME", ppNumber);
                                                    json.put("WORKFLOWVERSION", version);
                                                    json.put("ANALYSISID", analysisNumber);
                                                    json.put("BJID", partId);
                                                    json.put("GYY", document.getModifier().getName());
                                                    json.put("TYPE", IBAHelper.getIBAValue(document, "PPLANTYPE"));
                                                    //返回路卡号
                                                    String renwuyaoqiu = taskItem.getRenwuyaoqiu();
                                                    String card = "";
                                                    if(StrUtil.isNotEmpty(renwuyaoqiu)) {
                                                        String[] strs = renwuyaoqiu.split("；");
                                                        for(String str : strs) {
                                                            if(str.indexOf("路卡号：") > -1) {
                                                                String mes = str.substring(str.indexOf("路卡号：") + 4);
                                                                if(StrUtil.isNotEmpty(mes)) {
                                                                    card += mes + ",";
                                                                }
                                                            }
                                                        }
                                                        if(card.length() > 0) {
                                                            card = card.substring(0, card.length() - 1);
                                                        }
                                                    }
                                                    json.put("CARD", card);
                                                    String url = "http://10.125.237.6/CamstarPortal/webservice.asmx";
                                                    if(!PDMConfig.isZS){
                                                        url = "http://10.125.192.60/CamstarPortal/webservice.asmx";
                                                    }
                                                    String namespace = "http://tempuri.org/";
                                                    Service service = new Service();
                                                    try {
                                                        Call call = (Call) service.createCall();
                                                        call.setTimeout(new Integer(60000));
                                                        call.setTargetEndpointAddress(new URL(url));
                                                        call.setOperationName(new QName(namespace, "ReceiveWorkFlowAnalysisInfo"));
                                                        call.addParameter(new QName(namespace, "JsonData"), XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);
                                                        call.setReturnType(XMLType.XSD_STRING);
                                                        call.setUseSOAPAction(true);
                                                        call.setSOAPActionURI(namespace + "ReceiveWorkFlowAnalysisInfo");
                                                        String ret = call.invoke(new Object[]{json.toString()}).toString();
                                                        System.out.println("制品返修工艺推送结果 result: " + ret);
                                                    } catch(Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
							}
						}
					}

					}
            }
            msg.append("操作成功，任务已经处理完毕！");
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            formProcessingStatus = FormProcessingStatus.FAILURE;
            msg.append("操作失败，任务未做任何处理！");
            e.printStackTrace();
        } finally {
            if (transaction != null) {
                transaction.rollback();
            }
            message.addMessage(msg.toString());
            formResult.addFeedbackMessage(message);
            formResult.setStatus(formProcessingStatus);
            formResult.setNextAction(FormResultAction.REFRESH_CURRENT_PAGE);
        }
        return formResult;
    }

    /**
     * 辅制工艺任务点击"无需编制工艺"按钮并完成任务
     *
     * @param commandBean
     * @return
     * @throws WTException
     */
    public static FormResult completeTask2(NmCommandBean commandBean) throws WTException {
    	WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
        FormResult formResult = new FormResult();
        FeedbackMessage message = new FeedbackMessage();
        StringBuffer msg = new StringBuffer();
        FormProcessingStatus formProcessingStatus = FormProcessingStatus.SUCCESS;
        Transaction transaction = new Transaction();
        try {
            transaction.start();
            NmOid nmOid = commandBean.getActionOid();
            Object object = nmOid.getRefObject();
            if ((object != null) && (object instanceof ProcessTaskItem)) {
                ProcessTaskItem taskItem = (ProcessTaskItem) object;
                IBAUtility ibaUti = new IBAUtility(taskItem);
                String taskSubmitor = ProcessUtil.objtect2String(ibaUti.getIBAValue("PPTASKCREATOR"));
                if(!"".equals(taskSubmitor)) {
                	taskSubmitor = taskSubmitor.substring(0, taskSubmitor.indexOf("("));

                	// 获取jsp页面提交的信息
                    HttpServletRequest request = commandBean.getRequest();
                    Map requestmap = request.getParameterMap();
                    Iterator iterator = requestmap.keySet().iterator();
                    while (iterator.hasNext()) {
                        String key = String.valueOf(iterator.next());
                        if ("beizhi".equals(key)) {
                            String beizhiValue = request.getParameter(key);
                            if (beizhiValue != null) {
                                taskItem.setDescription(beizhiValue);
                            } else {
                                taskItem.setDescription("");
                            }
                        }
                    }
                    // 更改当前任务活动条目的状态
                    taskItem.setCompletedBy(currentUser.getFullName());
                    taskItem.setRouteSelect(ProcessConstants.TASKITEM_ROUTESELECT_WUXUBIANZHIGONGY);
                    taskItem.setTaskItemState(ProcessConstants.TASKITEM_STATE_YIWANCHENG);

                    taskItem = (ProcessTaskItem) PersistenceHelper.manager.save(taskItem);

                    ProcessTaskItem newTaskItem = ProcessTaskItem.newProcessTaskItem();
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
                    newTaskItem.setExecutorRole(ProcessConstants.ROLE_GONGYIYUAN);
                    newTaskItem.setOwner(taskSubmitor);
                    newTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_WUXUBIANZHIGONGY);

                    newTaskItem.setContainer(taskItem.getContainer());
                    Folder folder = getFolder("/Default", taskItem.getContainer());
                    FolderHelper.assignLocation((FolderEntry) newTaskItem, folder);

                    newTaskItem = (ProcessTaskItem) PersistenceHelper.manager.save(newTaskItem);
                    System.out.println("-------for user:" + taskSubmitor + " new ProcessTaskItem");

                    try {

        				IBAUtility ibaUtility = new IBAUtility(newTaskItem);
        				ibaUtility.setIBAValue("PPNUMBER", ProcessUtil.objtect2String(ibaUti.getIBAValue("PPNUMBER")));//主制工艺文件编号
        				ibaUtility.setIBAValue("PPNAME", ProcessUtil.objtect2String(ibaUti.getIBAValue("PPNAME")));//主制工艺文件名称
        				ibaUtility.setIBAValue("PPTASKTYPE", ProcessUtil.objtect2String(ibaUti.getIBAValue("PPTASKTYPE")));//此工艺任务为辅制工艺任务
        				ibaUtility.setIBAValue("PPTASKCREATOR", ProcessUtil.objtect2String(ibaUti.getIBAValue("PPTASKCREATOR")));//此工艺任务的提交者
        				newTaskItem = (ProcessTaskItem) ibaUtility.updateAttributeContainer(newTaskItem);
        				ibaUtility.updateIBAHolder(newTaskItem);
        			} catch (RemoteException e) {
        				e.printStackTrace();
        			} catch (ClassNotFoundException e) {
        				e.printStackTrace();
        			}

                    msg.append("操作成功，任务已经处理完毕！");
                    transaction.commit();
                    transaction = null;
                } else {
                	formProcessingStatus = FormProcessingStatus.FAILURE;
                    msg.append("操作失败，辅制工艺任务提交者为空！");
                    transaction = null;
                }
            }
        } catch (Exception e) {
            formProcessingStatus = FormProcessingStatus.FAILURE;
            msg.append("操作失败，任务未做任何处理！");
            e.printStackTrace();
        } finally {
            if (transaction != null) {
                transaction.rollback();
            }
            formResult.setStatus(formProcessingStatus);
            message.addMessage(msg.toString());
            formResult.addFeedbackMessage(message);
            formResult.setNextAction(FormResultAction.NONE);
            formResult.setNextAction(FormResultAction.REFRESH_OPENER);
        }
        return formResult;
    }


    /**
     * 非工艺计划任务"确认完成"按钮
     *
     * @param commandBean
     * @return
     * @throws WTException
     */
    public static FormResult completeTask3(NmCommandBean commandBean) throws WTException {
    	WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
        FormResult formResult = new FormResult();
        FeedbackMessage message = new FeedbackMessage();
        StringBuffer msg = new StringBuffer();
        FormProcessingStatus formProcessingStatus = FormProcessingStatus.SUCCESS;
        Transaction transaction = new Transaction();
        try {
            transaction.start();
            NmOid nmOid = commandBean.getActionOid();
            Object object = nmOid.getRefObject();
            if ((object != null) && (object instanceof ProcessTaskItem)) {
                ProcessTaskItem taskItem = (ProcessTaskItem) object;
                taskItem.setTaskItemState(ProcessConstants.TASK_STATE_FEIGONGYIWANCHENG);
                taskItem.setCompletedBy(currentUser.getFullName());
                taskItem.setRouteSelect(ProcessConstants.TASKITEM_ROUTESELECT_WANCHENGRENWU);
                PersistenceHelper.manager.save(taskItem);

                //
                ProcessTask processTask = ProcessUtil.getProcessTask(taskItem.getProcessTaskId());
                processTask.setTaskState(ProcessConstants.TASK_STATE_FEIGONGYIWANCHENG);
                Date wanchengdate = new Date();
                processTask.setWanchengDate(new Timestamp(wanchengdate.getTime()));
                System.out.println("---------ProcessTask " + processTask.getNumber() + " completed!");
                PersistenceHelper.manager.save(processTask);



                msg.append("操作成功，任务已经处理完毕！");
                transaction.commit();
                transaction = null;
            }

        } catch (Exception e) {
            formProcessingStatus = FormProcessingStatus.FAILURE;
            msg.append("操作失败，任务未做任何处理！");
            e.printStackTrace();
        } finally {
            if (transaction != null) {
                transaction.rollback();
            }
            formResult.setStatus(formProcessingStatus);
            message.addMessage(msg.toString());
            formResult.addFeedbackMessage(message);
            formResult.setNextAction(FormResultAction.NONE);
            formResult.setNextAction(FormResultAction.REFRESH_OPENER);
        }
        return formResult;
    }


    /**
     * 非工艺计划任务"问题反馈"按钮
     *
     * @param commandBean
     * @return
     * @throws WTException
     */
    public static FormResult rejectTask3(NmCommandBean commandBean) throws WTException {
    	WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
        FormResult formResult = new FormResult();
        FeedbackMessage message = new FeedbackMessage();
        StringBuffer msg = new StringBuffer();
        FormProcessingStatus formProcessingStatus = FormProcessingStatus.SUCCESS;
        Transaction transaction = new Transaction();
        String beizhiValue = "";

        HttpServletRequest request = commandBean.getRequest();
        Map requestmap = request.getParameterMap();
        Iterator iterator = requestmap.keySet().iterator();
        beizhiValue = request.getParameter("beizhi");
        try {
            transaction.start();
            NmOid nmOid = commandBean.getActionOid();
            Object object = nmOid.getRefObject();
            if ((object != null) && (object instanceof ProcessTaskItem)) {
                ProcessTaskItem taskItem = (ProcessTaskItem) object;

             //-----
                ProcessTaskItem newTaskItem = ProcessTaskItem.newProcessTaskItem();
                IBAUtility ibaUti = new IBAUtility(taskItem);
                String taskSubmitor = ProcessUtil.objtect2String(ibaUti.getIBAValue("PPTASKCREATOR"));
                newTaskItem.setOwner(taskSubmitor);

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
                newTaskItem.setExecutorRole(ProcessConstants.ROLE_GONGYIYUAN);
//                newTaskItem.setDescription(taskItem.getDescription());
                if (beizhiValue != null) {
                	taskItem.setDescription(beizhiValue);
              } else {
            	  taskItem.setDescription("");
              }
                if (taskItem.getTaskType().equals(
                        ProcessConstants.TASK_TYPE_FEIGONGYISHEJI)) {
                	newTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_FEIGONGYISHEFANKUI);
                }

                newTaskItem.setContainer(taskItem.getContainer());
                Folder folder = getFolder("/Default", taskItem.getContainer());
                FolderHelper.assignLocation((FolderEntry) newTaskItem, folder);

                newTaskItem = (ProcessTaskItem) PersistenceHelper.manager.save(newTaskItem);

                try {

    				IBAUtility ibaUtility = new IBAUtility(newTaskItem);
    				ibaUtility.setIBAValue("PPNUMBER", ProcessUtil.objtect2String(ibaUti.getIBAValue("PPNUMBER")));//主制工艺文件编号
    				ibaUtility.setIBAValue("PPNAME", ProcessUtil.objtect2String(ibaUti.getIBAValue("PPNAME")));//主制工艺文件名称
    				ibaUtility.setIBAValue("PPTASKTYPE", ProcessUtil.objtect2String(ibaUti.getIBAValue("PPTASKTYPE")));//此工艺任务为辅制工艺任务
    				ibaUtility.setIBAValue("PPTASKCREATOR", ProcessUtil.objtect2String(ibaUti.getIBAValue("PPTASKCREATOR")));//此工艺任务的提交者
    				newTaskItem = (ProcessTaskItem) ibaUtility.updateAttributeContainer(newTaskItem);
    				ibaUtility.updateIBAHolder(newTaskItem);
    			} catch (RemoteException e) {
    				e.printStackTrace();
    			} catch (ClassNotFoundException e) {
    				e.printStackTrace();
    			}

                taskItem.setCompletedBy(currentUser.getFullName());
                taskItem.setRouteSelect(ProcessConstants.TASKITEM_ROUTESELECT_BOHUI);
                taskItem.setTaskItemState(ProcessConstants.TASK_STATE_FEIGONGYIWANCHENG);
                taskItem = (ProcessTaskItem) PersistenceHelper.manager.save(taskItem);

                IBAUtility ibaUtility2 = new IBAUtility(taskItem);
                taskItem = (ProcessTaskItem) ibaUtility2.updateAttributeContainer(taskItem);
                ibaUtility2.updateIBAHolder(taskItem);

                //---




                msg.append("操作成功，任务已经处理完毕！");
                transaction.commit();
                transaction = null;
            }

        } catch (Exception e) {
            formProcessingStatus = FormProcessingStatus.FAILURE;
            msg.append("操作失败，任务未做任何处理！");
            e.printStackTrace();
        } finally {
            if (transaction != null) {
                transaction.rollback();
            }
            formResult.setStatus(formProcessingStatus);
            message.addMessage(msg.toString());
            formResult.addFeedbackMessage(message);
            formResult.setNextAction(FormResultAction.NONE);
            formResult.setNextAction(FormResultAction.REFRESH_OPENER);
        }
        return formResult;
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
    /**
     *
     * @throws WTException
     * */

	public static String checkYiwancheng(ProcessTaskItem taskItem) throws WTException {
		if (ProcessConstants.TASK_TYPE_BAOBIAOLEI.equals(taskItem.getTaskType())) {
			boolean hasReport = false;
			WTPart part = ProcessUtil.getWtPart(taskItem.getPartId());
			if (part != null) {
				WTPart lpart = (WTPart) VersionControlHelper.service.getLatestIteration(part, false);
				QueryResult qr2 = WTPartHelper.service.getDescribedByWTDocuments(lpart, true);
				LatestConfigSpec lcs = new LatestConfigSpec();
				qr2 = lcs.process(qr2);
				while (qr2.hasMoreElements()) {
					WTDocument document = (WTDocument) qr2.nextElement();
					String typeName = TypedUtility.getTypeIdentifier(document).getTypename();
					//System.out.println("typeName=" + typeName);

					if (typeName.contains("casc.sast.149.reportTechnics")) {
						hasReport = true;
						document = (WTDocument) VersionControlHelper.service.getLatestIteration(document, true);
						String state = document.getState().getState().getDisplay(Locale.CHINA);
						if (!Constants.STATE_YIPIZHUN.equals(state)) {
							return "操作失败，该任务下的所有报表类工艺必须已批准才能完成该任务";
						}
					}
				}
				// "操作失败，该任务下的所有报表类工艺必须已批准才能完成该任务！"
				if (!hasReport) {
					return "操作失败，该任务下的还未创建报表类工艺，不能完成该任务！";
				}

			}

		} else {
			return "";
		}
		return "";
	}

}
