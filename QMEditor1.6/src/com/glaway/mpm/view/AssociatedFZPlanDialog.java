package com.glaway.mpm.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.DefaultTableCellRenderer;

import org.dom4j.Element;

import com.glaway.mpm.model.UploadTechnics;
import com.glaway.mpm.parameter.commonui.CommonTableModel;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.wcIntf.TechnicsIntf;

public class AssociatedFZPlanDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static VaLogger logger = VaLogger.getLogger(AssociatedFZPlanDialog.class);
    private NewTechnicsPart mainFrame = null;
    private Element technicsElement = null;
    private boolean editable = false;
    private JPanel buttonPanel = null;
    private JButton view = null;
    private JTable table = null;
    private CommonTableModel tableModel = null;

    private static final int COL_BSOID = 0;
    private static final int COL_ZSTEPNUMBER = 1;
    private static final int COL_ZSTEPNAME = 2;
    private static final int COL_FPLANNUMBER = 3;
    private static final int COL_FPLANNAME = 4;
    private static final int COL_FVERSION = 5;
    private static final int COL_CREATOR = 6;
    private static final int COL_CREATETIME = 7;
    private static final int COL_FDOCNUMBER = 8;
    private static final int COL_OPERATION = 9;

    public AssociatedFZPlanDialog(Element technicsElement, NewTechnicsPart mainFrame, boolean editable) {
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
        table.setEnabled(editable);
    }

    private void loadInitData() {
        if (technicsElement != null) {
            final String zTechnicsNumber = technicsElement.attributeValue("technicsNumber");
            String zVersion = technicsElement.attributeValue("version");
            final String version = zVersion.lastIndexOf(".") != -1 ? zVersion.substring(0, zVersion.lastIndexOf(".")) : zVersion;
            final VaActionProgressBar progressBar = new VaActionProgressBar(null, this, "加载数据", "正在加载主辅关联数据，请等待...", "正在加载主辅关联数据，请等待...");
            Thread th = new Thread() {
                @Override
                public void run() {
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("ZZTECHNICSNUMBER", zTechnicsNumber);
                        params.put("ZZTECHNICSVERSION", version);
                        Map<String, Map<String, String>> rowMaps = TechnicsIntf.searchZFLinkByMainPlan(params);
                        List<Element> procedures = XmlUtility.getAllSteps(technicsElement);
                        String stepNumber = null;
                        String stepName = null;
                        String bsoid = null;
                        Map<String, String> oneRowMap = new HashMap<String, String>();
                        for (Element e : procedures) {
                            Vector<String> row = new Vector<String>();
                            bsoid = e.attributeValue("bsoID");
                            stepNumber = e.attributeValue("stepNumber");
                            stepName = e.attributeValue("stepName");
                            row.add(bsoid);
                            row.add(stepNumber);
                            row.add(stepName);
                            if (rowMaps.containsKey(bsoid)) {
                                oneRowMap = rowMaps.get(bsoid);
                                row.add(oneRowMap.get("fPlanNumber"));
                                row.add(oneRowMap.get("fPlanName"));
                                row.add(oneRowMap.get("fVersion"));
                                row.add(oneRowMap.get("creator"));
                                row.add(oneRowMap.get("createTime"));
                                row.add(oneRowMap.get("fDocNum"));
                            }
                            tableModel.addRow(row);
                        }
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
        buttonPanel = new JPanel();
        view = new JButton("查看关联历史记录");
        view.addActionListener(this);
        JScrollPane tableScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        String[] header = {"BSOID", "主制工序号", "主制工序名称", "辅制工艺编号", "辅制工艺", "辅制工艺版本", "关联人", "关联时间", "fDocNum", "操作"};
        Class<?>[] colClass = {String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, Object.class};
        tableModel = new CommonTableModel(header, colClass, new int[]{COL_OPERATION});
        table = new JTable(tableModel);
        table.getColumnModel().getColumn(COL_OPERATION).setCellEditor(new AssociatedPlanTableCellEditor(new JComboBox()));
        table.getColumnModel().getColumn(COL_OPERATION).setCellRenderer(new AssociatedPlanTableCellRender());
//        CommonUIUtil.setColumnWidth(table, COL_OPERATION, 100);
//        CommonUIUtil.setColumnWidth(table, COL_ZSTEPNUMBER, 70);
        table.getColumnModel().getColumn(COL_OPERATION).setMinWidth(100);
        table.getColumnModel().getColumn(COL_ZSTEPNUMBER).setMinWidth(70);
        table.setRowHeight(30);
        table.getTableHeader().setReorderingAllowed(false);
        CommonUIUtil.hiddenCell(table, COL_BSOID);
        CommonUIUtil.hiddenCell(table, COL_FDOCNUMBER);
        JViewport viewport = new JViewport();
        viewport.add(table.getTableHeader());
        tableScrollPane.setColumnHeader(viewport);
        tableScrollPane.setViewportView(table);
        tableScrollPane.setPreferredSize(new Dimension(750, 500));

        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.add(view, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
                5, 5, 5, 0), 0, 0));

        setLayout(new GridBagLayout());
        add(buttonPanel,new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(
                5, 5, 5, 0), 0, 0));
        add(tableScrollPane,new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                0, 0, 0, 0), 0, 0));
    }

    private void initDialog() {
        setTitle("添加辅制工艺");
        setSize(800, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        SwingUtil.setMiddle(this);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (view == e.getSource()) {
            String zTechnicsNumber = technicsElement.attributeValue("technicsNumber");
            String zVersion = technicsElement.attributeValue("version");
            String version = zVersion.lastIndexOf(".") != -1 ? zVersion.substring(0, zVersion.lastIndexOf(".")) : zVersion;
            new ZFRelatedHistoryDialog(zTechnicsNumber, version);
        }
    }

    class AssociatedPlanTableCellEditor extends DefaultCellEditor implements ActionListener {

        private static final long serialVersionUID = 1L;
        JComboBox combox = null;
        private JButton edit = null;
        private JButton remove = null;
        JPanel panel = null;

        public AssociatedPlanTableCellEditor(JComboBox comboBox) {
            super(comboBox);
            setClickCountToStart(1);
            this.combox = comboBox;
            edit = new JButton("编辑");
            edit.addActionListener(this);
            remove = new JButton("移除");
            remove.addActionListener(this);
            panel = new JPanel();
            panel.add(edit);
            panel.add(remove);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                                                     int column) {
            return panel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (edit == e.getSource()) {
                logger.debug("edit row " + table.getSelectedRow());
                String number = technicsElement.attributeValue("technicsNumber");
                Vector<UploadTechnics> data = NewTechnicsPart.editTechnics;
                for (UploadTechnics o : data) {
                    if (o.getTechnicsNumber().equals(number)) {
                        JOptionPane.showMessageDialog(AssociatedFZPlanDialog.this, "检测到主工艺未上载，请先上载主工艺！");
                        return;
                    }
                }
                new AssociatedZZPlanStep(AssociatedFZPlanDialog.this, technicsElement, mainFrame);
            } else if (remove == e.getSource()) {
                int index = table.getSelectedRow();
                logger.debug("remove row " + table.getSelectedRow());
                String bsoid = convertNull(tableModel.getValueAt(index, COL_BSOID));
                String zDocNum = technicsElement.attributeValue("technicsNumber");
                String zVersion = technicsElement.attributeValue("version");
                //add by liangbo
                String zStepNumber = convertNull(tableModel.getValueAt(index, COL_ZSTEPNUMBER));
                String zStepName = convertNull(tableModel.getValueAt(index, COL_ZSTEPNAME));

                String fPlanNumber = convertNull(tableModel.getValueAt(index, COL_FPLANNUMBER));
                String fPlanName = convertNull(tableModel.getValueAt(index, COL_FPLANNAME));
                String fDocNum = convertNull(tableModel.getValueAt(index, COL_FDOCNUMBER));
                String fVersion = convertNull(tableModel.getValueAt(index, COL_FVERSION));
                String creator = convertNull(tableModel.getValueAt(index, COL_CREATOR));
                String createTime = convertNull(tableModel.getValueAt(index, COL_CREATETIME));
                SimpleDateFormat dataFormate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINESE);
                String date = dataFormate.format(new Date());

                zVersion = zVersion.lastIndexOf(".") != -1 ? zVersion.substring(0, zVersion.lastIndexOf(".")) : zVersion;
                Map<String, String> params = new HashMap<String, String>();
                params.put("ZZTECHNICSNUMBER", zDocNum);
                params.put("ZZTECHNICSVERSION", zVersion);
                params.put("ZZPROCEDUREBSOID", bsoid);

                Map<String, String> recordParams = new HashMap<String, String>();
                String recordzVersion = zVersion.lastIndexOf(".") != -1 ? zVersion.substring(0, zVersion.lastIndexOf(".")) : zVersion;
                String recordfVersion = fVersion.lastIndexOf(".") != -1 ? fVersion.substring(0, fVersion.lastIndexOf(".")) : fVersion;
                recordParams.put("ZSTEPNUMBER", zStepNumber);
                recordParams.put("ZSTEPNAME", zStepName);
                recordParams.put("ZSTEPBSOID", bsoid);
                recordParams.put("ZTECHNICSNUMBER", zDocNum);
                recordParams.put("ZPLANVERSION", recordzVersion);

                recordParams.put("FPLANNUMBER", fPlanNumber);
                recordParams.put("FPLANNAME", fPlanName);
                recordParams.put("FTECHNICSNUMBER", fDocNum);
                recordParams.put("FPLANVERSION", recordfVersion);

                recordParams.put("CREATOR", creator);
                recordParams.put("CREATETIME", date);
                recordParams.put("OPERATION", "已断开");

                try {
                    String result = TechnicsIntf.deleteZhuFuLink(params);
                    if ("success".equalsIgnoreCase(result)) {
                        tableModel.setValueAt("", index, COL_FDOCNUMBER);
                        tableModel.setValueAt("", index, COL_FPLANNUMBER);
                        tableModel.setValueAt("", index, COL_FPLANNAME);
                        tableModel.setValueAt("", index, COL_FVERSION);
                        tableModel.setValueAt("", index, COL_CREATOR);
                        tableModel.setValueAt("", index, COL_CREATETIME);
                        String recorResult = TechnicsIntf.recordZhuFuLink(recordParams);
                    } else {
                        JOptionPane.showMessageDialog(AssociatedFZPlanDialog.this, "移除失败\n" + result, "移除失败", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    class AssociatedPlanTableCellRender extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;
        private JButton edit = null;
        private JButton remove = null;
        JPanel panel = null;

        public AssociatedPlanTableCellRender() {
            edit = new JButton("编辑");
            remove = new JButton("移除");
            panel = new JPanel();
            panel.add(edit);
            panel.add(remove);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            return panel;
        }

    }

    private String convertNull(Object str) {
        return str == null ? "" : str.toString();
    }

    public void setTableValues(Map<String, Map<String, String>> tableDataMap) {
        if (tableDataMap != null && tableDataMap.size() > 0) {
            int index = table.getSelectedRow();
            for (Map.Entry<String, Map<String, String>> oneRowMap : tableDataMap.entrySet()) {
                Map<String, String> row = oneRowMap.getValue();
                tableModel.setValueAt(convertNull(row.get("docNumber")), index, COL_FDOCNUMBER);
                tableModel.setValueAt(convertNull(row.get("technicNumber")), index, COL_FPLANNUMBER);
                tableModel.setValueAt(convertNull(row.get("technicName")), index, COL_FPLANNAME);
                tableModel.setValueAt(convertNull(row.get("version")), index, COL_FVERSION);
                tableModel.setValueAt(convertNull(row.get("creator")), index, COL_CREATOR);
                tableModel.setValueAt(convertNull(row.get("createTime")), index, COL_CREATETIME);
            }
        }
    }

    public void associatedFZPlan(List<Map<String, Object>> tableDataMap) {
        if (tableDataMap != null && tableDataMap.size() > 0) {
            int index = table.getSelectedRow();
            String bsoid = convertNull(tableModel.getValueAt(index, COL_BSOID));
            String zDocNum = technicsElement.attributeValue("technicsNumber");
            String zVersion = technicsElement.attributeValue("version");
            String picihao = convertNull(technicsElement.attributeValue("PCNO"));

            //add by liangbo
            String zStepNumber = convertNull(tableModel.getValueAt(index, COL_ZSTEPNUMBER));
            String zStepName = convertNull(tableModel.getValueAt(index, COL_ZSTEPNAME));
            picihao = picihao.length() == 0 ? "无" : picihao;
            zVersion = zVersion.lastIndexOf(".") != -1 ? zVersion.substring(0, zVersion.lastIndexOf(".")) : zVersion;
            for (Map<String, Object> row : tableDataMap) {
                String fDocNum = convertNull(row.get("docNumber"));
                String fPlanNum = convertNull(row.get("technicNumber"));
                String fPlanName = convertNull(row.get("technicName"));
                String fVersion = convertNull(row.get("version"));
                String fVersionInfo = fVersion.lastIndexOf(".") != -1 ? fVersion.substring(0, fVersion.lastIndexOf(".")) : fVersion;
                String creator = convertNull(row.get("creator"));
                String createTime = convertNull(row.get("createTime"));
                Map<String, String> params = new HashMap<String, String>();
                params.put("ZZTECHNICSNUMBER", zDocNum);
                params.put("ZZTECHNICSVERSION", zVersion);
                params.put("ZZPROCEDUREBSOID", bsoid);
                params.put("PICIHAO", picihao);
                params.put("CREATOR", creator);
                params.put("CREATETIME", createTime);
                params.put("FZTECHNICSNUMBER", fDocNum);
                params.put("FZTECHNICSVERSION", fVersionInfo);

                try {
                    String recordzVersion = zVersion.lastIndexOf(".") != -1 ? zVersion.substring(0, zVersion.lastIndexOf(".")) : zVersion;
                    String recordfVersion = fVersion.lastIndexOf(".") != -1 ? fVersion.substring(0, fVersion.lastIndexOf(".")) : fVersion;
                    String result = TechnicsIntf.addZhuFuLinkByZplan(params);
                    if ("SUCCESS".equalsIgnoreCase(result)) {
                        String old_fDocNumber = convertNull(tableModel.getValueAt(index, COL_FDOCNUMBER));
                        String old_fPlanNum = convertNull(tableModel.getValueAt(index, COL_FPLANNUMBER));
                        String old_fPlanName = convertNull(tableModel.getValueAt(index, COL_FPLANNAME));
                        String old_fVersion = convertNull(tableModel.getValueAt(index, COL_FVERSION));
                        String old_creator = convertNull(tableModel.getValueAt(index, COL_CREATOR));
                        String old_createTime = convertNull(tableModel.getValueAt(index, COL_CREATETIME));

                        SimpleDateFormat dataFormate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINESE);
                        String date = dataFormate.format(new Date());

                        if(!fDocNum.equals(old_fDocNumber)){
                            if(!old_fDocNumber.isEmpty()){
                                //如果以前关联过，则需要记录断开
                                Map<String, String> recordParams_off = new HashMap<String, String>();
                                String old_fv = old_fVersion.lastIndexOf(".") != -1 ? old_fVersion.substring(0, old_fVersion.lastIndexOf(".")) : old_fVersion;
                                recordParams_off.put("ZSTEPNUMBER", zStepNumber);
                                recordParams_off.put("ZSTEPNAME", zStepName);
                                recordParams_off.put("ZSTEPBSOID", bsoid);
                                recordParams_off.put("ZTECHNICSNUMBER", zDocNum);
                                recordParams_off.put("ZPLANVERSION", recordzVersion);

                                recordParams_off.put("FPLANNUMBER", old_fPlanNum);
                                recordParams_off.put("FPLANNAME", old_fPlanName);
                                recordParams_off.put("FTECHNICSNUMBER", old_fDocNumber);
                                recordParams_off.put("FPLANVERSION", old_fv);

                                recordParams_off.put("CREATOR", old_creator);
                                recordParams_off.put("CREATETIME", date);
                                recordParams_off.put("OPERATION", "已断开");
                                TechnicsIntf.recordZhuFuLink(recordParams_off);
                            }
                            //记录关联关系
                            Map<String, String> recordParams_on = new HashMap<String, String>();
                            recordParams_on.put("ZSTEPNUMBER", zStepNumber);
                            recordParams_on.put("ZSTEPNAME", zStepName);
                            recordParams_on.put("ZSTEPBSOID", bsoid);
                            recordParams_on.put("ZTECHNICSNUMBER", zDocNum);
                            recordParams_on.put("ZPLANVERSION", recordzVersion);

                            recordParams_on.put("FPLANNUMBER", fPlanNum);
                            recordParams_on.put("FPLANNAME", fPlanName);
                            recordParams_on.put("FTECHNICSNUMBER", fDocNum);
                            recordParams_on.put("FPLANVERSION", recordfVersion);

                            recordParams_on.put("CREATOR", creator);
                            recordParams_on.put("CREATETIME", date);
                            recordParams_on.put("OPERATION", "已关联");
                            TechnicsIntf.recordZhuFuLink(recordParams_on);
                        }
                        tableModel.setValueAt(fDocNum, index, COL_FDOCNUMBER);
                        tableModel.setValueAt(fPlanNum, index, COL_FPLANNUMBER);
                        tableModel.setValueAt(fPlanName, index, COL_FPLANNAME);
                        tableModel.setValueAt(fVersion, index, COL_FVERSION);
                        tableModel.setValueAt(creator, index, COL_CREATOR);
                        tableModel.setValueAt(createTime, index, COL_CREATETIME);


//						JOptionPane.showMessageDialog(this, "添加成功!", "添加辅制关联添加成功", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "添加失败!\n" + result, "添加辅制关联失败", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "添加失败!\n" + e.getLocalizedMessage(), "添加辅制关联失败", JOptionPane.WARNING_MESSAGE);
                }
                break;
            }
        }
    }
}
