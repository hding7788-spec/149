package ext.casc.folder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.glaway.mpm.util.MPMProcessPlanUtil;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;

import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.iba.value.IBAHolder;
import wt.inf.container.WTContained;
import wt.inf.library.WTLibrary;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.log4j.LogR;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.pom.Transaction;
import wt.project.Role;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.team.TeamManaged;
import wt.team.TeamReference;
import wt.util.WTContext;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.vc.Iterated;
import wt.vc.IterationInfo;
import wt.vc.VersionControlException;
import wt.vc.VersionControlHelper;
import ext.casc.constants.Constants;
import ext.casc.util.CSCIBA;
import ext.casc.util.CSCPrincipal;
import ext.casc.util.CSCUtil;
import ext.casc.util.CommonUtil;
import ext.casc.util.DBConn;

public class AuthoriserPermissionUtil implements RemoteAccess,java.io.Serializable{

	private static final Logger log;

	private static String wthome;
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	private static final String SITE_DOMAIN_PRO = "wt.inf.container.SiteOrganization.internetDomain";

	public enum SECRETTYPE {
		NULL("公开"),NEIBU("内部"), MIMI("秘密★10年"), JIMI("机密★20年");
		private String name;

		SECRETTYPE() {
		}

		SECRETTYPE(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public static SECRETTYPE getSecret(String name){
			if(name.equals(NEIBU.getName())){
				return NEIBU;
			}else if(name.equals(MIMI.getName())){
				return MIMI;
			}else if(name.equals(JIMI.getName())){
				return JIMI;
			}else{
				return NULL;
			}
	}
	}
	private static HashMap<String,SECRETTYPE> secretMapping = new HashMap<String,SECRETTYPE>();

	static {
        try {
            wthome = (String) (WTProperties.getLocalProperties()).getProperty("wt.home", "");
            secretMapping.put("内部组",SECRETTYPE.NEIBU);
            secretMapping.put("秘密组",SECRETTYPE.MIMI);
            secretMapping.put("机密组",SECRETTYPE.JIMI);
            log = LogR.getLogger(AuthoriserPermissionUtil.class.getName());
        } catch (IOException e) {
        	throw new ExceptionInInitializerError(e);
        }
    }

	public static Set<String> getAllSelectedTeamMembers(Persistable p,
			List<Persistable> ps) throws WTException {
		Set<String> users = new HashSet<String>();
		Team team;
		StringBuffer userSB = null;
		String highSecret = getHighestSecret(ps);
		ContainerTeam conTeam = null;
		if (p instanceof PDMLinkProduct) {
			PDMLinkProduct product = (PDMLinkProduct) p;
			 conTeam = ContainerTeamHelper.service
					.getContainerTeam(product);

		}else if (p instanceof WTLibrary) {
			WTLibrary lib = (WTLibrary) p;
			conTeam = ContainerTeamHelper.service
					.getContainerTeam(lib);

		}
		if(conTeam==null){
			return users;
		}
		Vector vc = conTeam.getMembers();
		for (int i = 0; i < vc.size(); i++) {
			userSB = new StringBuffer();
			WTPrincipalReference principalRef = (WTPrincipalReference) vc
					.get(i);
			Persistable persistable = principalRef.getObject();
			if (persistable instanceof WTUser) {
				WTUser user = (WTUser) persistable;
				String name = user.getName();
				String fullName = user.getFullName();
				String orgName = user.getOrganizationName();
				userSB.append(name).append("(").append(fullName)
						.append(":").append(orgName).append(")");
				log.debug("----------highSecret----------------------->"+highSecret);
				if(highSecret==null||"".equals(highSecret)){
					users.add(userSB.toString());
				}else{
					Enumeration em = OrganizationServicesHelper.manager.parentGroups(CSCPrincipal.getUserByName(name));
					log.debug("----------------group begin------------------");
					while(em.hasMoreElements()){
						WTGroup group = (WTGroup)((WTPrincipalReference)em.nextElement()).getObject();
						String groupName = group.getName();
						log.debug("----------group name----------------------->"+groupName);
						SECRETTYPE secrect  = secretMapping.get(groupName);
						if(secrect!=null && secrect.ordinal()>=SECRETTYPE.getSecret(highSecret).ordinal()){
							users.add(userSB.toString());
						}else{
							continue;
						}
					}
					log.debug("----------------group end------------------");
				}
			}else if (persistable instanceof WTGroup) {
				WTGroup group = (WTGroup) persistable;
				users = getUserFromWTGroup(group,users,ps);
			}
		}
		return users;
	}

	 public static Set getUserFromWTGroup(WTGroup g, Set users,List<Persistable> ps) throws WTException {

	        if (g == null || users == null) {

	            return users;

	        }

	        Enumeration member = g.members();
	        String highSecret = getHighestSecret(ps);
	        while (member.hasMoreElements()) {
	        	StringBuffer userSB = new StringBuffer();
	            WTPrincipal principal = (WTPrincipal) member.nextElement();

	            if (principal instanceof WTUser) {

	            	WTUser user = (WTUser) principal;
					String name = user.getName();
					String fullName = user.getFullName();
					String orgName = user.getOrganizationName();
					userSB.append(name).append("(").append(fullName)
							.append(":").append(orgName).append(")");
					log.debug("----------highSecret----------------------->"+highSecret);
					if(highSecret==null||"".equals(highSecret)){
						users.add(userSB.toString());
					}else{
						Enumeration em = OrganizationServicesHelper.manager.parentGroups(CSCPrincipal.getUserByName(name));
						log.debug("----------------group begin------------------");
						while(em.hasMoreElements()){
							WTGroup group = (WTGroup)((WTPrincipalReference)em.nextElement()).getObject();
							String groupName = group.getName();
							log.debug("----------group name----------------------->"+groupName);
							SECRETTYPE secrect  = secretMapping.get(groupName);
							if(secrect!=null && secrect.ordinal()>=SECRETTYPE.getSecret(highSecret).ordinal()){
								users.add(userSB.toString());
							}else{
								continue;
							}
						}
						log.debug("----------------group end------------------");
					}

	            } else if (principal instanceof WTGroup) {

	                getUserFromWTGroup((WTGroup) principal, users,ps);

	            }

	        }

	        return users;

	    }
	public static List<Persistable> getALLSelectedObjs(List oids)throws WTException {
		List<Persistable> objs = new ArrayList<Persistable>();
		ReferenceFactory rf = new ReferenceFactory();
		for (Object oid : oids) {
			//oid = oid.substring(oid.lastIndexOf("$") + 1, oid.length() - 2);
			Persistable p = rf.getReference(oid.toString()).getObject();
			if(p instanceof Folder){
				Folder folder = (Folder)p;
				objs = getFromFolder(folder, (ArrayList)objs);
			}else{
				if(!objs.contains(p)){
					objs.add(p);
				}
			}
		}
		return objs;
	}

	public static String writeExcel(String authoriseName, String receiveName,
			String authoriseComment, List<Persistable> objs) {
		String fileName = authoriseName+System.currentTimeMillis();
		String toPath = wthome+File.separator+"temp"+File.separator+fileName;
		File toFile = new File(toPath);
		if(!toFile.exists()){
			toFile.mkdirs();
		}
		toPath = toFile.getPath()+File.separator+"授权单.xls";
		String fromPath = wthome+File.separator+"codebase"+File.separator+"ext"+File.separator+"casc"+File.separator+"folder"+File.separator+"authorise.xls";
		ReportExcelGenerateUtil rgu = new ReportExcelGenerateUtil(fromPath);
		rgu.setSheet(0);
		HSSFWorkbook workbook=rgu.getWorkbook();
		rgu.setCellWithStyle(1, 1, authoriseName);
		rgu.setCellWithStyle(2, 1, receiveName);
		String authoriseDate = sdf.format(new Date());
		rgu.setCellWithStyle(2, 1, receiveName);
		rgu.setCellWithStyle(3, 1, authoriseDate);
		rgu.setCellWithStyle(4, 1, authoriseComment);
		int rowNum = 6;
		String name = null;
		String number = null;
		String version = null;
		for (Persistable p : objs) {
			if (p instanceof WTPart) {
				WTPart part = (WTPart) p;
				name = part.getName();
				number = part.getNumber();
				version = part.getVersionIdentifier().getValue() + "."
						+ part.getIterationIdentifier().getValue();
			} else if (p instanceof WTDocument) {
				WTDocument doc = (WTDocument) p;
				name = doc.getName();
				number = doc.getNumber();
				version = doc.getVersionIdentifier().getValue() + "."
						+ doc.getIterationIdentifier().getValue();
			} else if (p instanceof EPMDocument) {
				EPMDocument epm = (EPMDocument) p;
				name = epm.getName();
				number = epm.getNumber();
				version = epm.getVersionIdentifier().getValue() + "."
						+ epm.getIterationIdentifier().getValue();
			}else if (p instanceof MPMProcessPlan) {
				MPMProcessPlan plan = (MPMProcessPlan) p;
				name = plan.getName();
				number = plan.getNumber();
				version = plan.getVersionIdentifier().getValue() + "."
						+ plan.getIterationIdentifier().getValue();
			}
			rgu.setCellWithStyle(rowNum, 0, name);
			rgu.setCellWithStyle(rowNum, 1, number);
			rgu.setCellWithStyle(rowNum, 2, version);
			rowNum++;
		}

		try {
			FileOutputStream fos= new FileOutputStream(toPath);
			workbook.write(fos);
		    fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return toPath;
	}

	public static RevisionControlled setCreatorAndModifier(
			RevisionControlled newVer, WTPrincipalReference principalRef)
			throws WTException {
		try {
			//newVer = (RevisionControlled) VersionControlHelper
					//.assignIterationCreator(newVer, principalRef);
			VersionControlHelper.setIterationModifier(newVer, principalRef);
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
		return newVer;
	}

	public static String getHighestSecret(List<Persistable> objs) throws WTException {

		String highestSecrect = null;
		for (Persistable p : objs) {
			CSCIBA cscIba = new CSCIBA((IBAHolder) p);
			String secrect = cscIba.getIBAValue("SECRET");
			if (secrect == null || "".equals(secrect)
					|| SECRETTYPE.NULL.getName().equals(secrect)) {
				continue;
			} else {
				if (highestSecrect == null
						|| SECRETTYPE.getSecret(highestSecrect).ordinal() < SECRETTYPE
								.getSecret(secrect).ordinal()) {
					highestSecrect = secrect;
				}
			}
		}
		return highestSecrect;

	}

	public static String getErrorSelectedObjects(List<Persistable> objs, String designerName) throws WTException{
		String modifyName =null;
		String objState=null;
		StringBuffer errorMsg = new StringBuffer("");
		boolean isManagerFlag = CommonUtil.isSiteOrOrgAdmin();
		for (Persistable p : objs) {
			if(p instanceof Iterated){
				Iterated it = (Iterated)p;
				WTPrincipalReference principalRef = VersionControlHelper.getIterationModifier(it);
				modifyName = principalRef.getFullName();
				if(!isManagerFlag){
					if (modifyName != null
							&& !designerName.equalsIgnoreCase(modifyName)) {
						errorMsg.append(getSelectedObjectNumber(p)+",");
					}
				}
			}
			/*取消状态限制
			if (p instanceof LifeCycleManaged) {
				LifeCycleManaged lfObject = (LifeCycleManaged) p;
				objState = lfObject.getState().toString();
				if(!WorkflowConstants.ASES_STATE_INWORK.equals(objState)){
					errorMsg.append(getSelectedObjectNumber(p)+",");
				}
			}
			*/
		}
		if("".equals(errorMsg.toString())){
			return errorMsg.toString();
		}else{
			return errorMsg.toString().substring(0,errorMsg.length()-1);
		}


	}

	public static String getSelectedObjectNumber(Persistable p){
		String number="";
		if(p instanceof WTDocument){
			WTDocument doc = (WTDocument)p;
			number =doc.getNumber();
		}else if(p instanceof EPMDocument){
			EPMDocument epm =(EPMDocument)p;
			number = epm.getNumber();
		}if(p instanceof WTPart){
			WTPart part =(WTPart)p;
			number = part.getNumber();
		}
		return number;
	}

	public static boolean isSameModifyUserAndNotApprovedState(List<Persistable> objs, String designerName)
			throws WTException {
		boolean isSameUserFlag = true;
		//String objState="";
		String modifyName = null;
		for (Persistable p : objs) {
			/*if (p instanceof WTPart) {
				WTPart part = (WTPart) p;
				modifyName = part.getModifierName();
			} else if (p instanceof WTDocument) {
				WTDocument doc = (WTDocument) p;
				modifyName = doc.getModifierName();
			} else if (p instanceof EPMDocument) {
				EPMDocument epm = (EPMDocument) p;
				modifyName = epm.getModifierName();
			}*/
			if(p instanceof Iterated){
				Iterated it = (Iterated)p;
				WTPrincipalReference principalRef = VersionControlHelper.getIterationModifier(it);
				modifyName = principalRef.getFullName();
			}

			if (modifyName != null
					&& !designerName.equalsIgnoreCase(modifyName)) {
				isSameUserFlag = false;
				break;
			}

			/*if (p instanceof LifeCycleManaged) {
				LifeCycleManaged lfObject = (LifeCycleManaged) p;
				objState = lfObject.getState().toString();
				if(!Constants.STATE_INWORK.equals(objState)){
					isNotApprovedFlag = false;
					break;
				}
			}*/
		}
		return isSameUserFlag;
	}
	public static String getMaxDocNumber() throws Exception {
		String maxDocNumber = null;
		QuerySpec qs = new QuerySpec(WTDocument.class);
		qs.appendOrderBy(new OrderBy(new ClassAttribute(WTDocument.class,
				WTDocument.NUMBER), true), new int[] { 0 });
		QueryResult qr  =PersistenceHelper.manager.find((StatementSpec)qs);
		while(qr.hasMoreElements()){
			WTDocument doc = (WTDocument)qr.nextElement();
			maxDocNumber = doc.getNumber();
			break;
		}
		return maxDocNumber;
	}


	public static void assignUserToRole(String name, WTDocument doc,
			String whichRole) throws WTException {
		boolean access = SessionServerHelper.manager.setAccessEnforced(false);
		Role ccb = Role.toRole(whichRole);
		Team caTeam = null;
		TeamReference teamRef = doc.getTeamId();
		caTeam = (Team) teamRef.getObject();
		WTUser user = CSCPrincipal.getUserByName(name);
		caTeam.addPrincipal(ccb, user);
		caTeam = (Team) PersistenceHelper.manager.refresh(caTeam);
		SessionServerHelper.manager.setAccessEnforced(access);
	}

	public static void assignUserToRole(WTUser user, WTDocument doc,
			String whichRole) throws WTException {
		boolean access = SessionServerHelper.manager.setAccessEnforced(false);
		Role ccb = Role.toRole(whichRole);
		Team caTeam = null;
		TeamReference teamRef = doc.getTeamId();
		caTeam = (Team) teamRef.getObject();
		//WTUser user = CSCPrincipal.getUserByName(name);
		caTeam.addPrincipal(ccb, user);
		caTeam = (Team) PersistenceHelper.manager.refresh(caTeam);
		SessionServerHelper.manager.setAccessEnforced(access);
	}

	public static String getDomainValue(){
		String domainOpp = new String();
		try {
			WTProperties wtp = WTProperties.getLocalProperties();
			String SITE_ORG_FILE = wtp.getProperty(
					"wt.inf.container.SiteOrganization.file",
					"wt/inf/container/SiteOrganization.properties");
			InputStream istream = null;
			try {
				istream = WTContext.getContext().getResourceAsStream(
						SITE_ORG_FILE);
				wtp = new WTProperties(/* no defaults */null);
				if (istream != null) {
					wtp.load(istream);
				}
				String SITE_DOMAIN_VALUE;
				SITE_DOMAIN_VALUE = wtp.getProperty(SITE_DOMAIN_PRO);
				String[] domainSplit = SITE_DOMAIN_VALUE.split("[.]");
				domainOpp = domainSplit[0];
				for (int i = 1; i < domainSplit.length; i++)
					domainOpp = domainSplit[i] + "." + domainOpp;
			} finally {
				if (istream != null) {
					istream.close();
				}
			}
		} catch (Throwable t) {
			throw new ExceptionInInitializerError(t);
		}
		return domainOpp;
	}

	public static void setModifier(Iterated obj, WTPrincipalReference uref)
			throws  Exception{
		DBConn conn = null;
		try {
			conn = new DBConn();
			long userid = PersistenceHelper.getObjectIdentifier(uref.getObject()).getId();
			long objid = PersistenceHelper.getObjectIdentifier(obj).getId();
			String sql = "";
			if(obj instanceof WTPart){
				sql = "update WTPart set idA3B2iterationInfo="+userid+" where ida2a2="+objid;
			}else if(obj instanceof WTDocument){
				sql = "update WTDocument set idA3B2iterationInfo="+userid+" where ida2a2="+objid;
			}else if(obj instanceof EPMDocument){
				sql = "update EPMDocument set idA3B2iterationInfo="+userid+" where ida2a2="+objid;
			}else if(obj instanceof MPMProcessPlan){
				sql = "update MPMProcessPlan set idA3B2iterationInfo="+userid+" where ida2a2="+objid;
			}
			conn.executeUpdate(sql);
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static void setAllObjModifier(List<Persistable> objs,WTPrincipalReference uref, boolean isManagerFlag, String authoriseName){
		if(!RemoteMethodServer.ServerFlag){
			try {
				RemoteMethodServer.getDefault().invoke("setAllObjModifier", AuthoriserPermissionUtil.class.getName(), null, new Class[] { List.class, WTPrincipalReference.class},
						new Object[] { objs, uref});
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			Transaction trx = null;
			try{
				trx = new Transaction();
				for(Persistable p: objs){
					if(p instanceof Iterated){
						Iterated it = (Iterated)p;

						WTPrincipalReference principalRef = VersionControlHelper.getIterationModifier(it);
						String modifyName = principalRef.getFullName();
						if (!isManagerFlag&&modifyName != null&& !authoriseName.equalsIgnoreCase(modifyName)) {
							 continue;
						}

						//设置修改者
						setModifier(it, uref);
						//设置修改者到提交者团队
			        	assignToSubmitter(it, (WTUser)uref.getPrincipal());
					}
				}
				trx.commit();
				trx=null;
			}catch (Exception e) {
				e.printStackTrace();
			}finally
			{
				if(trx!=null)
					trx.rollback();
			}
		}
	}


	public static void assignToSubmitter(Iterated obj, WTUser user) throws WTException
    {
    	if (obj instanceof TeamManaged)
		{
			if(((TeamManaged)obj).getTeamId() != null){

				Team team = (Team)((TeamManaged)obj).getTeamId().getObject();
				Role this_role  = Role.SUBMITTER;

				if (user != null && this_role != null)    {
					team.addPrincipal(this_role, user);
				}

				LifeCycleHelper.service.augmentRoles(team);
			}
		}
    }

	private static ArrayList getFromFolder(Folder folder,ArrayList list) throws WTException{
		QueryResult qr = FolderHelper.service.findFolderContents(folder);
		while(qr.hasMoreElements()){
			WTObject wtObj = (WTObject) qr.nextElement();
			if(wtObj instanceof Folder){
				Folder f =(Folder)wtObj;
				list =getFromFolder(f,list);
			}else{
				if(wtObj instanceof WTDocument || wtObj instanceof WTPart || wtObj instanceof
						EPMDocument){
					if(wtObj instanceof Iterated){
						Iterated it  = (Iterated)wtObj;
						Object obj = VersionControlHelper.service.allVersionsOf(it.getMaster()).nextElement();
						if(wtObj.equals(obj)&&!list.contains(wtObj)){
							//System.out.println(TypeIdentifierUtilityHelper.service.getTypeIdentifier(wtObj).getTypename());
							list.add(wtObj);
						}
					}
				}
			}
		}
		return list;
	}



	public static void main(String[] args) throws WTException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		System.out.println(getDomainValue());
		WTDocument doc  = (WTDocument) CSCUtil.getObjectByOid("VR:wt.doc.WTDocument:158438");
		Transaction trx = null;
		WTPrincipalReference oldprincipal= VersionControlHelper.getIterationModifier(doc);
		try{
			trx = new Transaction();
			WTPrincipalReference principalRef = SessionHelper.manager.getPrincipalReference();
			setModifier(doc,principalRef);
			trx.commit();
			trx=null;
		}catch (Exception e) {
			// TODO: handle exception
		}

	}

	public static String checkObjests(List<Persistable> objs,String currentUserName,boolean isAdmin) throws VersionControlException, WTException {
		String modifyName = null;
		String containerName = null;
		for (Persistable p : objs) {
			if(p instanceof EPMDocument||p instanceof WTPart||p instanceof WTDocument||p instanceof MPMProcessPlan){
				WTContained cp = (WTContained)p;
				if(containerName==null){
					containerName = cp.getContainerName();
				}else{
					if(!cp.getContainerName().equals(containerName)){
						return "只能选择同一产品库的数据进行快速授权。";
					}
				}

				if(p instanceof Iterated){
					Iterated it = (Iterated)p;
					WTPrincipalReference principalRef = VersionControlHelper.getIterationModifier(it);
					modifyName = principalRef.getFullName();
				}

				if (!isAdmin&&modifyName != null&& !currentUserName.equalsIgnoreCase(modifyName)) {
					//return "只能选择修改者为自己的数据进行快速授权。";
				}

			}else{
				return "只支持模型、文档、部件、工艺规程类型数据的动态授权，请取消其他类型数据再试！";
			}

		}
		return null;
	}

	public static List<Persistable> processSelectedObjs(List<Persistable> tempobjs) throws WTException {
		Set objs = new HashSet<Persistable>();
		for (Persistable p : tempobjs) {
			objs.add(p);
			if(p instanceof WTDocument){
				WTDocument doc = (WTDocument)p;
				MPMProcessPlan plan = MPMProcessPlanUtil.getProcessPlanByWTDocument(doc);
				if(plan!=null){
					objs.add(plan);
				}
			}
			if(p instanceof MPMProcessPlan){
				MPMProcessPlan plan = (MPMProcessPlan)p;
				WTDocument doc = MPMProcessPlanUtil.getWTDocumentByProcessPlan(plan);
				if(doc!=null){
					objs.add(doc);
				}
			}
		}
		return new ArrayList<Persistable>(objs);
	}

	/*public List<WTUser> getDesigers(String oid, String roleName)
			throws WTException {
		List<WTUser> desigerUsers = new ArrayList<WTUser>();
		ReferenceFactory rf = new ReferenceFactory();
		Persistable p = rf.getReference(oid).getObject();
		Team team = null;
		if (p instanceof WTPart) {
			WTPart part = (WTPart) p;
			team = (Team) part.getTeamId().getObject();
		} else if (p instanceof WTDocument) {
			WTDocument doc = (WTDocument) p;
			team = (Team) doc.getTeamId().getObject();
		} else if (p instanceof EPMDocument) {
			EPMDocument epm = (EPMDocument) p;
			team = (Team) epm.getTeamId().getObject();
		}
		Vector vc = team.getRoles();
		Role role = Role.toRole(roleName);
		if (team != null) {
			for (Enumeration enumeration = team.getPrincipalTarget(role); enumeration
					.hasMoreElements();) {
				WTPrincipalReference principalRef = (WTPrincipalReference) enumeration
						.nextElement();
				WTUser user = (WTUser) principalRef.getObject();
				desigerUsers.add(user);
			}
		}
		return desigerUsers;
	}

	public boolean isDesignerData(String[] oids, String roleName,
			WTUser currentUser) throws WTException {
		boolean isDesignerData = true;
		for (String oid : oids) {
			List<WTUser> designerUsers = getDesigers(oid, roleName);
			if (designerUsers != null && designerUsers.size() != 0
					&& !designerUsers.contains(currentUser)) {
				isDesignerData = false;
			}
		}
		return isDesignerData;
	}*/

}
