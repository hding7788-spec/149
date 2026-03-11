package com.ptc.extend.ixb;

import java.sql.Timestamp;
import java.util.Calendar;

import wt.facade.ixb.IxbElement;
import wt.fc.PersistenceServerHelper;
import wt.fc.collections.WTArrayList;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.ptc.extend.util.ObjectProperty;

import ext.sast.supply.GLSupply;

public class CmExpImpGLSupply extends CmExpImpPersistable {

	public CmExpImpGLSupply() throws WTException {
	}

	public CmExpImpGLSupply(CmExporter expHdl) throws WTException {
		super(expHdl);
	}

	public CmExpImpGLSupply(CmImporter impHdl, String fname) throws WTException {
		super(impHdl, fname);
	}

	public String getRootTag() {
		return CmExpImpConstraints.XML_GLSUPPLY;
	}

	public void exportObject(Object obj) throws WTException {
		if (!(obj instanceof GLSupply))
			throw new WTException("Object not GLSupply.");
		GLSupply supply = (GLSupply) obj;
		logger("==>Export GLSupply:" + ObjectProperty.getObjectDisplay(supply));
		exportAttribute(supply);
		this.expHdl.addExportedObject(supply);
	}

	private void exportAttribute(GLSupply supply) throws WTException {
		exportUfidAttribute(supply, this.root);
		exportLocalIdAttribute(supply, this.root);
		exportSupplyAttribute(supply, this.root);
		exportDomainFolderAttribute(supply, this.root);
		exportLifecycleAttribute(supply, this.root);
		exportTypeDefinitionAttribute(supply, this.root);
		exportIBAAttribute(supply, this.root);
		reallyStore();
	}

	private void exportSupplyAttribute(Object obj, IxbElement ixbelement) throws WTException {
	}


	@Override
	public Object importObject() throws WTException {
		Object obj = importAttribute();
		if (obj != null)
			this.impHdl.pubImportedObject(obj, getRemoteId());
		return obj;
	}

	private Object importAttribute() throws WTException {
		try {
			GLSupply supply = (GLSupply) CmExpImpSearchHelper
					.getGLSupplyByNumber(this.number);
			if (supply != null) {
				this.impHdl.putInExistedHashtable(getRemoteId(), supply);
				return supply;
			} else {
				logger.log("==>Create new Object: GLSupply number=<"
						+ this.number + ">");
				supply = createNewObject();
			}
			if (supply != null) {
				this.impHdl.putInNewCreatedHashtable(getRemoteId(), supply);
			}

			return supply;
		} catch (Exception exception) {
			logger.log("Exception in ProcessEnvelope, fname=<" + getPfilename()
					+ ">");
			processException(exception);
		}
		return null;
	}

	private GLSupply createNewObject() throws WTException, WTPropertyVetoException {
		GLSupply supply =  GLSupply.newGLSupply();
		long nowtime = Calendar.getInstance().getTimeInMillis();
		String createStampStr = getElementValue(root, "createtime");
		Timestamp createStamp;
		if (createStampStr != null)
			createStamp = new Timestamp(Long.parseLong(createStampStr));
		else {
			createStamp = new Timestamp(nowtime);
		}
		String modifyStampStr = getElementValue(root, "modifytime");
		Timestamp modifyStamp;
		if (modifyStampStr != null)
			modifyStamp = new Timestamp(Long.parseLong(modifyStampStr));
		else
			modifyStamp = new Timestamp(nowtime);
		String s = getElementValue(this.root, "number");
		supply.setNumber(s);
		s = getElementValue(this.root, "name");
		supply.setName(s);
		s = getElementValue(this.root, "code");
		supply.setCode(s);
		s = getElementValue(this.root, "jc");
		supply.setJc(s);
		s = getElementValue(this.root, "cym");
		supply.setCym(s);
		s = getElementValue(this.root, "bc");
		supply.setBc(s);
		s = getElementValue(this.root, "wzlb");
		supply.setWzlb(s);
		s = getElementValue(this.root, "rdcp");
		supply.setRdcp(s);
		s = getElementValue(this.root, "qyxz");
		supply.setQyxz(s);
		s = getElementValue(this.root, "lxr");
		supply.setLxr(s);
		s = getElementValue(this.root, "lxdh");
		supply.setLxdh(s);
		s = getElementValue(this.root, "cz");
		supply.setCz(s);
		s = getElementValue(this.root, "email");
		supply.setEmail(s);
		s = getElementValue(this.root, "address");
		supply.setAddress(s);
		s = getElementValue(this.root, "zipcode");
		supply.setZipcode(s);
		s = getElementValue(this.root, "classgrade");
		supply.setClassgrade(s);
		s = getElementValue(this.root, "state");
		supply.setState(s);
		s = getElementValue(this.root, "remark");
		supply.setRemark(s);

		supply = (GLSupply) PersistenceServerHelper.manager.store(supply, createStamp, modifyStamp);
		return supply;
	}

	@Override
	public WTArrayList importObjects() throws WTException {
		// TODO Auto-generated method stub
		return null;
	}

}