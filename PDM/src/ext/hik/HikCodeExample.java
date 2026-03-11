package ext.hik;

import com.glaway.mpm.util.ReferenceFactory;
import wt.conflict.ConflictResolution;
import wt.enterprise.CopyObjectInfo;
import wt.enterprise.CopyRules;
import wt.enterprise.EnterpriseHelper;
import wt.epm.*;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTSet;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.Iterated;
import wt.vc.VersionControlConflictType;
import wt.vc.VersionControlHelper;
import wt.vc.VersionControlResolutionType;

import java.util.HashMap;
import java.util.Map;

public class HikCodeExample {

    //VersionControlHelper.service.deleteIteratios
    private void saveAsEpm(EPMDocument epm, EPMDocument drwEpm) throws WTException, WTPropertyVetoException {
        Map<String,String> newEpmParams = new HashMap<String, String>();

        EPMDocument newEPMDoc = saveAsNewEpmDocument(epm,newEpmParams);
        EPMDocument newDrwEPMDoc = saveAsNewEpmDocument(drwEpm,newEpmParams);
        CopyObjectInfo copyObjectInfo1 = new CopyObjectInfo(epm,newEPMDoc,new CopyRules());
        CopyObjectInfo copyObjectInfo2 = new CopyObjectInfo(drwEpm,newDrwEPMDoc,new CopyRules());
        CopyObjectInfo[] copyObjectInfos = new CopyObjectInfo[]{copyObjectInfo1,copyObjectInfo2};
        CopyObjectInfo[] newCopyObjectInfos  = EnterpriseHelper.service.saveMultiObjectCopy(copyObjectInfos);
        EPMDocument newCpEpmDoc =(EPMDocument) newCopyObjectInfos[0].getCopy();
        EPMDocument newCpDrwEpmDoc =(EPMDocument) newCopyObjectInfos[1].getCopy();

    }

    private EPMDocument saveAsNewEpmDocument(EPMDocument epm, Map<String, String> newEpmParams) throws WTPropertyVetoException, WTException {
        String name = newEpmParams.get("name");
        String cadName = newEpmParams.get("cadName");
        String cadNumber = newEpmParams.get("cadNumber");
        String folderPath = newEpmParams.get("folderPath");
        EPMDocumentMaster copyEPMDocumentMaster =(EPMDocumentMaster) epm.getMaster();
        EPMAuthoringAppType authoringAppType = epm.getAuthoringApplication();
        EPMDocumentType docType = epm.getDocType();
        EPMContextHelper.setApplication(EPMApplicationType.toEPMApplicationType("EPM"));
        EPMDocument newEPMDoc = EPMDocument.newEPMDocument(cadNumber,name,authoringAppType,docType,cadName);
        WTContainer wtContainer = null;
        Folder folder = FolderHelper.service.getFolder(folderPath, WTContainerRef.newWTContainerRef(wtContainer));
        FolderHelper.assignLocation((FolderEntry)newEPMDoc,folder);
        newEPMDoc.setTypeDefinitionReference(epm.getTypeDefinitionReference());
        EPMDocumentMaster epmDocumentMaster = (EPMDocumentMaster)newEPMDoc.getMaster();
        epmDocumentMaster.setTypeDefinitionReference(copyEPMDocumentMaster.getTypeDefinitionReference());
        return newEPMDoc;
    }

    public void deleteIteration(String oid) throws WTException, WTPropertyVetoException {
        Iterated iterated =  (Iterated)ReferenceFactory.getObjectbyOid(oid);
        ConflictResolution[] resolutions = {
            new ConflictResolution(VersionControlConflictType.LATEST_ITERATION_DELETE, VersionControlResolutionType.ALLOW_LATEST_ITERATION_DELETE)
        };
        WTSet set = new WTHashSet();
        set.add(iterated);
        //可以删除中间版本，如A.1,A.2,A.3  可以传A.2版本删除
        VersionControlHelper.service.deleteIterations(set,resolutions);
    }
}
