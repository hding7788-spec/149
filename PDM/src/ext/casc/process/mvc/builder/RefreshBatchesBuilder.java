package ext.casc.process.mvc.builder;

import com.glaway.mpm.processplan.helper.ProcessPlanHelper;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;

import java.util.ArrayList;
import java.util.List;

@ComponentBuilder("ext.casc.process.mvc.builder.RefreshBatchesBuilder")
public class RefreshBatchesBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
		NmCommandBean commandbean = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
		Object actionObj = commandbean.getActionOid().getRefObject();
		ArrayList<WTDocument> list = new ArrayList<WTDocument>();
		WTPart part = null;
		if(actionObj instanceof WTPart){
			part = (WTPart) actionObj;
		}
		if(part!=null){
			list = getAllWTDocumentByPartAndChild(part);
		}
		return list;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams arg0)
			throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();
		table.setLabel("工艺规程清单");
		table.setSelectable(true);

		ColumnConfig number = factory.newColumnConfig("PPNUMBER",true);
		number.setLabel("工艺规程编号");
		number.setAutoSize(true);
		table.addComponent(number);

		ColumnConfig name = factory.newColumnConfig("name",true);
		name.setLabel("工艺规程名称");
		name.setAutoSize(true);
		table.addComponent(name);

		ColumnConfig version = factory.newColumnConfig("version",true);
		version.setLabel("版本");
		version.setAutoSize(true);
		table.addComponent(version);

		ColumnConfig state = factory.newColumnConfig("state.state",true);
		state.setLabel("状态");
		state.setAutoSize(true);
		table.addComponent(state);

		ColumnConfig batch = factory.newColumnConfig("BATCH",true);
		batch.setLabel("批次号");
		batch.setAutoSize(true);
		table.addComponent(batch);

		ColumnConfig biaoshi = factory.newColumnConfig("BIAOSHI",true);
		biaoshi.setLabel("标识");
		biaoshi.setAutoSize(true);
		table.addComponent(biaoshi);

		ColumnConfig tecType = factory.newColumnConfig("PPLANTYPE",true);
		tecType.setLabel("工艺文件类型");
		tecType.setAutoSize(true);
		table.addComponent(tecType);

		ColumnConfig creator = factory.newColumnConfig("iterationInfo.creator",true);
		creator.setLabel("创建者");
		creator.setAutoSize(true);
		table.addComponent(creator);

		return table;
	}

	public static ArrayList<WTDocument> getAllWTDocumentByPartAndChild(WTPart part) throws WTException {
		ArrayList<WTPart> partList = new ArrayList<WTPart>();
		partList.add(part);
		partList = getChildPart(part,partList);
		ArrayList<WTDocument> docList = new ArrayList<WTDocument>();
		for (WTPart wtPart : partList) {
			QueryResult qr2 = WTPartHelper.service.getDescribedByWTDocuments(wtPart, true);
			LatestConfigSpec lcs = new LatestConfigSpec();
			qr2 = lcs.process(qr2);
			while (qr2.hasMoreElements()) {
				WTDocument document = (WTDocument) qr2.nextElement();
				String typeName = TypedUtility.getTypeIdentifier(document).getTypename();
				if (typeName.contains("casc.sast.149.PROCESS_PLAN")) {
					document = (WTDocument) VersionControlHelper.service.getLatestIteration(document, true);
					if (docList.isEmpty() || !WTPartUtil.checkNumber(docList, document.getNumber())) {
						docList.add(document);
					}
				}
			}
		}
		return docList;
	}

	public static ArrayList<WTPart> getChildPart(WTPart part,ArrayList<WTPart> list) throws WTException {
		if (part != null) {
			QueryResult qr = WTPartHelper.service.getUsesWTParts(part, WTPartUtil.getConfigSpec());
			while (qr.hasMoreElements()) {
				Persistable[] per = (Persistable[]) qr.nextElement();
				Object obj = per[1];
				if (obj instanceof WTPart) {
					WTPart wtPart = WTPartUtil.getLatestPartByNumberAndView(((WTPart) obj).getNumber(),"Manufacturing");
					list.add(wtPart);
					getChildPart(wtPart,list);
				} else if (obj instanceof WTPartMaster) {
					WTPartMaster master = (WTPartMaster) obj;
					WTPart partTemp = WTPartUtil.getLatestPartByNumberAndView(master.getNumber(), "Manufacturing");
					list.add(partTemp);
					getChildPart(partTemp,list);
				}

			}
		}
		return list;
	}
}
