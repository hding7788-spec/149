package com.glaway.mpm.mvc.builders.print;

import java.util.ArrayList;
import java.util.List;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.workflow.work.WorkItem;

import com.glaway.mpm.intf.PrintToWCIntfRMI;
import com.glaway.mpm.print.PrintUserCodeProcessor;
import com.glaway.mpm.print.data.CmPrintRecordInfoBean;
import com.glaway.mpm.print.util.PrintUtil;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;

@ComponentBuilder("com.glaway.mpm.mvc.builders.print.ShowReceiveInfoBuilder")
public class ShowReceiveInfoBuilder extends AbstractComponentBuilder{

	@Override
	public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1) throws Exception {
		NmCommandBean commandBean = ((JcaComponentParams) arg1).getHelperBean().getNmCommandBean();
		WorkItem workItem = (WorkItem)commandBean.getPageOid().getRefObject();
		List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
		Persistable per = workItem.getPrimaryBusinessObject().getObject();
		String userName = SessionHelper.manager.getPrincipal().getName();
		String dept = PrintUtil.getUserByName(userName);
		String oid = String.valueOf(PersistenceHelper.getObjectIdentifier(per).getId());
		String str = workItem.getDisplayIdentifier().toString();
		if(str.contains("打印申请补打流程")){
			listBean = PrintUserCodeProcessor.queryOffSetInfo(oid,dept);
		}else{
			listBean = PrintToWCIntfRMI.queryReceiveInfo(oid , dept);
		}
		return listBean;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();
		table.setLabel("待领取文件信息");
		table.setSelectable(false);

		ColumnConfig fileNumber = factory.newColumnConfig("fileNumber",true);
		fileNumber.setLabel("文件编号");
		fileNumber.setWidth(100);
		fileNumber.setRequired(true);
		fileNumber.setDataUtilityId("ReleaseDataUtility");
		table.addComponent(fileNumber);

		ColumnConfig fileName = factory.newColumnConfig("fileName",true);
		fileName.setLabel("文件名称");
		fileName.setWidth(100);
		fileName.setRequired(true);
		fileName.setDataUtilityId("ReleaseDataUtility");
		table.addComponent(fileName);

		ColumnConfig docVersion = factory.newColumnConfig("docVersion",true);
		docVersion.setLabel("版本");
		docVersion.setWidth(100);
		docVersion.setRequired(true);
		docVersion.setDataUtilityId("ReleaseDataUtility");
		table.addComponent(docVersion);

		ColumnConfig phaseCode = factory.newColumnConfig("phaseCode",true);
		phaseCode.setLabel("阶段标记");
		phaseCode.setWidth(100);
		phaseCode.setRequired(true);
		phaseCode.setDataUtilityId("ReleaseDataUtility");
		table.addComponent(phaseCode);

		ColumnConfig secret = factory.newColumnConfig("secret",true);
		secret.setLabel("密级");
		secret.setWidth(100);
		secret.setRequired(true);
		secret.setDataUtilityId("ReleaseDataUtility");
		table.addComponent(secret);

		ColumnConfig disMessage = factory.newColumnConfig("disMessage",true);
		disMessage.setLabel("份数");
		disMessage.setWidth(100);
		disMessage.setRequired(true);
		disMessage.setDataUtilityId("ReleaseDataUtility");
		table.addComponent(disMessage);

		return table;
	}
}

