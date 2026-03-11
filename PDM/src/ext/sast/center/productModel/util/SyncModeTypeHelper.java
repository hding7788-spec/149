package ext.sast.center.productModel.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bjsasc.avidm.mq.message.Based;
import com.ptc.core.lwc.common.view.AttributeDefinitionReadView;
import com.ptc.core.lwc.common.view.PropertyHolderHelper;
import com.ptc.core.lwc.common.view.TypeDefinitionReadView;
import com.ptc.core.lwc.server.LWCTypeDefinition;
import com.ptc.core.lwc.server.TypeDefinitionServiceHelper;

import ext.sast.center.productModel.bean.ReceivedModelTypeInfo;
import wt.admin.AdministrativeDomainHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.MethodContext;
import wt.pom.WTConnection;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionAuthenticator;
import wt.util.WTException;

public class SyncModeTypeHelper {
	
	private static String context = "Unknown";
	
	private static final String[] TYPES = {"wt.doc.WTDocument","wt.part.WTPart"};

	public static List<TypeDefinitionReadView> getAllType() throws WTException{
		List<TypeDefinitionReadView> list = new ArrayList<TypeDefinitionReadView>();
		List<LWCTypeDefinition> parents = getModelType(TYPES);
		for(LWCTypeDefinition parent : parents) {
			long oid = PersistenceHelper.getObjectIdentifier(parent).getId();
			getChildType(list,oid);
		}
		return list;
	}
	@SuppressWarnings("deprecation")
	private static List<LWCTypeDefinition> getModelType(String[] types){
		List<LWCTypeDefinition> list = new ArrayList<LWCTypeDefinition>();
		try {
			for(int i=0;i<types.length;i++) {
				QuerySpec qs = new QuerySpec(LWCTypeDefinition.class);
				qs.appendWhere(new SearchCondition(LWCTypeDefinition.class,LWCTypeDefinition.NAME,SearchCondition.LIKE,types[i]));
				QueryResult qr = PersistenceHelper.manager.find(qs);
				while(qr.hasMoreElements()) {
					LWCTypeDefinition lwcType = (LWCTypeDefinition) qr.nextElement();
					list.add(lwcType);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}
	@SuppressWarnings("deprecation")
	private static void getChildType(List<TypeDefinitionReadView> list,long oid){
		try {
			QuerySpec qs = new QuerySpec(LWCTypeDefinition.class);
			qs.appendWhere(new SearchCondition(LWCTypeDefinition.class,"parentReference.key.id",SearchCondition.EQUAL,oid));
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while(qr.hasMoreElements()) {
				LWCTypeDefinition lwcType = (LWCTypeDefinition) qr.nextElement();
				String displayType = lwcType.getDisplayIdentifier().getLocalizedMessage(Locale.CHINA);
				TypeDefinitionReadView ty = TypeDefinitionServiceHelper.service.getTypeDefView(displayType);
				list.add(ty);
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	public static String saveModelType(String localHostType_ZH,String localHostType_US,String sastType_ZH,String sastType_US){
		StringBuilder sb = new StringBuilder();
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String result = "";
		ResultSet set = null;
		try{
			sb.append("select m.LOCALHOST_TYPE_US from MODELTYPEINFO m where m.LOCALHOST_TYPE_US = '"+localHostType_US+"' ");
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			pstmt = conn.prepareStatement(sb.toString());
			set = pstmt.executeQuery(sb.toString());
			if(set.next()){
				StringBuilder sb_update = new StringBuilder();
				sb_update.append("update MODELTYPEINFO set SAST_TYPE_ZH = '"+sastType_ZH+"', SAST_TYPE_US = '"+sastType_US+"',"
						+ "SYNC_TIME = '"+System.currentTimeMillis()+"' where LOCALHOST_TYPE_US = '"+localHostType_US+"'");
				pstmt = conn.prepareStatement(sb_update.toString());
				int num = pstmt.executeUpdate();
				if(num>0) {
					conn.commit();
				}
				result = "modify";
			}else{
				StringBuilder sb_insert = new StringBuilder();
				sb_insert.append("insert into MODELTYPEINFO (LOCALHOST_TYPE_ZH,LOCALHOST_TYPE_US,SAST_TYPE_ZH,SAST_TYPE_US,SYNC_TIME) values"
						+ "(?,?,?,?,?)");
				pstmt = conn.prepareStatement(sb_insert.toString());
				pstmt.setString(1, localHostType_ZH);
				pstmt.setString(2, localHostType_US);
				pstmt.setString(3, sastType_ZH);
				pstmt.setString(4, sastType_US);
				pstmt.setLong(5, System.currentTimeMillis());
				int num = pstmt.executeUpdate();
				if(num>0){
					conn.commit();
				}
				result = "add";
			}

		}catch(Exception e){
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
	
	public static String getSastModelTypeInfoByLocalHostType_US(String localHostType_US) {
		String result = null;
		StringBuilder sb = new StringBuilder();
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			sb.append("select m.SAST_TYPE_ZH,m.SAST_TYPE_US from MODELTYPEINFO m where m.LOCALHOST_TYPE_US = '"+localHostType_US+"' ");
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			pstmt = conn.prepareStatement(sb.toString());
			ResultSet set = pstmt.executeQuery(sb.toString());
			if(set.next()){
				String sastType_ZH = set.getString("SAST_TYPE_ZH");
				String sastType_US = set.getString("SAST_TYPE_US");
				result = sastType_ZH+","+sastType_US;
			}
			set.close();
			pstmt.close();
		}catch(Exception e){
			e.printStackTrace();
		} 
		return result;
	}
	
	public static String[] getModelTypeZHByLocalHostType_US(String localHostType_US) {
		String[] result = null;
		StringBuilder sb = new StringBuilder();
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			sb.append("select m.LOCALHOST_TYPE_ZH,m.SAST_TYPE_ZH,m.SAST_TYPE_US from MODELTYPEINFO m where m.LOCALHOST_TYPE_US = '"+localHostType_US+"' ");
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			pstmt = conn.prepareStatement(sb.toString());
			ResultSet set = pstmt.executeQuery(sb.toString());
			if(set.next()){
				String localHostType_ZH = set.getString("LOCALHOST_TYPE_ZH");
				String sastType_ZH = set.getString("SAST_TYPE_ZH");
				if(sastType_ZH == null || sastType_ZH.length()<=0 || "null".equalsIgnoreCase(sastType_ZH)) {
					return null;
				}
				String sast_type_us = set.getString("SAST_TYPE_US");
				if(sast_type_us == null || sast_type_us.length()<=0 ||"null".equalsIgnoreCase(sast_type_us)) {
					return null;
				}
				if(sastType_ZH != null && sastType_ZH.length()>0) {
					result = new String[] {localHostType_ZH,sastType_ZH,sast_type_us};
				}else {
					result = new String[] {localHostType_ZH};
				}
			}
			set.close();
			pstmt.close();
		}catch(Exception e){
			e.printStackTrace();
		} 
		return result;
	}
	
	public static void saveModelTypeAttributeInfo(String attr_zh,String attr_us,String sast_attr_zh,String sast_attr_us,String sastModelType_ZH,String sastModelType_US) {
		StringBuilder sb = new StringBuilder();
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet set = null;
		try{
			sb.append("select m.ATTRIBUTE_US from MODELTYPEATTRIBUTEINFO m where m.ATTRIBUTE_US = '"+attr_us+"' ");
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			pstmt = conn.prepareStatement(sb.toString());
			set = pstmt.executeQuery(sb.toString());
			if(set.next()){
				StringBuilder sb_update = new StringBuilder();
				sb_update.append("update MODELTYPEATTRIBUTEINFO set SAST_ATTRIBUTE_ZH = '"+sast_attr_zh+"', SAST_ATTRIBUTE_US = '"+sast_attr_us+"',"
						+ "SYNC_TIME ='"+System.currentTimeMillis()+"' where ATTRIBUTE_US = '"+attr_us+"'");
				pstmt = conn.prepareStatement(sb_update.toString());
				pstmt.executeUpdate();
				conn.commit();
			}else{
				StringBuilder sb_insert = new StringBuilder();
				sb_insert.append("insert into MODELTYPEATTRIBUTEINFO (ATTRIBUTE_ZH,ATTRIBUTE_US,SAST_ATTRIBUTE_ZH,SAST_ATTRIBUTE_US,SAST_MODELTYPE_ZH,SAST_MODELTYPE_US,SYNC_TIME) values"
						+ "(?,?,?,?,?,?,?)");
				pstmt = conn.prepareStatement(sb_insert.toString());
				pstmt.setString(1, attr_zh);
				pstmt.setString(2, attr_us);
				pstmt.setString(3, sast_attr_zh);
				pstmt.setString(4, sast_attr_us);
				pstmt.setString(5, sastModelType_ZH);
				pstmt.setString(6, sastModelType_US);
				pstmt.setLong(7, System.currentTimeMillis());
				int num = pstmt.executeUpdate();
				if(num>0){
					conn.commit();
				}
			}

		}catch(Exception e){
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
	}
	public static String[] getModelTypeAttributeInfo(String attr_us) {
		String[] result = null;
		StringBuilder sb = new StringBuilder();
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			sb.append("select m.SAST_ATTRIBUTE_US,m.SAST_ATTRIBUTE_ZH from MODELTYPEATTRIBUTEINFO m where m.ATTRIBUTE_US = '"+attr_us+"' ");
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			pstmt = conn.prepareStatement(sb.toString());
			ResultSet set = pstmt.executeQuery(sb.toString());
			if(set.next()){
				String sast_attribute_us = set.getString("SAST_ATTRIBUTE_US");
				String sast_attribute_zh =	set.getString("SAST_ATTRIBUTE_ZH");
				
				result = new String[] {sast_attribute_us,sast_attribute_zh};
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public static String getSastModelTypeAttrInfoByAttr_US(String attr_US,String sast_modelType_US) {
		String result = null;
		StringBuilder sb = new StringBuilder();
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			sb.append("select m.SAST_ATTRIBUTE_ZH,m.SAST_ATTRIBUTE_US from MODELTYPEATTRIBUTEINFO m where m.ATTRIBUTE_US = '"+attr_US+"'"
					+ " and SAST_MODELTYPE_US ='"+sast_modelType_US+"' ");
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			pstmt = conn.prepareStatement(sb.toString());
			ResultSet set = pstmt.executeQuery(sb.toString());
			System.out.println("MODELTYPEATTRIBUTEINFO === "+sb.toString());
			if(set.next()){
				String sastType_ZH = set.getString("SAST_ATTRIBUTE_ZH");
				String sastType_US = set.getString("SAST_ATTRIBUTE_US");
				result = sastType_ZH + "," + sastType_US;
			}
			set.close();
			pstmt.close();
		}catch(Exception e){
			e.printStackTrace();
		} 
		return result;
	}
	public static String getSastModelAttrDisplayName(String sastAttr_US,String sast_modelType_US) {
		String result = null;
		StringBuilder sb = new StringBuilder();
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			sb.append("select m.SAST_MODELATTR_ZH,m.SAST_MODELATTR_US from SAST_MODELATTR_INFO m where m.SAST_MODELATTR_US = '"+sastAttr_US+"'"
					+ " and SAST_MODELTYPE_US ='"+sast_modelType_US+"' ");
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			pstmt = conn.prepareStatement(sb.toString());
			ResultSet set = pstmt.executeQuery(sb.toString());
			if(set.next()){
				String sastType_ZH = set.getString("SAST_MODELATTR_ZH");
				//String sastType_US = set.getString("SAST_ATTRIBUTE_US");
				result = sastType_ZH;
			}
			set.close();
			pstmt.close();
		}catch(Exception e){
			e.printStackTrace();
		} 
		return result;
	}
	public static Map<String,String> getAllSastModelTypeAttrInfo() {
		Map<String,String> map = new HashMap<String,String>();
		StringBuilder sb = new StringBuilder();
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try{
			sb.append("select m.ATTRIBUTE_US,m.SAST_ATTRIBUTE_US from ModelTypeAttributeInfo m");
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			pstmt = conn.prepareStatement(sb.toString());
			ResultSet set = pstmt.executeQuery(sb.toString());
			while(set.next()){
				String attribute_us = set.getString("ATTRIBUTE_US");
				String sastType_US = set.getString("SAST_ATTRIBUTE_US");
				map.put(attribute_us, sastType_US);
			}
			set.close();
			pstmt.close();
		}catch(Exception e){
			e.printStackTrace();
		} 
		return map;
	}
	public static void getSastModelType(Map<String,String> map){
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sb = null;
	    //System.out.println("**********************中心域类型查循**********************");
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			sb = new StringBuilder();
			sb.append("select m.SAST_MODELID,m.SAST_MODELNAME from SAST_MODELTYPE_INFO m");
			pstmt = conn.prepareStatement(sb.toString());
			ResultSet set = pstmt.executeQuery();
			while(set.next()) {
				//String modelParentType = set.getString("SAST_MODELPARENT_TYPE");
				String sast_modeltype_us = set.getString("SAST_MODELID");
				String sast_modeltype_zh = set.getString("SAST_MODELNAME");
				map.put(sast_modeltype_us,sast_modeltype_zh);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void saveSastModelTypeInfo(String sast_modelType_oid,String sast_modelType_us,String sast_modelType_zh,String operate_type) {
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sb = null;
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			
			if("delete".equals(operate_type)) {
				sb = new StringBuilder();
			}else if("modify".equals(operate_type)){
				sb = new StringBuilder();
				sb.append("update SAST_MODELTYPE set SAST_MODELTYPE_ZH = '"+sast_modelType_zh+"', SYNC_TIME = '"+System.currentTimeMillis()+"', OPERATE_TYPE = 'modify' where SAST_MODELTYPE_US = '"+sast_modelType_us+"' and SAST_MODELTYPE_OID = '"+sast_modelType_oid+"'");
				pstmt = conn.prepareStatement(sb.toString());
				int num = pstmt.executeUpdate();
				if(num>0){
					conn.commit();
				}
			}else if("add".equals(operate_type)){
				sb = new StringBuilder();
				sb.append("insert into SAST_MODELTYPE (SAST_MODELTYPE_OID,SAST_MODELTYPE_US,SAST_MODELTYPE_ZH,SYNC_TIME,OPERATE_TYPE) values('','','','','')");
				pstmt = conn.prepareStatement(sb.toString());
				pstmt.setString(1, sast_modelType_oid);
				pstmt.setString(2, sast_modelType_us);
				pstmt.setString(3, sast_modelType_zh);
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
		}
	}
	
	public static Map<String,ArrayList<String>> getSastModelAttrByModelType(String sast_modelType_us) {
		Map<String,ArrayList<String>> map = new  HashMap<String,ArrayList<String>>();
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sb = null;
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			sb = new StringBuilder();
			sb.append("select m.SAST_MODELATTR_US,m.SAST_MODELATTR_ZH from SAST_MODELATTR_INFO m where SAST_MODELTYPE_US = '"+sast_modelType_us+"' ");
			pstmt = conn.prepareStatement(sb.toString());
			ResultSet set = pstmt.executeQuery();
			while(set.next()) {
				String sast_modelattr_us = set.getString("SAST_MODELATTR_US");
				String sast_modelattr_zh = set.getString("SAST_MODELATTR_ZH");
				if(map.containsKey("InternalValues")){
					ArrayList<String> interNalValueList = map.get("InternalValues");
					interNalValueList.add(sast_modelattr_us);
					map.put("InternalValues", interNalValueList);
				}else {
					ArrayList<String> interNalValueList = new ArrayList<String>();
					interNalValueList.add(sast_modelattr_us);
					map.put("InternalValues", interNalValueList);
				}
				if(map.containsKey("DisplayValues")) {
					ArrayList<String> displayValueList = map.get("DisplayValues");
					displayValueList.add(sast_modelattr_zh);
					map.put("DisplayValues", displayValueList);
				}else {
					ArrayList<String> displayValueList = new ArrayList<String>();
					displayValueList.add(sast_modelattr_zh);
					map.put("DisplayValues", displayValueList);
				}
			}
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public static void saveSastModelAttrInfo(String sast_modelId,String sast_modelAttr_us,String sast_modelAttr_zh,String operate_type) {
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sb = null;
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			
			if("delete".equals(operate_type)) {
				sb = new StringBuilder();
				sb.append("delete from SAST_MODELATTR where SAST_MODELID = '"+sast_modelId+"' and SAST_MODELATTR_US = '"+sast_modelAttr_us+"'");
				pstmt = conn.prepareStatement(sb.toString());
				int num = pstmt.executeUpdate();
				if(num>0){
					conn.commit();
				}
			}else if("modify".equals(operate_type)){
				sb = new StringBuilder();
				sb.append("update SAST_MODELATTR set sast_modelAttr_zh = '"+sast_modelAttr_zh+"', SYNC_TIME = '"+System.currentTimeMillis()+"',"
						+ " OPERATE_TYPE = 'modify' where SAST_MODELATTR_US = '"+sast_modelAttr_us+"' and SAST_MODELID = '"+sast_modelId+"'");
				pstmt = conn.prepareStatement(sb.toString());
				int num = pstmt.executeUpdate();
				if(num>0){
					conn.commit();
				}
			}else if("add".equals(operate_type)){
				sb = new StringBuilder();
				sb.append("insert into SAST_MODELATTR (SAST_MODELID,SAST_MODELATTR_US,SAST_MODELATTR_ZH,SYNC_TIME,OPERATE_TYPE) values('','','','','')");
				pstmt = conn.prepareStatement(sb.toString());
				pstmt.setString(1, sast_modelId);
				pstmt.setString(2, sast_modelAttr_us);
				pstmt.setString(3, sast_modelAttr_zh);
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
		}
	}
	
	public static List<ReceivedModelTypeInfo> getReceiveScopeype() {
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sb = null;
		List<ReceivedModelTypeInfo> list = new ArrayList<ReceivedModelTypeInfo>();
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			sb = new StringBuilder();
			sb.append("select a.PARENT_MODELTYPE_ID,a.PARENT_MODELTYPE_NAME,a.MODELTYPE_ID,a.MODELTYPE_NAME from RECEIVESCOPEMODELTYPE a");
			pstmt = conn.prepareStatement(sb.toString());
			ResultSet set = pstmt.executeQuery();
			while(set.next()) {
				ReceivedModelTypeInfo info = new ReceivedModelTypeInfo();
				info.setParent_modelType_id(set.getString("PARENT_MODELTYPE_ID"));
				info.setParent_modelType_name(set.getString("PARENT_MODELTYPE_NAME"));
				info.setModel_type_id(set.getString("MODELTYPE_ID"));
				info.setModel_type_name(set.getString("MODELTYPE_NAME"));
				list.add(info);
			}
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
		
	}
	public static List<ReceivedModelTypeInfo> getReceivedType() {
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sb = null;
		List<ReceivedModelTypeInfo> list = new ArrayList<ReceivedModelTypeInfo>();
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			sb = new StringBuilder();
			sb.append("select m.id,m.SAST_MODELTYPEID,m.SAST_MODELTYPENAME,m.LOCAL_MODELTYPEID,m.LOCAL_MODELTYPENAME from RECEIVEDMODELTYPE m ");
			pstmt = conn.prepareStatement(sb.toString());
			ResultSet set = pstmt.executeQuery();
			while(set.next()) {
				ReceivedModelTypeInfo info = new ReceivedModelTypeInfo();
				info.setInnerId(set.getString("id"));
				info.setSast_modeltypeid(set.getString("SAST_MODELTYPEID"));
				info.setSast_modeltypename(set.getString("SAST_MODELTYPENAME"));
				info.setLocal_modeltypeid(set.getString("LOCAL_MODELTYPEID"));
				info.setLocal_modeltypename(set.getString("LOCAL_MODELTYPENAME"));
				list.add(info);
			}
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
		
	}
	
	
	public static Map<String,ArrayList<String>> getLocalComboxList() {
		Map<String,ArrayList<String>> map = new HashMap<String,ArrayList<String>>();
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sb = null;
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			sb = new StringBuilder();
			sb.append("select a.PARENT_MODELTYPE_ID,a.PARENT_MODELTYPE_NAME,a.MODELTYPE_ID,a.MODELTYPE_NAME from RECEIVESCOPEMODELTYPE a");
			pstmt = conn.prepareStatement(sb.toString());
			ResultSet set = pstmt.executeQuery();
			ArrayList<String> dis_list = new ArrayList<String>();
			ArrayList<String> value_list = new ArrayList<String>();
			while(set.next()) {
				String modeltype_id = set.getString("MODELTYPE_ID");
				String modeltype_name = set.getString("MODELTYPE_NAME");
				if(!value_list.contains(modeltype_id)) {
					value_list.add(modeltype_id);
					dis_list.add(modeltype_name);
				}
			}
			map.put("DisplayValues", dis_list);
			map.put("InternalValues", value_list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public static Map<String,ArrayList<String>> getSastComboxList() {
		Map<String,ArrayList<String>> map = new HashMap<String,ArrayList<String>>();
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sb = null;
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			sb = new StringBuilder();
			sb.append("select m.SAST_MODELID,m.SAST_MODELNAME from SAST_MODELTYPE_INFO m");
			pstmt = conn.prepareStatement(sb.toString());
			ResultSet set = pstmt.executeQuery();
			ArrayList<String> dis_list = new ArrayList<String>();
			ArrayList<String> value_list = new ArrayList<String>();
			while(set.next()) {
				String modeltype_id = set.getString("SAST_MODELID");
				String modeltype_name = set.getString("SAST_MODELNAME");
				if(!value_list.contains(modeltype_id)) {
					value_list.add(modeltype_id);
					dis_list.add(modeltype_name);
				}
			}
			map.put("DisplayValues", dis_list);
			map.put("InternalValues", value_list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public static void saveReceivedModelTypeInfo(String id,String sastModelTypeId,String localModelTypeId) {
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sb = null;
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			sb = new StringBuilder();
			sb.append("update RECEIVEDMODELTYPE set SAST_MODELTYPEID = '"+sastModelTypeId+"',"
					+ "SAST_MODELTYPENAME = '"+getSastModelTypeNameById(sastModelTypeId)+"',"
					+ " LOCAL_MODELTYPEID = '"+localModelTypeId+"',"
					+ "LOCAL_MODELTYPENAME = '"+getLocalModelTypeDisplayName(localModelTypeId)+"' where id = '"+id+"' ");
			pstmt = conn.prepareStatement(sb.toString());
			int num  = pstmt.executeUpdate();
			if(num>0) {
				conn.commit();
			}

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
	
	public static String getSastModelTypeNameById(String modelTypeId) {
		String modelTypeName = null;
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sb = null;
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			sb = new StringBuilder();
			sb.append("select m.SAST_MODELNAME from SAST_MODELTYPE_INFO m where m.SAST_MODELID = '"+modelTypeId+"'");
			pstmt = conn.prepareStatement(sb.toString());
			ResultSet set = pstmt.executeQuery();
			if(set.next()) {
				modelTypeName = set.getString("SAST_MODELNAME");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return modelTypeName;
	}
	
	@SuppressWarnings("deprecation")
	public static String getLocalModelTypeDisplayName(String modelTypeId){
		String modelTypeName = null;
		try {
			
			QuerySpec qs = new QuerySpec(LWCTypeDefinition.class);
			qs.appendWhere(new SearchCondition(LWCTypeDefinition.class,LWCTypeDefinition.NAME,SearchCondition.LIKE,modelTypeId));
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while(qr.hasMoreElements()) {
				LWCTypeDefinition lwcType = (LWCTypeDefinition) qr.nextElement();
				String displayType = lwcType.getDisplayIdentifier().getLocalizedMessage(Locale.CHINA);
				TypeDefinitionReadView ty = TypeDefinitionServiceHelper.service.getTypeDefView(displayType);
				modelTypeName = PropertyHolderHelper.getDisplayName(ty,Locale.CHINA);
			}
			
		} catch (WTException e) {
			e.printStackTrace();
		}
		return modelTypeName;
	}
	
	public static List<ReceivedModelTypeInfo> getReceivedModelTypeInfoById(String id){
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sb = null;
		List<ReceivedModelTypeInfo> list = new ArrayList<ReceivedModelTypeInfo>();
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			sb = new StringBuilder();
			sb.append("select m.id,m.SAST_MODELTYPEID,m.SAST_MODELTYPENAME,m.LOCAL_MODELTYPEID,m.LOCAL_MODELTYPENAME from RECEIVEDMODELTYPE m where m.id = '"+id+"'");
			pstmt = conn.prepareStatement(sb.toString());
			ResultSet set = pstmt.executeQuery();
			while(set.next()) {
				ReceivedModelTypeInfo info = new ReceivedModelTypeInfo();
				info.setInnerId(set.getString("id"));
				info.setSast_modeltypeid(set.getString("SAST_MODELTYPEID"));
				info.setSast_modeltypename(set.getString("SAST_MODELTYPENAME"));
				info.setLocal_modeltypeid(set.getString("LOCAL_MODELTYPEID"));
				info.setLocal_modeltypename(set.getString("LOCAL_MODELTYPENAME"));
				list.add(info);
			}
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static Map<String,String> getReceiveSastModelAttrByModelType(String sast_modelType_us) {
		Map<String,String> map = new  HashMap<String,String>();
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sb = null;
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			sb = new StringBuilder();
			sb.append("select m.SAST_MODELATTR_US,m.SAST_MODELATTR_ZH from SAST_MODELATTR_INFO m where SAST_MODELTYPE_US = '"+sast_modelType_us+"' ");
			pstmt = conn.prepareStatement(sb.toString());
			ResultSet set = pstmt.executeQuery();
			while(set.next()) {
				String sast_modelattr_us = set.getString("SAST_MODELATTR_US");
				String sast_modelattr_zh = set.getString("SAST_MODELATTR_ZH");
				map.put(sast_modelattr_us, sast_modelattr_zh);
			}
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	@SuppressWarnings("deprecation")
	public static Map<String, ArrayList<String>> getLocalModelAttrByModelType(String modelTypeId) {
		Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		try {
			QuerySpec qs = new QuerySpec(LWCTypeDefinition.class);
			qs.appendWhere(new SearchCondition(LWCTypeDefinition.class,LWCTypeDefinition.NAME,SearchCondition.LIKE,modelTypeId));
			QueryResult qr = PersistenceHelper.manager.find(qs);
			if(qr.hasMoreElements()) {
				LWCTypeDefinition lwcType = (LWCTypeDefinition) qr.nextElement();
				String displayType = lwcType.getDisplayIdentifier().getLocalizedMessage(Locale.CHINA);
				TypeDefinitionReadView ty = TypeDefinitionServiceHelper.service.getTypeDefView(displayType);
				Collection<AttributeDefinitionReadView> collection = ty.getAllAttributes();
				Iterator<AttributeDefinitionReadView> iterator = collection.iterator();
				
				while(iterator.hasNext()) {
					AttributeDefinitionReadView adrv = iterator.next();
					String localHostType_ZH = PropertyHolderHelper.getDisplayName(adrv, Locale.CHINA);
					String localHostType_US = PropertyHolderHelper.getName(adrv);
					if(map.containsKey("InternalValues")){
						ArrayList<String> interNalValueList = map.get("InternalValues");
						interNalValueList.add(localHostType_US);
						map.put("InternalValues", interNalValueList);
					}else {
						ArrayList<String> interNalValueList = new ArrayList<String>();
						interNalValueList.add(localHostType_US);
						map.put("InternalValues", interNalValueList);
					}
					if(map.containsKey("DisplayValues")) {
						ArrayList<String> displayValueList = map.get("DisplayValues");
						displayValueList.add(localHostType_ZH);
						map.put("DisplayValues", displayValueList);
					}else {
						ArrayList<String> displayValueList = new ArrayList<String>();
						displayValueList.add(localHostType_ZH);
						map.put("DisplayValues", displayValueList);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	@SuppressWarnings("deprecation")
	public static String getLocalModelAttrDisplayName(String modelType,String localModelAttr) {
		try {
			QuerySpec qs = new QuerySpec(LWCTypeDefinition.class);
			qs.appendWhere(new SearchCondition(LWCTypeDefinition.class,LWCTypeDefinition.NAME,SearchCondition.LIKE,modelType));
			QueryResult qr = PersistenceHelper.manager.find(qs);
			if(qr.hasMoreElements()) {
				LWCTypeDefinition lwcType = (LWCTypeDefinition) qr.nextElement();
				String displayType = lwcType.getDisplayIdentifier().getLocalizedMessage(Locale.CHINA);
				TypeDefinitionReadView ty = TypeDefinitionServiceHelper.service.getTypeDefView(displayType);
				Collection<AttributeDefinitionReadView> collection = ty.getAllAttributes();
				Iterator<AttributeDefinitionReadView> iterator = collection.iterator();
				while(iterator.hasNext()) {
					AttributeDefinitionReadView adrv = iterator.next();
					String localHostType_US = PropertyHolderHelper.getName(adrv);
					if(localModelAttr.equals(localHostType_US)) {
						String localHostType_ZH = PropertyHolderHelper.getDisplayName(adrv, Locale.CHINA);
						return localHostType_ZH;
					}
				}
			}
		}catch(Exception e){
			
		}
		return null;
		
	}
	
	public static void saveReceivedModelTypeAttributeInfo(String sast_attribute_zh, String sast_attribute_us,
			String attribute_zh, String attribute_us, String sastModelType_US, String localModelType_US) {
			WTConnection wtconnection = null;
			Connection conn = null;
			PreparedStatement pstmt = null;
			StringBuilder sb = null;
			ResultSet set = null;
			try {
				MethodContext methodcontext = MethodContext.getContext();
				wtconnection = (WTConnection) methodcontext.getConnection();
				conn = wtconnection.getConnection();
				sb = new StringBuilder();
				sb.append("select m.LOCAL_MODELATTR_ID from RECEIVEDMODELATTR m where SAST_MODELTYPE_ID = '"+sastModelType_US+"' and SAST_MODELATTR_ID = '"+sast_attribute_us+"' and LOCAL_MODELTYPE_ID = '"+localModelType_US+"' ");
				pstmt = conn.prepareStatement(sb.toString());
				set = pstmt.executeQuery();
				if(set.next()) {
					sb = new StringBuilder();
					sb.append("update RECEIVEDMODELATTR set LOCAL_MODELATTR_ID = '"+attribute_us+"',LOCAL_MODELATTR_NAME = '"+attribute_zh+"' where SAST_MODELTYPE_ID = '"+sastModelType_US+"' and SAST_MODELATTR_ID = '"+sast_attribute_us+"' and LOCAL_MODELTYPE_ID = '"+localModelType_US+"'  ");
					pstmt = conn.prepareStatement(sb.toString());
					int num = pstmt.executeUpdate();
					if(num>0) {
						conn.commit();
					}
				}else {
					sb = new StringBuilder();
					sb.append("insert into RECEIVEDMODELATTR (SAST_MODELATTR_ID,SAST_MODELATTR_NAME,LOCAL_MODELATTR_ID,LOCAL_MODELATTR_NAME,SAST_MODELTYPE_ID,LOCAL_MODELTYPE_ID"
							+ ") values (?,?,?,?,?,?)");
					pstmt = conn.prepareStatement(sb.toString());
					pstmt.setString(1, sast_attribute_us);
					pstmt.setString(2, sast_attribute_zh);
					pstmt.setString(3, attribute_us);
					pstmt.setString(4, attribute_zh);
					pstmt.setString(5, sastModelType_US);
					pstmt.setString(6, localModelType_US);
					
					int num = pstmt.executeUpdate();
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
					if(pstmt != null){
						pstmt.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
	}
	
	public static String[] getReceivedModelAttr(String sast_modelAttr,String sast_modelTypeId,String localModelId) {
		String[] localModelAttr = null;
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sb = null;
		
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			sb = new StringBuilder();
			sb.append("select m.LOCAL_MODELATTR_ID,m.LOCAL_MODELATTR_NAME from RECEIVEDMODELATTR m where SAST_MODELTYPE_ID = '"+sast_modelTypeId+"' and SAST_MODELATTR_ID = '"+sast_modelAttr+"' and LOCAL_MODELTYPE_ID = '"+localModelId+"' ");
			pstmt = conn.prepareStatement(sb.toString());
			ResultSet set = pstmt.executeQuery();
			if(set.next()) {
				String localModelAttr_id = set.getString("LOCAL_MODELATTR_ID");
				String localModelAttr_name = set.getString("LOCAL_MODELATTR_NAME");
				localModelAttr = new String[]{localModelAttr_id,localModelAttr_name};
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return localModelAttr;
		
	}
	public static void saveDocSubdivisionInfo(String id, String siteName, String docTypeName, String docTypeInnerName,
			String localDocTypeInnerName) {
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sb = null;
		ResultSet set = null;
		
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			sb = new StringBuilder();
			sb.append("select m.innerId from DOCSUBDIVISION m where innerId = '"+id+"'");
			pstmt = conn.prepareStatement(sb.toString());
			set = pstmt.executeQuery();
			if(set.next()) {
				sb = new StringBuilder();
				sb.append("update DOCSUBDIVISION set comeFromSiteName='"+siteName+"',"
						+ "docTypeName='"+docTypeName+"',"
						+ "docTypeInnerName='"+docTypeInnerName+"',"
						+ "localDocTypeName='"+getLocalModelTypeDisplayName(localDocTypeInnerName)+"',"
						+ "localDocTypeInnerName='"+localDocTypeInnerName+"'"
						+ "where innerId='"+id+"'");
				pstmt = conn.prepareStatement(sb.toString());
				int num = pstmt.executeUpdate();
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
				if(pstmt != null){
					pstmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
	/**
	 * 中心域标准模型下发保存
	 * @param operateType
	 * @param jsonArray
	 */
	public static void saveDataCenterModelType(String operateType,JSONArray jsonArray) {
		WTConnection wtconnection = null;
		Connection conn = null;
		StringBuilder sb = null;
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
            System.out.println("**********设置上下文**********"+context);

			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			
			for(int i=0;i<jsonArray.length();i++) {
				JSONObject jsonobject = jsonArray.getJSONObject(i);
				String iid = (String) jsonobject.get(Based.IID);
				String modelid = (String) jsonobject.get(Based.MODELID);
				String modelname = (String) jsonobject.get(Based.MODELNAME);
				
				sb = new StringBuilder();
				if("ALL".equalsIgnoreCase(operateType)) {
					sb.append("delete from SAST_MODELTYPE_INFO a where a.SAST_MODELID = '"+modelid+"' and SAST_IID = '"+iid+"'");
					PreparedStatement state = conn.prepareStatement(sb.toString());
					int num = state.executeUpdate();
					if(num>0){
						conn.commit();
					}
					state.close();
					sb.delete(0, sb.length());
					
					sb.append("insert into SAST_MODELTYPE_INFO (SAST_IID,SAST_MODELID,SAST_MODELNAME,SAST_TYPE,SYNC_TIME,OPERATE_TYPE) values(?,?,?,?,?,?)");
					PreparedStatement state2 = conn.prepareStatement(sb.toString());
					state2.setString(1, iid);
					state2.setString(2, modelid);
					state2.setString(3, modelname);
					state2.setString(4, "");
					state2.setLong(5, System.currentTimeMillis());
					state2.setString(6, operateType);
					int index = state2.executeUpdate();
					if(index>0) {
						conn.commit();
					}
					state2.close();
					
				}else if("ADD".equalsIgnoreCase(operateType) || operateType == null || operateType.length()<=0) {
					sb.append("insert into SAST_MODELTYPE_INFO (SAST_IID,SAST_MODELID,SAST_MODELNAME,SAST_TYPE,SYNC_TIME,OPERATE_TYPE) values(?,?,?,?,?,?)");
					PreparedStatement state = conn.prepareStatement(sb.toString());
					state.setString(1, iid);
					state.setString(2, modelid);
					state.setString(3, modelname);
					state.setString(4, "");
					state.setLong(5, System.currentTimeMillis());
					state.setString(6, operateType);
					int index = state.executeUpdate();
					if(index>0) {
						conn.commit();
					}
					state.close();
				}else if("DELETE".equalsIgnoreCase(operateType)) {
					sb.append("delete from SAST_MODELTYPE_INFO a where a.SAST_MODELID = '"+modelid+"' and SAST_IID = '"+iid+"'");
					PreparedStatement state = conn.prepareStatement(sb.toString());
					int num = state.executeUpdate();
					if(num>0){
						conn.commit();
					}
					state.close();
				}else if("MODIFY".equalsIgnoreCase(operateType)) {
					sb.append("update SAST_MODELTYPE_INFO set SAST_MODELID = '"+modelid+"', SAST_MODELNAME = '"+modelname+"', SAST_TYPE = '',SYNC_TIME = '"+System.currentTimeMillis()+"',OPERATE_TYPE = '"+operateType+"' "
							+ "where SAST_IID = '"+iid+"'");
					PreparedStatement state = conn.prepareStatement(sb.toString());
					int num = state.executeUpdate();
					if(num>0){
						conn.commit();
					}
					state.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		if(mc!=null){
			mc.unregister();
		}
	}
	}
	/**
	 * 中心域标准模型属性下发保存
	 * @param operateType
	 * @param jsonArray
	 */
	public static void saveDataCenterModelAttr(String operateType,JSONArray jsonArray) {
		WTConnection wtconnection = null;
		Connection conn = null;
		StringBuilder sb = null;
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
            System.out.println("**********设置上下文**********"+context);

			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			
			for(int i=0;i<jsonArray.length();i++) {
				JSONObject jsonobject = jsonArray.getJSONObject(i);
				String iid = (String) jsonobject.get("iid");
				String modeliid = (String) jsonobject.get(Based.MODEL_IID);
				String modelattrid = (String) jsonobject.get("modelattrid");
				String modelattrname = (String) jsonobject.get("modelattrname");
				
				sb = new StringBuilder();
				if("ALL".equalsIgnoreCase(operateType)) {//
					sb.append("delete from SAST_MODELATTR_INFO a where a.SAST_IID = '"+iid+"' and SAST_MODELID = '"+modeliid+"' and SAST_MODELATTRID = '"+modelattrid+"' ");
					PreparedStatement state = conn.prepareStatement(sb.toString());
					int num = state.executeUpdate();
					if(num>0){
						conn.commit();
					}
					state.close();
					sb.delete(0, sb.length());
					sb.append("insert into SAST_MODELATTR_INFO (SAST_IID,SAST_MODELID,SAST_MODELATTRID,SAST_MODELATTRNAME,SYNC_TIME,OPERATE_TYPE) values(?,?,?,?,?,?)");
					PreparedStatement state2 = conn.prepareStatement(sb.toString());
					state2.setString(1, iid);
					state2.setString(2, modeliid);
					state2.setString(3, modelattrid);
					state2.setString(4, modelattrname);
					state2.setLong(5, System.currentTimeMillis());
					state2.setString(6, operateType);
					int index = state2.executeUpdate();
					if(index>0) {
						conn.commit();
					}
					state2.close();
				}else if("ADD".equalsIgnoreCase(operateType) || operateType == null || operateType.length()<=0) {
					sb.append("insert into SAST_MODELATTR_INFO (SAST_IID,SAST_MODELID,SAST_MODELATTRID,SAST_MODELATTRNAME,SYNC_TIME,OPERATE_TYPE) values(?,?,?,?,?,?)");
					PreparedStatement state = conn.prepareStatement(sb.toString());
					state.setString(1, iid);
					state.setString(2, modeliid);
					state.setString(3, modelattrid);
					state.setString(4, modelattrname);
					state.setLong(5, System.currentTimeMillis());
					state.setString(6, operateType);
					int index = state.executeUpdate();
					if(index>0) {
						conn.commit();
					}
					state.close();
				}else if("DELETE".equalsIgnoreCase(operateType)) {
					sb.append("delete from SAST_MODELATTR_INFO a where a.SAST_IID = '"+iid+"' and SAST_MODELID = '"+modeliid+"' and SAST_MODELATTRID = '"+modelattrid+"' ");
					PreparedStatement state = conn.prepareStatement(sb.toString());
					int num = state.executeUpdate();
					if(num>0){
						conn.commit();
					}
					state.close();
				}else if("MODIFY".equalsIgnoreCase(operateType)) {
					sb.append("update SAST_MODELATTR_INFO set SAST_MODELATTRID = '"+modelattrid+"', SAST_MODELATTRNAME = '"+modelattrname+"',SYNC_TIME = '"+System.currentTimeMillis()+"' OPERATE_TYPE ='"+operateType+"' "
							+ "where SAST_IID = '"+iid+"' and SAST_MODELID = '"+modeliid+"'");
					PreparedStatement state = conn.prepareStatement(sb.toString());
					int num = state.executeUpdate();
					if(num>0){
						conn.commit();
					}
					state.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		if(mc!=null){
			mc.unregister();
		}
	}
		
	}
}
