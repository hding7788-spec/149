package com.glaway.mpm.erp.pbom;

import com.glaway.mpm.erp.AbstractERPDialog;
import com.glaway.mpm.erp.DefaultZTableFactory;
import com.glaway.mpm.erp.ZTableOp;
import com.glaway.mpm.model.UploadTechnics;
import com.glaway.mpm.pdf.PDFBuilder;
import com.glaway.mpm.util.*;
import com.glaway.mpm.view.*;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import wt.part.WTPart;

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
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 填写创建零部件
 * 从ERP查询添加外购件页面
 *
 * @author Administrator
 */
public class SearchAndCreateDialog extends AbstractERPDialog {

    private static final long serialVersionUID = 1L;

    private CmPbomErpWzkPanel wzkPanel = new CmPbomErpWzkPanel(1, 1);

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
    private NewTechnicsPart frame;
    private XWTreeNode node;
    private WTPart parentPart;

    public SearchAndCreateDialog(NewTechnicsPart frame, XWTreeNode node, String title) {
        super(frame);
        this.setTitle(title);
        this.frame = frame;
        this.node = node;
        XWPartTreeObject partObj = (XWPartTreeObject) node.getP().getObject();
        Element partEle = partObj.getTreeCellData();
        try {
            this.parentPart = TechnicsIntf.getPartByOid(partEle.attributeValue("oid"));
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        loadInitDatas();
        initDimension();
        initComponents();

        initActions();
        initLayout();
        this.setResizable(true);
        this.setModal(false);
        this.setResizable(false);
        this.setVisible(true);
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
                    int pcunt = parttm.getRowCount();
                    String wzbm = (String) wztm.getValueAt(row, 1);
                    boolean isExist = false;
                    for (int i = 0; i < pcunt; i++) {
                        if (wzbm.equals((String) parttm.getValueAt(i, 2))) {
                            isExist = true;
                            break;
                        }
                    }
                    if (isExist) {
                        JOptionPane.showMessageDialog(frame, "【" + wzbm + "】物资编码已经使用");
                        return;
                    }
                    Object[] rows = new Object[partTableHeader.length];
                    rows[0] = (parttm.getRowCount() + 1) + "";
                    if(parentPart!=null)
                    	rows[1] = parentPart.getNumber();
                    rows[2] = wztm.getValueAt(row, 1);
                    rows[3] = wztm.getValueAt(row, 1);
                    rows[4] = wztm.getValueAt(row, 2);
                    rows[5] = 1 + "";
                    rows[7] = wztm.getValueAt(row, 3);
                    rows[8] = wztm.getValueAt(row, 4);
                    rows[9] = wztm.getValueAt(row, 5);
                    rows[10] = wztm.getValueAt(row, 6);
                    rows[11] = wztm.getValueAt(row, 7);
                    rows[12] = wztm.getValueAt(row, 8);
                    rows[13] = wztm.getValueAt(row, 9);
                    rows[14] = wztm.getValueAt(row, 10);
                    rows[15] = wztm.getValueAt(row, 11);
                    rows[16] = wztm.getValueAt(row, 12);
                    rows[17] = wztm.getValueAt(row, 13);
                    rows[18] = wztm.getValueAt(row, 14);
                    rows[19] = wztm.getValueAt(row, 15);
                    rows[20] = wztm.getValueAt(row, 16);
                    rows[21] = wztm.getValueAt(row, 17);

                    int rowCount = parttm.getRowCount();
                    if (rowCount > 0) {
                        partTableOp.insertOneRow(0, rows);
                    } else {
                        partTableOp.addOneRow(rows);
                    }
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
                    int numrow = ptable.getSelectedRows().length;
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
                            if ("".equals(message)) {
                                message = partNumber;
                            } else {
                                message = message + "," + partNumber;
                            }
                        }
                    }
                    if ("".equals(message)) {
                        for (int i = 0; i < numrow; i++) {
                            dtm.removeRow(ptable.getSelectedRow());
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, message + "已参与参装，请先取消参装再进行删除！");
                        return;
                    }
//                    for (int i = 0; i < numrow; i++) {
//                        String partNumber = (String) ptable.getValueAt(i,3);
//                        Map<String, PDFBuilder.CzjPart> czjPartMap = PDFBuilder.getAllCzjMap(techEle);
//                        if(czjPartMap.containsKey(partNumber)){
//                            JOptionPane.showMessageDialog(frame, partNumber + "已参与参装，请先取消参装再进行删除！");
//                            return;
//                        }
//                        dtm.removeRow(ptable.getSelectedRow());
//                    }
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
                        String partNumber = (String) dtm.getValueAt(rowcount, 3);
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

        saveButton.addActionListener(new SaveCjlbjListener(this, frame, partTableOp.getTableModel(), node));
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
        partTableHeader = new String[]{"序号", "上级图号", "图号", "存货编码", "存货名称", "*工艺数量", "*单位", "型号牌号", "规格", "技术条件", "生产厂家", "主计量单位", "附加条件", "螺纹规格/公称尺寸", "机械性能等级", "质量等级", "封装形式", "精度等级", "物资类别", "物资类别编码", "产品代号", "电参数特选要求", "备注"};

        Element techEle = null;
        XWTreeNode currentTechNode = frame.technicsTreePanel.getSelectedTreeNode();
        XWTreeObject treeObj = currentTechNode.getObject();
        if (treeObj instanceof XWTechnicsTreeObject) {
            XWTechnicsTreeObject techObj = (XWTechnicsTreeObject) treeObj;
            techEle = techObj.getTreeCellData();
        }

        if (techEle == null) {
            return;
        }

        //获取工艺定额元素下查询创建的标准间/元器件/外购件子元素
        Element gyde = XmlUtility.getTechnicsDEElement(techEle);
        List<Element> newParts = XmlUtility.getTechnicsGYDENewPart(gyde);
        partTableBody = new String[newParts.size()][partTableHeader.length];
        int tw1 = 200;
        partTableColWidth = new int[]{tw1 - 150, tw1, tw1 + 30, tw1, tw1 + 50, tw1 - 175, tw1 - 150, tw1 + 20, tw1, tw1, tw1 - 20, tw1 - 20, tw1 - 20, tw1 - 20, tw1, tw1, tw1, tw1, tw1, tw1, tw1, tw1, tw1};
        partTableEditCols = new int[]{5, 6, 22};
        partTableHideCols = new int[]{18, 19};

        if (newParts != null && !newParts.isEmpty()) {
            int row = 0;
            int n = 1;
            for (Element element : newParts) {
                partTableBody[row][0] = n + "";
                partTableBody[row][1] = objectToString(element.attributeValue("parentNumber"));
                partTableBody[row][2] = objectToString(element.attributeValue("number"));
                partTableBody[row][3] = objectToString(element.attributeValue("chbm"));
                partTableBody[row][4] = objectToString(element.attributeValue("chmc"));
                partTableBody[row][5] = objectToString(element.attributeValue("sl"));
                partTableBody[row][6] = objectToString(element.attributeValue("dw2"));
                partTableBody[row][7] = objectToString(element.attributeValue("xhph"));
                partTableBody[row][8] = objectToString(element.attributeValue("gg"));
                partTableBody[row][9] = objectToString(element.attributeValue("jstj"));
                partTableBody[row][10] = objectToString(element.attributeValue("sccj"));
                partTableBody[row][11] = objectToString(element.attributeValue("dw"));
                partTableBody[row][12] = objectToString(element.attributeValue("fjtj"));
                partTableBody[row][13] = objectToString(element.attributeValue("lwgggccc"));
                partTableBody[row][14] = objectToString(element.attributeValue("jxxndj"));
                partTableBody[row][15] = objectToString(element.attributeValue("zldj"));
                partTableBody[row][16] = objectToString(element.attributeValue("fzxs"));
                partTableBody[row][17] = objectToString(element.attributeValue("jddj"));
                partTableBody[row][18] = objectToString(element.attributeValue("wzlb"));
                partTableBody[row][19] = objectToString(element.attributeValue("wzlbbm"));
                partTableBody[row][20] = objectToString(element.attributeValue("cpdh"));
                partTableBody[row][21] = objectToString(element.attributeValue("dcstxyq"));
                partTableBody[row][22] = objectToString(element.attributeValue("comment"));
                row++;
                n++;
            }
        }
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

    class SaveCjlbjListener implements ActionListener {
        private JDialog dialog;
        private Window owner;
        private TableModel tm;
        private XWTreeNode node;

        public SaveCjlbjListener(JDialog dialog, Window owner, TableModel tm, XWTreeNode node) {
            this.dialog = dialog;
            this.owner = owner;
            this.tm = tm;
            this.node = node;
        }

        public void actionPerformed(ActionEvent e) {
            int rows = tm.getRowCount();
			/*if(rows == 0) {
				JOptionPane.showMessageDialog(owner, "请选择数据后再保存！");
				return;
			}*/

            if (!check()) {
                JOptionPane.showMessageDialog(owner, "工艺数量或单位不能为空！");
                return;
            }
//            if (!checkIsNumeric()) {
//                JOptionPane.showMessageDialog(owner, "工艺数量只能填写0-9的数字！");
//                return;
//            }
            for (int i = 0; i < rows; i++) {
                String num = objectToString(tm.getValueAt(i, 5));
                String unit = objectToString(tm.getValueAt(i, 6));
                if(!CommonUtil.checkGysl(num,unit)){
                    JOptionPane.showMessageDialog(owner, "第" + (i+1) + "行工艺数量填写不规范：\r\n单位为“个”，“只”，“件”的数量只能为整数，其他的只能为最多三位小数");
                    return;
                }
            }


            int flag = JOptionPane.showConfirmDialog(frame, "确定保存吗？", "确认", JOptionPane.OK_CANCEL_OPTION);
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
                Element newpart = XmlUtility.getChildElements(gyde, "NEWPART");
                if (newpart == null) {
                    newpart = DocumentHelper.createElement("NEWPART");
                } else {
                    Map<String, PDFBuilder.CzjPart> map = PDFBuilder.getAllCzjMap(techEle);
                    Map<String, String> newPartMap = new HashMap<String, String>();
                    List<Element> newParts = newpart.elements("NewPart");
                    for (Element newPart : newParts) {
                        newPartMap.put(newPart.attributeValue("chbm"), newPart.attributeValue("sl"));
                    }
                    for (int i = 0; i < rows; i++) {
                        String partNumber = (String) tm.getValueAt(i, 3);
                        String setCount = (String) tm.getValueAt(i, 5);
                        if (map.containsKey(partNumber)) {
                            double useCount = map.get(partNumber).getCount();
                            if (newPartMap.get(partNumber) != null && useCount > Double.valueOf(setCount)) {
                                JOptionPane.showMessageDialog(frame, "参装件:" + partNumber + "，已参装数量为：" + useCount + "，超出当前设置数量：" + setCount + "，保存失败！");
                                return;
                            }
                        }
                    }
                    XmlUtility.deleteAllChildElements(newpart);
                }

                for (int i = 0; i < rows; i++) {
//					String partNumber = (String) tm.getValueAt(i,3);
//					String count = (String) tm.getValueAt(i,5);
//					if(map.containsKey(partNumber)){
//						if(newPartMap.get(partNumber) != null && !count.equals(newPartMap.get(partNumber))){
//							JOptionPane.showMessageDialog(frame, "参装件:"+partNumber+" 已参与参装，无法修改数量，请取消参装后再修改！");
//							return;
//						}
//					}
                    Element newPartEle = createTechnicsNewPartElement(i, tm);
                    newpart.add(newPartEle);
                }

                //gyde.add(newpart);

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
            //add by machongqi 2015-6-24
            int b = frame.tecnicsJTabbedPane.getSelectedIndex();
            frame.tecnicsJTabbedPane.setSelectedIndex(1);
            frame.tecnicsJTabbedPane.setSelectedIndex(b);
            //add by machongqi end
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

    private Element createTechnicsNewPartElement(int i, TableModel tm) {
        Element element = DocumentHelper.createElement("NewPart");
        XmlUtility.setAttributeValue(element, "parentNumber", objectToString(tm.getValueAt(i, 1)));//"上级图号"
        XmlUtility.setAttributeValue(element, "number", objectToString(tm.getValueAt(i, 2)));//"图号"
        XmlUtility.setAttributeValue(element, "chbm", objectToString(tm.getValueAt(i, 3)));//"存货编码"
        XmlUtility.setAttributeValue(element, "chmc", objectToString(tm.getValueAt(i, 4)));//"存货名称"
        XmlUtility.setAttributeValue(element, "sl", objectToString(tm.getValueAt(i, 5)));//"使用数量"
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
        XmlUtility.setAttributeValue(element, "cpdh", objectToString(tm.getValueAt(i, 20)));//"产品代号"
        XmlUtility.setAttributeValue(element, "dcstxyq", objectToString(tm.getValueAt(i, 21)));//"电参数特选要求"
        XmlUtility.setAttributeValue(element, "comment", objectToString(tm.getValueAt(i, 22)));//备注
        XmlUtility.setAttributeValue(element, "dataFrom", "erp");//数据来源
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