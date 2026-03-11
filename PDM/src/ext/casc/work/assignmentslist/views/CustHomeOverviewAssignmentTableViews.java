package ext.casc.work.assignmentslist.views;

import com.ptc.core.htmlcomp.components.JCAConfigurableTable;
import com.ptc.core.htmlcomp.tableview.SortColumnDescriptor;
import com.ptc.core.htmlcomp.tableview.TableColumnDefinition;
import com.ptc.core.htmlcomp.tableview.TableViewDescriptor;
import com.ptc.windchill.enterprise.work.assignmentslist.server.AssignmentsTableUtilityHelper;
import com.ptc.windchill.enterprise.work.assignmentslist.server.WorkItemTypeCriteria;
import org.apache.log4j.Logger;
import wt.log4j.LogR;
import wt.meeting.actionitem.DiscreteActionItem;
import wt.projmgmt.execution.Milestone;
import wt.projmgmt.execution.ProjectActivity;
import wt.projmgmt.execution.ProjectWorkItem;
import wt.projmgmt.execution.SummaryActivity;
import wt.projmgmt.resource.Deliverable;
import wt.util.InstalledProperties;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.util.WTPropertyVetoException;
import wt.workflow.work.WorkItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

public class CustHomeOverviewAssignmentTableViews extends JCAConfigurableTable {
    private static final String RESOURCE = "com.ptc.netmarkets.work.workResource";
    private static final String ACTIONITEM_RESOURCE = "com.ptc.netmarkets.actionitem.actionitemResource";
    private static final String FOLDER_RESOURCE = "com.ptc.netmarkets.folder.folderResource";
    private static final boolean isProjectLinkInstalled = InstalledProperties.isInstalled("Windchill.ProjectLink");
    private static final Logger logger = LogR.getLogger(CustHomeOverviewAssignmentTableViews.class.getName());
    protected Vector viewColumns;

    public CustHomeOverviewAssignmentTableViews()
    {
        viewColumns = null;
    }

    public boolean canAttributeBeUsedInFilter(String s)
    {
        return AssignmentsTableUtilityHelper.isAttributeAvailable(s);
    }

    public boolean isAttributeValidForColumnStep(String s)
    {
        return AssignmentsTableUtilityHelper.isAttributeAvailable(s);
    }

    public boolean isAttributeValidForSortingStep(String s)
    {
        return AssignmentsTableUtilityHelper.isAttributeAvailable(s);
    }

    public Class[] getClassTypes()
    {
        Class aclass[] = null;
        if(isProjectLinkInstalled)
            aclass = (new Class[] {
                    WorkItem.class,
                    ProjectActivity.class,
                    SummaryActivity.class,
                    Milestone.class,
                    Deliverable.class,
                    DiscreteActionItem.class
            });
        else
            aclass = (new Class[] {
                WorkItem.class
            });
        return aclass;
    }

    public String getLabel(Locale locale)
    {
        return WTMessage.getLocalizedMessage("com.ptc.netmarkets.work.workResource", "103", null, locale);
    }

    public String getOOTBActiveViewName()
    {
        return getViewResourceEntryKey("com.ptc.netmarkets.work.workResource", "75");
    }

    public List getOOTBTableViews(String s, Locale locale)
        throws WTException
    {
        String s1 = "";
        String s3 = "";
        ArrayList arraylist = new ArrayList(15);
        TableViewDescriptor tableviewdescriptor = null;
        intializeViewColumns();
        Vector vector = new Vector(20);
        vector.addAll(viewColumns);
        vector.add(TableColumnDefinition.newTableColumnDefinition("ASSIGNMENT_ROLE", false));
        Vector vector1 = new Vector(10);
        s1 = getViewResourceEntryKey("com.ptc.netmarkets.work.workResource", "75");
        s3 = getViewResourceEntryKey("com.ptc.netmarkets.work.workResource", "75");
        vector1.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(WorkItem.class));
        if(isProjectLinkInstalled)
        {
            vector1.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(ProjectWorkItem.class));
            vector1.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(ProjectActivity.class));
            vector1.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(SummaryActivity.class));
            vector1.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(Milestone.class));
            vector1.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(Deliverable.class));
            vector1.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(DiscreteActionItem.class));
        }
        vector1.add(AssignmentsTableUtilityHelper.createOpenAssignmentCriteria(true));
        tableviewdescriptor = AssignmentsTableUtilityHelper.createTableView(s, s1, s3, vector, vector1, getDefaultSort("DESCENDING"));
        arraylist.add(tableviewdescriptor);
        logger.debug((new StringBuilder()).append("Created view - ").append(s1).append(" - for table id ").append(s).toString());
        Vector vector2 = new Vector(10);
        s1 = getViewResourceEntryKey("com.ptc.netmarkets.work.workResource", "76");
        s3 = getViewResourceEntryKey("com.ptc.netmarkets.work.workResource", "76");
        vector2.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(WorkItem.class));
        if(isProjectLinkInstalled)
        {
            vector2.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(ProjectWorkItem.class));
            vector2.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(ProjectActivity.class));
            vector2.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(SummaryActivity.class));
            vector2.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(Milestone.class));
            vector2.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(Deliverable.class));
            vector2.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(DiscreteActionItem.class));
        }
        vector2.add(AssignmentsTableUtilityHelper.createOpenAssignmentCriteria(true));
        vector2.add(AssignmentsTableUtilityHelper.createIsOverDueCriterion(true));
        tableviewdescriptor = AssignmentsTableUtilityHelper.createTableView(s, s1, s3, vector, vector2, getDefaultSort("ASCENDING"));
        arraylist.add(tableviewdescriptor);
        logger.debug((new StringBuilder()).append("Created view - ").append(s1).append(" - for table id ").append(s).toString());
        Vector vector3 = new Vector(10);
        s1 = getViewResourceEntryKey("com.ptc.netmarkets.work.workResource", "77");
        s3 = getViewResourceEntryKey("com.ptc.netmarkets.work.workResource", "77");
        vector3.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(WorkItem.class));
        if(isProjectLinkInstalled)
        {
            vector3.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(ProjectWorkItem.class));
            vector3.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(ProjectActivity.class));
            vector3.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(SummaryActivity.class));
            vector3.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(Milestone.class));
            vector3.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(Deliverable.class));
            vector3.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(DiscreteActionItem.class));
        }
        vector3.add(AssignmentsTableUtilityHelper.createOpenAssignmentCriteria(false));
        tableviewdescriptor = AssignmentsTableUtilityHelper.createTableView(s, s1, s3, vector, vector3, getDefaultSort("DESCENDING"));
        arraylist.add(tableviewdescriptor);
        logger.debug((new StringBuilder()).append("Created view - ").append(s1).append(" - for table id ").append(s).toString());
        Vector vector4 = new Vector(10);
        s1 = getViewResourceEntryKey("com.ptc.netmarkets.work.workResource", "70");
        s3 = getViewResourceEntryKey("com.ptc.netmarkets.work.workResource", "70");
        vector4.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(WorkItem.class));
        if(isProjectLinkInstalled)
        {
            vector4.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(ProjectWorkItem.class));
            vector4.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(ProjectActivity.class));
            vector4.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(SummaryActivity.class));
            vector4.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(Milestone.class));
            vector4.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(Deliverable.class));
            vector4.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(DiscreteActionItem.class));
        }
        tableviewdescriptor = AssignmentsTableUtilityHelper.createTableView(s, s1, s3, vector, vector4, getDefaultSort("DESCENDING"));
        arraylist.add(tableviewdescriptor);
        logger.debug((new StringBuilder()).append("Created view - ").append(s1).append(" - for table id ").append(s).toString());
        Vector vector5 = new Vector();
        s1 = getViewResourceEntryKey("com.ptc.netmarkets.work.workResource", "127");
        s3 = getViewResourceEntryKey("com.ptc.netmarkets.work.workResource", "127");
        vector5.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(WorkItem.class));
        vector5.add(AssignmentsTableUtilityHelper.createOpenAssignmentCriteria(true));
        vector5.add(AssignmentsTableUtilityHelper.createWorkItemTypeCriterion(WorkItemTypeCriteria.REASSIGNED));
        tableviewdescriptor = AssignmentsTableUtilityHelper.createTableView(s, s1, s3, vector, vector5, getDefaultSort("DESCENDING"));
        arraylist.add(tableviewdescriptor);
        logger.debug((new StringBuilder()).append("Created view - ").append(s1).append(" - for table id ").append(s).toString());
        Vector vector6 = new Vector();
        s1 = getViewResourceEntryKey("com.ptc.netmarkets.work.workResource", "131");
        s3 = getViewResourceEntryKey("com.ptc.netmarkets.work.workResource", "131");
        vector6.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(WorkItem.class));
        vector6.add(AssignmentsTableUtilityHelper.createOpenAssignmentCriteria(true));
        vector6.add(AssignmentsTableUtilityHelper.createWorkItemTypeCriterion(WorkItemTypeCriteria.ACCEPTED));
        tableviewdescriptor = AssignmentsTableUtilityHelper.createTableView(s, s1, s3, vector, vector6, getDefaultSort("DESCENDING"));
        arraylist.add(tableviewdescriptor);
        logger.debug((new StringBuilder()).append("Created view - ").append(s1).append(" - for table id ").append(s).toString());
        Vector vector7 = new Vector();
        s1 = getViewResourceEntryKey("com.ptc.netmarkets.work.workResource", "132");
        s3 = getViewResourceEntryKey("com.ptc.netmarkets.work.workResource", "132");
        vector7.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(WorkItem.class));
        vector7.add(AssignmentsTableUtilityHelper.createOpenAssignmentCriteria(true));
        vector7.add(AssignmentsTableUtilityHelper.createWorkItemTypeCriterion(WorkItemTypeCriteria.DELEGATED));
        tableviewdescriptor = AssignmentsTableUtilityHelper.createTableView(s, s1, s3, vector, vector7, getDefaultSort("DESCENDING"));
        arraylist.add(tableviewdescriptor);
        logger.debug((new StringBuilder()).append("Created view - ").append(s1).append(" - for table id ").append(s).toString());
        Vector vector8 = new Vector(10);
        s1 = getViewResourceEntryKey("com.ptc.netmarkets.work.workResource", "77e");
        s3 = getViewResourceEntryKey("com.ptc.netmarkets.work.workResource", "77e");
        vector8.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(WorkItem.class));
        vector8.add(AssignmentsTableUtilityHelper.createOpenAssignmentCriteria(true));
        tableviewdescriptor = AssignmentsTableUtilityHelper.createTableView(s, s1, s3, vector, vector8, getDefaultSort("DESCENDING"));
        arraylist.add(tableviewdescriptor);
        logger.debug((new StringBuilder()).append("Created view - ").append(s1).append(" - for table id ").append(s).toString());

        //begin add by LongXiuChuan 2013/5/3
        Vector vector18 = new Vector(10);
        s1 = getViewResourceEntryKey("ext.casc.work.CustWorkResource", "300");
        s3 = getViewResourceEntryKey("ext.casc.work.CustWorkResource", "300");
        vector18.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(WorkItem.class));
        vector18.add(AssignmentsTableUtilityHelper.createOpenAssignmentCriteria(true));
        tableviewdescriptor = AssignmentsTableUtilityHelper.createTableView(s, s1, s3, vector, vector18, getDefaultSort("DESCENDING"));
        arraylist.add(tableviewdescriptor);
        logger.debug((new StringBuilder()).append("Created view - ").append(s1).append(" - for table id ").append(s).toString());

        Vector vector19 = new Vector(10);
        s1 = getViewResourceEntryKey("ext.casc.work.CustWorkResource", "302");
        s3 = getViewResourceEntryKey("ext.casc.work.CustWorkResource", "302");
        vector19.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(WorkItem.class));
        vector19.add(AssignmentsTableUtilityHelper.createOpenAssignmentCriteria(true));
        tableviewdescriptor = AssignmentsTableUtilityHelper.createTableView(s, s1, s3, vector, vector19, getDefaultSort("DESCENDING"));
        arraylist.add(tableviewdescriptor);


        Vector vector20 = new Vector(10);
        s1 = getViewResourceEntryKey("ext.casc.work.CustWorkResource", "303");
        s3 = getViewResourceEntryKey("ext.casc.work.CustWorkResource", "303");
        vector20.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(WorkItem.class));
        vector20.add(AssignmentsTableUtilityHelper.createOpenAssignmentCriteria(true));
        tableviewdescriptor = AssignmentsTableUtilityHelper.createTableView(s, s1, s3, vector, vector20, getDefaultSort("DESCENDING"));
        arraylist.add(tableviewdescriptor);
        logger.debug((new StringBuilder()).append("Created view - ").append(s1).append(" - for table id ").append(s).toString());

        Vector vector21 = new Vector(10);
        s1 = getViewResourceEntryKey("ext.casc.work.CustWorkResource", "304");
        s3 = getViewResourceEntryKey("ext.casc.work.CustWorkResource", "304");
        vector21.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(WorkItem.class));
        vector21.add(AssignmentsTableUtilityHelper.createOpenAssignmentCriteria(true));
        tableviewdescriptor = AssignmentsTableUtilityHelper.createTableView(s, s1, s3, vector, vector21, getDefaultSort("DESCENDING"));
        arraylist.add(tableviewdescriptor);
        logger.debug((new StringBuilder()).append("Created view - ").append(s1).append(" - for table id ").append(s).toString());

        Vector vector22 = new Vector(10);
        s1 = getViewResourceEntryKey("ext.casc.work.CustWorkResource", "305");
        s3 = getViewResourceEntryKey("ext.casc.work.CustWorkResource", "305");
        vector22.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(WorkItem.class));
        vector22.add(AssignmentsTableUtilityHelper.createOpenAssignmentCriteria(true));
        tableviewdescriptor = AssignmentsTableUtilityHelper.createTableView(s, s1, s3, vector, vector22, getDefaultSort("DESCENDING"));
        arraylist.add(tableviewdescriptor);
        logger.debug((new StringBuilder()).append("Created view - ").append(s1).append(" - for table id ").append(s).toString());

        Vector vector23 = new Vector(10);
        s1 = getViewResourceEntryKey("ext.casc.work.CustWorkResource", "306");
        s3 = getViewResourceEntryKey("ext.casc.work.CustWorkResource", "306");
        vector23.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(WorkItem.class));
        vector23.add(AssignmentsTableUtilityHelper.createOpenAssignmentCriteria(true));
        tableviewdescriptor = AssignmentsTableUtilityHelper.createTableView(s, s1, s3, vector, vector23, getDefaultSort("DESCENDING"));
        arraylist.add(tableviewdescriptor);
        logger.debug((new StringBuilder()).append("Created view - ").append(s1).append(" - for table id ").append(s).toString());


        Vector vector24 = new Vector(10);
        s1 = getViewResourceEntryKey("ext.casc.work.CustWorkResource", "307");
        s3 = getViewResourceEntryKey("ext.casc.work.CustWorkResource", "307");
        vector24.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(WorkItem.class));
        vector24.add(AssignmentsTableUtilityHelper.createOpenAssignmentCriteria(true));
        tableviewdescriptor = AssignmentsTableUtilityHelper.createTableView(s, s1, s3, vector, vector24, getDefaultSort("DESCENDING"));
        arraylist.add(tableviewdescriptor);
        logger.debug((new StringBuilder()).append("Created view - ").append(s1).append(" - for table id ").append(s).toString());

        //end

        //add by libo 2017.02.06 begin
        Vector vector25 = new Vector(10);
        s1 = getViewResourceEntryKey("ext.casc.work.CustWorkResource", "308");
        s3 = getViewResourceEntryKey("ext.casc.work.CustWorkResource", "308");
        vector25.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(WorkItem.class));
        vector25.add(AssignmentsTableUtilityHelper.createOpenAssignmentCriteria(true));
        tableviewdescriptor = AssignmentsTableUtilityHelper.createTableView(s, s1, s3, vector, vector25, getDefaultSort("DESCENDING"));
        arraylist.add(tableviewdescriptor);
        logger.debug((new StringBuilder()).append("Created view - ").append(s1).append(" - for table id ").append(s).toString());

        Vector vector26 = new Vector(10);
        s1 = getViewResourceEntryKey("ext.casc.work.CustWorkResource", "309");
        s3 = getViewResourceEntryKey("ext.casc.work.CustWorkResource", "309");
        vector26.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(WorkItem.class));
        vector26.add(AssignmentsTableUtilityHelper.createOpenAssignmentCriteria(true));
        tableviewdescriptor = AssignmentsTableUtilityHelper.createTableView(s, s1, s3, vector, vector26, getDefaultSort("DESCENDING"));
        arraylist.add(tableviewdescriptor);
        logger.debug((new StringBuilder()).append("Created view - ").append(s1).append(" - for table id ").append(s).toString());
        //add by libo 2017.02.06 end

        Vector vector27 = new Vector(10);
        s1 = getViewResourceEntryKey("ext.casc.work.CustWorkResource", "310");
        s3 = getViewResourceEntryKey("ext.casc.work.CustWorkResource", "310");
        vector27.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(WorkItem.class));
//        vector27.add(AssignmentsTableUtilityHelper.createOpenAssignmentCriteria(true));
        tableviewdescriptor = AssignmentsTableUtilityHelper.createTableView(s, s1, s3, vector, vector27, getDefaultSort("DESCENDING"));
        arraylist.add(tableviewdescriptor);
        logger.debug((new StringBuilder()).append("Created view - ").append(s1).append(" - for table id ").append(s).toString());

        Vector vector28 = new Vector(10);
        s1 = getViewResourceEntryKey("ext.casc.work.CustWorkResource", "311");
        s3 = getViewResourceEntryKey("ext.casc.work.CustWorkResource", "311");
        vector28.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(WorkItem.class));
//        vector28.add(AssignmentsTableUtilityHelper.createOpenAssignmentCriteria(true));
        tableviewdescriptor = AssignmentsTableUtilityHelper.createTableView(s, s1, s3, vector, vector28, getDefaultSort("DESCENDING"));
        arraylist.add(tableviewdescriptor);
        logger.debug((new StringBuilder()).append("Created view - ").append(s1).append(" - for table id ").append(s).toString());
        
        Vector vector29 = new Vector(10);
        s1 = getViewResourceEntryKey("ext.casc.work.CustWorkResource", "312");
        s3 = getViewResourceEntryKey("ext.casc.work.CustWorkResource", "312");
        vector29.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(WorkItem.class));
//        vector28.add(AssignmentsTableUtilityHelper.createOpenAssignmentCriteria(true));
        tableviewdescriptor = AssignmentsTableUtilityHelper.createTableView(s, s1, s3, vector, vector29, getDefaultSort("DESCENDING"));
        arraylist.add(tableviewdescriptor);
        logger.debug((new StringBuilder()).append("Created view - ").append(s1).append(" - for table id ").append(s).toString());
        
        Vector vector30 = new Vector(10);
        s1 = getViewResourceEntryKey("ext.casc.work.CustWorkResource", "313");
        s3 = getViewResourceEntryKey("ext.casc.work.CustWorkResource", "313");
        vector30.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(WorkItem.class));
//        vector28.add(AssignmentsTableUtilityHelper.createOpenAssignmentCriteria(true));
        tableviewdescriptor = AssignmentsTableUtilityHelper.createTableView(s, s1, s3, vector, vector30, getDefaultSort("DESCENDING"));
        arraylist.add(tableviewdescriptor);
        logger.debug((new StringBuilder()).append("Created view - ").append(s1).append(" - for table id ").append(s).toString());
        
        Vector vector31 = new Vector(10);
        s1 = getViewResourceEntryKey("ext.casc.work.CustWorkResource", "314");
        s3 = getViewResourceEntryKey("ext.casc.work.CustWorkResource", "314");
        vector31.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(WorkItem.class));
//        vector28.add(AssignmentsTableUtilityHelper.createOpenAssignmentCriteria(true));
        tableviewdescriptor = AssignmentsTableUtilityHelper.createTableView(s, s1, s3, vector, vector31, getDefaultSort("DESCENDING"));
        arraylist.add(tableviewdescriptor);
        logger.debug((new StringBuilder()).append("Created view - ").append(s1).append(" - for table id ").append(s).toString());

        if(isProjectLinkInstalled)
        {
            Vector vector9 = new Vector();
            String s2 = getViewResourceEntryKey("com.ptc.netmarkets.work.workResource", "77a");
            String s4 = getViewResourceEntryKey("com.ptc.netmarkets.work.workResource", "77a");
            vector9.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(Deliverable.class));
            vector9.add(AssignmentsTableUtilityHelper.createOpenAssignmentCriteria(true));
            TableViewDescriptor tableviewdescriptor1 = AssignmentsTableUtilityHelper.createTableView(s, s2, s4, viewColumns, vector9, getDefaultSort("DESCENDING"));
            arraylist.add(tableviewdescriptor1);
            logger.debug((new StringBuilder()).append("Created view - ").append(s2).append(" - for table id ").append(s).toString());
            Vector vector10 = new Vector();
            s2 = getViewResourceEntryKey("com.ptc.netmarkets.work.workResource", "77b");
            s4 = getViewResourceEntryKey("com.ptc.netmarkets.work.workResource", "77b");
            vector10.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(Milestone.class));
            vector10.add(AssignmentsTableUtilityHelper.createOpenAssignmentCriteria(true));
            tableviewdescriptor1 = AssignmentsTableUtilityHelper.createTableView(s, s2, s4, viewColumns, vector10, getDefaultSort("DESCENDING"));
            arraylist.add(tableviewdescriptor1);
            logger.debug((new StringBuilder()).append("Created view - ").append(s2).append(" - for table id ").append(s).toString());
            Vector vector11 = new Vector();
            s2 = getViewResourceEntryKey("com.ptc.netmarkets.work.workResource", "77c");
            s4 = getViewResourceEntryKey("com.ptc.netmarkets.work.workResource", "77c");
            vector11.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(ProjectActivity.class));
            vector11.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(SummaryActivity.class));
            vector11.add(AssignmentsTableUtilityHelper.createOpenAssignmentCriteria(true));
            tableviewdescriptor1 = AssignmentsTableUtilityHelper.createTableView(s, s2, s4, viewColumns, vector11, getDefaultSort("DESCENDING"));
            arraylist.add(tableviewdescriptor1);
            logger.debug((new StringBuilder()).append("Created view - ").append(s2).append(" - for table id ").append(s).toString());
            Vector vector12 = new Vector(15);
            vector12.addAll(viewColumns);
            vector12.add(TableColumnDefinition.newTableColumnDefinition("ASSIGNMENT_CREATED_BY", true));
            vector12.add(TableColumnDefinition.newTableColumnDefinition("DISCUSSED", false));
            Vector vector13 = new Vector(1);
            s2 = getViewResourceEntryKey("com.ptc.netmarkets.work.workResource", "8101");
            s4 = getViewResourceEntryKey("com.ptc.netmarkets.work.workResource", "8101");
            vector13.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(DiscreteActionItem.class));
            tableviewdescriptor1 = AssignmentsTableUtilityHelper.createTableView(s, s2, s4, vector12, vector13, getDefaultSort("DESCENDING"));
            arraylist.add(tableviewdescriptor1);
            logger.debug((new StringBuilder()).append("Created view - ").append(s2).append(" - for table id ").append(s).toString());
            Vector vector14 = new Vector(1);
            s2 = getViewResourceEntryKey("com.ptc.netmarkets.work.workResource", "8102");
            s4 = getViewResourceEntryKey("com.ptc.netmarkets.work.workResource", "8102");
            vector14.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(DiscreteActionItem.class));
            vector14.add(AssignmentsTableUtilityHelper.createOpenAssignmentCriteria(true));
            tableviewdescriptor1 = AssignmentsTableUtilityHelper.createTableView(s, s2, s4, vector12, vector14, getDefaultSort("DESCENDING"));
            arraylist.add(tableviewdescriptor1);
            logger.debug((new StringBuilder()).append("Created view - ").append(s2).append(" - for table id ").append(s).toString());
            Vector vector15 = new Vector(1);
            s2 = getViewResourceEntryKey("com.ptc.netmarkets.work.workResource", "8103");
            s4 = getViewResourceEntryKey("com.ptc.netmarkets.work.workResource", "8103");
            vector15.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(DiscreteActionItem.class));
            vector15.add(AssignmentsTableUtilityHelper.createIsCreatedByMeCriterion(true));
            tableviewdescriptor1 = AssignmentsTableUtilityHelper.createTableView(s, s2, s4, vector12, vector15, getDefaultSort("DESCENDING"));
            arraylist.add(tableviewdescriptor1);
            logger.debug((new StringBuilder()).append("Created view - ").append(s2).append(" - for table id ").append(s).toString());
            Vector vector16 = new Vector(1);
            s2 = getViewResourceEntryKey("com.ptc.netmarkets.work.workResource", "8104");
            s4 = getViewResourceEntryKey("com.ptc.netmarkets.work.workResource", "8104");
            vector16.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(DiscreteActionItem.class));
            vector16.add(AssignmentsTableUtilityHelper.createOpenAssignmentCriteria(true));
            vector16.add(AssignmentsTableUtilityHelper.createIsCreatedByMeCriterion(true));
            tableviewdescriptor1 = AssignmentsTableUtilityHelper.createTableView(s, s2, s4, vector12, vector16, getDefaultSort("DESCENDING"));
            arraylist.add(tableviewdescriptor1);
            logger.debug((new StringBuilder()).append("Created view - ").append(s2).append(" - for table id ").append(s).toString());
            Vector vector17 = new Vector(1);
            s2 = getViewResourceEntryKey("com.ptc.netmarkets.folder.folderResource", "53");
            s4 = getViewResourceEntryKey("com.ptc.netmarkets.folder.folderResource", "53");
            vector17.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(DiscreteActionItem.class));
            vector17.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(Milestone.class));
            vector17.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(ProjectActivity.class));
            vector17.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(SummaryActivity.class));
            vector17.add(AssignmentsTableUtilityHelper.createClassTypeCriterion(Deliverable.class));
            vector17.add(AssignmentsTableUtilityHelper.createIsDiscussedCriterion(true));
            tableviewdescriptor1 = AssignmentsTableUtilityHelper.createTableView(s, s2, s4, vector12, vector17, getDefaultSort("DESCENDING"));
            arraylist.add(tableviewdescriptor1);
            logger.debug((new StringBuilder()).append("Created view - ").append(s2).append(" - for table id ").append(s).toString());
        }
        return arraylist;
    }

    public List getSpecialTableColumnsAttrDefinition(Locale locale)
    {
        ArrayList arraylist = new ArrayList(17);
        arraylist.add(new com.ptc.core.htmlcomp.createtableview.Attribute.TextAttribute("ASSIGNMENT_REASSIGNED", WTMessage.getLocalizedMessage("com.ptc.netmarkets.work.workResource", "217", null, locale), locale));
        arraylist.add(new com.ptc.core.htmlcomp.createtableview.Attribute.TextAttribute("ASSIGNMENT_NAME", WTMessage.getLocalizedMessage("com.ptc.netmarkets.work.workResource", "102", null, locale), locale));
        arraylist.add(new com.ptc.core.htmlcomp.createtableview.Attribute.TextAttribute("PBO_TYPEICON", WTMessage.getLocalizedMessage("com.ptc.netmarkets.work.workResource", "175", null, locale), locale));
        arraylist.add(new com.ptc.core.htmlcomp.createtableview.Attribute.TextAttribute("ASSIGNMENT_SUBJECT", WTMessage.getLocalizedMessage("com.ptc.netmarkets.work.workResource", "43", null, locale), locale));
        arraylist.add(new com.ptc.core.htmlcomp.createtableview.Attribute.TextAttribute("ASSIGNMENT_SUBJECT_LC_STATE", WTMessage.getLocalizedMessage("com.ptc.netmarkets.work.workResource", "45", null, locale), locale));
        arraylist.add(new com.ptc.core.htmlcomp.createtableview.Attribute.TextAttribute("ASSIGNMENT_DEADLINE", WTMessage.getLocalizedMessage("com.ptc.netmarkets.work.workResource", "146", null, locale), locale));
        arraylist.add(new com.ptc.core.htmlcomp.createtableview.Attribute.TextAttribute("ASSIGNMENT_CREATED", WTMessage.getLocalizedMessage("com.ptc.netmarkets.work.workResource", "99", null, locale), locale));
        arraylist.add(new com.ptc.core.htmlcomp.createtableview.Attribute.TextAttribute("ASSIGNMENT_CONTAINER", WTMessage.getLocalizedMessage("com.ptc.netmarkets.work.workResource", "100", null, locale), locale));
        arraylist.add(new com.ptc.core.htmlcomp.createtableview.Attribute.TextAttribute("ASSIGNMENT_ROLE", WTMessage.getLocalizedMessage("com.ptc.netmarkets.work.workResource", "42a", null, locale), locale));
        arraylist.add(new com.ptc.core.htmlcomp.createtableview.Attribute.TextAttribute("ASSIGNMENT_PERCENT_DONE", WTMessage.getLocalizedMessage("com.ptc.netmarkets.work.workResource", "49", null, locale), locale));
        arraylist.add(AssignmentsTableUtilityHelper.createAssignmentStatusAttribute(locale));
        arraylist.add(AssignmentsTableUtilityHelper.createIsOverDueAttribute(locale));
        arraylist.add(AssignmentsTableUtilityHelper.createOpenAssignmentAttribute(locale));
        arraylist.add(AssignmentsTableUtilityHelper.createWorkItemTypeAttribute(locale));
        if(isProjectLinkInstalled)
        {
            arraylist.add(new com.ptc.core.htmlcomp.createtableview.Attribute.TextAttribute("ASSIGNMENT_CREATED_BY", WTMessage.getLocalizedMessage("com.ptc.netmarkets.actionitem.actionitemResource", "24", null, locale), locale));
            arraylist.add(AssignmentsTableUtilityHelper.createCreatedByMeAttribute(locale));
            arraylist.add(AssignmentsTableUtilityHelper.createIsDiscussedAttribute(locale));
        }
        arraylist.add(new com.ptc.core.htmlcomp.createtableview.Attribute.TextAttribute("workItemStatus", WTMessage.getLocalizedMessage("wt.workflow.worklist.worklistResource", "84"), locale));
        return arraylist;
    }

    public boolean isColumnLocked(String s)
    {
        return false;
    }

    public String getDefaultSortColumn()
    {
        return "ASSIGNMENT_CREATED";
    }

    public ArrayList getDefaultSort(String s) throws WTException {
        ArrayList arraylist = new ArrayList();
        arraylist.clear();
        SortColumnDescriptor sortcolumndescriptor = new SortColumnDescriptor();
        sortcolumndescriptor = new SortColumnDescriptor();
        sortcolumndescriptor.setColumnId("ASSIGNMENT_CREATED");
        sortcolumndescriptor.setOrder("DESCENDING");
        arraylist.add(sortcolumndescriptor);
        return arraylist;
    }

    public void intializeViewColumns() throws WTException {
        try
        {
            TableColumnDefinition tablecolumndefinition = null;
            viewColumns = new Vector(20);
            viewColumns.add(TableColumnDefinition.newTableColumnDefinition("type_icon", true));
            viewColumns.add(TableColumnDefinition.newTableColumnDefinition("ASSIGNMENT_REASSIGNED", true));
            viewColumns.add(TableColumnDefinition.newTableColumnDefinition("ASSIGNMENT_NAME", true));
            viewColumns.add(TableColumnDefinition.newTableColumnDefinition("infoPageAction", true));
            tablecolumndefinition = TableColumnDefinition.newTableColumnDefinition("formatIcon", false);
            tablecolumndefinition.setHidden(true);
            viewColumns.add(tablecolumndefinition);
            viewColumns.add(TableColumnDefinition.newTableColumnDefinition("nmActions", false));
            viewColumns.add(TableColumnDefinition.newTableColumnDefinition("PBO_TYPEICON", true));
            viewColumns.add(TableColumnDefinition.newTableColumnDefinition("ASSIGNMENT_SUBJECT", false));
            viewColumns.add(TableColumnDefinition.newTableColumnDefinition("BIANZHIZHE", false));
            viewColumns.add(TableColumnDefinition.newTableColumnDefinition("ASSIGNMENT_SUBJECT_LC_STATE", false));
            tablecolumndefinition = TableColumnDefinition.newTableColumnDefinition("ASSIGNMENT_PERCENT_DONE", false);
            tablecolumndefinition.setHidden(true);
            viewColumns.add(tablecolumndefinition);
            viewColumns.add(TableColumnDefinition.newTableColumnDefinition("ASSIGNMENT_STATUS", false));
            viewColumns.add(TableColumnDefinition.newTableColumnDefinition("ASSIGNMENT_DEADLINE", true));
            viewColumns.add(TableColumnDefinition.newTableColumnDefinition("ASSIGNMENT_CREATED", false));
            viewColumns.add(TableColumnDefinition.newTableColumnDefinition("ASSIGNMENT_CONTAINER", false));
            tablecolumndefinition = TableColumnDefinition.newTableColumnDefinition("ASSIGNMENT_IS_CREATED_BY_ME", false);
            tablecolumndefinition.setHidden(true);
            viewColumns.add(tablecolumndefinition);
            tablecolumndefinition = TableColumnDefinition.newTableColumnDefinition("ASSIGNMENT_IS_OVERDUE", false);
            tablecolumndefinition.setHidden(true);
            viewColumns.add(tablecolumndefinition);
            tablecolumndefinition = TableColumnDefinition.newTableColumnDefinition("OPEN_ASSIGNMENTS", false);
            tablecolumndefinition.setHidden(true);
            viewColumns.add(tablecolumndefinition);
            tablecolumndefinition = TableColumnDefinition.newTableColumnDefinition("WORKITEM_TYPE", false);
            tablecolumndefinition.setHidden(true);
            viewColumns.add(tablecolumndefinition);
        } catch(WTPropertyVetoException wtpropertyvetoexception) {
            wtpropertyvetoexception.printStackTrace();
            throw new WTException(wtpropertyvetoexception);
        }
    }
}
