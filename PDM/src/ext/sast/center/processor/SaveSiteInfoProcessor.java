package ext.sast.center.processor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

import wt.admin.AdministrativeDomainHelper;
import wt.method.MethodContext;
import wt.pom.WTConnection;
import wt.session.SessionAuthenticator;

import com.bjsasc.avidm.mq.message.Based;

import ext.sast.center.bean.SiteBean;
import ext.sast.center.bean.message.SiteMessageBean;

public class SaveSiteInfoProcessor {

	private static String context = "Unknown";

	public static void saveSiteInfo(SiteMessageBean siteMessageCenter,String operate_type) {
		WTConnection wtconnection = null;
		Connection conn = null;
		StringBuilder sb = null;
		PreparedStatement pstate = null;
		PreparedStatement pstate_delete = null;
		System.out.println("**********开始保存域注册信息**********");
		MethodContext mc = null;
		ResultSet set = null;
		try {
			mc = MethodContext.getContext(Thread.currentThread());
            if (mc == null)
                mc = new MethodContext(null, null);
            if (mc.getAuthentication() == null) {
                SessionAuthenticator sa = new SessionAuthenticator();
                mc.setAuthentication(sa.setUserName(AdministrativeDomainHelper.ADMINISTRATOR_NAME));
            }
            context = mc.getId().toString();
            System.out.println("**********设置上下文**********"+context);

		System.out.println("operate_type == " + operate_type);
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();

			SiteBean  siteBean = siteMessageCenter.getJ_siteinfo_response();
			sb = new StringBuilder();
			sb.append("select m.iid from SYNCHSITEINFO m where iid = '"+siteBean.getIid()+"'");
			pstate = conn.prepareStatement(sb.toString());
			set = pstate.executeQuery();
			if(set.next()){
				System.out.println("**********sb update**********");
				sb = new StringBuilder();
				sb.append("update SYNCHSITEINFO set id = '"+siteBean.getId()+"',name='"+siteBean.getName()+"',ip='"+siteBean.getIp()+"',"
						+ "port='"+siteBean.getPort()+"',version='"+siteBean.getVersion()+"',academy_id='"+siteBean.getAcademy_id()+"',"
								+ "academy_name ='"+siteBean.getAcademy_name()+"' where iid='"+siteBean.getIid()+"'");
				PreparedStatement pstate_update = conn.prepareStatement(sb.toString());
				System.out.println("**********sb insert**********"+sb.toString());
				int num = pstate_update.executeUpdate();
				if(num>0) {
					conn.commit();
				}
				pstate_update.close();
			}else {
				System.out.println("**********sb insert**********");
				sb = new StringBuilder();
				sb.append("insert into SYNCHSITEINFO (iid,id,name,ip,port,version,academy_id,academy_name) values ("
						+ "?,?,?,?,?,?,?,?)");
				PreparedStatement pstate_insert = conn.prepareStatement(sb.toString());
				pstate_insert.setString(1, siteBean.getIid());
				pstate_insert.setString(2, siteBean.getId());
				pstate_insert.setString(3, siteBean.getName());
				pstate_insert.setString(4, siteBean.getIp());
				pstate_insert.setString(5, siteBean.getPort());
				pstate_insert.setString(6, siteBean.getVersion());
				pstate_insert.setString(7, siteBean.getAcademy_id());
				pstate_insert.setString(8, siteBean.getAcademy_name());
				System.out.println("**********sb insert**********"+sb.toString());
				int num = pstate_insert.executeUpdate();
				if(num>0) {
					conn.commit();
				}
			}
			if ("delete".equals(operate_type)) {
				sb = new StringBuilder();
				sb.append("delete SYNCHSITEINFO where iid='"+siteBean.getIid()+"'");
				pstate_delete = conn.prepareStatement(sb.toString());
				int num = pstate_delete.executeUpdate();
				if(num>0) {
					conn.commit();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(set != null){
					set.close();
				}
				if(pstate != null){
					pstate.close();
				}
				if(pstate_delete != null){
					pstate_delete.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if(mc!=null){
				mc.unregister();
			}
		}

	}

	/**
	 * @param array
	 * @throws Exception
	 */
	public static void synchSiteInfo(JSONArray array) {
		MethodContext mc = null;
		WTConnection wtconnection = null;
		Connection conn = null;
		StringBuilder sb = null;
		PreparedStatement pstate = null;
		PreparedStatement pstate_insert = null;
		PreparedStatement pstate_update = null;
		ResultSet set = null;
		try {
			mc = MethodContext.getContext(Thread.currentThread());
            if (mc == null)
                mc = new MethodContext(null, null);
            if (mc.getAuthentication() == null) {
                SessionAuthenticator sa = new SessionAuthenticator();
                mc.setAuthentication(sa.setUserName(AdministrativeDomainHelper.ADMINISTRATOR_NAME));
            }
            context = mc.getId().toString();
            System.out.println("**********设置上下文**********"+context);


		System.out.println("**********开始保存域注册信息**********");
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();

			for(int i=0;i<array.length();i++) {
				JSONObject site = array.getJSONObject(i);
				String iid = site.getString(Based.IID);
				String id = site.getString(Based.ID);
				String name = site.getString(Based.NAME);
				String ip = site.getString(Based.IP);
				String port = site.getString(Based.PORT);
				String version = site.getString(Based.VERSION);
				String academy_id = site.getString(Based.ACADEMY_ID);
				String academy_name = site.getString(Based.ACADEMY_NAME);

				sb = new StringBuilder();
				sb.append("select m.iid from SYNCHSITEINFO m where iid = '"+iid+"'");
				pstate = conn.prepareStatement(sb.toString());
				set = pstate.executeQuery();
				if(set.next()){
					sb = new StringBuilder();
					sb.append("update SYNCHSITEINFO set id = '"+id+"',name='"+name+"',ip='"+ip+"',"
							+ "port='"+port+"',version='"+version+"',academy_id='"+academy_id+"',"
									+ "academy_name ='"+academy_name+"' where iid='"+iid+"'");
					pstate_update = conn.prepareStatement(sb.toString());
					System.out.println("**********sb update**********"+sb.toString());
					int num = pstate_update.executeUpdate();
					if(num>0) {
						conn.commit();
					}
					pstate_update.close();
				}else {
					sb = new StringBuilder();
					sb.append("insert into SYNCHSITEINFO (iid,id,name,ip,port,version,academy_id,academy_name) values ("
							+ "?,?,?,?,?,?,?,?)");
					pstate_insert = conn.prepareStatement(sb.toString());
					pstate_insert.setString(1, iid);
					pstate_insert.setString(2, id);
					pstate_insert.setString(3, name);
					pstate_insert.setString(4, ip);
					pstate_insert.setString(5, port);
					pstate_insert.setString(6, version);
					pstate_insert.setString(7, academy_id);
					pstate_insert.setString(8, academy_name);
					System.out.println("**********sb insert**********"+sb.toString());
					int num = pstate_insert.executeUpdate();
					if(num>0) {
						conn.commit();
					}

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(set != null){
					set.close();
				}
				if(pstate != null){
					pstate.close();
				}
				if(pstate_insert != null){
					pstate_insert.close();
				}
				if(pstate_update != null){
					pstate_update.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if(mc!=null){
				mc.unregister();
			}
		}
	}

}
