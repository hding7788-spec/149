package com.glaway.mpm.pbombuilder.tree.pbom;

import javax.swing.JTree;  
import javax.swing.SwingUtilities;  
import javax.swing.table.AbstractTableModel;  
import javax.swing.tree.TreePath;  
import javax.swing.event.TreeExpansionEvent;  
import javax.swing.event.TreeExpansionListener;  
import javax.swing.event.TreeModelEvent;  
import javax.swing.event.TreeModelListener;  

import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
  
public class TreeTableModelAdapter extends AbstractTableModel {  
    JTree tree;  
  
    TreeTableModel treeTableModel;  
  
    public TreeTableModelAdapter(TreeTableModel treeTableModel, JTree tree) {  
        this.tree = tree;  
        for (int i = 0; i < this.tree.getRowCount(); i++) {
        	TreePath path =this.tree.getPathForRow(i);
        	CmTreeNode node=(CmTreeNode) path.getLastPathComponent();
			this.tree.expandRow(i);
		}
        this.treeTableModel = treeTableModel;  
  
        tree.addTreeExpansionListener(new TreeExpansionListener() {  
            // Don't use fireTableRowsInserted() here; the selection model  
            // would get updated twice.节点数的展开和收起  
            public void treeExpanded(TreeExpansionEvent event) {  
                fireTableDataChanged();  
            }  
  
            public void treeCollapsed(TreeExpansionEvent event) {  
                fireTableDataChanged();  
            }  
        });  
  
        treeTableModel.addTreeModelListener(new TreeModelListener() {  
            public void treeNodesChanged(TreeModelEvent e) {  
                delayedFireTableDataChanged();  
            }  
  
            public void treeNodesInserted(TreeModelEvent e) {  
                delayedFireTableDataChanged();  
            }  
  
            public void treeNodesRemoved(TreeModelEvent e) {  
                delayedFireTableDataChanged();  
            }  
  
            public void treeStructureChanged(TreeModelEvent e) {  
                delayedFireTableDataChanged();  
            }  
        });  
    }  
  
    public int getColumnCount() {  
        return treeTableModel.getColumnCount();  
    }  
  
    public String getColumnName(int column) {  
        return treeTableModel.getColumnName(column);  
    }  
  
    public Class getColumnClass(int column) {  
        return treeTableModel.getColumnClass(column);  
    }  
  
    public int getRowCount() {  
        return tree.getRowCount();  
    }  
  
    protected Object nodeForRow(int row) {  
        TreePath treePath = tree.getPathForRow(row);  
        return treePath.getLastPathComponent();  
    }  
  
    public Object getValueAt(int row, int column) {  
        return treeTableModel.getValueAt(nodeForRow(row), column);  
    }  
  
    public boolean isCellEditable(int row, int column) {  
        return treeTableModel.isCellEditable(nodeForRow(row), column);  
    }  
  
    public void setValueAt(Object value, int row, int column) {  
        treeTableModel.setValueAt(value, nodeForRow(row), column);  
    }  
  
    protected void delayedFireTableDataChanged() {  
        SwingUtilities.invokeLater(new Runnable() {  
            public void run() {  
                fireTableDataChanged();  
            }  
        });  
    }  
}  