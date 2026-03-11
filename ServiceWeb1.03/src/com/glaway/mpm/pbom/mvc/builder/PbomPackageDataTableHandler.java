package com.glaway.mpm.pbom.mvc.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wt.doc.WTDocument;
import wt.enterprise.Master;
import wt.epm.EPMDocument;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.util.WTException;

import com.ptc.core.components.beans.TreeHandlerAdapter;

import ext.casc.util.WCUtil;

public class PbomPackageDataTableHandler extends TreeHandlerAdapter{

	private WTPart topPart;
	List list = new ArrayList();
	List<String> allMemberNumber = new ArrayList<String>();
	String activityName = "";

	public PbomPackageDataTableHandler(WTPart topPart,List list,String activityName) {
		this.topPart = topPart;
		this.list = list;
		this.activityName = activityName;
		this.allMemberNumber = initAllMemberNumber(this.allMemberNumber,list);
	}

	public Map<Object, List> getNodes(List parentNodeList) throws WTException {
		Map<Object,List> nodesMap = new HashMap<Object,List>();
		for(int i = 0 ; i < parentNodeList.size() ; i++){
			WTObject wto = (WTObject)parentNodeList.get(i);
			if(wto instanceof WTPart){
				WTPart part = (WTPart)wto;
				getChildrenList(part,nodesMap,allMemberNumber);
			}
		}
		return nodesMap;
	}

	@SuppressWarnings("deprecation")
	public List<Object> getRootNodes() throws WTException {//获取树的根节点
		List<Object> rootList = new ArrayList<Object>();
		if("PBOM构建".equals(activityName)) {
			rootList.add(topPart);
		} else {
			WTPart mtopPart = WCUtil.getLatestPartByView((Master)topPart.getMaster(), "Manufacturing");
			rootList.add(mtopPart);
		}
		return rootList;
	}

	@SuppressWarnings("unchecked")
	private Map<Object,List> getChildrenList(WTPart part,Map<Object,List> nodesMap,List memberList) throws WTException{
		List childrenList = getChildren(part,memberList);
		if(childrenList != null){
			nodesMap.put(part, childrenList);
		}
		return nodesMap;
	}

	@SuppressWarnings("unchecked")
	private List getChildren(WTPart part,List memberList) throws WTException{
		List childrenList = new ArrayList();
		QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(part);
        while (qr.hasMoreElements()) {
            WTPartMaster wtpm = (WTPartMaster) ((WTPartUsageLink) qr.nextElement()).getUses();
            if(memberList.contains(wtpm.getNumber())) {
            	if("PBOM构建".equals(activityName)) {
            		WTPart childPart = WCUtil.getLatestPartByView((Master)wtpm, "Design");
                	if(childPart != null) {
                		childrenList.add(childPart);
                	}
            	} else {
            		WTPart childPart = WCUtil.getLatestPartByView((Master)wtpm, "Manufacturing");
                	if(childPart != null) {
                		childrenList.add(childPart);
                	}
            	}
            }
        }
		return childrenList;
	}

	public static List<String> initAllMemberNumber(List<String> allMemberNumber,List list) {
		for (Object object : list) {
			if(object instanceof WTPart) {
				allMemberNumber.add(((WTPart)object).getNumber());
			} else if(object instanceof WTDocument){
				allMemberNumber.add(((WTDocument)object).getNumber());
			} else if(object instanceof EPMDocument){
				allMemberNumber.add(((EPMDocument)object).getNumber());
			}
		}
		return allMemberNumber;
	}
}
