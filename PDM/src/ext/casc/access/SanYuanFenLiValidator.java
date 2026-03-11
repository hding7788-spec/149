/* bcwti
 *
 * Copyright (c) 2010 Parametric Technology Corporation (PTC). All Rights Reserved.
 *
 * This software is the confidential and proprietary information of PTC
 * and is subject to the terms of a software license agreement. You shall
 * not disclose such confidential information and shall use it only in accordance
 * with the terms of the license agreement.
 *
 * ecwti
 */
package ext.casc.access;

import java.util.ArrayList;
import java.util.Locale;

import org.apache.log4j.Logger;

import wt.inf.container.WTContainer;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.log4j.LogR;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;

import com.ptc.core.ui.validation.DefaultUIComponentValidator;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationResult;
import com.ptc.core.ui.validation.UIValidationResultSet;
import com.ptc.core.ui.validation.UIValidationStatus;

/**
 * This class validates the ManageSecurity Agreements step.
 *
 * <BR><BR><B>Supported API: </B>false
 * <BR><BR><B>Extendable: </B>false
 *
 * @version   1.0
 **/
public class SanYuanFenLiValidator extends DefaultUIComponentValidator {

   private static final Logger log;

   static
   {
      try
      {
         log = LogR.getLogger(SanYuanFenLiValidator.class.getName());
      }
      catch (Exception e)
      {
         throw new ExceptionInInitializerError(e);
      }
   }
   @Override
   public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
		try {
			if(AccessAdminUtil.isSysAdmin()){
				return UIValidationStatus.HIDDEN;
			}else if(AccessAdminUtil.isSecAdmin()){
				return UIValidationStatus.HIDDEN;
			}else if(AccessAdminUtil.isAuditAdmin()){
				return UIValidationStatus.HIDDEN;
			}else{
				return UIValidationStatus.ENABLED;
			}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return UIValidationStatus.ENABLED;
   }
    public UIValidationResultSet performFullPreValidation(UIValidationKey validationKey,
    												UIValidationCriteria validationCriteria,
    												Locale locale) throws WTException {
    	UIValidationResultSet resultSet = UIValidationResultSet.newInstance();
    	UIValidationResult result  = null;
    	if(AccessAdminUtil.isSysAdmin()){
    		result = UIValidationResult.newInstance(validationKey, UIValidationStatus.HIDDEN);
    	}else if(AccessAdminUtil.isSecAdmin()){
    		result = UIValidationResult.newInstance(validationKey, UIValidationStatus.HIDDEN);
    	}else if(AccessAdminUtil.isAuditAdmin()){
    		result = UIValidationResult.newInstance(validationKey, UIValidationStatus.HIDDEN);
    	}else{
    		result = UIValidationResult.newInstance(validationKey, UIValidationStatus.ENABLED);
    	}
    	resultSet.addResult(result);
        return resultSet;
    }

}

