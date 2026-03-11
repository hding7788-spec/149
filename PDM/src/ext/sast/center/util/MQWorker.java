/**
 *
 */
package ext.sast.center.util;

import org.json.JSONObject;

import wt.admin.AdministrativeDomainHelper;
import wt.httpgw.GatewayAuthenticator;
import wt.method.MethodContext;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.session.SessionAuthenticator;
import wt.session.SessionHelper;

/**
 * @author cfire
 *
 */
public class MQWorker extends Thread{
	@Override
	public void run() {
		for(int i=0;i<=5;i++){
	        try {
	        	 System.out.println("线程号："+Thread.currentThread().getId()+"的第"+i+"次");
	        	 MethodContext mc = MethodContext.getContext(Thread.currentThread());
	        	 System.out.println(mc);
	        	 if (mc == null){
	        		 System.out.println("找不到上下文，重新创建");
	                 mc = new MethodContext(null, null);
	        	 }
	        	 if (mc.getAuthentication() == null) {
	                 SessionAuthenticator sa = new SessionAuthenticator();
	                 mc.setAuthentication(sa.setUserName(AdministrativeDomainHelper.ADMINISTRATOR_NAME));
	             }
	        	 WTPrincipal loginUser = null;
	             try{
	             	loginUser = SessionHelper.manager.getPrincipal();
	             	 System.out.println(loginUser);
	             	 mc.unregister();
	             	 System.out.println("unregister之后上下文的值："+mc);
	             }catch(Exception e){
	             	e.printStackTrace();
	             	GatewayAuthenticator auth = new GatewayAuthenticator();
	             	auth.setRemoteUser("administrator");
	 	    		try {
	 					loginUser = SessionHelper.manager.getPrincipal();
	 	        		System.out.println(loginUser);

	 				} catch (Exception e1) {
	 					// TODO Auto-generated catch block
	 					e1.printStackTrace();
	 				}
	             }
	        }catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
			}
		}
	}
	public static void test2(String msg){
		System.out.println("已经远程调用3");
        WTPrincipal loginUser = null;
        try{
        	loginUser = SessionHelper.manager.getPrincipal();
        	System.out.println(loginUser);
        }catch(Exception e){
        	e.printStackTrace();
        	GatewayAuthenticator auth = new GatewayAuthenticator();
        	auth.setRemoteUser("administrator");
    		try {
				loginUser = SessionHelper.manager.getPrincipal();
        		System.out.println(loginUser);

			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	}
}

	public static void test(JSONObject msg){
			System.out.println("已经远程调用3");

	}

}
