package ext.casc.workflow.tree;

import cn.hutool.core.util.StrUtil;
import com.glaway.mpm.util.DBConnUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import com.ptc.extend.ixb.CmExpImpSearchHelper;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changepackaged.ChangePackagedResultLink;
import ext.ases.changerequest.ChangeRequest;
import ext.ases.envelope.EnvelopeMemberLink;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.change.ChangeHelper;
import ext.casc.constants.Constants;
import ext.casc.part.FaCiBomHelper;
import ext.casc.preview.Preview;
import ext.casc.preview.PreviewObject;
import ext.casc.preview.PreviewUtil;
import ext.casc.util.WCUtil;
import ext.casc.workflow.WfUtil;
import ext.casc.workflow.signtrue.zp.*;
import ext.csc.utilities.principal.CSCPrincipal;
import ext.sast.center.synch.MQConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import wt.change2.WTChangeOrder2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.epm.structure.EPMMemberLink;
import wt.fc.*;
import wt.httpgw.GatewayServletHelper;
import wt.httpgw.URLFactory;
import wt.inf.container.WTContainer;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.type.TypedUtilityServiceHelper;
import wt.util.IconSelector;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignmentState;
import wt.workflow.work.WorkItem;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.net.URL;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.util.*;
import java.util.Map.Entry;

public class GenerateJson {
	Map<String,String> allZhuZhiChejian = null;
	Map<String,String> allFuZhiChejian = null;
	WTContainer container = null;
	HttpServletRequest request =null;
	String workItemOid = "";
	SignatureGYZZXMLParser sxp = null;
    SignatureGYYXMLParser sxp2 = null;
    SignatureGYYXMLParser sxpOld = null;
    String activityName ="";
    String roleName ="";
    WorkItem wi = null;
    WfAssignmentState state = null;
    WfActivity wfAct = null;
    Object pbo = null;
    WTUser currentuser = null;
    String zpr = "";
    ReferenceFactory rf = new ReferenceFactory();
	public GenerateJson(String oid,HttpServletRequest request){
		this.request= request;
		this.workItemOid = oid;
		activityName = HuiQianWorkFlowService.getActivityNameByWorkItemOid(workItemOid);
		roleName = HuiQianWorkFlowService.getRoleByWorkItemOid(workItemOid);
	    try {
			wi= (WorkItem) rf.getReference(workItemOid).getObject();
			state = wi.getStatus();
		    wfAct = (WfActivity) wi.getSource().getObject();
		    pbo = wfAct.getContext().getValue("primaryBusinessObject");
		    currentuser = (WTUser) SessionHelper.manager.getPrincipal();
		    zpr = currentuser.getPersistInfo().getObjectIdentifier().toString();
		    if(activityName.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG)||activityName.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIHUIQIANBUMEN)){
	            InputStream is = SignatureService.getAttachmentsFromPBO((ContentHolder)pbo, ContentRoleType.SECONDARY, "signature_emps.xml");
	            if (is !=null) {
	            	sxp = new SignatureGYZZXMLParser(is);
				}
		    }else if(activityName.equals(Constants.ACTIVITYNAME_ZPGYHQ)||activityName.equals("指派工艺员")||activityName.equals(Constants.ACTIVITYNAME_ZPWZHQ)||activityName.equals("指派其他会签")||activityName.equals(Constants.TASK_ZHIPAIGONGYIYUSHEN)){
	            InputStream isOld = SignatureService.getAttachmentsFromPBO((ContentHolder) pbo, ContentRoleType.SECONDARY, "signature_emps2_old.xml");
	            InputStream	is = SignatureService.getAttachmentsFromPBO((ContentHolder) pbo, ContentRoleType.SECONDARY, "signature_emps2.xml");
	            sxp2 = new SignatureGYYXMLParser(is);
	            sxpOld = new SignatureGYYXMLParser(isOld);
		    }
	    } catch (WTRuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


    public  JSONObject generateTable() throws JSONException, WTException, RemoteException {
      //切换系统管理员
        List resultList = new ArrayList();
		WTUser admin = (WTUser)SessionHelper.manager.setAdministrator();
        ISignatureParser parser = null;
        if(parser==null){
			if("指派工艺员".equals(activityName)||Constants.ACTIVITYNAME_ZPGYHQ.equals(activityName)||Constants.ACTIVITYNAME_ZPWZHQ.equals(activityName)||"指派其他会签".equals(activityName)||Constants.TASK_ZHIPAIGONGYIYUSHEN.equals(activityName)){
				parser = new SignatureGYZZXMLParser((ContentHolder)pbo);
			}else if("工艺会签".equals(activityName)||Constants.ACTIVITYNAME_WZHQ.equals(activityName)|| MQConstants.ACTIVITY_NAME_KRS.equals(activityName)||Constants.TASK_GONGYYUSHEN.equals(activityName)||Constants.TASK_GONGYYUSHEN.equals(activityName)){
				parser = new SignatureGYYXMLParser((ContentHolder)pbo);
			}
		}
        if(pbo instanceof ProcessEnvelope){
			ProcessEnvelope pe = (ProcessEnvelope)pbo;			//获取pbo对象
			List list = SignatureService.getGYHQDealMembers(pe,currentuser,wi,parser);
			resultList.addAll(list);
		}else if (pbo instanceof WTChangeOrder2){
			WTChangeOrder2 ecn = (WTChangeOrder2)pbo;
			List list = ChangeHelper.getChangeResultItem(ecn);
			list = SignatureService.getGYHQDealMembers(list, currentuser, wi,parser);
			resultList.addAll(list);
            if(parser==null||parser.hasPrivilege((Persistable)ecn, currentuser, wi)){
            	resultList.add(ecn);
            }

		}else if (pbo instanceof WTDocument){
			resultList.add(pbo);
		}else if (pbo instanceof MPMProcessPlan) {
			resultList.add(pbo);
        } else if (pbo instanceof ChangeRequest) {
            ChangeRequest request = (ChangeRequest) pbo; // 获取pbo对象
            if(parser==null||parser.hasPrivilege((Persistable)request, currentuser, wi)){
            	resultList.add(request);
            }
        } else if (pbo instanceof ChangePackaged) {
			ChangePackaged change = (ChangePackaged) pbo; // 获取pbo对象
			if(parser==null||parser.hasPrivilege((Persistable)change, currentuser, wi)){
				resultList.add(change);
			}
			QueryResult qr = PersistenceHelper.manager.navigate(change,
					ChangePackagedResultLink.ROLE_BOBJECT_ROLE,
					ChangePackagedResultLink.class, true);
			while (qr.hasMoreElements()) {
				WTObject wtobject = (WTObject) qr.nextElement();
				if (!(wtobject instanceof WTPart)) {
					if(parser==null||parser.hasPrivilege((Persistable)wtobject, currentuser, wi)){
						resultList.add(wtobject);
					}
				}
			}
		} else if(pbo instanceof Preview) {
			Preview preview = (Preview) pbo;
			ArrayList<WTObject> list = PreviewUtil.getAllMembers(preview);
			list = (ArrayList<WTObject>) SignatureService.getGYHQDealMembers(list, currentuser, wi,parser);
			resultList.addAll(list);
		}

		//20250103 start
		List tempList = new ArrayList();
		tempList.addAll(resultList);
		try {
			SignatureService.filtrate(resultList);
		} catch(IllegalArgumentException e){
			System.out.println("====>>>> jdk bug: " + e);
			e.printStackTrace();
			resultList = new ArrayList();
			resultList.addAll(tempList);
		}
		//end

		JSONObject jsonObject = generateData(resultList);
		JSONArray  array = (JSONArray)jsonObject.get("data");
		jsonObject.put("totalCount", array.length());
		SessionHelper.manager.setPrincipal(currentuser.getAuthenticationName());

		return jsonObject;
    }

    public  JSONObject  generateData(List datas){
    	JSONObject result = new JSONObject();
    	JSONArray array = new JSONArray();

    	try {
	    	for(Object o :datas){
	    		JSONObject jsonObject = new JSONObject();
	    		Versioned version = null;
	            String veroid = null;
	            if (o instanceof ChangePackaged) {
	            	veroid = rf.getReference((ChangePackaged)o).toString();
	    		}else if (o instanceof ChangeRequest) {
	            	veroid = rf.getReference((ChangeRequest)o).toString();
	    		} else if(o instanceof PreviewObject) {
					veroid = rf.getReference((PreviewObject) o).toString();
				} else{
	    			 QueryResult qr = VersionControlHelper.service.allVersionsFrom((Versioned) o);
	    		        if (qr.hasMoreElements()) {
	    		            version = (Versioned) qr.nextElement();
	    		            veroid = rf.getReference(version).toString();
	    		        }
	    		}
	            veroid = veroid.replaceAll(">", ":");

	    		if(o instanceof EPMDocument){
	    			EPMDocument epm = (EPMDocument)o;
	    			jsonObject.put("id",veroid);
	    			jsonObject.put("icontype", getDataValue("icontype",epm,veroid));

	               // HashMap<String, String> m = new HashMap<String, String>();
					//m.put("oid", rf.getReferenceString(epm));
					//String newLocation = uf.getHREF("servlet/TypeBasedIncludeServlet", m, true );
					//newLocation=newLocation.replaceAll("/", "\\/");
	    			jsonObject.put("number", epm.getNumber()+"@"+rf.getReferenceString(epm));
	    			jsonObject.put("name", epm.getName());
	    			jsonObject.put("version", epm.getVersionIdentifier().getValue() + "." + epm.getIterationIdentifier().getValue());
	    			jsonObject.put("state", epm.getLifeCycleState().getDisplay(Locale.CHINA));
	    			//jsonObject.put("updatestate", getDataValue("updatestate",epm,veroid));
	    			//jsonObject.put("implementadvise", getDataValue("implementadvise",epm,veroid));
	    			jsonObject.put("sign_result", getDataValue("sign_result",epm,veroid));
	    			jsonObject.put("sign_advise", getDataValue("sign_advise",epm,veroid));
	    			//jsonObject.put("check_report", getDataValue("check_report",epm,veroid));
	    			 if (activityName.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG)||activityName.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIHUIQIANBUMEN)) {
	    				 jsonObject.put("zhuzhichejian", getDataValue("zhuzhichejian",epm,veroid));
	    				 jsonObject.put("fuzhichejian", getDataValue("fuzhichejian",epm,veroid));
	    			 }
	    			 if (activityName.equals(Constants.ACTIVITYNAME_ZPGYHQ)||activityName.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIHUIQIANBUMEN)||activityName.equals(Constants.ACTIVITYNAME_ZPWZHQ)||activityName.equals("指派其他会签")
					 	|| activityName.equals(Constants.TASK_ZHIPAIGONGYIYUSHEN)) {
	    				 jsonObject.put("sign_person", getDataValue("sign_person",epm,veroid));
//	    				 jsonObject.put("sign_otherperson", getDataValue("sign_otherperson",epm,veroid));
	    			 }
	    			//jsonObject.put("CMAT", iba.getIBAValue("CMAT"));
	    			//jsonObject.put("CMATUP", iba.getIBAValue("CMAT_UP"));
	    			//jsonObject.put("CMATDOWN", iba.getIBAValue("CMAT_DOWN"));
	    			//jsonObject.put("CSIZE", iba.getIBAValue("CSIZE"));
	    			//jsonObject.put("count", getDataValue("count",epm,veroid));
	    			//jsonObject.put("DESIGNER", iba.getIBAValue("DESIGNER"));
	    			//jsonObject.put("PTC_MATERIAL_NAME", iba.getIBAValue("PTC_MATERIAL_NAME"));


	    		}else if(o instanceof WTDocument){
	    			WTDocument doc = (WTDocument)o;
	    			jsonObject.put("id",veroid);
	    			jsonObject.put("icontype", getDataValue("icontype",doc,veroid));
	    			jsonObject.put("number", doc.getNumber()+"@"+rf.getReferenceString(doc));
	    			jsonObject.put("name", doc.getName());
	    			jsonObject.put("version", doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());
	    			jsonObject.put("state", doc.getLifeCycleState().getDisplay(Locale.CHINA));
	    			//jsonObject.put("updatestate", getDataValue("updatestate",doc,veroid));
	    			//jsonObject.put("implementadvise", getDataValue("implementadvise",doc,veroid));
	    			jsonObject.put("sign_result", getDataValue("sign_result",doc,veroid));
	    			jsonObject.put("sign_advise", getDataValue("sign_advise",doc,veroid));
	    			//jsonObject.put("check_report", getDataValue("check_report",doc,veroid));
	    			if (activityName.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG)||activityName.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIHUIQIANBUMEN)) {
	    				 jsonObject.put("zhuzhichejian", getDataValue("zhuzhichejian",doc,veroid));
	    				 jsonObject.put("fuzhichejian", getDataValue("fuzhichejian",doc,veroid));
	    			 }
	    			 if (activityName.equals(Constants.ACTIVITYNAME_ZPGYHQ)||activityName.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIHUIQIANBUMEN)||activityName.equals(Constants.ACTIVITYNAME_ZPWZHQ)
					 	|| activityName.equals(Constants.TASK_ZHIPAIGONGYIYUSHEN)) {
	    				 jsonObject.put("sign_person", getDataValue("sign_person",doc,veroid));
	    			 }
	    			//jsonObject.put("CMAT", iba.getIBAValue("CMAT"));
	    			//jsonObject.put("CMATUP", iba.getIBAValue("CMAT_UP"));
	    			//jsonObject.put("CMATDOWN", iba.getIBAValue("CMAT_DOWN"));
	    			//jsonObject.put("CSIZE", iba.getIBAValue("CSIZE"));
	    			//jsonObject.put("count","");
	    			//jsonObject.put("DESIGNER", iba.getIBAValue("DESIGNER"));
	    			//jsonObject.put("PTC_MATERIAL_NAME", iba.getIBAValue("PTC_MATERIAL_NAME"));
	    		}else if(o instanceof ChangePackaged){
	    			ChangePackaged cp = (ChangePackaged)o;
	    			jsonObject.put("id",veroid);
	    			jsonObject.put("icontype", getDataValue("icontype",cp,veroid));
	    			jsonObject.put("number", cp.getNumber()+"@"+rf.getReferenceString(cp));
	    			jsonObject.put("name", cp.getName());
	    			jsonObject.put("state", cp.getLifeCycleState().getDisplay(Locale.CHINA));
	    			//jsonObject.put("updatestate", getDataValue("updatestate",cp,veroid));
	    			//jsonObject.put("implementadvise", getDataValue("implementadvise",cp,veroid));
	    			jsonObject.put("sign_result", getDataValue("sign_result",cp,veroid));
	    			jsonObject.put("sign_advise", getDataValue("sign_advise",cp,veroid));
	    			if (activityName.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG)||activityName.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIHUIQIANBUMEN)) {
	    				 jsonObject.put("zhuzhichejian", getDataValue("zhuzhichejian",cp,veroid));
	    				 jsonObject.put("fuzhichejian", getDataValue("fuzhichejian",cp,veroid));
	    			 }
	    			 if (activityName.equals(Constants.ACTIVITYNAME_ZPGYHQ)||activityName.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIHUIQIANBUMEN)||activityName.equals(Constants.ACTIVITYNAME_ZPWZHQ)) {
	    				 jsonObject.put("sign_person", getDataValue("sign_person",cp,veroid));
	    			 }
	    		}else if(o instanceof ChangeRequest){
	    			ChangeRequest cr = (ChangeRequest)o;
	    			jsonObject.put("id",veroid);
	    			jsonObject.put("icontype", getDataValue("icontype",cr,veroid));
	    			jsonObject.put("number", cr.getNumber()+"@"+rf.getReferenceString(cr));
	    			jsonObject.put("name", cr.getName());
	    			jsonObject.put("state", cr.getLifeCycleState().getDisplay(Locale.CHINA));
	    			//jsonObject.put("updatestate", getDataValue("updatestate",cr,veroid));
	    			//jsonObject.put("implementadvise", getDataValue("implementadvise",cr,veroid));
	    			jsonObject.put("sign_result", getDataValue("sign_result",cr,veroid));
	    			jsonObject.put("sign_advise", getDataValue("sign_advise",cr,veroid));
	    			if (activityName.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG)||activityName.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIHUIQIANBUMEN)) {
	    				 jsonObject.put("zhuzhichejian", getDataValue("zhuzhichejian",cr,veroid));
	    				 jsonObject.put("fuzhichejian", getDataValue("fuzhichejian",cr,veroid));
	    			 }
	    			 if (activityName.equals(Constants.ACTIVITYNAME_ZPGYHQ)||activityName.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIHUIQIANBUMEN)||activityName.equals(Constants.ACTIVITYNAME_ZPWZHQ)) {
	    				 jsonObject.put("sign_person", getDataValue("sign_person",cr,veroid));
	    			 }
	    		}else if(o instanceof WTChangeOrder2){
	    			WTChangeOrder2 cp = (WTChangeOrder2)o;
	    			jsonObject.put("id",veroid);
	    			jsonObject.put("icontype", getDataValue("icontype",cp,veroid));
	    			jsonObject.put("number", cp.getNumber());
	    			jsonObject.put("name", cp.getName());
	    			jsonObject.put("state", cp.getLifeCycleState().getDisplay(Locale.CHINA));
	    			//jsonObject.put("updatestate", getDataValue("updatestate",cp,veroid));
	    			//jsonObject.put("implementadvise", getDataValue("implementadvise",cp,veroid));
	    			jsonObject.put("sign_result", getDataValue("sign_result",cp,veroid));
	    			jsonObject.put("sign_advise", getDataValue("sign_advise",cp,veroid));
	    			if (activityName.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG)||activityName.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIHUIQIANBUMEN)) {
	    				 jsonObject.put("zhuzhichejian", getDataValue("zhuzhichejian",cp,veroid));
	    				 jsonObject.put("fuzhichejian", getDataValue("fuzhichejian",cp,veroid));
	    			 }
	    			 if (activityName.equals(Constants.ACTIVITYNAME_ZPGYHQ)||activityName.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIHUIQIANBUMEN)||activityName.equals(Constants.ACTIVITYNAME_ZPWZHQ)) {
	    				 jsonObject.put("sign_person", getDataValue("sign_person",cp,veroid));
	    			 }
	    		}else if(o instanceof PreviewObject){
					PreviewObject previewObject = (PreviewObject)o;
					jsonObject.put("id",veroid);
					jsonObject.put("icontype", getDataValue("icontype",previewObject,veroid));
					jsonObject.put("number", previewObject.getNumber());
					jsonObject.put("name", previewObject.getName());
					jsonObject.put("version", previewObject.getObjVer());
					jsonObject.put("modelMaturity", previewObject.getModelMaturity());
					jsonObject.put("maturityReason", previewObject.getMaturityReason());
					jsonObject.put("sign_result", getDataValue("sign_result",previewObject,veroid));
					jsonObject.put("sign_advise", getDataValue("sign_advise",previewObject,veroid));
					if (activityName.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG)) {
						jsonObject.put("zhuzhichejian", getDataValue("zhuzhichejian",previewObject,veroid));
						jsonObject.put("fuzhichejian", getDataValue("fuzhichejian",previewObject,veroid));
					}
					if (activityName.equals(Constants.TASK_ZHIPAIGONGYIYUSHEN) || activityName.equals(Constants.TASK_ZHIPAIGONGYIHUIQIAN)) {
						jsonObject.put("sign_person", getDataValue("sign_person",previewObject,veroid));
					}
				}else if(o instanceof WTPart){
					WorkItem workItem = (WorkItem) rf.getReference(workItemOid).getObject();
					WfActivity wfAct = (WfActivity) workItem.getSource().getObject();
					WfProcess wfProcess = wfAct.getParentProcess();
					if(FaCiBomHelper.FACI_BOM_WORKFLOW.equals(wfProcess.getTemplate().getName()))
					{
						WTPart part = (WTPart)o;
						jsonObject.put("id",veroid);
						jsonObject.put("icontype", getDataValue("icontype",part,veroid));
						jsonObject.put("number", part.getNumber()+"@"+rf.getReferenceString(part));
						jsonObject.put("name", part.getName());
						jsonObject.put("version", part.getVersionIdentifier().getValue() + "." + part.getIterationIdentifier().getValue());
						jsonObject.put("state", part.getLifeCycleState().getDisplay(Locale.CHINA));
						jsonObject.put("sign_result", getDataValue("sign_result",part,veroid));
						jsonObject.put("sign_advise", getDataValue("sign_advise",part,veroid));

						if (activityName.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG)||activityName.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIHUIQIANBUMEN)) {
							jsonObject.put("zhuzhichejian", getDataValue("zhuzhichejian",part,veroid));
							jsonObject.put("fuzhichejian", getDataValue("fuzhichejian",part,veroid));
						}
						if (activityName.equals(Constants.ACTIVITYNAME_ZPGYHQ)||activityName.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIHUIQIANBUMEN)||activityName.equals(Constants.ACTIVITYNAME_ZPWZHQ)
								|| activityName.equals(Constants.TASK_ZHIPAIGONGYIYUSHEN)) {
							jsonObject.put("sign_person", getDataValue("sign_person",part,veroid));
						}
					}

				}

				if(isPreviewPkg(pbo) && o != null) {
					try {
						EnvelopeMemberLink link = CmExpImpSearchHelper.searchEnvelopeMemberLink((ProcessEnvelope) pbo, (RevisionControlled) o);
						if(link != null) {
							jsonObject.put("modelMaturity", link.getDescription());
							jsonObject.put("maturityReason", link.getImplementadvise());
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
	    		array.put(jsonObject);
	    	}
	    	result.put("data", array);
    	} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return result;
    }

    @SuppressWarnings("unused")
    public  String getDataValue(String columnName, Object obj,String veroid) throws Exception {
    	String oid = PersistenceHelper.getObjectIdentifier((Persistable) obj).toString();
        String name ="";

		SignatureRecord record =null;
		if ( sxp!=null) {
			record = sxp.getSignatureRecordByEPMOid(oid+"_"+zpr);
		}
		String number = "";

		if (obj instanceof EPMDocument) {
		    container =(( EPMDocument)obj).getContainer();
		    number = (( EPMDocument)obj).getNumber();
	   	 }else if(obj instanceof WTDocument){
	   		 container =(( WTDocument)obj).getContainer();
	   		number = (( WTDocument)obj).getNumber();
	   	 }else if(obj instanceof WTChangeOrder2){
	   		 container =(( WTChangeOrder2)obj).getContainer();
	   		number = (( WTChangeOrder2)obj).getNumber();
	   	 }else if(obj instanceof ChangePackaged){
	   		 container =(( ChangePackaged)obj).getContainer();
	   		number = (( ChangePackaged)obj).getNumber();
	   	 }else if(obj instanceof ChangeRequest){
	   		 container =(( ChangeRequest)obj).getContainer();
	   		number = (( ChangeRequest)obj).getNumber();
		} else if(obj instanceof PreviewObject) {
			container = ((Preview) pbo).getContainer();
			number = ((PreviewObject) obj).getNumber();
		}else if(obj instanceof WTPart) {
			container = ((ProcessEnvelope) pbo).getContainer();
			number = ((WTPart) obj).getNumber();
		}

		if ("zhuzhichejian".equals(columnName)) {
			//allZhuZhiChejian = (Map<String,String>)request.getAttribute("allZhuZhiChejian");
			if((activityName.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG)||activityName.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIHUIQIANBUMEN))&&allZhuZhiChejian==null){
				Map<String,String> allChejian = SignatureService.getAllCheJianAndXiangMuBuMapGYZZ(container,currentuser);
				allZhuZhiChejian=SignatureService.sortZhuZhiChejian(allChejian);
				allZhuZhiChejian.put("其他",rf.getReferenceString(currentuser));

			}
			//回显保存后的信息
			String key = workItemOid+"_"+oid+"_sign_person";
			String key2 = workItemOid+"_"+oid+"_sign_person_value";
			String key3 = workItemOid+"_"+oid+"_sign_person_valueA";
			String key4 = workItemOid+"_"+oid+"_sign_person_valueB";
		    /*String oldValue = getOldValue(workItemOid, key);
		    String oldValue2 = getOldValue(workItemOid, key2);
		    String zzcjValue = getOldValue(workItemOid, key3);
		    String fzcjValue = getOldValue(workItemOid, key4);*/
	        String zhuzhichejian = "";
	        if(record!=null){
	        	zhuzhichejian = record.getZhuzhichejian();
	        }
	        //回显保存后的信息
	        /*if ((zzcjValue != null)&&(!"".equals(zzcjValue))&&!"COMPLETED".equals(state.toString())) {
	            zhuzhichejian = zzcjValue;
	        }*/
	        //Map<String,String> users = SignatureService.getAllCheJianAndXiangMuBuMapGYZZ(container);
	        StringBuffer sb = new StringBuffer();

	        if(allZhuZhiChejian!=null){
		        sb.append("<select id=\"" +veroid +"_sign_person_value\" " + "name=\""+oid +"_sign_person_valueA\" value='"+zhuzhichejian+"' onChange=\"selectUser2(this,'"+veroid+"')\">");//getReference取到Version oid
		        Set<Entry<String, String>> useSets= allZhuZhiChejian.entrySet();
		        sb.append("<option value=\"\"/>");

		        for (Iterator<Entry<String, String>> iterator = useSets.iterator(); iterator.hasNext();) {

		            Entry<String, String> entry = iterator.next();

		            if(zhuzhichejian!=null &&entry.getValue()!=null&&!"null".equals(zhuzhichejian) && zhuzhichejian.contains(entry.getValue())){
		                sb.append("<option value=\""+entry.getKey()+"-"+entry.getValue()+"\" selected>");
		                sb.append(entry.getKey());
		                sb.append("</option>");
		            }else{
		                sb.append("<option value=\""+entry.getKey()+"-"+entry.getValue()+"\">");
		                sb.append(entry.getKey());
		                sb.append("</option>");
		            }

		        }
		        sb.append("</select>");
	        }
	        String value = sb.toString();
	        return value;
	    } else if ("fuzhichejian".equals(columnName)) {
	    	//allFuZhiChejian = (Map<String,String>)request.getAttribute("allFuZhiChejian");
	    	if((activityName.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG)||activityName.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIHUIQIANBUMEN))&&allFuZhiChejian==null){
				Map<String,String> allChejian = SignatureService.getAllCheJianAndXiangMuBuMapGYZZ(container,currentuser);
				allFuZhiChejian=SignatureService.sortFuZhiChejian(allChejian);
				allFuZhiChejian.put("其他",rf.getReferenceString(currentuser));
			}
			String key = workItemOid+"_"+oid+"_sign_person";
			String key2 = workItemOid+"_"+oid+"_sign_person_value";
			String key3 = workItemOid+"_"+oid+"_sign_person_valueA";
			String key4 = workItemOid+"_"+oid+"_sign_person_valueB";
		    //String oldValue = getOldValue(workItemOid, key);
		   // String oldValue2 = getOldValue(workItemOid, key2);
		   // String zzcjValue = getOldValue(workItemOid, key3);
		   // String fzcjValue = getOldValue(workItemOid, key4);
	    	String fuzhichejian = "";
	        if(record!=null){
	        	fuzhichejian = record.getFuzhichejian();
	        }
	        //回显保存后的信息
	       /* if ((fzcjValue != null)&&(!"".equals(fzcjValue))&&!"COMPLETED".equals(state.toString())) {
	            fuzhichejian = fzcjValue;
	        }*/
	       // Map<String,String> users = SignatureService.getAllCheJianAndXiangMuBuMapGYZZ(container);
	        StringBuffer sb = new StringBuffer();
	//                    qr = VersionControlHelper.service.allVersionsFrom((Versioned)obj);
	//                    if(qr.hasMoreElements())
	//                        version = (Versioned)qr.nextElement();
	        //System.out.println("version is:" + version);//此为小版本
	        if(allFuZhiChejian!=null){
		        Set<Entry<String, String>> useSets= allFuZhiChejian.entrySet();
		        for (Iterator<Entry<String, String>> iterator = useSets.iterator(); iterator.hasNext();) {

		            Entry<String, String> entry = iterator.next();
		            if (fuzhichejian!=null &&entry.getValue()!=null &&!"null".equals(fuzhichejian) && fuzhichejian.contains(entry.getKey()+"-"+entry.getValue())) {//如果是工艺更改或临时工艺，则显示上一次选择的辅制车间记录
		                sb.append("<input type=\"checkbox\" checked id=\""+veroid+"_sign_person_value"+entry.getKey()+"\" name=\""+oid+"_sign_person_valueB\" value='"+entry.getKey()+"-"+entry.getValue()+"' onChange=\"selectUser3(this,'"+veroid+"')\">"+entry.getKey()+"；</input>");
		            } else {
		                sb.append("<input type=\"checkbox\" id=\""+veroid+"_sign_person_value"+entry.getKey()+"\" name=\""+oid+"_sign_person_valueB\" value='"+entry.getKey()+"-"+entry.getValue()+"' onChange=\"selectUser3(this,'"+veroid+"')\">"+entry.getKey()+"；</input>");
		            }

		        }
	        }
	        //sb.append("<input type=\"hidden\"   name=\"" + oid + "_sign_person_valueB\" id=\"" + veroid + "_sign_person_valueB\" value=\""+persons+"\">");

	        String value = sb.toString();
	        //value = value + addrelated2DDRW(obj,oid,veroid);
	        value = value +"<input type=\"hidden\"   name=\"" + oid + "_number\" id=\"" + veroid + "_number\" value=\""+number+"\">";
	        return value;
	    }else if (columnName.equals("sign_person")) {//工艺会签人员:工艺组长、工艺员
            record = sxp2.getSignatureRecordByEPMOid(oid+"_"+zpr);
            if(record==null){
            	record = sxpOld.getSignatureRecordByEPMOid(oid+"_"+zpr);
            }
            String persons = "";
            String personsDis = "";
            if(record!=null){
                persons = record.getPersons();
                personsDis = record.getPersonsDis();
            }
            //String value = "<input type=\"button\" value=\"设置会签人员\" onclick=\"javascript:alert(11);\" id=\"" + oid + "_person\"/>";
           // String value = "<input type=\"text\"  readonly  id=\"" +  veroid + "_sign_person\" value=\""+personsDis+"\"/><input type=\"button\" value=\"选择\" onclick=\"javascript:showHQRY('"+veroid+"');\"  id=\"" +  veroid + "_selecPer\"/><input type=\"button\" value=\"清空\" onclick=\"javascript:clearHQRY('"+ veroid+"');\"/><input type=\"hidden\"   id=\"" +  veroid + "_sign_person_value\" value=\""+persons+"\">";
            String value = "<input type=\"text\"  readonly name=\"" + oid + "_sign_person\"  id=\"" + veroid + "_sign_person\" value=\""+personsDis+"\"/><input type=\"button\" value=\"选择工艺员\" onclick=\"javascript:showHQRY('"+veroid+"');\"  id=\"" + veroid + "_selecPer\"/><input type=\"button\" value=\"清空\" onclick=\"javascript:clearOtherUser('"+veroid+"');\"  id=\"" + veroid + "_selecClearPer\"/><input type=\"hidden\"   name=\"" + oid + "_sign_person_value\" id=\"" + veroid + "_sign_person_value\" value=\""+persons+"\">";

            if("SHISANCHEJIANGONGYIZUZHANG".equals(roleName)){
                 value = "<input type=\"text\"  readonly name=\"" + oid + "_sign_person\"  id=\"" + veroid + "_sign_person\" value=\""+personsDis+"\"/><input type=\"button\" value=\"选择其他人员\" onclick=\"javascript:showOTHERHQRY('"+veroid+"');\"  id=\"" + veroid + "_selecOtherPer\"/><input type=\"button\" value=\"清空\" onclick=\"javascript:clearOtherUser('"+veroid+"');\"  id=\"" + veroid + "_selecClearPer\"/><input type=\"hidden\"   name=\"" + oid + "_sign_person_value\" id=\"" + veroid + "_sign_person_value\" value=\""+persons+"\">";
            }
            // String value = "<input type=\"text\"  readonly  id=\"" + oid + "_sign_person\" value=\"\"/><input type=\"button\" value=\"选择\" onclick=\"javascript:showHQRY('"+oid+"');\"  id=\"" + oid + "_selecPer\"/><input type=\"button\" value=\"清空\" onclick=\"javascript:clearHQRY('"+oid+"');\"/><input type=\"hidden\"  value=''  id=\"" + oid + "_sign_person_value\" value=\"\">";
             if("COMPLETED".equals(state.toString())){
                 value = "<input type=\"text\"  readonly  name=\"" + oid + "_sign_person\" id=\"" + veroid + "_sign_person\" value=\""+personsDis+"\"/><input type=\"hidden\"   id=\"" + veroid + "_sign_person_value\" value=\""+persons+"\">";
             }
            // value = value + addrelated2DDRW(obj,oid,veroid);
             value = value +"<input type=\"hidden\"   name=\"" + oid + "_number\" id=\"" + veroid + "_number\" value=\""+number+"\">";
            return value;
        }  if (columnName.equals("icontype")) {
            WTPart part = null;
            WTDocument doc = null;
            EPMDocument epmdoc = null;
            WTChangeOrder2 co = null;
            MPMProcessPlan processPlan = null;
            ChangePackaged cp = null;
            ChangeRequest cr = null;
			PreviewObject previewObject = null;
            if (obj instanceof WTPart) {
                part = (WTPart) obj;
            } else if (obj instanceof WTDocument) {
                doc = (WTDocument) obj;
            } else if (obj instanceof EPMDocument) {
                epmdoc = (EPMDocument) obj;
            } else if (obj instanceof WTChangeOrder2) {
                co = (WTChangeOrder2) obj;
            } else if (obj instanceof ChangePackaged) {
                cp = (ChangePackaged) obj;
            }else if (obj instanceof ChangeRequest) {
                cr = (ChangeRequest) obj;
			} else if(obj instanceof PreviewObject) {
				previewObject = (PreviewObject) obj;
			}
			String value = "";
            if (part != null) {
                String partNumber = part.getNumber();
                String conOid = PersistenceHelper.getObjectIdentifier((Persistable) container).toString();
                oid = oid.replaceAll(":", "%3A");
                conOid = conOid.replaceAll(":", "%3A");
                String imgUrl = getIcon(part);
                URLFactory uf = new URLFactory();
                HashMap m = new HashMap();
                m.put("oid", oid);
                m.put("action", "ObjProps");
                String urlInfo = GatewayServletHelper.buildAuthenticatedHREF(uf, "wt.enterprise.URLProcessor",
                        "URLTemplateAction", m);
                value = "<img src=\"" + imgUrl + "\">";
            } else if (doc != null) {
                String docImgUrl = getIcon(doc);
                String docNumber = doc.getNumber();
                URLFactory uf = new URLFactory();
                HashMap m = new HashMap();
                m.put("oid", oid);
                m.put("action", "ObjProps");
                String urlInfo = GatewayServletHelper.buildAuthenticatedHREF(uf, "wt.enterprise.URLProcessor",
                        "URLTemplateAction", m);
                value = value + "<img src=\"" + docImgUrl + "\">";
            } else if (epmdoc != null) {
                String docImgUrl = getIcon(epmdoc);
                String docNumber = epmdoc.getNumber();
                URLFactory uf = new URLFactory();
                HashMap m = new HashMap();
                m.put("oid", oid);
                m.put("action", "ObjProps");
                String urlInfo = GatewayServletHelper.buildAuthenticatedHREF(uf, "wt.enterprise.URLProcessor",
                        "URLTemplateAction", m);
                value = value + "<img src=\"" + docImgUrl + "\">";
            } else if (co != null) {
                String docImgUrl = getIcon(co);
                String docNumber = co.getNumber();
                URLFactory uf = new URLFactory();
                HashMap m = new HashMap();
                m.put("oid", oid);
                m.put("action", "ObjProps");
                String urlInfo = GatewayServletHelper.buildAuthenticatedHREF(uf, "wt.enterprise.URLProcessor",
                        "URLTemplateAction", m);
                value = value + "<img src=\"" + docImgUrl + "\">";
            } else if (processPlan != null) {
                String processPlanImgUrl = getIcon(processPlan);
                String processPlanNumber = processPlan.getNumber();
                URLFactory uf = new URLFactory();
                HashMap m = new HashMap();
                m.put("oid", oid);
                m.put("action", "ObjProps");
                String urlInfo = GatewayServletHelper.buildAuthenticatedHREF(uf, "wt.enterprise.URLProcessor",
                        "URLTemplateAction", m);
                value = value + "<img src=\"" + processPlanImgUrl + "\">";
            }
            else if (cp != null) {
                String cpImgUrl = getIcon(cp);
                String cpNumber = cp.getNumber();
                URLFactory uf = new URLFactory();
                HashMap m = new HashMap();
                m.put("oid", oid);
                m.put("action", "ObjProps");
                String urlInfo = GatewayServletHelper.buildAuthenticatedHREF(uf, "wt.enterprise.URLProcessor",
                        "URLTemplateAction", m);
                value = value + "<img src=\"" + cpImgUrl + "\">";
            }else if (cr!=null) {
                String cpImgUrl = getIcon(cr);
                String cpNumber = cr.getNumber();
                URLFactory uf = new URLFactory();
                HashMap m = new HashMap();
                m.put("oid", oid);
                m.put("action", "ObjProps");
                String urlInfo = GatewayServletHelper.buildAuthenticatedHREF(uf, "wt.enterprise.URLProcessor",
                        "URLTemplateAction", m);
                value = value + "<img src=\"" + cpImgUrl + "\">";
            }else if (previewObject!=null) {
				String objType = previewObject.getObjType();
				if("EPMDocument".equals(objType)){
					String cpImgUrl = getIconByClass(EPMDocument.class);
					value = value + "<img src=\"" + cpImgUrl + "\">";
				} else if("WTDocument".equals(objType)) {
					String cpImgUrl = getIconByClass(WTDocument.class);
					value = value + "<img src=\"" + cpImgUrl + "\">";
				}
			}
            return value;
        }if (columnName.equals("count")) {
            EPMDocument epmDocument = (EPMDocument)obj;
            EPMMemberLink link = WCUtil.getEpmMemberLinkByChild(epmDocument);
            if (link != null) {
                return link.getQuantity().getAmount()+"";
            }
        }if (columnName.equals("sign_result")) {
            StringBuffer sb = new StringBuffer();
            List<String> testList = new ArrayList<String>();
            testList.add("同意");
            testList.add("无需会签");
            testList.add("不同意");
            ReferenceFactory rf = new ReferenceFactory();
            if (obj instanceof ChangePackaged) {
            	veroid = rf.getReference((ChangePackaged)obj).toString();
			}else if(obj instanceof ChangeRequest) {
				veroid = rf.getReference((ChangeRequest)obj).toString();
			}

            // System.out.println("version is:" + version);//此为小版本
            sb.append("<select name=\"" + veroid + "_select\" " + "id=\"" + oid + "_select\" onChange=\"selectSignResult(this,'"+veroid+"')\">");// getReference取到Version
            String key = workItemOid+"_"+veroid+"_select";
       /*     String oldValue = getOldValue(workItemOid, key);
            if (oldValue==null) {
                oldValue = "";
            }*/
			String oldValue = "";

			if("COMPLETED".equals(wi.getStatus().toString())){
				String activity = rf.getReference(wfAct).toString();
				DBConnUtil connUtil = null;
				ResultSet resultSet = null;
				try {
					connUtil = new DBConnUtil();
					long id = PersistenceHelper.getObjectIdentifier((Persistable) obj).getId();
					String sql = "SELECT ah.* from ASESHUIQIANSIGNATURE ah left join SIGNLINK sl on sl.IDA3B5 = ah.IDA2A2 where sl.IDA3A5 = '" + id +
							"' and ah.ACTIVITY = '" + activity + "'";
					resultSet = connUtil.executeQuery(sql);
					if (resultSet.next()){
						String conclusion = resultSet.getString("CONCLUSION");
						if(conclusion.contains("不同意")) {
							oldValue = "不同意";
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if(resultSet != null){
						resultSet.close();
					}
					if(connUtil != null){
						connUtil.close();
					}
				}
			}else {
				GWActivityRecord activityRecord = ActivityRecordHelper.getActivityRecord(workItemOid, veroid);
				if(activityRecord != null) {
					oldValue = activityRecord.getResult();
				}
			}

			for (int i = 0; i < testList.size(); i++) {
                String tempStr = testList.get(i);
                if (tempStr.equals(oldValue)) {
                    sb.append("<option selected=\"selected\" value=\"" + tempStr + "\">");
                }else {
                    sb.append("<option value=\"" + tempStr + "\">");
                }
                sb.append(tempStr);
                sb.append("</option>");
            }
            sb.append("</select>");
            String value = sb.toString();
            return value;
        } else if (columnName.equals("sign_advise")) {
            String key = workItemOid+"_"+oid+"_advise";
            String oldValue = "";
			/*getOldValue(workItemOid, key);
            if (oldValue==null) {
                oldValue = "";
            }*/

			if("COMPLETED".equals(wi.getStatus().toString())){
				String activity = rf.getReference(wfAct).toString();
				DBConnUtil connUtil = null;
				ResultSet resultSet = null;
				try {
					connUtil = new DBConnUtil();
					long id = PersistenceHelper.getObjectIdentifier((Persistable) obj).getId();
					String sql = "SELECT ah.* from ASESHUIQIANSIGNATURE ah left join SIGNLINK sl on sl.IDA3B5 = ah.IDA2A2 where sl.IDA3A5 = '" + id +
							"' and ah.ACTIVITY = '" + activity + "'";
					resultSet = connUtil.executeQuery(sql);
					if (resultSet.next()){
						String opinion = resultSet.getString("OPINION");
						if(StrUtil.isNotEmpty(opinion)) {
							oldValue = opinion;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if(resultSet != null){
						resultSet.close();
					}
					if(connUtil != null){
						connUtil.close();
					}
				}
			}else {
				GWActivityRecord activityRecord = ActivityRecordHelper.getActivityRecord(workItemOid, veroid);
				if(activityRecord != null) {
					oldValue = activityRecord.getAdvise();
				}
			}

            String value = "<input value=\""+oldValue+"\" type=\"text\" name=\""+veroid+"_advise\" id=\"" + oid + "_advise\"/>";
            return value;
        }else if(columnName.equals("check_report")){
        	String value = "";
        	EPMDocument epmDoc = null;
        	if(obj instanceof EPMDocument){
        		epmDoc = (EPMDocument) obj;
        	}
        	if(epmDoc != null){
//        		WTPart part = BomUtil.getPartByEPMDocument(epmDoc);
        		List<WTDocument> desDocList = WTDocumentUtil.getDocumnetByNameAndType(epmDoc.getName() + "_DFMReport.xlsx", "casc.sast.149.GONGYIJIANCHABAOGAO");
//        		List<WTDocument> desDocList = ProcessUtil.getDescribedDoc(part);
//        		for(WTDocument desDoc : desDocList){
//        			String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(desDoc);
//        			if(docType.endsWith("casc.sast.149.GONGYIJIANCHABAOGAO")){
        		if(desDocList.size() > 0){
        			ApplicationData data = WTDocumentUtil.getPrimaryByDocument(desDocList.get(0));
        			URL url = ContentHelper.service.getDownloadURL(desDocList.get(0), data);
        			String urlString = url.toString();
        			value = "<a href=\"" + urlString + "\">"+ data.getFileName() +"</a>";

        		}
//        			}
//        		}
        	}
        	return value;
        }else if (columnName.equals("updatestate")) {
            // 可能要用到
            ReferenceFactory rf = new ReferenceFactory();
            Persistable pbo = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");
            String value = null;
            if (pbo instanceof ProcessEnvelope) {
            	QueryResult qr = PersistenceHelper.manager.navigate((Persistable) obj, "theProcessEnvelope",
                        EnvelopeMemberLink.class, false);
                while (qr.hasMoreElements()) {
                    EnvelopeMemberLink link = (EnvelopeMemberLink) qr.nextElement();
                    Persistable roleA = link.getRoleAObject();
                    if (pbo.equals(roleA)) {
                        value = link.getDescription();
                        break;
                    }
                }
			}else if(pbo instanceof ChangePackaged){
                QueryResult qr = PersistenceHelper.manager.navigate((ChangePackaged)pbo, "theRevisionControlled",
                        ChangePackagedResultLink.class, false);
                while (qr.hasMoreElements()) {
                	ChangePackagedResultLink link = (ChangePackagedResultLink) qr.nextElement();
                    Persistable roleB = link.getRoleBObject();
                    if (obj.equals(roleB)) {
                        value = link.getDescription();
                        break;
                    }
                }
			}

            if (value == null) {
                value = "";
            }
            return value;
        } else if (columnName.equals("implementadvise")) {
            Persistable pbo = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");
            String value = null;
            if (pbo instanceof ProcessEnvelope) {
                QueryResult qr = PersistenceHelper.manager.navigate((Persistable) obj, "theProcessEnvelope",
                        EnvelopeMemberLink.class, false);
                while (qr.hasMoreElements()) {
                    EnvelopeMemberLink link = (EnvelopeMemberLink) qr.nextElement();
                    Persistable roleA = link.getRoleAObject();
                    if (pbo.equals(roleA)) {
                        value = link.getImplementadvise();
                        break;
                    }
                }
			}else if(pbo instanceof ChangePackaged){
				if (obj instanceof ChangePackaged) {
					ChangePackaged change = (ChangePackaged)obj;
					value = change.getImplement();
				}else{
					QueryResult qr = PersistenceHelper.manager.navigate((ChangePackaged)pbo, "theRevisionControlled",
                            ChangePackagedResultLink.class, false);
                    while (qr.hasMoreElements()) {
                    	ChangePackagedResultLink link = (ChangePackagedResultLink) qr.nextElement();
                        Persistable roleB = link.getRoleBObject();
                        if (obj.equals(roleB)) {
                            value = link.getImplementadvise();
                            break;
                        }
                    }
				}
				}

            if (value == null) {
                value = "";
            }
            return value;
        }
        return "";
    }

    private String getIcon(WTObject obj) throws WTException {
        String imgURL = null;
        try {
            IconDelegate delegate = IconDelegateFactory.getInstance()
                    .getIconDelegate(obj);
            IconSelector selector = delegate.getStandardIconSelector();
            while (!selector.isResourceKey()) {
                delegate = delegate.resolveSelector(selector);
                selector = delegate.getStandardIconSelector();
            }
            imgURL = selector.getIconKey();
        } catch (Exception e) {
            throw new WTException(e);
        }
        return imgURL;
    }

	private String getIconByClass(Class clazz) throws WTException {
		String imgURL = null;
		try {
			IconDelegate delegate = IconDelegateFactory.getInstance()
					.getIconDelegate(clazz);
			IconSelector selector = delegate.getStandardIconSelector();
			while (!selector.isResourceKey()) {
				delegate = delegate.resolveSelector(selector);
				selector = delegate.getStandardIconSelector();
			}
			imgURL = selector.getIconKey();
		} catch (Exception e) {
			throw new WTException(e);
		}
		return imgURL;
	}

    private  String addrelated2DDRW(Object obj,String oid,String veroid){
    	String value = "";
   	 	String drwveroid = "";
    	if(obj instanceof EPMDocument){
    		EPMDocument dRW2D = SignatureService.getLastestIter2DesignDocs((EPMDocument)obj);
    		if(dRW2D!=null){
        		String drwoid = PersistenceHelper.getObjectIdentifier( dRW2D).toString();
        		drwveroid = SignatureService.getVersionId(obj);
        		value = "<input type=\"hidden\"   name=\"" + oid + "_related2DDRW\"  id=\"" + veroid + "_related2DDRW\" value=\""+drwoid+"\"/>";
    		}else{
    			value = "<input type=\"hidden\"   name=\"" + oid + "_related2DDRW\"  id=\"" + veroid + "_related2DDRW\" value=\"\"/>";
    		}
    	}else{
    		value = "<input type=\"hidden\"   name=\"" + oid + "_related2DDRW\"  id=\"" + veroid + "_related2DDRW\" value=\"\"/>";
    	}
    	return value;
    }


    private  String getOldValue(String workItemOid,String key) {
        try {
            if("COMPLETED".equals(wi.getStatus().toString())){
                return "";
            }
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

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String searchUser(String searchValue) {
        JSONObject object = new JSONObject();
        JSONArray userArray = new JSONArray();

		try
		{
			if (searchValue != null && !searchValue.isEmpty()) {
				List<WTUser> userList = CSCPrincipal.getUserLikeName(searchValue);
				;
				JSONObject userObj;
				for (WTUser wtUser : userList) {
					userObj = new JSONObject();
					userObj.put("fullname", wtUser.getFullName());
					userObj.put("name", wtUser.getName());
					userObj.put("userid", wtUser.getPersistInfo().getObjectIdentifier().getId());
					userArray.put(userObj);
				}
			}
			object.put("data", userArray);
			object.put("totalCount", userArray.length());
		}catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}

        return object.toString();
    }

	public static boolean isPreviewPkg(Object obj) {
		if(obj != null && obj instanceof ProcessEnvelope){
			try {
				String identifier = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(obj);
				if(identifier.contains("casc.sast.149.PREVIEWPKG")){
					return true;
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
