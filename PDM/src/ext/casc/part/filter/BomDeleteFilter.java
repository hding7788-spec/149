package ext.casc.part.filter;


import org.apache.log4j.Logger;

import com.ibm.icu.text.MessageFormat;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

import wt.fc.Persistable;
import wt.fc.WTReference;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;

/**
 * 部件操作菜单中BOM删除功能 ，只有修改者可以启动BOM删除功能
 * 
 * @author Liluwen
 * @date 2025年8月14日上午10:15:10
 */
public class BomDeleteFilter extends DefaultSimpleValidationFilter {
	private static Logger LOGGER = Logger.getLogger(BomDeleteFilter.class);

	public UIValidationStatus preValidateAction(UIValidationKey uivalidationkey,
			UIValidationCriteria uivalidationcriteria) {
		UIValidationStatus uivalidationstatus = UIValidationStatus.DISABLED;
		WTReference wtreference = uivalidationcriteria.getContextObject();
		Persistable persistable = wtreference.getObject();
		if (persistable instanceof WTPart) {
			WTPart wtPart = (WTPart) persistable;
			try {
				WTUser currentUser = (WTUser) SessionHelper.manager.getPrincipal();
				LOGGER.debug(MessageFormat.format("BomDeleteFilter.partNumber {0},currentUser {1}", wtPart.getNumber(),currentUser.getName()));
				WTPrincipalReference modifier = wtPart.getModifier();
				LOGGER.debug(MessageFormat.format("BomDeleteFilter.partNumber {0},modifier {1}", wtPart.getNumber(),modifier.getName()));
				if(currentUser.getName().equals(modifier.getName())) {
					uivalidationstatus = UIValidationStatus.ENABLED;
				}
			} catch (WTException e) {
				e.printStackTrace();
			}
		}
		return uivalidationstatus;
	}
}
