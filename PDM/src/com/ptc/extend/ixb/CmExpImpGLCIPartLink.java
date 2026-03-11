package com.ptc.extend.ixb;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import wt.facade.ixb.IxbElement;
import wt.fc.collections.WTArrayList;
import wt.util.WTException;
import ext.sast.catalog.GLCIPartLink;

public class CmExpImpGLCIPartLink extends CmExpImpLink {

	public CmExpImpGLCIPartLink() throws WTException {
	}

	public CmExpImpGLCIPartLink(Object obj, CmExporter expHdl) throws WTException {
		super(obj, expHdl);
	}
	public CmExpImpGLCIPartLink(Object obj, CmImporter impHdl, String fname) throws WTException {
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
		Iterator it = list.iterator();
		while (it.hasNext()) {
			GLCIPartLink link = (GLCIPartLink) it.next();
			exportAttribute(link);
		}
		expHdl.storeDocumentInDir(ixbdocument, getSavePathInJar());
	}

	private void exportAttribute(GLCIPartLink link) throws WTException {
		exportCIPartLinkAttribute(link, this.root);
	}

	private void exportCIPartLinkAttribute(GLCIPartLink link, IxbElement ixbelement) throws WTException {
		ixbelement.addValue("CIPARTNUMBER", link.getCatalogItemNumber());
		ixbelement.addValue("WTPARTNUMBER", link.getPartNumber());
	}

	@Override
	public Object importObject() throws WTException {
		List objs = new ArrayList();
		Enumeration dataRecords = getElements("DataRecord");
		while (dataRecords.hasMoreElements()) {
			IxbElement data = (IxbElement) dataRecords.nextElement();
			String CIPARTNUMBER = getElementValue(data, "CIPARTNUMBER");
			String WTPARTNUMBER = getElementValue(data, "WTPARTNUMBER");
			GLCIPartLink link = CmExpImpSearchHelper.getGLCIPartLink(CIPARTNUMBER,WTPARTNUMBER);
			 if (link != null) {
			 }else{
				 link = new GLCIPartLink();
				 link.setCatalogItemNumber(CIPARTNUMBER);
				 link.setPartNumber(WTPARTNUMBER);
				 createNewObject(link);

			 }
			 objs.add(link);
		}

		return objs;
	}

	private void createNewObject(GLCIPartLink link) {
		CmExpImpCrudHelper.addGLCIPartLink(link);
	}

	@Override
	public WTArrayList importObjects() throws WTException {
		// TODO Auto-generated method stub
		return null;
	}

}