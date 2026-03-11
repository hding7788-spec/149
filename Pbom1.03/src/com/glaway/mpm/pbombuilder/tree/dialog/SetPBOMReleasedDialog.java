package com.glaway.mpm.pbombuilder.tree.dialog;

import com.glaway.mpm.pbombuilder.data.PBOMReleasedValidatorBean;
import com.glaway.mpm.pbombuilder.tree.*;
import com.glaway.mpm.pbombuilder.tree.action.BomTreeReportAction;
import com.glaway.mpm.pbombuilder.tree.action.PbomTreeEditReportAction;
import com.glaway.mpm.pbombuilder.tree.pbom.JTreeTable;
import com.glaway.mpm.pbombuilder.tree.pbom.ListModel;
import com.glaway.mpm.pbombuilder.tree.pbom.PBOMReleasedJTreeTable;
import com.glaway.mpm.pbombuilder.tree.pbom.PBOMReleasedListModel;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.CmUtil;
import com.glaway.mpm.pbombuilder.util.CmXmlUtil;
import com.glaway.mpm.pbombuilder.util.PbomReleaseUtil;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * 设置PBOM批次
 * <p>
 * Created on 2013-12-24
 *
 * @author longxiuchuan
 */
public class SetPBOMReleasedDialog {

    private static final long serialVersionUID = 1L;
    private CmTreeNode treeNode;
    public static List<CmTreeNode> cmTreeNodeList = null;//保存所有的节点
    private JButton sureButton;//确定按钮
    public static JButton cancelButton;//撤销按钮
    public static PBOMReleasedJTreeTable treeTable;
    private CmTree pbomtree;
    public static JDialog dialog;
    private PBOMReleasedListModel listModel;
    private String title = null;
    private CmTreeNode rootPart;

    public SetPBOMReleasedDialog(CmTreeNode node, CmTree pbomTree, List<PBOMReleasedValidatorBean> returnList) {
        cmTreeNodeList = new ArrayList<CmTreeNode>();
        this.treeNode = node;
        clearTreeTable(pbomTree.getRoot());
        this.pbomtree = pbomTree;
        this.rootPart = (CmTreeNode) pbomTree.getRoot().children().nextElement();
        newJDialog();
        initComponents();
        initTable();
        initAction();
        initLayout();
        showDialog(node, pbomTree);
    }

    @SuppressWarnings("unchecked")
    public void clearTreeTable(CmTreeNode root) {
        root.setChildren(null);
        if (CmCommonStringUtil.isPackage(root)) {
            for (CmTreeNode brother : root.getListNode()) {
                clearTreeTable(brother);
            }
        }
        Enumeration children = root.children();
        while (children.hasMoreElements()) {
            clearTreeTable((CmTreeNode) children.nextElement());
        }
    }

    /**
     * 用户选择完中间模型，关闭对话框后，返回被下载的文件的地址list
     *
     * @return
     */
    public void showDialog(CmTreeNode node, CmTree pbomTree) {
        cmTreeNodeList = new ArrayList<CmTreeNode>();
        this.treeNode = node;
        this.pbomtree = pbomTree;
        //cancelButton.setEnabled(false);
        initTable();
        initLayout();
        dialog.setVisible(true);

    }

    public void setSureButton(boolean b) {
        sureButton.setEnabled(b);
    }

    @SuppressWarnings("unchecked")
    public void arrayCmTreeNode(CmTreeNode node) {
        node.getPart().setSelected(false);
        Enumeration list = node.children();
        List<CmTreeNode> nodeList = new ArrayList<CmTreeNode>();
        while (list.hasMoreElements()) {
            CmTreeNode child = (CmTreeNode) list.nextElement();
            arrayCmTreeNode(child);
            nodeList.add(child);
            cmTreeNodeList.add(child);
        }
        node.setChildren(nodeList);
    }

    /**
     * 新增对话框jdialog
     */
    public void newJDialog() {
        dialog = new JDialog();
        dialog.setTitle(title);
        dialog.setSize(1100, 500);
        dialog.setMinimumSize(new Dimension(900, 500));
        dialog.setIconImage(CmUtil.getImageFromServer("mbom_edit.png"));
        dialog.setResizable(true);
        dialog.setCursor(Cursor.getDefaultCursor());
        CmCommonStringUtil.setMiddleOnScreenWithDialog(dialog);
    }

    private void initComponents() {
        sureButton = new JButton("确定");
        cancelButton = new JButton("取消");
        sureButton.setCursor(Cursor.getDefaultCursor());
        cancelButton.setCursor(Cursor.getDefaultCursor());
    }

    private void initTable() {
        cmTreeNodeList.add(treeNode);
        arrayCmTreeNode(treeNode);
        addDialogType(treeNode, "9");
        listModel = new PBOMReleasedListModel(treeNode, pbomtree, this);
        treeTable = new PBOMReleasedJTreeTable(listModel);
        treeTable.revalidate();
        treeTable.getColumnModel().getColumn(0).setPreferredWidth(350);
        treeTable.getColumnModel().getColumn(0).setMinWidth(240);
        treeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);//只能单选，没有拖拽事件
        treeTable.setRowHeight(25);
        treeTable.getTableHeader().setReorderingAllowed(false);// 表格是否可移动
        treeTable.getTableHeader().setResizingAllowed(true);// 表格大小是否可以变化
        treeTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        treeTable.setCursor(Cursor.getDefaultCursor());
    }

    private void initLayout() {
        JPanel background = new JPanel();
        background.setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setSize(new Dimension(800, 100));
        background.add(panel, BorderLayout.SOUTH);
        FlowLayout flow = new FlowLayout();
        flow.setAlignment(FlowLayout.CENTER);
        panel.setLayout(flow);
        panel.add(sureButton);
        panel.add(cancelButton);
        background.add(new JScrollPane(treeTable), BorderLayout.CENTER);
        background.setCursor(Cursor.getDefaultCursor());
        dialog.setContentPane(background);
    }

    public void addDialogType(CmTreeNode node, String type) {
        int dialogType = "".equals(type) ? 0 : Integer.valueOf(type);
        node.getPart().setDialogType(dialogType);
        List<CmTreeNode> childlist = node.getChildren();
        if (null != childlist && childlist.size() > 0) {
            for (CmTreeNode child : childlist) {
                addDialogType(child, type);
            }
        }
    }

    public void initAction() {
        treeTable.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                @SuppressWarnings("unused")
                JTreeTable table = (JTreeTable) evt.getSource();
                System.out.println("addPropertyChangeListener");

            }
        });

        //确定
        sureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("==========start PBOM Release===========");
                CmTreeNode root = pbomtree.getRoot();
                CmTreeNode rootNode = (CmTreeNode) pbomtree.getRoot().children().nextElement();
                String batch = rootNode.getPart().getBatch();
                if(rootNode.getPart().isBorrowedPart()) {
                    JOptionPane.showMessageDialog(dialog, "根节点不能勾选为借用件！");
                    return;
                }
                long containerId = ((CmTreeNode) root.children().nextElement()).getPart().getContainerId();
                System.out.println("batch====" + batch);
                setAllChildPartState(root, containerId, batch);

                long oid = ((CmTreeNode) root.children().nextElement()).getPart().getOid();
                boolean flag = false;
                List<List<Object>> updateNodesList = PbomReleaseUtil.dealWithAllChangeNodes(root);
                /**更新PBOM属性*/
                String str = PBOMEditorToWCIntf.pbomStructure(updateNodesList);// 实例化
                if ("success".equals(str)) {
                    CmXmlUtil xml = new CmXmlUtil();
                    saveModifyPartNumber(root, xml);
                    CmTreeNode firstNode = (CmTreeNode) root.children().nextElement();
                    List<Map<String, Object>> byteslist = xml.saveTreeToBytes(firstNode);
                    for (Map<String, Object> map : byteslist) {
                        /**保存PBOM xml*/
                        flag = PBOMEditorToWCIntf.savePBOMXml(String.valueOf(map.get("oid")), (byte[]) map.get("bytes"));
                        if (!flag) {
                            System.out.println("保存" + map.get("partNumber") + " xml失败！！！");
                            break;
                        }
                    }
                    if (flag) {
                        /**启动PBOM发布通知流程*/
                        flag = PBOMEditorToWCIntf.startPbomReleasedNoticeWorkflow(oid);
                        if (flag) {
                            JOptionPane.showMessageDialog(dialog, "PBOM发布通知流程已启动,相关型号调度员将会收到发布通知！");
                        } else {
                            JOptionPane.showMessageDialog(dialog, "启动PBOM发布通知流程失败,请联系管理员后再手动启动PBOM发布通知流程！");
                        }
                    } else {
                        JOptionPane.showMessageDialog(dialog, "同步本地PBOM  XML 到服务端失败，请联系管理员！");
                    }
                }else{
                    JOptionPane.showMessageDialog(dialog, "更新PBOM属性失败！");
                }


                pbomtree.updateUI();
                dialog.setVisible(false);
                System.out.println("==========end PBOM Release===========");
            }
        });

        //撤销
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                //treeTable.updateUI();
                //pbomtree.updateUI();
            }
        });

    }


    private void setAllChildPartState(CmTreeNode root, long containerId, String batch) {
        Enumeration childs = root.children();
        while (childs.hasMoreElements()) {
            CmTreeNode child = (CmTreeNode) childs.nextElement();
            CmLightPart part = child.getPart();
            System.out.println("part:number=" + part.getPartNumber() + ",batch=" + part.getBatch());
            if (containerId != part.getContainerId() || part.isBorrowedPart()) {
                System.out.println("借用件：" + part.getPartNumber() + " 未发布！");
            } else {
                if (batch != null && batch.equals(part.getBatch())) {
                    long oid = part.getOid();
                    System.out.println("set    " + part.getPartNumber() + "   APPROVED");
                    String msg = PBOMEditorToWCIntf.setPartStateForPbom(oid, "APPROVED",batch);
                    if ("".equals(msg)) {
                        part.setLifecycle("已批准");
                        setAllChildPartState(child, containerId, batch);
                    }else{
                        JOptionPane.showMessageDialog(dialog, "PBOM发布失败！" + msg);
                        return;
                    }
                }
            }
        }
    }

    /**
     * 遍历treetable
     *
     * @param node
     * @param typelist
     * @date 2013-1-15
     */
    public void circleCleanTreeNode(CmTreeNode node, List<CmCancelPart> typelist) {
        if (null != node.getChildren() && node.getChildren().size() > 0) {
            for (CmTreeNode cmnode : node.getChildren()) {
                addNodeToCancelList(cmnode, typelist);
                circleCleanTreeNode(cmnode, typelist);
            }
        }
    }

    /**
     * 同步更新零件的其他兄弟节点
     *
     * @param node
     * @date 2013-1-29
     */
    public void updateBrotherNodesOfPbomTree(CmTreeNode node, boolean isUpdateUI) {
        for (CmTreeNode cmnode : listModel.getPbomTreeList()) {
            if (CmCommonStringUtil.isCommon(node, cmnode)) {
                cmnode.getPart().setMtype(node.getPart().getMtype());
                cmnode.getPart().setZzcj(node.getPart().getZzcj());
                cmnode.getPart().setFzcj(node.getPart().getFzcj());
                cmnode.getPart().setSecondePlant(CmCommonStringUtil.copySecondePlant(node.getPart().getSecondePlant()));
                CmCommonStringUtil.checkPbomTreeNodeIsEdit(cmnode);
            }
        }
        PbomTreeEditReportAction.updatePbomTreeEditReport();
        BomTreeReportAction.updateBomReport();
        if (isUpdateUI) {
            pbomtree.updateUI();
        }
    }

    /**
     * 将节点添加到撤销前的对象list中去
     *
     * @param node
     * @param typelist
     * @date 2013-1-15
     */
    public void addNodeToCancelList(CmTreeNode node, List<CmCancelPart> typelist) {
        CmCancelPart cancel = new CmCancelPart();
        cancel.setCancelNode(node);
        cancel.setPart(CmCommonStringUtil.copyCmlightPart(node.getPart()));
        clearPbomTreeNode(node);
        typelist.add(cancel);
    }

    /**
     * 全选
     *
     * @param flag
     * @date 2013-1-15
     */
    public void allNodesSelected(boolean flag) {
        for (CmTreeNode edit : cmTreeNodeList) {
            if (!"PBOM".equals(edit.toString())) {
                edit.getPart().setSelected(flag);
                if (flag) {
                    //批量操作对象集
                    ListModel.nodeList.add(edit);
                } else {
                    ListModel.nodeList.remove(edit);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void allNodesSelected(boolean flag, CmTreeNode node) {
        Enumeration children = node.children();
        while (children.hasMoreElements()) {
            CmTreeNode child = (CmTreeNode) children.nextElement();
            if (!CmCommonStringUtil.isHasFilingOfObj(child)) {
                child.getPart().setSelected(flag);
                if (flag) {
                    //批量操作对象集
                    ListModel.nodeList.add(child);
                } else {
                    ListModel.nodeList.remove(child);
                }
            }
            allNodesSelected(flag, child);
        }
    }

    /**
     * 撤销
     *
     * @date 2013-1-15
     */
    public void cancelOperation() {
        List<CmCancelPart> cancelList = ListModel.cancelList;
        System.out.println("--cancelList---" + cancelList);
        if (null != cancelList && cancelList.size() > 0) {
//			CmCancelPart part = cancelList.get(cancelList.size() - 1);
//			if(part.isClear()){
//				for(CmCancelPart cancel:part.getList()){
//					cancel.getCancelNode().setPart(cancel.getPart());
//					listModel.updateBrotherNodesOfPbomTree(cancel.getCancelNode(), false);
//					CmCommonStringUtil.checkPbomTreeNodeIsEdit(cancel.getCancelNode());
//				}
//			}

            for (CmCancelPart cancel : cancelList) {
                if (cancel.isChangeMType()) {
                    cancel.getCancelNode().getPart().setMtype(cancel.getOldMType());
                }
                if (cancel.isChangeZzcj()) {
                    cancel.getCancelNode().getPart().setZzcj(cancel.getOldZzcj());
                }
                if (cancel.isChangeFzcj()) {
                    cancel.getCancelNode().getPart().setFzcj(cancel.getOldFzcj());
                    cancel.getCancelNode().getPart().setSecondePlant(cancel.getSecondePlant());
                }
                CmCommonStringUtil.checkPbomTreeNodeIsEdit(cancel.getCancelNode());
            }

            PbomTreeEditReportAction.updatePbomTreeEditReport();
            BomTreeReportAction.updateBomReport();
            pbomtree.updateUI();
            //cancelList.remove(part);
            if (null != cancelList && cancelList.size() > 0) {
                cancelButton.setEnabled(true);
            } else {
                cancelButton.setEnabled(false);
            }
        }
        dialog.setVisible(false);
    }

    /**
     * 将零件（包括父节点或其他节点上的相同零件）属性清空
     *
     * @param node
     * @date 2013-1-15
     */
    public void clearPbomTreeNode(CmTreeNode node) {
        for (CmTreeNode cmnode : cmTreeNodeList) {
            if (CmCommonStringUtil.isCommonNode(node, cmnode)) {
                CmTreeNode pbom = getNodeFromPbomTreeList(node);
                CmLightPart pbompart = pbom.getPart();
                CmLightPart part = node.getPart();
                part.setKey(pbompart.isKey());
                part.setSpecial(pbompart.isSpecial());
                part.setSpaceBorneTable(pbompart.isSpaceBorneTable());
                part.setSelected(pbompart.isSelected());
                part.setWorkShop(pbompart.getWorkShop());
                part.setOutsourcingUnits(pbompart.getOutsourcingUnits());
                part.setMaterialType(pbompart.getMaterialType());
                part.setBackupRate(pbompart.getBackupRate());
                part.setMaxBackupCount(pbompart.getMaxBackupCount());
                part.setBackupReason(pbompart.getBackupReason());
                part.setRemark(pbompart.getRemark());
                CmCommonStringUtil.checkPbomTreeNodeIsEdit(node);
                PbomTreeEditReportAction.updatePbomTreeEditReport();
                BomTreeReportAction.updateBomReport();
            }
        }
    }

    /**
     * 找到刚开始打开PBOM的时候的节点信息
     *
     * @param node
     * @return
     * @author chenyunlong
     * @date 2013-4-7
     */
    public CmTreeNode getNodeFromPbomTreeList(CmTreeNode node) {
//		for(CmTreeNode cmnode:CmScrollPaneTree.pbomlist){
//			if(CmCommonStringUtil.isSameNode(node, cmnode)){
//				return cmnode;
//			}
//		}
//		return null;
        return CmScrollPaneTree.pbomMap.get(node);
    }

    /**
     * pbomTreeList是按照零部件的个数存放在PbomTreeList里的，所以
     * 需要将相同编号的零部件过滤出来
     *
     * @param pbomTreeList
     * @return
     */
    public List<CmTreeNode> filterPbomTreeList(List<CmTreeNode> pbomTreeList) {
        List<CmTreeNode> pbomTreeListTemp = new ArrayList<CmTreeNode>();
        if (pbomTreeList != null) {
            for (CmTreeNode cmTreeNode : pbomTreeList) {
                String partNumber = cmTreeNode.getPart().getPartNumber();
                boolean flag = false;
                for (CmTreeNode cmTreeNode2 : pbomTreeListTemp) {
                    String partNumberTemp = cmTreeNode2.getPart().getPartNumber();
                    if (partNumber.equals(partNumberTemp)) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    pbomTreeListTemp.add(cmTreeNode);
                }
            }
        }
        return pbomTreeListTemp;
    }

    /**
     * 清空备注
     *
     * @param node
     * @param remarkList
     * @author chenyunlong
     * @date 2013-4-8
     */
    public void clearRemark(CmTreeNode node, List<CmCancelPart> remarkList) {
        if (!CmCommonStringUtil.isEmpty(node.getPart().getRemark())) {
            CmCancelPart remark = new CmCancelPart();
            remark.setObject(node.getPart().getRemark());
            remark.setCancelNode(node);
            remarkList.add(remark);
            node.getPart().setRemark(null);
        }
        if (null != node.getChildren() && node.getChildren().size() > 0) {
            for (CmTreeNode cmnode : node.getChildren()) {
                clearRemark(cmnode, remarkList);
            }
        }
    }

    public static void colseEditPbomDialog() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (null != dialog) {
                    dialog.setVisible(false);
                }
            }
        });
    }

    /**
     * 将有修改的xml的零组件的partNumber记录到直接或间接有xml的父节点中去
     *
     * @author chenyunlong
     * @date 2013-8-2
     * @param root
     * @param xml
     *
     */
    @SuppressWarnings("unchecked")
    public void saveModifyPartNumber(CmTreeNode root, CmXmlUtil xml) {
        Enumeration children = root.children();
        while (children.hasMoreElements()) {
            CmTreeNode child = (CmTreeNode) children.nextElement();
            if (child.getPart().isHasXml()) {
                if (xml.checkNodeIsSaveXML(child)) {
                    saveModifyChildPartNumber((CmTreeNode) child.getParent(), child.getPart().getPartNumber(), xml);
                }
            } else {
                boolean flag = xml.checkNodeIsChange(child);
                if (flag) {
                    CmTreeNode parent = getParentNodeWithXML((CmTreeNode) child.getParent());
                    if (null != parent) {
                        parent.getPart().setEditWithChild(true);
                        if (CmCommonStringUtil.isPackage(parent)) {
                            for (CmTreeNode brother : parent.getListNode()) {
                                brother.getPart().setEditWithChild(true);
                            }
                        }
                        saveModifyChildPartNumber((CmTreeNode) parent.getParent(), parent.getPart().getPartNumber(), xml);
                    }
                }
            }
            saveModifyPartNumber(child, xml);
        }
    }

    /**
     * 为了提高工艺变更查找变更的有xml的零组件的效率 需要将变化的有xml的零组件的partNumber信息记录到直接或间接有xml的父节点中去
     *
     * @author chenyunlong
     * @date 2013-8-2
     * @param parent
     * @param partNumber
     * @param xml
     *
     */
    public void saveModifyChildPartNumber(CmTreeNode parent, String partNumber, CmXmlUtil xml) {
        if (null != parent) {
            if ((parent.getPart().isHasXml() || (null != parent.getParent() && "PBOM".equals(parent.getParent().toString())))
                    && !CmCommonStringUtil.isEqual(parent.getPart().getPartNumber(), partNumber)) {
                String modifyPartNumber = parent.getPart().getModifyXmlPartNumber() + "";
                String[] number = null;
                if (CmCommonStringUtil.isEmpty(modifyPartNumber)) {
                    number = new String[0];
                } else {
                    number = modifyPartNumber.split(",");
                }
                boolean flag = true;
                for (int i = 0; i < number.length; i++) {
                    if (CmCommonStringUtil.isEqual(number[i], partNumber)) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    if (CmCommonStringUtil.isEmpty(modifyPartNumber)) {
                        parent.getPart().setModifyXmlPartNumber(partNumber);
                    } else {
                        parent.getPart().setModifyXmlPartNumber(modifyPartNumber + "," + partNumber);
                    }
                }
            }
            saveModifyChildPartNumber((CmTreeNode) parent.getParent(), partNumber, xml);
        }
    }

    /**
     * 有些零件被编辑过属性，或升版，但是自己没有xml，就要找出有xml的父节点
     *
     * @author chenyunlong
     * @date 2013-8-2
     * @param parent
     * @return
     *
     */
    public CmTreeNode getParentNodeWithXML(CmTreeNode parent) {
        if (null != parent) {
            if (null != parent.getParent() && !"PBOM".equals(parent.getParent().toString())) {
                if (parent.getPart().isHasXml()) {
                    return parent;
                } else {
                    return getParentNodeWithXML((CmTreeNode) parent.getParent());
                }
            } else if (null != parent.getParent() && "PBOM".equals(parent.getParent().toString())) {
                return parent;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }



}
