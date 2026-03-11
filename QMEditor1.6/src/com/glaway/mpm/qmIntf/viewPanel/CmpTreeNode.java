package com.glaway.mpm.qmIntf.viewPanel;

import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;

public class CmpTreeNode extends DefaultMutableTreeNode implements Serializable {

	private static final long serialVersionUID = 1L;
	private OlImage image;
	private boolean isSelected;
	private boolean isUsed = false;

	public boolean isUsed() {
		return isUsed;
	}

	public void setUsed(boolean isUsed) {
		this.isUsed = isUsed;
	}

	public CmpTreeNode(String name,String fullName,String oid) {
		super(name);
		this.image = new OlImage(name, fullName, oid);
	}

	public OlImage getImage() {
		return image;
	}

	public void setImage(OlImage image) {
		this.image = image;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
//	@Override
//	public boolean equals(Object o){
//		CmpTreeNode node = null;
//		if( o != null &&o instanceof CmpTreeNode){
//			node = (CmpTreeNode)o;
//		}else{
//			return false;
//		}
//			
//		try{
//			System.out.println("-------- " + this.getImage().getName() + "--------------" + node.getImage().getName());
//			if(this.getImage().getName().equals(node.getImage().getName())  //&& this.getImage().getOid().equals(node.getImage().getOid()) 
////					&& this.getImage().getFullName().equals(node.getImage().getFullName())
//					){
//				return true;
//			}else{
//				return false;
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//			return false;
//		}
//	}
	

}
