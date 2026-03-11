package ext.casc.analysisActivity.process;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.analysisActivity.helper.AnalysisUtil;
import wt.change2.WTAnalysisActivity;
import wt.fc.Persistable;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import java.util.List;

public class ExtDeleteAnalysisProcessor extends DefaultObjectFormProcessor {

    /**
     * 方法功能: 删除影响分析并删除关联的条目和外系统反馈数据
     *
     * @author cjh
     * @date 2024/3/28
     */
    @Override
    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> beans) throws WTException {
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        FormResult formResult = new FormResult();
        try {
            WTAnalysisActivity analysisActivity = null;
            Persistable persistable = commandBean.getPrimaryOid().getWtRef().getObject();
            if(persistable instanceof WTAnalysisActivity) {
                analysisActivity = (WTAnalysisActivity) persistable;
            }
            if(analysisActivity != null) {
                AnalysisUtil.deleteAnalysisEntry(analysisActivity.getNumber());
                AnalysisUtil.deleteGwDealProductRecord(analysisActivity.getNumber());
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return formResult;
    }

}
