package com.glaway.mpm.pbombuilder.jws;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import com.glaway.mpm.pbombuilder.data.CmLightContainer;
import com.glaway.mpm.pbombuilder.util.CmBase64;
import com.glaway.mpm.pbombuilder.util.CmSettings;
import com.jacob.com.LibraryLoader;

/**
 * <br>Created on 2012-11-23
 * @author chenyunlong
 */
public class CmContext {
//   private static final String     CLASSNAME        = CmContext.class.getName();
//   private static final CmLogger   log              = CmLogger.getLogger(CLASSNAME);

   private static String           derivativeNumber = null;
   private static Long             derivativeOid    = null;
   private static String           derivativeType   = null;

   private static CmLightContainer container        = null;

   private static String           application      = null;
   private static boolean          connected        = false;
   private static boolean          sessionPVStarted = false;
   private static String           userPrefView     = null;
   private static boolean          editByInstance   = true;

   private static boolean          isJWS            = false;
   private static String           password         = null;
   private static String           user             = null;

   private static File             td               = null;

   private static JFrame           mainFrame        = null;
   private static String           authoringAppType = null;

   private static Locale           locale           = Locale.SIMPLIFIED_CHINESE;
   private static String           wsName           = null;
   private static String           wsContainerName  = null;
   private static String           wsLocalDirectory = null;

   private static String           rootNodeType     = null;
   private static int              contextCnt       = 0;
   private static String           serverName       = null;
   private static String           serverURL        = null;
   
   private static boolean         isEdit=true;

   /**
    * logonToServer: constructor
    *
    * @param usr - String userName
    * @param pass - String password
    **/
   // ------------------------------------------------
   public static void logonToServer(String usr, String pass) throws Exception {
      setUser(usr);
      setPassword(pass);
      logonToServer();
   }

   public static void logonToServer(String auth) {
      setCred(auth);
      logonToServer();
   }
   public static boolean isEdit(){
      return CmContext.isEdit;
   }
   public static boolean isEdit(boolean b){
      return CmContext.isEdit=b;
   }
   public static void logonToServer() {
//      try {
//         log.debug("wt.home used: " + WTProperties.getLocalProperties().getProperty("wt.server.codebase"));
//      } catch (IOException ex) {
//         log.error(ex);
//      }
//      if (!connected) {
//         RemoteMethodServer rmi = RemoteMethodServer.getDefault();
//         rmi.setUserName(getUser());
//         rmi.setPassword(getPassword());
//         try {
//            log.debug("before auth check");
//            rmi.getInfo();
//            log.debug("after auth check");
//         } catch (RemoteException remoteexception) {
//            log.debug("after auth check with ex: " + remoteexception);
//            if ((remoteexception instanceof WTRemoteException) && (((WTRemoteException) remoteexception).getNestedThrowable() instanceof AuthenticationException)) {
//               return;
//            } else {
//               if (JWSUtil.isInJWS())
//                  JWSUtil.shutdown();
//               return;
//            }
//         }
//         NetAuthenticatorInstaller.install();
//         NetAuthenticatorInstaller.netAuthenticator.setCredentials(null, null, null, getUser(), false, getPassword());
//         try {
//            //Authentication.getUserName();
//         } catch (Exception re) {
//            if ((re instanceof WTRemoteException) && (((WTRemoteException) re).getNestedThrowable() instanceof AuthenticationException)) {
//               if (JWSUtil.isInJWS())
//                  JWSUtil.shutdown();
//               return;
//            }
//            log.error(re);
//         }
//         connected = true;
//      }
   }

   public static boolean getConnected() {
      return connected;
   }

   public static void setDerivativeNumber(String number) {
      derivativeNumber = number;
   }

   public static String getDerivativeNumber() {
      return derivativeNumber;
   }

   public static void setDerivativeOid(Long oid) {
      derivativeOid = oid;
   }

   public static String getContainerName() {
      return container == null ? null : container.getName();
   }

   public static void setContainerName(String containerName) {
      container = new CmLightContainer(containerName);
   }

   public static long getContainerOid() {
      return container == null ? 0 : container.getId();
   }

   public static void setContainerOid(long containerOid) {
      container = new CmLightContainer(containerOid);
   }

   public static String getDerivativeType() {
      return derivativeType;
   }

   public static void setDerivativeType(String derivativeType) {
      CmContext.derivativeType = derivativeType;
   }

   public static Long getDerivativeOid() {
      return derivativeOid;
   }

   public static String getApplication() {
      return application;
   }

   public static void setApplication(String application) {
      CmContext.application = application;
   }

   public static boolean isSessionPVStarted() {
      return sessionPVStarted;
   }

   public static void setSessionPVStarted(boolean sessionPVStarted) {
      CmContext.sessionPVStarted = sessionPVStarted;
   }

   public static String getUserPrefView() {
      return userPrefView;
   }

   public static void setUserPrefView(String userPrefView) {
      CmContext.userPrefView = userPrefView;
   }

   public static String getCred() {
      return "Basic " + CmBase64.encode(user + ":" + password);
   }

   public static JFrame getMainFrame() {
      return mainFrame;
   }

   public static void setMainFrame(JFrame mainFrame) {
      CmContext.mainFrame = mainFrame;
   }

   public static File getTempDir() {
      if (td == null) {
         try {
            td = File.createTempFile("samc_", "");
            td.delete();
            td.mkdirs();
         } catch (IOException ex) {
            ex.printStackTrace();
         }
      } else if (!td.exists()) {
         td.mkdirs();
      }
      return td;
   }

   public static String getPassword() {
      return password;
   }

   public static String getUser() {
      return user;
   }

   public static void setCred(String auth) {
      if (auth != null) {
         String s4 = CmBase64.decode(auth);
         int i = s4.indexOf(':');
         if (s4 != null) {
            user = s4.substring(0, i);
            password = s4.substring(i + 1);
         }
      }
   }

   public static void setPassword(String password) {
      CmContext.password = password;
   }

   public static void setUser(String user) {
      CmContext.user = user;
   }

   public static void setJWS(boolean b) {
      isJWS = b;
   }

   public static boolean isJWS() {
      return isJWS;
   }

   public static boolean isEditByInstance() {
      return editByInstance;
   }

   public static void setEditByInstance(boolean editByInstance) {
      CmContext.editByInstance = editByInstance;
   }

   public static String getAuthoringAppType() {
      if (authoringAppType == null)
         return "CATIAV5";
      return authoringAppType;
   }

   public static void setAuthoringAppType(String authoringAppType) {
//      log.debug("DICContext.setAuthoringAppType(): " + authoringAppType);
      //workaround for dll incompatibility bug between wf4 and pview; should be solved with pview9.0M050

      try {
         if (authoringAppType.equalsIgnoreCase("PROE")) {
            System.loadLibrary("pfcasyncmt");
         }
         if (authoringAppType.equalsIgnoreCase("CATIAV5")) {
            LibraryLoader.loadJacobLibrary();
         }

         CmContext.authoringAppType = authoringAppType;

      } catch (UnsatisfiedLinkError ex) {

         if (authoringAppType.equalsIgnoreCase("PROE")) {

            JOptionPane.showMessageDialog(null, "Required ProE JLink configuration steps missing.\n\n" + "Please make sure that the following Enviroment Variables are set\n\n"
                  + "for Windows 32 Bit System\n" + "PRO_COMM_MSG_EXE = [PROE_LOADPOINT]\\i486_nt\\obj\\pro_comm_msg.exe\n" + "PATH = [PROE_LOADPOINT]\\bin;[PROE_LOADPOINT]\\i486_nt\\lib\n\n"
                  + "for Windows 64 Bit System\n" + "make sure to install a x64 Java together with a x64 ProE\n" + "PRO_COMM_MSG_EXE = [PROE_LOADPOINT]\\x86e_win64\\obj\\pro_comm_msg.exe\n"
                  + "PATH = [PROE_LOADPOINT]\\bin;[PROE_LOADPOINT]\\x86e_win64\\lib\n\n" + "Until this is fixed the 'Generate in ProE' action is disabled !!\n\n", "ProE Generation Setup Error",
                  JOptionPane.ERROR_MESSAGE);
         }
         if (authoringAppType.equalsIgnoreCase("CATIAV5")) {

            JOptionPane.showMessageDialog(null, "Required CatiaV5 configuration step missing.\n\n" + "Please make sure that the Jacob dll's aree accessible through your System Path.\n"
                  + "Until this is fixed the 'Generate in CatiaV5' action is disabled !!\n\n", "CATIAV5 Generation Setup Error", JOptionPane.ERROR_MESSAGE);
         }

         CmContext.authoringAppType = "STANDARD";
      }

   }

   public static Locale getLocale() {
      return locale;
   }

   public static void setLocale(Locale locale) {
      CmContext.locale = locale;
//      if (!RemoteMethodServer.ServerFlag)
//         CmUtil.setLocale(locale);
   }

   // -----------------------------------------------
   /**
    *
    * getProperty:
    *
    * @param  prop_name - String property name
    * @return prop_value - String value
    *
    **/
   // ------------------------------------------------
   public static String getProperty(String prop_name) {
      String prop_value = CmSettings.getSection(CmSettings.SECTION_CAD).get(prop_name);
      return prop_value;
   }

   /**
    * @return the wsName
    */
   public static String getWsName() {
      return wsName;
   }

   /**
    * @return the wsContainerName
    */
   public static String getWsContainerName() {
      return wsContainerName;
   }

   /**
    * @return the wsLocalDirectory
    */
   public static String getWsLocalDirectory() {
      return wsLocalDirectory;
   }

   /**
    * @param wsName the wsName to set
    */
   public static void setWsName(String wsName) {
      CmContext.wsName = wsName;
   }

   /**
    * @param wsContainerName the wsContainerName to set
    */
   public static void setWsContainerName(String wsContainerName) {
      CmContext.wsContainerName = wsContainerName;
   }

   /**
    * @param wsLocalDirectory the wsLocalDirectory to set
    */
   public static void setWsLocalDirectory(String wsLocalDirectory) {
      CmContext.wsLocalDirectory = wsLocalDirectory;
   }

   public static void setRootNodeType(String type) {
      rootNodeType = type;
   }

   public static String getRootNodeType() {
      if (rootNodeType == null)
         return getProperty("DIC.RootStrucLevel");
      return rootNodeType;
   }

   public static void addContext() {
      contextCnt = contextCnt + 1;
   }

   public static int getContextCnt() {
      return contextCnt;
   }

   /**
    * @param serverName the serverName to set
    */
   public static void setServerName(String serverName) {
      CmContext.serverName = serverName;
   }

   /**
    * @param serverURL the serverURL to set
    */
   public static void setServerURL(String serverURL) {
      CmContext.serverURL = serverURL;
   }

   /**
    * @return the serverName
    */
   public static String getServerName() {
      return serverName;
   }

   /**
    * @return the serverURL
    */
   public static String getServerURL() {
      return serverURL;
   }
}
