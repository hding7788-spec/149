/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command;

import com.ptc.extend.ixb.CmExpImpSearchHelper;
import ext.casc.util.Tools;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.doc.WTDocumentMasterIdentity;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.EPMDocumentMasterIdentity;
import wt.fc.IdentityHelper;
import wt.log4j.LogR;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartMasterIdentity;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * 类功能：PBOM状态查询接口
 *
 * @author hding
 * @date 2020/6/24
 */

public class PDMSynchDataCommand implements WebServiceCommand, InitializingBean {
	private static final Logger LOGGER = LogR.getLogger(PDMSynchDataCommand.class.getName());
	// 方法标识
	public static final String METHOD_NAME = "synchData";
	private static String SYNCH_DELETE = "DELETE";
	private static String SYNCH_RENAME = "RENAME";
	private static String OBJECTTYPE_WTDOCUMENT = "wt.doc.WTDocument";
	private static String OBJECTTYPE_WTPART = "wt.part.WTPart";
	@Override
	public String execute(String params) {
		String errorMsg = null;

		JSONObject jparams = null;
		try {
			jparams = new JSONObject(params);
		} catch (JSONException e) {
			errorMsg = "参数JSON格式不正确： " + e.getLocalizedMessage();

			LOGGER.error("", e);
		}

		String flag = jparams.getString("flag");
		String masterId = jparams.getString("masterId");
		String objectType = jparams.getString("objectType");
		String number = jparams.getString("number");
		String name = jparams.getString("name");
		String version = jparams.getString("version");
		String iteration = jparams.getString("iteration");
		String cadName = jparams.getString("cadName");
		if(SYNCH_DELETE.equalsIgnoreCase(flag)){

			/*try {
				Iterated obj =CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(Class.forName(objectType),
						number, version, iteration);
				if(obj != null){
					CmWorkflowHelper.changeObjNumberOrDeleteObj(obj, true, new HashSet());
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				errorMsg = "objectType 转换出错";
			}catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				if(e.getLocalizedMessage()!=null){
					errorMsg = "删除数据出错:"+e.getLocalizedMessage();
				}else{
					errorMsg = "删除数据出错!";
				}
			}*/
		}else if(SYNCH_RENAME.equalsIgnoreCase(flag)){
			try {
				Object obj = null;
				if(objectType.contains(OBJECTTYPE_WTPART)){
					obj = CmExpImpSearchHelper.getObjectByMasterIdAndViewName(masterId, Class.forName(objectType),"Design");
				}else{
					obj = CmExpImpSearchHelper.getObjectByMasterId(masterId, Class.forName(objectType));
				}
				if(obj != null){
					changeIdentity(obj, number,name);
				}
			} catch (Exception e) {
				e.printStackTrace();
				if(e.getLocalizedMessage()!=null){
					errorMsg = "重命名数据出错:"+e.getLocalizedMessage();
				}else{
					errorMsg = "重命名数据出错!";
				}
			}
		}

		JSONObject rtnMsgObj = new JSONObject();

		try {
			if(errorMsg!=null&&!"".equals(errorMsg)){
				rtnMsgObj.put("status", "N");
				rtnMsgObj.put("result", errorMsg);
			}else{
				rtnMsgObj.put("status", "Y");
				rtnMsgObj.put("result", errorMsg);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return rtnMsgObj.toString();
	}
	public  void changeIdentity(Object obj, String number,String name) throws WTException, WTPropertyVetoException {
    	if(obj instanceof WTDocument){
    		WTDocument doc = (WTDocument)obj;
    		WTDocumentMaster master = (WTDocumentMaster) doc.getMaster();
        	WTDocumentMasterIdentity idy = (WTDocumentMasterIdentity) master.getIdentificationObject();
    		if(!Tools.isNull(number)){
            	idy.setNumber(number);
    		}
    		if(!Tools.isNull(name)){
        		idy.setName(name);
    		}
    		master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, idy);
    	}else if(obj instanceof WTPart){
    		WTPart part = (WTPart)obj;
    		WTPartMaster master = (WTPartMaster) part.getMaster();
    		WTPartMasterIdentity idy = (WTPartMasterIdentity) master.getIdentificationObject();
    		if(!Tools.isNull(number)){
            	idy.setNumber(number);
    		}
    		if(!Tools.isNull(name)){
        		idy.setName(name);
    		}
    		master = (WTPartMaster) IdentityHelper.service.changeIdentity(master, idy);
    	}else if(obj instanceof EPMDocument){
    		EPMDocument epm = (EPMDocument)obj;
    		EPMDocumentMaster master = (EPMDocumentMaster) epm.getMaster();
        	EPMDocumentMasterIdentity idy = (EPMDocumentMasterIdentity) master.getIdentificationObject();
        	if(!Tools.isNull(number)){
            	idy.setNumber(number);
    		}
    		if(!Tools.isNull(name)){
        		idy.setName(name);
    		}
    		master = (EPMDocumentMaster) IdentityHelper.service.changeIdentity(master, idy);
    	}

	}
	@Override
	public void afterPropertiesSet() throws Exception {
		WebServiceCommandFactory.register(METHOD_NAME, this);
	}
}
