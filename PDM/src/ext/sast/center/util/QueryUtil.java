package ext.sast.center.util;

import com.bjsasc.avidm.mq.message.Based;
import com.bjsasc.avidm.mq.util.TUUID;
import com.ptc.core.meta.common.impl.TypeIdentifierUtilityHelper;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.envelope.ProcessEnvelope;
import ext.sast.center.bean.GroupBean;
import ext.sast.center.bean.SychnUserGroupBean;
import ext.sast.center.bean.UserBean;
import ext.sast.center.bean.UserGroupLinkBean;
import ext.sast.center.bean.message.GroupMessageBean;
import ext.sast.center.bean.message.UserGroupLinkMessageBean;
import ext.sast.center.bean.message.UserMessageBean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import wt.admin.AdministrativeDomainHelper;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.httpgw.WTContextBean;
import wt.inf.container.OrgContainer;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerHelper;
import wt.ixb.publicforhandlers.IxbHndHelper;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.*;
import wt.part.WTPart;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionAuthenticator;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTProperties;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

/**
 * @ Author     ：LB.
 * @ Date       ：Created in 2019/3/13
 * @ Description：
 * @ Modified By：
 */
public class QueryUtil implements RemoteAccess{
	private static String context = "Unknown";

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		rms.setUserName("wcadmin");
		rms.setPassword("wcadmin");
		try {
			Class[] types = { };
			Object[] vls  = { };
			rms.invoke("getAllUserAndGroup", QueryUtil.class.getName(), null, types, vls);
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    /**
     * 获取所有用户
     * @return
     * @throws WTException
     */
	public static List<UserBean> getAllUser() throws WTException {
		MethodContext mc = null;
		boolean enforce = false;
		List<UserBean> userBeanList = null;
		try {
			mc = MethodContext.getContext(Thread.currentThread());
			if (mc == null)
				mc = new MethodContext(null, null);
			if (mc.getAuthentication() == null) {
				SessionAuthenticator sa = new SessionAuthenticator();
				mc.setAuthentication(sa.setUserName(AdministrativeDomainHelper.ADMINISTRATOR_NAME));
			}
			context = mc.getId().toString();
			System.out.println("**********设置上下文**********" + context);
			if(!RemoteMethodServer.ServerFlag){
				String method = "getAllUser";
				RemoteMethodServer rs = null;
				try {
					rs = RemoteMethodServer.getDefault();
				} catch (Exception e) {
					WTContextBean bean = new WTContextBean();
					rs = RemoteMethodServer.getDefault();
				}
				return (List<UserBean>)rs.invoke(method, QueryUtil.class.getName(),null, new Class[]{},new Object[]{});
			}else{
				enforce = SessionServerHelper.manager.setAccessEnforced(false);
				userBeanList = new ArrayList<UserBean>();
				UserBean userBean;
				WTUser wtUser;
				QuerySpec querySpec = new QuerySpec(WTUser.class);
				@SuppressWarnings("deprecation")
				QueryResult queryResult = PersistenceHelper.manager.find(querySpec);
				while (queryResult.hasMoreElements()) {
					Object object = queryResult.nextElement();
					if (object instanceof WTUser) {
						wtUser = (WTUser) object;
						userBean = new UserBean();
						userBean.setIid(String.valueOf(wtUser.getPersistInfo().getObjectIdentifier().getId()));
						userBean.setId(wtUser.getName());
						userBean.setName(wtUser.getFullName());
						//setSeclevel
						userBean.setSeclevel("");
						userBean.setTelephone(wtUser.getTelephoneNumber());
						userBean.setEmail(wtUser.getEMail());
						if (!userBeanList.contains(userBean)) {
							userBeanList.add(userBean);
						}
					}
				}

			}
		} catch (Throwable t) {
			t.printStackTrace();
			System.err.println("Error create service session context.");
		} finally {
			wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
			if (mc != null) {
				mc.unregister();
			}
		}
        return userBeanList;
    }

    /**
     * @description 获取所有组
     * @return
     * @throws WTException
     */
    public static List<GroupBean> getAllGroup() throws WTException {
		MethodContext mc = null;
		boolean enforce = false;
		List<GroupBean> groupBeanList = null;
		try {
			mc = MethodContext.getContext(Thread.currentThread());
			if (mc == null)
				mc = new MethodContext(null, null);
			if (mc.getAuthentication() == null) {
				SessionAuthenticator sa = new SessionAuthenticator();
				mc.setAuthentication(sa.setUserName(AdministrativeDomainHelper.ADMINISTRATOR_NAME));
			}
			context = mc.getId().toString();
			System.out.println("**********设置上下文**********" + context);
			if(!RemoteMethodServer.ServerFlag){
				String method = "getAllGroup";
				RemoteMethodServer rs = null;
				try {
					rs = RemoteMethodServer.getDefault();
				} catch (Exception e) {
					WTContextBean bean = new WTContextBean();
					rs = RemoteMethodServer.getDefault();
				}
				return (List<GroupBean>)rs.invoke(method, QueryUtil.class.getName(),null, new Class[]{},new Object[]{});
			}else{
				groupBeanList = new ArrayList<GroupBean>();
				enforce = SessionServerHelper.manager.setAccessEnforced(false);
				GroupBean groupBean;
				WTPrincipal admin = SessionHelper.manager.getAdministrator();
				WTOrganization wtOrganization = OrganizationServicesHelper.manager.getOrganization(admin);
				OrgContainer orgContainer = WTContainerHelper.service.getOrgContainer(wtOrganization);
				List<?> list = OrgUtil.getNodes(orgContainer);
				for (Object object : list) {
					if (object instanceof WTGroup) {
						WTGroup group = (WTGroup) object;
						groupBean = new GroupBean();
						groupBean.setIid(String.valueOf(group.getPersistInfo().getObjectIdentifier().getId()));
						groupBean.setId(group.getName());
						groupBean.setName(group.getName());
						//setParent_iid
						groupBean.setParent_iid(wtOrganization.getName());
						if (!groupBeanList.contains(groupBean)) {
							groupBeanList.add(groupBean);
						}
					}
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
			System.err.println("Error create service session context.");
		} finally {
			wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
			if (mc != null) {
				mc.unregister();
			}
		}
		return groupBeanList;
	}
    @SuppressWarnings("deprecation")
	private static OrgContainer searchOrgContainer(String orgName) {
        OrgContainer org = null;
		MethodContext mc = null;
        try {
			mc = MethodContext.getContext(Thread.currentThread());
			if (mc == null)
				mc = new MethodContext(null, null);
			if (mc.getAuthentication() == null) {
				SessionAuthenticator sa = new SessionAuthenticator();
				mc.setAuthentication(sa.setUserName(AdministrativeDomainHelper.ADMINISTRATOR_NAME));
			}
			context = mc.getId().toString();
			System.out.println("**********设置上下文**********" + context);
            QuerySpec qs = new QuerySpec(OrgContainer.class);
            qs.appendWhere(new SearchCondition(OrgContainer.class, OrgContainer.NAME, SearchCondition.EQUAL, orgName), new int[] { 0 });
            QueryResult qr = PersistenceHelper.manager.find(qs);
            if (qr.hasMoreElements()) {
                org = (OrgContainer) qr.nextElement();
            }
        } catch (WTException e) {
            e.printStackTrace();
        }
        return org;
    }

    public static SychnUserGroupBean getAllUserAndGroup() throws WTException {
		System.out.println("**********start*******");
		SychnUserGroupBean sychnUserGroupBean = null;
		boolean enforce = false;
		MethodContext mc = null;
		try {
			mc = MethodContext.getContext(Thread.currentThread());
			if (mc == null)
				mc = new MethodContext(null, null);
			if (mc.getAuthentication() == null) {
				SessionAuthenticator sa = new SessionAuthenticator();
				mc.setAuthentication(sa.setUserName(AdministrativeDomainHelper.ADMINISTRATOR_NAME));
			}
			context = mc.getId().toString();
			System.out.println("**********设置上下文**********" + context);
			if(!RemoteMethodServer.ServerFlag){
				String method = "getAllUserAndGroup";
				RemoteMethodServer rs = null;
				try {
					rs = RemoteMethodServer.getDefault();
				} catch (Exception e) {
					WTContextBean bean = new WTContextBean();
					rs = RemoteMethodServer.getDefault();
				}
				return (SychnUserGroupBean)rs.invoke(method, QueryUtil.class.getName(),null, new Class[]{},new Object[]{});
			}else{
				enforce = SessionServerHelper.manager.setAccessEnforced(false);
				sychnUserGroupBean = new SychnUserGroupBean();

				List<UserBean> userBeanList = getAllUser();
				List<GroupBean> groupBeanList = new ArrayList<GroupBean>();
				List<UserGroupLinkBean> userGroupLinkBeanList = new ArrayList<UserGroupLinkBean>();

				UserBean userBean;
				GroupBean groupBean;
				UserGroupLinkBean userGroupLinkBean;

				PropertiesUtil propertiesUtil = new PropertiesUtil(File.separator + "ext/sast/center/center.properties");
				String orgContainerName = propertiesUtil.getProperty("orgContainerName");
				OrgContainer container = searchOrgContainer(orgContainerName);
				if (container != null) {
					DirectoryContextProvider dir = container.getContextProvider();
					Enumeration<?> enumeration = OrganizationServicesHelper.manager.findLikeGroups("", dir);
					while (enumeration.hasMoreElements()) {
						WTGroup group = (WTGroup) enumeration.nextElement();
						groupBean = new GroupBean();
						groupBean.setIid(String.valueOf(group.getPersistInfo().getObjectIdentifier().getId()));
						groupBean.setId(group.getName());
						groupBean.setName(group.getName());
						groupBean.setParent_iid("");
						if (!groupBeanList.contains(groupBean)) {
							groupBeanList.add(groupBean);
						}

						Enumeration<?> members = group.members();

						while (members.hasMoreElements()) {
							Object o = members.nextElement();
							if (o instanceof WTUser) {
								WTUser wtUser = (WTUser) o;

								userBean = new UserBean();
								userBean.setIid(String.valueOf(wtUser.getPersistInfo().getObjectIdentifier().getId()));
								userBean.setId(wtUser.getName());
								userBean.setName(wtUser.getFullName());
								userBean.setSeclevel("");
								userBean.setTelephone(wtUser.getTelephoneNumber());
								if(wtUser.getEMail() == null || "".equals(wtUser.getEMail())){
									continue;
								}
								userBean.setEmail(wtUser.getEMail());
								if (!userBeanList.contains(userBean)) {
									userBeanList.add(userBean);
								}
								userGroupLinkBean = new UserGroupLinkBean();
								userGroupLinkBean.setUser_iid(userBean.getIid());
								userGroupLinkBean.setDiv_iid(groupBean.getIid());
								if (!userGroupLinkBeanList.contains(userGroupLinkBean)) {
									userGroupLinkBeanList.add(userGroupLinkBean);
								}
							}
						}

					}
				}
				String domain = (String) (WTProperties.getLocalProperties()).getProperty("wt.rmi.server.hostname", "");
				UserMessageBean userMessageBean = new UserMessageBean();
				userMessageBean.setMsg_id(TUUID.getUUID());
				userMessageBean.setMsg_type(Based.DC_RESPONSE_SYNUSER_RECEIVER);
				userMessageBean.setMsg_description("同步用户信息");
				userMessageBean.setMsg_created_time(System.currentTimeMillis());
				userMessageBean.setResponse_site_iid(domain);
				System.out.println("userBeanList = " + userBeanList.size());
				userMessageBean.setJa_users_response(userBeanList);

				GroupMessageBean groupMessageBean = new GroupMessageBean();
				groupMessageBean.setMsg_id(TUUID.getUUID());
				groupMessageBean.setMsg_type(Based.DC_RESPONSE_SYNDIV_RECEIVER);
				groupMessageBean.setMsg_description("用户组同步信息");
				groupMessageBean.setMsg_created_time(System.currentTimeMillis());
				groupMessageBean.setResponse_site_iid(domain);
				System.out.println("groupBeanList = " + groupBeanList.size());
				groupMessageBean.setJa_divs_response(groupBeanList);

				UserGroupLinkMessageBean userGroupLinkMessageBean = new UserGroupLinkMessageBean();
				userGroupLinkMessageBean.setMsg_id(TUUID.getUUID());
				userGroupLinkMessageBean.setMsg_type(Based.DC_RESPONSE_SYNPRINCIPAL_RECEIVER);
				userGroupLinkMessageBean.setMsg_description("用户和组织关系信息");
				userGroupLinkMessageBean.setMsg_created_time(System.currentTimeMillis());
				userGroupLinkMessageBean.setResponse_site_iid(domain);
				System.out.println("groupBeanList = " + userGroupLinkBeanList.size());
				userGroupLinkMessageBean.setJa_principals_response(userGroupLinkBeanList);

				sychnUserGroupBean.setUserMessageBean(userMessageBean);
				sychnUserGroupBean.setGroupMessageBean(groupMessageBean);
				sychnUserGroupBean.setUserGroupLinkMessageBean(userGroupLinkMessageBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
			if (mc != null) {
				mc.unregister();
			}
		}
		System.out.println("*********end*******");
		return sychnUserGroupBean;
	}

    @SuppressWarnings("rawtypes")
	public static JSONArray getAllMappingProductInfo(String mainProductName,List list,JSONArray array) {
    	JSONArray ja_std_products = new JSONArray();
    	List<String> productList = new ArrayList<String>();
    	for(int i=0;i<list.size();i++) {
    		Object obj = list.get(i);
    		WTContainer continer = null;
    		if(obj instanceof WTPart) {
    			continer = ((WTPart)obj).getContainer();
    		}else if(obj instanceof WTDocument){
    			continer = ((WTDocument)obj).getContainer();
    		}else if(obj instanceof EPMDocument){
    			continer = ((EPMDocument)obj).getContainer();
    		}
    		String localProductName = continer.getName();
    		if(productList.contains(localProductName) || mainProductName.equals(localProductName)) {
    			continue;
    		}
    		productList.add(localProductName);
    		//判断是否超库
    		if(localProductName.startsWith("八院")) {

    		}
    		String standardProductName = ProductConvertUtil.getStandardProdutcName(localProductName);
    		JSONObject temp = ProductConvertUtil.getSastProdcutInfo(standardProductName);
    		JSONObject json = new JSONObject();
    		json.put(Based.IID, temp.getString(Based.PRODUCT_IID));
    		json.put(Based.ID, temp.getString(Based.PRODUCT_ID));
    		json.put(Based.NAME, temp.getString(Based.PRODUCT_NAME));
    		ja_std_products.put(json);
    	}
		return ja_std_products;
    }
	@SuppressWarnings("rawtypes")
	public static JSONArray getAllObjectInfo(WTObject pbo,List memberList) throws JSONException, RemoteException, WTException {
		JSONArray ja_objects = new JSONArray();
		for(int i=0;i<memberList.size();i++) {
    		Object obj = memberList.get(i);
    		JSONObject object = new JSONObject();
    		if(obj instanceof WTPart) {
    			WTPart part = (WTPart) obj;
    			object.put(Based.OBJECT_OID, IxbHndHelper.getObjectIdImage(part));
				object.put(Based.OBJECT_MASTER_IID,part.getMaster().getPersistInfo().getObjectIdentifier().getId()+"");
				object.put(Based.OBJECT_ID, part.getNumber());
				object.put(Based.OBJECT_NAME, part.getName());
				object.put(Based.OBJECT_STATE, part.getLifeCycleState().getDisplay(Locale.CHINA));
				object.put(Based.OBJECT_VERSION, part.getVersionInfo().getIdentifier().getValue()+"."+part.getIterationInfo().getIdentifier().getValue());
				object.put(Based.OBJECT_CLASSNAME, TypeIdentifierUtilityHelper.service.getTypeIdentifier(part).toString());
				object.put(Based.OBJECT_TYPE,Based.OBJECT_TYPE_PART);
    		}else if(obj instanceof WTDocument){
    			WTDocument doc = (WTDocument) obj;
    			object.put(Based.OBJECT_OID, IxbHndHelper.getObjectIdImage(doc));
				object.put(Based.OBJECT_MASTER_IID,doc.getMaster().getPersistInfo().getObjectIdentifier().getId()+"");
				object.put(Based.OBJECT_ID, doc.getNumber());
				object.put(Based.OBJECT_NAME, doc.getName());
				object.put(Based.OBJECT_STATE, doc.getLifeCycleState().getDisplay(Locale.CHINA));
				object.put(Based.OBJECT_VERSION, doc.getVersionInfo().getIdentifier().getValue()+"."+doc.getIterationInfo().getIdentifier().getValue());
				object.put(Based.OBJECT_CLASSNAME, TypeIdentifierUtilityHelper.service.getTypeIdentifier(doc).toString());
				object.put(Based.OBJECT_TYPE,Based.OBJECT_TYPE_DOC);
    		}else if(obj instanceof EPMDocument){
    			EPMDocument epm = (EPMDocument) obj;
    			object.put(Based.OBJECT_OID, IxbHndHelper.getObjectIdImage(epm));
				object.put(Based.OBJECT_MASTER_IID,epm.getMaster().getPersistInfo().getObjectIdentifier().getId()+"");
				object.put(Based.OBJECT_ID, epm.getNumber());
				object.put(Based.OBJECT_NAME, epm.getName());
				object.put(Based.OBJECT_STATE, epm.getLifeCycleState().getDisplay(Locale.CHINA));
				object.put(Based.OBJECT_VERSION, epm.getVersionInfo().getIdentifier().getValue()+"."+epm.getIterationInfo().getIdentifier().getValue());
				object.put(Based.OBJECT_CLASSNAME, TypeIdentifierUtilityHelper.service.getTypeIdentifier(epm).toString());
				object.put(Based.OBJECT_TYPE,Based.OBJECT_TYPE_DOC);
    		}
    		//未来需要添加单据信息进来
    		ja_objects.put(object);

    		JSONObject mainObject = new JSONObject();
    		if(pbo instanceof ProcessEnvelope) {
    			ProcessEnvelope pe = (ProcessEnvelope) pbo;
    			mainObject.put(Based.OBJECT_OID, PersistenceHelper.getObjectIdentifier(pe).getId()+"");
    			mainObject.put(Based.OBJECT_MASTER_IID,"");
    			mainObject.put(Based.OBJECT_ID, pe.getNumber());
    			mainObject.put(Based.OBJECT_NAME, pe.getName());
    			mainObject.put(Based.OBJECT_STATE, pe.getLifeCycleState().getDisplay(Locale.CHINA));
    			mainObject.put(Based.OBJECT_VERSION, "");
    			mainObject.put(Based.OBJECT_CLASSNAME, TypeIdentifierUtilityHelper.service.getTypeIdentifier(pe).toString());
    			mainObject.put(Based.OBJECT_TYPE,Based.OBJECT_TYPE_ORDER);
    		}else if(pbo instanceof ChangePackaged) {
    			ChangePackaged cp = (ChangePackaged) pbo;
    			mainObject.put(Based.OBJECT_OID, PersistenceHelper.getObjectIdentifier(cp).getId()+"");
    			mainObject.put(Based.OBJECT_MASTER_IID,"");
    			mainObject.put(Based.OBJECT_ID, cp.getNumber());
    			mainObject.put(Based.OBJECT_NAME, cp.getName());
    			mainObject.put(Based.OBJECT_STATE, cp.getLifeCycleState().getDisplay(Locale.CHINA));
    			mainObject.put(Based.OBJECT_VERSION, "");
    			mainObject.put(Based.OBJECT_CLASSNAME, TypeIdentifierUtilityHelper.service.getTypeIdentifier(cp).toString());
				mainObject.put(Based.OBJECT_TYPE,Based.OBJECT_TYPE_ORDER);
    		}
    		ja_objects.put(mainObject);
    	}
		return ja_objects;
	}

}
