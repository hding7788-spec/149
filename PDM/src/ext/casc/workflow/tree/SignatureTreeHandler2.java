package ext.casc.workflow.tree;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import wt.change2.WTChangeOrder2;
import wt.content.ContentHolder;
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
import wt.vc.config.ConfigHelper;
import wt.vc.config.ConfigSpec;
import wt.workflow.engine.WfActivity;
import wt.workflow.work.WorkItem;

import com.ptc.core.components.beans.TreeHandlerAdapter;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;

import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changepackaged.ChangePackagedResultLink;
import ext.ases.changerequest.ChangeRequest;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.change.ChangeHelper;
import ext.casc.constants.Constants;
import ext.casc.part.PackagedPartHelper;
import ext.casc.workflow.signtrue.zp.ISignatureParser;
import ext.casc.workflow.signtrue.zp.SignatureGYYXMLParser;
import ext.casc.workflow.signtrue.zp.SignatureGYZZXMLParser;
import ext.casc.workflow.signtrue.zp.SignatureService;

public class SignatureTreeHandler2 extends TreeHandlerAdapter{
    private String activityName="";
    private WTPart topObject = null;
    private ISignatureParser parser = null;
    private WorkItem wi = null;
    private WTUser currentuser = null;
    private List<Object> flagList = new ArrayList<Object>();

    public SignatureTreeHandler2(String type){
        this.activityName = type;
    }

    public SignatureTreeHandler2(WTPart topObject){
        this.topObject = topObject;
    }

    public SignatureTreeHandler2(){}

	public Map<Object, List> getNodes(List parentNodeList) throws WTException {
	    NmCommandBean cb = getModelContext().getNmCommandBean();
        Map map = cb.getRequestData().getParameterMap();
        String oid = (String)map.get("oid");//读取任务页面的流程oid
        ReferenceFactory rf = new ReferenceFactory();
        wi = (WorkItem) rf.getReference(oid).getObject();
        WfActivity wfAct = (WfActivity) wi.getSource().getObject();
        Persistable pbo = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");
        if(parser==null){
			if("指派工艺员".equals(activityName)||Constants.ACTIVITYNAME_ZPGYHQ.equals(activityName)){
				parser = new SignatureGYZZXMLParser((ContentHolder)pbo);
			}else if("工艺会签".equals(activityName)){
				parser = new SignatureGYYXMLParser((ContentHolder)pbo);
			}
		}

        Map<Object,List> nodesMap = new HashMap<Object,List>();
        for(int i = 0 ; i < parentNodeList.size() ; i++){
            WTObject wto = (WTObject)parentNodeList.get(i);
            nodesMap.put(wto, new ArrayList());
        }

        return nodesMap;
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	public List<Object> getRootNodes() throws WTException {//获取树的根节点
		List<Object> rootList = new ArrayList<Object>();

		if (topObject != null) {//顶层节点，以树形结构显示
            rootList.add(topObject);
            return rootList;
        }

		//切换系统管理员
	    currentuser = (WTUser)SessionHelper.manager.getPrincipal();
		WTUser admin = (WTUser)SessionHelper.manager.setAdministrator();
		try{
			NmCommandBean cb = getModelContext().getNmCommandBean();
			Map map = cb.getRequestData().getParameterMap();
			String oid = (String)map.get("oid");//读取任务页面的流程oid
			ReferenceFactory rf = new ReferenceFactory();
		    wi = (WorkItem) rf.getReference(oid).getObject();
			WfActivity wfAct = (WfActivity) wi.getSource().getObject();
			Persistable pbo = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");
			if(parser==null){
				if("指派工艺员".equals(activityName)||Constants.ACTIVITYNAME_ZPGYHQ.equals(activityName)){
					parser = new SignatureGYZZXMLParser((ContentHolder)pbo);
				}else if("工艺会签".equals(activityName)){
					parser = new SignatureGYYXMLParser((ContentHolder)pbo);
				}
			}
			if(pbo instanceof ProcessEnvelope){
				ProcessEnvelope pe = (ProcessEnvelope)pbo;			//获取pbo对象
				List list = SignatureService.getGYHQDealMembers(pe,currentuser,wi,parser);
                rootList.addAll(list);
			}else if (pbo instanceof WTChangeOrder2){
				WTChangeOrder2 ecn = (WTChangeOrder2)pbo;
				List list = ChangeHelper.getChangeResultItem(ecn);
				list = SignatureService.getGYHQDealMembers(list, currentuser, wi,parser);
	            rootList.addAll(list);
	            if(parser==null||parser.hasPrivilege((Persistable)ecn, currentuser, wi)){
	                rootList.add(ecn);
                }

			}else if (pbo instanceof WTDocument){
				rootList.add(pbo);
			}else if (pbo instanceof MPMProcessPlan) {
                rootList.add(pbo);
            } else if (pbo instanceof ChangeRequest) {
                ChangeRequest request = (ChangeRequest) pbo; // 获取pbo对象
                if(parser==null||parser.hasPrivilege((Persistable)request, currentuser, wi)){
                    rootList.add(request);
                }
            } else if (pbo instanceof ChangePackaged) {
				ChangePackaged change = (ChangePackaged) pbo; // 获取pbo对象
				if(parser==null||parser.hasPrivilege((Persistable)change, currentuser, wi)){
					rootList.add(change);
				}
				QueryResult qr = PersistenceHelper.manager.navigate(change,
						ChangePackagedResultLink.ROLE_BOBJECT_ROLE,
						ChangePackagedResultLink.class, true);
				while (qr.hasMoreElements()) {
					WTObject wtobject = (WTObject) qr.nextElement();
					if (!(wtobject instanceof WTPart)) {
						if(parser==null||parser.hasPrivilege((Persistable)wtobject, currentuser, wi)){
							rootList.add(wtobject);
						}
					}
				}
			}
			SignatureService.filtrate(rootList);
		}catch(WTException e){
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			SessionHelper.manager.setPrincipal(currentuser.getAuthenticationName());
		}
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
                    if (!flagList.contains(obj)) {
                        flagList.add(obj);
                        if(obj instanceof WTPart){
                            realChildrenList.add(obj);
                        }else if(parser==null||parser.hasPrivilege((Persistable)obj, wi)){
                            realChildrenList.add(obj);
                        }
                    }
                }
            }
            // realChildrenList = SignatureService.filtrate2(realChildrenList);
            nodesMap.put(part, realChildrenList);
        }
        return nodesMap;
    }

    private List getChildren(WTPart part,List memberList) throws WTException {
        List childrenList = new ArrayList();
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
        try {
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

    protected ConfigSpec getDefaultConfigSpec() throws WTException {
        return ConfigHelper.service.getDefaultConfigSpecFor(WTPart.class);
    }

    public static void removeExsitItem(Set<EPMDocument> allCads,Set<EPMDocument> tempCadList){
        Set<EPMDocument> tempallCads = new HashSet<EPMDocument>(allCads);
        for (EPMDocument tempEpm : tempallCads) {
            for (EPMDocument epmDocument : tempCadList) {
                if (epmDocument.getNumber().equals(tempEpm.getNumber())) {
                    allCads.remove(tempEpm);
                }
            }
        }
    }
}
