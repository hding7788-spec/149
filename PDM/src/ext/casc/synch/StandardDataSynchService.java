package ext.casc.synch;

import com.ptc.core.foundation.type.server.impl.TypeHelper;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.extend.ixb.*;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changepackaged.ChangePackagedResultLink;
import ext.ases.changerequest.ChangeRequest;
import ext.ases.envelope.EnvelopeHelper;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.constants.Constants;
import ext.casc.ixb.CmExportHandler;
import ext.casc.ixb.ExpImpLogger;
import ext.casc.preview.Preview;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.ixb.clientAccess.StandardIXBService;
import wt.services.StandardManager;
import wt.util.WTException;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StandardDataSynchService extends StandardManager implements
        DataSynchService {

    /**
	 *
	 */
    private static final long serialVersionUID = 7927270429208191097L;

    public static StandardDataSynchService newStandardDataSynchService()
            throws WTException {
        StandardDataSynchService service = new StandardDataSynchService();
        service.initialize();
        return service;
    }



    public String exportProcessEnvelopeTargets(ProcessEnvelope pe)
            throws WTException {
        ArrayList list = EnvelopeHelper.service.getAllMembers(pe);
        list.add(pe);
        return exportObjects(list);
    }


	public String exportChangePackagedTargets(ChangePackaged packaged)
			throws WTException {
		// TODO Auto-generated method stub
		ArrayList list = new ArrayList();
        QueryResult qr = PersistenceHelper.manager.navigate(packaged, ChangePackagedResultLink.ROLE_BOBJECT_ROLE,
                ChangePackagedResultLink.class, true);
        while (qr.hasMoreElements()) {
            list.add(qr.nextElement());
        }
        list.add(packaged);
		return exportObjects(list);
	}

	public String exportChangeRequestTargets(ChangeRequest cr)
            throws WTException {
        // TODO Auto-generated method stub
        ArrayList list = new ArrayList();
        list.add(cr);
        return exportObjects(list);
    }

    public static String exportObjects(List objects) throws WTException {
       return exportObjects(objects,false);
    }

    public static String exportObjects(List objects,boolean isSender) throws WTException {
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
                WTDocument doc = (WTDocument)obj;
                if(isSender){
                    new CmExpImpWTDocument(exphnd).exportObject(doc);
                }else{
                    TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(doc);
                    if(TypeHelper.isA(ti, TypeHelper.getTypeIdentifier(Constants.PROCESS_NOTICE_DOCUMENT))){
                        new CmExpImpWTDocument(exphnd).exportObject(doc);
                        new CmExpWTMarkUp(exphnd).exportObject(obj);
                    }else{
                        //增加文档批注信息
                        new CmExpWTMarkUp(exphnd).exportObject(obj);
                    }
                }

            }else if(obj instanceof ProcessEnvelope){
                new CmExpImpProcessEnvelope(exphnd).exportObject(obj);
            }else if(obj instanceof ChangePackaged){
                new CmExpImpChangePackaged(exphnd).exportObject(obj);
            }else if(obj instanceof ChangeRequest){
                new CmExpImpChangeRequest(exphnd).exportObject(obj);
            }else if(obj instanceof Preview){
                new CmExpImpPreview(exphnd).exportObject(obj);
            }else{
                new CmExpWTMarkUp(exphnd).exportObject(obj);
            }
        }
        exphnd.finalizeJar();
        String fullfile = fileonserver.getName();
        logs.log((new StringBuilder()).append("Done Export to File:")
                .append(fullfile).toString());
        return fullfile;
    }

    /* 将工艺通知单或工艺更改单数据打包发放外部会签单位
	 * @WTDocument doc 工艺通知单或工艺更改单
	 */
	@Override
	public String exportProcessNotice(WTDocument doc) throws WTException {
		//如果是工艺通知单则list为空值，如果为工艺更改单则list中为工艺通知单
		 ArrayList<WTDocument> list = new ArrayList<WTDocument>();
	     list.add(doc);
	     return exportObjects(list);
	}

    @Override
    public String exportCommonProcess(WTDocument doc) throws WTException {
        ArrayList<WTDocument> list = new ArrayList<WTDocument>();
        list.add(doc);
        return exportObjects(list,true);
    }


	@Override
	public String exportPreviewTargets(Preview preview) throws WTException {
		ArrayList list = new ArrayList();
        list.add(preview);
        return exportObjects(list);
	}
}
