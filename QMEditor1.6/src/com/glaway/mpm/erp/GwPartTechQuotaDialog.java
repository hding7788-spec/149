package com.glaway.mpm.erp;

import com.glaway.mpm.util.DealFileUtil;
import com.glaway.mpm.util.IconUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.view.*;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import ext.ases.techMaterial.bean.*;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.*;

/**
 * @program: SAST-149-PDM
 * @description:
 * @author: MChen
 * @create: 2020-12-15 14:33
 */
public class GwPartTechQuotaDialog extends AbstractERPDialog {
    private ZTableOp partTableOp;
    private JPanel centerPanel;
    private JPanel bottomPanel;
    private Window owner;
    private XWTreeNode node;
    private NewTechnicsPart frame;
    private String[] partTableHeader = null;
    private String[][] partTableBody;
    private int[] partTableColWidth;
    private int[] partTableEditCols;
    private int[] partTableHideCols;
    public JTabbedPane technicsJTabbedPane = new JTabbedPane();
    public GwPartTechnicsQuotaSearchPanel wzkPanel;
    public TechnicsQuotaDZYQJJPanel technicsQuotaDZYQJJPanel;
    public TechnicsQuotaBZJGJJPanel technicsQuotaBZJGJJPanel;
    public TechnicsQuotaJSCLJPanel technicsQuotaJSCLJPanel;
    public TechnicsQuotaFJSCLJPanel technicsQuotaFJSCLJPanel;
    public TechnicsQuotaFHCLJPanel technicsQuotaFHCLJPanel;
    public TechnicsQuotaJDCLJPanel technicsQuotaJDCLJPanel;
    public TechnicsQuotaHGPJPanel technicsQuotaHGPJPanel;

    // 标准紧固件
    public static LinkedHashMap<String, String> standardMap;
    // 电子元器件
    public static LinkedHashMap<String, String> eleComponentsMap;
    // 非金属材料
    public static LinkedHashMap<String, String> nonmetallicMap;
    // 复合材料
    public static LinkedHashMap<String, String> compoundMaterialMap;
    // 金属材料
    public static LinkedHashMap<String, String> metallicMap;


    public static List<String> componentInfoList;
    public static List<String> standardInfoList;
    public static List<String> materialInfoList;
    public static List<String> nonMaterialInfoList;
    public static List<String> compoundInfoList;
    public static List<String> jdclInfoList;
    public static List<String> hgpInfoList;

    JPanel myPanel1 = new JPanel();//面板1
    JPanel myPanel2 = new JPanel();//面板2
    JSplitPane jSplitPane = new JSplitPane();//设定为左右拆分布局

    private Map<String, List<XWTreeNode>> map;
    private GWTechnicsCLDEJPanel technicsCLDEJPanel;
    private GWTechnicsDEJPanel technicsDEJPanel;
    private GWTechnicsDEForSjzykJPanel gwtechnicsdeforsjzykjpanel;

    static {
        componentInfoList = new ArrayList<String>();
        componentInfoList.add("WZMC");
        componentInfoList.add("XHGG");
        componentInfoList.add("ZLDJ");
        componentInfoList.add("SCCJ");
        componentInfoList.add("JLDW");
        componentInfoList.add("ZGF");
        componentInfoList.add("XXGF");
        componentInfoList.add("XH");
        componentInfoList.add("FZXS");
        componentInfoList.add("WXCC");
        componentInfoList.add("ZYTJ");
        componentInfoList.add("FJXY");
        componentInfoList.add("TSSM");
        componentInfoList.add("SFJK");
        componentInfoList.add("KFSZBTID");
        componentInfoList.add("KFSZBSEE");
        componentInfoList.add("XNCS");
        componentInfoList.add("SFJDMG");
        componentInfoList.add("JDMGDJ");
        componentInfoList.add("SMDJ");
        componentInfoList.add("WZJC");
        componentInfoList.add("BMYXJB");
        componentInfoList.add("BMZT");
        componentInfoList.add("BMLX");
        componentInfoList.add("BMDJ");

        standardInfoList = new ArrayList<String>();
        standardInfoList.add("WZMC");
        standardInfoList.add("GG");
        standardInfoList.add("BZH");
        standardInfoList.add("JXXNDJ");
        standardInfoList.add("JLDW");
        standardInfoList.add("CL");
        standardInfoList.add("BMCL");
        standardInfoList.add("RCL");
        standardInfoList.add("SCCJ");
        standardInfoList.add("CPXS");
        standardInfoList.add("CPDJ");
        standardInfoList.add("NBXS");
        standardInfoList.add("TSSM");
        standardInfoList.add("SFJK");
        standardInfoList.add("WZJC");
        standardInfoList.add("BMYXJB");
        standardInfoList.add("BMZT");
        standardInfoList.add("BMLX");
        standardInfoList.add("BMDJ");

        materialInfoList = new ArrayList<String>();
        materialInfoList.add("WZMC");
        materialInfoList.add("PH");
        materialInfoList.add("GG");
        materialInfoList.add("GYZT");
        materialInfoList.add("CYBZ");
        materialInfoList.add("GYDW");
        materialInfoList.add("PZGGBZ");
        materialInfoList.add("JD");
        materialInfoList.add("ZLTZ");
        materialInfoList.add("SCCJ");
        materialInfoList.add("WZJC");
        materialInfoList.add("TSSM");
        materialInfoList.add("SFJK");
        materialInfoList.add("HSL");
        materialInfoList.add("XS");
        materialInfoList.add("BMYXJB");
        materialInfoList.add("BMZT");
        materialInfoList.add("BMLX");
        materialInfoList.add("BMDJ");

        nonMaterialInfoList = new ArrayList<String>();
        nonMaterialInfoList.add("WZMC");
        nonMaterialInfoList.add("PH");
        nonMaterialInfoList.add("GG");
        nonMaterialInfoList.add("CYBZ");
        nonMaterialInfoList.add("GYDW");
        nonMaterialInfoList.add("SCCJ");
        nonMaterialInfoList.add("TSSM");
        nonMaterialInfoList.add("SFJK");
        nonMaterialInfoList.add("WZJC");
        nonMaterialInfoList.add("HSL");
        nonMaterialInfoList.add("XS");
        nonMaterialInfoList.add("BMYXJB");
        nonMaterialInfoList.add("BMZT");
        nonMaterialInfoList.add("BMLX");
        nonMaterialInfoList.add("BMDJ");

        compoundInfoList = new ArrayList<String>();
        compoundInfoList.add("WZMC");
        compoundInfoList.add("PH");
        compoundInfoList.add("GG");
        compoundInfoList.add("CYBZ");
        compoundInfoList.add("JLDW");
        compoundInfoList.add("SCCJ");
        compoundInfoList.add("TSSM");
        compoundInfoList.add("SFJK");
        compoundInfoList.add("WZJC");
        compoundInfoList.add("HSL");
        compoundInfoList.add("XS");
        compoundInfoList.add("BMYXJB");
        compoundInfoList.add("BMZT");
        compoundInfoList.add("BMLX");
        compoundInfoList.add("BMDJ");

        jdclInfoList = new ArrayList<String>();
        jdclInfoList.add("WZMC");
        jdclInfoList.add("PH");
        jdclInfoList.add("BZH");
        jdclInfoList.add("XHGG");
        jdclInfoList.add("JLDW");
        jdclInfoList.add("SCCJ");
        jdclInfoList.add("BMDJ");
        jdclInfoList.add("SFJK");
        jdclInfoList.add("XNCS");
        jdclInfoList.add("TSSM");
        jdclInfoList.add("BMZT");

        hgpInfoList = new ArrayList<String>();
        hgpInfoList.add("WZMC");
        hgpInfoList.add("BZH");
        hgpInfoList.add("JLDW");
        hgpInfoList.add("SCCJ");
        hgpInfoList.add("BMDJ");
        hgpInfoList.add("CPDH");
        hgpInfoList.add("ZL");
        hgpInfoList.add("ZCSM");
        hgpInfoList.add("TNT");
        hgpInfoList.add("XNCS");
        hgpInfoList.add("TSSM");
        hgpInfoList.add("BMZT");
    }


    private JDialog jdialog;
    //元器件字段
    private Map<String, String> yqjMap = getStringToMap("工艺物资条目oid:tmeOid,设计编码:sjbm,物资编码:chbm, 物资名称:chmc, 物资简称:wzjc, *数量:sl, *单位:dw, 编码优选级别:bmyxjb, 编码状态:bmzt, 编码类型:bmlx,编码等级:bmdj, 型号规格:xhph, 质量等级:zldj, 总规范:zgf, 详细规范:xxgf, 型号:xh, 封装形式:fzxs, 外形尺寸:wxcc, 专用条件:zytj, 附加协议:fjtj,特殊说明:tssm, 是否进口:sfjk, 抗辐射指标TID:kfszbtid, 抗辐射指标SEE:kfszbsee, 性能参数:xncs, 是否静电敏感:sfjdmg, 静电敏感等级:jdmgdj, 湿敏等级:smdj,生产厂家:sccj,位号:wh");
    //标准紧固件字段
    private Map<String, String> bzjgjMap = getStringToMap("工艺物资条目oid:tmeOid,设计编码:sjbm,物资编码:chbm, 物资名称:chmc,物资简称:wzjc,*数量:sl,*单位:dw,编码优选级别:bmyxjb,编码状态:bmzt,编码类型:bmlx,编码等级:bmdj,标准号:jstj,规格:gg,材料:cl,机械性能等级或硬度:jxxndj,表面处理:bmcl,热处理:rcl,产品形式:cpxs,产品等级:cpdj,拧板形式:nbxs,特殊说明:tssm,是否进口:sfjk,生产厂家:sccj");
    //金属材料字段
    private Map<String, String> jsclMap = getStringToMap("工艺物资条目oid:tmeOid,设计编码:sjbm,物资编码:chbm, 物资名称:chmc,物资简称:wzjc,下料尺寸:xlcc,可制件数:kzjs,数量:sl,*单位:dw,编码优选级别:bmyxjb,编码状态:bmzt,编码类型:bmlx,编码等级:bmdj,换算率:hsl,系数:xs,牌号:xhph,供应状态:gyztrcl,采用标准:jstj,精度:jddj,质量特征:zldj,品种规格标准:pzggbz,特殊说明:fjtj,是否进口:sfjk,生产厂家:sccj,规格:gg");
    //非金属材料字段
    private Map<String, String> fjsclMap = getStringToMap("工艺物资条目oid:tmeOid,设计编码:sjbm,物资编码:chbm, 物资名称:chmc,物资简称:wzjc,下料尺寸:xlcc,可制件数:kzjs,数量:sl,*单位:dw,编码优选级别:bmyxjb,编码状态:bmzt,编码类型:bmlx,编码等级:bmdj,换算率:hsl,系数:xs,牌号:xhph,规格:gg,采用标准:jstj,特殊说明:fjtj,是否进口:sfjk,生产厂家:sccj");
    //复合材料字段
    private Map<String, String> fhclMap = getStringToMap("工艺物资条目oid:tmeOid,设计编码:sjbm,物资编码:chbm, 物资名称:chmc,物资简称:wzjc,下料尺寸:xlcc,*数量:sl,*单位:dw,编码优选级别:bmyxjb,编码状态:bmzt,编码类型:bmlx,编码等级:bmdj,换算率:hsl,系数:xs,牌号:xhph,规格:gg,采用标准:jstj,特殊说明:fjtj,是否进口:sfjk,生产厂家:sccj");
    //机电材料字段
    private Map<String, String> jdMap = getStringToMap("工艺物资条目oid:tmeOid,设计编码:sjbm,物资编码:chbm, 物资名称:chmc,*数量:sl,*单位:dw,标准号:jstj,型号规格:xhgg,牌号:xhph,生产厂家:sccj,编码等级:bmdj,是否进口:sfjk,性能参数:xncs,特殊说明:tssm,编码状态:bmzt");
    //火工品字段
    private Map<String, String> hgpMap = getStringToMap("工艺物资条目oid:tmeOid,设计编码:sjbm,物资编码:chbm, 物资名称:chmc,*数量:sl,*单位:dw,标准号:jstj,生产厂家:sccj,编码等级:bmdj,产品代号:cpdh,重量:zl,贮存寿命:zcsm,TNT当量:tnt,性能参数:xncs,特殊说明:tssm,编码状态:bmzt");

    private static GwPartTechQuotaDialog instance = null;
    public synchronized static GwPartTechQuotaDialog getInstance(NewTechnicsPart frame, String title, XWTreeNode node, Map<String, List<XWTreeNode>> map) {
        if (instance == null) {
            instance =  new GwPartTechQuotaDialog(frame,title,node,map);
        }
        return instance;
    }

    public static boolean isHas(){
        if(instance==null){
            return false;
        }else{
            return true;
        }
    }

    private GwPartTechQuotaDialog(NewTechnicsPart frame, String title, XWTreeNode node, Map<String, List<XWTreeNode>> map) {
        super(frame);
        this.setTitle(title);
        this.map = map;
        this.frame = frame;
        this.node = node;
        this.jdialog = this;
        wzkPanel = new GwPartTechnicsQuotaSearchPanel(this, 22, node);
        technicsCLDEJPanel = new GWTechnicsCLDEJPanel(frame);
        technicsDEJPanel = new GWTechnicsDEJPanel(frame);
        gwtechnicsdeforsjzykjpanel = new GWTechnicsDEForSjzykJPanel(frame);
        initComponents();
        initDimension();
        initActions();
        initLayout();
        loadInitDatas();
        this.setVisible(true);
        this.setResizable(false);
        setValue();
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public void setValue() {
        if (node != null) {
            XWTreeObject xo = node.getObject();
            if ((xo instanceof XWTechnicsTreeObject)) {
                Element element = xo.getTreeCellData();
                technicsCLDEJPanel.setTableValues(element);
                technicsDEJPanel.setTableValues(element);
                gwtechnicsdeforsjzykjpanel.setTableValues(element);
            }
        }
    }

    public void initWzkPanelZTableOp(ZTableOp zTableOp) {
        zTableOp.getZTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = wzkPanel.getTableOp().getZTable().getSelectedRow();
                String value = ((KVItem) (wzkPanel.getComboBox().getSelectedItem())).getValue();
                if (row == -1) {
                    return;
                }
                if (e.getClickCount() == 1) {
                    JTable zTable =wzkPanel.getTableOp().getZTable();
                    wzkPanel.wzkDescLable.setText(desInfo(zTable,row));
                }
                if (e.getClickCount() == 2) {
                    if ("元器件".equals(value)) {
                        technicsJTabbedPane.setSelectedIndex(0);
                        int rowCount = technicsQuotaDZYQJJPanel.getTable().getRowCount();
                        DefaultTableModel model = (DefaultTableModel) technicsQuotaDZYQJJPanel.getTableModel();
                        //判断物资编码是否存在
                        String sjbm = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, 23);
                        for (int i = 0; i < rowCount; i++) {
                            String sjbmData = (String) model.getValueAt(i, 26);
                            if (!"".equals(sjbm)&&sjbm.equals(sjbmData)) {
                                int x = JOptionPane.showConfirmDialog(wzkPanel, "物资编码已存在，是否重复添加？", "确认", JOptionPane.OK_CANCEL_OPTION);
                                if(x == JOptionPane.YES_OPTION) {
                                    break;
                                }else {
                                    return;
                                }
                            }
                        }
                        int columnCount = technicsQuotaDZYQJJPanel.getTable().getColumnModel().getColumnCount();
                        Object[] values = new Object[columnCount];
                        values[0] = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, 0);
                        values[1] = false;
                        values[2] = String.valueOf("主要材料");
                        for (int i = 3; i < columnCount; i++) {
                            if (i < 7) {
                                String valueAt = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, i - 1);
                                values[i] = valueAt;
                            }else if(i==8){
                                String valueAt = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, 6);
                                values[i] = valueAt;
                            } else if (i==7 || i==9) {
                                values[i] = "";
                            } else {
                                String valueAt = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, i - 3);
                                values[i] = valueAt;
                            }
                        }
                        model.addRow(values);
                        setTabTitle(0, "电子元器件", technicsJTabbedPane, rowCount + 1);
                    } else if ("标准紧固件".equals(value)) {
                        technicsJTabbedPane.setSelectedIndex(1);
                        int rowCount = technicsQuotaBZJGJJPanel.getTable().getRowCount();
                        DefaultTableModel model = (DefaultTableModel) technicsQuotaBZJGJJPanel.getTableModel();
                        //判断物资编码是否存在
                        String sjbm = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, 17);
                        for (int i = 0; i < rowCount; i++) {
                            String sjbmData = (String) model.getValueAt(i, 19);
                            if (!"".equals(sjbm)&&sjbm.equals(sjbmData)) {
                                int x = JOptionPane.showConfirmDialog(wzkPanel, "物资编码已存在，是否重复添加？", "确认", JOptionPane.OK_CANCEL_OPTION);
                                if(x == JOptionPane.YES_OPTION) {
                                    break;
                                }else {
                                    return;
                                }
                            }
                        }
                        int columnCount = technicsQuotaBZJGJJPanel.getTable().getColumnModel().getColumnCount();
                        Object[] values = new Object[columnCount];
                        values[0] = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, 0);
                        values[1] = false;
                        values[2] = String.valueOf("主要材料");
                        for (int i = 3; i < columnCount; i++) {
                            if (i < 7) {
                                String valueAt = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, i - 1);
                                values[i] = valueAt;
                            } else if(i==8){
                                String valueAt = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, 6);
                                values[i] = valueAt;
                            } else if (6 < i && i < 9&&i!=8) {
                                values[i] = "";
                            } else {
                                String valueAt = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, i - 2);
                                values[i] = valueAt;
                            }
                        }
                        model.addRow(values);
                        setTabTitle(1, "标准紧固件", technicsJTabbedPane, rowCount + 1);
                    } else if ("金属材料".equals(value)) {
                        technicsJTabbedPane.setSelectedIndex(2);
                        int rowCount = technicsQuotaJSCLJPanel.getTable().getRowCount();
                        DefaultTableModel model = (DefaultTableModel) technicsQuotaJSCLJPanel.getTableModel();
                        //判断物资编码是否存在
                        String sjbm = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, 19);
                        for (int i = 0; i < rowCount; i++) {
                            String sjbmData = (String) model.getValueAt(i, 22);
                            if (!"".equals(sjbm)&&sjbm.equals(sjbmData)) {
                                int x = JOptionPane.showConfirmDialog(wzkPanel, "物资编码已存在，是否重复添加？", "确认", JOptionPane.OK_CANCEL_OPTION);
                                if(x == JOptionPane.YES_OPTION) {
                                    break;
                                }else {
                                    return;
                                }
                            }
                        }
                        int columnCount = technicsQuotaJSCLJPanel.getTable().getColumnModel().getColumnCount();
                        Object[] values = new Object[columnCount];
                        values[0] = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, 0);
                        values[1] = false;
                        values[2] = String.valueOf("原材料");
                        for (int i = 3; i < columnCount; i++) {
                            if (i < 8) {
                                String valueAt = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, i - 1);
                                values[i] = valueAt;
                            }else if(i==11){
                                String valueAt = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, 8);
                                values[i] = valueAt;
                            } else if (7 < i && i < 12&i!=11) {
                                values[i] = "";
                            } else {
                                String valueAt = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, i - 3);
                                values[i] = valueAt;
                            }
                        }
                        model.addRow(values);
                        setTabTitle(2, "金属材料", technicsJTabbedPane, rowCount + 1);
                    } else if ("非金属材料".equals(value)) {
                        technicsJTabbedPane.setSelectedIndex(3);
                        int rowCount = technicsQuotaFJSCLJPanel.getTable().getRowCount();
                        DefaultTableModel model = (DefaultTableModel) technicsQuotaFJSCLJPanel.getTableModel();
                        //判断物资编码是否存在
                        String sjbm = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, 15);
                        for (int i = 0; i < rowCount; i++) {
                            String sjbmData = (String) model.getValueAt(i, 18);
                            if (!"".equals(sjbm)&&sjbm.equals(sjbmData)) {
                                int x = JOptionPane.showConfirmDialog(wzkPanel, "物资编码已存在，是否重复添加？", "确认", JOptionPane.OK_CANCEL_OPTION);
                                if(x == JOptionPane.YES_OPTION) {
                                    break;
                                }else {
                                    return;
                                }
                            }
                        }
                        int columnCount = technicsQuotaFJSCLJPanel.getTable().getColumnModel().getColumnCount();
                        Object[] values = new Object[columnCount];
                        values[0] = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, 0);
                        values[1] = false;
                        values[2] = String.valueOf("原材料");
                        for (int i = 3; i < columnCount; i++) {
                            if (i < 7) {
                                String valueAt = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, i - 1);
                                values[i] = valueAt;
                            } else if(i==9){
                                String valueAt = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, 7);
                                values[i] = valueAt;
                            }else if (6 < i && i < 11&&i!=9) {
                                values[i] = "";
                            } else {
                                String valueAt = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, i - 3);
                                values[i] = valueAt;
                            }
                        }
                        model.addRow(values);
                        setTabTitle(3, "非金属材料", technicsJTabbedPane, rowCount + 1);
                    } else if ("复合材料".equals(value)) {
                        technicsJTabbedPane.setSelectedIndex(4);
                        int rowCount = technicsQuotaFHCLJPanel.getTable().getRowCount();
                        DefaultTableModel model = (DefaultTableModel) technicsQuotaFHCLJPanel.getTableModel();
                        //判断物资编码是否存在
                        String sjbm = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, 14);
                        for (int i = 0; i < rowCount; i++) {
                            String sjbmData = (String) model.getValueAt(i, 16);
                            if (!"".equals(sjbm)&&sjbm.equals(sjbmData)) {
                                int x = JOptionPane.showConfirmDialog(wzkPanel, "物资编码已存在，是否重复添加？", "确认", JOptionPane.OK_CANCEL_OPTION);
                                if(x == JOptionPane.YES_OPTION) {
                                    break;
                                }else {
                                    return;
                                }
                            }
                        }
                        int columnCount = technicsQuotaFHCLJPanel.getTable().getColumnModel().getColumnCount();
                        Object[] values = new Object[columnCount];
                        values[0] = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, 0);
                        values[1] = false;
                        values[2] = String.valueOf("主要材料");

                        for (int i = 3; i < columnCount; i++) {
                            if (i < 7) {
                                String valueAt = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, i - 1);
                                values[i] = valueAt;
                            } else if (i == 7) {
                            	 values[i] = "";
                            } else if (i == 8) {
                            	 String valueAt = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, 6);
                                 values[i] = valueAt;
                            }else if (i == 9) {
                                values[i] = "";
                            } else {
                                String valueAt = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, i - 3);
                                values[i] = valueAt;
                            }
                        }
                        model.addRow(values);
                        setTabTitle(4, "复合材料", technicsJTabbedPane, rowCount + 1);
                    } else if ("机电材料".equals(value)) {
                        technicsJTabbedPane.setSelectedIndex(5);
                        int rowCount = technicsQuotaJDCLJPanel.getTable().getRowCount();
                        DefaultTableModel model = (DefaultTableModel) technicsQuotaJDCLJPanel.getTableModel();
                        //判断物资编码是否存在
                        String sjbm = (String) wzkPanel.getTableOp().getZTable().getValueAt(row,9 );
                        for (int i = 0; i < rowCount; i++) {
                            String sjbmData = (String) model.getValueAt(i, 11);
                            if (sjbm.equals(sjbmData)) {
                                int x = JOptionPane.showConfirmDialog(wzkPanel, "物资编码已存在，是否重复添加？", "确认", JOptionPane.OK_CANCEL_OPTION);
                                if(x == JOptionPane.YES_OPTION) {
                                    break;
                                }else {
                                    return;
                                }
                            }
                        }
                        int columnCount = technicsQuotaJDCLJPanel.getTable().getColumnModel().getColumnCount();
                        Object[] values = new Object[columnCount];
                        values[0] = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, 0);
                        values[1] = false;
                        values[2] = String.valueOf("主要材料");
                        for (int i = 3; i < columnCount; i++) {
                            if(i<7) {
                                String valueAt = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, i - 1);
                                values[i] = valueAt;
                            }else if(i==8){
                                String valueAt = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, 6);
                                values[i] = valueAt;
                            }else if (i==7) {
                                values[i] = "";
                            } else {
                                String valueAt = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, i - 2);
                                values[i] = valueAt;
                            }
                        }
                        model.addRow(values);
                        setTabTitle(5, "机电材料", technicsJTabbedPane, rowCount + 1);
                        technicsJTabbedPane.setSelectedIndex(5);
                    } else if ("火工品".equals(value)) {
                        technicsJTabbedPane.setSelectedIndex(6);
                        int rowCount = technicsQuotaHGPJPanel.getTable().getRowCount();
                        DefaultTableModel model = (DefaultTableModel) technicsQuotaHGPJPanel.getTableModel();
                        //判断物资编码是否存在
                        String sjbm = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, 6);
                        for (int i = 0; i < rowCount; i++) {
                            String sjbmData = (String) model.getValueAt(i, 9);
                            if (sjbm.equals(sjbmData)) {
                                int x = JOptionPane.showConfirmDialog(wzkPanel, "物资编码已存在，是否重复添加？", "确认", JOptionPane.OK_CANCEL_OPTION);
                                if(x == JOptionPane.YES_OPTION) {
                                    break;
                                }else {
                                    return;
                                }
                            }
                        }
                        int columnCount = technicsQuotaHGPJPanel.getTable().getColumnModel().getColumnCount();
                        Object[] values = new Object[columnCount];
                        values[0] = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, 0);
                        values[1] = false;
                        values[2] = String.valueOf("主要材料");
                        for (int i = 3; i < columnCount; i++) {
                            if(i<5) {
                                String valueAt = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, i - 1);
                                values[i] = valueAt;
                            }else if(i==6){
                                String valueAt = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, 7);
                                values[i] = valueAt;
                            }else if(i==5){
                                values[i] = "";
                            }else if(i==7 || i == 8 || i == 9){
                                String valueAt = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, i-3 );
                                values[i] = valueAt;
                            }else{
                                String valueAt = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, i-2 );
                                values[i] = valueAt;
                            }
                        }
                        model.addRow(values);
                        setTabTitle(6, "火工品", technicsJTabbedPane, rowCount + 1);
                        technicsJTabbedPane.setSelectedIndex(6);
                    }


                }
            }

        });
    }


    @Override
    protected void initActions() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                instance = null;
            }
        });

    }

    protected void initComponents() {


        centerPanel = new JPanel();
        bottomPanel = new JPanel();

    }

    protected void initDimension() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screen.width;
        int height = screen.height;
        String maximized = "";

        if (maximized.equals("MAIN_MAXIMIZED")) {
            setBounds(0, 0, screen.width, screen.height);
        } else {
            int ancleft = (screen.width - width) / 2;
            int anctop = (screen.height - height) / 2;

            setBounds(ancleft, anctop, width, height);
        }
    }

    @Override
    protected void initLayout() {
        jSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);//设置分割线方向
        jSplitPane.setLeftComponent(myPanel1);//布局中添加组件 ，面板1
        jSplitPane.setRightComponent(myPanel2);//添加面板2
        jSplitPane.setDividerSize(10);//设置分割线的宽度
        jSplitPane.setDividerLocation(880);
        jSplitPane.setContinuousLayout(true);
        jSplitPane.setOneTouchExpandable(true);
        setContentPane(jSplitPane);
        centerPanel.add(wzkPanel, BorderLayout.CENTER);
        JPanel rightPanel = new JPanel();
        final JLabel lbjLable = new JLabel("零件列表");
        lbjLable.setFont(new Font("宋体", Font.PLAIN, 20));
        technicsQuotaDZYQJJPanel = new TechnicsQuotaDZYQJJPanel(frame,this);
        technicsQuotaDZYQJJPanel.jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new SupplyNumberTechQuotaDialog(frame, "电子元器件", node, "元器件", technicsJTabbedPane, technicsQuotaDZYQJJPanel,
                        technicsQuotaBZJGJJPanel, technicsQuotaJSCLJPanel, technicsQuotaFJSCLJPanel,
                        technicsQuotaFHCLJPanel, technicsQuotaJDCLJPanel, technicsQuotaHGPJPanel);
            }
        });
        technicsQuotaDZYQJJPanel.jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                DefaultTableModel model = (DefaultTableModel) technicsQuotaDZYQJJPanel.getTableModel();
                int[] selectedRows = technicsQuotaDZYQJJPanel.getTable().getSelectedRows();
                if (selectedRows.length > 0) {
                    for (int i = 0; i < selectedRows.length; i++) {
                        model.removeRow(technicsQuotaDZYQJJPanel.getTable().getSelectedRow());
                    }
                }
                //重置序号
                int rowCount = technicsQuotaDZYQJJPanel.getTable().getRowCount();
                /*  for (int i = 0; i < rowCount; i++) {
                    model.setValueAt(i + 1, i, 1);
                }*/
                setTabTitle(0, "电子元器件", technicsJTabbedPane, rowCount);
            }
        });
        technicsQuotaDZYQJJPanel.jButton4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                DefaultTableModel model = (DefaultTableModel) technicsQuotaDZYQJJPanel.getTableModel();
                int count = model.getRowCount();
                boolean isSelected = !(Boolean) model.getValueAt(0,1);
                for (int i = 0; i < count; i++) {
                    model.setValueAt(isSelected,i,1);
                }
            }
        });
        /*technicsQuotaDZYQJJPanel.jButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new editTechQuotaDialog(frame,"电子元器件",node,"元器件", technicsQuotaDZYQJJPanel,
                        technicsQuotaBZJGJJPanel, technicsQuotaJSCLJPanel, technicsQuotaFJSCLJPanel,
                        technicsQuotaFHCLJPanel, technicsQuotaJDCLJPanel, technicsQuotaHGPJPanel);
            }
        });*/
        int rowCount = technicsQuotaDZYQJJPanel.getTable().getRowCount();
        technicsJTabbedPane.add("电子元器件(" + rowCount + ")", technicsQuotaDZYQJJPanel);

        technicsQuotaBZJGJJPanel = new TechnicsQuotaBZJGJJPanel(frame,this);
        technicsQuotaBZJGJJPanel.jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new SupplyNumberTechQuotaDialog(frame, "标准紧固件", node, "标准紧固件", technicsJTabbedPane, technicsQuotaDZYQJJPanel,
                        technicsQuotaBZJGJJPanel, technicsQuotaJSCLJPanel, technicsQuotaFJSCLJPanel,
                        technicsQuotaFHCLJPanel, technicsQuotaJDCLJPanel, technicsQuotaHGPJPanel);
            }
        });
        technicsQuotaBZJGJJPanel.jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

                DefaultTableModel model = (DefaultTableModel) technicsQuotaBZJGJJPanel.getTableModel();
                int[] selectedRows = technicsQuotaBZJGJJPanel.getTable().getSelectedRows();
                if (selectedRows.length > 0) {
                    for (int i = 0; i < selectedRows.length; i++) {
                        model.removeRow(technicsQuotaBZJGJJPanel.getTable().getSelectedRow());
                    }
                }
                //重置序号
                int rowCount = technicsQuotaBZJGJJPanel.getTable().getRowCount();
               /* for (int i = 0; i < rowCount; i++) {
                    model.setValueAt(i + 1, i, 1);
                }*/
                setTabTitle(1, "标准紧固件", technicsJTabbedPane, rowCount);
            }
        });
        technicsQuotaBZJGJJPanel.jButton4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                DefaultTableModel model = (DefaultTableModel) technicsQuotaBZJGJJPanel.getTableModel();
                int count = model.getRowCount();
                boolean isSelected = !(Boolean) model.getValueAt(0,1);
                for (int i = 0; i < count; i++) {
                    model.setValueAt(isSelected,i,1);
                }
            }
        });
     /*   technicsQuotaBZJGJJPanel.jButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new editTechQuotaDialog(frame,"标准紧固件",node,"标准紧固件", technicsQuotaDZYQJJPanel,
                        technicsQuotaBZJGJJPanel, technicsQuotaJSCLJPanel, technicsQuotaFJSCLJPanel,
                        technicsQuotaFHCLJPanel, technicsQuotaJDCLJPanel, technicsQuotaHGPJPanel);
            }
        });*/
        int rowCount1 = technicsQuotaBZJGJJPanel.getTable().getRowCount();
        technicsJTabbedPane.add("标准紧固件(" + rowCount1 + ")", technicsQuotaBZJGJJPanel);

        technicsQuotaJSCLJPanel = new TechnicsQuotaJSCLJPanel(frame,this);
        technicsQuotaJSCLJPanel.jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new SupplyNumberTechQuotaDialog(frame, "金属材料", node, "金属材料", technicsJTabbedPane, technicsQuotaDZYQJJPanel,
                        technicsQuotaBZJGJJPanel, technicsQuotaJSCLJPanel, technicsQuotaFJSCLJPanel,
                        technicsQuotaFHCLJPanel, technicsQuotaJDCLJPanel, technicsQuotaHGPJPanel);
            }
        });
        technicsQuotaJSCLJPanel.jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                DefaultTableModel model = (DefaultTableModel) technicsQuotaJSCLJPanel.getTableModel();
                int[] selectedRows = technicsQuotaJSCLJPanel.getTable().getSelectedRows();
                if (selectedRows.length > 0) {
                    for (int i = 0; i < selectedRows.length; i++) {
                        model.removeRow(technicsQuotaJSCLJPanel.getTable().getSelectedRow());
                    }
                }
                //重置序号
                int rowCount = technicsQuotaJSCLJPanel.getTable().getRowCount();
               /* for (int i = 0; i < rowCount; i++) {
                    model.setValueAt(i + 1, i, 1);
                }*/
                setTabTitle(2, "金属材料", technicsJTabbedPane, rowCount);
            }
        });
        technicsQuotaJSCLJPanel.jButton4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                DefaultTableModel model = (DefaultTableModel) technicsQuotaJSCLJPanel.getTableModel();
                int count = model.getRowCount();
                boolean isSelected = !(Boolean) model.getValueAt(0,1);
                for (int i = 0; i < count; i++) {
                    model.setValueAt(isSelected,i,1);
                }
            }
        });
       /* technicsQuotaJSCLJPanel.jButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new editTechQuotaDialog(frame,"金属材料",node,"金属材料", technicsQuotaDZYQJJPanel,
                        technicsQuotaBZJGJJPanel, technicsQuotaJSCLJPanel, technicsQuotaFJSCLJPanel,
                        technicsQuotaFHCLJPanel, technicsQuotaJDCLJPanel, technicsQuotaHGPJPanel);
            }
        });*/
        int rowCount2 = technicsQuotaJSCLJPanel.getTable().getRowCount();
        technicsJTabbedPane.add("金属材料(" + rowCount2 + ")", technicsQuotaJSCLJPanel);

        technicsQuotaFJSCLJPanel = new TechnicsQuotaFJSCLJPanel(frame,this);
        technicsQuotaFJSCLJPanel.jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new SupplyNumberTechQuotaDialog(frame, "非金属材料", node, "非金属材料", technicsJTabbedPane, technicsQuotaDZYQJJPanel,
                        technicsQuotaBZJGJJPanel, technicsQuotaJSCLJPanel, technicsQuotaFJSCLJPanel,
                        technicsQuotaFHCLJPanel, technicsQuotaJDCLJPanel, technicsQuotaHGPJPanel);
            }
        });
        technicsQuotaFJSCLJPanel.jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                DefaultTableModel model = (DefaultTableModel) technicsQuotaFJSCLJPanel.getTableModel();
                int[] selectedRows = technicsQuotaFJSCLJPanel.getTable().getSelectedRows();
                if (selectedRows.length > 0) {
                    for (int i = 0; i < selectedRows.length; i++) {
                        model.removeRow(technicsQuotaFJSCLJPanel.getTable().getSelectedRow());
                    }
                }
                //重置序号
                int rowCount = technicsQuotaFJSCLJPanel.getTable().getRowCount();
               /* for (int i = 0; i < rowCount; i++) {
                    model.setValueAt(i + 1, i, 1);
                }*/
                setTabTitle(3, "非金属材料", technicsJTabbedPane, rowCount);
            }
        });
        technicsQuotaFJSCLJPanel.jButton4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                DefaultTableModel model = (DefaultTableModel) technicsQuotaFJSCLJPanel.getTableModel();
                int count = model.getRowCount();
                boolean isSelected = !(Boolean) model.getValueAt(0,1);
                for (int i = 0; i < count; i++) {
                    model.setValueAt(isSelected,i,1);
                }
            }
        });
        /*technicsQuotaFJSCLJPanel.jButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new editTechQuotaDialog(frame,"非金属材料",node,"非金属材料", technicsQuotaDZYQJJPanel,
                        technicsQuotaBZJGJJPanel, technicsQuotaJSCLJPanel, technicsQuotaFJSCLJPanel,
                        technicsQuotaFHCLJPanel, technicsQuotaJDCLJPanel, technicsQuotaHGPJPanel);
            }
        });*/
        int rowCount3 = technicsQuotaFJSCLJPanel.getTable().getRowCount();
        technicsJTabbedPane.add("非金属材料(" + rowCount3 + ")", technicsQuotaFJSCLJPanel);

        technicsQuotaFHCLJPanel = new TechnicsQuotaFHCLJPanel(frame,this);
        technicsQuotaFHCLJPanel.jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new SupplyNumberTechQuotaDialog(frame, "复合材料", node, "复合材料", technicsJTabbedPane, technicsQuotaDZYQJJPanel,
                        technicsQuotaBZJGJJPanel, technicsQuotaJSCLJPanel, technicsQuotaFJSCLJPanel,
                        technicsQuotaFHCLJPanel, technicsQuotaJDCLJPanel, technicsQuotaHGPJPanel);
            }
        });
        technicsQuotaFHCLJPanel.jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

                DefaultTableModel model = (DefaultTableModel) technicsQuotaFHCLJPanel.getTableModel();
                int[] selectedRows = technicsQuotaFHCLJPanel.getTable().getSelectedRows();
                if (selectedRows.length > 0) {
                    for (int i = 0; i < selectedRows.length; i++) {
                        model.removeRow(technicsQuotaFHCLJPanel.getTable().getSelectedRow());
                    }
                }
                //重置序号
                int rowCount = technicsQuotaFHCLJPanel.getTable().getRowCount();
               /* for (int i = 0; i < rowCount; i++) {
                    model.setValueAt(i + 1, i, 1);
                }*/
                setTabTitle(4, "复合材料", technicsJTabbedPane, rowCount);
            }
        });
        technicsQuotaFHCLJPanel.jButton4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                DefaultTableModel model = (DefaultTableModel) technicsQuotaFHCLJPanel.getTableModel();
                int count = model.getRowCount();
                boolean isSelected = !(Boolean) model.getValueAt(0,1);
                for (int i = 0; i < count; i++) {
                    model.setValueAt(isSelected,i,1);
                }
            }
        });
        /*technicsQuotaFHCLJPanel.jButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new editTechQuotaDialog(frame,"复合材料",node,"复合材料", technicsQuotaDZYQJJPanel,
                        technicsQuotaBZJGJJPanel, technicsQuotaJSCLJPanel, technicsQuotaFJSCLJPanel,
                        technicsQuotaFHCLJPanel, technicsQuotaJDCLJPanel, technicsQuotaHGPJPanel);
            }
        });*/
        int rowCount4 = technicsQuotaFHCLJPanel.getTable().getRowCount();
        technicsJTabbedPane.add("复合材料(" + rowCount4 + ")", technicsQuotaFHCLJPanel);

        technicsQuotaJDCLJPanel = new TechnicsQuotaJDCLJPanel(frame,this);
        technicsQuotaJDCLJPanel.jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new addTechQuotaDialog(frame, "机电材料", node, "机电材料", technicsJTabbedPane, technicsQuotaDZYQJJPanel,
                        technicsQuotaBZJGJJPanel, technicsQuotaJSCLJPanel, technicsQuotaFJSCLJPanel,
                        technicsQuotaFHCLJPanel, technicsQuotaJDCLJPanel, technicsQuotaHGPJPanel);
            }
        });
        technicsQuotaJDCLJPanel.jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                DefaultTableModel model = (DefaultTableModel) technicsQuotaJDCLJPanel.getTableModel();
                int[] selectedRows = technicsQuotaJDCLJPanel.getTable().getSelectedRows();
                if (selectedRows.length > 0) {
                    for (int i = 0; i < selectedRows.length; i++) {
                        model.removeRow(technicsQuotaJDCLJPanel.getTable().getSelectedRow());
                    }
                }
                //重置序号
                int rowCount = technicsQuotaJDCLJPanel.getTable().getRowCount();
               /* for (int i = 0; i < rowCount; i++) {
                    model.setValueAt(i + 1, i, 1);
                }*/
                setTabTitle(5, "机电材料", technicsJTabbedPane, rowCount);
            }
        });
        /*technicsQuotaJDCLJPanel.jButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new editTechQuotaDialog(frame, "机电材料", node, "机电材料", technicsQuotaDZYQJJPanel,
                        technicsQuotaBZJGJJPanel, technicsQuotaJSCLJPanel, technicsQuotaFJSCLJPanel,
                        technicsQuotaFHCLJPanel, technicsQuotaJDCLJPanel, technicsQuotaHGPJPanel);
            }
        });*/
        technicsQuotaJDCLJPanel.jButton4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                DefaultTableModel model = (DefaultTableModel) technicsQuotaJDCLJPanel.getTableModel();
                int count = model.getRowCount();
                boolean isSelected = !(Boolean) model.getValueAt(0,1);
                for (int i = 0; i < count; i++) {
                    model.setValueAt(isSelected,i,1);
                }
            }
        });
        int rowCount5 = technicsQuotaJDCLJPanel.getTable().getRowCount();
        technicsJTabbedPane.add("机电材料(" + rowCount5 + ")", technicsQuotaJDCLJPanel);

        technicsQuotaHGPJPanel = new TechnicsQuotaHGPJPanel(frame,this);
        technicsQuotaHGPJPanel.jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new addTechQuotaDialog(frame, "火工品", node, "火工品", technicsJTabbedPane, technicsQuotaDZYQJJPanel,
                        technicsQuotaBZJGJJPanel, technicsQuotaJSCLJPanel, technicsQuotaFJSCLJPanel,
                        technicsQuotaFHCLJPanel, technicsQuotaJDCLJPanel, technicsQuotaHGPJPanel);
            }
        });
        technicsQuotaHGPJPanel.jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {

                DefaultTableModel model = (DefaultTableModel) technicsQuotaHGPJPanel.getTableModel();
                int[] selectedRows = technicsQuotaHGPJPanel.getTable().getSelectedRows();
                if (selectedRows.length > 0) {
                    for (int i = 0; i < selectedRows.length; i++) {
                        model.removeRow(technicsQuotaHGPJPanel.getTable().getSelectedRow());
                    }
                }
                //重置序号
                int rowCount = technicsQuotaHGPJPanel.getTable().getRowCount();
               /* for (int i = 0; i < rowCount; i++) {
                    model.setValueAt(i + 1, i, 1);
                }*/
                setTabTitle(6, "火工品", technicsJTabbedPane, rowCount);
            }
        });
        /*technicsQuotaHGPJPanel.jButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new editTechQuotaDialog(frame, "火工品", node, "火工品", technicsQuotaDZYQJJPanel,
                        technicsQuotaBZJGJJPanel, technicsQuotaJSCLJPanel, technicsQuotaFJSCLJPanel,
                        technicsQuotaFHCLJPanel, technicsQuotaJDCLJPanel, technicsQuotaHGPJPanel);
            }
        });*/
        technicsQuotaHGPJPanel.jButton4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                DefaultTableModel model = (DefaultTableModel) technicsQuotaHGPJPanel.getTableModel();
                int count = model.getRowCount();
                boolean isSelected = !(Boolean) model.getValueAt(0,1);
                for (int i = 0; i < count; i++) {
                    model.setValueAt(isSelected,i,1);
                }
            }
        });
        int rowCount6 = technicsQuotaHGPJPanel.getTable().getRowCount();
        technicsJTabbedPane.add("火工品(" + rowCount6 + ")", technicsQuotaHGPJPanel);

        rightPanel.add(technicsJTabbedPane);
        JTabbedPane jTabbedPane = new JTabbedPane();
        jTabbedPane.setForeground(Color.RED);
        jTabbedPane.setName("定额信息");
        jTabbedPane.addTab("零件定额信息(条目“历史数据”列为“是”的，则必须手动重新匹配)", technicsCLDEJPanel);
        jTabbedPane.addTab("装配定额信息", technicsDEJPanel);
        jTabbedPane.addTab("装配定额信息-设计资源库", gwtechnicsdeforsjzykjpanel);
        // jTabbedPane.addTab("装配工艺定额信息", new TechnicsDEJPanel(frame));
        JScrollPane pane = new JScrollPane(jTabbedPane);
        pane.setPreferredSize(new Dimension(850, 322));
        myPanel1.add(centerPanel, BorderLayout.NORTH);
        myPanel1.add(pane, BorderLayout.SOUTH);

        JPanel rightBottomPanel = new JPanel(new GridLayout(1, 2,5,10));
        //JButton saveButtom =new GwIconButton("/images/tech_quota_save.png", "保存数据");
        JButton saveButtom = new JButton();
        saveButtom.setPreferredSize(new Dimension(100, 30));
        saveButtom.setIcon(IconUtil.getImageIcon("/images/tech_quota_save.png"));
        saveButtom.setText("保存数据");
        rightBottomPanel.add(saveButtom);
        JButton saveButtom2 = new JButton();
        saveButtom2.setPreferredSize(new Dimension(100, 30));
        saveButtom2.setIcon(IconUtil.getImageIcon("/images/tech_quota_save.png"));
        saveButtom2.setText("保存并关闭");
        rightBottomPanel.add(saveButtom2);
        myPanel2.add(rightPanel, BorderLayout.CENTER);
        myPanel2.add(rightBottomPanel, BorderLayout.SOUTH);
        technicsJTabbedPane.setForegroundAt(0, Color.RED);
        technicsJTabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int index = technicsJTabbedPane.getSelectedIndex();
                if (index != -1) {
                    for (int i = 0; i < technicsJTabbedPane.getTabCount(); i++) {
                        if (index != i) {
                            technicsJTabbedPane.setForegroundAt(i, Color.BLACK);
                        }
                    }
                    technicsJTabbedPane.setForegroundAt(index, Color.RED);
                }
            }

        });

        //保存数据
        saveButtom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String validateHistory = validateHistory();
                if (!"".equals(validateHistory)) {
                    JOptionPane.showMessageDialog(null, validateHistory);
                    return;
                }
                String msg = validateData();
                if (!"".equals(msg)) {
                    JOptionPane.showMessageDialog(null, msg);
                    return;
                }
                final VaActionProgressBar progressBar = new VaActionProgressBar(frame, "保存数据", "正在保存数据,请等待...", "保存数据中");
                Thread thread = new Thread() {
                    public void run() {
                        save();
                        progressBar.setHeaderMessage("数据加载完成！");
                        progressBar.finish();
                        progressBar.setVisible(false);
                    }
                };
                thread.start();
                progressBar.setVisible(true);


            }
        });
        //保存并关闭
        saveButtom2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String validateHistory = validateHistory();
                if (!"".equals(validateHistory)) {
                    JOptionPane.showMessageDialog(null, validateHistory);
                    return;
                }
                String msg = validateData();
                if (!"".equals(msg)) {
                    JOptionPane.showMessageDialog(null, msg);
                    return;
                }
                final VaActionProgressBar progressBar = new VaActionProgressBar(frame, "保存数据", "正在保存数据,请等待...", "保存数据中");
                Thread thread = new Thread() {
                    public void run() {
                        save();
                        progressBar.setHeaderMessage("数据加载完成！");
                        progressBar.finish();
                        progressBar.setVisible(false);
                    }
                };
                thread.start();
                progressBar.setVisible(true);
                jdialog.setVisible(false);
                instance = null;
            }
        });
    }

    public void save(){
        //保存数据
        if (node != null) {
            XWTreeObject xo = node.getObject();
            if ((xo instanceof XWTechnicsTreeObject)) {
                Element element = xo.getTreeCellData();
                String partNumber = convertToString(element.attributeValue("partNumber"));
                Element clde = XmlUtility.getTechnicsCLDEElement(element);
                Element gyde = XmlUtility.getTechnicsDEElement(element);
                deleteAllGyde(gyde);
                Element yclde = XmlUtility.getChildElements(clde, "YCLDE");
                Element zyclde = XmlUtility.getChildElements(clde, "ZYCLDE");
                Element sjyclde = XmlUtility.getChildElements(clde, "SJYCLDE");
                if (yclde == null) {
                    yclde = DocumentHelper.createElement("YCLDE");
                    clde.add(yclde);
                } else {
                    XmlUtility.deleteAllChildElements(yclde);
                }
                if (zyclde == null) {
                    zyclde = DocumentHelper.createElement("ZYCLDE");
                    clde.add(zyclde);
                } else {
                    XmlUtility.deleteAllChildElements(zyclde);
                }
                if (sjyclde == null) {
                    sjyclde = DocumentHelper.createElement("SJYCLDE");
                    clde.add(sjyclde);
                } else {
                    XmlUtility.deleteAllChildElements(sjyclde);
                }
                Set<String> set = new HashSet<String>();
                //电子元器件
                JTable dzyqjTable = technicsQuotaDZYQJJPanel.getTable();
                savaXmlInfo(clde, yclde, zyclde, sjyclde, dzyqjTable, yqjMap, "电子元器件");
                set = getTchnicsQuotaInfo(dzyqjTable, set);
                //标准紧固件
                JTable bzjgjTable = technicsQuotaBZJGJJPanel.getTable();
                savaXmlInfo(clde, yclde, zyclde, sjyclde, bzjgjTable, bzjgjMap, "标准紧固件");
                set = getTchnicsQuotaInfo(bzjgjTable, set);
                //金属材料
                JTable jsclTable = technicsQuotaJSCLJPanel.getTable();
                savaXmlInfo(clde, yclde, zyclde, sjyclde, jsclTable, jsclMap, "金属材料");
                set = getTchnicsQuotaInfo(jsclTable, set);
                //非金属材料
                JTable fjsclTable = technicsQuotaFJSCLJPanel.getTable();
                savaXmlInfo(clde, yclde, zyclde, sjyclde, fjsclTable, fjsclMap, "非金属材料");
                set = getTchnicsQuotaInfo(fjsclTable, set);
                //复合材料
                JTable fhclTable = technicsQuotaFHCLJPanel.getTable();
                savaXmlInfo(clde, yclde, zyclde, sjyclde, fhclTable, fhclMap, "复合材料");
                set = getTchnicsQuotaInfo(fhclTable, set);
                //机电材料
                JTable jdclTable = technicsQuotaJDCLJPanel.getTable();
                savaXmlInfo(clde, yclde, zyclde, sjyclde, jdclTable, jdMap, "机电材料");
                //火工品
                JTable hgpTable = technicsQuotaHGPJPanel.getTable();
                savaXmlInfo(clde, yclde, zyclde, sjyclde, hgpTable, hgpMap, "火工品");
                String techPath = WorkSpaceUtil.getTechnicsDirectory(element.attributeValue("technicsNumber"));
                String xmlFilePath = techPath + File.separator + element.attributeValue("technicsNumber") + ".xml";
                try {
                    XmlUtility.saveDocument(element.getDocument(), xmlFilePath);
                    //保存自定义工艺定额到数据库
                    String technicsNumber = convertToString(element.attributeValue("technicsNumber"));
                    if (!"".equals(technicsNumber)) {
                        ErpToWCIntf.saveTechnicaQuotaInfo(set, technicsNumber, "零件定额");
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void deleteAllGyde(Element gyde){
        Element yclde = XmlUtility.getChildElements(gyde, "NEWPART");
        Element zyclde = XmlUtility.getChildElements(gyde, "ZYCLDE");
        Element sjyclde = XmlUtility.getChildElements(gyde, "SJYCLDE");
        Element matchpart = XmlUtility.getChildElements(gyde, "MATCHPART");
        Element sjzyknewpart = XmlUtility.getChildElements(gyde, "SJZYKNEWPART");
        Element sjzykmatchpart = XmlUtility.getChildElements(gyde, "SJZYKMATCHPART");
        if (yclde == null) {
            yclde = DocumentHelper.createElement("YCLDE");
            gyde.add(yclde);
        } else {
            XmlUtility.deleteAllChildElements(yclde);
        }
        if (zyclde == null) {
            zyclde = DocumentHelper.createElement("ZYCLDE");
            gyde.add(zyclde);
        } else {
            XmlUtility.deleteAllChildElements(zyclde);
        }
        if (sjyclde == null) {
            sjyclde = DocumentHelper.createElement("SJYCLDE");
            gyde.add(sjyclde);
        } else {
            XmlUtility.deleteAllChildElements(sjyclde);
        }
        if (matchpart == null) {
            matchpart = DocumentHelper.createElement("MATCHPART");
            gyde.add(matchpart);
        } else {
            XmlUtility.deleteAllChildElements(matchpart);
        }
        if (sjzyknewpart == null) {
            sjzyknewpart = DocumentHelper.createElement("SJZYKNEWPART");
            gyde.add(sjzyknewpart);
        } else {
            XmlUtility.deleteAllChildElements(sjzyknewpart);
        }
        if (sjzykmatchpart == null) {
            sjzykmatchpart = DocumentHelper.createElement("SJZYKMATCHPART");
            gyde.add(sjzykmatchpart);
        } else {
            XmlUtility.deleteAllChildElements(sjzykmatchpart);
        }
    }

    /**
     * 获取所有自己添加的工艺定额
     *
     * @param table
     * @param set
     * @return
     */
    private Set<String> getTchnicsQuotaInfo(JTable table, Set<String> set) {
        TableColumn ssjbmColoum = table.getColumn("设计编码");
        TableColumn wzbmColoum = table.getColumn("物资编码");
        if (ssjbmColoum != null && wzbmColoum != null) {
            int modelIndex = ssjbmColoum.getModelIndex();
            int wzbmModelIndex = wzbmColoum.getModelIndex();
            int rowCount = table.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                String sjbm = convertToString(table.getValueAt(i, modelIndex));
                if ("".equals(sjbm)) {
                    String wzbm = convertToString(table.getValueAt(i, wzbmModelIndex));
                    set.add(wzbm);
                }
            }
        }
        return set;

    }

    private void savaXmlInfo(Element clde, Element yclde, Element zyclde, Element sjyclde, JTable jtable, Map<String, String> infoMap, String tabType) {
        int dzyqjTableRowCount = jtable.getRowCount();
        if (dzyqjTableRowCount > 0) {
            HashMap<String, Integer> indexColumnMap = new HashMap<String, Integer>();
            Enumeration<TableColumn> columns = jtable.getColumnModel().getColumns();
            while (columns.hasMoreElements()) {
                TableColumn tableColumn = columns.nextElement();
                int modelIndex = tableColumn.getModelIndex();
                Object headerValue = tableColumn.getHeaderValue();
                indexColumnMap.put(convertToString(headerValue), modelIndex);
            }
            for (int i = 0; i < dzyqjTableRowCount; i++) {
                String xmfl = (String) jtable.getValueAt(i, 2);
                if ("原材料".equals(xmfl)) {
                    Element ycldeElement = createTechnicsYCLDEElement();

                    for (String key : infoMap.keySet()) {
                        XmlUtility.setAttributeValue(ycldeElement, infoMap.get(key), convertToString(jtable.getValueAt(i, indexColumnMap.get(key))));
                    }
                    //设置类型
                    XmlUtility.setAttributeValue(ycldeElement, "tabType", tabType);
                    XmlUtility.setAttributeValue(ycldeElement, "dw2", convertToString(jtable.getValueAt(i, indexColumnMap.get("*单位"))));

                    yclde.add(ycldeElement);

                } else if ("主要材料".equals(xmfl)) {
                    Element zycldeElement = createTechnicsZYCLDEElement();
                    for (String key : infoMap.keySet()) {
                        XmlUtility.setAttributeValue(zycldeElement, infoMap.get(key), convertToString(jtable.getValueAt(i, indexColumnMap.get(key))));
                    }
                    XmlUtility.setAttributeValue(zycldeElement, "tabType", tabType);
                    XmlUtility.setAttributeValue(zycldeElement, "dw2", convertToString(jtable.getValueAt(i, indexColumnMap.get("*单位"))));

                    zyclde.add(zycldeElement);

                } else if ("试件原材料".equals(xmfl)) {
                    Element sjycldeElement = createTechnicsSJYCLDEElement();
                    for (String key : infoMap.keySet()) {
                        String columnName = infoMap.get(key);
                        XmlUtility.setAttributeValue(sjycldeElement, columnName, convertToString(jtable.getValueAt(i, indexColumnMap.get(key))));
                        if("xlcc".equals(columnName)){
                            XmlUtility.setAttributeValue(sjycldeElement, "sjcc", convertToString(jtable.getValueAt(i, indexColumnMap.get(key))));
                        }else if("kzjs".equals(columnName)){
                            XmlUtility.setAttributeValue(sjycldeElement, "sjkzjs", convertToString(jtable.getValueAt(i, indexColumnMap.get(key))));
                        }else if("sl".equals(columnName)){
                            XmlUtility.setAttributeValue(sjycldeElement, "sjsl", convertToString(jtable.getValueAt(i, indexColumnMap.get(key))));
                        }
                    }
                    XmlUtility.setAttributeValue(sjycldeElement, "tabType", tabType);
                    XmlUtility.setAttributeValue(sjycldeElement, "dw2", convertToString(jtable.getValueAt(i, indexColumnMap.get("*单位"))));
                    sjyclde.add(sjycldeElement);
                }

            }
        }

    }


    /**
     * 试件原材料之前的xml
     *
     * @return
     */
    private Element createTechnicsSJYCLDEElement() {
        Element element = DocumentHelper.createElement("sjycldeRecord");
        XmlUtility.setAttributeValue(element, "chbm", "");
        XmlUtility.setAttributeValue(element, "chmc", "");
        XmlUtility.setAttributeValue(element, "sjcc", "");
        XmlUtility.setAttributeValue(element, "sjkzjs", "");
        XmlUtility.setAttributeValue(element, "sjsl", "");
        XmlUtility.setAttributeValue(element, "dw", "");
        XmlUtility.setAttributeValue(element, "xhph", "");
        XmlUtility.setAttributeValue(element, "gg", "");
        XmlUtility.setAttributeValue(element, "jstj", "");
        XmlUtility.setAttributeValue(element, "sccj", "");
        XmlUtility.setAttributeValue(element, "zjldw", "");
        XmlUtility.setAttributeValue(element, "fjtj", "");
        XmlUtility.setAttributeValue(element, "gyztrcl", "");

        XmlUtility.setAttributeValue(element, "zldj", "");
        XmlUtility.setAttributeValue(element, "fzxs", "");
        XmlUtility.setAttributeValue(element, "jddj", "");
        XmlUtility.setAttributeValue(element, "lwgg", "");
        XmlUtility.setAttributeValue(element, "jxxndj", "");
        XmlUtility.setAttributeValue(element, "dcstxyq", "");
        XmlUtility.setAttributeValue(element, "comment", "");
        XmlUtility.setAttributeValue(element, "dataFrom", "erp");//数据来源
        return element;
    }

    private Element createTechnicsZYCLDEElement() {
        Element element = DocumentHelper.createElement("zycldeRecord");
        XmlUtility.setAttributeValue(element, "cindex", "");
        XmlUtility.setAttributeValue(element, "chbm", "");
        XmlUtility.setAttributeValue(element, "chmc", "");
        XmlUtility.setAttributeValue(element, "sl", "");
        XmlUtility.setAttributeValue(element, "dw", "");
        XmlUtility.setAttributeValue(element, "xhph", "");
        XmlUtility.setAttributeValue(element, "gg", "");
        XmlUtility.setAttributeValue(element, "jstj", "");
        XmlUtility.setAttributeValue(element, "sccj", "");
        XmlUtility.setAttributeValue(element, "zjldw", "");
        XmlUtility.setAttributeValue(element, "fjtj", "");
        XmlUtility.setAttributeValue(element, "gyztrcl", "");
        XmlUtility.setAttributeValue(element, "zldj", "");
        XmlUtility.setAttributeValue(element, "fzxs", "");
        XmlUtility.setAttributeValue(element, "jddj", "");
        XmlUtility.setAttributeValue(element, "lwgg", "");
        XmlUtility.setAttributeValue(element, "jxxndj", "");
        XmlUtility.setAttributeValue(element, "dcstxyq", "");
        XmlUtility.setAttributeValue(element, "comment", "");
        XmlUtility.setAttributeValue(element, "isAdd", "");
        XmlUtility.setAttributeValue(element, "dataFrom", "erp");//数据来源
        return element;
    }

    private Element createTechnicsYCLDEElement() {
        Element element = DocumentHelper.createElement("ycldeRecord");
        XmlUtility.setAttributeValue(element, "chbm", "");
        XmlUtility.setAttributeValue(element, "chmc", "");
        XmlUtility.setAttributeValue(element, "xlcc", "");
        XmlUtility.setAttributeValue(element, "kzjs", "");
        XmlUtility.setAttributeValue(element, "dw", "");
        XmlUtility.setAttributeValue(element, "xhph", "");
        XmlUtility.setAttributeValue(element, "gg", "");
        XmlUtility.setAttributeValue(element, "jstj", "");
        XmlUtility.setAttributeValue(element, "sccj", "");
        XmlUtility.setAttributeValue(element, "zjldw", "");
        XmlUtility.setAttributeValue(element, "fjtj", "");
        XmlUtility.setAttributeValue(element, "gyztrcl", "");
        XmlUtility.setAttributeValue(element, "zldj", "");
        XmlUtility.setAttributeValue(element, "fzxs", "");
        XmlUtility.setAttributeValue(element, "jddj", "");
		XmlUtility.setAttributeValue(element, "cpdh", "");//"产品代号"
        XmlUtility.setAttributeValue(element, "zl", "");//"重量"
        XmlUtility.setAttributeValue(element, "zcsm", "");//"贮存寿命"
        XmlUtility.setAttributeValue(element, "tnt", "");//"TNT"
        XmlUtility.setAttributeValue(element, "lwgg", "");
        XmlUtility.setAttributeValue(element, "jxxndj", "");
        XmlUtility.setAttributeValue(element, "dcstxyq", "");
        XmlUtility.setAttributeValue(element, "comment", "");
        XmlUtility.setAttributeValue(element, "dataFrom", "erp");//数据来源
        return element;
    }

    /**
     * String转map
     *
     * @param str
     * @return
     */
    public static Map<String, String> getStringToMap(String str) {
        //根据逗号截取字符串数组
        String[] str1 = str.split(",");
        //创建Map对象
        Map<String, String> map = new HashMap<String, String>();
        //循环加入map集合
        for (int i = 0; i < str1.length; i++) {
            //根据":"截取字符串数组
            String[] str2 = str1[i].split(":");
            map.put(str2[0].replace(" ", ""), str2[1].replace(" ", ""));
        }
        return map;
    }

    /**
     * 校验数据是否必填
     *
     * @return
     */
    public String validateData() {
        StringBuffer buffer = new StringBuffer();
        //电子元器件
        JTable dzyqjTable = technicsQuotaDZYQJJPanel.getTable();
        buffer.append(validataTable(dzyqjTable, "电子元器件"));
        //标准紧固件
        JTable bzjgjTable = technicsQuotaBZJGJJPanel.getTable();
        buffer.append(validataTable(bzjgjTable, "标准紧固件"));
        //金属材料
        JTable jsclTable = technicsQuotaJSCLJPanel.getTable();
        buffer.append(validataTable(jsclTable, "金属材料"));
        //非金属材料
        JTable fjsclTable = technicsQuotaFJSCLJPanel.getTable();
        buffer.append(validataTable(fjsclTable, "非金属材料"));
        //复合材料
        JTable fhclTable = technicsQuotaFHCLJPanel.getTable();
        buffer.append(validataTable(fhclTable, "复合材料"));
        //机电材料
        JTable jdclTable = technicsQuotaJDCLJPanel.getTable();
        buffer.append(validataTable(jdclTable, "机电材料"));
        //火工品
        JTable hgpTable = technicsQuotaHGPJPanel.getTable();
        buffer.append(validataTable(hgpTable, "火工品"));
        return buffer.toString();
    }

    /**
     * 校验table必填字段是否必填
     *
     * @param dzyqjTable
     * @param tableName
     * @return
     */
    public String validataTable(JTable jTable, String tableName) {
        StringBuffer buffer = new StringBuffer();
        //电子元器件
        int rowCount = jTable.getRowCount();
        if (rowCount > 0) {
            List<Integer> columnList = new ArrayList<Integer>();
            TableColumnModel columnModel = jTable.getColumnModel();
            int wzbmColumnIndex = columnModel.getColumnIndex("物资编码");
            int xmflColumnIndex = columnModel.getColumnIndex("*项目分类");
            if(wzbmColumnIndex > 0) {
                Set<String> numberSet = new HashSet<String>();
                boolean yclFlag = false;
                boolean sjyclFlag = false;
                for(int i = 0; i < rowCount; i++) {
                    String wzbm = convertToString(jTable.getValueAt(i, wzbmColumnIndex));
                    String xmfl = convertToString(jTable.getValueAt(i, xmflColumnIndex));
                    if("主要材料".equals(xmfl)) {
                        if(numberSet.contains(wzbm)){
                            buffer.append(tableName + "标签主要材料第" + (i + 1) + "行" + wzbm + "编码重复\n");
                        }else {
                            numberSet.add(wzbm);
                        }
                    }
                    if("原材料".equals(xmfl)){
                        if(yclFlag){
                            buffer.append(tableName + "标签只能有一个原材料\n");
                        }else {
                            yclFlag = true;
                        }
                    }
                    if("试件原材料".equals(xmfl)){
                        if(sjyclFlag){
                            buffer.append(tableName + "标签只能有一个试件原材料\n");
                        }else {
                            sjyclFlag = true;
                        }
                    }
                }
            }
            int slColumnIndex = 0;
            if ("电子元器件".equals(tableName)) {
                int wzmcColumnIndex = columnModel.getColumnIndex("物资名称");
                int xhggColumnIndex =columnModel.getColumnIndex("型号规格");
                slColumnIndex = columnModel.getColumnIndex("*数量");
                int dwColumnIndex = columnModel.getColumnIndex("*单位");
                columnList.add(xmflColumnIndex);
                columnList.add(wzmcColumnIndex);
                columnList.add(xhggColumnIndex);
                columnList.add(slColumnIndex);
                columnList.add(dwColumnIndex);
            }else if("标准紧固件".equals(tableName)){
                int wzmcColumnIndex = columnModel.getColumnIndex("物资名称");
                //int ggColumnIndex =columnModel.getColumnIndex("规格");
                //int bzhColumnIndex =columnModel.getColumnIndex("标准号");
                slColumnIndex = columnModel.getColumnIndex("*数量");
                int dwColumnIndex = columnModel.getColumnIndex("*单位");
                columnList.add(xmflColumnIndex);
                columnList.add(wzmcColumnIndex);
                //columnList.add(ggColumnIndex);
               // columnList.add(bzhColumnIndex);
                columnList.add(slColumnIndex);
                columnList.add(dwColumnIndex);
            }else if("金属材料".equals(tableName)){
                //int wzmcColumnIndex = columnModel.getColumnIndex("物资名称");
                //int phColumnIndex =columnModel.getColumnIndex("牌号");
//                int ggColumnIndex =columnModel.getColumnIndex("规格");
                //int cybzColumnIndex =columnModel.getColumnIndex("采用标准");

                int dwColumnIndex = columnModel.getColumnIndex("*单位");
                columnList.add(xmflColumnIndex);
                //columnList.add(wzmcColumnIndex);
               // columnList.add(phColumnIndex);
//                columnList.add(ggColumnIndex);
                //columnList.add(cybzColumnIndex);
                columnList.add(dwColumnIndex);
                //特殊处理
                int xlccColumnIndex = columnModel.getColumnIndex("下料尺寸");
                int kzjsColumnIndex = columnModel.getColumnIndex("可制件数");
                slColumnIndex = columnModel.getColumnIndex("数量");
                for (int i = 0; i <rowCount ; i++) {
                    String xmflValue = convertToString(jTable.getValueAt(i, xmflColumnIndex));
                    if ("原材料".equals(xmflValue) || "试件".equals(xmflValue) || "试件原材料".equals(xmflValue)) {
                        String xlcclValue = convertToString(jTable.getValueAt(i, xlccColumnIndex));
                        if ("".equals(xlcclValue)) {
                            buffer.append(tableName + "标签第" + (i + 1) + "行、" + (xlccColumnIndex) + "列为必填\n");
                        }
                        String kzjslValue = convertToString(jTable.getValueAt(i, kzjsColumnIndex));
                        if ("".equals(kzjslValue)) {
                            buffer.append(tableName + "标签第" + (i + 1) + "行、" + (kzjsColumnIndex) + "列为必填\n");
                        }
                    }
                    if ("主要材料".equals(xmflValue)) {
                        String sllValue = convertToString(jTable.getValueAt(i, slColumnIndex));
                        if ("".equals(sllValue)) {
                            buffer.append(tableName + "标签主要材料的第" + (i + 1) + "行、" + (slColumnIndex) + "列为必填\n");
                        }
                    }
                }

            } else if ("非金属材料".equals(tableName)) {
                int wzmcColumnIndex = columnModel.getColumnIndex("物资名称");
               // int phColumnIndex = columnModel.getColumnIndex("牌号");
//                int ggColumnIndex = columnModel.getColumnIndex("规格");
               // int cybzColumnIndex = columnModel.getColumnIndex("采用标准");
                int dwColumnIndex = columnModel.getColumnIndex("*单位");
                columnList.add(xmflColumnIndex);
                columnList.add(wzmcColumnIndex);
                //columnList.add(phColumnIndex);
//                columnList.add(ggColumnIndex);
               // columnList.add(cybzColumnIndex);
                columnList.add(dwColumnIndex);
                //特殊处理
                int xlccColumnIndex = columnModel.getColumnIndex("下料尺寸");
                int kzjsColumnIndex = columnModel.getColumnIndex("可制件数");
                slColumnIndex = columnModel.getColumnIndex("数量");
                for (int i = 0; i <rowCount ; i++) {
                    String xmflValue = convertToString(jTable.getValueAt(i, xmflColumnIndex));
                    if ("原材料".equals(xmflValue) || "试件".equals(xmflValue) || "试件原材料".equals(xmflValue)) {
                        String xlcclValue = convertToString(jTable.getValueAt(i, xlccColumnIndex));
                        if ("".equals(xlcclValue)) {
                            buffer.append(tableName + "标签第" + (i + 1) + "行、" + (xlccColumnIndex) + "列为必填\n");
                        }
                        String kzjslValue = convertToString(jTable.getValueAt(i, kzjsColumnIndex));
                        if ("".equals(kzjslValue)) {
                            buffer.append(tableName + "标签第" + (i + 1) + "行、" + (kzjsColumnIndex) + "列为必填\n");
                        }
                    }
                    if ("主要材料".equals(xmflValue)) {
                        String sllValue = convertToString(jTable.getValueAt(i, slColumnIndex));
                        if ("".equals(sllValue)) {
                            buffer.append(tableName + "标签主要材料第" + (i + 1) + "行、" + (slColumnIndex) + "列为必填\n");
                        }
                    }
                }

            } else if ("复合材料".equals(tableName)) {
                int wzmcColumnIndex = columnModel.getColumnIndex("物资名称");
                int dwColumnIndex = columnModel.getColumnIndex("*单位");
                slColumnIndex = columnModel.getColumnIndex("*数量");
                columnList.add(xmflColumnIndex);
                columnList.add(wzmcColumnIndex);
                columnList.add(slColumnIndex);
                columnList.add(dwColumnIndex);

            } else if ("机电材料".equals(tableName)) {
                int wzmcColumnIndex = columnModel.getColumnIndex("物资名称");
                int dwColumnIndex = columnModel.getColumnIndex("*单位");
                slColumnIndex = columnModel.getColumnIndex("*数量");
                columnList.add(xmflColumnIndex);
                columnList.add(wzmcColumnIndex);
                columnList.add(slColumnIndex);
                columnList.add(dwColumnIndex);

            } else if ("火工品".equals(tableName)) {
                int wzmcColumnIndex = columnModel.getColumnIndex("物资名称");
                int dwColumnIndex = columnModel.getColumnIndex("*单位");
                slColumnIndex = columnModel.getColumnIndex("*数量");
                columnList.add(xmflColumnIndex);
                columnList.add(wzmcColumnIndex);
                columnList.add(slColumnIndex);
                columnList.add(dwColumnIndex);
            }
            for (int i = 0; i < rowCount; i++) {
                for (int j = 0; j < columnList.size(); j++) {
                    Integer integer = columnList.get(j);
                    String xmflValue = convertToString(jTable.getValueAt(i, integer));
                    if ("".equals(xmflValue)) {
                        buffer.append(tableName + "标签第" + (i + 1) + "行、" + (integer) + "列为必填\n");
                    }
                }
                String sl = convertToString(jTable.getValueAt(i, slColumnIndex));
                if(!"".equals(sl)){
                    if(!DealFileUtil.isNumeric(sl)){
                        buffer.append(tableName + "标签第" + (i + 1) + "行数量必须为数字\n");
                    }
                }
            }

//            if ("电子元器件".equals(tableName) || "标准紧固件".equals(tableName) || "金属材料".equals(tableName) ||
//                    "非金属材料".equals(tableName) || "复合材料".equals(tableName)) {
                String[] ids = new String[rowCount];
                for (int i = 0; i < rowCount; i++) {
                    String id = convertToString(jTable.getValueAt(i, 0));
                    ids[i] = id;
                }
                try {
                    buffer.append(ErpToWCIntf.validateWZFLData(ids, tableName));
                } catch (Exception e) {
                    e.printStackTrace();
                }
//            }
        }
        return buffer.toString();

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

    protected void loadInitDatas() {
        //TODO,加载xml中数据没有值则将pbom数据带入
        if (node != null) {
            XWTreeObject xo = node.getObject();
            if ((xo instanceof XWTechnicsTreeObject)) {
                //电子元器件
                JTable dzyqjTable = technicsQuotaDZYQJJPanel.getTable();
                DefaultTableModel dzyqjModel = (DefaultTableModel) technicsQuotaDZYQJJPanel.getTableModel();

                HashMap<String, Integer> dzyqjIndexColumnMap = new LinkedHashMap<String, Integer>();
                Enumeration<TableColumn> dzyqjColumns = dzyqjTable.getColumnModel().getColumns();
                while (dzyqjColumns.hasMoreElements()) {
                    TableColumn tableColumn = dzyqjColumns.nextElement();
                    int modelIndex = tableColumn.getModelIndex();
                    Object headerValue = tableColumn.getHeaderValue();
                    if ("选择".equals(headerValue) || "*项目分类".equals(headerValue) || "工艺物资条目oid".equals(headerValue)) {
                        continue;
                    }
                    dzyqjIndexColumnMap.put(convertToString(headerValue), modelIndex);
                }
                //标准紧固件
                JTable bzjgjTable = technicsQuotaBZJGJJPanel.getTable();
                DefaultTableModel bzjgjModel = (DefaultTableModel) technicsQuotaBZJGJJPanel.getTableModel();

                HashMap<String, Integer> bzjgjIndexColumnMap = new LinkedHashMap<String, Integer>();
                Enumeration<TableColumn> bzjgjColumns = bzjgjTable.getColumnModel().getColumns();
                while (bzjgjColumns.hasMoreElements()) {
                    TableColumn tableColumn = bzjgjColumns.nextElement();
                    int modelIndex = tableColumn.getModelIndex();
                    Object headerValue = tableColumn.getHeaderValue();
                    if ("选择".equals(headerValue) || "*项目分类".equals(headerValue) || "工艺物资条目oid".equals(headerValue)) {
                        continue;
                    }
                    bzjgjIndexColumnMap.put(convertToString(headerValue), modelIndex);
                }
                //金属材料
                JTable jsclTable = technicsQuotaJSCLJPanel.getTable();
                DefaultTableModel jsclModel = (DefaultTableModel) technicsQuotaJSCLJPanel.getTableModel();

                HashMap<String, Integer> jsclIndexColumnMap = new LinkedHashMap<String, Integer>();
                Enumeration<TableColumn> jsclColumns = jsclTable.getColumnModel().getColumns();
                while (jsclColumns.hasMoreElements()) {
                    TableColumn tableColumn = jsclColumns.nextElement();
                    int modelIndex = tableColumn.getModelIndex();
                    Object headerValue = tableColumn.getHeaderValue();
                    if ("选择".equals(headerValue) || "*项目分类".equals(headerValue) || "工艺物资条目oid".equals(headerValue)) {
                        continue;
                    }
                    jsclIndexColumnMap.put(convertToString(headerValue), modelIndex);
                }
                //非金属材料
                JTable fjsclTable = technicsQuotaFJSCLJPanel.getTable();
                DefaultTableModel fjsclModel = (DefaultTableModel) technicsQuotaFJSCLJPanel.getTableModel();

                HashMap<String, Integer> fjsclIndexColumnMap = new LinkedHashMap<String, Integer>();
                Enumeration<TableColumn> fjsclColumns = fjsclTable.getColumnModel().getColumns();
                while (fjsclColumns.hasMoreElements()) {
                    TableColumn tableColumn = fjsclColumns.nextElement();
                    int modelIndex = tableColumn.getModelIndex();
                    Object headerValue = tableColumn.getHeaderValue();
                    if ("选择".equals(headerValue) || "*项目分类".equals(headerValue) || "工艺物资条目oid".equals(headerValue)) {
                        continue;
                    }
                    fjsclIndexColumnMap.put(convertToString(headerValue), modelIndex);
                }
                //复合材料
                JTable fhclTable = technicsQuotaFHCLJPanel.getTable();
                DefaultTableModel fhclModel = (DefaultTableModel) technicsQuotaFHCLJPanel.getTableModel();

                HashMap<String, Integer> fhclIndexColumnMap = new LinkedHashMap<String, Integer>();
                Enumeration<TableColumn> fhclColumns = fhclTable.getColumnModel().getColumns();
                while (fhclColumns.hasMoreElements()) {
                    TableColumn tableColumn = fhclColumns.nextElement();
                    int modelIndex = tableColumn.getModelIndex();
                    Object headerValue = tableColumn.getHeaderValue();
                    if ("选择".equals(headerValue) || "*项目分类".equals(headerValue) || "工艺物资条目oid".equals(headerValue)) {
                        continue;
                    }
                    fhclIndexColumnMap.put(convertToString(headerValue), modelIndex);
                }
                //机电材料
                JTable jdclTable = technicsQuotaJDCLJPanel.getTable();
                DefaultTableModel jdclModel = (DefaultTableModel) technicsQuotaJDCLJPanel.getTableModel();

                HashMap<String, Integer> jdclIndexColumnMap = new LinkedHashMap<String, Integer>();
                Enumeration<TableColumn> jdclColumns = jdclTable.getColumnModel().getColumns();
                while (jdclColumns.hasMoreElements()) {
                    TableColumn tableColumn = jdclColumns.nextElement();
                    int modelIndex = tableColumn.getModelIndex();
                    Object headerValue = tableColumn.getHeaderValue();
                    if ("选择".equals(headerValue) || "*项目分类".equals(headerValue) || "工艺物资条目oid".equals(headerValue)) {
                        continue;
                    }
                    jdclIndexColumnMap.put(convertToString(headerValue), modelIndex);
                }
                //火工品
                JTable hgpTable = technicsQuotaHGPJPanel.getTable();
                DefaultTableModel hgpModel = (DefaultTableModel) technicsQuotaHGPJPanel.getTableModel();

                HashMap<String, Integer> hgpIndexColumnMap = new LinkedHashMap<String, Integer>();
                Enumeration<TableColumn> hgpColumns = hgpTable.getColumnModel().getColumns();
                while (hgpColumns.hasMoreElements()) {
                    TableColumn tableColumn = hgpColumns.nextElement();
                    int modelIndex = tableColumn.getModelIndex();
                    Object headerValue = tableColumn.getHeaderValue();
                    if ("选择".equals(headerValue) || "*项目分类".equals(headerValue) || "工艺物资条目oid".equals(headerValue)) {
                        continue;
                    }
                    hgpIndexColumnMap.put(convertToString(headerValue), modelIndex);
                }
                Element element = xo.getTreeCellData();
                Element clde = XmlUtility.getTechnicsCLDEElement(element);
                Element gyde = XmlUtility.getTechnicsDEElement(element);
                List<Element> technicsYCLDE = XmlUtility.getTechnicsYCLDE(clde);
                List<Element> zyclde = XmlUtility.getTechnicsZYCLDE(clde);
                List<Element> sjyclde = XmlUtility.getTechnicsSJYCLDE(clde);
                List<Element> gydeNewPart = XmlUtility.getTechnicsGYDENewPart(gyde);
                List<Element> gydeMatchPart = XmlUtility.getTechnicsGYDEMatchPart(gyde);
                List<Element> gyZyclde = XmlUtility.getTechnicsZYCLDE(gyde);
                List<Element> gySjyclde = XmlUtility.getTechnicsSJYCLDE(gyde);
                List<Element> sjzykgydeNewPart = XmlUtility.getTechnicsSJZYKGYDENewPart(gyde);
                List<Element> sjzykgydeMatchPart = XmlUtility.getTechnicsSJZYKGYDEMatchPart(gyde);
                for (int i = 0; i < technicsYCLDE.size(); i++) {
                    Element ele = technicsYCLDE.get(i);
                    String tabType = ele.attributeValue("tabType");
                    if ("电子元器件".equals(tabType)) {
                        Object[] dzyqjString = new Object[dzyqjIndexColumnMap.size() + 3];
                        int dzyqjTableRowCount = dzyqjTable.getRowCount() + 1;
                        dzyqjString[0] = String.valueOf(convertToString(ele.attributeValue("tmeOid")));
                        dzyqjString[1] = false;
                        dzyqjString[2] = "原材料";
                        int flag = 3;
                        for (String key : dzyqjIndexColumnMap.keySet()) {
                            dzyqjString[flag] = convertToString(ele.attributeValue(yqjMap.get(key)));
                            flag++;
                        }
                        dzyqjModel.addRow(dzyqjString);
                    } else if ("标准紧固件".equals(tabType)) {
                        Object[] bzjgjString = new Object[bzjgjIndexColumnMap.size() + 3];
                        int bzjgjRowCount = bzjgjTable.getRowCount() + 1;
                        bzjgjString[0] = String.valueOf(convertToString(ele.attributeValue("tmeOid")));
                        bzjgjString[1] = false;
                        bzjgjString[2] = "原材料";
                        int flag = 3;
                        for (String key : bzjgjIndexColumnMap.keySet()) {
                            bzjgjString[flag] = convertToString(ele.attributeValue(bzjgjMap.get(key)));
                            flag++;
                        }
                        bzjgjModel.addRow(bzjgjString);
                    } else if ("金属材料".equals(tabType)) {
                        Object[] jsclString = new Object[jsclIndexColumnMap.size() + 3];
                        int jsclRowCount = jsclTable.getRowCount() + 1;
                        jsclString[0] = String.valueOf(convertToString(ele.attributeValue("tmeOid")));
                        jsclString[1] = false;
                        jsclString[2] = "原材料";
                        int flag = 3;
                        for (String key : jsclIndexColumnMap.keySet()) {
                            jsclString[flag] = convertToString(ele.attributeValue(jsclMap.get(key)));
                            flag++;
                        }
                        jsclModel.addRow(jsclString);

                    } else if ("非金属材料".equals(tabType)) {
                        Object[] fjsclString = new Object[fjsclIndexColumnMap.size() + 3];
                        int fjsclRowCount = fjsclTable.getRowCount() + 1;
                        fjsclString[0] = String.valueOf(convertToString(ele.attributeValue("tmeOid")));
                        fjsclString[1] = false;
                        fjsclString[2] = "原材料";
                        int flag = 3;
                        for (String key : fjsclIndexColumnMap.keySet()) {
                            fjsclString[flag] = convertToString(ele.attributeValue(fjsclMap.get(key)));
                            flag++;
                        }
                        fjsclModel.addRow(fjsclString);

                    } else if ("复合材料".equals(tabType)) {
                        Object[] fhclString = new Object[fhclIndexColumnMap.size() + 3];
                        int fhclRowCount = fhclTable.getRowCount() + 1;
                        fhclString[0] = String.valueOf(convertToString(ele.attributeValue("tmeOid")));
                        fhclString[1] = false;
                        fhclString[2] = "原材料";
                        int flag = 3;
                        for (String key : fhclIndexColumnMap.keySet()) {
                            fhclString[flag] = convertToString(ele.attributeValue(fhclMap.get(key)));
                            flag++;
                        }
                        fhclModel.addRow(fhclString);

                    } else if ("机电材料".equals(tabType)) {
                        Object[] jdclString = new Object[jdclIndexColumnMap.size() + 3];
                        int jdclRowCount = jdclTable.getRowCount() + 1;
                        jdclString[0] = String.valueOf(convertToString(ele.attributeValue("tmeOid")));
                        jdclString[1] = false;
                        jdclString[2] = "原材料";
                        int flag = 3;
                        for (String key : jdclIndexColumnMap.keySet()) {
                            jdclString[flag] = convertToString(ele.attributeValue(jdMap.get(key)));
                            flag++;
                        }
                        jdclModel.addRow(jdclString);

                    } else if ("火工品".equals(tabType)) {
                        Object[] hgpString = new Object[hgpIndexColumnMap.size() + 3];
                        int hgpRowCount = hgpTable.getRowCount() + 1;
                        hgpString[0] = String.valueOf(convertToString(ele.attributeValue("tmeOid")));
                        hgpString[1] = false;
                        hgpString[2] = "原材料";
                        int flag = 3;
                        for (String key : hgpIndexColumnMap.keySet()) {
                            hgpString[flag] = convertToString(ele.attributeValue(hgpMap.get(key)));
                            flag++;
                        }
                        hgpModel.addRow(hgpString);
                    }
                }
                List<Element> technicsZYCLDE = XmlUtility.getTechnicsZYCLDE(clde);
                for (int i = 0; i < technicsZYCLDE.size(); i++) {
                    Element ele = technicsZYCLDE.get(i);
                    String tabType = ele.attributeValue("tabType");
                    if ("电子元器件".equals(tabType)) {
                        Object[] dzyqjString = new Object[dzyqjIndexColumnMap.size() + 3];
                        int dzyqjTableRowCount = dzyqjTable.getRowCount() + 1;
                        dzyqjString[0] = String.valueOf(convertToString(ele.attributeValue("tmeOid")));
                        dzyqjString[1] = false;
                        dzyqjString[2] = "主要材料";
                        int flag = 3;
                        for (String key : dzyqjIndexColumnMap.keySet()) {
                            dzyqjString[flag] = convertToString(ele.attributeValue(yqjMap.get(key)));
                            flag++;
                        }
                        dzyqjModel.addRow(dzyqjString);
                    } else if ("标准紧固件".equals(tabType)) {
                        Object[] bzjgjString = new Object[bzjgjIndexColumnMap.size() + 3];
                        int bzjgjRowCount = bzjgjTable.getRowCount() + 1;
                        bzjgjString[0] = String.valueOf(convertToString(ele.attributeValue("tmeOid")));
                        bzjgjString[1] = false;
                        bzjgjString[2] = "主要材料";
                        int flag = 3;
                        for (String key : bzjgjIndexColumnMap.keySet()) {
                            bzjgjString[flag] = convertToString(ele.attributeValue(bzjgjMap.get(key)));
                            flag++;
                        }
                        bzjgjModel.addRow(bzjgjString);
                    } else if ("金属材料".equals(tabType)) {
                        Object[] jsclString = new Object[jsclIndexColumnMap.size() + 3];
                        int jsclRowCount = jsclTable.getRowCount() + 1;
                        jsclString[0] = String.valueOf(convertToString(ele.attributeValue("tmeOid")));
                        jsclString[1] = false;
                        jsclString[2] = "主要材料";
                        int flag = 3;
                        for (String key : jsclIndexColumnMap.keySet()) {
                            jsclString[flag] = convertToString(ele.attributeValue(jsclMap.get(key)));
                            flag++;
                        }
                        jsclModel.addRow(jsclString);

                    } else if ("非金属材料".equals(tabType)) {
                        Object[] fjsclString = new Object[fjsclIndexColumnMap.size() + 3];
                        int fjsclRowCount = fjsclTable.getRowCount() + 1;
                        fjsclString[0] = String.valueOf(convertToString(ele.attributeValue("tmeOid")));
                        fjsclString[1] = false;
                        fjsclString[2] = "主要材料";
                        int flag = 3;
                        for (String key : fjsclIndexColumnMap.keySet()) {
                            fjsclString[flag] = convertToString(ele.attributeValue(fjsclMap.get(key)));
                            flag++;
                        }
                        fjsclModel.addRow(fjsclString);

                    } else if ("复合材料".equals(tabType)) {
                        Object[] fhclString = new Object[fhclIndexColumnMap.size() + 3];
                        int fhclRowCount = fhclTable.getRowCount() + 1;
                        fhclString[0] = String.valueOf(convertToString(ele.attributeValue("tmeOid")));
                        fhclString[1] = false;
                        fhclString[2] = "主要材料";
                        int flag = 3;
                        for (String key : fhclIndexColumnMap.keySet()) {
                            fhclString[flag] = convertToString(ele.attributeValue(fhclMap.get(key)));
                            flag++;
                        }
                        fhclModel.addRow(fhclString);

                    } else if ("机电材料".equals(tabType)) {
                        Object[] jdclString = new Object[jdclIndexColumnMap.size() + 3];
                        int jdclRowCount = jdclTable.getRowCount() + 1;
                        jdclString[0] = String.valueOf(convertToString(ele.attributeValue("tmeOid")));
                        jdclString[1] = false;
                        jdclString[2] = "主要材料";
                        int flag = 3;
                        for (String key : jdclIndexColumnMap.keySet()) {
                            jdclString[flag] = convertToString(ele.attributeValue(jdMap.get(key)));
                            flag++;
                        }
                        jdclModel.addRow(jdclString);

                    } else if ("火工品".equals(tabType)) {
                        Object[] hgpString = new Object[hgpIndexColumnMap.size() + 3];
                        int hgpRowCount = hgpTable.getRowCount() + 1;
                        hgpString[0] = String.valueOf(convertToString(ele.attributeValue("tmeOid")));
                        hgpString[1] = false;
                        hgpString[2] = "主要材料";
                        int flag = 3;
                        for (String key : hgpIndexColumnMap.keySet()) {
                            hgpString[flag] = convertToString(ele.attributeValue(hgpMap.get(key)));
                            flag++;
                        }
                        hgpModel.addRow(hgpString);
                    }
                }
                List<Element> technicsSJYCLDE = XmlUtility.getTechnicsSJYCLDE(clde);
                for (int i = 0; i < technicsSJYCLDE.size(); i++) {
                    Element ele = technicsSJYCLDE.get(i);
                    String tabType = ele.attributeValue("tabType");
                    if ("电子元器件".equals(tabType)) {
                        Object[] dzyqjString = new Object[dzyqjIndexColumnMap.size() + 3];
                        int dzyqjTableRowCount = dzyqjTable.getRowCount() + 1;
                        dzyqjString[0] = String.valueOf(convertToString(ele.attributeValue("tmeOid")));
                        dzyqjString[1] = false;
                        dzyqjString[2] = "试件原材料";
                        int flag = 3;
                        for (String key : dzyqjIndexColumnMap.keySet()) {
                            dzyqjString[flag] = convertToString(ele.attributeValue(yqjMap.get(key)));
                            flag++;
                        }
                        dzyqjModel.addRow(dzyqjString);
                    } else if ("标准紧固件".equals(tabType)) {
                        Object[] bzjgjString = new Object[bzjgjIndexColumnMap.size() + 3];
                        int bzjgjRowCount = bzjgjTable.getRowCount() + 1;
                        bzjgjString[0] = String.valueOf(convertToString(ele.attributeValue("tmeOid")));
                        bzjgjString[1] = false;
                        bzjgjString[2] = "试件原材料";
                        int flag = 3;
                        for (String key : bzjgjIndexColumnMap.keySet()) {
                            bzjgjString[flag] = convertToString(ele.attributeValue(bzjgjMap.get(key)));
                            flag++;
                        }
                        bzjgjModel.addRow(bzjgjString);
                    } else if ("金属材料".equals(tabType)) {
                        Object[] jsclString = new Object[jsclIndexColumnMap.size() + 3];
                        int jsclRowCount = jsclTable.getRowCount() + 1;
                        jsclString[0] = String.valueOf(convertToString(ele.attributeValue("tmeOid")));
                        jsclString[1] = false;
                        jsclString[2] = "试件原材料";
                        int flag = 3;
                        for (String key : jsclIndexColumnMap.keySet()) {
                            jsclString[flag] = convertToString(ele.attributeValue(jsclMap.get(key)));
                            flag++;
                        }
                        jsclModel.addRow(jsclString);

                    } else if ("非金属材料".equals(tabType)) {
                        Object[] fjsclString = new Object[fjsclIndexColumnMap.size() + 3];
                        int fjsclRowCount = fjsclTable.getRowCount() + 1;
                        fjsclString[0] = String.valueOf(convertToString(ele.attributeValue("tmeOid")));
                        fjsclString[1] = false;
                        fjsclString[2] = "试件原材料";
                        int flag = 3;
                        for (String key : fjsclIndexColumnMap.keySet()) {
                            fjsclString[flag] = convertToString(ele.attributeValue(fjsclMap.get(key)));
                            flag++;
                        }
                        fjsclModel.addRow(fjsclString);

                    } else if ("复合材料".equals(tabType)) {
                        Object[] fhclString = new Object[fhclIndexColumnMap.size() + 3];
                        int fhclRowCount = fhclTable.getRowCount() + 1;
                        fhclString[0] = String.valueOf(convertToString(ele.attributeValue("tmeOid")));
                        fhclString[1] = false;
                        fhclString[2] = "试件原材料";
                        int flag = 3;
                        for (String key : fhclIndexColumnMap.keySet()) {
                            fhclString[flag] = convertToString(ele.attributeValue(fhclMap.get(key)));
                            flag++;
                        }
                        fhclModel.addRow(fhclString);

                    } else if ("机电材料".equals(tabType)) {
                        Object[] jdclString = new Object[jdclIndexColumnMap.size() + 3];
                        int jdclRowCount = jdclTable.getRowCount() + 1;
                        jdclString[0] = String.valueOf(convertToString(ele.attributeValue("tmeOid")));
                        jdclString[1] = false;
                        jdclString[2] = "试件原材料";
                        int flag = 3;
                        for (String key : jdclIndexColumnMap.keySet()) {
                            jdclString[flag] = convertToString(ele.attributeValue(jdMap.get(key)));
                            flag++;
                        }
                        jdclModel.addRow(jdclString);

                    } else if ("火工品".equals(tabType)) {
                        Object[] hgpString = new Object[hgpIndexColumnMap.size() + 3];
                        int hgpRowCount = hgpTable.getRowCount() + 1;
                        hgpString[0] = String.valueOf(convertToString(ele.attributeValue("tmeOid")));
                        hgpString[1] = false;
                        hgpString[2] = "试件原材料";
                        int flag = 3;
                        for (String key : hgpIndexColumnMap.keySet()) {
                            hgpString[flag] = convertToString(ele.attributeValue(hgpMap.get(key)));
                            flag++;
                        }
                        hgpModel.addRow(hgpString);
                    }
                }

                //加载历史数据的零件定额的元器件标准件原材料主要材料试件原材料
                XmlUtility.initializeNewOrMatchPart(technicsYCLDE, dzyqjModel, bzjgjModel, jsclModel, fjsclModel, fhclModel, jdclModel, hgpModel, 1, "kzjs", componentInfoList, standardInfoList, materialInfoList, nonMaterialInfoList, compoundInfoList, jdclInfoList, hgpInfoList);
                XmlUtility.initializeNewOrMatchPart(zyclde, dzyqjModel, bzjgjModel, jsclModel, fjsclModel, fhclModel, jdclModel, hgpModel, 1, "sl", componentInfoList, standardInfoList, materialInfoList, nonMaterialInfoList, compoundInfoList, jdclInfoList, hgpInfoList);
                XmlUtility.initializeNewOrMatchPart(sjyclde, dzyqjModel, bzjgjModel, jsclModel, fjsclModel, fhclModel, jdclModel, hgpModel, 1, "sjsl", componentInfoList, standardInfoList, materialInfoList, nonMaterialInfoList, compoundInfoList, jdclInfoList, hgpInfoList);
                //加载历史数据的装配定额添加匹配的元器件标准件 主要材料试件原材料
                XmlUtility.initializeNewOrMatchPart(gydeNewPart, dzyqjModel, bzjgjModel, jsclModel, fjsclModel, fhclModel, jdclModel, hgpModel, 1, "sl", componentInfoList, standardInfoList, materialInfoList, nonMaterialInfoList, compoundInfoList, jdclInfoList, hgpInfoList);
                XmlUtility.initializeNewOrMatchPart(gydeMatchPart, dzyqjModel, bzjgjModel, jsclModel, fjsclModel, fhclModel, jdclModel, hgpModel, 1, "gyCount", componentInfoList, standardInfoList, materialInfoList, nonMaterialInfoList, compoundInfoList, jdclInfoList, hgpInfoList);
                XmlUtility.initializeNewOrMatchPart(gyZyclde, dzyqjModel, bzjgjModel, jsclModel, fjsclModel, fhclModel, jdclModel, hgpModel, 1, "sl", componentInfoList, standardInfoList, materialInfoList, nonMaterialInfoList, compoundInfoList, jdclInfoList, hgpInfoList);
                XmlUtility.initializeNewOrMatchPart(gySjyclde, dzyqjModel, bzjgjModel, jsclModel, fjsclModel, fhclModel, jdclModel, hgpModel, 1, "sjsl", componentInfoList, standardInfoList, materialInfoList, nonMaterialInfoList, compoundInfoList, jdclInfoList, hgpInfoList);
                //加载添加匹配的设计资源库元器件标准件
                XmlUtility.initializeSjzykPart(sjzykgydeNewPart, dzyqjModel, bzjgjModel, 1, componentInfoList, standardInfoList);
                XmlUtility.initializeSjzykPart(sjzykgydeMatchPart, dzyqjModel, bzjgjModel, 1, componentInfoList, standardInfoList);

                //20240409 首先加载历史定额数据  如果历史定额某个分类有值 则不再加载PBOM下的相关信息

                //获取PBOM下标准件信息
                Map<String,String> useCountMap = new HashMap<String, String>();
                List<XWTreeNode> standradList = map.get("standradList");
                if (standradList.size() > 0 && bzjgjTable.getRowCount() == 0) {
                    List<String> list = new ArrayList<String>();
                    for (int i = 0; i < standradList.size(); i++) {
                        XWTreeNode xwTreeNode = standradList.get(i);
                        XWTreeObject object = xwTreeNode.getObject();
                        if (object instanceof XWPartTreeObject) {
                            XWPartTreeObject partObj = (XWPartTreeObject) object;
                            Element partEle = partObj.getTreeCellData();
                            String partNumber = partEle.attributeValue("partNumber");
                            list.add(partNumber);
                            String useCount = partEle.attributeValue("useCount");
                            if(useCount!=null && !"".equals(useCount)){
                                useCountMap.put(partNumber,useCount);
                            }
                        }
                    }
                    try {
                        DefaultTableModel model = (DefaultTableModel) bzjgjTable.getModel();
                        List<Object> resultList = ErpToWCIntf.getTechnicsMaterialInfoByPartNum(list, "标准紧固件");
                        for (int i = 0; i < resultList.size(); i++) {
                            Object obj = resultList.get(i);
                            TMEStandPartLinkBean bean = (TMEStandPartLinkBean) obj;
                            Object[] values = new Object[bzjgjTable.getColumnCount()];
                            values[0] = bean.getTechnicsmaterialentriesid();
                            values[1] = false;
                            values[2] = String.valueOf("主要材料");
                            int intFlag = 3;
                            for (int j = 0; j < standardInfoList.size(); j++) {
                                String s = standardInfoList.get(j);
                                Object getMethod = getGetMethod(bean, s);
                                String value = "";
                                if (getMethod == null) {
                                    value = "";
                                } else {
                                    value = String.valueOf(getMethod);
                                }
                                values[intFlag] = value;
                                if ("JXXNDJ".equals(s)) {
                                    String useCount = useCountMap.get(bean.getSjbm());
                                    if(useCount!=null && !"".equals(useCount)){
                                        values[intFlag + 1] = useCount;
                                    }else{
                                        values[intFlag + 1] = "";
                                    }
                                    intFlag = intFlag + 1;
                                }
                                if ("SFJK".equals(s)) {
                                    values[intFlag + 1] = bean.getSjbm();
                                    values[intFlag + 2] = bean.getWzbm();
                                    intFlag = intFlag + 2;
                                }
                                intFlag++;
                            }
                            model.addRow(values);
                        }
                        int rowCount = bzjgjTable.getRowCount();
                        setTabTitle(1, "标准紧固件", technicsJTabbedPane, rowCount);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //获取PBOM下元器件信息
                List<XWTreeNode> componentList = map.get("componentList");
                if (componentList.size() > 0 && dzyqjTable.getRowCount() == 0) {
                    List<String> list = new ArrayList<String>();
                    for (int i = 0; i < componentList.size(); i++) {
                        XWTreeNode xwTreeNode = componentList.get(i);
                        XWTreeObject object = xwTreeNode.getObject();
                        if (object instanceof XWPartTreeObject) {
                            XWPartTreeObject partObj = (XWPartTreeObject) object;
                            Element partEle = partObj.getTreeCellData();
                            String partNumber = partEle.attributeValue("partNumber");
                            list.add(partNumber);
                            String useCount = partEle.attributeValue("useCount");
                            if(useCount!=null && !"".equals(useCount)){
                                useCountMap.put(partNumber,useCount);
                            }
                        }
                    }
                    try {
                        DefaultTableModel model = (DefaultTableModel) dzyqjTable.getModel();
                        List<Object> resultList = ErpToWCIntf.getTechnicsMaterialInfoByPartNum(list, "元器件");
                        for (int i = 0; i < resultList.size(); i++) {
                            Object obj = resultList.get(i);
                            TMEEleComponentsPartLinkBean bean = (TMEEleComponentsPartLinkBean) obj;
                            Object[] values = new Object[dzyqjTable.getColumnCount()];
                            values[0] = bean.getTechnicsmaterialentriesid();
                            values[1] = false;
                            values[2] = String.valueOf("主要材料");
                            int intFlag = 3;
                            for (int j = 0; j < componentInfoList.size(); j++) {
                                String s = componentInfoList.get(j);
                                Object getMethod = getGetMethod(bean, s);
                                String value = "";
                                if (getMethod == null) {
                                    value = "";
                                } else {
                                    value = String.valueOf(getMethod);
                                }
                                values[intFlag] = value;
                                if("SCCJ".equals(s)){
                                    String useCount = useCountMap.get(bean.getSjbm());
                                    if(useCount!=null && !"".equals(useCount)){
                                        values[intFlag + 1] = useCount;
                                    }else{
                                        values[intFlag + 1] = "";
                                    }
                                    intFlag = intFlag + 1;
                                }
                                if ("JLDW".equals(s)) {
                                    values[intFlag + 1] = "";
                                    intFlag = intFlag + 1;
                                }
                                if ("SMDJ".equals(s)) {
                                    values[intFlag + 1] = bean.getSjbm();
                                    values[intFlag + 2] = bean.getWzbm();
                                    intFlag = intFlag + 2;
                                }
                                intFlag++;
                            }
                            model.addRow(values);
                        }
                        int rowCount = dzyqjTable.getRowCount();
                        setTabTitle(0, "电子元器件", technicsJTabbedPane, rowCount);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //获取PBOM下金属材料信息
                List<XWTreeNode> materialList = map.get("materialList");
                if (materialList.size() > 0 && jsclTable.getRowCount() == 0) {
                    List<String> list = new ArrayList<String>();
                    for (int i = 0; i < materialList.size(); i++) {
                        XWTreeNode xwTreeNode = materialList.get(i);
                        XWTreeObject object = xwTreeNode.getObject();
                        if (object instanceof XWPartTreeObject) {
                            XWPartTreeObject partObj = (XWPartTreeObject) object;
                            Element partEle = partObj.getTreeCellData();
                            String partNumber = partEle.attributeValue("partNumber");
                            list.add(partNumber);
                            String useCount = partEle.attributeValue("useCount");
                            if(useCount!=null && !"".equals(useCount)){
                                useCountMap.put(partNumber,useCount);
                            }
                        }
                    }
                    try {
                        DefaultTableModel model = (DefaultTableModel) jsclTable.getModel();
                        List<Object> resultList = ErpToWCIntf.getTechnicsMaterialInfoByPartNum(list, "金属材料");
                        for (int i = 0; i < resultList.size(); i++) {
                            Object obj = resultList.get(i);
                            TMEMetallicPartLinkBean bean = (TMEMetallicPartLinkBean) obj;
                            Object[] values = new Object[jsclTable.getColumnCount()];
                            values[0] = bean.getTechnicsmaterialentriesid();
                            values[1] = false;
                            values[2] = String.valueOf("主要材料");
                            int intFlag = 3;
                            for (int j = 0; j < materialInfoList.size(); j++) {
                                String s = materialInfoList.get(j);
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
                                    String useCount = useCountMap.get(bean.getSjbm());
                                    if(useCount!=null && !"".equals(useCount)){
                                        values[intFlag + 3] = useCount;
                                    }else{
                                        values[intFlag + 3] = "";
                                    }
                                    intFlag = intFlag + 3;
                                }
                                if ("XS".equals(s)) {
                                    values[intFlag + 1] = bean.getSjbm();
                                    values[intFlag + 2] = bean.getWzbm();
                                    intFlag = intFlag + 2;
                                }
                                intFlag++;
                            }
                            model.addRow(values);
                        }
                        int rowCount = jsclTable.getRowCount();
                        setTabTitle(2, "金属材料", technicsJTabbedPane, rowCount);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //获取PBOM下非金属材料信息
                List<XWTreeNode> nonMaterialList = map.get("nonMaterialList");
                if (nonMaterialList.size() > 0 && fjsclTable.getRowCount() == 0) {
                    List<String> list = new ArrayList<String>();
                    for (int i = 0; i < nonMaterialList.size(); i++) {
                        XWTreeNode xwTreeNode = nonMaterialList.get(i);
                        XWTreeObject object = xwTreeNode.getObject();
                        if (object instanceof XWPartTreeObject) {
                            XWPartTreeObject partObj = (XWPartTreeObject) object;
                            Element partEle = partObj.getTreeCellData();
                            String partNumber = partEle.attributeValue("partNumber");
                            list.add(partNumber);
                            String useCount = partEle.attributeValue("useCount");
                            if(useCount!=null && !"".equals(useCount)){
                                useCountMap.put(partNumber,useCount);
                            }
                        }
                    }
                    try {
                        DefaultTableModel model = (DefaultTableModel) fjsclTable.getModel();
                        List<Object> resultList = ErpToWCIntf.getTechnicsMaterialInfoByPartNum(list, "非金属材料");
                        for (int i = 0; i < resultList.size(); i++) {
                            Object obj = resultList.get(i);
                            TMENonMetallicPartLinkBean bean = (TMENonMetallicPartLinkBean) obj;
                            Object[] values = new Object[fjsclTable.getColumnCount()];
                            values[0] = bean.getTechnicsmaterialentriesid();
                            values[1] = false;
                            values[2] = String.valueOf("主要材料");
                            int intFlag = 3;
                            for (int j = 0; j < nonMaterialInfoList.size(); j++) {
                                String s = nonMaterialInfoList.get(j);
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
                                    String useCount = useCountMap.get(bean.getSjbm());
                                    if(useCount!=null && !"".equals(useCount)){
                                        values[intFlag + 1] = useCount;
                                    }else{
                                        values[intFlag + 1] = "";
                                    }
                                    intFlag = intFlag + 1;
                                }
                                if ("XS".equals(s)) {
                                    values[intFlag + 1] = bean.getSjbm();
                                    values[intFlag + 2] = bean.getWzbm();
                                    intFlag = intFlag + 2;
                                }
                                intFlag++;

                            }
                            model.addRow(values);
                        }
                        int rowCount = fjsclTable.getRowCount();
                        setTabTitle(3, "非金属材料", technicsJTabbedPane, rowCount);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //获取PBOM下复合材料信息
                List<XWTreeNode> compoundList = map.get("compoundList");
                if (compoundList.size() > 0 && fhclTable.getRowCount() == 0) {
                    List<String> list = new ArrayList<String>();
                    for (int i = 0; i < compoundList.size(); i++) {
                        XWTreeNode xwTreeNode = compoundList.get(i);
                        XWTreeObject object = xwTreeNode.getObject();
                        if (object instanceof XWPartTreeObject) {
                            XWPartTreeObject partObj = (XWPartTreeObject) object;
                            Element partEle = partObj.getTreeCellData();
                            String partNumber = partEle.attributeValue("partNumber");
                            list.add(partNumber);
                            String useCount = partEle.attributeValue("useCount");
                            if(useCount!=null && !"".equals(useCount)){
                                useCountMap.put(partNumber,useCount);
                            }
                        }
                    }
                    try {
                        DefaultTableModel model = (DefaultTableModel) fhclTable.getModel();
                        List<Object> resultList = ErpToWCIntf.getTechnicsMaterialInfoByPartNum(list, "复合材料");
                        for (int i = 0; i < resultList.size(); i++) {
                            Object obj = resultList.get(i);
                            TMECompoundMaterialPartLinkBean bean = (TMECompoundMaterialPartLinkBean) obj;
                            Object[] values = new Object[fhclTable.getColumnCount()];
                            values[0] = bean.getTechnicsmaterialentriesid();
                            values[1] = false;
                            values[2] = String.valueOf("主要材料");
                            int intFlag = 3;
                            for (int j = 0; j < compoundInfoList.size(); j++) {
                                String s = compoundInfoList.get(j);
                                Object getMethod = getGetMethod(bean, s);
                                String value = "";
                                if (getMethod == null) {
                                    value = "";
                                } else {
                                    value = String.valueOf(getMethod);
                                }
                                values[intFlag] = value;

                                if ("JLDW".equals(s)) {
                                    String useCount = useCountMap.get(bean.getSjbm());
                                    if(useCount!=null && !"".equals(useCount)){
                                        values[intFlag + 1] = useCount;
                                    }else{
                                        values[intFlag + 1] = "";
                                    }
                                    intFlag = intFlag + 1;
                                }
                                if ("XS".equals(s)) {
                                    values[intFlag + 1] = bean.getSjbm();
                                    values[intFlag + 2] = bean.getWzbm();
                                    intFlag = intFlag + 2;
                                }
                                intFlag++;
                            }
                            model.addRow(values);
                        }
                        int rowCount = fhclTable.getRowCount();
                        setTabTitle(4, "复合材料", technicsJTabbedPane, rowCount);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //获取PBOM下机电材料信息
                List<XWTreeNode> jdclList = map.get("jdclList");
                if (jdclList.size() > 0 && jdclTable.getRowCount() == 0) {
                    List<String> list = new ArrayList<String>();
                    for (int i = 0; i < jdclList.size(); i++) {
                        XWTreeNode xwTreeNode = jdclList.get(i);
                        XWTreeObject object = xwTreeNode.getObject();
                        if (object instanceof XWPartTreeObject) {
                            XWPartTreeObject partObj = (XWPartTreeObject) object;
                            Element partEle = partObj.getTreeCellData();
                            String partNumber = partEle.attributeValue("partNumber");
                            list.add(partNumber);
                            String useCount = partEle.attributeValue("useCount");
                            if(useCount!=null && !"".equals(useCount)){
                                useCountMap.put(partNumber,useCount);
                            }
                        }
                    }
                    try {
                        DefaultTableModel model = (DefaultTableModel) jdclTable.getModel();
                        List<Object> resultList = ErpToWCIntf.getTechnicsMaterialInfoByPartNum(list, "机电材料");
                        for (int i = 0; i < resultList.size(); i++) {
                            Object obj = resultList.get(i);
                            TMEEleMachinePartLinkBean bean = (TMEEleMachinePartLinkBean) obj;
                            Object[] values = new Object[jdclTable.getColumnCount()];
                            values[0] = bean.getTechnicsmaterialentriesid();
                            values[1] = false;
                            values[2] = String.valueOf("主要材料");
                            int intFlag = 3;
                            for (int j = 0; j < jdclInfoList.size(); j++) {
                                String s = jdclInfoList.get(j);
                                Object getMethod = getGetMethod(bean, s);
                                String value = "";
                                if (getMethod == null) {
                                    value = "";
                                } else {
                                    value = String.valueOf(getMethod);
                                }
                                values[intFlag] = value;

                                if ("XHGG".equals(s)) {
                                    String useCount = useCountMap.get(bean.getSjbm());
                                    if(useCount!=null && !"".equals(useCount)){
                                        values[intFlag + 1] = useCount;
                                    }else{
                                        values[intFlag + 1] = "";
                                    }
                                    intFlag = intFlag + 1;
                                }
                                if ("SCCJ".equals(s)) {
                                    values[intFlag + 1] = bean.getSjbm();
                                    values[intFlag + 2] = bean.getWzbm();
                                    intFlag = intFlag + 2;
                                }
                                intFlag++;
                            }
                            model.addRow(values);
                        }
                        int rowCount = jdclTable.getRowCount();
                        setTabTitle(5, "机电材料", technicsJTabbedPane, rowCount);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //获取PBOM下火工品信息
                List<XWTreeNode> hgpList = map.get("hgpList");
                if (hgpList.size() > 0 && hgpTable.getRowCount() == 0) {
                    List<String> list = new ArrayList<String>();
                    for (int i = 0; i < hgpList.size(); i++) {
                        XWTreeNode xwTreeNode = hgpList.get(i);
                        XWTreeObject object = xwTreeNode.getObject();
                        if (object instanceof XWPartTreeObject) {
                            XWPartTreeObject partObj = (XWPartTreeObject) object;
                            Element partEle = partObj.getTreeCellData();
                            String partNumber = partEle.attributeValue("partNumber");
                            list.add(partNumber);
                            String useCount = partEle.attributeValue("useCount");
                            if(useCount!=null && !"".equals(useCount)){
                                useCountMap.put(partNumber,useCount);
                            }
                        }
                    }
                    try {
                        DefaultTableModel model = (DefaultTableModel) hgpTable.getModel();
                        List<Object> resultList = ErpToWCIntf.getTechnicsMaterialInfoByPartNum(list, "火工品");
                        for (int i = 0; i < resultList.size(); i++) {
                            Object obj = resultList.get(i);
                            TMEExpDevicePartLinkBean bean = (TMEExpDevicePartLinkBean) obj;
                            Object[] values = new Object[hgpTable.getColumnCount()];
                            values[0] = bean.getTechnicsmaterialentriesid();
                            values[1] = false;
                            values[2] = String.valueOf("主要材料");
                            int intFlag = 3;
                            for (int j = 0; j < hgpInfoList.size(); j++) {
                                String s = hgpInfoList.get(j);
                                Object getMethod = getGetMethod(bean, s);
                                String value = "";
                                if (getMethod == null) {
                                    value = "";
                                } else {
                                    value = String.valueOf(getMethod);
                                }
                                values[intFlag] = value;

                                if ("BZH".equals(s)) {
                                    String useCount = useCountMap.get(bean.getSjbm());
                                    if(useCount!=null && !"".equals(useCount)){
                                        values[intFlag + 1] = useCount;
                                    }else{
                                        values[intFlag + 1] = "";
                                    }
                                    intFlag = intFlag + 1;
                                }
                                if ("SCCJ".equals(s)) {
                                    values[intFlag + 1] = bean.getSjbm();
                                    values[intFlag + 2] = bean.getWzbm();
                                    intFlag = intFlag + 2;
                                }
                                intFlag++;
                            }
                            model.addRow(values);
                        }
                        int rowCount = hgpTable.getRowCount();
                        setTabTitle(6, "火工品", technicsJTabbedPane, rowCount);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                setTabTitle(0, "电子元器件", technicsJTabbedPane, dzyqjTable.getRowCount());
                setTabTitle(1, "标准紧固件", technicsJTabbedPane, bzjgjTable.getRowCount());
                setTabTitle(2, "金属材料", technicsJTabbedPane, jsclTable.getRowCount());
                setTabTitle(3, "非金属材料", technicsJTabbedPane, fjsclTable.getRowCount());
                setTabTitle(4, "复合材料", technicsJTabbedPane, fhclTable.getRowCount());
                setTabTitle(5, "机电材料", technicsJTabbedPane, jdclTable.getRowCount());
                setTabTitle(6, "火工品", technicsJTabbedPane, hgpTable.getRowCount());
            }
        }


    }


    public static void setTabTitle(int index, String title, JTabbedPane technicsJTabbedPane, int rowCount) {

        technicsJTabbedPane.setTitleAt(index, title + "(" + rowCount + ")");
    }

    public String desInfo(JTable table, int row) {
        StringBuffer buffer = new StringBuffer();
        Enumeration<TableColumn> fhclColumns = table.getColumnModel().getColumns();
        while (fhclColumns.hasMoreElements()) {
            TableColumn tableColumn = fhclColumns.nextElement();
            int modelIndex = tableColumn.getModelIndex();
            String headerValue = convertToString(tableColumn.getHeaderValue());
            String value = convertToString(table.getValueAt(row, modelIndex));
            if(headerValue!=null && headerValue.indexOf("物资条目oid")>-1){
                int i = value.indexOf(":");
                if(i>-1){
                    value = value.substring(i+1,value.length());
                }
            }
            buffer.append(headerValue + ":" + value);
        }
        return buffer.toString();

    }

    public Object getGetMethod(Object ob, String name) {
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

    /**
     * 校验历史定额信息是否勾选
     *
     * @return
     */
    public String validateHistory() {
        StringBuffer buffer = new StringBuffer();
        JTable cldeTable = technicsCLDEJPanel.getTable();
        JTable table1 = technicsDEJPanel.getjTable1();
        JTable table2 = technicsDEJPanel.getjTable2();
        JTable table3 = technicsDEJPanel.getjTable3();
        JTable table4 = technicsDEJPanel.getjTable4();
        JTable sjtable1 = gwtechnicsdeforsjzykjpanel.getjTable1();
        JTable sjtable2 = gwtechnicsdeforsjzykjpanel.getjTable2();
        ArrayList<JTable> list = new ArrayList<JTable>();
        list.add(cldeTable);
        list.add(table1);
        list.add(table2);
        list.add(table3);
        list.add(table4);
        list.add(sjtable1);
        list.add(sjtable2);
        for (JTable table : list) {
            if(!"".equals(buffer.toString()))
                break;
            TableModel model = table.getModel();
            int rowCount = table.getRowCount();
            if (rowCount > 0) {
                for (int i = 0; i < rowCount; i++) {
                    Boolean isSelected = (Boolean) model.getValueAt(i,1);
                    if(!isSelected){
                        buffer.append("历史定额信息需全部勾选匹配！\r\n");
                        buffer.append("请确认已匹配全部历史定额！");
                        break;
                    }
                }
            }
        }
        return buffer.toString();
    }

}