package ext.casc.workflow.tree;

import ext.sast.common.fc.CmPersistenceHelper;
import ext.sast.common.fc.CmQueryResult;
import ext.sast.common.fc.CmQuerySpec;
import wt.session.SessionServerHelper;

/**
 * 类功能：
 *
 * @author cjh
 * @date 2025-02-13
 */
public class ActivityRecordHelper {

    public static GWActivityRecord getActivityRecord(String workitemOid, String verOid) {
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            CmQuerySpec qs = new CmQuerySpec(GWActivityRecord.class);
            qs.appendWhere(GWActivityRecord.WORKITEMOID, CmQuerySpec.EQUAL, workitemOid);
            qs.appendAnd();
            qs.appendWhere(GWActivityRecord.VEROID, CmQuerySpec.EQUAL, verOid);
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            if(qr.hasNext()) {
                GWActivityRecord record = (GWActivityRecord) qr.next();
                return record;
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return null;
    }

}
