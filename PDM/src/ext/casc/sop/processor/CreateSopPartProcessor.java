package ext.casc.sop.processor;

import com.glaway.mpm.intf.SopProcessEditorToWCIntfRMI;
import com.glaway.mpm.util.Constant;
import com.glaway.mpm.util.FolderUtil;
import com.glaway.mpm.util.WTContainerUtil;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.CreateObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.sop.constants.SopConstants;
import ext.casc.sop.util.SopUtil;
import ext.casc.util.IBAUtility;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.inf.library.WTLibrary;
import wt.part.WTPart;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.views.ViewReference;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CreateSopPartProcessor extends CreateObjectFormProcessor {

    @Override
    public FormResult doOperation(NmCommandBean nmCommandBean, List<ObjectBean> objectBeans) throws WTException {
        boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
        FormResult formResult = new FormResult();
        formResult.setStatus(FormProcessingStatus.SUCCESS);
        String professionalCode = "";
        String stationNo = "";
        String department = "";
        professionalCode = nmCommandBean.getText().get("professionalCode").toString();
        stationNo = nmCommandBean.getText().get("stationNo").toString();
        department = ((ArrayList) nmCommandBean.getComboBox().get("department")).get(0).toString();
        String specializedTypeValue = nmCommandBean.getTextParameter("specializedTypeValue");
        String proceduceNameValue = nmCommandBean.getTextParameter("proceduceNameValue");
        Iterator iterator1 = objectBeans.iterator();
        String term;
        String select = ((ArrayList)nmCommandBean.getComboBox().get("SECRET")).get(0).toString();
        if(SopConstants.SOP_MSG_MIMI.equals(select) || SopConstants.SOP_MSG_JIMI.equals(select)){
            term = ((ArrayList)nmCommandBean.getComboBox().get("term")).get(0).toString();
        }else{
            term = nmCommandBean.getText().get("termtext").toString();
        }
        try {
            while (iterator1.hasNext()) {
                ObjectBean objectbean = (ObjectBean) iterator1.next();
                if (objectbean.getObject() != null && (objectbean.getObject() instanceof Persistable)) {
                    System.out.println("objectbean.getObject() is: " + objectbean.getObject());
                    WTPart wtPart = (WTPart) objectbean.getObject();
                    String pre = "S" + professionalCode + "-" + stationNo + "-";
                    String seqNumber = SopUtil.getSopSeqNumber(2,pre);
                    wtPart.setNumber(pre + seqNumber);
                    // 保存为持久对象
                    WTLibrary wtContainer = WTContainerUtil.getLibraryByName(SopConstants.SOP_CONTAINER_GYZSK);
                    wtPart.setContainer(wtContainer);
                    String folderPath = SopConstants.SOP_FOLDOR_BOM + specializedTypeValue + "/" + proceduceNameValue;
                    Folder folder = FolderUtil.getFolder(folderPath, WTContainerRef.newWTContainerRef(wtContainer));
                    ViewReference viewReference = WTPartUtil.getViewReferenceOfPartByName(SopConstants.PBOM_VIEW);
                    wtPart.setView(viewReference);
                    FolderHelper.assignFolder(wtPart, folder);
                    PersistenceHelper.manager.save(wtPart);
                    IBAUtility ibaUtility = new IBAUtility(wtPart);
                    ibaUtility.setIBAValue(SopConstants.SOP_IBA_SPECIALIZEDTYPE, specializedTypeValue);
                    ibaUtility.setIBAValue(SopConstants.SOP_IBA_PROCEDUCENAME, proceduceNameValue);
                    ibaUtility.setIBAValue(SopConstants.SOP_IBA_PROFESSIONALCODE, professionalCode);
                    ibaUtility.setIBAValue(SopConstants.SOP_IBA_GONGXUJIANHAO, stationNo);
                    ibaUtility.setIBAValue(SopConstants.SOP_IBA_DEPARTMENT, department);
                    ibaUtility.setIBAValue(SopConstants.SOP_IBA_TERM, term);
                    ibaUtility.setIBAValue(SopConstants.SOP_IBA_SECRET, select);
                    wtPart = (WTPart) ibaUtility.updateAttributeContainer(wtPart);
                    IBAUtility.updateIBAHolder(wtPart);
                }
                SessionServerHelper.manager.setAccessEnforced(enforce);
            }
        } catch (WTPropertyVetoException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return formResult;
    }
}
