/**project windchillTools**/

package ext.casc.cadsign.wcserver;

import wt.admin.AdministrativeDomainHelper;
import wt.method.MethodContext;
import wt.session.SessionAuthenticator;

/**
 * <p>
 * Description:
 * </p>
 *
 * @version 1.0
 */

public class CADSignThread extends Thread {

	private static boolean runningStatus = false;
	private static final String CLASSNAME = CADSignThread.class.getName();
	//更新运行时间
	private static final long INTERVAL = 12 * 3600 * 1000;

	public static boolean getRunningStatus() {
		return runningStatus;
	}

	public static void setRunningStatus(boolean status) {
		runningStatus = status;
	}

	/**
	 * 执行进程
	 */
	public void run() {
		setRunningStatus(true);
		try {
			MethodContext mc = MethodContext.getContext(Thread.currentThread());
			if (mc == null)
				mc = new MethodContext(null, null);
			if (mc.getAuthentication() == null) {
				SessionAuthenticator sa = new SessionAuthenticator();
				mc.setAuthentication(sa.setUserName(AdministrativeDomainHelper.ADMINISTRATOR_NAME));
			}
		} catch (Throwable t) {
			System.out.println(CLASSNAME  + "扫描签名表------>创建活动上下文出错,信息：" + t.getMessage());
			t.printStackTrace();
		}
		while (true) {
			try {
				sleep(15000); // 起动时15秒后起动
				CADSignUpdate.scanSignTable();
				sleep(12 * 3600 * 1000 - 15000); //12小时运行一次
			} catch (Exception ie) {
				System.out.println(CLASSNAME  + "扫描签名表出错，本服务终止，信息----->" + ie.getMessage());
				ie.printStackTrace();
				break;
			}
		}
	}
}
