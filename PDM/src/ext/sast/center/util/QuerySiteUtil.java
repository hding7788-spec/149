package ext.sast.center.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bjsasc.avidm.mq.message.Based;

import wt.admin.AdministrativeDomainHelper;
import wt.method.MethodContext;
import wt.pom.WTConnection;
import wt.session.SessionAuthenticator;

public class QuerySiteUtil {

	public static String getDstSiteByIID(String siteIId) {
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String id="";
		try {
			if (!"null".equals(siteIId)&&siteIId!=null) {
				MethodContext mc = MethodContext.getContext(Thread.currentThread());
				if (mc == null)
					mc = new MethodContext(null, null);
				if (mc.getAuthentication() == null) {
					SessionAuthenticator sa = new SessionAuthenticator();
					mc.setAuthentication(sa.setUserName(AdministrativeDomainHelper.ADMINISTRATOR_NAME));
				}
				wtconnection = (WTConnection) mc.getConnection();
				conn = wtconnection.getConnection();
				StringBuilder sb = new StringBuilder();
				sb.append("select a.id from SYNCHSITEINFO a where iid = '"+siteIId+"'");
				pstmt = conn.prepareStatement(sb.toString());

				ResultSet set = pstmt.executeQuery();
				if(set.next()) {
					id = set.getString("id");
				}
				pstmt.close();
				set.close();
			}
		} catch (Exception e) {
			System.out.println();
			e.printStackTrace();
		}
		return id;
	}
	public static JSONObject getDstSite(String siteId) {
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		JSONObject dstSite = new JSONObject();
		ResultSet set = null;
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();

			StringBuilder sb = new StringBuilder();
			sb.append("select a.iid,a.name,a.id from SYNCHSITEINFO a where id = '"+siteId+"'");
			pstmt = conn.prepareStatement(sb.toString());

			set = pstmt.executeQuery();
			if(set.next()) {
				String iid = set.getString("iid");
				String id = set.getString("id");
				String name = set.getString("name");

				dstSite.put(Based.IID, iid);
				dstSite.put(Based.ID, id);
				dstSite.put(Based.NAME, name);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(set != null){
					set.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return dstSite;
	}

	public static JSONObject getDstSiteInfoForMetaMessage(List sendSites) {
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		JSONObject dstSite = new JSONObject();
		ResultSet set = null;
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();

			StringBuilder sb = new StringBuilder();
			sb.append("select a.iid,a.name,a.id from SYNCHSITEINFO a where 1=1");
			sb.append(" and id in (");
			String ids = "";
			for(int i=0;i<sendSites.size();i++) {
				String id = (String)sendSites.get(i);
				if(ids.length()<=0) {
					ids = "'"+id+"'";
				}else {
					ids = ids + ","+"'"+id+"'";
				}
			}
			sb.append(ids);
			sb.append(" )");
			System.out.println("getDstSiteInfoForMetaMessage查询单位信息 @@@@@@ = "+sb.toString());
			pstmt = conn.prepareStatement(sb.toString());

			set = pstmt.executeQuery();
			String dstNames = "";
			JSONArray dstIIds = new JSONArray();
			while(set.next()) {
				String iid = set.getString("iid");
				//String id = set.getString("id");
				String name = set.getString("name");
				if(dstNames.length()<=0) {
					dstNames = name;
				}else {
					dstNames = dstNames+ "," + name;
				}
				dstIIds.put(iid);
			}
			dstSite.put("META_DSTNAME", dstNames);
			dstSite.put("META_DSTIID", dstIIds);


		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(set != null){
					set.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return dstSite;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List getSendUtitForTest() {
		PropertiesUtil propertiesUtil = new PropertiesUtil("ext/sast/center/center.properties");
	    System.out.println("getSendUtitForTest单位 @@@@@ = " + propertiesUtil.getProperty("sendTo"));
	    String sendToStr = propertiesUtil.getProperty("sendTo");
	    List list = new ArrayList();
	    String[] array = sendToStr.split(",");
	    for(int i=0;i<array.length;i++) {
	    	if(!list.contains(array[i])) {
	    		list.add(array[i]);
	    	}
	    }
	    return list;
	}
}
