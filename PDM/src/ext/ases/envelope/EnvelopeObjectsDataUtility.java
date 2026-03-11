package ext.ases.envelope;

import java.util.HashMap;
import ext.casc.util.NmTableGUIComponent;
import wt.enterprise.RevisionControlled;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.httpgw.GatewayServletHelper;
import wt.httpgw.URLFactory;
import wt.part.WTPart;
import wt.util.WTException;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;

public class EnvelopeObjectsDataUtility extends AbstractDataUtility{
	public Object getDataValue(String columnName, Object obj, ModelContext mc) throws WTException {
		String oid = PersistenceHelper.getObjectIdentifier((Persistable)obj).toString();
		GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
		String value = "";
		RevisionControlled revisioncontrolled = null;
		if(obj instanceof ProcessEnvelope){
			ProcessEnvelope processEnvelope = (ProcessEnvelope)obj;
			revisioncontrolled = EnvelopeHelper.service.getTopObject(processEnvelope);
		}

		if(columnName.equals("topObject")){
			if(revisioncontrolled!=null){
				WTPart part = null;
				if(revisioncontrolled instanceof WTPart){
					part = (WTPart)revisioncontrolled;
				}
				if(part != null){
					String partNumber = part.getNumber();
					URLFactory uf = new URLFactory();
					HashMap m = new HashMap();
					m.put("oid", part.toString());
					m.put("action", "ObjProps");
					String urlInfo = GatewayServletHelper.buildAuthenticatedHREF(uf, "wt.enterprise.URLProcessor", "URLTemplateAction",m, true);
					value ="<a href=\""+urlInfo+"\">"+partNumber+"</a>";
				}
			}
		
			NmTableGUIComponent text = new NmTableGUIComponent(value);
			guicomponentarrayMain.addGUIComponent(text);
		}
		
		return guicomponentarrayMain;
	}
}
