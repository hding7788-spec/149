/**
 *
 */
package ext;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import org.apache.commons.codec.binary.Base64;

import wt.doc.WTDocument;
import wt.httpgw.GatewayAuthenticator;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import ext.casc.util.WTUtil;

/**
 * @author cfire
 *
 */
public class TestDelete implements RemoteAccess{
	public static void main(String[] args) {
				byte[] result = Base64.decodeBase64("aHR0cHM6Ly9qb2luLnYyZmx5LmNsdWIvIy9yZWdpc3Rlcj9jb2RlPW15ajZMVGJk");
				System.out.println(new String(result));
			//test("5");

	}

	public static void test(String s) throws MalformedURLException{
		if (!RemoteMethodServer.ServerFlag) {
			System.out.println("test");

			RemoteMethodServer ms= RemoteMethodServer.getInstance(new URL("http://192.168.1.5/Windchill/"));
			//RemoteMethodServer ms = RemoteMethodServer.getDefault();
			ms.setUserName("wcadmin");
			//				server.setPassword("wcadmin");
			ms.setPassword("Admin@149");
			try {
				ms.invoke("test", TestDelete.class.getName(), null,
						new Class[] {String.class},
						new Object[] {s});
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

				WTDocument doc = WTUtil.findDoc("testAbcd", "space");
				System.out.println(doc);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



		}
	}
}
