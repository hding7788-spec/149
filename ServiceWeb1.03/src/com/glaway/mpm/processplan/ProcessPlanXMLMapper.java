/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.glaway.mpm.processplan;

import java.io.*;
import java.util.List;
import java.util.Vector;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import wt.inf.container.WTContainer;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;


import com.glaway.mpm.util.MPMProcessPlanUtil;
import com.glaway.mpm.util.MPMResourceUtil;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import com.ptc.windchill.mpml.resource.MPMProcessMaterial;
import com.ptc.windchill.mpml.resource.MPMResourceGroup;
import com.ptc.windchill.mpml.resource.MPMTooling;

/**
 *
 * @author hywang
 */
public class ProcessPlanXMLMapper extends DefaultHandler {

	private WTContainer wtContainer;
	private MPMProcessPlan processPlan;
	private MPMOperation operation;
	private MPMOperation subOperation;
	private WTPart wtPart;
	private MPMResourceGroup equipment;
	private MPMTooling tool;
	private MPMProcessMaterial material;
	private CharArrayWriter contents = new CharArrayWriter();

	private Boolean operationTag = false;
	private Boolean subOperationTag = false;

	public ProcessPlanXMLMapper() {
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

		System.out.println("SAX Event: START ELEMENT[ " + localName + " ]");

		contents.reset();
		try {
			if ("QMFawTechnicsInfo" == localName) {

				processPlan = MPMProcessPlanUtil.createMPMProcessPlan(attr, wtContainer);

			}

			if ("steps" == localName) {
				operationTag = true;
			}

			if ("QMProcedureInfo" == localName) {
				if (this.subOperationTag == true) {
//					subOperation = MPMProcessPlanUtil.createSubOperation(attr, wtContainer);
				} else {
					operation = MPMProcessPlanUtil.createOperation(attr, wtContainer);
				}
			}

			if ("paces" == localName) {
				subOperationTag = true;
			}

			if ("equips" == localName) {
			}

			if ("QMEquipmentInfo" == localName) {
//				equipment = MPMResourceUtil.getMPMResourceGroupByNumber(attr.getValue(""));
			}

			if ("tools" == localName) {

			}
//			if ("QMToolInfo" == localName) {
//				tool = MPMResourceUtil.getMPMToolingByNumber(attr.getValue(""));
//			}

			if ("materials" == localName) {

			}

			if ("QMMaterialInfo" == localName) {
				material = MPMResourceUtil.getMPMProcessMaterialByNumber(attr.getValue(""));
			}
			if ("parts" == localName) {

			}

			if ("QMPartInfo" == localName) {
				wtPart = WTPartUtil.getLatestPartByPartNumber(attr.getValue(""));
			}
			if ("images" == localName) {

			}
			if ("DrawingInfo" == localName) {

			}
			if ("procedureContent" == localName) {
			}
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		System.out.println("SAX Event: END ELEMENT[ " + localName + " ]");

		if ("QMFawTechnicsInfo" == localName) {

		}
		if ("steps" == localName) {
			operationTag = false;
		}
		if ("QMProcedureInfo" == localName) {
			if (subOperation != null) {
				subOperation = null;
			} else if (operation != null) {
				operation = null;
			}
		}
		if ("paces" == localName) {
			subOperationTag = false;
		}
		if ("equips" == localName) {

		}
		if ("QMEquipmentInfo" == localName) {

		}
		if ("tools" == localName) {

		}

		if ("QMToolInfo" == localName) {

		}

		if ("materials" == localName) {

		}

		if ("QMMaterialInfo" == localName) {

		}

		if ("parts" == localName) {

		}

		if ("QMPartInfo" == localName) {

		}

		if ("images" == localName) {

		}

		if ("DrawingInfo" == localName) {

		}

		if ("procedureContent" == localName) {

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

	public static boolean initData(InputStream stream) {
		try {
			ProcessPlanXMLMapper mapper = new ProcessPlanXMLMapper();
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(mapper);
			reader.parse(new InputSource(stream));
			return true;
		} catch (IOException ex) {
			return false;
		} catch (SAXException ex) {
			return false;
		}
	}


	private Boolean stringToBoolean(String str) {
		Boolean tag = false;
		if ("true".equals(str.toLowerCase())) {
			tag = true;
		} else if ("false".equals(str.toLowerCase())) {

		}
		return tag;
	}

}