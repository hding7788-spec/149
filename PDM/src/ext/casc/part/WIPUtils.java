package ext.casc.part;
import com.ptc.core.ui.validation.UIValidationResult;
import com.ptc.core.ui.validation.UIValidationStatus;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmURLFactoryBean;
import com.ptc.netmarkets.util.misc.NetmarketURL;
import com.ptc.windchill.enterprise.wip.DefaultWIPValidator;

import org.apache.log4j.Logger;
import wt.access.AccessControlHelper;
import wt.access.AccessControlManager;
import wt.access.AccessPermission;
import wt.httpgw.URLFactory;
import wt.locks.LockException;
import wt.log4j.LogR;
import wt.org.WTUser;
import wt.part.WTProductInstance2;
import wt.session.SessionHelper;
import wt.session.SessionManager;
import wt.util.WTException;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.WorkInProgressService;
import wt.vc.wip.Workable;

public class WIPUtils extends DefaultWIPValidator
{
  protected static boolean VERBOSE = false;
  public static final String FULL = "full";
  public static final String LIMITED = "limited";
  public static final String SELECTED = "selected";
  public static final boolean ENABLED = true;
  public static final boolean DISABLED = false;
  private static final Logger log;

  public static Workable getCheckOutObject(Workable paramWorkable)
    throws LockException, WTException
  {
    return getCheckOutObject(paramWorkable, "");
  }

  public static Workable getCheckOutObject(Workable paramWorkable, String paramString)
    throws LockException, WTException
  {
    Workable localWorkable = null;
    try
    {
      //WorkInProgressHelper.service.checkout(paramWorkable, WorkInProgressHelper.service.getCheckoutFolder(), paramString, true);

      localWorkable = WorkInProgressHelper.service.workingCopyOf(paramWorkable);
    } catch (Exception localException) {
      localException.printStackTrace();
      throw new WTException(localException.getMessage());
    }
    if (localWorkable == null)
      throw new WTException("Check Out Failed!");
    return localWorkable;
  }

  public static Workable getCheckInObject(Workable paramWorkable)
    throws LockException, WTException
  {
    return getCheckInObject(paramWorkable, "");
  }

  public static Workable getCheckInObject(Workable paramWorkable, String paramString)
    throws LockException, WTException
  {
    Workable localWorkable = null;
    try {
      localWorkable = WorkInProgressHelper.service.checkin(paramWorkable, paramString);
    } catch (Exception localException) {
      localException.printStackTrace();
      throw new WTException(localException.getMessage());
    }
    if (localWorkable == null)
      throw new WTException("Check In Failed!");
    return localWorkable;
  }

  public static Workable getUndoCheckOutObject(Workable paramWorkable)
    throws LockException, WTException
  {
    Workable localWorkable = null;
    try
    {
      localWorkable = WorkInProgressHelper.service.undoCheckout(paramWorkable);
    } catch (Exception localException) {
      localException.printStackTrace();
      throw new WTException(localException.getMessage());
    }
    if (localWorkable == null)
      throw new WTException("Undo Check Out Failed!");
    return localWorkable;
  }

  public static Boolean enableableObject(Object paramObject)
    throws WTException
  {
    Workable localWorkable = null;
    if ((paramObject instanceof Workable)) {
      localWorkable = (Workable)paramObject;
    }
    if (localWorkable == null) {
      if (((paramObject instanceof WTProductInstance2)) &&
        (AccessControlHelper.manager.hasAccess(paramObject, AccessPermission.MODIFY)))
      {
        return Boolean.TRUE;
      }
      return Boolean.FALSE;
    }

    if (!AccessControlHelper.manager.hasAccess(localWorkable, AccessPermission.MODIFY))
    {
      return Boolean.FALSE;
    }
    if ((WorkInProgressHelper.isCheckedOut(localWorkable)) && (WorkInProgressHelper.isCheckedOut(localWorkable, SessionHelper.manager.getPrincipal())))
    {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  public static boolean isCheckOutValid(Object paramObject, String paramString)
    throws WTException
  {
    log.debug("*****  is Checkout Valid WIPUtil  ***** ");
    WIPUtils localWIPUtils = new WIPUtils();
    UIValidationStatus localUIValidationStatus = localWIPUtils.performCheckoutValidation(null, (Workable)paramObject, paramString).getStatus();

    boolean bool = (localUIValidationStatus == UIValidationStatus.ENABLED) || (localUIValidationStatus == UIValidationStatus.PERMITTED);

    log.debug("isCheckOutValid:" + bool);
    return bool;
  }

  @Deprecated
  public static boolean isCheckinValid(Object paramObject, String paramString, WTUser paramWTUser)
    throws WTException
  {
    log.debug("*****  is isCheckinValid Valid WIPUtil  ***** ");
    WIPUtils localWIPUtils = new WIPUtils();
    UIValidationStatus localUIValidationStatus = localWIPUtils.performCheckinValidation(null, (Workable)paramObject, paramString, paramWTUser).getStatus();

    boolean bool = (localUIValidationStatus == UIValidationStatus.ENABLED) || (localUIValidationStatus == UIValidationStatus.PERMITTED);

    log.debug("isCheckinValid:" + bool);
    return bool;
  }

  @Deprecated
  public static boolean isUndoCheckOutValid(Object paramObject)
    throws WTException
  {
    log.debug("*****  is isCheckinValid Valid WIPUtil  ***** ");
    WIPUtils localWIPUtils = new WIPUtils();
    UIValidationStatus localUIValidationStatus = localWIPUtils.performUndoCheckOutValidation(null, (Workable)paramObject, "full").getStatus();

    boolean bool = (localUIValidationStatus == UIValidationStatus.ENABLED) || (localUIValidationStatus == UIValidationStatus.PERMITTED);

    log.debug("isCheckinValid:" + bool);
    return bool;
  }

  public static String getInfoPageUrlForOid(NmOid paramNmOid, NmURLFactoryBean paramNmURLFactoryBean) throws WTException {
    if (paramNmURLFactoryBean == null) {
      paramNmURLFactoryBean = new NmURLFactoryBean();
      paramNmURLFactoryBean.setFactory(new URLFactory());
    }
    return NetmarketURL.buildURL(paramNmURLFactoryBean, "object", "view", paramNmOid);
  }

  static
  {
    try
    {
      log = LogR.getLogger(WIPUtils.class.getName());
    } catch (Exception localException) {
      throw new ExceptionInInitializerError(localException);
    }
  }
}