package ext.casc.workflow.tree;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wt.doc.WTDocument;
import wt.enterprise.Master;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.workflow.engine.WfActivity;
import wt.workflow.work.WorkItem;

import com.ptc.core.components.beans.TreeHandlerAdapter;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.ases.envelope.ProcessEnvelope;
import ext.ases.envelope.ProcessEnvelopeUtil;
import ext.casc.part.PackagedPartHelper;

public class SignatureResultTreeHandler extends TreeHandlerAdapter{

	public Map<Object, List> getNodes(List parentNodeList) throws WTException {
		NmCommandBean cb = getModelContext().getNmCommandBean();
		Map map = cb.getRequestData().getParameterMap();
		String oid = (String)map.get("oid");				//读取任务页面的流程oid
		ReferenceFactory rf = new ReferenceFactory();
		WorkItem wi = (WorkItem) rf.getReference(oid).getObject();
		WfActivity wfAct = (WfActivity) wi.getSource().getObject();
		Persistable pbo = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");
		ProcessEnvelope pe = (ProcessEnvelope)pbo;			//获取pbo对象
		List memberList = ProcessEnvelopeUtil.getAllMembers(pe); //获取审签包所有对象
		Map<Object,List> nodesMap = new HashMap<Object,List>();
		
		for(int i = 0 ; i < parentNodeList.size() ; i++){
			WTObject wto = (WTObject)parentNodeList.get(i);
			if(wto instanceof WTPart){
				WTPart part = (WTPart)wto;
				getChildrenList(part,nodesMap,memberList);
			}
		}
		return nodesMap;	
	}

	@SuppressWarnings("deprecation")
	public List<Object> getRootNodes() throws WTException {//获取树的根节点
		List<Object> rootList = new ArrayList<Object>();
		NmCommandBean cb = getModelContext().getNmCommandBean();
		Map map = cb.getRequestData().getParameterMap();
		String oid = (String)map.get("oid");				//读取任务页面的流程oid
		ReferenceFactory rf = new ReferenceFactory();
		WorkItem wi = (WorkItem) rf.getReference(oid).getObject();
		WfActivity wfAct = (WfActivity) wi.getSource().getObject();
		Persistable pbo = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");
		ProcessEnvelope pe = (ProcessEnvelope)pbo;			//获取pbo对象
		List list = ProcessEnvelopeUtil.getAllMembers(pe);
		WTObject obj = (WTObject)ProcessEnvelopeUtil.getTopObject(pe);
		WTPart latestPart = null;
		if(obj == null || !(obj instanceof WTPart)){		//如果对象不是part
			return rootList;
		}else{
			latestPart = (WTPart)VersionControlHelper.service.getLatestIteration((WTPart)obj, false);
			if(latestPart != null)
				rootList.add(latestPart);                      //如果是part，找到跟节点返回
		}
		return rootList;
	}
	
	@SuppressWarnings("unchecked")
	private Map<Object,List> getChildrenList(WTPart part,Map<Object,List> nodesMap,List memberList) throws WTException{
		List childrenList = getChildren(part,memberList);
		if(childrenList != null){
			List realChildrenList = new ArrayList();
			for(int i = 0 ; i < childrenList.size() ; i++){
				Object obj = childrenList.get(i);
				if(memberList.contains(obj)){//过滤子节点，只取审签包中有的对象
					realChildrenList.add(obj);
				}
			}
			//realChildrenList = SignatureService.filtrate2(realChildrenList);
			nodesMap.put(part, realChildrenList);
		}
		return nodesMap;
	}
	
	@SuppressWarnings("unchecked")
	private List getChildren(WTPart part,List memberList) throws WTException{
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
		} catch (RemoteException e) {
			e.printStackTrace();
		}//获取part所有相关文档
		return childrenList;
	}
}
