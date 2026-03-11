package com.ptc.extend.ixb;

import java.util.Enumeration;

import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.structure.EPMVariantLink;
import wt.facade.ixb.IxbElement;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.collections.WTArrayList;
import wt.util.WTException;

public class CmExpImpEPMVariantLink extends CmExpImpLink {

	public CmExpImpEPMVariantLink(Object obj, CmExporter expHdl)
			throws WTException {
		super(obj, expHdl);
	}

	public CmExpImpEPMVariantLink(Object obj, CmImporter impHdl, String fname)
			throws WTException {
		super(obj, impHdl, fname);
	}

	public String getRootTag() {
		return CmExpImpConstraints.XML_EPMVARIANTLINK;
	}

	public void exportObject(Object obj) throws WTException {
	}

	public Object importObject() throws WTException {
		return importAttribute();
	}

	public WTArrayList importObjects() throws WTException {
		return null;
	}

	private WTArrayList importAttribute() throws WTException {
		WTArrayList list = new WTArrayList();
		try {
			boolean missingobj = false;
			boolean dofailed = false;
			Enumeration variantRecords = getElements("variant");
			while (variantRecords.hasMoreElements()) {
				IxbElement variant = (IxbElement) variantRecords.nextElement();
				String variantNumber = getElementValue(variant,
						"variant/number");
				String variantVersion = getElementValue(variant,
						"variant/versions");
				variantVersion = getPropertiesValue(variantVersion);
				String variantIteration = getElementValue(variant,
						"variant/iteration");

				EPMDocument variantEPM = (EPMDocument) CmExpImpSearchHelper
						.searchIteratedByNumberVersionIteration(
								EPMDocument.class, variantNumber,
								variantVersion, variantIteration);

				String genericNumber = getElementValue(variant,
						"generic/number");
				EPMDocument genericEPM = (EPMDocument) CmExpImpSearchHelper
						.searchLatestIteratedByNumber(EPMDocument.class,
								genericNumber);

				logger("==>Import EPMVariantLink:number=<" + variantNumber
						+ "> Ver=" + variantVersion + "." + variantIteration
						+ " to master:number=<" + genericNumber + ">");
				if (!isTypeDefinitionImported()) {
					logger("==>WARNING:Import EPMVariantLink:TypeDefinition not imported, SKIP!");
				} else {
					if (variantEPM == null) {
						logger("==>Missing EPMDocument:" + variantNumber + " "
								+ variantVersion + "." + variantIteration);
						this.impHdl
								.putInMissingObjectSet("EPMDocument",
										variantNumber, variantVersion,
										variantIteration);
					}
					if (genericEPM == null) {
						logger("==>Missing EPMDocumentMaster:" + genericNumber);
						this.impHdl.putInMissingMasterObjectSet(
								"EPMDocumentMaster", genericNumber);
					}
					if ((variantEPM == null) || (genericEPM == null)) {
						missingobj = true;
					} else {
						EPMDocumentMaster mastered = (EPMDocumentMaster) genericEPM
								.getMaster();
						EPMVariantLink link = CmExpImpSearchHelper
								.searchEPMVariantLink(variantEPM, mastered);
						if (link == null) {
							link = EPMVariantLink
									.newEPMVariantLink(variantEPM, mastered);
							String isRequired = getElementValue(variant,
									"isRequired");
							if (isRequired != null)
								link.setRequired(Boolean
										.valueOf(isRequired));
							PersistenceServerHelper.manager.insert(link);
							if (link != null) {
								list.add(link);
								logger("==>EPMVariantLink import OK!");
							} else {
								dofailed = true;
							}
						} else {
							logger("==>EPMVariantLink already imported, IGNORE!");
						}
					}
				}
			}
			if (missingobj)
				throw new MissingObjectException(
						"==>Missing objects when create EPMVariantLink.");
			if (dofailed)
				throw new WTException(
						"==>Not All EPMVariantLink Imported, need to rearrange.");
		} catch (Exception e) {
			logger(e.getMessage());
			if ((e instanceof WTException))
				throw ((WTException) e);
			throw new WTException(e);
		}
		return list;
	}

}