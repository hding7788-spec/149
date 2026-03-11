package ext.sast.center.synch;

import java.io.File;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bjsasc.avidm.mq.fileserver.FSUtil;
import com.bjsasc.avidm.mq.message.Based;

import ext.sast.center.ixb.util.Deserialize;
import ext.sast.center.util.PropertiesUtil;
import wt.admin.AdministrativeDomainHelper;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.session.SessionAuthenticator;

//上传：ext.sast.center.synch.TestDemo upload <xxxxxx.expimp>
//下载：ext.sast.center.synch.TestDemo download <xxxxxx.expimp> <xxxxxx> <key> <ivkey>
//导入：ext.sast.center.synch.TestDemo importData pbonumber <xxxxxx> msgType <xxxxxx.expimp> <xxxxxx>
public class TestDemo implements RemoteAccess{
	private static String KEY = "";
	private static String IVKEY = "";
	static JSONObject F_JSON = null;
	
	public static void main(String[] args) {
		
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		rms.setUserName("wcadmin");
		rms.setPassword("wcadmin");
		try {
			String methodName = args[0];
			if("download".equalsIgnoreCase(methodName)) {
				rms.invoke("download", TestDemo.class.getName(), null, new Class[] { String.class,String.class },
						new Object[] { args[1],args[2],args[3],args[4]});
			}else if("upload".equalsIgnoreCase(methodName)) {
				rms.invoke("upload", TestDemo.class.getName(), null, new Class[] { String.class },
						new Object[] { args[1]});
			}else if("importData".equalsIgnoreCase(methodName)){
				rms.invoke("importData", TestDemo.class.getName(), null, new Class[] { String.class },
						new Object[] { args[1],args[2], });
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public static void download(String file_name,String file_id,String key,String ivkey) {
		String localPath = PropertiesUtil.getTempPath() + File.separator + "IXBExpImp"+ File.separator +"DataCenterReceive"+ File.separator + file_name;
		String url = ext.sast.center.synch.MQConstants.DC_DOWNLOAD;
		//localFile 保存到本地的文件，如：d:/download.zip
		//endpoint	下载文件的REST地址，如：http://10.112.1.213:8081/avidm/rest/dc/attach/download
		//file_id	文件的唯一标识
		//key		AES加密的key
		//ivkey		AES解密的ivkey
		System.out.println("key @@@@ = "+key);
		if(key == null || key.length()<=0) {
			key = KEY;
		}
		System.out.println("ivkey @@@@ = "+ivkey);
		if(ivkey == null || ivkey.length()<=0) {
			ivkey = IVKEY;
		}
		FSUtil.download(localPath, url, file_id, key, ivkey);
	}
	public static void upload(String file_name) {
		String localPath = PropertiesUtil.getTempPath() + File.separator + "IXBExpImp"+ File.separator + file_name;
		String url = ext.sast.center.synch.MQConstants.DC_UPLOAD;
		JSONObject json = FSUtil.upload(localPath, url);
		F_JSON = json;
		System.out.println("json @@@@ = "+json);
		KEY = json.getString(Based.KEY);
		System.out.println("key @@@@ = "+KEY);
		IVKEY = json.getString(Based.IVKEY);
		System.out.println("ivkey @@@@ = "+IVKEY);
	}
	
	@SuppressWarnings("unchecked")
	public static String importData(String pbonumber,String msgType,String file_name,String file_id) {
		String result = "";
		String context = "Unknown";
		try {
            MethodContext mc = MethodContext.getContext(Thread.currentThread());
            if (mc == null)
                mc = new MethodContext(null, null);
            if (mc.getAuthentication() == null) {
                SessionAuthenticator sa = new SessionAuthenticator();
                mc.setAuthentication(sa.setUserName(AdministrativeDomainHelper.ADMINISTRATOR_NAME));
            }
            context = mc.getId().toString();
            System.out.println("**********设置上下文**********"+context);
        } catch (Throwable t) {
            t.printStackTrace();
            System.err.println("Error create service session context.");
        }
		try {
			JSONObject msg = new JSONObject();
			msg.put(Based.MSG_ID,pbonumber);
			
			JSONObject j_file = null;
			if(null == F_JSON ) {
				j_file = new JSONObject();
				j_file.put(Based.FILE_ID, file_id);
				j_file.put(Based.FILE_NAME, file_name);
				String size = "2000KB";
				j_file.put(Based.FILE_SIZE, size);
				j_file.put(Based.KEY, KEY);
				j_file.put(Based.IVKEY, IVKEY);
			}else {
				j_file = F_JSON;
			}
			System.out.println("j_file @@@@ = "+ j_file);
			msg.put(Based.J_FILE,j_file);
			if(msgType.contains("会签")) {
				msg.put(Based.MSG_TYPE,Based.DC_REQUEST_SIGN_DC);
			}else if(msgType.contains("发放")) {
				msg.put(Based.MSG_TYPE,Based.DC_REQUEST_DISTRIBUTE_DC);
			}else if(msgType.contains("预审")) {
				msg.put(Based.MSG_TYPE,Based.DC_RESPONSE_SHARE_DC);
			}
			msg.put(Based.MSG_DESCRIPTION,msgType);
			msg.put(Based.MSG_CREATED_TIME,System.currentTimeMillis());
			//原始发起单位的系统版本
			msg.put(Based.SYS_VERSION_INITIAL,Based.SYS_VERSION_A4);
			//当前发起请求的系统版本
			msg.put(Based.SYS_VERSION_REQUEST,Based.SYS_VERSION_A4);
			//原始发起单位信息
			JSONObject j_src_site = new JSONObject();
			j_src_site.put(Based.IID, "no8");
			j_src_site.put(Based.ID, "no8");
			j_src_site.put(Based.NAME, "no8");
			msg.put(Based.J_SRC_SITE,j_src_site);
			
			JSONObject j_product = new JSONObject();
			j_product.put(Based.IID,"APK_TEST");
			j_product.put(Based.ID, "APK_TEST");
			j_product.put(Based.NAME, "APK_TEST");
			msg.put(Based.J_PRODUCT,j_product);
			
			JSONObject j_creator = new JSONObject();
			j_creator.put(Based.IID,"149");
			j_creator.put(Based.ID, "149");
			j_creator.put(Based.NAME,"149");
			msg.put(Based.J_CREATOR, j_creator);
			
			JSONArray ja_dst_sites = new JSONArray();
			JSONObject sendUnit = new JSONObject();
			sendUnit.put(Based.IID, "pdmtest.149.sast.casc");
			sendUnit.put(Based.ID, "pdmtest.149.sast.casc");
			sendUnit.put(Based.NAME, "pdmtest.149.sast.casc");
			ja_dst_sites.put(sendUnit);
			msg.put(Based.JA_DST_SITES,ja_dst_sites);
			
			JSONObject ja_receivers = new JSONObject();
			ja_receivers.put(Based.SITE_ID, "no8");
			ja_receivers.put(Based.IID, "149");
			ja_receivers.put(Based.ID, "149");
			ja_receivers.put(Based.NAME, "149");
			msg.put(Based.JA_RECEIVERS,ja_receivers);
			
			
			JSONArray ja_objects_request = new JSONArray();
			msg.put(Based.JA_OBJECTS_REQUEST,ja_objects_request);
			msg.put(Based.DC_REQUEST_DISTRIBUTE_DC,ja_objects_request);
			
			@SuppressWarnings("rawtypes")
			HashMap inputparams = new HashMap();
			inputparams.put("reviewType", "ApproveOrder");//ApproveOrder,DisOrder,ECN,approvedCompleted
			inputparams.put("workflowType", "工艺会签");//技术会签、工艺会签、正式发放
			inputparams.put("orderIID", "444");
			inputparams.put("activityOid", "3333");//800 OID
			inputparams.put("activityTemplateID", "111");
			inputparams.put("ReceiveStateID", "adsfsdf");				
			inputparams.put("activityName","跨域工艺会签");
			inputparams.put("sendFrom", "no8");
			String inputparamsStr = Deserialize.serializeMap(inputparams);
			System.out.println("inputparamsStr @@@@ = "+inputparamsStr);
			
			msg.put("soapparams",inputparamsStr);
			result = MQDataReceiveHelper.receiveData(msg);
			System.out.println("result @@@@@@ = " + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result ;
	}
}	

