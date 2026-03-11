package ext.casc.doc;

import java.util.ArrayList;
import java.util.List;

import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.type.TypedUtility;
import wt.util.WTException;

import com.glaway.mpm.processplan.ProcessPlanStructure;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;

import ext.casc.util.DeleteProcessDocUtility;
import ext.casc.workflow.WorkflowHelper;

/**
 * @describe used to submit approval workFlow process
 * @author Long,XiuChuan
 * @since 2012/7/23
 *
 */
public class RollupIterationsProcessor extends DefaultObjectFormProcessor {
    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> objectBeans) throws WTException {
        FormResult formresult = super.doOperation(commandBean, objectBeans);
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
        	 ArrayList localArrayList = commandBean.getSelected();
        	 if ((localArrayList != null) && (localArrayList.size() > 0)) {
        	        for (int i = 0; i < localArrayList.size(); i++) {
        	          NmContext localNmContext = (NmContext)localArrayList.get(i);
        	          Persistable localPersistable1 = localNmContext.getTargetOid().getWtRef().getObject();
        	          if (localPersistable1 != null)
        	          {
        	            Object localObject;
        	            if ((localPersistable1 instanceof WTDocument)) {
        	            	WTDocument doc = (WTDocument)localPersistable1;

        	            	QuerySpec qs = new QuerySpec(WTDocument.class);

        	    		    qs.appendWhere(new SearchCondition(WTDocument.class,
        	    		    		WTDocument.NUMBER, SearchCondition.EQUAL, doc.getNumber()),
        	    		     new int[]{0});
        	    		    qs.appendAnd();
        	    		    qs.appendWhere(new SearchCondition(WTDocument.class,WTDocument.LATEST_ITERATION, SearchCondition.IS_TRUE), new int[] { 0 });
        	    		   // qs = new LatestConfigSpec().appendSearchCriteria(qs);

        	    		    QueryResult qr = PersistenceHelper.manager.find(qs);
        	    		    while(qr.hasMoreElements()){
        	    		    	WTDocument document = (WTDocument)qr.nextElement();
        	    		    	String typeName = TypedUtility.getTypeIdentifier(document).getTypename();
    	    	                if(typeName.contains("PROCESS_PLAN")){
    	    	                	String state = document.getState().getState().toString();
                	            	if("APPROVED".equals(state)){//只清空已受控
                	            		DeleteProcessDocUtility.deleteIterations(document);
                	            	}
    	    	                }else{
    	    	                	DeleteProcessDocUtility.deleteIterations(document);
    	    	                }

        	    	            //PersistenceHelper.manager.delete(document);
        	    		    }
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
            formresult.setNextAction(FormResultAction.NONE);
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return formresult;
    }

}
