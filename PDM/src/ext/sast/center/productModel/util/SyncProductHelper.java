package ext.sast.center.productModel.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import wt.admin.AdministrativeDomainHelper;
import wt.inf.container.OrgContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.org.WTOrganization;
import wt.pom.WTConnection;
import wt.session.SessionAuthenticator;
import wt.util.WTException;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;

import com.bjsasc.avidm.mq.message.Based;
import com.ptc.windchill.uwgm.common.container.OrganizationHelper;

import ext.casc.util.DBConn;
import ext.sast.center.productModel.bean.SAST_PDMLinkProduct;


public class SyncProductHelper implements RemoteAccess{
	private static String context = "Unknown";
	/**
	 * 根据本地型号获取中心域型号名称
	 * @param localHost_productOid
	 * @return
	 */
	public static String getSastProductNameByProductOid(String localHost_productOid){
		String sast_product = null;
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet set = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("select m.SAST_PRODUCT,m.SAST_PRODUCT_NAME from PRODUCTINFO m where 1=1");
			if(localHost_productOid != null && localHost_productOid.length()>0){
				sb.append(" and m.LOCALHOST_PRODUCT = '"+localHost_productOid+"' ");
			}
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			pstmt = conn.prepareStatement(sb.toString());
			set = pstmt.executeQuery(sb.toString());
			if(set.next()) {
				sast_product = set.getString("SAST_PRODUCT_NAME");
			}
			set.close();
			pstmt.close();
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
		return sast_product;
	}

	/**
	 * 根据本地型号更新中心域映射信息
	 * @param localhost_product
	 * @param sast_product
	 * @param sast_product_name
	 * @param sast_remarks
	 */
	public static String saveSastProductInfo(String localhost_product,String localhost_productName,String sast_product,String sast_product_name,String sast_remarks,String sastProductIID){
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet set = null;
		String result = "";
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("select m.SAST_PRODUCT,m.SAST_PRODUCT_NAME from PRODUCTINFO m where 1=1");
			if(localhost_product != null && localhost_product.length()>0){
				sb.append(" and m.LOCALHOST_PRODUCT = '"+localhost_product+"' ");
			}
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			pstmt = conn.prepareStatement(sb.toString());
			set = pstmt.executeQuery(sb.toString());
			if(set.next()) {
				StringBuilder sb_update = new StringBuilder();
				sb_update.append("update PRODUCTINFO set LOCALHOST_PRODUCT_NAME='"+localhost_productName+"',SAST_PRODUCT_IID = '"+sastProductIID+"',SAST_PRODUCT = '"+sast_product+"',SAST_PRODUCT_NAME = '" + sast_product_name + "',SAST_REMARKS = '"+sast_remarks+"',"
						+ "SYNC_TIME = '" + System.currentTimeMillis() + "' where LOCALHOST_PRODUCT = '"+localhost_product+"'");
				pstmt = conn.prepareStatement(sb_update.toString());
				int num = pstmt.executeUpdate();
				if(num>0){
					conn.commit();
				}
				result = "modify";
			}else {
				StringBuilder sb_insert = new StringBuilder();
				sb_insert.append("insert into PRODUCTINFO (LOCALHOST_PRODUCT,LOCALHOST_PRODUCT_NAME,SAST_PRODUCT,SAST_PRODUCT_NAME,SAST_REMARKS,SYNC_TIME,SAST_PRODUCT_IID) values (?,?,?,?,?,?,?)");
				pstmt = conn.prepareStatement(sb_insert.toString());
				pstmt.setString(1, localhost_product);
				pstmt.setString(2, localhost_productName);
				pstmt.setString(3, sast_product);
				pstmt.setString(4, sast_product_name);
				pstmt.setString(5, sast_remarks);
				pstmt.setLong(6, System.currentTimeMillis());
				pstmt.setString(7, sastProductIID);

				int num = pstmt.executeUpdate();
				if(num>0){
					conn.commit();
				}
				result = "add";
			}
			set.close();
			pstmt.close();
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
		return result;
	}

	public static List<SAST_PDMLinkProduct> getAllSastProductInfo(String sast_productoid) {
		List<SAST_PDMLinkProduct> list = new ArrayList<SAST_PDMLinkProduct>();
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet set = null;

		try {Set<String> filter = new HashSet<String>();
			StringBuilder sb = new StringBuilder();
			sb.append("select m.SAST_PRODUCT_ID,m.SAST_PRODUCT_NAME from SAST_PRODUCT_INFO m where 1=1");
			if(sast_productoid != null && sast_productoid.length()>0) {
				sb.append(" and SAST_PRODUCT_NAME like '%"+sast_productoid+"'%");
			}
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			pstmt = conn.prepareStatement(sb.toString());
			set = pstmt.executeQuery();
			while(set.next()) {
				String sast_porductoid = set.getString("SAST_PRODUCT_ID");
				String sast_porductname = set.getString("SAST_PRODUCT_NAME");
				//String sast_remarks = set.getString("SAST_REMARKS");
				SAST_PDMLinkProduct product = new SAST_PDMLinkProduct();
				product.setSAST_ProductOid(sast_porductoid);
				product.setSAST_ProductName(sast_porductname);
				//product.setSAST_Remarks(sast_remarks);
				if(!filter.contains(sast_porductoid)){
					list.add(product);
				}
				filter.add(sast_porductoid);

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
		return list;
	}

	public static void saveSastProductInfo(String sast_productoid,String sast_productName,String operate_type,String remarks) {
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sb = null;
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();

			if("delete".equals(operate_type)) {

			}else if("modify".equals(operate_type)){
				sb = new StringBuilder();
				sb.append("update SAST_PRODUCTINFO set SAST_PRODUCTNAME = '"+sast_productName+"', SAST_REMARKS = '"+remarks+"', SYNC_TIME = '"+System.currentTimeMillis()+"', OPERATE_TYPE = 'modify' where SAST_PRODUCTOID = '"+sast_productoid+"' ");
				pstmt = conn.prepareStatement(sb.toString());
				int num = pstmt.executeUpdate();
				if(num>0){
					conn.commit();
				}
			}else if("add".equals(operate_type)){
				sb = new StringBuilder();
				sb.append("insert into SAST_PRODUCTNAME (SAST_PRODUCTOID,SAST_PRODUCTNAME,SAST_REMARKS,SYNC_TIME,OPERATE_TYPE) values('','','','','')");
				pstmt = conn.prepareStatement(sb.toString());
				pstmt.setString(1, sast_productoid);
				pstmt.setString(2, sast_productName);
				pstmt.setString(3, remarks);
				pstmt.setLong(4, System.currentTimeMillis());
				pstmt.setString(5, "add");
				int num = pstmt.executeUpdate();
				if(num>0){
					conn.commit();
				}
			}else if("all".equals(operate_type)) {

			}
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null){
					pstmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 中心域产品下发保存
	 * @param operateType
	 * @param product_iid
	 * @param product_id
	 * @param product_name
	 */
	public static void saveDataCenterProduct(String operateType,JSONArray jsonArray) {
		WTConnection wtconnection = null;
		Connection conn = null;
		StringBuilder sb = null;
		MethodContext mc = null;
		PreparedStatement allDelState = null;
		PreparedStatement delState = null;
		PreparedStatement addState = null;
		PreparedStatement modState = null;
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

			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();

			//如果是全部下发，先删除全部，再重新增加
			if("ALL".equalsIgnoreCase(operateType)){
				sb.append("delete from SAST_PRODUCT_INFO");
				delState = conn.prepareStatement(sb.toString());
				int num = delState.executeUpdate();
				if(num>0){
					conn.commit();
				}
			}

			for(int i=0;i<jsonArray.length();i++) {
				JSONObject jsonobject = jsonArray.getJSONObject(i);
				String iid = (String) jsonobject.get(Based.IID);
				String product_iid = (String) jsonobject.get(Based.PRODUCT_IID);
				String product_id = (String) jsonobject.get(Based.PRODUCT_ID);
				String product_name = (String) jsonobject.get(Based.PRODUCT_NAME);
				String siteIID = (String)jsonobject.get(Based.SITE_IID);
				String sietId = (String)jsonobject.get(Based.SITE_ID);
				String siteName = (String)jsonobject.get(Based.SITE_NAME);
				sb = new StringBuilder();
				if("ALL".equalsIgnoreCase(operateType)) {
					sb.append("insert into SAST_PRODUCT_INFO (SAST_IID,SAST_PRODUCT_IID,SAST_PRODUCT_ID,SAST_PRODUCT_NAME,SYNC_TIME,OPERATE_TYPE，SITE_IID,SITE_ID,SITE_NAME) values"
							+ " (?,?,?,?,?,?,?,?,?)");
					allDelState = conn.prepareStatement(sb.toString());
					allDelState.setString(1, iid);
					allDelState.setString(2, product_iid);
					allDelState.setString(3, product_id);
					allDelState.setString(4, product_name);
					allDelState.setLong(5, System.currentTimeMillis());
					allDelState.setString(6, operateType);
					allDelState.setString(7, siteIID);
					allDelState.setString(8, sietId);
					allDelState.setString(9, siteName);
					int index = allDelState.executeUpdate();
					if(index>0) {
						conn.commit();
					}

				}else if("ADD".equalsIgnoreCase(operateType) || operateType == null || operateType.length()<=0) {
					String processName = "中心域标准型号下发提示";
					//先判断是否已经存在
					sb.append("delete from SAST_PRODUCT_INFO where SAST_PRODUCT_IID = ? and SITE_IID = ?");
					delState = conn.prepareStatement(sb.toString());
					delState.setString(1,product_iid);
					delState.setString(2,siteIID);
					int delIndex = delState.executeUpdate();
					if(delIndex > 0) {
						conn.commit();
						processName = "中心域标准型号修改提示";

						//更新型号映射
						//updateProductInfoLink(product_iid,product_id,product_name);
					}
					delState.close();

					sb = new StringBuilder();
					sb.append("insert into SAST_PRODUCT_INFO (SAST_IID,SAST_PRODUCT_IID,SAST_PRODUCT_ID,SAST_PRODUCT_NAME,SYNC_TIME,OPERATE_TYPE，SITE_IID,SITE_ID,SITE_NAME) values"
							+ " (?,?,?,?,?,?,?,?,?)");
					addState = conn.prepareStatement(sb.toString());
					addState.setString(1, iid);
					addState.setString(2, product_iid);
					addState.setString(3, product_id);
					addState.setString(4, product_name);
					addState.setLong(5, System.currentTimeMillis());
					addState.setString(6, operateType);
					addState.setString(7, siteIID);
					addState.setString(8, sietId);
					addState.setString(9, siteName);
					int index = addState.executeUpdate();
					if(index>0) {
						conn.commit();
					}
					addState.close();
					//启动流程通知
					startMqCenterRemindProcess(processName,product_id,product_name,siteName);
				}else if("DELETE".equalsIgnoreCase(operateType)) {
					sb.append("delete from SAST_PRODUCT_INFO a where a.SAST_PRODUCT_IID = '"+product_iid+"' and SAST_PRODUCT_ID = '"+product_id+"'");
					delState = conn.prepareStatement(sb.toString());
					int num = delState.executeUpdate();
					if(num>0){
						conn.commit();
					}
					delState.close();
				}else if("MODIFY".equalsIgnoreCase(operateType)) {
					sb.append("update SAST_PRODUCT_INFO set SAST_PRODUCT_ID = '"+product_id+"',SAST_PRODUCT_NAME = '"+product_name+"',"
							+ "SYNC_TIME = '"+System.currentTimeMillis()+"',OPERATE_TYPE = '"+operateType+"' where SAST_PRODUCT_IID = '"+product_iid+"'");
					modState = conn.prepareStatement(sb.toString());
					int num = modState.executeUpdate();
					if(num>0){
						conn.commit();
					}
					modState.close();

					updateProductInfoLink(product_iid,product_id,product_name);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(delState != null && !delState.isClosed()){
					delState.close();
				}
				if(modState != null && !modState.isClosed()){
					modState.close();
				}
				if(allDelState != null && !allDelState.isClosed()){
					allDelState.close();
				}
				if(addState != null && !addState.isClosed()){
					addState.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			if(mc!=null){
				mc.unregister();
			}
		}

	}

	public static void startMqCenterRemindProcess(String processName,String productIid, String productName, String siteName){
		WTOrganization org;
		try {
			org = OrganizationHelper.getOrganizationByName("149");
			OrgContainer container = WTContainerHelper.service.getOrgContainer(org);
			WTContainerRef containerRef = WTContainerRef.newWTContainerRef(container);
			WfProcessDefinition wfProcessDefinition = WfDefinerHelper.service.getProcessDefinition(processName,containerRef);
			WfProcess process = WfEngineHelper.service.createProcess(wfProcessDefinition,null);
			process.setName(processName+"_" + productName);
			ProcessData processData = process.getContext();
			processData.setValue("message","产品型号：" + productIid + ",产品名称：" + productName + ",标准型号所属单位：" + siteName);
			WfEngineHelper.service.startProcess(process,processData,1);
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param product_iid
	 * @param product_id
	 * @param product_name
	 */
	private static void updateProductInfoLink(String product_iid, String product_id, String product_name) {
		DBConn conn = null;
		try {
			conn = new DBConn();
			String sql = "update PRODUCTINFO  set SAST_PRODUCT='"+ product_id +"',SAST_PRODUCT_NAME='"+product_name+"' where SAST_PRODUCT_IID='"+product_iid+"'";
			conn.executeUpdate(sql);
			conn.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public static Object getSastProductIndexByProductOid(String productOid) {
		String sast_product = null;
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet set = null;
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("select m.SAST_PRODUCT from PRODUCTINFO m where 1=1");
			if(productOid != null && productOid.length()>0){
				sb.append(" and m.LOCALHOST_PRODUCT = '"+productOid+"' ");
			}
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			pstmt = conn.prepareStatement(sb.toString());
			set = pstmt.executeQuery(sb.toString());
			if(set.next()) {
				sast_product = set.getString("SAST_PRODUCT");
			}
			set.close();
			pstmt.close();
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
		return sast_product;
	}
}
