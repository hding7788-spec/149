/**
 *
 */
package ext.sast.center.synch;

import java.util.HashSet;
import java.util.Set;

/**
 * @author cfire
 *
 */
public class MQConstantsTest {

	public static Set<String> units  = new HashSet<String>();
	static {
		units.add("805.11");
		units.add("805所");
		units.add("805");
		units.add("no8");
		units.add("八部");
		units.add("A4CASC_08_NO8_test");
		units.add("509");
	}

	//测试
	/*public static String DC_UPLOAD = "http://10.112.1.213:8081/avidm/rest/dc/attach/upload";
	public static String DC_DOWNLOAD = "http://10.112.1.213:8081/avidm/rest/dc/attach/download";
	public static String DC_SITEIID = "";
	public static String DC_SITEID = "CASC_08_test";
	public static String DC_SITENAME = "八院A4测试";

	//格式 ： 149 + 10.125.192.32 此值唯一不要改动
	public static String SITEIID_149 = "1491012519232";
	//此id对应queue.properties中的queue_self的值
	public static String SITEID_149 = "149";
	public static String SITENAME_149 = "149测试系统";
	public static String SITEIP_149 = "10.125.192.32";
	public static String SITEPORT_149 = "80";
	public static String SOAPRECEIVER_URL_149 = "http://10.125.192.32:80/Windchill";*/

	//正式
	public static String DC_UPLOAD = "http://10.112.1.213:8081/avidm/rest/dc/attach/upload";
	public static String DC_DOWNLOAD = "http://10.112.1.213:8081/avidm/rest/dc/attach/download";
	public static String DC_USERS = "http://10.112.1.213:8080";
	public static String DC_SITEIID = "";
	public static String DC_SITEID = "CASC_08";
	public static String DC_SITENAME = "八院A4";

	//格式 ： 149 + 10.125.192.32 此值唯一不要改动
	public static String SITEIID_149 = "1491012519219";
	//此id对应queue.properties中的queue_self的值
	public static String SITEID_149 = "149";
	public static String SITENAME_149 = "149正式系统";
	public static String SITEIP_149 = "10.125.192.19";
	public static String SITEPORT_149 = "80";
	public static String SOAPRECEIVER_URL_149 = "http://10.125.192.19:80/Windchill";



	//public static String SITEIID_149 = "1491012519219";
//	public static String SITEID_149 = "149";
//	public static String SITENAME_149 = "149正式系统";
//	public static String SITEIP_149 = "10.125.192.19";
//	public static String SITEPORT_149 = "80";
//	public static String SOAPRECEIVER_URL_149 = "http://10.125.192.19:80/Windchill";
	//格式 ： 805 + 10.123.67.228此值唯一不要改动
	//public static String SITEIID_805 = "8051012367228";
	//此id对应queue.properties中的queue_self的值
	//public static String SITEID_805 = "805";
	//public static String SITENAME_805 = "805测试系统";
	//public static String SITEIP_805 = "10.123.67.228";
	//public static String SITEPORT_805 = "80";
	//public static String SOAPRECEIVER_URL_805 = "http://10.123.67.228:80/Windchill";

	//导出
	public static final String EXPIMP_FLAG_EXP = "0";
	
	/**
	 * 协同流程异常状态
	 */
	public static final String STATUS_1 = "数据打包成功";
	public static final String STATUS_2 = "数据打包失败";
	public static final String STATUS_3 = "数据包发送至中心域成功";
	public static final String STATUS_4 = "数据包发送至中心域失败";
	public static final String STATUS_5 = "中心域处理成功";
	public static final String STATUS_6 = "中心域处理失败";
	public static final String STATUS_7 = "下游数据接收成功";
	public static final String STATUS_8 = "下游数据接收失败";
	public static final String STATUS_9 = "数据导入成功";
	public static final String STATUS_10 = "数据导入失败";
	public static final String STATUS_11 = "下游提交待办任务成功";
	public static final String STATUS_12 = "下游提交待办任务失败";
	public static final String STATUS_13 = "中心域处理下游待办任务成功";
	public static final String STATUS_14 = "中心域处理下游待办任务失败";
	public static final String STATUS_15 = "上游接收意见成功";
	public static final String STATUS_16 = "上游接收意见失败";
	public static final String STATUS_17 = "下游处理强制会签请求成功";
	public static final String STATUS_18 = "下游处理强制会签请求失败";
	
}
