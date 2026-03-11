package ext.casc.part;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.util.WTException;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;

import ext.casc.util.NmTableGUIComponent;

public class GYWJPrintTaskDataUtility extends AbstractDataUtility {
	 public Object getDataValue(String columnName, Object obj, ModelContext mc) throws WTException {
		 if (columnName.equals("gywjoid")){
			 String oid = PersistenceHelper.getObjectIdentifier((Persistable) obj).toString();
			 GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
		     guicomponentarrayMain.setValueHidden(false);
		     String value = "<input type=\"hidden\"   name=\"gywjoid\"  value=\""+oid+"\">";
			 NmTableGUIComponent gui = new NmTableGUIComponent(value);
             guicomponentarrayMain.addGUIComponent(gui);
             return guicomponentarrayMain;
		 }
		 return null;
	 }
}
