package com.glaway.mpm.sop.util;

import com.glaway.mpm.EditorConfig;
import com.glaway.mpm.log.VaLogger;
import wt.method.RemoteMethodServer;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class SopIntfUtil {

    private static VaLogger logger = VaLogger.getLogger(SopIntfUtil.class.getName());
    private static String SERVICE_NAME = "com.glaway.mpm.intf.SopProcessEditorToWCIntfRMI";

    public static Object getRemoteMethodInvoke(String mentodName, Class<?>[] classArray, Object[] objectArray) {
        if(EditorConfig.isWebInfLib){
            try {
                Class server = Class.forName(SERVICE_NAME);
                return server.getMethod(mentodName, classArray).invoke(null, objectArray);
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }else {
            RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
            Object object = null;
            try {
                long startTime = System.currentTimeMillis();
                logger.debug("=====>>>>>开始执行远程调用方法  mentodName:" + mentodName
                        + "   Class[]:" + Arrays.toString(classArray)
                        + "   Object[]:" + Arrays.toString(objectArray));
                object = methodServer.invoke(mentodName, SERVICE_NAME, null, classArray, objectArray);
                long endTime = System.currentTimeMillis();
                logger.debug("=====>>>>>执行远程调用方法 " + mentodName + "  耗时：" + (endTime - startTime) + " ms\r\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return object;
        }
    }
}
