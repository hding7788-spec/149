package ext.casc.workflow.tree;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wt.content.ContentHolder;
import wt.enterprise.Master;
import wt.enterprise.RevisionControlled;
import wt.fc.Persistable;
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
import wt.workflow.engine.WfAssignmentEventAudit;
import wt.workflow.work.WorkItem;

import com.ptc.core.components.beans.TreeHandlerAdapter;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.workflow.WorkflowCommands;

import ext.ases.envelope.ProcessEnvelope;
import ext.ases.envelope.ProcessEnvelopeUtil;
import ext.casc.constants.Constants;
import ext.casc.part.PackagedPartHelper;
import ext.casc.util.WCUtil;
import ext.casc.util.WTUtil;
import ext.casc.workflow.signtrue.zp.ISignatureParser;
import ext.casc.workflow.signtrue.zp.SignatureGYYXMLParser;
import ext.casc.workflow.signtrue.zp.SignatureGYZZXMLParser;
import ext.casc.workflow.signtrue.zp.SignatureService;

public class SignatureTreeHandler1 extends TreeHandlerAdapter{
	private String activityName ="";
    private ISignatureParser parser = null;
    private WorkItem wi = null;
    private WTUser currentuser = null;
    private List<Object> flagList = new ArrayList<Object>();

	public SignatureTreeHandler1(){

	}
	public SignatureTreeHandler1(String activityName){
		this.activityName = activityName;
	}
	public Map<Object, List> getNodes(List parentNodeList) throws WTException {
		Map<Object,List> nodesMap = new HashMap<Object,List>();
		//切换系统管理员
		currentuser = (WTUser)SessionHelper.manager.getPrincipal();
		WTUser admin = (WTUser)SessionHelper.manager.setAdministrator();
		try{
			NmCommandBean cb = getModelContext().getNmCommandBean();
			Map map = cb.getRequestData().getParameterMap();
			String oid = (String)map.get("oid");				//读取任务页面的流程oid
			ReferenceFactory rf = new ReferenceFactory();
			WorkItem wi = (WorkItem) rf.getReference(oid).getObject();
			WfActivity wfAct = (WfActivity) wi.getSource().getObject();
			Persistable pbo = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");
			ProcessEnvelope pe = (ProcessEnvelope)pbo;			//获取pbo对象
			List memberList = new ArrayList();
			if(parser==null){
				if("指派工艺员".equals(activityName)||Constants.ACTIVITYNAME_ZPGYHQ.equals(activityName)){
					parser = new SignatureGYZZXMLParser((ContentHolder)pbo);
				}else if("工艺会签".equals(activityName)){
					parser = new SignatureGYYXMLParser((ContentHolder)pbo);
				}
			}
			memberList.addAll(ProcessEnvelopeUtil.getAllMembers(pe));

			//memberList = ProcessEnvelopeUtil.getAllMembers(pe); //获取审签包所有对象
			for(int i = 0 ; i < parentNodeList.size() ; i++){
				WTObject wto = (WTObject)parentNodeList.get(i);
				if(wto instanceof WTPart){
					WTPart part = (WTPart)wto;
					getChildrenList(part,nodesMap,memberList);
				}
			}

		}catch(WTException e){
			e.printStackTrace();
		}finally{
			WTUser user = (WTUser)SessionHelper.manager.setPrincipal(currentuser.getAuthenticationName());
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
		currentuser = (WTUser)SessionHelper.manager.getPrincipal();
		wi = (WorkItem) rf.getReference(oid).getObject();
		WfActivity wfAct = (WfActivity) wi.getSource().getObject();
		Persistable pbo = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");
		ProcessEnvelope pe = (ProcessEnvelope)pbo;			//获取pbo对象
		WTObject obj = (WTObject)ProcessEnvelopeUtil.getTopObject(pe);
		WTPart latestPart = null;
		if(parser==null){
			if("指派工艺员".equals(activityName)||Constants.ACTIVITYNAME_ZPGYHQ.equals(activityName)){
				parser = new SignatureGYZZXMLParser((ContentHolder)pbo);
			}else if("工艺会签".equals(activityName)){
				parser = new SignatureGYYXMLParser((ContentHolder)pbo);
			}
		}
		if(obj == null || !(obj instanceof WTPart)){		//如果对象不是part
			return rootList;
		}else{
			latestPart = (WTPart)WCUtil.getLatestPartByView((WTPartMaster)((WTPart)obj).getMaster(),"Design");
			if (latestPart != null)
				rootList.add(latestPart);                      //如果是part，找到跟节点返回
		}
		return rootList;
	}

	@SuppressWarnings("unchecked")
	private Map<Object,List> getChildrenList(WTPart part,Map<Object,List> nodesMap,List memberList) throws WTException{
		List childrenList = getChildren(part);
		if(childrenList != null){
			List realChildrenList = new ArrayList();
			for(int i = 0 ; i < childrenList.size() ; i++){
				Object obj = childrenList.get(i);
				if(memberList.contains(obj)){//过滤子节点，只取审签包中有的对象
				    if (!flagList.contains(obj)) {
                        flagList.add(obj);
                        if(obj instanceof WTPart){
                            realChildrenList.add(obj);
                        }else if(parser==null||parser.hasPrivilege((Persistable)obj, currentuser,wi)){
                            realChildrenList.add(obj);
                        }
				    }
				}
			}
			//realChildrenList = SignatureService.filtrate2(realChildrenList);
			nodesMap.put(part, realChildrenList);
		}
		return nodesMap;
	}

	@SuppressWarnings("unchecked")
	private List getChildren(WTPart part) throws WTException{
		List childrenList = new ArrayList();
		QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(part);
		while(qr.hasMoreElements()){ //获取part所有子节点
			WTPartMaster wtpm = (WTPartMaster)((WTPartUsageLink)qr.nextElement()).getUses();
			WTPart child = (WTPart)WCUtil.getLatestPartByView((Master)wtpm,"Design");
			if (child!=null)
				childrenList.add(child);
		}
		try {
			List docList = PackagedPartHelper.getPartReleatedDOC(part);
			childrenList.addAll(docList);
		} catch (RemoteException e) {
			e.printStackTrace();
		}//获取part所有相关文档
		return childrenList;
	}

}
