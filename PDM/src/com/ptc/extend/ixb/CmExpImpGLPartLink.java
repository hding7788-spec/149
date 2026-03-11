package com.ptc.extend.ixb;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import wt.facade.ixb.IxbElement;
import wt.fc.collections.WTArrayList;
import wt.util.WTException;
import ext.sast.catalog.GLCIPartLink;
import ext.sast.catalog.GLPartLink;

public class CmExpImpGLPartLink extends CmExpImpLink {

	public CmExpImpGLPartLink() throws WTException {
	}

	public CmExpImpGLPartLink(Object obj, CmExporter expHdl) throws WTException {
		super(obj, expHdl);
	}
	public CmExpImpGLPartLink(Object obj, CmImporter impHdl, String fname) throws WTException {
        super(obj, impHdl, fname);
   }
	public String getRootTag() {
		return CmExpImpConstraints.XML_GLCIPARTLINK;
	}

	public void exportObject(Object obj) throws WTException {
		if (!(obj instanceof ArrayList))
			throw new WTException("Object not ArrayList.");
		exportAttribute((ArrayList) obj);
	}

	private void exportAttribute(ArrayList list) throws WTException {
	}

	private void exportAttribute(GLPartLink link) throws WTException {
	}

	private void exportCIPartLinkAttribute(GLPartLink link, IxbElement ixbelement) throws WTException {
	}

	@Override
	public Object importObject() throws WTException {
		List objs = new ArrayList();
		Enumeration dataRecords = getElements("DataRecord");
		while (dataRecords.hasMoreElements()) {
			IxbElement data = (IxbElement) dataRecords.nextElement();
			String CIPARTNUMBER = getElementValue(data, "CIPARTNUMBER");
			String WTPARTNUMBER = getElementValue(data, "WTPARTNUMBER");
			GLPartLink link = CmExpImpSearchHelper.getGLPartLink(CIPARTNUMBER,WTPARTNUMBER);
			 if (link != null) {
			 }else{
				 link = new GLPartLink();
				 link.setCatalogNumber(CIPARTNUMBER);
				 link.setPartNumber(WTPARTNUMBER);
				 createNewObject(link);

			 }
			 objs.add(link);
		}

		return objs;
	}

	private void createNewObject(GLPartLink link) {
		CmExpImpCrudHelper.addGLPartLink(link);
	}

	@Override
	public WTArrayList importObjects() throws WTException {
		// TODO Auto-generated method stub
		return null;
	}

}