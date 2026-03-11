/**
 * @(#)DeleteEnvelopeActionValidator.java
 *
 *
 * @author Leon Zhang
 * @version 1.00 2010/1/25
 */

package ext.ases.envelope;

import ext.ases.envelope.*;

import com.ptc.core.ui.validation.*;
import java.util.Locale;
import org.apache.log4j.Logger;
import wt.access.AccessPermission;
import wt.fc.WTReference;
import wt.log4j.LogR;
import wt.util.WTException;

// Referenced classes of package com.ptc.windchill.enterprise.baseline.validators:
//            BaselineValidatorHelper

public class DeleteEnvelopeActionValidator extends DefaultUIComponentValidator
{

    private static final Logger logger;

    public DeleteEnvelopeActionValidator()
    {
    }

    public UIValidationResultSet performFullPreValidation(UIValidationKey uivalidationkey, UIValidationCriteria uivalidationcriteria, Locale locale)
        throws WTException
    {
        logger.debug("Enter performFullPreValidation Method");
        UIValidationStatus uivalidationstatus = UIValidationStatus.DISABLED;
        UIValidationResultSet uivalidationresultset = new UIValidationResultSet();
        WTReference wtreference = uivalidationcriteria.getContextObject();
        if(EnvelopeValidatorHelper.validateContainerAndPermissions(uivalidationcriteria, wtreference, AccessPermission.DELETE))
        {
            uivalidationstatus = UIValidationStatus.ENABLED;
            //uivalidationstatus = EnvelopeValidatorHelper.getDisplayStatusForLockCheck(wtreference, uivalidationcriteria);
        }
        logger.debug("Setting display status to: ");
        logger.debug(uivalidationstatus);
        uivalidationresultset.addResult(new UIValidationResult(uivalidationkey, uivalidationstatus, uivalidationcriteria.getContextObject()));
        logger.debug("Exit performFullPreValidation Method");
        return uivalidationresultset;
    }

    static 
    {
        try
        {
            logger = LogR.getLogger(ext.ases.envelope.DeleteEnvelopeActionValidator.class.getName());
        }
        catch(Exception exception)
        {
            throw new ExceptionInInitializerError(exception);
        }
    }
}