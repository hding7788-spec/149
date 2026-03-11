package ext.ases.changepackaged;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import wt.enterprise.RevisionControlled;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.log4j.LogR;
import wt.util.WTException;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.CreateObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class CreateChangePackagedFormProcessor extends CreateObjectFormProcessor {
    private static final Logger log;

    static {
        try {
            log = LogR.getLogger(CreateChangePackagedFormProcessor.class.getName());
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public FormResult doOperation(NmCommandBean clientData, List<ObjectBean> objectBeans) throws WTException {
        FormResult phaseResult = new FormResult();
        phaseResult = new FormResult();
        phaseResult.setStatus(FormProcessingStatus.SUCCESS);
        phaseResult = super.doOperation(clientData, objectBeans);
        System.out.println("phaseResult:" + phaseResult);

        Iterator iterator1 = objectBeans.iterator();
        do {
            if (!iterator1.hasNext()) {
                break;
            }
            ObjectBean objectbean = (ObjectBean) iterator1.next();
            if (objectbean.getObject() != null && (objectbean.getObject() instanceof Persistable)) {
                System.out.println("objectbean.getObject() is: " + objectbean.getObject());

                ChangePackaged changePackaged = (ChangePackaged) objectbean.getObject();
                PersistenceHelper.manager.refresh(changePackaged);
                // String[] relatedBeforeObjOids
                // =objectbean.getTextParameterValues("changeTask_affectedItems_table__objRef");
                String affectObjInitOids = objectbean.getTextParameter("initialRows_changeTask_affectedItems_table");
                String affectObjAddOids = objectbean.getTextParameter("addRows_changeTask_affectedItems_table");
                String affectObjRmOids = objectbean.getTextParameter("rmRows_changeTask_affectedItems_table");
                String affectObjObjOidsStr = affectObjInitOids + affectObjAddOids;
                String[] tempOids = affectObjRmOids.split("#");
                for (int i = 0; i < tempOids.length; i++) {
                    affectObjObjOidsStr = affectObjObjOidsStr.replace(tempOids[i], "");
                }
                String[] affectObjOids = affectObjObjOidsStr.split("#");

                String resultObjInitOids = objectbean.getTextParameter("initialRows_changeTask_resultingItems_table");
                String resultObjAddOids = objectbean.getTextParameter("addRows_changeTask_resultingItems_table");
                String resultObjRmOids = objectbean.getTextParameter("rmRows_changeTask_resultingItems_table");
                String resultObjOidsStr = resultObjInitOids + resultObjAddOids;
                tempOids = resultObjRmOids.split("#");
                for (int i = 0; i < tempOids.length; i++) {
                    resultObjOidsStr = resultObjOidsStr.replace(tempOids[i], "");
                }
                String[] resultObjOids = resultObjOidsStr.split("#");

                // 如果是从对象下拉菜单里，获得的oid是当前对象的oid，可以用来判断是否成套件，进行TopObject设置
                String[] oids = clientData.getTextParameterValues("oid");
                String objoid = new String();
                if (affectObjOids != null && affectObjOids.length > 0) {
                    saveAffectDataLink(affectObjOids, changePackaged, objectbean);
                }

                if (resultObjOids != null && resultObjOids.length > 0) {
                    saveReultDataLink(resultObjOids, changePackaged, objectbean);
                }

            }
        } while (true);

        return phaseResult;

    }

    private void saveAffectDataLink(String[] oids, ChangePackaged changePackaged, ObjectBean objectbean)
            throws WTException {
        ReferenceFactory rf = new ReferenceFactory();
        for (String oid : oids) {
            System.out.println("***related obj oid=" + oid + "***");
            WTReference ref = rf.getReference(oid);
            Persistable obj = ref.getObject();
            WTObject wtobject;
            if (obj != null && obj instanceof WTObject) {
                wtobject = (WTObject) obj;
                try {
                    ChangePackagedAffectLink affectLink = ChangePackagedAffectLink.newChangePackagedAffectLink(
                            changePackaged, (RevisionControlled) wtobject);
                    PersistenceHelper.manager.save(affectLink);
                } catch (wt.util.WTException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void saveReultDataLink(String[] oids, ChangePackaged changePackaged, ObjectBean objectbean)
            throws WTException {

        ReferenceFactory rf = new ReferenceFactory();
        for(String oid:oids){
            System.out.println("***related obj oid="+oid+"***");            
            WTReference ref=rf.getReference(oid);
            Persistable obj=ref.getObject();
            WTObject wtobject;
            if(obj!=null && obj instanceof WTObject){
                wtobject=(WTObject)obj;
                try
                {
                    ChangePackagedResultLink resultLink = ChangePackagedResultLink.newChangePackagedResultLink(changePackaged,(RevisionControlled)wtobject);                 
                    PersistenceHelper.manager.save(resultLink);
                }
                catch (wt.util.WTException e){
                    e.printStackTrace();
                }
            
            }
        }
    
    }

}
