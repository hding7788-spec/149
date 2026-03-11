package com.glaway.mpm.view;

import com.glaway.mpm.model.TempObject;
import com.glaway.mpm.qmIntf.technics.BorrowTechnicsSearchDialog;
import com.glaway.mpm.qmIntf.technics.RelateTypicalTechnicDialog;
import com.glaway.mpm.qmIntf.technics.entity.Technics;
import com.glaway.mpm.util.*;
import com.glaway.mpm.wcIntf.ResourceIntf;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.List;

/**
 * 典型/通用工艺
 *
 * @author Administrator
 */
public class BorrowThecnicsJPanel extends JPanel {
    private Container parentPanel;

    JPanel panel = new JPanel();

    private DefaultTableModel tableModel = new DefaultTableModel() {
        private static final long serialVersionUID = 1L;

        public boolean isCellEditable(int row, int column) {
            if (column == 5 || column == 6) return true;
            return false;
        }
    };


    private JTable table = new JTable(tableModel);

    private JButton addJButton = new IconButton("/images/button_add.png", "添加");
    private JButton deleteJButton = new IconButton("/images/button_remove.png", "移除");
    private JButton lookJButton = new IconButton("/images/button_open.png", "查看");
    private JButton saveJButton = new IconButton("/images/button_save.png", "保存");
    private JButton relatedTypicalTechButton = new IconButton("/images/button_save.png", "关联典型工艺");
    private static String imagePath = "";
    private ParameterTableModelListener modelListener;

    public BorrowThecnicsJPanel(Container parentPanel) {
        NewTechnicsPart.startAnimFrame.setHeaderMessage("加载典型/通用工艺面板");
        this.parentPanel = parentPanel;
        jbInit();
        setName("BorrowThecnics");
        NewTechnicsPart.startAnimFrame.setHeaderMessage("完成加载典型/通用工艺面板");
    }

    public void setTabTitle() {
//		int i = tableModel.getRowCount();

    }

    private void jbInit() {
    	modelListener = new ParameterTableModelListener();
    	tableModel.addTableModelListener(modelListener);
        addJButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addTechnics();
            }
        });

        deleteJButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteTechnics();
            }
        });
        lookJButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int j = table.getSelectedRowCount();
                if (j == 0) {
                    JOptionPane.showMessageDialog(null, "请选择需要打开的典型/通用工艺！", "提示", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                openTechnics2();
            }
        });
        saveJButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Element techele = XmlUtility.getTechnicsElement(((NewTechnicsPart) parentPanel).getCurrentTechnics());
                Element borrowTechnics = XmlUtility.getBorrowTechnicsElement(techele);
                List borrowTechnicsList = XmlUtility.getBorrowTechnics(techele);
                for (int i = 0; i < borrowTechnicsList.size(); i++) {
                    Element object = (Element) borrowTechnicsList.get(i);
                    borrowTechnics.remove(object);
                }
                List<Element> elements = getElements();
                for (int i = 0; i < elements.size(); i++) {
                    Element element = elements.get(i);
                    borrowTechnics.add(element);
                }
                ((NewTechnicsPart) parentPanel).saveProcess(techele);
                JOptionPane.showMessageDialog(null, "保存成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        relatedTypicalTechButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NewTechnicsPart frame = null;
                if (parentPanel instanceof NewTechnicsPart) {
                    frame = (NewTechnicsPart) parentPanel;
                } else if (parentPanel instanceof TechnicsStepJPanel_XW) {
                    TechnicsStepJPanel_XW stepPanel = (TechnicsStepJPanel_XW) parentPanel;
                    frame = (NewTechnicsPart) stepPanel.getFrame();
                } else if (parentPanel instanceof TechnicsPaceJDialog) {
                    TechnicsPaceJDialog paceDialog = (TechnicsPaceJDialog) parentPanel;
                    frame = (NewTechnicsPart) paceDialog.getFrame();
                }
                Element technicElement = XmlUtility.getTechnicsElement(frame.getCurrentTechnics());
                List<Technics> technicsList = new ArrayList<Technics>();
                Technics technics;
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String type = tableModel.getValueAt(i,2).toString();
                    if("结构化典型工艺".equals(type)){
                        technics = new Technics();
                        technics.setTechnicsNumber(tableModel.getValueAt(i,0).toString());
                        technics.setDocNumber(tableModel.getValueAt(i,4).toString());
                        technics.setVersion(tableModel.getValueAt(i,7).toString());
                        technicsList.add(technics);
                    }
                    if("典型工艺".equals(type)){
                        String pplanNumber = tableModel.getValueAt(i,0).toString();
                        try {
                            TempObject tempObject = TechnicsIntf.getSamePplanNumberDxPlan(pplanNumber);
                            if(tempObject != null){
                                technics = new Technics();
                                technics.setTechnicsNumber(tempObject.getNumber());
                                technics.setDocNumber(tempObject.getDocNumber());
                                technics.setVersion(tempObject.getVersion());
                                technicsList.add(technics);
                            }
                        } catch (InvocationTargetException e1) {
                            e1.printStackTrace();
                        } catch (RemoteException e1) {
                            e1.printStackTrace();
                        }
                    }
                }

                RelateTypicalTechnicDialog relateTypicalTechnicDialog = new RelateTypicalTechnicDialog(frame,technicsList,technicElement);
                relateTypicalTechnicDialog.showDialog();
            }
        });

        panel.setLayout(new GridBagLayout());
        panel.add(addJButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                5, 5, 0, 5), 0, 0));
        panel.add(deleteJButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                5, 5, 0, 5), 0, 0));
        panel.add(lookJButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                5, 5, 0, 5), 0, 0));
        /*panel.add(saveJButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                5, 5, 0, 5), 0, 0));*/
        panel.add(relatedTypicalTechButton, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                5, 5, 0, 5), 0, 0));
        setLayout(new GridBagLayout());
        tableModel.addColumn("工艺编号");
        tableModel.addColumn("工艺名称");
        tableModel.addColumn("工艺类型");
        tableModel.addColumn("oid");
        tableModel.addColumn("文档编号");
        tableModel.addColumn("责任部门");
        tableModel.addColumn("备注");
        tableModel.addColumn("版本");

        Map workShop = ResourceIntf.getWorkShops();
        JComboBox box = new JComboBox();
        box.addItem("");
        if (workShop != null && workShop.size() > 0) {
            List<String> allList = new ArrayList<String>();
            Collection coll = workShop.values();
            Iterator it = coll.iterator();
            while (it.hasNext()) {
                allList.add(String.valueOf(it.next()));
            }
            Collections.sort(allList);
            for (String str : allList) {
                box.addItem(str);
            }
        }
        TableColumn column = table.getColumn("责任部门");
        TableColumnModel model = table.getColumnModel();
        model.getColumn(1).setCellEditor(new DefaultCellEditor(box));
        column.setCellEditor(new DefaultCellEditor(box));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        setHideColumn(3);
        setHideColumn(4);
        setHideColumn(7);
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(table.getTableHeader(), BorderLayout.PAGE_START);
        p.add(table, BorderLayout.CENTER);

        JScrollPane pane = new JScrollPane(p,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//
        if (parentPanel instanceof TechnicsStepJPanel_XW || parentPanel instanceof TechnicsPaceJDialog) {
            setLayout(new GridBagLayout());
            add(pane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                    0, 0, 0, 0), 0, 0));
//
            add(panel, new GridBagConstraints(1, 0, 1, 1, 0, 1.0,
                    GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(
                    0, 0, 0, 0), 0, 0));
        } else {
//		pane = new JScrollPane(table);
            JPanel leftPanel = new JPanel();
            JPanel totalPanel = new JPanel();
            GroupLayout jPanel2Layout = new GroupLayout(panel);
            panel.setLayout(jPanel2Layout);
            jPanel2Layout.setHorizontalGroup(
                    jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                            .addComponent(addJButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(deleteJButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lookJButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            //.addComponent(saveJButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(relatedTypicalTechButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    )
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            jPanel2Layout.setVerticalGroup(
                    jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGap(26, 26, 26)
                                    .addComponent(addJButton)
                                    .addGap(26, 26, 26)
                                    .addComponent(deleteJButton)
                                    .addGap(28, 28, 28)
                                    .addComponent(lookJButton)
                                    .addGap(26, 26, 26)
                                    //.addComponent(saveJButton)
                                    .addGap(26, 26, 26)
                                    .addComponent(relatedTypicalTechButton)
                                    .addContainerGap(225, Short.MAX_VALUE))
            );
            GroupLayout jPanel1Layout = new GroupLayout(leftPanel);
            leftPanel.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                    jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(pane, GroupLayout.PREFERRED_SIZE, 766, GroupLayout.PREFERRED_SIZE)
            );
            jPanel1Layout.setVerticalGroup(
                    jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(pane, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            );

            GroupLayout jPanel3Layout = new GroupLayout(totalPanel);
            totalPanel.setLayout(jPanel3Layout);
            jPanel3Layout.setHorizontalGroup(
                    jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGap(0, 917, Short.MAX_VALUE)
                            .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addContainerGap()
                                            .addComponent(leftPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addContainerGap()))
            );
            jPanel3Layout.setVerticalGroup(
                    jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGap(0, 407, Short.MAX_VALUE)
                            .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addContainerGap()
                                            .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(leftPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                            .addContainerGap()))
            );

            GroupLayout layout = new GroupLayout(this);
            this.setLayout(layout);
            layout.setHorizontalGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                    .addGap(0, 0, 0)
                                    .addComponent(totalPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            layout.setVerticalGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                    .addGap(0, 0, 0)
                                    .addComponent(totalPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }
    }

    private void openTechnics2() {
        int row = table.getSelectedRow();
        if (row == -1) {
            SwingUtil.showMessageDialog("请选择工艺", "提示", 2);
        } else {
            Object obj = table.getValueAt(row, 0);
            String number = obj == null ? "" : obj.toString().trim();
            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            try {
                byte[] bytes = null;
                try {
                    bytes = TechnicsIntf.getPrintPdf(number);
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (bytes != null) {
                    String tempFile = WorkSpaceUtil.getWorkSpace() + File.separator + "technics" + File.separator + number + ".pdf";
                    fos = new FileOutputStream(tempFile);
                    bos = new BufferedOutputStream(fos);

                    bos.write(bytes);

                    bos.close();
                    fos.close();

                    openFile(tempFile);
                } else {
                    SwingUtil.showMessageDialog("没有找到主内容为pdf的工艺文件", "提示", 2);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void openFile(String filePath) throws IOException {
        Runtime.getRuntime().exec("rundll32 url.dll FileProtocolHandler   " + filePath);
    }

    private void openTechnics() {
        int row = table.getSelectedRow();
        if (row == -1) {
            SwingUtil.showMessageDialog("请选择工艺", "提示", 2);
        } else {
            Object oid = table.getValueAt(row, 3);
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
                    new NewTechnicsHistoryView(technicsDocument, (NewTechnicsPart) parentPanel, null, folderName, "查看工艺");
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

    public List<Element> getElements() {
        Vector<Element> elements = new Vector<Element>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (isRowNull(i))
                continue;
            Element element = DocumentHelper.createElement("borrowTechnics");
            element.setAttributeValue("bsoID", "");
            element.setAttributeValue("oid", "");
            element.setAttributeValue("technicsNumber", "");
            element.setAttributeValue("technicsName", "");
            element.setAttributeValue("technicsType", "");
            element.setAttributeValue("docNumber", "");

            XmlUtility.setAttributeValue(element, "bsoID", "");
            XmlUtility.setAttributeValue(element, "technicsNumber", (String) tableModel.getValueAt(i, 0));
            XmlUtility.setAttributeValue(element, "technicsName", (String) tableModel.getValueAt(i, 1));
            XmlUtility.setAttributeValue(element, "technicsType", (String) tableModel.getValueAt(i, 2));
            XmlUtility.setAttributeValue(element, "technicsVersion", (String) tableModel.getValueAt(i, 7));
            XmlUtility.setAttributeValue(element, "oid", (String) tableModel.getValueAt(i, 3));
            XmlUtility.setAttributeValue(element, "docNumber", (String) tableModel.getValueAt(i, 4));
            XmlUtility.setAttributeValue(element, "DEPT", (String) tableModel.getValueAt(i, 5));
            XmlUtility.setAttributeValue(element, "comment", (String) tableModel.getValueAt(i, 6));
            elements.add(element);
        }
        return elements;
    }


    private Element createBorrowTechnicsElement(int row) {
        Element element = DocumentHelper.createElement("borrowTechnics");
        element.setAttributeValue("bsoID", "");
        element.setAttributeValue("oid", "");
        element.setAttributeValue("technicsNumber", "");
        element.setAttributeValue("technicsName", "");
        element.setAttributeValue("technicsType", "");
        element.setAttributeValue("docNumber", "");


        XmlUtility.setAttributeValue(element, "bsoID", "");
        XmlUtility.setAttributeValue(element, "technicsNumber", (String) tableModel.getValueAt(row, 0));
        XmlUtility.setAttributeValue(element, "technicsName", (String) tableModel.getValueAt(row, 1));
        XmlUtility.setAttributeValue(element, "technicsType", (String) tableModel.getValueAt(row, 2));
        XmlUtility.setAttributeValue(element, "oid", (String) tableModel.getValueAt(row, 3));
        XmlUtility.setAttributeValue(element, "docNumber", (String) tableModel.getValueAt(row, 4));
        XmlUtility.setAttributeValue(element, "DEPT", (String) tableModel.getValueAt(row, 5));
        XmlUtility.setAttributeValue(element, "comment", (String) tableModel.getValueAt(row, 6));
        XmlUtility.setAttributeValue(element, "technicsVersion", (String) tableModel.getValueAt(row, 7));
        return element;
    }

    public void setTableValues(Vector<Element> vec) {
        clearTable();
        tableModel.removeTableModelListener(modelListener);
        for (int i = 0; i < vec.size(); i++) {
            Vector vector = new Vector();
            for (int j = 0; j < table.getColumnCount(); j++) {
                vector.add("");
            }
            tableModel.addRow(vector);
            Element element = vec.get(i);
            String thechnicsNumber = element.attributeValue("technicsNumber");
            String thechnicsName = element.attributeValue("technicsName");
            tableModel.setValueAt(thechnicsNumber, i, 0);
            tableModel.setValueAt(thechnicsName, i, 1);
            tableModel.setValueAt(element.attributeValue("technicsType"), i, 2);
            tableModel.setValueAt(element.attributeValue("oid"), i, 3);
            tableModel.setValueAt(element.attributeValue("docNumber"), i, 4);
            tableModel.setValueAt(element.attributeValue("DEPT"), i, 5);
            tableModel.setValueAt(element.attributeValue("comment"), i, 6);
            tableModel.setValueAt(element.attributeValue("technicsVersion"), i, 7);


        }
        tableModel.addTableModelListener(modelListener);
    }

    private void setHideColumn(int index) {
        table.getColumnModel().getColumn(index).setMinWidth(0);
        table.getColumnModel().getColumn(index).setMaxWidth(0);
    }

    public boolean judgeFileType(String name, String suffix) {
        if (name == null) {
            return false;
        }
        for (String str : suffix.split(",")) {
            if (name.endsWith(str)) {
                return true;
            } else {
                continue;
            }
        }
        return false;
    }


    private void addTechnics() {
        Technics technics = new Technics();
        NewTechnicsPart frame = null;
        if (parentPanel instanceof NewTechnicsPart) {
            frame = (NewTechnicsPart) parentPanel;
        } else if (parentPanel instanceof TechnicsStepJPanel_XW) {
            TechnicsStepJPanel_XW stepPanel = (TechnicsStepJPanel_XW) parentPanel;
            frame = (NewTechnicsPart) stepPanel.getFrame();
        } else if (parentPanel instanceof TechnicsPaceJDialog) {
            TechnicsPaceJDialog paceDialog = (TechnicsPaceJDialog) parentPanel;
            frame = (NewTechnicsPart) paceDialog.getFrame();
        }
        BorrowTechnicsSearchDialog dia = new BorrowTechnicsSearchDialog(frame, technics,parentPanel);
        technics = dia.showDialog();

        if (technics == null || technics.getTechnicsNumber() == null || "".equals(technics.getTechnicsNumber())) return;

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String number = tableModel.getValueAt(i, 0).toString();
            if (technics.getTechnicsNumber().equals(number)) {
                JOptionPane.showMessageDialog(null, "相同的工艺已经添加！", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        int row = addToTable(technics);

        setTabTitle();
        try {
            writeFile2Xml(technics, row);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void writeFile2Xml(Technics technics, int row) throws Exception {
        NewTechnicsPart newTechnicsPart = null;
        Element techele = null;
        Element ele = null;
        if (parentPanel instanceof NewTechnicsPart) {
            newTechnicsPart = (NewTechnicsPart) parentPanel;
            techele = XmlUtility.getTechnicsElement(newTechnicsPart.getCurrentTechnics());
            ele = techele;
        } else if (parentPanel instanceof TechnicsStepJPanel_XW) {
            TechnicsStepJPanel_XW stepPanel = (TechnicsStepJPanel_XW) parentPanel;
            newTechnicsPart = (NewTechnicsPart) stepPanel.getFrame();
            techele = XmlUtility.getTechnicsElement(newTechnicsPart.getCurrentTechnics());
            ele = stepPanel.getStepElement();
        } else if (parentPanel instanceof TechnicsPaceJDialog) {
            TechnicsPaceJDialog paceDialog = (TechnicsPaceJDialog) parentPanel;
            newTechnicsPart = (NewTechnicsPart) paceDialog.getFrame();
            techele = XmlUtility.getTechnicsElement(newTechnicsPart.getCurrentTechnics());
            ele = paceDialog.getPaceElement();
        }
        if (techele == null) return;
        Element borrowTechnics = XmlUtility.getBorrowTechnicsElement(ele);
        borrowTechnics.add(this.createBorrowTechnicsElement(row));
        newTechnicsPart.saveProcess(techele);
    }


    private int addToTable(Technics technics) {
        addOneRow();
        int rowCount = table.getRowCount();
        tableModel.setValueAt(technics.getTechnicsNumber(), rowCount - 1, 0);
        tableModel.setValueAt(technics.getTechnicsName(), rowCount - 1, 1);
        tableModel.setValueAt(technics.getTechnicsType(), rowCount - 1, 2);
        tableModel.setValueAt(technics.getOid(), rowCount - 1, 3);
        tableModel.setValueAt(technics.getDocNumber(), rowCount - 1, 4);
        tableModel.setValueAt(technics.getVersion(), rowCount - 1, 7);
        return rowCount - 1;
    }

    private void addOneRow() {
        Vector vector = new Vector();
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            vector.add("");
        }
        tableModel.addRow(vector);
    }

    private void deleteTechnics() {
        if (table.getCellEditor() != null) {
            table.getCellEditor().stopCellEditing();
        }
        int row = table.getSelectedRow();
        if (row == -1) {
            return;
        }
        NewTechnicsPart frame = null;
        Element techele = null;
        Element ele = null;
        if (parentPanel instanceof NewTechnicsPart) {
            frame = (NewTechnicsPart) parentPanel;
            techele = XmlUtility.getTechnicsElement(frame.getCurrentTechnics());
            ele = techele;
        } else if (parentPanel instanceof TechnicsStepJPanel_XW) {
            TechnicsStepJPanel_XW stepPanel = (TechnicsStepJPanel_XW) parentPanel;
            frame = (NewTechnicsPart) stepPanel.getFrame();
            techele = XmlUtility.getTechnicsElement(frame.getCurrentTechnics());
            ele = stepPanel.getStepElement();
        } else if (parentPanel instanceof TechnicsPaceJDialog) {
            TechnicsPaceJDialog paceDialog = (TechnicsPaceJDialog) parentPanel;
            frame = (NewTechnicsPart) paceDialog.getFrame();
            techele = XmlUtility.getTechnicsElement(frame.getCurrentTechnics());
            ele = paceDialog.getPaceElement();
        }
        if (techele == null) {
            return;
        }

        String borrowTechnicsNumber = (String) tableModel.getValueAt(row, 4);
        tableModel.removeRow(row);
        Element borrowTechnics = XmlUtility.getBorrowTechnicsElement(ele);
        List borrowTechnicsList = XmlUtility.getBorrowTechnics(ele);
        if (borrowTechnicsList != null) {
            for (int j = 0; j < borrowTechnicsList.size(); j++) {
                Element e = (Element) borrowTechnicsList.get(j);
                if (borrowTechnicsNumber.equals(XmlUtility.getAttributeValue(e, "docNumber"))) {
                    borrowTechnics.remove(e);
                    removeRelatedTypicalTech(ele, borrowTechnicsNumber);
                    frame.saveProcess(techele);
                }
            }
        }
        setTabTitle();
    }

    public static void removeRelatedTypicalTech(Element technicsElement, String technicsNumber) {
        List<Element> stepElements = technicsElement.selectNodes("steps/QMProcedureInfo");
        List<String> nameList = new ArrayList<String>();
        for (Element stepElement : stepElements) {
            String relatedTypicalNumber = stepElement.attributeValue("relatedTypicalNumber");
            if (technicsNumber.equals(relatedTypicalNumber)) {
                List<Attribute> attributes = stepElement.attributes();
                for (Attribute attribute : attributes) {
                    String name = attribute.getName();
                    if (name.contains("relatedTypical")) {
                        nameList.add(name);
                    }
                }
                for (String name : nameList) {
                    stepElement.remove(stepElement.attribute(name));
                }
            }
        }
    }

    private boolean isRowNull(int row) {
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            if (!tableModel.getValueAt(row, i).equals(""))
                return false;
        }
        return true;
    }

    public void clearTable() {
        tableModel.setRowCount(0);
    }

    public void setUIEnabled(boolean b) {
        table.setEnabled(b);
        addJButton.setEnabled(b);
        deleteJButton.setEnabled(b);
        lookJButton.setEnabled(b);
        relatedTypicalTechButton.setEnabled(b);
    }
	public class ParameterTableModelListener implements TableModelListener {

		@Override
		public void tableChanged(TableModelEvent e) {
			int row = e.getFirstRow();
			int col = e.getColumn();
			if (row != -1 && (col == 5 || col == 6)) {
				NewTechnicsPart newTechnicsPart = null;
		        Element techele = null;
		        Element ele = null;
		        if (parentPanel instanceof NewTechnicsPart) {
		            newTechnicsPart = (NewTechnicsPart) parentPanel;
		            techele = XmlUtility.getTechnicsElement(newTechnicsPart.getCurrentTechnics());
		            ele = techele;
		        } else if (parentPanel instanceof TechnicsStepJPanel_XW) {
		            TechnicsStepJPanel_XW stepPanel = (TechnicsStepJPanel_XW) parentPanel;
		            newTechnicsPart = (NewTechnicsPart) stepPanel.getFrame();
		            techele = XmlUtility.getTechnicsElement(newTechnicsPart.getCurrentTechnics());
		            ele = stepPanel.getStepElement();
		        } else if (parentPanel instanceof TechnicsPaceJDialog) {
		            TechnicsPaceJDialog paceDialog = (TechnicsPaceJDialog) parentPanel;
		            newTechnicsPart = (NewTechnicsPart) paceDialog.getFrame();
		            techele = XmlUtility.getTechnicsElement(newTechnicsPart.getCurrentTechnics());
		            ele = paceDialog.getPaceElement();
		        }
				if(newTechnicsPart != null){
					Element borrowTechnics = XmlUtility.getBorrowTechnicsElement(ele);
					List borrowTechnicsList = XmlUtility.getBorrowTechnics(ele);
					for (int i = 0; i < borrowTechnicsList.size(); i++) {
						Element object = (Element) borrowTechnicsList.get(i);
						borrowTechnics.remove(object);
					}
					List<Element> elements = getElements();
					for (int i = 0; i < elements.size(); i++) {
						Element element = elements.get(i);
						borrowTechnics.add(element);
					}
					newTechnicsPart.saveProcess(techele);
				}
			}

		}
	}
    public void setRelatedTypicalTechButtonEnable(boolean b){
        relatedTypicalTechButton.setVisible(b);
    }
}