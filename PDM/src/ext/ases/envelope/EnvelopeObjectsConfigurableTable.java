/**
 * @(#)EnvelopeObjectsConfigurableTable.java
 *
 *
 * @author Leon Zhang
 * @version 1.00 2010/1/18
 */
package ext.ases.envelope;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import wt.util.WTException;
import wt.util.WTMessage;

import com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers;
import com.ptc.core.htmlcomp.components.JCAConfigurableTable;
import com.ptc.core.htmlcomp.createtableview.Attribute;
import com.ptc.core.htmlcomp.tableview.TableColumnDefinition;
import com.ptc.core.htmlcomp.tableview.TableViewDescriptor;
import com.ptc.windchill.enterprise.baseline.BaselineClientHelper;
import com.ptc.windchill.enterprise.baseline.baselineResource;

import ext.ases.envelope.*;
import wt.enterprise.RevisionControlled;
import wt.log4j.LogR;
import org.apache.log4j.Logger;

import static com.ptc.core.components.descriptor.DescriptorConstants.ColumnIdentifiers.*;

public class EnvelopeObjectsConfigurableTable extends JCAConfigurableTable {

    private static final Logger logger = LogR.getLogger("ext.ases.envelope.EnvelopeObjectsConfigurableTable");
	private static final String TABLE_RESOURCE= "ext.ases.envelope.envelopeResource";
	private static final List<String> NON_FILTER_ATTRIBUTES = new ArrayList<String>();
	
	public static String STRUCTURE_VIEW = getViewResourceEntryKey(TABLE_RESOURCE, "STRUCTURE_TABLEVIEW_NAME");
	public static String LIST_VIEW = getViewResourceEntryKey(TABLE_RESOURCE, "LIST_TABLEVIEW_NAME");
    
    
    /**
     * Get the list of classes that are supported by this table
     * These are supposed to reflect the object types that are shown in tables
     */
    public Class[] getClassTypes() {
        return new Class[]{ RevisionControlled.class };
    }
    
    /**
     * Get the User visible name of the table
     */
    public String getLabel(Locale locale) {
       //return WTMessage.getLocalizedMessage(TABLE_RESOURCE,
       //      ext.ases.envelope.envelopeResource.ENVELOPE_CONTENTS_TEXT, null, locale);
//       System.out.println("getViewResourceEntryKey(TABLE_RESOURCE, ENVELOPE_CONTENTS_TEXT) is: " + getViewResourceEntryKey(TABLE_RESOURCE, "ENVELOPE_CONTENTS_TEXT"));
       return getViewResourceEntryKey(TABLE_RESOURCE, "ENVELOPE_CONTENTS_TEXT");
    }

    
    

    /**
     * Get the Out Of The Box table view - All Objects view
     */
    public List getOOTBTableViews(String tableId, Locale locale) throws WTException {

        //The list of all ootb views
      List views = new ArrayList();

      //The columns that make up one ootb view
      Vector columns = new Vector();
      
      columns.add(TableColumnDefinition.newTableColumnDefinition(ColumnIdentifiers.ICON, false));
      columns.add(TableColumnDefinition.newTableColumnDefinition(ColumnIdentifiers.NUMBER, false));
      columns.add(TableColumnDefinition.newTableColumnDefinition(ColumnIdentifiers.NAME, false));
      columns.add(TableColumnDefinition.newTableColumnDefinition(ColumnIdentifiers.VERSION, false));
      columns.add(TableColumnDefinition.newTableColumnDefinition(ColumnIdentifiers.CONTAINER_NAME, false));
      columns.add(TableColumnDefinition.newTableColumnDefinition(ColumnIdentifiers.STATE, false));

      //Construct a descriptor based on the columns
      String name = getViewResourceEntryKey(TABLE_RESOURCE, "STRUCTURE_TABLEVIEW_NAME");//getViewResourceEntryKey(BaselineClientHelper.BASELINERESOURCE, baselineResource.ALL_VIEW);
		String description = getViewResourceEntryKey(TABLE_RESOURCE, "STRUCTURE_TABLEVIEW_NAME");//getViewResourceEntryKey(BaselineClientHelper.BASELINERESOURCE, baselineResource.ALL_VIEW_DESCRIPTION);
		
		TableViewDescriptor structureView = TableViewDescriptor.newTableViewDescriptor(name, tableId, true,
		        true, columns, null, true, description);
		//Add the descriptor to the list of all ootb views
		views.add(structureView);
		logger.debug("name is; "+ name);
		logger.debug("description is; "+ description);
		logger.debug("structureView is; "+ structureView);
		name = getViewResourceEntryKey(TABLE_RESOURCE, "LIST_TABLEVIEW_NAME");//getViewResourceEntryKey(BaselineClientHelper.BASELINERESOURCE, baselineResource.ALL_VIEW);
		description = getViewResourceEntryKey(TABLE_RESOURCE, "LIST_TABLEVIEW_NAME");//getViewResourceEntryKey(BaselineClientHelper.BASELINERESOURCE, baselineResource.ALL_VIEW_DESCRIPTION);
		
		TableViewDescriptor listView = TableViewDescriptor.newTableViewDescriptor(name, tableId, true,
		        true, columns, null, true, description);
		logger.debug("listView is; "+ listView);        
		views.add(listView);
      
      return views;

    }

    /**
     * Gets the Attribute instances of the columns that are special for this table
     * These attributes are considered as miscellanious attributes/columns
     */
    public List getSpecialTableColumnsAttrDefinition(Locale locale) {
         List result =  new ArrayList(1);
         result.add(new Attribute.TextAttribute(ColumnIdentifiers.NM_ACTIONS,  WTMessage.getLocalizedMessage("com.ptc.core.ui.componentRB", "ACTIONS", null, locale), locale));
         
        return result;
    }


    /**
     * Get the name of the table view that should be the first one
     * If null is returned, the first OOTB table view will be used as default
     */
    public String getOOTBActiveViewName() {
       return getViewResourceEntryKey(TABLE_RESOURCE, "STRUCTURE_TABLEVIEW_NAME");
    }


    /**
     * Gets the id of the columns that should be used by default for sorting
     */
    public String getDefaultSortColumn() {
       return ColumnIdentifiers.NAME;
    }
}