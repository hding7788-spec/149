package ext.casc.integrate.task;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.glaway.mpm.util.PropertiesConfigs;
import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.UserUtil;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.integrate.bean.TaskBean;
import ext.casc.process.ProcessTaskItem;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceManagerEvent;
import wt.fc.ReferenceFactory;
import wt.fc.WTReference;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.ownership.Ownership;
import wt.queue.ProcessingQueue;
import wt.queue.QueueHelper;
import wt.queue.WtQueue;
import wt.services.Manager;
import wt.services.ManagerServiceFactory;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

import java.text.SimpleDateFormat;

public class SyncTaskUtil {

    private static PropertiesUtil propertiesUtil = new PropertiesUtil(PropertiesConfigs.GLAWAY_149_CONFIG_PATH);

    /**
     * 方法功能:门户集成
     *
     * @author cjh
     * @date 2024/1/22
     */
    public static void sendTask(String oid, String type) throws Exception {
        try {
            Thread.sleep(1000);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        ReferenceFactory rf = new ReferenceFactory();
        Persistable target = null;
        try {
            target = rf.getReference(oid).getObject();
        } catch(Exception e){
            e.printStackTrace();
        }
        if(target == null) {
            return;
        }else {

        }

        TaskBean task = new TaskBean();
        JSON json = null;
        task.setSyscode("GY_PDM");

        WTProperties prop = WTProperties.getLocalProperties();
        String hName = prop.getProperty("wt.server.hostname");
        String webPort = prop.getProperty("wt.webserver.port");
        String url = "http://" + hName + ":" + webPort + "/Windchill/ca/ca_login.jsp?oid=OR%3A";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        String flowId = oid.replaceAll(":", "%3A");
        task.setFlowid(flowId);
        task.setPcurl(url + flowId);
        String time = sdf.format(target.getPersistInfo().getCreateStamp());
        task.setCreatedatetime(time);
        task.setReceivedatetime(time);
        task.setReceivets(StrUtil.toString(System.currentTimeMillis()));

        if(target instanceof ProcessTaskItem) {
            ProcessTaskItem taskItem = (ProcessTaskItem) target;
            task.setRequestname(taskItem.getTaskItemName()+"("+taskItem.getNumber()+")");
            task.setWorkflowname("工艺任务");
            task.setNodename(taskItem.getExecutorRole());
            if(StrUtil.isEmpty(taskItem.getOwner())){
                return;
            }
            WTUser creator = UserUtil.getUser(taskItem.getOwner());
            if(creator == null) {
                System.out.println("同步工艺任务错误 >>>>>> 未找到任务所有者对应人员：" + taskItem.getOwner());
                return;
            }
            String name = creator.getName() + "@149.sast.casc";
            if(StrUtil.containsAny(name,"wcadmin","Administrator")){
                return;
            }
            task.setCreator(name);
            task.setReceiver(name);
            String state = taskItem.getTaskItemState();
            if(PersistenceManagerEvent.POST_MODIFY.equals(type) && StrUtil.equalsAny(state,"已完成","已作废","已删除","已终止","已完工")) {
                task.setIsremark("2");
                task.setViewtype("1");
                json = JSONUtil.parse(task);
            } else if(PersistenceManagerEvent.POST_STORE.equals(type) && StrUtil.equalsAny(state,"正在进行")) {
                task.setIsremark("0");
                task.setViewtype("0");
                json = JSONUtil.parse(task);
            }
        } else if(target instanceof WorkItem) {
            WorkItem workItem = (WorkItem) target;
            WfAssignedActivity wa = (WfAssignedActivity) workItem.getSource().getObject();
            task.setWorkflowname("流程任务");
            task.setNodename(wa.getName());
            WTUser creator = null;
            Ownership ownership = workItem.getOwnership();
            if(ownership != null) {
                creator = (WTUser) ownership.getOwner().getObject();
            }else {
                return;
            }
            String name = creator.getName() + "@149.sast.casc";
            if(StrUtil.containsAny(name,"wcadmin","Administrator")){
                return;
            }
            task.setCreator(name);
            task.setReceiver(name);
            if(PersistenceManagerEvent.POST_MODIFY.equals(type) || PersistenceManagerEvent.PRE_REMOVE.equals(type)){
                String state = workItem.getStatus().toString();
                if("COMPLETED".equals(state)) {
                    task.setRequestname(getWorkItemName(workItem));
                    task.setIsremark("2");
                    task.setViewtype("1");
                    json = JSONUtil.parse(task);
                }
            } else if(PersistenceManagerEvent.INSERT.equals(type)) {
                task.setRequestname(getWorkItemName(workItem));
                task.setIsremark("0");
                task.setViewtype("0");
                json = JSONUtil.parse(task);
            }
        }
        if(json != null) {
            String httpUrl = propertiesUtil.getProperty("synctaskurl");
            System.out.println("门户集成url = " + httpUrl);
            System.out.println("任务信息 = " + json);
//            String body = HttpRequest.post(httpUrl).body(json.toString(), "application/json").execute().body();
//            System.out.println("返回 = " + body);
        }
    }

    public static String getWorkItemName(WorkItem workItem) {
        boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
        WfAssignedActivity wa = (WfAssignedActivity) workItem.getSource().getObject();
        String label = "";
        try {
            WTReference ref = wa.getParentProcess().getBusinessObjectReference(new ReferenceFactory());
            String pboNumber = "";
            if(ref != null) {
                Object pbo = ref.getObject();
                if(pbo != null) {
                    if(pbo instanceof WTDocument) {
                        pboNumber = "_" + ((WTDocument) pbo).getNumber();
                    } else if(pbo instanceof WTChangeOrder2) {
                        pboNumber = "_" + ((WTChangeOrder2) pbo).getNumber();
                    } else if(pbo instanceof ProcessEnvelope) {
                        pboNumber = "_" + ((ProcessEnvelope) pbo).getNumber();
                    } else if(pbo instanceof ChangePackaged) {
                        pboNumber = "_" + ((ChangePackaged) pbo).getNumber();
                    }
                }
            }
            String templateName = "(" + wa.getParentProcess().getTemplate().getName() + pboNumber + ")";
            label = wa.getName() + "  " + templateName;
        } catch(Exception e){
            e.printStackTrace();
            label = wa.getName();
        }finally {
            wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
        }
        return label;
    }

    public static boolean createBpmQueue(String oid, String type) {
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            ProcessingQueue queue = getFreeQueue("SyncBpmWorkflowQueue");
            String targetClass = SyncTaskUtil.class.getName();
            String targetMethod = "sendTask";
            Class[] argClass = new Class[]{String.class,String.class};
            Object[] argObj = new Object[]{oid,type};
            WTPrincipal user = SessionHelper.manager.getPrincipal();
            queue.addEntry(user, targetMethod, targetClass, argClass, argObj);
        } catch (WTException e) {
            e.printStackTrace();
        } finally{
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return true;
    }

    private static ProcessingQueue getFreeQueue(String queueName) throws WTException {
        WtQueue queue = QueueHelper.manager.getQueue(queueName);
        if(queue==null){
            Manager manager = ManagerServiceFactory.getDefault().getManager(wt.queue.StandardQueueService.class);
            queue = ((wt.queue.StandardQueueService) manager).createQueue(queueName, true);
        }
        return (ProcessingQueue)queue;
    }

}