package ext.casc.analysisActivity.process;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;
import ext.casc.process.ProcessTaskItem;
import ext.casc.util.IBAHelper;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExtRelatedTechnicsForTaskProcessor extends DefaultObjectFormProcessor {

    /**
     * 方法功能: 为临时工艺任务关联受控临时工艺
     *
     * @author cjh
     * @date 2024/12/5
     */
    @Override
    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> beans) throws WTException {
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);

        FormResult formResult = new FormResult();
        try {
            ArrayList list = commandBean.getSelected();
            if(list.size() > 1) {
                formResult.setStatus(FormProcessingStatus.FAILURE);
                FeedbackMessage message = new FeedbackMessage(FeedbackType.FAILURE, null, null, null, "只允许选择一个工艺进行关联！");
                formResult.addFeedbackMessage(message);
                return formResult;
            }else if(list.size() == 1){
                Object o = list.get(0);
                NmContext nmContext = (NmContext) o;
                Persistable object = nmContext.getTargetOid().getWtRef().getObject();
                Persistable persistable = commandBean.getPrimaryOid().getWtRef().getObject();
                if(persistable instanceof ProcessTaskItem) {
                    ProcessTaskItem taskItem = (ProcessTaskItem) persistable;
                    if(object instanceof WTDocument) {
                        WTDocument document = (WTDocument) object;
                        String pplantype = IBAHelper.getIBAStringValue(document,"PPLANTYPE");
                        if(!"已批准".equals(document.getState().getState().getDisplay(Locale.CHINA)) || !"临时工艺文件".equals(pplantype)){
                            formResult.setStatus(FormProcessingStatus.FAILURE);
                            FeedbackMessage message = new FeedbackMessage(FeedbackType.FAILURE, null, null, null, "只能关联已批准临时工艺文件！");
                            formResult.addFeedbackMessage(message);
                            return formResult;
                        }
                        IBAHelper.setIBAStringValue(taskItem, "PROCESSDOCNUM", document.getNumber());
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
