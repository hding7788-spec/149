/**
 *
 */
package ext.sast.center.synch;

import com.ptc.core.foundation.type.server.impl.TypeHelper;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.extend.ixb.center.*;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changerequest.ChangeRequest;
import ext.ases.envelope.EnvelopeHelper;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.constants.Constants;
import ext.casc.ixb.CmExportHandler;
import ext.casc.ixb.ExpImpLogger;
import ext.casc.preview.Preview;
import wt.doc.WTDocument;
import wt.ixb.clientAccess.StandardIXBService;
import wt.util.WTException;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author cfire
 *
 */
public class MQDataExportService {
	 public String exportProcessEnvelopeTargets(ProcessEnvelope pe)
	            throws WTException {
	        ArrayList list = EnvelopeHelper.service.getAllMembers(pe);
	        list.add(pe);
	        return exportObjects(list);
	  }

	 public static String exportObjects(List objects) throws WTException {
	        ExpImpLogger logs = ExpImpLogger.getInstance();
	        logs.log("Begin to export objects to jar file...");
	        if (objects.size() <= 0) {
	            logs.log("==>Nothing to export.");
	            return null;
	        }
	        File fileonserver = StandardIXBService.getSaveFileOnServer();
	        CmExportHandler exphnd = new CmExportHandler(fileonserver);
	        exphnd.writeManifest(CmExportHandler.getLocalExchangeContainerURL());
	        Iterator it = objects.iterator();
	        while (it.hasNext()) {
	            Object obj = it.next();
	            if (obj instanceof WTDocument) {
	            	//只对工艺通知单
	            	WTDocument doc = (WTDocument)obj;
	            	TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(doc);
					if(TypeHelper.isA(ti, TypeHelper.getTypeIdentifier(Constants.PROCESS_NOTICE_DOCUMENT))){
						new MQExpImpWTDocument(exphnd).exportObject(doc);
						//增加文档批注信息
						new MQExpWTMarkUp(exphnd).exportObject(obj);
					}else{
						//增加文档批注信息
						new MQExpWTMarkUp(exphnd).exportObject(obj);
					}
				}else if(obj instanceof ProcessEnvelope){
				    new MQExpImpProcessEnvelope(exphnd).exportObject(obj);
				}else if(obj instanceof ChangePackaged){
				    new MQExpImpChangePackaged(exphnd).exportObject(obj);
				}else if(obj instanceof ChangeRequest){
	                new MQExpImpChangeRequest(exphnd).exportObject(obj);
	            }else if(obj instanceof Preview){
	                new MQExpImpPreview(exphnd).exportObject(obj);
	            }else{
				    new MQExpWTMarkUp(exphnd).exportObject(obj);
				}
	        }
	        exphnd.finalizeJar();
	        String fullfile = fileonserver.getName();
	        logs.log((new StringBuilder()).append("Done Export to File:")
	                .append(fullfile).toString());
	        return fullfile;
	    }

	/**
	 * @param doc
	 * @return
	 * @throws WTException
	 */
	public String exportProcessNotice(WTDocument doc) throws WTException {
		List list = new ArrayList();
		list.add(doc);
		return exportObjects(list);
	}

	public String exportCommonProcess(WTDocument doc) throws WTException {
		List list = new ArrayList();
		list.add(doc);
		return exportObjects(list);
	}

}
