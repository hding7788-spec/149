package ext.sast.center.util;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ext.casc.product.CSCProduct;
import org.json.JSONObject;

import com.bjsasc.avidm.mq.message.Based;

import wt.method.MethodContext;
import wt.pdmlink.PDMLinkProduct;
import wt.pom.WTConnection;

public class ProductConvertUtil {

	/**
	 * 获取中心域型号信息(型号映射请求)
	 * @param productName
	 * @return
	 */
	public static JSONObject getSastProdcutInfo(String productName) {
		JSONObject j_product = new JSONObject();

		WTConnection wtconnection = null;
		Connection conn = null;
		StringBuilder sb = null;
		PreparedStatement pstate = null;
		ResultSet set  = null;
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();

			sb = new StringBuilder();
			sb.append("select a.SAST_IID,a.SAST_PRODUCT_IID,a.SAST_PRODUCT_ID,a.SAST_PRODUCT_NAME from SAST_PRODUCT_INFO a where a.SAST_PRODUCT_ID = '"+getStandardProdutcName(productName) +"'");
			pstate = conn.prepareStatement(sb.toString());
			 set  = pstate.executeQuery();
			if(set.next()) {
				String iid = set.getString("SAST_IID");
				String productiid = set.getString("SAST_PRODUCT_IID");
				String productid = set.getString("SAST_PRODUCT_ID");
				String productname = set.getString("SAST_PRODUCT_NAME");
				j_product.put(Based.IID, iid);
				j_product.put(Based.PRODUCT_IID,productiid );
				j_product.put(Based.PRODUCT_ID, productid);
				j_product.put(Based.ID, productid);
				j_product.put(Based.PRODUCT_NAME, productname);
				j_product.put(Based.NAME, productname);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(pstate!=null){
				try {
					pstate.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(set!=null){
				try {
					set.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return j_product;
	}

	/**
	 * 中心域标准型号转为本地型号
	 * @param sastProductId
	 * @return
	 */
	public static String getLocalProductName(String sastProductId) {
		String productName = "";
		String sastPId = "";
		WTConnection wtconnection = null;
		Connection conn = null;
		StringBuilder sb = null;
		PreparedStatement pstate = null;
		ResultSet set  = null;
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();

			sb = new StringBuilder();
			sb.append("select a.LOCALHOST_PRODUCT,a.LOCALHOST_PRODUCT_NAME,a.SAST_PRODUCT from PRODUCTINFO a where a.SAST_PRODUCT like '%"+sastProductId+"%'");
			pstate = conn.prepareStatement(sb.toString());
			set  = pstate.executeQuery();
			while(set.next()) {
				sastPId = set.getString("SAST_PRODUCT");
				if(sastPId.contains(",")){
					String[] sastPids = sastPId.split(",");
					for(String pid : sastPids){
						if(pid.equals(sastProductId)){
							productName = set.getString("LOCALHOST_PRODUCT_NAME");
							return productName;
						}
					}
				}else{
					if(sastPId.equals(sastProductId)){
						productName = set.getString("LOCALHOST_PRODUCT_NAME");
						return productName;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(pstate!=null){
				try {
					pstate.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(set!=null){
				try {
					set.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return productName;

	}

	/**
	 * 中心域标准型号转为本地型号
	 * @return
	 */
	public static PDMLinkProduct getLocalProductBySastProductiid(String sastProductiid) {
		WTConnection wtconnection = null;
		Connection conn = null;
		StringBuilder sb = null;
		PreparedStatement pstate = null;
		ResultSet set  = null;
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();

			sb = new StringBuilder();
			sb.append("select a.LOCALHOST_PRODUCT from PRODUCTINFO a where a.SAST_PRODUCT_IID = '"+sastProductiid+"'");
			pstate = conn.prepareStatement(sb.toString());
			set  = pstate.executeQuery();
			while(set.next()) {
				String localProductId = set.getString("LOCALHOST_PRODUCT");
				return CSCProduct.getProductByOid(localProductId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(pstate!=null){
				try {
					pstate.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(set!=null){
				try {
					set.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;

	}

	/**
	 * 本地型号转为中心域标准型号
	 * @param sastProductId
	 * @return
	 */
	public static String getStandardProdutcName(String productName) {
		String productId = "";
		WTConnection wtconnection = null;
		Connection conn = null;
		StringBuilder sb = null;
		PreparedStatement pstate = null;
		ResultSet set  = null;
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();

			sb = new StringBuilder();
			sb.append("select a.SAST_PRODUCT from PRODUCTINFO a where a.LOCALHOST_PRODUCT_NAME = '"+productName+"'");
			pstate = conn.prepareStatement(sb.toString());
			 set  = pstate.executeQuery();
			if(set.next()) {
				productId = set.getString("SAST_PRODUCT");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(pstate!=null){
				try {
					pstate.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(set!=null){
				try {
					set.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return productId;

	}

	/**
	 * 本地模型转为中心域标准模型
	 * @param modelId
	 * @return
	 */
	public static String getStandardModelType(String modelId) {
		String standardModelId = "";
		WTConnection wtconnection = null;
		Connection conn = null;
		StringBuilder sb = null;
		PreparedStatement pstate = null;
		ResultSet set  = null;
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();

			sb = new StringBuilder();
			sb.append("select a.SAST_TYPE_US from MODELTYPEINFO a where a.LOCALHOST_TYPE_US = '"+modelId+"'");
			pstate = conn.prepareStatement(sb.toString());
			 set  = pstate.executeQuery();
			if(set.next()) {
				standardModelId = set.getString("SAST_TYPE_US");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(pstate!=null){
				try {
					pstate.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(set!=null){
				try {
					set.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return standardModelId;
	}

	/**
	 * 中心域标准模型转为本地模型
	 * @param modelId
	 * @return
	 */
	public static String getlocalModelType(String standardModelId) {
		String modelId = "";
		WTConnection wtconnection = null;
		Connection conn = null;
		StringBuilder sb = null;
		PreparedStatement pstate = null;
		ResultSet set  = null;
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();

			sb = new StringBuilder();
			sb.append("select a.LOCALHOST_TYPE_US from MODELTYPEINFO a where a.SAST_TYPE_US = '"+standardModelId+"'");
			pstate = conn.prepareStatement(sb.toString());
			set = pstate.executeQuery();
			if(set.next()) {
				modelId = set.getString("LOCALHOST_TYPE_US");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(pstate!=null){
				try {
					pstate.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(set!=null){
				try {
					set.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return modelId;
	}

	/**
	 * 本地属性转为中心域属性
	 * @param attrId
	 * @return
	 */
	public static String getStandardAttr(String attrId) {
		String standardAttrId = "";
		WTConnection wtconnection = null;
		Connection conn = null;
		StringBuilder sb = null;
		PreparedStatement pstate = null;
		ResultSet set  = null;
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();

			sb = new StringBuilder();
			sb.append("select a.SAST_ATTRIBUTE_US from MODELTYPEATTRIBUTEINFO a where a.ATTRIBUTE_US = '"+attrId+"'");
			pstate = conn.prepareStatement(sb.toString());
			 set  = pstate.executeQuery();
			if(set.next()) {
				standardAttrId = set.getString("SAST_ATTRIBUTE_US");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(pstate!=null){
				try {
					pstate.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(set!=null){
				try {
					set.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return standardAttrId;
	}

	/**
	 *中心域属性 转为本地属性
	 * @param attrId
	 * @return
	 */
	public static String getLocalAttr(String standardAttrId) {
		String attrId = "";
		WTConnection wtconnection = null;
		Connection conn = null;
		StringBuilder sb = null;
		PreparedStatement pstate = null;
		ResultSet set  = null;
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();

			sb = new StringBuilder();
			sb.append("select a.ATTRIBUTE_US from MODELTYPEATTRIBUTEINFO a where a.SAST_ATTRIBUTE_US = '"+standardAttrId+"'");
			pstate = conn.prepareStatement(sb.toString());
			 set  = pstate.executeQuery();
			if(set.next()) {
				attrId = set.getString("ATTRIBUTE_US");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(pstate!=null){
				try {
					pstate.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(set!=null){
				try {
					set.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return attrId;
	}

	public static JSONObject getStandardProductInfoByID(String prod_id) {
		JSONObject json = new JSONObject();
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;

		StringBuilder sb = new StringBuilder();
		sb.append("select a.SAST_IID,a.SAST_PRODUCT_IID,a.SAST_PRODUCT_ID,a.SAST_PRODUCT_NAME from SAST_PRODUCT_INFO a　where　a.SAST_PRODUCT_ID = '"+prod_id+"'");
		ResultSet set = null;
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			pstmt = conn.prepareStatement(sb.toString());
			 set = pstmt.executeQuery(sb.toString());
			if(set.next()) {
				String iid = set.getString("SAST_IID");
				String sast_productIID = set.getString("SAST_PRODUCT_IID");
				String sast_productID = set.getString("SAST_PRODUCT_ID");
				String sast_productName = set.getString("SAST_PRODUCT_NAME");

				json.put(Based.IID, iid);
				json.put(Based.PRODUCT_IID, sast_productIID);
				json.put(Based.PRODUCT_ID, sast_productID);
				json.put(Based.PRODUCT_NAME, sast_productName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(pstmt!=null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(set!=null){
				try {
					set.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return json;
	}

	/**
	 * 本地属性转为中心域属性
	 * @param localAttrId
	 * @return
	 */
	public static String getStandardAttrId(String localAttrId) {
		String standardAttrId = "";
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sb = new StringBuilder();
		ResultSet set = null;
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();

			sb.append("select m.SAST_ATTRIBUTE_US from MODELTYPEATTRIBUTEINFO m where m.ATTRIBUTE_US = '"+localAttrId+"'");
			pstmt = conn.prepareStatement(sb.toString());
			set = pstmt.executeQuery(sb.toString());
			if(set.next()) {
				standardAttrId = set.getString("SAST_ATTRIBUTE_US");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(pstmt!=null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(set!=null){
				try {
					set.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return standardAttrId;

	}
	/**
	 * 中心域属性转为本地属性
	 * @param standardAttrId
	 * @return
	 */
	public static String getLocalAttrId(String standardAttrId) {
		String localAttrId = "";
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sb = new StringBuilder();
		ResultSet set = null;
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();

			sb.append("select m.ATTRIBUTE_US from MODELTYPEATTRIBUTEINFO m where m.SAST_ATTRIBUTE_US = '"+standardAttrId+"'");
			pstmt = conn.prepareStatement(sb.toString());
			 set = pstmt.executeQuery(sb.toString());
			if(set.next()) {
				localAttrId = set.getString("ATTRIBUTE_US");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(pstmt!=null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(set!=null){
				try {
					set.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return localAttrId;

	}

	public static String getStandardProductByLocalProductId(String localProductId) {
		String productId = "";
		WTConnection wtconnection = null;
		Connection conn = null;
		StringBuilder sb = null;
		PreparedStatement pstate = null;
		ResultSet set = null;
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();

			sb = new StringBuilder();
			sb.append("select a.SAST_PRODUCT from PRODUCTINFO a where a.LOCALHOST_PRODUCT = '"+localProductId+"'");
			pstate = conn.prepareStatement(sb.toString());
			set  = pstate.executeQuery();
			if(set.next()) {
				productId = set.getString("SAST_PRODUCT");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(pstate!=null) {
					pstate.close();
				}
				if(set!=null) {
					set.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return productId;

	}
}
