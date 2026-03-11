/**
 *
 */
package com.ptc.extend.ixb.center;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cfire
 *
 */
public class MQExpImpConstants {
	public  static final String SERIES ="wt.series.HarvardSeries.newversion149";
	public  static final String SERIES2 ="wt.series.HarvardSeries";
	public  static final String PRIMARY ="PRIMARY";
	public  static final String SECONDARY ="SECONDARY";
	public static String XML_MQDOCUMENT = "Document";
	public static String XML_MQPART = "Part";
	public static String XML_MQCADDOCUMENT = "CADDocument";
	public static String XML_MQECA = "ApproveOrder";
	public static String XML_MQECO = "ECO";
	public static String XML_MQECR = "ECR";
	public static String XML_MQTNO = "TNO";
	public static String XML_MQTNR = "TNR";
	public static String XML_MQPPCO = "PPCO";

	public static String VIEW_SHEJI = "设计";
	public static String VIEW_DESIGN = "Design";

	public static String STATE_APPROVED = "APPROVED";

	public static String VALUE_DOMAINNAME = "149";
	public static String REF_OWNER = "OWNER";
	public static String REF_IMAGE = "IMAGE";
	public static String REF_CONTENT = "CONTENT";

	public static Map<String,String> BUILD_TYPE;
	static{
		BUILD_TYPE = new HashMap<String,String>();
		BUILD_TYPE.put(REF_OWNER, "7");
		BUILD_TYPE.put(REF_IMAGE, "6");
		BUILD_TYPE.put(REF_CONTENT, "2");
	}



	public static String WAILAI_PRODUCT = "外来数据产品库";//非标准型号映射无映射时，存于此库
	public static String WAILAI_LIB = "外来数据存储库";//非标准型号映射无映射时，存于此库


}
