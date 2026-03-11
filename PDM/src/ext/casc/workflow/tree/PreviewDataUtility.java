package ext.casc.workflow.tree;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.ases.envelope.EnvelopeMemberLink;
import ext.ases.envelope.ProcessEnvelope;
import ext.ases.part.ASESHuiqianSignature;
import ext.casc.util.NmTableGUIComponent;
import ext.casc.workflow.WfUtil;
import wt.epm.EPMDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.project.Role;
import wt.team.Team;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.util.WTStandardDateFormat;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class PreviewDataUtility extends AbstractDataUtility {

	@Override
	public Object getDataValue(String columnName, Object obj, ModelContext mc)
			throws WTException {
		NmCommandBean commandBean = mc.getNmCommandBean();

		GUIComponentArray guicomponentarrayMain = new GUIComponentArray();
        guicomponentarrayMain.setValueHidden(false);
		if(columnName.equals("advice_result")){
        	Map<WTObject,List<ASESHuiqianSignature>> signMap = (Map)mc.getNmCommandBean().getRequest().getAttribute("signMap");
//        	System.out.println(">>>signMap=="+signMap);
				String value ="";
				if(signMap != null){
					try {
						value = getSignValue(obj,signMap,"");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
//				System.out.println("value===="+value);
				NmTableGUIComponent gui = new NmTableGUIComponent(value);
				guicomponentarrayMain.addGUIComponent(gui);
				return guicomponentarrayMain;
		}else if(columnName.equals("implementadvise")){//final_advice
			Map<WTObject,List<ASESHuiqianSignature>> signMap = (Map)mc.getNmCommandBean().getRequest().getAttribute("signMap");
//        	System.out.println(">>>signMap=="+signMap);
				String value ="";
				if(signMap != null){
					try {
						value = getSignValue1(obj,signMap,"通过监听");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
//				System.out.println("value===="+value);
				NmTableGUIComponent gui = new NmTableGUIComponent(value);
				guicomponentarrayMain.addGUIComponent(gui);
				return guicomponentarrayMain;
		}else if(columnName.equals("maturityUser")){
			if(obj instanceof ProcessEnvelope){
				ProcessEnvelope envelope = (ProcessEnvelope)obj;
				QueryResult processes = WfEngineHelper.service.getAssociatedProcesses(envelope, null, null);
				if(processes.hasMoreElements()){
					WfProcess process = (WfProcess) processes.nextElement();
					Role role = Role.toRole("ZHIPAIGONGYIZUZHANGZHE");
					Team team = (Team) process.getTeamId().getObject();
					Map map = team.getRolePrincipalMap();
					List tempUserList = (List) map.get(role);
					if(tempUserList != null && tempUserList.size() > 0){
						WTUser zhurengongyishi = (WTUser) ((WTPrincipalReference) tempUserList.get(0)).getObject();
						return zhurengongyishi.getFullName();
					}
				}
			}
		}else if(columnName.equals("maturityTime")){
			if(obj instanceof ProcessEnvelope){
				ProcessEnvelope envelope = (ProcessEnvelope)obj;
				QueryResult processes = WfEngineHelper.service.getAssociatedProcesses(envelope, null, null);
				if(processes.hasMoreElements()){
					WfProcess process = (WfProcess) processes.nextElement();
					if(process.getEndTime() != null){
						return WTStandardDateFormat.format(process.getEndTime(), "yyyy/MM/dd HH:mm:ss");
					}
				}
			}
		}else if(columnName.equals("maturity")){
			if(obj instanceof EPMDocument){
				EPMDocument epmDocument = (EPMDocument) obj;
				QueryResult qr = PersistenceHelper.manager.navigate(epmDocument, "theProcessEnvelope", EnvelopeMemberLink.class, false);
				while(qr.hasMoreElements()) {
					EnvelopeMemberLink link=(EnvelopeMemberLink)qr.nextElement();
					String modelMaturity = link.getDescription();
					return modelMaturity;
				}
			}
		}
		return null;
	}
	private String getSignValue(Object obj,Map<WTObject,List<ASESHuiqianSignature>> signMap , String activityName)
        	throws WTException{
        	String value = "";
//        	ReferenceFactory rf = new ReferenceFactory();
    		List<ASESHuiqianSignature> tempSignList = signMap.get(obj);
    		System.out.println("obj="+obj);
    		System.out.println("tempSignList="+tempSignList);
    		if(tempSignList == null){
    			return value;
    		}
    		String tempValue = "";
    		Hashtable ht = new Hashtable();
    		for(int i = 0 ; i < tempSignList.size() ; i++){
    			ASESHuiqianSignature tempSign = tempSignList.get(i);
//    			String tempActOid = tempSign.getActivity();
//    			WfActivity wfAct = (WfActivity)rf.getReference(tempActOid).getObject();
//    			if(wfAct.getName().equalsIgnoreCase(activityName)){
    				String conclution = tempSign.getConclusion();
    				if(conclution == null){
    					continue;
    				}
    				String userName = "";
    				if (conclution.contains("同意")&&!conclution.contains("不同意")) {
    				    userName = conclution.substring(0,conclution.length()-2);
                    }else if (conclution.contains("不同意")) {
                        userName = conclution.substring(0,conclution.length()-3);
                    }

    				ASESHuiqianSignature tempSign1 = (ASESHuiqianSignature)ht.get(userName);
    				if(tempSign1!=null){
    					if(tempSign.getCreateTimestamp().after(tempSign1.getCreateTimestamp())){
    						ht.put(userName, tempSign);
    					}
    				}else{
    					ht.put(userName, tempSign);
    				}
//    			}
    		}
    		if(ht.size()>0) {
    			Enumeration enum1 = ht.keys();
    			while(enum1.hasMoreElements()){
    				Object obj1 = enum1.nextElement();
    				ASESHuiqianSignature tempSign = (ASESHuiqianSignature)ht.get(obj1);
    				String conclusion = tempSign.getConclusion();
    				String opinion = tempSign.getOpinion();
    				if(opinion == null){
    					opinion = "";
    				}
    				if(tempValue.equals("")){
    					tempValue = conclusion + ";" + opinion;

    				}else{
    					tempValue = tempValue + "/" + conclusion + ";" + opinion;
    				}
    				value = tempValue;
    				if (!"".equals(value)&&value.contains(",")) {
                        value = value.replaceAll(",", "");
                    }
    			}
    		}
    		return value;
        }

	private String getSignValue1(Object obj,Map<WTObject,List<ASESHuiqianSignature>> signMap , String activityName)
        	throws WTException{
        	String value = "";
        	ReferenceFactory rf = new ReferenceFactory();
    		List<ASESHuiqianSignature> tempSignList = signMap.get(obj);
    		System.out.println("getSignValue1 obj="+obj);
    		System.out.println("getSignValue1 tempSignList="+tempSignList);
    		if(tempSignList == null){
    			return value;
    		}
//    		String tempValue = "";
//    		Hashtable ht = new Hashtable();
    		for(int i = 0 ; i < tempSignList.size() ; i++){
    			ASESHuiqianSignature tempSign = tempSignList.get(i);
    			String tempActOid = tempSign.getActivity();
    			WfActivity wfAct = (WfActivity)rf.getReference(tempActOid).getObject();
    			System.out.println("wfAct="+wfAct.getName()+",--"+activityName);
    			if(wfAct.getName().equalsIgnoreCase(activityName)){
    				String implement = tempSign.getOpinion();
        			System.out.println("implement=="+implement);
        			if(implement != null && !"".equals(implement.trim())){
        				if(implement.contains(" 805;")||implement.contains(" no8;")||implement.contains(" 八部;")){
        					implement = implement.replace(" 805;", " ");
        					implement = implement.replace(" no8;", " ");
        					implement = implement.replace(" 八部;", " ");
        				}
        				return implement;
        			}
    			}
    		}
    		return value;
        }


	private static String getOldValue(ModelContext mc,String key) {
        try {
            NmCommandBean commandBean = mc.getNmCommandBean();
            HttpServletRequest request = commandBean.getRequest();
            String workItemOid = request.getParameter("oid");
            ReferenceFactory rf = new ReferenceFactory();
            WorkItem wi = (WorkItem) rf.getReference(workItemOid).getObject();
            if("COMPLETED".equals(wi.getStatus().toString())){
                return "";
            }
            WfActivity wfAct = (WfActivity) wi.getSource().getObject();
            WfProcess process = wfAct.getParentProcess();
            InputStream is = WfUtil.getAttachByWfProcess(process);
            if (is != null) {
                Properties pro = new Properties();
                pro.load(is);
                is.close();
                if (pro.get(key)==null) {
                    return "";
                }else {
                    return String.valueOf(pro.get(key));
                }
            }

        } catch (WTRuntimeException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

	private static Map<String,String> getOldValue(ModelContext mc) {
        try {
            NmCommandBean commandBean = mc.getNmCommandBean();
            HttpServletRequest request = commandBean.getRequest();
            String workItemOid = request.getParameter("oid");
            ReferenceFactory rf = new ReferenceFactory();
            WorkItem wi = (WorkItem) rf.getReference(workItemOid).getObject();
            if("COMPLETED".equals(wi.getStatus().toString())){
                return null;
            }
            WfActivity wfAct = (WfActivity) wi.getSource().getObject();
            WfProcess process = wfAct.getParentProcess();
            InputStream is = WfUtil.getAttachByWfProcess(process);
            if (is != null) {
                Properties pro = new Properties();
                pro.load(is);
                is.close();
                Iterator ite = pro.keySet().iterator();
                Map<String,String> map = new HashMap<String,String>();
                while(ite.hasNext()) {
                    String key = String.valueOf(ite.next());
                    String value = String.valueOf(pro.get(key));
                    map.put(key, value);
                }
                request.getSession().setAttribute("oldMap", map);
                return map;
            } else {
                request.setAttribute("hasAttach", "no");
            }

        } catch (WTRuntimeException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
