package ext.casc.process.assign;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import wt.fc.PersistenceHelper;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.folder.FolderNotFoundException;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;

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
import ext.casc.process.util.ProcessUtil;
import ext.casc.util.IBAUtility;
import ext.casc.util.WCUtil;

public class AddProTaskProcessor extends DefaultObjectFormProcessor {

    @Override
    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> list) throws WTException {
        FormResult form = new FormResult();
        HttpServletRequest request = commandBean.getRequest();
        WTUser currentUser = (WTUser)SessionHelper.getPrincipal();
        // 用于存储各零件的任务辅制车间
        Map<String, String> fzcjMap = new HashMap<String, String>();
        Map map = request.getParameterMap();
        Iterator iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = String.valueOf(iterator.next());
            //System.out.println("-------key:" + key);
            if (key.indexOf("_addchejian")>-1) {
                String value = key.substring(key.length()-1, key.length());
                String oidValue = key.substring(0, key.indexOf("_"));
                Set<String> keysSet = fzcjMap.keySet();
                if (keysSet.contains(oidValue)) {
                    value = fzcjMap.get(oidValue) + "&" + value;
                }
                //System.out.println("-------value:" + value);
                //System.out.println("-------oidValue:" + oidValue);
                fzcjMap.put(oidValue, value);
            }
        }
        //System.out.println("----------fzcjMap:" + fzcjMap);
        FeedbackMessage message = new FeedbackMessage();
        StringBuffer msg = new StringBuffer();
        FormProcessingStatus formProcessingStatus = FormProcessingStatus.SUCCESS;
        Transaction tx = new Transaction();
        try {
            tx.start();
            if (fzcjMap != null && !fzcjMap.isEmpty()) {
                WTContainer container = commandBean.getContainer();
                Map<String, List<WTUser>> gongYiZuZhangmap = ProcessUtil.getGroupAndUsersInOrgContainer();
                //获取"项目部型号主管"角色的人
                List<WTUser> xmbUsers = ProcessUtil.getRoleUsersByWTContainer("XIANGMUBUXINGHAOZHUGUANG", container);
                gongYiZuZhangmap.put(Constants.ROLE_XIANGMUBUXINGHAOZHUGUAN, xmbUsers);
                Set<String> set = fzcjMap.keySet();
                for (String oidValue : set) {
                    WTPart part = (WTPart) WCUtil.getPersistable(oidValue);
                    ProcessTask processTask = ProcessUtil.getProcessTaskByPart(part,ProcessConstants.TASK_TYPE_ZZGYRW);
                    //System.out.println("----------processTask:" + processTask);
                    Folder folder = getFolder("/Default", container);

                    // 创建工艺任务活动条目,针对每一个车间工艺组长创建一个活动条目
                    String fuzhichejian = fzcjMap.get(oidValue);
                    if (fuzhichejian.contains("&")) {
                        String[] chenjian = fuzhichejian.split("&");
                        for (String str : chenjian) {
                            List<WTUser> fzZuZhang = gongYiZuZhangmap.get(str);
                            //System.out.println("----------fzZuZhang:" + fzZuZhang);
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
                                processTaskItem.setRenwuyaoqiu(processTask.getRenwuyaoqiu());
                                processTaskItem.setTaskType(processTask.getTaskType());
                                processTaskItem.setTaskItemState(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN);
                                processTaskItem.setEndDate(processTask.getEndDate());
                                if (processTask.getTaskType().equals(ProcessConstants.TASK_TYPE_LINSHIGONGYI)) {
                                    processTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_LINSHIGONGYIRENWUZHIPAI);
                                } else if (processTask.getTaskType().equals(ProcessConstants.TASK_TYPE_GONGYISHEJI)) {
                                    processTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_ZHIPAIGONGYIYUAN);
                                } else if (processTask.getTaskType().equals(ProcessConstants.TASK_TYPE_GONGYIGENGGAI)) {
                                    processTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_GONGYIGENGGAIRENWUZHIPAI);
                                }
                                processTaskItem.setContainer(part.getContainer());
                                FolderHelper.assignLocation((FolderEntry) processTaskItem, folder);

                                PersistenceHelper.manager.save(processTaskItem);
                            }
                        }
                    } else {
                        List<WTUser> fzZuZhang = gongYiZuZhangmap.get(fuzhichejian);
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
                            processTaskItem.setRenwuyaoqiu(processTask.getRenwuyaoqiu());
                            processTaskItem.setTaskType(processTask.getTaskType());
                            processTaskItem.setTaskItemState(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN);
                            processTaskItem.setEndDate(processTask.getEndDate());
                            if (processTask.getTaskType().equals(ProcessConstants.TASK_TYPE_LINSHIGONGYI)) {
                                processTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_LINSHIGONGYIRENWUZHIPAI);
                            } else if (processTask.getTaskType().equals(ProcessConstants.TASK_TYPE_GONGYISHEJI)) {
                                processTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_ZHIPAIGONGYIYUAN);
                            } else if (processTask.getTaskType().equals(ProcessConstants.TASK_TYPE_GONGYIGENGGAI)) {
                                processTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_GONGYIGENGGAIRENWUZHIPAI);
                            }
                            processTaskItem.setContainer(part.getContainer());
                            FolderHelper.assignLocation((FolderEntry) processTaskItem, folder);

                            PersistenceHelper.manager.save(processTaskItem);

                            msg.append(ProcessConstants.JSP_MSG_SUCCESS);
                        }
                    }
                    //更新此工艺任务的辅制车间
                    String processTaskFzchejian = processTask.getFuzhichejian();
                    processTaskFzchejian = processTaskFzchejian+"&"+fuzhichejian;
                    processTask.setFuzhichejian(processTaskFzchejian);
                    PersistenceHelper.manager.save(processTask);

                    //更新part属性"辅制车间"的值
                    IBAUtility ibaUtility = new IBAUtility(part);
                    ibaUtility.setIBAValue("FZCJ", processTaskFzchejian.replaceAll("&", "-"));
                    part = (WTPart)ibaUtility.updateAttributeContainer(part);
                    ibaUtility.updateIBAHolder(part);
                    part = (WTPart)PersistenceHelper.manager.refresh(part);
                }
            }else {
                formProcessingStatus = FormProcessingStatus.FAILURE;
                msg.append(ProcessConstants.JSP_MSG_FAILD);
            }
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
        return form;
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
