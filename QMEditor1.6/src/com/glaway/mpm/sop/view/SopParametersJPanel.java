package com.glaway.mpm.sop.view;

import com.glaway.mpm.parameter.commonui.CommonTableModel;
import com.glaway.mpm.sop.intf.SopIntf;
import com.glaway.mpm.sop.model.SopResourceBean;
import com.glaway.mpm.sop.util.SopXMLUtility;
import com.glaway.mpm.util.*;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.wcIntf.TechnicsIntf;

import com.ptc.windchill.mpml.resource.MPMTooling;
import ext.casc.util.ExcelFileGenerator;
import ext.casc.util.IBAUtility;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.dom4j.Element;

import wt.part.WTPart;
import wt.util.WTException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SopParametersJPanel extends JPanel implements ActionListener {

    private NewTechnicsPart frame;
    private static final String FILTER = "xls";

    private Element technicsElement;
    //表格页面
    private JPanel tablePanel = null;
    //按钮页面
    private JPanel buttonPanel = null;
    //添加按钮
    private JButton addButton = null;
    //移除按钮
    private JButton removeButton = null;
    //手动录入按钮
    private JButton inputButton = null;
    //导入按钮
    private JButton importButton = null;
    //导出按钮
    private JButton exportButton = null;
    //展示表格
    private JTable table = null;
    private DefaultTableModel tableModel = null;

    public static final int COL_OID = 0;
    public static final int COL_NUMBER = 1;
    public static final int COL_ZYLB = 2;
    public static final int COL_CSXMMC = 3;
    public static final int COL_GXMC = 4;
    public static final int COL_WZLB = 5;
    public static final int COL_CSZ = 6;
    public static final int COL_SM = 7;
    public static final int COL_OBJ = 8;
    public static final int COL_ISNEW = 9;

    public static final String SOP_IBA_SPECIALIZEDTYPE = "SpecializedType";
    public static final String SOP_IBA_PROCEDUCENAME = "ProceduceName";
    public static final String SOP_IBA_MATERIALCATEGORY = "MaterialCategory";
    public static final String SOP_IBA_CANSHUZHI = "CANSHUZHI";
    public static final String SOP_IBA_PARAMETERSNAME = "ParametersName";
    public static final String SOP_IBA_REMARK = "REMARK";
    public static final String SOP_CONTAINER_GYZYK = "工艺资源库";
    public static final String SOP_TYPE_PARAMETERS = "casc.sast.149.Parameters";

    public SopParametersJPanel(NewTechnicsPart frame) {
        this.frame = frame;
        initComponent();
        setEditable();
    }

    private void initComponent() {
        JScrollPane tableScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        String[] header = {"oid", "编号", "专业类别", "参数项目名称", "工序名称", "物资类别", "参数值", "说明", "obj", "isNew"};
        Class<?>[] colClass = {String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, Object.class, String.class};
        tableModel = new CommonTableModel(header, colClass, new int[]{7});
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.getTableHeader().setReorderingAllowed(false);
        CommonUIUtil.hiddenCell(table, COL_OID);
        CommonUIUtil.hiddenCell(table, COL_OBJ);
        CommonUIUtil.hiddenCell(table, COL_ISNEW);
        JViewport viewport = new JViewport();
        viewport.add(table.getTableHeader());
        tableScrollPane.setColumnHeader(viewport);
        tableScrollPane.setViewportView(table);
        tableScrollPane.setPreferredSize(new Dimension(600, 390));

        addButton = new JButton("添加");
        addButton.setPreferredSize(new Dimension(90, 25));
        addButton.addActionListener(this);
        removeButton = new JButton("移除");
        removeButton.setPreferredSize(new Dimension(90, 25));
        removeButton.addActionListener(this);
        inputButton = new JButton("手动录入");
        inputButton.setPreferredSize(new Dimension(90, 25));
        inputButton.addActionListener(this);
        importButton = new JButton("导入");
        importButton.setPreferredSize(new Dimension(90, 25));
        importButton.addActionListener(this);
        exportButton = new JButton("导出");
        exportButton.setPreferredSize(new Dimension(90, 25));
        exportButton.addActionListener(this);

        tablePanel = new JPanel();
        tablePanel.setPreferredSize(new Dimension(600, 400));
        tablePanel.add(tableScrollPane);
        buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(120, 400));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(inputButton);
        buttonPanel.add(importButton);
        buttonPanel.add(exportButton);

        this.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        this.add(tablePanel);
        this.add(buttonPanel);
    }

    private void setEditable() {
//    	 technicsElement = XmlUtility.getTechnicsElement(frame.getCurrentTechnics());
//         Element sopElement = SopXMLUtility.getSOPElement(technicsElement);
//    	 String technicsNumber = sopElement.elementText("number");
//         String technicsCategory = sopElement.elementText("technicsCategory");
//    	 	String docState = "";
// 		try {
// 			docState = TechnicsIntf.getDocumentStateByNumber(technicsNumber);
// 		} catch (RemoteException e) {
// 			e.printStackTrace();
// 		} catch (WTException e) {
// 			e.printStackTrace();
// 		} catch (InvocationTargetException e) {
// 			e.printStackTrace();
// 		}
//         if ("SOPDoc".equals(technicsCategory) && !"".equals(docState) && !"正在工作".equals(docState) && !"修改中".equals(docState)) {
//        	 addButton.setEnabled(false);
//        	 removeButton.setEnabled(false);
//         }
    }

    /**
     * 加载数据
     */
    public void loadInitData(Element technicsElement) {
        Element sopElement = SopXMLUtility.getSOPElement(technicsElement);
        Element parameterElement = SopXMLUtility.getParameterElement(sopElement);
        List<Element> parameterInfo = SopXMLUtility.getParameterInfo(sopElement);
        List<SopResourceBean> resourceBeanList = new ArrayList<SopResourceBean>();
        SopResourceBean sopResourceBean;
        if (parameterInfo != null && parameterInfo.size() > 0) {
            for (Element element : parameterInfo) {
                sopResourceBean = new SopResourceBean();
                sopResourceBean.setOid(element.attributeValue("oid"));
                sopResourceBean.setNumber(element.attributeValue("number"));
                sopResourceBean.setName(element.attributeValue("name"));
                sopResourceBean.setSpecializedType(element.attributeValue("specializedType"));
                sopResourceBean.setProcedureName(element.attributeValue("procedureName"));
                sopResourceBean.setMaterialCategory(element.attributeValue("materialCategory"));
                sopResourceBean.setCanshuzhi(element.attributeValue("canshuzhi"));
                sopResourceBean.setDescription(element.attributeValue("description"));
                sopResourceBean.setIsNew(element.attributeValue("isNew"));
                resourceBeanList.add(sopResourceBean);
            }
        }

        setLoadTableValues(resourceBeanList);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (addButton == e.getSource()) {
            new AddSopParametersDialog(frame, this);
        } else if (removeButton == e.getSource()) {
            removeTableData();
        } else if (inputButton == e.getSource()) {
            new InputParametersDialog(frame, this);
        } else if (importButton == e.getSource()) {
            importData();
        } else if (exportButton == e.getSource()) {
            try {
                exportData();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    /**
     * 移除
     */
    private void removeTableData() {
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "请选择需要移除的行！");
            return;
        }
        List<String> oidList = new ArrayList<String>();
        for (int i = 0; i < selectedRows.length; i++) {
            String oid = (String) tableModel.getValueAt(selectedRows[i] - i, COL_OID);
            tableModel.removeRow(selectedRows[i] - i);
            oidList.add(oid);
        }
        removeFromXml(oidList);
    }

    /**
     * 从xml中移除
     *
     * @param oidList
     */
    private void removeFromXml(List<String> oidList) {
        technicsElement = XmlUtility.getTechnicsElement(frame.getCurrentTechnics());
        Element sopElement = SopXMLUtility.getSOPElement(technicsElement);
        Element parameterElement = SopXMLUtility.getParameterElement(sopElement);
        List<Element> parameterInfo = SopXMLUtility.getParameterInfo(sopElement);
        for (Element element : parameterInfo) {
            String oid = element.attributeValue("oid");
            if (oidList.contains(oid)) {
                parameterElement.remove(element);
            }
        }
        frame.saveProcess(technicsElement);
    }

    /**
     * 加载表格数据
     *
     * @param resourceBeanList
     * @return
     */
    public String setTableValues(List<SopResourceBean> resourceBeanList) {
        technicsElement = XmlUtility.getTechnicsElement(frame.getCurrentTechnics());
        List<String> oidList = new ArrayList<String>();
        for (int j = 0; j < tableModel.getRowCount(); j++) {
            String oid = (String) tableModel.getValueAt(j, COL_OID);
            oidList.add(oid);
        }
        SopResourceBean sopResourceBean;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < resourceBeanList.size(); i++) {
            sopResourceBean = resourceBeanList.get(i);
            if (oidList.contains(sopResourceBean.getOid())) {

                if (sb.toString().isEmpty()) {
                    sb.append(sopResourceBean.getName());
                } else {
                    sb.append(",").append(sopResourceBean.getName());
                }
                continue;
            }
            CommonUIUtil.addOneRow(tableModel);
            int row = tableModel.getRowCount() - 1;
            tableModel.setValueAt(sopResourceBean.getOid(), row, COL_OID);
            tableModel.setValueAt(sopResourceBean.getNumber(), row, COL_NUMBER);
            tableModel.setValueAt(sopResourceBean.getSpecializedType(), row, COL_ZYLB);
            tableModel.setValueAt(sopResourceBean.getName(), row, COL_CSXMMC);
            tableModel.setValueAt(sopResourceBean.getProcedureName(), row, COL_GXMC);
            tableModel.setValueAt(sopResourceBean.getMaterialCategory(), row, COL_WZLB);
            tableModel.setValueAt(sopResourceBean.getCanshuzhi(), row, COL_CSZ);
            tableModel.setValueAt(sopResourceBean.getDescription(), row, COL_SM);
            tableModel.setValueAt(sopResourceBean, row, COL_OBJ);
            tableModel.setValueAt(sopResourceBean.getIsNew(), row, COL_ISNEW);
            saveToXml(sopResourceBean, technicsElement);
        }
        frame.saveProcess(technicsElement);
        return sb.toString();
    }

    /**
     * 加载表格数据
     *
     * @param resourceBeanList
     * @return
     */
    public String setLoadTableValues(List<SopResourceBean> resourceBeanList) {
        technicsElement = XmlUtility.getTechnicsElement(frame.getCurrentTechnics());
        List<String> oidList = new ArrayList<String>();
        for (int j = 0; j < tableModel.getRowCount(); j++) {
            String oid = (String) tableModel.getValueAt(j, COL_OID);
            oidList.add(oid);
        }
        SopResourceBean sopResourceBean;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < resourceBeanList.size(); i++) {
            sopResourceBean = resourceBeanList.get(i);
            if (oidList.contains(sopResourceBean.getOid())) {

                if (sb.toString().isEmpty()) {
                    sb.append(sopResourceBean.getName());
                } else {
                    sb.append(",").append(sopResourceBean.getName());
                }
                continue;
            }
            CommonUIUtil.addOneRow(tableModel);
            int row = tableModel.getRowCount() - 1;
            tableModel.setValueAt(sopResourceBean.getOid(), row, COL_OID);
            tableModel.setValueAt(sopResourceBean.getNumber(), row, COL_NUMBER);
            tableModel.setValueAt(sopResourceBean.getSpecializedType(), row, COL_ZYLB);
            tableModel.setValueAt(sopResourceBean.getName(), row, COL_CSXMMC);
            tableModel.setValueAt(sopResourceBean.getProcedureName(), row, COL_GXMC);
            tableModel.setValueAt(sopResourceBean.getMaterialCategory(), row, COL_WZLB);
            tableModel.setValueAt(sopResourceBean.getCanshuzhi(), row, COL_CSZ);
            tableModel.setValueAt(sopResourceBean.getDescription(), row, COL_SM);
            tableModel.setValueAt(sopResourceBean, row, COL_OBJ);
            tableModel.setValueAt(sopResourceBean.getIsNew(), row, COL_ISNEW);
        }
        return sb.toString();
    }


    /**
     * 保存到xml
     *
     * @param resourceBean
     * @param technicsElement
     */
    private void saveToXml(SopResourceBean resourceBean, Element technicsElement) {
        Element sopElement = SopXMLUtility.getSOPElement(technicsElement);
        Element parameterElement = SopXMLUtility.getParameterElement(sopElement);
        Element parameterInfo = SopXMLUtility.createParameterInfo(parameterElement);
        parameterInfo.addAttribute(SopResourceBean.OID, resourceBean.getOid());
        parameterInfo.addAttribute(SopResourceBean.NUMBER, resourceBean.getNumber());
        parameterInfo.addAttribute(SopResourceBean.NAME, resourceBean.getName());
        parameterInfo.addAttribute(SopResourceBean.SPECIALIZEDTYPE, resourceBean.getSpecializedType());
        parameterInfo.addAttribute(SopResourceBean.PROCEDURENAME, resourceBean.getProcedureName());
        parameterInfo.addAttribute(SopResourceBean.MATERIALCATEGORY, resourceBean.getMaterialCategory());
        parameterInfo.addAttribute(SopResourceBean.CANSHUZHI, resourceBean.getCanshuzhi());
        parameterInfo.addAttribute(SopResourceBean.DESCRIPTION, resourceBean.getDescription());
        parameterInfo.addAttribute(SopResourceBean.ISNEW, resourceBean.getIsNew());
        parameterElement.add(parameterInfo);
    }

    /**
     * 导出数据
     *
     * @throws Exception
     */
    public void exportData() throws Exception {
        ArrayList<String> tableModel = getTableModelList();
        ArrayList<ArrayList<String>> dataList = getDataList(table);
        ExcelFileGenerator excelFileGenerator = new ExcelFileGenerator(tableModel, dataList);
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.showSaveDialog(frame);
        File selectedFile = jFileChooser.getSelectedFile();
        if (selectedFile != null) {
            if ((selectedFile.getName()).toUpperCase().endsWith("XLS")) {
                excelFileGenerator.expordExcel(new FileOutputStream(selectedFile));
            } else {
                CommonUIUtil.showMessageDialog(null, "请选择XLS结尾的文件！", "提示", 1);
                return;
            }
        }
    }

    public static ArrayList<String> getTableModelList() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("专业类别");
        list.add("参数项目名称");
        list.add("工序名称");
        list.add("物资类别");
        list.add("参数值");
        list.add("说明");
        return list;
    }

    public static ArrayList<ArrayList<String>> getDataList(JTable table) {
        ArrayList<ArrayList<String>> dataList = new ArrayList<ArrayList<String>>();
        ArrayList<String> list = null;
        for (int i = 0; i < table.getRowCount(); i++) {
            list = new ArrayList<String>();
            list.add(CommonUtil.objectToString(table.getValueAt(i, COL_ZYLB)));
            list.add(CommonUtil.objectToString(table.getValueAt(i, COL_CSXMMC)));
            list.add(CommonUtil.objectToString(table.getValueAt(i, COL_GXMC)));
            list.add(CommonUtil.objectToString(table.getValueAt(i, COL_WZLB)));
            list.add(CommonUtil.objectToString(table.getValueAt(i, COL_CSZ)));
            list.add(CommonUtil.objectToString(table.getValueAt(i, COL_SM)));
            dataList.add(list);
        }
        if (table.getRowCount() == 0) {
            list = new ArrayList<String>();
            list.add("");
            list.add("");
            list.add("");
            list.add("");
            list.add("");
            list.add("");
            dataList.add(list);
        }
        return dataList;
    }

    public void importData() {
        File file = FileChooserTool.getFile(FILTER, null);
        if (file == null)
            return;
        String fileName = file.getName();
        if ((fileName.toUpperCase()).endsWith("XLS")) {
            try {
                InputStream is = new FileInputStream(file);
                HSSFWorkbook workbook = new HSSFWorkbook(is);
                HSSFSheet sheet = workbook.getSheetAt(0);
                int rows = sheet.getLastRowNum() - sheet.getFirstRowNum();
                Row row = null;
                List<SopResourceBean> resourceBeanList = new ArrayList<SopResourceBean>();
                StringBuffer sb = new StringBuffer();
                for (int i = 1; i <= rows; i++) {
                    row = sheet.getRow(i);
                    if (row == null) {
                        continue;
                    }
                    String zylb = DealFileUtil.getValue(row.getCell(0)).toString().trim();//专业类别
                    String csxmmc = DealFileUtil.getValue(row.getCell(1)).toString().trim();//参数项目名称
                    String gxmc = DealFileUtil.getValue(row.getCell(2)).toString().trim();//工序名称
                    String wzlb = DealFileUtil.getValue(row.getCell(3)).toString().trim();//物资类别
                    String csz = DealFileUtil.getValue(row.getCell(4)).toString().trim();//参数值
                    String desc = DealFileUtil.getValue(row.getCell(5)).toString().trim();//说明

                    if (zylb == null || "".equals(zylb)) {
                        sb.append("Excel第").append(i + 1).append("行专业类别为空;\n");
                        continue;
                    }
                    if (csxmmc == null || "".equals(csxmmc)) {
                        sb.append("Excel第").append(i + 1).append("行参数项目名称为空;\n");
                        continue;
                    }
                    if (gxmc == null || "".equals(gxmc)) {
                        sb.append("Excel第").append(i + 1).append("行工序名称为空;\n");
                        continue;
                    }
                    boolean istrue = false;
                    for (int j = 0; j < tableModel.getRowCount(); j++) {
                        String zz = (String) tableModel.getValueAt(j, COL_ZYLB);
                        String cc = (String) tableModel.getValueAt(j, COL_CSXMMC);
                        String gg = (String) tableModel.getValueAt(j, COL_GXMC);
                        if(zz.equals(zylb) && cc.equals(csxmmc) && gg.equals(gxmc)){
                            istrue = true;
                            sb.append("Excel第").append(i + 1).append("行是表格内存在已有的(" + zylb + "-" + csxmmc + "-" + gxmc+")专业类别、参数项目名称和工序名称组合值!\n");
                            break;
                        }
                    }
                    if(istrue){
                        continue;
                    }
                    HashMap<String, String> ibaMap = new HashMap<String, String>();
                    ibaMap.put(SOP_IBA_PARAMETERSNAME, csxmmc);
                    ibaMap.put(SOP_IBA_SPECIALIZEDTYPE, zylb);
                    ibaMap.put(SOP_IBA_PROCEDUCENAME, gxmc);
                    List<WTPart> wtPartList = SopIntf.searchLatestPartList(SOP_CONTAINER_GYZYK, null, csxmmc, null, ibaMap, true, SOP_TYPE_PARAMETERS);
                    if (wtPartList != null && wtPartList.size() > 0) {
                        for (WTPart part : wtPartList) {
                            if(part instanceof MPMTooling){
                                MPMTooling tooling = (MPMTooling) part;
                                if(tooling!=null){
                                    Map<String, String> map = SopIntf.getIBAMapByTooling(tooling);
                                    SopResourceBean bean = new SopResourceBean();
                                    bean.setOid(tooling.getPersistInfo().getObjectIdentifier().getId()+"");
                                    bean.setName(tooling.getName());
                                    bean.setNumber(tooling.getNumber());
                                    bean.setSpecializedType(map.get(SOP_IBA_SPECIALIZEDTYPE));
                                    bean.setProcedureName(map.get(SOP_IBA_PROCEDUCENAME));
                                    bean.setMaterialCategory(map.get(SOP_IBA_MATERIALCATEGORY));
                                    bean.setCanshuzhi(map.get(SOP_IBA_CANSHUZHI));
                                    bean.setDescription(map.get(SOP_IBA_REMARK));
                                    bean.setIsNew("false");
                                    resourceBeanList.add(bean);
                                }
                            }
                        }
                        continue;
                    }
                    SopResourceBean bean = new SopResourceBean();
                    bean.setOid(System.currentTimeMillis() + "");
                    bean.setName(csxmmc);
                    bean.setNumber("已生成");
                    bean.setSpecializedType(zylb);
                    bean.setProcedureName(gxmc);
                    bean.setMaterialCategory(wzlb);
                    bean.setCanshuzhi(csz);
                    bean.setDescription(desc);
                    bean.setIsNew("true");
                    resourceBeanList.add(bean);
                }
                String msg = setTableValues(resourceBeanList);
                if (!msg.isEmpty()) {
                    sb.append("参数项目[" + msg + "]已存在，未完成添加!\n");
                }
                if("".equals(sb.toString())){
                    CommonUIUtil.showMessageDialog(null, "导入完成！", "提示", 1);
                }else{
                    CommonUIUtil.showMessageDialog(null, sb.toString(), "提示", 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                CommonUIUtil.showMessageDialog(null, "读取文件信息异常！", "提示", 1);
            }
        } else {
            CommonUIUtil.showMessageDialog(null, "请选择xls结尾的excel文件！", "提示", 1);
            return;
        }
    }


    public void setUIEnabled(boolean b) {
        addButton.setEnabled(b);
        removeButton.setEnabled(b);
        inputButton.setEnabled(b);
        importButton.setEnabled(b);
        exportButton.setEnabled(b);
    }
}
