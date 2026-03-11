/**
 *
 */
package ext;

import java.rmi.RemoteException;
import java.util.Vector;

import wt.method.MethodServerInfo;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.util.WTContext;

/**
 * @author cfire
 *
 */
public class BgNameTest implements RemoteAccess{
	public static void  test(){
		RemoteMethodServer remote =	RemoteMethodServer.getDefault();
		try {
			Vector v = remote.getAllInfo();
			MethodServerInfo inf = remote.getInfo();
			System.out.println("test");

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WTContext context = WTContext.getContext();
		System.out.println("test");
	}
}
