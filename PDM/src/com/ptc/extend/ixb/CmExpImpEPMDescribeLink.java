package com.ptc.extend.ixb;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import wt.epm.EPMDocument;
import wt.epm.structure.EPMDescribeLink;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.collections.WTArrayList;
import wt.facade.ixb.IxbElement;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.util.WTException;
import wt.vc.Iterated;

import com.ptc.extend.util.ObjectProperty;

public class CmExpImpEPMDescribeLink extends CmExpImpLink {
	private CmExpImpEPMDescribeLink() {
	}

	public CmExpImpEPMDescribeLink(Object obj, CmExporter expHdl)
			throws WTException {
		super(obj, expHdl);
	}

	public CmExpImpEPMDescribeLink(Object obj, CmImporter impHdl, String fname)
			throws WTException {
		super(obj, impHdl, fname);
	}

	public String getRootTag() {
		return CmExpImpConstraints.XML_EPMDESCRIBELINK;
	}

	public void exportObject(Object obj) throws WTException {
		if (!(obj instanceof ArrayList))
			throw new WTException("Object not ArrayList.");
		exportAttribute((ArrayList) obj);
	}

  private void exportAttribute(ArrayList list) throws WTException {
    Iterator it = list.iterator();
    while (it.hasNext()) {
      EPMDescribeLink link = (EPMDescribeLink)it.next();
      exportAttribute(link);
    }
    this.expHdl.storeDocumentInDir(this.ixbdocument, getSavePathInJar());
  }

  private void exportAttribute(EPMDescribeLink link) throws WTException {
    IxbElement ixbelement = addElement("DataRecord");
    exportLocalIdAttribute(link, ixbelement);
    exportTypeDefinitionAttribute(link, ixbelement);
    Iterated describedby = link.getDescribedBy();
    ixbelement.addValue("describedBy/number", emptyIfNull(ObjectProperty.getNumber(describedby)));
    ixbelement.addValue("describedBy/version", emptyIfNull(ObjectProperty.getVersion(describedby)));
    ixbelement.addValue("describedBy/iteration", emptyIfNull(ObjectProperty.getIteration(describedby)));
    Iterated describes = link.getDescribes();
    ixbelement.addValue("describes/number", emptyIfNull(ObjectProperty.getNumber(describes)));
    ixbelement.addValue("describes/version", emptyIfNull(ObjectProperty.getVersion(describes)));
    ixbelement.addValue("describes/iteration", emptyIfNull(ObjectProperty.getIteration(describes)));
    ixbelement.addValue("isBuiltFlag", link.isBuiltFlag());
  }

  public Object importObject() throws WTException {
    return importAttribute();
  }

  public WTArrayList importObjects() throws WTException {
    return importAttribute();
  }

  public WTArrayList importAttribute() throws WTException {
    WTArrayList list = new WTArrayList();
    try {
      boolean missingobj = false;
      boolean dofailed = false;
      Enumeration dataRecords = getElements("DataRecord");
      while (dataRecords.hasMoreElements()) {
        IxbElement data = (IxbElement)dataRecords.nextElement();
        String describedByNumber = getNoTrimElementValue(data, "describedBy/number");
        if (this.impHdl.isLoopTest())
          describedByNumber = this.impHdl.getLoopTestPrefix() + describedByNumber;
        String describedByVersion = getElementValue(data, "describedBy/version");
        describedByVersion = getPropertiesValue(describedByVersion);
        String describedByIteration = getElementValue(data, "describedBy/iteration");
        EPMDocument doc = (EPMDocument)CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(EPMDocument.class, describedByNumber, describedByVersion, describedByIteration);
        String describesNumber = getNoTrimElementValue(data, "describes/number");
        if (this.impHdl.isLoopTest())
          describesNumber = this.impHdl.getLoopTestPrefix() + describesNumber;
        String describesVersion = getElementValue(data, "describes/version");
        describesVersion = getPropertiesValue(describesVersion);
        String describesIteration = getElementValue(data, "describes/iteration");
        WTPart part = (WTPart)CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(WTPart.class, describesNumber, describesVersion, describesIteration);
        logger.log("==>Import EPMBuildDescribeLink:describedBy:number=<" + describedByNumber + 
          "> Ver=" + describedByVersion + "." + describedByIteration + " to describes:number=<" + 
          describesNumber + ">" + describesVersion + "." + describesIteration);
        if (doc == null) {
          logger.log("==>Missing EPMDocument:" + describedByNumber + " " + describedByVersion + "." + describedByIteration);
          this.impHdl.putInMissingObjectSet("EPMDocument", describedByNumber, describedByVersion, describedByIteration);
        }
        if (part == null) {
          logger.log("==>Missing WTPart:" + describesNumber + " " + describesVersion + "." + describesIteration);
          this.impHdl.putInMissingObjectSet("WTPart", describesNumber, describesVersion, describesIteration);
        }
        if ((doc == null) || (part == null)) {
          missingobj = true;
        } else {
          EPMDescribeLink link = CmExpImpSearchHelper.searchEPMDescribeLink(part, doc);
          if (link == null) {
            link = createLink(part, doc, data);
            if (link != null) {
              list.add(link);
              logger.log("==>EPMDescribeLink import OK!");
            } else {
              dofailed = true;
            }
          } else {
            logger.log("==>EPMDescribeLink already imported, IGNORE!");
          }
        }
      }
      if (missingobj)
        throw new MissingObjectException("Missing objects");
      if (dofailed)
        throw new WTException("Not All Link Imported, need to rearrange.");
    } catch (Exception e) {
      logger.log(e.getMessage());
      if ((e instanceof WTException)) throw ((WTException)e);
      throw new WTException(e);
    }
    return list;
  }

  private EPMDescribeLink createLink(WTPart part, EPMDocument doc, IxbElement ixbelement) {
    Transaction tx = new Transaction();
    try {
      tx.start();
      EPMDescribeLink link = EPMDescribeLink.newEPMDescribeLink(part, doc);
      link = (EPMDescribeLink)importTypeDefinitionAttribute(link, ixbelement, ixbelement);
      Boolean boolean1 = ixbelement.getBooleanValue("isBuiltFlag");
      if (boolean1 != null)
        link.setBuiltFlag(boolean1.booleanValue());
      PersistenceServerHelper.manager.insert(link);
      tx.commit();
      tx = null;
      EPMDescribeLink localEPMDescribeLink1 = (EPMDescribeLink)PersistenceHelper.manager.refresh(link);
      return localEPMDescribeLink1;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (tx != null)
        tx.rollback();
    }
    return null;
  }
}