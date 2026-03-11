/**
 * @(#)UpdateEnvelopeActionValidator.java
 *
 *
 * @author Leon Zhang
 * @version 1.00 2010/1/25
 */
package ext.ases.envelope;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.core.ui.validation.DefaultUIComponentValidator;
import com.ptc.core.ui.validation.UIComponentValidator;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationFeedbackMsg;
import com.ptc.core.ui.validation.UIValidationResult;
import com.ptc.core.ui.validation.UIValidationResultSet;
import com.ptc.core.ui.validation.UIValidationStatus;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmContextBean;
import com.ptc.netmarkets.util.misc.NmContext;
import com.ptc.netmarkets.util.misc.NmContextItem;
import com.ptc.windchill.enterprise.baseline.NmBaselineCommands;

import wt.access.AccessPermission;
import wt.admin.AdministrativeDomainHelper;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentType;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.fc.WTReference;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.vc.baseline.Baseline;
import wt.vc.baseline.ManagedBaseline;
import wt.util.WTException;
import wt.util.WTMessage;

import org.apache.log4j.Logger;
import wt.log4j.LogR;

public class UpdateEnvelopeActionValidator extends DefaultUIComponentValidator {

    public UpdateEnvelopeActionValidator() {
    }
    
    public static final String ENVELOPE_RESOURCE = "ext.ases.envelope.envelopeResource";
   public static final String ENVELOPE_OID_ELEMENT_ID = "envelopeOid";

   // Get log4j logger for Baseline Client
   private static final Logger logger;
   static {
      try {
         logger = LogR.getLogger("ext.ases.envelope.envelope");
      } catch (Exception e) {
         throw new ExceptionInInitializerError(e);
      }
   }
   /**
    * Determines if the action is accessible for a table row action.
    * (Access Control permissions ARE NOT checked.)<br>
    * <br>
    * The action is enabled when the following condition is met:
    * <br>
    * <li> the container is a PDMLinkProduct or WTLibrary
    * <br>
    * <br>
    * This validation makes the assumption that the action will never be added
    * as an action to a non-valid type.
    * <br>
    * @param     validationKey  A UIValidationKey object representing the action or UI component to be validated.
    * @param     validationCriteria  Object holding information required to perform validation tasks.
    * @param     locale  The user's Locale.  If a <i>null</i> value is passed in, the session locale will be used.
    * @return    UIValidationResultSet
    * @exception wt.util.WTException
    */
   @Override
   public UIValidationResultSet performLimitedPreValidation (UIValidationKey validationKey,
         UIValidationCriteria validationCriteria, Locale locale)  throws WTException {

      logger.debug("ENTER -> UpdateEnvelopeActionValidator.performLimitedPreValidation");
      UIValidationStatus displayStatus;

      // Check that the container is a product or library
      WTContainerRef containerRef = validationCriteria.getInvokedFromContainer();
      if (EnvelopeValidatorHelper.isValidContainer(containerRef)) {
         displayStatus = UIValidationStatus.ENABLED;
         logger.debug("Setting display status to ENABLED.");
      } else {
         displayStatus = UIValidationStatus.HIDDEN;
         logger.debug("Setting display status to HIDDEN.");
      }

      // Add the result for each target object.
      UIValidationResultSet resultSet = new UIValidationResultSet();
      for (Object targetObj : validationCriteria.getTargetObjects()) {
         WTReference targetRef = (WTReference) targetObj;
         logger.debug("Target reference is: ");
         logger.debug(targetRef);
         resultSet.addResult(new UIValidationResult(validationKey, displayStatus, targetRef));
      }

      logger.debug("EXIT -> UpdateEnvelopeActionValidator.performLimitedPreValidation");
      return resultSet;
   }

   /**
    * Determines if the action is accessible in an actions drop down list.
    * (Access Control permissions ARE checked.)<br>
    * <br>
    * The action is enabled when all the following conditions are met:
    *
    * <li> the container or current context is a PDMLinkProduct or WTLibrary
    * <li> the user has update permissions for the ManagedBaseline type<br>
    * <li> the target object is NOT in the workspace
    * <li> the target object is NOT in a workpackage
    * <li> if applicable, the target object is not an EPMDocument of type Note and is a template
    *
    * @param     validationKey  A UIValidationKey object representing the action or UI component to be validated.
    * @param     validationCriteria  Object holding information required to perform validation tasks.
    * @param     locale  The user's Locale.  If a <i>null</i> value is passed in, the session locale will be used.
    * @return    UIValidationResultSet
    * @exception wt.util.WTException
    */
   @Override
   public UIValidationResultSet performFullPreValidation (UIValidationKey validationKey,
         UIValidationCriteria validationCriteria, Locale locale)  throws WTException {

      logger.debug("ENTER -> UpdateEnvelopeActionValidator.performFullPreValidation");
      UIValidationStatus displayStatus;
      UIValidationResultSet resultSet = new UIValidationResultSet();

      // Check that the container and permissions for the user are valid.
      WTReference contextObj = validationCriteria.getContextObject();
      if (contextObj == null){
    	  displayStatus = UIValidationStatus.HIDDEN;
    	  resultSet.addResult(new UIValidationResult(validationKey, displayStatus));
    	  return resultSet;
      }
      if (EnvelopeValidatorHelper.validateContainerAndPermissions(validationCriteria,
                                                                  contextObj,
                                                                  AccessPermission.MODIFY)) {

         displayStatus = UIValidationStatus.ENABLED;
         logger.debug("Setting display status to ENABLED, valid container and permissions.");

         // If not in workspace, check type to ensure it isn't an EPMDoc "Note" template
         // or a Ghost object.
         if (!validationCriteria.getInWorkspace().booleanValue()) {

            // Unfortunately, the object has to be inflated to check doc type and placeholder.
            if (EPMDocument.class.isAssignableFrom(contextObj.getReferencedClass())) { 
               EPMDocument epmDoc = (EPMDocument) contextObj.getObject();
               if (epmDoc != null && epmDoc.isTemplated()) {
          	      EPMDocumentType docType = epmDoc.getDocType();
                  if (docType.equals(EPMDocumentType.toEPMDocumentType("NOTE"))) {
                     displayStatus = UIValidationStatus.HIDDEN;
                     logger.debug("Not a valid member type - object is a Note Template.");
                  }
               } else if (epmDoc != null && epmDoc.isPlaceHolder()) {
                  displayStatus = UIValidationStatus.HIDDEN;
                  logger.debug("Not a valid member type - object is a ghost epmdoc.");
               }
            }
         } else {
        	 // For hiding the action when object is new or checked out to the workspace, we have included filters for the action.
        	 
        	 // Enable action if the part is added to workspace
        	 if (WTPart.class.isAssignableFrom(contextObj.getReferencedClass())) {
        		 displayStatus = UIValidationStatus.ENABLED;
                 logger.debug("Setting display status to ENABLED, for parts added to the workspace.");  
        	 }
        	 // Don't display if in the workspace.
        	 else {
        		 displayStatus = UIValidationStatus.HIDDEN;
                 logger.debug("Setting display status to HIDDEN, in the workspace.");  
        	 }
            
         }

      } else {
		 // Don't display if container and permissions check fail.
         displayStatus = UIValidationStatus.HIDDEN;
         logger.debug("Setting display status to HIDDEN.");
      }

      resultSet.addResult(new UIValidationResult(validationKey, displayStatus, contextObj));

      logger.debug("EXIT -> UpdateEnvelopeActionValidator.performFullPreValidation");
      return resultSet;
   }

   /**
    * Determines if the action (as a row action) is valid to proceed.
    * (Access Control permissions ARE checked.)<br>
    * <br>
    * The action is permitted when all the following conditions are met:
    *
    * <li> the container or current context is a PDMLinkProduct or WTLibrary
    * <li> the user has update permissions for the ManagedBaseline type
    * <br>
    * @param     validationKey  A UIValidationKey object representing the action or UI component to be validated.
    * @param     validationCriteria  Object holding information required to perform validation tasks.
    * @param     locale  The user's Locale.  If a <i>null</i> value is passed in, the session locale will be used.
    * @return    UIValidationResult
    * @exception wt.util.WTException
    */
   @Override
   public UIValidationResult validateSelectedAction (UIValidationKey validationKey,
         UIValidationCriteria validationCriteria, Locale locale)  throws WTException {

      logger.debug("ENTER -> UpdateEnvelopeActionValidator.validateSelectedAction");
      UIValidationResult result;
      WTReference contextObj = validationCriteria.getContextObject();

      // Permit if validation passes, otherwise, deny with an error message.
      if (EnvelopeValidatorHelper.validateContainerAndPermissions(validationCriteria,
                                                                  contextObj,
                                                                  AccessPermission.MODIFY)) {
         result = new UIValidationResult(validationKey, UIValidationStatus.PERMITTED, contextObj);
         logger.debug("Setting display status to PERMITTED.");
      } else {
         String message = WTMessage.getLocalizedMessage(ENVELOPE_RESOURCE,
                 "ENVELOPE_UPDATE_ACTION_PERM_ERROR",
                 null,
                 locale);
         UIValidationFeedbackMsg feedbackMsg = new UIValidationFeedbackMsg(message, FeedbackType.ERROR);
         ArrayList feedbackList = new ArrayList();
         feedbackList.add(feedbackMsg);
         result = new UIValidationResult(validationKey, UIValidationStatus.DENIED, contextObj, feedbackList);
         logger.debug("Setting display status to DENIED.");
      }

      logger.debug("EXIT -> UpdateEnvelopeActionValidator.validateSelectedAction");
      return result;
   }
   

   /**
    * Peforms validation of the action on submission, to ensure that:
    *
    * <li> The baseline oid was set on the form.
    *
    * @param     validationKey  A UIValidationKey object representing the action or UI component to be validated.
    * @param     validationCriteria  Object holding information required to perform validation tasks.
    * @param     locale  The user's Locale.  If a <i>null</i> value is passed in, the session locale will be used.
    * @return    UIValidationResult
    * @exception wt.util.WTException
    */
   @Override
   public UIValidationResult validateFormSubmission (UIValidationKey validationKey,
         UIValidationCriteria validationCriteria, Locale locale) throws WTException {

      logger.debug("ENTER -> UpdateEnvelopeActionValidator.validateFormSubmission");
      UIValidationStatus status = UIValidationStatus.DENIED;
      ArrayList feedbackList = new ArrayList();
      status = UIValidationStatus.PERMITTED;
      UIValidationResult result = new UIValidationResult(validationKey, status, validationCriteria.getContextObject(), feedbackList);

      logger.debug("EXIT -> UpdateEnvelopeActionValidator.validateFormSubmission");
      return result;
   }
}