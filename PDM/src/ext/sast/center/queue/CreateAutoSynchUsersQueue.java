package ext.sast.center.queue;

import ext.sast.center.processor.RestQueueProcessor;
import ext.sast.center.util.PropertiesUtil;
import org.apache.log4j.Logger;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.log4j.LogR;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.scheduler.ScheduleItem;
import wt.scheduler.SchedulingHelper;
import wt.session.SessionHelper;
import wt.util.WTException;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @ Author     ：LB.
 * @ Date       ：Created in 2019/2/25
 * @ Description：人员组织信息同步  排程队列，每周六晚上23：00获取当前PDM系统的用户和组的信息，调用中心域的rest接口同步到院中心域系统。
 * @ Modified By：
 */
public class CreateAutoSynchUsersQueue implements RemoteAccess {

    private static final String CLASSNAME = CreateAutoSynchUsersQueue.class.getName();
    private static final Logger log = LogR.getLogger(CreateAutoSynchUsersQueue.class.getName());
    private static final String propertiesFilePath = "ext/sast/center/center.properties";

    //队列启动时间
    private static int SCHEDULE_QUEUE_START_HOUR ;            //时
    private static int SCHEDULE_QUEUE_START_MINUTE ;        //分
    private static int SCHEDULE_QUEUE_START_SECOND ;        //秒
    private static int SCHEDULE_QUEUE_START_NANO ;           //毫秒
    private static long SUSPEND_DURATION;                           //暂停时间/时间间隔

    private static String SCHEDULE_ITEM_NAME = "SynchUsers";
    private static String QUEUE_NAME = "AutoSynchUsersQueue";

    public static void main(String[] args) {
        try {
            RemoteMethodServer.getDefault().setUserName(args[0]);
            RemoteMethodServer.getDefault().setPassword(args[1]);
            addEntry();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加队列
     * @return
     * @throws RemoteException
     * @throws InvocationTargetException
     * @throws WTException
     */
    @SuppressWarnings({ "deprecation", "rawtypes" })
	public static Boolean addEntry() throws RemoteException,
            InvocationTargetException, WTException {
        if (!RemoteMethodServer.ServerFlag) {
            Class[] types = {};
            Object[] objs = {};
            return (Boolean) RemoteMethodServer.getDefault().invoke("addEntry",
                    CreateAutoSynchUsersQueue.class.getName(),
                    null, types, objs);
        }
        try {
            PropertiesUtil propertiesUtil = new PropertiesUtil("codebase"+ File.separator + propertiesFilePath);
            SCHEDULE_QUEUE_START_HOUR = Integer.parseInt(propertiesUtil.getProperty("queue_start_hour"));
            SCHEDULE_QUEUE_START_MINUTE = Integer.parseInt(propertiesUtil.getProperty("queue_start_minute"));
            SCHEDULE_QUEUE_START_SECOND = Integer.parseInt(propertiesUtil.getProperty("queue_start_second"));
            SCHEDULE_QUEUE_START_NANO = Integer.parseInt(propertiesUtil.getProperty("queue_start_nano"));
            SUSPEND_DURATION = Integer.parseInt(propertiesUtil.getProperty("queue_suspend_duration"));
            Date today = new Date();
            Timestamp timestamp = new Timestamp(today.getYear(), today.getMonth(),today.getDate(),
                    SCHEDULE_QUEUE_START_HOUR,
                    SCHEDULE_QUEUE_START_MINUTE,
                    SCHEDULE_QUEUE_START_SECOND,
                    SCHEDULE_QUEUE_START_NANO); //这四个参数依次为小时，分，秒，毫秒
            ScheduleItem scheduleitem = getScheduleItem();
            if (scheduleitem == null) {
                log.debug(CLASSNAME + "-->createScheduleQueue: 开始创建 院中心域：人员组织同步  队列!");
                scheduleitem = ScheduleItem.newScheduleItem();
                scheduleitem.setItemDescription("人员组织同步队列");
                scheduleitem.setQueueName(QUEUE_NAME);
                scheduleitem.setTargetClass(RestQueueProcessor.class.getName());
                scheduleitem.setTargetMethod("synchUserAndGroupInfo");
                scheduleitem.setToBeRun(-1l);
                scheduleitem.setStartDate(timestamp);
                //执行间隔1天1次 还是1小时1次
                scheduleitem.setPeriodicity(SUSPEND_DURATION);
                WTPrincipal administrator = SessionHelper.manager.getAdministrator();

                scheduleitem.setPrincipalRef(WTPrincipalReference.newWTPrincipalReference(administrator));
                scheduleitem.setItemName(SCHEDULE_ITEM_NAME);
                scheduleitem = SchedulingHelper.service.addItem(scheduleitem, null);
                log.debug(CLASSNAME + "-->createScheduleQueue: 结束创建  院中心域：人员组织同步  队列!");
            } else {
                log.debug(CLASSNAME + "-->createScheduleQueue: 队列： "+scheduleitem+" 已存在" );

                boolean needModify = false;
                if (scheduleitem.getStartDate().getHours() != SCHEDULE_QUEUE_START_HOUR
                        || scheduleitem.getStartDate().getMinutes() != SCHEDULE_QUEUE_START_MINUTE) {
                    scheduleitem.setStartDate(timestamp);
                    needModify = true;
                }

                if (scheduleitem.getItemDescription().equalsIgnoreCase("备份人员组织同步队列")) {
                    scheduleitem.setItemDescription("备份人员组织队列");
                    needModify = true;
                }

                if (scheduleitem.getPeriodicity() != SUSPEND_DURATION) {
                    scheduleitem.setPeriodicity(SUSPEND_DURATION);
                    needModify = true;
                }

                if (scheduleitem.getToBeRun() != -1l) {
                    scheduleitem.setToBeRun(-1l);
                    needModify = true;
                }

                if (needModify == true) {
                    SchedulingHelper.service.modifyItem(scheduleitem);
                }
            }
            log.debug(CLASSNAME + "-->AutoSynchUsersQueue: scheduleitem.getNextTime() is "
                    + scheduleitem.getNextTime());
            log.debug(CLASSNAME + "-->AutoSynchUsersQueue: 发布定时执行队列'" + scheduleitem.getDisplayIdentity()
                    + "'启动时间为'" + scheduleitem.getStartDate() + "'，间隔为'" + scheduleitem.getPeriodicity() + "'!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Boolean.FALSE;
    }

    /**f'f
     * 获取队列
     * @throws WTException
     *
     */
    @SuppressWarnings("deprecation")
	public static ScheduleItem getScheduleItem() throws WTException {
        ScheduleItem scheduleItem = null;
        QuerySpec queryspec = new QuerySpec(ScheduleItem.class);
        queryspec.appendWhere(new SearchCondition(ScheduleItem.class, ScheduleItem.ITEM_NAME, SearchCondition.EQUAL,
                SCHEDULE_ITEM_NAME));
        queryspec.appendAnd();
        queryspec.appendWhere(new SearchCondition(ScheduleItem.class, ScheduleItem.QUEUE_NAME, SearchCondition.EQUAL,
                QUEUE_NAME));
        queryspec.appendOrderBy(ScheduleItem.class, ScheduleItem.START_DATE, true);

        QueryResult queryResult = PersistenceHelper.manager.find(queryspec);
        while (queryResult.hasMoreElements()) {
            scheduleItem = (ScheduleItem) queryResult.nextElement();
        }

        return scheduleItem;
    }
}
