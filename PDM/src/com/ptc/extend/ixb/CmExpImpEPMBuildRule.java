// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 2012/5/24 10:35:27
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   CmExpImpEPMBuildRule.java

package com.ptc.extend.ixb;

import com.ptc.extend.ixb.center.MQExpImpConstants;
import com.ptc.extend.util.ObjectProperty;
import java.rmi.RemoteException;
import java.util.*;

import ext.sast.center.synch.MQExpImpUtil;
import wt.epm.EPMDocument;
import wt.epm.build.EPMBuildAttribute;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMStandardStructureService;
import wt.fc.*;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTHashSet;
import wt.iba.definition.AbstractAttributeDefinition;
import wt.iba.definition.AttributeDefinitionReference;
import wt.iba.definition.service.*;
import wt.ixb.handlers.forattributes.ExpImpForIBAAttr;
import wt.facade.ixb.IxbElement;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

// Referenced classes of package com.netflux.ixb:
//            CmExpImpLink, CmExpImpConstraints, CmExporter, CmImporter,
//            CmExpImpSearchHelper, MissingObjectException

public class CmExpImpEPMBuildRule extends CmExpImpLink
{



	public CmExpImpEPMBuildRule(Object obj, CmExporter expHdl)
			throws WTException {
		super(obj, expHdl);
	}

	public CmExpImpEPMBuildRule(Object obj, CmImporter impHdl, String fname)
			throws WTException {
		super(obj, impHdl, fname);
	}

	public String getRootTag() {
		return CmExpImpConstraints.XML_EPMBUILDRULE;
	}

	public void exportObject(Object obj) throws WTException {
		if (!(obj instanceof ArrayList)) {
			throw new WTException("Object not ArrayList.");
		} else {
			exportAttribute((ArrayList) obj);
			return;
		}
	}

	private void exportAttribute(ArrayList list) throws WTException {
		EPMBuildRule link;
		for (Iterator it = list.iterator(); it.hasNext(); exportAttribute(link))
			link = (EPMBuildRule) it.next();

		expHdl.storeDocumentInDir(ixbdocument, getSavePathInJar());
	}

    private void exportAttribute(EPMBuildRule obj) throws WTException {
        IxbElement ixbelement = this.root.addElement("DataRecord");
        EPMBuildRule epmbuildrule = obj;
        exportLocalIdAttribute(epmbuildrule, ixbelement);
        exportTypeDefinitionAttribute(epmbuildrule, ixbelement);
        EPMDocument epm = (EPMDocument)epmbuildrule.getBuildSource();
        ixbelement.addValue("buildSource/number", emptyIfNull(ObjectProperty.getNumber(epm)));
        ixbelement.addValue("buildSource/version", emptyIfNull(ObjectProperty.getVersion(epm)));
        ixbelement.addValue("buildSource/iteration", emptyIfNull(ObjectProperty.getIteration(epm)));
        WTPart part = (WTPart)epmbuildrule.getBuildTarget();
        ixbelement.addValue("buildTarget/number", emptyIfNull(ObjectProperty.getNumber(part)));
        ixbelement.addValue("buildTarget/version", emptyIfNull(ObjectProperty.getVersion(part)));
        ixbelement.addValue("buildTarget/iteration", emptyIfNull(ObjectProperty.getIteration(part)));
        ixbelement.addValue("buildType", epmbuildrule.getBuildType());
        ixbelement.addValue("uniqueId", obj.getUniqueID());

        QueryResult queryresult = null;
        if ((epmbuildrule.getAttributes(1) == 3) ||
          (epmbuildrule.getAttributes(2) == 3) ||
          (epmbuildrule.getAttributes(4) == 3) ||
          (epmbuildrule.getAttributes(3) == 3)) {
          WTHashSet wthashset = new WTHashSet();
          wthashset.add(epmbuildrule);
          QuerySpec queryspec = new QuerySpec(EPMBuildAttribute.class);
          queryspec.appendWhere(new SearchCondition(EPMBuildAttribute.class, "ruleReference.key.id", wthashset.toArray(new long[wthashset.size()])), new int[1]);
          queryresult = PersistenceHelper.manager.find(queryspec);
        }
        if (epmbuildrule.getAttributes(1) == 3) {
          queryresult.reset();
          IxbElement ixbelement1 = ixbelement.addElement("userDefinedAttributesOnTarget");

          while (queryresult.hasMoreElements())
          {
            EPMBuildAttribute epmbuildattribute = (EPMBuildAttribute)queryresult.nextElement();
            if (epmbuildattribute.getPublishedOn() == 1) {
              AbstractAttributeDefinition abstractattributedefinition = ExpImpForIBAAttr.getIBADefOfHierarchyID(epmbuildattribute.getDefinition().getHierarchyID());
              String s4 = ExpImpForIBAAttr.getPathOfAttributeDefinition_WithoutOrganizer(abstractattributedefinition);
              ixbelement1.addValue("ibaPath", emptyIfNull(s4));
            }
          }
        }
        ixbelement.addValue("attributesOnTarget", epmbuildrule.getAttributes(1));

        if (epmbuildrule.getAttributes(2) == 3) {
          queryresult.reset();
          IxbElement ixbelement2 = ixbelement.addElement("userDefinedAttributesOnLink");

          while (queryresult.hasMoreElements())
          {
            EPMBuildAttribute epmbuildattribute1 = (EPMBuildAttribute)queryresult.nextElement();
            if (epmbuildattribute1.getPublishedOn() == 2) {
              AbstractAttributeDefinition abstractattributedefinition1 = ExpImpForIBAAttr.getIBADefOfHierarchyID(epmbuildattribute1.getDefinition().getHierarchyID());
              String s5 = ExpImpForIBAAttr.getPathOfAttributeDefinition_WithoutOrganizer(abstractattributedefinition1);
              ixbelement2.addValue("ibaPath", emptyIfNull(s5));
            }
          }
        }
        ixbelement.addValue("attributesOnLink", epmbuildrule.getAttributes(2));

        if (epmbuildrule.getAttributes(3) == 3) {
          queryresult.reset();
          IxbElement ixbelement3 = ixbelement.addElement("userDefinedAttributesOnOccurrence");

          while (queryresult.hasMoreElements())
          {
            EPMBuildAttribute epmbuildattribute2 = (EPMBuildAttribute)queryresult.nextElement();
            if (epmbuildattribute2.getPublishedOn() == 3) {
              AbstractAttributeDefinition abstractattributedefinition2 = ExpImpForIBAAttr.getIBADefOfHierarchyID(epmbuildattribute2.getDefinition().getHierarchyID());
              String s6 = ExpImpForIBAAttr.getPathOfAttributeDefinition_WithoutOrganizer(abstractattributedefinition2);
              ixbelement3.addValue("ibaPath", emptyIfNull(s6));
            }
          }
        }
        ixbelement.addValue("attributesOnOccurrence", epmbuildrule.getAttributes(3));

        if (epmbuildrule.getAttributes(4) == 3) {
          queryresult.reset();
          IxbElement ixbelement4 = ixbelement.addElement("userDefinedAttributesOnMaster");

          while (queryresult.hasMoreElements())
          {
            EPMBuildAttribute epmbuildattribute3 = (EPMBuildAttribute)queryresult.nextElement();
            if (epmbuildattribute3.getPublishedOn() == 4) {
              AbstractAttributeDefinition abstractattributedefinition3 = ExpImpForIBAAttr.getIBADefOfHierarchyID(epmbuildattribute3.getDefinition().getHierarchyID());
              String s7 = ExpImpForIBAAttr.getPathOfAttributeDefinition_WithoutOrganizer(abstractattributedefinition3);
              ixbelement4.addValue("ibaPath", emptyIfNull(s7));
            }
          }
        }
        ixbelement.addValue("attributesOnMaster", epmbuildrule.getAttributes(4));
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
            String targetNumber = getNoTrimElementValue(data, "buildTarget/number");
            if (this.impHdl.isLoopTest())
              targetNumber = this.impHdl.getLoopTestPrefix() + targetNumber;
            String targetVersion = getElementValue(data, "buildTarget/version");
            if(!"zyk".equals(getSendFrom())){
                targetVersion = MQExpImpUtil.attrConvertValueBySendFrom("versionInfo", targetVersion, "IMP",getSendFrom());

            }

            String targetIteration = getElementValue(data, "buildTarget/iteration");
            WTPart targetPart = (WTPart)CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(WTPart.class, targetNumber, targetVersion, targetIteration);

            String sourceNumber = getNoTrimElementValue(data, "buildSource/number");
            if (this.impHdl.isLoopTest())
              sourceNumber = this.impHdl.getLoopTestPrefix() + sourceNumber;
            String sourceVersion = getElementValue(data, "buildSource/version");
            if(!"zyk".equals(getSendFrom())) {
                sourceVersion = MQExpImpUtil.attrConvertValueBySendFrom("versionInfo", sourceVersion, "IMP",getSendFrom());
            }
            String sourceIteration = getElementValue(data, "buildSource/iteration");
            EPMDocument sourceEPM = (EPMDocument)CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(EPMDocument.class, sourceNumber, sourceVersion, sourceIteration);
            logger.log("==>Import EPMBuildRule:target:number=<" + targetNumber +
              "> Ver=" + targetVersion + "." + targetIteration + " to source:number=<" +
              sourceNumber + "> Ver=" + sourceVersion + "." + sourceIteration);
            if (!isTypeDefinitionImported()) {
              logger.log("==>TypeDefinition not imported, SKIP!");
            }
            else {
              if (targetPart == null) {
                logger.log("==>Missing WTPart:" + targetNumber + " " + targetVersion + "." + targetIteration);
                this.impHdl.putInMissingObjectSet("WTPart", targetNumber, targetVersion, targetIteration);
              }
              if (sourceEPM == null) {
                logger.log("==>Missing EPMDocument:" + sourceNumber + " " + sourceVersion + "." + sourceIteration);
                this.impHdl.putInMissingObjectSet("EPMDocument", sourceNumber, sourceVersion, sourceIteration);
              }
              if ((targetPart == null) || (sourceEPM == null)) {
                missingobj = true;
              } else {
                EPMBuildRule link = CmExpImpSearchHelper.searchEPMBuildRule(sourceEPM, targetPart);
                if (link == null) {
                  link = createLink(sourceEPM, targetPart, data);
                  if (link != null) {
                    list.add(link);
                    logger.log("==>EPMBuildRule import OK!");
                  } else {
                	 logger.log("==>EPMBuildRule import ERROR!");
                    dofailed = true;
                  }
                } else {
                  logger.log("==>EPMBuildRule already imported, IGNORE");
                }
              }
            }
          }
          if (missingobj)
            throw new MissingObjectException("==>Missing objects when create EPMBuildRule.");
          if (dofailed)
            throw new WTException("==>Not All EPMBuildRule Imported, need to rearrange for deliver.");
        }
        catch (Exception e)
        {
          logger.log(e.getMessage());
          if ((e instanceof WTException)) throw ((WTException)e);
          throw new WTException(e);
        }
        return list;
      }

    private EPMBuildRule createLink(EPMDocument epm, WTPart part, IxbElement ixbelement) throws WTException {
        Transaction tx = new Transaction();
        try {
          tx.start();
          String  buildType = ixbelement.getValue("buildType");

          buildType = getBuildTypeValue(buildType,"buildType");

          EPMBuildRule epmbuildrule = EPMBuildRule.newEPMBuildRule(epm, part, Integer.parseInt(buildType));
          epmbuildrule = (EPMBuildRule)importTypeDefinitionAttribute(epmbuildrule, ixbelement, ixbelement);
          if (getElementValue(ixbelement, "attributesOnTarget") != null)
            epmbuildrule.setAttributes(1, ixbelement.getIntValue("attributesOnTarget").intValue());
          else
            epmbuildrule.setAttributes(1, 3);
          if (getElementValue(ixbelement, "attributesOnLink") != null)
            epmbuildrule.setAttributes(2, ixbelement.getIntValue("attributesOnLink").intValue());
          else
            epmbuildrule.setAttributes(2, 3);
          if (getElementValue(ixbelement, "attributesOnOccurrence") != null)
            epmbuildrule.setAttributes(3, ixbelement.getIntValue("attributesOnOccurrence").intValue());
          else
            epmbuildrule.setAttributes(3, 3);
          if (getElementValue(ixbelement, "attributesOnMaster") != null)
            epmbuildrule.setAttributes(4, ixbelement.getIntValue("attributesOnMaster").intValue());
          else
            epmbuildrule.setAttributes(4, 3);
          epmbuildrule.setUniqueID(EPMStandardStructureService.getNextEPMLinkSequence());
          PersistenceServerHelper.manager.insert(epmbuildrule);
          tx.commit();
          tx = null;

          epmbuildrule = importObjectAttributesAfterStore(epmbuildrule, ixbelement);
          EPMBuildRule localEPMBuildRule1 = epmbuildrule;
          return localEPMBuildRule1;
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          if (tx != null)
            tx.rollback();
        }
        return null;
      }

    private EPMBuildRule importObjectAttributesAfterStore(EPMBuildRule epmbuildrule, IxbElement ixbelement)
        throws WTException
    {
        WTHashSet wthashset = new WTHashSet();
        try
        {
            epmbuildrule = (EPMBuildRule)PersistenceHelper.manager.refresh(epmbuildrule);
            if(ixbelement.getElement("userDefinedAttributesOnTarget") != null)
            {
                IxbElement ixbelement1 = ixbelement.getElement("userDefinedAttributesOnTarget");
                EPMBuildAttribute epmbuildattribute;
                for(Enumeration enumeration = ixbelement1.getElements("ibaPath"); enumeration.hasMoreElements(); wthashset.add(epmbuildattribute))
                {
                    IxbElement ixbelement5 = (IxbElement)enumeration.nextElement();
                    String s = ixbelement5.getValue();
                    wt.iba.definition.litedefinition.AttributeDefDefaultView attributedefdefaultview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(s);
                    AttributeDefinitionReference attributedefinitionreference = IBADefinitionObjectsFactory.newAttributeDefinitionReference(attributedefdefaultview);
                    epmbuildattribute = EPMBuildAttribute.newEPMBuildAttribute(epmbuildrule, attributedefinitionreference, 1);
                }

            }
            if(ixbelement.getElement("userDefinedAttributesOnLink") != null)
            {
                IxbElement ixbelement2 = ixbelement.getElement("userDefinedAttributesOnLink");
                EPMBuildAttribute epmbuildattribute1;
                for(Enumeration enumeration1 = ixbelement2.getElements("ibaPath"); enumeration1.hasMoreElements(); wthashset.add(epmbuildattribute1))
                {
                    IxbElement ixbelement6 = (IxbElement)enumeration1.nextElement();
                    String s1 = ixbelement6.getValue();
                    wt.iba.definition.litedefinition.AttributeDefDefaultView attributedefdefaultview1 = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(s1);
                    AttributeDefinitionReference attributedefinitionreference1 = IBADefinitionObjectsFactory.newAttributeDefinitionReference(attributedefdefaultview1);
                    epmbuildattribute1 = EPMBuildAttribute.newEPMBuildAttribute(epmbuildrule, attributedefinitionreference1, 2);
                }

            }
            if(ixbelement.getElement("userDefinedAttributesOnMaster") != null)
            {
                IxbElement ixbelement3 = ixbelement.getElement("userDefinedAttributesOnMaster");
                EPMBuildAttribute epmbuildattribute2;
                for(Enumeration enumeration2 = ixbelement3.getElements("ibaPath"); enumeration2.hasMoreElements(); wthashset.add(epmbuildattribute2))
                {
                    IxbElement ixbelement7 = (IxbElement)enumeration2.nextElement();
                    String s2 = ixbelement7.getValue();
                    wt.iba.definition.litedefinition.AttributeDefDefaultView attributedefdefaultview2 = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(s2);
                    AttributeDefinitionReference attributedefinitionreference2 = IBADefinitionObjectsFactory.newAttributeDefinitionReference(attributedefdefaultview2);
                    epmbuildattribute2 = EPMBuildAttribute.newEPMBuildAttribute(epmbuildrule, attributedefinitionreference2, 4);
                }

            }
            if(ixbelement.getElement("userDefinedAttributesOnOccurrence") != null)
            {
                IxbElement ixbelement4 = ixbelement.getElement("userDefinedAttributesOnOccurrence");
                EPMBuildAttribute epmbuildattribute3;
                for(Enumeration enumeration3 = ixbelement4.getElements("ibaPath"); enumeration3.hasMoreElements(); wthashset.add(epmbuildattribute3))
                {
                    IxbElement ixbelement8 = (IxbElement)enumeration3.nextElement();
                    String s3 = ixbelement8.getValue();
                    wt.iba.definition.litedefinition.AttributeDefDefaultView attributedefdefaultview3 = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(s3);
                    AttributeDefinitionReference attributedefinitionreference3 = IBADefinitionObjectsFactory.newAttributeDefinitionReference(attributedefdefaultview3);
                    epmbuildattribute3 = EPMBuildAttribute.newEPMBuildAttribute(epmbuildrule, attributedefinitionreference3, 3);
                }

            }
            if(wthashset.size() > 0)
                PersistenceHelper.manager.save(wthashset);
        }
        catch(WTPropertyVetoException wtpropertyvetoexception)
        {
            throw new WTException(wtpropertyvetoexception);
        }
        catch(RemoteException remoteexception)
        {
            throw new WTException(remoteexception);
        }
        return epmbuildrule;
    }
}
