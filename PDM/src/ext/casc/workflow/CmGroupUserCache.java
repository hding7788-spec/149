
package ext.casc.workflow;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;

import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.session.SessionServerHelper;
import wt.util.WTException;


public class CmGroupUserCache implements RemoteAccess
{
//   private static final CmLogger logger = CmLogger.getLogger(CmGroupUserCache.class);

   private static final long       serialVersionUID    = 7049777401223420222L;
   private static final long       interalMillis       = 3 * 60 * 60 * 1000L; // 3小时*60分钟*60秒*1000毫秒
   private static long             lastLoaded          = 0L;

   private static HashMap<Object, Object>          cache               = null;
   private static CmGroupUserCache groupUserCache      = null;

   public static String            KEY_LEVEL1          = "KEY_LEVEL1";
   public static String            KEY_LEVEL2          = "KEY_LEVEL2";
   public static String            KEY_LEVEL3          = "KEY_LEVEL3";
   public static String            KEY_USRLIB          = "KEY_USRLIB";

   private HashMap<Object, Object> getCache()
   {
      return cache;
   }

   private CmGroupUserCache() {
      load();
   }

   private void checkCache()
   {
      if ((System.currentTimeMillis() - lastLoaded) > interalMillis)
         load();
   }

   private ArrayList<WTPrincipal> sortPrincipalEnum(Enumeration ee)
   {
      ArrayList<WTPrincipal> principals = new ArrayList<WTPrincipal>();
      while (ee.hasMoreElements()){
          Object object = ee.nextElement();
          principals.add((WTPrincipal) object);
      }

      Collections.sort(principals, new Comparator<WTPrincipal>() {
         public int compare(WTPrincipal o1, WTPrincipal o2)
         {
            String s1, s2;
            if (o1 instanceof WTUser)
               s1 = "00" + getKey(((WTUser) o1).getFullName());
            else if (o1 instanceof WTGroup)
               s1 = "01" + getKey(o1.getName());
            else
               s1 = "02" + getKey(o1.getName());

            if (o2 instanceof WTUser)
               s2 = "00" + getKey(((WTUser) o2).getFullName());
            else if (o1 instanceof WTGroup)
               s2 = "01" + getKey(o2.getName());
            else
               s2 = "02" + getKey(o2.getName());

            return s1.compareTo(s2);
         }

         private String getKey(Object o)
         {
            if (o == null)
               return "";

            String s = String.valueOf(o);
            try
            {
               s = new String(s.getBytes("gb18030"), "iso-8859-1");
            } catch (UnsupportedEncodingException e)
            {
            }
            return s;
         }
      });
      return principals;
   }

   @SuppressWarnings("unchecked")
   private void load()
   {
      cache = new HashMap<Object, Object>();
      ArrayList topLvl = new ArrayList();// 顶层组名
      HashMap flwLvl = new HashMap();//第二层组名与其对应的子组List
      HashMap lastLvl = new HashMap();//第三层组名与其对应的子组List
      HashMap usrLib = new HashMap();//所有组名与其对应的所有用户List
      boolean accesscheck = SessionServerHelper.manager.setAccessEnforced(false);
      try
      {
         WTGroup topGrp = OrganizationServicesHelper.manager.getGroup("CSIC711", OrganizationServicesHelper.manager.newDirectoryContextProvider((String[]) null, null));
         if (topGrp == null)
         {
            cache.put(KEY_LEVEL1, topLvl);
            cache.put(KEY_LEVEL2, flwLvl);
            cache.put(KEY_LEVEL3, lastLvl);
            cache.put(KEY_USRLIB, usrLib);
//            logger.warn("Can't found Group ", CmConstants.GROUP_TOP, ", empty return.");
            return;
         }
         ArrayList eelist = sortPrincipalEnum(OrganizationServicesHelper.manager.members(topGrp, false));
         for (int i = 0; i < eelist.size(); i++)
         {
            WTPrincipal wp = (WTPrincipal) eelist.get(i);
            if (wp instanceof WTUser)
            {
//               logger.warn("Find User Under Top Group:", ((WTUser) wp).getFullName(), "--Ignore this user.");
            } else if (wp instanceof WTGroup)
            {
               // 顶层，处理topLvl, flwLvl, usrLib
               WTGroup grp = (WTGroup) wp;
               ArrayList topGrpUsr = new ArrayList();
               ArrayList sublist = new ArrayList();

               // modify by LongXiuChuan,修改存放对象为WTGroup，之前是组名
               topLvl.add(grp);

               //end

               flwLvl.put(grp.getName(), sublist);
               usrLib.put(grp.getName(), topGrpUsr);
               ArrayList ee = sortPrincipalEnum(OrganizationServicesHelper.manager.members(grp, false));
               if ("访客".equals(grp.getName())) {
                   ee = sortPrincipalEnum(grp.members());
               }

               for (int j = 0; j < ee.size(); j++)
               {
                  WTPrincipal wpp = (WTPrincipal) ee.get(j);
                  if (wpp instanceof WTUser)
                  {
                     topGrpUsr.add(wpp);
                  } else if (wpp instanceof WTGroup)
                  {
                     // 第二层，处理flwLvl, lastLvl, usrLib
                     WTGroup grpp = (WTGroup) wpp;
                     ArrayList flwGrpUsr = new ArrayList();
                     ArrayList flwSubList = new ArrayList();
                     sublist.add(grpp.getName());
                     lastLvl.put(grpp.getName(), flwSubList);
                     usrLib.put(grpp.getName(), flwGrpUsr);
                     ArrayList userall = sortPrincipalEnum(OrganizationServicesHelper.manager.members(grpp, false));
                     for (int k = 0; k < userall.size(); k++)
                     {
                        WTPrincipal wppp = (WTPrincipal) userall.get(k);
                        if (wppp instanceof WTUser)
                        {
                           flwGrpUsr.add(wppp);
                        } else if (wppp instanceof WTGroup)
                        {
                           // 第三层，处理lastLvl, usrLib，忽略第三层的组
                           WTGroup grppp = (WTGroup) wppp;
                           ArrayList lastGrpUsr = new ArrayList();
                           flwSubList.add(grppp.getName());
                           usrLib.put(grppp.getName(), lastGrpUsr);

                           ArrayList eee = sortPrincipalEnum(OrganizationServicesHelper.manager.members(grppp, true));
                           for (int m = 0; m < eee.size(); m++)
                           {
                              WTPrincipal wpppp = (WTPrincipal) eee.get(m);
                              if (wpppp instanceof WTUser)
                                 lastGrpUsr.add(wpppp);
                           }
                        }
                     }
                  }
               }
            }
         }
      } catch (WTException wte)
      {
//         logger.error("Find Group " + CmConstants.GROUP_TOP +" Exception.");
//         logger.error(wte);
      } finally
      {
         SessionServerHelper.manager.setAccessEnforced(accesscheck);
         cache.put(KEY_LEVEL1, topLvl);
         cache.put(KEY_LEVEL2, flwLvl);
         cache.put(KEY_LEVEL3, lastLvl);
         cache.put(KEY_USRLIB, usrLib);
         lastLoaded = System.currentTimeMillis();

//         logger.debug("LEVEL1 = ", topLvl.size());
//         logger.debug("LEVEL2 = ", flwLvl.size());
//         logger.debug("LEVEL3 = ", lastLvl.size());
//         logger.debug("USRLIB = ", usrLib.size());
      }
   }

   @SuppressWarnings("unchecked")
   synchronized public static HashMap getGroupUser()
   {
      if (!RemoteMethodServer.ServerFlag)
      {
         HashMap<Object, Object> ret = null;
         try
         {
            ret = (HashMap<Object, Object>) RemoteMethodServer.getDefault().invoke("getGroupUser", CmGroupUserCache.class.getName(), null, null, null);
         } catch (RemoteException e)
         {
            e.printStackTrace();
         } catch (InvocationTargetException e)
         {
            e.printStackTrace();
         }
         return ret;
      }

      if (groupUserCache == null)
      {
         groupUserCache = new CmGroupUserCache();
      } else
      {
         groupUserCache.checkCache();
      }
      return groupUserCache.getCache();
   }
}
