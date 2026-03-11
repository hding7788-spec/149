package ext.ases.envelope;

import java.util.HashMap;

import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.ReferenceFactory;
import wt.inf.container.WTContainerRef;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import ext.casc.workflow.TaskConfigrationHelper;
import ext.casc.workflow.util.DocHelper;


public class WorkflowUtil {
	public static String createPrintApplyDoc(Object process) {
//		CSCDebug.outDebugInfo("Now create PrintApplyDoc fuction");
		String processOid = PersistenceHelper.getObjectIdentifier((Persistable)process).toString();
		Object pbo = null;
		try {
			pbo = TaskConfigrationHelper.getPBO(processOid);
		} catch (WTRuntimeException e1) {
			e1.printStackTrace();
		} catch (WTException e1) {
			e1.printStackTrace();
		}
		
		if (pbo instanceof WTDocument){
			HashMap<String, String> attributes = new HashMap<String, String>();
			HashMap<String, Object> softattributes = new HashMap<String, Object>();
			
			attributes.put("FILE_PATH", "");
			attributes.put("TYPE", "wt.doc.Document|casc.sast.805.TESTDOC");
			attributes.put("DEPARTMENT", "");
			attributes.put("DOC_TYPE", "");
			attributes.put("INPUT_STREAM", "");
			attributes.put("FOLDER", "");
			
			softattributes.put("APPROVPROID", processOid);
			ReferenceFactory rf = new ReferenceFactory();
			WTContainerRef wtcf = ((WTDocument)pbo).getContainerReference();
			WTDocument wtd = null;
			try {
				wtd = (WTDocument)DocHelper.service.createDoc(null,"打印申请单"+((WTDocument)pbo).getName(),"",attributes,softattributes,wtcf);
				return PersistenceHelper.getObjectIdentifier(wtd).toString();
			} catch (WTException e) {
				e.printStackTrace();
			}
		}else{
			//当签审的PBO是审签包时
		}
		return null;
	} 
}
