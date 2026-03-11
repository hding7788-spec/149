package com.glaway.mpm.qmIntf.technics;

import com.glaway.mpm.model.TempObject;
import com.glaway.mpm.qmIntf.technics.entity.Technics;
import com.glaway.mpm.util.*;
import com.glaway.mpm.view.*;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import org.dom4j.Document;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class TechnicsSearchPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private VaLogger logger = VaLogger.getLogger(this.getClass());

    private JPanel mainPanel;
    private JPanel topPanel;
    private JPanel middlePanel;
    private JPanel middleMainPanel;
    private JPanel topMainPanel;

    private JLabel nameLabel;
    private JLabel numberLabel;
    private JLabel typeLabel;

    private JTextField number;
    private JTextField name;
    private JComboBox jComboBox1;

    private JButton searchButton;
    private JButton clearButton;

    private JScrollPane jScrollPane;
    private JTable jTable;

//    private JButton openButton;
    private JButton sureButton;
    private JButton borrowTechnicsButton;
    private JButton cancelButton;
    private JDialog dialog;
    private NewTechnicsPart frame;
    private Technics technics;

    public TechnicsSearchPanel(JDialog dialog, NewTechnicsPart frame) {
        this.frame = frame;
        this.dialog = dialog;
        init();
    }

    public TechnicsSearchPanel(JDialog dialog, NewTechnicsPart frame, Technics technics) {
        this.frame = frame;
        this.dialog = dialog;
        this.technics = technics;
        init();
    }

    private void init() {
        initLookAndFeel();
        initDimension();
        initComponents();
        initLayout();
        initActions();
        loadInitDatas();
    }

    private void initLookAndFeel() {

    }

    private void initDimension() {

    }

    private void initComponents() {
        mainPanel = new JPanel();
        topPanel = new JPanel();
        middlePanel = new JPanel();
        topMainPanel = new JPanel();
        middleMainPanel = new JPanel();

        nameLabel = new JLabel();
        numberLabel = new JLabel();
        typeLabel = new JLabel();

        jComboBox1 = new JComboBox();

        searchButton = new JButton();

        number = new JTextField();
        name = new JTextField();
//        openButton = new JButton();
        sureButton = new JButton();
        borrowTechnicsButton = new JButton();
        cancelButton = new JButton();
        clearButton = new JButton();

        jScrollPane = new JScrollPane();
        jTable = new JTable();
    }

    private void initLayout() {
        number.setPreferredSize(new Dimension(400, 25));
        name.setPreferredSize(new Dimension(400, 25));
        nameLabel.setPreferredSize(new Dimension(75, 25));
        numberLabel.setPreferredSize(new Dimension(75, 25));
        jComboBox1.setPreferredSize(new Dimension(200, 25));
        typeLabel.setPreferredSize(new Dimension(75, 25));

        searchButton.setPreferredSize(new Dimension(90, 25));

        clearButton.setPreferredSize(new Dimension(90, 25));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = new Insets(10, 0, 5, 5);
        c.gridx = 1;
        c.gridy = 1;
        topMainPanel.setLayout(new GridBagLayout());
        topMainPanel.add(numberLabel, c);
        c.gridx = 2;
        topMainPanel.add(number, c);
        c.gridx = 3;
        c.insets = new Insets(10, 3, 5, 5);
        topMainPanel.add(searchButton, c);

        c.insets = new Insets(10, 0, 5, 5);
        c.gridy = 2;
        c.gridx = 1;
        topMainPanel.add(nameLabel, c);
        c.gridx = 2;
        topMainPanel.add(name, c);
        c.gridx = 3;
        c.insets = new Insets(10, 3, 5, 5);
        topMainPanel.add(clearButton, c);

        c.insets = new Insets(10, 0, 5, 5);
        c.gridy = 3;
        c.gridx = 1;
        topMainPanel.add(typeLabel, c);

        jComboBox1.setPreferredSize(new Dimension(150, 25));
        c.insets = new Insets(10, 0, 5, 5);
        c.gridx = 2;
        topMainPanel.add(jComboBox1, c);

        topPanel.add(topMainPanel);

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = new Insets(10, 0, 5, 5);
        c.gridx = 1;
        c.gridy = 1;

        middleMainPanel.setLayout(new GridBagLayout());
        jTable.setRowHeight(23);
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable.getTableHeader().setPreferredSize(new Dimension(20, 25));
        jScrollPane.setViewportView(jTable);
        jScrollPane.setPreferredSize(new Dimension(590, 270));
        middleMainPanel.add(jScrollPane, c);

        c.gridy = 2;

        sureButton.setPreferredSize(new Dimension(90, 25));
//        openButton.setPreferredSize(new Dimension(90, 25));
        borrowTechnicsButton.setPreferredSize(new Dimension(90, 25));
        cancelButton.setPreferredSize(new Dimension(90, 25));

//        c.insets = new Insets(30, 190, 5, 0);
//        middleMainPanel.add(openButton, c);

        c.insets = new Insets(30, 290, 5, 0);
        middleMainPanel.add(borrowTechnicsButton, c);

        c.insets = new Insets(30, 390, 5, 0);
        middleMainPanel.add(sureButton, c);

        c.insets = new Insets(30, 490, 5, 20);
        middleMainPanel.add(cancelButton, c);

        middlePanel.add(middleMainPanel);

        mainPanel.setLayout(new BorderLayout(1, 2));
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(middlePanel, BorderLayout.CENTER);
        if (technics == null) {
            borrowTechnicsButton.setVisible(false);
        } else {
            borrowTechnicsButton.setVisible(true);
        }

        this.add(mainPanel);
    }

    private DefaultTableModel getModel(Object[][] tableValue) {
        DefaultTableModel model = new DefaultTableModel(tableValue,
                new String[]{"", "工艺编号", "工艺名称", "工艺类型"}) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        return model;
    }

    private DefaultTableModel generatorModel(List<TempObject> list) {
        Object[][] tableValue = null;
        if (list == null || list.size() == 0) {
            tableValue = new Object[0][4];
        } else {
            tableValue = new Object[list.size()][4];
            for (int i = 0; i < list.size(); i++) {
                tableValue[i][0] = convertNull(list.get(i).getOid());
                tableValue[i][1] = convertNull(list.get(i).getNumber());
                tableValue[i][2] = convertNull(list.get(i).getName());
                tableValue[i][3] = convertNull(list.get(i).getType());
            }
        }
        return getModel(tableValue);
    }

    private void loadTable() {
        TableColumn column = jTable.getColumnModel().getColumn(0);
        column.setMaxWidth(0);
        column.setMinWidth(0);
        column.setPreferredWidth(0);
        column.setWidth(0);
    }

    private void initActions() {

        jTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 1 && e.getClickCount() == 2) {
                    openTechnics();
                }
            }

        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String numberValue = CommonUtil.trim(number.getText());
                String nameValue = CommonUtil.trim(name.getText());
                String type = String.valueOf(jComboBox1.getSelectedIndex());
                if (numberValue.equals("") && nameValue.equals("")) {
//					SwingUtil.showMessageDialog("请输入工艺编号或者名称", "提示", 2);
                    JOptionPane.showMessageDialog(dialog, "请输入工艺编号或者名称", "提示", JOptionPane.OK_OPTION);

                    // } else if (jComboBox1.getSelectedIndex() == 0) {
                    // SwingUtil.showMessageDialog("请选择工艺类型", "提示", 2);
                } else {
                    logger.debug("number：" + numberValue + " name:" + nameValue + " type:" + type);
                    List<TempObject> list = new ArrayList<TempObject>();
                    try {
                        list = TechnicsIntf.searchTechnicsAttributes(numberValue, nameValue, type);
                    } catch (RemoteException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    } catch (InvocationTargetException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    logger.debug("搜索工艺编号和名称====================" + list);
                    if (list != null) {
                        jTable.setModel(generatorModel(list));
                        loadTable();
                    }
                    if (list == null || list.size() == 0) {
//						SwingUtil.showMessageDialog("搜索结果为空", "提示", 1);
                        JOptionPane.showMessageDialog(dialog, "搜索结果为空", "提示", JOptionPane.OK_OPTION);
                    }
                }
            }
        });
        clearButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                name.setText("");
                number.setText("");
                if (jComboBox1.getSelectedObjects().length != 0) {
                    jComboBox1.setSelectedIndex(0);
                }
            }
        });
        sureButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                open();
            }
        });

//        openButton.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                open();
//            }
//        });

        borrowTechnicsButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                borrowTechnics();

            }
        });

        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                technics = null;
                dialog.dispose();
            }
        });
    }

    private void loadInitDatas() {
        searchButton.setText("搜索");
        clearButton.setText("清除条件");
        nameLabel.setText("工艺名称");
        numberLabel.setText("工艺编号");
        typeLabel.setText("工艺类型");
        sureButton.setText("打开");
//        openButton.setText("打开");
        borrowTechnicsButton.setText("借用");
        cancelButton.setText("取消");
        jTable.setModel(getModel(null));
        jTable.getTableHeader().setReorderingAllowed(false);
        //jTable.getTableHeader().setResizingAllowed(false);
        String[] technicsTypes = LoadConfig.getInstance().getTechnicsType()[1];
        String[] ttypes = new String[technicsTypes.length + 1];
        ttypes[0] = "请选择工艺类型";
        for (int i = 0; i < technicsTypes.length; i++) {
            ttypes[i + 1] = technicsTypes[i];
        }
        jComboBox1.setModel(new DefaultComboBoxModel(ttypes));
        loadTable();
    }

    private String convertNull(Object str) {
        return str == null ? "" : str.toString();
    }


    private void borrowTechnics() {
        technics = new Technics();
        int row = jTable.getSelectedRow();
        if (row == -1) {
            SwingUtil.showMessageDialog("请选择工艺", "提示", 2);
            technics = null;
        } else {
            String oid = (String) jTable.getValueAt(row, 0);
            String technicsNumber = (String) jTable.getValueAt(row, 1);
            String technicsName = (String) jTable.getValueAt(row, 2);
            String technicsType = (String) jTable.getValueAt(row, 3);
            technics.setTechnicsName(technicsName);
            technics.setTechnicsNumber(technicsNumber);
            technics.setTechnicsType(technicsType);
            technics.setOid(oid);
            dialog.dispose();
        }
    }

    public Technics getBorrowTechnics() {
        return technics;
    }

    private void open() {
        Document doc;
        try {
            int row = jTable.getSelectedRow();
            if (row == -1) {
                SwingUtil.showMessageDialog("请选择工艺", "提示", 2);
            } else {
                String technicsOid = (String) jTable.getValueAt(row, 0);
                byte[] pbomBytes = TechnicsIntf.getPbomBytesByTechnicsOid(technicsOid);
                if (pbomBytes != null) {
                    XWTreeNode rootNode = (XWTreeNode) frame.xwPartTreePanel.getTree().getModel().getRoot();
                    if (rootNode.getChildCount() > 1) {
                        XWTreeNode node = (XWTreeNode) rootNode.getChildAt(1);
                        rootNode.remove(node);
                    }
                    doc = BomXMLUtil.getDocument(pbomBytes);
                    XWTreeObject partObject = new XWTreeObjectForSearchTech(doc, technicsOid);
                    rootNode.addChild(partObject);
                    XWTreeNode searchNode = (XWTreeNode) rootNode.getChildAt(1);
                    if (searchNode != null) {
                        expandAllNode(searchNode);
                    }
//                    XWTreeNode techNode = getTechnicTreeNode(searchNode);
//                    TreePath treePath = new TreePath(techNode.getPath());
                    frame.xwPartTreePanel.getTree().updateUI();
                    dialog.dispose();
                } else {
                    SwingUtil.showMessageDialog("获取PBOM的XML文件失败！", "提示", 2);
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void expandAllNode(XWTreeNode xwTreeNode) {
        if (xwTreeNode != null) {
            XWTreeObject xo = xwTreeNode.getObject();
            if (((xo instanceof XWPartTreeObjectForSearchTech)) || ((xo instanceof XWTreeObjectForSearchTech))) {
                xwTreeNode.expand();
                TreePath treePath = new TreePath(xwTreeNode.getPath());
                frame.xwPartTreePanel.getTree().expandPath(treePath);
                for (int i = 0; i < xwTreeNode.getChildCount(); i++) {
                    XWTreeNode child = (XWTreeNode) xwTreeNode.getChildAt(i);
                    expandAllNode(child);
                }
            }
        }
    }

    public XWTreeNode getTechnicTreeNode(XWTreeNode xwTreeNode) {
        XWTreeNode childNode = null;
        XWTreeObject xwTreeObject;
        for (int i = 0; i < xwTreeNode.getChildCount(); i++) {
            childNode = (XWTreeNode) xwTreeNode.getChildAt(i);
            xwTreeObject = childNode.getObject();
            if (xwTreeObject instanceof TechnicsMessageTreeObject) {
                return childNode;
            } else {
                return getTechnicTreeNode(childNode);
            }
        }
        return null;
    }

    public List<TreePath> getTreePathList(XWTreeNode techNode, List<TreePath> treePathList) {
        XWTreeNode parentNode = (XWTreeNode) techNode.getParent();
        TreePath treePath = new TreePath(techNode.getPath());
        treePathList.add(treePath);
        XWTreeObject xwTreeObject = parentNode.getObject();
        if (xwTreeObject instanceof XWTreeObjectForSearchTech) {
            return treePathList;
        }
        return getTreePathList(parentNode, treePathList);
    }

    private void openTechnics() {
        int row = jTable.getSelectedRow();
        if (row == -1) {
            SwingUtil.showMessageDialog("请选择工艺", "提示", 2);
        } else {
            Object oid = jTable.getValueAt(row, 0);
            String oidValue = oid == null ? "" : oid.toString().trim();
            List<Object> returnList = new ArrayList<Object>();
            try {
                returnList = TechnicsIntf.searchTechnics(oidValue);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("returnList= " + returnList);
            if (returnList == null || returnList.size() == 0) {
                JOptionPane.showMessageDialog(null, "工艺下载出错！", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            List<Object> list = (List<Object>) returnList.get(0);
            String technicsFolderName = (String) list.get(0);
            String versionId = (String) list.get(3);
            if (technicsFolderName.toLowerCase().endsWith(".zip")) {
                technicsFolderName = technicsFolderName.substring(0, technicsFolderName.length() - 4);
            }
            logger.debug("历史文件路径====" + technicsFolderName);
            byte[] bytes = (byte[]) list.get(1);
            String folderName = null;
            try {
                folderName = WorkSpaceUtil.getTempRootPath() + "\\" + technicsFolderName;
                File file = new File(folderName);
                if (file.exists()) {
                    FilesUtil.delFolder(folderName);
                }
                file.mkdir();
                TechnicsReleaseUtil.unZip(bytes, folderName);
                File xmlFile = new File(folderName + "\\" + technicsFolderName + ".xml");
                Document technicsDocument = null;
                if (xmlFile.exists()) {
                    technicsDocument = XmlUtility.getDocument(xmlFile);
                }
                if (technicsDocument != null) {
                    Element technicsElement = XmlUtility.getTechnicsElement(technicsDocument);
                    XmlUtility.setAttributeValue(technicsElement, "version", versionId);
                    new NewTechnicsHistoryView(technicsDocument, frame, null, folderName, "查看工艺");
                }
            } catch (Exception e1) {
                if (folderName != null) {
                    File file = new File(folderName);
                    if (file.exists())
                        FilesUtil.delFolder(folderName);
                }
                JOptionPane.showMessageDialog(null, "将相关工艺版本下载到本地时出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
                e1.printStackTrace();
            }

//			dialog.dispose();
        }

    }
}
