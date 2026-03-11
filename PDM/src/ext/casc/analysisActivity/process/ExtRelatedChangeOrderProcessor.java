package ext.casc.analysisActivity.process;

import cn.hutool.core.util.StrUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;
import ext.casc.analysisActivity.bean.AnalysisObjEntry;
import ext.casc.analysisActivity.helper.AnalysisConstant;
import ext.casc.analysisActivity.helper.AnalysisUtil;
import ext.casc.util.IBAHelper;
import ext.sast.common.fc.CmPersistenceHelper;
import wt.change2.WTAnalysisActivity;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.workflow.work.WorkItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExtRelatedChangeOrderProcessor extends DefaultObjectFormProcessor {

    /**
     * 方法功能: 关联工艺更改单
     *
     * @author cjh
     * @date 2024/6/28
     */
    @Override
    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> beans) throws WTException {
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);

        FormResult formResult = new FormResult();
        try {
            ArrayList list = commandBean.getSelected();
            if(list.size() > 1) {
                formResult.setStatus(FormProcessingStatus.FAILURE);
                FeedbackMessage message = new FeedbackMessage(FeedbackType.FAILURE, null, null, null, "只允许选择一个工艺更改单/工艺进行关联！");
                formResult.addFeedbackMessage(message);
                return formResult;
            }else if(list.size() == 1){
                Object o = list.get(0);
                NmContext nmContext = (NmContext) o;
                Persistable object = nmContext.getTargetOid().getWtRef().getObject();
                WTAnalysisActivity analysisActivity = null;
                Persistable persistable = commandBean.getPrimaryOid().getWtRef().getObject();
                String docId = commandBean.getTextParameter("docId");
                if(persistable instanceof WorkItem) {
                    WorkItem workItem = (WorkItem) persistable;
                    analysisActivity = (WTAnalysisActivity) workItem.getPrimaryBusinessObject().getObject();
                } else if(persistable instanceof WTAnalysisActivity) {
                    analysisActivity = (WTAnalysisActivity) persistable;
                }
                if(analysisActivity != null && StrUtil.isNotEmpty(docId)) {
                    if(object instanceof WTChangeOrder2) {
                        WTChangeOrder2 changeOrder2 = (WTChangeOrder2) object;
                        String state = changeOrder2.getState().getState().getDisplay(Locale.CHINA);
                        ReferenceFactory rf = new ReferenceFactory();

                        AnalysisObjEntry entry = AnalysisUtil.getAnalysisObjEntry(analysisActivity.getNumber(), docId, AnalysisConstant.TYPE_TECHNICS);
                        if(entry != null) {
                            entry.setRelatedOrder(changeOrder2.getNumber());
                            if("已批准".equals(state)) {
                                entry.setDealStatus(AnalysisConstant.DEAL_STATUS_FINISH);
                            }
                            CmPersistenceHelper.manager.update(entry);
                        }
                        IBAHelper.setIBAStringValue(changeOrder2, "ANALYSISNUMBER", analysisActivity.getNumber() + "@!@" + docId);

//                        boolean isOk = false;
//                        校验受影响工艺与更改单更改前对象是否是同一份工艺
//                        QueryResult result = ChangeHelper2.service.getChangeablesBefore(changeOrder2);
//                        while(result.hasMoreElements()) {
//                            Object obj = result.nextElement();
//                            if(obj instanceof WTDocument) {
//                                WTDocument doc = (WTDocument) obj;
//                                String changeDocId = rf.getReference(doc).toString().replaceAll(">", ":");
//                                if(docId.equals(changeDocId)){
//                                    isOk = true;
//                                    break;
//                                }
//                            }
//                        }
//                        if(isOk) {
//
//                        }else {
//                            formResult.setStatus(FormProcessingStatus.FAILURE);
//                            FeedbackMessage message = new FeedbackMessage(FeedbackType.FAILURE, null, null, null, "请选择更改前对象为受影响工艺且大版本一致的工艺更改单！");
//                            formResult.addFeedbackMessage(message);
//                            return formResult;
//                        }
                    } else if(object instanceof WTDocument) {
                        WTDocument document = (WTDocument) object;
                        String pplantype = IBAHelper.getIBAStringValue(document,"PPLANTYPE");
                        if(!"已批准".equals(document.getState().getState().getDisplay(Locale.CHINA)) || !"临时工艺文件".equals(pplantype)){
                            formResult.setStatus(FormProcessingStatus.FAILURE);
                            FeedbackMessage message = new FeedbackMessage(FeedbackType.FAILURE, null, null, null, "只能关联已批准工艺文件！");
                            formResult.addFeedbackMessage(message);
                            return formResult;
                        }
                        AnalysisObjEntry entry = AnalysisUtil.getAnalysisObjEntry(analysisActivity.getNumber(), docId, AnalysisConstant.TYPE_TECHNICS);
                        if(entry != null) {
                            entry.setRelatedOrder(document.getNumber());
                            entry.setDealStatus(AnalysisConstant.DEAL_STATUS_FINISH);
                            CmPersistenceHelper.manager.update(entry);
                        }
                    }
                }
            }
            formResult.setStatus(FormProcessingStatus.SUCCESS);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return formResult;
    }

}
