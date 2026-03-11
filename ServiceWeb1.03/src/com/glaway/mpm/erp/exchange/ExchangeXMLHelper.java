package com.glaway.mpm.erp.exchange;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.util.WTException;

import com.glaway.mpm.util.WTPartUtil;

/**
 * @author lbzhang
 * 
 */
@SuppressWarnings("unused")
public class ExchangeXMLHelper {
	public static final String TECH_STEP_PATH = "/technics/QMFawTechnicsInfo/steps/QMProcedureInfo";
	public static final String TECH_QMFawTechnicsInfo = "/technics/QMFawTechnicsInfo";
	public static final String TECH_TOOLS_PATH = "/technics/QMFawTechnicsInfo/steps/QMProcedureInfo/tools/QMToolInfo";
	public static final String TECH_PACES_TOOLS_PATH = "/technics/QMFawTechnicsInfo/steps/QMProcedureInfo/paces/QMProcedureInfo/tools/QMToolInfo";
	public static final String TECH_MATERIALS_PATH = "/technics/QMFawTechnicsInfo/steps/QMProcedureInfo/materials/QMMaterialInfo";

	public static Element createBillHeadForAssembleBomTools(WTPart part)
			throws WTException {

		Element billHead = DocumentHelper.createElement("billhead");
		Element bblx = billHead.addElement("bblx");// 有效版本0，无效版本1
		bblx.setText("0");
		Element bomlx = billHead.addElement("bomlx");// BOM类型
		bomlx.setText("0");
		Element creator = billHead.addElement("creator"); // 工号（零件的创建者）
		creator.setText(part.getCreator().getName());
		Element sl = billHead.addElement("sl");
		// TODO 添加数量
		Element wlbm = billHead.addElement("wlbm");// 物料编码（图号）
		wlbm.setText(part.getNumber());
		Element wlmc = billHead.addElement("wlmc");// 物料名称（图号对应的名称）
		wlmc.setText(part.getName());
		Element version = billHead.addElement("version");
		version.setText(part.getVersionIdentifier().getValue() + "."
				+ part.getIterationIdentifier().getValue());
		Element pk_jobmngfil = billHead.addElement("pk_jobmngfil"); // 次节点保持空，暂不上传
		Element dr = billHead.addElement("dr"); // 默认值0，pds产生xml时，只设置默认值即可
		dr.setText("0");
		return billHead;
	}

	public static Element createBillHeadForBomTools(WTPart part)
			throws WTException {

		Element billHead = DocumentHelper.createElement("billhead");
		Element bblx = billHead.addElement("bblx");// 有效版本0，无效版本1
		bblx.setText("0");
		Element bomlx = billHead.addElement("bomlx");// BOM类型
		bomlx.setText("0");
		Element creator = billHead.addElement("creator"); // 工号（零件的创建者）
		creator.setText(part.getCreator().getName());
		Element sl = billHead.addElement("sl");
		// TODO 添加数量
		Element wlbm = billHead.addElement("wlbm");// 物料编码（图号）
		wlbm.setText(part.getNumber());
		Element wlmc = billHead.addElement("wlmc");// 物料名称（图号对应的名称）
		wlmc.setText(part.getName());
		Element version = billHead.addElement("version");
		version.setText(part.getVersionIdentifier().getValue() + "."
				+ part.getIterationIdentifier().getValue());
		Element pk_jobmngfil = billHead.addElement("pk_jobmngfil"); // 次节点保持空，暂不上传
		Element dr = billHead.addElement("dr"); // 默认值0，pds产生xml时，只设置默认值即可
		dr.setText("0");
		return billHead;
	}

	/**
	 * @author Double
	 * @date 2013-5-23
	 * @param part
	 * @return Element
	 * @throws WTException
	 * 
	 */
	public static Element createBillHeadForAssembleBom(WTPart part)
			throws WTException {

		Element billHead = DocumentHelper.createElement("billhead");
		Element bblx = billHead.addElement("bblx");// 有效版本0，无效版本1
		bblx.setText("0");
		Element bomlx = billHead.addElement("bomlx");// BOM类型
		bomlx.setText("0");
		Element creator = billHead.addElement("creator"); // 工号（零件的创建者）
		creator.setText(part.getCreator().getName());
		Element sl = billHead.addElement("sl");
		// TODO 添加数量
		Element wlbm = billHead.addElement("wlbm");// 物料编码（图号）
		wlbm.setText(part.getNumber());
		Element wlmc = billHead.addElement("wlmc");// 物料名称（图号对应的名称）
		wlmc.setText(part.getName());
		Element version = billHead.addElement("version");
		version.setText(part.getVersionIdentifier().getValue() + "."
				+ part.getIterationIdentifier().getValue());
		Element pk_jobmngfil = billHead.addElement("pk_jobmngfil"); // 次节点保持空，暂不上传
		Element dr = billHead.addElement("dr"); // 默认值0，pds产生xml时，只设置默认值即可
		dr.setText("0");
		return billHead;
	}

	public static Element createBillHeadForPartBom(WTPart part)
			throws WTException {

		Element billHead = DocumentHelper.createElement("billhead");
		Element bblx = billHead.addElement("bblx");// 有效版本0，无效版本1
		bblx.setText("0");
		Element bomlx = billHead.addElement("bomlx");// BOM类型
		bomlx.setText("0");
		Element creator = billHead.addElement("creator"); // 工号（零件的创建者）
		creator.setText(part.getCreator().getName());
		Element sl = billHead.addElement("sl");
		// TODO 添加数量
		Element wlbm = billHead.addElement("wlbm");// 物料编码（图号）
		wlbm.setText(part.getNumber());
		Element wlmc = billHead.addElement("wlmc");// 物料名称（图号对应的名称）
		wlmc.setText(part.getName());
		Element version = billHead.addElement("version");
		version.setText(part.getVersionIdentifier().getValue() + "."
				+ part.getIterationIdentifier().getValue());
		Element pk_jobmngfil = billHead.addElement("pk_jobmngfil"); // 次节点保持空，暂不上传
		Element dr = billHead.addElement("dr"); // 默认值0，pds产生xml时，只设置默认值即可
		dr.setText("0");
		return billHead;
	}

	/**
	 * @author double
	 * @date 2013-5-23
	 * @param part
	 * @return
	 * @throws WTException
	 * 
	 */
	public static Element createBillBodyForAssembleBom(WTPart part)
			throws WTException {

		Element billBody = DocumentHelper.createElement("billbody");
		List<WTPart> listChild = WTPartUtil.getChildPart(part);
		for (WTPart child : listChild) {
			if (WTPartUtil.getChildPart(child).size() == 0) {
				// 子零件 而不是子组件
				Element entry = billBody.addElement("entry");
				Element zxbm = entry.addElement("zxbm");// 子节零件编号，如果外购就时物资编码
				zxbm.setText(child.getNumber());
				Element zxmc = entry.addElement("zxmc");// 子节零件名称
				zxmc.setText(child.getName());
				Element billBody_sl = entry.addElement("sl");// 数量
				WTPartUsageLink link = WTPartUtil.getWTPartUsageLink(part,
						(WTPartMaster) child.getMaster());
				Double qty = link.getQuantity().getAmount();
				billBody_sl.setText(qty.toString());
				Element billBody_dr = entry.addElement("dr"); // 标识
																// ,默认值0，pds产生xml时，只设置默认值即可
				billBody_dr.setText("0");
			}
		}
		return billBody;
	}

	/**
	 * @author Double
	 * @date 2013-5-23
	 * @param part
	 * @param techDoc
	 * @return
	 * @throws WTException
	 * 
	 */
	public static Element createBillBodyForAssembleBomTools(WTPart part,
			Document techDoc) throws WTException {

		List<Node> listTools = techDoc
				.selectNodes(ExchangeXMLHelper.TECH_TOOLS_PATH);
		listTools.addAll(techDoc
				.selectNodes(ExchangeXMLHelper.TECH_PACES_TOOLS_PATH));

		Element billBody = DocumentHelper.createElement("billbody");
		for (Node toolNode : listTools) {
			Element tool = (Element) toolNode;
			Element entry = billBody.addElement("entry");

			Element toolNum = entry.addElement("toolNum");
			toolNum.setText(tool.attributeValue("toolNum"));

			Element toolName = entry.addElement("toolName");
			toolName.setText(tool.attributeValue("toolName"));

			Element toolType = entry.addElement("toolType");
			toolType.setText(tool.attributeValue("toolType"));

			Element useCount = entry.addElement("useCount");
			useCount.setText(tool.attributeValue("useCount", ""));

			Element billBody_dr = entry.addElement("dr"); // 标识
															// ,默认值0，pds产生xml时，只设置默认值即可
			billBody_dr.setText("0");
		}
		return billBody;
	}

	public static Element createBillBodyForBomTools(WTPart part,
			Document techDoc) throws WTException {

		List<Node> listTools = techDoc
				.selectNodes(ExchangeXMLHelper.TECH_TOOLS_PATH);
		listTools.addAll(techDoc
				.selectNodes(ExchangeXMLHelper.TECH_PACES_TOOLS_PATH));

		Element billBody = DocumentHelper.createElement("billbody");
		for (Node toolNode : listTools) {
			Element tool = (Element) toolNode;
			Element entry = billBody.addElement("entry");

			Element toolNum = entry.addElement("toolNum");
			toolNum.setText(tool.attributeValue("toolNum"));

			Element toolName = entry.addElement("toolName");
			toolName.setText(tool.attributeValue("toolName"));

			Element toolType = entry.addElement("toolType");
			toolType.setText(tool.attributeValue("toolType"));

			Element useCount = entry.addElement("useCount");
			useCount.setText(tool.attributeValue("useCount", ""));

			Element billBody_dr = entry.addElement("dr"); // 标识
															// ,默认值0，pds产生xml时，只设置默认值即可
			billBody_dr.setText("0");
		}
		return billBody;
	}

	public static Element createBillBodyForPartBom(WTPart part, Document techDoc)
			throws WTException {

		Element billBody = DocumentHelper.createElement("billbody");
		List<Node> listMaterialNodes = techDoc
				.selectNodes(ExchangeXMLHelper.TECH_MATERIALS_PATH);

		for (Node materialNode : listMaterialNodes) {
			Element material = (Element) materialNode;

			Element entry = billBody.addElement("entry");

			Element materialNumber = entry.addElement("materialNumber");
			materialNumber.setText(material.attributeValue("materialNumber"));

			Element materialName = entry.addElement("materialName");
			materialName.setText(material.attributeValue("materialName"));

			Element materialCrision = entry.addElement("materialCrision");
			materialCrision.setText(material.attributeValue("materialCrision"));

			Element materialType = entry.addElement("materialType");
			materialType.setText(material.attributeValue("materialType"));

			Element materialBrand = entry.addElement("materialBrand");
			materialBrand.setText(material.attributeValue("materialBrand"));

			Element materialCategory = entry.addElement("materialCategory");
			materialCategory.setText(material
					.attributeValue("materialCategory"));

			Element materialUnit = entry.addElement("materialUnit");
			materialUnit.setText(material.attributeValue("materialUnit"));

			Element singleCount = entry.addElement("singleCount");
			singleCount.setText(material.attributeValue("singleCount", ""));

			Element materialQuota = entry.addElement("materialQuota");
			materialQuota.setText(material.attributeValue("materialQuota", ""));

			Element billBody_dr = entry.addElement("dr"); // 标识
															// ,默认值0，pds产生xml时，只设置默认值即可
			billBody_dr.setText("0");
		}
		return billBody;
	}

	/**
	 * @author Double
	 * @date 2013-5-23
	 * @param part
	 * @return
	 * @throws WTException
	 * 
	 */
	public static Document createMainDocument(WTPart part, String fName) throws WTException {
		Document doc = DocumentHelper.createDocument();
		Element uf = doc.addElement("ufinterface");
		Element rootTag = uf.addElement("roottag"); // 固定值:bill
		rootTag.setText("bill");
		Element billType = uf.addElement("billtype"); // 固定值:bom
		billType.setText("bom");
		Element replace = uf.addElement("replace");// Y/N Y
		replace.setText("Y");
		Element receiver = uf.addElement("receiver"); // 0001
		receiver.setText("0001");
		Element sender = uf.addElement("sender"); // 0001
		sender.setText("0001");
		Element isExchange = uf.addElement("isexchange"); // Y/N Y
		isExchange.setText("Y");
		Element fileName = uf.addElement("filename"); // xml文件的文件名，包含文件后缀名
														// partNumber + PartName
														// + version
		fileName.setText(fName);
		Element proc = uf.addElement("proc");// 固定设值为 "add"
		proc.setText("add");
		Element bill = uf.addElement("bill");
		return doc;
	}

	/**
	 * @author double
	 * @date 2013-5-23
	 * @param part
	 * @param techDoc
	 * @return
	 * @throws WTException
	 * 
	 */
	public static Element createBillHeadForProcessRouteTime(WTPart part,
			Document techDoc) throws WTException {

		List<Node> listTechInfor = techDoc
				.selectNodes(ExchangeXMLHelper.TECH_QMFawTechnicsInfo);
		Element techInfor = (Element) listTechInfor.get(0);

		// 创建head
		Element billHead = DocumentHelper.createElement("billhead");

		Element artbfnum = billHead.addElement("artbfnum");// 备份比例
		artbfnum.setText(techInfor.attributeValue("backupRate"));

		Element zdbfsl = billHead.addElement("zdbfsl");// 工艺最大备份件数
		artbfnum.setText(techInfor.attributeValue("maxBackupCount"));

		Element artcatgory = billHead.addElement("artcatgory"); // 工艺类型 生产0，调试1
		artcatgory.setText("0");

		Element bblx = billHead.addElement("bblx"); // 有效版本0，无效版本1
		bblx.setText("0");

		Element designerid = billHead.addElement("designerid"); // 设计师 工号
		designerid.setText(techInfor.attributeValue("creator"));

		Element gcbm = billHead.addElement("gcbm"); // 工厂编码 固定值
		gcbm.setText("固定值");

		Element gylxlx = billHead.addElement("gylxlx"); // 工艺路线类型 0/1默认为1
		gylxlx.setText("1");

		Element memo = billHead.addElement("memo"); // 备注
		memo.setText("备注");

		Element sfmr = billHead.addElement("sfmr"); // 是否默认版本 Y/N
		sfmr.setText("Y");

		Element sfgs = billHead.addElement("sfgs"); // 是否同批默认 Y/N
		sfgs.setText("Y");

		Element sl = billHead.addElement("sl"); // 数量
		sl.setText(techInfor.attributeValue("partCount", ""));

		Element th = billHead.addElement("th"); // 图号
		th.setText(techInfor.attributeValue("parentPartNumber", ""));

		Element version = billHead.addElement("version");
		version.setText(techInfor.attributeValue("partVersion"));

		Element wlbm = billHead.addElement("eu_number");// 物料编码（图号）
		wlbm.setText(part.getNumber());

		Element wlmc = billHead.addElement("wlmc");// 物料名称（图号对应的名称）
		wlmc.setText(part.getName());

		Element sfjjd = billHead.addElement("sfjjd"); // 是否交接点 默认直
		sfjjd.setText("0");

		Element zdy19 = billHead.addElement("zdy19"); //
		zdy19.setText("zdy19");

		Element zdy20 = billHead.addElement("zdy20");
		zdy20.setText("zdy20");

		Element dr = billHead.addElement("dr"); // 默认值0，pds产生xml时，只设置默认值即可
		dr.setText("0");

		return billHead;
	}

	public static Element createBillBodyForProcessRouteTime(WTPart part,
			Document techDoc) throws WTException {

		// 创建body
		Element billBody = DocumentHelper.createElement("billbody");
		List<Node> listTechSteps = techDoc
				.selectNodes(ExchangeXMLHelper.TECH_STEP_PATH);
		for (Node step : listTechSteps) {
			Element stepElement = (Element) step;
			// 添加 entry 节点
			Element entry = billBody.addElement("entry");

			Element checkmode = entry.addElement("checkmode"); // 首检方式 默认33
			checkmode.setText("33");

			Element gxh = entry.addElement("gxh"); // 工序号
			gxh.setText(stepElement.attributeValue("stepNumber"));

			Element gyms = entry.addElement("gyms"); // 工序名称
			gyms.setText(stepElement.attributeValue("stepName"));

			Element gzzxbm = entry.addElement("gzzxbm"); // 工作中心编码
			gzzxbm.setText(stepElement.attributeValue("workShopID") + "_"
					+ stepElement.attributeValue("workTypeID"));

			Element gzzxmc = entry.addElement("gzzxmc"); // 工作中心名称
			gzzxmc.setText("空");

			Element xhsl = entry.addElement("xhsl"); // 工装数量
			xhsl.setText("工装数量");

			Element primarytype = entry.addElement("primarytype"); // 关键工序 Y/N
			primarytype.setText(stepElement.attributeValue("isKey", ""));

			Element zdy1 = entry.addElement("zdy1"); // 4
			zdy1.setText("4");

			Element pk_work_type = entry.addElement("pk_work_type"); // 空值
			pk_work_type.setText("");

			Element dr = entry.addElement("dr"); // 默认值0，pds产生xml时，只设置默认值即可
			dr.setText("0");

			Element zbsj = entry.addElement("zbsj"); // 准备时间 默认单位小时
			zbsj.setText(stepElement.attributeValue("PrepareWorkHours", ""));

			Element jgsj = entry.addElement("jgsj"); // 加工时间 默认单位小时
			jgsj.setText(stepElement.attributeValue("TaktTime", ""));

			Element gdzqpl = entry.addElement("gdzqpl"); // 每组数量
			gdzqpl.setText(stepElement.attributeValue("NumberOfGroup", ""));

			Element sfjjd = entry.addElement("sfjjd"); // 是否交接点
			sfjjd.setText("Y");
		}

		return billBody;
	}

	public static void saveDocument(Document document, File outputXml) {
		try {
			// 美化格式
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("GBK");
			/*
			 * // 缩减格式 OutputFormat format = OutputFormat.createCompactFormat();
			 */
			/*
			 * // 指定XML编码 format.setEncoding("GBK");
			 */
			XMLWriter output = new XMLWriter(new FileWriter(outputXml), format);
			output.write(document);
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args) throws WTException {
		File file = new File("c:\\middle_bom.xml");
		// xml 文件保存 名字 上加上part number + version No
		ExchangeXMLHelper helper = new ExchangeXMLHelper();
		// helper.saveDocument(helper.createBomDocument(new WTPart()), file);
	}

}
