package ext.casc.dfmRule.common;

import java.io.IOException;
import java.io.InputStream;

import wt.httpgw.URLFactory;
import wt.util.WTContext;
import wt.util.WTException;
import wt.util.WTProperties;


/**
 
 * @author xuetao

 *
 */

public class Constants {


	public static  final String LIFECYCLESTETE_INWORK="正在工作";	//提交签审处判断时使用




	private static final String SITE_DOMAIN_PRO = "wt.inf.container.SiteOrganization.internetDomain";
	public static String baseUrl = null;
	public static String domain = null;
	public static String WT_HOME = null;
	public static String WT_TEMP = null;
	public static String WT_CODEBASE = null;
	static {
		// WT_HOME
		try {
			baseUrl = new URLFactory().getBaseHREF();
			domain = getDomainValue();
			WTProperties prop = WTProperties.getLocalProperties();
			WT_HOME = prop.getProperty("wt.home");
			WT_TEMP = prop.getProperty("wt.temp");
			WT_CODEBASE = prop.getProperty("wt.codebase.location");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	public static String getDomainValue() {
		String domainOpp = new String();
		try {
			WTProperties wtp = WTProperties.getLocalProperties();
			String SITE_ORG_FILE = wtp.getProperty("wt.inf.container.SiteOrganization.file", "wt/inf/container/SiteOrganization.properties");
			InputStream istream = null;
			try {
				istream = WTContext.getContext().getResourceAsStream(SITE_ORG_FILE);
				wtp = new WTProperties(null);
				if (istream != null) {
					wtp.load(istream);
				}
				String SITE_DOMAIN_VALUE = wtp.getProperty(SITE_DOMAIN_PRO);
				String[] domainSplit = SITE_DOMAIN_VALUE.split("[.]");
				domainOpp = domainSplit[0];
				for (int i = 1; i < domainSplit.length; i++)
					domainOpp = domainSplit[i] + "." + domainOpp;
			} finally {
				if (istream != null) {
					istream.close();
				}
			}
		} catch (Throwable t) {
			throw new ExceptionInInitializerError(t);
		}
		return domainOpp;
	}

	public static final String LIB_GUIZE = "DFMPro工艺检查规则库";

	public static final String GROUP_RULEMANAGER_149 = "149工艺规则管理员"; // 149工艺规则管理员

	public static String DOC_RULEPACKAGE = "casc.sast.149.GYRULEFILE"; // 工艺规则包
	public static String DOC_RULEPACKAGE_CHECKREPORT = "casc.sast.149.PROCESS_CHECK_REPORT"; // 工艺规则检查报告

	public static final String IBA_CHANPINDAIHAO = "PINDEX"; // 产品代号
	public static final String IBA_XINGHAO = "modelType"; // 型号

	public static final String DOC_GONGYIWENJIAN = "工艺文件"; // 工艺文件显示名字
	public static final String DOC_GONGYIGUIZEBAO = "工艺规则包"; // 工艺规则包
	public static final String DOC_GONGYICHECKREPORT = "工艺检查报告"; // 工艺检查报告
	public static final String IBA_BIGTYPE = "docBigType"; // 大类
	public static final String IBA_SMALLTYPE = "docSmallType"; // 小类

	public static final String LIBRARY_MANAGER = "LIBRARY MANAGER"; // 存储库经理
	public static final String PRODUCT_MANAGER = "PRODUCT MANAGER"; // 产品库经理
	public static final String TYPE_WTDOCUMENT = "wt.doc.WTDocument";

}
