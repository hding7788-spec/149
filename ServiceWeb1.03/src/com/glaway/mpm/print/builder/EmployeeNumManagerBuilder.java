package com.glaway.mpm.print.builder;

import com.glaway.mpm.print.bean.CmUserQrCodeBean;
import com.glaway.mpm.print.util.PrintDataQueryUtil;
import com.glaway.mpm.util.UserUtil;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import wt.org.WTUser;
import wt.util.WTException;

import java.util.ArrayList;
import java.util.List;

@ComponentBuilder("com.glaway.mpm.print.builder.EmployeeNumManagerBuilder")
public class EmployeeNumManagerBuilder extends AbstractComponentBuilder{

	@Override
	public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1) throws Exception {
		NmCommandBean commandbean = ((JcaComponentParams)arg1).getHelperBean().getNmCommandBean();
        String userName = (String) commandbean.getText().get("userName");
		List<CmUserQrCodeBean> userList = null;
		if (userName != null) {
			userList = PrintDataQueryUtil.getCmUserByUserName(userName);
		}
		if (userList == null) {
			userList = new ArrayList<CmUserQrCodeBean>();
		}
		if (userList.size() == 0 && userName != null) {
			WTUser user = UserUtil.getUser(userName);
			if (user != null) {
				CmUserQrCodeBean cmUserQrCodeBean = new CmUserQrCodeBean();
				cmUserQrCodeBean.setUserName(user.getName());
				userList.add(cmUserQrCodeBean);
			}
		}
        return userList;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();
		table.setActionModel("custom_employeeNumManager_actions");
		table.setLabel("配置列表");
		table.setSelectable(false);

		ColumnConfig userName = factory.newColumnConfig("userName",true);
		userName.setLabel("用户名");
		userName.setWidth(100);
		userName.setRequired(true);
		userName.setDataUtilityId("RoleDataUtility");
		table.addComponent(userName);

		ColumnConfig employeeNo = factory.newColumnConfig("employeeNo",true);
		employeeNo.setLabel("工号");
		employeeNo.setWidth(100);
		employeeNo.setRequired(true);
		employeeNo.setDataUtilityId("RoleDataUtility");
		table.addComponent(employeeNo);

		return table;
	}

}
