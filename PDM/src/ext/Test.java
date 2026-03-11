/**
 *
 */
package ext;

import com.glaway.mpm.util.ReferenceFactory;
import com.glaway.mpm.util.WTPartUtil;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.common.PartCommonHelper;
import ext.casc.util.CSCPrincipal;
import ext.casc.util.IBAHelper;
import ext.casc.util.IBAUtility;
import ext.casc.version.VersionCommonHelper;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTHashSet;
import wt.folder.FolderHelper;
import wt.iba.value.service.MultiObjIBAValueDBService;
import wt.method.MethodContext;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.series.MultilevelSeries;
import wt.series.Series;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTStandardDateFormat;
import wt.vc.IterationIdentifier;
import wt.vc.StandardVersionControlService;
import wt.vc.VersionControlHelper;
import wt.vc.VersionIdentifier;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.engine.*;
import wt.workflow.work.WorkItem;
import wt.workflow.work.WorkflowHelper;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * @author cfire
 *
 */
public class Test {
	public static void main(String[] args) throws Exception {
		WTPart part = (WTPart)ReferenceFactory.getObjectbyOid("VR:wt.part.WTPart:207736");
		System.out.println(part.getCreator().getObject());


		WTDocument doc = (WTDocument)ReferenceFactory.getObjectbyOid("VR:wt.doc.WTDocument:5054866");
		System.out.println(doc.getCreator().getObject());


		WTUser wtuser = (WTUser)ReferenceFactory.getObjectbyOid("OR:wt.org.WTUser:126530");
		System.out.println(wtuser.getFullName());

	}


}
