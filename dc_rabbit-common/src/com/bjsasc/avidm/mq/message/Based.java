package com.bjsasc.avidm.mq.message;

/**
 * 公共的字段定义
 * 
 * @author hwz
 *
 */
public interface Based {
	public static final String MSG_ID = "msg_id";

	public static final String MSG_TYPE = "msg_type";
	//以下为metaMessage类型定义
	//发起会签
	public static final String META_MSG_TYPE_SIGNATURE = "meta_msg_type_signature";
	//会签反馈
	public static final String META_MSG_TYPE_SIGN_REPLY = "meta_msg_type_sign_reply";
	//删除单位
	public static final String META_MSG_TYPE_SIGN_DELSITE = "meta_msg_type_sign_delsite";
	//增加人员
	public static final String META_MSG_TYPE_SIGN_ADDPERSON = "meta_msg_type_sign_addperson";
	//删除人员
	public static final String META_MSG_TYPE_SIGN_DELPERSON = "meta_msg_type_sign_delperson";
	//强制结束
	public static final String META_MSG_TYPE_SIGN_TERMINATE = "meta_msg_type_sign_terminate";
	//会签任务操作同步
	public static final String META_MSG_TYPE_SIGN_TASKSYN = "meta_msg_type_sign_tasksyn";
	//预审
	public static final String META_MSG_TYPE_SHARE = "meta_msg_type_share";
	//预审反馈
	public static final String META_MSG_TYPE_SHARE_REPLY = "meta_msg_type_share_reply";
	//预审任务操作同步
	public static final String META_MSG_TYPE_SHARE_TASKSYN = "meta_msg_type_share_tasksyn";
	//发放
	public static final String META_MSG_TYPE_DISTRIBUTE = "meta_msg_type_distribute";
	//发放反馈
	public static final String META_MSG_TYPE_DISTRIBUTE_REPLY = "meta_msg_type_distribute_reply";
	//发放任务操作同步
	public static final String META_MSG_TYPE_DISTRIBUTE_TASKSYN = "meta_msg_type_distribute_tasksyn";
	//型号下发
	public static final String META_MSG_TYPE_PRODSEND = "meta_msg_type_prodsend";
	//模型下发
	public static final String META_MSG_TYPE_MODELSEND = "meta_msg_type_modelsend";
	//型号映射
	public static final String META_MSG_TYPE_PRODMAPPING = "meta_msg_type_prodmapping";
	//同步用户
	public static final String META_MSG_TYPE_SYNUSER = "meta_msg_type_synuser";
	//同步组织
	public static final String META_MSG_TYPE_SYNDIV = "meta_msg_type_syndiv";
	//同步组织和用户关系
	public static final String META_MSG_TYPE_SYNPRINCIPAL = "meta_msg_type_synprincipal";
	//同步总体所型号
	public static final String META_MSG_TYPE_SYNPRODUCT = "meta_msg_type_synproduct";
	//域注册
	public static final String META_MSG_TYPE_SITEREGISTER = "meta_msg_type_siteregister"; 
	//域信息同步
	public static final String META_MSG_TYPE_SYNSITE = "meta_msg_type_synsite"; 
	//域信息删除
	public static final String META_MSG_TYPE_SITEDELETE = "meta_msg_type_sitedelete"; 
	
	//以上为metaMessage类型定义
	
	public static final String MSG_DESCRIPTION = "msg_description";

	// 会签：发起方 --> 数据中心
	public static final String DC_REQUEST_SIGN_DC = "avidm_dc_countersign_req_datacenter";

	// 会签：发起方 <-- 数据中心
	public static final String DC_RESPONSE_SIGN_DC = "avidm_dc_countersign_resp_datacenter";

	// 会签：数据中心 --> 接收方
	public static final String DC_REQUEST_SIGN_RECEIVER = "avidm_dc_countersign_req_receiver";

	// 会签：数据中心 <-- 接收方
	public static final String DC_RESPONSE_SIGN_RECEIVER = "avidm_dc_countersign_resp_receiver";

	// 预审：发起方 --> 数据中心
	public static final String DC_REQUEST_SHARE_DC = "avidm_dc_share_req_datacenter";

	// 预审：发起方 <-- 数据中心
	public static final String DC_RESPONSE_SHARE_DC = "avidm_dc_share_resp_datacenter";

	// 预审：数据中心 --> 接收方
	public static final String DC_REQUEST_SHARE_RECEIVER = "avidm_dc_share_req_receiver";

	// 预审：数据中心 <-- 接收方
	public static final String DC_RESPONSE_SHARE_RECEIVER = "avidm_dc_share_resp_receiver";

	// 分发：发起方 --> 数据中心
	public static final String DC_REQUEST_DISTRIBUTE_DC = "avidm_dc_distribute_req_datacenter";

	// 分发：发起方 <-- 数据中心
	public static final String DC_RESPONSE_DISTRIBUTE_DC = "avidm_dc_distribute_resp_datacenter";

	// 分发：数据中心 --> 接收方
	public static final String DC_REQUEST_DISTRIBUTE_RECEIVER = "avidm_dc_distribute_req_receiver";

	// 分发：数据中心 <-- 接收方
	public static final String DC_RESPONSE_DISTRIBUTE_RECEIVER = "avidm_dc_distribute_resp_receiver";
	

	// 会签增加人员：发起方 --> 数据中心
	public static final String DC_REQUEST_SIGN_ADDPERSON_DC = "avidm_dc_countersign_addperson_req_datacenter";

	// 会签增加人员：发起方 <-- 数据中心
	public static final String DC_RESPONSE_SIGN_ADDPERSON_DC = "avidm_dc_countersign_addperson_resp_datacenter";

	// 会签增加人员：数据中心 --> 接收方
	public static final String DC_REQUEST_SIGN_ADDPERSON_RECEIVER = "avidm_dc_countersign_addperson_req_receiver";

	// 会签增加人员：数据中心 <-- 接收方
	public static final String DC_RESPONSE_SIGN_ADDPERSON_RECEIVER = "avidm_dc_countersign_addperson_resp_receiver";

	// 会签删除单位：发起方 --> 数据中心
	public static final String DC_REQUEST_SIGN_DELETESITE_DC = "avidm_dc_countersign_deletesite_req_datacenter";

	// 会签删除单位：数据中心 --> 接收方
	public static final String DC_REQUEST_SIGN_DELETESITE_RECEIVER = "avidm_dc_countersign_deletesite_req_receiver";

	// 会签删除人员：发起方 --> 数据中心
	public static final String DC_REQUEST_SIGN_DELETEPERSON_DC = "avidm_dc_countersign_deleteperson_req_datacenter";

	// 会签删除人员：数据中心 --> 接收方
	public static final String DC_REQUEST_SIGN_DELETEPERSON_RECEIVER = "avidm_dc_countersign_deleteperson_req_receiver";

	// 会签强制结束：发起方 --> 数据中心
	public static final String DC_REQUEST_SIGN_TERMINATE_DC = "avidm_dc_countersign_terminate_req_datacenter";

	// 会签强制结束：数据中心 --> 接收方
	public static final String DC_REQUEST_SIGN_TERMINATE_RECEIVER = "avidm_dc_countersign_terminate_req_receiver";

	// 会签任务操作同步：数据中心 <-- 接收方
	public static final String DC_RESPONSE_SIGN_TASKSYN_RECEIVER = "avidm_dc_countersign_tasksyn_resp_receiver";

	// 会签任务操作同步：发起方 <-- 数据中心
	public static final String DC_RESPONSE_SIGN_TASKSYN_DC = "avidm_dc_countersign_tasksyn_resp_datacenter";
	// 标准型号下发请求 ： 数据中心 -->厂所
	public static final String DC_REQUEST_STANDARD_PROD_RECEIVER = "dc_standard_product_req_receiver";
	// 型号映射同步到中心域 ： 厂所 -->数据中心
	public static final String DC_RESPONSE_PROD_MAPPING_RECEIVER = "dc_prod_mapping_resp_receiver";

	// 预审任务操作同步：数据中心 <-- 接收方
	public static final String DC_RESPONSE_SHARE_TASKSYN_RECEIVER = "avidm_dc_share_tasksyn_resp_receiver";

	// 预审任务操作同步：发起方 <-- 数据中心
	public static final String DC_RESPONSE_SHARE_TASKSYN_DC = "avidm_dc_share_tasksyn_resp_datacenter";
	
	// 发放任务操作同步：数据中心 <-- 接收方
	public static final String DC_RESPONSE_DISTRIBUTE_TASKSYN_RECEIVER = "avidm_dc_distribute_tasksyn_resp_receiver";

	// 发放任务操作同步：发起方 <-- 数据中心
	public static final String DC_RESPONSE_DISTRIBUTE_TASKSYN_DC = "avidm_dc_distribute_tasksyn_resp_datacenter";

	// 标准模型及属性下发请求 ： 数据中心 -->厂所
	public static final String DC_REQUEST_STANDARD_MODEL_RECEIVER = "avidm_dc_standard_model_req_receiver";
	
	// 域注册消息，中心域推送本域信息到厂所 ： 数据中心 -->厂所
	public static final String DC_REQUEST_REGISTER_RECEIVER = "avidm_dc_register_req_receiver";
	
	//域注册反馈消息，厂所保存中心域信息后，推送本域信息到中心域：厂所---》数据中心
	public static final String DC_RESPONSE_REGISTER_RECEIVER = "avidm_dc_register_resp_receiver";
	
	// 同步用户，中心域发送请求到厂所 ： 数据中心 -->厂所
	public static final String DC_REQUEST_SYNUSER_RECEIVER = "avidm_dc_synuser_req_receiver";
	
	// 同步组织，中心域发送请求到厂所 ： 数据中心 -->厂所
	public static final String DC_REQUEST_SYNDIV_RECEIVER = "avidm_dc_syndiv_req_receiver";
	
	// 同步用户与组织关系，中心域发送请求到厂所 ： 数据中心 -->厂所
	public static final String DC_REQUEST_SYNPRINCIPAL_RECEIVER = "avidm_dc_synprincipal_req_receiver";
	
	// 同步产品，中心域发送请求到厂所 ： 数据中心 -->厂所
	public static final String DC_REQUEST_SYNPRODUCT_RECEIVER = "avidm_dc_synprod_req_receiver";
	
	// 同步用户， 数据中心 <--厂所
	public static final String DC_RESPONSE_SYNUSER_RECEIVER = "avidm_dc_synuser_resp_receiver";
	
	// 同步组织，数据中心 <--厂所
	public static final String DC_RESPONSE_SYNDIV_RECEIVER = "avidm_dc_syndiv_resp_receiver";
	
	// 同步用户与组织关系，数据中心 <--厂所
	public static final String DC_RESPONSE_SYNPRINCIPAL_RECEIVER = "avidm_dc_synprincipal_resp_receiver";
	
	// 同步产品，数据中心 <--厂所
	public static final String DC_RESPONSE_SYNPRODUCT_RECEIVER = "avidm_dc_synprod_resp_receiver";	
	
	// 同步域信息，厂所 -- 》中心
	public static final String DC_REQUEST_SYNSITE_DC = "avidm_dc_synsite_req_dc";
	// 域信息反馈，中心 -- 》厂所
	public static final String DC_RESPONSE_SYNSITE_RECEIVER = "avidm_dc_synsite_resp_receiver";
	//中心域删除 ，中心 --》厂所
	public static final String DC_REQUEST_SITEDELETE_RECEIVER = "avidm_dc_sitedelete_req_receiver";
	
	public static final String MSG_CREATED_TIME = "msg_created_time";

	// 整体业务的状态
	public static final String MSG_STATUS = "msg_status";

	// 刚创建
	public static final int MSG_STATUS_NEW = 1;

	// 消息已发出，各单位正处理中
	public static final int MSG_STATUS_PROCESSING = 2;

	// 其它单位处理时候发生异常
	public static final int MSG_STATUS_PROCESSING_EXCEPTION = 3;

	// 已完成
	public static final int MSG_STATUS_FINISHED = 11;

	// 失败
	public static final int MSG_STATUS_FAILED = 12;

	public static final String MSG_PROCESSING_STATE = "msg_processing_state";

	public static final String MSG_PROCESSING_STATE_PROCESSING = "processing";

	public static final String MSG_PROCESSING_STATE_FINISHED = "finished";

	public static final String MSG_PROCESSING_STATE_EXCEPTION = "exception";

	public static final String MSG_PROCESSING_STATE_TERMINATED = "terminated";

	public static final String SYS_VERSION_INITIAL = "sys_version_initial";

	public static final String SYS_VERSION_REQUEST = "sys_version_request";

	public static final String SYS_VERSION_A3 = "sys_version_a3";

	public static final String SYS_VERSION_A4 = "sys_version_a4";

	public static final String SYS_VERSION_A5 = "sys_version_a5";

	public static final String SYS_VERSION_DC = "sys_version_dc";

	public static final String SYS_VERSION_DC_A4 = "sys_version_dc_a4";
	//windchill10
	public static final String SYS_VERSION_WIN10 = "sys_version_win10";
	//windchill11
	public static final String SYS_VERSION_WIN11 = "sys_version_win11";
	// 单据信息
	public static final String ORDER_IID = "order_iid";

	public static final String ORDER_ID = "order_id";

	public static final String ORDER_NAME = "order_name";

	// 发起站点信息
	public static final String J_SRC_SITE = "j_src_site";

	public static final String SRC_SITE_NAME = "src_site_name";

	public static final String SRC_SITE_IID = "src_site_iid";

	// 反馈的站点信息
	public static final String RESPONSE_SITE_IID = "response_site_iid";

	// 目标站点列表
	public static final String JA_DST_SITES = "ja_dst_sites";

	public static final String DST_SITES_INFO = "dst_sites_info";

	public static final String IID = "iid";

	public static final String ID = "id";

	public static final String NAME = "name";

	public static final String DESCRIPTION = "description";

	public static final String URL = "url";

	public static final String SUMMARY = "summary";

	// 发起单位的请求内容原始信息，base64编码
	public static final String MSG_CONTENT_INITIAL = "msg_content_initial";

	// 请求的信息，base64编码
	public static final String MSG_CONTENT_REQUEST = "msg_content_request";

	// 响应的信息，base64编码
	public static final String MSG_CONTENT_RESPONSE = "msg_content_response";

	public static final String JA_OBJECTS_INITIAL = "ja_objects_initial";

	public static final String JA_OBJECTS_REQUEST = "ja_objects_request";

	public static final String JA_OBJECTS_RESPONSE = "ja_objects_response";
	// 反馈的任务信息
	public static final String JA_TASKS_RESPONSE = "ja_tasks_response";
	// 反馈的意见信息
	public static final String JA_SIGNS_RESPONSE = "ja_signs_response";
	// 转发时文档拆分信息，windchill系统可不填，用于A4，A5系统接收方拆包转发
	public static final String JA_DOCBINDS_RESPONSE = "ja_docbinds_response";

	public static final String SITE_IID = "site_iid";

	public static final String JA_RECEIVERS = "ja_receivers";

	public static final String J_PRODUCT = "j_product";//发起会签时传递，为了在接收方单据上展示原有产品信息

	public static final String J_CREATOR = "j_creator";
	
	public static final String J_STD_PRODUCT = "j_std_product";//发起会签时传递，用于判断在接收方是否配置了 一对多

	// ********以下属性为跨域请求对象需要***********
	// 对象oid
	public static final String OBJECT_OID = "object_oid";
	// 对象编号
	public static final String OBJECT_ID = "object_id";
	// 对象名称
	public static final String OBJECT_NAME = "object_name";
	// 有就填值，没有填空字符串
	public static final String OBJECT_MASTER_IID = "master_iid";
	// 设计中，审批中，受控中
	public static final String OBJECT_STATE = "object_state";
	// 有版本就填写，例如A.1。无版本对象填写空字符串
	public static final String OBJECT_VERSION = "object_version";
	// 对象类全路径
	public static final String OBJECT_CLASSNAME = "classname";
	// 文档及EPM文档对象为OBJECT_TYPE_DOC，部件对象为OBJECT_TYPE_PART，其他单据对象为OBJECT_TYPE_ORDER
	public static final String OBJECT_TYPE = "object_type";

	public static final String OBJECT_TYPE_DOC = "_Doc";

	public static final String OBJECT_TYPE_PART = "_Item";

	public static final String OBJECT_TYPE_ORDER = "_Order";

	public static final String OBJECT_SUMMARY = "object_summary";

	// ********以上属性为跨域请求对象需要***********

	public static final String USER_IID = "user_iid";

	public static final String USER_ID = "user_id";

	public static final String USER_NAME = "user_name";

	// **************以下属性为反馈任务对象所用
	// 任务唯一标识
	public static final String TASK_IID = "task_iid";
	// 任务名称
	public static final String TASK_NAME = "task_name";
	// 父任务唯一标识，如果没有父任务，该值为-1
	public static final String TASK_PARENT_IID = "task_parent_iid";
	// 任务创建时间
	public static final String TASK_CREATE_TIME = "task_create_time";
	// 任务处理人唯一标识
	public static final String TASK_USER_IID = "task_user_iid";
	// 任务处理人编号
	public static final String TASK_USER_ID = "task_user_id";
	// 任务处理人名称
	public static final String TASK_USER_NAME = "task_user_name";
	// 任务处理状态
	public static final String TASK_STATE = "task_state";

	// 以下几个字段为神软内部使用，用于神软特定的转发逻辑
	// 转发模式，并行转发和串行转发
	public static final String TASK_FORWARDMODEL = "task_forwardmodel";
	// 任务是否转发
	public static final String TASK_ISTRANSMIT = "task_istransmit";
	// 是否等待子任务
	public static final String TASK_ISWAITCHILD = "task_iswaitchild";
	// 任务是否抢先
	public static final String TASK_ISLEADUP = "task_isleadup";
	// *********以上几个字段用于神软单位之间的传递
	// **************以上属性为反馈任务对象所用

	// 目标域iid，很多场景只需要该字段，为了解析简单，故单独定义
	public static final String TARGET_SITE_IID = "target_site_iid";

	// **************以下属性为反馈任务签署意见对象所用
	// 签署人部门名称
	public static final String SIGN_DIV_NAME = "div_name";
	// 签署时间
	public static final String SIGN_TIME = "sign_time";
	// 意见类型，单位意见：division，个人意见：person
	public static final String SIGN_TYPE = "mind_type";
	// 意见是否同意，是1，否0
	public static final String SIGN_IS_AGREE = "is_agree";

	public static final String SIGN_USER_NAME = "sign_user_name";
	// 签署意见内容
	public static final String SIGN_CONTENT = "sign_content";

	// **************以上属性为反馈任务签署意见对象所用

	// **************以下属性为神软专用，用于拆包转发对象传递
	// 拆分文档唯一标识
	public static final String DOC_IID = "doc_iid";
	// 拆分文档名称
	public static final String DOC_NAME = "doc_name";
	// 送审单唯一标识
	public static final String SENDORDER_IID = "sendorder_iid";
	// 是否主治工艺，是 1，否 0
	public static final String IS_CRAFTWORK = "is_craftwork";

	// **************以上属性为神软专用，用于拆包转发对象传递

	// **************以下属性为配置标准型号、配置型号映射对象所用-qxd-open
	// 操作类型
	public static final String OPERATE_TYPE = "operate_type";
	//all
	public static final String ALL = "all";
	// 新增
	public static final String ADD = "add";
	// 删除
	public static final String DELETE = "delete";
	// 修改
	public static final String MODIFY = "modify";
	
	// 标准型号
	public static final String JA_PRODUCTS_REQUEST = "ja_products_request";
	// 型号映射
	public static final String JA_PRODUCTMAPPINGS_REQUEST = "ja_productmappings_request";
	// 反馈型号映射的数据
	// 产品iid
	public static final String PRODUCT_IID = "product_iid";
	// 产品标识
	public static final String PRODUCT_ID = "product_id";
	// 产品名称
	public static final String PRODUCT_NAME = "product_name";
	// 域iid
	public static final String SITE_NAME = "site_name";
	// 域名称
	public static final String SITE_ID = "site_id";
	// 场所产品iid
	public static final String CS_PRODUCT_IID = "cs_product_iid";
	// 场所产品标识
	public static final String CS_PRODUCT_ID = "cs_product_id";
	// 场所产品名称
	public static final String CS_PRODUCT_NAME = "cs_product_name";
	// 场所域iid
	public static final String CS_SITE_IID = "cs_site_iid";
	
	public static final String STD_PRODUCT_IID = "std_product_iid";
	// **************以上属性为配置标准型号对象所用-qxd-end

	// **************以下属性为配置模型所用-qxd-open
	// 请求配置模型属性数据
	public static final String JA_MODELS_REQUEST = "ja_models_request";
	
	public static final String JA_MODELATTRS_REQUEST = "ja_modelattrs_request";
	
	public static final String MODEL_IID = "model_iid";
	// 模型名称
	public static final String MODELNAME = "modelname";
	// 模型id
	public static final String MODELID = "modelid";
	// 模型属性名称
	public static final String MODELATTRNAME = "modelattrname";
	// 模型属性id
	public static final String MODELATTRID = "modelattrid";
	// **************以上属性为配置模型所用-qxd-end
	//以下 为文件属性
	public static final String J_FILE = "j_file";
	
	public static final String FILE_ID = "file_id";
	
	public static final String KEY = "key";
	
	public static final String IVKEY = "ivkey";
	
	public static final String FILE_SIZE = "file_size";
	
	public static final String FILE_NAME = "file_name";
	//以上为文件属性
	//域注册start
	public static final String J_SITEINFO_REQUEST = "j_siteinfo_request";
	
	public static final String J_SITEINFO_RESPONSE = "j_siteinfo_response";
	
	public static final String IP = "ip";
	
	public static final String PORT = "port";
	
	public static final String VERSION = "version";
	
	public static final String ACADEMY_ID = "academy_id";
	
	public static final String ACADEMY_NAME = "academy_name";
	
	public static final String IS_DC ="is_dc";
	
	public static final String SOAPRECEIVER_URL = "soapreceiver_url";//http://10.0.1.144:8080/avidm/messageReceiver
	//域注册属性end
	
	//同步用户属性start
	public static final String SECLEVEL = "seclevel";
	
	public static final String TELEPHONE = "telephone";
	
	public static final String EMAIL = "email";
	
	public static final String JA_USERS_RESPONSE = "ja_users_response";
	//同步用户属性end
	
	//同步组织属性start
	public static final String JA_DIVS_RESPONSE = "ja_divs_response";
	
	public static final String PARENT_IID = "parent_iid";
	
	public static final String DIV_IID = "div_iid";
	//同步组织属性end
	
	public static final String JA_PRINCIPALS_RESPONSE = "ja_principals_response";
	
	public static final String JA_PRODUCTS_RESPONSE = "ja_products_response";
	
	public static final String JA_SITES_RESPONSE = "ja_sites_response";
	
	//目标域导入上下文定义
	public static final String DST_CONTEXT = "dst_context";
	
	public static final String SOAPPARAMS = "soapparams";
}
