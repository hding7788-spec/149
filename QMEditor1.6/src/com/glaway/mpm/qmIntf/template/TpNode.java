package com.glaway.mpm.qmIntf.template;

import java.io.Serializable;

import javax.swing.tree.DefaultMutableTreeNode;

import com.glaway.mpm.model.ProcessTemplate;

public class TpNode extends DefaultMutableTreeNode implements Serializable {

	private static final long serialVersionUID = 1L;
	private ProcessTemplate template;

	public TpNode(String oid, String number, String name) {
		template = new ProcessTemplate(oid, number, name);
	}

	public ProcessTemplate getTemplate() {
		return template;
	}

	public void setTemplate(ProcessTemplate template) {
		this.template = template;
	}

}
