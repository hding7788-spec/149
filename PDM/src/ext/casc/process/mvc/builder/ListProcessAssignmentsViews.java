package ext.casc.process.mvc.builder;

import com.ptc.core.htmlcomp.components.JCAConfigurableTable;
import com.ptc.core.htmlcomp.tableview.TableColumnDefinition;
import com.ptc.core.htmlcomp.tableview.TableViewDescriptor;
import ext.casc.process.resource.ProcessAssignmentsViewRB;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

public class ListProcessAssignmentsViews extends JCAConfigurableTable {

    private static final String PRESOURCE = "ext.casc.process.resource.ProcessAssignmentsViewRB";

    @Override
    public Class[] getClassTypes() {
        return new Class[] { WTPart.class };
    }

    @Override
    public String getDefaultSortColumn() {
        return "number";
    }

    @Override
    public String getLabel(Locale locale) {
        return WTMessage.getLocalizedMessage(PRESOURCE, ProcessAssignmentsViewRB.PROCESSASSIGNMENT_TABLE_VIEWS, null, locale);
    }

    @Override
    public String getOOTBActiveViewName() {
        return "Process Assignments Vies";
    }

    @Override
    public List getOOTBTableViews(String tableId, Locale locale) throws WTException {
        List result = new ArrayList();

        Vector columns = new Vector();
        columns.add(TableColumnDefinition.newTableColumnDefinition("type_icon", false));
        columns.add(TableColumnDefinition.newTableColumnDefinition("isComplete", false));
        columns.add(TableColumnDefinition.newTableColumnDefinition("taskItemName", false));
        columns.add(TableColumnDefinition.newTableColumnDefinition("taskOwner", false));
        columns.add(TableColumnDefinition.newTableColumnDefinition("number", false));
        columns.add(TableColumnDefinition.newTableColumnDefinition("name", false));
        columns.add(TableColumnDefinition.newTableColumnDefinition("version", false));
//        columns.add(TableColumnDefinition.newTableColumnDefinition("zhuzhichejian", false));
//        columns.add(TableColumnDefinition.newTableColumnDefinition("fuzhichejian", false));
        columns.add(TableColumnDefinition.newTableColumnDefinition("taskType", false));
        columns.add(TableColumnDefinition.newTableColumnDefinition("taskItemState", false));
        columns.add(TableColumnDefinition.newTableColumnDefinition("technicsState", false));
        columns.add(TableColumnDefinition.newTableColumnDefinition("thePersistInfo.createStamp", false));
        columns.add(TableColumnDefinition.newTableColumnDefinition("endDate", false));
        columns.add(TableColumnDefinition.newTableColumnDefinition("thePersistInfo.modifyStamp", false));
        columns.add(TableColumnDefinition.newTableColumnDefinition("iszhuzhi", false));
        columns.add(TableColumnDefinition.newTableColumnDefinition("executorRole", false));
        columns.add(TableColumnDefinition.newTableColumnDefinition("renwuyaoqiu", false));
        columns.add(TableColumnDefinition.newTableColumnDefinition("description", false));

        String viewName = getViewResourceEntryKey(PRESOURCE, "ALL_TABLE_VIEW");
        TableViewDescriptor desc = TableViewDescriptor.newTableViewDescriptor(viewName, tableId, true, true, columns,
                null, true, "Process Assignment View Description");
        result.add(desc);

        viewName = getViewResourceEntryKey(PRESOURCE, "YIWANCHENG_TABLE_VIEW");
        desc = TableViewDescriptor.newTableViewDescriptor(viewName, tableId, true, true, columns,
                null, true, "Process Assignment View Description");
        result.add(desc);

        viewName = getViewResourceEntryKey(PRESOURCE, "ZHENGZAIJINXING_TABLE_VIEW");
        desc = TableViewDescriptor.newTableViewDescriptor(viewName, tableId, true, true, columns, null, true,
                "Process Assignment View Description");
        result.add(desc);

        viewName = getViewResourceEntryKey(PRESOURCE, "YIZUOFEI_TABLE_VIEW");
        desc = TableViewDescriptor.newTableViewDescriptor(viewName, tableId, true, true, columns, null, true,
                "Process Assignment View Description");
        result.add(desc);

        viewName = getViewResourceEntryKey(PRESOURCE, "YISHANCHU_TABLE_VIEW");
        desc = TableViewDescriptor.newTableViewDescriptor(viewName, tableId, true, true, columns, null, true,
                "Process Assignment View Description");
        result.add(desc);

        viewName = getViewResourceEntryKey(PRESOURCE, "GONGYISHEJI_TABLE_VIEW");
        desc = TableViewDescriptor.newTableViewDescriptor(viewName, tableId, true, true, columns, null, true,
                "Process Assignment View Description");
        result.add(desc);

        viewName = getViewResourceEntryKey(PRESOURCE, "GONGYIGENGGAI_TABLE_VIEW");
        desc = TableViewDescriptor.newTableViewDescriptor(viewName, tableId, true, true, columns, null, true,
                "Process Assignment View Description");
        result.add(desc);

        viewName = getViewResourceEntryKey(PRESOURCE, "LINSHIGONGYI_TABLE_VIEW");
        desc = TableViewDescriptor.newTableViewDescriptor(viewName, tableId, true, true, columns, null, true,
                "Process Assignment View Description");
        result.add(desc);

        return result;
    }

    @Override
    public List getSpecialTableColumnsAttrDefinition(Locale locale) {
        return null;
    }

}
