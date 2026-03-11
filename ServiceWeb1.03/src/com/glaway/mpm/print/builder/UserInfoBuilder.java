package com.glaway.mpm.print.builder;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import wt.org.WTUser;
import wt.util.WTException;

import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import com.glaway.mpm.print.util.PrintDataQueryUtil;
import ext.casc.util.CommonUtil;

@ComponentBuilder("d800.print.builder.UserInfoBuilder")
public class UserInfoBuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
    	NmCommandBean cb = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
    	HttpServletRequest request = cb.getRequest();
    	request.setAttribute("flag", true);
        String userName = CommonUtil.objectToString(params.getParameter("userName"));
		String fullName = CommonUtil.objectToString(params.getParameter("userFullName"));
		if (userName.equals("") && fullName.equals("")) {
			return null;
		}
		List<WTUser> list = PrintDataQueryUtil.queryUser(userName, fullName);
        return list;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig tableConfig = factory.newTableConfig();
        tableConfig.setLabel("用户信息");
        tableConfig.setId("UserInfoBuilder");
        tableConfig.setActionModel("custom_printManage_qrCodeManage_toolbar_actions");
        tableConfig.setSelectable(true);

        ColumnConfig nameConfig = factory.newColumnConfig("name", true);
        nameConfig.setLabel("用户名");
        nameConfig.setWidth(150);
        tableConfig.addComponent(nameConfig);

        ColumnConfig fullNameConfig = factory.newColumnConfig("fullName", true);
        fullNameConfig.setLabel("全名");
        fullNameConfig.setWidth(150);
        fullNameConfig.setDataUtilityId("PrintDataUtility");
        tableConfig.addComponent(fullNameConfig);

        ColumnConfig deptConfig = factory.newColumnConfig("dept", true);
        deptConfig.setLabel("部门");
        deptConfig.setWidth(150);
        deptConfig.setDataUtilityId("PrintDataUtility");
        tableConfig.addComponent(deptConfig);

        ColumnConfig codeConfig = factory.newColumnConfig("userCode", true);
        codeConfig.setLabel("二维码");
        codeConfig.setAutoSize(true);
        codeConfig.setDataUtilityId("PrintDataUtility");
        tableConfig.addComponent(codeConfig);
        return tableConfig;
    }

}
