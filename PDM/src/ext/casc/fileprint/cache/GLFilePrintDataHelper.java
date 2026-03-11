package ext.casc.fileprint.cache;

import org.json.JSONObject;
import wt.doc.WTDocument;
import wt.fc.WTObject;

import java.util.Hashtable;

public class GLFilePrintDataHelper {
    public static void cache(Hashtable signHashtable, WTObject obj) {
        if(obj instanceof WTDocument){
            WTDocument doc = (WTDocument)obj;
            String docNumber = doc.getNumber();
            String docVersion = doc.getVersionIdentifier().getValue()+"."+doc.getIterationIdentifier().getValue();

            GLFilePrintData cacheData = new GLFilePrintData();
            cacheData.setKeyId(doc.getPersistInfo().getObjectIdentifier().getStringValue());
            cacheData.setDocNumber(docNumber);
            cacheData.setDocVersion(docVersion);
            JSONObject json = new JSONObject(signHashtable);
            cacheData.setPrintData(json.toString());

            GLFilePrintDataService.saveOrUpdate(cacheData);
        }
    }

    public static GLFilePrintData getObject(WTDocument document) {
        return GLFilePrintDataService.getObject(document);
    }
}
