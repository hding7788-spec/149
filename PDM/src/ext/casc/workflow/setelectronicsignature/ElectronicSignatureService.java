package ext.casc.workflow.setelectronicsignature;

import java.util.ArrayList;
import wt.util.WTException;

public interface ElectronicSignatureService {
	public ArrayList getElectronicSignatureFromWTObject(String oid) throws WTException;
}
