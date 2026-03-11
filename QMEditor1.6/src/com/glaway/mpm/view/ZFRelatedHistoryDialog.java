package com.glaway.mpm.view;

import com.glaway.mpm.mesParameter.model.GLZhuFuLink;
import com.glaway.mpm.parameter.commonui.CommonTableModel;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.TechnicsIntf;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

public class ZFRelatedHistoryDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private static final int COL_ZSTEPNUMBER = 0;
    private static final int COL_ZSTEPNAME = 1;
    private static final int COL_FPLANNUMBER = 2;
    private static final int COL_OPERATION = 3;
    private static final int COL_FPLANNAME = 4;
    private static final int COL_FVERSION = 5;
    private static final int COL_CREATOR = 6;
    private static final int COL_CREATETIME = 7;

    private JTable table = null;
    private CommonTableModel tableModel = null;
    private String technicsNumber;
    private String version;
    private JPanel topPanel;
    private JLabel produceNumberLabel;
    private JTextField produceNumberTextField;
    private List<GLZhuFuLink> glZhuFuLinkList;

    private static VaLogger logger = VaLogger.getLogger(AssociatedFZPlanDialog.class);

    public ZFRelatedHistoryDialog(String technicsNumber, String version) {
        this.technicsNumber = technicsNumber;
        this.version = version;
        this.setModal(true);
        initComponent();
        loadInitData();
        initDialog();
    }

    private void loadInitData() {
        try {
            if(technicsNumber == null || technicsNumber.isEmpty()){
                JOptionPane.showMessageDialog(null,"未检测到工艺文件编号");
                return;
            }
            if(version == null || version.isEmpty()){
                JOptionPane.showMessageDialog(null,"未检测到工艺文件版本");
                return;
            }
            glZhuFuLinkList = TechnicsIntf.getRecordList(technicsNumber,version,null);
            setTableValues(null);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    private void setTableValues(String stepNumber) {
        while(tableModel.getRowCount()>0){
            tableModel.removeRow(tableModel.getRowCount()-1);
        }
        for(GLZhuFuLink glZhuFuLink : glZhuFuLinkList){
            if(stepNumber != null && !stepNumber.isEmpty()){
                if(glZhuFuLink.getZstepNumber().equals(stepNumber)){
                    Vector<Object> rowVector = new Vector<Object>();
                    rowVector.add(glZhuFuLink.getZstepNumber());
                    rowVector.add(glZhuFuLink.getZstepName());
                    rowVector.add(glZhuFuLink.getFppanNumber());
                    rowVector.add(glZhuFuLink.getOperation());
                    rowVector.add(glZhuFuLink.getFtechnicsName());
                    rowVector.add(glZhuFuLink.getFztechnicsversion());
                    rowVector.add(glZhuFuLink.getCreator());
                    rowVector.add(glZhuFuLink.getCreateTime());
                    tableModel.addRow(rowVector);
                }
            }else{
                Vector<Object> rowVector = new Vector<Object>();
                rowVector.add(glZhuFuLink.getZstepNumber());
                rowVector.add(glZhuFuLink.getZstepName());
                rowVector.add(glZhuFuLink.getFppanNumber());
                rowVector.add(glZhuFuLink.getOperation());
                rowVector.add(glZhuFuLink.getFtechnicsName());
                rowVector.add(glZhuFuLink.getFztechnicsversion());
                rowVector.add(glZhuFuLink.getCreator());
                rowVector.add(glZhuFuLink.getCreateTime());
                tableModel.addRow(rowVector);
            }
        }

    }

    private void initComponent() {
        topPanel = new JPanel();
        produceNumberLabel = new JLabel("工序号：");
        produceNumberTextField = new JTextField();
        produceNumberTextField.setPreferredSize(new Dimension(200, 20));
        produceNumberTextField.getDocument().addDocumentListener(produceListener);

        JScrollPane tableScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        String[] header = {"主制工序号", "主制工序名称", "辅制工艺编号", "操作", "辅制工艺", "辅制工艺版本", "操作人", "操作时间" };
        Class<?>[] colClass = { String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class };
        tableModel = new CommonTableModel(header, colClass, new int[]{COL_OPERATION});
        table = new JTable(tableModel);
        RowSorter sorter = new TableRowSorter(tableModel);
        table.setRowSorter(sorter);
//        CommonUIUtil.setColumnWidth(table, COL_OPERATION, 50);
//        CommonUIUtil.setColumnWidth(table, COL_CREATETIME, 130);
//        CommonUIUtil.setColumnWidth(table, COL_ZSTEPNUMBER, 70);
        table.getColumnModel().getColumn(COL_OPERATION).setMinWidth(50);
        table.getColumnModel().getColumn(COL_CREATETIME).setMinWidth(130);
        table.getColumnModel().getColumn(COL_ZSTEPNUMBER).setMinWidth(70);
        table.setRowHeight(30);
        table.getTableHeader().setReorderingAllowed(true);
        JViewport viewport = new JViewport();
        viewport.add(table.getTableHeader());
        tableScrollPane.setColumnHeader(viewport);
        tableScrollPane.setViewportView(table);
        tableScrollPane.setPreferredSize(new Dimension(750, 500));

        topPanel.setLayout(new GridBagLayout());
        topPanel.add(produceNumberLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                5, 5, 5, 0), 0, 0));
        topPanel.add(produceNumberTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                5, 5, 5, 0), 0, 0));

        setLayout(new GridBagLayout());
        add(topPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
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

    DocumentListener produceListener = new DocumentListener() {

        @Override
        public void insertUpdate(DocumentEvent e) {
            String text = produceNumberTextField.getText();
            setTableValues(text);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            String text = produceNumberTextField.getText();
            setTableValues(text);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            String text = produceNumberTextField.getText();
            setTableValues(text);
        }
    };
}
