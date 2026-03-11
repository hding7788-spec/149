package ext.sast.center.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bjsasc.avidm.mq.message.Based;

import ext.sast.center.bean.FileBean;
import ext.sast.center.bean.GroupBean;
import ext.sast.center.bean.JaDispatchBean;
import ext.sast.center.bean.JaObjectBean;
import ext.sast.center.bean.JaTaskBean;
import ext.sast.center.bean.ProductBean;
import ext.sast.center.bean.SiteBean;
import ext.sast.center.bean.UserBean;
import ext.sast.center.bean.UserGroupLinkBean;
import ext.sast.center.bean.message.BusinessDataMessageBean;
import ext.sast.center.bean.message.SiteMessageBean;

/**
 * @ Author     ：LB.
 * @ Date       ：Created in 2019/3/11
 * @ Description：
 * @ Modified By：
 */
public class JsonConvertUtil implements Based {

    /**
     * 同步用户时调用
     * 对应接口规范中  ja_users_response 对象
     * @param userBeanList
     * @return
     * @throws JSONException
     */
    public static JSONArray convertUserListToJson(List<UserBean> userBeanList) throws JSONException {
        JSONArray userJsonArray = new JSONArray();
        JSONObject userJsonObject;
        for (UserBean userBean : userBeanList) {
            userJsonObject = new JSONObject();
            userJsonObject.put(Based.IID, userBean.getIid());
            userJsonObject.put(Based.ID, userBean.getId());
            userJsonObject.put(Based.NAME, userBean.getName());
            userJsonObject.put(Based.SECLEVEL, "10");
            userJsonObject.put(Based.EMAIL, userBean.getEmail());
            if(userBean.getTelephone() == null || "".equals(userBean.getTelephone())){
                userJsonObject.put(Based.TELEPHONE, "0");
            }else{
                userJsonObject.put(Based.TELEPHONE, userBean.getTelephone());
            }
            userJsonArray.put(userJsonObject);
        }
        return userJsonArray;
    }

    /**
     * 同步组时调用
     * 对应接口规范中  ja_divs_response 对象
     * @param groupBeanList
     * @return
     * @throws JSONException
     */
    public static JSONArray convertGroupToJson(List<GroupBean> groupBeanList) throws JSONException {
        JSONArray groupJsonArray = new JSONArray();
        JSONObject groupJsonObject;
        for (GroupBean groupBean : groupBeanList) {
            groupJsonObject = new JSONObject();
            groupJsonObject.put(Based.IID, groupBean.getIid());
            groupJsonObject.put(Based.ID, groupBean.getId());
            groupJsonObject.put(Based.NAME, groupBean.getName());
            groupJsonObject.put(Based.PARENT_IID, groupBean.getParent_iid());
            groupJsonArray.put(groupJsonObject);
        }
        return groupJsonArray;
    }

    /**
     * 同步用户与组关联时调用
     * @param userGroupLinkBeanList
     * @return
     * @throws JSONException
     */
    public static JSONArray convertUserGroupLinkToJson(List<UserGroupLinkBean> userGroupLinkBeanList) throws JSONException {
        JSONArray userGroupLinkJsonArray = new JSONArray();
        JSONObject userGroupLinkJsonObject;
        for (UserGroupLinkBean userGroupLinkBean : userGroupLinkBeanList) {
            userGroupLinkJsonObject = new JSONObject();
            userGroupLinkJsonObject.put(Based.USER_IID, userGroupLinkBean.getUser_iid());
            userGroupLinkJsonObject.put(Based.DIV_IID, userGroupLinkBean.getDiv_iid());
            userGroupLinkJsonArray.put(userGroupLinkJsonObject);
        }
        return userGroupLinkJsonArray;
    }

    /**
     * JSONObject--->>>JaObjectBean
     * @param jsonObject
     * @return
     */
    public static JaObjectBean convertToJaObjectBean(JSONObject jsonObject){

        String objectOid = jsonObject.getString(Based.OBJECT_OID);
        String masterIid = jsonObject.getString(Based.OBJECT_MASTER_IID);
        String objectId = jsonObject.getString(Based.OBJECT_ID);
        String objectName = jsonObject.getString(Based.OBJECT_NAME);
        String objectState = jsonObject.getString(Based.OBJECT_STATE);
        String objectVersion = jsonObject.getString(Based.OBJECT_VERSION);
        String objectClassName = jsonObject.getString(Based.OBJECT_CLASSNAME);
        String objectType = jsonObject.getString(Based.OBJECT_TYPE);

        JaObjectBean jaObjectBean = new JaObjectBean();
        jaObjectBean.setObject_oid(objectOid);
        jaObjectBean.setMaster_iid(masterIid);
        jaObjectBean.setObject_id(objectId);
        jaObjectBean.setObject_name(objectName);
        jaObjectBean.setObject_state(objectState);
        jaObjectBean.setObject_version(objectVersion);
        jaObjectBean.setClassname(objectClassName);
        jaObjectBean.setObject_type(objectType);

        return jaObjectBean;
    }

    public static List<JaObjectBean> convertToJaObjectList(JSONArray jsonArray){
        List<JaObjectBean> jaObjectBeanList = new ArrayList<JaObjectBean>();
        JaObjectBean jaObjectBean;
        JSONObject jsonObject;
        for (int i = 0; i < jsonArray.length(); i++) {
            jsonObject = jsonArray.getJSONObject(i);
            jaObjectBean = convertToJaObjectBean(jsonObject);
            jaObjectBeanList.add(jaObjectBean);
        }
        return jaObjectBeanList;
    }

    /**
     * JSONObject--->>>JaTaskBean
     * @param jsonObject
     * @return
     */
    public static JaTaskBean convertToJaTaskBean(JSONObject jsonObject){

        String taskIid = jsonObject.getString(Based.TASK_IID);
        String taskName = jsonObject.getString(Based.TASK_NAME);
        String taskUserIid = jsonObject.getString(Based.TASK_USER_IID);
        String taskUserId = jsonObject.getString(Based.TASK_USER_ID);
        String taskUserName = jsonObject.getString(Based.TASK_USER_NAME);
        String taskState = jsonObject.getString(Based.TASK_STATE);
        String taskParentIid = jsonObject.getString(Based.TASK_PARENT_IID);
        long taskCreateTime = jsonObject.getLong(Based.TASK_CREATE_TIME);

        JaTaskBean jaTaskBean = new JaTaskBean();
        jaTaskBean.setTask_iid(taskIid);
        jaTaskBean.setTask_name(taskName);
        jaTaskBean.setTask_user_iid(taskUserIid);
        jaTaskBean.setTask_user_id(taskUserId);
        jaTaskBean.setTask_user_name(taskUserName);
        jaTaskBean.setTask_state(taskState);
        jaTaskBean.setTask_parent_iid(taskParentIid);
        jaTaskBean.setTask_create_time(taskCreateTime);

        return jaTaskBean;
    }

    /**
     * JSONObject--->>>FileBean
     * @param jsonObject
     * @return
     */
    public static FileBean convertToFileBean(JSONObject jsonObject){
        String fileId = jsonObject.getString(Based.FILE_ID);
        String fileName = jsonObject.getString(Based.FILE_NAME);
        String fileSize = jsonObject.getString(Based.FILE_SIZE);
        String key = jsonObject.getString(Based.KEY);
        String ivkey = jsonObject.getString(Based.IVKEY);

        FileBean fileBean = new FileBean();
        fileBean.setFile_id(fileId);
        fileBean.setFile_name(fileName);
        fileBean.setFile_size(fileSize);
        fileBean.setKey(key);
        fileBean.setIvkey(ivkey);

        return fileBean;
    }

    /**
     * JSONObject--->>>ProductBean
     * @param jsonObject
     * @return
     */
    public static ProductBean convertToProductBean(JSONObject jsonObject){
        String iid = jsonObject.getString(Based.IID);
        String id = jsonObject.getString(Based.ID);
        String name = jsonObject.getString(Based.NAME);

        ProductBean productBean = new ProductBean();
        productBean.setIid(iid);
        productBean.setId(id);
        productBean.setName(name);

        return productBean;
    }

    /**
     * JSONObject--->>>UserBean
     * @param jsonObject
     * @return
     */
    public static UserBean convertToUserBean(JSONObject jsonObject){
        String iid = jsonObject.getString(Based.IID);
        String id = jsonObject.getString(Based.ID);
        String name = jsonObject.getString(Based.NAME);
        String secLevel = jsonObject.getString(Based.SECLEVEL);
        String telephone = jsonObject.getString(Based.TELEPHONE);
        String email = jsonObject.getString(Based.EMAIL);
        String domainIid = jsonObject.getString(Based.SITE_IID);

        UserBean userBean = new UserBean();
        userBean.setIid(iid);
        userBean.setId(id);
        userBean.setName(name);
        userBean.setSeclevel(secLevel);
        userBean.setTelephone(telephone);
        userBean.setEmail(email);
        userBean.setDomain_iid(domainIid);
        return userBean;
    }

    /**
     * JSONArray--->>>List<UserBean>
     * @param jsonArray
     * @return
     */
    public static List<UserBean> convertToUserBeanList(JSONArray jsonArray){
        List<UserBean> userBeanList = new ArrayList<UserBean>();
        UserBean userBean;
        JSONObject jsonObject;
        for (int i = 0; i < jsonArray.length(); i++) {
            jsonObject = jsonArray.getJSONObject(i);
            userBean = convertToUserBean(jsonObject);
            userBeanList.add(userBean);
        }
        return userBeanList;
    }

    /**
     * JSONObject--->>>SiteBean
     * @param jsonObject
     * @return
     */
    public static SiteBean convertToSiteBean(JSONObject jsonObject){
        String iid = jsonObject.getString(Based.IID);
        String id = jsonObject.getString(Based.ID);
        String name = jsonObject.getString(Based.NAME);

        SiteBean siteBean = new SiteBean();
        siteBean.setIid(iid);
        siteBean.setId(id);
        siteBean.setName(name);

        return siteBean;
    }

    /**
     * JSONArray--->>>List<SiteBean>
     * @param jsonArray
     * @return
     */
    public static List<SiteBean> convertToSiteBeanList(JSONArray jsonArray){
        List<SiteBean> siteBeanList = new ArrayList<SiteBean>();
        SiteBean siteBean;
        JSONObject jsonObject;
        for (int i = 0; i < jsonArray.length(); i++) {
            jsonObject = jsonArray.getJSONObject(i);
            siteBean = convertToSiteBean(jsonObject);
            siteBeanList.add(siteBean);
        }
        return siteBeanList;
    }

    /**
     * JSONObject--->>>JaDispatchBean
     * @param jsonObject
     * @return
     */
    public static JaDispatchBean convertToJaDispatchBean(JSONObject jsonObject){
        //jaDispatchBean方案中未定义
        JaDispatchBean jaDispatchBean = new JaDispatchBean();

        return jaDispatchBean;
    }

    /**
     * JSONArray--->>>List<JaDispatchBean>
     * @param jsonArray
     * @return
     */
    public static List<JaDispatchBean> convertToJaDispatchBeanList(JSONArray jsonArray){
        List<JaDispatchBean> jaDispatchBeanList = new ArrayList<JaDispatchBean>();
        JaDispatchBean jaDispatchBean;
        JSONObject jsonObject;
        for (int i = 0; i < jsonArray.length(); i++) {
            jsonObject = jsonArray.getJSONObject(i);
            jaDispatchBean = convertToJaDispatchBean(jsonObject);
            jaDispatchBeanList.add(jaDispatchBean);
        }
        return jaDispatchBeanList;
    }

    /**
     * JSONObject--->>>SiteMessageBean
     * @param jsonObject
     * @return
     */
    public static SiteMessageBean convertJSONObjectToSiteMessageBean(JSONObject jsonObject){

        String msg_id = jsonObject.getString(Based.MSG_ID);
        String msg_type = jsonObject.getString(Based.MSG_TYPE);
        //String msg_description = jsonObject.getString(Based.MSG_DESCRIPTION);
        long msg_created_time = jsonObject.getLong(Based.MSG_CREATED_TIME);
        String sys_version_request = jsonObject.getString(Based.SYS_VERSION_REQUEST);
        //String operate_type = jsonObject.getString(Based.OPERATE_TYPE);
        JSONObject j_siteinfo_request = jsonObject.getJSONObject(Based.J_SITEINFO_REQUEST);
        SiteBean siteBean = convertJSONObjectToSiteBean(j_siteinfo_request);

        SiteMessageBean siteMessageBean = new SiteMessageBean();
        siteMessageBean.setMsg_id(msg_id);
        siteMessageBean.setMsg_type(msg_type);
        //siteMessageBean.setMsg_description(msg_description);
        siteMessageBean.setMsg_created_time(msg_created_time);
        siteMessageBean.setSys_version_request(sys_version_request);
        //siteMessageBean.setOperate_type(operate_type);
        siteMessageBean.setJ_siteinfo_response(siteBean);

        return siteMessageBean;
    }

    /**
     * JSONArray--->>>List<SiteBean>
     * @param jsonArray
     * @return
     */
    public static List<SiteBean> convertJSONObjectToSiteBeanList(JSONArray jsonArray){
        List<SiteBean> siteBeanList = new ArrayList<SiteBean>();
        SiteBean siteBean;
        JSONObject jsonObject;
        for (int i = 0; i < jsonArray.length(); i++) {
            jsonObject = jsonArray.getJSONObject(i);
            siteBean = convertToSiteBean(jsonObject);
            siteBeanList.add(siteBean);
        }
        return siteBeanList;
    }
    /**
     * JSONObject--->>>SiteBean
     * @param jsonObject
     * @return
     */
    public static SiteBean convertJSONObjectToSiteBean(JSONObject jsonObject){
        String iid = jsonObject.getString(Based.IID);
        String id = jsonObject.getString(Based.ID);
        String name = jsonObject.getString(Based.NAME);
        String ip = jsonObject.getString(Based.IP);
        String port = jsonObject.getString(Based.PORT);
        String version = jsonObject.getString(Based.VERSION);
        String academy_id = jsonObject.getString(Based.ACADEMY_ID);
        String academy_name = jsonObject.getString(Based.ACADEMY_NAME);

        SiteBean siteBean = new SiteBean();
        siteBean.setIid(iid);
        siteBean.setId(id);
        siteBean.setName(name);
        siteBean.setIp(ip);
        siteBean.setPort(port);
        siteBean.setVersion(version);
        siteBean.setAcademy_id(academy_id);
        siteBean.setAcademy_name(academy_name);

        return siteBean;
    }

    /**
     * 解析处理预审、发放、会签发送来业务的数据
     * @param jsonObject
     * @return
     */
    public static BusinessDataMessageBean convertJSONObjectToBusinessDataMessageBean(JSONObject jsonObject){

        String msgId = jsonObject.getString(Based.MSG_ID);//消息的唯一标识
        String msgType  = jsonObject.getString(Based.MSG_TYPE);//消息类型，不能为空且为协议约定类型
        String msgDes = jsonObject.getString(Based.MSG_DESCRIPTION);//消息描述，可不填
        long createTime = jsonObject.getLong(Based.MSG_CREATED_TIME);//消息创建时间
        String sysVersionInitial = jsonObject.getString(Based.SYS_VERSION_INITIAL);//原始发起单位的系统版本
        String sysVersionRequest = jsonObject.getString(Based.SYS_VERSION_REQUEST);//当前发起请求的系统版本
        JSONObject jSrcSite = jsonObject.getJSONObject(Based.J_SRC_SITE);//原始发起单位信息
        JSONObject jCreator = jsonObject.getJSONObject(Based.J_CREATOR);//发起人信息
        JSONObject jFile = jsonObject.getJSONObject(Based.J_FILE);//附件信息
        JSONObject jProduct = jsonObject.getJSONObject(Based.J_PRODUCT);//发起单据型号信息
        JSONArray jaDstSites = jsonObject.getJSONArray(Based.JA_DST_SITES);//接收单位信息
        JSONArray jaReceivers = jsonObject.getJSONArray(Based.JA_RECEIVERS);//接收人信息
        JSONArray jaObjectsRequest = jsonObject.getJSONArray(Based.JA_OBJECTS_REQUEST);//发起请求的对象信息
        JSONArray jaDispatchsRequest = jsonObject.getJSONArray(Based.DC_RESPONSE_DISTRIBUTE_RECEIVER);//发起请求的发放信息

        SiteBean srcSite = JsonConvertUtil.convertToSiteBean(jSrcSite);
        UserBean creator = JsonConvertUtil.convertToUserBean(jCreator);
        FileBean file = JsonConvertUtil.convertToFileBean(jFile);
        ProductBean productBean = JsonConvertUtil.convertToProductBean(jProduct);
        List<SiteBean> dstSites = JsonConvertUtil.convertToSiteBeanList(jaDstSites);
        List<UserBean> receivers = JsonConvertUtil.convertToUserBeanList(jaReceivers);
        List<JaObjectBean> objectsRequest = JsonConvertUtil.convertToJaObjectList(jaObjectsRequest);
        List<JaDispatchBean> dispatchsRequest = JsonConvertUtil.convertToJaDispatchBeanList(jaDispatchsRequest);

        BusinessDataMessageBean businessDataMessageBean = new BusinessDataMessageBean();
        businessDataMessageBean.setMsg_id(msgId);
        businessDataMessageBean.setMsg_type(msgType);
        businessDataMessageBean.setMsg_description(msgDes);
        businessDataMessageBean.setMsg_created_time(createTime);
        businessDataMessageBean.setSys_version_initial(sysVersionInitial);
        businessDataMessageBean.setSys_version_request(sysVersionRequest);
        businessDataMessageBean.setJ_src_site(srcSite);
        businessDataMessageBean.setJ_creator(creator);
        businessDataMessageBean.setJ_file(file);
        businessDataMessageBean.setJ_product(productBean);
        businessDataMessageBean.setJa_dst_sites(dstSites);
        businessDataMessageBean.setJa_receivers(receivers);
        businessDataMessageBean.setJa_objects_request(objectsRequest);
        businessDataMessageBean.setJa_dispatchs_request(dispatchsRequest);

        return businessDataMessageBean;
    }
    // public static String PERSISTENTPATH = "E:\\04_Glaway\\01_Program\\805\\中心域改造\\22";
   public static final String PERSISTENTPATH = PropertiesUtil.getWTHome()+File.separator+"dataCenterJson";

    public static final String DcDistributeRequestHandler ="DcDistributeRequestHandler";
    public static final String DcShareRequestHandler ="DcShareRequestHandler";
    public static final String DcSignRequestHandler ="DcSignRequestHandler";
    public static final String DcSignTaskSynResponseHandler ="DcSignTaskSynResponseHandler";
    public static final String DcSignTeminateRequestHandler ="DcSignTeminateRequestHandler";
    public static final String FeedBack ="FeedBack";


    /**
     *持久化json
     */
    public static void  persistentJson(String json,String toPath){
    	FileOutputStream fos = null;;
		try {
			JSONObject msg = new JSONObject(json);
	    	String id = msg.getString(Based.MSG_ID);
	    	File path = new File(toPath);
	    	if(!path.exists()){
	    		path.mkdirs();
	    	}
	    	File file = new File(toPath+File.separator+id);
			fos = new FileOutputStream(file);
	        fos.write(json.getBytes());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(fos!=null){
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

    }
    public static String  getJsonFromFile(String msgId,String path){
        File filepath = new File(PERSISTENTPATH+File.separator+path+File.separator+msgId);
        return fileRead(filepath);
    }
    public static File  getJsonFromFile(String msgId){
    	File DcSignRequestHandlerPath = new File(PERSISTENTPATH+File.separator+DcSignRequestHandler+File.separator+msgId);
    	File DcDistributeRequestHandlerPath = new File(PERSISTENTPATH+File.separator+DcDistributeRequestHandler+File.separator+msgId);
    	File DcSignTaskSynResponseHandlerPath = new File(PERSISTENTPATH+File.separator+DcSignTaskSynResponseHandler+File.separator+msgId);
    	File DcSignTeminateRequestHandlerPath = new File(PERSISTENTPATH+File.separator+DcSignTeminateRequestHandler+File.separator+msgId);
    	File DcShareRequestHandlerPath = new File(PERSISTENTPATH+File.separator+DcShareRequestHandler+File.separator+msgId);
    	if(DcSignRequestHandlerPath.exists()){
    		return DcSignRequestHandlerPath;
    	}
    	if(DcDistributeRequestHandlerPath.exists()){
    		return DcDistributeRequestHandlerPath;
    	}
    	if(DcSignTaskSynResponseHandlerPath.exists()){
    		return DcSignTaskSynResponseHandlerPath;
    	}
    	if(DcSignTeminateRequestHandlerPath.exists()){
    		return DcSignTeminateRequestHandlerPath;
    	}
    	if(DcShareRequestHandlerPath.exists()){
    		return DcShareRequestHandlerPath;
    	}
    	return null;
    }

    public static File  getFeedBackJsonFromFile(String msgId){
    	File feedBackPath = new File(PERSISTENTPATH+File.separator+FeedBack+File.separator+msgId);
    	if(feedBackPath.exists()){
    		return feedBackPath;
    	}
    	return null;
    }
    public static File  getSendJsonFromFile(String msgId){
        File sendFile = new File(PERSISTENTPATH+File.separator+"Send"+File.separator+msgId);
        if(sendFile.exists()){
            return sendFile;
        }
        return null;
    }

    public static String  fileRead(File file )  {
        FileReader reader;
        StringBuilder sb = new StringBuilder("");
    	BufferedReader bReader = null;
		try {
			reader = new FileReader(file);
			bReader = new BufferedReader(reader);
	        String s = "";
	        while ((s =bReader.readLine()) != null) {
	            sb.append(s);
	        }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
	        try {
	        	if(bReader!=null){
					bReader.close();
	        	}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

        return sb.toString();
    }
    public static void main(String[] args) {
    	String msgId = "1234567";
        String result = "操作成功";
        try{
        	File file = JsonConvertUtil.getJsonFromFile(msgId);
        	if(file!=null){
        		String msg = JsonConvertUtil.fileRead(file);
        		System.out.println(msg);
    			if(file.getAbsolutePath().contains(JsonConvertUtil.DcDistributeRequestHandler)){
    				JSONObject jsonObject = new JSONObject(msg);
    				System.out.println("HandlerProcess.doDcDistributeRequestHandler(jsonObject)");
    			}else if(file.getAbsolutePath().contains(JsonConvertUtil.DcShareRequestHandler)){
    				JSONObject jsonObject = new JSONObject(msg);
    				System.out.println("HandlerProcess.doDcShareRequestHandler(jsonObject)");
    			}
    			else if(file.getAbsolutePath().contains(JsonConvertUtil.DcSignRequestHandler)){
    				JSONObject jsonObject = new JSONObject(msg);
    				System.out.println("HandlerProcess.doDcSignRequestHandler(jsonObject);");

    			}
    			else if(file.getAbsolutePath().contains(JsonConvertUtil.DcSignTaskSynResponseHandler)){
    				JSONObject jsonObject = new JSONObject(msg);
    				System.out.println("doDcSignTaskSynResponseHandler");
    			}
    			else if(file.getAbsolutePath().contains(JsonConvertUtil.DcSignTeminateRequestHandler)){
    				JSONObject jsonObject = new JSONObject(msg);
    				System.out.println("doDcSignTeminateRequestHandler");

    			}
        	}else{
        		result = "没有对应的消息文件！";
        	}
        }catch(Exception e){
        	e.printStackTrace();
            result = "操作失败";
        }
	}
}
