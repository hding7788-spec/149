package com.glaway.mpm.mvc.builders.pbom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import wt.fc.ReferenceFactory;
import wt.part.WTPart;
import wt.util.WTException;
import wt.workflow.engine.WfActivity;
import wt.workflow.work.WfAssignmentState;
import wt.workflow.work.WorkItem;

import com.glaway.mpm.util.Constant;
import com.glaway.mpm.util.GLLogger;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.core.components.beans.TreeHandlerAdapter;
import com.ptc.netmarkets.util.beans.NmCommandBean;
 public	class PartTypeBuilderTreeAdapter extends TreeHandlerAdapter {

		@SuppressWarnings(value={"unchecked"})
		public Map<Object, List> getNodes(List arg0) throws WTException {
			HashMap map = new HashMap();
			for (Object obj : arg0) {
				WTPart part = (WTPart) obj;
				String lifecycleStatus = part.getLifeCycleState().getStringValue();
				ArrayList<WTPart> list = (ArrayList<WTPart>) WTPartUtil.getChildPart(part);
				ArrayList<WTPart> partlist = new ArrayList<WTPart>();
				for(int i = 0; i < list.size(); i++){
						WTPart temp = list.get(i);
						temp = WTPartUtil.getLatestPartByNumberAndView(temp, Constant.PBOM_VIEW);
						partlist.add(temp);
				}
				map.put(part, partlist);
			}
			return map;
		}

		@SuppressWarnings(value={"unchecked"})
		public List<Object> getRootNodes() throws WTException {
			NmCommandBean commandbean = getModelContext().getNmCommandBean();
			HttpServletRequest request = commandbean.getRequest();
			String oid = request.getParameter("oid");
			GLLogger.debug("oid====>" + oid);
			ReferenceFactory rf = new ReferenceFactory();
	        WorkItem wi= (WorkItem) rf.getReference(oid).getObject();
	        WfAssignmentState state = wi.getStatus();
	        WfActivity wfAct = (WfActivity) wi.getSource().getObject();
	        Object pbo = wfAct.getContext().getValue("primaryBusinessObject");
	        WTPart part = (WTPart)pbo;
	        ArrayList list = new ArrayList();
	        if(Constant.PBOM_VIEW.equals(part.getViewName())){
	        	list.add(part);
	        }else{
	        	WTPart pbomPart = WTPartUtil.getLatestPartByNumberAndView(part, Constant.PBOM_VIEW);
	        	list.add(pbomPart);
	        }
			return list;
		}

	}