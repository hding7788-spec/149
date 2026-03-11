package ext.casc.work.mvc.builders;

import cn.hutool.core.util.StrUtil;
import com.ptc.core.htmlcomp.components.AbstractConfigurableTableBuilder;
import com.ptc.core.htmlcomp.tableview.ConfigurableTable;
import com.ptc.core.htmlcomp.tableview.TableViewDescriptor;
import com.ptc.core.htmlcomp.tableview.TableViewDescriptorHelper;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.mvc.util.ClientMessageSource;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.beans.NmHelperBean;
import com.ptc.windchill.enterprise.object.mvc.HomeOverviewHelper;
import com.ptc.windchill.enterprise.work.assignmentslist.server.AssignmentsListHelper;
import ext.casc.access.AccessAdminUtil;
import ext.casc.constants.Constants;
import ext.casc.util.IBAHelper;
import ext.casc.work.assignmentslist.views.CustHomeOverviewAssignmentTableViews;
import org.apache.log4j.Logger;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.log4j.LogR;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

import java.util.*;


/**
 * <pre>
 * 功能描述：
 * 1,我的任务"开启的"不显示"驳回重新指派工艺组长"
 * 2,在视图中增加"驳回重新指派工艺组长"用于显示"驳回重新指派工艺组长"任务活动对象
 * 3,任务活动名称通过颜色标示超期活动任务，超过5天显示为黄色，超过10天显示为红色
 * 使用方法：
 * 修改记录:（修改时间、修改人、修改内容、修改原因）
 * </pre>
 *
 * @author LongXiuChuan 2013-5-3
 * @since 1.0
 */
@ComponentBuilder(Constants.CONST_EXT_PLAN_HOME_OVERVIEW_WORKLIST_TABLE_ID)
public class OverviewAssignmentsBuilder extends AbstractConfigurableTableBuilder {
    ClientMessageSource messageActionItemSource;
    ClientMessageSource messageWorkSource;
    private static final Logger log = LogR.getLogger(OverviewAssignmentsBuilder.class.getName());

    public OverviewAssignmentsBuilder() {
        this.messageActionItemSource = getMessageSource("com.ptc.netmarkets.actionitem.actionitemResource");

        this.messageWorkSource = getMessageSource("com.ptc.netmarkets.work.workResource");
    }

    public ConfigurableTable buildConfigurableTable(String paramString)
            throws WTException {
        return new CustHomeOverviewAssignmentTableViews();
    }

    public Object buildComponentData(ComponentConfig paramComponentConfig, ComponentParams paramComponentParams) throws WTException {
        boolean accessFlag = SessionServerHelper.manager
                .setAccessEnforced(false);

        NmHelperBean localNmHelperBean = ((JcaComponentParams) paramComponentParams).getHelperBean();
        NmCommandBean localNmCommandBean = localNmHelperBean.getNmCommandBean();
        localNmCommandBean.getRequest().getSession(true).putValue("tk", "orv");

        TableViewDescriptor currTableViewDescriptor = TableViewDescriptorHelper.getCurrentActiveView(
                Constants.CONST_EXT_PLAN_HOME_OVERVIEW_WORKLIST_TABLE_ID, SessionHelper.getLocale());
        String select = currTableViewDescriptor.getName();

        if ("指派工艺组长_已委派".equals(select)) {
            List result = ext.casc.work.AssignmentsListHelper.listRessignAssignments();
            return result;
        }

        QueryResult qResult = AssignmentsListHelper.service.listAssignments(localNmHelperBean.getNmCommandBean());

        //如果选择"全部",则返回所有的活动
        if (Constants.SELECT_NAME_ALL.equals(select)) {
            List result = (List) ext.casc.work.AssignmentsListHelper.listAll(qResult);
            return result;
        }
        if (Constants.SHOWVIEW805.equals(select)) {
            List result = (List) ext.casc.work.AssignmentsListHelper.listSendFrom805Assignments(qResult);
            return result;
        }
        if (Constants.SHOWVIEWNO8.equals(select)) {
            List result = (List) ext.casc.work.AssignmentsListHelper.listSendFromNO8Assignments(qResult);
            return result;
        }
        if (Constants.SHOWVIEWGONGYI.equals(select)) {
            List result = (List) ext.casc.work.AssignmentsListHelper.listGongYiAssignments(qResult);
            return result;
        }
        if (Constants.SHOWVIEWZIYAN.equals(select)) {
            List result = (List) ext.casc.work.AssignmentsListHelper.listZiYanAssignments(qResult);
            return result;
        }
        //add by libo 2017.02.06 begin
        //仅显示最新活动
        if (Constants.WORKITEM_NAME_JXSZXHD.equals(select)) {
            List result = (List) ext.casc.work.AssignmentsListHelper.listJXSZXHDAssignments(qResult);
            return result;
        }
        //本人发起的
        if (Constants.WORKITEM_NAME_BRFQ.equals(select)) {
            List result = (List) ext.casc.work.AssignmentsListHelper.listBRFQAssignments(qResult);
            return  result;
        }
        //本人发起未完成
        if (Constants.WORKITEM_NAME_BRFQWWC.equals(select)) {
            List result = (List) ext.casc.work.AssignmentsListHelper.listBRFQWWCAssignments(qResult);
            return result;
        }
        //工时定额
        if (Constants.WORKITEM_NAME_GSDE.equals(select)) {
            List result = (List) ext.casc.work.AssignmentsListHelper.listGSDEAssignments(qResult);
            return result;
        }
        //已隐藏
        if (Constants.WORKITEM_NAME_YIYINCANG.equals(select)) {
            List result = (List) ext.casc.work.AssignmentsListHelper.listYYCAssignments(qResult);
            return result;
        }
        //add by libo 2017.02.06 end
        //过滤"驳回重新指派工艺组长"活动
        List allList = new ArrayList();
        List allList2 = new ArrayList();
        while (qResult.hasMoreElements()) {
            Object object = qResult.nextElement();
            if (object instanceof WorkItem) {
                WorkItem workItem = (WorkItem) object;
                //过滤已隐藏流程任务 add by cjh 20231228
                if(Constants.WORKITEM_NAME_YIYINCANG.equals(workItem.getEventSet().toString())){
                    continue;
                }
                //保密检查改造：过滤高密于用户的数据条目 20251021
                try {
                    Persistable obj = workItem.getPrimaryBusinessObject().getObject();
                    String secret = IBAHelper.getIBAStringValue((WTObject) obj, "SECRET");
                    if(StrUtil.isNotEmpty(secret)) {
                        if(secret.indexOf("机密") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")) {
                                continue;
                            }
                        } else if(secret.indexOf("秘密") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")
                                    && !AccessAdminUtil.isGroup("一般用户密级组")) {
                                continue;
                            }
                        } else if(secret.indexOf("内部") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")
                                    && !AccessAdminUtil.isGroup("一般用户密级组")
                                    && !AccessAdminUtil.isGroup("内部用户密级组")) {
                                continue;
                            }
                        }
                    }
                } catch(Exception e) {
                    //e.printStackTrace();
                }
                WfAssignedActivity wa = (WfAssignedActivity) workItem.getSource().getObject();
                WfProcess process = wa.getParentProcess();
                String processName = process.getName();
                String taskName = wa.getName();
                //如果选择"驳回重新指派工艺组长",则只显示"驳回重新指派工艺组长"任务活动
                if (Constants.WORKITEM_NAME_BHCXZPGYZZ.equals(select) && Constants.WORKITEM_NAME_BHCXZPGYZZ.equals(taskName)) {
                    allList2.add(workItem);
                } else if (Constants.WORKITEM_NAME_SELECTED_TZZRGYS.equals(select)
                        && (Constants.WORKITEM_NAME_SELECTED_TZZRGYS.equals(taskName))) {//通知主任工艺师
                    allList2.add(workItem);
                } else if (Constants.WORKITEM_NAME_KAIQI_BUBAOHANTONGZHI.equals(select)) {
                	if (!Constants.WORKITEM_NAME_TONGZHI.equals(taskName)) {
                		allList.add(workItem);
                	}
                }  else if (Constants.WORKITEM_NAME_TONGZHI_GONGYIQIANSHEN.equals(select)) {
                	if(processName.contains(Constants.WFN_WUJIPROCESSWF)
                			|| processName.contains(Constants.WFN_PROCESS_ECN)
                			|| processName.contains(Constants.WFN_SANJIPROCESSWF)
                			|| processName.contains(Constants.WFN_SANJIGENGGAIWF)) {
                		if (Constants.WORKITEM_NAME_TONGZHI.equals(taskName)) {
                    		allList.add(workItem);
                    	}
                	}
                }  else if (Constants.WORKITEM_NAME_TONGZHI_CAILIAODINGEQIANSHEN.equals(select)) {
                	if(processName.contains(Constants.WFN_PROCESS_CAILIAODINGE)) {
                		if (Constants.WORKITEM_NAME_TONGZHI.equals(taskName)) {
                    		allList.add(workItem);
                    	}
                	}
                } else {//如果选择其他的选项,则过滤不显示"驳回重新指派工艺组长"任务活动
                    if (!Constants.WORKITEM_NAME_BHCXZPGYZZ.equals(taskName)
                            && !Constants.WORKITEM_NAME_SELECTED_TZZRGYS.equals(taskName)
                            ) {
                        allList.add(workItem);
                    }
                }
            } else {
                allList.add(object);
            }
        }
        SessionServerHelper.manager.setAccessEnforced(accessFlag);
        if (Constants.WORKITEM_NAME_BHCXZPGYZZ.equals(select) || Constants.WORKITEM_NAME_SELECTED_TZZRGYS.equals(select)) {
            return allList2;
        } else {
            return allList;
        }
    }

    public ComponentConfig buildComponentConfig(ComponentParams paramComponentParams)
            throws WTException {
        ComponentConfigFactory localComponentConfigFactory = getComponentConfigFactory();
        TableConfig localTableConfig = localComponentConfigFactory.newTableConfig();
        NmHelperBean localNmHelperBean = ((JcaComponentParams) paramComponentParams).getHelperBean();

        HashMap localHashMap = null;
        if (localNmHelperBean.getNmCommandBean().getText() != null) {
            localHashMap = localNmHelperBean.getNmCommandBean().getText();
        } else {
            localHashMap = new HashMap();
            localNmHelperBean.getNmCommandBean().setText(localHashMap);
        }

        localHashMap.put("TABLE_ID", Constants.CONST_EXT_PLAN_HOME_OVERVIEW_WORKLIST_TABLE_ID);
//    localHashMap.put("TABLE_ID", "netmarkets.overview.assignments.list");

        localHashMap.put("HOME_OVERVIEW_WORKLIST", "HOME_OVERVIEW_WORKLIST");
        localNmHelperBean.getNmSessionBean().getStorage().put("tk", "orv");

        HomeOverviewHelper localHomeOverviewHelper = new HomeOverviewHelper(paramComponentParams);
        boolean bool = localHomeOverviewHelper.isSortable();
        int i = localHomeOverviewHelper.getPageLimit();
        if (localHomeOverviewHelper.isOverView()) {
            localHashMap.put("overview", "true");
            localHashMap.put("maxrows", (String) paramComponentParams.getAttribute("maxRows"));
        }
        localTableConfig.setLabel("流程任务");
        localTableConfig.setActionModel("home_assignments_actions_toolbar");
        localTableConfig.setSelectable(true);

        localTableConfig.addComponent(localComponentConfigFactory.newColumnConfig("formatIcon", false));

        localTableConfig.addComponent(localComponentConfigFactory.newColumnConfig("type_icon", true));

        localTableConfig.addComponent(localComponentConfigFactory.newColumnConfig("ASSIGNMENT_REASSIGNED", true));

        ColumnConfig assignmentName = localComponentConfigFactory.newColumnConfig("ASSIGNMENT_NAME", this.messageWorkSource.getMessage("102"), true);
        //localTableConfig.addComponent(localComponentConfigFactory.newColumnConfig("ASSIGNMENT_NAME", this.messageWorkSource.getMessage("102"), true));
        assignmentName.setDataUtilityId("assignmentUtility");
        localTableConfig.addComponent(assignmentName);

        ColumnConfig localColumnConfig = localComponentConfigFactory.newColumnConfig("infoPageAction", true);
        localColumnConfig.setDataUtilityId("ASSIGNMENT_INFO");
        localTableConfig.addComponent(localColumnConfig);

        localTableConfig.addComponent(localComponentConfigFactory.newColumnConfig("ASSIGNMENT_ROWACTIONS", this.messageWorkSource.getMessage("101"), true));

        localTableConfig.addComponent(localComponentConfigFactory.newColumnConfig("ASSIGNMENT_SUBJECT", this.messageWorkSource.getMessage("43"), true));

        ColumnConfig BIANZHIZHE = localComponentConfigFactory.newColumnConfig("BIANZHIZHE", "编制者", true);
        BIANZHIZHE.setDataUtilityId("assignmentUtility");
        localTableConfig.addComponent(BIANZHIZHE);


        localTableConfig.addComponent(localComponentConfigFactory.newColumnConfig("ASSIGNMENT_SUBJECT_LC_STATE", this.messageWorkSource.getMessage("45"), true));

        localTableConfig.addComponent(localComponentConfigFactory.newColumnConfig("ASSIGNMENT_PERCENT_DONE", this.messageWorkSource.getMessage("49"), true));

        localTableConfig.addComponent(localComponentConfigFactory.newColumnConfig("ASSIGNMENT_STATUS", this.messageWorkSource.getMessage("48"), true));

        localTableConfig.addComponent(localComponentConfigFactory.newColumnConfig("ASSIGNMENT_CREATED", this.messageWorkSource.getMessage("99"), true));

        localTableConfig.addComponent(localComponentConfigFactory.newColumnConfig("ASSIGNMENT_CONTAINER", this.messageWorkSource.getMessage("100"), true));

        localTableConfig.addComponent(localComponentConfigFactory.newColumnConfig("ASSIGNMENT_OWNER", this.messageWorkSource.getMessage("42"), true));

        localTableConfig.addComponent(localComponentConfigFactory.newColumnConfig("ASSIGNMENT_ROLE", this.messageWorkSource.getMessage("42a"), true));

        localTableConfig.addComponent(localComponentConfigFactory.newColumnConfig("ASSIGNMENT_CREATED_BY", this.messageActionItemSource.getMessage("24"), true));

        localColumnConfig = localComponentConfigFactory.newColumnConfig("ASSIGNMENT_IS_OVERDUE", this.messageWorkSource.getMessage("133"), false);

        localColumnConfig.setHidden(true);
        localTableConfig.addComponent(localColumnConfig);

        localColumnConfig = localComponentConfigFactory.newColumnConfig("OPEN_ASSIGNMENTS", this.messageWorkSource.getMessage("135"), false);

        localColumnConfig.setHidden(true);
        localTableConfig.addComponent(localColumnConfig);

        localColumnConfig = localComponentConfigFactory.newColumnConfig("WORKITEM_TYPE", this.messageWorkSource.getMessage("138"), false);

        localColumnConfig.setHidden(true);
        localTableConfig.addComponent(localColumnConfig);

        localColumnConfig = localComponentConfigFactory.newColumnConfig("ASSIGNMENT_IS_CREATED_BY_ME", this.messageWorkSource.getMessage("136"), false);

        localColumnConfig.setHidden(true);
        localTableConfig.addComponent(localColumnConfig);

        localColumnConfig = localComponentConfigFactory.newColumnConfig("DISCUSSED", this.messageWorkSource.getMessage("137"), false);

        localColumnConfig.setHidden(true);
        localTableConfig.addComponent(localColumnConfig);

        localColumnConfig = localComponentConfigFactory.newColumnConfig("description", true);
        localColumnConfig.setNeed("description,itemDescription");
        localTableConfig.addComponent(localColumnConfig);

        localColumnConfig = localComponentConfigFactory.newColumnConfig("workItemStatus", WTMessage.getLocalizedMessage("wt.workflow.worklist.worklistResource", "84"), true);
        localTableConfig.addComponent(localColumnConfig);

        localTableConfig.setHelpContext("Overview_Assignments_Help");

        if (log.isDebugEnabled()) {
            log.debug("Configured tableConfig : " + localTableConfig);
        }

        return localTableConfig;
    }

    /**
     * 根据时间过滤，超过半年的不显示 add by liangbo 20170325
     *
     * @param workItems
     * @return
     */
    public static List<WorkItem> filterWorkItemByTime(List<WorkItem> workItems) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        Date end = c.getTime();
        c.add(Calendar.MONTH, -6);
        Date start = c.getTime();
        Calendar from = Calendar.getInstance();
        from.setTime(start);
        Calendar to = Calendar.getInstance();
        to.setTime(end);
        List<WorkItem> list = new ArrayList<WorkItem>();
        for (WorkItem workItem : workItems) {
            WfAssignedActivity wa = (WfAssignedActivity) workItem.getSource().getObject();
            String state = wa.getState().toString();
            Date date = workItem.getModifyTimestamp();
            Calendar time = Calendar.getInstance();
            time.setTime(date);
            if (time.after(from) && time.before(to) || "OPEN_RUNNING".equals(state)) {
                list.add(workItem);
            }
        }
        return list;
    }
}