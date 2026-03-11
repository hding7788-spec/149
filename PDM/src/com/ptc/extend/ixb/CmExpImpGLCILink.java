package com.ptc.extend.ixb;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import wt.facade.ixb.IxbElement;
import wt.fc.collections.WTArrayList;
import wt.util.WTException;
import ext.sast.catalog.GLCILink;

public class CmExpImpGLCILink extends CmExpImpLink {

	public CmExpImpGLCILink() throws WTException {
	}

	public CmExpImpGLCILink(Object obj, CmExporter expHdl) throws WTException {
		super(obj, expHdl);
	}
	 public CmExpImpGLCILink(Object obj, CmImporter impHdl, String fname) throws WTException {
	        super(obj, impHdl, fname);
	   }
	public String getRootTag() {
		return CmExpImpConstraints.XML_GLCILINK;
	}

	public void exportObject(Object obj) throws WTException {
		if (!(obj instanceof ArrayList))
			throw new WTException("Object not ArrayList.");
		exportAttribute((ArrayList) obj);
	}

	private void exportAttribute(ArrayList list) throws WTException {
		Iterator it = list.iterator();
		while (it.hasNext()) {
			GLCILink link = (GLCILink) it.next();
			exportAttribute(link);
		}
		expHdl.storeDocumentInDir(ixbdocument, getSavePathInJar());
	}

	private void exportAttribute(GLCILink link) throws WTException {
		exportCILinkAttribute(link, this.root);
	}

	private void exportCILinkAttribute(GLCILink link, IxbElement ixbelement) throws WTException {
		ixbelement.addValue("GLCATALOGNAME", link.getCatalogName());
		ixbelement.addValue("CIPARTNUMBER", link.getPartNumber());
		ixbelement.addValue("GLSUPPLIERS", link.getSuppliers());
	}

	@Override
	public Object importObject() throws WTException {
		List objs = new ArrayList();
		Enumeration dataRecords = getElements("DataRecord");
		while (dataRecords.hasMoreElements()) {
			  IxbElement data = (IxbElement) dataRecords.nextElement();
			String GLCATALOGNAME = getElementValue(data, "GLCATALOGNAME");
			String GLCATALOGNUMBER = getElementValue(data, "GLCATALOGNUMBER");
			String CIPARTNUMBER = getElementValue(data, "CIPARTNUMBER");
			String GLSUPPLIERS = getElementValue(data, "GLSUPPLIERS");
			GLCILink link = CmExpImpSearchHelper.getGLCILink(GLCATALOGNUMBER,CIPARTNUMBER,GLSUPPLIERS,GLCATALOGNAME);
			 if (link != null) {
				 return link;
			 }else{
				 link = new GLCILink();
				 link.setCatalogName(GLCATALOGNAME);
				 link.setPartNumber(CIPARTNUMBER);
				 link.setSuppliers(GLSUPPLIERS);
				 link.setCatalogNumber(GLCATALOGNUMBER);
				 createNewObject(link);
			 }
			 objs.add(link);
		}

		return objs;
	}

	private GLCILink createNewObject(GLCILink link) {
		CmExpImpCrudHelper.addGLCILink(link);
		return null;
	}

	@Override
	public WTArrayList importObjects() throws WTException {
		// TODO Auto-generated method stub
		return null;
	}

}