package ext.sast.center.util;

import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;

/**
 * @ Author     ：LB.
 * @ Date       ：Created in 2019/3/7
 * @ Description：
 * @ Modified By：
 */
public class InvokeUtil implements RemoteAccess {

    public static void main(String[] args) {
        if (!RemoteMethodServer.ServerFlag) {
            try {
                RemoteMethodServer server = RemoteMethodServer.getDefault();
                server.setUserName("wcadmin");
                server.setPassword("Admin@149");
                String method = args[0];
                Class<?>[] types = null;
                Object[] vals = null;
                if ("synchUser".equals(method)) {
                    types = new Class<?>[] { };
                    vals = new Object[] { };
                }
                if (types != null && vals != null) {
                    server.invoke(method, InvokeUtil.class.getName(), null, types, vals);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
