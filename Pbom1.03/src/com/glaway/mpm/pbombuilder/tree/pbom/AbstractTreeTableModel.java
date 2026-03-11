package com.glaway.mpm.pbombuilder.tree.pbom;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import com.glaway.mpm.pbombuilder.tree.CmScrollPaneTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.LoadConfig;

public abstract class AbstractTreeTableModel implements TreeTableModel {
    protected Object root;

    protected EventListenerList listenerList = new EventListenerList();

    public AbstractTreeTableModel(Object root) {
        this.root = root;
    }

    public Object getRoot() {
        return root;
    }

    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    public int getIndexOfChild(Object parent, Object child) {
        for (int i = 0; i < getChildCount(parent); i++) {
            if (getChild(parent, i).equals(child)) {
                return i;
            }
        }
        return -1;
    }

    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.remove(TreeModelListener.class, l);
    }

    protected void fireTreeNodesChanged(Object source, Object[] path,
            int[] childIndices, Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path, childIndices, children);
                ((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
            }
        }
    }

    protected void fireTreeNodesInserted(Object source, Object[] path,
            int[] childIndices, Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path, childIndices, children);
                ((TreeModelListener) listeners[i + 1]).treeNodesInserted(e);
            }
        }
    }

    protected void fireTreeNodesRemoved(Object source, Object[] path,
            int[] childIndices, Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path, childIndices, children);
                ((TreeModelListener) listeners[i + 1]).treeNodesRemoved(e);
            }
        }
    }

    protected void fireTreeStructureChanged(Object source, Object[] path,
            int[] childIndices, Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path, childIndices, children);
                ((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
            }
        }
    }

    public Class getColumnClass(int column) {
        return Object.class;
    }

    /**
     * PBOM节点不可更改
     * 关键件属性是从EBOM带过来的不可更改
     *
     */
    public boolean isCellEditable(Object node, int column) {
    	CmTreeNode cmTreeNode=(CmTreeNode)node;

    	String [] plants = LoadConfig.getInstance().getMainPlant();
    	String plant = cmTreeNode.getPart().getZzcj();
    	String materialType = cmTreeNode.getPart().getMtype();

    	int ccol = -1;

    	for(int i=0;i<plants.length;i++){
    		if(plants[i].equals(plant)){
    			ccol = i;
    			break;
    		}
    	}
    	if("PBOM".equals(node.toString())){
    		return false;
    	} else if(cmTreeNode.getPart().getDialogType() == 1){
    		if(column == 2 || column == 3 || column == 4 || column == 5 || column == 6) {
    			return false;
    		} else {
    			return true;
    		}
    	} else if(cmTreeNode.getPart().getDialogType() == 2){
    		if("自制件".equals(materialType)||"外配套件".equals(materialType)
    				||"带料委外件".equals(materialType)||"不带料委外件".equals(materialType)){
    			if(column == 2 || column == 3 || column == 4 || column == 5 || column == 6 || column == 7) {
    				return false;
    			} else {
    				return true;
    			}
    		} else {
    			return false;
    		}
    	}  else if (cmTreeNode.getPart().getDialogType() == 3){
    		if(column == 2 || column == 3 || column == 4 || column == 5 || column == 6) {
    			return false;
    		} else {
    			return true;
    		}
    	}  else if (cmTreeNode.getPart().getDialogType() == 4){
    		String partNum=cmTreeNode.getPart().getPartNumber();
    		String version=CmScrollPaneTree.versionInfor.get(partNum);
    		if(null!=version && !"".equals(version) && !"null".equals(version)){
				if (!version.equals("space.0")) {
					if (column == 6 ) {
						return true;
					}
				} else {
					if (column == 2) {
						return false;
					}
				}
    	    }else{
    	    	return true;
    	    }
    	} else if (cmTreeNode.getPart().getDialogType() == 9){
            return column == 11;
        }
    	else{
    		return false;
    	}
		return true;
    }


    public void setValueAt(Object aValue, Object node, int column) {
    }

}