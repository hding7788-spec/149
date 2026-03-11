/**
 * @(#)EnvelopeValidatorHelper.java
 *
 *
 * @author Leon Zhang
 * @version 1.00 2010/1/25
 */
package ext.ases.envelope;

import org.apache.log4j.Logger;

import wt.access.AccessControlHelper;
import wt.access.AccessPermission;
import wt.admin.AdminDomainRef;
import wt.fc.WTReference;
import wt.inf.container.OrgContainer;
import wt.inf.container.WTContainerRef;
import wt.inf.library.WTLibrary;
import wt.locks.Lockable;
import wt.log4j.LogR;
import wt.org.WTPrincipalReference;
import wt.pdmlink.PDMLinkProduct;
import wt.util.WTException;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class EnvelopeValidatorHelper {

    public EnvelopeValidatorHelper() {
    }
    
    // Get log4j logger for Baseline Client
   private static final Logger logger;
   static {
      try {
         logger = LogR.getLogger(EnvelopeValidatorHelper.class.getName());
      } catch (Exception e) {
         throw new ExceptionInInitializerError(e);
      }
   }

   /**
    * Checks to ensure the container is a valid container for the action.
    * <br><br>
    * Return value is true when:
    *
    * <li> the container or current context is a PDMLinkProduct or WTLibrary
    * <br>
    * @param containerRef  The container reference that the action is launched in.
    * @return boolean      True if container checks are okay, false otherwise.
    * @throws wt.util.WTException
    */
   public static boolean isValidContainer(WTReference containerRef) throws WTException {

      boolean isValidCont = false;
      logger.debug("Container reference is:");
      logger.debug(containerRef);
      if (containerRef != null &&
            (PDMLinkProduct.class.isAssignableFrom(containerRef.getReferencedClass()) ||
             WTLibrary.class.isAssignableFrom(containerRef.getReferencedClass()))) {
         isValidCont = true;
         logger.debug("Container reference is a valid container.");
      }
      return isValidCont;
   }


   /**
    * Checks to ensure that the container is valid and the user has the correct permissions.<br>
    * <br>
    * Return value is true when:
    * <br>
    * <li> the container or current context is a PDMLinkProduct or WTLibrary
    * <li> the user has permission specified for the ManagedBaseline type<br>
    * <br>
    * @param validationCriteria - Critera object that contains the parent container and current user.
    * @param actionRef  The object that the action is ocurring on.
    * @param permssion  The AccessPermission type to check (e.g. AccessPermission.CREATE).
    * @return boolean   True if container and permission check are okay, false otherwise.
    * @throws wt.util.WTException
    */
   public static boolean validateContainerAndPermissions(UIValidationCriteria validationCriteria,
      WTReference actionRef, AccessPermission permission) throws WTException {

      logger.debug("ENTER -> EnvelopeValidatorHelper.validateContainerAndPermissions");
      boolean isValidAction = false;

      logger.debug("Action reference: ");
      logger.debug(actionRef);

      // If there is no actionRef then this action was invoked from a template
      // processor and validation occurred in the processor delegate.
      // Don't re-evaluate validation here.  Also, there is no way to get the container.
      if (actionRef == null) {

         isValidAction = true;
         logger.debug("No action reference, so action must have been launched from template processor."
                      +"  Set container and permission validation to true.");
      } else {

         // Check that the context or container is a product or library
         WTContainerRef containerRef = validationCriteria.getInvokedFromContainer();
         
         // If InvokedFromContainer is null, get it from parent container.
         if (containerRef == null) {
             containerRef = validationCriteria.getParentContainer();
         }
         
         // If containerRef is still null or this is an org container, 
         // get the container from the object.  This will happen from Template Processor Page launchpoints.
         if (containerRef == null || OrgContainer.class.isAssignableFrom(containerRef.getReferencedClass())) {
             if (!(actionRef instanceof wt.inf.container.WTContainerRef)) {
                 wt.fc.Persistable object = actionRef.getObject();
                 if (object instanceof wt.inf.container.WTContained) {
                    containerRef = ((wt.inf.container.WTContained)object).getContainerReference();
                 }
              } else {
                 containerRef = (WTContainerRef)actionRef;
              }
         }
         
         // Ensure the user has appropriate permissions on Managed Baseline objects
         if (isValidContainer(containerRef)) {
            logger.debug("Container check passed.");
            if (AccessControlHelper.manager.hasAccess(validationCriteria.getUser().getPrincipal(),
                  ProcessEnvelope.class.getName(),
                  permission)) {
               logger.debug("User has permission for ProcessEnvelope types.");
               isValidAction = true;
            }
         }
      }
      logger.debug("Is valid action?: ");
      logger.debug(isValidAction);

      logger.debug("EXIT  <- EnvelopeValidatorHelper.validateContainerAndPermissions");
      return isValidAction;
   }



   /**
    * Determines the validation status for an action with respect to the lock
    * currently held on the object.
    * <br>
    * <br>
    * The following are possible display status values returned:
    * <br>
    * <li> HIDDEN:    When the WTReference parameter isn't a Lockable object. 
    * <li> ENABLED:   When the object isn't locked or when locked, the current lock owner 
    *                 is the current user or container admin.
    * <li> DISABLED:  When the object is locked and the lock isn't owned by the current user
    *                 or container admin or when it is locked, if the action is "Unlock".
    * <br> 
    * @param lockObjectRef        WTReference for the object relevant for the action validation 
    * @param validationCriteria   Validation object that contains the parent container and current user.
    * @param validationKey        Validation key that is used to determine current action.
    * @return UIValidationStatus  Action display status 
    * @throws wt.util.WTException Thrown when any abnormal processing error occurs.
    */
   public static UIValidationStatus getDisplayStatusForLockCheck(WTReference lockObjectRef, 
         UIValidationCriteria validationCriteria, UIValidationKey validationKey) throws WTException {
       
      logger.debug("ENTER -> getDisplayStatusForLockCheck");
      
      UIValidationStatus displayStatus = UIValidationStatus.HIDDEN;
       
      // Check first to ensure the object is a Lockable object,
      // if not return HIDDEN status.
      if (!Lockable.class.isAssignableFrom(lockObjectRef.getReferencedClass())) {
         return displayStatus;          
      }

      Lockable lockableObj = (Lockable) lockObjectRef.getObject();
      
      // Check to ensure user has modify access to this baseline.
      // If not, disable the action instead of hide it, because the current
      // state of the object is causing user to not have access.
      if (!AccessControlHelper.manager.hasAccess(lockableObj, AccessPermission.MODIFY)) { 
          displayStatus = UIValidationStatus.DISABLED;
          logger.debug("\tUser doesn't have modify access for object.");  
      
      } else {
      
         if (lockableObj.isLocked()) {
            logger.debug("Lockable Object is locked.");
            WTPrincipalReference curUser = validationCriteria.getUser();
            logger.debug("Current user is: ");
            logger.debug(curUser);
            // Enable the action if the user currently owns the lock or is a container admin.
            if (curUser != null && (curUser.equals(lockableObj.getLocker()) || validationCriteria.isContainerAdmin())) {
               displayStatus = UIValidationStatus.ENABLED;
            } else {
               displayStatus = UIValidationStatus.DISABLED;
               logger.debug("Current user doesn't own lock.");
            }
         } else {
            // If the action is unlock, then action should be DISABLED and not Enabled.
            // For other actions it should be enabled.    
            String actionName = validationKey != null ? validationKey.getComponentID() : null;
            if (actionName != null && "unlockBaseline".equals(actionName)) {
               displayStatus = UIValidationStatus.DISABLED;
               logger.debug("Lockable is not locked and action is Unlock.");             
            } else {
               displayStatus = UIValidationStatus.ENABLED;
               logger.debug("Lockable is not locked and action is not Unlock.");
            }
         }
      } 
      
      logger.debug("EXIT  <- getDisplayStatusForLockCheck");
      return displayStatus;
   }
   
   
   /**
    * Determines the validation status for an action with respect to the lock
    * currently held on the object.
    * <br>
    * <br>
    * The following are possible display status values returned:
    * <br>
    * <li> HIDDEN:    When the WTReference parameter isn't a Lockable object. 
    * <li> ENABLED:   When the object isn't locked or when locked, the current lock owner 
    *                 is the current user or container admin.
    * <li> DISABLED:  When the object is locked and the lock isn't owned by the current user
    *                 or container admin.
    * <br> 
    * @param lockObjectRef        WTReference for the object relevant for the action validation 
    * @param validationCriteria   Validation object that contains the parent container and current user.
    * @return UIValidationStatus  Action display status 
    * @throws wt.util.WTException Thrown when any abnormal processing error occurs.
    */
   public static UIValidationStatus getDisplayStatusForLockCheck(WTReference lockObjectRef, 
         UIValidationCriteria validationCriteria) throws WTException {
       
      logger.debug("ENTER -> getDisplayStatusForLockCheck");
      
      logger.debug("EXIT  <- getDisplayStatusForLockCheck");
      return getDisplayStatusForLockCheck(lockObjectRef, validationCriteria, null);
      
   }
}