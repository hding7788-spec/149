package com.ptc.extend.ixb;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;

import wt.facade.ixb.IxbElement;
import wt.fc.PersistenceServerHelper;
import wt.fc.collections.WTArrayList;
import wt.inf.container.WTContainerRef;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.ptc.extend.util.ObjectProperty;

import ext.ases.changepackaged.ChangePackaged;
import ext.ases.envelope.ProcessEnvelope;
import ext.sast.catalog.GLCatalog;
import ext.sast.navigation.GLClassificationNode;
import ext.sast.supply.GLSupply;

public class CmExpImpGLCatalog extends CmExpImpPersistable {

	public CmExpImpGLCatalog() throws WTException {
	}

	public CmExpImpGLCatalog(CmExporter expHdl) throws WTException {
		super(expHdl);
	}

	public CmExpImpGLCatalog(CmImporter impHdl, String fname) throws WTException {
		 super(impHdl, fname);
	}

	public String getRootTag() {
		return CmExpImpConstraints.XML_GLCATALOG;
	}

	public void exportObject(Object obj) throws WTException {
		if (!(obj instanceof GLCatalog))
			throw new WTException("Object not GLCatalog.");
		GLCatalog catalog = (GLCatalog) obj;
		logger("==>Export GLCatalog:" + catalog.getCatalogname());
		exportAttribute(catalog);
		this.expHdl.addExportedObject(catalog);
	}

	private void exportAttribute(GLCatalog catalog) throws WTException {
		exportCatalogAttribute(catalog, this.root);
		reallyStore();
	}

	private void exportCatalogAttribute(GLCatalog catalog, IxbElement ixbelement)
			throws WTException {
		ixbelement.addValue("catalogname", catalog.getCatalogname());
		ixbelement.addValue("bzh", catalog.getBzh());
		ixbelement.addValue("catalogtype", catalog.getCatalogtype());
		ixbelement.addValue("grade", catalog.getGrade());
		ixbelement.addValue("scope", catalog.getScope());
		ixbelement.addValue("state", catalog.getState());
		ixbelement.addValue("createunit", catalog.getCreateunit());
		ixbelement.addValue("creator", catalog.getCreator());
		ixbelement.addValue("modifier", catalog.getModifier());
		ixbelement.addValue("remark", catalog.getRemark());
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
			GLCatalog catalog = (GLCatalog) CmExpImpSearchHelper
					.getGLCatalogByNumber(this.number);
			if (catalog != null) {
				this.impHdl.putInExistedHashtable(getRemoteId(), catalog);
				return catalog;
			} else {
				logger.log("==>Create new Object: ProcessEnvelope number=<"
						+ this.number + ">");
				catalog = createNewObject();
			}
			if (catalog != null) {
				this.impHdl.putInNewCreatedHashtable(getRemoteId(), catalog);
			}

			return catalog;
		} catch (Exception exception) {
			logger.log("Exception in ProcessEnvelope, fname=<" + getPfilename()
					+ ">");
			processException(exception);
		}
		return null;
	}

	private GLCatalog createNewObject() throws WTException, WTPropertyVetoException {
		GLCatalog catalog =  GLCatalog.newGLCatalog();
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
		String s = getElementValue(this.root, "catalogname");
		catalog.setCatalogname(s);
		s = getElementValue(this.root, "bzh");
		catalog.setBzh(s);
		s = getElementValue(this.root, "catalogtype");
		catalog.setCatalogtype(s);
		s = getElementValue(this.root, "grade");
		catalog.setGrade(s);
		s = getElementValue(this.root, "scope");
		catalog.setScope(s);
		s = getElementValue(this.root, "state");
		catalog.setState(s);
		s = getElementValue(this.root, "createunit");
		catalog.setCreateunit(s);
		s = getElementValue(this.root, "creator");
		catalog.setCreator(s);
		s = getElementValue(this.root, "modifier");
		catalog.setModifier(s);
		s = getElementValue(this.root, "remark");
		catalog.setRemark(s);

		catalog = (GLCatalog) PersistenceServerHelper.manager.store(catalog, createStamp, modifyStamp);
		return catalog;
	}

	@Override
	public WTArrayList importObjects() throws WTException {
		// TODO Auto-generated method stub
		return null;
	}

}