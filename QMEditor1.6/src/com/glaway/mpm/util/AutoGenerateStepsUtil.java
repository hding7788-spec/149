package com.glaway.mpm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.server.UID;
import java.util.List;

import javax.swing.JOptionPane;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.TechnicsIntf;

public class AutoGenerateStepsUtil {

	public static final String WRL_EXTEND = ".wrl";
	private static VaLogger logger = VaLogger.getLogger(AutoGenerateStepsUtil.class);

	public static void autoGenerateSteps(Element techElement,
			String cortonaXmlFilePath) throws Exception {
		Document doc = getDocument(cortonaXmlFilePath);
		if (doc == null)
			throw new Exception("XML文件转换出现错误");
		List userlist = UserUtil.getCurrentUserOid();

		String creatorOid = "";
		if ((userlist != null) && (userlist.size() == 3)) {
			String creator = (String) userlist.get(0);
			creatorOid = (String) userlist.get(1);
			logger.debug("设置当前工序责任人为======" + creatorOid);
		}
		String partOid = techElement.attributeValue("partOid");
		String responserGroup = "";
		if (partOid != null) {
			responserGroup = TechnicsIntf.getUsertechnicsGroupName(partOid);
			logger.debug("当前工艺零件======" + partOid + "===责任组==" + responserGroup);
			if (responserGroup == null) {
				responserGroup = "";
			}
		}
		Element rootElement = doc.getRootElement();
		Element simuElement = rootElement.element("Simulation");
		String simuID = simuElement.attributeValue("id");
		techElement.setAttributeValue("cortonaID", simuID);
		Element simuInfoElement = rootElement.element("SimulationInformation");
		String wrlFileName = simuInfoElement.elementText("FileName");
		techElement.setAttributeValue("wrlFile", wrlFileName);

		String technicsNumber = techElement.attributeValue("technicsNumber");
		String technicsPath = WorkSpaceUtil
				.getTechnicsDirectory(technicsNumber);
		String targetFilePath = technicsPath + File.separator + wrlFileName
				+ ".wrl";
		File cortonaXmlFile = new File(cortonaXmlFilePath);
		String parentPath = cortonaXmlFile.getParent();
		String wrlFilePath = parentPath + File.separator + wrlFileName + ".wrl";
		File f = new File(wrlFilePath);
		if (!f.exists()) {
			JOptionPane.showMessageDialog(null, "动画文件不存在，不能进行自动创建工序操作！", "提示",
					1);
			return;
		}
		copyFile(wrlFilePath, targetFilePath);

		List stepList = simuElement.elements("Step");

		Element stepElement = null;

		Element subStepElement = null;

		Element procedureELement = null;

		Element subProcedureELement = null;

		List list = XmlUtility.getProcedures(techElement).elements();
		int insertStepLocation = -1;

		for (int i = 0; i < stepList.size(); i++) {
			stepElement = (Element) stepList.get(i);
			String stepCortonaID = stepElement.attributeValue("id");
			Element step = XmlUtility.getStepByCortonaID(techElement,
					stepCortonaID);
			if (step == null) {
				step = XmlUtility.createProcedure();
				step.setAttributeValue("stepNumber", "10");
				step.setAttributeValue("cortonaID",
						stepElement.attributeValue("id"));
				step.setAttributeValue("bsoID", new UID().toString());
				XmlUtility.setProcedureContent(step,
						stepElement.attributeValue("Description"));
				step.setAttributeValue("responser", creatorOid);
				step.setAttributeValue("responserGroup", responserGroup);
				list.add(++insertStepLocation, step);
			} else {
				insertStepLocation = XmlUtility.getStepIndex(techElement, step);
			}

			List paceList = XmlUtility.getPaces(step).elements();
			int insertPaceLocation = -1;
			List subStepList = stepElement.elements("Substep");
			for (int j = 0; j < subStepList.size(); j++) {
				Element subPace = (Element) subStepList.get(j);
				String paceCortonaID = subPace.attributeValue("id");
				Element pace = XmlUtility.getPaceByCortonaID(step,
						paceCortonaID);
				if (pace == null) {
					pace = XmlUtility.createProcedure();
					pace.setAttributeValue("stepNumber", "-1");
					pace.setAttributeValue("cortonaID",
							subPace.attributeValue("id"));
					pace.setAttributeValue("bsoID", new UID().toString());
					XmlUtility.setProcedureContent(pace,
							subPace.attributeValue("Description"));

					paceList.add(++insertPaceLocation, pace);
				} else {
					insertPaceLocation = XmlUtility.getPaceIndex(step, pace);
				}
			}
			XmlUtility.reSetPaceNumbers(step);
		}
		XmlUtility.reSetStepNumbers(techElement);
	}

	private static void createOrUpdateProcedureElement(Element techElement,
			Element stepElement) {
		List procedureElements = techElement.element("steps").elements();

		for (int i = 0; i < procedureElements.size(); i++) {
			Element procedureELement = (Element) procedureElements.get(i);
			if (stepElement.attributeValue("id").equals(
					procedureELement.attributeValue("cortonaID"))) {
				updateProcedureElement(procedureELement, stepElement);
				return;
			}
		}
		createProcedureElement(techElement, stepElement);
	}

	private static void createOrUpdateProcedureElement(Element techElement,
			Element stepElement, String creater, String group) {
		List procedureElements = techElement.element("steps").elements();

		for (int i = 0; i < procedureElements.size(); i++) {
			Element procedureELement = (Element) procedureElements.get(i);
			if (stepElement.attributeValue("id").equals(
					procedureELement.attributeValue("cortonaID"))) {
				updateProcedureElement(procedureELement, stepElement);
				return;
			}
		}
		createProcedureElement(techElement, stepElement);
	}

	private static void createProcedureElement(Element techElement,
			Element stepElement) {
		Element procedureELement = XmlUtility.createProcedure();
		procedureELement.setAttributeValue("cortonaID",
				stepElement.attributeValue("id"));
		procedureELement.setAttributeValue("stepName",
				stepElement.elementText("Description"));
		procedureELement.setAttributeValue("bsoID", new UID().toString());

		XmlUtility.addProcedure(techElement, procedureELement);

		List subStepList = stepElement.elements("Substep");
		for (int sub = 0; sub < subStepList.size(); sub++) {
			Element subStepElement = (Element) subStepList.get(sub);
			Element subProcedureELement = XmlUtility.createProcedure();
			subProcedureELement.setAttributeValue("cortonaID",
					subStepElement.attributeValue("id"));

			subProcedureELement.element("procedureContent").setText(
					subStepElement.elementText("Description"));

			XmlUtility.addChildProcedure(procedureELement, subProcedureELement);
		}
	}

	private static void updateProcedureElement(Element procedureELement,
			Element stepElement) {
		procedureELement.setAttributeValue("stepName",
				stepElement.elementText("Description"));

		List subStepList = stepElement.elements("Substep");
		for (int sub = 0; sub < subStepList.size(); sub++) {
			Element subStepElement = (Element) subStepList.get(sub);
			createOrUpdateSubProcedureElement(procedureELement, subStepElement);
		}
	}

	private static void createOrUpdateSubProcedureElement(
			Element procedureELement, Element subStepElement) {
		List subProcedureELementList = procedureELement.element("paces")
				.elements();
		for (int i = 0; i < subProcedureELementList.size(); i++) {
			Element subProcedureELement = (Element) subProcedureELementList
					.get(i);
			if (subStepElement.attributeValue("id").equals(
					subProcedureELement.attributeValue("cortonaID"))) {
				subProcedureELement.element("procedureContent").setText(
						subStepElement.elementText("Description"));
				return;
			}
		}
		Element subProcedureELement = XmlUtility.createProcedure();
		subProcedureELement.setAttributeValue("cortonaID",
				subStepElement.attributeValue("id"));

		subProcedureELement.element("procedureContent").setText(
				subStepElement.elementText("Description"));

		XmlUtility.addChildProcedure(procedureELement, subProcedureELement);
	}

	private static Document getDocument(String fileName) throws Exception {
		if ((fileName == null) || (fileName.trim().equals("")))
			return null;
		return getDocument(new File(fileName));
	}

	private static Document getDocument(File file) throws Exception {
		if ((file == null) || (!file.isFile()))
			return null;
		SAXReader saxReader = new SAXReader();
		saxReader.setEncoding("UTF-8");
		Document document = saxReader.read(file);
		return document;
	}

	public static void copyFile(String sourceFileName, String targetFileName)
			throws IOException {
		FileInputStream instream = new FileInputStream(sourceFileName);
		byte[] b = new byte[1024];
		FileOutputStream outstream = new FileOutputStream(targetFileName);
		int nRead = 0;
		while ((nRead = instream.read(b, 0, 1024)) > 0)
			outstream.write(b, 0, nRead);
		outstream.close();
		instream.close();
	}

	private static void test() throws Exception {
		Element technicsElemnet = XmlUtility.createTechnics();
		String xmlFilePath = "C:\\Documents and Settings\\Administrator\\桌面\\南京出差（20120612-20120622）\\各种XML文件结构和格式\\自动生成工序的XML格式.xml";

		copyFile(xmlFilePath, "d:\\xmls\\autoSource.xml");

		autoGenerateSteps(technicsElemnet, xmlFilePath);
		Document doc = DocumentHelper.createDocument();
		doc.add(technicsElemnet);
		XmlUtility.saveDocument(doc, "c:\\auto.xml");
	}

	private static void autoGenerateStepNumber(Element techElement) {
		List procedureElements = techElement.element("steps").elements();

		int number = 10;
		for (int i = 0; i < procedureElements.size(); i++) {
			Element procedureELement = (Element) procedureElements.get(i);
			String stepNumber = number * (i + 1) + "";
			procedureELement.setAttributeValue("stepNumber", stepNumber);
			autoGenerateSubStepNumber(procedureELement);
		}
	}

	private static void autoGenerateSubStepNumber(Element procedureELement) {
		List subProcedureELementList = procedureELement.element("paces")
				.elements();

		int number = 1;
		for (int i = 0; i < subProcedureELementList.size(); i++) {
			Element subProcedureELement = (Element) subProcedureELementList
					.get(i);
			String stepNumber = number * (i + 1) + "";
			subProcedureELement.setAttributeValue("stepNumber", stepNumber);
		}
	}

	public static void main(String[] args) throws Exception {
	}
}
