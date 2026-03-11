package ext.sast.center.productModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.bjsasc.avidm.mq.log.LogMessage;
import com.bjsasc.avidm.mq.message.Based;
import com.bjsasc.avidm.mq.message.Message;
import com.bjsasc.avidm.mq.message.MetaMessage;
import com.bjsasc.avidm.mq.message.win10.Win10ProdMappingRespDcMessage;
import com.bjsasc.avidm.mq.sender.Sender;
import com.bjsasc.avidm.mq.util.TUUID;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.sast.center.productModel.util.SyncProductHelper;
import ext.sast.center.synch.MQConstants;
import wt.method.MethodContext;
import wt.org.WTUser;
import wt.pom.WTConnection;
import wt.session.SessionHelper;
import wt.util.WTException;

/**
 *
 * @author Wang-ya-qi
 * 保存型号映射信息
 *
 */
public class SaveSyncProductInfoProcessor extends DefaultObjectFormProcessor{
	private static final Logger log = Logger.getLogger(SaveSyncProductInfoProcessor.class);
	@Override
	public FormResult doOperation(NmCommandBean commandBean,List<ObjectBean> objectbeans) throws WTException {
		FormResult formResult = new FormResult();
		String sastModelStr = "";
		String sastModelNameStr = "";
		String remarks = "";
		JSONArray jsonArray = null;
		Sender sender = null;
		MetaMessage metaMsg = null;
		String id = "";
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet set = null;
		String sastProductIID = "";
		try {
			id = TUUID.getUUID();

			sender = Sender.getInstance();
			metaMsg = new MetaMessage();
			id = TUUID.getUUID();
			WTUser user = (WTUser) SessionHelper.getPrincipal();
			metaMsg.setMsgId(id);
			metaMsg.setOrderID(id);
			metaMsg.setOrderName("型号映射");
			metaMsg.setSrcSiteIID(MQConstants.SITEIID_149);
			metaMsg.setSrcSiteName(MQConstants.SITENAME_149);
			metaMsg.setMsgType(Based.META_MSG_TYPE_PRODMAPPING);
			metaMsg.setUserIID(user.getPersistInfo().getObjectIdentifier().getId()+"");//发起人
			metaMsg.setUserID(user.getName());
			metaMsg.setUserName(user.getFullName());
			metaMsg.setdDstSiteInfo(MQConstants.DC_SITENAME);
			JSONArray dstSites = new JSONArray();
			dstSites.put(MQConstants.DC_SITEIID);
			metaMsg.setdDstSites(dstSites);

			String productOid = commandBean.getTextParameter("productOid");
			String productName = commandBean.getTextParameter("productName");
			String selectedData = (String) commandBean.getText().get("selectedData");
			if(productOid == null || productOid.length()<=0){

				FeedbackMessage message = new FeedbackMessage(FeedbackType.FAILURE, null,"失败", null, "获取本地型号失败");
				formResult.addFeedbackMessage(message);
				formResult.setNextAction(FormResultAction.JAVASCRIPT);
				formResult.setJavascript("window.opener.location.reload();window.close();");
				return formResult;
			}

			jsonArray = new JSONArray();
			if(selectedData.length()<=0) {
				StringBuilder sb = new StringBuilder();
				sb.append("select a.SAST_IID,a.SAST_PRODUCT_IID,a.SAST_PRODUCT_ID,a.SAST_PRODUCT_NAME from SAST_PRODUCT_INFO a　where　a.SAST_PRODUCT_ID = (select m.SAST_PRODUCT from PRODUCTINFO m where m.LOCALHOST_PRODUCT = '"+productOid+"' )");

				MethodContext methodcontext = MethodContext.getContext();
				wtconnection = (WTConnection) methodcontext.getConnection();
				conn = wtconnection.getConnection();
				pstmt = conn.prepareStatement(sb.toString());
				set = pstmt.executeQuery(sb.toString());
				while(set.next()) {
					String iid = set.getString("SAST_IID");
					String sast_productIID = set.getString("SAST_PRODUCT_IID");
					String sast_productID = set.getString("SAST_PRODUCT_ID");
					String sast_productName = set.getString("SAST_PRODUCT_NAME");
					sastProductIID = sast_productIID;
					JSONObject json = new JSONObject();
					json.put(Based.STD_PRODUCT_IID, iid);
					json.put(Based.PRODUCT_IID, sast_productIID);
					json.put(Based.PRODUCT_ID, sast_productID);
					json.put(Based.PRODUCT_NAME, sast_productName);
					json.put(Based.IID, productOid);
					json.put(Based.CS_SITE_IID, MQConstants.SITEIID_149);
					json.put(Based.CS_PRODUCT_IID, productOid);
					json.put(Based.CS_PRODUCT_ID, productOid);
					json.put(Based.CS_PRODUCT_NAME,productName);
					jsonArray.put(json);
				}

				LogMessage logMsg = new LogMessage(id, MQConstants.SITENAME_149, MQConstants.SITEIID_149, MQConstants.SITENAME_149+"准备发起型号映射删除请求");
				sender.addLog(logMsg);
			}else {
				String[] values = selectedData.split(";");
				for(int i=0;i<values.length;i++){
					String rowValues = values[i];
					String[] row = rowValues.split(",");
					if(i==0){
						sastModelStr = row[0];
						sastModelNameStr = row[1];
					}else{
						sastModelStr = sastModelStr + "," + row[0];
						sastModelNameStr = sastModelNameStr + "," + row[1];
					}
					String sastProductId = row[0];
					JSONObject json = ext.sast.center.util.ProductConvertUtil.getStandardProductInfoByID(sastProductId);
					sastProductIID = json.getString(Based.PRODUCT_IID);

					json.put(Based.STD_PRODUCT_IID, json.getString(Based.IID));
					json.put(Based.IID, productOid);
					json.put(Based.CS_SITE_IID, MQConstants.SITEIID_149);
					json.put(Based.CS_PRODUCT_IID, productOid);
					json.put(Based.CS_PRODUCT_ID, productOid);
					json.put(Based.CS_PRODUCT_NAME,productName);
					jsonArray.put(json);
				}

			}
			LogMessage logMsg = new LogMessage(id, MQConstants.SITENAME_149, MQConstants.SITEIID_149, MQConstants.SITENAME_149+"准备发起型号映射新增请求");
			sender.addLog(logMsg);

			String result = SyncProductHelper.saveSastProductInfo(productOid,productName,sastModelStr,sastModelNameStr,remarks,sastProductIID);

			try {

				Message msg = new Win10ProdMappingRespDcMessage();
				msg.put(Based.MSG_ID, id);
				msg.put(Based.MSG_TYPE, Based.DC_RESPONSE_SYNPRODUCT_RECEIVER);
				msg.put(Based.JA_PRODUCTMAPPINGS_REQUEST, jsonArray);
				msg.put(Based.OPERATE_TYPE, result);
				sender = Sender.getInstance();

				logMsg = new LogMessage(id, MQConstants.SITENAME_149, MQConstants.SITEIID_149, MQConstants.SITENAME_149+"成功发起型号映射请求");
				sender.addLog(logMsg);

				System.out.println("型号映射同步到中心域 ###### "+msg);
				sender.send(msg);


			} catch (Exception e) {
				log.info("型号映射同步到中心域出错");
				metaMsg.setMsgStatus(Based.MSG_STATUS_FAILED);
				sender.updateMetaMessage(metaMsg);
			}

			FeedbackMessage message = new FeedbackMessage(FeedbackType.SUCCESS, null,"成功", null, "映射型号成功");
			formResult.addFeedbackMessage(message);
			formResult.setNextAction(FormResultAction.JAVASCRIPT);
			formResult.setJavascript("window.opener.location.reload();window.close();");
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
		return formResult;
	}
}
