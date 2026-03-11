package ext.casc.process.mvc.builder;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import wt.util.WTException;

import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;

import ext.casc.process.ProcessTaskItem;
import ext.casc.util.WCUtil;

@ComponentBuilder("ext.casc.process.mvc.builder.ZhiPaiGongYiYuanBuilder")
public class ZhiPaiGongYiYuanBuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
        NmCommandBean commandBean = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
        List<Object> list = commandBean.getSelectedOidForPopup();
        if (list == null || list.isEmpty()) {
            NmOid nmOid = commandBean.getActionOid();
            list.add(nmOid);
        }
        System.out.println("--------list5:"+list);
        List<String> oidList = new ArrayList<String>();
        List<Object> allItems = new ArrayList<Object>();
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
                oidList.add(WCUtil.getOid(taskItem));
                allItems.add(taskItem);
            }
        }
        HttpSession session = commandBean.getRequest().getSession();
        session.setAttribute("oidList", oidList);
        return allItems;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig tableConfig = factory.newTableConfig();
        tableConfig.setSelectable(true);
        tableConfig.setConfigurable(false);
        tableConfig.setId("zhipaigongyiyuan_table");

        tableConfig.setLabel("指派工艺员");

        ColumnConfig icon = factory.newColumnConfig("type_icon", false);
        tableConfig.addComponent(icon);

        ColumnConfig numberColumnConfig = factory.newColumnConfig("number", true);
        numberColumnConfig.setAutoSize(true);
        numberColumnConfig.setInfoPageLink(false);
        tableConfig.addComponent(numberColumnConfig);

        ColumnConfig nameColumnConfig = factory.newColumnConfig("name", true);
        nameColumnConfig.setAutoSize(true);
        tableConfig.addComponent(nameColumnConfig);

        ColumnConfig versionColumnConfig = factory.newColumnConfig("version", false);
        versionColumnConfig.setAutoSize(true);
        tableConfig.addComponent(versionColumnConfig);

        ColumnConfig renwuleixing = factory.newColumnConfig("taskType", false);
        renwuleixing.setLabel("任务类型");
        renwuleixing.setId("taskType");
        renwuleixing.setDataUtilityId("");
        tableConfig.addComponent(renwuleixing);

        ColumnConfig endDate = factory.newColumnConfig("endDate", false);
        endDate.setLabel("计划完成时间");
        endDate.setId("endDate");
        endDate.setDataUtilityId("ProcessTaskItemDataUtility");
        tableConfig.addComponent(endDate);

        ColumnConfig iszhuzhi = factory.newColumnConfig("iszhuzhi", false);
        iszhuzhi.setLabel("是否主制");
        iszhuzhi.setId("iszhuzhi");
        iszhuzhi.setDataUtilityId("ProcessTaskItemDataUtility");
        tableConfig.addComponent(iszhuzhi);

        ColumnConfig renwuyaoqiu = factory.newColumnConfig("renwuyaoqiu", false);
        renwuyaoqiu.setLabel("任务要求");
        renwuyaoqiu.setId("renwuyaoqiu");
        renwuyaoqiu.setDataUtilityId("");
        tableConfig.addComponent(renwuyaoqiu);

        ColumnConfig columnConfig = factory.newColumnConfig("gongyiyuan",false);
        columnConfig.setLabel("工艺员");
        columnConfig.setId("gongyiyuan");
        columnConfig.setDataUtilityId("ProcessTaskItemDataUtility");
        columnConfig.setWidth(150);
        tableConfig.addComponent(columnConfig);

        ColumnConfig columnConfig4 = factory.newColumnConfig("description",false);
        columnConfig4.setLabel("备注");
        columnConfig4.setId("description");
        columnConfig4.setDataUtilityId("ProcessTaskItemDataUtility");
        tableConfig.addComponent(columnConfig4);

        return tableConfig;
    }

}
