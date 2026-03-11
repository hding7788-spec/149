package ext.casc.process.mvc.builder;

import java.util.ArrayList;
import java.util.List;

import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.fc.ObjectVector;
import wt.fc.QueryResult;
import wt.inf.container.WTContainerRef;
import wt.org.WTUser;
import wt.preference.PreferenceHelper;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.MultiComponentConfig;
import com.ptc.mvc.components.TableConfig;
import com.ptc.mvc.util.ClientMessageSource;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.attachments.server.AttachmentsHelper;

import ext.casc.process.ProcessTask;
import ext.casc.process.ProcessTaskItem;
import ext.casc.process.util.ProcessUtil;

@ComponentBuilder("ext.casc.process.mvc.builder.ListAttachmentsTableBuilder")
public class ListAttachmentsTableBuilder extends AbstractComponentBuilder {

	private final ClientMessageSource messageSource = getMessageSource("com.ptc.windchill.enterprise.attachments.attachmentsResource");

	public Object buildComponentData(ComponentConfig paramComponentConfig,
			ComponentParams paramComponentParams) throws Exception {
		ContentRoleType localContentRoleType = ContentRoleType.SECONDARY;
		NmCommandBean cb = (NmCommandBean) paramComponentParams.getAttribute("commandBean");
		NmOid nmOid = cb.getPageOid();
		Object object = nmOid.getRefObject();
		ProcessTask processTask = null;
        if (object instanceof ProcessTaskItem) {
        	ProcessTaskItem taskItem = (ProcessTaskItem)object;
        	processTask = ProcessUtil.getProcessTask(taskItem.getProcessTaskId());
        } else if (object instanceof ProcessTask) {
            processTask = (ProcessTask)object;

        }

        if(processTask == null) {
        	return null;
        }

		QueryResult qr = AttachmentsHelper.service.getAttachments(processTask, localContentRoleType);
		ObjectVector ov = new ObjectVector();
		while(qr.hasMoreElements()) {
			Object obj = qr.nextElement();
			if(obj instanceof ApplicationData) {
				ApplicationData data = (ApplicationData)obj;
				String name = data.getFileName();
				if(!name.endsWith(".xml")) {
					ov.addElement(data);
				}
			}
		}
		qr = new QueryResult(ov);
		return qr;
	}

	protected String getView(ComponentParams paramComponentParams) {
		return "/object/contents.jsp";
	}

	public ComponentConfig buildComponentConfig(ComponentParams paramComponentParams) throws WTException {
		paramComponentParams.setAttribute("showContextInfo", "true");

		ComponentConfigFactory localComponentConfigFactory = getComponentConfigFactory();
		MultiComponentConfig localMultiComponentConfig = new MultiComponentConfig();

		NmCommandBean localNmCommandBean = (NmCommandBean) paramComponentParams.getAttribute("commandBean");

		ContentRoleType localContentRoleType2 = ContentRoleType.SECONDARY;
		List localList2 = getColumns(localNmCommandBean, localContentRoleType2);

		TableConfig localTableConfig = localComponentConfigFactory.newTableConfig();
		localTableConfig.setLabel(this.messageSource.getMessage("ATTACHMENT_LABEL"));
		localTableConfig.setId("attachments.table.secondary");
		localTableConfig.setHelpContext("SecondaryAttachmentsTableHelp");

		localTableConfig.setType("wt.content.ContentItem");
		localTableConfig.setConfigurable(false);
		localTableConfig.setActionModel("workflow_pbo_attach_table_actions");
		localTableConfig.setSelectable(true);

//		ColumnConfig nmActionsCol = localComponentConfigFactory.newColumnConfig(ColumnIdentifiers.NM_ACTIONS, false);
//        ((JcaColumnConfig) nmActionsCol).setActionModel("workflow_pbo_attach_table_actions");
//        localTableConfig.addComponent(nmActionsCol);

		Boolean localBoolean = (Boolean) PreferenceHelper.service
				.getValue(
						"/com/ptc/windchill/enterprise/attachments/optionalAttributes/sortNumber",
						"WINDCHILL");

		if ((localBoolean.booleanValue()) && (!localContentRoleType2.equals(ContentRoleType.PRIMARY))) {
			localTableConfig.addComponent(localComponentConfigFactory.newColumnConfig("lineNumber", true));
		}

		localTableConfig.addComponents(localList2);

		localMultiComponentConfig.addComponent(localTableConfig);

		localMultiComponentConfig.setView(getView(paramComponentParams));
		return localMultiComponentConfig;
	}

	public List<ColumnConfig> getColumns(NmCommandBean paramNmCommandBean,
			ContentRoleType paramContentRoleType) throws WTException {
		ComponentConfigFactory localComponentConfigFactory = getComponentConfigFactory();

		Boolean localBoolean1 = (Boolean) PreferenceHelper.service
				.getValue(
						"/com/ptc/windchill/enterprise/attachments/optionalAttributes/comments",
						"WINDCHILL");

		Boolean localBoolean2 = (Boolean) PreferenceHelper.service
				.getValue(
						"/com/ptc/windchill/enterprise/attachments/optionalAttributes/distributable",
						"WINDCHILL");

		Boolean localBoolean3 = (Boolean) PreferenceHelper.service
				.getValue(
						"/com/ptc/windchill/enterprise/attachments/optionalAttributes/authoredBy",
						"WINDCHILL");

		Boolean localBoolean4 = (Boolean) PreferenceHelper.service
				.getValue(
						"/com/ptc/windchill/enterprise/attachments/optionalAttributes/lastAuthored",
						"WINDCHILL");

		Boolean localBoolean5 = (Boolean) PreferenceHelper.service
				.getValue(
						"/com/ptc/windchill/enterprise/attachments/optionalAttributes/fileVersion",
						"WINDCHILL");

		Boolean localBoolean6 = (Boolean) PreferenceHelper.service
				.getValue(
						"/com/ptc/windchill/enterprise/attachments/optionalAttributes/toolName",
						"WINDCHILL");

		Boolean localBoolean7 = (Boolean) PreferenceHelper.service
				.getValue(
						"/com/ptc/windchill/enterprise/attachments/optionalAttributes/toolVersion",
						"WINDCHILL");

		WTContainerRef localWTContainerRef = paramNmCommandBean.getContainerRef();
		WTUser localWTUser = (WTUser) SessionHelper.manager.getPrincipal();
		Boolean localBoolean8 = (Boolean) PreferenceHelper.service
				.getValue(
						localWTContainerRef,
						"/com/ptc/windchill/enterprise/attachments/optionalAttributes/primaryContentDescription",
						"WINDCHILL", localWTUser);

		ArrayList localArrayList = new ArrayList();
		localArrayList.add(localComponentConfigFactory.newColumnConfig(
				"attachmentsName",
				this.messageSource.getMessage("ATTACHMENT_NAME"), true));

		String str = "";
		if (paramNmCommandBean != null) {
			str = paramNmCommandBean.getTextParameter("ua");
		}

		if (!"DTI".equals(str)) {
			localArrayList.add(localComponentConfigFactory.newColumnConfig(
					"infoPageActionAttachment", false));
			localArrayList.add(localComponentConfigFactory.newColumnConfig(
					"formatIcon", false));
		}

		localArrayList.add(localComponentConfigFactory.newColumnConfig(
				"formatName", this.messageSource.getMessage("FORMAT"), true));

		if (!ContentRoleType.PRIMARY.equals(paramContentRoleType)) {
			ColumnConfig localColumnConfig = localComponentConfigFactory
					.newColumnConfig("description", this.messageSource
							.getMessage("ATTACHMENT_DESCRIPTION"), true);
			((JcaColumnConfig) localColumnConfig).setVariableHeight(true);
			localArrayList.add(localColumnConfig);
		} else if (localBoolean8.booleanValue()) {
			ColumnConfig localColumnConfig = localComponentConfigFactory
					.newColumnConfig("description", this.messageSource
							.getMessage("ATTACHMENT_DESCRIPTION"), true);
			((JcaColumnConfig) localColumnConfig).setVariableHeight(true);
			localArrayList.add(localColumnConfig);
		}
		localArrayList.add(localComponentConfigFactory.newColumnConfig(
				"thePersistInfo.modifyStamp", true));

		ColumnConfig localColumnConfig = localComponentConfigFactory
				.newColumnConfig("modifier", true);
		localColumnConfig.setTargetObject("modifiedBy");
		localColumnConfig.setNeed("modifiedBy");
		localArrayList.add(localColumnConfig);

		if (localBoolean1.booleanValue()) {
			localArrayList
					.add(localComponentConfigFactory.newColumnConfig(
							"comments", this.messageSource
									.getMessage("ATTACHMENT_COMMENTS"), true));
		}

		if (localBoolean2.booleanValue()) {
			localArrayList.add(localComponentConfigFactory.newColumnConfig(
					"distributable", this.messageSource
							.getMessage("ATTACHMENT_EXTERNALDISTRIBUTION"),
					true));
		}

		if (localBoolean3.booleanValue()) {
			localArrayList.add(localComponentConfigFactory.newColumnConfig(
					"authoredBy",
					this.messageSource.getMessage("ATTACHMENT_AUTHOREDBY"),
					true));
		}

		if (localBoolean4.booleanValue()) {
			localArrayList.add(localComponentConfigFactory.newColumnConfig(
					"lastAuthored",
					this.messageSource.getMessage("ATTACHMENT_LASTAUTHORED"),
					true));
		}

		if (localBoolean5.booleanValue()) {
			localArrayList.add(localComponentConfigFactory.newColumnConfig(
					"fileVersion",
					this.messageSource.getMessage("ATTACHMENT_FILEVERSION"),
					true));
		}

		if (localBoolean6.booleanValue()) {
			localArrayList
					.add(localComponentConfigFactory.newColumnConfig(
							"toolName", this.messageSource
									.getMessage("ATTACHMENT_TOOLNAME"), true));
		}

		if (localBoolean7.booleanValue()) {
			localArrayList.add(localComponentConfigFactory.newColumnConfig(
					"toolVersion",
					this.messageSource.getMessage("ATTACHMENT_TOOLVERSION"),
					true));
		}

		return localArrayList;
	}

}
