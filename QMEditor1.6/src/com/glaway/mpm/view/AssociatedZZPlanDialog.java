package com.glaway.mpm.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;

import com.glaway.mpm.mesParameter.model.GLZhuFuLink;
import org.apache.batik.gvt.font.Glyph;
import org.dom4j.Element;

import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.model.MainPlanProcedure;
import com.glaway.mpm.parameter.commonui.CommonTableModel;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.wcIntf.TechnicsIntf;

public class AssociatedZZPlanDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;
    private static VaLogger logger = VaLogger.getLogger(AssociatedZZPlanDialog.class);
    private NewTechnicsPart mainFrame = null;
    private Element technicsElement = null;
    private boolean editable = false;
    private JTable table = null;
    private CommonTableModel tableModel = null;
    private JButton addBtn = null;
    private JButton removeBtn = null;
    private JButton comfirmBtn = null;
    private JButton cancleBtn = null;

    private static final int COL_GWKEY = 0;
    private static final int COL_EDIT = 1;
    private static final int COL_DOCNUMBER = 2;
    private static final int COL_ZPLANNUMBER = 3;
    private static final int COL_ZVERSION = 5;
    private static final int COL_OPELABEL = 6;
    private static final int COL_CREATOR = 7;
    private static final int COL_CREATETIME = 8;
    private static final int COL_PCNO = 9;
    private Map<String, Vector<MainPlanProcedure>> cache = new HashMap<String, Vector<MainPlanProcedure>>();
    private Map<String, String> cache2;

    public AssociatedZZPlanDialog(Element technicsElement, NewTechnicsPart mainFrame, boolean editable) {
        this.setModal(true);
        this.mainFrame = mainFrame;
        this.technicsElement = technicsElement;
        this.editable = editable;
        initComponent();
        loadInitData();
        setEditable();
        initDialog();
    }

    private void setEditable() {
//		editable = false;
        table.setEnabled(editable);
        addBtn.setEnabled(editable);
        removeBtn.setEnabled(editable);
        comfirmBtn.setEnabled(editable);
    }

    private void loadInitData() {
        if (technicsElement != null) {
            final String fPlanNumber = technicsElement.attributeValue("technicsNumber");
            String Version = technicsElement.attributeValue("version");
            final String fVersion = Version.lastIndexOf(".") != -1 ? Version.substring(0, Version.lastIndexOf(".")) : Version;
            final VaActionProgressBar progressBar = new VaActionProgressBar(null, this, "加载数据", "正在加载主辅关联数据，请等待...", "正在加载主辅关联数据，请等待...");
            Thread th = new Thread() {
                @Override
                public void run() {
                    try {
                        List<Map<String, Object>> rowMaps = TechnicsIntf.searchZhuFuLinkByFPlan(fPlanNumber, fVersion);
                        setTableValues(rowMaps, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                        SwingUtil.showMessageDialog(e.getMessage(), "加载已有关联数据出错" + e.getLocalizedMessage(), JOptionPane.INFORMATION_MESSAGE);
                    } finally {
                        progressBar.finish();
                        progressBar.setVisible(false);
                    }
                }
            };
            th.start();
            progressBar.setVisible(true);
        }
    }

    private void initComponent() {
        JScrollPane tableScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JPanel topBtnPanel = new JPanel();
        JPanel downBtnPanel = new JPanel();
        String[] header = {"gwKey", "edit", "文档编号", "主制工艺文件编号", "主制工艺文件名称", "主制工艺文件版本", "主制工艺文件工序号", "创建人", "创建时间", "批次号"};
        Class<?>[] colClass = {String.class, String.class, String.class, String.class, String.class, String.class, Object.class, String.class, String.class, String.class};
        tableModel = new CommonTableModel(header, colClass, new int[]{COL_OPELABEL});
        table = new JTable(tableModel);
        table.getColumnModel().getColumn(COL_OPELABEL).setCellEditor(new AssociatedPlanTableCellEditor(new JComboBox()));
        table.setRowHeight(30);
        table.getTableHeader().setReorderingAllowed(false);
        CommonUIUtil.hiddenCell(table, COL_GWKEY);
        CommonUIUtil.hiddenCell(table, COL_EDIT);
        CommonUIUtil.hiddenCell(table, COL_DOCNUMBER);
        CommonUIUtil.hiddenCell(table, COL_PCNO);
        JViewport viewport = new JViewport();
        viewport.add(table.getTableHeader());
        tableScrollPane.setColumnHeader(viewport);
        tableScrollPane.setViewportView(table);
        this.add(tableScrollPane, BorderLayout.CENTER);

        addBtn = new JButton("添加");
        addBtn.addActionListener(this);
        removeBtn = new JButton("移除");
        removeBtn.addActionListener(this);
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setHgap(20);
        flowLayout.setAlignment(FlowLayout.LEFT);
        topBtnPanel.setLayout(flowLayout);
        topBtnPanel.add(addBtn);
        topBtnPanel.add(removeBtn);
        this.add(topBtnPanel, BorderLayout.NORTH);
        comfirmBtn = new JButton("确定");
        comfirmBtn.addActionListener(this);
        cancleBtn = new JButton("取消");
        cancleBtn.addActionListener(this);
        FlowLayout flowLayout2 = new FlowLayout();
        flowLayout2.setHgap(20);
        flowLayout2.setAlignment(FlowLayout.RIGHT);
        downBtnPanel.setLayout(flowLayout2);
        downBtnPanel.add(comfirmBtn);
        downBtnPanel.add(cancleBtn);
        this.add(downBtnPanel, BorderLayout.SOUTH);
    }

    private void initDialog() {
        setTitle("关联主制工艺");
        setSize(800, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        SwingUtil.setMiddle(this);
        setVisible(true);
    }

    public void setTableValues(List<Map<String, Object>> rowMaps, boolean edit) {
        if (rowMaps != null && rowMaps.size() > 0) {
            for (Map<String, Object> row : rowMaps) {
                Vector<Object> rowVector = new Vector<Object>();
                rowVector.add(convertNull(row.get("gwKey")));
                rowVector.add(Boolean.toString(edit));
                rowVector.add(convertNull(row.get("docNumber")));
                rowVector.add(convertNull(row.get("technicNumber")));
                rowVector.add(convertNull(row.get("technicName")));
                rowVector.add(convertNull(row.get("version")));
                rowVector.add(row.get("procedureLabel"));
                rowVector.add(convertNull(row.get("creator")));
                rowVector.add(convertNull(row.get("createTime")));
                rowVector.add(convertNull(row.get("picihao")));
                tableModel.addRow(rowVector);
            }
        }
    }

    private String convertNull(Object str) {
        return str == null ? "" : str.toString();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (cancleBtn == e.getSource()) {
            this.dispose();
        } else if (comfirmBtn == e.getSource()) {
            if (!editable) {
                this.dispose();
            } else {
                saveLink();
            }
        } else if (addBtn == e.getSource()) {
            if (!editable) {
                JOptionPane.showMessageDialog(this, "您没有权限修改", "警告", JOptionPane.WARNING_MESSAGE);
                return;
            }
            new AssociatedZZPlanStep(this, technicsElement, mainFrame);
        } else if (removeBtn == e.getSource()) {
            delete();
        }
    }

    private void saveLink() {
        Vector<?> dataVector = tableModel.getDataVector();
        Vector<?> row = null;
        int i = 0;
        String docNum = null;
        String zVersion = null;
        String zOpeLabel = null;
        String key = null;
        String picihao = null;
        String creator = null;
        String createTime = null;
        List<String> keyList = new ArrayList<String>();
        Map<String, String> rowMap = null;
        Map<String, String> recordParams = null;
        List<Map<String, String>> dataMap = new ArrayList<Map<String, String>>();
//        List<Map<String, String>> recordMap = new ArrayList<Map<String, String>>();
        String fDocNum = convertNull(technicsElement.attributeValue("technicsNumber"));
        String fVersion = convertNull(technicsElement.attributeValue("version"));
        String fPlanNumber = convertNull(technicsElement.attributeValue("pplanNumber"));
        String fPlanName = convertNull(technicsElement.attributeValue("technicsName"));
        fVersion = fVersion.lastIndexOf(".") != -1 ? fVersion.substring(0, fVersion.lastIndexOf(".")) : fVersion;
        List<GLZhuFuLink> glZhuFuLinkList = new ArrayList<GLZhuFuLink>();
        GLZhuFuLink glZhuFuLink;
        for (Object e : dataVector) {
            if (e instanceof Vector) {
                i++;
                row = (Vector<?>) e;
                docNum = convertNull(row.get(COL_DOCNUMBER));
                zVersion = convertNull(row.get(COL_ZVERSION));
                zVersion = zVersion.lastIndexOf(".") != -1 ? zVersion.substring(0, zVersion.lastIndexOf(".")) : zVersion;
                Object stepObj = row.get(COL_OPELABEL);
//				zOpeLabel = convertNull(row.get(COL_OPELABEL));
                if (stepObj == null || !(stepObj instanceof MainPlanProcedure)) {
                    logger.debug("==>selected stepObj is[]" + (stepObj == null ? "null" : stepObj.getClass().getName()));
                    JOptionPane.showMessageDialog(this, String.format("第【%s】行主制工序名称未选择！", i), "WARNING", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (stepObj.toString() == null || stepObj.toString().length() == 0) {
                    JOptionPane.showMessageDialog(this, String.format("第【%s】行主制工序名称未选择！", i), "WARNING", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                picihao = convertNull(row.get(COL_PCNO));
                picihao = picihao.length() == 0 ? "无" : picihao;
                creator = convertNull(row.get(COL_CREATOR));
                createTime = convertNull(row.get(COL_CREATETIME));
                key = docNum + "&" + zVersion + "&" + stepObj.toString();
                zOpeLabel = ((MainPlanProcedure) stepObj).getBsoid();
                int indexOf = keyList.indexOf(key);
                if (indexOf != -1) {
                    JOptionPane.showMessageDialog(this, String.format("第【%s】行与第【%s】行一致，请移除！", indexOf + 1, i), "WARNING", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                keyList.add(key);
                String gwKey = convertNull(row.get(COL_GWKEY));
                rowMap = new HashMap<String, String>();
                if (gwKey.length() > 0) {
                    rowMap.put("GWKEY", gwKey);
                    rowMap.put("FZTECHNICSNUMBER", fDocNum);
                    rowMap.put("ZZTECHNICSNUMBER", docNum);
                    rowMap.put("ZZTECHNICSVERSION", zVersion);
                    rowMap.put("ZZPROCEDUREBSOID", zOpeLabel);
                    rowMap.put("ZPROCEDURELABEL", stepObj.toString());
                    rowMap.put("ZPLANNUMBER", convertNull(row.get(COL_ZPLANNUMBER)));
                } else {
                    rowMap.put("FZTECHNICSNUMBER", fDocNum);
                    rowMap.put("FZTECHNICSVERSION", fVersion);
                    rowMap.put("ZZTECHNICSNUMBER", docNum);
                    rowMap.put("ZZTECHNICSVERSION", zVersion);
                    rowMap.put("ZZPROCEDUREBSOID", zOpeLabel);
                    rowMap.put("PICIHAO", picihao);
                    rowMap.put("CREATOR", creator);
                    rowMap.put("CREATETIME", createTime);
                    rowMap.put("ZPROCEDURELABEL", stepObj.toString());
                    rowMap.put("ZPLANNUMBER", convertNull(row.get(COL_ZPLANNUMBER)));
                }

                if (stepObj.toString().contains("_")) {
                    String zStepNumber = stepObj.toString().split("_")[0];
                    String zStepName = stepObj.toString().split("_")[1];
                    String recordzVersion = zVersion.lastIndexOf(".") != -1 ? zVersion.substring(0, zVersion.lastIndexOf(".")) : zVersion;
                    String recordfVersion = fVersion.lastIndexOf(".") != -1 ? fVersion.substring(0, fVersion.lastIndexOf(".")) : fVersion;
//                    recordParams = new HashMap<String, String>();
//                    recordParams.put("ZSTEPNUMBER", zStepNumber);
//                    recordParams.put("ZSTEPNAME", zStepName);
//                    recordParams.put("ZSTEPBSOID", zOpeLabel);
//                    recordParams.put("ZTECHNICSNUMBER", docNum);
//                    recordParams.put("ZPLANVERSION", recordzVersion);
//
//                    recordParams.put("FPLANNUMBER", fPlanNumber);
//                    recordParams.put("FPLANNAME", fPlanName);
//                    recordParams.put("FTECHNICSNUMBER", fDocNum);
//                    recordParams.put("FPLANVERSION", recordfVersion);
//
//                    recordParams.put("CREATOR", creator);
//                    recordParams.put("CREATETIME", createTime);
//                    recordParams.put("OPERATION", "已关联");

                    glZhuFuLink = new GLZhuFuLink();
                    glZhuFuLink.setZstepNumber(zStepNumber);
                    glZhuFuLink.setZstepName(zStepName);
                    glZhuFuLink.setZstepBsoid(zOpeLabel);
                    glZhuFuLink.setZztechnicsnumber(docNum);
                    glZhuFuLink.setZztechnicsversion(recordzVersion);
                    glZhuFuLink.setFppanNumber(fPlanNumber);
                    glZhuFuLink.setFtechnicsName(fPlanName);
                    glZhuFuLink.setFztechnicsnumber(fDocNum);
                    glZhuFuLink.setFztechnicsversion(recordfVersion);
                    glZhuFuLink.setCreator(creator);
                    glZhuFuLink.setCreateTime(createTime);
                    glZhuFuLinkList.add(glZhuFuLink);
                }

                dataMap.add(rowMap);
//                recordMap.add(recordParams);
            }
        }
        try {
            if (dataMap == null || dataMap.size() == 0) {
                this.dispose();
                return;
            }
            List<GLZhuFuLink> glZhuFuLinkList_old = TechnicsIntf.getAllZhufuLink(fDocNum, fVersion);
            String result = TechnicsIntf.saveZhuFuLink(dataMap);

            if ("SUCCESS".equalsIgnoreCase(result)) {
                //关联成功后，记录关联
                List<GLZhuFuLink> glZhuFuLinkList_new = new ArrayList<GLZhuFuLink>();
                glZhuFuLinkList_new.addAll(glZhuFuLinkList);
                glZhuFuLinkList_new.removeAll(glZhuFuLinkList_old);
                glZhuFuLinkList_old.removeAll(glZhuFuLinkList);

                SimpleDateFormat dataFormate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINESE);
                String date = dataFormate.format(new Date());
                //关联新的关联
                for (GLZhuFuLink newlink : glZhuFuLinkList_new) {
                    newlink.setOperation("已关联");
                    newlink.setCreateTime(date);
                    TechnicsIntf.recordZhuFuLink(newlink);
                }
                //断开旧的关联
                for (GLZhuFuLink oldlink : glZhuFuLinkList_old) {
                    oldlink.setFppanNumber(fPlanNumber);
                    oldlink.setFtechnicsName(fPlanName);
                    oldlink.setOperation("已断开");
                    oldlink.setCreateTime(date);
                    TechnicsIntf.recordZhuFuLink(oldlink);
                }
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "主辅关联失败\n" + result, "关联失败", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "主辅关联失败\n" + e.getLocalizedMessage(), "关联失败", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void checkUpload() {
        String docNum = technicsElement.attributeValue("technicsNumber");
        List<Element> allSteps = XmlUtility.getAllSteps(technicsElement);
        try {
            boolean flag = true;
            List<Element> allProcedures = TechnicsIntf.getProceduresOfTechnic(docNum);
            if (allProcedures == null || allSteps.size() != allProcedures.size()) {
                flag = false;
            } else {
                String localStepNum = null;
                String localStepName = null;
                String remoteStepNum = null;
                String remoteStepName = null;
                for (int i = 0; i < allSteps.size(); i++) {
                    Element localProcedure = allSteps.get(i);
                    Element remoteProcedure = allProcedures.get(i);
                    localStepNum = convertNull(localProcedure.attributeValue("stepNumber"));
                    localStepName = convertNull(localProcedure.attributeValue("stepName"));
                    remoteStepNum = convertNull(remoteProcedure.attributeValue("stepNumber"));
                    remoteStepName = convertNull(remoteProcedure.attributeValue("stepName"));
                    if (!localStepNum.equals(remoteStepNum) || !localStepName.equals(remoteStepName)) {
                        flag = false;
                        break;
                    }
                }
            }
            if (!flag) {
                JOptionPane.showMessageDialog(this, "检测到工艺文件未上载，点击确定上载工艺文件！", "工艺未上载", JOptionPane.INFORMATION_MESSAGE);
//                mainFrame.newTechnicsUploadThread();
                mainFrame.technicsUploadThread();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private void delete() {
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "请选择需要移除的行！");
            return;
        }
        for (int i = 0; i < selectedRows.length; i++) {
            String gwKey = convertNull(tableModel.getValueAt(selectedRows[i] - i, COL_GWKEY));
            if (gwKey.length() == 0) {
                tableModel.removeRow(selectedRows[i] - i);
            } else {
                // 根据gwKey删除数据库中记录
                try {
                    TechnicsIntf.deleteZhuFuLinkByID(gwKey.split("\\,"));
                    Object stepLabel = table.getValueAt(selectedRows[i], COL_OPELABEL);
                    String zOpeLabel = ((MainPlanProcedure) stepLabel).getBsoid();
                    if (stepLabel.toString().contains("_")) {
                        SimpleDateFormat dataFormate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINESE);
                        String date = dataFormate.format(new Date());
                        String zStepNumber = stepLabel.toString().split("_")[0];
                        String zStepName = stepLabel.toString().split("_")[1];
                        String docNum = convertNull(table.getValueAt(selectedRows[i], COL_DOCNUMBER));
                        String zVersion = convertNull(table.getValueAt(selectedRows[i], COL_ZVERSION));
                        String creator = convertNull(table.getValueAt(selectedRows[i], COL_CREATOR));
                        String createTime = convertNull(table.getValueAt(selectedRows[i], COL_CREATETIME));
                        String fDocNum = convertNull(technicsElement.attributeValue("technicsNumber"));
                        String fVersion = convertNull(technicsElement.attributeValue("version"));
                        String fPlanNumber = convertNull(technicsElement.attributeValue("pplanNumber"));
                        String fPlanName = convertNull(technicsElement.attributeValue("technicsName"));


                        Map<String, String> recordParams = new HashMap<String, String>();
                        String recordzVersion = zVersion.lastIndexOf(".") != -1 ? zVersion.substring(0, zVersion.lastIndexOf(".")) : zVersion;
                        String recordfVersion = fVersion.lastIndexOf(".") != -1 ? fVersion.substring(0, fVersion.lastIndexOf(".")) : fVersion;
                        recordParams.put("ZSTEPNUMBER", zStepNumber);
                        recordParams.put("ZSTEPNAME", zStepName);
                        recordParams.put("ZSTEPBSOID", zOpeLabel);
                        recordParams.put("ZTECHNICSNUMBER", docNum);
                        recordParams.put("ZPLANVERSION", recordzVersion);

                        recordParams.put("FPLANNUMBER", fPlanNumber);
                        recordParams.put("FPLANNAME", fPlanName);
                        recordParams.put("FTECHNICSNUMBER", fDocNum);
                        recordParams.put("FPLANVERSION", recordfVersion);

                        recordParams.put("CREATOR", creator);
                        recordParams.put("CREATETIME", date);
                        recordParams.put("OPERATION", "已断开");

                        try {
                            TechnicsIntf.recordZhuFuLink(recordParams);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                    tableModel.removeRow(selectedRows[i] - i);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "移除关联失败\n" + e.getMessage(), "移除失败", JOptionPane.WARNING_MESSAGE);
                    e.printStackTrace();
                }
            }
        }
    }

    class AssociatedPlanTableCellEditor extends DefaultCellEditor {

        private static final long serialVersionUID = 1L;
        JComboBox combox = null;

        public AssociatedPlanTableCellEditor(JComboBox comboBox) {
            super(comboBox);
            this.combox = comboBox;
            this.setClickCountToStart(1);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                                                     int column) {
            combox.removeAllItems();
            String docNumber = null;
            try {
                docNumber = (String) AssociatedZZPlanDialog.this.table.getValueAt(row, COL_DOCNUMBER);
                Vector<MainPlanProcedure> temp = cache.get(docNumber);
                if (temp != null) {
                    for (MainPlanProcedure step : temp) {
                        combox.addItem(step);
                    }
                    if (temp.contains(value)) {
                        combox.setSelectedItem(value);
                    }
                } else {
                    List<Element> proceduresDX = TechnicsIntf.getProceduresOfTechnic(docNumber);
                    if (proceduresDX != null && proceduresDX.size() > 0) {
                        Vector<MainPlanProcedure> vector = new Vector<MainPlanProcedure>();
                        for (Element dxProcedure : proceduresDX) {
                            String bsoid = dxProcedure.attributeValue("bsoID");
                            String stepNumber = dxProcedure.attributeValue("stepNumber");
                            String stepName = dxProcedure.attributeValue("stepName");
                            String label = stepNumber + "_" + stepName;
                            MainPlanProcedure mainPlanProcedure = new MainPlanProcedure(bsoid, label);
                            vector.add(mainPlanProcedure);
                            combox.addItem(mainPlanProcedure);
                        }
                        if (vector.size() > 0) {
                            cache.put(docNumber, vector);
                        }
                        if (vector.contains(value)) {
                            combox.setSelectedItem(value);
                        }
                    } else {
                        JOptionPane.showMessageDialog(AssociatedZZPlanDialog.this, "主工艺" + docNumber + "下的没有工序", "提示", JOptionPane.WARNING_MESSAGE);
                        combox.addItem(new MainPlanProcedure("", ""));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(AssociatedZZPlanDialog.this, "获取主工艺" + docNumber + "下的工序出错", "提示", JOptionPane.WARNING_MESSAGE);
                combox.addItem(new MainPlanProcedure("", ""));
            }
            return combox;
        }
    }
}
