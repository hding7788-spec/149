package ext.casc.change.process;

import cn.hutool.core.util.StrUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.change2.forms.processors.EditChangeNoticeFormProcessor;
import ext.casc.changeRequest.Change2Util;
import ext.casc.util.IBAHelper;
import wt.change2.AddressedBy2;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import java.util.List;

public class ExtEditChangeOrderProcessor extends EditChangeNoticeFormProcessor {

    /**
     * 方法功能: 编辑更改单
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
                    String ecrNumber = IBAHelper.getIBAStringValue(changeOrder2, "ECRNUMBER");
                    if(StrUtil.isNotEmpty(ecrNumber)) {
                        WTChangeRequest2 request2 = Change2Util.getWTChangeRequest2ByNumber(ecrNumber);
                        if(request2 != null) {
                            boolean isHas = false;
                            QueryResult qr = ChangeHelper2.service.getChangeRequest(changeOrder2);
                            while(qr.hasMoreElements()) {
                                Object o = qr.nextElement();
                                if(o instanceof WTChangeRequest2) {
                                    WTChangeRequest2 changeRequest2 = (WTChangeRequest2) o;
                                    if(changeRequest2.getNumber().equals(request2.getNumber())) {
                                        isHas = true;
                                    }
                                }
                            }
                            if(!isHas){
                                AddressedBy2 addressed = AddressedBy2.newAddressedBy2(request2, changeOrder2);
                                PersistenceHelper.manager.save(addressed);
                            }
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
