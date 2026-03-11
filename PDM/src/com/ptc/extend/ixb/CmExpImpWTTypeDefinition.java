package com.ptc.extend.ixb;

import java.util.HashSet;
import java.util.Iterator;

import wt.facade.ixb.IxbElement;
import wt.fc.collections.WTArrayList;
import wt.util.WTException;

// Referenced classes of package com.netflux.ixb:
//            CmExpImpObject, CmExporter, CmExpImpConstraints, CmImporter

public class CmExpImpWTTypeDefinition extends CmExpImpObject {

    public CmExpImpWTTypeDefinition(CmExporter expHdl) throws WTException {
        super(expHdl);
    }

    public CmExpImpWTTypeDefinition(CmImporter impHdl, String fname)
            throws WTException {
        super(impHdl, fname);
    }

    public void exportObject(Object obj) throws WTException {
        HashSet set = expHdl.getTypeDefinitionSet();
        String externalTypeId;
        IxbElement ixbelement;
        for (Iterator it = set.iterator(); it.hasNext(); ixbelement.addValue(
                "externalTypeId", externalTypeId)) {
            externalTypeId = (String) it.next();
            ixbelement = root.addElement("typeDef");
        }

        expHdl.storeDocumentInDir(ixbdocument, "configuration");
    }

    protected String getRootTag() {
        return CmExpImpConstraints.XML_WTTYPEDEFINITIONS;
    }

    public Object importObject() throws WTException {
        return null;
    }

    public WTArrayList importObjects() throws WTException {
        return null;
    }
}
