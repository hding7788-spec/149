package com.glaway.mpm.view;

import java.awt.Image;
import java.util.Vector;

import javax.swing.ImageIcon;

import org.dom4j.Element;

public class GongZhuangSQDObject  implements XWTreeObject{
    private String name;
    private String version;
    private String number;
 	public GongZhuangSQDObject(String name,String version) {
		// TODO Auto-generated constructor stub
// 		this.number=number;
 		this.name=name;
 		this.version=version;
	}
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setTreeCellData(Element paramElement) {
		// TODO Auto-generated method stub

	}

	@Override
	public Element getTreeCellData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector expand() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return this.name+"_"+version;
	}

	@Override
	public String getTipNoteText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image getOpenImage() {
		String iconName = "/images/gongzhuangshenqing.png";
		ImageIcon icon = new ImageIcon(getClass().getResource(iconName));
		return icon.getImage();
	}

	@Override
	public Image getCloseImage() {
		return getOpenImage();
	}
	public String getName() {
		return name;
	}
	public String getVersion() {
		return version;
	}
	public String getNumber() {
		return number;
 }
	@Override
	public String getEqualsName() {
		// TODO Auto-generated method stub
		return this.getDisplayName();
	}
}
