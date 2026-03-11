/**
 * <br>Created on 2011-3-28
 * @author Alex.Huang - ����
 */
package com.glaway.mpm.visual.view.tree.menu;

import java.awt.Window;

import com.glaway.mpm.visual.view.tree.VaTree;
import com.glaway.mpm.visual.view.tree.VaTreeNode;


/**
 * <br>Created on 2011-3-28
 * @author Alex.Huang - ����
 */
public interface VaMenuItemFactory {
   VaMenuItem[] createMenuItem(VaTree tree,VaTreeNode currNode,Window owner);
}
