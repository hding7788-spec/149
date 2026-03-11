package com.glaway.mpm.erp.pbom;

import com.glaway.mpm.erp.AbstractERPDialog;
import com.glaway.mpm.erp.DefaultZTableFactory;
import com.glaway.mpm.erp.ZTableOp;
import com.glaway.mpm.model.UploadTechnics;
import com.glaway.mpm.pdf.PDFBuilder;
import com.glaway.mpm.util.*;
import com.glaway.mpm.view.*;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 填写物料编码
 * 外购件匹配ERP物资编码界面
 *
 * @author LongXiuChuan
 */
public class MatchClbmDialog extends AbstractERPDialog {

    private static final long serialVersionUID = 1L;

    private CmPbomErpWzkPanel wzkPanel = new CmPbomErpWzkPanel(1, 0);

    private ZTableOp partTableOp;
    private String[] partTableHeader = null;
    private String[][] partTableBody;
    private int[] partTableColWidth;
    private int[] partTableEditCols;
    private int[] partTableHideCols;
    private JButton saveButton;
    private JButton deleteButton;
    private JButton deleteAllButton;
    private JButton moveUpButton;
    private JButton moveDownButton;
    private JPanel centerPanel;
    private JPanel bottomPanel;
    private List<XWTreeNode> selPbomNodes;

    private NewTechnicsPart frame;
    private XWTreeNode node;


    public MatchClbmDialog(NewTechnicsPart frame, List selPbomNodes, String title, XWTreeNode node) {
        super(frame);
        this.setTitle(title);
        this.selPbomNodes = selPbomNodes;
        this.frame = frame;
        this.node = node;

        loadInitDatas();

        initDimension();
        initComponents();

        initActions();
        initLayout();
        this.setModal(false);
        this.setVisible(true);
        this.setResizable(false);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());
    }

    protected void initActions() {
        wzkPanel.getTableOp().getZTable().addMouseListener(new MouseAdapter() {
            int row = -1;
            TableModel wztm, parttm;

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                wztm = wzkPanel.getTableOp().getTableModel();
                parttm = partTableOp.getTableModel();
                row = wzkPanel.getTableOp().getZTable().getSelectedRow();
                if (row == -1) {
                    return;
                }
                if (e.getClickCount() == 2) {
                    int prow = partTableOp.getZTable().getSelectedRow();
                    if (prow == -1) {
                        JOptionPane.showMessageDialog(frame, "请选择part表，然后在设置值");
                        return;
                    }
                    parttm = partTableOp.getTableModel();
                    parttm.setValueAt(wztm.getValueAt(row, 1), prow, 2);
                    parttm.setValueAt(wztm.getValueAt(row, 2), prow, 3);
                    //parttm.setValueAt(wztm.getValueAt(row, 3), prow, 4);
                    //parttm.setValueAt(wztm.getValueAt(row, 4), prow, 5);
                    parttm.setValueAt(wztm.getValueAt(row, 3), prow, 7);
                    parttm.setValueAt(wztm.getValueAt(row, 4), prow, 8);
                    parttm.setValueAt(wztm.getValueAt(row, 5), prow, 9);
                    parttm.setValueAt(wztm.getValueAt(row, 6), prow, 10);
                    parttm.setValueAt(wztm.getValueAt(row, 7), prow, 11);
                    parttm.setValueAt(wztm.getValueAt(row, 8), prow, 12);
                    parttm.setValueAt(wztm.getValueAt(row, 9), prow, 13);
                    parttm.setValueAt(wztm.getValueAt(row, 10), prow, 14);
                    parttm.setValueAt(wztm.getValueAt(row, 11), prow, 15);
                    parttm.setValueAt(wztm.getValueAt(row, 12), prow, 16);
                    parttm.setValueAt(wztm.getValueAt(row, 13), prow, 17);
                    parttm.setValueAt(wztm.getValueAt(row, 14), prow, 18);
                    parttm.setValueAt(wztm.getValueAt(row, 15), prow, 19);

                    parttm.setValueAt(wztm.getValueAt(row, 16), prow, 20);
                    parttm.setValueAt(wztm.getValueAt(row, 17), prow, 21);


                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            JTable ptable = partTableOp.getZTable();
            DefaultTableModel dtm = (DefaultTableModel) partTableOp.getTableModel();

            @Override
            public void actionPerformed(ActionEvent e) {
                int isDelete = JOptionPane.showConfirmDialog(frame, "确定要删除所选行吗？", "确定", JOptionPane.YES_NO_OPTION);
                if (isDelete == JOptionPane.YES_OPTION) {
                    Element techEle = null;
                    XWTreeNode currentTechNode = frame.technicsTreePanel.getSelectedTreeNode();
                    XWTreeObject treeObj = currentTechNode.getObject();
                    if (treeObj instanceof XWTechnicsTreeObject) {
                        XWTechnicsTreeObject techObj = (XWTechnicsTreeObject) treeObj;
                        techEle = techObj.getTreeCellData();
                    }

                    if (techEle == null) {
                        JOptionPane.showMessageDialog(frame, "没有找到工艺数据，请选择工艺文件进行工艺定额！");
                        return;
                    }
                    Map<String, PDFBuilder.CzjPart> czjPartMap = PDFBuilder.getAllCzjMap(techEle);
                    int[] rows = ptable.getSelectedRows();
                    String message = "";
                    for (int row : rows) {
                        String partNumber = (String) ptable.getValueAt(row, 2);
                        if (czjPartMap.containsKey(partNumber)) {
                            if("".equals(message)){
                                message  = partNumber;
                            }else{
                                message = message + "," + partNumber;
                            }
                        }
                    }
                    if("".equals(message)){
                        for (int i = 0; i < rows.length; i++) {
                            dtm.removeRow(ptable.getSelectedRow());
                        }
                    }else{
                        JOptionPane.showMessageDialog(frame, message + "已参与参装，请先取消参装再进行删除！");
                        return;
                    }
//					int numrow = ptable.getSelectedRows().length;
//					for (int i = 0; i < numrow; i++) {
//
//						String partNumber = (String) ptable.getValueAt(i,2);
//						Map<String, PDFBuilder.CzjPart> czjPartMap = PDFBuilder.getAllCzjMap(techEle);
//						if(czjPartMap.containsKey(partNumber)){
//							JOptionPane.showMessageDialog(frame, partNumber + "已参与参装，请先取消参装再进行删除！");
//							return;
//						}
//						dtm.removeRow(ptable.getSelectedRow());
//					}
                }
            }
        });

        deleteAllButton.addActionListener(new ActionListener() {
            DefaultTableModel dtm = (DefaultTableModel) partTableOp.getTableModel();

            @Override
            public void actionPerformed(ActionEvent e) {
                int isDelete = JOptionPane.showConfirmDialog(frame, "确定要删除所选行吗？", "确定", JOptionPane.YES_NO_OPTION);
                if (isDelete == JOptionPane.YES_OPTION) {
                    int rowcount = dtm.getRowCount() - 1;
                    Element techEle = null;
                    XWTreeNode currentTechNode = frame.technicsTreePanel.getSelectedTreeNode();
                    XWTreeObject treeObj = currentTechNode.getObject();
                    if (treeObj instanceof XWTechnicsTreeObject) {
                        XWTechnicsTreeObject techObj = (XWTechnicsTreeObject) treeObj;
                        techEle = techObj.getTreeCellData();
                    }
                    if (techEle == null) {
                        JOptionPane.showMessageDialog(frame, "没有找到工艺数据，请选择工艺文件进行工艺定额！");
                        return;
                    }
                    while (rowcount >= 0) {
                        String partNumber = (String) dtm.getValueAt(rowcount, 2);
                        Map<String, PDFBuilder.CzjPart> czjPartMap = PDFBuilder.getAllCzjMap(techEle);
                        if (czjPartMap.containsKey(partNumber)) {
                            JOptionPane.showMessageDialog(frame, partNumber + "已参与参装，请先取消参装再进行删除！");
                            return;
                        }
                        dtm.removeRow(rowcount);
                        dtm.setRowCount(rowcount);
                        rowcount = dtm.getRowCount() - 1;
                    }
                }
            }
        });

        moveUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTableUtil.changeRowValue(true,partTableOp.getZTable());
            }
        });

        moveDownButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTableUtil.changeRowValue(false,partTableOp.getZTable());
            }
        });
        saveButton.addActionListener(new SaveWlbmListener(this, frame, partTableOp.getTableModel(), selPbomNodes));
    }

    @Override
    protected void initComponents() {

        //将零部件的材料信息设置到查询物资库的条件里
        this.wzkPanel.setInitValue(node);

        partTableOp = new DefaultZTableFactory();
        partTableOp.setColumnsEditable(partTableEditCols);
        partTableOp.setTableInfors(partTableHeader, partTableBody, partTableColWidth);
        partTableOp.setTableStyle(partTableOp.getZTable());
        partTableOp.setColumnsHidden(partTableOp.getZTable(), partTableHideCols);

        TableColumn tableColumn = partTableOp.getZTable().getColumn("*单位");
        tableColumn.setCellEditor(new DefaultCellEditor(CommonUtil.getDWJComboBox()));

        saveButton = new JButton("保 存");
        deleteButton = new JButton("删除选择行");
        deleteAllButton = new JButton("删除所有行");
        moveUpButton = new JButton("上 移");
        moveDownButton = new JButton("下 移");

        centerPanel = new JPanel();
        bottomPanel = new JPanel();
    }

    protected void loadInitDatas() {
        partTableHeader = new String[]{"序号", "图号", "存货编码", "存货名称", "设计数量", "*工艺数量", "*单位", "型号牌号", "规格", "技术条件", "生产厂家", "主计量单位", "附加条件", "螺纹规格/公称尺寸", "机械性能等级", "质量等级", "封装形式", "精度等级", "物资类别", "物资类别编码", "产品代号", "电参数特选要求", "备注"};
        partTableBody = new String[selPbomNodes.size()][partTableHeader.length];
        XWTreeNode partNode = null;
        Map<String, List<String>> map = getRecord();
        for (int i = 0; i < selPbomNodes.size(); i++) {
            partNode = selPbomNodes.get(i);
            partTableBody[i][0] = (i + 1) + "";

            XWTreeObject treeObj = partNode.getObject();
            String partNumber = "";
            String useCount = "1";
            String gysl = "1";
            String usecount_805 = "1";
            if (treeObj instanceof XWPartTreeObject) {
                XWPartTreeObject partObj = (XWPartTreeObject) treeObj;
                Element partEle = partObj.getTreeCellData();

                useCount = partEle.attributeValue("useCount");
                String gyslTemp = partEle.attributeValue("gysl");
                if (gyslTemp != null && !"".equals(gyslTemp)) {
                    gysl = gyslTemp;
                } else {
                    gysl = useCount;
                }

                partNumber = partEle.attributeValue("partNumber");
                partTableBody[i][1] = partNumber;

                usecount_805 = partEle.attributeValue("usecount_805");

                if (null == usecount_805 || "null".equals(usecount_805) || "".equals(usecount_805)) {
                    usecount_805 = useCount;
                }
            }
            if (map.keySet().contains(partNumber)) {
                List<String> list = map.get(partNumber);
                partTableBody[i][2] = list.get(1);
                partTableBody[i][3] = list.get(2);
                partTableBody[i][4] = list.get(3);
                partTableBody[i][5] = list.get(4);
                partTableBody[i][6] = list.get(5);
                partTableBody[i][7] = list.get(6);
                partTableBody[i][8] = list.get(7);
                partTableBody[i][9] = list.get(8);
                partTableBody[i][10] = list.get(9);
                partTableBody[i][11] = list.get(10);
                partTableBody[i][12] = list.get(11);
                partTableBody[i][13] = list.get(12);
                partTableBody[i][14] = list.get(13);
                partTableBody[i][15] = list.get(14);
                partTableBody[i][16] = list.get(15);
                partTableBody[i][17] = list.get(16);
                partTableBody[i][18] = list.get(17);
                partTableBody[i][19] = list.get(18);
                partTableBody[i][20] = list.get(19);
                partTableBody[i][21] = list.get(20);
                partTableBody[i][22] = list.get(21);
            } else {
                partTableBody[i][2] = "";
                partTableBody[i][3] = "";
                partTableBody[i][4] = usecount_805;
                partTableBody[i][5] = gysl;
                partTableBody[i][7] = "";
                partTableBody[i][8] = "";
                partTableBody[i][9] = "";
                partTableBody[i][10] = "";
                partTableBody[i][11] = "";
                partTableBody[i][12] = "";
                partTableBody[i][13] = "";
                partTableBody[i][14] = "";
                partTableBody[i][15] = "";
                partTableBody[i][16] = "";
                partTableBody[i][17] = "";
                partTableBody[i][18] = "";
                partTableBody[i][19] = "";
                partTableBody[i][20] = "";
                partTableBody[i][21] = "";
                partTableBody[i][22] = "";
            }
        }
        int tw1 = 200;
        partTableColWidth = new int[]{tw1 - 175, tw1, tw1 + 30, tw1, tw1 + 50, tw1 + 20, tw1 + 20, tw1, tw1, tw1 - 20, tw1 - 20, tw1 - 20, tw1 - 20, tw1, tw1, tw1, tw1, tw1, tw1, tw1, tw1, tw1, tw1};
        partTableEditCols = new int[]{5, 6, 22};
        partTableHideCols = new int[]{18, 19};
    }

    private Map<String, List<String>> getRecord() {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        Element techEle = null;
        XWTreeNode currentTechNode = frame.technicsTreePanel.getSelectedTreeNode();
        XWTreeObject treeObj = currentTechNode.getObject();
        if (treeObj instanceof XWTechnicsTreeObject) {
            XWTechnicsTreeObject techObj = (XWTechnicsTreeObject) treeObj;
            techEle = techObj.getTreeCellData();
        }

        if (techEle == null) {
            return map;
        }

        //获取工艺定额元素下查询创建的标准间/元器件/外购件子元素
        Element gyde = XmlUtility.getTechnicsDEElement(techEle);
        List<Element> matchParts = XmlUtility.getTechnicsGYDEMatchPart(gyde);
        if (matchParts != null && !matchParts.isEmpty()) {
            for (Element element : matchParts) {
                List<String> list = new ArrayList<String>();

                list.add(element.attributeValue("number"));
                list.add(element.attributeValue("chbm"));
                list.add(element.attributeValue("chmc"));
                list.add(element.attributeValue("useCount"));
                list.add(element.attributeValue("gyCount"));
                list.add(element.attributeValue("dw2"));
                list.add(element.attributeValue("xhph"));
                list.add(element.attributeValue("gg"));
                list.add(element.attributeValue("jstj"));
                list.add(element.attributeValue("sccj"));
                list.add(element.attributeValue("dw"));
                list.add(element.attributeValue("fjtj"));
                list.add(element.attributeValue("lwgggccc"));
                list.add(element.attributeValue("jxxndj"));
                list.add(element.attributeValue("zldj"));
                list.add(element.attributeValue("fzxs"));
                list.add(element.attributeValue("jddj"));
                list.add(element.attributeValue("wzlb"));
                list.add(element.attributeValue("wzlbbm"));
                list.add(element.attributeValue("cpdh"));
                list.add(element.attributeValue("dcstxyq"));
                list.add(element.attributeValue("comment"));
                map.put(element.attributeValue("number"), list);
            }
        }
        return map;
    }

    @Override
    protected void initLayout() {
        JScrollPane wztmsp = new JScrollPane(partTableOp.getZTable());
        wztmsp.getViewport().setBackground(Color.WHITE);
        BoxLayout box = new BoxLayout(centerPanel, BoxLayout.Y_AXIS);
        centerPanel.setLayout(box);
        centerPanel.add(wzkPanel);
        final JLabel lbjLable = new JLabel("零部件表");
        lbjLable.setFont(new Font("宋体", Font.PLAIN, 20));
        JPanel partPanel = new JPanel();
        partPanel.setLayout(new BorderLayout());
        partPanel.add(lbjLable, BorderLayout.NORTH);
        partPanel.add(wztmsp, BorderLayout.CENTER);
        centerPanel.add(partPanel);
        bottomPanel.add(saveButton);
        bottomPanel.add(deleteButton);
        bottomPanel.add(deleteAllButton);
//        bottomPanel.add(moveUpButton);
//        bottomPanel.add(moveDownButton);


        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
//		mainPanel.add(wzkPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        this.setContentPane(mainPanel);
    }

    final class SaveWlbmListener implements ActionListener {
        private JDialog dialog;
        private Window owner;
        private TableModel tm;
        List<XWTreeNode> updateNodes;

        public SaveWlbmListener(JDialog dialog, Window owner, TableModel tm, List<XWTreeNode> updateNodes) {
            this.dialog = dialog;
            this.owner = owner;
            this.tm = tm;
            this.updateNodes = updateNodes;
        }

        public void actionPerformed(ActionEvent e) {
            if (!check()) {
                JOptionPane.showMessageDialog(owner, "工艺数量或单位不能空！");
                return;
            }
//            if (!checkIsNumeric()) {
//                JOptionPane.showMessageDialog(owner, "工艺数量只能填写0-9的数字！");
//                return;
//            }


            int rows = tm.getRowCount();
            for (int i = 0; i < rows; i++) {
                String num = objectToString(tm.getValueAt(i, 5));
                String unit = objectToString(tm.getValueAt(i, 6));
                if(!CommonUtil.checkGysl(num,unit)){
                    JOptionPane.showMessageDialog(owner, "第" + (i+1) + "行工艺数量填写不规范：\r\n单位为“个”，“只”，“件”的数量只能为整数，其他的只能为最多三位小数");
                    return;
                }
            }
            int flag = JOptionPane.showConfirmDialog(owner, "确定保存吗？", "确认", JOptionPane.OK_CANCEL_OPTION);
            if (flag == JOptionPane.YES_OPTION) {
                //当前工艺
                Element techEle = null;
                XWTreeNode currentTechNode = frame.technicsTreePanel.getSelectedTreeNode();
                XWTreeObject treeObj = currentTechNode.getObject();
                if (treeObj instanceof XWTechnicsTreeObject) {
                    XWTechnicsTreeObject techObj = (XWTechnicsTreeObject) treeObj;
                    techEle = techObj.getTreeCellData();
                }

                if (techEle == null) {
                    JOptionPane.showMessageDialog(owner, "没有找到工艺数据，请选择工艺文件进行工艺定额！");
                    return;
                }

                //获取工艺定额元素下查询创建的标准间/元器件/外购件子元素
                Element gyde = XmlUtility.getTechnicsDEElement(techEle);
                Element matchpart = XmlUtility.getChildElements(gyde, "MATCHPART");
                if (matchpart == null) {
                    matchpart = DocumentHelper.createElement("MATCHPART");
                } else {
                    Map<String, PDFBuilder.CzjPart> map = PDFBuilder.getAllCzjMap(techEle);
                    Map<String, String> newPartMap = new HashMap<String, String>();
                    List<Element> matchpartElements = matchpart.elements();
                    for (Element matchpartElement : matchpartElements) {
                        newPartMap.put(matchpartElement.attributeValue("chbm"), matchpartElement.attributeValue("gyCount"));
                    }
                    for (int i = 0; i < rows; i++) {
                        String partNumber = (String) tm.getValueAt(i, 2);
                        String setCount = (String) tm.getValueAt(i, 5);
                        if (map.containsKey(partNumber)) {
                            double useCount = map.get(partNumber).getCount();
                            if (newPartMap.get(partNumber) != null && useCount > Double.valueOf(setCount)) {
                                JOptionPane.showMessageDialog(frame, "参装件:" + partNumber + "，已参装数量为：" + useCount + "，超出当前设置数量：" + setCount + "，保存失败！");
                                return;
                            }
                        }
                    }
                    XmlUtility.deleteAllChildElements(matchpart);
                }

                for (int i = 0; i < rows; i++) {
                    Element matchPartEle = createTechnicsMatchPartElement(i, tm);
                    matchpart.add(matchPartEle);
                }

                String techPath = WorkSpaceUtil.getTechnicsDirectory(techEle.attributeValue("technicsNumber"));
                String xmlFilePath = techPath + File.separator + techEle.attributeValue("technicsNumber") + ".xml";
                try {
                    XmlUtility.saveDocument(techEle.getDocument(), xmlFilePath);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                UploadTechnics technics = ObjectTransfer.technicsElementToUploadTechnics(techEle);
                if (!frame.editTechnics.contains(technics)) {
                    frame.editTechnics.add(technics);
                }
            }
            dialog.dispose();
        }

        private boolean check() {
            int rows = tm.getRowCount();
            for (int i = 0; i < rows; i++) {
                if ("".equals(objectToString(tm.getValueAt(i, 5)))) {
                    return false;
                }
                if ("".equals(objectToString(tm.getValueAt(i, 6)))) {
                    return false;
                }
            }
            return true;
        }

        private boolean checkIsNumeric() {
            int rows = tm.getRowCount();
            for (int i = 0; i < rows; i++) {
                String num = objectToString(tm.getValueAt(i, 5));
                boolean b = CommonUtil.isDouble(num);
                if (!b) {
                    return false;
                }
            }
            return true;
        }
    }

    private Element createTechnicsMatchPartElement(int i, TableModel tm) {
        Element element = DocumentHelper.createElement("MatchPart");
        XmlUtility.setAttributeValue(element, "number", objectToString(tm.getValueAt(i, 1)));//"图号"
        XmlUtility.setAttributeValue(element, "chbm", objectToString(tm.getValueAt(i, 2)));//"存货编码"
        XmlUtility.setAttributeValue(element, "chmc", objectToString(tm.getValueAt(i, 3)));//"存货名称"
        XmlUtility.setAttributeValue(element, "useCount", objectToString(tm.getValueAt(i, 4)));//"设计使用数量"
        XmlUtility.setAttributeValue(element, "gyCount", objectToString(tm.getValueAt(i, 5)));//"工艺使用数量"
        XmlUtility.setAttributeValue(element, "dw2", objectToString(tm.getValueAt(i, 6)));//"单位"
        XmlUtility.setAttributeValue(element, "xhph", objectToString(tm.getValueAt(i, 7)));//"型号牌号"
        XmlUtility.setAttributeValue(element, "gg", objectToString(tm.getValueAt(i, 8)));//"规格"
        XmlUtility.setAttributeValue(element, "jstj", objectToString(tm.getValueAt(i, 9)));//"技术条件"
        XmlUtility.setAttributeValue(element, "sccj", objectToString(tm.getValueAt(i, 10)));//"生产厂家"
        XmlUtility.setAttributeValue(element, "dw", objectToString(tm.getValueAt(i, 11)));//"主计量单位"
        XmlUtility.setAttributeValue(element, "fjtj", objectToString(tm.getValueAt(i, 12)));//"附加条件"
        XmlUtility.setAttributeValue(element, "lwgggccc", objectToString(tm.getValueAt(i, 13)));//"螺纹规格/公称尺寸"
        XmlUtility.setAttributeValue(element, "jxxndj", objectToString(tm.getValueAt(i, 14)));//"机械性能等级"
        XmlUtility.setAttributeValue(element, "zldj", objectToString(tm.getValueAt(i, 15)));//"质量等级"
        XmlUtility.setAttributeValue(element, "fzxs", objectToString(tm.getValueAt(i, 16)));//"封装形式"
        XmlUtility.setAttributeValue(element, "jddj", objectToString(tm.getValueAt(i, 17)));//"精度等级"
        XmlUtility.setAttributeValue(element, "wzlb", objectToString(tm.getValueAt(i, 18)));//"物资类别"
        XmlUtility.setAttributeValue(element, "wzlbbm", objectToString(tm.getValueAt(i, 19)));//"物资类别编码"
        XmlUtility.setAttributeValue(element, "cpdh", objectToString(tm.getValueAt(i, 20)));//"物资类别"
        XmlUtility.setAttributeValue(element, "dcstxyq", objectToString(tm.getValueAt(i, 21)));//"物资类别编码"
        XmlUtility.setAttributeValue(element, "comment", objectToString(tm.getValueAt(i, 22)));//"备注"
        XmlUtility.setAttributeValue(element, "dataFrom", "erp");//数据来源
        XmlUtility.setAttributeValue(element, "mtype", ValueCache.allPartsType.get(tm.getValueAt(i, 1)));//类型
        return element;
    }

    public static String objectToString(Object obj) {
        if (null == obj || "".equals(obj)) {
            return "";
        } else {
            return String.valueOf(obj);
        }
    }


}