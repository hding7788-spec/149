package com.glaway.mpm.view;

import com.glaway.mpm.model.PTaoBean;
import com.glaway.mpm.util.BomXMLUtil;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.XmlUtility;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.*;

/**
 * 方法功能:配套明细表
 *
 * @author cjh
 * @date 2025/2/19
 */
public class PeiTaoListTableJPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private NewTechnicsPart frame;
    private JPanel mainPanel;
    private JPanel buttonPanel;
    private JButton initialData;
    private JButton cleanFrom;
    private JButton save;
    private JButton removeButon;
    private JButton shangyi;
    private JButton xiayi;
    private JScrollPane scrollPanel;
    private JTable peiTaoTable;
    private Object[] o = {"自制件", "标准件", "元器件", "外购件", "带料委外件", "非带料委外件", "主要材料", "外配套件", "试件"};
    private JComboBox box = new JComboBox(o);

    public PeiTaoListTableJPanel(NewTechnicsPart frame) {
        this.frame = frame;
        initComponents();
        initLayout();
        initActions();
        setName("PeiTaoListTableJPanel");
    }

    private void initComponents() {
        mainPanel = new JPanel();
        scrollPanel = new JScrollPane();
        buttonPanel = new JPanel();
        peiTaoTable = new JTable();
        peiTaoTable.setModel(new DefaultTableModel(new Object[][]{}, new String[]{
                "编号", "名称", "零组件生产类型", "工艺数量", "型号牌号", "规格", "版本", "单位", "备注", "技术条件", "来自何处", "零件编号", "质量等级", "数据来源", "性能参数"
        }) {
            private static final long serialVersionUID = 1L;
            Class[] types = new Class[]{
                    String.class, String.class, String.class, String.class, String.class, String.class, String.class,
                    String.class, String.class, String.class, String.class, String.class, String.class, String.class,
                    String.class
            };
            boolean[] canEdit = new boolean[]{
                    false, false, true, false, false, false, false, false, true, false, true, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                try {
                    Document document = frame.getCurrentTechnics();
                    Element techElement = XmlUtility.getTechnicsElement(document);
                    String pplanType = XmlUtility.getAttributeValue(techElement, "PPLANTYPE");
                    if("临时工艺文件".equals(pplanType)) {
                        if(columnIndex == 3) {//工艺数量
                            DefaultTableModel tableModel = (DefaultTableModel) peiTaoTable.getModel();
                            if(!"".equals(tableModel.getValueAt(rowIndex, 6))) {
                                return true;
                            }
                        }
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
                return canEdit[columnIndex];
            }
        });

        peiTaoTable.getTableHeader().setReorderingAllowed(false);
        CommonUIUtil.hiddenCell(peiTaoTable, 9);
        CommonUIUtil.hiddenCell(peiTaoTable, 11);
        CommonUIUtil.hiddenCell(peiTaoTable, 13);

        initialData = new JButton("恢复初始值");
        initialData.setPreferredSize(new Dimension(120, 25));
        cleanFrom = new JButton("清除厂商");
        cleanFrom.setPreferredSize(new Dimension(120, 25));
        save = new JButton("保   存");
        save.setPreferredSize(new Dimension(120, 25));
        removeButon = new JButton("移   除");
        removeButon.setPreferredSize(new Dimension(120, 25));
        shangyi = new JButton("上移");
        shangyi.setPreferredSize(new Dimension(120, 25));
        xiayi = new JButton("下移");
        xiayi.setPreferredSize(new Dimension(120, 25));
    }

    private void initLayout() {
        peiTaoTable.setRowHeight(23);
        peiTaoTable.getTableHeader().setPreferredSize(new Dimension(30, 25));
        scrollPanel.setViewportView(peiTaoTable);
        scrollPanel.setPreferredSize(new Dimension(1000, 800));
        mainPanel.setBorder(BorderFactory.createTitledBorder("配套明细表 （配套明细表“移除”按钮的使用，不影响后端nc系统拉取数据。仅调整工艺文件pdf版本的输出样式。）"));
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(scrollPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.EAST);
        mainPanel.setPreferredSize(new Dimension(1000, 800));

        FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT);
        buttonPanel.setLayout(flowLayout);
        buttonPanel.setBorder(BorderFactory.createTitledBorder(""));
        buttonPanel.setPreferredSize(new Dimension(130, 800));
        buttonPanel.add(initialData);
        buttonPanel.add(cleanFrom);
        buttonPanel.add(save);
        buttonPanel.add(removeButon);
        buttonPanel.add(shangyi);
        buttonPanel.add(xiayi);

        TableColumn typeColumn = peiTaoTable.getColumn("零组件生产类型");
        typeColumn.setCellEditor(new DefaultCellEditor(box));

        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    private void initActions() {
        CmActionListener listener = new CmActionListener();
        initialData.addActionListener(listener);
        cleanFrom.addActionListener(listener);
        save.addActionListener(listener);
        removeButon.addActionListener(listener);
        shangyi.addActionListener(listener);
        xiayi.addActionListener(listener);

    }

    class CmActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == initialData) {
                initialData();
            } else if(e.getSource() == cleanFrom) {
                cleanFrom();
            } else if(e.getSource() == save) {
                save();
            } else if(e.getSource() == removeButon) {
                remove();
            } else if(e.getSource() == shangyi) {
                changeRowValue(true);
            } else if(e.getSource() == xiayi) {
                changeRowValue(false);
            }
        }
    }

    public void setTableValues(Element techEle) {
        Element peiTaoTable = techEle.element("PEITAOTABLE");
        DefaultTableModel tableModel = (DefaultTableModel) this.peiTaoTable.getModel();
        tableModel.setRowCount(0);
        //老数据或是第一次新建的工艺文件时是没有配套表的，需要从该零部件的下级子件中获取
        if(peiTaoTable != null) {
            List<Element> list = XmlUtility.getPeiTaoListTableElements(techEle);
            //add by machongqi end
            if(list != null && !list.isEmpty()) {
                for(Element element : list) {
                    addOneRow(tableModel);
                    int rowCount = this.peiTaoTable.getRowCount();
                    // "序号", "编号", "名称", "零组件生产类型", "数量", "型号牌号", "规格", "版本"
                    tableModel.setValueAt(element.attributeValue("number"), rowCount - 1, 0);
                    tableModel.setValueAt(element.attributeValue("name"), rowCount - 1, 1);
                    tableModel.setValueAt(element.attributeValue("MTYPE"), rowCount - 1, 2);
                    tableModel.setValueAt(element.attributeValue("useCount"), rowCount - 1, 3);
                    tableModel.setValueAt(element.attributeValue("XHPH"), rowCount - 1, 4);
                    tableModel.setValueAt(element.attributeValue("CSIZE"), rowCount - 1, 5);
                    tableModel.setValueAt(element.attributeValue("version"), rowCount - 1, 6);
                    tableModel.setValueAt(element.attributeValue("comment"), rowCount - 1, 8);
                    tableModel.setValueAt(element.attributeValue("jstj"), rowCount - 1, 9);
                    tableModel.setValueAt(element.attributeValue("gys"), rowCount - 1, 10);
                    tableModel.setValueAt(element.attributeValue("partNumber"), rowCount - 1, 11);
                    tableModel.setValueAt(element.attributeValue("zldj"), rowCount - 1, 12);
                    tableModel.setValueAt(element.attributeValue("dataFrom"), rowCount - 1, 13);
                    tableModel.setValueAt(element.attributeValue("xncs"), rowCount - 1, 14);
                    if("".equals(element.attributeValue("dw")) || element.attributeValue("dw") == null || element.attributeValue("dw") == "null") {
                        tableModel.setValueAt("", rowCount - 1, 7);
                    } else {
                        tableModel.setValueAt(element.attributeValue("dw"), rowCount - 1, 7);
                    }
                }
            }
        }

    }

    //恢复初始值按钮逻辑
    private void initialData() {
        DefaultTableModel tableModel = (DefaultTableModel) peiTaoTable.getModel();
        tableModel.setRowCount(0);
        XWTreeNode node = frame.xwPartTreePanel.getSelectedTreeNode();
        XWTreeObject xo = node.getObject();
        Element techEle = xo.getTreeCellData();
        resetNew(tableModel, techEle);
    }


    private void cleanFrom() {
        int[] sel = peiTaoTable.getSelectedRows();
        if(sel == null || sel.length == 0) {
            JOptionPane.showMessageDialog(frame, "请选择需要移除的数据！");
            return;
        }
        DefaultTableModel tableModel = (DefaultTableModel) peiTaoTable.getModel();
        for(int i = 0; i < sel.length; i++) {
            tableModel.setValueAt("", sel[i], 10);
        }
    }

    private void changeRowValue(boolean up) {
        //add by Mchen
        if(peiTaoTable.getSelectedRowCount() > 1) {
            int[] selectedRows = peiTaoTable.getSelectedRows();
            int maxRow = selectedRows[selectedRows.length - 1];
            int minRow = selectedRows[0];
            int copareRow = maxRow - minRow + 1;
            if(copareRow > selectedRows.length) {
                JOptionPane.showMessageDialog(frame, "请选择连续的多行进行上移或下移!");
                return;
            }
            if(up && minRow == 0) {
                return;
            }
            if(!up && maxRow == peiTaoTable.getRowCount() - 1) {
                return;
            }
            if(up) {
                for(int i = 0; i < selectedRows.length; i++) {
                    int currentRow = selectedRows[i];
                    int neighbor = currentRow - 1;
                    Object[] obj1 = new Object[peiTaoTable.getColumnCount()];
                    Object[] obj2 = new Object[peiTaoTable.getColumnCount()];
                    for(int i1 = 0; i1 < peiTaoTable.getColumnCount(); i1++) {
                        obj1[i1] = peiTaoTable.getValueAt(currentRow, i1);
                        obj2[i1] = peiTaoTable.getValueAt(neighbor, i1);
                    }
                    for(int j = 0; j < peiTaoTable.getColumnCount(); j++) {
                        peiTaoTable.setValueAt(obj2[j], currentRow, j);
                        peiTaoTable.setValueAt(obj1[j], neighbor, j);
                    }
                }
                peiTaoTable.setRowSelectionInterval(selectedRows[0] - 1, selectedRows[selectedRows.length - 1] - 1);
            } else if(!up) {
                for(int i = selectedRows.length - 1; i >= 0; i--) {
                    int currentRow = selectedRows[i];
                    int neighbor = currentRow + 1;
                    Object[] obj1 = new Object[peiTaoTable.getColumnCount()];
                    Object[] obj2 = new Object[peiTaoTable.getColumnCount()];
                    for(int i1 = peiTaoTable.getColumnCount() - 1; i1 >= 0; i1--) {
                        obj1[i1] = peiTaoTable.getValueAt(currentRow, i1);
                        obj2[i1] = peiTaoTable.getValueAt(neighbor, i1);
                    }
                    for(int j = 0; j < peiTaoTable.getColumnCount(); j++) {
                        peiTaoTable.setValueAt(obj2[j], currentRow, j);
                        peiTaoTable.setValueAt(obj1[j], neighbor, j);
                    }
                }
                peiTaoTable.setRowSelectionInterval(selectedRows[0] + 1, selectedRows[selectedRows.length - 1] + 1);

            }

        } else {
            int select = peiTaoTable.getSelectedRow();
            if(select < 0)
                return;
            if(up && select == 0)
                return;
            if(!up && select == peiTaoTable.getRowCount() - 1)
                return;
            Object[] obj1 = new Object[peiTaoTable.getColumnCount()];
            Object[] obj2 = new Object[peiTaoTable.getColumnCount()];
            int neighbor;
            if(up)
                neighbor = select - 1;
            else
                neighbor = select + 1;
            for(int i = 0; i < peiTaoTable.getColumnCount(); i++) {
                obj1[i] = peiTaoTable.getValueAt(select, i);
                obj2[i] = peiTaoTable.getValueAt(neighbor, i);
            }
            for(int j = 0; j < peiTaoTable.getColumnCount(); j++) {
                peiTaoTable.setValueAt(obj2[j], select, j);
                peiTaoTable.setValueAt(obj1[j], neighbor, j);
            }
            peiTaoTable.setRowSelectionInterval(neighbor, neighbor);
        }
    }

    private void save() {
        Element techEle = XmlUtility.getTechnicsElement((frame).getCurrentTechnics());
        Element peiTaoEle = XmlUtility.getPeiTaoListTableElement(techEle);
        techEle.remove(peiTaoEle);
        peiTaoEle = XmlUtility.getPeiTaoListTableElement(techEle);
        int n = peiTaoTable.getRowCount();
        for(int i = 0; i < n; i++) {
            String number = String.valueOf(peiTaoTable.getValueAt(i, 0));
            String name = String.valueOf(peiTaoTable.getValueAt(i, 1));

            String mtype = String.valueOf(peiTaoTable.getValueAt(i, 2));
            if(mtype == null || "".equals(mtype) || "null".equals(mtype)) {
                mtype = "";
            }
            String useCount = String.valueOf(peiTaoTable.getValueAt(i, 3));

            String xhph = String.valueOf(peiTaoTable.getValueAt(i, 4));
            if(xhph == null || "".equals(xhph) || "null".equals(xhph)) {
                xhph = "";
            }

            String csize = String.valueOf(peiTaoTable.getValueAt(i, 5));
            if(csize == null || "".equals(csize) || "null".equals(csize)) {
                csize = "";
            }

            String version = String.valueOf(peiTaoTable.getValueAt(i, 6));

            String dw = String.valueOf(peiTaoTable.getValueAt(i, 7));

            String comment = String.valueOf(peiTaoTable.getValueAt(i, 8));
            if(comment == null || "".equals(comment) || "null".equals(comment)) {
                comment = "";
            }
            String jstj = String.valueOf(peiTaoTable.getValueAt(i, 9));
            if(jstj == null || "".equals(jstj) || "null".equals(jstj)) {
                jstj = "";
            }
            String gys = String.valueOf(peiTaoTable.getValueAt(i, 10));
            if(gys == null || "".equals(gys) || "null".equals(gys)) {
                gys = "";
            }
            String partNumber = String.valueOf(peiTaoTable.getValueAt(i, 11));
            if(partNumber == null || "".equals(partNumber) || "null".equals(partNumber)) {
                partNumber = "";
            }
            String zldj = String.valueOf(peiTaoTable.getValueAt(i, 12));
            if(zldj == null || "".equals(zldj) || "null".equals(zldj)) {
                zldj = "";
            }
            String dataFrom = String.valueOf(peiTaoTable.getValueAt(i, 13));
            if(dataFrom == null || "".equals(dataFrom) || "null".equals(dataFrom)) {
                dataFrom = "";
            }
            String xncs = String.valueOf(peiTaoTable.getValueAt(i, 14));
            if(xncs == null || "".equals(xncs) || "null".equals(xncs)) {
                xncs = "";
            }
            Element element = DocumentHelper.createElement("PeiTaoElement");
            XmlUtility.setAttributeValue(element, "number", number);
            XmlUtility.setAttributeValue(element, "name", name);
            XmlUtility.setAttributeValue(element, "MTYPE", mtype);
            XmlUtility.setAttributeValue(element, "useCount", useCount);
            XmlUtility.setAttributeValue(element, "XHPH", xhph);
            XmlUtility.setAttributeValue(element, "CSIZE", csize);
            XmlUtility.setAttributeValue(element, "version", version);
            XmlUtility.setAttributeValue(element, "dw", dw);
            XmlUtility.setAttributeValue(element, "comment", comment);
            XmlUtility.setAttributeValue(element, "jstj", jstj);
            XmlUtility.setAttributeValue(element, "gys", gys);
            XmlUtility.setAttributeValue(element, "partNumber", partNumber);
            XmlUtility.setAttributeValue(element, "zldj", zldj);
            XmlUtility.setAttributeValue(element, "dataFrom", dataFrom);
            XmlUtility.setAttributeValue(element, "xncs", xncs);

            peiTaoEle.add(element);
        }
        try {
            frame.saveProcess(techEle);
            JOptionPane.showMessageDialog(frame, "保存配套明细表成功！");
        } catch(Exception e1) {
            JOptionPane.showMessageDialog(frame, "保存配套明细表失败,错误信息:" + e1.getLocalizedMessage());
            e1.printStackTrace();
        }
    }

    private void remove() {
        List<String> partNumberList = new ArrayList<String>();
        XWTreeNode xwTreeNode = frame.xwPartTreePanel.getSelectedTreeNode().getP();
        Enumeration<XWTreeNode> enu = xwTreeNode.children();
        while(enu.hasMoreElements()) {
            XWTreeNode tNode = enu.nextElement();
            if(tNode.getObject() instanceof XWPartTreeObject) {
                XWPartTreeObject childObj = (XWPartTreeObject) tNode.getObject();
                Element pele = childObj.getTreeCellData();
                partNumberList.add(pele.attributeValue("partNumber"));
            }
        }
        System.out.println(partNumberList);

        int[] sel = peiTaoTable.getSelectedRows();
        if(sel == null || sel.length == 0) {
            JOptionPane.showMessageDialog(frame, "请选择需要移除的数据！");
            return;
        }
        String msg = "";
        for(int j = 0; j < sel.length; j++) {
            String tableValue = (String) peiTaoTable.getValueAt(sel[j], 0);
            if(!partNumberList.contains(tableValue)) {
                if("".equals(msg)) {
                    msg = tableValue;
                } else {
                    msg += "," + tableValue;
                }
            }
        }

        DefaultTableModel tableModel = (DefaultTableModel) peiTaoTable.getModel();
        for(int i = 0; i < sel.length; i++) {
            tableModel.removeRow(peiTaoTable.getSelectedRow());
        }
    }

    /**
     * 恢复初始值新逻辑：
     * 1、加载配套表中已存在信息  peitaoMap
     * 2、加载PBOM、加载材料定额、加载工艺定额  dataMap
     * 3、更新配套表中的信息（包含新增和修改操作）
     * 4、表格中加载（包含移除操作）
     */
    private void resetNew(DefaultTableModel tableModel, Element techEle) {
        //加载配套表中已保存的信息
        Map<String, PTaoBean> peitaoMap = new LinkedHashMap<String, PTaoBean>();
        loadPTaoData(techEle, peitaoMap);
        //加载PBOM、材料定额、工艺定额信息
        Map<String, PTaoBean> dataMap = new LinkedHashMap<String, PTaoBean>();
        Map<String, PTaoBean> pbomDataMap = new LinkedHashMap<String, PTaoBean>();
        loadPbomData(techEle, pbomDataMap);
        loadCldeData(techEle, dataMap);
        loadGydeData(techEle, dataMap);

        for(Map.Entry<String, PTaoBean> dataEntry : dataMap.entrySet()) {
            String key = dataEntry.getKey();
            PTaoBean dataPtaoBean = dataEntry.getValue();
            PTaoBean pTaoBean;
            //如果已存在,更新信息
            if(peitaoMap.containsKey(key)) {
                pTaoBean = peitaoMap.get(key);
                pTaoBean.setCount(dataPtaoBean.getCount());
                pTaoBean.setUnit(dataPtaoBean.getUnit());
                pTaoBean.setXhph(dataPtaoBean.getXhph());
                pTaoBean.setGg(dataPtaoBean.getGg());
                pTaoBean.setName(dataPtaoBean.getName());
                pTaoBean.setVersion(dataPtaoBean.getVersion());
                pTaoBean.setZldj(dataPtaoBean.getZldj());
                pTaoBean.setDataFrom(dataPtaoBean.getDataFrom());
                pTaoBean.setJstj(dataPtaoBean.getJstj());
                pTaoBean.setMtype(dataPtaoBean.getMtype());
                pTaoBean.setXncs(dataPtaoBean.getXncs());
            } else {
                peitaoMap.put(key, dataPtaoBean);
            }
        }

        for(Map.Entry<String, PTaoBean> dataEntry : pbomDataMap.entrySet()) {
            String key = dataEntry.getKey();
            PTaoBean dataPtaoBean = dataEntry.getValue();
            PTaoBean pTaoBean;
            if(!peitaoMap.containsKey(key)) {
                peitaoMap.put(key, dataPtaoBean);
            } else {
                if(!dataMap.containsKey(key)) {
                    pTaoBean = peitaoMap.get(key);
                    pTaoBean.setCount(dataPtaoBean.getCount());
                    pTaoBean.setUnit(dataPtaoBean.getUnit());
                    pTaoBean.setXhph(dataPtaoBean.getXhph());
                    pTaoBean.setGg(dataPtaoBean.getGg());
                    pTaoBean.setName(dataPtaoBean.getName());
                    pTaoBean.setVersion(dataPtaoBean.getVersion());
                    pTaoBean.setZldj(dataPtaoBean.getZldj());
                    pTaoBean.setDataFrom(dataPtaoBean.getDataFrom());
                    pTaoBean.setJstj(dataPtaoBean.getJstj());
                    pTaoBean.setMtype(dataPtaoBean.getMtype());
                }
            }
        }

        for(Map.Entry<String, PTaoBean> peitaoEntry : peitaoMap.entrySet()) {
            String number = peitaoEntry.getKey();
            PTaoBean peitaoBean = peitaoEntry.getValue();
            //没有则表示已删除
            if(!dataMap.containsKey(number) && !pbomDataMap.containsKey(number)) {
                continue;
            }
            if(number.indexOf("_试件") > -1) {
                number = number.substring(0, number.indexOf("_试件"));
            }
            addOneRow(tableModel);
            int rowCount = peiTaoTable.getRowCount();
            tableModel.setValueAt(number, rowCount - 1, 0);
            tableModel.setValueAt(objectToString(peitaoBean.getName()), rowCount - 1, 1);
            tableModel.setValueAt(objectToString(peitaoBean.getMtype()), rowCount - 1, 2);
            tableModel.setValueAt(objectToString(peitaoBean.getCount()), rowCount - 1, 3);
            tableModel.setValueAt(objectToString(peitaoBean.getXhph()), rowCount - 1, 4);
            tableModel.setValueAt(objectToString(peitaoBean.getGg()), rowCount - 1, 5);
            tableModel.setValueAt(objectToString(peitaoBean.getVersion()), rowCount - 1, 6);
            tableModel.setValueAt(objectToString(peitaoBean.getJstj()), rowCount - 1, 9);
            tableModel.setValueAt(objectToString(peitaoBean.getCountry()), rowCount - 1, 10);
            tableModel.setValueAt(objectToString(peitaoBean.getPartNumber()), rowCount - 1, 11);
            tableModel.setValueAt(objectToString(peitaoBean.getUnit()), rowCount - 1, 7);
            tableModel.setValueAt(objectToString(peitaoBean.getRemark()), rowCount - 1, 8);
            tableModel.setValueAt(objectToString(peitaoBean.getZldj()), rowCount - 1, 12);
            tableModel.setValueAt(objectToString(peitaoBean.getDataFrom()), rowCount - 1, 13);
            tableModel.setValueAt(objectToString(peitaoBean.getXncs()), rowCount - 1, 14);
        }

    }

    private void loadPTaoData(Element techEle, Map<String, PTaoBean> peitaoMap) {
        List<Element> list = XmlUtility.getPeiTaoListTableElements(techEle);
        PTaoBean pTaoBean;
        for(Element element : list) {
            String number = element.attributeValue("number");
            String name = element.attributeValue("name");
            String mtype = element.attributeValue("MTYPE");
            String useCount = element.attributeValue("useCount");
            String xhph = element.attributeValue("XHPH");
            String csize = element.attributeValue("CSIZE");
            String version = element.attributeValue("version");
            String jstj = element.attributeValue("jstj");
            String gys = element.attributeValue("gys");
            String partNumber = element.attributeValue("partNumber");
            String comment = element.attributeValue("comment");
            String zldj = element.attributeValue("zldj");
            String dataFrom = element.attributeValue("dataFrom");
            String xncs = element.attributeValue("xncs");

            pTaoBean = new PTaoBean();
            pTaoBean.setNumber(number);
            pTaoBean.setName(name);
            pTaoBean.setMtype(mtype);
            pTaoBean.setCount(Double.valueOf(useCount));
            pTaoBean.setXhph(xhph);
            pTaoBean.setGg(csize);
            pTaoBean.setVersion(version);
            pTaoBean.setJstj(jstj);
            pTaoBean.setRemark(comment);
            pTaoBean.setZldj(zldj);
            pTaoBean.setCountry(gys);
            pTaoBean.setDataFrom(dataFrom);
            pTaoBean.setXncs(xncs);
            if("试件".equals(mtype)) {
                number += "_试件";
            }
            peitaoMap.put(number, pTaoBean);
        }
    }

    /**
     * 加载PBOM信息
     *
     * @param tableModel
     * @param techEle
     * @param dataMap
     * @return
     */
    private void loadPbomData(Element techEle, Map<String, PTaoBean> dataMap) {
        PTaoBean peitaoBean;
        String technicsType = techEle.attributeValue("technicsType");
        String zwptFlag = techEle.attributeValue("zwptFlag");
        //获取该零部件下所有子件（一层，标准件、自制件、元器件、外购件）信息
        XWTreeNode treeNode = frame.xwPartTreePanel.getSelectedTreeNode().getP();
        XWTreeObject partObj = treeNode.getObject();
        if(partObj instanceof XWPartTreeObject) {
            if("维修工艺".equals(technicsType) || "是".equals(zwptFlag)) {
                Element pele = partObj.getTreeCellData();
                // "序号", "编号", "名称", "零组件生产类型", "数量", "型号牌号", "规格", "版本"
                String partNumber = pele.attributeValue("partNumber");
                String partName = pele.attributeValue("partName");
                String gysl = BomXMLUtil.getGYSLFromWNC(pele, pele.attributeValue("partNumber"));
                if(gysl == null || "".equals(gysl)) {
                    gysl = pele.attributeValue("gysl");
                }
                if(gysl == null || "".equals(gysl)) {
                    gysl = pele.attributeValue("useCount");
                }
                if(gysl == null || gysl.isEmpty()) {
                    gysl = "0";
                }
                String xhph = pele.attributeValue("XHPH");
                String csize = pele.attributeValue("XHPH");
                String version = pele.attributeValue("version");
                String mtype = pele.attributeValue("MTYPE");
                if(mtype == null) {
                    mtype = "";
                }
                peitaoBean = new PTaoBean();
                peitaoBean.setNumber(partNumber);
                peitaoBean.setName(partName);
                peitaoBean.setCount(Double.valueOf(gysl));
                peitaoBean.setXhph(xhph);
                peitaoBean.setGg(csize);
                peitaoBean.setVersion(version);
                peitaoBean.setMtype(mtype);
                peitaoBean.setDataFrom("pbom");
                dataMap.put(partNumber, peitaoBean);
            }
            Enumeration<XWTreeNode> enu = treeNode.children();
            while(enu.hasMoreElements()) {
                XWTreeNode tNode = enu.nextElement();
                if(tNode.getObject() instanceof XWPartTreeObject) {
                    XWPartTreeObject childObj = (XWPartTreeObject) tNode.getObject();
                    Element pele = childObj.getTreeCellData();
                    // "序号", "编号", "名称", "零组件生产类型", "数量", "型号牌号", "规格", "版本"
                    String partNumber = pele.attributeValue("partNumber");
                    String partCindex = pele.attributeValue("CINDEX");
                    String partName = pele.attributeValue("partName");
                    String gysl = BomXMLUtil.getGYSLFromWNC(pele, pele.attributeValue("partNumber"));
                    if(gysl == null || "".equals(gysl)) {
                        gysl = pele.attributeValue("gysl");
                    }
                    if(gysl == null || "".equals(gysl)) {
                        gysl = pele.attributeValue("useCount");
                    }
                    String xhph = pele.attributeValue("XHPH");
                    String csize = pele.attributeValue("XHPH");
                    String version = pele.attributeValue("version");
                    String mtype = pele.attributeValue("MTYPE");
                    if(mtype == null) {
                        mtype = "";
                    }
                    peitaoBean = new PTaoBean();
                    if("外配套件".equals(mtype)) {
                        peitaoBean.setNumber(partCindex);
                    } else {
                        peitaoBean.setNumber(partNumber);
                    }
                    peitaoBean.setName(partName);
                    peitaoBean.setCount(Double.valueOf(gysl));
                    peitaoBean.setXhph(xhph);
                    peitaoBean.setGg(csize);
                    peitaoBean.setVersion(version);
                    peitaoBean.setMtype(mtype);
                    peitaoBean.setDataFrom("pbom");
                    if(!"标准件".equals(mtype) && !"元器件".equals(mtype)) {
                        dataMap.put(partNumber, peitaoBean);
                    }
                }
            }
        }
    }

    /**
     * 加载工艺定额信息
     *
     * @param tableModel
     * @param techEle
     * @param dataMap
     */
    private void loadGydeData(Element techEle, Map<String, PTaoBean> dataMap) {
        PTaoBean pTaoBean;
        Element gyde = XmlUtility.getTechnicsDEElement(techEle);
        if(gyde != null) {
            //从ERP查询添加
            List<Element> newPart = XmlUtility.getTechnicsGYDENewPart(gyde);
            for(Element element : newPart) {
                String number = element.attributeValue("number");
                if("".equals(number)) {
                    number = element.attributeValue("chbm");
                }
                String chmc = element.attributeValue("chmc");
                String sl = element.attributeValue("sl");
                if(sl == null || "".equals(sl)) {
                    sl = "0";
                }
                String xhph = element.attributeValue("xhph");
                String gg = element.attributeValue("gg");
                String xlcc = element.attributeValue("xlcc");
                String dw2 = element.attributeValue("dw2");
                String comment = element.attributeValue("comment");
                String jstj = element.attributeValue("jstj");
                String sccj = element.attributeValue("sccj");
                String zldj = element.attributeValue("zldj");
                String tabType = element.attributeValue("tabType");
                String xncs = element.attributeValue("xncs");
                String mtype = "";
                if("电子元器件".equals(tabType)) {
                    mtype = "元器件";
                    gg = element.attributeValue("fzxs");
                    comment = element.attributeValue("sfjdmg");
                    jstj = element.attributeValue("xxgf");
                    String wh = element.attributeValue("wh");
                    if(wh != null && !"".equals(wh)) {
                        gg = gg + "(位号:" + wh + ")";
                    }
                } else if("标准紧固件".equals(tabType)) {
                    mtype = "标准件";
                    xhph = element.attributeValue("cl");
                    xncs = element.attributeValue("jxxndj");
                    comment = element.attributeValue("bmcl") + " " + element.attributeValue("tssm");
                } else if("金属材料".equals(tabType)) {
                    if(!"".equals(xlcc)) {
                        gg = gg + "(下料尺寸:" + xlcc + ")";
                    }
                    xncs = element.attributeValue("gyztrcl");
                    comment = element.attributeValue("tssm");
                } else if("非金属材料".equals(tabType)) {
                    if(!"".equals(xlcc)) {
                        gg = gg + "(下料尺寸:" + xlcc + ")";
                    }
                    comment = element.attributeValue("tssm");
                } else if("复合材料".equals(tabType)) {
                    comment = element.attributeValue("tssm");
                } else if("机电材料".equals(tabType)) {
                    gg = element.attributeValue("xhgg");
                    comment = element.attributeValue("tssm");
                } else if("火工品".equals(tabType)) {
                    jstj = element.attributeValue("cpdh");
                    gg = element.attributeValue("zcsm");
                    xncs = element.attributeValue("tnt");
                    comment = element.attributeValue("zl") + " " + element.attributeValue("tssm");
                }

                if(!"".equals(number) && dataMap.containsKey(number)) {
                    pTaoBean = dataMap.get(number);
                    pTaoBean.setCount(pTaoBean.getCount() + Double.valueOf(sl));
                    pTaoBean.setGg(gg);
                    if(!"".equals(mtype)) {
                        pTaoBean.setMtype(mtype);
                    }
                } else {
                    pTaoBean = new PTaoBean();
                    pTaoBean.setNumber(number);
                    pTaoBean.setName(chmc);
                    pTaoBean.setCount(Double.valueOf(sl));
                    pTaoBean.setXhph(xhph);
                    pTaoBean.setGg(gg);
                    pTaoBean.setUnit(dw2);
                    pTaoBean.setRemark(comment);
                    pTaoBean.setJstj(jstj);
                    pTaoBean.setCountry(sccj);
                    pTaoBean.setZldj(zldj);
                    pTaoBean.setXncs(xncs);
                    if(!"".equals(mtype)) {
                        pTaoBean.setMtype(mtype);
                    }
                    pTaoBean.setDataFrom("gyde_new");
                    dataMap.put(number, pTaoBean);
                }
            }

            //从ERP匹配
            List<Element> matchPart = XmlUtility.getTechnicsGYDEMatchPart(gyde);
            for(Element element : matchPart) {
                String chbm = element.attributeValue("chbm");
                String number = element.attributeValue("number");
                String chmc = element.attributeValue("chmc");
                String gyCount = element.attributeValue("gyCount");
                String wzlb = element.attributeValue("wzlb");
                String lwgggcc;
                String jxxndj;
                String xhph;
                if(!"".equals(wzlb) && wzlb.startsWith("02")) {
                    if(element.attributeValue("xhph") == null) {
                        xhph = "";
                    } else {
                        xhph = element.attributeValue("xhph");
                    }
                    if(element.attributeValue("lwgggcc") == null) {
                        lwgggcc = "";
                    } else {
                        lwgggcc = element.attributeValue("lwgggcc");
                    }
                    if(element.attributeValue("jxxndj") == null) {
                        jxxndj = "";
                    } else {
                        jxxndj = element.attributeValue("jxxndj");
                    }
                    xhph = xhph + " " + lwgggcc + "  " + jxxndj;
                } else {
                    xhph = element.attributeValue("xhph");
                }
                String gg = element.attributeValue("gg");
                String dw2 = element.attributeValue("dw2");
                String comment = element.attributeValue("comment");
                String jstj = element.attributeValue("jstj");
                String sccj = element.attributeValue("sccj");
                String zldj = element.attributeValue("zldj");
                String xncs = element.attributeValue("xncs");

                if(dataMap.containsKey(number)) {
                    dataMap.remove(number);
                }

                if(dataMap.containsKey(chbm)) {
                    pTaoBean = dataMap.get(chbm);
                    pTaoBean.setCount(pTaoBean.getCount() + Double.valueOf(gyCount));
                } else {
                    pTaoBean = new PTaoBean();
                    pTaoBean.setNumber(chbm);
                    pTaoBean.setName(chmc);
                    pTaoBean.setCount(Double.valueOf(gyCount));
                    pTaoBean.setXhph(xhph);
                    pTaoBean.setGg(gg);
                    pTaoBean.setUnit(dw2);
                    pTaoBean.setRemark(comment);
                    pTaoBean.setJstj(jstj);
                    pTaoBean.setCountry(sccj);
                    pTaoBean.setZldj(zldj);
                    pTaoBean.setXncs(xncs);
                    pTaoBean.setDataFrom("gyde_match");

                    dataMap.put(chbm, pTaoBean);
                }
            }
            //主要材料
            List<Element> zyclde = XmlUtility.getTechnicsZYCLDE(gyde);
            for(Element element : zyclde) {
                String chbm = element.attributeValue("chbm");
                String chmc = element.attributeValue("chmc");
                String sl = element.attributeValue("sl");
                if(sl == null || "".equals(sl)) {
                    sl = "0";
                }
                String xhph = element.attributeValue("xhph");
                String gg = element.attributeValue("gg");
                String xlcc = element.attributeValue("xlcc");
                String dw = element.attributeValue("dw");
                String comment = element.attributeValue("comment");
                String jstj = element.attributeValue("jstj");
                String sccj = element.attributeValue("sccj");
                String zldj = element.attributeValue("zldj");
                String xncs = element.attributeValue("xncs");
                String mtype = "主要材料";
                String tabType = element.attributeValue("tabType");
                if("电子元器件".equals(tabType)) {
                    gg = element.attributeValue("fzxs");
                    comment = element.attributeValue("sfjdmg");
                    jstj = element.attributeValue("xxgf");
                    String wh = element.attributeValue("wh");
                    if(wh != null && !"".equals(wh)) {
                        gg = gg + "(位号:" + wh + ")";
                    }
                } else if("标准紧固件".equals(tabType)) {
                    xhph = element.attributeValue("cl");
                    xncs = element.attributeValue("jxxndj");
                    comment = element.attributeValue("bmcl") + " " + element.attributeValue("tssm");
                } else if("金属材料".equals(tabType)) {
                    if(!"".equals(xlcc)) {
                        gg = gg + "(下料尺寸:" + xlcc + ")";
                    }
                    xncs = element.attributeValue("gyztrcl");
                    comment = element.attributeValue("tssm");
                } else if("非金属材料".equals(tabType)) {
                    if(!"".equals(xlcc)) {
                        gg = gg + "(下料尺寸:" + xlcc + ")";
                    }
                    comment = element.attributeValue("tssm");
                } else if("复合材料".equals(tabType)) {
                    comment = element.attributeValue("tssm");
                } else if("机电材料".equals(tabType)) {
                    gg = element.attributeValue("xhgg");
                    comment = element.attributeValue("tssm");
                } else if("火工品".equals(tabType)) {
                    jstj = element.attributeValue("cpdh");
                    gg = element.attributeValue("zcsm");
                    xncs = element.attributeValue("tnt");
                    comment = element.attributeValue("zl") + " " + element.attributeValue("tssm");
                }

                if(dataMap.containsKey(chbm)) {
                    pTaoBean = dataMap.get(chbm);
                    pTaoBean.setCount(pTaoBean.getCount() + Double.valueOf(sl));
                } else {
                    pTaoBean = new PTaoBean();
                    pTaoBean.setNumber(chbm);
                    pTaoBean.setName(chmc);
                    pTaoBean.setCount(Double.valueOf(sl));
                    pTaoBean.setXhph(xhph);
                    pTaoBean.setGg(gg);
                    pTaoBean.setUnit(dw);
                    pTaoBean.setRemark(comment);
                    pTaoBean.setJstj(jstj);
                    pTaoBean.setCountry(sccj);
                    pTaoBean.setZldj(zldj);
                    pTaoBean.setMtype(mtype);
                    pTaoBean.setXncs(xncs);
                    pTaoBean.setDataFrom("zyclde");

                    dataMap.put(chbm, pTaoBean);

                }
            }
            //试件原材料
            List<Element> sjyclde = XmlUtility.getTechnicsSJYCLDE(gyde);
            for(Element element : sjyclde) {
                String chbm = element.attributeValue("chbm");
                String chmc = element.attributeValue("chmc");
                String sjsl = element.attributeValue("sjsl");
                String xhph = element.attributeValue("xhph");
                String gg = element.attributeValue("gg");
                String dw = element.attributeValue("dw");
                String comment = element.attributeValue("comment");
                String jstj = element.attributeValue("jstj");
                String sccj = element.attributeValue("sccj");
                String zldj = element.attributeValue("zldj");
                String xlcc = element.attributeValue("xlcc");
                String xncs = element.attributeValue("xncs");
                String mtype = "试件";
                String tabType = element.attributeValue("tabType");
                if("电子元器件".equals(tabType)) {
                    gg = element.attributeValue("fzxs");
                    comment = element.attributeValue("sfjdmg");
                    jstj = element.attributeValue("xxgf");
                    String wh = element.attributeValue("wh");
                    if(wh != null && !"".equals(wh)) {
                        gg = gg + "(位号:" + wh + ")";
                    }
                } else if("标准紧固件".equals(tabType)) {
                    xhph = element.attributeValue("cl");
                    xncs = element.attributeValue("jxxndj");
                    comment = element.attributeValue("bmcl") + " " + element.attributeValue("tssm");
                } else if("金属材料".equals(tabType)) {
                    if(!"".equals(xlcc)) {
                        gg = gg + "(下料尺寸:" + xlcc + ")";
                    }
                    xncs = element.attributeValue("gyztrcl");
                    comment = element.attributeValue("tssm");
                } else if("非金属材料".equals(tabType)) {
                    if(!"".equals(xlcc)) {
                        gg = gg + "(下料尺寸:" + xlcc + ")";
                    }
                    comment = element.attributeValue("tssm");
                } else if("复合材料".equals(tabType)) {
                    comment = element.attributeValue("tssm");
                } else if("机电材料".equals(tabType)) {
                    gg = element.attributeValue("xhgg");
                    comment = element.attributeValue("tssm");
                } else if("火工品".equals(tabType)) {
                    jstj = element.attributeValue("cpdh");
                    gg = element.attributeValue("zcsm");
                    xncs = element.attributeValue("tnt");
                    comment = element.attributeValue("zl") + " " + element.attributeValue("tssm");
                }

                if("".equals(sjsl) || "null".equals(sjsl) || sjsl == null) {
                    sjsl = element.attributeValue("sl");
                }
                if(sjsl == null || "".equals(sjsl)) {
                    sjsl = "0";
                }
                /*if(dataMap.containsKey(chbm)){
                    pTaoBean = dataMap.get(chbm);
                    pTaoBean.setCount(pTaoBean.getCount() + Double.valueOf(sjsl));
                }else{*/
                pTaoBean = new PTaoBean();
                pTaoBean.setNumber(chbm);
                pTaoBean.setName(chmc);
                pTaoBean.setCount(Double.valueOf(sjsl));
                pTaoBean.setXhph(xhph);
                pTaoBean.setGg(gg);
                pTaoBean.setUnit(dw);
                pTaoBean.setRemark(comment);
                pTaoBean.setJstj(jstj);
                pTaoBean.setCountry(sccj);
                pTaoBean.setZldj(zldj);
                pTaoBean.setMtype(mtype);
                pTaoBean.setXncs(xncs);
                pTaoBean.setDataFrom("sjyclde");

                dataMap.put(chbm + "_试件", pTaoBean);
                //}
            }

            //设计资源库集成获取的数据
            List<Element> newSjzykParts = XmlUtility.getTechnicsSJZYKGYDENewPart(gyde);
            if(newSjzykParts != null && !newSjzykParts.isEmpty()) {
                for(Element element : newSjzykParts) {
                    String number = element.attributeValue("sjbm");
                    String name = element.attributeValue("name");
                    String dataType = element.attributeValue("dataType");
                    String mtype = "";
                    Double count = Double.valueOf(element.attributeValue("gysl"));
                    String xhph;
                    String gg;
                    String version = "";
                    String dw = element.attributeValue("dw");
                    String comment = element.attributeValue("comment");
                    String jstj = element.attributeValue("bzh");
                    String country = element.attributeValue("gys");
                    String xncs = element.attributeValue("xncs");
                    String partNumber = "";
                    String zldj = element.attributeValue("zldj");
                    if("标准件".equals(dataType)) {
                        mtype = "标准件";
                        String cl;
                        if(element.attributeValue("cl") == null) {
                            cl = "";
                        } else {
                            cl = element.attributeValue("cl");
                        }
                        String jxxndjhyd;
                        if(element.attributeValue("jxxndjhyd") == null) {
                            jxxndjhyd = "";
                        } else {
                            jxxndjhyd = element.attributeValue("jxxndjhyd");
                        }
                        xhph = cl + "  " + jxxndjhyd;
                        gg = element.attributeValue("gg");
                    } else {
                        mtype = "元器件";
                        xhph = element.attributeValue("xh");
                        gg = element.attributeValue("xhgg");
                    }

                    if(dataMap.containsKey(number)) {
                        pTaoBean = dataMap.get(number);
                        pTaoBean.setCount(pTaoBean.getCount() + Double.valueOf(count));
                    } else {
                        pTaoBean = new PTaoBean();
                        pTaoBean.setNumber(number);
                        pTaoBean.setName(name);
                        pTaoBean.setCount(count);
                        pTaoBean.setXhph(xhph);
                        pTaoBean.setGg(gg);
                        pTaoBean.setUnit(dw);
                        pTaoBean.setRemark(comment);
                        pTaoBean.setJstj(jstj);
                        pTaoBean.setCountry(country);
                        pTaoBean.setZldj(zldj);
                        pTaoBean.setMtype(mtype);
                        pTaoBean.setXncs(xncs);
                        pTaoBean.setDataFrom("gyde_new_sjzyk");

                        dataMap.put(number, pTaoBean);
                    }

                }
            }

            //设计资源库集成匹配数据
            newSjzykParts = XmlUtility.getTechnicsSJZYKGYDEMatchPart(gyde);
            if(newSjzykParts != null && !newSjzykParts.isEmpty()) {
                for(Element element : newSjzykParts) {
                    String partNumber = element.attributeValue("partNumber");
                    String number = element.attributeValue("sjbm");
                    String name = element.attributeValue("name");
                    String dataType = element.attributeValue("dataType");
                    String mtype = "";
                    Double count = Double.valueOf(element.attributeValue("gysl"));
                    String xhph;
                    String gg;
                    String version = "";
                    String dw = element.attributeValue("dw");
                    String comment = element.attributeValue("comment");
                    String jstj = element.attributeValue("bzh");
                    String country = element.attributeValue("gys");
                    String zldj = element.attributeValue("zldj");
                    String xncs = element.attributeValue("xncs");
                    if("标准件".equals(dataType)) {
                        mtype = "标准件";
                        String cl;
                        if(element.attributeValue("cl") == null) {
                            cl = "";
                        } else {
                            cl = element.attributeValue("cl");
                        }
                        String jxxndjhyd;
                        if(element.attributeValue("jxxndjhyd") == null) {
                            jxxndjhyd = "";
                        } else {
                            jxxndjhyd = element.attributeValue("jxxndjhyd");
                        }
                        xhph = cl + "  " + jxxndjhyd;
                        gg = element.attributeValue("gg");
                    } else {
                        mtype = "元器件";
                        xhph = element.attributeValue("xh");
                        gg = element.attributeValue("xhgg");
                    }

                    if(dataMap.containsKey(partNumber)) {
                        dataMap.remove(partNumber);
                    }

                    if(dataMap.containsKey(number)) {
                        pTaoBean = dataMap.get(number);
                        pTaoBean.setCount(pTaoBean.getCount() + Double.valueOf(count));
                    } else {
                        pTaoBean = new PTaoBean();
                        pTaoBean.setNumber(number);
                        pTaoBean.setName(name);
                        pTaoBean.setCount(count);
                        pTaoBean.setXhph(xhph);
                        pTaoBean.setGg(gg);
                        pTaoBean.setUnit(dw);
                        pTaoBean.setRemark(comment);
                        pTaoBean.setJstj(jstj);
                        pTaoBean.setCountry(country);
                        pTaoBean.setZldj(zldj);
                        pTaoBean.setMtype(mtype);
                        pTaoBean.setPartNumber(partNumber);
                        pTaoBean.setXncs(xncs);
                        pTaoBean.setDataFrom("gyde_match_sjzyk");

                        dataMap.put(number, pTaoBean);
                    }

                }
            }
        }
    }

    /**
     * 加载材料定额信息
     * 1、加载主要材料信息
     * 2、加载试件原材料信息
     *
     * @param tableModel 表格
     * @param techEle    工艺element
     * @param dataMap
     */
    private void loadCldeData(Element techEle, Map<String, PTaoBean> dataMap) {
        PTaoBean pTaoBean;
        Element clde = XmlUtility.getTechnicsCLDEElement(techEle);
        if(clde != null) {
            //原材料定额
            //List<Element> yclde = XmlUtility.getTechnicsYCLDE(clde);
            //主要材料定额
            List<Element> zyclde = XmlUtility.getTechnicsZYCLDE(clde);
            for(Element element : zyclde) {
                String chbm = element.attributeValue("chbm");
                String chmc = element.attributeValue("chmc");
                String sl = element.attributeValue("sl");
                if(sl == null || "".equals(sl)) {
                    sl = "0";
                }
                String xhph = element.attributeValue("xhph");
                String gg = element.attributeValue("gg");
                String xlcc = element.attributeValue("xlcc");
                String dw = element.attributeValue("dw");
                String comment = element.attributeValue("comment");
                String jstj = element.attributeValue("jstj");
                String sccj = element.attributeValue("sccj");
                String zldj = element.attributeValue("zldj");
                String xncs = element.attributeValue("xncs");
                String mtype = "主要材料";
                String tabType = element.attributeValue("tabType");
                if("电子元器件".equals(tabType)) {
                    gg = element.attributeValue("fzxs");
                    comment = element.attributeValue("sfjdmg");
                    jstj = element.attributeValue("xxgf");
                    String wh = element.attributeValue("wh");
                    if(wh != null && !"".equals(wh)) {
                        gg = gg + "(位号:" + wh + ")";
                    }
                } else if("标准紧固件".equals(tabType)) {
                    xhph = element.attributeValue("cl");
                    xncs = element.attributeValue("jxxndj");
                    comment = element.attributeValue("bmcl") + " " + element.attributeValue("tssm");
                } else if("金属材料".equals(tabType)) {
                    if(!"".equals(xlcc)) {
                        gg = gg + "(下料尺寸:" + xlcc + ")";
                    }
                    xncs = element.attributeValue("gyztrcl");
                    comment = element.attributeValue("tssm");
                } else if("非金属材料".equals(tabType)) {
                    if(!"".equals(xlcc)) {
                        gg = gg + "(下料尺寸:" + xlcc + ")";
                    }
                    comment = element.attributeValue("tssm");
                } else if("复合材料".equals(tabType)) {
                    comment = element.attributeValue("tssm");
                } else if("机电材料".equals(tabType)) {
                    gg = element.attributeValue("xhgg");
                    comment = element.attributeValue("tssm");
                } else if("火工品".equals(tabType)) {
                    jstj = element.attributeValue("cpdh");
                    gg = element.attributeValue("zcsm");
                    xncs = element.attributeValue("tnt");
                    comment = element.attributeValue("zl") + " " + element.attributeValue("tssm");
                }

                if(dataMap.containsKey(chbm)) {
                    pTaoBean = dataMap.get(chbm);
                    pTaoBean.setCount(pTaoBean.getCount() + Double.valueOf(sl));
                } else {
                    pTaoBean = new PTaoBean();
                    pTaoBean.setNumber(chbm);
                    pTaoBean.setName(chmc);
                    pTaoBean.setCount(Double.valueOf(sl));
                    pTaoBean.setXhph(xhph);
                    pTaoBean.setGg(gg);
                    pTaoBean.setUnit(dw);
                    pTaoBean.setRemark(comment);
                    pTaoBean.setJstj(jstj);
                    pTaoBean.setCountry(sccj);
                    pTaoBean.setZldj(zldj);
                    pTaoBean.setMtype(mtype);
                    pTaoBean.setXncs(xncs);
                    pTaoBean.setDataFrom("zyclde");
                    dataMap.put(chbm, pTaoBean);
                }
            }
            //试件原材料定额
            List<Element> sjyclde = XmlUtility.getTechnicsSJYCLDE(clde);
            for(Element element : sjyclde) {
                String chbm = element.attributeValue("chbm");
                String chmc = element.attributeValue("chmc");
                String sjsl = element.attributeValue("sjsl");
                if("".equals(sjsl) || "null".equals(sjsl) || sjsl == null) {
                    sjsl = element.attributeValue("sl");
                }
                if(sjsl == null || "".equals(sjsl)) {
                    sjsl = "0";
                }
                String xhph = element.attributeValue("xhph");
                String gg = element.attributeValue("gg");
                String xlcc = element.attributeValue("xlcc");
                String dw = element.attributeValue("dw");
                String comment = element.attributeValue("comment");
                String jstj = element.attributeValue("jstj");
                String sccj = element.attributeValue("sccj");
                String zldj = element.attributeValue("zldj");
                String xncs = element.attributeValue("xncs");
                String mtype = "试件";
                String tabType = element.attributeValue("tabType");
                if("电子元器件".equals(tabType)) {
                    gg = element.attributeValue("fzxs");
                    comment = element.attributeValue("sfjdmg");
                    jstj = element.attributeValue("xxgf");
                    String wh = element.attributeValue("wh");
                    if(wh != null && !"".equals(wh)) {
                        gg = gg + "(位号:" + wh + ")";
                    }
                } else if("标准紧固件".equals(tabType)) {
                    xhph = element.attributeValue("cl");
                    xncs = element.attributeValue("jxxndj");
                    comment = element.attributeValue("bmcl") + " " + element.attributeValue("tssm");
                } else if("金属材料".equals(tabType)) {
                    if(!"".equals(xlcc)) {
                        gg = gg + "(下料尺寸:" + xlcc + ")";
                    }
                    xncs = element.attributeValue("gyztrcl");
                    comment = element.attributeValue("tssm");
                } else if("非金属材料".equals(tabType)) {
                    if(!"".equals(xlcc)) {
                        gg = gg + "(下料尺寸:" + xlcc + ")";
                    }
                    comment = element.attributeValue("tssm");
                } else if("复合材料".equals(tabType)) {
                    comment = element.attributeValue("tssm");
                } else if("机电材料".equals(tabType)) {
                    gg = element.attributeValue("xhgg");
                    comment = element.attributeValue("tssm");
                } else if("火工品".equals(tabType)) {
                    jstj = element.attributeValue("cpdh");
                    gg = element.attributeValue("zcsm");
                    xncs = element.attributeValue("tnt");
                    comment = element.attributeValue("zl") + " " + element.attributeValue("tssm");
                }

                /*if(dataMap.containsKey(chbm)){
                    pTaoBean = dataMap.get(chbm);
                    pTaoBean.setCount(pTaoBean.getCount() + Double.valueOf(sjsl));
                }else{*/
                pTaoBean = new PTaoBean();
                pTaoBean.setNumber(chbm);
                pTaoBean.setName(chmc);
                pTaoBean.setCount(Double.valueOf(sjsl));
                pTaoBean.setXhph(xhph);
                pTaoBean.setGg(gg);
                pTaoBean.setUnit(dw);
                pTaoBean.setRemark(comment);
                pTaoBean.setJstj(jstj);
                pTaoBean.setCountry(sccj);
                pTaoBean.setZldj(zldj);
                pTaoBean.setMtype(mtype);
                pTaoBean.setXncs(xncs);
                pTaoBean.setDataFrom("sjyclde");
                dataMap.put(chbm + "_试件", pTaoBean);
                // }
            }
        }
    }

    private void addOneRow(DefaultTableModel tableModel) {
        Vector vector = new Vector();
        for(int i = 0; i < tableModel.getColumnCount(); i++) {
            vector.add("");
        }
        tableModel.addRow(vector);
    }

    private String objectToString(Object object) {
        if(object != null) {
            return object.toString();
        }
        return "";
    }

    public void setUIEnabled(boolean b) {
        initialData.setEnabled(b);
        cleanFrom.setEnabled(b);
        shangyi.setEnabled(b);
        xiayi.setEnabled(b);
        save.setEnabled(b);
        removeButon.setEnabled(b);
        peiTaoTable.setEnabled(b);
    }
}
