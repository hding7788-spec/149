package ext.casc.dfmRule.sign.processor;

import java.rmi.RemoteException;
import java.util.Locale;

import wt.doc.WTDocument;
import wt.fc.WTReference;
import wt.org.OrganizationServicesHelper;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.workflow.work.WorkItem;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

import ext.casc.dfmRule.common.Constants;

/**
 * @author xuetao
 * @Description : 签审按钮过滤器
 */
public class SubmitDataReviewFilter extends DefaultSimpleValidationFilter {

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key,
			UIValidationCriteria criteria) {
		boolean isHasLimit = false;
		Object object = criteria.getContextObject().getObject();
		if (object instanceof WTDocument) {
			WTDocument document = (WTDocument) object; // 获取当前对象
			String state = document.getState().getState().getDisplay(Locale.CHINA);// 获取对象的状态，中文显示
			if (Constants.LIFECYCLESTETE_INWORK.equals(state)) {
				return UIValidationStatus.ENABLED;
			} else {
				return UIValidationStatus.DISABLED; // 灰色不可见状态；
			}
		}
		return UIValidationStatus.DISABLED;
	}

}
