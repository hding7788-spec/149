package ext.sast.center.synch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import ext.sast.center.util.PropertiesUtil;


public class MQExpImpUtil {

	private static Logger logger = Logger.getLogger(MQExpImpUtil.class);

	private static Map<String,Map<String,String>> expConvertMap = null;
	private static Map<String,Map<String,String>> impConvertMap = null;

	/**
	 * 是否使用三总三体打包格式
	 * @param dstSiteId
	 */
	public static boolean isCmPackage(String dstSiteId){
		if(dstSiteId==null)
			return true;
		if(dstSiteId.contains("805"))
			return true;
		if(dstSiteId.contains("八部"))
			return true;
		if(dstSiteId.contains("NO8"))
			return true;
		if(dstSiteId.contains("no8"))
			return true;
		if(dstSiteId.contains("509"))
			return true;
		if(dstSiteId.contains("149"))
			return true;
		if(dstSiteId.contains("cacl"))
			return true;
		return false;
	}

	/**
	 * 根据标准模型属性id，属性值，导入导出标识 来进行属性值的转换
	 * 转换的内容来源为系统配置的导入导出属性值映射xml文件
	 *
	 * @param attrId 标准模型属性ID
	 * @param attrValue
	 * @param expImpFlag
	 * @return
	 */
	public static String attrConvertValue(String attrId, Object attrValue, String expImpFlag) {
		if (null == attrValue) {
			return null;
		}
		//属性值映射的属性的值转换处理
		String attrValueStr = String.valueOf(attrValue);
		Map<String,String> attrConvertMap = new HashMap<String,String>();
		if(MQConstants.EXPIMP_FLAG_EXP.equals(expImpFlag)){
			//导入配置文件内容
			if(null == expConvertMap){
				loadAttrValueConvertConfig();
			}
			//属性存在导出需要的属性值映射配置
			if(expConvertMap.containsKey(attrId)){
				attrConvertMap = expConvertMap.get(attrId);
			}
		}else{
			//导入配置文件内容
			if(null == impConvertMap){
				loadAttrValueConvertConfig();
			}
			//属性存在导入需要的属性值映射配置
			if(impConvertMap.containsKey(attrId)){
				attrConvertMap = impConvertMap.get(attrId);
			}
		}
		if(attrConvertMap.containsKey(attrValueStr)){
			attrValueStr = attrConvertMap.get(attrValueStr);
		}
		return attrValueStr;
	}

	public static String attrConvertValueBySendFrom(String attrId, Object attrValue, String expImpFlag,String sendFrom) {
		if(sendFrom!=null && sendFrom.contains("805")&&"versionInfo".equals(attrId)&&"F".equals(attrValue)){
			return (String)attrValue;
		}
		if (null == attrValue) {
			return null;
		}
		//属性值映射的属性的值转换处理
		String attrValueStr = String.valueOf(attrValue);
		Map<String,String> attrConvertMap = new HashMap<String,String>();
		if(MQConstants.EXPIMP_FLAG_EXP.equals(expImpFlag)){
			//导入配置文件内容
			if(null == expConvertMap){
				loadAttrValueConvertConfig();
			}
			//属性存在导出需要的属性值映射配置
			if(expConvertMap.containsKey(attrId)){
				attrConvertMap = expConvertMap.get(attrId);
			}
		}else{
			//导入配置文件内容
			if(null == impConvertMap){
				loadAttrValueConvertConfig();
			}
			//属性存在导入需要的属性值映射配置
			if(impConvertMap.containsKey(attrId)){
				attrConvertMap = impConvertMap.get(attrId);
			}
		}
		if(attrConvertMap.containsKey(attrValueStr)){
			attrValueStr = attrConvertMap.get(attrValueStr);
		}
		return attrValueStr;
	}


	public static String emptyConvertValue(String attrId, Object attrValue, String expImpFlag) {
		if (null == attrValue) {
			return null;
		}
		//属性值映射的属性的值转换处理
		String attrValueStr = String.valueOf(attrValue);
		Map<String,String> attrConvertMap = new HashMap<String,String>();
		if(MQConstants.EXPIMP_FLAG_EXP.equals(expImpFlag)){
			//导入配置文件内容
			if(null == expConvertMap){
				loadAttrValueConvertConfig();
			}
			//属性存在导出需要的属性值映射配置
			if(expConvertMap.containsKey(attrId)){
				attrConvertMap = expConvertMap.get(attrId);
			}

		}else{
			//导入配置文件内容
			if(null == impConvertMap){
				loadAttrValueConvertConfig();
			}
			//属性存在导入需要的属性值映射配置
			if(impConvertMap.containsKey(attrId)){
				attrConvertMap = impConvertMap.get(attrId);
			}
		}
		if(attrConvertMap.containsKey(attrValueStr)){
			attrValueStr = attrConvertMap.get(attrValueStr);
		}
		return attrValueStr;
	}

	/**
	 * 读取属性值映射配置文件
	 *
	 */
	@SuppressWarnings("unchecked")
	public static void loadAttrValueConvertConfig() {
		//获取xml文件的Document对象
		InputStream is = null;
		Document attrConvertXmlDoc = null;
		String path = PropertiesUtil.getWTHome() + File.separator + "codebase"+File.separator+"ext"+File.separator+
				"sast"+File.separator+"center"+File.separator+"synch"+File.separator+"attrvalconvert.xml";
		try {
			is = new FileInputStream(path);
			SAXBuilder builder = new SAXBuilder(false);
			attrConvertXmlDoc = builder.build(is);
			// 获取根节点
			Element rootEle = attrConvertXmlDoc.getRootElement();
			//导出的属性值映射配置
			expConvertMap = new HashMap<String,Map<String,String>>();
			Element  expConfigElement = rootEle.getChild("exp_Config");
			List<Element> expAttrElements = expConfigElement.getChildren("attr");
			for (Element attrEle : expAttrElements) {
				String attrId = attrEle.getAttributeValue("id");

				List<Element> convertMapElements = attrEle.getChildren("convertMap");
				Map<String,String> convertMap = new HashMap<String,String>();
				for (Element convertMapEle : convertMapElements) {
					String fromValue = convertMapEle.getAttributeValue("fromValue");
					String toValue = convertMapEle.getAttributeValue("toValue");
					convertMap.put(fromValue, toValue);
				}
				expConvertMap.put(attrId, convertMap);
			}

			//导入的属性值映射配置
			impConvertMap = new HashMap<String,Map<String,String>>();
			Element  impConfigElement = rootEle.getChild("imp_Config");

			List<Element> impAttrElements = impConfigElement.getChildren("attr");
			for (Element attrEle : impAttrElements) {
				String attrId = attrEle.getAttributeValue("id");

				List<Element> convertMapElements = attrEle.getChildren("convertMap");
				Map<String,String> convertMap = new HashMap<String,String>();
				for (Element convertMapEle : convertMapElements) {
					String fromValue = convertMapEle.getAttributeValue("fromValue");
					String toValue = convertMapEle.getAttributeValue("toValue");
					convertMap.put(fromValue, toValue);
				}
				impConvertMap.put(attrId, convertMap);
			}
		} catch (FileNotFoundException e) {
			String errMsg = "属性值映射配置文件不存在。文件名：" + path;
			logger.error(errMsg, e);
			throw new RuntimeException(errMsg, e);
		} catch (JDOMException e) {
			String errMsg = "属性值映射配置文件解析失败。文件名：" + path;
			logger.error(errMsg, e);
			throw new RuntimeException(errMsg, e);
		} catch (IOException e) {
			String errMsg = "属性值映射配置文件解析失败。文件名：" + path;
			logger.error(errMsg, e);
			throw new RuntimeException(errMsg, e);
		}
	}

}
