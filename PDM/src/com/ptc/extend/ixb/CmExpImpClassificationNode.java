package com.ptc.extend.ixb;

import wt.facade.ixb.IxbElement;
import wt.fc.collections.WTArrayList;
import wt.util.WTException;
import ext.sast.navigation.GLClassificationNode;

public class CmExpImpClassificationNode extends CmExpImpPersistable
{
  private static final String TOPNODEID = "0";

private CmExpImpClassificationNode()
  {
  }

  public CmExpImpClassificationNode(CmExporter expHdl)
    throws WTException
  {
    super(expHdl);
  }
  public CmExpImpClassificationNode(CmImporter impHdl, String fname) throws WTException {
    super(impHdl, fname);
  }
  public String getRootTag() {
    return CmExpImpConstraints.XML_CLASSIFICATIONNODE;
  }

  public void exportObject(Object obj) throws WTException {
  }

  private void exportAttribute(GLClassificationNode node) throws WTException {
  }

private void exportClassificationNodeAttribute(GLClassificationNode node,
		IxbElement ixbelement) throws WTException {
}

@Override
public Object importObject() throws WTException {
	Object obj = importAttribute();
    if (obj != null)
        this.impHdl.pubImportedObject(obj, getRemoteId());
    return obj;
}

private Object importAttribute() {
	GLClassificationNode node = CmExpImpSearchHelper.getGLClassificationNodeByNumber(this.number);
	 if (node != null) {
		 return node;
	 }else{
		 try {
			node = createNewObject();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	return node;
}

	private GLClassificationNode createNewObject() throws WTException {
		GLClassificationNode node = new GLClassificationNode();
		String id = getElementValue(this.root, "id");
		node.setId(Long.parseLong(id));
		String parentId = getElementValue(this.root, "parentId");
		node.setParentId(Long.parseLong(parentId));
		String number = getElementValue(this.root, "number");
		node.setNumber(number);
		CmExpImpCrudHelper.addClassificationNode(node);
		return node;
	}

	@Override
	public WTArrayList importObjects() throws WTException {
		// TODO Auto-generated method stub
		return null;
	}

}