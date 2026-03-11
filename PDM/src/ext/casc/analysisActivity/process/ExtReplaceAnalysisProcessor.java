package ext.casc.analysisActivity.process;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmActionServiceHelper;
import com.ptc.netmarkets.util.misc.NmContext;
import ext.ases.envelope.ProcessEnvelope;
import ext.ases.envelope.ProcessEnvelopeUtil;
import ext.casc.analysisActivity.bean.AnalysisToSourceLink;
import wt.change2.WTAnalysisActivity;
import wt.doc.WTDocument;
import wt.fc.*;
import wt.httpgw.URLFactory;
import wt.session.SessionServerHelper;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.work.WorkItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExtReplaceAnalysisProcessor extends DefaultObjectFormProcessor {

    /**
     * 方法功能: 更改影响分析手动替换影响源
     *
     * @author cjh
     * @date 2024/8/8
     */
//    @Override
    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> beans) throws WTException {
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);

        FormResult formResult = new FormResult();
        FeedbackMessage message = new FeedbackMessage();
        try {
            WTAnalysisActivity analysisActivity = null;
            ArrayList list = commandBean.getSelected();
            Persistable persistable = commandBean.getPrimaryOid().getWtRef().getObject();
            if(persistable instanceof WorkItem) {
                WorkItem workItem = (WorkItem) persistable;
                analysisActivity = (WTAnalysisActivity) workItem.getPrimaryBusinessObject().getObject();
            } else if(persistable instanceof WTAnalysisActivity) {
                analysisActivity = (WTAnalysisActivity) persistable;
            }
            if(analysisActivity != null) {
                //现有的这个必须关联影响源
                QueryResult links = PersistenceHelper.manager.navigate(analysisActivity, "sourceObject", AnalysisToSourceLink.class, false);
                if(links.size() == 0){
                    formResult.setStatus(FormProcessingStatus.FAILURE);
                    message.addMessage("影响分析没有关联影响源，无法替换！");
                    formResult.addFeedbackMessage(message);
                    return formResult;
                }
                for(Object oj : list) {
                    NmContext nmContext = (NmContext) oj;
                    Persistable object = nmContext.getTargetOid().getWtRef().getObject();
                    if(object instanceof WTAnalysisActivity) {
                        //选择的影响分析不能关联影响源
                        WTAnalysisActivity activity = (WTAnalysisActivity) object;
                        QueryResult old = PersistenceHelper.manager.navigate(activity, "sourceObject", AnalysisToSourceLink.class, false);
                        if(old.size() > 0 || analysisActivity.getNumber().equals(activity.getNumber())) {
                            formResult.setStatus(FormProcessingStatus.FAILURE);
                            message.addMessage("所选影响分析已经关联影响源，无法替换！");
                            formResult.addFeedbackMessage(message);
                            return formResult;
                        }

                        //删除现有的关联关系
                        WTObject obj = null;
                        while(links.hasMoreElements()){
                            AnalysisToSourceLink link = (AnalysisToSourceLink) links.nextElement();
                            obj = link.getSourceObject();
                            PersistenceHelper.manager.delete(link);
                        }

                        //将现有的影响源与所选影响分析单建立关联
                        if(obj != null) {
                            AnalysisToSourceLink link = AnalysisToSourceLink.newAnalysisToSourceLink(activity, obj);
                            if(obj instanceof ProcessEnvelope) {
                                ProcessEnvelope pe = (ProcessEnvelope) obj;
                                ArrayList members = ProcessEnvelopeUtil.getAllMembers(pe);
                                for(Object member : members) {
                                    if(member instanceof WTDocument) {
                                        WTDocument document = (WTDocument) member;
                                        TypeIdentifier identifier = TypedUtility.getTypeIdentifier(document);
                                        String typeName = identifier.getTypename();
                                        if(typeName.contains("casc.sast.149.TECHNOTICE_DOC")) {
                                            link.setPlNumber(document.getNumber());
                                            break;
                                        }
                                    }
                                }
                            }
                            PersistenceHelper.manager.save(link);
                        }

                        //重定向
                        formResult.setNextAction(FormResultAction.LOAD_OPENER_URL);
                        formResult.setURL(getURL(activity));
                    }
                }
                //删除现有的影响分析单，终止流程
                WfEngineHelper.service.terminateObjectsRunningWorkflows(analysisActivity);
                PersistenceHelper.manager.delete(analysisActivity);
            }
            formResult.setStatus(FormProcessingStatus.SUCCESS);
            message.addMessage("替换影响源成功！");
            formResult.addFeedbackMessage(message);
            return formResult;
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return formResult;
    }

    protected String getURL(WTAnalysisActivity activity) throws WTException {
        ReferenceFactory rf = new ReferenceFactory();
        String referenceString = rf.getReferenceString(activity);
        URLFactory localURLFactory = new URLFactory();
        HashMap localHashMap = new HashMap();
        localHashMap.put("oid", referenceString);
        String url = NmActionServiceHelper.service.getAction("object", "view").getUrl();
        return localURLFactory.getHREF(url, localHashMap, true);
    }

}
