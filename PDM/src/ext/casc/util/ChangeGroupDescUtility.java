package ext.casc.util;

import java.util.List;

import wt.fc.PersistenceServerHelper;
import wt.fc.WTObject;
import wt.inf.container.OrgContainer;
import wt.method.RemoteMethodServer;
import wt.org.WTGroup;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import ext.casc.process.util.ProcessUtil;

/**
 * update glcilink t set t.cipartnumber = 'ML'||t.cipartnumber
update glcipartlink t set t.cipartnumber = 'ML'||t.cipartnumber
 * @author Administrator
 *
 */
public class ChangeGroupDescUtility {

	public static void main(String[] args) throws Exception {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		String username = null;
		String passwd = null;
		if (args.length >= 2) {
			username = args[0];
			passwd = args[1];
			if (username == null)
				username = "wcadmin";

			if (passwd == null)
				passwd = "wcadmin";
		}
		System.out.println("------user:"+username+"    password:"+passwd);
		rms.setUserName(username);
		rms.setPassword(passwd);
		if (!RemoteMethodServer.ServerFlag) {

		}
	}

	public static void process() throws WTPropertyVetoException, WTException {
		OrgContainer orgContainer = ProcessUtil.getOrgContainer();
        List list = ProcessUtil.getNodes(orgContainer);
        if (list != null) {
            WTGroup group = null;
            for (int i = 0; i < list.size(); i++) {
                Object object = list.get(i);
                if (object instanceof WTGroup) {
                	  group = (WTGroup) object;
                      group.setDescription(group.getName());
                      PersistenceServerHelper.manager.update(group);
                      System.out.println("更新+"+group.getName()+"描述为："+group.getDescription());
                }
            }
        }
	}

}
