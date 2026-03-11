package ext.casc.validator;

import java.util.Locale;

import wt.folder.Cabinet;
import wt.folder.Folder;
import wt.folder.SubFolder;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

import ext.casc.constants.Constants;
import ext.casc.util.IBAUtility;

public class CreateProcessEnvelopeValidator extends DefaultSimpleValidationFilter {

    @Override
    public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
        Object object = criteria.getContextObject().getObject();
        
        try {
            WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
            if (object instanceof WTPart) {
                WTPart part = (WTPart) object;
                //批量签审只针对design视图零部件
                String viewName = part.getViewName();
                if ("Manufacturing".equals(viewName)) {
                    return UIValidationStatus.HIDDEN;
                }
                IBAUtility ibaUtility = new IBAUtility(part);
                String partType = ibaUtility.getIBAValue("CTYPE");
                String creatorName = part.getCreatorName();
                String state = part.getState().getState().getDisplay(Locale.CHINA);
                //正在工作、自己设计且非标准件的零部件显示
                if (state.contains(Constants.STATE_ZHENGZAIGONGZUO) && creatorName.equals(currentUser.getName())
                        & !Constants.TYPE_BIAOZHUNJIAN.equals(partType)) {
                    return UIValidationStatus.ENABLED;
                }
            }else if ((object instanceof Cabinet)||(object instanceof SubFolder)||(object instanceof Folder)) {//在文件夹内容列表工具栏上显示
                return UIValidationStatus.ENABLED;
            }
        } catch (WTException e) {
            e.printStackTrace();
        }
        return UIValidationStatus.HIDDEN;
    }

}
