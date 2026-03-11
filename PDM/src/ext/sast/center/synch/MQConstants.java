/**
 *
 */
package ext.sast.center.synch;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * @author cfire
 *
 */
public class MQConstants {

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
	public static String DC_UPLOAD = "http://10.112.1.33:8081/avidm/rest/dc/attach/upload";
	public static String DC_DOWNLOAD = "http://10.112.1.33:8081/avidm/rest/dc/attach/download";
	public static String DC_USERS = "http://10.112.1.33:8080";
	public static String DC_IP = "10.112.1.33";
	public static String DC_PORT = "8080";
	public static String DC_SITEIID = "";
	public static String DC_SITEID = "CASC_08";
	public static String DC_SITENAME = "八院A4";

	//格式 ： 149 + 10.125.192.32 此值唯一不要改动
	public static String SITEIID_149 = "1491012519219";
	//此id对应queue.properties中的queue_self的值
	public static String SITEID_149 = "149";
	//public static String SITEID_149KRS = "149KRS";
	public static String SITEID_149KRS = "149KRTest";

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
	public static final String ACTIVITY_NAME_KRS = "科瑞所会签";
	public static final String DEPT_KR = "科瑞所";
	public static final String DEPT_KR_OLD = "研发";
	public static final String PACKAGED_TYPE_FEEDBACK = "FeedBack";
	public static final String PACKAGED_TYPE_ALL = "All";
	public static final String FAWANGDANWEI = "FAWANGDANWEI";
	public static final String FAWANGDANWEI_LABEL= "发往单位";
	public static final String FAWANGDANWEI_VALUE1= "科瑞所";
	public static final String FAWANGDANWEI_VALUE2= "复材公司";
	static{

			String path = System.getProperty("AVIDM_HOME") + File.separator + "center.properties";
			File f = new File(path);
			if (f.exists()) {
				FileInputStream fis = null;
				BufferedInputStream bis = null;
				Properties props = new Properties();

				try {
					fis = new FileInputStream(f);
					bis = new BufferedInputStream(fis);
					props.load(bis);
					DC_IP=props.getProperty("DC_IP");
					DC_PORT=props.getProperty("DC_PORT");

					DC_UPLOAD = "http://"+DC_IP+":8081/avidm/rest/dc/attach/upload";
					DC_DOWNLOAD = "http://"+DC_IP+":8081/avidm/rest/dc/attach/download";
					DC_USERS = "http://"+DC_IP+":8080";

					SITEIID_149 = props.getProperty("SITEIID_149");
					//此id对应queue.properties中的queue_self的值
					SITEID_149 = props.getProperty("SITEID_149");
					SITEID_149KRS = props.getProperty("SITEID_149KRS");
					SITENAME_149 = props.getProperty("SITENAME_149");
					SITEIP_149 = props.getProperty("SITEIP_149");
					SITEPORT_149 = props.getProperty("SITEPORT_149");
					SOAPRECEIVER_URL_149 = "http://"+SITEIP_149+":"+SITEPORT_149+"/Windchill";

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					IOUtils.closeQuietly(bis);
					IOUtils.closeQuietly(fis);
				}
			}



		}

}
