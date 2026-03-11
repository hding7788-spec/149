package com.glaway.mpm.util;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.glaway.mpm.qmIntf.technics.entity.Attach;
import com.glaway.mpm.qmIntf.technics.entity.Equipment;
import com.glaway.mpm.qmIntf.technics.entity.Image;
import com.glaway.mpm.qmIntf.technics.entity.Material;
import com.glaway.mpm.qmIntf.technics.entity.Part;
import com.glaway.mpm.qmIntf.technics.entity.Step;
import com.glaway.mpm.qmIntf.technics.entity.Technics;
import com.glaway.mpm.qmIntf.technics.entity.Tools;
import com.glaway.mpm.qmIntf.technics.template.TemplateBuilder;

public class XmlMapper extends DefaultHandler {

	private Technics tech;
	private Step step;
	private Step subStep;
	private Part part;
	private Equipment equip;
	private Tools tool;
	private Material material;
	private Image image;
	private Attach attach;
	private Vector steps;
	private Vector parts;
	private Vector tools;
	private Vector images;
	private Vector materials;
	private Vector attachs;
	private Vector subSteps;
	private Vector equips;
	private CharArrayWriter contents = new CharArrayWriter();
	private int count = 0;
	private boolean pacersInPacers = false;

	private static String techFolder;
	private static String strTechFolder;

	private XmlMapper() {
	}

	@Override
	public void startDocument() throws SAXException {
		System.out.println("SAX Event: START DOCUMENT");
	}

	@Override
	public void endDocument() throws SAXException {
		System.out.println("SAX Event: END DOCUMENT");
	}

	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes attr) throws SAXException {
		contents.reset();
		if ("QMFawTechnicsInfo" == localName) {
			tech = new Technics();
			tech.setOid(attr.getValue("bsoID"));
			tech.setCreator(attr.getValue("creator"));
			tech.setCreateTime(attr.getValue("createTime"));
			tech.setLifeCycleState(attr.getValue("lifeCycleState"));
			tech.setParentPartNumber(attr.getValue("parentPartNumber"));
			tech.setPartName(attr.getValue("partName"));
			tech.setPartNumber(attr.getValue("partNumber"));
			tech.setProductName(attr.getValue("productName"));
			tech.setProductNumber(attr.getValue("productNumber"));
			tech.setTechnicsName(attr.getValue("technicsName"));
			tech.setTechnicsNumber(attr.getValue("technicsNumber"));
			tech.setTechnicsType(attr.getValue("technicsType"));
			tech.setVersion(attr.getValue("version"));
			tech.setBackupNote(attr.getValue("backupReason"));
			tech.setBackupNote(attr.getValue("remark"));
			tech.setBackupRate(attr.getValue("backupRate"));
			tech.setMaxBackupCount(attr.getValue("maxBackupCount"));
			tech.setMaoWeight(attr.getValue("maoWeight"));
			tech.setStateSize(attr.getValue("stateSize"));
			tech.setPartCount(attr.getValue("partCount"));
			tech.setPartSize(attr.getValue("partSize"));
			tech.setWorkShop(attr.getValue("workshop"));
			tech.setWrlFile(attr.getValue("wrlFile"));
			String user1 = attr.getValue("user1");
			tech.setUser1(user1 == null ? "" : user1);
			String user2 = attr.getValue("user2");
			tech.setUser2(user2 == null ? "" : user2);
			String user3 = attr.getValue("user3");
			tech.setUser3(user3 == null ? "" : user3);
			String date1 = attr.getValue("date1");
			tech.setDate1(date1 == null ? "" : date1);
			String date2 = attr.getValue("date2");
			tech.setDate2(date2 == null ? "" : date2);
			String date3 = attr.getValue("date3");
			tech.setDate3(date3 == null ? "" : date3);
			String comment1 = attr.getValue("comment1");
			tech.setComment1(comment1 == null ? "" : comment1);
			String comment2 = attr.getValue("comment2");
			tech.setComment2(comment2 == null ? "" : comment2);
			String comment3 = attr.getValue("comment3");
			tech.setComment3(comment3 == null ? "" : comment3);
		}

		if ("steps" == localName) {
			steps = new Vector();
		}

		if ("paces" == localName) {
			if (subSteps == null)
				subSteps = new Vector();
			else
				pacersInPacers = true;
		}

		if ("equips" == localName) {
			equips = new Vector();
		}

		if ("tools" == localName) {
			tools = new Vector();
		}

		if ("images" == localName) {
			images = new Vector();
		}

		if ("materials" == localName) {
			materials = new Vector();
		}

		if ("attachs" == localName) {
			attachs = new Vector();
		}

		if ("parts" == localName) {
			parts = new Vector();
		}

		if ("QMProcedureInfo" == localName) {
			if (this.subSteps != null) {
				subStep = new Step();
				subStep.setOid(attr.getValue("bsoID"));
				subStep.setCortonaID(attr.getValue("cortonaID"));
				subStep.setIsKey(attr.getValue("isKey"));
				subStep.setWorkType(attr.getValue("workType"));
				subStep.setStepHour(attr.getValue("stepHour"));
				subStep.setStepName(attr.getValue("stepName"));
				subStep.setStepNumber(attr.getValue("stepNumber"));
				subStep.setWorkShop(attr.getValue("workShop"));
				subStep.setType("subStep");
				String prepareWorkHours = attr.getValue("PrepareWorkHours");
				subStep.setPrepareWorkHours(prepareWorkHours == null ? "" : prepareWorkHours);
				String taktTime = attr.getValue("TaktTime");
				subStep.setTaktTime(taktTime == null ? "" : taktTime);
				String numberOfGroup = attr.getValue("NumberOfGroup");
				subStep.setNumberOfGroup(numberOfGroup == null ? "" : numberOfGroup);
			} else {
				step = new Step();
				step.setOid(attr.getValue("bsoID"));
				step.setCortonaID(attr.getValue("cortonaID"));
				step.setIsKey(attr.getValue("isKey"));
				step.setWorkType(attr.getValue("workType"));
				step.setStepHour(attr.getValue("stepHour"));
				step.setStepName(attr.getValue("stepName"));
				step.setStepNumber(attr.getValue("stepNumber"));
				step.setWorkShop(attr.getValue("workShop"));
				step.setType("step");
				String prepareWorkHours = attr.getValue("PrepareWorkHours");
				step.setPrepareWorkHours(prepareWorkHours == null ? "" : prepareWorkHours);
				String taktTime = attr.getValue("TaktTime");
				step.setTaktTime(taktTime == null ? "" : taktTime);
				String numberOfGroup = attr.getValue("NumberOfGroup");
				step.setNumberOfGroup(numberOfGroup == null ? "" : numberOfGroup);
			}
		}

		if ("procedureContent" == localName) {
		}

		if ("QMPartInfo" == localName) {
			part = new Part();
			part.setOid(attr.getValue("bsoID"));
			part.setDutu(attr.getValue("dutu"));
			part.setMaterial(attr.getValue("material"));
			part.setPartName(attr.getValue("partName"));
			part.setPartNumber(attr.getValue("partNumber"));
			part.setRemark(attr.getValue("remark"));
			String useCount = attr.getValue("useCount");
			if (useCount.matches("\\d+")) {
				part.setUseCount(useCount);
			} else {
				part.setUseCount("");
			}
		}

		if ("QMEquipmentInfo" == localName) {
			equip = new Equipment();
			equip.setOid(attr.getValue("bsoID"));
			equip.setEqModel(attr.getValue("eqModel"));
			equip.setEqName(attr.getValue("eqName"));
			equip.setEqNum(attr.getValue("eqNum"));
			String useCount = attr.getValue("useCount");
			if (useCount.matches("\\d+")) {
				equip.setUseCount(useCount);
			} else {
				equip.setUseCount("");
			}
		}

		if ("QMToolInfo" == localName) {
			tool = new Tools();
			tool.setOid(attr.getValue("bsoID"));
			tool.setToolName(attr.getValue("toolName"));
			tool.setToolNum(attr.getValue("toolNum"));
			tool.setToolSpec(attr.getValue("toolSpec"));
			tool.setToolStdNum(attr.getValue("toolStdNum"));
			String useCount = attr.getValue("useCount");
			if (useCount.matches("\\d+")) {
				tool.setUseCount(useCount);
			} else {
				tool.setUseCount("");
			}
		}

		if ("QMMaterialInfo" == localName) {
			material = new Material();
			material.setOid(attr.getValue("bsoID"));
			material.setMaterialName(attr.getValue("materialName"));
			material.setMaterialNumber(attr.getValue("materialNumber"));
			String useCount = attr.getValue("useCount");
			if (useCount.matches("\\d+")) {
				material.setUseCount(useCount);
			} else {
				material.setUseCount("");
			}
			material.setMaterialCode(attr.getValue("materialCode"));
			material.setMaterialCrision(attr.getValue("materialCrision"));
			material.setMaterialState(attr.getValue("materialState"));
		}

		if ("PDrawingInfo" == localName) {
			image = new Image();
			image.setOid(attr.getValue("bsoID"));
			String path = attr.getValue("absolutePath");
			path = path.replace("\\", "/");
			image.setDrawingLink(path);
			image.setDrawingName(attr.getValue("drawingName"));
			image.setDrawingType(attr.getValue("drawingType"));
		}
		if ("PAttachInfo" == localName) {
			attach = new Attach();
			attach.setOid(attr.getValue("bsoID"));
			String path = attr.getValue("absolutePath");
			path = path.replace("\\", "/");
			attach.setPath(path);
			attach.setName(attr.getValue("attachName"));
			attach.setType(attr.getValue("attachType"));
			attach.setSize(attr.getValue("attachSize"));
		}
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		if ("QMFawTechnicsInfo" == localName) {

		}
		if ("steps" == localName) {
			this.tech.setSteps(steps);
			steps = null;
		}
		if ("paces" == localName) {
			if (!pacersInPacers) {
				if (subSteps != null && subSteps.size() > 0)
					this.step.setSubSteps(subSteps);
				subSteps = null;
			} else {
				pacersInPacers = false;
			}
		}
		if ("equips" == localName) {
			if (subStep != null) {
				subStep.setEquips(equips);
			} else if (step != null) {
				step.setEquips(equips);
			}
			equips = null;
		}
		if ("tools" == localName) {
			if (subStep != null) {
				subStep.setTools(tools);
			} else if (step != null) {
				step.setTools(tools);
			}
			tools = null;
		}
		if ("materials" == localName) {
			if (subStep != null) {
				subStep.setMaterials(materials);
			} else if (step != null) {
				step.setMaterials(materials);
			} else {
				tech.setMaterials(materials);
			}
			materials = null;
		}
		if ("attachs" == localName) {
			if (subStep != null) {
				subStep.setAttachs(attachs);
			} else if (step != null) {
				step.setAttachs(attachs);
			}
			attachs = null;
		}
		if ("images" == localName) {
			if (subStep != null) {
				subStep.setImages(images);
			} else if (step != null) {
				step.setImages(images);
			} else {
				tech.setImages(images);
			}
			images = null;
		}
		if ("parts" == localName) {
			if (subStep != null) {
				subStep.setParts(parts);
			} else if (step != null) {
				step.setParts(parts);
			}
			parts = null;
		}
		if ("QMProcedureInfo" == localName) {
			if (subStep != null && subSteps != null) {
				this.subSteps.add(subStep);
				subStep = null;
			} else {
				this.steps.add(step);
				step = null;
			}
		}
		if ("procedureContent" == localName) {
			if (this.subStep != null) {
				this.subStep.setProcedureContent(replaceToImgUrl(step.getStepNumber(), subStep.getStepNumber(),
						contents.toString().trim()));
			} else {
				this.step.setProcedureContent(replaceToImgUrl(step.getStepNumber(), "", contents.toString().trim()));
			}
		}
		if ("QMPartInfo" == localName) {
			if (part != null && parts != null)
				this.parts.add(part);
			part = null;
		}
		if ("QMEquipmentInfo" == localName) {
			if (equip != null && equips != null)
				this.equips.add(equip);
			equip = null;
		}
		if ("QMToolInfo" == localName) {
			if (tool != null && tools != null)
				this.tools.add(tool);
			tool = null;
		}
		if ("QMMaterialInfo" == localName) {
			if (material != null && materials != null)
				this.materials.add(material);
			material = null;
		}
		if ("PDrawingInfo" == localName) {
			if (images != null && image != null)
				this.images.add(image);
			image = null;
		}
		if ("PAttachInfo" == localName) {
			if (attachs != null && attach != null)
				this.attachs.add(attach);
			attach = null;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		try {
			contents.write(ch, start, length);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解析xml，并把数据装配到对象中
	 */
	public static boolean initData(Technics te, String xmlFilePath, String targPath, String type) {
		try {
			XmlMapper ma = new XmlMapper();
			XMLReader xr = XMLReaderFactory.createXMLReader();
			xr.setContentHandler(ma);
			File xmlFile = new File(xmlFilePath);
			InputStream is = new FileInputStream(xmlFile);
			String tFolder = ".";
			techFolder = xmlFile.getParent();
			strTechFolder = tFolder;
			xr.parse(new InputSource(is));
			// System.out.println("in XMLMapper :\n" + ma.tech.toJSONString());
			TemplateBuilder builder = new TemplateBuilder();
			builder.makeTemplate(ma.tech, targPath, tFolder);
			return true;
		} catch (IOException ex) {
			Logger.getLogger(XmlMapper.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		} catch (SAXException ex) {
			Logger.getLogger(XmlMapper.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		}
	}

	private String replaceToImgUrl(String StepNumber, String subStepNumber, String pContent) {
		String imgFolder = techFolder + "\\content";
		// System.out.println("imgFolder==="+imgFolder);
		File img = new File(imgFolder);
		if (!img.exists()) {
			return pContent;
		}
		File[] imgs = img.listFiles();
		String tempImg = StepNumber + "_" + subStepNumber + ".JPG";
		for (int i = 0; i < imgs.length; i++) {
			if (tempImg.equals(imgs[i].getName())) {
				return (strTechFolder + "\\content" + "\\" + tempImg).replace('\\', '/');
			}
		}
		return pContent;
	}

	public static void main(String[] argv) {
		System.out.println("Example1 SAX Events:");
		try {
			XmlMapper ma = new XmlMapper();
			// Create SAX 2 parser...
			XMLReader xr = XMLReaderFactory.createXMLReader();
			// Set the ContentHandler...
			xr.setContentHandler(ma);
			// Parse the file...
			InputStream is = new FileInputStream(
					new File(
							"C:\\Users\\Administrator\\预览\\预览_AL2_907_1460__1225`副天线和差选束开关__1225装配工艺`装配工艺`AL2_907_1460__1225`多基地面雷达\\AL2_907_1460__1225`副天线和差选束开关__1225装配工艺`装配工艺`AL2_907_1460__1225`多基地面雷达.xml"));
			// FileReader fr = new FileReader()
			xr.parse(new InputSource(is));
			// System.out.println(ma.tech.toJSONString());
			// ma.tech.toFormatString();
			// System.err.println("why red!");
			TemplateBuilder builder = new TemplateBuilder();
			// builder.makeTemplate(ma.tech);
			// System.out.println(builder.makejs(ma.tech.toJSONString()));
			// System.out.println(builder.makeHTML("",ma.tech));
			// System.err.println(ma.tech.toJSONString());
			// String str = ma.tech.getTechnicsName();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}