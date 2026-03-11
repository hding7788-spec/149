package ext.casc.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.inf.team.ContainerTeam;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.State;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pdmlink.PDMLinkProduct;
import wt.project.Role;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.OrderByExpression;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.baseline.ManagedBaseline;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;

import com.ptc.core.foundation.type.server.impl.TypeHelper;
import com.ptc.core.meta.common.TypeIdentifier;

import ext.casc.workflow.setelectronicsignature.ElectronicSignatureHelper;

public class RemoteUtility implements RemoteAccess, Serializable{
	private static final long serialVersionUID = 1308063005528212814L;

	public static WTReference getReferenceByOid(String oid) throws WTException{
		try {
			if (!RemoteMethodServer.ServerFlag) {
				return (WTReference) RemoteMethodServer.getDefault().invoke("getReferenceByOidRemote", RemoteUtility.class.getName(), null,
						new Class[] {String.class},
						new Object[] {oid});
			} else {
				return getReferenceByOidRemote(oid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static WTReference getReferenceByOidRemote(String oid) throws WTException{
		WTReference refer = null;
		String user = "";
		
    	try {
    		user = wt.session.SessionHelper.manager.getPrincipal().getName();
    		wt.session.SessionHelper.manager.setAdministrator();
//    		CSCDebug.outDebugInfo("cccccc" + wt.session.SessionHelper.manager.getPrincipal().getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ReferenceFactory referencefactory = new ReferenceFactory();
		WTReference wtreference = referencefactory.getReference( oid ); 
        
		return wtreference;
	}
	
	public static Persistable getObjectByOid(String oid) throws WTException{
		try {
			if (!RemoteMethodServer.ServerFlag) {
				return (Persistable) RemoteMethodServer.getDefault().invoke("getObjectByOidRemote", RemoteUtility.class.getName(), null,
						new Class[] {String.class},
						new Object[] {oid});
			} else {
				return getObjectByOidRemote(oid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Persistable getObjectByOidRemote(String oid) throws WTException{
		Persistable persis = null;
		String user = "";
		
    	try {
    		user = wt.session.SessionHelper.manager.getPrincipal().getName();
    		wt.session.SessionHelper.manager.setAdministrator();
//    		CSCDebug.outDebugInfo("dddddd" + wt.session.SessionHelper.manager.getPrincipal().getName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		WTReference wtreference = getReferenceByOid(oid);
		persis = wtreference.getObject();
		
		return persis;
	}
	
	public static void setPartType(WTPart part, TypeIdentifier ti){
		try {
			if (!RemoteMethodServer.ServerFlag) {
				RemoteMethodServer.getDefault().invoke("setPartTypeRemote", RemoteUtility.class.getName(), null,
						new Class[] {WTPart.class, TypeIdentifier.class},
						new Object[] {part, ti});
			} else {
				setPartTypeRemote(part, ti);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setPartTypeRemote(WTPart part, TypeIdentifier ti){
	    boolean access = true;
		try {
	        access = SessionServerHelper.manager.setAccessEnforced(false);
	        TypeHelper.setType(part, ti);
	        PersistenceServerHelper.manager.update(part);
		} catch (Exception e) {
			// TODO: handle exception
		} finally{
	         SessionServerHelper.manager.setAccessEnforced(access);
		}
	}
	
	public static void setPartLifeCycle(WTPart part, LifeCycleTemplate lifeCycle){
		try {
			if (!RemoteMethodServer.ServerFlag) {
				RemoteMethodServer.getDefault().invoke("setPartLifeCycleRemote", RemoteUtility.class.getName(), null,
						new Class[] {WTPart.class, LifeCycleTemplate.class},
						new Object[] {part, lifeCycle});
			} else {
				setPartLifeCycleRemote(part, lifeCycle);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setPartLifeCycleRemote(WTPart part, LifeCycleTemplate lifeCycle){
	    boolean access = true;
		try {
	        access = SessionServerHelper.manager.setAccessEnforced(false);
	        part = (WTPart) LifeCycleHelper.setLifeCycle(part, lifeCycle);
	        PersistenceServerHelper.manager.update(part);
		} catch (Exception e) {
			// TODO: handle exception
		} finally{
	         SessionServerHelper.manager.setAccessEnforced(access);
		}
	}
	
	public static void setPartLifeCycleState(WTPart part, State state){
		try {
			if (!RemoteMethodServer.ServerFlag) {
				RemoteMethodServer.getDefault().invoke("setPartLifeCycleStateRemote", RemoteUtility.class.getName(), null,
						new Class[] {WTPart.class, State.class},
						new Object[] {part, state});
			} else {
				setPartLifeCycleStateRemote(part, state);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setPartLifeCycleStateRemote(WTPart part, State state){
	    boolean access = true;
		try {
	        access = SessionServerHelper.manager.setAccessEnforced(false);
			part = (WTPart)LifeCycleHelper.service.setLifeCycleState(part, state);
	    	PersistenceServerHelper.manager.update(part);
		} catch (Exception e) {
			// TODO: handle exception
		} finally{
	         SessionServerHelper.manager.setAccessEnforced(access);
		}
	}

	public static void setDocLifeCycleState(WTDocument doc, State state){
		try {
			if (!RemoteMethodServer.ServerFlag) {
				RemoteMethodServer.getDefault().invoke("setDocLifeCycleStateRemote", RemoteUtility.class.getName(), null,
						new Class[] {WTDocument.class, State.class},
						new Object[] {doc, state});
			} else {
				setDocLifeCycleStateRemote(doc, state);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setDocLifeCycleStateRemote(WTDocument doc, State state){
	    boolean access = true;
		try {
	        access = SessionServerHelper.manager.setAccessEnforced(false);
	        doc = (WTDocument)LifeCycleHelper.service.setLifeCycleState(doc, state);
	    	PersistenceServerHelper.manager.update(doc);
		} catch (Exception e) {
			// TODO: handle exception
		} finally{
	         SessionServerHelper.manager.setAccessEnforced(access);
		}
	}
	
	public static void setEPMLifeCycleState(EPMDocument epm, State state){
		try {
			if (!RemoteMethodServer.ServerFlag) {
				RemoteMethodServer.getDefault().invoke("setEPMLifeCycleStateRemote", RemoteUtility.class.getName(), null,
						new Class[] {EPMDocument.class, State.class},
						new Object[] {epm, state});
			} else {
				setEPMLifeCycleStateRemote(epm, state);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setEPMLifeCycleStateRemote(EPMDocument epm, State state){
	    boolean access = true;
		try {
	        access = SessionServerHelper.manager.setAccessEnforced(false);
	        epm = (EPMDocument)LifeCycleHelper.service.setLifeCycleState(epm, state);
	    	PersistenceServerHelper.manager.update(epm);
		} catch (Exception e) {
			// TODO: handle exception
		} finally{
	         SessionServerHelper.manager.setAccessEnforced(access);
		}
	}
	
	public static void setBaselineLifeCycleState(ManagedBaseline baseline, State state){
		try {
			if (!RemoteMethodServer.ServerFlag) {
				RemoteMethodServer.getDefault().invoke("setBaselineLifeCycleStateRemote", RemoteUtility.class.getName(), null,
						new Class[] {ManagedBaseline.class, State.class},
						new Object[] {baseline, state});
			} else {
				setBaselineLifeCycleStateRemote(baseline, state);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setBaselineLifeCycleStateRemote(ManagedBaseline baseline, State state){
	    boolean access = true;
		try {
	        access = SessionServerHelper.manager.setAccessEnforced(false);
	        baseline = (ManagedBaseline)LifeCycleHelper.service.setLifeCycleState(baseline, state);
	    	PersistenceServerHelper.manager.update(baseline);
		} catch (Exception e) {
			// TODO: handle exception
		} finally{
	         SessionServerHelper.manager.setAccessEnforced(access);
		}
	}
	
	public static List<WTGroup> getUserGroups(WTUser user){
		try {
			if (!RemoteMethodServer.ServerFlag) {
				return (List<WTGroup>) RemoteMethodServer.getDefault().invoke("getUserGroupsRemote", RemoteUtility.class.getName(), null,
						new Class[] {WTUser.class},
						new Object[] {user});
			} else {
				return getUserGroupsRemote(user);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
    public static List<WTGroup> getUserGroupsRemote(WTUser user) throws WTException{
        List<WTGroup> userGroupList = new ArrayList<WTGroup>();
        boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
        try{
                 Enumeration em = OrganizationServicesHelper.manager.parentGroups(user);
                 while(em.hasMoreElements()){
                          WTGroup group = (WTGroup)((WTPrincipalReference)em.nextElement()).getObject();
                          userGroupList.add(group);
                 }
        }catch(WTException e){
                 e.printStackTrace();
        }finally{
                 wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
        }
        return userGroupList;
    }
    
    public static ArrayList getAllPrincipalsByRole(Role role, ContainerTeam containerTeam){
		try {
			if (!RemoteMethodServer.ServerFlag) {
				return (ArrayList) RemoteMethodServer.getDefault().invoke("getAllPrincipalsByRoleRemote", RemoteUtility.class.getName(), null,
						new Class[] {Role.class, ContainerTeam.class},
						new Object[] {role, containerTeam});
			} else {
				return getAllPrincipalsByRoleRemote(role, containerTeam);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
    }
    
    public static ArrayList getAllPrincipalsByRoleRemote(Role role, ContainerTeam containerTeam){
    	ArrayList result = new ArrayList();
        boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
        try {
        	result = WCUtil.getAllPrincipalsByRole(role, containerTeam);
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
                 wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
        }
		return result;
    }
    
    public static void startWorkFlow(String workFlowName, Object pbo) {
		try {
			if (!RemoteMethodServer.ServerFlag) {
				RemoteMethodServer.getDefault().invoke("startWorkFlowRemote", RemoteUtility.class.getName(), null,
						new Class[] {String.class, Object.class},
						new Object[] {workFlowName, pbo});
			} else {
				startWorkFlowRemote(workFlowName, pbo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public static void startWorkFlowRemote(String workFlowName, Object pbo) {
		WTContainerRef containerRef = null;
		long WORKFLOW_PRIORITY = 1;
		
        boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
        try {
			containerRef = WTContainerHelper.service.getExchangeRef ();
			
			if(pbo instanceof WTContained){
				WTContained contained = (WTContained)pbo;
				containerRef = contained.getContainerReference();
			}
			
			WTProperties wtproperties = WTProperties.getLocalProperties();
			WORKFLOW_PRIORITY = Long.parseLong(wtproperties.getProperty(
					"wt.lifecycle.defaultWfProcessPriority", "1"));

			WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service
					.getProcessDefinition(workFlowName);
			if (wfprocessdefinition == null) {
//				CSCDebug.outDebugInfo("Exit initiateWorkflow: " + workFlowName
//						+ "is null");
				return;
			}
			
			WfProcess wfprocess = null;
			wfprocess = WfEngineHelper.service.createProcess(wfprocessdefinition,  null, containerRef);

			// wfprocess.setTeamTemplateId(lifecyclemanaged.getTeamTemplateId());

			
			ProcessData processdata = wfprocess.getContext();
			processdata.setValue("primaryBusinessObject", pbo);
			
			WfEngineHelper.service.startProcessImmediate(wfprocess,
					processdata, WORKFLOW_PRIORITY);
        }catch (Exception e) {
			// TODO Auto-generated catch block
//			CSCDebug.outDebugInfo("Error in reading wt.lifecycle.defaultWfProcessPriority");
			e.printStackTrace();
		}finally{
            wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
        }
	}
    
    
    public static QueryResult getProduct(String prdName) {
		try {
			if (!RemoteMethodServer.ServerFlag) {
				return (QueryResult)RemoteMethodServer.getDefault().invoke("getProductRemote", RemoteUtility.class.getName(), null,
						new Class[] {String.class},
						new Object[] {prdName});
			} else {
				return getProductRemote(prdName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
    }
    
    public static QueryResult getProductRemote(String prdName){
//    	CSCDebug.outDebugInfo("PRODUCT TEST CONDITION = " + prdName);
 
    	QueryResult result = null;
        boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
        try{
			PDMLinkProduct product = null;
			QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
			SearchCondition sc = new
					SearchCondition(PDMLinkProduct.class, PDMLinkProduct.NAME, SearchCondition.LIKE,prdName + "%",false);
			qs.appendSearchCondition(sc);
			
	        ClassAttribute clsAttr = new ClassAttribute(PDMLinkProduct.class,PDMLinkProduct.MODIFY_TIMESTAMP);
	        OrderBy order = new OrderBy((OrderByExpression)clsAttr,true);
	        qs.appendOrderBy(order);
			
			QueryResult qr = PersistenceHelper.manager.find(qs);
			result = qr;
        }catch (Exception e) {
			// TODO Auto-generated catch block
//			CSCDebug.outDebugInfo("Error in reading wt.lifecycle.defaultWfProcessPriority");
			e.printStackTrace();
        }finally{
            wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
        }
        
        return result;
    }
    
    public static void setElectronicSignatureToObjectRelateObjects(WTUser user, WTObject wtobject, String comments, String instructions, String role, String vote){
		try {
			if (!RemoteMethodServer.ServerFlag) {
				RemoteMethodServer.getDefault().invoke("setElectronicSignatureToObjectRelateObjects", RemoteUtility.class.getName(), null,
						new Class[] {WTUser.class, WTObject.class, String.class, String.class, String.class, String.class},
						new Object[] {user, wtobject, comments, instructions, role, vote});
			} else {
				setElectronicSignatureToObjectRelateObjectsRemote(user, wtobject, comments, instructions, role, vote);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public static void setElectronicSignatureToObjectRelateObjectsRemote(WTUser user, WTObject wtobject, String comments, String instructions, String role, String vote){
        boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
        try {
        	ElectronicSignatureHelper.setElectronicSignatureToObjectRelateObjects(user, wtobject, comments, instructions, role, vote);
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
        }
    }
}
