package ext.sast.center.ixb.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bjsasc.avidm.mq.message.Based;
import com.ptc.core.meta.common.impl.TypeIdentifierUtilityHelper;

import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.inf.container.WTContainer;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipalReference;
import wt.part.WTPart;
import wt.pom.WTConnection;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.workflow.engine.WfProcess;

public class SendDataHelper implements RemoteAccess{
	private static final DecimalFormat df = new DecimalFormat("#");
	@SuppressWarnings("rawtypes")
	public static JSONObject getProductInfo(Object obj) throws RemoteException, InvocationTargetException {
		JSONObject j_product = new JSONObject();
		String productOid = "";
		String productName = "";
		WTContainer container = null;
		String user = "";
		if (!RemoteMethodServer.ServerFlag) {
			String method = "getProductInfo";
			Class[] types = { Object.class };
			Object[] vals = { obj};
			return (JSONObject) RemoteMethodServer.getDefault().invoke(method, SendDataHelper.class.getName(), null, types, vals);
		}else{
			try {
				SessionHelper.manager.setAdministrator();

				if(obj instanceof WTDocument) {
					WTDocument doc = (WTDocument) obj;
					container = doc.getContainer();
				}else if(obj instanceof EPMDocument){
					EPMDocument epm = (EPMDocument) obj;
					container = epm.getContainer();
				}else if(obj instanceof WTPart){
					WTPart part = (WTPart) obj;
					container = part.getContainer();
				}else if(obj instanceof WTChangeOrder2){
					WTChangeOrder2 part = (WTChangeOrder2) obj;
					container = part.getContainer();
				}
				if(container != null) {
					productOid = PersistenceHelper.getObjectIdentifier(container).getId()+"";
					productName = container.getName();
				}
				j_product.put(Based.IID,productOid );
				j_product.put(Based.ID, productOid);
				j_product.put(Based.NAME, productName);

			}catch (WTException e) {
				e.printStackTrace();
			} finally {
				try {
					user = SessionHelper.manager.getPrincipal().getName();
					SessionHelper.manager.setPrincipal(user);
				} catch (WTException e) {
					e.printStackTrace();
				}
			}
		}
		return j_product;
	}
	@SuppressWarnings("rawtypes")
	public static JSONArray getSendUnitInfo(HashMap inputparams) throws RemoteException, InvocationTargetException {
		JSONArray ja_dst_sites = new JSONArray();
		String user = "";
		if (!RemoteMethodServer.ServerFlag) {
			String method = "getSendUnitInfo";
			Class[] types = { HashMap.class };
			Object[] vals = { inputparams};
			return (JSONArray) RemoteMethodServer.getDefault().invoke(method, SendDataHelper.class.getName(), null, types, vals);
		}else{
			try {
				SessionHelper.manager.setAdministrator();

				String sendToStr = (String) inputparams.get("sendTo");
				if(sendToStr.indexOf(",") > 0) {
					String[] sendUnits = sendToStr.split(",");
					for(String sendTo : sendUnits) {
						JSONObject sendUnit = getSiteInfoById(sendTo);
						ja_dst_sites.put(sendUnit);
					}
				}else {
					JSONObject sendUnit = getSiteInfoById(sendToStr);
					ja_dst_sites.put(sendUnit);
				}
			}catch (WTException e) {
				e.printStackTrace();
			} finally {
				try {
					user = SessionHelper.manager.getPrincipal().getName();
					SessionHelper.manager.setPrincipal(user);
				} catch (WTException e) {
					e.printStackTrace();
				}
			}
		}
		return ja_dst_sites;
	}
	public static JSONObject getSiteInfoById(String siteId) {
		JSONObject sendUnit = new JSONObject();
		StringBuilder sb = new StringBuilder();
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet set = null;
		try {
			sb.append("select m.iid,m.id,m.name from SYNCHSITEINFO m where m.id = '"+siteId+"' ");
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			pstmt = conn.prepareStatement(sb.toString());
			set = pstmt.executeQuery(sb.toString());
			if(set.next()){
				sendUnit.put(Based.SITE_IID, set.getString("iid"));
				sendUnit.put(Based.SITE_ID, set.getString("id"));
				sendUnit.put(Based.SITE_NAME, set.getString("name"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(set!=null){
				try {
					set.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(pstmt!=null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return sendUnit;
	}

	@SuppressWarnings("rawtypes")
	public static JSONObject getSendFromInfo(HashMap inputparams) throws RemoteException, InvocationTargetException {
		JSONObject j_src_site = new JSONObject();
		String user = "";
		if (!RemoteMethodServer.ServerFlag) {
			String method = "getSendFromInfo";
			Class[] types = { HashMap.class };
			Object[] vals = { inputparams};
			return (JSONObject) RemoteMethodServer.getDefault().invoke(method, SendDataHelper.class.getName(), null, types, vals);
		}else{
			try {
				SessionHelper.manager.setAdministrator();
				String domain = (String) (WTProperties.getLocalProperties()).getProperty("wt.rmi.server.hostname", "");
				j_src_site.put(Based.IID, domain);
				j_src_site.put(Based.ID, domain);
				j_src_site.put(Based.NAME, domain);

			}catch (WTException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					user = SessionHelper.manager.getPrincipal().getName();
					SessionHelper.manager.setPrincipal(user);
				} catch (WTException e) {
					e.printStackTrace();
				}
			}
		}
		return j_src_site;
	}
	@SuppressWarnings("rawtypes")
	public static JSONArray getSendObjects(Object obj,HashMap inputparams) throws RemoteException, InvocationTargetException {
		JSONArray ja_objects_request = new JSONArray();
		String user = "";
		JSONObject object = null;
		if (!RemoteMethodServer.ServerFlag) {
			String method = "getSendObjects";
			Class[] types = { Object.class,HashMap.class };
			Object[] vals = { obj,inputparams};
			return (JSONArray) RemoteMethodServer.getDefault().invoke(method, SendDataHelper.class.getName(), null, types, vals);
		}else{
			try {
				SessionHelper.manager.setAdministrator();

				//对发放进行过滤
				String sendTo = (String) inputparams.get("sendTo");
				System.out.println("sendTo = >>> " + sendTo);
				if(obj instanceof WTDocument || obj instanceof EPMDocument || obj instanceof WTPart) {
					object = getObjectInfo(obj);
					ja_objects_request.put(object);

				}else if(obj instanceof WTChangeOrder2){
					WTChangeOrder2 order = (WTChangeOrder2) obj;

					object = new JSONObject();
					object.put(Based.OBJECT_OID, PersistenceHelper.getObjectIdentifier(order).getId()+"");
					object.put(Based.OBJECT_MASTER_IID,"");
					object.put(Based.OBJECT_ID, PersistenceHelper.getObjectIdentifier(order).getId()+"");
					object.put(Based.OBJECT_NAME, order.getName());
					object.put(Based.OBJECT_STATE, order.getLifeCycleState().getDisplay(Locale.CHINA));
					object.put(Based.OBJECT_VERSION, Based.SYS_VERSION_WIN10);
					object.put(Based.OBJECT_CLASSNAME, TypeIdentifierUtilityHelper.service.getTypeIdentifier(order).toString());
					object.put(Based.OBJECT_TYPE,Based.OBJECT_TYPE_ORDER);

					QueryResult qr = ChangeHelper2.service.getChangeablesAfter(order);
					while(qr.hasMoreElements()) {
						WTObject wtobj = (WTObject) qr.nextElement();
						object = getObjectInfo(wtobj);
						ja_objects_request.put(object);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					user = SessionHelper.manager.getPrincipal().getName();
					SessionHelper.manager.setPrincipal(user);
				} catch (WTException e) {
					e.printStackTrace();
				}
			}

		}

		return ja_objects_request;
	}

	@SuppressWarnings("rawtypes")
	public static JSONObject getObjectInfo(Object obj) throws RemoteException, InvocationTargetException {
		String user = "";
		JSONObject object = null;
		if (!RemoteMethodServer.ServerFlag) {
			String method = "getObjectInfo";
			Class[] types = { Object.class };
			Object[] vals = { obj};
			return (JSONObject) RemoteMethodServer.getDefault().invoke(method, SendDataHelper.class.getName(), null, types, vals);
		}else{
			try {
				SessionHelper.manager.setAdministrator();
				if(obj instanceof WTDocument) {
					WTDocument doc = (WTDocument) obj;
					object = new JSONObject();
					object.put(Based.OBJECT_OID, PersistenceHelper.getObjectIdentifier(doc).getId()+"");
					object.put(Based.OBJECT_MASTER_IID,PersistenceHelper.getObjectIdentifier(doc.getMaster()).getId()+"");
					object.put(Based.OBJECT_ID, PersistenceHelper.getObjectIdentifier(doc).getId()+"");
					object.put(Based.OBJECT_NAME, doc.getName());
					object.put(Based.OBJECT_STATE, doc.getLifeCycleState().getDisplay(Locale.CHINA));
					object.put(Based.OBJECT_VERSION, doc.getIterationDisplayIdentifier().toString());
					object.put(Based.OBJECT_CLASSNAME, TypeIdentifierUtilityHelper.service.getTypeIdentifier(doc).toString());
					object.put(Based.OBJECT_TYPE,Based.OBJECT_TYPE_DOC);
				}else if(obj instanceof EPMDocument){
					EPMDocument epm = (EPMDocument) obj;
					object = new JSONObject();
					object.put(Based.OBJECT_OID, PersistenceHelper.getObjectIdentifier(epm).getId()+"");
					object.put(Based.OBJECT_MASTER_IID,PersistenceHelper.getObjectIdentifier(epm.getMaster()).getId()+"");
					object.put(Based.OBJECT_ID, PersistenceHelper.getObjectIdentifier(epm).getId()+"");
					object.put(Based.OBJECT_NAME, epm.getName());
					object.put(Based.OBJECT_STATE, epm.getLifeCycleState().getDisplay(Locale.CHINA));
					object.put(Based.OBJECT_VERSION, epm.getIterationDisplayIdentifier().toString());
					object.put(Based.OBJECT_CLASSNAME, TypeIdentifierUtilityHelper.service.getTypeIdentifier(epm).toString());
					object.put(Based.OBJECT_TYPE,Based.OBJECT_TYPE_DOC);

				}else if(obj instanceof WTPart){
					WTPart part = (WTPart) obj;
					object = new JSONObject();
					object.put(Based.OBJECT_OID, PersistenceHelper.getObjectIdentifier(part).getId()+"");
					object.put(Based.OBJECT_MASTER_IID,PersistenceHelper.getObjectIdentifier(part.getMaster()).getId()+"");
					object.put(Based.OBJECT_ID, PersistenceHelper.getObjectIdentifier(part).getId()+"");
					object.put(Based.OBJECT_NAME, part.getName());
					object.put(Based.OBJECT_STATE, part.getLifeCycleState().getDisplay(Locale.CHINA));
					object.put(Based.OBJECT_VERSION, part.getIterationDisplayIdentifier().toString());
					object.put(Based.OBJECT_CLASSNAME, TypeIdentifierUtilityHelper.service.getTypeIdentifier(part).toString());
					object.put(Based.OBJECT_TYPE,Based.OBJECT_TYPE_PART);
				}
			} catch (WTException e) {
				e.printStackTrace();
			} finally {
				try {
					user = SessionHelper.manager.getPrincipal().getName();
					SessionHelper.manager.setPrincipal(user);
				} catch (WTException e) {
					e.printStackTrace();
				}
			}
		}
		return object;
	}

	@SuppressWarnings("rawtypes")
	public static JSONArray getReceiversInfo(Object obj) throws RemoteException, InvocationTargetException {
		JSONArray ja_receivers = new JSONArray();
		String user = "";
		if (!RemoteMethodServer.ServerFlag) {
			String method = "getReceiversInfo";
			Class[] types = { Object.class };
			Object[] vals = { obj};
			return (JSONArray) RemoteMethodServer.getDefault().invoke(method, SendDataHelper.class.getName(), null, types, vals);
		}else{
			try {
				SessionHelper.manager.setAdministrator();

				JSONObject object = new JSONObject();
				object.put(Based.SITE_ID, "");
				object.put(Based.IID, "");
				object.put(Based.ID, "");
				object.put(Based.NAME, "");

				ja_receivers.put(object);
			}catch (WTException e) {
				e.printStackTrace();
			} finally {
				try {
					user = SessionHelper.manager.getPrincipal().getName();
					SessionHelper.manager.setPrincipal(user);
				} catch (WTException e) {
					e.printStackTrace();
				}
			}
		}
		return ja_receivers;
	}

	@SuppressWarnings("rawtypes")
	public static JSONObject getCreatorInfo(HashMap inputparams) throws RemoteException, InvocationTargetException {
		JSONObject ja_receivers = new JSONObject();
		String user = "";
		if (!RemoteMethodServer.ServerFlag) {
			String method = "getCreatorInfo";
			Class[] types = { HashMap.class };
			Object[] vals = { inputparams};
			return (JSONObject) RemoteMethodServer.getDefault().invoke(method, SendDataHelper.class.getName(), null, types, vals);
		}else{
			try {
				SessionHelper.manager.setAdministrator();
				String wfProcessOid = (String) inputparams.get("wfProcessOid");
				ReferenceFactory rf = new ReferenceFactory();
				WfProcess wfprocess = (WfProcess) rf.getReference(wfProcessOid).getObject();
				WTPrincipalReference principal = wfprocess.getCreator();
				String id = PersistenceHelper.getObjectIdentifier((Persistable) principal).getId()+"";
				ja_receivers.put(Based.IID,id);
				ja_receivers.put(Based.ID, id);
				ja_receivers.put(Based.NAME,principal.getFullName());

			}catch (WTException e) {
				e.printStackTrace();
			} finally {
				try {
					user = SessionHelper.manager.getPrincipal().getName();
					SessionHelper.manager.setPrincipal(user);
				} catch (WTException e) {
					e.printStackTrace();
				}
			}
		}
		return ja_receivers;
	}

	@SuppressWarnings("rawtypes")
	public static JSONObject getFileInfo(File file) throws RemoteException, InvocationTargetException {
		JSONObject j_file = new JSONObject();
		String user = "";
		if (!RemoteMethodServer.ServerFlag) {
			String method = "getFileInfo";
			Class[] types = { File.class };
			Object[] vals = { file};
			return (JSONObject) RemoteMethodServer.getDefault().invoke(method, SendDataHelper.class.getName(), null, types, vals);
		}else{
			try {
				SessionHelper.manager.setAdministrator();
				j_file.put(Based.FILE_ID, file.getName());
				j_file.put(Based.FILE_NAME, file.getName());
				String size = df.format(file.length()/1024.0)+"KB";
				j_file.put(Based.FILE_SIZE, size);
				j_file.put(Based.KEY, "");
				j_file.put(Based.IVKEY, "");
			}catch (WTException e) {
				e.printStackTrace();
			} finally {
				try {
					user = SessionHelper.manager.getPrincipal().getName();
					SessionHelper.manager.setPrincipal(user);
				} catch (WTException e) {
					e.printStackTrace();
				}
			}
		}
		return j_file;
	}
}
