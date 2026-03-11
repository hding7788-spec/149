package ext.sast.synergy.mvc.command;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.sast.center.record.GWMQRecordService;
import wt.util.WTException;

import java.util.ArrayList;
import java.util.List;

public class SerchSyengyRecordCommand extends DefaultObjectFormProcessor {

    @Override
    public FormResult doOperation(NmCommandBean nmCommandBean, List<ObjectBean> list) throws WTException {
        System.out.println("====SerchSyengyRecordCommand=====");
        ArrayList msgIdList = nmCommandBean.getNmOidSelected();
        for(Object msgId : msgIdList){
            System.out.println("====msgId=====" + msgId);
            GWMQRecordService.updateStateByMsgId(String.valueOf(msgId), "已完成");
        }
        return super.doOperation(nmCommandBean, list);
    }
}
