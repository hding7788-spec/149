package ext.casc.report.mvc.builders;

import com.glaway.mpm.util.IBAHelper;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.part.CSCPart;
import ext.casc.process.util.ProcessUtil;
import ext.casc.report.technics.DownloadTechnicsReportUtil;
import ext.casc.util.SoftTypeUtil;
import ext.casc.util.Tools;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.iba.definition.StringDefinition;
import wt.iba.definition._AttributeHierarchyChild;
import wt.iba.value._StringValue;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.query.*;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtility;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTStandardDateFormat;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.View;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@ComponentBuilder("SearchWorkItem_table_id_gywdhz")
public class GYWDHZBuilder<E> extends AbstractComponentBuilder {
	public static String partNumber;
	public static String cpdh;
	public static String xhdh;
	public static String jdbj;
	public static String GYWJ = "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN";
	public static String GYZFA = "wt.doc.WTDocument|casc.sast.149.GONGYIZONGFANGAN";
	public static String GYFFA = "wt.doc.WTDocument|casc.sast.149.GONGYIFENFANGAN";
	public static String GYQTL = "wt.doc.WTDocument|casc.sast.149.QITALEIWENDANG";
	public static String GYTZD = "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_NOTICE";
	public static String GYJSXY = "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.TECHNOLOGY_AGREEMENT";

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
		String work = String.valueOf(params.getParameter("work"));
		ArrayList list = new ArrayList();
		if ("search".equals(work)) {
			String chuangjianzhe = ProcessUtil.getNotNullParam(params.getParameter("applicant"));
			if (chuangjianzhe != null && !"".equals(chuangjianzhe)) {

				chuangjianzhe = chuangjianzhe.split(",")[0].split("=")[1];
			}
			String technicsStyle = ProcessUtil.getNotNullParam(params.getParameter("technicsStyle"));
			String contain = ProcessUtil.getNotNullParam(params.getParameter("containerTypeList"));
			NmCommandBean commandBean = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
			String docStyle = ProcessUtil.getNotNullParam(params.getParameter("docStyle"));
			String smzqzt = ProcessUtil.getNotNullParam(params.getParameter("smzqzt"));
			String chejian = ProcessUtil.getNotNullParam(params.getParameter("chejian"));
			String partNumber = ProcessUtil.getNotNullParam(params.getParameter("partNumber"));
			String dayinFlag = ProcessUtil.getNotNullParam(params.getParameter("dayinFlag"));
			String startDate = ProcessUtil.getNotNullParam(params.getParameter("startDate"));
			String endDate = ProcessUtil.getNotNullParam(params.getParameter("endDate"));

			if (!"".equals(endDate)&&!"null".equals(endDate)&&endDate!=null) {
                endDate=addOneDate(endDate);
            }
			if(!Tools.isNull(startDate)||!Tools.isNull(endDate)){
				smzqzt = "APPROVED";
			}
			long startTime = System.currentTimeMillis();
			Object result = getQueryRefDoc(chuangjianzhe, contain, docStyle, smzqzt, chejian, partNumber, dayinFlag, technicsStyle,startDate,endDate);
			System.out.println("getQueryRefDoc="+(System.currentTimeMillis()-startTime));
			return result;
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public Object getQueryRefDoc(String chuangjianzhe, String contain, String docStyle, String smzqzt, String chejian, String partNumber, String dayinFlag, String technicsStyle,String startDate,String endDate)
			throws ParseException, WTException, RemoteException {
		ArrayList list = new ArrayList<WTDocument>();
		Set<String> docNumber = new HashSet<String>();
		if (partNumber != null && !"".equals(partNumber)) {
			QuerySpec qs = new QuerySpec(WTPart.class);
			if(partNumber.contains("*")){
				String tmpNumber = partNumber.replace("*", "%");
				qs.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.LIKE, tmpNumber, true));
			}else{
				qs.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, partNumber , true));

			}

			View view = CSCPart.getViewByName("Manufacturing");
			if (view != null) {
				long viewOid = view.getPersistInfo().getObjectIdentifier().getId();
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTPart.class, "view.key.id", SearchCondition.EQUAL, viewOid),
						new int[] { 0 });

				qs.setAdvancedQueryEnabled(true);
			}

			QueryResult qr = PersistenceHelper.manager.find(qs);
			 LatestConfigSpec lcs1 = new LatestConfigSpec();
			 qr = lcs1.process(qr);
			Set<String> hasGet = new HashSet<String>();
			while (qr.hasMoreElements()) {
				WTPart rootPart = (WTPart) qr.nextElement();
				List<WTPart> allPart = new ArrayList<WTPart>();
				List<WTPart> partList = new ArrayList<WTPart>();
				allPart.add(rootPart);
				partList.add(rootPart);
				if(!hasGet.contains(rootPart.getNumber())){
					DownloadTechnicsReportUtil.getAllChildPart(rootPart, allPart);
				}

				hasGet.add(rootPart.getNumber());
				for (int i = 0; i < allPart.size(); i++) {
					QueryResult allIter = VersionControlHelper.service.allIterationsOf(allPart.get(i).getMaster());
					while (allIter.hasMoreElements()) {
						WTPart part = (WTPart) allIter.nextElement();
						partList.add(part);
						hasGet.add(part.getNumber());
					}
				}

				for (int i = 0; i < partList.size(); i++) {
					QueryResult documents = WTPartHelper.service.getDescribedByDocuments(partList.get(i));
					LatestConfigSpec lcs = new LatestConfigSpec();
					documents = lcs.process(documents);
					while (documents.hasMoreElements()) {
						Object obj = documents.nextElement();
						if (obj instanceof WTDocument) {
							WTDocument doc = (WTDocument) obj;
							if (smzqzt != null && !"".equals(smzqzt)) {
								String state = doc.getState().getState().toString();
								if (!smzqzt.equals(state)) {
									continue;
								}

							}

							if (docStyle != null && !"".equals(docStyle)) {
								String identifier = TypedUtility.getTypeIdentifier(doc).toString();
								String docType = identifier.replace("WCTYPE|", "");
								String style = docStyle.replace("WCTYPE|", "");
								if (!style.equals(docType)) {
									continue;
								}
							} else {
								ArrayList<TypeIdentifier> gywjList = null;
								try {
									gywjList = SoftTypeUtil.getChildTypes("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN", null);
								} catch (WTPropertyVetoException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								String[] name = new String[gywjList.size() + 5];
								// name[0]=GYWJ;
								name[0] = GYZFA;
								name[1] = GYFFA;
								name[2] = GYQTL;
								name[3] = GYTZD;
								name[4] = GYJSXY;
								for (int i1 = 5; i1 < name.length; i1++) {
									name[i1] = gywjList.get(i1 - 5).toString().replace("WCTYPE|", "");

								}
								// String docType = docStyle.replace("WCTYPE|",
								// "");
								String identifier = TypedUtility.getTypeIdentifier(doc).toString();
								String docType = identifier.replace("WCTYPE|", "");
								List<String> list11 = Arrays.asList(name);

								// ArrayList<String> list11 = new
								// ArrayList<String>( (ArrayList<String>)
								// Arrays.asList(name));
								if (!list11.contains(docType)) {
									continue;
								}
							}

							if (contain != null && !"".equals(contain)) {
								if (contain.startsWith("OR:wt.pdmlink.PDMLinkProduct")) {
									contain = contain.split("OR:wt.pdmlink.PDMLinkProduct:")[1];
									String containId = doc.getContainerReference().getObjectId().toString();
									if (!contain.equals(containId)) {
										continue;
									}

								}
							}
							if (chuangjianzhe != null && !"".equals(chuangjianzhe)) {
								System.out.println("-=-=-=" + doc.getCreatorName());
								if (!doc.getCreatorName().equals(chuangjianzhe)) {
									continue;
								}
							}
							if (!Tools.isNull(startDate)) {
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
								Date dsd = sdf.parse(startDate);
								doc.getModifyTimestamp();
								if (doc.getModifyTimestamp().getTime()<dsd.getTime()) {
									continue;
								}
							}
							if (!Tools.isNull(endDate)) {
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
								Date dsd = sdf.parse(endDate);
								doc.getModifyTimestamp();
								if (doc.getModifyTimestamp().getTime()>dsd.getTime()) {
									continue;
								}
							}


							if (dayinFlag != null && !"".equals(dayinFlag)) {
								IBAHelper ibaHelper = new IBAHelper(doc);
								String printFlag = ibaHelper.getIBAValue("PrintFlag");
								System.out.println("FLAG" + printFlag);
								if (!dayinFlag.equals(printFlag)) {
									continue;
								}
							}
							if(!docNumber.contains(doc.getNumber())){
								list.add(doc);
								docNumber.add(doc.getNumber());
							}
						}
					}

				}

			}
			return list;
		} else {
			QuerySpec qs = new QuerySpec();
			qs.setAdvancedQueryEnabled(true);
			int ibaHolderIndex = qs.appendClassList(WTDocument.class, true);
			if (docStyle != null && !"".equals(docStyle)) {
				String style = docStyle.replace("WCTYPE|", "");
				String[] docStyle1 = new String[1];
				docStyle1[0] = style;
				long[] docTypeId = getDocTypeId(docStyle1);
				// qs.appendAnd();
				qs.appendWhere(new SearchCondition(new ClassAttribute(WTDocument.class, "typeDefinitionReference.key.branchId"), SearchCondition.IN, new ArrayExpression(docTypeId)));

			} else {
				try {
					ArrayList<TypeIdentifier> gywjList = SoftTypeUtil.getChildTypes("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN", null);
					String[] name = new String[gywjList.size() + 5];
					// name[0]=GYWJ;
					name[0] = GYZFA;
					name[1] = GYFFA;
					name[2] = GYQTL;
					name[3] = GYTZD;
					name[4] = GYJSXY;
					for (int i = 5; i < name.length; i++) {
						name[i] = gywjList.get(i - 5).toString().replace("WCTYPE|", "");

					}
					long[] docTypeId = getDocTypeId(name);
					// qs.appendAnd();
					qs.appendWhere(new SearchCondition(new ClassAttribute(WTDocument.class, "typeDefinitionReference.key.branchId"), SearchCondition.IN, new ArrayExpression(docTypeId)));
				} catch (WTPropertyVetoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (contain != null && !"".equals(contain)) {
				// OR:wt.pdmlink.PDMLinkProduct:1177901
				if (contain.startsWith("OR:wt.pdmlink.PDMLinkProduct")) {
					long containId = Long.valueOf(contain.split("OR:wt.pdmlink.PDMLinkProduct:")[1]);
					qs.appendAnd();
					qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.CONTAINER_ID, SearchCondition.EQUAL, containId), new int[] { 0 });
				}

			}

			if (chuangjianzhe != null && !"".equals(chuangjianzhe)) {
				String name = getWTUserIdByName(chuangjianzhe);
				String[] name1 = new String[1];
				name1[0] = name;
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(new ClassAttribute(WTDocument.class, "iterationInfo.creator.key.id"), SearchCondition.IN, new ArrayExpression(name1)));

			}
			if (smzqzt != null && !"".equals(smzqzt)) {
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.LIFE_CYCLE_STATE, SearchCondition.EQUAL, smzqzt, true));
			}

			if (startDate != null && !"".equals(startDate)) {
                Date dateFrom = WTStandardDateFormat.parse(startDate, "yyyy/M/d");
                qs.appendAnd();
                SearchCondition sc1 =  new SearchCondition(WTDocument.class,WTDocument.MODIFY_TIMESTAMP,SearchCondition.GREATER_THAN_OR_EQUAL, new Timestamp(dateFrom.getTime()));
                qs.appendSearchCondition(sc1);
            }


            if (endDate != null && !"".equals(endDate)) {
                Date dateFrom1 = WTStandardDateFormat.parse(endDate, "yyyy/M/d");
                qs.appendAnd();
                SearchCondition sc11 =  new SearchCondition(WTDocument.class,WTDocument.MODIFY_TIMESTAMP,SearchCondition.LESS_THAN_OR_EQUAL, new Timestamp(dateFrom1.getTime()));
                qs.appendSearchCondition(sc11);
            }
			if (dayinFlag != null && !"".equals(dayinFlag)) {
				qs.appendAnd();
				int ibaStringValueIndex = qs.appendClassList(wt.iba.value.StringValue.class, false);
				int ibaStringDefinitionIndex = qs.appendClassList(StringDefinition.class, false);
				// Latest Iteration
				SearchCondition scLatestIteration = new SearchCondition(WTDocument.class, WTAttributeNameIfc.LATEST_ITERATION, SearchCondition.IS_TRUE);
				// String Value With IBA Holder
				SearchCondition scJoinStringValueIBAHolder = new SearchCondition(wt.iba.value.StringValue.class, "theIBAHolderReference.key.id", WTDocument.class, WTAttributeNameIfc.ID_NAME);
				// String Value With Definition
				SearchCondition scJoinStringValueStringDefinition = new SearchCondition(wt.iba.value.StringValue.class, "definitionReference.key.id", StringDefinition.class,
						WTAttributeNameIfc.ID_NAME);
				qs.appendWhere(scLatestIteration, ibaHolderIndex);
				qs.appendAnd();
				qs.appendWhere(scJoinStringValueIBAHolder, ibaStringValueIndex, ibaHolderIndex);
				qs.appendAnd();
				qs.appendWhere(scJoinStringValueStringDefinition, ibaStringValueIndex, ibaStringDefinitionIndex);
				SearchCondition scStringDefinitionName1 = new SearchCondition(StringDefinition.class, _AttributeHierarchyChild.NAME, SearchCondition.EQUAL, "PrintFlag");
				SearchCondition scStringValueValue1 = new SearchCondition(wt.iba.value.StringValue.class, _StringValue.VALUE, SearchCondition.LIKE, dayinFlag.toUpperCase());
				qs.appendAnd();
				qs.appendWhere(scStringDefinitionName1, ibaStringDefinitionIndex);
				qs.appendAnd();
				qs.appendWhere(scStringValueValue1, ibaStringValueIndex);
			}
			if (technicsStyle != null && !"".equals(technicsStyle)) {
				qs.appendAnd();
				int ibaStringValueIndex = qs.appendClassList(wt.iba.value.StringValue.class, false);
				int ibaStringDefinitionIndex = qs.appendClassList(StringDefinition.class, false);
				// Latest Iteration
				SearchCondition scLatestIteration = new SearchCondition(WTDocument.class, WTAttributeNameIfc.LATEST_ITERATION, SearchCondition.IS_TRUE);
				// String Value With IBA Holder
				SearchCondition scJoinStringValueIBAHolder = new SearchCondition(wt.iba.value.StringValue.class, "theIBAHolderReference.key.id", WTDocument.class, WTAttributeNameIfc.ID_NAME);
				// String Value With Definition
				SearchCondition scJoinStringValueStringDefinition = new SearchCondition(wt.iba.value.StringValue.class, "definitionReference.key.id", StringDefinition.class,
						WTAttributeNameIfc.ID_NAME);
				qs.appendWhere(scLatestIteration, ibaHolderIndex);
				qs.appendAnd();
				qs.appendWhere(scJoinStringValueIBAHolder, ibaStringValueIndex, ibaHolderIndex);
				qs.appendAnd();
				qs.appendWhere(scJoinStringValueStringDefinition, ibaStringValueIndex, ibaStringDefinitionIndex);
				SearchCondition scStringDefinitionName1 = new SearchCondition(StringDefinition.class, _AttributeHierarchyChild.NAME, SearchCondition.EQUAL, "PPLANTYPE");
				SearchCondition scStringValueValue1 = new SearchCondition(wt.iba.value.StringValue.class, _StringValue.VALUE, SearchCondition.LIKE, technicsStyle.toUpperCase());
				qs.appendAnd();
				qs.appendWhere(scStringDefinitionName1, ibaStringDefinitionIndex);
				qs.appendAnd();
				qs.appendWhere(scStringValueValue1, ibaStringValueIndex);
			}

			qs = new LatestConfigSpec().appendSearchCriteria(qs);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				Object[] obj = (Object[]) qr.nextElement();
				if(!docNumber.contains(((WTDocument) obj[0]).getNumber())){
					list.add((WTDocument) obj[0]);
					docNumber.add(((WTDocument) obj[0]).getNumber());
				}

			}
			return list;
		}

	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig tableConfig = factory.newTableConfig();
		tableConfig.setId("SearchWorkItem_table_id_gywdhz");
		tableConfig.setComponentMode(ComponentMode.VIEW);
		tableConfig.setConfigurable(true);
		tableConfig.setSelectable(true);

		tableConfig.setActionModel("custom_SearchWorkItem_actions");
		tableConfig.setLabel("工艺文档汇总统计表");
		// ColumnConfig icon = factory.newColumnConfig("type_icon", false);
		// tableConfig.addComponent(icon);

		ColumnConfig gywdhzNumberColumnConfig = factory.newColumnConfig("gywdhzNumber", true);
		gywdhzNumberColumnConfig.setAutoSize(true);
		gywdhzNumberColumnConfig.setLabel("工艺文件编号");
		gywdhzNumberColumnConfig.setDataUtilityId("SearchDownloadDataUtility");
		tableConfig.addComponent(gywdhzNumberColumnConfig);

		ColumnConfig nameColumnConfig = factory.newColumnConfig("gywdhzName", true);
		nameColumnConfig.setAutoSize(true);
		nameColumnConfig.setLabel("工艺文件名称");
		nameColumnConfig.setDataUtilityId("SearchDownloadDataUtility");
		tableConfig.addComponent(nameColumnConfig);

		ColumnConfig GYWDHZPartNumber = factory.newColumnConfig("GYWDHZPartNumber", true);
		GYWDHZPartNumber.setLabel("零部件代号");
		GYWDHZPartNumber.setAutoSize(true);
		GYWDHZPartNumber.setDataUtilityId("SearchDownloadDataUtility");
		tableConfig.addComponent(GYWDHZPartNumber);

		ColumnConfig GYWDHZChangeReason = factory.newColumnConfig("GYWDHZChangeReason", true);
		GYWDHZChangeReason.setLabel("通知原因");
		GYWDHZChangeReason.setAutoSize(true);
		GYWDHZChangeReason.setDataUtilityId("SearchDownloadDataUtility");
		tableConfig.addComponent(GYWDHZChangeReason);

		ColumnConfig GYWDHZLimitedTime = factory.newColumnConfig("GYWDHZLimitedTime", true);
		GYWDHZLimitedTime.setLabel("有效期限");
		GYWDHZLimitedTime.setAutoSize(true);
		GYWDHZLimitedTime.setDataUtilityId("SearchDownloadDataUtility");
		tableConfig.addComponent(GYWDHZLimitedTime);


		ColumnConfig GYWDHZPartName = factory.newColumnConfig("GYWDHZPartName", true);
		GYWDHZPartName.setLabel("零部件名称");
		GYWDHZPartName.setAutoSize(true);
		GYWDHZPartName.setDataUtilityId("SearchDownloadDataUtility");
		tableConfig.addComponent(GYWDHZPartName);

		ColumnConfig GYWDHZYeshu = factory.newColumnConfig("GYWDHZYeshu", true);
		GYWDHZYeshu.setLabel("页数");
		GYWDHZYeshu.setAutoSize(true);
		GYWDHZYeshu.setDataUtilityId("SearchDownloadDataUtility");
		tableConfig.addComponent(GYWDHZYeshu);

		ColumnConfig GYWDHZChejian = factory.newColumnConfig("GYWDHZChejian", true);
		GYWDHZChejian.setLabel("主制车间");
		GYWDHZChejian.setAutoSize(true);
		GYWDHZChejian.setDataUtilityId("SearchDownloadDataUtility");
		tableConfig.addComponent(GYWDHZChejian);

		ColumnConfig dept = factory.newColumnConfig("DEPT", true);
		dept.setLabel("编制部门");
		dept.setAutoSize(true);
		tableConfig.addComponent(dept);

		ColumnConfig GYWDHZJieduanbiaoji = factory.newColumnConfig("GYWDHZJieduanbiaoji", true);
		GYWDHZJieduanbiaoji.setLabel("阶段标记");
		GYWDHZJieduanbiaoji.setAutoSize(true);
		GYWDHZJieduanbiaoji.setDataUtilityId("SearchDownloadDataUtility");
		tableConfig.addComponent(GYWDHZJieduanbiaoji);

		ColumnConfig bianhaoColumnConfig = factory.newColumnConfig("number", true);
		bianhaoColumnConfig.setAutoSize(true);
		bianhaoColumnConfig.setLabel("编号");
		tableConfig.addComponent(bianhaoColumnConfig);

		ColumnConfig containerReference = factory.newColumnConfig("containerReference", true);
		containerReference.setAutoSize(true);
		containerReference.setLabel("上下文");
		tableConfig.addComponent(containerReference);

		ColumnConfig revisionColumnConfig = factory.newColumnConfig("versionInfo.identifier.versionId", true);
		revisionColumnConfig.setAutoSize(true);
		revisionColumnConfig.setLabel("版本");
		tableConfig.addComponent(revisionColumnConfig);

		ColumnConfig docType = factory.newColumnConfig("docTypeName", true);
		docType.setAutoSize(true);
		docType.setLabel("文档类型");
		tableConfig.addComponent(docType);

		ColumnConfig state = factory.newColumnConfig("state.state", true);
		state.setLabel("生命周期状态");
		state.setAutoSize(true);
		tableConfig.addComponent(state);

		ColumnConfig GYWDHZPartVersion = factory.newColumnConfig("GYWDHZPartVersion", true);
		GYWDHZPartVersion.setLabel("部件版本");
		GYWDHZPartVersion.setAutoSize(true);
		GYWDHZPartVersion.setDataUtilityId("SearchDownloadDataUtility");
		tableConfig.addComponent(GYWDHZPartVersion);

		ColumnConfig GYWDHZPici = factory.newColumnConfig("GYWDHZPici", true);
		GYWDHZPici.setLabel("批次");
		GYWDHZPici.setAutoSize(true);
		GYWDHZPici.setDataUtilityId("SearchDownloadDataUtility");
		tableConfig.addComponent(GYWDHZPici);

		ColumnConfig GYWDHZDayingzhuangtai = factory.newColumnConfig("GYWDHZDayingzhuangtai", true);
		GYWDHZDayingzhuangtai.setLabel("打印状态");
		GYWDHZDayingzhuangtai.setAutoSize(true);
		GYWDHZDayingzhuangtai.setDataUtilityId("SearchDownloadDataUtility");
		tableConfig.addComponent(GYWDHZDayingzhuangtai);

		ColumnConfig createStamp = factory.newColumnConfig("thePersistInfo.createStamp", true);
		createStamp.setAutoSize(true);
		createStamp.setLabel("创建时间");
		tableConfig.addComponent(createStamp);

		ColumnConfig creator = factory.newColumnConfig("iterationInfo.creator", true);
		creator.setAutoSize(true);
		creator.setLabel("创建者");
		tableConfig.addComponent(creator);

		return tableConfig;
	}

	// 根据oid得到对象
	public static ArrayList<String[]> getDocStyle() throws WTPropertyVetoException, WTException {
		ArrayList<String[]> docList = new ArrayList<String[]>();
		ArrayList<TypeIdentifier> list = SoftTypeUtil.getChildTypes("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN", null);
		// List<TypeIdentifier> list =
		// TypeUtil.getChildTypes(PROCESS_PLAN.class.getName());
		if (list != null && list.size() != 0) {
			for (TypeIdentifier identifier : list) {
				String[] value = new String[2];
				String diplayName = TypedUtility.getLocalizedTypeName(identifier, Locale.CHINA);
				value[0] = diplayName;
				value[1] = identifier.toString();
				docList.add(value);
			}
		}
		docList.add(new String[] { "工艺总方案", "wt.doc.WTDocument|casc.sast.149.GONGYIZONGFANGAN" });
		docList.add(new String[] { "工艺分方案", "wt.doc.WTDocument|casc.sast.149.GONGYIFENFANGAN" });
		docList.add(new String[] { "其他类文档", "wt.doc.WTDocument|casc.sast.149.QITALEIWENDANG" });
		docList.add(new String[] { "工艺技术通知单", "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_NOTICE" });
		docList.add(new String[] { "工艺技术协议", "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.TECHNOLOGY_AGREEMENT" });
		return docList;

	}

	public static long[] getDocTypeId(String[] name) throws RemoteException, WTException {
		long[] value = new long[name.length];
		for (int i = 0; i < name.length; i++) {
			TypeDefinitionReference gywj = ClientTypedUtility.getTypeDefinitionReference(name[i]);
			long id = 0;
			if (gywj != null) {
				id = gywj.getKey().getBranchId();
				value[i] = id;
			}
		}

		return value;

	}

	@SuppressWarnings("deprecation")
	public static String getWTUserIdByName(String name) throws WTException {

		try {
			QuerySpec qs = new QuerySpec(WTUser.class);
			qs.appendWhere(new SearchCondition(WTUser.class, WTUser.NAME, SearchCondition.EQUAL, name, true));
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				WTUser user = (WTUser) qr.nextElement();
				System.out.println("ididid" + user.getPersistInfo().getObjectIdentifier().getId());
				return String.valueOf(user.getPersistInfo().getObjectIdentifier().getId());

			}
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
		// TODO Auto-generated method stub

	}

	private String addOneDate(String date) throws ParseException {
		Calendar calendar = new GregorianCalendar();
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		Date de = df.parse(date);
		calendar.setTime(de);
		calendar.add(calendar.DATE, 1);
		return new SimpleDateFormat("yyyy/MM/dd").format(calendar.getTime());
	}

}
