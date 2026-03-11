package ext.casc.doc.mvc.builder;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.util.SoftTypeUtil;
import wt.doc.WTDocument;
import wt.fc.*;
import wt.iba.definition.IBADefinitionException;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value._StringValue;
import wt.query.*;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.config.LatestConfigSpec;

import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;

@ComponentBuilder("ext.casc.doc.mvc.builder.SearchProcessPlanBuilder")
public class SearchProcessPlanBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params)
			throws Exception {
		String work = String.valueOf(params.getParameter("work"));
		if ("search".equals(work)) {
			NmCommandBean cb = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
			String number = (String) cb.getText().get("number");
			String name = (String) cb.getText().get("name");
			return getQueryRefDoc(number, name);
		}
		return null;
	}

	private Object getQueryRefDoc(String number, String name) throws WTException, RemoteException, WTPropertyVetoException {
		List<WTDocument> list = new ArrayList<WTDocument>();
		ArrayList<TypeIdentifier> types = SoftTypeUtil.getChildTypes("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN", null);
		QuerySpec qs = new QuerySpec();
		qs.setAdvancedQueryEnabled(true);
		qs.appendClassList(WTDocument.class, true);
		qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.LIFE_CYCLE_STATE, SearchCondition.EQUAL, "APPROVED"), new int[] { 0 });
		qs.appendAnd();
		List<Long> typeIds = new ArrayList<Long>();
		for (TypeIdentifier ti : types) {
			String type = ti.toString().substring(7);
			TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference(type);
			if(tdr != null) {
				typeIds.add(tdr.getKey().getBranchId());
			}
		}
		long[] value = new long[typeIds.size()];
		for(int i = 0; i < typeIds.size(); i++) {
			value[i] = typeIds.get(i);
		}
		qs.appendWhere(new SearchCondition(new ClassAttribute(WTDocument.class, "typeDefinitionReference.key.branchId"), SearchCondition.IN, new ArrayExpression(value)));
		if (number != null && !"".equals(number)) {
			qs.appendAnd();
			//查询工艺规程软属性工艺文件编号"PPNUMBER"
			ClassAttribute caId = new ClassAttribute(WTDocument.class, Persistable.PERSIST_INFO + "."
					+ PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
			SubSelectExpression subSelectExpression = getStringIBAQuery("PPNUMBER", number);
			qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), new int[] { 0 });
		}
		if (name != null && !"".equals(name)) {
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NAME, SearchCondition.LIKE, "%"+name+"%"), new int[] { 0 });
		}
		qs = new LatestConfigSpec().appendSearchCriteria(qs);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		while (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			list.add((WTDocument) obj[0]);
		}
		return list;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params)
			throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();
		table.setLabel("工艺文件列表");

		table.setId("ext.casc.doc.mvc.builder.SearchProcessPlanBuilder");
		table.setSelectable(true);
		ColumnConfig icon = factory.newColumnConfig("type_icon", false);
		table.addComponent(icon);

		ColumnConfig numberConfig = factory.newColumnConfig("number", false);
		numberConfig.setWidth(100);
		table.addComponent(numberConfig);

		ColumnConfig nameConfig = factory.newColumnConfig("name", false);
		nameConfig.setWidth(150);
		table.addComponent(nameConfig);

		ColumnConfig versionConfig = factory.newColumnConfig("version", false);
		versionConfig.setWidth(50);
		table.addComponent(versionConfig);

		ColumnConfig modifierConfig = factory.newColumnConfig("iterationInfo.modifier", false);
		modifierConfig.setLabel("修改者");
		modifierConfig.setWidth(100);
		table.addComponent(modifierConfig);

		ColumnConfig status = factory.newColumnConfig("state.state",true);
		status.setLabel("状态");
		status.setWidth(100);
		table.addComponent(status);

		return table;
	}

	private static SubSelectExpression getStringIBAQuery(String ibaName,
														 String ibaValue) throws WTException, WTPropertyVetoException,
			RemoteException {
		// 获取IBA属性定义
		AttributeDefDefaultView addv = IBADefinitionHelper.service
				.getAttributeDefDefaultViewByPath(ibaName);
		if (addv == null)
			throw new IBADefinitionException("No IBA Definition: " + ibaName);
		long ibaDefId = addv.getObjectID().getId();
		QuerySpec qs = new QuerySpec();
		int idx = qs.appendClassList(wt.iba.value.StringValue.class, false);
		qs.appendSelect(new ClassAttribute(wt.iba.value.StringValue.class,
				"theIBAHolderReference.key.id"), new int[] { idx }, false);
		qs.appendWhere(new SearchCondition(wt.iba.value.StringValue.class,
						"definitionReference.key.id", SearchCondition.EQUAL, ibaDefId),
				new int[] { idx });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(wt.iba.value.StringValue.class,
						_StringValue.VALUE2, SearchCondition.EQUAL, ibaValue ),
				new int[] { idx });
		return new SubSelectExpression(qs);
	}

}
