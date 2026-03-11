package ext.casc.part.mvc.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import wt.doc.WTDocument;
import wt.part.WTPart;
import wt.util.WTException;

import com.ptc.core.meta.common.impl.TypeIdentifierUtilityHelper;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.part.CSCPart;
import ext.casc.part.PackagedPartHelper;

@ComponentBuilder("ext.casc.part.mvc.builder.GYWJPrintTaskBuilder")
public class GYWJPrintTaskBuilder extends AbstractComponentBuilder {
	public Object buildComponentData(ComponentConfig config,
			ComponentParams params) throws Exception {
		NmCommandBean commandBean = ((JcaComponentParams) params)
				.getHelperBean().getNmCommandBean();
		Map map = commandBean.getRequestData().getParameterMap();
		Object oidO = map.get("oid");				//读取任务页面的流程oid
		String oid = "";
		if(oidO instanceof String[]){
			oid = ((String[])oidO)[0];
		}else{
			oid = oidO.toString();
		}
		List<WTPart> parts = PackagedPartHelper.getCHGLPartBOMData(oid);
		List<WTDocument> result = new ArrayList<WTDocument>();
		for (WTPart part : parts) {
			List<WTDocument> docs = CSCPart.getDescribedDocumentsWithoutEpmByPart(part);
			for(WTDocument doc :docs){
				String docType = TypeIdentifierUtilityHelper.service.getTypeIdentifier(doc).toString();
				if(docType.contains("casc.sast.149.PROCESS_DOC")){
					result.add(doc);
				}
			}
		}
		return result;
	}

	public ComponentConfig buildComponentConfig(ComponentParams arg0)
			throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig tableConfig = factory.newTableConfig();
		tableConfig.setLabel("工艺文件列表");
		tableConfig.setComponentMode(ComponentMode.VIEW);
		tableConfig.setConfigurable(false);
		tableConfig.setId("ext.casc.part.mvc.builder.GYWJPrintTaskBuilder");
		tableConfig.setSelectable(true);
		tableConfig.setActionModel("custom_gywjprinttask_actions");

		ColumnConfig icon = factory.newColumnConfig("type_icon", false);
		tableConfig.addComponent(icon);

		ColumnConfig numberConfig = factory.newColumnConfig("number", false);
		numberConfig.setInfoPageLink(true);
		numberConfig.setWidth(150);
		tableConfig.addComponent(numberConfig);

		ColumnConfig nameConfig = factory.newColumnConfig("name", false);
		nameConfig.setWidth(150);
		tableConfig.addComponent(nameConfig);

		ColumnConfig versionConfig = factory.newColumnConfig("version", false);
		versionConfig.setAutoSize(true);
		tableConfig.addComponent(versionConfig);

		ColumnConfig gywjoid = factory.newColumnConfig("gywjoid", false);
		gywjoid.setHidden(true);
		gywjoid.setDataUtilityId("GYWJPrintTaskDataUtility");
		tableConfig.addComponent(gywjoid);
		return tableConfig;
	}
}
