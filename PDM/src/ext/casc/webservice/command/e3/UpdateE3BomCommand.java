/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command.e3;


import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.WTDocumentUtil;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.extend.ixb.CmExpImpSearchHelper;
import ext.casc.constants.Constants;
import ext.casc.importdata.StandardImportService;
import ext.casc.part.CSCPart;
import ext.casc.product.CSCProduct;
import ext.casc.util.*;
import ext.casc.webservice.WSConstants;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import wt.doc.WTDocument;
import wt.fc.PersistenceServerHelper;
import wt.org.WTUser;
import wt.part.*;
import wt.pdmlink.PDMLinkProduct;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class UpdateE3BomCommand implements WebServiceCommand, InitializingBean {
	static Logger LOGGER = Logger.getLogger(UpdateE3BomCommand.class.getName());
	// 方法标识
	public static final String METHOD_NAME = "updateE3Bom";
	@Override
	public String execute(String params) {
		String rtnCode = "E";
		String rtnMsg="";
        JSONObject jrtnObj = new JSONObject();

		JSONObject jparams;
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			jparams = new JSONObject(params);
			String loginUser =jparams.optString("loginUser");
			if(loginUser!=null &&!"".equals(loginUser)){
				WTUser user= CSCPrincipal.getUserByName(loginUser);
				if(user!=null){
					SessionHelper.manager.setPrincipal(user.getName());
				}
			}


			//JSONArray e3ss =jparams.optJSONArray("e3s");
			JSONArray boms =jparams.optJSONArray("bom");
			StringBuilder errorMsg  = new StringBuilder("");
			PDMLinkProduct product  = null;
			for(int i = 0;i<boms.length();i++) {
				JSONObject object = boms.getJSONObject(i);
				String productName = object.optString("productName");
				if (product == null){
					String parentNumber = object.optString("parentNumber");
					if (!Tools.isNull(parentNumber)) {
						WTPart part = (WTPart) CmExpImpSearchHelper.searchLatestWTPartByNumberView(parentNumber, "Design");
						if (part != null) {
							product = (PDMLinkProduct) part.getContainer();
						}
					} else {
						String partNumber = object.optString("partNumber");
						WTPart part = (WTPart) CmExpImpSearchHelper.searchLatestWTPartByNumberView(partNumber, "Design");
						if (part != null) {
							product = (PDMLinkProduct) part.getContainer();
						}
					}
					if (product == null) {
						if (!Tools.isNull(productName)) {
							product = CSCProduct.getPDMLinkProduct(productName);
						}
					}
				}
				if(product == null){
					errorMsg.append(productName+"产品在PDM中不存在");
				}else{
					errorMsg.append(updateBom(object,product));
				}
			}
			/*if(e3ss!=null){
				for(int i = 0;i<e3ss.length();i++){
					JSONObject  object= e3ss.getJSONObject(i);
					errorMsg.append(updateE3Doc(object));
				}
			}*/

			if(errorMsg.length()==0){
				rtnCode="S";
				rtnMsg="更新成功";
			}else{
				rtnCode="N";
				rtnMsg="更新失败："+errorMsg;
			}

		} catch (JSONException e) {
			rtnMsg = "参数JSON格式不正确： " + e.getLocalizedMessage();
			LOGGER.error(rtnMsg, e);
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rtnCode="N";
			rtnMsg="更新失败："+e.getLocalizedMessage();
		}finally{
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
		try {
	           jrtnObj.put(WSConstants.RTN_MSG, rtnMsg);
	           jrtnObj.put(WSConstants.RTN_CODE, rtnCode);
	     } catch (JSONException ex) {
	            LOGGER.error("Error building JSON: ", ex);
	     }

		return jrtnObj.toString();
	}

	private String updateE3Doc(JSONObject object) throws IOException, WTException, PropertyVetoException {
		String docNumber = object.optString("docNumber");
		String docName = object.optString("docName");
		String partNumber = object.optString("CINDEX");
		WTPart part =  CSCPart.getPart(partNumber);
		if(part==null){
			return "对用的图号在PDM中找不到相应的零件";
		}else{
			WTDocument  doc = WTDocumentUtil.getDocumentByNumber(docNumber);
			if(doc==null){
				doc = WTDocumentUtil.createDocument(docNumber, docName, part.getContainer(), "Default/02工艺文件/E3图样", "casc.sast.149.DRAWING_DOC");

			}
			if(doc!=null){
				String fileContent = object.optString("fileContent");
				String fileName = object.optString("fileName");
				if(!Tools.isNull(fileContent)){
					byte[] contents = Base64.decodeBase64(fileContent);
					WTDocumentUtil.setPrimaryForDocument(doc,fileName,contents);
				}else{
					return "E3图样文件创建失败，无相应的文件！";
				}
				if(ReplaceRepUtility.getLinkByPartAndDoc(part, doc)==null) {
					WTPartUtil.createWTPartDescribeLink(part, doc);
				}

			}else{
				return "E3图样文件创建失败！";
			}
		}
		return "";
	}

	private String  updateBom(JSONObject object,PDMLinkProduct product) throws WTException {

		String parentNumber = object.optString("parentNumber");
		String partNumber = object.optString("partNumber");
		String partName = object.optString("partName");
		HashMap<String,String> ibaMap = new HashMap<String,String>();

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("FOLDER", "/Default/02工艺文件/E3Bom");
		map.put("PARTTYPE", "inseparable");
		map.put("DEFAULTTRACECODE", "0");
		map.put("DEFAULTUNIT", Constants.useUnitMap.get("每个"));
		map.put("VIEW", "Design");
		map.put("STATE", "INWORK");
		map.put("ENDITEMIN", "否");

		ibaMap.put("KEYCOMPONENT", object.optString("KEYCOMPONENT"));
		ibaMap.put("REMARK", object.optString("REMARK"));
		ibaMap.put("SETMARK", object.optString("SETMARK"));
		ibaMap.put("CTYPE", "自制件");
		ibaMap.put("PINDEX", object.optString("PINDEX"));
		ibaMap.put("MINDEX", object.optString("MINDEX"));
		ibaMap.put("CINDEX", object.optString("CINDEX"));
		String secret = object.optString("SECRET");
		if(Tools.isNull(secret)){
			secret = "内部";
		}
		ibaMap.put("SECRET", secret);
		ibaMap.put("PHASE_CODE", object.optString("PHASE_CODE"));
		ibaMap.put("DESIGNER", object.optString("DESIGNER"));
		ibaMap.put("ENDITEMIN", object.optString("ENDITEMIN"));
		ibaMap.put("COMPANY", object.optString("COMPANY"));

		WTPart part = (WTPart) CmExpImpSearchHelper.searchLatestWTPartByNumberView(partNumber,"Design");
		if(part==null){
			 part = CSCPart.createPart(partNumber,partName,map,ibaMap,product);
			try {
				StandardImportService.setIterationId(part, "0");
				PersistenceServerHelper.manager.update(part);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			if("0".equals(part.getIterationIdentifier().getValue())){
				Set<Map.Entry<String, String>> set = ibaMap.entrySet();
				for(Map.Entry<String, String> entry :set){
					IBAHelper.setIBAStringValue(part,entry.getKey(),entry.getValue());
				}
			}
		}

		if(!Tools.isNull(parentNumber)&&part!=null){
			WTPart parent =  CSCPart.getPart(parentNumber);
			if(parent!=null){
				WTPartUsageLink oldlink = WTPartUtil.getWTPartUsageLink(parent,(WTPartMaster)part.getMaster());
				if(oldlink==null){
					WTPartUsageLink link = WTPartUsageLink.newWTPartUsageLink(parent, (WTPartMaster)part.getMaster());
					String mount = object.optString("amount");
					String unit = object.optString("unit");
					if(Tools.isNull(mount)){
						mount="1";
					}

					if(Tools.isNull(unit)){
						unit="每个";
					}
					double quantity =Double.parseDouble(mount);
					Quantity quantity2 = new Quantity();
					quantity2.setAmount(quantity);

					//设置单位
					String unitKey = Constants.useUnitMap.get(unit);
					if(unitKey != null && !"".equals(unitKey)) {
						QuantityUnit quantityUnit = QuantityUnit.toQuantityUnit(unitKey);
						quantity2.setUnit(quantityUnit);
					}
					link.setQuantity(quantity2);
					PersistenceServerHelper.manager.insert(link);
				}

			}
		}

		return "";
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		WebServiceCommandFactory.register(METHOD_NAME, this);
	}

}
