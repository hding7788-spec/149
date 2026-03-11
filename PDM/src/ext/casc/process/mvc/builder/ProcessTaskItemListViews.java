package ext.casc.process.mvc.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTMessage;

import com.ptc.core.htmlcomp.components.JCAConfigurableTable;
import com.ptc.core.htmlcomp.tableview.TableColumnDefinition;
import com.ptc.core.htmlcomp.tableview.TableViewDescriptor;

import ext.casc.process.resource.ProcessAssignmentsViewRB;

public class ProcessTaskItemListViews extends JCAConfigurableTable {

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
        columns.add(TableColumnDefinition.newTableColumnDefinition("taskItemName", false));
        columns.add(TableColumnDefinition.newTableColumnDefinition("number", false));
        columns.add(TableColumnDefinition.newTableColumnDefinition("name", false));
        columns.add(TableColumnDefinition.newTableColumnDefinition("version", false));
        columns.add(TableColumnDefinition.newTableColumnDefinition("thePersistInfo.createStamp", false));
        columns.add(TableColumnDefinition.newTableColumnDefinition("endDate", false));
        columns.add(TableColumnDefinition.newTableColumnDefinition("thePersistInfo.modifyStamp", false));
        columns.add(TableColumnDefinition.newTableColumnDefinition("owner", false));
        columns.add(TableColumnDefinition.newTableColumnDefinition("taskState", false));
        columns.add(TableColumnDefinition.newTableColumnDefinition("taskType", false));
        columns.add(TableColumnDefinition.newTableColumnDefinition("renwuyaoqiu", false));

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
