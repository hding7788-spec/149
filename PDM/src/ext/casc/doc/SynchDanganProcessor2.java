package ext.casc.doc;

import java.util.Iterator;
import java.util.List;

import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.fc.collections.WTCollection;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;

import ext.casc.erp.CallWebServiceOME;
import ext.casc.erp.ERPUtil;

/**
 * @describe used to submit approval workFlow process
 * @author Long,XiuChuan
 * @since 2012/7/23
 *
 */
public class SynchDanganProcessor2 extends DefaultObjectFormProcessor {
    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> objectBeans) throws WTException {
        FormResult formresult = super.doOperation(commandBean, objectBeans);
        formresult =new FormResult();
    	Object actionObj = commandBean.getActionOid().getRefObject();
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        List selectedList =commandBean.getSelected();

        try {
        	if(actionObj instanceof WTDocument){
        		WTDocument doc = (WTDocument) actionObj;
        		String file1 = ERPUtil.createCommonDocXml(doc,"1");
                CallWebServiceOME.callWebServiceUploadFile(file1);
                WTCollection coll = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(doc);
                Iterator it = coll.iterator();
                if (it.hasNext()) {
                    WTChangeOrder2 ecn = (WTChangeOrder2) ((ObjectReference) it.next()).getObject();
                    String file2 = ERPUtil.createCommonDocXml(ecn,"1");
                    CallWebServiceOME.callWebServiceUploadFile(file2);
                }
        	}else{
        		ReferenceFactory rf = new ReferenceFactory();
        		for (Object oid : selectedList) {
        			oid = oid.toString().substring(oid.toString().lastIndexOf("$") + 1, oid.toString().length() - 2);
        			Persistable p = rf.getReference(oid.toString()).getObject();
        			 if (p instanceof WTDocument) {//文档提交签审
        	                WTDocument doc = (WTDocument) p;
        	                String file1 = ERPUtil.createCommonDocXml(doc,"1");
        	                CallWebServiceOME.callWebServiceUploadFile(file1);
        	                WTCollection coll = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(doc);
                            Iterator it = coll.iterator();
                            if (it.hasNext()) {
                                WTChangeOrder2 ecn = (WTChangeOrder2) ((ObjectReference) it.next()).getObject();
                                String file2 = ERPUtil.createCommonDocXml(ecn,"1");
                                CallWebServiceOME.callWebServiceUploadFile(file2);
                            }
        	          }
        		}
        	}


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	FeedbackMessage message = new FeedbackMessage();
        	message.addMessage("操作成功");
        	formresult.addFeedbackMessage(message);
        	formresult.setNextAction(FormResultAction.JAVASCRIPT);
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return formresult;
    }

}
