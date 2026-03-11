package ext.casc.process.mvc.builder;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import wt.enterprise.Master;
import wt.part.WTPart;
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

import ext.casc.process.ProcessConstants;
import ext.casc.util.WCUtil;

@ComponentBuilder("ext.casc.process.mvc.builder.ProcessAssignReportBuilder")
public class ProcessAssignReportBuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
        NmCommandBean cb = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
        List<WTPart> list = new ArrayList<WTPart>();
        List<String> oidList = new ArrayList<String>();
        NmOid nmOid = cb.getActionOid();
        Object object = nmOid.getRefObject();
        if (object instanceof WTPart) {
            WTPart part = (WTPart)object;
            part = WCUtil.getLatestPartByView((Master)part.getMaster(), "Manufacturing");
            list.add(part);
        }
        HttpSession session = cb.getRequest().getSession();
        session.setAttribute("oidList", oidList);
        session.setAttribute("TaskType", ProcessConstants.TASK_TYPE_GONGYISHEJI);
        return list;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig tableConfig = factory.newTableConfig();
        tableConfig.setLabel("查询结果列表");
        
        ColumnConfig columnConfig1 = factory.newColumnConfig("taskType", false);
        columnConfig1.setLabel("任务类型");
        tableConfig.addComponent(columnConfig1);
        
        ColumnConfig columnConfig2 = factory.newColumnConfig("taskState", false);
        columnConfig2.setLabel("任务状态");
        tableConfig.addComponent(columnConfig2);
        
        ColumnConfig columnConfig3 = factory.newColumnConfig("taskComments", false);
        columnConfig3.setLabel("任务要去");
        tableConfig.addComponent(columnConfig3);
        
        ColumnConfig columnConfig4 = factory.newColumnConfig("partNumber", false);
        columnConfig4.setLabel("自制件编号");
        tableConfig.addComponent(columnConfig4);
        
        ColumnConfig columnConfig5 = factory.newColumnConfig("partName", false);
        columnConfig5.setLabel("自制件名称");
        tableConfig.addComponent(columnConfig5);
        
        ColumnConfig columnConfig6 = factory.newColumnConfig("chejian", false);
        columnConfig6.setLabel("车间");
        tableConfig.addComponent(columnConfig6);
        
        ColumnConfig columnConfig7 = factory.newColumnConfig("zhuorfuchejian", false);
        columnConfig7.setLabel("承担类型");
        tableConfig.addComponent(columnConfig7);
        
        ColumnConfig columnConfig8 = factory.newColumnConfig("gongyiyuan", false);
        columnConfig8.setLabel("工艺员");
        tableConfig.addComponent(columnConfig8);
        
        ColumnConfig columnConfig9 = factory.newColumnConfig("jihuadate", false);
        columnConfig9.setLabel("计划完成时间");
        tableConfig.addComponent(columnConfig9);
        
        ColumnConfig columnConfig10 = factory.newColumnConfig("shijidate", false);
        columnConfig10.setLabel("实际完成时间");
        tableConfig.addComponent(columnConfig10);
        
        return tableConfig;
    }

}
