package ext.casc.report.mvc.builders;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import wt.doc.WTDocument;
import wt.fc.ObjectIdentifier;
import wt.fc.PersistInfo;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.iba.definition.IBADefinitionException;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.util.WTContext;
import wt.util.WTException;
import wt.util.WTStandardDateFormat;

import com.glaway.mpm.util.TypeUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.process.ProcessTask;
import ext.casc.process.ProcessTaskItem;
import ext.casc.process.ProcessTaskLink;
import ext.casc.process.util.ProcessUtil;
import ext.casc.util.IBAUtility;

@ComponentBuilder("SearchCLDEJH_table_id_clde")
public class CLDEJHBuilder extends AbstractComponentBuilder {
	public static String partNumber;
	public static String cpdh;
	public static String xhdh;
	public static String jdbj;
	private static int index1[] = { 0 };

	private final static String CREATE_DATE = "thePersistInfo.createStamp";

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
		String work = String.valueOf(params.getParameter("work"));
		ArrayList<ProcessTask> list = new ArrayList<ProcessTask>();
		if ("search".equals(work)) {
			NmCommandBean commandBean = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
			String faqizhe = ProcessUtil.getNotNullParam(params.getParameter("faqizhe"));// 发起者
			if (faqizhe != null && !"".equals(faqizhe)) {

				faqizhe = faqizhe.split(",")[0].split("=")[1];
			}
			String jieshouzhe = ProcessUtil.getNotNullParam(params.getParameter("jieshouzhe"));// 接收者
			if (jieshouzhe != null && !"".equals(jieshouzhe)) {

				jieshouzhe = jieshouzhe.split(",")[0].split("=")[1];
			}
			String contain = ProcessUtil.getNotNullParam(params.getParameter("containerTypeList"));// 上下文
			String renwuState = ProcessUtil.getNotNullParam(params.getParameter("rwzt"));// 材料定额任务状态
			String chejian = ProcessUtil.getNotNullParam(params.getParameter("cj"));// 车间

			// 开始时间
			String startDate = ProcessUtil.getNotNullParam(params.getParameter("startDate"));
			String endDate = ProcessUtil.getNotNullParam(params.getParameter("endDate"));

			// 完成时间
			String startDate2 = ProcessUtil.getNotNullParam(params.getParameter("startDate2"));
			String endDate2 = ProcessUtil.getNotNullParam(params.getParameter("endDate2"));

			// 计划时间
			String startDate3 = ProcessUtil.getNotNullParam(params.getParameter("startDate3"));
			String endDate3 = ProcessUtil.getNotNullParam(params.getParameter("endDate3"));

			if (!"".equals(endDate) && !"null".equals(endDate) && endDate != null) {
				endDate = addOneDate(endDate);
			}
			if (!"".equals(endDate2) && !"null".equals(endDate2) && endDate2 != null) {
				endDate2 = addOneDate(endDate2);
			}
			if (!"".equals(endDate3) && !"null".equals(endDate3) && endDate3 != null) {
				endDate3 = addOneDate(endDate3);
			}

			String partNumber = ProcessUtil.getNotNullParam(params.getParameter("partNumber"));// 部件编号
			String tasktype = ProcessUtil.getNotNullParam(params.getParameter("tasktype"));// 任务类型

			// 最后更新时间起始条件
			TimeZone tz = WTContext.getContext().getTimeZone();
			Calendar ca = Calendar.getInstance(tz);
			try {
				int index = 0;
				QuerySpec qs = new QuerySpec(ProcessTask.class);
				qs.setAdvancedQueryEnabled(true);
				if (startDate != null && !"".equals(startDate)) {
					Date dateFrom = WTStandardDateFormat.parse(startDate, "yyyy/M/d");
					// 加入时区信息
					ca.setTime(dateFrom);
					dateFrom = ca.getTime();
					if (index > 0) {
						qs.appendAnd();
					}
					index++;
					SearchCondition sc1 = new SearchCondition(ProcessTask.class, CREATE_DATE, SearchCondition.GREATER_THAN_OR_EQUAL, new Timestamp(dateFrom.getTime() + 8 * 60 * 60 * 1000));
					qs.appendSearchCondition(sc1);
				}

				if (endDate != null && !"".equals(endDate)) {
					Date dateFrom1 = WTStandardDateFormat.parse(endDate, "yyyy/M/d");
					// 加入时区信息
					ca.setTime(dateFrom1);
					dateFrom1 = ca.getTime();
					if (index > 0) {
						qs.appendAnd();
					}
					index++;
					SearchCondition sc11 = new SearchCondition(ProcessTask.class, CREATE_DATE, SearchCondition.LESS_THAN_OR_EQUAL, new Timestamp(dateFrom1.getTime() + 8 * 60 * 60 * 1000));
					qs.appendSearchCondition(sc11);
				}
				if (startDate2 != null && !"".equals(startDate2)) {
					Date dateFrom = WTStandardDateFormat.parse(startDate2, "yyyy/M/d");
					// 加入时区信息
					ca.setTime(dateFrom);
					dateFrom = ca.getTime();
					if (index > 0) {
						qs.appendAnd();
					}
					index++;
					SearchCondition sc1 = new SearchCondition(ProcessTask.class, "thePersistInfo.modifyStamp", SearchCondition.GREATER_THAN_OR_EQUAL, new Timestamp(dateFrom.getTime() + 8 * 60 * 60
							* 1000));
					qs.appendSearchCondition(sc1);
				}

				if (endDate2 != null && !"".equals(endDate2)) {
					Date dateFrom1 = WTStandardDateFormat.parse(endDate2, "yyyy/M/d");
					// 加入时区信息
					ca.setTime(dateFrom1);
					dateFrom1 = ca.getTime();
					if (index > 0) {
						qs.appendAnd();
					}
					index++;
					SearchCondition sc11 = new SearchCondition(ProcessTask.class, "thePersistInfo.modifyStamp", SearchCondition.LESS_THAN_OR_EQUAL, new Timestamp(dateFrom1.getTime() + 8 * 60 * 60
							* 1000));
					qs.appendSearchCondition(sc11);
				}

				if (startDate3 != null && !"".equals(startDate3)) {
					if (index > 0) {
						qs.appendAnd();
					}
					index++;

					startDate3 = startDate3.replaceAll("/", "-") + " " + "00:00:00";

					ClassAttribute caId = new ClassAttribute(ProcessTask.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
					TypeUtil.getTypeQuery(ProcessTask.class, "ext.casc.process.ProcessTask", qs);
					qs.appendAnd();
					qs.appendOpenParen();

					AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("cldePlanTime");
					if (addv == null)
						throw new IBADefinitionException("No IBA Definition: " + "cldePlanTime");
					long ibaDefId = addv.getObjectID().getId();
					QuerySpec qs1 = new QuerySpec();
					int idx = qs1.appendClassList(StringValue.class, false);
					qs1.appendSelect(new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), new int[] { idx }, false);
					qs1.appendWhere(new SearchCondition(StringValue.class, "definitionReference.key.id", SearchCondition.EQUAL, ibaDefId), new int[] { idx });
					qs1.appendAnd();
					qs1.appendWhere(new SearchCondition(StringValue.class, StringValue.VALUE2, SearchCondition.GREATER_THAN_OR_EQUAL, startDate3, true), new int[] { idx });

					SubSelectExpression stringIBAQuery = new SubSelectExpression(qs1);
					qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, stringIBAQuery), index1);
					qs.appendCloseParen();

				}

				if (endDate3 != null && !"".equals(endDate3)) {
					if (index > 0) {
						qs.appendAnd();
					}
					index++;

					endDate3 = endDate3.replaceAll("/", "-") + " " + "00:00:00";

					ClassAttribute caId = new ClassAttribute(ProcessTask.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
					TypeUtil.getTypeQuery(ProcessTask.class, "ext.casc.process.ProcessTask", qs);
					qs.appendAnd();
					qs.appendOpenParen();

					AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("cldePlanTime");
					if (addv == null)
						throw new IBADefinitionException("No IBA Definition: " + "cldePlanTime");
					long ibaDefId = addv.getObjectID().getId();
					QuerySpec qs2 = new QuerySpec();
					int idx = qs2.appendClassList(StringValue.class, false);
					qs2.appendSelect(new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), new int[] { idx }, false);
					qs2.appendWhere(new SearchCondition(StringValue.class, "definitionReference.key.id", SearchCondition.EQUAL, ibaDefId), new int[] { idx });
					qs2.appendAnd();
					qs2.appendWhere(new SearchCondition(StringValue.class, StringValue.VALUE2, SearchCondition.LESS_THAN_OR_EQUAL, endDate3, true), new int[] { idx });

					SubSelectExpression stringIBAQuery = new SubSelectExpression(qs2);
					qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, stringIBAQuery), index1);
					qs.appendCloseParen();

				}

				if ((startDate3 == null || "".equals(startDate3)) && (endDate3 == null || "".equals(endDate3))) {
					if (index > 0) {
						qs.appendAnd();
					}

					ClassAttribute caId = new ClassAttribute(ProcessTask.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
					TypeUtil.getTypeQuery(ProcessTask.class, "ext.casc.process.ProcessTask", qs);
					qs.appendAnd();
					qs.appendOpenParen();

					AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("cldePlanTime");
					if (addv == null)
						throw new IBADefinitionException("No IBA Definition: " + "cldePlanTime");
					long ibaDefId = addv.getObjectID().getId();
					QuerySpec qs2 = new QuerySpec();
					int idx = qs2.appendClassList(StringValue.class, false);
					qs2.appendSelect(new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), new int[] { idx }, false);
					qs2.appendWhere(new SearchCondition(StringValue.class, "definitionReference.key.id", SearchCondition.EQUAL, ibaDefId), new int[] { idx });
					qs2.appendAnd();
					qs2.appendWhere(new SearchCondition(StringValue.class, StringValue.VALUE2, SearchCondition.NOT_NULL, "", true), new int[] { idx });
					SubSelectExpression stringIBAQuery = new SubSelectExpression(qs2);
					qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, stringIBAQuery), index1);
					qs.appendCloseParen();
				}

				if (tasktype != null && !"".equals(tasktype)) {
					if (index > 0) {
						qs.appendAnd();
					}
					index++;
					SearchCondition sc = new SearchCondition(ProcessTask.class, ProcessTask.TASK_TYPE, SearchCondition.EQUAL, tasktype, false);
					qs.appendSearchCondition(sc);
				}
				if (renwuState != null && !"".equals(renwuState)) {
					if (index > 0) {
						qs.appendAnd();
					}
					index++;
					SearchCondition sc = new SearchCondition(ProcessTask.class, ProcessTask.TASK_STATE, SearchCondition.EQUAL, renwuState, false);
					qs.appendSearchCondition(sc);
				}
				if (chejian != null && !"".equals(chejian)) {
					if (index > 0) {
						qs.appendAnd();
					}
					index++;
					SearchCondition sc = new SearchCondition(ProcessTask.class, ProcessTask.ZHUZHICHEJIAN, SearchCondition.EQUAL, chejian, false);
					qs.appendSearchCondition(sc);
				}
				if (partNumber != null && !"".equals(partNumber)) {
					if (index > 0) {
						qs.appendAnd();
					}
					index++;
					SearchCondition sc = new SearchCondition(ProcessTask.class, ProcessTask.NUMBER, SearchCondition.LIKE, "%" + partNumber + "%", false);
					qs.appendSearchCondition(sc);
				}
				if (faqizhe != null && !"".equals(faqizhe)) {
					if (index > 0) {
						qs.appendAnd();
					}
					index++;
					SearchCondition sc = new SearchCondition(ProcessTask.class, ProcessTask.CREATOR_FULL_NAME, SearchCondition.EQUAL, faqizhe, false);
					qs.appendSearchCondition(sc);
				}
				if (jieshouzhe != null && !"".equals(jieshouzhe)) {
					if (index > 0) {
						qs.appendAnd();
					}
					index++;
					SearchCondition sc = new SearchCondition(ProcessTask.class, ProcessTask.OWNERSHIP, SearchCondition.EQUAL, jieshouzhe, false);
					qs.appendSearchCondition(sc);
				}
				if (contain != null && !"".equals(contain)) {
					if (contain.startsWith("OR:wt.pdmlink.PDMLinkProduct")) {
						contain = contain.split("OR:wt.pdmlink.PDMLinkProduct:")[1];
						if (index > 0) {
							qs.appendAnd();
						}
						index++;
						// qs.appendWhere(new
						// SearchCondition(ProcessTaskItem.class,ProcessTaskItem.CONTAINER_ID,Long.valueOf(contain),
						// true));
						qs.appendWhere(new SearchCondition(ProcessTask.class, ProcessTask.CONTAINER_ID, SearchCondition.EQUAL, Long.valueOf(contain).longValue()));
					}

				}

				QueryResult qr = PersistenceHelper.manager.find(qs);
				while (qr.hasMoreElements()) {
					ProcessTask pt = (ProcessTask) qr.nextElement();
					// IBAUtility ibaUtility = new IBAUtility(pt);
					// String ibaValue = ibaUtility.getIBAValue("cldePlanTime");
					// if(ibaValue!=null && !"".equals(ibaValue)){
					// list.add(pt);
					// }
					list.add(pt);
				}
				return list;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig tableConfig = factory.newTableConfig();
		tableConfig.setId("SearchCLDEJH_table_id_clde");
		// tableConfig.setComponentMode(ComponentMode.VIEW);
		tableConfig.setSelectable(true);
		tableConfig.setConfigurable(true);

		tableConfig.setActionModel("custom_SearchWorkItem_actions");
		tableConfig.setLabel("材料定额计划完成情况汇总表");
		ColumnConfig renwuleixing = factory.newColumnConfig("cldeleixing", true);
		renwuleixing.setAutoSize(true);
		renwuleixing.setLabel("任务名称");
		renwuleixing.setDataUtilityId("SearchDownloadDataUtility");
		tableConfig.addComponent(renwuleixing);

		ColumnConfig numberColumnConfig = factory.newColumnConfig("number", true);
		numberColumnConfig.setAutoSize(true);
		numberColumnConfig.setLabel("编号");
		numberColumnConfig.setInfoPageLink(true);
		tableConfig.addComponent(numberColumnConfig);

		ColumnConfig wenjianName = factory.newColumnConfig("clderenwuName", true);
		wenjianName.setAutoSize(true);
		wenjianName.setLabel("名称");
		wenjianName.setDataUtilityId("SearchDownloadDataUtility");
		tableConfig.addComponent(wenjianName);

		ColumnConfig banben = factory.newColumnConfig("version", true);
		banben.setAutoSize(true);
		banben.setLabel("版本");
		tableConfig.addComponent(banben);

		ColumnConfig gyrwState = factory.newColumnConfig("taskState", true);
		gyrwState.setLabel("状态");
		gyrwState.setAutoSize(true);
		tableConfig.addComponent(gyrwState);

		ColumnConfig cldeState = factory.newColumnConfig("cldeState", true);
		cldeState.setLabel("材料定额状态");
		cldeState.setAutoSize(true);
		cldeState.setDataUtilityId("SearchDownloadDataUtility");
		tableConfig.addComponent(cldeState);

		ColumnConfig chejian = factory.newColumnConfig("ZHUZHICHEJIAN", true);
		chejian.setLabel("车间");
		chejian.setAutoSize(true);
		chejian.setDataUtilityId("SearchDownloadDataUtility");
		tableConfig.addComponent(chejian);

		ColumnConfig faqizhe = factory.newColumnConfig("faqizhe", true);
		faqizhe.setLabel("发起者");
		faqizhe.setAutoSize(true);
		faqizhe.setDataUtilityId("SearchDownloadDataUtility");
		tableConfig.addComponent(faqizhe);

		ColumnConfig faqizhebumen = factory.newColumnConfig("faqizhebumen", true);
		faqizhebumen.setLabel("发起者部门");
		faqizhebumen.setAutoSize(true);
		faqizhebumen.setDataUtilityId("SearchDownloadDataUtility");
		tableConfig.addComponent(faqizhebumen);

		ColumnConfig createDate = factory.newColumnConfig("thePersistInfo.createStamp", true);
		createDate.setLabel("任务创建时间");
		createDate.setId("thePersistInfo.createStamp");
		tableConfig.addComponent(createDate);

		ColumnConfig cldeJHEndTime = factory.newColumnConfig("cldeJHEndTime", true);
		cldeJHEndTime.setAutoSize(true);
		cldeJHEndTime.setLabel("材料定额计划完成时间");
		cldeJHEndTime.setDataUtilityId("SearchDownloadDataUtility");
		tableConfig.addComponent(cldeJHEndTime);

		ColumnConfig cldeSJEndTime = factory.newColumnConfig("cldeSJEndTime", true);
		cldeSJEndTime.setAutoSize(true);
		cldeSJEndTime.setLabel("材料定额实际完成时间");
		cldeSJEndTime.setDataUtilityId("SearchDownloadDataUtility");
		tableConfig.addComponent(cldeSJEndTime);

		ColumnConfig yuqiFlag = factory.newColumnConfig("yuqiFlag", true);
		yuqiFlag.setAutoSize(true);
		yuqiFlag.setLabel("是否逾期");
		yuqiFlag.setDataUtilityId("SearchDownloadDataUtility");
		tableConfig.addComponent(yuqiFlag);

		ColumnConfig bianzhizhe = factory.newColumnConfig("bianzhizhe", true);
		bianzhizhe.setAutoSize(true);
		bianzhizhe.setLabel("编制者");
		bianzhizhe.setDataUtilityId("SearchDownloadDataUtility");
		tableConfig.addComponent(bianzhizhe);

		ColumnConfig cldexinghao = factory.newColumnConfig("cldexinghao", true);
		cldexinghao.setAutoSize(true);
		cldexinghao.setLabel("型号");
		cldexinghao.setDataUtilityId("SearchDownloadDataUtility");
		tableConfig.addComponent(cldexinghao);

		return tableConfig;
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
