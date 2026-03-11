package com.glaway.mpm.pbombuilder.data;

import java.awt.Window;

import javax.swing.JMenu;

import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;

/**
 * <br>Created on 2012-10-19
 * @author chenyunlong
 */
public  abstract class CmMenuItemFactory {
    public abstract  CmMenuItem[] createMenuItem(CmTree tree,CmTreeNode currNode,Window owner);
	public JMenu [] createMenu(CmTree tree,CmTreeNode currNode,Window owner){
		   return null;
	}
}
