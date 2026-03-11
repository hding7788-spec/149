package ext.casc.doc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import wt.util.WTException;
import wt.util.WTProperties;

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
public class DownloadSearchDocProcessor extends DefaultObjectFormProcessor {
    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> objectBeans) throws WTException {
        FormResult formresult = super.doOperation(commandBean, objectBeans);
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        String fileNameTemp = UUID.randomUUID().toString();
        try {
        	 ArrayList localArrayList = commandBean.getSelected();
        	 ArrayList doclist =new ArrayList();;
        	 if ((localArrayList != null) && (localArrayList.size() > 0)) {
        	        for (int i = 0; i < localArrayList.size(); i++) {
        	          NmContext localNmContext = (NmContext)localArrayList.get(i);
        	          Persistable localPersistable1 = localNmContext.getTargetOid().getWtRef().getObject();
        	          if (localPersistable1 != null)
        	          {
        	            Object localObject;
        	            if ((localPersistable1 instanceof WTDocument)) {
        	            	WTDocument doc = (WTDocument)localPersistable1;
        	            	doclist.add(doc);
        	            }
        	          }
        	        }

        	  }

        	ext.casc.search.SearchUtil.getDownLoadPrintFileName(doclist,fileNameTemp);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FeedbackMessage message = new FeedbackMessage();
            message.addMessage("操作成功");
            formresult.addFeedbackMessage(message);
            String url = "/netmarkets/jsp/ext/ases/document/downloadSKDocPrintFile.jsp?fileNameTemp="+fileNameTemp;
            String strCodeBase = "";
    		try {
    			strCodeBase = WTProperties.getLocalProperties().getProperty(
    					"wt.server.codebase", null);
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
   		    String baseUrl = strCodeBase + url;
   		    formresult.setJavascript("window.open(\"" + baseUrl + "\",\"_blank\")");
            formresult.setNextAction(FormResultAction.JAVASCRIPT);
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return formresult;
    }

}
