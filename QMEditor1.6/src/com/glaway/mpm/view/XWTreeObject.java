package com.glaway.mpm.view;

import java.awt.Image;
import java.util.Vector;
import org.dom4j.Element;

public abstract interface XWTreeObject extends Comparable {
	public abstract void setTreeCellData(Element paramElement);

	public abstract Element getTreeCellData();

	public abstract Vector expand() throws Exception;

	public abstract String getDisplayName();

	public abstract String getEqualsName();

	public abstract String getTipNoteText();

	public abstract Image getOpenImage();

	public abstract Image getCloseImage();
}
