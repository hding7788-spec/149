/**
 *
 */
package ext.casc.util;

import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import ext.ases.part.ASESHuiqianSignature;
import ext.ases.part.SignLink;

/**
 * @author hding
 *
 */
public class ProcessSign {
	public static void process(String activityOid,String advise,String result,String message,String oid){
            WTObject obj;
			try {
				obj = (WTObject)WCUtil.getPersistable(oid);
				ASESHuiqianSignature tempSign = new ASESHuiqianSignature();
	            tempSign.setActivity(activityOid);
	            tempSign.setOpinion(advise);
	            tempSign.setConclusion(result);
	            tempSign.setSignature(message);
	            tempSign = (ASESHuiqianSignature) PersistenceHelper.manager.save(tempSign);
	            SignLink sl = SignLink.newSignLink(obj, tempSign);
	            PersistenceHelper.manager.save(sl);
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WTPropertyVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}
}
