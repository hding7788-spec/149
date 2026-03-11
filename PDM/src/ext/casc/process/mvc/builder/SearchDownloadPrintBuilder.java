package ext.casc.process.mvc.builder;

import com.glaway.mpm.processplan.helper.ProcessPlanHelper;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.WTDocumentUtil;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers;
import com.ptc.core.foundation.type.server.impl.TypeHelper;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.process.util.ProcessUtil;
import ext.casc.product.model.Batch;
import ext.casc.report.technics.DownloadTechnicsReportUtil;
import ext.casc.util.SoftTypeUtil;
import org.dom4j.DocumentException;
import wt.doc.WTDocument;
import wt.fc.*;
import wt.inf.container.WTContained;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.pom.PersistenceException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@ComponentBuilder("SearchProcessTask_table_id_downloadPrint")
public class SearchDownloadPrintBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
		String work = String.valueOf(params.getParameter("work"));
		ArrayList list = new ArrayList();
		if ("search".equals(work)) {
			String pici = ProcessUtil.getNotNullParam(params.getParameter("pici"));
			String docStyle = ProcessUtil.getNotNullParam(params.getParameter("docStyle"));
			NmCommandBean nmcommandbean = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
			Object actionObj = nmcommandbean.getActionOid().getRefObject();
			if(actionObj instanceof WTPart){
				WTPart part = (WTPart)actionObj;
				ArrayList doc = getDoc(part, pici, docStyle);
				return doc;
			}
		}
		return null;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig tableConfig = factory.newTableConfig();
		tableConfig.setId("SearchProcessTask_table_id_downloadPrint");
		tableConfig.setComponentMode(ComponentMode.VIEW);
		tableConfig.setConfigurable(false);
		tableConfig.setSelectable(true);
		tableConfig.setLabel("快速打印列表");
		tableConfig.setActionModel("custom_print_download_actions2");

		ColumnConfig icon = factory.newColumnConfig("type_icon", false);
		tableConfig.addComponent(icon);

		ColumnConfig nmActionsCol = factory.newColumnConfig(ColumnIdentifiers.NM_ACTIONS, false);
		((JcaColumnConfig) nmActionsCol).setActionModel("custom_print_download_actions");
		tableConfig.addComponent(nmActionsCol);

		ColumnConfig partNumber = factory.newColumnConfig("PartNumber", true);
		partNumber.setAutoSize(true);
		partNumber.setLabel("部件编号");
		partNumber.setDataUtilityId("SearchDownloadDataUtility");
		tableConfig.addComponent(partNumber);

		ColumnConfig daihao = factory.newColumnConfig("PINDEX", true);
		daihao.setAutoSize(true);
		daihao.setLabel("产品代号");
		daihao.setDataUtilityId("SearchDownloadDataUtility");
		tableConfig.addComponent(daihao);

		ColumnConfig xhdh = factory.newColumnConfig("MINDEX", true);
		xhdh.setAutoSize(true);
		xhdh.setLabel("型号代号");
		xhdh.setDataUtilityId("SearchDownloadDataUtility");
		tableConfig.addComponent(xhdh);

		ColumnConfig jdbj = factory.newColumnConfig("PHASE_CODE", true);
		jdbj.setAutoSize(true);
		jdbj.setLabel("阶段标记");
		jdbj.setDataUtilityId("SearchDownloadDataUtility");
		tableConfig.addComponent(jdbj);

		ColumnConfig gywjbh = factory.newColumnConfig("number", true);
		gywjbh.setAutoSize(true);
		gywjbh.setLabel("工艺文件编号");
		tableConfig.addComponent(gywjbh);

		ColumnConfig name = factory.newColumnConfig("name", true);
		name.setAutoSize(true);
		name.setLabel("工艺文件名称");
		tableConfig.addComponent(name);

		ColumnConfig dept = factory.newColumnConfig("DEPT", true);
		dept.setAutoSize(true);
		dept.setLabel("车间");
		tableConfig.addComponent(dept);

		ColumnConfig PDFDoc = factory.newColumnConfig("PDFDoc", true);
		PDFDoc.setAutoSize(true);
		PDFDoc.setLabel("PDF文档");
		PDFDoc.setDataUtilityId("SearchDownloadDataUtility");
		tableConfig.addComponent(PDFDoc);

		ColumnConfig pici = factory.newColumnConfig("versionInfo.identifier.versionId", true);
		pici.setLabel("版本");
		pici.setAutoSize(true);
		tableConfig.addComponent(pici);

		ColumnConfig dayinFlag = factory.newColumnConfig("PrintFlag", true);
		dayinFlag.setAutoSize(true);
		dayinFlag.setLabel("打印标识");
		tableConfig.addComponent(dayinFlag);

		ColumnConfig changeDoc = factory.newColumnConfig("changeDoc", true);
		changeDoc.setAutoSize(true);
		changeDoc.setLabel("更改单");
		changeDoc.setDataUtilityId("SearchDownloadDataUtility");
		tableConfig.addComponent(changeDoc);

		return tableConfig;
	}

	// 根据oid得到对象
	public static WTObject findObject(String oid) {
		Object obj = null;
		if (oid == null || oid.equals("")) {
			return null;
		}
		ReferenceFactory factory = new ReferenceFactory();
		try {
			WTReference reference = factory.getReference(oid);
			obj = reference.getObject();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (WTObject) obj;
	}
	public static List<WTDocument> getAllWTDocumentByAllSameVersionViewPart(WTPart part) throws WTException, PropertyVetoException, DocumentException {
        QueryResult qr = ProcessPlanHelper.searchAllIteratedByNumberVersionView(WTPart.class,part.getNumber(),part.getVersionInfo().getIdentifier().getValue(),"Manufacturing");
        List<WTDocument> docList  = new ArrayList<WTDocument>();
        while(qr.hasMoreElements()){
            WTPart newpart = (WTPart)qr.nextElement();
            QueryResult qr2 = WTPartHelper.service.getDescribedByWTDocuments(newpart, true);
            LatestConfigSpec lcs = new LatestConfigSpec();
            qr2 = lcs.process(qr2);
            while (qr2.hasMoreElements()) {
                WTDocument document = (WTDocument) qr2.nextElement();
				document = WTDocumentUtil.getLatestDocumentByNumber(document.getNumber());
                String typeName = TypedUtility.getTypeIdentifier(document).getTypename();
                if (typeName.contains("PROCESS_PLAN")) {
                    document = (WTDocument)VersionControlHelper.service.getLatestIteration(document, true);
                    //只获取关联的最新版本
                    if(docList.isEmpty() || !WTPartUtil.checkNumber(docList,document.getNumber())) {
                        docList.add(document);
                    }
                }
            }
        }
        return docList;
    }

	public static ArrayList getDoc(WTPart part, String pici, String docStyle) {
		ArrayList<WTDocument> list = new ArrayList<WTDocument>();
		try {
			List<WTPart> partList = new ArrayList<WTPart>();
			partList.add(part);
			DownloadTechnicsReportUtil.getAllChildPart(part, partList);
			for (WTPart part1 : partList) {
				IBAHelper ibaHelper = new IBAHelper(part1);
				String value = ibaHelper.getIBAValue("BATCH");
				List<WTDocument> document = getAllWTDocumentByAllSameVersionViewPart(part1);
				if (document != null && !document.isEmpty()) {
					for (WTDocument doc : document) {
						String docState = doc.getState().getState().getDisplay(Locale.CHINA);
						if ("已批准".equals(docState)) {
								String style = TypeHelper.getLocalizedTypeString(doc, Locale.CHINA);
								if (pici != null && !"".equals(pici)) {
									if (pici.equals(value)) {
										if (docStyle != null && !"".equals(docStyle)) {
											if (style.equals(docStyle)) {
												list.add(doc);
											}
										} else {
											list.add(doc);
										}
									}
								} else {
									if (docStyle != null && !"".equals(docStyle)) {
										if (style.equals(docStyle)) {
											list.add(doc);
										}
									} else {
										list.add(doc);
									}
								}
						}
					}

				}
			}
			return list;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;

	}

	public static ArrayList getPiCi(NmOid oid) throws PersistenceException, WTException {

		ArrayList list = new ArrayList();
		ArrayList list1 = new ArrayList();
		list1.add("");
		String[] str = (oid.toString()).split("~");
		String oid1 = str[0];
		WTPart part = (WTPart) findObject(oid1);
		try {
			QuerySpec qs = new QuerySpec(WTPart.class);
			SearchCondition sc = new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, part.getNumber(), false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				WTPart part1 = (WTPart) qr.nextElement();
				WTContained contained = part1.getContainer();
				String containerName = part1.getContainerName();
				String productOid = String.valueOf(PersistenceHelper.getObjectIdentifier(contained).getId());
				List<Batch> batchList = ext.casc.util.DBUtil.getBatchesByProduct(productOid, containerName);
				for (int i = 0; i < batchList.size(); i++) {
					list.add(batchList.get(i).getName());
				}
			}
			for (int i = 0; i < list.size(); i++) {
				if (!list1.contains(list.get(i))) {
					list1.add(list.get(i));
				}
			}

			return list1;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
		// TODO Auto-generated method stub

	}

	public static ArrayList getDocStyle(NmOid oid) throws WTPropertyVetoException, WTException {
		ArrayList list1 = new ArrayList();
		list1.add("");
		ArrayList<TypeIdentifier> list = SoftTypeUtil.getChildTypes("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN", null);
		for (TypeIdentifier ti : list) {
			String type = SoftTypeUtil.getTypeIdentifierDefinition(ti).getDisplay();
			list1.add(type);
		}
		return list1;

	}

}
