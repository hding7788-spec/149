package ext.casc.workflow;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteMethodServer;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

import com.ptc.core.htmlcomp.tableview.TableViewDescriptor;

public class RemoveViewsMain {
	public static void main(String[] args) throws Exception{
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		String username = null;
		String passwd = null;
		if(args.length == 2){
			username = args[0];
			passwd = args[1];
			if(username == null)
				username = "wcadmin";
					
			if(passwd == null)
				passwd = "wcadmin";
		}
		rms.setUserName(username);
		rms.setPassword(passwd);
		
		if(true){
			QuerySpec qs = new QuerySpec(TableViewDescriptor.class);
			qs.appendWhere(new SearchCondition(TableViewDescriptor.class,TableViewDescriptor.TABLE_ID, "=", "ext.casc.workflow.tree.mvc.builder.SetSignatureZPBuilder"));
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while(qr.hasMoreElements()){
				Object obj = qr.nextElement();
				TableViewDescriptor tvd = (TableViewDescriptor) obj;
				System.out.println(tvd.getTableID());
				PersistenceHelper.manager.delete(tvd);
			}
		}
		
		
		if(true){
			QuerySpec qs = new QuerySpec(TableViewDescriptor.class);
			qs.appendWhere(new SearchCondition(TableViewDescriptor.class,TableViewDescriptor.TABLE_ID, "=", "ext.casc.workflow.tree.mvc.builder.SetListSignatureBuilder"));
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while(qr.hasMoreElements()){
				Object obj = qr.nextElement();
				TableViewDescriptor tvd = (TableViewDescriptor) obj;
				System.out.println(tvd.getTableID());
				PersistenceHelper.manager.delete(tvd);
			}
		}
	}
}
