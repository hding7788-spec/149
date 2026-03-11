package ext.casc.common.mvc.builder;

import com.ptc.core.components.descriptor.DescriptorConstants;
import com.ptc.mvc.components.*;
import ext.casc.system.SystemConfigurationBean;
import ext.sast.common.fc.CmPersistenceHelper;
import ext.sast.common.fc.CmQueryResult;
import ext.sast.common.fc.CmQuerySpec;
import wt.util.WTException;

import java.util.ArrayList;
import java.util.List;

@ComponentBuilder("ext.casc.common.mvc.builder.SystemConfigurationBuilder")
public class SystemConfigurationBuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1) throws Exception {
        CmQuerySpec qs = new CmQuerySpec(SystemConfigurationBean.class);
        CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
        List<SystemConfigurationBean> list = new ArrayList<>();
        while(qr.hasNext()) {
            list.add((SystemConfigurationBean) qr.next());
        }
        return list;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig table = factory.newTableConfig();
        table.setActionModel("custom_SystemConfiguration_actions");
        table.setLabel("配置列表");
        table.setSelectable(true);
        table.setId("ext.casc.common.mvc.builder.SystemConfigurationBuilder");

        ColumnConfig key = factory.newColumnConfig("key", false);
        key.setLabel("配置名称");
        key.setWidth(300);
        table.addComponent(key);

        ColumnConfig value = factory.newColumnConfig("value", false);
        value.setLabel("配置值");
        value.setWidth(300);
        table.addComponent(value);

        ColumnConfig remark = factory.newColumnConfig("remark", false);
        remark.setLabel("备注");
        remark.setWidth(600);
        table.addComponent(remark);

        return table;
    }

}
