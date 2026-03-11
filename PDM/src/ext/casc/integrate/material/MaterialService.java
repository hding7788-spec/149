package ext.casc.integrate.material;

import java.util.ArrayList;
import java.util.List;

import wt.part.WTPart;
import wt.util.WTException;
import ext.casc.util.IBAUtility;
import ext.casc.util.WCUtil;

public class MaterialService {

	/**
	 * 通过零部件编号查询零部件，并以XML格式返回属性信息
	 *
	 * @param number
	 * @return
	 */
	public String getMaterialByNumber(String number) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		try {
			WTPart part = WCUtil.getPartByNumber(number);
			if(part != null) {
				IBAUtility ibaUtility = new IBAUtility(part);
				String ctype = ibaUtility.getIBAValue("CTYPE");
				String ph = "";
				String gg = "";
				String jstj = "";
				String dw = "";


				List<String> attrs = new ArrayList<String>();
				attrs.add("PLATECSCREWFORM");//板拧形式
				attrs.add("SURFACETREATMENT");//表面处理
				attrs.add("PRODUCTLEVEL");//产品等级
				attrs.add("PRODUCTFORM");//产品型式
				attrs.add("PACKAGINGFORM");//封装形式
				attrs.add("EXTRACONDITION");//附加协议
				attrs.add("SUPPLYSTATE");//供应状态
				attrs.add("RATEOFCONVERSION");//换算率
				attrs.add("MECHANICALPROPERTYORHARDNESS");//机械性能等级或硬度
				attrs.add("PRECISION");//精度
				attrs.add("VARIETYSTANDARD");//品种规格标准
				attrs.add("HEATTREATMENT");//热处理
				attrs.add("ISIMPORT");//是否进口
				attrs.add("SPECIALINSTRUCTION");//特殊说明
				attrs.add("SCOPE");//适用范围
				attrs.add("OUTLINESIZE");//外形尺寸
				attrs.add("SHORTNAME");//物资简称
				attrs.add("RATIO");//系数
				attrs.add("DETAILSTANDARD");//详细规范
				attrs.add("TYPE");//型号
				attrs.add("QUALITYLEVEL");//质量等级
				attrs.add("QUALITYCHARACTER");//质量特征
				attrs.add("SPECIALCONDITION");//专用条件
				attrs.add("TOTALSTANDARD");//总规范
				attrs.add("SUPPLIERS");//总规范
				attrs.add("ClassificationNode");//分类


				if(ibaUtility.getIBAValue("MEASUREUNIT") != null) {
					dw = ibaUtility.getIBAValue("MEASUREUNIT");
				}
				if("金属材料".equals(ctype)
						|| "非金属材料".equals(ctype)
						|| "复合材料".equals(ctype)) {
					if(ibaUtility.getIBAValue("MARKNUMBER") != null) {
						ph = ibaUtility.getIBAValue("MARKNUMBER");
					}
					if(ibaUtility.getIBAValue("CMAT") != null) {
						ph = ph + " " +ibaUtility.getIBAValue("CMAT");
					}
					if(ibaUtility.getIBAValue("CSIZE") != null) {
						gg = ibaUtility.getIBAValue("CSIZE");
					}
					if(ibaUtility.getIBAValue("USESTANDARD") != null) {
						jstj = ibaUtility.getIBAValue("USESTANDARD");
					}
				} else if ("标准件".equals(ctype)) {
					if(ibaUtility.getIBAValue("MARKNUMBER") != null) {
						ph = ibaUtility.getIBAValue("MARKNUMBER");
					}
					if(ibaUtility.getIBAValue("CMAT") != null) {
						ph = ph + " " +ibaUtility.getIBAValue("CMAT");
					}
					if(ibaUtility.getIBAValue("CSIZE") != null) {
						gg = ibaUtility.getIBAValue("CSIZE");
					}
					if(ibaUtility.getIBAValue("STANDARDNUMBER") != null) {
						jstj = ibaUtility.getIBAValue("STANDARDNUMBER");
					}
				} else if ("元器件".equals(ctype)) {
					if(ibaUtility.getIBAValue("TYPE") != null) {
						ph = ibaUtility.getIBAValue("TYPE");
					}
					if(ibaUtility.getIBAValue("CMAT") != null) {
						ph = ph + " " +ibaUtility.getIBAValue("CMAT");
					}
					if(ibaUtility.getIBAValue("TYPESTANDARD") != null) {
						gg = ibaUtility.getIBAValue("TYPESTANDARD");
					}
					//jstj = ibaUtility.getIBAValue("USESTANDARD");
				}




				sb.append("<info>");
				sb.append("<number>");
				sb.append(part.getNumber());
				sb.append("</number>");
				sb.append("<name>");
				sb.append(part.getName());
				sb.append("</name>");
				sb.append("<ph>");
				sb.append(ph);
				sb.append("</ph>");
				sb.append("<gg>");
				sb.append(gg);
				sb.append("</gg>");
				sb.append("<jstj>");
				sb.append(jstj);
				sb.append("</jstj>");
				sb.append("<dw>");
				sb.append(dw);
				sb.append("</dw>");

				for(String a:attrs){
					if(ibaUtility.getIBAValue(a) != null) {
						sb.append("<").append(a.toLowerCase()).append(">");
						sb.append(ibaUtility.getIBAValue(a));
						sb.append("</").append(a.toLowerCase()).append(">");
					}else{
						sb.append("<").append(a.toLowerCase()).append(">");
						sb.append("");
						sb.append("</").append(a.toLowerCase()).append(">");
					}
				}
				//分类内部名称：classificationnode
				sb.append("</info>");
			} else {
				sb.append("<info>");
				sb.append("<error>");
				sb.append("所查询编码的材料不存在！");
				sb.append("</error>");
				sb.append("</info>");
			}
		} catch (WTException e) {
			sb.append("<info>");
			sb.append("<error>");
			sb.append(e.getLocalizedMessage());
			sb.append("</error>");
			sb.append("</info>");
			//e.printStackTrace();
		}
		return sb.toString();
	}

}
