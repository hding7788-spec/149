package com.glaway.mpm.sop.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.swing.*;

import com.glaway.mpm.sop.intf.SopIntf;
import com.glaway.mpm.sop.model.ParametersBean;
import com.glaway.mpm.sop.model.SopBean;
import com.glaway.mpm.sop.model.SopResourceBean;
import com.glaway.mpm.view.AssociatedZZPlanDialog;
import com.glaway.mpm.view.NewTechnicsPart;
import org.dom4j.Element;

import wt.doc.WTDocument;
import wt.util.WTException;

import com.glaway.mpm.parameter.commonui.CommonTableModel;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.util.UserUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;

public class AddSopParametersDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;
    private NewTechnicsPart frame = null;
    private SopParametersJPanel sopParametersJPanel;
    private JTable table = null;
    private CommonTableModel tableModel = null;
    private JButton searchBtn = null;
    private JButton comfirmBtn = null;
    private JButton cancleBtn = null;
    private JLabel zylbLabel = null;
//    private JLabel csxmmcLabel = null;
    private JLabel gxmcLabel = null;
    private JLabel wzlbLabel = null;
    private JTextField zylbField = null;
//    private JTextField csxmmcField = null;
    private JTextField gxmcField = null;
    private JComboBox wzlbComboBox = null;
    private JPanel searchPanel = null;
    private static final int COL_OID = 0;
    private static final int COL_OBJ = 8;

    public AddSopParametersDialog(NewTechnicsPart frame, SopParametersJPanel sopParametersJPanel) {
        this.frame = frame;
        this.sopParametersJPanel = sopParametersJPanel;
        setModal(true);
        initComponent();
        initData();
        searchBtn.doClick();
        initDialog();
    }

    private void initComponent() {
        JScrollPane tableScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JPanel topBtnPanel = new JPanel();
        JPanel topPanel2 = new JPanel();
        JPanel downBtnPanel = new JPanel();
        String[] header = {"oid", "编号", "专业类别", "参数项目名称", "工序名称", "物资类别", "参数值", "说明", "obj"};
        Class<?>[] colClass = {String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, Object.class};
        tableModel = new CommonTableModel(header, colClass, null);
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.getTableHeader().setReorderingAllowed(false);
        CommonUIUtil.hiddenCell(table, COL_OID);
        CommonUIUtil.hiddenCell(table, COL_OBJ);
        JViewport viewport = new JViewport();
        viewport.add(table.getTableHeader());
        tableScrollPane.setColumnHeader(viewport);
        tableScrollPane.setViewportView(table);
        this.add(tableScrollPane, BorderLayout.CENTER);

        zylbLabel = new JLabel("专业类别：");
        zylbField = new JTextField();
        zylbField.setEditable(false);
        zylbField.setPreferredSize(new Dimension(200, 25));
//        csxmmcLabel = new JLabel("参数项目名称：");
//        csxmmcField = new JTextField();
//        csxmmcField.setEditable(false);
//        csxmmcField.setPreferredSize(new Dimension(200, 25));
        gxmcLabel = new JLabel("工序名称：");
        gxmcField = new JTextField();
        gxmcField.setEditable(false);
        gxmcField.setPreferredSize(new Dimension(200, 25));
        wzlbLabel = new JLabel("物资类别：");
        wzlbComboBox = new JComboBox();
        wzlbComboBox.setPreferredSize(new Dimension(200, 25));
        searchBtn = new JButton("搜索");
        searchBtn.addActionListener(this);

        searchPanel = new JPanel();
        searchPanel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.insets = new Insets(5, 5, 0, 0);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        searchPanel.add(zylbLabel, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        searchPanel.add(zylbField, gridBagConstraints);

//        gridBagConstraints.gridx = 2;
//        gridBagConstraints.gridy = 0;
//        gridBagConstraints.gridwidth = 1;
//        searchPanel.add(csxmmcLabel, gridBagConstraints);
//
//        gridBagConstraints.gridx = 3;
//        gridBagConstraints.gridy = 0;
//        gridBagConstraints.gridwidth = 1;
//        searchPanel.add(csxmmcField, gridBagConstraints);

        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        searchPanel.add(searchBtn, gridBagConstraints);

        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        searchPanel.add(gxmcLabel, gridBagConstraints);

        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        searchPanel.add(gxmcField, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        searchPanel.add(wzlbLabel, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        searchPanel.add(wzlbComboBox, gridBagConstraints);

        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setHgap(20);
        flowLayout.setAlignment(FlowLayout.LEFT);
        topBtnPanel.setLayout(flowLayout);
        topBtnPanel.add(searchPanel);
        FlowLayout layout = new FlowLayout();
        layout.setHgap(20);
        layout.setAlignment(FlowLayout.RIGHT);
        JPanel panel = new JPanel();
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = new Insets(10, 0, 5, 5);
        c.gridy = 1;
        c.gridx = 1;
        panel.setLayout(new GridBagLayout());
        panel.add(topBtnPanel, c);
        c.gridy = 2;
        c.anchor = GridBagConstraints.NORTHEAST;
        panel.add(topPanel2, c);
        this.add(panel, BorderLayout.NORTH);
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
        setTitle("添加");
        setSize(800, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        SwingUtil.setMiddle(this);
        setVisible(true);
    }

    private void initData() {
        Element technicsEle = frame.xwPartTreePanel.getSelectedTreeElement();
        String zylb = technicsEle.attributeValue("SpecializedType");
        String csxmmc = technicsEle.attributeValue("Parameters");
        String gxmc = technicsEle.attributeValue("ProceduceName");
        zylbField.setText(zylb);
//        csxmmcField.setText(csxmmc);
        gxmcField.setText(gxmc);
//        Map<String, String> materialCategoryMap = SopIntf.getMaterialCategoryByWzlb(zylb);
//        wzlbComboBox.addItem("");
//        for (Map.Entry<String, String> entry : materialCategoryMap.entrySet()) {
//            String value = entry.getValue();
//            wzlbComboBox.addItem(value);
//        }
        List<SopResourceBean> sopResourceBeanList = SopIntf.searchParameters(zylb, csxmmc, gxmc, "");
		wzlbComboBox.removeAllItems();
		wzlbComboBox.addItem("");
		for (SopResourceBean parametersBean : sopResourceBeanList) {
			String materialCategory = parametersBean.getMaterialCategory();
			wzlbComboBox.addItem(materialCategory);
		}

    }

    private String convertNull(Object str) {
        return str == null ? "" : str.toString();
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (comfirmBtn == e.getSource()) {
            int[] selectedRows = table.getSelectedRows();
            if (selectedRows == null || selectedRows.length == 0) {
                SwingUtil.showMessageDialog("请选择参数项目", "提示", 2);
                return;
            }
            List<SopResourceBean> resourceBeanList = new ArrayList<SopResourceBean>();
            for (int index : selectedRows) {
                SopResourceBean resourceBean = (SopResourceBean) tableModel.getValueAt(index, COL_OBJ);
                resourceBean.setIsNew("false");
                resourceBeanList.add(resourceBean);
            }
            String msg = sopParametersJPanel.setTableValues(resourceBeanList);
            if(!msg.isEmpty()){
                JOptionPane.showMessageDialog(this,"参数项目[" + msg + "]已存在，未完成添加!");
            }
            this.dispose();
        } else if (cancleBtn == e.getSource()) {
            this.dispose();
        } else if (searchBtn == e.getSource()) {
            String zylb = zylbField.getText().trim();
//            String csxmmc = csxmmcField.getText().trim();
            String gxmc = gxmcField.getText().trim();
            String wzlb = String.valueOf(wzlbComboBox.getSelectedItem());
//            if (wzlb.length() == 0) {
//                JOptionPane.showMessageDialog(this, "请选择物资类别");
//                return;
//            }
            searchParameters(zylb, "", gxmc, wzlb);
        }
    }

    /**
     * 搜索参数项目名称
     *
     * @param zylb   专业类别
     * @param csxmmc 参数项目名称
     * @param gxmc   工序名称
     * @param wzlb   物资类别
     */
    private void searchParameters(final String zylb, final String csxmmc, final String gxmc, final String wzlb) {
        tableModel.setRowCount(0);
        final VaActionProgressBar progressBar = new VaActionProgressBar(null, this, "搜索参数项目名称", "正在搜索参数项目名称，请等待...", String.format("正在通过专业类别【%s】参数项目名称【%s】工序名称【%s】物资类别【%s】模糊搜索工艺文件，请等待...", zylb, csxmmc, gxmc, wzlb));
        Thread th = new Thread() {
            @Override
            public void run() {
                try {
                    List<SopResourceBean> sopResourceBeanList = SopIntf.searchParameters(zylb, csxmmc, gxmc, wzlb);
                    if (sopResourceBeanList != null && sopResourceBeanList.size() > 0) {
                        for (SopResourceBean obj : sopResourceBeanList) {
                            Vector<Object> rowData = new Vector<Object>();
                            rowData.add(convertNull(obj.getOid()));
                            rowData.add(convertNull(obj.getNumber()));
                            rowData.add(convertNull(obj.getSpecializedType()));
                            rowData.add(convertNull(obj.getName()));
                            rowData.add(convertNull(obj.getProcedureName()));
                            rowData.add(convertNull(obj.getMaterialCategory()));
                            rowData.add(convertNull(obj.getCanshuzhi()));
                            rowData.add(convertNull(obj.getDescription()));
                            rowData.add(obj);
                            tableModel.addRow(rowData);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
