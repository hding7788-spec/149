package ext.casc.doc.mvc.builder;

import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.util.WTException;

import java.util.ArrayList;
import java.util.List;

@ComponentBuilder("ext.casc.doc.mvc.builder.UploadAttachmentBuilder")
public class UploadAttachmentBuilder extends AbstractComponentBuilder{

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
		List<ApplicationData> list = new ArrayList<ApplicationData>();
		NmCommandBean nmCommandBean = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
		Object object = nmCommandBean.getPrimaryOid().getRefObject();
		if(object instanceof WTDocument){
			WTDocument document = (WTDocument) object;
			QueryResult queryResult = ContentHelper.service.getContentsByRole(document, ContentRoleType.SECONDARY);
			while(queryResult.hasMoreElements()){
				ApplicationData ap = (ApplicationData)queryResult.nextElement();
				if("数控程序".equals(ap.getDescription())){
					list.add(ap);
				}
			}
		}
		return list;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();
		table.setActionModel("custom_uploadAttachment4SK_actions");
		table.setLabel("数控程序列表");
		table.setSelectable(true);

		ColumnConfig name = factory.newColumnConfig("fileName",true);
		name.setLabel("文件名称");
		name.setAutoSize(true);
		name.setRequired(true);
		table.addComponent(name);

		ColumnConfig createTime = factory.newColumnConfig("thePersistInfo.modifyStamp",true);
		createTime.setLabel("上传时间");
		createTime.setAutoSize(true);
		createTime.setRequired(true);
		table.addComponent(createTime);

		return table;
	}

}
