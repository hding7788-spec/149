package com.glaway.mpm.erp;

import com.glaway.mpm.erp.component.TechnicsMaterialJCombox;
import com.glaway.mpm.erp.component.WZFLJTextField;
import com.glaway.mpm.intf.ERPToWCInfRMI;
import com.glaway.mpm.util.IconUtil;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.XWTreeNode;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import ext.ases.techMaterial.bean.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.List;
import java.util.*;

/**
 * @program: SAST-149-PDM
 * @description:
 * @author: MChen
 * @create: 2021-01-20 13:34
 */
public class SupplyNumberTechQuotaDialog extends AbstractERPDialog {

    private NewTechnicsPart frame;
    private XWTreeNode node;
    private JDialog jdialog;
    private JLabel databaseLable;

    private Vector databaseModel; //
    private Vector wzlbMode;
    private Vector<KVItem> wzlbMode2;
    private LinkedHashMap<JComponent, String> conditionLinkMap = new LinkedHashMap<JComponent, String>();
    //物资名称
    private JLabel chmcLable;
    private TechnicsMaterialJCombox wzmcJbo = new TechnicsMaterialJCombox("物资名称");
    //物资简称
    private JLabel wzjcLabel;
    private TechnicsMaterialJCombox wzjcJbo = new TechnicsMaterialJCombox("物资简称");
    //编码优选级别
    private JLabel bmyxjbLabel;
    private TechnicsMaterialJCombox bmyxjbJbo = new TechnicsMaterialJCombox("编码优选级别");
    //编码状态
    private JLabel bmztLabel;
    private TechnicsMaterialJCombox bmztJbo = new TechnicsMaterialJCombox("编码状态");
    //编码类型
    private JLabel bmlxLabel;
    private TechnicsMaterialJCombox bmlxJbo = new TechnicsMaterialJCombox("编码类型");
    //编码等级
    private JLabel bmdjLabel;
    private TechnicsMaterialJCombox bmdjJbo = new TechnicsMaterialJCombox("编码等级");
    //换算率
    private JLabel hslLabel;
    private TechnicsMaterialJCombox hslJbo = new TechnicsMaterialJCombox("换算率");
    //系数
    private JLabel xsLabel;
    private TechnicsMaterialJCombox xsJbo = new TechnicsMaterialJCombox("系数");
    //牌号
    private JLabel phLabel;
    private TechnicsMaterialJCombox phJbo = new TechnicsMaterialJCombox("牌号");
    //规格
    private JLabel ggLabel;
    private TechnicsMaterialJCombox ggJbo = new TechnicsMaterialJCombox("规格");
    //采用标准
    private JLabel cybzLabel;
    private TechnicsMaterialJCombox cybzJbo = new TechnicsMaterialJCombox("采用标准");
    //特殊说明
    private JLabel tssmLabel;
    private TechnicsMaterialJCombox tssmJbo = new TechnicsMaterialJCombox("特殊说明");
    //是否进口
    private JLabel sfjkLabel;
    private TechnicsMaterialJCombox sfjkJbo = new TechnicsMaterialJCombox("是否进口");
    //型号规格
    private JLabel xhggLabel;
    private TechnicsMaterialJCombox xhggJbo = new TechnicsMaterialJCombox("型号规格");
    //质量等级
    private JLabel zldjLabel;
    private TechnicsMaterialJCombox zldjJbo = new TechnicsMaterialJCombox("质量等级");
    //总规范
    private JLabel zgfLabel;
    private TechnicsMaterialJCombox zgfJbo = new TechnicsMaterialJCombox("总规范");
    //详细规范
    private JLabel xxgfLabel;
    private TechnicsMaterialJCombox xxgfJbo = new TechnicsMaterialJCombox("详细规范");
    //型号
    private JLabel xhLabel;
    private TechnicsMaterialJCombox xhJbo = new TechnicsMaterialJCombox("型号");
    //封装形式
    private JLabel fzxsLabel;
    private TechnicsMaterialJCombox fzxsJbo = new TechnicsMaterialJCombox("封装形式");
    //外形尺寸
    private JLabel wxccLabel;
    private TechnicsMaterialJCombox wxccJbo = new TechnicsMaterialJCombox("外形尺寸");
    //专用条件
    private JLabel zytjLabel;
    private TechnicsMaterialJCombox zytjJbo = new TechnicsMaterialJCombox("专用条件");
    //附加协议
    private JLabel fjxyLabel;
    private TechnicsMaterialJCombox fjxyJbo = new TechnicsMaterialJCombox("附加协议");
    //抗辐射指标TID
    private JLabel kfszbtidLabel;
    private TechnicsMaterialJCombox tidJbo = new TechnicsMaterialJCombox("抗辐射指标TID");
    //抗辐射指标SEE
    private JLabel kfszbseeLabel;
    private TechnicsMaterialJCombox seeJbo = new TechnicsMaterialJCombox("抗辐射指标SEE");
    //性能参数
    private JLabel xncsLabel;
    private TechnicsMaterialJCombox xncsJbo = new TechnicsMaterialJCombox("性能参数");
    //是否静电敏感
    private JLabel sfjdmgLabel;
    private TechnicsMaterialJCombox sfjdmgJbo = new TechnicsMaterialJCombox("是否静电敏感");
    //静电敏感等级
    private JLabel jdmgdjLabel;
    private TechnicsMaterialJCombox jdmgdjJbo = new TechnicsMaterialJCombox("静电敏感等级");
    //湿敏等级
    private JLabel smdjLabel;
    private TechnicsMaterialJCombox smdjJbo = new TechnicsMaterialJCombox("湿敏等级");
    //供应状态
    private JLabel gyztLabel;
    private TechnicsMaterialJCombox gyztJbo = new TechnicsMaterialJCombox("供应状态");
    //精度
    private JLabel jdLabel;
    private TechnicsMaterialJCombox jdJbo = new TechnicsMaterialJCombox("精度");
    //质量特征
    private JLabel zltzLabel;
    private TechnicsMaterialJCombox zltzJbo = new TechnicsMaterialJCombox("质量特征");
    //品种规格标准
    private JLabel pzggbzLabel;
    private TechnicsMaterialJCombox pzggbzJbo = new TechnicsMaterialJCombox("品种规格标准");
    //标准号
    private JLabel bzhLabel;
    private TechnicsMaterialJCombox bzhJbo = new TechnicsMaterialJCombox("标准号");
    //材料
    private JLabel clLabel;
    private TechnicsMaterialJCombox clJbo = new TechnicsMaterialJCombox("材料");
    //机械性能等级或硬度
    private JLabel jxxndjLabel;
    private TechnicsMaterialJCombox jxxndjJbo = new TechnicsMaterialJCombox("机械性能等级或硬度");
    //表面处理
    private JLabel bmclLabel;
    private TechnicsMaterialJCombox bmclJbo = new TechnicsMaterialJCombox("表面处理");
    //热处理
    private JLabel rclLabel;
    private TechnicsMaterialJCombox rclJbo = new TechnicsMaterialJCombox("热处理");
    //产品型式
    private JLabel cpxsLabel;
    private TechnicsMaterialJCombox cpxsJbo = new TechnicsMaterialJCombox("产品型式");
    //产品等级
    private JLabel cpdjLabel;
    private TechnicsMaterialJCombox cpdjJbo = new TechnicsMaterialJCombox("产品等级");
    //板拧形式
    private JLabel nbxsLabel;
    private TechnicsMaterialJCombox nbxsJbo = new TechnicsMaterialJCombox("板拧形式");
    //生产厂家
    private JLabel sccjLabel;
    private TechnicsMaterialJCombox sccjJbo = new TechnicsMaterialJCombox("生产厂家");
    //计量单位
    private JLabel jldwLabel;
    private JComboBox jldwJComboBox;
    //单位
    private JLabel gydwLabel;
    private JComboBox gydwJComboBox;
    //设置物资分类
    private JLabel wzflLabel = new JLabel("物资分类");
    private WZFLJTextField wzfljTextField;


    public LinkedHashMap<JComponent, String> componentMap;
    public LinkedHashMap<JComponent, String> standardMap;
    public LinkedHashMap<JComponent, String> materialMap;
    public LinkedHashMap<JComponent, String> nonMaterialMap;
    public LinkedHashMap<JComponent, String> compoundMap;

    public LinkedHashMap<String, String> ibaYqjMap;
    public LinkedHashMap<String, String> ibaBzjMap;
    public LinkedHashMap<String, String> ibaJsclMap;
    public LinkedHashMap<String, String> ibaFjsclMap;
    public LinkedHashMap<String, String> ibaFhclMap;

    public List<Object> componentList;
    public List<Object> standardList;
    public List<Object> materialList;
    public List<Object> nonMaterialList;
    public List<Object> compoundList;
    public List<Object> jdclList;
    public List<Object> hgpList;
    public TechnicsQuotaDZYQJJPanel technicsQuotaDZYQJJPanel;
    public TechnicsQuotaBZJGJJPanel technicsQuotaBZJGJJPanel;
    public TechnicsQuotaJSCLJPanel technicsQuotaJSCLJPanel;
    public TechnicsQuotaFJSCLJPanel technicsQuotaFJSCLJPanel;
    public TechnicsQuotaFHCLJPanel technicsQuotaFHCLJPanel;
    public TechnicsQuotaJDCLJPanel technicsQuotaJDCLJPanel;
    public TechnicsQuotaHGPJPanel technicsQuotaHGPJPanel;

    public JDialog currentDialog;

    public JTabbedPane technicsJTabbedPane;


    public SupplyNumberTechQuotaDialog(NewTechnicsPart frame, String title, XWTreeNode node, String type, JTabbedPane technicsJTabbedPane, TechnicsQuotaDZYQJJPanel technicsQuotaDZYQJJPanel,
                                       TechnicsQuotaBZJGJJPanel technicsQuotaBZJGJJPanel, TechnicsQuotaJSCLJPanel technicsQuotaJSCLJPanel, TechnicsQuotaFJSCLJPanel technicsQuotaFJSCLJPanel,
                                       TechnicsQuotaFHCLJPanel technicsQuotaFHCLJPanel, TechnicsQuotaJDCLJPanel technicsQuotaJDCLJPanel, TechnicsQuotaHGPJPanel technicsQuotaHGPJPanel) {
        super(frame);
        this.currentDialog = this;
        this.technicsJTabbedPane = technicsJTabbedPane;
        this.setTitle(title);
        this.frame = frame;
        this.node = node;
        this.technicsQuotaDZYQJJPanel = technicsQuotaDZYQJJPanel;
        this.technicsQuotaBZJGJJPanel = technicsQuotaBZJGJJPanel;
        this.technicsQuotaJSCLJPanel = technicsQuotaJSCLJPanel;
        this.technicsQuotaFJSCLJPanel = technicsQuotaFJSCLJPanel;
        this.technicsQuotaFHCLJPanel = technicsQuotaFHCLJPanel;
        this.technicsQuotaJDCLJPanel = technicsQuotaJDCLJPanel;
        this.technicsQuotaHGPJPanel = technicsQuotaHGPJPanel;
        initComponents(type);
        initAttrCollect();

        initDimension();
        initActions();
        initLayout(type);
        loadInitDatas(type);
        this.setVisible(true);
        this.setResizable(false);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    }

    public void loadInitDatas(String type) {
        JTable zTable = null;
        if ("元器件".equals(type)) {
            zTable = technicsQuotaDZYQJJPanel.getTable();
        } else if ("标准紧固件".equals(type)) {
            zTable = technicsQuotaBZJGJJPanel.getTable();
        } else if ("金属材料".equals(type)) {
            zTable = technicsQuotaJSCLJPanel.getTable();
        } else if ("非金属材料".equals(type)) {
            zTable = technicsQuotaFJSCLJPanel.getTable();
        } else if ("复合材料".equals(type)) {
            zTable = technicsQuotaFHCLJPanel.getTable();
        } else if ("机电材料".equals(type)) {
            zTable = technicsQuotaJDCLJPanel.getTable();
        } else if ("火工品".equals(type)) {
            zTable = technicsQuotaHGPJPanel.getTable();
        }
        int[] selectedRows = zTable.getSelectedRows();
        if (selectedRows.length == 0) {
            return;
        }
        if (selectedRows.length > 1) {
            JOptionPane.showMessageDialog(null, "请选择一条数据进行设置");
            currentDialog.dispose();
        }
        String wzbm = "";
        if ("元器件".equals(type)) {
            wzbm = (String) zTable.getValueAt(selectedRows[0], 26);
        } else if ("标准紧固件".equals(type)) {
            wzbm = (String) zTable.getValueAt(selectedRows[0], 19);
        } else if ("金属材料".equals(type)) {
            wzbm = (String) zTable.getValueAt(selectedRows[0], 22);
        } else if ("非金属材料".equals(type)) {
            wzbm = (String) zTable.getValueAt(selectedRows[0], 18);
        } else if ("复合材料".equals(type)) {
            wzbm = (String) zTable.getValueAt(selectedRows[0], 16);
        } else if ("机电材料".equals(type)) {
            wzbm = (String) zTable.getValueAt(selectedRows[0], 10);
        } else if ("火工品".equals(type)) {
            wzbm = (String) zTable.getValueAt(selectedRows[0], 9);
        }
        Object obj = ErpToWCIntf.getTMELinkByWzbm(wzbm, type);
        if (obj != null) {
            if (obj instanceof TMEEleComponentsPartLinkBean) {
                TMEEleComponentsPartLinkBean bean = (TMEEleComponentsPartLinkBean) obj;
                for (JComponent key : componentMap.keySet()) {
                    String s = componentMap.get(key);
                    Object getMethod = getGetMethod(bean, s);
                    String value = "";
                    if (getMethod == null) {
                        value = "";
                    } else {
                        value = String.valueOf(getMethod);
                    }
                    if (key instanceof JTextField) {
                        JTextField jtf = (JTextField) key;
                        jtf.setText(value);
                    } else if (key instanceof JComboBox) {
                        JComboBox jcbb = (JComboBox) key;
                        jcbb.setSelectedItem(value);
                    }

                }

            } else if (obj instanceof TMEStandPartLinkBean) {
                TMEStandPartLinkBean bean = (TMEStandPartLinkBean) obj;
                for (JComponent key : standardMap.keySet()) {
                    String s = standardMap.get(key);
                    Object getMethod = getGetMethod(bean, s);
                    String value = "";
                    if (getMethod == null) {
                        value = "";
                    } else {
                        value = String.valueOf(getMethod);
                    }
                    if (key instanceof JTextField) {
                        JTextField jtf = (JTextField) key;
                        jtf.setText(value);
                    } else if (key instanceof JComboBox) {
                        JComboBox jcbb = (JComboBox) key;
                        jcbb.setSelectedItem(value);
                    }
                }

            } else if (obj instanceof TMEMetallicPartLinkBean) {
                TMEMetallicPartLinkBean bean = (TMEMetallicPartLinkBean) obj;
                for (JComponent key : materialMap.keySet()) {
                    String s = materialMap.get(key);
                    Object getMethod = getGetMethod(bean, s);
                    String value = "";
                    if (getMethod == null) {
                        value = "";
                    } else {
                        value = String.valueOf(getMethod);
                    }
                    if (key instanceof JTextField) {
                        JTextField jtf = (JTextField) key;
                        jtf.setText(value);
                    } else if (key instanceof JComboBox) {
                        JComboBox jcbb = (JComboBox) key;
                        jcbb.setSelectedItem(value);
                    }
                }

            } else if (obj instanceof TMENonMetallicPartLinkBean) {
                TMENonMetallicPartLinkBean bean = (TMENonMetallicPartLinkBean) obj;
                for (JComponent key : nonMaterialMap.keySet()) {
                    String s = nonMaterialMap.get(key);
                    Object getMethod = getGetMethod(bean, s);
                    String value = "";
                    if (getMethod == null) {
                        value = "";
                    } else {
                        value = String.valueOf(getMethod);
                    }
                    if (key instanceof JTextField) {
                        JTextField jtf = (JTextField) key;
                        jtf.setText(value);
                    } else if (key instanceof JComboBox) {
                        JComboBox jcbb = (JComboBox) key;
                        jcbb.setSelectedItem(value);
                    }
                }
            } else if (obj instanceof TMECompoundMaterialPartLinkBean) {
                TMECompoundMaterialPartLinkBean bean = (TMECompoundMaterialPartLinkBean) obj;
                for (JComponent key : compoundMap.keySet()) {
                    String s = compoundMap.get(key);
                    Object getMethod = getGetMethod(bean, s);
                    String value = "";
                    if (getMethod == null) {
                        value = "";
                    } else {
                        value = String.valueOf(getMethod);
                    }
                    if (key instanceof JTextField) {
                        JTextField jtf = (JTextField) key;
                        jtf.setText(value);
                    } else if (key instanceof JComboBox) {
                        JComboBox jcbb = (JComboBox) key;
                        jcbb.setSelectedItem(value);
                    }
                }

            }
        }


    }

    public void initAttrCollect() {
        componentMap = new LinkedHashMap<JComponent, String>();
        componentMap.put(wzmcJbo, "WZMC");
        componentMap.put(xhggJbo, "XHGG");
        componentMap.put(zldjJbo, "ZLDJ");
        componentMap.put(sccjJbo, "SCCJ");
        componentMap.put(jldwJComboBox, "JLDW");
        componentMap.put(zgfJbo, "ZGF");
        componentMap.put(xxgfJbo, "XXGF");
        componentMap.put(xhJbo, "XH");
        componentMap.put(fzxsJbo, "FZXS");
        componentMap.put(wxccJbo, "WXCC");
        componentMap.put(zytjJbo, "ZYTJ");
        componentMap.put(fjxyJbo, "FJXY");
        componentMap.put(tssmJbo, "TSSM");
        componentMap.put(sfjkJbo, "SFJK");
        componentMap.put(tidJbo, "KFSZBTID");
        componentMap.put(seeJbo, "KFSZBSEE");
        componentMap.put(xncsJbo, "XNCS");
        componentMap.put(sfjdmgJbo, "SFJDMG");
        componentMap.put(jdmgdjJbo, "JDMGDJ");
        componentMap.put(smdjJbo, "SMDJ");
        componentMap.put(wzjcJbo, "WZJC");
        componentMap.put(bmyxjbJbo, "BMYXJB");
        componentMap.put(bmztJbo, "BMZT");
        componentMap.put(bmlxJbo, "BMLX");
        componentMap.put(bmdjJbo, "BMDJ");
        componentMap.put(wzfljTextField, "WZFL");
        //申请编码集合
        ibaYqjMap = new LinkedHashMap<String, String>();
        //型号规格/规格/规格
        ibaYqjMap.put("XHGG", "TYPESTANDARD");
        //质量等级/产品等级/质量特征
        ibaYqjMap.put("ZLDJ", "QUALITYLEVEL");
        //生产厂家全称
        ibaYqjMap.put("SCCJ", "MANUQC");
        //计量单位
        ibaYqjMap.put("JLDW", "UNIT");
        //总规范/标准号/采用标准
        ibaYqjMap.put("ZGF", "TOTALSTANDARD");
        //详细规范//品种标准号
        ibaYqjMap.put("XXGF", "DETAILSTANDARD");
        //型号/材料/牌号 待确认
        ibaYqjMap.put("XH", "MODEL");
        //封装形式/产品形式
        ibaYqjMap.put("FZXS", "PACKAGINGFORM");
        //外形尺寸/扳拧形式/
        ibaYqjMap.put("WXCC", "OUTLINESIZE");
        //专用条件/机械性能等级或硬度/
        ibaYqjMap.put("ZYTJ", "SPECIALCONDITION");
        //附加协议
        ibaYqjMap.put("FJXY", "EXTRACONDITION");
        //特殊说明
        ibaYqjMap.put("TSSM", "SPECIALINSTRUCTION");
        //国产进口
        ibaYqjMap.put("SFJK", "ISIMPORT");
        //抗辐射指标TID
        ibaYqjMap.put("KFSZBTID", "KFZBTID");
        //抗辐射指标SEE
        ibaYqjMap.put("KFSZBSEE", "KFZBSEE");
        //性能参数
        ibaYqjMap.put("XNCS", "XNCS");
        //是否静电敏感（是/否）
        ibaYqjMap.put("SFJDMG", "JDMGDJ_STATE");
        //静电敏感等级
        ibaYqjMap.put("JDMGDJ", "JDMGDJ");
        //湿敏等级
        ibaYqjMap.put("SMDJ", "SMDJ");
        //简称
        ibaYqjMap.put("WZJC", "SHORTNAME");
        //物资名称 新增
        ibaYqjMap.put("WZMC", "FULLNAME");
        //编码优选级别
        ibaYqjMap.put("BMYXJB", "YXJB");
        //编码状态
        ibaYqjMap.put("BMZT", "NUMBER_STATE");
        //编码类型
        ibaYqjMap.put("BMLX", "BMLX");

        componentList = new ArrayList<Object>();
        componentList.add(chmcLable);
        componentList.add(wzmcJbo);
        componentList.add(wzjcLabel);
        componentList.add(wzjcJbo);
        componentList.add(bmyxjbLabel);
        componentList.add(bmyxjbJbo);
        componentList.add(bmztLabel);
        componentList.add(bmztJbo);
        componentList.add(bmlxLabel);
        componentList.add(bmlxJbo);
        componentList.add(bmdjLabel);
        componentList.add(bmdjJbo);
        componentList.add(xhggLabel);
        componentList.add(xhggJbo);
        componentList.add(zldjLabel);
        componentList.add(zldjJbo);
        componentList.add(zgfLabel);
        componentList.add(zgfJbo);
        componentList.add(xxgfLabel);
        componentList.add(xxgfJbo);
        componentList.add(xhLabel);
        componentList.add(xhJbo);
        componentList.add(fzxsLabel);
        componentList.add(fzxsJbo);
        componentList.add(wxccLabel);
        componentList.add(wxccJbo);
        componentList.add(zytjLabel);
        componentList.add(zytjJbo);
        componentList.add(fjxyLabel);
        componentList.add(fjxyJbo);
        componentList.add(tssmLabel);
        componentList.add(tssmJbo);
        componentList.add(sfjkLabel);
        componentList.add(sfjkJbo);
        componentList.add(kfszbtidLabel);
        componentList.add(tidJbo);
        componentList.add(kfszbseeLabel);
        componentList.add(seeJbo);
        componentList.add(xncsLabel);
        componentList.add(xncsJbo);
        componentList.add(sfjdmgLabel);
        componentList.add(sfjdmgJbo);
        componentList.add(jdmgdjLabel);
        componentList.add(jdmgdjJbo);
        componentList.add(smdjLabel);
        componentList.add(smdjJbo);
        componentList.add(sccjLabel);
        componentList.add(sccjJbo);
        componentList.add(jldwLabel);
        componentList.add(jldwJComboBox);
        componentList.add(wzflLabel);
        componentList.add(wzfljTextField);

        standardMap = new LinkedHashMap<JComponent, String>();
        standardMap.put(wzmcJbo, "WZMC");
        standardMap.put(ggJbo, "GG");
        standardMap.put(bzhJbo, "BZH");
        standardMap.put(jxxndjJbo, "JXXNDJ");
        standardMap.put(jldwJComboBox, "JLDW");
        standardMap.put(clJbo, "CL");
        standardMap.put(bmclJbo, "BMCL");
        standardMap.put(rclJbo, "RCL");
        standardMap.put(sccjJbo, "SCCJ");
        standardMap.put(cpxsJbo, "CPXS");
        standardMap.put(cpdjJbo, "CPDJ");
        standardMap.put(nbxsJbo, "NBXS");
        standardMap.put(tssmJbo, "TSSM");
        standardMap.put(sfjkJbo, "SFJK");
        standardMap.put(wzjcJbo, "WZJC");
        standardMap.put(bmyxjbJbo, "BMYXJB");
        standardMap.put(bmztJbo, "BMZT");
        standardMap.put(bmlxJbo, "BMLX");
        standardMap.put(bmdjJbo, "BMDJ");
        standardMap.put(wzfljTextField, "WZFL");
        //申请接口集合
        ibaBzjMap = new LinkedHashMap<String, String>();
        //型号规格/规格/规格
        ibaBzjMap.put("GG", "TYPESTANDARD");
        //总规范/标准号/采用标准
        ibaBzjMap.put("BZH", "TOTALSTANDARD");
        //专用条件/机械性能等级或硬度/
        ibaBzjMap.put("JXXNDJ", "SPECIALCONDITION");
        //计量单位
        ibaBzjMap.put("JLDW", "UNIT");
        //型号/材料/牌号 待确认
        ibaBzjMap.put("CL", "MODEL");
        //表面处理
        ibaBzjMap.put("BMCL", "SURFACETREATMENT");
        ///热处理/供应状态
        ibaBzjMap.put("RCL", "HEATTREATMENT");
        //生产厂家简称
        ibaBzjMap.put("SCCJ", "MANUJC");
        //生产厂家全称
        ibaBzjMap.put("SCCJ", "MANUQC");
        //封装形式/产品形式/
        ibaBzjMap.put("CPXS", "PACKAGINGFORM");
        //质量等级/产品等级/质量特征
        ibaBzjMap.put("CPDJ", "QUALITYLEVEL");
        //板拧型式
        ibaBzjMap.put("NBXS", "OUTLINESIZE");
        //特殊说明
        ibaBzjMap.put("TSSM", "SPECIALINSTRUCTION");
        //国产进口
        ibaBzjMap.put("SFJK", "ISIMPORT");
        //简称
        ibaBzjMap.put("WZJC", "SHORTNAME");
        //物资名称 新增
        ibaBzjMap.put("WZMC", "FULLNAME");
        //编码优选级别
        ibaBzjMap.put("BMYXJB", "YXJB");
        //编码状态
        ibaBzjMap.put("BMZT", "NUMBER_STATE");
        //编码类型
        ibaBzjMap.put("BMLX", "BMLX");

        standardList = new ArrayList<Object>();
        standardList.add(chmcLable);
        standardList.add(wzmcJbo);
        standardList.add(wzjcLabel);
        standardList.add(wzjcJbo);
        standardList.add(bmyxjbLabel);
        standardList.add(bmyxjbJbo);
        standardList.add(bmztLabel);
        standardList.add(bmztJbo);
        standardList.add(bmlxLabel);
        standardList.add(bmlxJbo);
        standardList.add(bmdjLabel);
        standardList.add(bmdjJbo);
        standardList.add(bzhLabel);
        standardList.add(bzhJbo);
        standardList.add(ggLabel);
        standardList.add(ggJbo);
        standardList.add(clLabel);
        standardList.add(clJbo);
        standardList.add(jxxndjLabel);
        standardList.add(jxxndjJbo);
        standardList.add(bmclLabel);
        standardList.add(bmclJbo);
        standardList.add(rclLabel);
        standardList.add(rclJbo);
        standardList.add(cpxsLabel);
        standardList.add(cpxsJbo);
        standardList.add(cpdjLabel);
        standardList.add(cpdjJbo);
        standardList.add(nbxsLabel);
        standardList.add(nbxsJbo);
        standardList.add(tssmLabel);
        standardList.add(tssmJbo);
        standardList.add(sfjkLabel);
        standardList.add(sfjkJbo);
        standardList.add(sccjLabel);
        standardList.add(sccjJbo);
        standardList.add(jldwLabel);
        standardList.add(jldwJComboBox);
        standardList.add(wzflLabel);
        standardList.add(wzfljTextField);

        materialMap = new LinkedHashMap<JComponent, String>();
        materialMap.put(wzmcJbo, "WZMC");
        materialMap.put(phJbo, "PH");
        materialMap.put(ggJbo, "GG");
        materialMap.put(gyztJbo, "GYZT");
        materialMap.put(cybzJbo, "CYBZ");
        materialMap.put(gydwJComboBox, "GYDW");
        materialMap.put(pzggbzJbo, "PZGGBZ");
        materialMap.put(jdJbo, "JD");
        materialMap.put(zltzJbo, "ZLTZ");
        materialMap.put(sccjJbo, "SCCJ");
        materialMap.put(wzjcJbo, "WZJC");
        materialMap.put(tssmJbo, "TSSM");
        materialMap.put(sfjkJbo, "SFJK");
        materialMap.put(hslJbo, "HSL");
        materialMap.put(xsJbo, "XS");
        materialMap.put(bmyxjbJbo, "BMYXJB");
        materialMap.put(bmztJbo, "BMZT");
        materialMap.put(bmlxJbo, "BMLX");
        materialMap.put(bmdjJbo, "BMDJ");
        materialMap.put(wzfljTextField, "WZFL");
        //申请编码金属
        ibaJsclMap = new LinkedHashMap<String, String>();
        //型号/材料/牌号
        ibaJsclMap.put("PH", "MODEL");
        //型号规格/规格/规格
        ibaJsclMap.put("GG", "TYPESTANDARD");
        //热处理/供应状态
        ibaJsclMap.put("GYZT", "HEATTREATMENT");
        //总规范/标准号/采用标准
        ibaJsclMap.put("CYBZ", "TOTALSTANDARD");
        //工艺单位
        //ibaJsclMap.put("GYDW", "MEASURENIT");
        //计量单位
        ibaJsclMap.put("GYDW", "UNIT");
        //详细规范//品种标准号
        ibaJsclMap.put("PZGGBZ", "DETAILSTANDARD");
        //精度等级
        ibaJsclMap.put("JD", "PRECISION");
        //质量等级/产品等级/质量特征
        ibaJsclMap.put("ZLTZ", "QUALITYLEVEL");
        //生产厂家简称
        ibaJsclMap.put("SCCJ", "MANUJC");
        //生产厂家全称
        ibaJsclMap.put("SCCJ", "MANUQC");
        //简称
        ibaJsclMap.put("WZJC", "SHORTNAME");
        //物资名称 新增
        ibaJsclMap.put("WZMC", "FULLNAME");
        //特殊说明
        ibaJsclMap.put("TSSM", "SPECIALINSTRUCTION");
        //国产进口
        ibaJsclMap.put("SFJK", "ISIMPORT");
        //换算率
        ibaJsclMap.put("HSL", "RATEOFCONVERSION");
        //系数
        ibaJsclMap.put("XS", "RATIO");
        //物资简称
        ibaJsclMap.put("WZJC", "SHORTNAME");
        //物资名称
        ibaJsclMap.put("WZMC", "FULLNAME");
        //编码优选级别
        ibaJsclMap.put("BMYXJB", "YXJB");
        //编码状态
        ibaJsclMap.put("BMZT", "NUMBER_STATE");
        //编码类型
        ibaJsclMap.put("BMLX", "BMLX");

        materialList = new ArrayList<Object>();
        materialList.add(chmcLable);
        materialList.add(wzmcJbo);
        materialList.add(wzjcLabel);
        materialList.add(wzjcJbo);
        materialList.add(bmyxjbLabel);
        materialList.add(bmyxjbJbo);
        materialList.add(bmztLabel);
        materialList.add(bmztJbo);
        materialList.add(bmlxLabel);
        materialList.add(bmlxJbo);
        materialList.add(bmdjLabel);
        materialList.add(bmdjJbo);
        materialList.add(hslLabel);
        materialList.add(hslJbo);
        materialList.add(xsLabel);
        materialList.add(xsJbo);
        materialList.add(phLabel);
        materialList.add(phJbo);
        materialList.add(gyztLabel);
        materialList.add(gyztJbo);
        materialList.add(cybzLabel);
        materialList.add(cybzJbo);
        materialList.add(jdLabel);
        materialList.add(jdJbo);
        materialList.add(zltzLabel);
        materialList.add(zltzJbo);
        materialList.add(pzggbzLabel);
        materialList.add(pzggbzJbo);
        materialList.add(tssmLabel);
        materialList.add(tssmJbo);
        materialList.add(sfjkLabel);
        materialList.add(sfjkJbo);
        materialList.add(sccjLabel);
        materialList.add(sccjJbo);
        materialList.add(ggLabel);
        materialList.add(ggJbo);
        materialList.add(gydwLabel);
        materialList.add(gydwJComboBox);
        materialList.add(wzflLabel);
        materialList.add(wzfljTextField);

        nonMaterialMap = new LinkedHashMap<JComponent, String>();
        nonMaterialMap.put(wzmcJbo, "WZMC");
        nonMaterialMap.put(phJbo, "PH");
        nonMaterialMap.put(ggJbo, "GG");
        nonMaterialMap.put(cybzJbo, "CYBZ");
        nonMaterialMap.put(gydwJComboBox, "GYDW");
        nonMaterialMap.put(sccjJbo, "SCCJ");
        nonMaterialMap.put(tssmJbo, "TSSM");
        nonMaterialMap.put(sfjkJbo, "SFJK");
        nonMaterialMap.put(wzjcJbo, "WZJC");
        nonMaterialMap.put(hslJbo, "HSL");
        nonMaterialMap.put(xsJbo, "XS");
        nonMaterialMap.put(bmyxjbJbo, "BMYXJB");
        nonMaterialMap.put(bmztJbo, "BMZT");
        nonMaterialMap.put(bmlxJbo, "BMLX");
        nonMaterialMap.put(bmdjJbo, "BMDJ");
        nonMaterialMap.put(wzfljTextField, "WZFL");
        //申请编码-非金属
        ibaFjsclMap = new LinkedHashMap<String, String>();
        //型号/材料/牌号
        ibaFjsclMap.put("PH", "MODEL");
        //型号规格/规格/规格
        ibaFjsclMap.put("GG", "TYPESTANDARD");
        //热处理/供应状态
        ibaFjsclMap.put("GYZT", "HEATTREATMENT");
        //总规范/标准号/采用标准
        ibaFjsclMap.put("CYBZ", "TOTALSTANDARD");
        //工艺单位
        //ibaFjsclMap.put("GYDW", "MEASURENIT");
        //计量单位
        ibaFjsclMap.put("GYDW", "UNIT");
        //详细规范//品种标准号
        ibaFjsclMap.put("PZGGBZ", "DETAILSTANDARD");
        //精度等级
        ibaFjsclMap.put("JD", "PRECISION");
        //质量等级/产品等级/质量特征
        ibaFjsclMap.put("ZLTZ", "QUALITYLEVEL");
        //生产厂家简称
        ibaFjsclMap.put("SCCJ", "MANUJC");
        //生产厂家全称
        ibaFjsclMap.put("SCCJ", "MANUQC");
        //简称
        ibaFjsclMap.put("WZJC", "SHORTNAME");
        //物资名称 新增
        ibaFjsclMap.put("WZMC", "FULLNAME");
        //特殊说明
        ibaFjsclMap.put("TSSM", "SPECIALINSTRUCTION");
        //国产进口
        ibaFjsclMap.put("SFJK", "ISIMPORT");
        //换算率
        ibaFjsclMap.put("HSL", "RATEOFCONVERSION");
        //系数
        ibaFjsclMap.put("XS", "RATIO");
        //物资简称
        ibaFjsclMap.put("WZJC", "SHORTNAME");
        //物资名称
        ibaFjsclMap.put("WZMC", "FULLNAME");
        //编码优选级别
        ibaFjsclMap.put("BMYXJB", "YXJB");
        //编码状态
        ibaFjsclMap.put("BMZT", "NUMBER_STATE");
        //编码类型
        ibaFjsclMap.put("BMLX", "BMLX");

        nonMaterialList = new ArrayList<Object>();
        nonMaterialList.add(chmcLable);
        nonMaterialList.add(wzmcJbo);
        nonMaterialList.add(wzjcLabel);
        nonMaterialList.add(wzjcJbo);
        nonMaterialList.add(bmyxjbLabel);
        nonMaterialList.add(bmyxjbJbo);
        nonMaterialList.add(bmztLabel);
        nonMaterialList.add(bmztJbo);
        nonMaterialList.add(bmlxLabel);
        nonMaterialList.add(bmlxJbo);
        nonMaterialList.add(bmdjLabel);
        nonMaterialList.add(bmdjJbo);
        nonMaterialList.add(hslLabel);
        nonMaterialList.add(hslJbo);
        nonMaterialList.add(xsLabel);
        nonMaterialList.add(xsJbo);
        nonMaterialList.add(phLabel);
        nonMaterialList.add(phJbo);
        nonMaterialList.add(ggLabel);
        nonMaterialList.add(ggJbo);
        nonMaterialList.add(cybzLabel);
        nonMaterialList.add(cybzJbo);
        nonMaterialList.add(tssmLabel);
        nonMaterialList.add(tssmJbo);
        nonMaterialList.add(sfjkLabel);
        nonMaterialList.add(sfjkJbo);
        nonMaterialList.add(sccjLabel);
        nonMaterialList.add(sccjJbo);
        nonMaterialList.add(gydwLabel);
        nonMaterialList.add(gydwJComboBox);
        nonMaterialList.add(wzflLabel);
        nonMaterialList.add(wzfljTextField);

        compoundMap = new LinkedHashMap<JComponent, String>();
        compoundMap.put(wzmcJbo, "WZMC");
        compoundMap.put(phJbo, "PH");
        compoundMap.put(ggJbo, "GG");
        compoundMap.put(cybzJbo, "CYBZ");
        compoundMap.put(jldwJComboBox, "JLDW");
        compoundMap.put(sccjJbo, "SCCJ");
        compoundMap.put(tssmJbo, "TSSM");
        compoundMap.put(sfjkJbo, "SFJK");
        compoundMap.put(wzjcJbo, "WZJC");
        compoundMap.put(hslJbo, "HSL");
        compoundMap.put(xsJbo, "XS");
        compoundMap.put(bmyxjbJbo, "BMYXJB");
        compoundMap.put(bmztJbo, "BMZT");
        compoundMap.put(bmlxJbo, "BMLX");
        compoundMap.put(bmdjJbo, "BMDJ");
        compoundMap.put(wzfljTextField, "WZFL");
        //申请编码-复合材料
        ibaFhclMap = new LinkedHashMap<String, String>();
        //型号/材料/牌号
        ibaFhclMap.put("PH", "MODEL");
        //型号规格/规格/规格
        ibaFhclMap.put("GG", "TYPESTANDARD");
        //热处理/供应状态
        ibaFhclMap.put("GYZT", "HEATTREATMENT");
        //总规范/标准号/采用标准
        ibaFhclMap.put("CYBZ", "TOTALSTANDARD");
        //工艺单位
        //ibaFhclMap.put("GYDW", "MEASURENIT");
        //计量单位
        ibaFhclMap.put("JLDW", "UNIT");
        //详细规范//品种标准号
        ibaFhclMap.put("PZGGBZ", "DETAILSTANDARD");
        //精度等级
        ibaFhclMap.put("JD", "PRECISION");
        //质量等级/产品等级/质量特征
        ibaFhclMap.put("ZLTZ", "QUALITYLEVEL");
        //生产厂家简称
        ibaFhclMap.put("SCCJ", "MANUJC");
        //生产厂家全称
        ibaFhclMap.put("SCCJ", "MANUQC");
        //简称
        ibaFhclMap.put("WZJC", "SHORTNAME");
        //物资名称 新增
        ibaFhclMap.put("WZMC", "FULLNAME");
        //特殊说明
        ibaFhclMap.put("TSSM", "SPECIALINSTRUCTION");
        //国产进口
        ibaFhclMap.put("SFJK", "ISIMPORT");
        //换算率
        ibaFhclMap.put("HSL", "RATEOFCONVERSION");
        //系数
        ibaFhclMap.put("XS", "RATIO");
        //物资简称
        ibaFhclMap.put("WZJC", "SHORTNAME");
        //物资名称
        ibaFhclMap.put("WZMC", "FULLNAME");
        //编码优选级别
        ibaFhclMap.put("BMYXJB", "YXJB");
        //编码状态
        ibaFhclMap.put("BMZT", "NUMBER_STATE");
        //编码类型
        ibaFhclMap.put("BMLX", "BMLX");

        compoundList = new ArrayList<Object>();
        compoundList.add(chmcLable);
        compoundList.add(wzmcJbo);
        compoundList.add(wzjcLabel);
        compoundList.add(wzjcJbo);
        compoundList.add(bmyxjbLabel);
        compoundList.add(bmyxjbJbo);
        compoundList.add(bmztLabel);
        compoundList.add(bmztJbo);
        compoundList.add(bmlxLabel);
        compoundList.add(bmlxJbo);
        compoundList.add(bmdjLabel);
        compoundList.add(bmdjJbo);
        compoundList.add(hslLabel);
        compoundList.add(hslJbo);
        compoundList.add(xsLabel);
        compoundList.add(xsJbo);
        compoundList.add(phLabel);
        compoundList.add(phJbo);
        compoundList.add(ggLabel);
        compoundList.add(ggJbo);
        compoundList.add(cybzLabel);
        compoundList.add(cybzJbo);
        compoundList.add(tssmLabel);
        compoundList.add(tssmJbo);
        compoundList.add(sfjkLabel);
        compoundList.add(sfjkJbo);
        compoundList.add(sccjLabel);
        compoundList.add(sccjJbo);
        compoundList.add(jldwLabel);
        compoundList.add(jldwJComboBox);
        compoundList.add(wzflLabel);
        compoundList.add(wzfljTextField);

    }


    @Override
    protected void initActions() {

    }

    @Override
    protected void initComponents() {

    }

    protected void initComponents(String type) {
        databaseLable = new JLabel("基础数据库");
        databaseLable.setForeground(Color.red);
        chmcLable = new JLabel("物资名称");
        wzjcLabel = new JLabel("物资简称");
        bmyxjbLabel = new JLabel("编码优选级别");
        bmztLabel = new JLabel("编码状态");
        bmlxLabel = new JLabel("编码类型");
        bmdjLabel = new JLabel("编码等级");
        hslLabel = new JLabel("换算率");
        xsLabel = new JLabel("系数");
        phLabel = new JLabel("牌号");
        gyztLabel = new JLabel("供应状态");
        cybzLabel = new JLabel("采用标准");
        jdLabel = new JLabel("精度");
        zltzLabel = new JLabel("质量特征");
        pzggbzLabel = new JLabel("品种规格标准");
        tssmLabel = new JLabel("特殊说明");
        sfjkLabel = new JLabel("是否进口");
        bzhLabel = new JLabel("标准号");
        clLabel = new JLabel("材料");
        jxxndjLabel = new JLabel("机械性能等级或硬度");
        bmclLabel = new JLabel("表面处理");
        rclLabel = new JLabel("热处理");
        cpxsLabel = new JLabel("产品型式");
        cpdjLabel = new JLabel("产品等级");
        nbxsLabel = new JLabel("板拧形式");
        xhggLabel = new JLabel("型号规格");
        zldjLabel = new JLabel("质量等级");
        zgfLabel = new JLabel("总规范");
        xxgfLabel = new JLabel("详细规范");
        xhLabel = new JLabel("型号");
        fzxsLabel = new JLabel("封装形式");
        wxccLabel = new JLabel("外形尺寸");
        zytjLabel = new JLabel("专用条件");
        fjxyLabel = new JLabel("附加协议");
        kfszbtidLabel = new JLabel("抗辐射指标TID");
        kfszbseeLabel = new JLabel("抗辐射指标SEE");
        xncsLabel = new JLabel("性能参数");
        sfjdmgLabel = new JLabel("是否静电敏感");
        jdmgdjLabel = new JLabel("静电敏感等级");
        smdjLabel = new JLabel("湿敏等级");
        ggLabel = new JLabel("规格");
        sccjLabel = new JLabel("生产厂家");
        String[] keyComponentValues = {"个", "只", "件", "升", "毫升", "立方米", "平方米", "米", "毫米", "克", "千克", "磅(lb)", "英寸(inch)"};
        jldwLabel = new JLabel("计量单位");
        jldwJComboBox = new JComboBox(keyComponentValues);
        gydwLabel = new JLabel("单位");
        gydwJComboBox = new JComboBox(keyComponentValues);
        String nodeName = "";
        if ("元器件".equals(type)) {
            nodeName = "01_元器件";
        } else if ("标准紧固件".equals(type)) {
            nodeName = "02_标准件";
        } else if ("金属材料".equals(type)) {
            nodeName = "03_金属材料";
        } else if ("非金属材料".equals(type)) {
            nodeName = "04_非金属材料";
        } else if ("复合材料".equals(type)) {
            nodeName = "05_复合材料";
        }
        wzfljTextField = new WZFLJTextField(frame, nodeName);
    }

    @Override
    protected void initLayout() {

    }

    protected void initLayout(final String type) {
        JPanel jPanel = new JPanel();
        JPanel bottomPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        //setContentPane(jPanel);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(jPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        this.setContentPane(mainPanel);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(25, 10, 25, 10);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        jPanel.setLayout(new GridBagLayout());
        int width = 80;
        c.gridwidth = 1;
        List<Object> layOutList = new ArrayList<Object>();
        String nodeName = "";
        if ("元器件".equals(type)) {
            layOutList = componentList;
            nodeName = "01_元器件";
        } else if ("标准紧固件".equals(type)) {
            layOutList = standardList;
            nodeName = "02_标准件";
        } else if ("金属材料".equals(type)) {
            layOutList = materialList;
            nodeName = "03_金属材料";
        } else if ("非金属材料".equals(type)) {
            layOutList = nonMaterialList;
            nodeName = "04_非金属材料";
        } else if ("复合材料".equals(type)) {
            layOutList = compoundList;
            nodeName = "05_复合材料";
        }
        double row = Math.ceil((double) layOutList.size() / (double) 8);
        for (int i = 1; i < row + 1; i++) {
            //物资编码
            c.gridy = i;
            for (int j = 0; j < 8; j++) {
                int forRow = (i - 1) * 8 + j;
                if (forRow >= layOutList.size()) {
                    break;
                }
                c.gridx = j;
                Component component = (Component) layOutList.get(forRow);
                if (component instanceof JTextField) {
                    component.setPreferredSize(new Dimension(120, 30));
                }
                jPanel.add((Component) layOutList.get(forRow), c);
            }
        }
        JButton saveButtom = new JButton();
        //JButton saveButtom =new GwIconButton("/images/tech_quota_save.png", "保存数据");
        Dimension buttonDimension = new Dimension(120, 30);
        saveButtom.setPreferredSize(buttonDimension);
        saveButtom.setMinimumSize(buttonDimension);
        saveButtom.setMaximumSize(buttonDimension);
        saveButtom.setText("保存数据");
        saveButtom.setIcon(IconUtil.getImageIcon("/images/tech_quota_save.png"));
        saveButtom.setToolTipText("保存数据");
        bottomPanel.add(saveButtom, BorderLayout.CENTER);

        saveButtom.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
               // reset();
                final Map<String, String> conditionMap = new HashMap<String, String>();
                conditionLinkMap = new LinkedHashMap<JComponent, String>();
                //用来存储iba
                Map<String, String> ibaMap = new HashMap<String, String>();
                List<Object> nameList = new ArrayList<Object>();
                //根据不同的页签获取不同值
                if ("元器件".equals(type)) {
                    conditionLinkMap = componentMap;
                    nameList = componentList;
                    ibaMap = ibaYqjMap;
                } else if ("标准紧固件".equals(type)) {
                    conditionLinkMap = standardMap;
                    nameList = standardList;
                    ibaMap = ibaBzjMap;
                } else if ("金属材料".equals(type)) {
                    conditionLinkMap = materialMap;
                    nameList = materialList;
                    ibaMap = ibaJsclMap;
                } else if ("非金属材料".equals(type)) {
                    conditionLinkMap = nonMaterialMap;
                    nameList = nonMaterialList;
                    ibaMap = ibaFjsclMap;
                } else if ("复合材料".equals(type)) {
                    conditionLinkMap = compoundMap;
                    nameList = compoundList;
                    ibaMap = ibaFhclMap;
                }
                //校验物资名称必填
                String text1 = (String) wzmcJbo.getSelectedItem();
                if ("null".equals(text1) || "".equals(text1) || text1 == null) {
                    JOptionPane.showMessageDialog(owner, "物资名称必填");
                    return;
                }
                //校验物资分类必填
                String wzflText = (String) wzfljTextField.getText();
                if ("null".equals(wzflText) || "".equals(wzflText) || wzflText == null) {
                    JOptionPane.showMessageDialog(owner, "物资分类必填");
                    return;
                }

                //校验工艺物资名称是否合法
                final Map<String, String> validateConditionMap = new HashMap<String, String>();
                for (JComponent key : conditionLinkMap.keySet()) {
                    int i = nameList.indexOf(key);
                    String text = "";
                    if (key instanceof JComboBox) {
                        JComboBox jComboBox = (JComboBox) key;
                        text = (String) jComboBox.getSelectedItem();
                    }
                    JLabel o = (JLabel) nameList.get(i - 1);
                    if (!"null".equals(text) && !"".equals(text) && text != null) {
                        String labelValue = o.getText();
                        if ("单位".equals(labelValue)) {
                            labelValue = "工艺单位";
                        }
                        validateConditionMap.put(labelValue, text);
                    }
                }
                String msg = ErpToWCIntf.validateTmInfo(validateConditionMap);
                if (!"null".equals(msg) && !"".equals(msg) && msg != null) {
                    JOptionPane.showMessageDialog(owner, msg);
                    return;
                }
                for (JComponent key : conditionLinkMap.keySet()) {

                    String text = "";
                    if (key instanceof JTextField) {
                        JTextField jField = (JTextField) key;
                        text = jField.getText();
                    } else if (key instanceof JComboBox) {
                        JComboBox jComboBox = (JComboBox) key;
                        text = (String) jComboBox.getSelectedItem();
                    }
                    if ("null".equals(text)) {
                        text = "";
                    }
                    String column = conditionLinkMap.get(key);
                    conditionMap.put(column, text);
                }
                final VaActionProgressBar progressBar = new VaActionProgressBar(frame, "申请C类码", "正在向院设计资源库申请C类码,请等待...", "申请数据中");
                final Map<String, String> finalIbaMap = ibaMap;
                Thread thread = new Thread() {
                    public void run() {
                        List list = ErpToWCIntf.getRealTechMaterialInfo(type, conditionMap);
                        if (list.size() > 0) {
                            //判断查出数据和现有数据是否一致
                            if(!validateDate(type,list)){
                                addRowInfo(list, type);
                            }
                            progressBar.finish();
                            progressBar.setVisible(false);
                            currentDialog.dispose();
                        } else {
                            int flag = JOptionPane.showConfirmDialog(owner, "是否申请编码并创建新的物资条目信息？", "确认", JOptionPane.OK_CANCEL_OPTION);
                            if (flag == 0) {
                                //申请编码
                                String csnumber = "GYWZTM" + ErpToWCIntf.getTechnicsMaterialEntriesNum();
                                String wzmc = convertToString(conditionMap.get("WZMC"));
                                String wzfl = convertToString(conditionMap.get("WZFL"));
                                String classNum = "";
                                String className = "";
                                if (!"".equals(wzfl) && !"null".equals(wzfl) && wzfl != null) {
                                    String[] s = wzfl.split("_");
                                    if (s.length == 2) {
                                        classNum = s[0];
                                        className = s[1];
                                    }
                                }
                                Map<String, String> attrMap = new HashMap<String, String>();
                                attrMap.put("csnumber", csnumber);
                                attrMap.put("wzmc", wzmc);
                                attrMap.put("wzfl", wzfl);
                                attrMap.put("classNum", classNum);
                                attrMap.put("className", className);
                                attrMap.put("type", type);
                                System.out.println("----csnumber--" + csnumber + "-wzmc---" + wzmc + "--wzfl--" + wzfl + "---classNum---" + classNum);
                                Map<String, String> sendMap = new HashMap<String, String>();
                                for (String key : finalIbaMap.keySet()) {
                                    String s = conditionMap.get(key);
                                    if("SCCJ".equals(key)){
                                        if (!"".equals(s) && !"null".equals(s) && s != null) {
                                            sendMap.put(finalIbaMap.get(key), s);
                                            sendMap.put("MANUJC", s);
                                        } else {
                                            sendMap.put(finalIbaMap.get(key), "");
                                            sendMap.put("MANUJC", "");
                                        }
                                    }else{
                                        if (!"".equals(s) && !"null".equals(s) && s != null) {
                                            sendMap.put(finalIbaMap.get(key), s);
                                        } else {
                                            sendMap.put(finalIbaMap.get(key), "");
                                        }
                                    }
                                }
                                //调用资源库接口
                                String msg = ErpToWCIntf.supplyNum(attrMap, sendMap);
                                if (msg != null && !msg.contains("通过")) {
                                    JOptionPane.showMessageDialog(owner, msg);
                                    progressBar.finish();
                                    progressBar.setVisible(false);
                                    return;
                                }
                                //创建细新的物资条目信息，并返回
                                String sjbm = "";
                                String[] strings = msg.split("partCode");
                                JOptionPane.showMessageDialog(owner, msg);
                                if(strings.length==3){
                                    sjbm = strings[1];
                                    sjbm = sjbm.substring(4,sjbm.length()-5);
                                    conditionMap.put("SJBM",sjbm);
                                }
                                Object techniMaterial = ErpToWCIntf.createTechniMaterial(conditionMap, type,csnumber);
                                if (techniMaterial != null) {
                                    loadCurrentData(techniMaterial, type);
                                    progressBar.finish();
                                    progressBar.setVisible(false);
                                    currentDialog.dispose();
                                }
                            } else {
                                // currentDialog.dispose();
                                progressBar.finish();
                                progressBar.setVisible(false);
                                //currentDialog.dispose();
                            }
                        }
                    }
                };
                thread.start();
                progressBar.setVisible(true);


            }
        });
    }

    /**
     * 未选择的的下拉框值设为“”
     */
    public void reset() {
        for (JComponent Comp : conditionLinkMap.keySet()) {
            if (Comp instanceof JComboBox) {
                JComboBox box = (JComboBox) Comp;
                String selectedItem = (String) box.getSelectedItem();
                int itemCount = box.getItemCount();
                boolean falg = false;
                for (int i = 0; i < itemCount; i++) {
                    String value = (String) box.getItemAt(i);
                    if (selectedItem.equals(value)) {
                        falg = true;
                        break;
                    }
                }
                if (!falg) {
                    box.setSelectedItem("");
                }

            }
        }
    }

    public String getXmflByDialog(AbstractERPDialog parentDialog,String type){
        String xmfl = "";
        if(parentDialog !=null){
            if ("元器件".equals(type)) {
                if(parentDialog instanceof GwPartTechQuotaDialog){
                    xmfl = "主要材料";
                }else if(parentDialog instanceof GwAssembTechQuotaDialog){
                    xmfl = "配套件";
                }
            } else if ("标准紧固件".equals(type)) {
                if(parentDialog instanceof GwPartTechQuotaDialog){
                    xmfl = "主要材料";
                }else if(parentDialog instanceof GwAssembTechQuotaDialog){
                    xmfl = "配套件";
                }
            } else if ("金属材料".equals(type)) {
                if(parentDialog instanceof GwPartTechQuotaDialog){
                    xmfl = "原材料";
                }else if(parentDialog instanceof GwAssembTechQuotaDialog){
                    xmfl = "配套件";
                }
            } else if ("非金属材料".equals(type)) {
                if(parentDialog instanceof GwPartTechQuotaDialog){
                    xmfl = "原材料";
                }else if(parentDialog instanceof GwAssembTechQuotaDialog){
                    xmfl = "配套件";
                }
            } else if ("复合材料".equals(type)) {
                if(parentDialog instanceof GwPartTechQuotaDialog){
                    xmfl = "主要材料";
                }else if(parentDialog instanceof GwAssembTechQuotaDialog){
                    xmfl = "配套件";
                }
            } else if ("机电材料".equals(type)) {
                if(parentDialog instanceof GwPartTechQuotaDialog){
                    xmfl = "主要材料";
                }else if(parentDialog instanceof GwAssembTechQuotaDialog){
                    xmfl = "配套件";
                }
            } else if ("火工品".equals(type)) {
                if(parentDialog instanceof GwPartTechQuotaDialog){
                    xmfl = "主要材料";
                }else if(parentDialog instanceof GwAssembTechQuotaDialog){
                    xmfl = "配套件";
                }
            }
        }
        return xmfl;
    }

    /**
     * 加载数据
     *
     * @param objData
     * @param type
     */
    public void loadCurrentData(Object objData, String type) {
        DefaultTableModel model = null;
        AbstractERPDialog parentDialog = null;
        if ("元器件".equals(type)) {
            model = (DefaultTableModel) technicsQuotaDZYQJJPanel.getTableModel();
            parentDialog = technicsQuotaDZYQJJPanel.parentDialog;
        } else if ("标准紧固件".equals(type)) {
            model = (DefaultTableModel) technicsQuotaBZJGJJPanel.getTable().getModel();
        } else if ("金属材料".equals(type)) {
            parentDialog = technicsQuotaJSCLJPanel.parentDialog;
            model = (DefaultTableModel) technicsQuotaJSCLJPanel.getTableModel();
        } else if ("非金属材料".equals(type)) {
            parentDialog = technicsQuotaFJSCLJPanel.parentDialog;
            model = (DefaultTableModel) technicsQuotaFJSCLJPanel.getTableModel();
        } else if ("复合材料".equals(type)) {
            parentDialog = technicsQuotaFHCLJPanel.parentDialog;
            model = (DefaultTableModel) technicsQuotaFHCLJPanel.getTableModel();
        } else if ("机电材料".equals(type)) {
            parentDialog = technicsQuotaJDCLJPanel.parentDialog;
            model = (DefaultTableModel) technicsQuotaJDCLJPanel.getTableModel();
        } else if ("火工品".equals(type)) {
            parentDialog = technicsQuotaHGPJPanel.parentDialog;
            model = (DefaultTableModel) technicsQuotaHGPJPanel.getTableModel();
        }
        String xmfl = getXmflByDialog(parentDialog,type);
        int row = model.getRowCount();
        //model.setRowCount(0);
        Object obj = objData;
        if (obj instanceof TMEEleComponentsPartLinkBean) {
            TMEEleComponentsPartLinkBean bean = (TMEEleComponentsPartLinkBean) obj;
            Object[] values = new Object[componentMap.size() + 14];
            values[0] = bean.getTechnicsmaterialentriesid();
            values[1] = false;
            values[2] = xmfl;
            int intFlag = 3;
            for (JComponent key : componentMap.keySet()) {
                String s = componentMap.get(key);
                Object getMethod = getGetMethod(bean, s);
                String value = "";
                if (getMethod == null) {
                    value = "";
                } else {
                    value = String.valueOf(getMethod);
                }
                values[intFlag] = value;
                if ("SCCJ".equals(s) || "JLDW".equals(s)) {
                    for (int i = intFlag + 1; i < intFlag + 2; i++) {
                        values[i] = "";
                    }
                    intFlag = intFlag + 1;
                }
                if ("SMDJ".equals(s)) {
                    values[intFlag + 1] = convertToString(bean.getSjbm());
                    values[intFlag + 2] = bean.getWzbm();
                    intFlag = intFlag + 2;
                }
                intFlag++;
            }
            model.addRow(values);
            JTable dzyqjTable = technicsQuotaDZYQJJPanel.getTable();
            int rowCount = dzyqjTable.getRowCount();
            setTabTitle(0, "电子元器件", technicsJTabbedPane, rowCount);

        } else if (obj instanceof TMEStandPartLinkBean) {
            TMEStandPartLinkBean bean = (TMEStandPartLinkBean) obj;
            Object[] values = new Object[standardMap.size() + 12];
            values[0] = bean.getTechnicsmaterialentriesid();
            values[1] = false;
            values[2] = xmfl;
            int intFlag = 3;
            for (Object key : standardMap.keySet()) {
                Object s = standardMap.get(key);
                Object getMethod = getGetMethod(bean, s);
                String value = "";
                if (getMethod == null) {
                    value = "";
                } else {
                    value = String.valueOf(getMethod);
                }
                values[intFlag] = value;
                if ("JXXNDJ".equals(s)) {
                    values[intFlag + 1] = "";
                    intFlag = intFlag + 1;
                }
                if ("SFJK".equals(s)) {
                    values[intFlag + 1] = convertToString(bean.getSjbm());
                    values[intFlag + 2] = bean.getWzbm();
                    intFlag = intFlag + 2;
                }
                intFlag++;
            }
            model.addRow(values);
            JTable bzjTable = technicsQuotaBZJGJJPanel.getTable();
            int rowCount = bzjTable.getRowCount();
            setTabTitle(1, "标准紧固件", technicsJTabbedPane, rowCount);
        } else if (obj instanceof TMEMetallicPartLinkBean) {
            TMEMetallicPartLinkBean bean = (TMEMetallicPartLinkBean) obj;
            Object[] values = new Object[materialMap.size() + 12];
            values[0] = bean.getTechnicsmaterialentriesid();
            values[1] = false;
            values[2] = xmfl;
            int intFlag = 3;
            for (JComponent key : materialMap.keySet()) {
                String s = materialMap.get(key);
                Object getMethod = getGetMethod(bean, s);
                String value = "";
                if (getMethod == null) {
                    value = "";
                } else {
                    value = String.valueOf(getMethod);
                }
                values[intFlag] = value;
                if ("CYBZ".equals(s)) {
                    values[intFlag + 1] = "";
                    values[intFlag + 2] = "";
                    values[intFlag + 3] = "";
                    intFlag = intFlag + 3;
                }
                if ("XS".equals(s)) {
                    values[intFlag + 1] =convertToString( bean.getSjbm());
                    values[intFlag + 2] = bean.getWzbm();
                    intFlag = intFlag + 2;
                }
                intFlag++;
            }
            model.addRow(values);
            JTable jsclTable = technicsQuotaJSCLJPanel.getTable();
            int rowCount = jsclTable.getRowCount();
            setTabTitle(2, "金属材料", technicsJTabbedPane, rowCount);
        } else if (obj instanceof TMENonMetallicPartLinkBean) {
            TMENonMetallicPartLinkBean bean = (TMENonMetallicPartLinkBean) obj;
            Object[] values = new Object[nonMaterialMap.size() + 12];
            values[0] = bean.getTechnicsmaterialentriesid();
            values[1] = false;
            values[2] = xmfl;
            int intFlag = 3;
            for (JComponent key : nonMaterialMap.keySet()) {
                String s = nonMaterialMap.get(key);
                Object getMethod = getGetMethod(bean, s);
                String value = "";
                if (getMethod == null) {
                    value = "";
                } else {
                    value = String.valueOf(getMethod);
                }
                values[intFlag] = value;
                if ("CYBZ".equals(s)) {
                    values[intFlag + 1] = "";
                    values[intFlag + 2] = "";
                    intFlag = intFlag + 2;
                }
                if ("GYDW".equals(s)) {
                    values[intFlag + 1] = "";
                    intFlag = intFlag + 1;
                }
                if ("XS".equals(s)) {
                    values[intFlag + 1] =convertToString(bean.getSjbm());
                    values[intFlag + 2] = bean.getWzbm();
                    intFlag = intFlag + 2;
                }
                intFlag++;
            }
            model.addRow(values);
            JTable fjsclTable = technicsQuotaFJSCLJPanel.getTable();
            int rowCount = fjsclTable.getRowCount();
            setTabTitle(3, "非金属材料", technicsJTabbedPane, rowCount);
        } else if (obj instanceof TMECompoundMaterialPartLinkBean) {
            TMECompoundMaterialPartLinkBean bean = (TMECompoundMaterialPartLinkBean) obj;
            Object[] values = new Object[compoundMap.size() + 12];
            values[0] = bean.getTechnicsmaterialentriesid();
            values[1] = false;
            values[2] = xmfl;
            int intFlag = 3;
            for (JComponent key : compoundMap.keySet()) {
                String s = compoundMap.get(key);
                Object getMethod = getGetMethod(bean, s);
                String value = "";
                if (getMethod == null) {
                    value = "";
                } else {
                    value = String.valueOf(getMethod);
                }
                values[intFlag] = value;

                if ("JLDW".equals(s)) {
                    values[intFlag + 1] = "";
                    intFlag = intFlag + 1;
                }
                if ("XS".equals(s)) {
                    values[intFlag + 1] = convertToString(bean.getSjbm());
                    values[intFlag + 2] = bean.getWzbm();
                    intFlag = intFlag + 2;
                }
                intFlag++;
            }
            model.addRow(values);
            JTable fhclTable = technicsQuotaFHCLJPanel.getTable();
            int rowCount = fhclTable.getRowCount();
            setTabTitle(4, "复合材料", technicsJTabbedPane, rowCount);

        }

    }

    public void addRowInfo(List list, String type) {
        Object o = list.get(0);
        loadCurrentData(o, type);
    }

    public Object getGetMethod(Object ob, Object name) {
        try {
            Method[] m = ob.getClass().getMethods();
            for (int i = 0; i < m.length; i++) {
                if (("get" + name).toLowerCase().equals(m[i].getName().toLowerCase())) {
                    return m[i].invoke(ob);
                }
            }
        } catch (Exception e) {

        }
        return null;
    }

    public static void setTabTitle(int index, String title, JTabbedPane technicsJTabbedPane, int rowCount) {

        technicsJTabbedPane.setTitleAt(index, title + "(" + rowCount + ")");
    }

    /**
     * 将null或者字符串null转为string的空值
     *
     * @param obj
     * @return
     */
    public String convertToString(Object obj) {
        if (obj == null) {
            return "";
        } else {
            return String.valueOf(obj);
        }
    }

    @Override
    protected void loadInitDatas() {

    }

    public boolean validateDate(String type, List list) {

        JTable zTable = null;
        if ("元器件".equals(type)) {
            zTable = technicsQuotaDZYQJJPanel.getTable();
        } else if ("标准紧固件".equals(type)) {
            zTable = technicsQuotaBZJGJJPanel.getTable();
        } else if ("金属材料".equals(type)) {
            zTable = technicsQuotaJSCLJPanel.getTable();
        } else if ("非金属材料".equals(type)) {
            zTable = technicsQuotaFJSCLJPanel.getTable();
        } else if ("复合材料".equals(type)) {
            zTable = technicsQuotaFHCLJPanel.getTable();
        } else if ("机电材料".equals(type)) {
            zTable = technicsQuotaJDCLJPanel.getTable();
        } else if ("火工品".equals(type)) {
            zTable = technicsQuotaHGPJPanel.getTable();
        }
        int rowCount = zTable.getRowCount();
        String searchWzbm = "";
        Object obj = list.get(0);
        if (obj instanceof TMEEleComponentsPartLinkBean) {
            TMEEleComponentsPartLinkBean bean = (TMEEleComponentsPartLinkBean) obj;
            searchWzbm = bean.getWzbm();
        } else if (obj instanceof TMEStandPartLinkBean) {
            TMEStandPartLinkBean bean = (TMEStandPartLinkBean) obj;
            searchWzbm = bean.getWzbm();
        } else if (obj instanceof TMEMetallicPartLinkBean) {
            TMEMetallicPartLinkBean bean = (TMEMetallicPartLinkBean) obj;
            searchWzbm = bean.getWzbm();
        } else if (obj instanceof TMENonMetallicPartLinkBean) {
            TMENonMetallicPartLinkBean bean = (TMENonMetallicPartLinkBean) obj;
            searchWzbm = bean.getWzbm();
        } else if (obj instanceof TMECompoundMaterialPartLinkBean) {
            TMECompoundMaterialPartLinkBean bean = (TMECompoundMaterialPartLinkBean) obj;
            searchWzbm = bean.getWzbm();
        }
        for (int i = 0; i < rowCount; i++) {
            String wzbm = "";
            if ("元器件".equals(type)) {
                wzbm = (String) zTable.getValueAt(i, 26);
            } else if ("标准紧固件".equals(type)) {
                wzbm = (String) zTable.getValueAt(i, 19);
            } else if ("金属材料".equals(type)) {
                wzbm = (String) zTable.getValueAt(i, 22);
            } else if ("非金属材料".equals(type)) {
                wzbm = (String) zTable.getValueAt(i, 18);
            } else if ("复合材料".equals(type)) {
                wzbm = (String) zTable.getValueAt(i, 16);
            } else if ("机电材料".equals(type)) {
                wzbm = (String) zTable.getValueAt(i, 10);
            } else if ("火工品".equals(type)) {
                wzbm = (String) zTable.getValueAt(i, 9);
            }
            if (wzbm.equals(searchWzbm)) {
                return true;

            }
        }
        /*int[] selectedRows = zTable.getSelectedRows();
        if (selectedRows.length > 0) {
            String wzbm = "";
            if ("元器件".equals(type)) {
                wzbm = (String) zTable.getValueAt(selectedRows[0], 26);
            } else if ("标准紧固件".equals(type)) {
                wzbm = (String) zTable.getValueAt(selectedRows[0], 19);
            } else if ("金属材料".equals(type)) {
                wzbm = (String) zTable.getValueAt(selectedRows[0], 22);
            } else if ("非金属材料".equals(type)) {
                wzbm = (String) zTable.getValueAt(selectedRows[0], 18);
            } else if ("复合材料".equals(type)) {
                wzbm = (String) zTable.getValueAt(selectedRows[0], 16);
            } else if ("机电材料".equals(type)) {
                wzbm = (String) zTable.getValueAt(selectedRows[0], 10);
            } else if ("火工品".equals(type)) {
                wzbm = (String) zTable.getValueAt(selectedRows[0], 9);
            }
            String searchWzbm = "";
            Object obj = list.get(0);
            if (obj instanceof TMEEleComponentsPartLinkBean) {
                TMEEleComponentsPartLinkBean bean = (TMEEleComponentsPartLinkBean) obj;
                searchWzbm = bean.getWzbm();
            } else if (obj instanceof TMEStandPartLinkBean) {
                TMEStandPartLinkBean bean = (TMEStandPartLinkBean) obj;
                searchWzbm = bean.getWzbm();
            } else if (obj instanceof TMEMetallicPartLinkBean) {
                TMEMetallicPartLinkBean bean = (TMEMetallicPartLinkBean) obj;
                searchWzbm = bean.getWzbm();
            } else if (obj instanceof TMENonMetallicPartLinkBean) {
                TMENonMetallicPartLinkBean bean = (TMENonMetallicPartLinkBean) obj;
                searchWzbm = bean.getWzbm();
            } else if (obj instanceof TMECompoundMaterialPartLinkBean) {
                TMECompoundMaterialPartLinkBean bean = (TMECompoundMaterialPartLinkBean) obj;
                searchWzbm = bean.getWzbm();
            }
            if (wzbm.equals(searchWzbm)) {
                return true;

            }
        }*/
        return false;

    }
}