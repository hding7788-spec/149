package com.ptc.windchill.enterprise.object.mvc.builders;

import wt.util.InstalledProperties;
import wt.util.WTException;

import com.ptc.core.components.util.RequestHelper;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentConfigBuilder;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentBuilderType;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.MultiComponentConfig;
import com.ptc.mvc.util.ClientMessage;

import ext.casc.constants.Constants;

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
@ComponentBuilder(value={"home.assignments"}, type=ComponentBuilderType.CONFIG_ONLY)
public class HomeAssignmentsComponentBuilder extends AbstractComponentConfigBuilder
{
  public ComponentConfig buildComponentConfig(ComponentParams paramComponentParams)
    throws WTException
  {
    ClientMessage localClientMessage = new ClientMessage("com.ptc.core.ui.navigationRB", "WIN_TITLE_HOME_TAB_HOME");
    RequestHelper.setBrowserWinTitle(((JcaComponentParams)paramComponentParams).getHelperBean().getRequest(), localClientMessage, false);

    MultiComponentConfig localMultiComponentConfig = new MultiComponentConfig();
    if (InstalledProperties.isInstalled("Windchill.ProjectLink")) {
      localMultiComponentConfig.addNestedComponent("projectmanagement.overview.assignments.list");
    } else {
      localMultiComponentConfig.addNestedComponent(Constants.CONST_EXT_PLAN_HOME_OVERVIEW_WORKLIST_TABLE_ID);
      //localMultiComponentConfig.addNestedComponent("netmarkets.overview.assignments.list");
    }
    paramComponentParams.setAttribute("overview", "true");
    paramComponentParams.setAttribute("tablewidth", "100");
    localMultiComponentConfig.setView("/object/homeAssignments.jsp");

    return localMultiComponentConfig;
  }
}