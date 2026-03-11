package ext.casc.analysisActivity.process;

import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;
import ext.casc.analysisActivity.bean.AnalysisObjEntry;
import ext.casc.analysisActivity.helper.AnalysisConstant;
import ext.casc.analysisActivity.helper.AnalysisUtil;
import ext.casc.changeRequest.Change2WorkflowHelper;
import ext.casc.common.PartCommonHelper;
import ext.casc.constants.Constants;
import ext.casc.util.WTUserUtil;
import ext.sast.common.fc.CmPersistenceHelper;
import wt.change2.WTAnalysisActivity;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.workflow.work.WorkItem;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExtAddRelatedPbomProcessor extends DefaultObjectFormProcessor {

    /**
     * 方法功能: 添加受影响对象
     *
     * @author cjh
     * @date 2024/3/28
     */
//    @Override
    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> beans) throws WTException {
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);

        FormResult formResult = new FormResult();
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
            Vector<Persistable> vector = new Vector();
            ReferenceFactory rf = new ReferenceFactory();
            if(analysisActivity != null) {
                for(Object o : list) {
                    NmContext nmContext = (NmContext) o;
                    Persistable object = nmContext.getTargetOid().getWtRef().getObject();
                    WTPart part = null;
                    if(object instanceof WTPart) {
                        part = (WTPart) object;
                        //存在PBOM不允许添加EBOM
                        if("Design".equals(part.getViewName())) {
                            WTPart manufacturing = WTPartUtil.getLatestPartByNumberAndView(part, "Manufacturing");
                            if(manufacturing != null) {
                                formResult.setStatus(FormProcessingStatus.FAILURE);
                                FeedbackMessage message = new FeedbackMessage(FeedbackType.FAILURE, null, null, null, "部件" + part.getNumber() + "存在Manufacturing视图，不允许选择Design视图。");
                                formResult.addFeedbackMessage(message);
                                return formResult;
                            }
                        }
                        //自动带入部件关联的正式工艺
                        List<WTDocument> documentList = PartCommonHelper.getDescribedByWTDocuments(part);
                        for(WTDocument document : documentList) {
                            String verOid = rf.getReference(document).toString();
                            verOid = verOid.replaceAll(">", ":");
                            AnalysisObjEntry entry = AnalysisUtil.getAnalysisObjEntry(analysisActivity.getNumber(), verOid, AnalysisConstant.TYPE_TECHNICS);
                            if(entry == null){
                                String pplantype = IBAHelper.getIBAValue(document, "PPLANTYPE");
                                if("正式工艺文件".equals(pplantype)) {
                                    vector.add(document);
                                }
                            }
                        }
                    } else if(object instanceof WTDocument) {
                        WTDocument document = (WTDocument)object;
                        String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(document);
                        if (!docType.contains("WCTYPE|wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN")
                            && !docType.contains("casc.sast.149.GONGYIZONGFANGAN")
                            && !docType.contains("casc.sast.149.GONGYIFENFANGAN")
                            && !docType.contains("casc.sast.149.GONGYIFENXICEHUAZONGJIE")
                            && !docType.contains("casc.sast.149.GONGYIDINGXING")
                            && !docType.contains("casc.sast.149.GONGYIJIANDING")
                            && !docType.contains("casc.sast.149.JISHUKETI")
                            && !docType.contains("casc.sast.149.TEST_REPORT")
                            && !docType.contains("casc.sast.149.TECHNOLOGY_AGREEMENT")){
                            formResult.setStatus(FormProcessingStatus.FAILURE);
                            FeedbackMessage message = new FeedbackMessage(FeedbackType.FAILURE, null, null, null, "文档" + document.getNumber() + "不在影响分析可选择的文档范围中，请重新选择！");
                            formResult.addFeedbackMessage(message);
                            return formResult;
                        }
                        String type = commandBean.getTextParameter("type");
                        if("2".equals(type) && !Constants.STATE_YIPIZHUN.equals(document.getState().getState().getDisplay(Locale.CHINA))) {
                            formResult.setStatus(FormProcessingStatus.FAILURE);
                            FeedbackMessage message = new FeedbackMessage(FeedbackType.FAILURE, null, null, null, "文档" + document.getNumber() + "未受控，请重新选择！");
                            formResult.addFeedbackMessage(message);
                            return formResult;
                        }
                    }
                    vector.add(object);
                }
            }
            for(Persistable p : vector) {
                String verOid = rf.getReference(p).toString();
                verOid = verOid.replaceAll(">", ":");
                if(p instanceof WTPart){
                    AnalysisObjEntry entry = AnalysisUtil.getAnalysisObjEntry(analysisActivity.getNumber(), verOid, AnalysisConstant.TYPE_PBOM);
                    if(entry != null) {
                        formResult.setStatus(FormProcessingStatus.FAILURE);
                        FeedbackMessage message = new FeedbackMessage(FeedbackType.FAILURE, null, null, null, "部件" + ((WTPart)p).getNumber() + "已经在此单据内完成过更改，不允许添加！");
                        formResult.addFeedbackMessage(message);
                        return formResult;
                    }
                    //pbom
                    AnalysisObjEntry pbom = new AnalysisObjEntry(analysisActivity.getNumber(), verOid, AnalysisConstant.TYPE_PBOM);
                    CmPersistenceHelper.manager.save(pbom);
                    //更改影响分析活动才创建制品条目
                    String type = commandBean.getTextParameter("type");
                    if("1".equals(type)) {
                        //制品
                        AnalysisObjEntry zproduct = new AnalysisObjEntry(analysisActivity.getNumber(), verOid, AnalysisConstant.TYPE_ZAIZHIPIN);
                        CmPersistenceHelper.manager.save(zproduct);
                        AnalysisObjEntry yproduct = new AnalysisObjEntry(analysisActivity.getNumber(), verOid, AnalysisConstant.TYPE_YIZHIPIN);
                        CmPersistenceHelper.manager.save(yproduct);
                    }
                } else if(p instanceof WTDocument) {
                    AnalysisObjEntry entry = AnalysisUtil.getAnalysisObjEntry(analysisActivity.getNumber(), verOid, AnalysisConstant.TYPE_TECHNICS);
                    if(entry != null) {
                        formResult.setStatus(FormProcessingStatus.FAILURE);
                        FeedbackMessage message = new FeedbackMessage(FeedbackType.FAILURE, null, null, null, "工艺" + ((WTDocument)p).getNumber() + "已经在此单据内完成过更改，不允许添加！");
                        formResult.addFeedbackMessage(message);
                        return formResult;
                    }
                    //工艺
                    AnalysisObjEntry technics = new AnalysisObjEntry(analysisActivity.getNumber(), verOid, AnalysisConstant.TYPE_TECHNICS);
                    //执行更改环节设置负责人为当前用户 主任工艺师和工艺员意见为有影响
                    String type = commandBean.getTextParameter("type");
                    if("2".equals(type)) {
                        WTUser user = (WTUser) SessionHelper.getPrincipal();
                        String dept = WTUserUtil.getDeptShortName(user);
                        //设置条目下发时间
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
                        String date = sdf.format(new Date());
                        Timestamp timestamp = Timestamp.valueOf(date);
                        technics.setResponser(rf.getReferenceString(user));
                        technics.setAffected(AnalysisConstant.ANALYSIS_AFFECTED_HAS);
                        technics.setAffectedGyy(AnalysisConstant.ANALYSIS_AFFECTED_HAS);
                        technics.setUnit(dept);
                        technics.setTaskTime(timestamp);
                    }
                    CmPersistenceHelper.manager.save(technics);
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
