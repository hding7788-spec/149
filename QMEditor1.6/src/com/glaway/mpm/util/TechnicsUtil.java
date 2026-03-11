package com.glaway.mpm.util;

import com.glaway.mpm.model.*;
import com.glaway.mpm.pdf.PDFBuilder;
import com.glaway.mpm.resource.Constants;
import com.glaway.mpm.view.*;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import com.jacob.activeX.ActiveXComponent;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;
import ext.casc.sop.util.StringUtil;
import org.dom4j.Document;
import org.dom4j.Element;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.rmi.server.UID;
import java.util.*;
import java.util.Map.Entry;

public class TechnicsUtil {
	private static VaLogger logger = VaLogger.getLogger(NewTechnicsPart.class);

	public static int getReworkNumber(String name) {
		return Integer.parseInt(name.substring(name.indexOf("_fg") + 3,
				name.indexOf("_fg") + 5));
	}

	public static int getTempNumber(String name) {
		return Integer.parseInt(name.substring(name.indexOf("_ls") + 3,
				name.indexOf("_ls") + 5));
	}

	public static void main(String[] args) {
		List<List<String>> parentList = new ArrayList<List<String>>();
		List<String> list = new ArrayList<String>();
		list.add("1");
		list.add("3");
		list.add("7");
		list.add("2");
		list.add("9");

		parentList.add(list);

		List<String> list1 = new ArrayList<String>();
		list1.add("1");
		list1.add("3");
		list1.add("7");
		list1.add("2");
		list1.add("9");

		parentList.add(list1);

		System.out.println(parentList);

		for (int i = parentList.size() - 1; i > 0; i--) {
			List<String> child = parentList.get(i);
			for (int j = child.size() - 1; j > 0; j--) {
				if (i == 1 && j == 3) {
					child.remove(j);
				}
			}

		}

		System.out.println(parentList);

	}


	/**
	 * 检查工艺
	 *
	 * @param techElement
	 * @return
	 */
	public static String checkTechnics(JFrame frame, Element techElement) {
		List<Object> check = parseResource(techElement);
		CheckTechnics checkTechnics = (CheckTechnics) check.get(0);
		String technicsName = XmlUtility.getAttributeValue(techElement, "technicsName");

		String stepNameMessage = getStepNameMessage(technicsName,(List<String>) check.get(1));
		String wholeMessage = "";
		if (!"".equals(stepNameMessage)) {
			wholeMessage += stepNameMessage;
		}
		Map<String,String> expireStepNameNumbers = (Map<String,String>)check.get(3);
		if(!expireStepNameNumbers.isEmpty()){
			wholeMessage += getExpireStepNameMessage(technicsName,expireStepNameNumbers);
		}


		String paceContentMessage = getPaceContentMessage((Map<String, List<String>>) check.get(2));
		if (!"".equals(paceContentMessage)) {
			wholeMessage += paceContentMessage + "\n";
		}

//		printCheckTechnics(checkTechnics);
		try {
			checkTechnics = TechnicsIntf.checkTechnics(checkTechnics);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
//		printCheckTechnics(checkTechnics);

		List<StepObject> stepObjects = checkTechnics.getStepObjects();

		if (stepObjects != null && stepObjects.size() != 0) {
			for (StepObject stepObject : stepObjects) {
				String stepNumber = stepObject.getStepNumber();

				List<Equipment> equipments = stepObject.getEquipments();
				String stepWarnningMessage = "";

				String epMessage = "";
				if (equipments != null && equipments.size() != 0) {
					for (Equipment equipment : equipments) {
						epMessage += "," + equipment.getName();
					}
				}
				if (!"".equals(epMessage)) {
					epMessage = getWarningMessage("equipment", JavaUtil.deletePrefix(epMessage));
					stepWarnningMessage += epMessage + "；";
				}

				String toolMessage = "";
				List<Tool> tools = stepObject.getTools();
				if (tools != null && tools.size() != 0) {
					for (Tool tool : tools) {
						toolMessage += "," + tool.getToolName();
					}
				}
				if (!"".equals(toolMessage)) {
					toolMessage = getWarningMessage("tool", JavaUtil.deletePrefix(toolMessage));
					stepWarnningMessage += toolMessage + "；";
				}

				String materialMessage = "";
				List<Material> materials = stepObject.getMaterials();
				if (materials != null && materials.size() != 0) {
					for (Material material : materials) {
						materialMessage += "," + material.getMaterialName();
					}
				}
				if (!"".equals(materialMessage)) {
					materialMessage = getWarningMessage("material", JavaUtil.deletePrefix(materialMessage));
					stepWarnningMessage += materialMessage;
				}

				if (!stepWarnningMessage.equals("")) {
					stepWarnningMessage += "\n";
				}

				// 工步
				List<PaceObject> paceObjects = stepObject.getPaceObjects();
				if (paceObjects != null && paceObjects.size() != 0) {
					for (PaceObject paceObject : paceObjects) {
						String paceNumber = paceObject.getPaceNumber();
						equipments = paceObject.getEquipments();
						String paceWarnningMessage = "";
						epMessage = "";
						if (equipments != null && equipments.size() != 0) {
							for (Equipment equipment : equipments) {
								epMessage += "," + equipment.getName();
							}
						}
						if (!"".equals(epMessage)) {
							epMessage = getWarningMessage("equipment", JavaUtil.deletePrefix(epMessage));
							paceWarnningMessage += epMessage + "；";
						}

						toolMessage = "";
						tools = paceObject.getTools();
						if (tools != null && tools.size() != 0) {
							for (Tool tool : tools) {
								toolMessage += "," + tool.getToolName();
							}
						}
						if (!"".equals(toolMessage)) {
							toolMessage = getWarningMessage("tool", JavaUtil.deletePrefix(toolMessage));
							paceWarnningMessage += toolMessage + "；";
						}

						materialMessage = "";
						materials = paceObject.getMaterials();
						if (materials != null && materials.size() != 0) {
							for (Material material : materials) {
								materialMessage += "," + material.getMaterialName();
							}
						}
						if (!"".equals(materialMessage)) {
							materialMessage = getWarningMessage("material", JavaUtil.deletePrefix(materialMessage));
							paceWarnningMessage += materialMessage;
						}

						if (!"".equals(paceWarnningMessage)) {
							paceWarnningMessage = "      工步号为" + paceNumber + "：" + paceWarnningMessage;
							System.out.println(paceWarnningMessage);
							stepWarnningMessage += paceWarnningMessage + "\n";
						}
					}

				}

				if (!"".equals(stepWarnningMessage)) {
					stepWarnningMessage = "工序号为" + stepNumber + "：" + stepWarnningMessage;
					wholeMessage += stepWarnningMessage + "\n";
				}
			}

		}

		if ("".equals(wholeMessage)) {
			//return true;
			return wholeMessage;
		} else {
			TechnicsSubmitWarnningDialog dialog = new TechnicsSubmitWarnningDialog(frame);
			dialog.showDialog(wholeMessage);
			return wholeMessage;
		}

	}

	/**
	 * 生成工步内容提示信息
	 *
	 * @param stepNameNumbers
	 * @return
	 */
	private static String getPaceContentMessage(
			Map<String, List<String>> paceContentNumbers) {
		String message = "";
		Set<Entry<String, List<String>>> set = paceContentNumbers.entrySet();
		for (Entry<String, List<String>> entry : set) {
			String stepNumber = entry.getKey();
			List<String> paceNumbers = entry.getValue();
			if (paceNumbers != null && paceNumbers.size() != 0) {
				String numbers = "";
				for (String paceNumber : paceNumbers) {
					numbers += "," + paceNumber;
				}
				if (numbers.startsWith(",")) {
					numbers = numbers.substring(1);
				}
				message += "工序号为" + stepNumber + "工步号为" + numbers
						+ "的工步内容没有填写\n";
			}
		}

		return message;
	}

	/**
	 * 生成工序名称提示信息
	 * @param technicsName
	 *
	 * @param stepNameNumbers
	 * @return
	 */
	private static String getStepNameMessage(String technicsName, List<String> stepNameNumbers) {
		String message = "";
		for (String stepNumber : stepNameNumbers) {
			message += "," + stepNumber;
		}
		if (message.startsWith(",")) {
			message = technicsName+"工序号为" + message.substring(1) + "的工序名称没有填写\n\n";
		}
		return message;
	}

	private static String getExpireStepNameMessage(String technicsName,Map<String,String> expireStepNameNumbers) {
		String message = "";
		Set<String> keys = expireStepNameNumbers.keySet();
		for (String stepNumber : keys) {
			String stepName = expireStepNameNumbers.get(stepNumber);
			message += "," + stepNumber+ "_"+ stepName;
		}
		if (message.startsWith(",")) {
			message = technicsName+"工序" + message.substring(1) + "的工序名称已失效\n\n";
		}
		return message;
	}

	private static void printCheckTechnics(CheckTechnics checkTechnics) {
		List<StepObject> stepObjects = checkTechnics.getStepObjects();
		if (stepObjects != null) {
			for (StepObject stepObject : stepObjects) {
				logger.debug("StepNumber = " + stepObject.getStepNumber());
				logger.debug("Equipment = " + stepObject.getEquipments());
				logger.debug("Tool =  " + stepObject.getTools());
				logger.debug("Material = " + stepObject.getMaterials());
				List<PaceObject> paceObjects = stepObject.getPaceObjects();
				if (paceObjects != null) {
					for (PaceObject paceObject : paceObjects) {
						logger.debug("PaceNumber = " + paceObject.getPaceNumber());
						logger.debug("Equipment = " + paceObject.getEquipments());
						logger.debug("Tool =  " + paceObject.getTools());
						logger.debug("Material = " + paceObject.getMaterials());
					}
				}
			}
		}
	}

	/**
	 * 获取资源
	 *
	 * @param elements
	 * @param type
	 * @param stepNumber
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static List<Object> parseResource(Element techElement) {
		List<Object> check = new ArrayList<Object>();
		CheckTechnics checkTechnics = new CheckTechnics();
		Element element;
		// 工序名称没有填写的工步号
		List<String> stepNameNumbers = new ArrayList<String>();
		Map<String,String> expireStepNameNumbers = new LinkedHashMap<String, String>();
		Collection<String> stepNames  = NewTechnicsPart.allStepNameMap.values();

		// 工步内容没有填写的工序工步号
		Map<String, List<String>> paceContentNumbers = new LinkedHashMap<String, List<String>>();
		if (techElement != null
				&& (element = techElement.element("steps")) != null) {
			List<Element> elements = element.elements("QMProcedureInfo");
			List<StepObject> stepObjects = new ArrayList<StepObject>();
			for (Element stepElement : elements) {
				StepObject stepObject = new StepObject();
				String stepNumber = stepElement.attributeValue("stepNumber");
				String stepName = stepElement.attributeValue("stepName");
				if (stepName == null || stepName.trim().length() == 0) {
					stepNameNumbers.add(stepNumber);
				}else{
					if(!stepNames.contains(stepName)){
						expireStepNameNumbers.put(stepNumber,stepName);
					}
				}
				String preStep = stepElement.attributeValue("preStep");
				stepObject.setStepNumber(stepNumber);
				stepObject.setPreStep(preStep);
				stepObject = getResourceOids(stepElement, stepObject);

				// 工步
				List<PaceObject> paceObjects = new ArrayList<PaceObject>();
				Element pace = stepElement.element("paces");
				List<Element> paceElement = pace.elements();
				List<String> paceContent = new ArrayList<String>();
				if (paceElement != null) {
					for (Element temp : paceElement) {
						PaceObject paceObject = new PaceObject();
						String paceNumber = temp.attributeValue("stepNumber");
						paceObject.setPaceNumber(paceNumber);
						// 工步内容
						Element procedureContent = temp.element("procedureContent");
						String text = procedureContent.getTextTrim();
						if (null == text || "".equals(text)) {
							paceContent.add(paceNumber);
						}

						paceObject = getResourceOids(temp, paceObject);
						paceObjects.add(paceObject);
					}
				}

				paceContentNumbers.put(stepNumber, paceContent);

				// 参装件是否都参与装配
				// if
				// (!"true".equals(techElement.attributeValue("isCompleted"))) {
				// SwingUtil.showMessageDialog("零件没有全部参与装配", Constants.TIP, 2);
				// }
				// return true;
				// }

				stepObject.setPaceObjects(paceObjects);
				stepObjects.add(stepObject);
			}
			checkTechnics.setStepObjects(stepObjects);
		}

		check.add(checkTechnics);
		check.add(stepNameNumbers);
		check.add(paceContentNumbers);
		check.add(expireStepNameNumbers);
		return check;
	}

	/**
	 * 获取资源
	 *
	 * @param elements
	 * @param type
	 * @param stepNumber
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static StepObject getResourceOids(Element stepElement,
			StepObject stepObject) {
		List<Element> epElements = stepElement.element("equips").elements(
				"QMEquipmentInfo");
		List<?> equipments = getObjects("equipment", epElements);

		List<Element> toolElements = stepElement.element("tools").elements(
				"QMToolInfo");
		List<?> tools = getObjects("tool", toolElements);

		List<Element> materialElements = stepElement.element("materials")
				.elements("QMMaterialInfo");
		List<?> materials = getObjects("material", materialElements);

		stepObject.setEquipments((List<Equipment>) equipments);
		stepObject.setTools((List<Tool>) tools);
		stepObject.setMaterials((List<Material>) materials);

		return stepObject;
	}

	/**
	 * 获取资源
	 *
	 * @param elements
	 * @param type
	 * @param stepNumber
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static PaceObject getResourceOids(Element stepElement,
			PaceObject paceObject) {
		List<Element> epElements = stepElement.element("equips").elements(
				"QMEquipmentInfo");
		List<?> equipments = getObjects("equipment", epElements);

		List<Element> toolElements = stepElement.element("tools").elements(
				"QMToolInfo");
		List<?> tools = getObjects("tool", toolElements);

		List<Element> materialElements = stepElement.element("materials")
				.elements("QMMaterialInfo");
		List<?> materials = getObjects("material", materialElements);

		paceObject.setEquipments((List<Equipment>) equipments);
		paceObject.setTools((List<Tool>) tools);
		paceObject.setMaterials((List<Material>) materials);

		return paceObject;
	}

	/**
	 * 获取资源
	 *
	 * @param elements
	 * @param type
	 * @param stepNumber
	 * @return
	 */
	private static List<Object> getObjects(String type, List<Element> elements) {
		List<Object> objects = new ArrayList<Object>();
		if (elements != null && elements.size() != 0) {
			for (Element element : elements) {
				if ("equipment".equals(type)) {
					Equipment ep = new Equipment();
					ep.setEquipmentNumber(element.attributeValue("epNum"));
					ep.setName(element.attributeValue("eqName"));
					ep.setOid(element.attributeValue("oid"));
					objects.add(ep);
				} else if ("tool".equals(type)) {
					Tool tool = new Tool();
					tool.setToolNum(element.attributeValue("toolNum"));
					tool.setToolName(element.attributeValue("toolName"));
					tool.setOid(element.attributeValue("oid"));
					objects.add(tool);
				} else if ("material".equals(type)) {
					Material material = new Material();
					material.setMaterialNumber(element
							.attributeValue("materialNumber"));
					material.setMaterialName(element
							.attributeValue("materialName"));
					material.setOid(element.attributeValue("oid"));
					objects.add(material);
				}

			}
		}
		return objects;

		// String warningMessage = "";
		// if (elements != null && elements.size() != 0) {
		// for (Element element : elements) {
		// String oid = element.attributeValue("oid");
		// if (ResourceIntf.isResourceAbandon(type, oid)) {
		// SwingUtil.showMessageDialog(
		// getWarningMessage(element, stepNumber, type),
		// Constants.TIP, 2);
		// return null;
		// }
		// if ("material".equals(type)) {
		// String materialNumber = element
		// .attributeValue("materialNumber");
		// if (materialNumber != null
		// && materialNumber.trim().length() != 0) {
		// String materialQuota = element
		// .attributeValue("materialQuota");
		// if (materialQuota == null
		// || materialQuota.trim().length() == 0) {
		// warningMessage += getQuotaWarningMessage(element,
		// stepNumber, type);
		// }
		// }
		// }
		// if ("tool".equals(type)) {
		// if (!ResourceIntf.isFrockCompleted(oid)) {
		// warningMessage += getFrockWarningMessage(element,
		// stepNumber, type);
		// }
		// }
		// }
		//
		// }
		// if (warningMessage.startsWith(",")) {
		// warningMessage = warningMessage.substring(1);
		// }
		// return warningMessage;
	}

	/**
	 * 根据类型获取警告信息
	 *
	 * @param element
	 * @param stepNumber
	 * @param type
	 * @return
	 */
	private static String getWarningMessage(String type, String names) {
		String warningMessage = "名称为";
		if ("equipment".equals(type)) {
			warningMessage += names + "的设备已作废";
		} else if ("tool".equals(type)) {
			warningMessage += names + "的工装或工具已作废";
		} else if ("material".equals(type)) {
			warningMessage += names + "的材料已作废";
		}
		return warningMessage;
	}

	/**
	 * 根据类型获取警告信息
	 *
	 * @param element
	 * @param stepNumber
	 * @param type
	 * @return
	 */
	private static String getQuotaWarningMessage(Element element,
			String stepNumber, String type) {
		return "工序号为" + stepNumber + "名称为"
				+ element.attributeValue("materialName") + ",的材料的材料定额没有填写完整";
	}

	/**
	 * 根据类型获取警告信息
	 *
	 * @param element
	 * @param stepNumber
	 * @param type
	 * @return
	 */
	private static String getFrockWarningMessage(Element element,
			String stepNumber, String type) {
		return "工序号为" + stepNumber + "名称为" + element.attributeValue("toolName")
				+ ",的工装未归档";
	}

	/**
	 * 生成提交的Map
	 *
	 * @param element
	 * @return
	 */
	public static Map<String, Object> generateSubmitMap(Element element,
			Map<String, Object> map) {
		if (map == null) {
			map = new HashMap<String, Object>();
		}
		map.put("topPartOid", element.attributeValue("parentPartOid"));
		map.put("partOid", element.attributeValue("partOid"));
		map.put("unite", element.attributeValue("unite"));
		map.put("technicsCategory", element.attributeValue("technicsCategory"));
		String version = element.attributeValue("version");
		if ((version == null) || (version.equals(""))) {
			version = "1.0";
		}
		map.put("xmlVersion", version);
		return map;
	}

	public static List<CMatBean> generateSubmitList(Element element,String partUseCount,String cindex) {
		List<CMatBean> list = new ArrayList<CMatBean>();

		CMatBean bean = null;
		List<Element> ycl = element.selectNodes("CLDE/YCLDE/ycldeRecord");
		for(Element ele:ycl){
			bean = new CMatBean();
			bean.setCmatType("原材料");
			bean.setPplanNumber(element.attributeValue("pplanNumber"));
			bean.setPplanName(element.attributeValue("pplanName"));
			bean.setTechnicsName(element.attributeValue("technicsName"));
			bean.setTechnicsNumber(element.attributeValue("technicsNumber"));
			bean.setTechnicsType(element.attributeValue("technicsType"));
			bean.setPartUseCount(partUseCount);
			bean.setPartName(element.attributeValue("partName"));
			bean.setCindex(cindex);

			bean.setChbm(ele.attributeValue("chbm"));
			bean.setChmc(ele.attributeValue("chmc"));
			bean.setXlcc(ele.attributeValue("xlcc"));
			bean.setKzjs(ele.attributeValue("kzjs"));
			bean.setXhph(ele.attributeValue("xhph"));
			bean.setGg(ele.attributeValue("gg"));
			bean.setJstj(ele.attributeValue("jstj"));
			bean.setSccj(ele.attributeValue("sccj"));
			bean.setZjldw(ele.attributeValue("zjldw"));
			bean.setFjtj(ele.attributeValue("fjtj"));
			bean.setGyztrcl(ele.attributeValue("gyztrcl"));
			bean.setComment(ele.attributeValue("comment"));
			bean.setDataFrom(ele.attributeValue("dataFrom"));

			list.add(bean);
		}

		List<Element> zycl = element.selectNodes("CLDE/ZYCLDE/zycldeRecord");
		for(Element ele:zycl){
			bean = new CMatBean();
			bean.setCmatType("主要材料");
			bean.setPplanNumber(element.attributeValue("pplanNumber"));
			bean.setPplanName(element.attributeValue("pplanName"));
			bean.setTechnicsName(element.attributeValue("technicsName"));
			bean.setTechnicsNumber(element.attributeValue("technicsNumber"));
			bean.setTechnicsType(element.attributeValue("technicsType"));
			bean.setPartUseCount(partUseCount);
			bean.setPartName(element.attributeValue("partName"));
			bean.setCindex(cindex);

			bean.setChbm(ele.attributeValue("chbm"));
			bean.setChmc(ele.attributeValue("chmc"));
			bean.setSl(ele.attributeValue("sl"));
			bean.setXhph(ele.attributeValue("xhph"));
			bean.setGg(ele.attributeValue("gg"));
			bean.setJstj(ele.attributeValue("jstj"));
			bean.setSccj(ele.attributeValue("sccj"));
			bean.setZjldw(ele.attributeValue("zjldw"));
			bean.setDw(ele.attributeValue("dw"));
			bean.setFjtj(ele.attributeValue("fjtj"));
			bean.setGyztrcl(ele.attributeValue("gyztrcl"));
			bean.setComment(ele.attributeValue("comment"));
			bean.setDataFrom(ele.attributeValue("dataFrom"));

			list.add(bean);
		}

		List<Element> sjycl = element.selectNodes("CLDE/SJYCLDE/sjycldeRecord");
		for(Element ele:sjycl){
			bean = new CMatBean();
			bean.setCmatType("试件原材料");
			bean.setPplanNumber(element.attributeValue("pplanNumber"));
			bean.setPplanName(element.attributeValue("pplanName"));
			bean.setTechnicsName(element.attributeValue("technicsName"));
			bean.setTechnicsNumber(element.attributeValue("technicsNumber"));
			bean.setTechnicsType(element.attributeValue("technicsType"));
			bean.setPartUseCount(partUseCount);
			bean.setPartName(element.attributeValue("partName"));
			bean.setCindex(cindex);

			bean.setChbm(ele.attributeValue("chbm"));
			bean.setChmc(ele.attributeValue("chmc"));
			bean.setSjcc(ele.attributeValue("sjcc"));
			bean.setSjkzjs(ele.attributeValue("sjkzjs"));
			bean.setSjsl(ele.attributeValue("sjsl"));
			bean.setXhph(ele.attributeValue("xhph"));
			bean.setGg(ele.attributeValue("gg"));
			bean.setJstj(ele.attributeValue("jstj"));
			bean.setSccj(ele.attributeValue("sccj"));
			bean.setZjldw(ele.attributeValue("zjldw"));
			bean.setFjtj(ele.attributeValue("fjtj"));
			bean.setGyztrcl(ele.attributeValue("gyztrcl"));
			bean.setComment(ele.attributeValue("comment"));
			bean.setDataFrom(ele.attributeValue("dataFrom"));

			list.add(bean);
		}

		//设计资源库新增
		List<Element> sjzykycl = element.selectNodes("CLDE/SJZYKYCLDE/ycldeRecord");
		for(Element ele:sjzykycl){
			bean = new CMatBean();
			bean.setCmatType("原材料");
			bean.setPplanNumber(element.attributeValue("pplanNumber"));
			bean.setPplanName(element.attributeValue("pplanName"));
			bean.setTechnicsName(element.attributeValue("technicsName"));
			bean.setTechnicsNumber(element.attributeValue("technicsNumber"));
			bean.setTechnicsType(element.attributeValue("technicsType"));
			bean.setPartUseCount(partUseCount);
			bean.setPartName(element.attributeValue("partName"));
			bean.setCindex(cindex);

			bean.setChbm(ele.attributeValue("sjbm"));
			bean.setChmc(ele.attributeValue("name"));
			bean.setXlcc(ele.attributeValue("xlcc"));
			bean.setKzjs(ele.attributeValue("kzjs"));
			bean.setXhph(ele.attributeValue("xh"));
			bean.setGg(ele.attributeValue("gg"));
			bean.setJstj(ele.attributeValue("jstj"));//此属性没有
			bean.setSccj(ele.attributeValue("sccj"));//此属性没有
			bean.setZjldw(ele.attributeValue("jldw"));
			bean.setFjtj(ele.attributeValue("fjtj"));//此属性没有
			bean.setGyztrcl(ele.attributeValue("gyztrcl"));//此属性没有
			bean.setComment(ele.attributeValue("comment"));
			bean.setDataFrom(ele.attributeValue("dataFrom"));

			list.add(bean);
		}

		List<Element> sjzykzycl = element.selectNodes("CLDE/SJZYKZYCLDE/zycldeRecord");
		for(Element ele:sjzykzycl){
			bean = new CMatBean();
			bean.setCmatType("主要材料");
			bean.setPplanNumber(element.attributeValue("pplanNumber"));
			bean.setPplanName(element.attributeValue("pplanName"));
			bean.setTechnicsName(element.attributeValue("technicsName"));
			bean.setTechnicsNumber(element.attributeValue("technicsNumber"));
			bean.setTechnicsType(element.attributeValue("technicsType"));
			bean.setPartUseCount(partUseCount);
			bean.setPartName(element.attributeValue("partName"));
			bean.setCindex(cindex);

			bean.setChbm(ele.attributeValue("sjbm"));
			bean.setChmc(ele.attributeValue("name"));
			bean.setSl(ele.attributeValue("sl"));
			bean.setXhph(ele.attributeValue("xh"));
			bean.setGg(ele.attributeValue("gg"));
			bean.setJstj(ele.attributeValue("jstj"));//此属性没有
			bean.setSccj(ele.attributeValue("sccj"));//此属性没有
			bean.setZjldw(ele.attributeValue("jldw"));
			bean.setFjtj(ele.attributeValue("fjtj"));//此属性没有
			bean.setGyztrcl(ele.attributeValue("gyztrcl"));//此属性没有
			bean.setComment(ele.attributeValue("comment"));
			bean.setDataFrom(ele.attributeValue("dataFrom"));

			list.add(bean);
		}

		List<Element> sjzyksjycl = element.selectNodes("CLDE/SJZYKSJYCLDE/sjycldeRecord");
		for(Element ele:sjzyksjycl){
			bean = new CMatBean();
			bean.setCmatType("试件原材料");
			bean.setPplanNumber(element.attributeValue("pplanNumber"));
			bean.setPplanName(element.attributeValue("pplanName"));
			bean.setTechnicsName(element.attributeValue("technicsName"));
			bean.setTechnicsNumber(element.attributeValue("technicsNumber"));
			bean.setTechnicsType(element.attributeValue("technicsType"));
			bean.setPartUseCount(partUseCount);
			bean.setPartName(element.attributeValue("partName"));
			bean.setCindex(cindex);

			bean.setChbm(ele.attributeValue("sjbm"));
			bean.setChmc(ele.attributeValue("name"));
			bean.setSjcc(ele.attributeValue("sjcc"));
			bean.setSjkzjs(ele.attributeValue("sjkzjs"));
			bean.setSjsl(ele.attributeValue("sl"));
			bean.setXhph(ele.attributeValue("xh"));
			bean.setGg(ele.attributeValue("gg"));
			bean.setJstj(ele.attributeValue("jstj"));//此属性没有
			bean.setSccj(ele.attributeValue("sccj"));//此属性没有
			bean.setZjldw(ele.attributeValue("jldw"));
			bean.setFjtj(ele.attributeValue("fjtj"));//此属性没有
			bean.setGyztrcl(ele.attributeValue("gyztrcl"));//此属性没有
			bean.setComment(ele.attributeValue("comment"));
			bean.setDataFrom(ele.attributeValue("dataFrom"));

			list.add(bean);
		}

		//ERP匹配
		List<Element> erpMatchPart = element.selectNodes("GYDE/MATCHPART/MatchPart");
		for(Element ele:erpMatchPart){
			bean = new CMatBean();
			String wzlb = ele.attributeValue("wzlb");
			if(wzlb.startsWith("01")){
				bean.setCmatType("元器件");
			}else if(wzlb.startsWith("02")){
				bean.setCmatType("标准件");
			}
			bean.setPplanNumber(element.attributeValue("pplanNumber"));
			bean.setPplanName(element.attributeValue("pplanName"));
			bean.setTechnicsName(element.attributeValue("technicsName"));
			bean.setTechnicsNumber(element.attributeValue("technicsNumber"));
			bean.setTechnicsType(element.attributeValue("technicsType"));
			bean.setPartUseCount(partUseCount);
			bean.setPartName(element.attributeValue("partName"));
			bean.setCindex(cindex);

			bean.setChbm(ele.attributeValue("chbm"));
			bean.setChmc(ele.attributeValue("chmc"));
			bean.setSjcc("");
			bean.setSjkzjs("");
			bean.setSjsl(ele.attributeValue("gyCount"));
			bean.setXhph(ele.attributeValue("xhph"));
			bean.setGg(ele.attributeValue("gg"));
			bean.setJstj(ele.attributeValue("jstj"));//此属性没有
			bean.setSccj(ele.attributeValue("sccj"));//
			bean.setZjldw(ele.attributeValue("jldw"));
			bean.setFjtj(ele.attributeValue("fjtj"));//
			bean.setGyztrcl(ele.attributeValue("gyztrcl"));//
			bean.setComment(ele.attributeValue("comment"));
			bean.setDataFrom(ele.attributeValue("dataFrom"));
			list.add(bean);
		}

		//ERP新增
		List<Element> erpNewPart = element.selectNodes("GYDE/NEWPART/NewPart");
		for(Element ele:erpNewPart){
			bean = new CMatBean();
			String wzlb = ele.attributeValue("wzlb");
			if(wzlb.startsWith("01")){
				bean.setCmatType("元器件");
			}else if(wzlb.startsWith("02")){
				bean.setCmatType("标准件");
			}
			bean.setPplanNumber(element.attributeValue("pplanNumber"));
			bean.setPplanName(element.attributeValue("pplanName"));
			bean.setTechnicsName(element.attributeValue("technicsName"));
			bean.setTechnicsNumber(element.attributeValue("technicsNumber"));
			bean.setTechnicsType(element.attributeValue("technicsType"));
			bean.setPartUseCount(partUseCount);
			bean.setPartName(element.attributeValue("partName"));
			bean.setCindex(cindex);

			bean.setChbm(ele.attributeValue("chbm"));
			bean.setChmc(ele.attributeValue("chmc"));
			bean.setSjcc("");
			bean.setSjkzjs("");
			bean.setSjsl(ele.attributeValue("sl"));
			bean.setXhph(ele.attributeValue("xhph"));
			bean.setGg(ele.attributeValue("gg"));
			bean.setJstj(ele.attributeValue("jstj"));//此属性没有
			bean.setSccj(ele.attributeValue("sccj"));//
			bean.setZjldw(ele.attributeValue("jldw"));
			bean.setFjtj(ele.attributeValue("fjtj"));//
			bean.setGyztrcl(ele.attributeValue("gyztrcl"));//
			bean.setComment(ele.attributeValue("comment"));
			bean.setDataFrom(ele.attributeValue("dataFrom"));
			list.add(bean);
		}

		//主要材料定额
		List<Element> erpZyclde = element.selectNodes("GYDE/ZYCLDE/zycldeRecord");
		for(Element ele:erpZyclde){
			bean = new CMatBean();
			bean.setCmatType("主要材料");
			bean.setPplanNumber(element.attributeValue("pplanNumber"));
			bean.setPplanName(element.attributeValue("pplanName"));
			bean.setTechnicsName(element.attributeValue("technicsName"));
			bean.setTechnicsNumber(element.attributeValue("technicsNumber"));
			bean.setTechnicsType(element.attributeValue("technicsType"));
			bean.setPartUseCount(partUseCount);
			bean.setPartName(element.attributeValue("partName"));
			bean.setCindex(cindex);

			bean.setChbm(ele.attributeValue("sjbm"));
			bean.setChmc(ele.attributeValue("name"));
			bean.setSl(ele.attributeValue("sl"));
			bean.setXhph(ele.attributeValue("xh"));
			bean.setGg(ele.attributeValue("gg"));
			bean.setJstj(ele.attributeValue("jstj"));//此属性没有
			bean.setSccj(ele.attributeValue("sccj"));//此属性没有
			bean.setZjldw(ele.attributeValue("jldw"));
			bean.setFjtj(ele.attributeValue("fjtj"));//此属性没有
			bean.setGyztrcl(ele.attributeValue("gyztrcl"));//此属性没有
			bean.setComment(ele.attributeValue("comment"));
			bean.setDataFrom(ele.attributeValue("dataFrom"));

			list.add(bean);
		}

		//试件原材料材料定额--装配工艺定额
		List<Element> erpSjyclde = element.selectNodes("GYDE/SJYCLDE/sjycldeRecord");
		for(Element ele:erpSjyclde){
			bean = new CMatBean();
			bean.setCmatType("试件原材料");
			bean.setPplanNumber(element.attributeValue("pplanNumber"));
			bean.setPplanName(element.attributeValue("pplanName"));
			bean.setTechnicsName(element.attributeValue("technicsName"));
			bean.setTechnicsNumber(element.attributeValue("technicsNumber"));
			bean.setTechnicsType(element.attributeValue("technicsType"));
			bean.setPartUseCount(partUseCount);
			bean.setPartName(element.attributeValue("partName"));
			bean.setCindex(cindex);

			bean.setChbm(ele.attributeValue("sjbm"));
			bean.setChmc(ele.attributeValue("name"));
			bean.setSjcc(ele.attributeValue("sjcc"));
			bean.setSjkzjs(ele.attributeValue("sjkzjs"));
			bean.setSjsl(ele.attributeValue("sl"));
			bean.setXhph(ele.attributeValue("xh"));
			bean.setGg(ele.attributeValue("gg"));
			bean.setJstj(ele.attributeValue("jstj"));//此属性没有
			bean.setSccj(ele.attributeValue("sccj"));//此属性没有
			bean.setZjldw(ele.attributeValue("jldw"));
			bean.setFjtj(ele.attributeValue("fjtj"));//此属性没有
			bean.setGyztrcl(ele.attributeValue("gyztrcl"));//此属性没有
			bean.setComment(ele.attributeValue("comment"));
			bean.setDataFrom(ele.attributeValue("dataFrom"));

			list.add(bean);
		}


		//设计资源库匹配
		List<Element> sjkMatchPart = element.selectNodes("GYDE/SJZYKMATCHPART/SjzykMatchPart");
		for(Element ele:sjkMatchPart){
			bean = new CMatBean();
			bean.setCmatType(ele.attributeValue("dataType"));
			bean.setPplanNumber(element.attributeValue("pplanNumber"));
			bean.setPplanName(element.attributeValue("pplanName"));
			bean.setTechnicsName(element.attributeValue("technicsName"));
			bean.setTechnicsNumber(element.attributeValue("technicsNumber"));
			bean.setTechnicsType(element.attributeValue("technicsType"));
			bean.setPartUseCount(partUseCount);
			bean.setPartName(element.attributeValue("partName"));
			bean.setCindex(cindex);

			bean.setChbm(ele.attributeValue("sjbm"));
			bean.setChmc(ele.attributeValue("name"));
			bean.setSjcc("");
			bean.setSjkzjs("");
			bean.setSjsl(ele.attributeValue("gysl"));
			bean.setXhph(ele.attributeValue("xhph"));
			bean.setGg(ele.attributeValue("gg"));
			bean.setJstj(ele.attributeValue("jstj"));//此属性没有
			bean.setSccj(ele.attributeValue("gys"));//
			bean.setZjldw(ele.attributeValue("jldw"));
			bean.setFjtj(ele.attributeValue("fjtj"));//
			bean.setGyztrcl(ele.attributeValue("gyztrcl"));//
			bean.setComment(ele.attributeValue("comment"));
			bean.setDataFrom(ele.attributeValue("dataFrom"));
			list.add(bean);
		}

		//设计资源库新增
		List<Element> sjkNewPart = element.selectNodes("GYDE/SJZYKNEWPART/SjzykNewPart");
		for(Element ele:sjkNewPart){
			bean = new CMatBean();
			bean.setCmatType(ele.attributeValue("dataType"));
			bean.setPplanNumber(element.attributeValue("pplanNumber"));
			bean.setPplanName(element.attributeValue("pplanName"));
			bean.setTechnicsName(element.attributeValue("technicsName"));
			bean.setTechnicsNumber(element.attributeValue("technicsNumber"));
			bean.setTechnicsType(element.attributeValue("technicsType"));
			bean.setPartUseCount(partUseCount);
			bean.setPartName(element.attributeValue("partName"));
			bean.setCindex(cindex);

			bean.setChbm(ele.attributeValue("sjbm"));
			bean.setChmc(ele.attributeValue("name"));
			bean.setSjcc("");
			bean.setSjkzjs("");
			bean.setSjsl(ele.attributeValue("gysl"));
			bean.setXhph(ele.attributeValue("xhph"));
			bean.setGg(ele.attributeValue("gg"));
			bean.setJstj(ele.attributeValue("jstj"));//此属性没有
			bean.setSccj(ele.attributeValue("gys"));//
			bean.setZjldw(ele.attributeValue("jldw"));
			bean.setFjtj(ele.attributeValue("fjtj"));//
			bean.setGyztrcl(ele.attributeValue("gyztrcl"));//
			bean.setComment(ele.attributeValue("comment"));
			bean.setDataFrom(ele.attributeValue("dataFrom"));
			list.add(bean);
		}
		return list;
	}

	/**
	 * 根据工艺的oid下载工艺
	 *
	 * @param technicsOid
	 */
	public static void downloadTechncisById(String technicsOid) {
		List<Object> list = new ArrayList<Object>();
		try {
			list = TechnicsIntf.searchTechnics(technicsOid);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (list.size() == 1) {
			Vector vector = (Vector) list.get(0);
			if (vector.size() == 4) {
				String technicsZipName = (String) vector.get(0);
				String technicsFolderName = technicsZipName;
				if (technicsFolderName.endsWith(".zip")) {
					technicsFolderName = technicsFolderName.substring(0,
							technicsFolderName.lastIndexOf(".zip"));
				}
				byte[] bytes = (byte[]) vector.get(1);
				String lifeCycle = (String) vector.get(2);
				String version = (String) vector.get(3);

				String category = getCategoryByTechnicsFolderName(technicsFolderName);
				if (category == null) {
					return;
				} else {
					String path = WorkSpaceUtil.getTechnicsRootPath(category)
							+ technicsFolderName;
					File file = new File(path);
					if (file.exists()) {
						if (SwingUtil.showConfirmDialog("当前工艺数据已存在1，是否覆盖？",
								Constants.TIP, 2) != JOptionPane.YES_OPTION) {
							return;
						}
						FileUtil.deleteSubFile(file);
					} else {
						file.mkdirs();
					}
					String temp = WorkSpaceUtil.getTempRootPath()
							+ technicsZipName;
					file = new File(temp);
					if (file.exists()) {
						file.delete();
					}
					FileUtil.writeBytes(temp, bytes);
					ApacheZipUtil.decompress(temp, path);
					String xmlPath = path + File.separator + technicsFolderName
							+ ".xml";
					Document document = XmlUtil.getDocument(xmlPath);
					Element element = document.getRootElement().element(
							"QMFawTechnicsInfo");
					XmlUtility.setAttributeValue(element, "version", version);
					XmlUtility.setAttributeValue(element, "lifecycle",
							lifeCycle);
					XmlUtil.writeDocument(document, xmlPath);
				}
			}
		}

	}

	/**
	 * 根据文件夹的名称获取文件类型
	 *
	 * @param folderName
	 * @return
	 */
	private static String getCategoryByTechnicsFolderName(String folderName) {
		String category = null;
		if (folderName != null) {
			if (folderName.indexOf("_fg") != -1) {
				category = "rework";
			} else if (folderName.indexOf("_ls") != -1) {
				category = "temp";
			} else {
				category = "";
			}
		}
		return category;
	}

	/**
	 * 完成工艺任务
	 *
	 * @param workItemOid
	 * @return
	 * @return
	 */
	public static List<Object> completeTechnicsTask(String workItemOid,
			JFrame frame) {
		List<Object> returnList = new ArrayList<Object>();
		Map<String, Object> map = null;
		boolean flag = false;
		List<Object> list = new ArrayList<Object>();
		try {
			list = TechnicsIntf.getCompleteCondition(workItemOid);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.debug("list= " + list);
		if (list != null && list.size() == 2) {
			Map<String, List<String>> params = (Map<String, List<String>>) list
					.get(0);
			List<String> routes = (List<String>) list.get(1);
			if ((params != null && params.size() != 0)
					|| (routes != null && routes.size() != 0)) {
				WorkItemSelectDialog dialog = new WorkItemSelectDialog(params,
						routes, frame);
				map = dialog.showDialog();
			}
			flag = true;
		}
		returnList.add(flag);
		returnList.add(map);
		return returnList;
	}

	public static void main1(String[] args) {
		// RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
		// methodServer.setUserName("zlb01");
		// methodServer.setUserName("100002");
		// methodServer.setUserName("zlb06");
		// methodServer.setPassword("1");
		try {
			System.setProperty("swing.useSystemFontSettings", "0");
			System.setProperty("swing.handleTopLevelPaint", "false");
			System.setProperty("-Dswing.aatext", "true");

			LookUtils.setLookAndTheme(new Plastic3DLookAndFeel(),
					new ExperienceBlue());
		} catch (Exception e) {
			e.printStackTrace();
		}
		completeTechnicsTask("1486024", null);
		// downloadTechncisById("1475778");
	}

	// /**
	// * 获取所有参装件
	// *
	// * @param techElement
	// * @return
	// */
	// private static List<Element> getAllPartOids(Element techElement) {
	// List<Element> elements = new ArrayList<Element>();
	// String uri = techElement.getNamespaceURI();
	// HashMap<String, String> map = new HashMap<String, String>();
	// map.put("xx", uri);
	// String path =
	// " /technics/QMFawTechnicsInfo/steps/QMProcedureInfo/paces/QMProcedureInfo/parts/QMPartInfo/";
	// XPath xpath = DocumentHelper.createXPath(path);
	// xpath.setNamespaceURIs(map);
	// List<Element> list = xpath.selectNodes(techElement.getDocument());
	// if (list != null && list.size() != 0) {
	// for (Element temp : list) {
	// elements.add(temp);
	// }
	// }
	// return elements;
	// }


	public static String getGroupName(String partOid){
		String zzdw = "";
		try {
			zzdw = TechnicsIntf.getUsertechnicsGroupName(partOid);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		if(zzdw!=null){
//			String[] groups = zzdw.split("_");
//			if(groups.length>=2){
//				String group = groups[1];
//				return group;
//			}
//		}
		return zzdw;
	}

	public static String getGroupName(){
		String zzdw = "";
		try {
			zzdw = TechnicsIntf.getUsertechnicsGroupName();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return zzdw;
	}

	public static String getGroupValue(String name) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("一车间", "1");
		map.put("二车间", "2");
		map.put("三车间", "3");
		map.put("四车间", "4");
		map.put("五车间", "5");
		map.put("六车间", "6");
		map.put("七车间", "7");
		map.put("八车间", "8");
		map.put("九车间", "9");
		map.put("十车间", "10");

		if(map.containsKey(name)) {
			return map.get(name);
		} else {
			if(name.contains("_")){
				name = name.substring(name.lastIndexOf("_")+1);
				return name;
			}else{
				return name;
			}
		}
	}

	/**
	 * 检验前置工序是否合理
	 * @param newTechnicsPart
	 * @param techElement
	 * @return
	 */
	public static String checkBeforeStep(NewTechnicsPart newTechnicsPart, Element techElement) {
		String msg = "";
		List<Object> check = parseResource(techElement);
		CheckTechnics checkTechnics = (CheckTechnics) check.get(0);
		List<StepObject> stepObjects = checkTechnics.getStepObjects();
		if (stepObjects != null) {
			ArrayList<String> strList = new ArrayList<String>();
			for (StepObject stepObject : stepObjects) {
				String step = stepObject.getStepNumber();
				strList.add(step);
				String pre = stepObject.getPreStep();
				if(!"".equals(step) && !"".equals(pre) && step!=null && pre!=null){
					List<String> result = Arrays.asList(pre.split(","));
					for(int i=0;i<result.size();i++){
						String preNumber = result.get(i).substring(0, result.get(i).lastIndexOf("_"));
						if(!strList.contains(preNumber)){
							msg = step;
							break;
						}
						int stepNumber = Integer.parseInt(step);
						int preStep = Integer.parseInt(preNumber);
						if(stepNumber <= preStep){
							msg = step;
							break;
						}
					}
				}
			}
		}
		return msg;
	}

	/**
	 * 检查preBsoID nextBsoID 是否都属于现有的bsoID
	 * @param techElement
	 * @return
	 */
	public static String checkBsoID(Element techElement) {
		String msg = "";
		Element element;
		if (techElement != null
				&& (element = techElement.element("steps")) != null) {
			Map<String,String> duplicateBsoIds = new HashMap<String, String>();
			List<Element> elements = element.elements("QMProcedureInfo");
			List<String> bsoIDList = new ArrayList<String>();
			for (Element stepElement : elements) {
				String bsoID = stepElement.attributeValue("bsoID");
				if(bsoID != null && !"".equals(bsoID)){
					if(bsoIDList.contains(bsoID)){
						String newBsoId = new UID().toString();
						duplicateBsoIds.put(bsoID,newBsoId);
						XmlUtility.setAttributeValue(stepElement,"bsoID",newBsoId);
					}
					bsoIDList.add(bsoID);
				}
			}
			if(duplicateBsoIds.size()>0){
				return "duplicateBsoIds";
			}
			for (Element stepElement : elements) {
				String preBsoID = stepElement.attributeValue("preBsoID");
				String nextBsoID = stepElement.attributeValue("nextBsoID");
				if(preBsoID != null && !"".equals(preBsoID)){
					String[] strings = preBsoID.split(",");
					if(strings != null) {
						for(String string : strings) {
							if(!bsoIDList.contains(string)){
								msg = string;
							}
						}
					}
				}
				if(nextBsoID != null && !"".equals(nextBsoID)){
					String[] strings = nextBsoID.split(",");
					if(strings != null) {
						for(String string : strings) {
							if(!bsoIDList.contains(string)){
								msg = string;
							}
						}
					}
				}
			}
		}
		return msg;
	}

	/**
	 * 规范工步stepNumber顺序
	 * @param techElement
	 * @return
	 */
	public static void standardPaceStepNumber(Element techElement) {
		Element element;
		if (techElement != null
				&& (element = techElement.element("steps")) != null) {
			List<Element> elements = element.elements("QMProcedureInfo");
			for(Element stepElement : elements) {
				Element pace = stepElement.element("paces");
				if(pace != null) {
					List<Element> paceElements = pace.elements();
					if(paceElements != null) {
						for(int i = 1; i <= paceElements.size(); i++) {
							Element p = paceElements.get(i - 1);
							p.setAttributeValue("stepNumber", String.valueOf(i));
						}
					}
				}
			}
		}
	}

	/**
	 * 校验PBOM参装数量
	 */
	public static void checkPbomCzNumber(XWTreeNode technicsNode, Element technicsElement, VaActionProgressBar progressBar){
		try {
			Map<String, String> partMap = new HashMap<String, String>();
			if (technicsNode != null) {
				XWTreeNode parent = (XWTreeNode) technicsNode.getParent();
				XWTreeObject parentObj = parent.getObject();
				if (parentObj instanceof XWPartTreeObject) {
					Element partElenet = parentObj.getTreeCellData();
					List<Element> childProducts = BomXMLUtil.getChildProducts(partElenet);
					for (Element element : childProducts) {
						partMap.put(element.attributeValue("partNumber"), element.attributeValue("gysl"));
					}
				}
			}
			Map<String, PDFBuilder.CzjPart> czjPartMap = PDFBuilder.getAllCzjMap(technicsElement);
			for (Map.Entry<String, String> entry : partMap.entrySet()) {
				String partNumber = entry.getKey();
				if (czjPartMap.containsKey(partNumber)) {
					double czjCount = czjPartMap.get(partNumber).getCount();
					double partGysl = Double.valueOf(entry.getValue());
					if (czjCount > partGysl) {
						progressBar.finish();
						progressBar.setVisible(false);
						JOptionPane.showMessageDialog(null, partNumber + "参装数量不合理，请重新进行参装！");
						return;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isHasAdditionTables(Element technicsElement ){
		//Document doc = WorkSpaceUtil.getTechnicsDocumentByTechnicsNumber(technicsNumber);
		//Element technicsElement = XmlUtility.getTechnicsElement(doc);
		List<Element> technicsAdditionTables = XmlUtility.getTechnicsAdditionTables(technicsElement);
		if(technicsAdditionTables != null && technicsAdditionTables.size() > 0){
			return true;
		}
		List<Element> allSteps = XmlUtility.getAllSteps(technicsElement);
		for(Element step : allSteps){
			List<Element> stepAdditionTables = XmlUtility.getTechnicsAdditionTables(step);
			if(stepAdditionTables != null && stepAdditionTables.size() > 0){
				return true;
			}
			List<Element> allPaces = XmlUtility.getAllPaces(step);
			for(Element pace : allPaces){
				List<Element> paceAdditionTables = XmlUtility.getTechnicsAdditionTables(pace);
				if(paceAdditionTables != null && paceAdditionTables.size() > 0){
					return true;
				}
			}

		}
		return false;
	}

	public static void checkWordToPDF(Element technicsEle, VaActionProgressBar progressBar){
		//String technicsNumber = technicsEle.attributeValue("technicsNumber");
		boolean isTransPdf = TechnicsUtil.checkWordTransPDF(technicsEle);
		if(!isTransPdf){
			progressBar.finish();
			progressBar.setVisible(false);
			JOptionPane.showMessageDialog(null,  "Word转换PDF失败，请尝试通过以下方式解决!\n" +
					"1、请检查确认jacob插件是否安装;\n" +
					"2、若1已安装，请尝试升级本地Word至Word2010;\n" +
					"3、若以上都不行，请联系管理员！");
		}
	}

/*	public static boolean checkWordTransPDF(String technicsNumber){
		boolean hasAdditionTable = isHasAdditionTables(technicsNumber);
		if(hasAdditionTable){
			ActiveXComponent app = null;
			try {
				app = new ActiveXComponent("Word.Application");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(app == null){
					return false;
				}
			}
		}
		return true;
	}*/

	public static boolean checkWordTransPDF(Element technicsElement){
		boolean hasAdditionTable = isHasAdditionTables(technicsElement);
		if(hasAdditionTable){
			ActiveXComponent app = null;
			try {
				app = new ActiveXComponent("Word.Application");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(app == null){
					return false;
				}
			}
		}
		return true;
	}

	public static void checkIsHasWorkflow(Element technicsEle,VaActionProgressBar progressBar){
		try {
			String technicsNumber = technicsEle.attributeValue("technicsNumber");
			String docState = TechnicsIntf.getDocumentStateByNumber(technicsNumber);
			if (!"".equals(docState) && !"正在工作".equals(docState) && !"修改中".equals(docState)) {
				JOptionPane.showMessageDialog(null, "此工艺文件已经提交签审，不能再修改！");
				progressBar.finish();
				progressBar.setVisible(false);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 校验参装件是否多装漏装
	 * @param partElement
	 * @param technicsEle
	 * @return
	 * @throws Exception
	 */
	public static List<String> checkCzj(Element partElement, Element technicsEle) throws Exception {
		List<String> errorPartList = new ArrayList<String>();
		Map<String, Double> noEpmPartMap = new HashMap<String, Double>();
//		Map<String, List<String>> hasEpmPartMap = new HashMap<String, List<String>>();
		Map<String, Double> czPartMap = new HashMap<String, Double>();
		List<Element> childProducts = BomXMLUtil.getChildProducts(partElement);
//		List<String> occIdList;
		double sl = 0;
		//获取PBOM信息
		for (Element element : childProducts) {
//			occIdList = new ArrayList<String>();
			String partNumber = element.attributeValue("partNumber");
//			String partOid = element.attributeValue("oid");
			String gysl = element.attributeValue("gysl");
			String mtype = element.attributeValue("MTYPE");
			if("标准件".equals(mtype) || "元器件".equals(mtype) || "外购件".equals(mtype) || "主要材料".equals(mtype)){
				continue;
			}
			if(gysl != null){
				sl = Double.valueOf(gysl);
			}
//			String occidStr = element.attributeValue("occId");
//			String[] occids = occidStr.split(",");
//			occIdList = Arrays.asList(occids);
//			boolean hasEpm = TechnicsIntf.checkIsHasEpm(partOid);
//			if(hasEpm){
//				hasEpmPartMap.put(partNumber,occIdList);
//			}else{
//			}
			noEpmPartMap.put(partNumber,sl);
		}

		//获取定额信息
		Element gydeEle = technicsEle.element("GYDE");
		List<Element> gydeElements;
		if(gydeEle != null){
			double amount = 0;
			gydeElements = XmlUtility.getTechnicsGYDENewPart(gydeEle);
			for(Element ele : gydeElements){
				String number = ele.attributeValue("number");
				String gysl = ele.attributeValue("sl");
				if(gysl != null){
					amount = Double.valueOf(gysl);
				}
				if(noEpmPartMap.containsKey(number)){
					amount = CommonUtil.addDouble(amount,noEpmPartMap.get(number));
					noEpmPartMap.put(number,amount);
				}else{
					noEpmPartMap.put(number,amount);
				}
			}
			gydeElements = XmlUtility.getTechnicsGYDEMatchPart(gydeEle);
			for(Element ele : gydeElements){
				String number = ele.attributeValue("chbm");
				String gysl = ele.attributeValue("gyCount");
				if(gysl != null){
					amount = Double.valueOf(gysl);
				}
				if(noEpmPartMap.containsKey(number)){
					amount = CommonUtil.addDouble(amount,noEpmPartMap.get(number));
					noEpmPartMap.put(number,amount);
				}else{
					noEpmPartMap.put(number,amount);
				}
			}
			gydeElements = XmlUtility.getTechnicsSJZYKGYDENewPart(gydeEle);
			for(Element ele : gydeElements){
				String number = ele.attributeValue("sjbm");
				String gysl = ele.attributeValue("gysl");
				if(gysl != null){
					amount = Double.valueOf(gysl);
				}
				if(noEpmPartMap.containsKey(number)){
					amount = CommonUtil.addDouble(amount,noEpmPartMap.get(number));
					noEpmPartMap.put(number,amount);
				}else{
					noEpmPartMap.put(number,amount);
				}
			}
			gydeElements = XmlUtility.getTechnicsSJZYKGYDEMatchPart(gydeEle);
			for(Element ele : gydeElements){
				String number = ele.attributeValue("sjbm");
				String gysl = ele.attributeValue("gysl");
				if(gysl != null){
					amount = Double.valueOf(gysl);
				}
				if(noEpmPartMap.containsKey(number)){
					amount = CommonUtil.addDouble(amount,noEpmPartMap.get(number));
					noEpmPartMap.put(number,amount);
				}else{
					noEpmPartMap.put(number,amount);
				}
			}

		}
		//获取工序工步上已参装的信息
		List<Element> allSteps = XmlUtility.getAllSteps(technicsEle);
		for(Element stepElement : allSteps){
			Element stepPartsElement = XmlUtility.getParts(stepElement);
			if (stepPartsElement != null) {
				List<Element> czjElements = stepPartsElement.elements("QMPartInfo");
				for(Element partEle : czjElements){
					String partNumber = partEle.attributeValue("partNumber");
					String useCount = partEle.attributeValue("useCount");
					String oid = partEle.attributeValue("oid");
					String zcmark = partEle.attributeValue("ZCMARK");
					String occId = partEle.attributeValue("occId");
					if(useCount != null){
						sl = Double.valueOf(useCount);
					}
					/*boolean hasEpm = TechnicsIntf.checkIsHasEpm(oid);
					//如果有epm，检验occid
					if(hasEpm){
						if(hasEpmPartMap.containsKey(partNumber)){
							occIdList = hasEpmPartMap.get(partNumber);
							if(!occIdList.contains(occId)){
								if(!errorPartList.contains(occId)){
									errorPartList.add(occId);
								}
							}else{
								occIdList.remove(occId);
							}
						}else{
							if(!errorPartList.contains(occId)){
								errorPartList.add(occId);
							}
						}
					}else{*/
						//没有epm，校验数量
						if(czPartMap.containsKey(partNumber)){
							Double count = czPartMap.get(partNumber);
							if("Z".equals(zcmark)){
								czPartMap.put(partNumber,CommonUtil.addDouble(count, sl));
							}else if("C".equals(zcmark)){
								czPartMap.put(partNumber,CommonUtil.subDouble(count, sl));
							}
						}else{
							if("Z".equals(zcmark)){
								czPartMap.put(partNumber,CommonUtil.addDouble(0, sl));
							}else if("C".equals(zcmark)){
								czPartMap.put(partNumber,CommonUtil.subDouble(0, sl));
							}
						}

//					}
				}
			}
			List<Element> allPaces = XmlUtility.getAllPaces(stepElement);
			for(Element paceElement : allPaces){
				Element pacePartsElement = XmlUtility.getParts(paceElement);
				if (pacePartsElement != null) {
					List<Element> czjElements = pacePartsElement.elements("QMPartInfo");
					for(Element partEle : czjElements){
						String partNumber = partEle.attributeValue("partNumber");
						String useCount = partEle.attributeValue("useCount");
						String oid = partEle.attributeValue("oid");
						String zcmark = partEle.attributeValue("ZCMARK");
						String occId = partEle.attributeValue("occId");
						if(useCount != null){
							sl = Double.valueOf(useCount);
						}
						/*boolean hasEpm = TechnicsIntf.checkIsHasEpm(oid);
						//如果有epm，检验occid
						if(hasEpm){
							if(hasEpmPartMap.containsKey(partNumber)){
								occIdList = hasEpmPartMap.get(partNumber);
								if(!occIdList.contains(occId)){
									if(!errorPartList.contains(occId)){
										errorPartList.add(occId);
									}else{
										occIdList.remove(occId);
									}
								}
							}else{
								if(!errorPartList.contains(occId)){
									errorPartList.add(occId);
								}
							}
						}else{*/
							//没有epm，校验数量
							if(czPartMap.containsKey(partNumber)){
								Double count = czPartMap.get(partNumber);
								if("Z".equals(zcmark)){
									czPartMap.put(partNumber,CommonUtil.addDouble(count, sl));
								}else if("C".equals(zcmark)){
									czPartMap.put(partNumber,CommonUtil.subDouble(count, sl));
								}
							}else{
								if("Z".equals(zcmark)){
									czPartMap.put(partNumber,CommonUtil.addDouble(0, sl));
								}else if("C".equals(zcmark)){
									czPartMap.put(partNumber,CommonUtil.subDouble(0, sl));
								}
							}

//						}
					}
				}
			}
		}
		for(Map.Entry<String, Double> entry : czPartMap.entrySet()){
			String partNumber = entry.getKey();
			Double amount = entry.getValue();
			Double totalCount = noEpmPartMap.get(partNumber);
			if(totalCount == null){
				errorPartList.add(partNumber);
			}else{
				if(CommonUtil.subDouble(amount,totalCount) != 0){
					errorPartList.add(partNumber);
				}else{
					noEpmPartMap.remove(partNumber);
				}
			}
		}
//		for(Map.Entry<String, List<String>> entry : hasEpmPartMap.entrySet()){
//			String pNumber = entry.getKey();
//			List<String> idList = entry.getValue();
//			if(idList.size() > 0 && !errorPartList.contains(pNumber)){
//				errorPartList.add(pNumber);
//			}
//		}
		for(Map.Entry<String, Double> entry : noEpmPartMap.entrySet()){
			if(!errorPartList.contains(entry.getKey())){
				errorPartList.add(entry.getKey());
			}
		}
		return errorPartList;
	}

	/**
	 * 方法功能: 校验工艺辅料数量是否填写完整

	 * @param technicsElement
	 * @return java.lang.String
	 * @author LB
	 * @date 2020/10/16
	 */
	public static String checkMaterial(Element technicsElement) {
		List<Element> allSteps = XmlUtility.getAllSteps(technicsElement);
		String technicsName = XmlUtility.getAttributeValue(technicsElement, "technicsName");
		Element materials;
		List<Element> materialInfoList;
		for (Element step : allSteps) {
			String stepNumber = XmlUtility.getAttributeValue(step, "stepNumber");
			materials = XmlUtility.getMaterials(step);
			materialInfoList = materials.elements("QMMaterialInfo");
			if(materialInfoList != null && materialInfoList.size() > 0){
				for (Element materialInfo : materialInfoList) {
					String sl = XmlUtility.getAttributeValue(materialInfo, "sl");
					if(sl == null || sl.isEmpty()){
						return  technicsName+"的工序 " + stepNumber + " 存在未填写数量的工艺辅料，请填写完整后再提交";
					}
				}
			}
			List<Element> allPaces = XmlUtility.getAllPaces(step);
			for (Element pace : allPaces) {
				String paceNumber = XmlUtility.getAttributeValue(pace, "stepNumber");
				materials = XmlUtility.getMaterials(pace);
				materialInfoList = materials.elements("QMMaterialInfo");
				if(materialInfoList != null && materialInfoList.size() > 0){
					for (Element materialInfo : materialInfoList) {
						String sl = XmlUtility.getAttributeValue(materialInfo, "sl");
						if(sl == null || sl.isEmpty()){
							return  technicsName+"的工序-"+ stepNumber +",工步-" + paceNumber + " 存在未填写数量的工艺辅料，请填写完整后再提交";
						}
					}
				}
			}
		}
		return "";
	}

	public static String checkUpload(Element techElement) {
		String msg = "";
		Element element;
		if(techElement != null && (element = techElement.element("steps")) != null) {
			String keyStepTableName = TechnicsIntf.getSystemConfiguration("KeyStepTableName");
			if(StringUtil.isEmpty(keyStepTableName)) {
				keyStepTableName = "A.6关键工序检验卡";
			}
			List<Element> elements = element.elements("QMProcedureInfo");
			for(Element stepElement : elements) {
				String stepNumber = stepElement.attributeValue("stepNumber");
				//关键工序，需要校验工序下工步是否有A.6关键工序检验卡
				String isKey = stepElement.attributeValue("isKey");
				boolean isHasKeyTable = true;
				if("true".equals(isKey)){
					isHasKeyTable = false;
				}
				Element paces = stepElement.element("paces");
				List<Element> paceElement = paces.elements();
				if (paceElement != null) {
					for (Element pace : paceElement) {
						String paceNumber = pace.attributeValue("stepNumber");
						if(!isHasKeyTable) {
							List<Element> schemaDatas = XmlUtility.getSchemaDatas(pace);
							for(Element schemaData : schemaDatas) {
								String name = schemaData.attributeValue("name");
								if(keyStepTableName.equals(name)) {
									isHasKeyTable = true;
									break;
								}
							}
						}
					}
				}
				if(!isHasKeyTable) {
					msg += "工序" + stepNumber + "为关键工序，未使用"+keyStepTableName+"！";
				}
			}
		}
		return msg;
	}

}
