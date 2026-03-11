package ext.casc.analysisActivity.process;

import cn.hutool.core.util.StrUtil;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.analysisActivity.bean.AnalysisObjEntry;
import ext.casc.analysisActivity.helper.AnalysisConstant;
import ext.casc.analysisActivity.helper.AnalysisUtil;
import ext.casc.changeRequest.Change2WorkflowHelper;
import ext.casc.util.WTUserUtil;
import ext.sast.common.fc.CmPersistenceHelper;
import wt.change2.WTAnalysisActivity;
import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.workflow.work.WorkItem;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExtAddRelatedProductProcessor extends DefaultObjectFormProcessor {

    /**
     * 方法功能: 添加受影响制品
     *
     * @author cjh
     * @date 2024/6/10
     */
//    @Override
    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> beans) throws WTException {
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);

        FormResult formResult = new FormResult();
        try {
            WTAnalysisActivity analysisActivity = null;
            Persistable persistable = commandBean.getPrimaryOid().getWtRef().getObject();
            if(persistable instanceof WorkItem) {
                WorkItem workItem = (WorkItem) persistable;
                analysisActivity = (WTAnalysisActivity) workItem.getPrimaryBusinessObject().getObject();
            } else if(persistable instanceof WTAnalysisActivity) {
                analysisActivity = (WTAnalysisActivity) persistable;
            }
            HashMap text = commandBean.getText();
            HashMap comboBox = commandBean.getComboBox();
            HashMap textArea = commandBean.getTextArea();
            String vrOid = (String) text.get("number_value");
            String zzp = (String) ((ArrayList) comboBox.get("zaizhipin")).get(0);
            String yzp = (String) ((ArrayList) comboBox.get("yizhipin")).get(0);
            String gongyiyuan = (String) text.get("_responser_value_product");
            String jidiaoyuan = (String) text.get("_jidiaoyuan_value_product");
            String requirement = (String) textArea.get("requirement");
            String completeTime = (String) text.get("completeTime_value");
            Vector<Persistable> vector = new Vector();
            ReferenceFactory rf = new ReferenceFactory();
            WTPart part = (WTPart) rf.getReference(vrOid).getObject();
            String partId = WTPart.class.getName() + ":" + part.getBranchIdentifier();
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
            //检查是否已经在单据中
            AnalysisObjEntry zaizhipin = AnalysisUtil.getAnalysisObjEntry(analysisActivity.getNumber(), partId, AnalysisConstant.TYPE_ZAIZHIPIN);
            AnalysisObjEntry yizhipin = AnalysisUtil.getAnalysisObjEntry(analysisActivity.getNumber(), partId, AnalysisConstant.TYPE_YIZHIPIN);
            if(zaizhipin != null || yizhipin != null) {
                formResult.setStatus(FormProcessingStatus.FAILURE);
                FeedbackMessage message = new FeedbackMessage(FeedbackType.FAILURE, null, null, null, "部件" + part.getNumber() + "已经在此单据的制品列表中，不允许添加！");
                formResult.addFeedbackMessage(message);
                return formResult;
            }
            //制品
            AnalysisObjEntry zproduct = new AnalysisObjEntry(analysisActivity.getNumber(), partId, AnalysisConstant.TYPE_ZAIZHIPIN);
            AnalysisObjEntry yproduct = new AnalysisObjEntry(analysisActivity.getNumber(), partId, AnalysisConstant.TYPE_YIZHIPIN);
            zproduct.setProduct(zzp);
            zproduct.setResponser(gongyiyuan);
            zproduct.setRequirement(requirement);
            zproduct.setCompleteTime(completeTime);
            yproduct.setProduct(yzp);
            yproduct.setResponser(jidiaoyuan);
            yproduct.setRequirement(requirement);
            yproduct.setCompleteTime(completeTime);
            if(AnalysisConstant.ANALYSIS_AFFECTED_NO.equals(zzp) && AnalysisConstant.ANALYSIS_AFFECTED_NO.equals(yzp)){
                zproduct.setDealStatus(AnalysisConstant.DEAL_STATUS_FINISH);
                yproduct.setDealStatus(AnalysisConstant.DEAL_STATUS_FINISH);
            }
            if(!AnalysisConstant.DEAL_STATUS_FINISH.equals(zproduct.getDealStatus())){
                //设置条目下发时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
                String date = sdf.format(new Date());
                Timestamp timestamp = Timestamp.valueOf(date);
                zproduct.setTaskTime(timestamp);
                //设置责任部门
                String responser = zproduct.getResponser();
                if(StrUtil.isNotEmpty(responser)){
                    WTUser user = (WTUser) rf.getReference(responser).getObject();
                    String dept = WTUserUtil.getDeptShortName(user);
                    zproduct.setUnit(dept);
                }
            }
            if(!AnalysisConstant.DEAL_STATUS_FINISH.equals(yproduct.getDealStatus())){
                //设置条目下发时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
                String date = sdf.format(new Date());
                Timestamp timestamp = Timestamp.valueOf(date);
                yproduct.setTaskTime(timestamp);
                //设置责任部门
                String responser = yproduct.getResponser();
                if(StrUtil.isNotEmpty(responser)){
                    WTUser user = (WTUser) rf.getReference(responser).getObject();
                    String dept = WTUserUtil.getDeptShortName(user);
                    yproduct.setUnit(dept);
                }
            }
            CmPersistenceHelper.manager.save(zproduct);
            CmPersistenceHelper.manager.save(yproduct);
            //根据影响分析条目向NC发送制品影响分析结果
            Change2WorkflowHelper.sendAnalysisResult(analysisActivity);
            formResult.setStatus(FormProcessingStatus.SUCCESS);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return formResult;
    }

}
