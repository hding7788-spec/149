package ext.casc.workflow.signtrue.zp;

import wt.fc.Persistable;
import wt.org.WTUser;
import wt.workflow.work.WorkItem;

public interface ISignatureParser {
	public boolean hasPrivilege(String useroid,String empoid,WorkItem wi);
	public boolean hasPrivilege(Persistable obj,WorkItem wi);
	public boolean hasPrivilege(Persistable obj,WTUser user,WorkItem wi);

	public boolean hasKRPrivilege(Persistable obj);
}
