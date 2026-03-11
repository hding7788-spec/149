
/**
 *
 */
package ext;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;

import wt.epm.EPMDocument;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.session.SessionHelper;
import ext.casc.util.WTUtil;
import ext.casc.workflow.CmWorkflowHelper;

/**
 * @author cfire
 *
 */
public class TestRemote implements RemoteAccess{
	public static void main(String[] args) {
		try {
			test("5");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void test(String s) throws MalformedURLException{
		if (!RemoteMethodServer.ServerFlag) {
			System.out.println("test");

			RemoteMethodServer ms= RemoteMethodServer.getInstance(new URL("http://192.168.1.5/Windchill/"));
			//RemoteMethodServer ms = RemoteMethodServer.getDefault();
//			GatewayAuthenticator auth = new GatewayAuthenticator();
//			auth.setRemoteUser("administrator");
//			ms.setAuthenticator(auth);

			ms.setUserName("wcadmin");
			//				server.setPassword("wcadmin");
			ms.setPassword("Admin@149");
			try {
				ms.invoke("test", Test2.class.getName(), null,
						new Class[] {},
						new Object[] {});
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println(s);
			WTPrincipal p;
			try {
				p = SessionHelper.manager.getPrincipal();
				System.out.println(p.getName());

				EPMDocument epm = WTUtil.findEPM("JIGUI_21.ASM", "space");
		    	Set<String> hasChanged = new HashSet<String>();
				CmWorkflowHelper.changeObjNumberOrDeleteObj(epm,false,hasChanged);
				System.out.println(epm);


				WTPart part = WTUtil.findPart("JIGUI_21.ASM", "space");
				CmWorkflowHelper.changeObjNumberOrDeleteObj(part,false,hasChanged);
				System.out.println(part);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



		}
	}
}
