package ext.casc.util;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteMethodServer;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

import com.ptc.core.htmlcomp.tableview.TableViewDescriptor;

public class RemoveTableViewId {

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

		String tableId = "";
		if (args.length > 2) {
			tableId = args[2];
		}
		if (tableId == null) {
            tableId = "";
        }
		System.out.println("------tableId:"+tableId);
		if (!RemoteMethodServer.ServerFlag) {
			QuerySpec qs = new QuerySpec(TableViewDescriptor.class);
			qs.appendWhere(new SearchCondition(TableViewDescriptor.class, TableViewDescriptor.TABLE_ID, "=",tableId));
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec)qs);
			System.out.println("------qr:"+qr.size());
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				TableViewDescriptor tvd = (TableViewDescriptor) obj;
				System.out.println("-------------TableID:"+tvd.getTableID());
				PersistenceHelper.manager.delete(tvd);
			}
		}
	}

}
