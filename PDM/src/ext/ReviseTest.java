package ext;

import com.glaway.mpm.util.ReferenceFactory;
import wt.epm.EPMDocument;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTHashSet;
import wt.folder.FolderHelper;
import wt.iba.value.service.MultiObjIBAValueDBService;
import wt.method.MethodContext;
import wt.pom.Transaction;
import wt.series.MultilevelSeries;
import wt.series.Series;
import wt.util.WTException;
import wt.vc.IterationIdentifier;
import wt.vc.StandardVersionControlService;
import wt.vc.VersionControlHelper;
import wt.vc.VersionIdentifier;

public class ReviseTest {
    public static void main(String[] args) throws Exception {
        EPMDocument newepm = null;
        Transaction tx = new Transaction();
        MethodContext methodcontext = MethodContext.getContext();
        try {
            EPMDocument epm =(EPMDocument) ReferenceFactory.getObjectbyOid("OR:wt.epm.EPMDocument:772519");
            newepm = (EPMDocument) VersionControlHelper.service.newVersionable(epm);
            Series se = VersionControlHelper.getVersionIdentifier(epm).getSeries();
            se.setValueWithoutValidating("Z");
            VersionIdentifier vi = VersionIdentifier.newVersionIdentifier((MultilevelSeries) se);
            Series series = epm.getIterationInfo().getIdentifier().getSeries();
            series.setValueWithoutValidating("1");
            IterationIdentifier ii = IterationIdentifier.newIterationIdentifier(series);
            newepm = (EPMDocument) VersionControlHelper.service.newVersion(epm, true);
            VersionControlHelper.setIterationIdentifier(newepm, ii);
            VersionControlHelper.setVersionIdentifier(newepm, vi, false);
            FolderHelper.assignLocation(newepm, FolderHelper.service.getFolder(epm));
            newepm.setContainerReference(epm.getContainerReference());

            Boolean boolean1 = true;
            if (boolean1 != null)
                newepm.setMissingDependents(boolean1.booleanValue());
            methodcontext.put("ixb_store_object_context/key", newepm);
            WTArrayList wtarraylist = new WTArrayList(1);
            wtarraylist.add(newepm);
            Transaction.getGlobalMap().put(StandardVersionControlService.SVCS_SIGNAL_PERSISTENCE_OF_IGNORE_KEY,
                    true);
            Transaction.getGlobalMap().put(MultiObjIBAValueDBService.DO_NOT_COPY_FORWARD_IBAS_KEY, wtarraylist);
            WTHashSet wthashset = new WTHashSet();
            wthashset.add(newepm);
            Transaction.getGlobalMap().put("PREVENT_OWNERSHIP_CHECK", wthashset);
            if (PersistenceHelper.isPersistent(newepm))
                newepm = (EPMDocument) PersistenceHelper.manager.save(newepm);
            else newepm = (EPMDocument) PersistenceServerHelper.manager.store(newepm, epm.getModifyTimestamp(), epm.getModifyTimestamp());
            methodcontext.remove("ixb_store_object_context/key");
            tx.commit();
            tx = null;
            System.out.println("修订成功");
        } catch (Exception e) {
            if ((e instanceof WTException))
                throw ((WTException) e);
            throw new WTException(e);
        } finally {
            if (tx != null)
                tx.rollback();
            methodcontext.remove("ixb_store_object_context/key");
        }

    }
}
