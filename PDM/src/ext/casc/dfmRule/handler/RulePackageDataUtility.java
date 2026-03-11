package ext.casc.dfmRule.handler;

import wt.doc.WTDocument;
import wt.folder.SubFolder;
import wt.util.WTException;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.AbstractGuiComponent;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.core.components.rendering.guicomponents.IconComponent;
import com.ptc.core.components.rendering.guicomponents.RadioButton;
import com.ptc.core.components.rendering.guicomponents.TextDisplayComponent;

import ext.casc.dfmRule.common.Constants;
import ext.casc.dfmRule.util.GeneralUtil;


/**
 *
 *
 *
 */
public class RulePackageDataUtility extends AbstractDataUtility {

	@Override
	public Object getDataValue(String componentId, Object object, ModelContext modelContext) throws WTException {
		Object component = null;
		if (object instanceof WTDocument) {
			WTDocument document = (WTDocument) object;
			if ("operation".equals(componentId)) {
				AbstractGuiComponent gui = new RadioButton();
				((RadioButton)gui).setEnabled(true);
				((RadioButton)gui).setEnabled(true);
				((RadioButton)gui).setName("rulePackage");
				((RadioButton)gui).setId(componentId);
				((RadioButton)gui).setColumnName("rulePackage");
				((RadioButton)gui).setValue(GeneralUtil.getOidByPersistable(document));
				((RadioButton)gui).addJsAction("onclick", "getRadValue(this)");
//				RadioButton radioButton = new RadioButton();
//				radioButton.setEnabled(true);
//				radioButton.setName("rulePackage");
//				radioButton.setId(componentId);
//				radioButton.setColumnName("rulePackage");
//				radioButton.setValue(GeneralUtil.getOidByPersistable(document));
//				radioButton.addJsAction("onclick", "getValue(this)");

				component = gui;
			}
			if ("glawayicon".equals(componentId)) {
				IconComponent icon = new IconComponent();
				String src = Constants.baseUrl + "/wt/clients/images/generic.gif";
				icon.setSrc(src);

				TextDisplayComponent nameTextDisComp = new TextDisplayComponent("targetName");
				nameTextDisComp.setValue(document.getName());

				GUIComponentArray guiCompArray = new GUIComponentArray();
				guiCompArray.addGUIComponent(icon);
				guiCompArray.addGUIComponent(nameTextDisComp);

				component = guiCompArray;
			}
		} else if (object instanceof SubFolder) {
			if ("glawayicon".equals(componentId)) {
				SubFolder sfolder = (SubFolder) object;

				IconComponent icon = new IconComponent();
				String src = Constants.baseUrl + "/wt/clients/images/folder.gif";
				icon.setSrc(src);

				TextDisplayComponent nameTextDisComp = new TextDisplayComponent("targetName");
				nameTextDisComp.setValue(sfolder.getName());

				GUIComponentArray guiCompArray = new GUIComponentArray();
				guiCompArray.addGUIComponent(icon);
				guiCompArray.addGUIComponent(nameTextDisComp);

				component = guiCompArray;
			}
		}
		return component;
	}

}
