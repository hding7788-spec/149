package ext.casc.process.assign;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.process.ProcessConstants;
import ext.casc.process.ProcessTaskItem;
import ext.casc.util.IBAUtility;
import ext.casc.util.WCUtil;
import wt.fc.PersistenceHelper;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.folder.FolderNotFoundException;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.org.WTUser;
import wt.pom.Transaction;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

public class AssignGongYiYuanProcessor extends DefaultObjectFormProcessor {

    @Override
    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> list) throws WTException {
        FormResult form = new FormResult();
        FeedbackMessage message = new FeedbackMessage();
        StringBuffer msg = new StringBuffer();
        FormProcessingStatus formProcessingStatus = FormProcessingStatus.SUCCESS;
        Transaction tx = new Transaction();
        try {
            tx.start();
            HttpServletRequest request = commandBean.getRequest();
            HttpSession session = commandBean.getRequest().getSession();
            WTUser currentUser = (WTUser)SessionHelper.getPrincipal();

            List<String> oidList = (List<String>) session.getAttribute("oidList");
            System.out.println("---------oidList:" + oidList);

            //用于存储工艺员
            Map<String, String> gongyiyuanMap = new HashMap<String, String>();
            //用于存储备注
            Map<String, String> bzMap = new HashMap<String, String>();

            Map map = request.getParameterMap();
            Iterator iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String key = String.valueOf(iterator.next());
                if (key.endsWith("_sign_person_value")) {//工艺员
                    //System.out.println("-------key:" + key);
                	//ext.casc.process.ProcessTaskItem:209232_sign_person_value
                    String oidValue = key.substring(0, key.indexOf("_sign_person_value"));
                    //System.out.println("-------oidValue:" + oidValue);
                    String value = request.getParameter(key);
                    //System.out.println("-------工艺员:" + value);
                    gongyiyuanMap.put(oidValue, value);
                } else if (key.endsWith("_description___textbox")) {// 任务要求
                    //System.out.println("-------key:" + key);
                    String oidValue = key.substring(key.lastIndexOf("$"), key.length());
                    oidValue = oidValue.substring(4, oidValue.indexOf("_description___textbox"));
                    //System.out.println("-------oidValue:" + oidValue);
                    String value = request.getParameter(key);
                    //System.out.println("-------备注:" + value);
                    bzMap.put(oidValue, value);
                }
            }
            //System.out.println("---------gongyiyuanMap:"+gongyiyuanMap);
            for (String oid : oidList) {
                oid = oid.substring(oid.indexOf(":")+1, oid.length());
                ProcessTaskItem taskItem = (ProcessTaskItem)WCUtil.getPersistable(oid);
                //System.out.println("---------oid:"+oid);
                String userOids = gongyiyuanMap.get(oid);
                String beizhu = bzMap.get(oid);
                String[] userArr = userOids.split(";");
                for(String userOid:userArr) {
                	if(userOid != null && !"".equals(userOid)) {
                		WTUser user = (WTUser)WCUtil.getPersistable(userOid);
                		//给指定工艺员创建新任务
                        ProcessTaskItem newTaskItem = ProcessTaskItem.newProcessTaskItem();
                        newTaskItem.setProcessTaskId(taskItem.getProcessTaskId());
                        newTaskItem.setPartId(taskItem.getPartId());
                        newTaskItem.setOwner(user.getName());
                        newTaskItem.setName(taskItem.getName());
                        newTaskItem.setNumber(taskItem.getNumber());
                        newTaskItem.setVersion(taskItem.getVersion());
                        newTaskItem.setZhurengongyishi(taskItem.getZhurengongyishi());
                        newTaskItem.setChejian(taskItem.getChejian());
                        newTaskItem.setExecutorRole(ProcessConstants.ROLE_GONGYIYUAN);
                        newTaskItem.setIszhuzhi(taskItem.getIszhuzhi());
                        newTaskItem.setRenwuyaoqiu(taskItem.getRenwuyaoqiu());
                        newTaskItem.setRenwuyiju(taskItem.getRenwuyiju());
                        newTaskItem.setTaskType(taskItem.getTaskType());
                        newTaskItem.setTaskItemState(ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN);
                        newTaskItem.setEndDate(taskItem.getEndDate());
                        newTaskItem.setDescription(beizhu);
                        if (taskItem.getTaskType().equals(ProcessConstants.TASK_TYPE_LINSHIGONGYI)) {
                            newTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_LINSHIGONGYIRENWU);
                        } else if (taskItem.getTaskType().equals(ProcessConstants.TASK_TYPE_GONGYISHEJI)) {
                            newTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_BIANZHIGONGYI);
                        } else if (taskItem.getTaskType().equals(ProcessConstants.TASK_TYPE_GONGYIGENGGAI)) {
                            newTaskItem.setTaskItemName(ProcessConstants.TASK_NAME_GONGYIGENGGAIRENWU);
                        }

                        newTaskItem.setContainer(taskItem.getContainer());
                        Folder folder = getFolder("/Default", taskItem.getContainer());
                        FolderHelper.assignLocation((FolderEntry) newTaskItem, folder);

                        newTaskItem = (ProcessTaskItem)PersistenceHelper.manager.save(newTaskItem);

                        IBAUtility ibaUtility1 = new IBAUtility(newTaskItem);
                        IBAUtility ibaUtility2 = new IBAUtility(taskItem);
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

                        //结束当前任务，即是将当前任务的状态设置为"已完成"
                        taskItem.setTaskItemState(ProcessConstants.TASKITEM_STATE_YIWANCHENG);
                        taskItem.setCompletedBy(currentUser.getFullName());
                        taskItem.setRouteSelect(ProcessConstants.TASKITEM_ROUTESELECT_WANCHENGRENWU);
                        taskItem = (ProcessTaskItem)PersistenceHelper.manager.save(taskItem);
                	}
                }
            }

            msg.append("数据处理完毕,工艺员将收到工艺编制任务！");
            tx.commit();
            tx = null;
        } catch (Exception e) {
            formProcessingStatus = FormProcessingStatus.FAILURE;
            msg.append("指派工艺员失败！");
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
