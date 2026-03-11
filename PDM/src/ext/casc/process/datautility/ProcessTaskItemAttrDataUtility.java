package ext.casc.process.datautility;

import com.glaway.mpm.util.UserUtil;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.dataUtilities.AbstractAttributeDataUtility;
import com.ptc.core.components.rendering.guicomponents.AttributeGuiComponent;
import com.ptc.core.components.rendering.guicomponents.TextDisplayComponent;
import com.ptc.netmarkets.util.misc.NmActionServiceHelper;
import ext.casc.process.ProcessConstants;
import ext.casc.process.ProcessTaskItem;
import ext.casc.process.util.ProcessUtil;
import ext.casc.util.IBAUtility;
import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.httpgw.URLFactory;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.util.WTException;

import java.sql.Timestamp;
import java.util.HashMap;

public class ProcessTaskItemAttrDataUtility extends AbstractAttributeDataUtility {

    @Override
    public AttributeGuiComponent createSingleValueDisplayComponent(String componentId,
                                                                   Object datum, Object rawVal, ModelContext mc) throws WTException {
        if (datum instanceof ProcessTaskItem) {
            ProcessTaskItem taskItem = (ProcessTaskItem) datum;
            if ("taskItemName".equals(componentId)) {
                TextDisplayComponent guiComp = new TextDisplayComponent(componentId);
                String objURL = getURL(taskItem);
                String label = makeShowLabel(taskItem.getTaskItemName(), taskItem, objURL);
                guiComp.disableMoreLink();
                guiComp.setRichText(true);
                guiComp.setEnabled(true);
                guiComp.setReadOnly(true);
                guiComp.setValue(label);
                guiComp.setPlainTextValue("1");//因为是富文本，为了让程序认为没有换行，达到不显示更多链接的目的，写死一个没有换行的值
                return guiComp;
            } else if ("taskOwner".equals(componentId)) {
                TextDisplayComponent guiComp = new TextDisplayComponent(componentId);
                if (taskItem != null && taskItem.getZhurengongyishi() != null) {
                    WTUser wtUser = UserUtil.getUser(taskItem.getZhurengongyishi());
                    if (wtUser != null) {
                        guiComp.setValue(wtUser.getFullName());
                    }
                }
                return guiComp;
            } else if ("number".equals(componentId)) {
                String label = taskItem.getNumber();
                if (!"非工艺设计类工艺任务".equals(taskItem.getTaskType())) {
                    WTPart part = ProcessUtil.getWtPart(taskItem.getPartId());
                    if (part != null) {
                        part = ProcessUtil.getPartByNumber(part.getNumber(),
                                "Manufacturing");
                        String objURL = getURL(part);
                        label = "<a href='" + objURL + "'>" + part.getNumber()
                                + "</a>";
                    }
                }
                TextDisplayComponent guiComp = new TextDisplayComponent(
                        componentId);
                guiComp.disableMoreLink();
                guiComp.setRichText(true);
                guiComp.setEnabled(true);
                guiComp.setReadOnly(true);
                guiComp.setValue(label);
                guiComp.setPlainTextValue("1");//因为是富文本，为了让程序认为没有换行，达到不显示更多链接的目的，写死一个没有换行的值
                return guiComp;
            }
        }
        return null;
    }

    /**
     * <pre>
     * 功能描述: 根据任务时间显示不同的颜色,如果活动不是待办的活动的话，则不用高亮显示
     * 使用方法：
     * 修改记录:（修改时间、修改人、修改内容、修改原因）
     * </pre>
     *
     * @param label 显示数据
     * @param taskItem    活动
     * @return 显示的名称及活动
     * @author LongXiuChuan 2013-5-3
     */
    private String makeShowLabel(String label, ProcessTaskItem taskItem, String objURL) throws WTException {
        String taskItemState = taskItem.getTaskItemState();
        IBAUtility ibaUtility = new IBAUtility(taskItem);
        String cldePlanTime = ibaUtility.getIBAValue("cldePlanTime");
        String cldeEndTime = ibaUtility.getIBAValue("cldeEndTime");
        Timestamp cldePlan = null;
        if (cldePlanTime != null && !cldePlanTime.isEmpty()) {
            cldePlan = Timestamp.valueOf(cldePlanTime);

        }
        if (!ProcessConstants.TASKITEM_STATE_ZHENGZAIJINGXIN.equals(taskItemState)) {
            return "<a href='" + objURL + "' target=\"_blank\">" + label + "</a>";
        }
        if (isPassDays(taskItem.getCreateTimestamp(), 10)) {
            if (cldePlan != null && isBeforeDays(cldePlan, 2) && (cldeEndTime == null || cldeEndTime.isEmpty())) {
                return "<a href='" + objURL + "' target=\"_blank\"><font color=\"red\">" + label + "(材料定额任务即将逾期)" + "</font></a>";
            } else if (cldePlan != null && isPassDays(cldePlan, 0) && (cldeEndTime == null || cldeEndTime.isEmpty())) {
                return "<a href='" + objURL + "' target=\"_blank\"><font color=\"red\">" + label + "(材料定额任务已超期)" + "</font></a>";
            } else {
                return "<a href='" + objURL + "' target=\"_blank\"><font color=\"red\">" + label + "</font></a>";
            }
        } else if (isPassDays(taskItem.getCreateTimestamp(), 5)) {
            if (cldePlan != null && isBeforeDays(cldePlan, 2) && (cldeEndTime == null || cldeEndTime.isEmpty())) {
                return "<a href='" + objURL + "' target=\"_blank\"><font color='#FFCC00'>" + label + "(材料定额任务即将逾期)" + "</font></a>";
            } else if (cldePlan != null && isPassDays(cldePlan, 0) && (cldeEndTime == null || cldeEndTime.isEmpty())) {
                return "<a href='" + objURL + "' target=\"_blank\"><font color='#FFCC00'>" + label + "(材料定额任务已超期)" + "</font></a>";
            } else {
                return "<a href='" + objURL + "' target=\"_blank\"><font color='#FFCC00'>" + label + "</font></a>";
            }
        } else {
            if (cldePlan != null && isBeforeDays(cldePlan, 2) && (cldeEndTime == null || cldeEndTime.isEmpty())) {
                return "<a href='" + objURL + "' target=\"_blank\">" + label + "(材料定额任务即将逾期)" + "</a>";
            } else if (cldePlan != null && isPassDays(cldePlan, 0) && (cldeEndTime == null || cldeEndTime.isEmpty())) {
                return "<a href='" + objURL + "' target=\"_blank\">" + label + "(材料定额任务已超期)" + "</a>";
            } else {
                return "<a href='" + objURL + "' target=\"_blank\">" + label + "</a>";
            }
        }

    }

    /**
     * <pre>
     * 功能描述: 和当前时间相差的天数
     * 使用方法：
     * 修改记录:（修改时间、修改人、修改内容、修改原因）
     * </pre>
     *
     * @param startTime 开始时间
     * @param days      天数
     * @return 超过：true；没有超过:false;
     * @author LongXiuChuan 2013-5-3
     */
    private boolean isPassDays(Timestamp startTime, int days) {
        long day = 1 * 24 * 60 * 60 * 1000;
        long s = startTime.getTime();
        long e = System.currentTimeMillis();
        if ((e - s) > day * days) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * <pre>
     * 功能描述: 和当前时间相差的天数
     * 使用方法：
     * 修改记录:（修改时间、修改人、修改内容、修改原因）
     * </pre>
     *
     * @param startTime 开始时间
     * @param days      天数
     * @return 超过：true；没有超过:false;
     * @author LongXiuChuan 2013-5-3
     */
    private boolean isBeforeDays(Timestamp startTime, int days) {
        long day = 1 * 24 * 60 * 60 * 1000;
        long s = startTime.getTime();
        long e = System.currentTimeMillis();
        if ((s - e) < day * days) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取对象url
     *
     * @param obj
     * @return
     * @throws WTException
     * @create date: 2013-5-3
     * @methodName: getURL
     * @return: String
     */
    public static String getURL(Object obj) throws WTException {
        ReferenceFactory referencefactory = new ReferenceFactory();
        String s1 = referencefactory.getReferenceString((Persistable) obj);
        URLFactory urlfactory = new URLFactory();
        HashMap hashmap = new HashMap();
        hashmap.put("oid", s1);
        String s2 = NmActionServiceHelper.service.getAction("object", "view").getUrl();
        return urlfactory.getHREF(s2, hashmap, true);
    }
}
