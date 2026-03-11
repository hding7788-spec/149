package ext.casc.dingeInformation;

import com.glaway.mpm.util.ApacheZipUtil;
import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.util.Tools;
import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import wt.content.ApplicationData;
import wt.doc.WTDocument;
import wt.util.WTException;

import java.io.File;
import java.util.*;

@ComponentBuilder("ext.casc.dingeInformation.DingeInformationBuilder")
public class DingeInformationBuilder extends AbstractComponentBuilder {

	private static List<String> keys = new ArrayList<String>();

	static {
		keys.add("QMFawTechnicsInfo/CLDE/YCLDE/ycldeRecord");
		keys.add("QMFawTechnicsInfo/CLDE/ZYCLDE/zycldeRecord");
		keys.add("QMFawTechnicsInfo/CLDE/SJYCLDE/sjycldeRecord");
		keys.add("QMFawTechnicsInfo/CLDE/SJZYKYCLDE/ycldeRecord");
		keys.add("QMFawTechnicsInfo/CLDE/SJZYKZYCLDE/zycldeRecord");
		keys.add("QMFawTechnicsInfo/CLDE/SJZYKSJYCLDE/sjycldeRecord");
		keys.add("QMFawTechnicsInfo/GYDE/MATCHPART/MatchPart");
		keys.add("QMFawTechnicsInfo/GYDE/NEWPART/NewPart");
		keys.add("QMFawTechnicsInfo/GYDE/ZYCLDE/zycldeRecord");
		keys.add("QMFawTechnicsInfo/GYDE/SJYCLDE/sjycldeRecord");
		keys.add("QMFawTechnicsInfo/GYDE/SJZYKMATCHPART/SjzykMatchPart");
		keys.add("QMFawTechnicsInfo/GYDE/SJZYKNEWPART/SjzykNewPart");
	}

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
		NmCommandBean cb = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
		Object ob = cb.getPrimaryOid().getRefObject();
		if (ob instanceof WTDocument) {
			WTDocument doc = (WTDocument) ob;
			String number = doc.getNumber();
			ApplicationData appData = WTDocumentUtil.getPrimaryByDocument(doc);
			byte[] bytes = WTDocumentUtil.applicationDataToByte(appData);

			String tempFilePath = PropertiesUtil.getTempPath() + File.separator + "tempDinge" + File.separator + String.valueOf(new Date().getTime()) + File.separator;
			FileUtil.writeBytes(tempFilePath, appData.getFileName(), bytes);
			ApacheZipUtil.decompress(tempFilePath + appData.getFileName(), tempFilePath + number);
			File xmlFile = new File(tempFilePath + number + File.separator + number + ".xml");
			Map<String, List<org.dom4j.Element>> map = getDingEInformation(xmlFile);
			List<CldeBean> beanlist = getDingeData(map);
			FileUtil.deleteFile(new File(tempFilePath));
			return beanlist;

		}
		return null;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig tableConfig = factory.newTableConfig();
		tableConfig.setLabel("定额信息列表");
		// tableConfig.setSelectable(true);
		tableConfig.setConfigurable(true);
		tableConfig.setId("ext.casc.dingeInformation.DingeInformationBuilder");

		// ColumnConfig icon = factory.newColumnConfig("type_icon", false);
		// tableConfig.addComponent(icon);

		ColumnConfig dataTypeColumnConfig = factory.newColumnConfig("dataType", true);
		dataTypeColumnConfig.setAutoSize(true);
		dataTypeColumnConfig.setId("dataType");
		dataTypeColumnConfig.setLabel("数据类型");
		tableConfig.addComponent(dataTypeColumnConfig);

		ColumnConfig dataFromColumnConfig = factory.newColumnConfig("datafrom", true);
		dataFromColumnConfig.setAutoSize(true);
		dataFromColumnConfig.setId("datafrom");
		dataFromColumnConfig.setLabel("数据来源");
		tableConfig.addComponent(dataFromColumnConfig);

		ColumnConfig parentPartNumberColumnConfig = factory.newColumnConfig("parentPartNumber", true);
		parentPartNumberColumnConfig.setAutoSize(true);
		parentPartNumberColumnConfig.setId("parentPartNumber");
		parentPartNumberColumnConfig.setLabel("上级图号");
		tableConfig.addComponent(parentPartNumberColumnConfig);

		ColumnConfig partNumberColumnConfig = factory.newColumnConfig("partNumber", true);
		partNumberColumnConfig.setAutoSize(true);
		partNumberColumnConfig.setId("partNumber");
		partNumberColumnConfig.setLabel("图号");
		tableConfig.addComponent(partNumberColumnConfig);

		ColumnConfig sjbmColumnConfig = factory.newColumnConfig("sjbm", true);
		sjbmColumnConfig.setAutoSize(true);
		sjbmColumnConfig.setId("sjbm");
		sjbmColumnConfig.setLabel("存货编码");
		tableConfig.addComponent(sjbmColumnConfig);

		ColumnConfig nameColumnConfig = factory.newColumnConfig("name", true);
		nameColumnConfig.setLabel("名称");
		nameColumnConfig.setAutoSize(true);
		nameColumnConfig.setId("name");
		tableConfig.addComponent(nameColumnConfig);

		ColumnConfig wzjcColumnConfig = factory.newColumnConfig("wzjc", true);
		wzjcColumnConfig.setLabel("物资简称");
		wzjcColumnConfig.setAutoSize(true);
		wzjcColumnConfig.setId("wzjc");
		tableConfig.addComponent(wzjcColumnConfig);

		ColumnConfig xlccColumnConfig = factory.newColumnConfig("xlcc", true);
		xlccColumnConfig.setLabel("下料尺寸");
		xlccColumnConfig.setAutoSize(true);
		xlccColumnConfig.setId("xlcc");
		tableConfig.addComponent(xlccColumnConfig);

		ColumnConfig kzjsColumnConfig = factory.newColumnConfig("kzjs", true);
		kzjsColumnConfig.setLabel("可制件数");
		kzjsColumnConfig.setAutoSize(true);
		kzjsColumnConfig.setId("kzjs");
		tableConfig.addComponent(kzjsColumnConfig);

		ColumnConfig sjslColumnConfig = factory.newColumnConfig("sjsl", true);
		sjslColumnConfig.setLabel("设计数量");
		sjslColumnConfig.setAutoSize(true);
		sjslColumnConfig.setId("sjsl");
		tableConfig.addComponent(sjslColumnConfig);

		ColumnConfig gyslColumnConfig = factory.newColumnConfig("gysl", true);
		gyslColumnConfig.setLabel("工艺数量");
		gyslColumnConfig.setAutoSize(true);
		gyslColumnConfig.setId("gysl");
		tableConfig.addComponent(gyslColumnConfig);

		ColumnConfig dwColumnConfig = factory.newColumnConfig("dw", true);
		dwColumnConfig.setLabel("单位");
		dwColumnConfig.setAutoSize(true);
		dwColumnConfig.setId("dw");
		tableConfig.addComponent(dwColumnConfig);

		ColumnConfig hslColumnConfig = factory.newColumnConfig("hsl", true);
		hslColumnConfig.setLabel("换算率");
		hslColumnConfig.setAutoSize(true);
		hslColumnConfig.setId("hsl");
		tableConfig.addComponent(hslColumnConfig);

		ColumnConfig phColumnConfig = factory.newColumnConfig("ph", true);
		phColumnConfig.setLabel("牌号");
		phColumnConfig.setAutoSize(true);
		phColumnConfig.setId("ph");
		tableConfig.addComponent(phColumnConfig);

		ColumnConfig ggColumnConfig = factory.newColumnConfig("gg", true);
		ggColumnConfig.setLabel("规格");
		ggColumnConfig.setAutoSize(true);
		ggColumnConfig.setId("gg");
		tableConfig.addComponent(ggColumnConfig);

		ColumnConfig jstjColumnConfig = factory.newColumnConfig("jstj", true);
		jstjColumnConfig.setLabel("技术条件");
		jstjColumnConfig.setAutoSize(true);
		jstjColumnConfig.setId("jstj");
		tableConfig.addComponent(jstjColumnConfig);

		ColumnConfig sccjColumnConfig = factory.newColumnConfig("sccj", true);
		sccjColumnConfig.setLabel("生产厂家");
		sccjColumnConfig.setAutoSize(true);
		sccjColumnConfig.setId("sccj");
		tableConfig.addComponent(sccjColumnConfig);

		ColumnConfig zjldwColumnConfig = factory.newColumnConfig("jldw", true);
		zjldwColumnConfig.setLabel("主计量单位");
		zjldwColumnConfig.setAutoSize(true);
		zjldwColumnConfig.setId("jldw");
		tableConfig.addComponent(zjldwColumnConfig);

		ColumnConfig fjtjColumnConfig = factory.newColumnConfig("fjtj", true);
		fjtjColumnConfig.setLabel("附加条件");
		fjtjColumnConfig.setAutoSize(true);
		fjtjColumnConfig.setId("fjtj");
		tableConfig.addComponent(fjtjColumnConfig);

		ColumnConfig zldjColumnConfig = factory.newColumnConfig("zldj", true);
		zldjColumnConfig.setLabel("质量等级");
		zldjColumnConfig.setAutoSize(true);
		zldjColumnConfig.setId("zldj");
		tableConfig.addComponent(zldjColumnConfig);

		ColumnConfig fzxsColumnConfig = factory.newColumnConfig("fzxs", true);
		fzxsColumnConfig.setLabel("封装形式");
		fzxsColumnConfig.setAutoSize(true);
		fzxsColumnConfig.setId("fzxs");
		tableConfig.addComponent(fzxsColumnConfig);

		ColumnConfig cybzColumnConfig = factory.newColumnConfig("cybz", true);
		cybzColumnConfig.setLabel("采用标准");
		cybzColumnConfig.setAutoSize(true);
		cybzColumnConfig.setId("cybz");
		tableConfig.addComponent(cybzColumnConfig);

		ColumnConfig gyztColumnConfig = factory.newColumnConfig("gyzt", true);
		gyztColumnConfig.setLabel("供应状态");
		gyztColumnConfig.setAutoSize(true);
		gyztColumnConfig.setId("gyzt");
		tableConfig.addComponent(gyztColumnConfig);

		ColumnConfig jdColumnConfig = factory.newColumnConfig("jd", true);
		jdColumnConfig.setLabel("精度");
		jdColumnConfig.setAutoSize(true);
		jdColumnConfig.setId("jd");
		tableConfig.addComponent(jdColumnConfig);

		ColumnConfig jxxndjColumnConfig = factory.newColumnConfig("jxxndj", true);
		jxxndjColumnConfig.setLabel("机械性能等级");
		jxxndjColumnConfig.setAutoSize(true);
		jxxndjColumnConfig.setId("jxxndj");
		tableConfig.addComponent(jxxndjColumnConfig);

		ColumnConfig dcstxyqColumnConfig = factory.newColumnConfig("dcstxyq", true);
		dcstxyqColumnConfig.setLabel("电参考特选要求");
		dcstxyqColumnConfig.setAutoSize(true);
		dcstxyqColumnConfig.setId("dcstxyq");
		tableConfig.addComponent(dcstxyqColumnConfig);

		ColumnConfig zltzColumnConfig = factory.newColumnConfig("zltz", true);
		zltzColumnConfig.setLabel("质量特征");
		zltzColumnConfig.setAutoSize(true);
		zltzColumnConfig.setId("zltz");
		tableConfig.addComponent(zltzColumnConfig);

		ColumnConfig pzggbzColumnConfig = factory.newColumnConfig("pzggbz", true);
		pzggbzColumnConfig.setLabel("品种规格标准");
		pzggbzColumnConfig.setAutoSize(true);
		pzggbzColumnConfig.setId("pzggbz");
		tableConfig.addComponent(pzggbzColumnConfig);

		ColumnConfig bmclColumnConfig = factory.newColumnConfig("bmcl", true);
		bmclColumnConfig.setLabel("表面处理");
		bmclColumnConfig.setAutoSize(true);
		bmclColumnConfig.setId("bmcl");
		tableConfig.addComponent(bmclColumnConfig);

		ColumnConfig rclColumnConfig = factory.newColumnConfig("rcl", true);
		rclColumnConfig.setLabel("热处理");
		rclColumnConfig.setAutoSize(true);
		rclColumnConfig.setId("rcl");
		tableConfig.addComponent(rclColumnConfig);

		ColumnConfig cpxsColumnConfig = factory.newColumnConfig("cpxs", true);
		cpxsColumnConfig.setLabel("产品形式");
		cpxsColumnConfig.setAutoSize(true);
		cpxsColumnConfig.setId("cpxs");
		tableConfig.addComponent(cpxsColumnConfig);

		ColumnConfig cpdjColumnConfig = factory.newColumnConfig("cpdj", true);
		cpdjColumnConfig.setLabel("产品等级");
		cpdjColumnConfig.setAutoSize(true);
		cpdjColumnConfig.setId("cpdj");
		tableConfig.addComponent(cpdjColumnConfig);

		return tableConfig;
	}

	public static Map<String, List<org.dom4j.Element>> getDingEInformation(File file) throws DocumentException {
		Map<String, List<org.dom4j.Element>> dingeMap = new HashMap<String, List<org.dom4j.Element>>();
		if (!file.exists()) {
			System.out.println(file + " is not exist!");
			return null;
		}
		SAXReader reader = new SAXReader();
		org.dom4j.Document document = reader.read(file);
		org.dom4j.Element rootElement = document.getRootElement();
		for (String key : keys) {
			List<org.dom4j.Element> dinge = rootElement.selectNodes(key);
			if (dinge != null && dinge.size() > 0) {
				List<org.dom4j.Element> list = new ArrayList<org.dom4j.Element>();
				for (org.dom4j.Element ele : dinge) {
					list.add(ele);
				}
				dingeMap.put(key, list);
			}

		}

		return dingeMap;
	}

	public static List<CldeBean> getDingeData(Map<String, List<org.dom4j.Element>> map) {
		List<CldeBean> cldebeans = new ArrayList<CldeBean>();
		Iterator<String> its = map.keySet().iterator();
		while (its.hasNext()) {
			String key = its.next();
			System.out.println("its.next==========" + key);
			List<org.dom4j.Element> elements = map.get(key);
			if (elements != null && elements.size() > 0) {
				if (key.equals("QMFawTechnicsInfo/CLDE/SJZYKYCLDE/ycldeRecord") || key.equals("QMFawTechnicsInfo/CLDE/SJZYKZYCLDE/zycldeRecord")
						|| key.equals("QMFawTechnicsInfo/CLDE/SJZYKSJYCLDE/sjycldeRecord") || key.equals("QMFawTechnicsInfo/GYDE/SJZYKMATCHPART/SjzykMatchPart")
						|| key.equals("QMFawTechnicsInfo/GYDE/SJZYKNEWPART/SjzykNewPart")) {
					for (org.dom4j.Element element : elements) {
						CldeBean clde = new CldeBean();

						clde.setDataType(getCompatibleValue(element, "dataType"));
						clde.setSjbm(getCompatibleValue(element, "sjbm"));
						clde.setName(getCompatibleValue(element, "name"));
						clde.setWzjc(getCompatibleValue(element, "wzjc"));
						clde.setBmyyjb(getCompatibleValue(element, "bmyyjb"));
						clde.setBmzt(getCompatibleValue(element, "bmzt"));
						clde.setXh(getCompatibleValue(element, "xh"));
						clde.setCllx(getCompatibleValue(element, "cllx"));
						clde.setHsl(getCompatibleValue(element, "hsl"));
						clde.setPh(getCompatibleValue(element, "ph"));
						clde.setBzh(getCompatibleValue(element, "bzh"));
						clde.setGg(getCompatibleValue(element, "gg"));
						clde.setGyzt(getCompatibleValue(element, "gyzt"));
						clde.setCybz(getCompatibleValue(element, "cybz"));
						clde.setJd(getCompatibleValue(element, "jd"));
						clde.setZltz(getCompatibleValue(element, "zltz"));
						clde.setPzggbz(getCompatibleValue(element, "pzggbz"));
						clde.setCl(getCompatibleValue(element, "cl"));
						clde.setXhgg(getCompatibleValue(element, "xhgg"));
						clde.setZldj(getCompatibleValue(element, "zldj"));
						clde.setZgf(getCompatibleValue(element, "zgf"));
						clde.setXxgf(getCompatibleValue(element, "xxgf"));

						clde.setFzxs(getCompatibleValue(element, "fzxs"));
						clde.setWxcc(getCompatibleValue(element, "wxcc"));
						clde.setZytj(getCompatibleValue(element, "zytj"));
						clde.setFjxy(getCompatibleValue(element, "fjxy"));
						clde.setJxxndjhyd(getCompatibleValue(element, "jxxndjhyd"));
						clde.setBmcl(getCompatibleValue(element, "bmcl"));
						clde.setRcl(getCompatibleValue(element, "rcl"));
						clde.setCpxs(getCompatibleValue(element, "cpxs"));
						clde.setCpdj(getCompatibleValue(element, "cpdj"));

						clde.setBnxs(getCompatibleValue(element, "bnxs"));
						clde.setTssm(getCompatibleValue(element, "tssm"));
						clde.setSfjk(getCompatibleValue(element, "sfjk"));
						clde.setJldw(getCompatibleValue(element, "jldw"));
						clde.setSyfw(getCompatibleValue(element, "syfw"));
						clde.setGysl(getCompatibleValue(element, "gysl"));
						clde.setDw(getCompatibleValue(element, "dw"));
						clde.setSjsl(getCompatibleValue(element, "sjsl"));
						clde.setKzjs(getCompatibleValue(element, "kzjs"));
						clde.setNumber(getCompatibleValue(element, "number"));
						clde.setXlcc(getCompatibleValue(element, "xlcc"));
						clde.setSl(getCompatibleValue(element, "sl"));
						clde.setSjkzjs(getCompatibleValue(element, "sjkzjs"));
						clde.setSjcc(getCompatibleValue(element, "sjcc"));
						clde.setPartNumber(getCompatibleValue(element, "partNumber"));
						clde.setParentPartNumber(getCompatibleValue(element, "parentPartNumber"));
						// clde.setDatafrom(getCompatibleValue(element,"dataFrom"));

						if (key.equals("QMFawTechnicsInfo/GYDE/SJZYKNEWPART/SjzykNewPart")||key.equals("QMFawTechnicsInfo/GYDE/SJZYKMATCHPART/SjzykMatchPart")) {
							clde.setCybz(getCompatibleValue(element, "bzh"));
						}
						if (key.equals("QMFawTechnicsInfo/CLDE/SJZYKYCLDE/ycldeRecord")) {
							clde.setDatafrom("设计资源库（原材料定额）");
							clde.setGysl(getCompatibleValue(element, "sjsl"));

						} else if (key.equals("QMFawTechnicsInfo/CLDE/SJZYKZYCLDE/zycldeRecord")) {
							clde.setDatafrom("设计资源库（主要料定额）");
							clde.setGysl(getCompatibleValue(element, "sl"));
						} else if (key.equals("QMFawTechnicsInfo/CLDE/SJZYKSJYCLDE/sjycldeRecord")) {
							clde.setDatafrom("设计资源库（试件原材料定额）");
							clde.setGysl(getCompatibleValue(element, "sl"));
							clde.setXlcc(getCompatibleValue(element, "sjcc"));
							clde.setKzjs(getCompatibleValue(element, "sjkzjs"));
						} else if (key.equals("QMFawTechnicsInfo/GYDE/SJZYKMATCHPART/SjzykMatchPart")) {
							clde.setDatafrom("设计资源库（工艺定额匹配）");
						} else if (key.equals("QMFawTechnicsInfo/GYDE/SJZYKNEWPART/SjzykNewPart")) {
							clde.setDatafrom("设计资源库（工艺定额新增）");
						}
						cldebeans.add(clde);
					}
				} else if (key.equals("QMFawTechnicsInfo/CLDE/YCLDE/ycldeRecord") || key.equals("QMFawTechnicsInfo/CLDE/ZYCLDE/zycldeRecord")
						|| key.equals("QMFawTechnicsInfo/CLDE/SJYCLDE/sjycldeRecord") || key.equals("QMFawTechnicsInfo/GYDE/MATCHPART/MatchPart")
						|| key.equals("QMFawTechnicsInfo/GYDE/NEWPART/NewPart") || key.equals("QMFawTechnicsInfo/GYDE/ZYCLDE/zycldeRecord")
						|| key.equals("QMFawTechnicsInfo/GYDE/SJYCLDE/sjycldeRecord")) {
					for (org.dom4j.Element element : elements) {
						CldeBean clde = new CldeBean();

						clde.setSjbm(getCompatibleValue(element, "chbm"));
						clde.setName(getCompatibleValue(element, "chmc"));
						clde.setXlcc(getCompatibleValue(element, "xlcc"));
						clde.setKzjs(getCompatibleValue(element, "kzjs"));
						clde.setDw(getCompatibleValue(element, "dw"));
						clde.setPh(getCompatibleValue(element, "xhph"));
						clde.setGg(getCompatibleValue(element, "gg"));
						clde.setJstj(getCompatibleValue(element, "jstj"));
						clde.setSccj(getCompatibleValue(element, "sccj"));
						clde.setJldw(getCompatibleValue(element, "zjldw"));
						clde.setFjxy(getCompatibleValue(element, "fjtj"));
						clde.setGyzt(getCompatibleValue(element, "gyztrcl"));
						clde.setZldj(getCompatibleValue(element, "zldj"));
						clde.setFzxs(getCompatibleValue(element, "fzxs"));
						clde.setJd(getCompatibleValue(element, "jddj"));
//						clde.setGg(getCompatibleValue(element, "lwgg"));
						clde.setJxxndjhyd(getCompatibleValue(element, "jxxndj"));
						clde.setDcstxyq(getCompatibleValue(element, "dcstxyq"));
						clde.setComment(getCompatibleValue(element, "comment"));
						// clde.setDatafrom(getCompatibleValue(element,"dataFrom"));
						clde.setPartNumber(getCompatibleValue(element, "number"));
						clde.setParentPartNumber(getCompatibleValue(element, "parentNumber"));
						clde.setDataType(getCompatibleValue(element, "mtype"));

						if (key.equals("QMFawTechnicsInfo/CLDE/YCLDE/ycldeRecord")) {
							clde.setDatafrom("ERP（原材料定额）");
						} else if (key.equals("QMFawTechnicsInfo/CLDE/ZYCLDE/zycldeRecord") || key.equals("QMFawTechnicsInfo/GYDE/ZYCLDE/zycldeRecord")) {
							clde.setDatafrom("ERP（主要料定额）");
							clde.setGysl(getCompatibleValue(element, "sl"));
						} else if (key.equals("QMFawTechnicsInfo/CLDE/SJYCLDE/sjycldeRecord") || key.equals("QMFawTechnicsInfo/GYDE/SJYCLDE/sjycldeRecord")) {
							clde.setDatafrom("ERP（试件原材料定额）");
							clde.setXlcc(getCompatibleValue(element, "sjcc"));
							clde.setKzjs(getCompatibleValue(element, "sjkzjs"));
							clde.setGysl(getCompatibleValue(element, "sjsl"));
						} else if (key.equals("QMFawTechnicsInfo/GYDE/MATCHPART/MatchPart")) {
							clde.setDatafrom("ERP（工艺定额匹配）");
							clde.setGysl(getCompatibleValue(element, "gyCount"));
							clde.setSjsl(getCompatibleValue(element, "useCount"));
							clde.setDw(getCompatibleValue(element, "dw2"));
						} else if (key.equals("QMFawTechnicsInfo/GYDE/NEWPART/NewPart")) {
							clde.setDatafrom("ERP（工艺定额新增）");
							clde.setPartNumber(getCompatibleValue(element, "chbm"));
							clde.setDw(getCompatibleValue(element, "dw2"));
							clde.setGysl(getCompatibleValue(element, "sl"));
						}

						cldebeans.add(clde);

					}

				}
			}

		}
		return cldebeans;
	}

	public static String getCompatibleValue(org.dom4j.Element element, String name) {
		if("gysl".equals(name)){
			String value = CheckValue(element,name);
			if(Tools.isNull(value)){
				return CheckValue(element,"sl");
			}else{
				return value;
			}
		}else if("dw2".equals(name)){
			String value = CheckValue(element,name);
			if(Tools.isNull(value)){
				return CheckValue(element,"dw");
			}else{
				return value;
			}
		}else{
			return CheckValue(element,name);
		}
	}

	public static String CheckValue(org.dom4j.Element element, String name) {

		Attribute attr = element.attribute(name);

		if (attr == null) {
			return "";
		}
		String value = element.attributeValue(name);

		if (null == value || "null".equals(value) || " ".equals(value)) {
			value = "";
		}
		if("BZJ".equals(value)){
			return "标准件";
		}
		if("YQJ".equals(value)){
			return "元器件";
		}
		return value;
	}

}
