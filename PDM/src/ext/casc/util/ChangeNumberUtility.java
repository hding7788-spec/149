package ext.casc.util;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.doc.WTDocumentMasterIdentity;
import wt.fc.IdentityHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartMasterIdentity;
import wt.pom.UniquenessException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.config.LatestConfigSpec;

import com.ptc.wpcfg.doc.VariantSpec;
import com.ptc.wpcfg.doc.VariantSpecMaster;
import com.ptc.wpcfg.doc.VariantSpecMasterIdentity;

/**
 * update glcilink t set t.cipartnumber = 'ML'||t.cipartnumber
update glcipartlink t set t.cipartnumber = 'ML'||t.cipartnumber
 * @author Administrator
 *
 */
public class ChangeNumberUtility {

	public static void main(String[] args) throws Exception {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		String username = null;
		String passwd = null;
		if (args.length >= 2) {
			username = args[0];
			passwd = args[1];
			if (username == null)
				username = "wcadmin";

			if (passwd == null)
				passwd = "wcadmin";
		}
		System.out.println("------user:"+username+"    password:"+passwd);
		rms.setUserName(username);
		rms.setPassword(passwd);
		if (!RemoteMethodServer.ServerFlag) {
			TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference("wt.part.WTPart|casc.sast.149.GLCatalogItemPart");
			long typeId = 0;
			if (tdr != null) {
				typeId = tdr.getKey().getBranchId();
			}
			QuerySpec qs = new QuerySpec(WTPart.class);
			qs = new LatestConfigSpec().appendSearchCriteria(qs);
			qs.appendAnd();
		    qs.appendWhere(new SearchCondition(WTPart.class,
		    "typeDefinitionReference.key.branchId", SearchCondition.EQUAL, typeId),
		     new int[]{0});
		    QueryResult qr = PersistenceHelper.manager.find(qs);
		    while(qr.hasMoreElements()){
		    	WTPart part = (WTPart) qr.nextElement();
		    	if(!part.getNumber().startsWith("ML")){
		    		rename("ML"+part.getNumber(),part);
		    	}
		    }
		    System.out.println("更新目录条目"+qr.size()+"条！");
		}
	}

	/**
	 * 更改对象的name和number
	 *
	 * @param name
	 * @param number
	 * @param obj
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	public static void rename( String number, WTObject obj) throws WTPropertyVetoException, WTException {
		if (obj instanceof WTDocument) {
			WTDocument doc = (WTDocument) obj;
			WTDocumentMaster docMaster = (WTDocumentMaster) doc.getMaster();
			WTDocumentMasterIdentity docMasterIdentity;
			try {
				docMasterIdentity = (WTDocumentMasterIdentity) docMaster.getIdentificationObject();
				if (number != null)
					docMasterIdentity.setNumber(number);
				docMaster = (WTDocumentMaster) IdentityHelper.service.changeIdentity(docMaster, docMasterIdentity);
			} catch (UniquenessException e) {
				e.printStackTrace();
				throw new WTException(e.getLocalizedMessage());
			}
		} else if (obj instanceof WTPart) {
			WTPart wtp = (WTPart) obj;
			WTPartMaster wtpMaster = (WTPartMaster) wtp.getMaster();

			WTPartMasterIdentity wtpMasterIdentity;
			try {
				wtpMasterIdentity = (WTPartMasterIdentity) wtpMaster.getIdentificationObject();
				if (number != null )
					wtpMasterIdentity.setNumber(number);
				wtpMaster = (WTPartMaster) IdentityHelper.service.changeIdentity(wtpMaster, wtpMasterIdentity);
			} catch (UniquenessException e) {
				e.printStackTrace();
				throw new WTException(e.getLocalizedMessage());
			}
		} else if (obj instanceof VariantSpec) {
			VariantSpec vs = (VariantSpec) obj;
			VariantSpecMaster vsMaster = (VariantSpecMaster) vs.getMaster();

			VariantSpecMasterIdentity vsMasterIdentity;
			try {
				vsMasterIdentity = (VariantSpecMasterIdentity) vsMaster.getIdentificationObject();
				if (number != null )
					vsMasterIdentity.setNumber(number);
				vsMaster = (VariantSpecMaster) IdentityHelper.service.changeIdentity(vsMaster, vsMasterIdentity);
			} catch (UniquenessException e) {
				e.printStackTrace();
				throw new WTException(e.getLocalizedMessage());
			}
		}
	}

}
