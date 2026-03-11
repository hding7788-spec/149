package ext.casc.fileprint.cache;

import ext.sast.common.fc.CmPersistenceHelper;
import wt.doc.WTDocument;

public class GLFilePrintDataService {
    public static GLFilePrintData saveOrUpdate(GLFilePrintData record) {
        try {
            CmPersistenceHelper.manager.save(record);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return record;
    }

    public static GLFilePrintData getObject(WTDocument doc) {
        try {
           return (GLFilePrintData) CmPersistenceHelper.manager.find(GLFilePrintData.class,doc.getPersistInfo().getObjectIdentifier().getStringValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
