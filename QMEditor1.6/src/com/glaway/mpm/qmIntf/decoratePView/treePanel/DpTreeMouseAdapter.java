package com.glaway.mpm.qmIntf.decoratePView.treePanel;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import com.glaway.mpm.qmIntf.fittingTool.view.FittingsDistributionFrame;
import com.glaway.mpm.qmIntf.participatePart.ParticipatePartUtil;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.tree.VaTree;
import com.glaway.mpm.visual.view.tree.VaTreeNode;
import com.glaway.mpm.visual.view.tree.menu.*;

/**
 * <br>
 * Created on 2011-3-17
 *
 * @author Alex.Huang - ����
 */
public class DpTreeMouseAdapter extends JPopupMenu implements MouseListener {
    private static final long serialVersionUID = 2275535113893449843L;

    private static final VaLogger log = VaLogger.getLogger(DpTreeMouseAdapter.class.getName());

    private VaTree tree;
    private Window owner;
    private VaTreeNode currNode;
    private List<VaTreeNode> selectedNodes;

    public VaMenuItemFactory itemFactory;

    /**
     * @param owner ָ�Ҽ�˵����е������ owner
     */
    public DpTreeMouseAdapter(Window owner, VaMenuItemFactory itemFactory) {
        super();
        this.owner = owner;
        this.itemFactory = itemFactory;
        initUI();
    }

    protected void initUI() {
        initActions();
        initComponents();
    }

    protected void initActions() {

    }

    protected void initComponents() {

    }

    public void addItems(JMenuItem[] itemArray) {
        if (itemArray == null)
            return;
        for (JMenuItem item : itemArray)
            this.add(item);
    }

    /**
     * MouseListener�ӿڷ���
     */
    public void mousePressed(MouseEvent e) {
        log.debug("mousePressed");
        this.tree = (VaTree) e.getSource();
        int row = tree.getRowForLocation(e.getX(), e.getY());
        TreePath currPath = tree.getPathForRow(row);
        TreePath[] paths = tree.getSelectionPaths();
        List<TreePath> pathList = new Vector<TreePath>();
        if (paths != null)
            for (TreePath path : paths) {
                pathList.add(path);
            }

        if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0 && currPath != null) {
            VaTreeNode node = (VaTreeNode) currPath.getLastPathComponent();
            this.currNode = node;
            if (!pathList.contains(currPath)) {
                if (node != node.getRoot()) {
                    tree.setSelectionPath(currPath);
                }
            }
            if (configureUI())
                this.show(e.getComponent(), e.getX(), e.getY());
        }

    }

    private void initMenu() {
        for (Component comp : this.getComponents()) {
            this.remove(comp);
        }
    }

    private boolean configureUI() {
        initMenu();
        if (itemFactory == null)
            return false;
        JMenuItem[] items = itemFactory.createMenuItem(tree, currNode, owner);
        addItems(items);
        setEnable(currNode.isUsed(), items);
        if (currNode instanceof DpStepNode || currNode instanceof DpPaceNode) {
            add(new VaBomAnnoNodeMenu(tree, currNode, owner));
        }
        if (items.length > 0)
            return true;
        else
            return false;
    }

    public void setEnable(boolean flag, JMenuItem[] items) {
        for (JMenuItem item : items) {
            if (item instanceof VaEBomPackupNodeMenuItem) {
                if (currNode.getPart() != null && currNode.getPart().getAmount() > 1) {
                    item.setEnabled(false);
                } else if (currNode instanceof DpStepNode || currNode instanceof DpPaceNode) {
                    item.setEnabled(false);
                } else {
                    item.setEnabled(true);
                }
            }
            if (item instanceof VaEBomUnpackNodeMenuItem) {
                if (currNode.getPart() != null && currNode.getPart().getAmount() > 1) {
                    item.setEnabled(true);
                } else if (currNode instanceof DpStepNode || currNode instanceof DpPaceNode) {
                    item.setEnabled(false);
                } else {
                    item.setEnabled(false);
                }
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        log.debug("1:mouseClick");
        this.tree = (VaTree) e.getSource();
        int row = tree.getRowForLocation(e.getX(), e.getY());
        TreePath currPath = tree.getPathForRow(row);
        if (currPath != null) {
            VaTreeNode node = (VaTreeNode) currPath.getLastPathComponent();
            if (node != node.getRoot()) {
                // tree.setSelectionPath(currPath);
                /*
                 * Vector bboxs = node.getBboxes(); if (bboxs != null) { for
                 * (Object obj : bboxs) { BoundingBox bbox = (BoundingBox) obj;
                 * Point3d lower = new Point3d(); Point3d upper = new Point3d();
                 * bbox.getLower(lower); bbox.getUpper(upper);
                 *
                 * VaMainframe main = VaMainframe.getInstance();
                 * main.setX1(String.valueOf(lower.getX()));
                 * main.setX2(String.valueOf(upper.getX()));
                 * main.setY1(String.valueOf(lower.getY()));
                 * main.setY2(String.valueOf(upper.getY()));
                 * main.setZ1(String.valueOf(lower.getZ()));
                 * main.setZ2(String.valueOf(upper.getZ())); } }
                 */
                //显示工序/工步内容 add by zhuhao 2017.12.25
                if (node instanceof DpStepNode) {
                    DpStepNode step = (DpStepNode) node;
                    String stepOid = step.getStep().getOid();
                    String stepValue = ParticipatePartUtil.getStepValue(stepOid);
                    stepValue = delHTMLTag(stepValue);
                    stepValue = stepValue.replace("&nbsp;", "");
                    stepValue = stepValue.replaceAll(" ", "");
                    stepValue = stepValue.replaceAll("\r|\n", " ");
                    stepValue = stepValue.replace("&gt;", ">");
                    stepValue = stepValue.replace("&lt;", "<");
                    FittingsDistributionFrame.bomLable.setText(stepValue);
                } else if (node instanceof DpPaceNode) {
                    DpPaceNode pace = (DpPaceNode) node;
                    String paceValue = pace.getPace().getContent();
                    paceValue = delHTMLTag(paceValue);
                    paceValue = paceValue.replace("&nbsp;", "");
                    paceValue = paceValue.replaceAll("\r|\n", " ");
                    paceValue = paceValue.replace("&gt;", ">");
                    paceValue = paceValue.replace("&lt;", "<");
                    FittingsDistributionFrame.bomLable.setText(paceValue);
                }
            }
        }


    }

    public void mouseEntered(MouseEvent e) {
        // log.debug("2");
    }

    public void mouseExited(MouseEvent e) {
        // log.debug("3");
    }

    public void mouseReleased(MouseEvent e) {
        // log.debug("4");
    }

    public static String delHTMLTag(String htmlStr) {
        String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
        String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式

        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); //过滤script标签

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); //过滤style标签

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); //过滤html标签

        return htmlStr.trim(); //返回文本字符串
    }
}
