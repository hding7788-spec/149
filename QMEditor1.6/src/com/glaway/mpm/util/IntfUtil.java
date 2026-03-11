package com.glaway.mpm.util;

import com.glaway.mpm.EditorConfig;
import com.glaway.mpm.log.VaLogger;
import wt.method.RemoteMethodServer;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.Arrays;
public class IntfUtil {

	private static VaLogger logger = VaLogger.getLogger(IntfUtil.class.getName());
	private static String SERVICE_NAME = "com.glaway.mpm.intf.ProcessEditorToWCIntfRMI";

	public static Object getRemoteMethodInvoke(String mentodName, Class<?>[] classArray,
			Object[] objectArray) {
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

		}
		RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
		Object object = null;
		try {
			System.out.println();
			long startTime = System.currentTimeMillis();
			logger.debug("=====>>>>>开始执行远程调用方法  mentodName:"+mentodName
					+"   Class[]:"+Arrays.toString(classArray)
					+"   Object[]:"+Arrays.toString(objectArray));
			object = methodServer.invoke(mentodName, SERVICE_NAME, null, classArray, objectArray);
			long endTime = System.currentTimeMillis();
			logger.debug("=====>>>>>执行远程调用方法 " + mentodName + "  耗时：" + (endTime - startTime) + " ms\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return object;
	}

	public static Object getPbomRemoteMethodInvoke(String mentodName,
			Class[] classArray, Object[] objectArray) {
		return getRemoteMethodInvoke(mentodName, classArray, objectArray,
				"com.glaway.mpm.intf.PBOMEditorToWCIntfRMI");
	}

	public static Object getPeRemoteMethodInvoke(String mentodName,
			Class[] classArray, Object[] objectArray) {
		return getRemoteMethodInvoke(mentodName, classArray, objectArray,
				"com.glaway.mpm.intf.ProcessEditorToWCIntfRMI");
	}

	public static Object getRemoteMethodInvoke(String mentodName,
			Class[] classArray, Object[] objectArray, String name) {
		Object object = null;

		if(EditorConfig.isWebInfLib){
			try {
				Class server = Class.forName(name);
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

		}else{
			RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
			try {
				object = methodServer.invoke(mentodName, name, null, classArray,
						objectArray);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return object;
		}
		return object;


	}
	private static final String PARAMETER_SERVERNAME = "com.glaway.mpm.intf.ProcessParameterToWCIntfRMI";

	public static Object parameterRemoteMethodInvoke(String mentodName,
			Class<?>[] classArray, Object[] objectArray) throws RemoteException,
			InvocationTargetException {
		Object object = null;

		if(EditorConfig.isWebInfLib){
			try {
				Class server = Class.forName(PARAMETER_SERVERNAME);
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

		}else{
			RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
			try {
				object = methodServer.invoke(mentodName, PARAMETER_SERVERNAME, null, classArray, objectArray);
			} catch (Exception e) {
			}
			return object;
		}
		return object;
	}
	private static final String MES_PARAMETER_SERVERNAME = "com.glaway.mpm.intf.ProcessMESParameterToWCIntfRMI";

	public static Object mesParameterRemoteMethodInvoke(String methodName,Class<?>[] classArray, Object[] objectArray) throws RemoteException,
			InvocationTargetException {
		Object object = null;
		if(EditorConfig.isWebInfLib){
			try {
				Class server = Class.forName(MES_PARAMETER_SERVERNAME);
				return server.getMethod(methodName, classArray).invoke(null, objectArray);
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

		}else{
			RemoteMethodServer methodServer = RemoteMethodServer.getDefault();

			try {
				object = methodServer.invoke(methodName, MES_PARAMETER_SERVERNAME, null, classArray, objectArray);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return object;
		}
		return object;

	}

	private static final String MESDataSearch_SERVERNAME = "com.glaway.mpm.intf.ProcessMESDataSearchToWCIntfRMI";

	public static Object mesDataSearchRemoteMethodInvoke(String methodName,Class<?>[] classArray, Object[] objectArray) throws RemoteException,
			InvocationTargetException {
		Object object = null;

		if(EditorConfig.isWebInfLib){
			try {
				Class server = Class.forName(MESDataSearch_SERVERNAME);
				return server.getMethod(methodName, classArray).invoke(null, objectArray);
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

		}else{
			RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
			try {
				object = methodServer.invoke(methodName, MESDataSearch_SERVERNAME, null, classArray, objectArray);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return object;

		}
		return object;
	}

}
