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

import java.util.Locale;

import com.ptc.core.components.validators.ContextWizardStepValidator;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationResult;
import com.ptc.core.ui.validation.UIValidationResultSet;
import com.ptc.core.ui.validation.UIValidationStatus;

import org.apache.log4j.Logger;

import wt.access.configuration.SecurityLabelsConfiguration;
import wt.log4j.LogR;
import wt.util.WTException;

/**
 * This class validates the ManageSecurity Agreements step.
 *
 * <BR><BR><B>Supported API: </B>false
 * <BR><BR><B>Extendable: </B>false
 *
 * @version   1.0
 **/
public class ManageSecurityStepValidator extends ContextWizardStepValidator {

   private static final Logger log;

   static
   {
      try
      {
         log = LogR.getLogger(ManageSecurityStepValidator.class.getName());
      }
      catch (Exception e)
      {
         throw new ExceptionInInitializerError(e);
      }
   }

    public UIValidationResultSet performFullPreValidation(UIValidationKey validationKey,
    												UIValidationCriteria validationCriteria,
    												Locale locale) throws WTException {


	    UIValidationResultSet resultSet = UIValidationResultSet.newInstance();
        resultSet.addResult(UIValidationResult.newInstance(validationKey, UIValidationStatus.HIDDEN));
        
        return resultSet;
    }

}

