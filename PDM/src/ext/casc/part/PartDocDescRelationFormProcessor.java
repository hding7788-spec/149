package ext.casc.part;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;
import com.ptc.windchill.enterprise.part.forms.PartDocRelationDefaultObjectFormProcessor;
import com.ptc.windchill.enterprise.util.PartManagementHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.WTReference;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTProductInstance2;
import wt.session.SessionHelper;
import wt.util.WTException;

/**
 * author:chenming
 * data:2015-10-21
 * **/




public class PartDocDescRelationFormProcessor extends PartDocRelationDefaultObjectFormProcessor
{
  public FormResult doOperation(NmCommandBean paramNmCommandBean, List<ObjectBean> paramList)
    throws WTException
  {
    WTCollection localWTCollection = null;
    Vector localVector = new Vector();
    Persistable localPersistable = paramNmCommandBean.getPrimaryOid().getWtRef().getObject();
    String str = "relatedPartsDocuments";
    FormResult localFormResult = new FormResult();
    localFormResult.setStatus(FormProcessingStatus.SUCCESS);
    Object localObject1;
    Object localObject2;
    Iterator localIterator;
    Object localObject3;
    ArrayList arrayList=new ArrayList();
    if ((localPersistable instanceof WTPart)) {
    	 boolean bool2 = WIPUtils.isCheckOutValid(localPersistable, "full");
    	 if(!bool2){
    		      localObject1 = SessionHelper.getLocale();
    		      localFormResult.addFeedbackMessage(PartManagementHelper.getAddFbMsg(FeedbackType.FAILURE, localVector, (Locale)localObject1));
    		      localFormResult.setStatus(FormProcessingStatus.FAILURE);
    	 }else{

      localObject1 = (WTPart)localPersistable;
      Object localObject12 = new WTArrayList();
      ArrayList localArrayList = paramNmCommandBean.getSelected();
      if ((localArrayList != null) && (localArrayList.size() > 0)) {
        for (int i = 0; i < localArrayList.size(); i++) {
          NmContext localNmContext = (NmContext)localArrayList.get(i);
          Persistable localPersistable1 = localNmContext.getTargetOid().getWtRef().getObject();
          if (localPersistable1 != null)
          {
            Object localObject;
            if ((localPersistable1 instanceof WTDocument)) {
              arrayList.add((WTDocument)localPersistable1);
            	WTPartDescribeLink link=  WTPartDescribeLink.newWTPartDescribeLink((WTPart) localObject1, (WTDocument)localPersistable1);
            	PersistenceServerHelper.manager.insert(link);
            	PersistenceHelper.manager.refresh(link);
            }
          }
        }

      }
//      localObject2 = localObject1;
//      for (localIterator = localWTCollection.persistableIterator(); localIterator.hasNext(); ) {
//        localObject3 = localIterator.next();
//        if ((localObject3 instanceof WTPart)) {
//          localObject2 = (WTPart)localObject3;
//        }
//        else {
//          localVector.add(localObject3);
//        }
//      }
//      if (((WTPart)localObject1).equals(localObject2))
//      {
//        this.redirectURL = null;
//      }
//      else
//      {
//        this.redirectURL = getURL(localObject2, str);
//      }
    }

//
    }
    super.doOperation(paramNmCommandBean, paramList);
    return (FormResult)(FormResult)localFormResult;

  }
}
