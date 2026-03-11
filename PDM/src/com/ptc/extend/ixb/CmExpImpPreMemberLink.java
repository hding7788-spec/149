package com.ptc.extend.ixb;

import cn.hutool.core.util.StrUtil;
import ext.casc.preview.PreMemberLink;
import ext.casc.preview.Preview;
import ext.casc.preview.PreviewObject;
import wt.facade.ixb.IxbElement;
import wt.fc.PersistenceHelper;
import wt.fc.collections.WTArrayList;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import java.util.Enumeration;

public class CmExpImpPreMemberLink extends CmExpImpLink {

    public CmExpImpPreMemberLink(Object obj, CmExporter expHdl)
            throws WTException {
        super(obj, expHdl);
    }

    public CmExpImpPreMemberLink(Object obj, CmImporter impHdl, String fname) throws WTException {
        super(obj, impHdl, fname);
    }

    public String getRootTag() {
        return CmExpImpConstraints.PREMEMBERLINK;
    }

    public void exportObject(Object obj) throws WTException {
    }

    public Object importObject() throws WTException {
        return importAttribute();
    }

    public WTArrayList importObjects() throws WTException {
        return importAttribute();
    }

    public WTArrayList importAttribute() throws WTException {
        	WTArrayList list = new WTArrayList();
            Preview preview = null;
            Enumeration records = getElements("DataRecord");
            while (records.hasMoreElements()) {
                IxbElement element = (IxbElement) records.nextElement();
                String number = getElementValue(element, "preview/number");
                if (number != null) {
                	if(preview==null)
                	preview = CmExpImpSearchHelper.getPreviewByNumber(number);
                } else {
                    logger.log("==>Import CmExpImpPreMemberLink Encounter Error,Can't Found Preview");
                    throw new WTException(
                            "==>Import CmExpImpPreMemberLink Encounter Error,Can't Found Preview");
                }
                if (preview != null) {
                	String objNumber = getElementValue(element, "member/number");
                	String objName = getElementValue(element, "member/name");
                	String objVersion = getElementValue(element, "member/version");
                	String objIteration = getElementValue(element, "member/iteration");
                	String objVer = objVersion+"."+objIteration;
                	String objType = getElementValue(element, "member/type");
                	String description = getElementValue(element, "member/description");
                	String designer = getElementValue(element, "member/designer");
                	String designCompany = getElementValue(element, "member/designCompany");
                	String modelMaturity = getElementValue(element, "member/maturity");
                    String maturityReason = getElementValue(element, "member/maturityChangeReason");
                    if(StrUtil.isNotEmpty(objNumber) && StrUtil.isNotEmpty(objName)
                        && StrUtil.isNotEmpty(objVer) && StrUtil.isNotEmpty(objType)){
                        PreviewObject previewObject = CmExpImpSearchHelper.searchPreviewObject(
                                PreviewObject.class, objNumber, objVer, objType);
                        if (previewObject==null) {
                            previewObject = PreviewObject.newPreviewObject();
                            try {
                                previewObject.setNumber(objNumber);
                                previewObject.setName(objName);
                                previewObject.setObjVer(objVer);
                                previewObject.setObjType(objType);
                                previewObject.setDescription(description);
                                previewObject.setDesigner(designer);
                                previewObject.setDescription(designCompany);
                                previewObject.setModelMaturity(modelMaturity);
                                previewObject.setMaturityReason(maturityReason);
                                previewObject = (PreviewObject)PersistenceHelper.manager.save(previewObject);
                            } catch (WTPropertyVetoException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        if(previewObject!=null){
                            PreMemberLink link = PreMemberLink.newPreMemberLink(preview, previewObject);
                            PersistenceHelper.manager.save(link);
                            list.add(link);
                        }
                    }
                }
            }
        return list;
    }

}