package com.glaway.mpm.visual.view.tree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpPartNode;
import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpTecnicsNode;
import com.glaway.mpm.visual.bean.VaLightPart;

/**
 * 根据提供的java.util.Comparator实现类
 * 在valueChange方法中判断传入的节点与CmTree中的节点是否相同，判断完成后将所有相同节点的集合发送给doLinkage方法。
 * 使用本类，您可能需要一个继承于本类的实例类来实现本类的doLinkage方法。
 * 另外，如果你想按照你自己想法判断两个实例是否相同，你需要一个实现了java.util.Comparator接口的实例，这里把它叫做【比较方案类】
 * 如果，你没有提供这样的一个类，将会使用本类中提供的默认比较方案类来作比较，尽管那样并不太合适
 *
 * @param comtor <br>
 * @author
 */
public abstract class VaAbstractTreeLinkage<T extends DefaultMutableTreeNode>
        implements VaTreeLinkage<T> {
    // protected Map<String, JTree> trees = new HashMap<String, JTree>();
    protected List<JTree> trees = new ArrayList<JTree>();
    protected Comparator comtor = new DefaultComparator();

    public VaAbstractTreeLinkage(Comparator comtor) {
        this.comtor = comtor;
    }

    public void valueChange(List<T> nodes) {
        linkageBefore(nodes);
        for (JTree tree : trees) {
            System.out.println("=======valuechangetree" + tree);
        }
        Iterator<JTree> it = trees.iterator();
        while (it.hasNext()) {
            JTree tree = it.next();
            List<T> selectNodes = new ArrayList<T>();
            for (T node : nodes) {
                comparat(tree, node, selectNodes);
            }
            doLinkage(tree, selectNodes);
        }
        linkageAfter(nodes);

    }

    public void nodeChange(List<T> nodes) {

        Iterator<JTree> it = trees.iterator();
        while (it.hasNext()) {
            JTree tree = it.next();
//			if(tree.getModel().getRoot() instanceof DpTecnicsNode && false){
//				VaTreeNode root = (VaTreeNode)tree.getModel().getRoot();
//				Enumeration<VaTreeNode> children = root.breadthFirstEnumeration();
//				while (children.hasMoreElements()) {
//					VaTreeNode treeNode = (VaTreeNode) children.nextElement();
//
//					if (this.comtor.compare(node, treeNode) == 0) {
//						((DefaultTreeModel)tree.getModel()).removeNodeFromParent(treeNode);
//					}
//
//				}
//			}
            VaTreeNode root = (VaTreeNode) tree.getModel().getRoot();
            if ("ZPBOM".equals(root.toString()) || "FPBOM".equals(root.toString())) {
                continue;
            }

            List<T> selectNodes = new ArrayList<T>();

            for (T node : nodes) {
                comparaAndCheck(tree, (VaTreeNode) node, (List<VaTreeNode>) selectNodes);
            }

            doLinkage(tree, selectNodes);

            tree.repaint();

        }

    }

    public void modelNodeAdd(T node) {
        System.out.println("treemodel change");
        for (JTree tree : trees) {
            System.out.println("=======valuechangetree" + tree);
        }
        Iterator<JTree> it = trees.iterator();
        while (it.hasNext()) {
            JTree tree = it.next();
            VaTreeNode root = (VaTreeNode) tree.getModel().getRoot();
            if (!"ZPBOM".equals(root.toString()) && !"FPBOM".equals(root.toString())) {
                continue;
            }
            Enumeration<VaTreeNode> children = root.breadthFirstEnumeration();
            while (children.hasMoreElements()) {
                VaTreeNode treeNode = (VaTreeNode) children.nextElement();


                if (this.comtor.compare((VaTreeNode) node, treeNode) == 0) {
                    if (treeNode.isUsed()) {
                        treeNode.setUsed(false);
                    } else {
                        treeNode.setUsed(true);
                    }

                }

            }

            tree.repaint();
        }
    }

    public void modelNodeRemove(T node) {
        System.out.println("treemodel change");
        Iterator<JTree> it = trees.iterator();
        while (it.hasNext()) {
            JTree tree = it.next();
            VaTreeNode root = (VaTreeNode) tree.getModel().getRoot();
            if (!"PBOM".equals(root.toString())) {
                continue;
            }
            Enumeration<VaTreeNode> children = root.breadthFirstEnumeration();
            while (children.hasMoreElements()) {
                VaTreeNode treeNode = (VaTreeNode) children.nextElement();

                if (this.comtor.compare((VaTreeNode) node, treeNode) == 0) {
                    treeNode.setUsed(false);
                }

            }

            tree.repaint();
        }
    }


//	@Override
//	public int compare(VaTreeNode o1, VaTreeNode o2) {
//		System.out.println("==========addnode========="+o1);
//		System.out.println("==========addnode========="+o2);
//		System.out.println("==========addnode========="+o1.getOccpath());
//		System.out.println("==========addnode========="+o2.getOccpath());
//		if(o1.getOccId().equals(o2.getOccId())&&(o1.getPart().getZcmark().equals(o2.getPart().getZcmark()))){
//			return 0;
//		}
//		return 1;
//	}

    protected void comparat(JTree tree, T node, List<T> selectNodes) {
        T root = (T) tree.getModel().getRoot();
        Enumeration<T> children = root.breadthFirstEnumeration();

        while (children.hasMoreElements()) {
            T treeNode = children.nextElement();
            if (this.comtor.compare((VaTreeNode) node, (VaTreeNode) treeNode) == 0) {
                selectNodes.add(treeNode);
            }
        }

    }

    protected void comparaAndCheck(JTree tree, VaTreeNode node, List<VaTreeNode> selectNodes) {
        T root = (T) tree.getModel().getRoot();
        Enumeration<T> children = root.breadthFirstEnumeration();

        while (children.hasMoreElements()) {
            VaTreeNode treeNode = (VaTreeNode) children.nextElement();
            if (this.comtor.compare(node, treeNode) == 0) {
                treeNode.setSelected(node.isSelected());
                selectNodes.add(treeNode);
            }
        }

    }

    protected void linkageBefore(List<T> nodes) {

    }

    protected void linkageAfter(List<T> nodes) {

    }

    /**
     * 匹配成功后执行的操作
     *
     * @param tree
     * @param node
     */
    public abstract void doLinkage(JTree tree, List<T> node);

    /**
     * 匹配成功后执行的操作
     *
     * @param tree
     * @param node
     */
    public abstract void doLinkageNode(List<T> nodes, List<T> nodes2);

    /**
     * 添加关联树
     *
     * @param tree
     * @param node
     */
    public VaAbstractTreeLinkage addLinkageTree(VaTree tree) {
        trees.add(tree);
        return this;
    }

    /**
     * 删除关联树
     *
     * @param key
     * @param tree
     */
    public VaAbstractTreeLinkage delLinkageTree(String key) {
        trees.remove(key);
        return this;
    }

    class DefaultComparator implements Comparator<VaTreeNode> {
        public int compare(VaTreeNode o1, VaTreeNode o2) {
            if (o1.getOccId().equals(o2.getOccId())) {
                return 0;
            }
            return -1;
        }
    }

    public VaTreeNode findLinkNode(VaTreeNode node, VaTree tree) {
        VaTreeNode root = (VaTreeNode) tree.getModel().getRoot();
        @SuppressWarnings("unchecked")
        Enumeration<VaTreeNode> children = root.breadthFirstEnumeration();
        while (children.hasMoreElements()) {
            VaTreeNode treeNode = children.nextElement();
            if (this.comtor.compare(node, treeNode) == 0) {
                return treeNode;
            }
        }
        return null;
    }
}
