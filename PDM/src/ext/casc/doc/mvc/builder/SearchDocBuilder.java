package ext.casc.doc.mvc.builder;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.util.WTException;
import wt.util.WTStandardDateFormat;
import wt.vc.config.LatestConfigSpec;

import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;

import ext.casc.process.util.ProcessUtil;

@ComponentBuilder("ext.casc.doc.mvc.builder.SearchDocBuilder")
public class SearchDocBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params)
			throws Exception {
		String work = String.valueOf(params.getParameter("work"));
		if ("search".equals(work)) {
			String number = ProcessUtil.getNotNullParam(params.getParameter("number"));
			String name = ProcessUtil.getNotNullParam(params.getParameter("name"));
			String startDate = ProcessUtil.getNotNullParam(params.getParameter("startDate"));
			String endDate = ProcessUtil.getNotNullParam(params.getParameter("endDate"));
			return getQueryRefDoc(number, name, startDate, endDate);
		}
		return null;
	}
	public static String GYTZD = "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_NOTICE";
	private Object getQueryRefDoc(String number, String name, String startDate, String endDate) throws ParseException, WTException, RemoteException {
		TypeDefinitionReference gywj = ClientTypedUtility.getTypeDefinitionReference(GYTZD);
		long id = 0;
		if (gywj != null) {
			id = gywj.getKey().getBranchId();
		}
		List<WTDocument> list = new ArrayList<WTDocument>();
		QuerySpec qs = new QuerySpec();
		qs.setAdvancedQueryEnabled(true);
		int ibaHolderIndex = qs.appendClassList(WTDocument.class, true);
		qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.LIFE_CYCLE_STATE, SearchCondition.EQUAL, "APPROVED"), new int[] { 0 });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTDocument.class, "typeDefinitionReference.key.branchId", SearchCondition.EQUAL, id), new int[] { 0 });
		if (number != null && !"".equals(number)) {
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.LIKE, "%"+number+"%"), new int[] { 0 });
		}
		if (name != null && !"".equals(name)) {
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NAME, SearchCondition.LIKE, "%"+name+"%"), new int[] { 0 });
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
        table.setLabel("替换文档列表");

        table.setId("ext.casc.doc.mvc.builder.SearchDocBuilder");
        table.setSingleSelect(true);
        table.setSelectable(true);
        ColumnConfig icon = factory.newColumnConfig("type_icon", false);
        table.addComponent(icon);

        ColumnConfig numberConfig = factory.newColumnConfig("number", false);
        numberConfig.setInfoPageLink(true);
        numberConfig.setWidth(100);
//        numberConfig.setAutoSize(true);
        table.addComponent(numberConfig);

        ColumnConfig nameConfig = factory.newColumnConfig("name", false);
//        nameConfig.setAutoSize(true);
        nameConfig.setWidth(150);
        table.addComponent(nameConfig);

        ColumnConfig versionConfig = factory.newColumnConfig("version", false);
//        versionConfig.setAutoSize(true);
        versionConfig.setWidth(50);
        table.addComponent(versionConfig);

		return table;
	}

}
