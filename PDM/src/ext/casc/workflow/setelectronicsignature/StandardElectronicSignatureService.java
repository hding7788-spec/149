package ext.casc.workflow.setelectronicsignature;

import java.io.Serializable;
import java.util.ArrayList;

import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.org.electronicIdentity.ElectronicallySignable;
import wt.org.electronicIdentity.SignatureLink;
import wt.org.electronicIdentity.SignatureQuery;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class StandardElectronicSignatureService extends StandardManager implements ElectronicSignatureService, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String CLASSNAME = StandardElectronicSignatureService.class.getName();

	public String getConceptualClassname(){
		return CLASSNAME;		
	}
	
	public static StandardElectronicSignatureService newStandardElectronicSignatureService() throws WTException {
		StandardElectronicSignatureService instance = new StandardElectronicSignatureService();
		instance.initialize();
		return instance;
	}
	
	public ArrayList getElectronicSignatureFromWTObject(String oid) throws WTException{
		ReferenceFactory rf = new ReferenceFactory();
		Persistable pt = rf.getReference(oid).getObject();
		ArrayList result = new ArrayList();
		
		if(pt instanceof ElectronicallySignable){
			ElectronicallySignable object = (ElectronicallySignable)pt;
	
			try {
				SignatureQuery sq = SignatureQuery.newSignatureQuery();
				SignatureLink[] signs = (SignatureLink[]) sq.getQuery(object, null, null);
				for (int i = 0; i < signs.length; i++) {
					if(signs[i].getVote() == null){
						signs[i].setVote("");
					}
					result.add(signs[i]);
//					CSCDebug.outDebugInfo("Printing Signature of " + oid + " | Information = USER IS " + signs[i].getSignature().getName() + " VOTE = " + signs[i].getVote());
				}
			} catch (WTException e) {
				// TODO: handle exception
			} catch (WTPropertyVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
}
