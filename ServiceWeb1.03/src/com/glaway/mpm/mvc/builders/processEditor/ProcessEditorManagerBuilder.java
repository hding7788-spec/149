package com.glaway.mpm.mvc.builders.processEditor;

import com.glaway.mpm.model.ProcessEditorBean;
import com.glaway.mpm.util.LoadProcessEditorProperties;
import com.glaway.mpm.util.ProcessEditorHander;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import wt.org.WTUser;
import wt.util.WTException;


public class ProcessEditorManagerBuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1) throws Exception {
        NmCommandBean commandbean = ((JcaComponentParams) arg1).getHelperBean().getNmCommandBean();
        String userName = (String) commandbean.getText().get("userName");
        ProcessEditorBean processEditorBean = null;
        if (userName != null) {
            processEditorBean = LoadProcessEditorProperties.getInstance().checkUser(userName);
            if (processEditorBean == null) {
                WTUser user = ProcessEditorHander.getUser(userName);
                if (user != null) {
                    processEditorBean = new ProcessEditorBean(user.getName(), "", "");
                }
            }
        }
        return processEditorBean;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig table = factory.newTableConfig();
        table.setActionModel("custom_ProcessEditorManager_actions");
        table.setLabel("配置列表");
        table.setSelectable(false);

        ColumnConfig userName = factory.newColumnConfig("userName", true);
        userName.setLabel("用户名");
        userName.setWidth(100);
        userName.setRequired(true);
        userName.setDataUtilityId("MpmplanReleaseDataUtility");
        table.addComponent(userName);

        ColumnConfig swt = factory.newColumnConfig("swt", true);
        swt.setLabel("位数");
        swt.setWidth(100);
        swt.setRequired(true);
        swt.setDataUtilityId("MpmplanReleaseDataUtility");
        table.addComponent(swt);

        ColumnConfig jwsRuntimeParameters = factory.newColumnConfig("jwsRuntimeParameters", true);
        jwsRuntimeParameters.setLabel("分配内存");
        jwsRuntimeParameters.setWidth(100);
        jwsRuntimeParameters.setRequired(true);
        jwsRuntimeParameters.setDataUtilityId("MpmplanReleaseDataUtility");
        table.addComponent(jwsRuntimeParameters);

        return table;
    }

}
