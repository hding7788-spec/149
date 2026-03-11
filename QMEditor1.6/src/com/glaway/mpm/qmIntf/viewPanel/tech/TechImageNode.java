package com.glaway.mpm.qmIntf.viewPanel.tech;

import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;

public class TechImageNode extends DefaultMutableTreeNode implements
		Serializable {

	private static final long serialVersionUID = 1L;
	private String modelName;
	private String name;
	private String version;
	private String imageOid;
	private String dir;

	public TechImageNode(String modelName, String name, String dir,
			String version, String imageOid) {
		super();
		this.modelName = modelName;
		this.name = name;
		this.dir = dir;
		this.version = version;
		this.imageOid = imageOid;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageOid() {
		return imageOid;
	}

	public void setImageOid(String imageOid) {
		this.imageOid = imageOid;
	}

}
