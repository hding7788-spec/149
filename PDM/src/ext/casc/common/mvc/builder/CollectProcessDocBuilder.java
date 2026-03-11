package ext.casc.common.mvc.builder;


import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.resource.MPMResourceHelper;
import ext.casc.common.PartCommonHelper;
import wt.doc.WTDocument;
import wt.fc.ObjectReference;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.VersionControlHelper;

import java.util.*;


@ComponentBuilder("ext.casc.common.mvc.builder.CollectProcessDocBuilder")
public class CollectProcessDocBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params)
			throws Exception {
		NmCommandBean commandbean = ((JcaComponentParams)params).getHelperBean().getNmCommandBean();
		Object object = commandbean.getActionOid().getRefObject();
		WTDocument doc = null;
		if(object instanceof MPMProcessPlan){
			MPMProcessPlan pplan = (MPMProcessPlan)object;
			Collection collection = MPMResourceHelper.service.getAssociatedDescribeDocuments(pplan);
			Iterator iterator = collection.iterator();
			while(iterator.hasNext()) {
				ObjectReference oref = (ObjectReference) iterator.next();
				WTDocument tmpdoc = (WTDocument) oref.getObject();
				if (pplan.getNumber().equals(tmpdoc.getNumber())) {
					doc = (WTDocument) VersionControlHelper.service.getLatestIteration(tmpdoc, true);
					break;
				}
			}
		}else if(object instanceof WTDocument) {
				doc = (WTDocument) object;
		}
		List<WTDocument> resultList  = new ArrayList<WTDocument>();
		if(doc!=null){
			String zhuanye = doc.getName().substring(0,doc.getName().indexOf("("));
			QueryResult partqr = WTPartHelper.service.getDescribesWTParts(doc);
			LinkedHashSet<WTPart> partList = new LinkedHashSet<WTPart>();
			if (partqr.hasMoreElements()) {
				WTPart wtPart = (WTPart) partqr.nextElement();
				partList = PartCommonHelper.getSiblings(wtPart);
			}

			WTUser curentuser = (WTUser) SessionHelper.getPrincipal();
			for(WTPart p:partList){
				List<WTDocument> documentList = PartCommonHelper.getLatestDescribedByWTDocuments(p);
				for(WTDocument tmpDoc:documentList){
					String state =tmpDoc.getState().getState().toString();
					if(tmpDoc.getName().startsWith(zhuanye)&&
							"APPROVED".equals(state)&&
							(curentuser.getName().equals(tmpDoc.getModifierName())||curentuser.getName().equals(tmpDoc.getCreatorName()))){
						QueryResult qr2 = RelatedChangesQueryCommands.getRelatedAffectingChangeNotices( tmpDoc);
						if(!qr2.hasMoreElements()){
							if(!doc.getNumber().equals(tmpDoc.getNumber())){
								resultList.add(tmpDoc);
							}
						}

					}
				}
			}
		}

		return resultList;
	}


	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params)
			throws WTException {

		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();
		//table.setComponentMode(ComponentMode.CREATE);
		table.setLabel("工艺文件列表");

		table.setId("ext.casc.common.mvc.builder.CollectProcessDocBuilder");
		table.setSelectable(true);
		ColumnConfig icon = factory.newColumnConfig("type_icon", false);
		table.addComponent(icon);

		ColumnConfig numberConfig = factory.newColumnConfig("number", true);
		numberConfig.setWidth(100);
		table.addComponent(numberConfig);

		ColumnConfig nameConfig = factory.newColumnConfig("name", true);
		nameConfig.setWidth(150);
		table.addComponent(nameConfig);

		ColumnConfig versionConfig = factory.newColumnConfig("version", true);
		versionConfig.setWidth(50);
		table.addComponent(versionConfig);

		ColumnConfig modifierConfig = factory.newColumnConfig("iterationInfo.modifier", true);
		modifierConfig.setLabel("修改者");
		modifierConfig.setWidth(100);
		table.addComponent(modifierConfig);

		ColumnConfig status = factory.newColumnConfig("state.state",true);
		status.setLabel("状态");
		status.setWidth(100);
		table.addComponent(status);

		return table;
	}

}
