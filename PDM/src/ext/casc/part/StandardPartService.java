package ext.casc.part;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ext.casc.util.CSCIBA;

import wt.part.WTPart;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardPartService extends StandardManager implements PartService, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 746484923898560684L;

	public static StandardPartService newStandardPartService()throws WTException {
		StandardPartService instance = new StandardPartService();
		instance.initialize();
		return instance;
	}

	@SuppressWarnings("unchecked")
	public List<String> getEndPartList(String context) throws WTException {
		List<String> ibaValueList = new ArrayList<String>();
		List<WTPart> partList = CSCPart.getEndItemPartByContext(context);
		for(int i = 0 ; i < partList.size() ; i++){
			WTPart tempPart = partList.get(i);
			CSCIBA ibaTool = new CSCIBA(tempPart);
			String value = ibaTool.getIBAValue("ENDITEMIN");
			if(value == null){
				value = "";
			}
			ibaValueList.add(value);
		}
		return ibaValueList;
	}
}
