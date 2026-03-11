package ext.casc.workflow.tree;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.enterprise.Master;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.workflow.engine.WfActivity;
import wt.workflow.work.WorkItem;

import com.ptc.core.components.beans.TreeHandlerAdapter;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;

import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changepackaged.ChangePackagedResultLink;
import ext.ases.changerequest.ChangeRequest;
import ext.ases.envelope.ProcessEnvelope;
import ext.ases.envelope.ProcessEnvelopeUtil;
import ext.casc.change.ChangeHelper;
import ext.casc.part.PackagedPartHelper;
import ext.casc.workflow.signtrue.zp.SignatureService;

public class SignatureResultTreeHandler2 extends TreeHandlerAdapter {
    private String activityName = "";
    private WTPart topObject = null;
    private WorkItem wi = null;
    private WTUser currentuser = null;
    public SignatureResultTreeHandler2(String type) {
        this.activityName = type;
    }

    public SignatureResultTreeHandler2(WTPart topObject) {
        this.topObject = topObject;
    }

    public SignatureResultTreeHandler2() {
    }

    public Map<Object, List> getNodes(List parentNodeList) throws WTException {
        NmCommandBean cb = getModelContext().getNmCommandBean();
        Map map = cb.getRequestData().getParameterMap();
        String oid = (String) map.get("oid");// 读取任务页面的流程oid
        ReferenceFactory rf = new ReferenceFactory();
        wi = (WorkItem) rf.getReference(oid).getObject();
        WfActivity wfAct = (WfActivity) wi.getSource().getObject();
        Persistable pbo = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");
        Map<Object, List> nodesMap = new HashMap<Object, List>();
        currentuser = (WTUser)SessionHelper.manager.getPrincipal();
        for (int i = 0; i < parentNodeList.size(); i++) {
            WTObject wto = (WTObject) parentNodeList.get(i);
            nodesMap.put(wto, new ArrayList());
        }

        return nodesMap;
    }

    @SuppressWarnings("deprecation")
    public List<Object> getRootNodes() throws WTException {// 获取树的根节点
        List<Object> rootList = new ArrayList<Object>();
        NmCommandBean cb = getModelContext().getNmCommandBean();
        Map map = cb.getRequestData().getParameterMap();
        String oid = (String) map.get("oid"); // 读取任务页面的流程oid
        ReferenceFactory rf = new ReferenceFactory();
        wi = (WorkItem) rf.getReference(oid).getObject();
        WfActivity wfAct = (WfActivity) wi.getSource().getObject();
        Persistable pbo = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");
        if (pbo instanceof ProcessEnvelope) {
            if (topObject != null) {// 顶层节点，以树形结构显示
                rootList.add(topObject);
                return rootList;
            } else {
                ProcessEnvelope pe = (ProcessEnvelope) pbo; // 获取pbo对象
                List list = ProcessEnvelopeUtil.getAllMembersNoPart(wi, pe);
                rootList.addAll(list);
            }
        } else if (pbo instanceof WTChangeOrder2) {
            WTChangeOrder2 ecn = (WTChangeOrder2) pbo;
            List list = ChangeHelper.getChangeResultItem(ecn);
            rootList.addAll(list);
            rootList.add(ecn);
        } else if (pbo instanceof MPMProcessPlan) {
            rootList.add(pbo);
        } else if (pbo instanceof WTDocument) {
            rootList.add(pbo);
        } else if (pbo instanceof ChangePackaged) {
			ChangePackaged change = (ChangePackaged) pbo; // 获取pbo对象
			rootList.add(change);
			QueryResult qr = PersistenceHelper.manager.navigate(change,
					ChangePackagedResultLink.ROLE_BOBJECT_ROLE,
					ChangePackagedResultLink.class, true);
			while (qr.hasMoreElements()) {
				WTObject wtobject = (WTObject) qr.nextElement();
				if (!(wtobject instanceof WTPart)) {
					rootList.add(wtobject);
				}
			}
		} else if (pbo instanceof ChangeRequest) {
		    ChangeRequest request = (ChangeRequest) pbo; // 获取pbo对象
            rootList.add(request);
        }
        
        SignatureService.filtrate(rootList);
        return rootList;
    }

    private Map<Object, List> getChildrenList(WTPart part, Map<Object, List> nodesMap, List memberList,Persistable pbo)
            throws WTException {
        List childrenList = getChildren(part,memberList);
        if (childrenList != null) {
            List realChildrenList = new ArrayList();
            for (int i = 0; i < childrenList.size(); i++) {
                Object obj = childrenList.get(i);
                if (memberList.contains(obj)) {// 过滤子节点，只取审签包中有的对象
                    realChildrenList.add(obj);
                }
            }
            // realChildrenList = SignatureService.filtrate2(realChildrenList);
            nodesMap.put(part, realChildrenList);
        }
        return nodesMap;
    }

    private List getChildren(WTPart part,List memberList) throws WTException {
        List childrenList = new ArrayList();
        try {
            QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(part);
            while (qr.hasMoreElements()) { // 获取part所有子节点
                WTPartMaster wtpm = (WTPartMaster) ((WTPartUsageLink) qr.nextElement()).getUses();
                QueryResult queryResult = VersionControlHelper.service.allVersionsOf((Master) wtpm);
                while(queryResult.hasMoreElements()){
                    Object object = queryResult.nextElement();
                    if (memberList.contains(object)) {
                        childrenList.add(object);
                    }
                }
            }
            List docList = PackagedPartHelper.getPartReleatedDOC(part);
            for (int i = 0; i < docList.size(); i++) {
                Object object = docList.get(i);
                if (object instanceof WTDocument) {
                    WTDocument document = (WTDocument)object;
                    QueryResult queryResult = VersionControlHelper.service.allVersionsOf((Master) document.getMaster());
                    while(queryResult.hasMoreElements()){
                        Object object2 = queryResult.nextElement();
                        if (memberList.contains(object2)) {
                            childrenList.add(object2);
                        }
                    }
                }else if (object instanceof EPMDocument) {
                    EPMDocument epmDocument = (EPMDocument)object;
                    QueryResult queryResult = VersionControlHelper.service.allVersionsOf((Master) epmDocument.getMaster());
                    while(queryResult.hasMoreElements()){
                        Object object2 = queryResult.nextElement();
                        if (memberList.contains(object2)) {
                            childrenList.add(object2);
                        }
                    }
                }
            }
            //childrenList.addAll(docList);
        } catch (RemoteException e) {
            e.printStackTrace();
        }// 获取part所有相关文档
        return childrenList;
    }
    
    public static void removeExsitItem(Set<EPMDocument> allCads,Set<EPMDocument> tempCadList){
        Set<EPMDocument> tempallCads = new HashSet<EPMDocument>(allCads);
        for (EPMDocument tempEpm : tempallCads) {
            for (EPMDocument epmDocument : tempCadList) {
                if (epmDocument.getName().equals(tempEpm.getName())) {
                    allCads.remove(tempEpm);
                }
            }
        }
    }
}
