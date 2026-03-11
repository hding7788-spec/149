package ext.casc.part;


import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;
import com.ptc.windchill.enterprise.part.forms.PartDocRelationDefaultObjectFormProcessor;
import ext.casc.util.IBAHelper;
import ext.casc.util.WCUtil;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.struct.StructHelper;

import java.util.ArrayList;
import java.util.List;

public class PartDocDescDeleteRelationFormProcessor extends PartDocRelationDefaultObjectFormProcessor {
    private static final String RESOURCE = "com.ptc.netmarkets.util.utilResource";

    public FormResult doOperation(NmCommandBean paramNmCommandBean, List<ObjectBean> paramList)
            throws WTException {
        Persistable localPersistable = paramNmCommandBean.getPrimaryOid().getWtRef().getObject();
        FormResult localFormResult = new FormResult();
        localFormResult.setStatus(FormProcessingStatus.SUCCESS);
        Object localObject1;
        Object localObject2;
        ArrayList arrayList = new ArrayList();
        if((localPersistable instanceof WTPart)) {
            localObject1 = (WTPart) localPersistable;
            ArrayList localArrayList = paramNmCommandBean.getSelected();
            if((localArrayList != null) && (localArrayList.size() > 0)) {
                WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
                if(!WCUtil.isAdmin()) {
                    for(int i = 0; i < localArrayList.size(); i++) {
                        NmContext localNmContext = (NmContext) localArrayList.get(i);
                        Persistable localPersistable1 = localNmContext.getTargetOid().getWtRef().getObject();
                        if(localPersistable1 != null && localPersistable1 instanceof WTDocument) {
                            WTDocument document = (WTDocument) localPersistable1;
                            String pplantype = IBAHelper.getIBAStringValue(document, "PPLANTYPE");
                            if(!"临时工艺文件".equals(pplantype)){
                                localFormResult.setStatus(FormProcessingStatus.SUCCESS);
                                FeedbackMessage feedbackMessage = new FeedbackMessage();
                                feedbackMessage.addMessage("只能删除与临时工艺文件的关联关系!");
                                localFormResult.addFeedbackMessage(feedbackMessage);
                                return localFormResult;
                            }
                            WTUser mofifier = (WTUser) document.getModifier().getObject();
                            if(mofifier.getFullName() != currentUser.getFullName()) {
                                localFormResult.setStatus(FormProcessingStatus.SUCCESS);
                                FeedbackMessage feedbackMessage = new FeedbackMessage();
                                feedbackMessage.addMessage("您不是该工艺的修改者，没有删除关联权限!");
                                localFormResult.addFeedbackMessage(feedbackMessage);
                                return localFormResult;
                            }
                        }
                    }
                }

                for(int i = 0; i < localArrayList.size(); i++) {
                    NmContext localNmContext = (NmContext) localArrayList.get(i);
                    Persistable localPersistable1 = localNmContext.getTargetOid().getWtRef().getObject();
                    if(localPersistable1 != null) {
                        if((localPersistable1 instanceof WTDocument)) {
                            arrayList.add(localPersistable1);
                            localObject2 = StructHelper.service.navigateDescribedBy((WTPart) localObject1, WTPartDescribeLink.class, false);
                            if(((QueryResult) localObject2).size() > 0)
                                while(((QueryResult) localObject2).hasMoreElements()) {
                                    WTPartDescribeLink link = (WTPartDescribeLink) ((QueryResult) localObject2).nextElement();
                                    if(PersistenceHelper.isEquivalent((Persistable) localPersistable1, ((WTPartDescribeLink) link).getDescribedBy())) {
                                        PersistenceServerHelper.manager.remove(link);
                                    }
                                }
                        }

                    }
                }
            }
        }
        return localFormResult;
    }
}
