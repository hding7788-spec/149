package ext.casc.change.process;

import cn.hutool.core.util.StrUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.change2.forms.processors.CreateChangeNoticeFormProcessor;
import ext.casc.analysisActivity.bean.AnalysisObjEntry;
import ext.casc.analysisActivity.helper.AnalysisConstant;
import ext.casc.analysisActivity.helper.AnalysisUtil;
import ext.casc.changeRequest.Change2Util;
import ext.casc.util.IBAHelper;
import ext.sast.common.fc.CmPersistenceHelper;
import wt.change2.AddressedBy2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.fc.PersistenceHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import java.util.List;

public class ExtCreateChangeOrderProcessor extends CreateChangeNoticeFormProcessor {

    /**
     * 方法功能: 创建更改单
     *
     * @author cjh
     * @date 2024/6/28
     */
    @Override
    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> beans) throws WTException {
        FormResult formResult = super.doOperation(commandBean, beans);
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            if(formResult.getStatus() == FormProcessingStatus.SUCCESS){
                WTChangeOrder2 changeOrder2 = null;
                if(beans.size() > 0) {
                    changeOrder2 = (WTChangeOrder2) beans.get(0).getObject();
                }
                if(changeOrder2 != null) {
                    //更改影响分析工艺关联更改单
                    String docId = commandBean.getTextParameter("docId");
                    String analysisNumber = commandBean.getTextParameter("analysisNumber");
                    if(StrUtil.isNotEmpty(docId) && StrUtil.isNotEmpty(analysisNumber)) {
                        AnalysisObjEntry entry = AnalysisUtil.getAnalysisObjEntry(analysisNumber, docId, AnalysisConstant.TYPE_TECHNICS);
                        if(entry != null) {
                            entry.setRelatedOrder(changeOrder2.getNumber());
                            CmPersistenceHelper.manager.update(entry);
                        }
                        IBAHelper.setIBAStringValue(changeOrder2, "ANALYSISNUMBER", analysisNumber + "@!@" + docId);
                    }

                    //新建工艺更改单，根据工艺更改申请编号关联到更改单
                    String ecrNumber = IBAHelper.getIBAStringValue(changeOrder2, "ECRNUMBER");
                    if(StrUtil.isNotEmpty(ecrNumber)) {
                        WTChangeRequest2 request2 = Change2Util.getWTChangeRequest2ByNumber(ecrNumber);
                        if(request2 != null) {
                            AddressedBy2 addressed = AddressedBy2.newAddressedBy2(request2, changeOrder2);
                            PersistenceHelper.manager.save(addressed);
                        }
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return formResult;
    }

}
