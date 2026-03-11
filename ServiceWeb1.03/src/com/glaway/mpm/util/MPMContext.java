package com.glaway.mpm.util;

import java.rmi.RemoteException;
import java.util.Locale;

import wt.auth.Authentication;
import wt.method.AuthenticationException;
import wt.method.RemoteMethodServer;
import wt.util.WTRemoteException;

import com.ptc.jws.JWSUtil;
import com.ptc.net.auth.NetAuthenticatorInstaller;

public class MPMContext {
	private static final String CLASSNAME = MPMContext.class.getName();
	private static boolean connected = false;
	private static boolean isJWS = false;
	private static String password = null;
	private static String user = null;
	private static Locale locale = Locale.SIMPLIFIED_CHINESE;

	private static String application = null;
	private static String taskType = null;
	private static String param1 = null;
	private static String param2 = null;
	private static String param3 = null;
	private static String param4 = null;
	private static String param5 = null;
	private static String param6 = null;
	private static String param7 = null;
	private static String category = null;

	public static String getParam8() {
		return param8;
	}

	public static void setParam8(String param8) {
		MPMContext.param8 = param8;
	}

	private static String param8 = null;
	private static String param9 = null;
	private static String param10 = null;

	public static String getParam9() {
		return param9;
	}

	public static void setParam9(String param9) {
		MPMContext.param9 = param9;
	}

	public static String getParam10() {
		return param10;
	}

	public static void setParam10(String param10) {
		MPMContext.param10 = param10;
	}

	/**
	 * logonToServer: constructor
	 *
	 * @param user
	 *            - String userName
	 * @param password
	 *            - String password
	 **/
	// ------------------------------------------------
	public static void logonToServer(String user, String password) throws Exception {
		setUser(user);
		setPassword(password);
		logonToServer();
	}

	public static void logonToServer(String authorization) {
		setCred(authorization);
		logonToServer();
	}

	public static void logonToServer() {
		if (!connected) {
			RemoteMethodServer rmi = RemoteMethodServer.getDefault();
			rmi.setUserName(getUser());
			rmi.setPassword(getPassword());
			try {
				rmi.getInfo();
			} catch (RemoteException remoteexception) {
				if ((remoteexception instanceof WTRemoteException)
						&& (((WTRemoteException) remoteexception).getNestedThrowable() instanceof AuthenticationException)) {
					return;
				} else {
					if (JWSUtil.isInJWS())
						JWSUtil.shutdown();
					return;
				}
			}
			NetAuthenticatorInstaller.install();
			NetAuthenticatorInstaller.netAuthenticator.setCredentials(null, null, null, getUser(), false, getPassword());
			try {
				Authentication.getUserName();
			} catch (RemoteException re) {
				System.out.println(CLASSNAME+"----re:"+re.getLocalizedMessage());
				if ((re instanceof WTRemoteException)
						&& (((WTRemoteException) re).getNestedThrowable() instanceof AuthenticationException)) {
					if (JWSUtil.isInJWS())
						JWSUtil.shutdown();
					return;
				}
			}
			connected = true;
		}
	}

	public static boolean getConnected() {
		return connected;
	}

	public static String getApplication() {
		return application;
	}

	public static void setApplication(String application) {
		MPMContext.application = application;
	}

	public static String getCred() {
		return "Basic " + MPMBase64.encode(user + ":" + password);
	}

	public static String getPassword() {
		return password;
	}

	public static String getUser() {
		return user;
	}

	public static void setCred(String authorization) {
		if (authorization != null) {
			String s4 = MPMBase64.decode(authorization);
			int i = s4.indexOf(':');
			if (s4 != null) {
				user = s4.substring(0, i);
				password = s4.substring(i + 1);
			}
		}
	}

	public static void setPassword(String password) {
		MPMContext.password = password;
	}

	public static void setUser(String user) {
		MPMContext.user = user;
	}

	public static void setJWS(boolean b) {
		isJWS = b;
	}

	public static boolean isJWS() {
		return isJWS;
	}

	public static Locale getLocale() {
		return locale;
	}

	public static void setLocale(Locale locale) {
		MPMContext.locale = locale;
	}

	public static String getTaskType() {
		return taskType;
	}

	public static void setTaskType(String taskType) {
		MPMContext.taskType = taskType;
	}

	public static String getParam1() {
		return param1;
	}

	public static void setParam1(String param1) {
		MPMContext.param1 = param1;
	}

	public static String getParam2() {
		return param2;
	}

	public static void setParam2(String param2) {
		MPMContext.param2 = param2;
	}

	public static String getParam3() {
		return param3;
	}

	public static void setParam3(String param3) {
		MPMContext.param3 = param3;
	}

	public static String getParam4() {
		return param4;
	}

	public static void setParam4(String param4) {
		MPMContext.param4 = param4;
	}

	public static String getParam5() {
		return param5;
	}

	public static void setParam5(String param5) {
		MPMContext.param5 = param5;
	}

	public static String getParam6() {
        return param6;
    }

    public static void setParam6(String param6) {
        MPMContext.param6 = param6;
    }

    public static void setConnected(boolean connected) {
		MPMContext.connected = connected;
	}

    public static String getParam7() {
        return param7;
    }

    public static void setParam7(String param7) {
        MPMContext.param7 = param7;
    }

}
