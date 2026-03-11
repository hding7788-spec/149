package ext.casc.access;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import wt.fc.WTObject;
import wt.util.WTException;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.dataUtilities.StringDataUtility;
import com.ptc.core.components.rendering.AbstractGuiComponent;
import com.ptc.core.components.rendering.guicomponents.ComboBox;
import com.ptc.core.ui.resources.ComponentMode;

import ext.casc.util.IBAHelper;

public class SecretLevelDataUtility extends StringDataUtility {

	@Override
	public Object getDataValue(String columnName, Object obj, ModelContext modelcontext)
			throws WTException {
		Object component = super.getDataValue(columnName, obj, modelcontext);
		ComponentMode mode = modelcontext.getDescriptorMode();
		String cName = ((AbstractGuiComponent) component).getColumnName();
		if(mode != ComponentMode.CREATE && mode != ComponentMode.EDIT) {
            return component;
        }
		if (columnName.equals("SECRET")) {
			String id ="SECRET";
			String name = "SECRET";
			ArrayList<String> display = new ArrayList<String>();
			ArrayList<String> internal = new ArrayList<String>();
			String selected = "NULL";
			if(ComponentMode.EDIT.equals(mode)){
				WTObject o = (WTObject) modelcontext.getNmCommandBean().getPageOid().getRefObject();
				selected  = IBAHelper.getIBAStringValue(o,"SECRET");
			}
			if(StringUtils.isBlank(selected)){
				selected = "NULL";
			}
			if(AccessAdminUtil.isJIMIGroup()){
				display.add("公开");
				display.add("内部");
				display.add("秘密★10年");
				display.add("机密★20年");
				internal.add("NULL");
				internal.add("NEIBU");
				internal.add("MIMI");
				internal.add("JIMI");

			}else if (AccessAdminUtil.isMIMIGroup()){
				display.add("公开");
				display.add("内部");
				display.add("秘密★10年");
				internal.add("NULL");
				internal.add("NEIBU");
				internal.add("MIMI");
			}else if (AccessAdminUtil.isNEIBUGroup()){
				display.add("公开");
				display.add("内部");
				internal.add("NULL");
				internal.add("NEIBU");
			}else{
				display.add("公开");
				internal.add("NULL");
			}
			ComboBox box = getComboBox(id,name,display,display,selected,true);
			box.setColumnName(cName);
			return box;
		}
		return null;
	}
	 public ComboBox getComboBox(String id,String name ,ArrayList<String> display,
	            ArrayList<String> internal, String selected, boolean flag) {
	        ComboBox combobox = new ComboBox();
	        combobox.setId(id);
	        combobox.setValues(display);
	        combobox.setSelected(selected);
	        combobox.setInternalValues(internal);
	        combobox.setEditable(flag);
	        return combobox;
	    }
}
