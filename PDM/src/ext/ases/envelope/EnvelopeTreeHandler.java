/**
 * @(#)EnvelopeTreeHandler.java
 *
 *
 * @author Leon Zhang
 * @version 1.00 2010/1/18
 */
package ext.ases.envelope;

import com.ptc.core.components.beans.TreeHandlerAdapter;
import com.ptc.core.htmlcomp.components.TableViewBean;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import org.apache.log4j.Logger;
import wt.doc.DocumentVersion;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.enterprise.RevisionControlled;
import wt.fc.*;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTKeyedMap;
import wt.log4j.LogR;
import wt.part.*;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.vc.config.ConfigHelper;
import wt.vc.config.ConfigSpec;
import wt.workflow.work.WorkItem;

import java.util.*;

public class EnvelopeTreeHandler extends TreeHandlerAdapter {

    public EnvelopeTreeHandler() {
    }


    private static final Logger log;
    private ConfigSpec configSpec;
    public static WTPart rootPart = null;
    public static String viewResult = null;
    public static List topList;
    public static List memberList;

   static {
      try {
         log = LogR.getLogger(EnvelopeTreeHandler.class.getName());

         topList = new ArrayList();
         memberList = new ArrayList();

      } catch (Exception e) {
         throw new ExceptionInInitializerError(e);
      }
   }

   protected ConfigSpec getDefaultConfigSpec() throws WTException {
      return ConfigHelper.service.getDefaultConfigSpecFor(WTPart.class);
   }


   /**
    * Get the root node from the command bean,
    * and get a config spec based on the root node
   **/
   public List getRootNodes() throws WTException {

      // TODO Auto-generated method stub
        List result = new ArrayList();
        NmCommandBean nmcommandbean = getModelContext().getNmCommandBean();
        if(nmcommandbean == null) {
            return null;
        }
        NmOid primaryOid = nmcommandbean.getPrimaryOid();
        NmOid nmoid = nmcommandbean.getPageOid();
        if(nmoid == null) {
            return null;
        } else {
            Object obj = nmoid.getRef();
            StringBuffer output = new StringBuffer();
            log.debug("obj is:" + obj);
            if (obj instanceof ProcessEnvelope) {
                ProcessEnvelope processEnvelope = (ProcessEnvelope) obj;
                RevisionControlled topObject = EnvelopeHelper.service.getTopObject(processEnvelope);
                memberList = EnvelopeHelper.service.getAllMembers(processEnvelope);
                /*/调试用
                for (ListIterator i = memberList.listIterator(); i.hasNext();) {
                 Object obj1 = i.next();
                 if(obj1 instanceof WTPart){
                    rootPart = (WTPart)obj1;
                    topObject = rootPart;
                    break;
                 }
                }//调试用结束*/
                viewResult = TableViewBean.getCurrentView("envelopeMembersTable2");
                log.debug("viewResult is: " + viewResult);
                if(topObject!=null){
                    if(viewResult.equalsIgnoreCase("作为列表")){
                        result.addAll(memberList);
                    }else if(viewResult.equalsIgnoreCase("作为结构")){
                        WTPart wtpart = (WTPart)topObject;
                        String version = VersionControlHelper.getVersionIdentifier((Versioned)wtpart).getValue();
                        rootPart = getSpecialPartByMasterAndVersion((WTPartMaster)(((WTPart)topObject).getMaster()),version);
                        return Collections.singletonList(rootPart);//如果有TopObject，则从TopObject向下遍历
                    }
                }else{
                    if(viewResult.equalsIgnoreCase("作为列表")){
                        result.addAll(memberList);
                    }else if(viewResult.equalsIgnoreCase("作为结构")){
                        List partsList = new ArrayList();
                        List l = new ArrayList();
                        for (ListIterator i = memberList.listIterator(); i.hasNext();) {
                            Object obj1 = i.next();
                            if(obj1 instanceof WTPart)
                                l.add((WTPart)obj1);
                        }
                        if(l.size()==0) {
                            result.addAll(memberList);
                        }else {
                            result.addAll(l);
                        }
                    }
                }
            }else if(obj instanceof RevisionControlled) {
                Versioned version = (Versioned)obj;
                QueryResult qr = EnvelopeHelper.service.getEnvelopeByMemberObject((RevisionControlled)version);
                while(qr.hasMoreElements()) {
                    ProcessEnvelope pe = ((EnvelopeMemberLink)(qr.nextElement())).getProcessEnvelope();
                    result.add(pe);
                }
            }else if(obj instanceof WorkItem){
            	Persistable per = ((WorkItem) obj).getPrimaryBusinessObject().getObject();
            	if(per instanceof ProcessEnvelope){
                    ProcessEnvelope processEnvelope = (ProcessEnvelope) per;
                    RevisionControlled topObject = EnvelopeHelper.service.getTopObject(processEnvelope);
                    memberList = EnvelopeHelper.service.getAllMembers(processEnvelope);
                    viewResult = TableViewBean.getCurrentView("envelopeMembersTable2");
                    log.debug("viewResult is: " + viewResult);
                    if (topObject != null) {
                        if (viewResult.equalsIgnoreCase("作为列表")) {
                            result.addAll(memberList);
                        } else if (viewResult.equalsIgnoreCase("作为结构")) {
                            WTPart wtpart = (WTPart) topObject;
                            String version = VersionControlHelper.getVersionIdentifier((Versioned) wtpart).getValue();
                            rootPart = getSpecialPartByMasterAndVersion((WTPartMaster) (((WTPart) topObject).getMaster()), version);
                            return Collections.singletonList(rootPart);//如果有TopObject，则从TopObject向下遍历
                        }
                    } else {
                        if (viewResult.equalsIgnoreCase("作为列表")) {
                            result.addAll(memberList);
                        } else if (viewResult.equalsIgnoreCase("作为结构")) {
                            List partsList = new ArrayList();
                            List l = new ArrayList();
                            for (ListIterator i = memberList.listIterator(); i.hasNext(); ) {
                                Object obj1 = i.next();
                                if (obj1 instanceof WTPart)
                                    l.add((WTPart) obj1);
                            }
                            if (l.size() == 0) {
                                result.addAll(memberList);
                            } else {
                                result.addAll(l);
                            }
                        }
                    }
                }
            }
        }
        return result;
   }

   /**
    * Get the child parents for the given list of parent parts
   **/
   public Map<Object,List> getNodes(List parents) throws WTException {

      if (configSpec == null) {
         configSpec = getDefaultConfigSpec();
      }
      //将parents中的WTPart取出来，用于产品结构遍历
      List partsList = new ArrayList();
      for (ListIterator i = parents.listIterator(); i.hasNext();) {
             Object obj = i.next();
             if(obj instanceof WTPart)
                partsList.add((WTPart)obj);
      }
      Map<Object,List> result = new HashMap<Object,List>();
      viewResult = TableViewBean.getCurrentView("envelopeMembersTable2");
      if(viewResult.equalsIgnoreCase("作为列表")){
        for (ListIterator i = parents.listIterator(); i.hasNext();) {
            Object obj = i.next();
            log.debug("列表obj is:" + obj);
             result.put(obj,new ArrayList(1));
        }
      }else if(viewResult.equalsIgnoreCase("作为结构")){
          //此种方法用于纯属WTPart时展示结构树更高效率
          /*
          //API returns a 3D array where the 1st dim is the parent parts,
          //the 2nd dim is the list of children for a given parent,
          //and the 3rd dim is 2 element array w/the link obj at 0 and the child part at 1
          Persistable[][][] all_children = WTPartHelper.service.getUsesWTParts(new WTArrayList(partsList), configSpec);
          for (ListIterator i = partsList.listIterator(); i.hasNext();) {
             WTPart parent = (WTPart)i.next();
             List children = new ArrayList();
             result.put(parent,children);
             QueryResult qr = WTPartHelper.service.getDescribedByDocuments(parent);
             while(qr.hasMoreElements()){
                Object obj = qr.nextElement();
                if(memberList.contains(obj))
                    children.add((Persistable)obj);
             }
             Persistable[][] branch = all_children[i.previousIndex()];
             if (branch == null) {
                continue;
             }
             for (Persistable[] child : branch) {
                WTPart childPart = (WTPart)child[1];
                if(memberList.contains(childPart)){
                    children.add(child[1]);
                    QueryResult qr1 = WTPartHelper.service.getDescribedByDocuments(childPart);
                    while(qr1.hasMoreElements()){
                        Object obj = qr1.nextElement();
                        if(memberList.contains(obj))
                            children.add((Persistable)obj);
                    }
                }
             }
          }*/
          //根据partsList获得子列表
          getChildrenList(partsList,result);
      }
      log.debug("ParentsToChildren: " + result);
      return result;
   }
   private Map<Object,List> getChildrenList(List partsList,Map<Object,List> result) throws WTException{
       for (ListIterator i = partsList.listIterator(); i.hasNext();) {
         Object obj = i.next();
         if(obj instanceof WTPart) {
             WTPart part = (WTPart)obj;
             log.debug("part is:" + part.getIdentity());
             List childrenList = getChildren(part);
             if(childrenList != null){
                List realChildrenList = new ArrayList();
                for(int j = 0 ; j < childrenList.size() ; j++){
                    Object obj1 = childrenList.get(j);
                    if(obj1 instanceof WTPart)
                        log.debug(part.getIdentity() + "'s part child is:" + ((WTPart)obj1).getIdentity());
                    if(obj1 instanceof WTDocument)
                        log.debug(part.getIdentity() + "'s doc child is:" + ((WTDocument)obj1).getIdentity());
                    if(memberList.contains(obj1)){//过滤子节点，只取审签包中有的对象
                        realChildrenList.add(obj1);
                    }
                }
                result.put(part, realChildrenList);
                log.debug("realChildrenList.size() is: " + realChildrenList.size());


                for(int j = 0 ; j < realChildrenList.size() ; j++){
                    WTObject obj2 = (WTObject)realChildrenList.get(j);
                    if(obj2 instanceof WTPart){
                        getChildrenList((WTPart)obj2,result);//递归所有子节点
                    }
                }
            }
         }

       }
       return result;
   }
   private Map<Object,List> getChildrenList(WTPart part,Map<Object,List> result) throws WTException{
        List childrenList = getChildren(part);
        if(childrenList != null){
            List realChildrenList = new ArrayList();
            for(int j = 0 ; j < childrenList.size() ; j++){
                Object obj1 = childrenList.get(j);
                if(obj1 instanceof WTPart)
                    log.debug("child is:" + ((WTPart)obj1).getIdentity());
                if(obj1 instanceof WTDocument)
                    log.debug("child is:" + ((WTDocument)obj1).getIdentity());
                if(memberList.contains(obj1)){//过滤子节点，只取审签包中有的对象
                    realChildrenList.add(obj1);
                }
            }
            result.put(part, realChildrenList);
            log.debug("realChildrenList.size() is: " + realChildrenList.size());

            for(int i = 0 ; i < realChildrenList.size() ; i++){
                WTObject obj = (WTObject)realChildrenList.get(i);
                if(obj instanceof WTPart){
                    getChildrenList((WTPart)obj,result);//递归所有子节点
                }
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private List getChildren(WTPart part) throws WTException{
        if (configSpec == null) {
             configSpec = getDefaultConfigSpec();
        }
        List childrenList = new ArrayList();
        //获得所有关联的文档
        QueryResult qr0 = getAssociatedCADDocuments(part);
        while(qr0.hasMoreElements()){
            Object obj = qr0.nextElement();
            if(memberList.contains(obj))
                childrenList.add((Persistable)obj);
        }
        QueryResult qr1 = WTPartHelper.service.getDescribedByWTDocuments(part);
        while(qr1.hasMoreElements()){
            Object obj = qr1.nextElement();
            if(memberList.contains(obj))
                childrenList.add((Persistable)obj);
        }
        QueryResult qr2 = WTPartHelper.service.getReferencesWTDocumentMasters(part);
        while(qr2.hasMoreElements()){
            Object obj = qr2.nextElement();
            WTDocument doc = getHighestWTDocument((WTDocumentMaster)obj);
            if(memberList.contains(doc))
                childrenList.add(doc);
        }
        QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(part);
        while(qr.hasMoreElements()){ //获取part所有子节点
            WTPartMaster wtpm = (WTPartMaster)((WTPartUsageLink)qr.nextElement()).getUses();
            WTPart child = getHighestWTPart(wtpm);
            if (child != null) {
                log.debug("getChildren->" + part.getIdentity() + "'s child is:"
                        + ((WTPart) child).getIdentity());
                childrenList.add(child);
            }
        }
        return childrenList;
    }

    public static QueryResult getAssociatedCADDocuments(WTPart wtpart)
        throws WTException
    {
        WTArrayList wtarraylist = new WTArrayList();
        wtarraylist.add(wtpart);
        WTKeyedMap wtkeyedmap = PartDocHelper.service.getAssociatedCADDocuments(wtarraylist);
        WTCollection wtcollection = (WTCollection)wtkeyedmap.get(wtpart);
        return getDocs(wtcollection);
    }
    private static QueryResult getDocs(WTCollection wtcollection)
    {
        QueryResult queryresult = new QueryResult();
        try
        {
            if(wtcollection != null)
            {
                ObjectVector objectvector = new ObjectVector();
                DocumentVersion documentversion;
                for(Iterator iterator = wtcollection.persistableIterator(); iterator.hasNext(); objectvector.addElement(documentversion))
                {
                    documentversion = (DocumentVersion)iterator.next();
                }
                queryresult.appendObjectVector(objectvector);
            }
        }
        catch(WTException wtexception)
        {
            wtexception.printStackTrace();
        }
        return queryresult;
    }


    public static WTPart getHighestWTPart(WTPartMaster master) throws WTException {
        WTPart thepart = null;
        String number = master.getNumber();
        QuerySpec qs = new QuerySpec(wt.part.WTPart.class);
        SearchCondition temp = new SearchCondition(wt.part.WTPart.class,
                                                   wt.part.WTPart.NUMBER,
                                                   SearchCondition.EQUAL,
                                                   number);
        qs.appendSearchCondition(temp);
        qs.appendAnd();
        SearchCondition latest = VersionControlHelper.getSearchCondition(wt.part.WTPart.class, true);
        qs.appendSearchCondition(latest);
        QueryResult qr = PersistenceHelper.manager.find(qs);
        if (qr.hasMoreElements() == false) {
          return (null);
        }
        while(qr.hasMoreElements())
        {
            thepart =  (WTPart) qr.nextElement();
        }

        return thepart;
      }

    public static WTDocument getHighestWTDocument(WTDocumentMaster master) throws WTException {
        WTDocument thedoc = null;
        String number = master.getNumber();
        QuerySpec qs = new QuerySpec(wt.doc.WTDocument.class);
        SearchCondition temp = new SearchCondition(wt.doc.WTDocument.class,
                                                   wt.doc.WTDocument.NUMBER,
                                                   SearchCondition.EQUAL,
                                                   number);
        qs.appendSearchCondition(temp);
        qs.appendAnd();
        SearchCondition latest = VersionControlHelper.getSearchCondition(wt.doc.WTDocument.class, true);
        qs.appendSearchCondition(latest);
        QueryResult qr = PersistenceHelper.manager.find(qs);
        if (qr.hasMoreElements() == false) {
          return (null);
        }
        while(qr.hasMoreElements())
        {
            thedoc =  (WTDocument) qr.nextElement();
        }

        return thedoc;
    }

    public static WTPart getSpecialPartByMasterAndVersion(WTPartMaster partMaster, String specialVersion)
        throws WTException
    {
        WTPart part=null;
        String version="";
        //根据master得到该master的的所有的大版本
        QueryResult queryresult=VersionControlHelper.service.allVersionsOf(partMaster);
        if(queryresult !=null && queryresult.size()<=0)
        {
            return null;
        }
        while( queryresult.hasMoreElements())
        {
            part = (WTPart)(queryresult.nextElement());
            //得到零部件的大版本，并与指定的版本比较，有则返回
            version= VersionControlHelper.getVersionIdentifier((Versioned)(WTPart)part).getValue();
            if(version.equalsIgnoreCase(specialVersion))
                return part;
        }
        return null;
    }
}