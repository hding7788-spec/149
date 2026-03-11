package com.ptc.extend.ixb;

import java.util.HashSet;
import java.util.Iterator;

import wt.facade.ixb.IxbElement;
import wt.fc.collections.WTArrayList;
import wt.util.WTException;

// Referenced classes of package com.netflux.ixb:
//            CmExpImpObject, CmExporter, CmExpImpConstraints, CmImporter

public class CmExpImpIBADefinition extends CmExpImpObject {

    public CmExpImpIBADefinition(CmExporter expHdl) throws WTException {
        super(expHdl);
    }

    public CmExpImpIBADefinition(CmImporter impHdl, String fname)
            throws WTException {
        super(impHdl, fname);
    }

    public void exportObject(Object obj) throws WTException {
        HashSet set = expHdl.getIBADefinitionSet();
        String ibaPath;
        IxbElement ixbelement;
        for (Iterator it = set.iterator(); it.hasNext(); ixbelement.addValue(
                "ibaPath", ibaPath)) {
            ibaPath = (String) it.next();
            ixbelement = root.addElement("ibaDef");
        }

        expHdl.storeDocumentInDir(ixbdocument, "configuration");
    }

    protected String getRootTag() {
        return CmExpImpConstraints.XML_IBADEFINITIONS;
    }

    public Object importObject() throws WTException {
        return null;
    }

    public WTArrayList importObjects() throws WTException {
        return null;
    }
}
