package com.glaway.mpm.pbombuilder.tree;

import java.awt.Color;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.JToolTip;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.ptc.pview.pvkapp.Instance;

/**
 * 报表中ebom和pbom树的渲染
 * @author chenyunlong
 *
 */
public class CmReportTree  extends JTree implements Cloneable {
	private static final long             serialVersionUID       = 4607849422784307260L;

	   private CmTreeNode                    root                   = null;
	   private HashMap<Instance, CmTreeNode> instanceDICNodeMapping = new HashMap<Instance, CmTreeNode>();
	   private boolean                       showTooltip            = false;
	   private ToolTipManager                ttm;

	   public CmReportTree(CmTreeNode dictn) {
	      super();
	      if (dictn != null)
	         root = dictn;
	      ((DefaultTreeModel) getModel()).setRoot(root);

	      setOpaque(false);
	      setRowHeight(16);
	      //addTreeWillExpandListener(new DICTreeWillExpandListener());
	      setCellRenderer(new CmReportTreeRenderer());
	      addMouseListener(new CmTreeMouseAdapter(this));
	      getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

	      //addMouseListener(new DICMouseAdapter(this));
	      // addTreeSelectionListener(this);

	      ttm = ToolTipManager.sharedInstance();
	      ttm.setDismissDelay(10000);//pref
	      ttm.setInitialDelay(100);//pref
	      ttm.setReshowDelay(50);
	   }

	   @Override
	   public JToolTip createToolTip() {
	      if (showTooltip) {
	         JToolTip ttp = super.createToolTip();
	         ttp.setBackground(new Color(255, 255, 200));
	         ttp.setForeground(Color.BLACK);
	         return ttp;
	      }
	      return null;
	   }

		public void addPVMapping(Instance instance, CmTreeNode node) {
			instanceDICNodeMapping.put(instance, node);
		}

		public void removePVMapping(Instance instance) {
			instanceDICNodeMapping.remove(instance);
		}

		public CmTreeNode getNodeFromInstance(Instance instance) {
			return (CmTreeNode) instanceDICNodeMapping.get(instance);
		}

	   public boolean isShowTooltip() {
	      return showTooltip;
	   }

	   public void setShowTooltip(boolean showTooltip) {
	      if (showTooltip)
	         ttm.registerComponent(this);
	      else
	         ttm.unregisterComponent(this);
	      this.showTooltip = showTooltip;
	   }

	   public CmTreeNode getSelectedNode() {
	      return (CmTreeNode) getLastSelectedPathComponent();
	   }

	   public void collapse(CmTreeNode selectedDICTreeNode) {
	      if (selectedDICTreeNode != null) {
	         Enumeration depthFirstEnumeration = selectedDICTreeNode.depthFirstEnumeration();
	         while (depthFirstEnumeration.hasMoreElements()) {
	            CmTreeNode currentICBTreeNode = (CmTreeNode) depthFirstEnumeration.nextElement();
	            if (!isCollapsed(new TreePath(currentICBTreeNode.getPath())))
	               collapsePath(new TreePath(currentICBTreeNode.getPath()));
	         }
	      }
	   }

	   public void expandAllLevels(CmTreeNode anICBTreeNode) {
	      if (anICBTreeNode != null) {
	         Enumeration breadthFirstEnumeration = anICBTreeNode.breadthFirstEnumeration();
	         while (breadthFirstEnumeration.hasMoreElements()) {
	            CmTreeNode currentICBTreeNode = (CmTreeNode) breadthFirstEnumeration.nextElement();
	            if (isCollapsed(new TreePath(currentICBTreeNode.getPath())))
	               expandPath(new TreePath(currentICBTreeNode.getPath()));
	         }
	      }
	   }

	   public CmTreeNode getRoot() {
	      return root;
	   }

	   public void setRoot(CmTreeNode root) {
	      this.root = root;
	      ((DefaultTreeModel)this.getModel()).setRoot(root);
	   }

	   /**
	    * ��д������ʼ����ʾ�ڵ���
	    * @return
	    */
	   public boolean getShowsRootHandles() {
	      return true;
	   }

	   @Override
	   protected Object clone() throws CloneNotSupportedException {
	      try {
	         return super.clone();
	      } catch (Exception e) {
	         e.printStackTrace();
	         return null;
	      }

	   }
}
