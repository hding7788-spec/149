package com.glaway.mpm.view;

import com.glaway.mpm.erp.*;
import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpPartNode;
import com.glaway.mpm.util.*;
import com.glaway.mpm.visual.bean.VaEPartInstance;
import com.glaway.mpm.visual.bean.VaLightPart;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.visual.view.tree.VaTreeNode;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Method;
import java.util.List;
import java.util.*;

/**
 * @program: SAST-149-PDM
 * @description:
 * @author: cjh
 * @create: 2023-1-13
 */
public class GwAssembQuotaDialog extends AbstractERPDialog {
    private XWTreeNode node;
    private Container parentFrame;
    private NewTechnicsPart frame;
    public JTabbedPane technicsJTabbedPane = new JTabbedPane();
    public GwAssembQuotaSearchPanel wzkPanel;
    public TechnicsQuotaDZYQJJPanel gwQuotaDZYQJJPanel;
    public TechnicsQuotaBZJGJJPanel gwQuotaBZJGJJPanel;
    public TechnicsQuotaJSCLJPanel gwQuotaJSCLJPanel;
    public TechnicsQuotaFJSCLJPanel gwQuotaFJSCLJPanel;
    public TechnicsQuotaFHCLJPanel gwQuotaFHCLJPanel;
    public TechnicsQuotaJDCLJPanel gwQuotaJDCLJPanel;
    public TechnicsQuotaHGPJPanel gwQuotaHGPJPanel;

    JPanel myPanel1 = new JPanel();//面板1
    JPanel myPanel2 = new JPanel();//面板2
    JSplitPane jSplitPane = new JSplitPane();//设定为左右拆分布局

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
    private Map<String, String> fhclMap = getStringToMap("工艺物资条目oid:tmeOid,设计编码:sjbm,物资编码:chbm, 物资名称:chmc,物资简称:wzjc,*数量:sl,*单位:dw,编码优选级别:bmyxjb,编码状态:bmzt,编码类型:bmlx,编码等级:bmdj,换算率:hsl,系数:xs,牌号:xhph,规格:gg,采用标准:jstj,特殊说明:fjtj,是否进口:sfjk,生产厂家:sccj");
    //机电材料字段
    private Map<String, String> jdMap = getStringToMap("工艺物资条目oid:tmeOid,设计编码:sjbm,物资编码:chbm, 物资名称:chmc,*数量:sl,*单位:dw,标准号:jstj,型号规格:xhgg,牌号:xhph,生产厂家:sccj,编码等级:bmdj,是否进口:sfjk,性能参数:xncs,特殊说明:tssm,编码状态:bmzt");
    //火工品字段
    private Map<String, String> hgpMap = getStringToMap("工艺物资条目oid:tmeOid,设计编码:sjbm,物资编码:chbm, 物资名称:chmc,*数量:sl,*单位:dw,标准号:jstj,生产厂家:sccj,编码等级:bmdj,产品代号:cpdh,重量:zl,贮存寿命:zcsm,TNT当量:tnt,性能参数:xncs,特殊说明:tssm,编码状态:bmzt");

    private static GwAssembQuotaDialog instance = null;
    public synchronized static GwAssembQuotaDialog getInstance(JFrame parentFrame, NewTechnicsPart frame, String title, XWTreeNode node) {
        if (instance == null) {
            instance =  new GwAssembQuotaDialog(parentFrame,frame,title,node);
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

    public GwAssembQuotaDialog(JFrame parentFrame, NewTechnicsPart frame, String title, XWTreeNode node) {
        super(parentFrame);
        this.setTitle(title);
        this.parentFrame = parentFrame;
        this.frame = frame;
        this.node = node;
        this.jdialog = this;
        wzkPanel = new GwAssembQuotaSearchPanel(this, node);
        initComponents();
        initDimension();
        initActions();
        initLayout();
        loadInitDatas();
        this.setVisible(true);
        this.setResizable(false);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
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
                        int rowCount = gwQuotaDZYQJJPanel.getTable().getRowCount();
                        DefaultTableModel model = (DefaultTableModel) gwQuotaDZYQJJPanel.getTableModel();
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
                        int columnCount = gwQuotaDZYQJJPanel.getTable().getColumnModel().getColumnCount();
                        Object[] values = new Object[columnCount];
                        values[0] = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, 0);
                        values[1] = false;
                        values[2] = String.valueOf("配套件");
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
                        int rowCount = gwQuotaBZJGJJPanel.getTable().getRowCount();
                        DefaultTableModel model = (DefaultTableModel) gwQuotaBZJGJJPanel.getTableModel();
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
                        int columnCount = gwQuotaBZJGJJPanel.getTable().getColumnModel().getColumnCount();
                        Object[] values = new Object[columnCount];
                        values[0] = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, 0);
                        values[1] = false;
                        values[2] = String.valueOf("配套件");
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
                        int rowCount = gwQuotaJSCLJPanel.getTable().getRowCount();
                        DefaultTableModel model = (DefaultTableModel) gwQuotaJSCLJPanel.getTableModel();
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
                        int columnCount = gwQuotaJSCLJPanel.getTable().getColumnModel().getColumnCount();
                        Object[] values = new Object[columnCount];
                        values[0] = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, 0);
                        values[1] = false;
                        values[2] = String.valueOf("配套件");
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
                        int rowCount = gwQuotaFJSCLJPanel.getTable().getRowCount();
                        DefaultTableModel model = (DefaultTableModel) gwQuotaFJSCLJPanel.getTableModel();
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
                        int columnCount = gwQuotaFJSCLJPanel.getTable().getColumnModel().getColumnCount();
                        Object[] values = new Object[columnCount];
                        values[0] = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, 0);
                        values[1] = false;
                        values[2] = String.valueOf("配套件");
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
                        int rowCount = gwQuotaFHCLJPanel.getTable().getRowCount();
                        DefaultTableModel model = (DefaultTableModel) gwQuotaFHCLJPanel.getTableModel();
                        //判断物资编码是否存在
                        String sjbm = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, 15);
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
                        int columnCount = gwQuotaFHCLJPanel.getTable().getColumnModel().getColumnCount();
                        Object[] values = new Object[columnCount];
                        values[0] = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, 0);
                        values[1] = false;
                        values[2] = String.valueOf("配套件");
                        for (int i = 3; i < columnCount ;i++) {
                            if (i < 7) {
                                String valueAt = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, i - 1);
                                values[i] = valueAt;
                            } else if(i==7){
                                String valueAt = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, 6);
                                values[i] = valueAt;
                            }else if (i==8) {
                                values[i] = "";
                            } else {
                                String valueAt = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, i - 2);
                                values[i] = valueAt;
                            }
                        }
                        model.addRow(values);
                        setTabTitle(4, "复合材料", technicsJTabbedPane, rowCount + 1);
                    } else if ("机电材料".equals(value)) {
                        technicsJTabbedPane.setSelectedIndex(5);
                        int rowCount = gwQuotaJDCLJPanel.getTable().getRowCount();
                        DefaultTableModel model = (DefaultTableModel) gwQuotaJDCLJPanel.getTableModel();
                        //判断物资编码是否存在
                        String sjbm = (String) wzkPanel.getTableOp().getZTable().getValueAt(row,8 );
                        for (int i = 0; i < rowCount; i++) {
                            String sjbmData = (String) model.getValueAt(i, 10);
                            if (sjbm.equals(sjbmData)) {
                                int x = JOptionPane.showConfirmDialog(wzkPanel, "物资编码已存在，是否重复添加？", "确认", JOptionPane.OK_CANCEL_OPTION);
                                if(x == JOptionPane.YES_OPTION) {
                                    break;
                                }else {
                                    return;
                                }
                            }
                        }
                        int columnCount = gwQuotaJDCLJPanel.getTable().getColumnModel().getColumnCount();
                        Object[] values = new Object[columnCount];
                        values[0] = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, 0);
                        values[1] = false;
                        values[2] = String.valueOf("配套件");
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
                        int rowCount = gwQuotaHGPJPanel.getTable().getRowCount();
                        DefaultTableModel model = (DefaultTableModel) gwQuotaHGPJPanel.getTableModel();
                        //判断物资编码是否存在
                        String sjbm = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, 7);
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
                        int columnCount = gwQuotaHGPJPanel.getTable().getColumnModel().getColumnCount();
                        Object[] values = new Object[columnCount];
                        values[0] = (String) wzkPanel.getTableOp().getZTable().getValueAt(row, 0);
                        values[1] = false;
                        values[2] = String.valueOf("配套件");
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

    @Override
    protected void initComponents() {

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

        myPanel1.add(wzkPanel);

        JPanel rightPanel = new JPanel();

        //电子元器件
        gwQuotaDZYQJJPanel = new TechnicsQuotaDZYQJJPanel(frame,this);
        gwQuotaDZYQJJPanel.jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new GwSupplyQuotaDialog(frame,"电子元器件",node,"元器件",technicsJTabbedPane, gwQuotaDZYQJJPanel,
                        gwQuotaBZJGJJPanel, gwQuotaJSCLJPanel, gwQuotaFJSCLJPanel,
                        gwQuotaFHCLJPanel, gwQuotaJDCLJPanel, gwQuotaHGPJPanel);
            }
        });

        gwQuotaDZYQJJPanel.jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                DefaultTableModel model = (DefaultTableModel) gwQuotaDZYQJJPanel.getTableModel();
                int[] selectedRows = gwQuotaDZYQJJPanel.getTable().getSelectedRows();
                if(selectedRows.length>0){
                    for (int i = 0; i < selectedRows.length; i++) {
                        model.removeRow(gwQuotaDZYQJJPanel.getTable().getSelectedRow());
                    }
                }
                //重置序号
                int rowCount = gwQuotaDZYQJJPanel.getTable().getRowCount();
                setTabTitle(0, "电子元器件", technicsJTabbedPane, rowCount);
            }
        });
        gwQuotaDZYQJJPanel.jButton4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                DefaultTableModel model = (DefaultTableModel) gwQuotaDZYQJJPanel.getTableModel();
                int count = model.getRowCount();
                boolean isSelected = !(Boolean) model.getValueAt(0,1);
                for (int i = 0; i < count; i++) {
                    model.setValueAt(isSelected,i,1);
                }
            }
        });
        int rowCount = gwQuotaDZYQJJPanel.getTable().getRowCount();
        technicsJTabbedPane.add("电子元器件(" + rowCount + ")", gwQuotaDZYQJJPanel);
        TableColumn dzyqjTableColumn = gwQuotaDZYQJJPanel.getTable().getColumn("*项目分类");
        final JComboBox dzyqjXmflJComboBox = CommonUtil.getZpXmflJComboBox();
        dzyqjXmflJComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                int stateChange = e.getStateChange();
                if(stateChange==2){
                    String selectedItem = (String) dzyqjXmflJComboBox.getSelectedItem();
                    JTable jTable = gwQuotaDZYQJJPanel.getTable();
                    int rowCount = jTable.getRowCount();
                    for (int i = 0; i <rowCount ; i++) {
                        Boolean selected = (Boolean) jTable.getValueAt(i, 1);
                        if(selected){
                            jTable.setValueAt(selectedItem,i,2);
                        }
                    }
                }
            }
        });
        dzyqjTableColumn.setCellEditor(new DefaultCellEditor(dzyqjXmflJComboBox));

        //标准紧固件
        gwQuotaBZJGJJPanel = new TechnicsQuotaBZJGJJPanel(frame,this);
        gwQuotaBZJGJJPanel.jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new GwSupplyQuotaDialog(frame,"标准紧固件",node,"标准紧固件",technicsJTabbedPane, gwQuotaDZYQJJPanel,
                        gwQuotaBZJGJJPanel, gwQuotaJSCLJPanel, gwQuotaFJSCLJPanel,
                        gwQuotaFHCLJPanel, gwQuotaJDCLJPanel, gwQuotaHGPJPanel);
            }
        });
        gwQuotaBZJGJJPanel.jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                DefaultTableModel model = (DefaultTableModel) gwQuotaBZJGJJPanel.getTableModel();
                int[] selectedRows = gwQuotaBZJGJJPanel.getTable().getSelectedRows();
                if(selectedRows.length>0){
                    for (int i = 0; i < selectedRows.length; i++) {
                        model.removeRow(gwQuotaBZJGJJPanel.getTable().getSelectedRow());
                    }
                }
                //重置序号
                int rowCount = gwQuotaBZJGJJPanel.getTable().getRowCount();
                setTabTitle(1, "标准紧固件", technicsJTabbedPane, rowCount);
            }
        });
        gwQuotaBZJGJJPanel.jButton4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                DefaultTableModel model = (DefaultTableModel) gwQuotaBZJGJJPanel.getTableModel();
                int count = model.getRowCount();
                boolean isSelected = !(Boolean) model.getValueAt(0,1);
                for (int i = 0; i < count; i++) {
                    model.setValueAt(isSelected,i,1);
                }
            }
        });
        int rowCount1 = gwQuotaBZJGJJPanel.getTable().getRowCount();
        technicsJTabbedPane.add("标准紧固件(" + rowCount1 + ")", gwQuotaBZJGJJPanel);
        TableColumn bzjgjTableColumn = gwQuotaBZJGJJPanel.getTable().getColumn("*项目分类");
        final JComboBox bzjXmflJComboBox = CommonUtil.getZpXmflJComboBox();
        bzjXmflJComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                int stateChange = e.getStateChange();
                if(stateChange==2){
                    String selectedItem = (String) bzjXmflJComboBox.getSelectedItem();
                    JTable jTable = gwQuotaBZJGJJPanel.getTable();
                    int rowCount = jTable.getRowCount();
                    for (int i = 0; i <rowCount ; i++) {
                        Boolean selected = (Boolean) jTable.getValueAt(i, 1);
                        if(selected){
                            jTable.setValueAt(selectedItem,i,2);
                        }
                    }
                }
            }
        });
        bzjgjTableColumn.setCellEditor(new DefaultCellEditor(bzjXmflJComboBox));

        //金属材料
        gwQuotaJSCLJPanel = new TechnicsQuotaJSCLJPanel(frame,this);
        gwQuotaJSCLJPanel.jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new GwSupplyQuotaDialog(frame,"金属材料",node,"金属材料",technicsJTabbedPane, gwQuotaDZYQJJPanel,
                        gwQuotaBZJGJJPanel, gwQuotaJSCLJPanel, gwQuotaFJSCLJPanel,
                        gwQuotaFHCLJPanel, gwQuotaJDCLJPanel, gwQuotaHGPJPanel);
            }
        });
        gwQuotaJSCLJPanel.jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                DefaultTableModel model = (DefaultTableModel) gwQuotaJSCLJPanel.getTableModel();
                int[] selectedRows = gwQuotaJSCLJPanel.getTable().getSelectedRows();
                if(selectedRows.length>0){
                    for (int i = 0; i < selectedRows.length; i++) {
                        model.removeRow(gwQuotaJSCLJPanel.getTable().getSelectedRow());
                    }
                }
                //重置序号
                int rowCount = gwQuotaJSCLJPanel.getTable().getRowCount();
                setTabTitle(2, "金属材料", technicsJTabbedPane, rowCount);
            }
        });
        gwQuotaJSCLJPanel.jButton4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                DefaultTableModel model = (DefaultTableModel) gwQuotaJSCLJPanel.getTableModel();
                int count = model.getRowCount();
                boolean isSelected = !(Boolean) model.getValueAt(0,1);
                for (int i = 0; i < count; i++) {
                    model.setValueAt(isSelected,i,1);
                }
            }
        });
        int rowCount2 = gwQuotaJSCLJPanel.getTable().getRowCount();
        technicsJTabbedPane.add("金属材料(" + rowCount2 + ")", gwQuotaJSCLJPanel);
        TableColumn jsclTableColumn = gwQuotaJSCLJPanel.getTable().getColumn("*项目分类");
        final JComboBox jsclXmflJComboBox = CommonUtil.getZpXmflJComboBox();
        jsclXmflJComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                int stateChange = e.getStateChange();
                if(stateChange==2){
                    String selectedItem = (String) jsclXmflJComboBox.getSelectedItem();
                    JTable jTable = gwQuotaJSCLJPanel.getTable();
                    int rowCount = jTable.getRowCount();
                    for (int i = 0; i <rowCount ; i++) {
                        Boolean selected = (Boolean) jTable.getValueAt(i, 1);
                        if(selected){
                            jTable.setValueAt(selectedItem,i,2);
                        }
                    }
                }
            }
        });
        jsclTableColumn.setCellEditor(new DefaultCellEditor(jsclXmflJComboBox));

        //非金属材料
        gwQuotaFJSCLJPanel = new TechnicsQuotaFJSCLJPanel(frame,this);
        gwQuotaFJSCLJPanel.jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new GwSupplyQuotaDialog(frame,"非金属材料",node,"非金属材料",technicsJTabbedPane, gwQuotaDZYQJJPanel,
                        gwQuotaBZJGJJPanel, gwQuotaJSCLJPanel, gwQuotaFJSCLJPanel,
                        gwQuotaFHCLJPanel, gwQuotaJDCLJPanel, gwQuotaHGPJPanel);
            }
        });
        gwQuotaFJSCLJPanel.jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                DefaultTableModel model = (DefaultTableModel) gwQuotaFJSCLJPanel.getTableModel();
                int[] selectedRows = gwQuotaFJSCLJPanel.getTable().getSelectedRows();
                if(selectedRows.length>0){
                    for (int i = 0; i < selectedRows.length; i++) {
                        model.removeRow(gwQuotaFJSCLJPanel.getTable().getSelectedRow());
                    }
                }
                //重置序号
                int rowCount = gwQuotaFJSCLJPanel.getTable().getRowCount();
                setTabTitle(3, "非金属材料", technicsJTabbedPane, rowCount);
            }
        });
        gwQuotaFJSCLJPanel.jButton4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                DefaultTableModel model = (DefaultTableModel) gwQuotaFJSCLJPanel.getTableModel();
                int count = model.getRowCount();
                boolean isSelected = !(Boolean) model.getValueAt(0,1);
                for (int i = 0; i < count; i++) {
                    model.setValueAt(isSelected,i,1);
                }
            }
        });
        int rowCount3 = gwQuotaFJSCLJPanel.getTable().getRowCount();
        technicsJTabbedPane.add("非金属材料(" + rowCount3 + ")", gwQuotaFJSCLJPanel);
        TableColumn fjsclTableColumn = gwQuotaFJSCLJPanel.getTable().getColumn("*项目分类");
        final JComboBox xmflJComboBox = CommonUtil.getZpXmflJComboBox();
        xmflJComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                int stateChange = e.getStateChange();
                if(stateChange==2){
                    String selectedItem = (String) xmflJComboBox.getSelectedItem();
                    JTable jTable = gwQuotaFJSCLJPanel.getTable();
                    int rowCount = jTable.getRowCount();
                    for (int i = 0; i <rowCount ; i++) {
                        Boolean selected = (Boolean) jTable.getValueAt(i, 1);
                        if(selected){
                            jTable.setValueAt(selectedItem,i,2);
                        }
                    }
                }
            }
        });
        fjsclTableColumn.setCellEditor(new DefaultCellEditor(xmflJComboBox));

        //复合材料
        gwQuotaFHCLJPanel = new TechnicsQuotaFHCLJPanel(frame,this);
        gwQuotaFHCLJPanel.jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new GwSupplyQuotaDialog(frame,"复合材料",node,"复合材料",technicsJTabbedPane, gwQuotaDZYQJJPanel,
                        gwQuotaBZJGJJPanel, gwQuotaJSCLJPanel, gwQuotaFJSCLJPanel,
                        gwQuotaFHCLJPanel, gwQuotaJDCLJPanel, gwQuotaHGPJPanel);
            }
        });
        gwQuotaFHCLJPanel.jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                DefaultTableModel model = (DefaultTableModel) gwQuotaFHCLJPanel.getTableModel();
                int[] selectedRows = gwQuotaFHCLJPanel.getTable().getSelectedRows();
                if(selectedRows.length>0){
                    for (int i = 0; i < selectedRows.length; i++) {
                        model.removeRow(gwQuotaFHCLJPanel.getTable().getSelectedRow());
                    }
                }
                //重置序号
                int rowCount = gwQuotaFHCLJPanel.getTable().getRowCount();
                setTabTitle(4, "复合材料", technicsJTabbedPane, rowCount);
            }
        });
        gwQuotaFHCLJPanel.jButton4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                DefaultTableModel model = (DefaultTableModel) gwQuotaFHCLJPanel.getTableModel();
                int count = model.getRowCount();
                boolean isSelected = !(Boolean) model.getValueAt(0,1);
                for (int i = 0; i < count; i++) {
                    model.setValueAt(isSelected,i,1);
                }
            }
        });
        int rowCount4 = gwQuotaFHCLJPanel.getTable().getRowCount();
        technicsJTabbedPane.add("复合材料(" + rowCount4 + ")", gwQuotaFHCLJPanel);
        TableColumn fhclTableColumn = gwQuotaFHCLJPanel.getTable().getColumn("*项目分类");
        final JComboBox fhclXmflJComboBox = CommonUtil.getZpXmflJComboBox();
        fhclXmflJComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                int stateChange = e.getStateChange();
                if(stateChange==2){
                    String selectedItem = (String) fhclXmflJComboBox.getSelectedItem();
                    JTable jTable = gwQuotaFHCLJPanel.getTable();
                    int rowCount = jTable.getRowCount();
                    for (int i = 0; i <rowCount ; i++) {
                        Boolean selected = (Boolean) jTable.getValueAt(i, 1);
                        if(selected){
                            jTable.setValueAt(selectedItem,i,2);
                        }
                    }
                }
            }
        });
        fhclTableColumn.setCellEditor(new DefaultCellEditor(fhclXmflJComboBox));

        //机电材料
        gwQuotaJDCLJPanel = new TechnicsQuotaJDCLJPanel(frame,this);
        gwQuotaJDCLJPanel.jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new GwSupplyQuotaDialog(frame,"机电材料",node,"机电材料",technicsJTabbedPane, gwQuotaDZYQJJPanel,
                        gwQuotaBZJGJJPanel, gwQuotaJSCLJPanel, gwQuotaFJSCLJPanel,
                        gwQuotaFHCLJPanel, gwQuotaJDCLJPanel, gwQuotaHGPJPanel);
            }
        });
        gwQuotaJDCLJPanel.jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                DefaultTableModel model = (DefaultTableModel) gwQuotaJDCLJPanel.getTableModel();
                int[] selectedRows = gwQuotaJDCLJPanel.getTable().getSelectedRows();
                if(selectedRows.length>0){
                    for (int i = 0; i < selectedRows.length; i++) {
                        model.removeRow(gwQuotaJDCLJPanel.getTable().getSelectedRow());
                    }
                }
                //重置序号
                int rowCount = gwQuotaJDCLJPanel.getTable().getRowCount();
                setTabTitle(5, "机电材料", technicsJTabbedPane, rowCount);
            }
        });
        gwQuotaJDCLJPanel.jButton4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                DefaultTableModel model = (DefaultTableModel) gwQuotaJDCLJPanel.getTableModel();
                int count = model.getRowCount();
                boolean isSelected = !(Boolean) model.getValueAt(0,1);
                for (int i = 0; i < count; i++) {
                    model.setValueAt(isSelected,i,1);
                }
            }
        });
        int rowCount5 = gwQuotaJDCLJPanel.getTable().getRowCount();
        technicsJTabbedPane.add("机电材料(" + rowCount5 + ")", gwQuotaJDCLJPanel);
        TableColumn jdclTableColumn = gwQuotaJDCLJPanel.getTable().getColumn("*项目分类");
        final JComboBox jdclXmflJComboBox = CommonUtil.getZpXmflJComboBox();
        jdclXmflJComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                int stateChange = e.getStateChange();
                if(stateChange==2){
                    String selectedItem = (String) jdclXmflJComboBox.getSelectedItem();
                    JTable jTable = gwQuotaJDCLJPanel.getTable();
                    int rowCount = jTable.getRowCount();
                    for (int i = 0; i <rowCount ; i++) {
                        Boolean selected = (Boolean) jTable.getValueAt(i, 1);
                        if(selected){
                            jTable.setValueAt(selectedItem,i,2);
                        }
                    }
                }
            }
        });
        jdclTableColumn.setCellEditor(new DefaultCellEditor(jdclXmflJComboBox));

        //火工品
        gwQuotaHGPJPanel = new TechnicsQuotaHGPJPanel(frame,this);
        gwQuotaHGPJPanel.jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new GwSupplyQuotaDialog(frame,"火工品",node,"火工品", technicsJTabbedPane, gwQuotaDZYQJJPanel,
                        gwQuotaBZJGJJPanel, gwQuotaJSCLJPanel, gwQuotaFJSCLJPanel,
                        gwQuotaFHCLJPanel, gwQuotaJDCLJPanel, gwQuotaHGPJPanel);
            }
        });
        gwQuotaHGPJPanel.jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                DefaultTableModel model = (DefaultTableModel) gwQuotaHGPJPanel.getTableModel();
                int[] selectedRows = gwQuotaHGPJPanel.getTable().getSelectedRows();
                if(selectedRows.length>0){
                    for (int i = 0; i < selectedRows.length; i++) {
                        model.removeRow(gwQuotaHGPJPanel.getTable().getSelectedRow());
                    }
                }
                //重置序号
                int rowCount = gwQuotaHGPJPanel.getTable().getRowCount();
                setTabTitle(6, "火工品", technicsJTabbedPane, rowCount);
            }
        });
        gwQuotaHGPJPanel.jButton4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                DefaultTableModel model = (DefaultTableModel) gwQuotaHGPJPanel.getTableModel();
                int count = model.getRowCount();
                boolean isSelected = !(Boolean) model.getValueAt(0,1);
                for (int i = 0; i < count; i++) {
                    model.setValueAt(isSelected,i,1);
                }
            }
        });
        int rowCount6 = gwQuotaHGPJPanel.getTable().getRowCount();
        technicsJTabbedPane.add("火工品(" + rowCount6 + ")", gwQuotaHGPJPanel);
        TableColumn hgpTableColumn = gwQuotaHGPJPanel.getTable().getColumn("*项目分类");
        final JComboBox hgpXmflJComboBox = CommonUtil.getZpXmflJComboBox();
        hgpXmflJComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                int stateChange = e.getStateChange();
                if(stateChange==2){
                    String selectedItem = (String) hgpXmflJComboBox.getSelectedItem();
                    JTable jTable = gwQuotaHGPJPanel.getTable();
                    int rowCount = jTable.getRowCount();
                    for (int i = 0; i <rowCount ; i++) {
                        Boolean selected = (Boolean) jTable.getValueAt(i, 1);
                        if(selected){
                            jTable.setValueAt(selectedItem,i,2);
                        }
                    }
                }
            }
        });
        hgpTableColumn.setCellEditor(new DefaultCellEditor(CommonUtil.getZpXmflJComboBox()));
        rightPanel.add(technicsJTabbedPane);

        JPanel rightBottomPanel = new JPanel(new GridLayout(1, 2,5,10));
        JButton saveButtom = new JButton();
        saveButtom.setPreferredSize(new Dimension(120, 30));
        saveButtom.setIcon(IconUtil.getImageIcon("/images/tech_quota_save.png"));
        saveButtom.setText("保存并关闭");
        rightBottomPanel.add(saveButtom);
        myPanel2.add(rightPanel, BorderLayout.NORTH);
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

        //保存并关闭
        saveButtom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
                if(parentFrame instanceof TechnicsPaceJDialog){
                    parentFrame.setVisible(true);
                }
            }
        });
    }

    public void save(){
        //保存数据
        Element parentEle = null;
        Element techEle = null;
        if(parentFrame instanceof TechnicsPaceJDialog){
            TechnicsPaceJDialog paceJDialog = (TechnicsPaceJDialog) parentFrame;
            parentEle = paceJDialog.paceElement;
            techEle = paceJDialog.getStepElement().getParent().getParent();
        }else if (parentFrame instanceof NewTechnicsPart){
            TechnicsStepJPanel_XW stepPanel = frame.getTechnicsStepJPanel();
            parentEle = stepPanel.getStepElement();
            techEle = parentEle.getParent().getParent();
        }
        String partNumber=convertToString(techEle.attributeValue("partNumber"));
        Element gyde = XmlUtility.getTechnicsDEElement(techEle);
        Element clde = XmlUtility.getTechnicsCLDEElement(techEle);
        deleteAllClde(clde);
        Element yclde = XmlUtility.getChildElements(gyde, "NEWPART");
        Element zyclde = XmlUtility.getChildElements(gyde, "ZYCLDE");
        Element sjyclde = XmlUtility.getChildElements(gyde, "SJYCLDE");
        if (yclde == null) {
            yclde = DocumentHelper.createElement("NEWPART");
            gyde.add(yclde);
        }
        if (zyclde == null) {
            zyclde = DocumentHelper.createElement("ZYCLDE");
            gyde.add(zyclde);
        }
        if (sjyclde == null) {
            sjyclde = DocumentHelper.createElement("SJYCLDE");
            gyde.add(sjyclde);
        }
        Set<String> set = new HashSet<String>();
        //电子元器件
        JTable dzyqjTable = gwQuotaDZYQJJPanel.getTable();
        savaXmlInfo(parentEle, yclde, zyclde, sjyclde, dzyqjTable, yqjMap, "电子元器件",partNumber);
        set=getTchnicsQuotaInfo(dzyqjTable,set);
        //标准紧固件
        JTable bzjgjTable = gwQuotaBZJGJJPanel.getTable();
        savaXmlInfo(parentEle, yclde, zyclde, sjyclde, bzjgjTable, bzjgjMap, "标准紧固件",partNumber);
        set=getTchnicsQuotaInfo(bzjgjTable,set);
        //金属材料
        JTable jsclTable = gwQuotaJSCLJPanel.getTable();
        savaXmlInfo(parentEle, yclde, zyclde, sjyclde, jsclTable, jsclMap, "金属材料",partNumber);
        set=getTchnicsQuotaInfo(jsclTable,set);
        //非金属材料
        JTable fjsclTable = gwQuotaFJSCLJPanel.getTable();
        savaXmlInfo(parentEle, yclde, zyclde, sjyclde, fjsclTable, fjsclMap, "非金属材料",partNumber);
        set=getTchnicsQuotaInfo(fjsclTable,set);
        //复合材料
        JTable fhclTable = gwQuotaFHCLJPanel.getTable();
        savaXmlInfo(parentEle, yclde, zyclde, sjyclde, fhclTable, fhclMap, "复合材料",partNumber);
        set=getTchnicsQuotaInfo(fhclTable,set);
        //机电材料
        JTable jdclTable = gwQuotaJDCLJPanel.getTable();
        savaXmlInfo(parentEle, yclde, zyclde, sjyclde, jdclTable, jdMap, "机电材料",partNumber);
        //set=getTchnicsQuotaInfo(jdclTable,set);
        //火工品
        JTable hgpTable = gwQuotaHGPJPanel.getTable();
        savaXmlInfo(parentEle, yclde, zyclde, sjyclde, hgpTable, hgpMap, "火工品",partNumber);
        //set=getTchnicsQuotaInfo(hgpTable,set);

        frame.saveProcess(techEle);
        try {
            //保存自定义工艺定额到数据库
            String technicsNumber = convertToString(techEle.attributeValue("technicsNumber"));
            if(!"".equals(technicsNumber)){
                ErpToWCIntf.saveTechnicaQuotaInfo(set,technicsNumber,"装配定额");
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        //刷新参装件列表
        if(parentFrame instanceof TechnicsPaceJDialog){
            TechnicsPaceJDialog paceJDialog = (TechnicsPaceJDialog) parentFrame;
            paceJDialog.refreshPartDatas();
            PaceTablePane paceTablePane = paceJDialog.getPaceTablePane();
            if(paceTablePane != null){
                String bsoID = paceJDialog.getPaceElement().attributeValue("bsoID");
                paceTablePane.addPartValues(bsoID);
            }
        }else if (parentFrame instanceof NewTechnicsPart){
            TechnicsStepJPanel_XW stepPanel = frame.getTechnicsStepJPanel();
            stepPanel.refreshPartDatas();
        }
    }

    public void deleteAllClde(Element clde){
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
    }

    /**
     *获取所有自己添加的工艺定额
     * @param table
     * @param set
     * @return
     */
    private Set<String> getTchnicsQuotaInfo(JTable table ,Set<String> set){
        TableColumn ssjbmColoum = table.getColumn("设计编码");
        TableColumn wzbmColoum = table.getColumn("物资编码");
        if(ssjbmColoum!=null&&wzbmColoum!=null){
            int modelIndex = ssjbmColoum.getModelIndex();
            int wzbmModelIndex = wzbmColoum.getModelIndex();
            int rowCount = table.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                String sjbm = convertToString(table.getValueAt(i, modelIndex));
                if("".equals(sjbm)){
                    String wzbm = convertToString(table.getValueAt(i, wzbmModelIndex));
                    set.add(wzbm);
                }
            }
        }
        return set;
    }

    private void savaXmlInfo(Element parentEle, Element yclde, Element zyclde, Element sjyclde, JTable jtable, Map<String, String> infoMap, String tabType,String partNumber) {
        int rowCount = jtable.getRowCount();
        if (rowCount > 0) {
            HashMap<String, Integer> indexColumnMap = new HashMap<String, Integer>();
            Enumeration<TableColumn> columns = jtable.getColumnModel().getColumns();
            while (columns.hasMoreElements()) {
                TableColumn tableColumn = columns.nextElement();
                int modelIndex = tableColumn.getModelIndex();
                Object headerValue = tableColumn.getHeaderValue();
                indexColumnMap.put(convertToString(headerValue), modelIndex);
            }
            for (int i = 0; i < rowCount; i++) {
                String xmfl = (String) jtable.getValueAt(i, 2);
                Integer wzbmIndex = indexColumnMap.get("物资编码");
                Integer slIndex = indexColumnMap.get("*数量") != null ? indexColumnMap.get("*数量") : indexColumnMap.get("数量");
                double newSl = Double.valueOf((String) jtable.getValueAt(i, slIndex));
                Integer sl = Integer.valueOf((String) jtable.getValueAt(i, slIndex));
                String wzbm = (String) jtable.getValueAt(i, wzbmIndex);
                Element gydeEle = null;
                if ("配套件".equals(xmfl)) {
                    List<Element> elements = yclde.elements();
                    if(elements != null) {
                        for(Element element : elements) {
                            String chbm = element.attributeValue("chbm");
                            if(wzbm.equals(chbm)) {
                                //移除已有的物资
                                yclde.remove(element);
                                Integer oldSl = Integer.valueOf(element.attributeValue("sl"));
                                sl = oldSl + sl;
                                break;
                            }
                        }
                    }
                    gydeEle = createTechnicsNewPartElement(partNumber);
                    for(String key : infoMap.keySet()) {
                        if(slIndex == indexColumnMap.get(key)){
                            XmlUtility.setAttributeValue(gydeEle, infoMap.get(key), convertToString(sl));
                        }else{
                            XmlUtility.setAttributeValue(gydeEle, infoMap.get(key), convertToString(jtable.getValueAt(i, indexColumnMap.get(key))));
                        }
                    }
                    //设置类型
                    XmlUtility.setAttributeValue(gydeEle, "tabType", tabType);
                    XmlUtility.setAttributeValue(gydeEle, "dw2", convertToString(jtable.getValueAt(i, indexColumnMap.get("*单位"))));
                    yclde.add(gydeEle);
                } else if ("主要材料".equals(xmfl)) {
                    List<Element> elements = zyclde.elements();
                    if(elements != null) {
                        for(Element element : elements) {
                            String chbm = element.attributeValue("chbm");
                            if(wzbm.equals(chbm)) {
                                //移除已有的物资
                                zyclde.remove(element);
                                Integer oldSl = Integer.valueOf(element.attributeValue("sl"));
                                sl = oldSl + sl;
                                break;
                            }
                        }
                    }
                    gydeEle = createTechnicsZYCLDEElement(partNumber);
                    for (String key : infoMap.keySet()) {
                        if(slIndex == indexColumnMap.get(key)){
                            XmlUtility.setAttributeValue(gydeEle, infoMap.get(key), convertToString(sl));
                        }else{
                            XmlUtility.setAttributeValue(gydeEle, infoMap.get(key), convertToString(jtable.getValueAt(i, indexColumnMap.get(key))));
                        }
                    }
                    //设置类型
                    XmlUtility.setAttributeValue(gydeEle, "tabType", tabType);
                    XmlUtility.setAttributeValue(gydeEle, "dw2", convertToString(jtable.getValueAt(i, indexColumnMap.get("*单位"))));
                    zyclde.add(gydeEle);
                } else if ("试件".equals(xmfl)) {
                    List<Element> elements = sjyclde.elements();
                    if(elements != null) {
                        for(Element element : elements) {
                            String chbm = element.attributeValue("chbm");
                            if(wzbm.equals(chbm)) {
                                //移除已有的物资
                                sjyclde.remove(element);
                                Integer oldSl = Integer.valueOf(element.attributeValue("sl"));
                                sl = oldSl + sl;
                                break;
                            }
                        }
                    }
                    gydeEle = createTechnicsSJYCLDEElement();
                    for (String key : infoMap.keySet()) {
                        if(slIndex == indexColumnMap.get(key)){
                            XmlUtility.setAttributeValue(gydeEle, infoMap.get(key), convertToString(sl));
                        }else{
                            XmlUtility.setAttributeValue(gydeEle, infoMap.get(key), convertToString(jtable.getValueAt(i, indexColumnMap.get(key))));
                        }
                    }
                    XmlUtility.setAttributeValue(gydeEle, "tabType", tabType);
                    XmlUtility.setAttributeValue(gydeEle, "dw2", convertToString(jtable.getValueAt(i, indexColumnMap.get("*单位"))));
                    sjyclde.add(gydeEle);
                    continue;
                }

                Element parts = XmlUtility.getParts(parentEle);
                if(parts != null) {
                    List<Element> elements = parts.elements();
                    if(elements != null && elements.size()>0) {
                        for(Element element : elements) {
                            String number = element.attributeValue("partNumber");
                            if(wzbm.equals(number)){
                                double useCount = Double.valueOf(element.attributeValue("useCount"));
                                newSl = useCount + newSl;
                                elements.remove(element);
                                break;
                            }
                        }
                    }
                }
                VaTreeNode vaTreeNode = generalNode(gydeEle, tabType, newSl);
                DpPartNode dpPartNode = covertTreeNodeToPartNode(vaTreeNode);
                Map<String, String> partMap = generalNodeMap(vaTreeNode,dpPartNode);
                Element part = BomXMLUtil.generatePartData(partMap, null);
                parentEle.element("parts").add(part);
            }
        }

    }

    /**
     * 试件之前的xml
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

    private Element createTechnicsZYCLDEElement(String parentNumber) {
        Element element = DocumentHelper.createElement("zycldeRecord");
        XmlUtility.setAttributeValue(element, "cindex", parentNumber);
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

    private Element createTechnicsNewPartElement(String parentNumber) {
        Element element = DocumentHelper.createElement("NewPart");
        XmlUtility.setAttributeValue(element, "parentNumber",parentNumber);//"上级图号"
        XmlUtility.setAttributeValue(element, "number","");//"图号"
        XmlUtility.setAttributeValue(element, "chbm", "");//"存货编码"
        XmlUtility.setAttributeValue(element, "chmc", "");//"存货名称"
        XmlUtility.setAttributeValue(element, "sl", "");//"使用数量"
        XmlUtility.setAttributeValue(element, "dw2", "");//"单位"
        XmlUtility.setAttributeValue(element, "xhph", "");//"型号牌号"
        XmlUtility.setAttributeValue(element, "gg", "");//"规格"
        XmlUtility.setAttributeValue(element, "jstj", "");//"技术条件"
        XmlUtility.setAttributeValue(element, "sccj", "");//"生产厂家"
        XmlUtility.setAttributeValue(element, "dw","");//"主计量单位"
        XmlUtility.setAttributeValue(element, "fjtj", "");//"附加条件"
        XmlUtility.setAttributeValue(element, "lwgggccc", "");//"螺纹规格/公称尺寸"
        XmlUtility.setAttributeValue(element, "jxxndj", "");//"机械性能等级"
        XmlUtility.setAttributeValue(element, "zldj", "");//"质量等级"
        XmlUtility.setAttributeValue(element, "fzxs", "");//"封装形式"
        XmlUtility.setAttributeValue(element, "jddj", "");//"精度等级"
        XmlUtility.setAttributeValue(element, "wzlb", "");//"物资类别"
        XmlUtility.setAttributeValue(element, "wzlbbm", "");//"物资类别编码"
        XmlUtility.setAttributeValue(element, "cpdh", "");//"产品代号"
        XmlUtility.setAttributeValue(element, "zl", "");//"重量"
        XmlUtility.setAttributeValue(element, "zcsm", "");//"贮存寿命"
        XmlUtility.setAttributeValue(element, "tnt", "");//"TNT"
        XmlUtility.setAttributeValue(element, "dcstxyq", "");//"电参数特选要求"
        XmlUtility.setAttributeValue(element, "comment", "");//备注
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
        JTable dzyqjTable = gwQuotaDZYQJJPanel.getTable();
        buffer.append(validataTable(dzyqjTable, "电子元器件"));
        //标准紧固件
        JTable bzjgjTable = gwQuotaBZJGJJPanel.getTable();
        buffer.append(validataTable(bzjgjTable, "标准紧固件"));
        //金属材料
        JTable jsclTable = gwQuotaJSCLJPanel.getTable();
        buffer.append(validataTable(jsclTable, "金属材料"));
        //非金属材料
        JTable fjsclTable = gwQuotaFJSCLJPanel.getTable();
        buffer.append(validataTable(fjsclTable, "非金属材料"));
        //复合材料
        JTable fhclTable = gwQuotaFHCLJPanel.getTable();
        buffer.append(validataTable(fhclTable, "复合材料"));
        //机电材料
        JTable jdclTable = gwQuotaJDCLJPanel.getTable();
        buffer.append(validataTable(jdclTable, "机电材料"));
        //火工品
        JTable hgpTable = gwQuotaHGPJPanel.getTable();
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
        int rowCount = jTable.getRowCount();
        if (rowCount > 0) {
            int slColumnIndex = 0;
            List<Integer> columnList = new ArrayList<Integer>();
            TableColumnModel columnModel = jTable.getColumnModel();
            int wzbmColumnIndex = columnModel.getColumnIndex("物资编码");
            int xmflColumnIndex = columnModel.getColumnIndex("*项目分类");
            if(wzbmColumnIndex > 0) {
                Set<String> zyNumberSet = new HashSet<String>();
                Set<String> ptjNumberSet = new HashSet<String>();
                Set<String> sjNumberSet = new HashSet<String>();
                for(int i = 0; i < rowCount; i++) {
                    String wzbm = convertToString(jTable.getValueAt(i, wzbmColumnIndex));
                    String xmfl = convertToString(jTable.getValueAt(i, xmflColumnIndex));
                    if("主要材料".equals(xmfl)) {
                        if(zyNumberSet.contains(wzbm)){
                            buffer.append(tableName + "标签主要材料第" + (i + 1) + "行" + wzbm + "编码重复\n");
                        }else {
                            zyNumberSet.add(wzbm);
                        }
                    }
                    if("配套件".equals(xmfl)) {
                        if(ptjNumberSet.contains(wzbm)){
                            buffer.append(tableName + "标签配套件第" + (i + 1) + "行" + wzbm + "编码重复\n");
                        }else {
                            ptjNumberSet.add(wzbm);
                        }
                    }
                    if("试件".equals(xmfl)) {
                        if(sjNumberSet.contains(wzbm)){
                            buffer.append(tableName + "标签试件第" + (i + 1) + "行" + wzbm + "编码重复\n");
                        }else {
                            sjNumberSet.add(wzbm);
                        }
                    }
                }
            }
            if ("电子元器件".equals(tableName)) {
                int wzmcColumnIndex = columnModel.getColumnIndex("物资名称");
                //int xhggColumnIndex =columnModel.getColumnIndex("型号规格");
                slColumnIndex = columnModel.getColumnIndex("*数量");
                int dwColumnIndex = columnModel.getColumnIndex("*单位");
                columnList.add(xmflColumnIndex);
                columnList.add(wzmcColumnIndex);
                //columnList.add(xhggColumnIndex);
                columnList.add(slColumnIndex);
                columnList.add(dwColumnIndex);
            }else if("标准紧固件".equals(tableName)){
                int wzmcColumnIndex = columnModel.getColumnIndex("物资名称");
               // int ggColumnIndex =columnModel.getColumnIndex("规格");
               // int bzhColumnIndex =columnModel.getColumnIndex("标准号");
                slColumnIndex = columnModel.getColumnIndex("*数量");
                int dwColumnIndex = columnModel.getColumnIndex("*单位");
                columnList.add(xmflColumnIndex);
                columnList.add(wzmcColumnIndex);
               // columnList.add(ggColumnIndex);
               // columnList.add(bzhColumnIndex);
                columnList.add(slColumnIndex);
                columnList.add(dwColumnIndex);
            }else if("金属材料".equals(tableName)){
                int wzmcColumnIndex = columnModel.getColumnIndex("物资名称");
                int phColumnIndex =columnModel.getColumnIndex("牌号");
//                int cybzColumnIndex =columnModel.getColumnIndex("采用标准");
                int dwColumnIndex = columnModel.getColumnIndex("*单位");
                columnList.add(xmflColumnIndex);
                columnList.add(wzmcColumnIndex);
                columnList.add(phColumnIndex);
//                columnList.add(cybzColumnIndex);
                columnList.add(dwColumnIndex);
                slColumnIndex = columnModel.getColumnIndex("数量");
                for (int i = 0; i <rowCount ; i++) {
                    String xmflValue = convertToString(jTable.getValueAt(i, xmflColumnIndex));
                    if("主要材料".equals(xmflValue) || "配套件".equals(xmflValue)){
                        String sllValue = convertToString(jTable.getValueAt(i, slColumnIndex));
                        if ("".equals(sllValue)) {
                            buffer.append(tableName + "标签"+xmflValue+"第" + (i + 1) + "行、" + (slColumnIndex ) + "列为必填\n");
                        }
                    }
                }

            }else if("非金属材料".equals(tableName)){
                int wzmcColumnIndex = columnModel.getColumnIndex("物资名称");
                int phColumnIndex =columnModel.getColumnIndex("牌号");
//                int cybzColumnIndex =columnModel.getColumnIndex("采用标准");
                int dwColumnIndex = columnModel.getColumnIndex("*单位");
                columnList.add(xmflColumnIndex);
                columnList.add(wzmcColumnIndex);
                columnList.add(phColumnIndex);
//                columnList.add(cybzColumnIndex);
                columnList.add(dwColumnIndex);
                slColumnIndex = columnModel.getColumnIndex("数量");
                for (int i = 0; i <rowCount ; i++) {
                    String xmflValue = convertToString(jTable.getValueAt(i, xmflColumnIndex));
                    if("主要材料".equals(xmflValue) || "配套件".equals(xmflValue)){
                        String sllValue = convertToString(jTable.getValueAt(i, slColumnIndex));
                        if ("".equals(sllValue)) {
                            buffer.append(tableName + "标签"+xmflValue+"第" + (i + 1) + "行、" + (slColumnIndex ) + "列为必填\n");
                        }
                    }
                }
            }else if("复合材料".equals(tableName)){
                int wzmcColumnIndex = columnModel.getColumnIndex("物资名称");
                int dwColumnIndex = columnModel.getColumnIndex("*单位");
                slColumnIndex = columnModel.getColumnIndex("*数量");
                columnList.add(xmflColumnIndex);
                columnList.add(wzmcColumnIndex);
                columnList.add(slColumnIndex);
                columnList.add(dwColumnIndex);
            }else if("机电材料".equals(tableName)){
                int wzmcColumnIndex = columnModel.getColumnIndex("物资名称");
                int dwColumnIndex = columnModel.getColumnIndex("*单位");
                slColumnIndex = columnModel.getColumnIndex("*数量");
                columnList.add(xmflColumnIndex);
                columnList.add(wzmcColumnIndex);
                columnList.add(slColumnIndex);
                columnList.add(dwColumnIndex);
            }else if("火工品".equals(tableName)){
                int wzmcColumnIndex = columnModel.getColumnIndex("物资名称");
                int dwColumnIndex = columnModel.getColumnIndex("*单位");
                slColumnIndex = columnModel.getColumnIndex("*数量");
                columnList.add(xmflColumnIndex);
                columnList.add(wzmcColumnIndex);
                columnList.add(slColumnIndex);
                columnList.add(dwColumnIndex);
            }
            for (int i = 0; i <rowCount ; i++) {
                for (int j = 0; j < columnList.size(); j++) {
                    Integer integer = columnList.get(j);
                    String xmflValue = convertToString(jTable.getValueAt(i, integer));
                    if ("".equals(xmflValue)) {
                        buffer.append(tableName + "标签第" + (i + 1) + "行、" + (integer ) + "列为必填\n");
                    }
                }

                String sl = convertToString(jTable.getValueAt(i, slColumnIndex));
                if(!"".equals(sl)){
                    if(!DealFileUtil.isNumeric(sl)){
                        buffer.append(tableName + "标签第" + (i + 1) + "行数量必须为数字\n");
                    }
                }
            }
            String [] ids=new String[rowCount];
            for (int i = 0; i <rowCount ; i++) {
                String id = convertToString(jTable.getValueAt(i, 0));
                ids[i]=id;
            }
            try{
                buffer.append(ErpToWCIntf.validateWZFLData(ids,tableName));
            }catch (Exception e){
                e.printStackTrace();
            }
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
        if (false) {
            XWTreeObject xo = node.getObject();
            if ((xo instanceof XWTechnicsTreeObject)) {
                //电子元器件
                JTable dzyqjTable = gwQuotaDZYQJJPanel.getTable();
                DefaultTableModel dzyqjModel = (DefaultTableModel) gwQuotaDZYQJJPanel.getTableModel();
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
                JTable bzjgjTable = gwQuotaBZJGJJPanel.getTable();
                DefaultTableModel bzjgjModel = (DefaultTableModel) gwQuotaBZJGJJPanel.getTableModel();
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
                JTable jsclTable = gwQuotaJSCLJPanel.getTable();
                DefaultTableModel jsclModel = (DefaultTableModel) gwQuotaJSCLJPanel.getTableModel();
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
                JTable fjsclTable = gwQuotaFJSCLJPanel.getTable();
                DefaultTableModel fjsclModel = (DefaultTableModel) gwQuotaFJSCLJPanel.getTableModel();
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
                JTable fhclTable = gwQuotaFHCLJPanel.getTable();
                DefaultTableModel fhclModel = (DefaultTableModel) gwQuotaFHCLJPanel.getTableModel();
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
                JTable jdclTable = gwQuotaJDCLJPanel.getTable();
                DefaultTableModel jdclModel = (DefaultTableModel) gwQuotaJDCLJPanel.getTableModel();
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
                JTable hgpTable = gwQuotaHGPJPanel.getTable();
                DefaultTableModel hgpModel = (DefaultTableModel) gwQuotaHGPJPanel.getTableModel();
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

    private DpPartNode covertTreeNodeToPartNode(VaTreeNode pauseNode) {
        VaLightPart vaLightPart = (VaLightPart) pauseNode.getPart().clone();
        VaTreeNode treenode = new VaTreeNode(new VaEPartInstance(vaLightPart));
        DpPartNode partNode = new DpPartNode(treenode.getUserObject());
        partNode.set_cadObject(pauseNode.get_cadObject());
        partNode.set_index(pauseNode.get_index());
        partNode.set_LockedForCAD(pauseNode.get_LockedForCAD());
        partNode.set_pviewComponentInstance(pauseNode.get_pviewComponentInstance());
        partNode.set_pviewComponentNode(pauseNode.get_pviewComponentNode());
        partNode.set_pviewShapeInstance(pauseNode.get_pviewShapeInstance());
        partNode.set_wsInfo(pauseNode.get_wsInfo());
        partNode.setAllowsChildren(pauseNode.getAllowsChildren());
        partNode.setBaseMatrix(pauseNode.getBaseMatrix());
        partNode.setBboxes(pauseNode.getBboxes());
        partNode.setDiode(pauseNode.getDiode());
        partNode.setFktGrp(pauseNode.getFktGrp());
        partNode.setInfoTxt(pauseNode.getInfoTxt());
        partNode.setInstanceIdentifier(pauseNode.getInstanceIdentifier());
        partNode.setLocChange(pauseNode.getLocChange());
        partNode.setMatrix(pauseNode.getMatrix());
        partNode.setNodeType(pauseNode.getNodeType());
        partNode.setOccId(pauseNode.getOccId());
        partNode.setPathFromCI(pauseNode.getPathFromCI());
        partNode.setRelative_matrix(pauseNode.getRelative_matrix());
        partNode.setRelativeMatrix(pauseNode.getRelativeMatrix());
        partNode.setDataType(pauseNode.getDataType());
        partNode.setBzh(pauseNode.getBzh());
        partNode.getPart().setRootType("TECHNICS");
        return partNode;
    }

    public VaTreeNode generalNode(Element gydeEle,String tabType,double gysl){
        if("电子元器件".equals(tabType)){
            tabType = "元器件";
        }
        String chbm = gydeEle.attributeValue("chbm");
        String chmc = gydeEle.attributeValue("chmc");
        String dw = gydeEle.attributeValue("dw");
        String dw2 = gydeEle.attributeValue("dw2");
        String xhph = gydeEle.attributeValue("xhph");
        String gg = gydeEle.attributeValue("gg");
        String sccj = gydeEle.attributeValue("sccj");
        String dcstxyq = gydeEle.attributeValue("dcstxyq");
        String fzxs = gydeEle.attributeValue("fzxs");
        String zldj = gydeEle.attributeValue("zldj");
        String comment = gydeEle.attributeValue("comment");
        String jstj = gydeEle.attributeValue("jstj");
        String wh = gydeEle.attributeValue("wh");
        VaLightPart vaLightPart = VaLightPart.newLightPart(chbm, chmc, "", 1, xhph, gg);
        vaLightPart.setAmount(gysl);
        vaLightPart.setzCount(gysl);
        vaLightPart.setZcmark("Z");
        vaLightPart.setDataType(tabType);
        vaLightPart.setRootType("rootType");
        VaTreeNode vaTreeNode = new VaTreeNode(new VaEPartInstance(vaLightPart));
        vaTreeNode.setOccpath(chbm);
        vaTreeNode.setOccId(chbm);
        vaTreeNode.setDw(dw);
        vaTreeNode.setDw2(dw2);
        vaTreeNode.setXhph(xhph);
        vaTreeNode.setGg(gg);
        vaTreeNode.setSccj(sccj);
        vaTreeNode.setDcstxyq(dcstxyq);
        vaTreeNode.setFzxs(fzxs);
        vaTreeNode.setZldj(zldj);
        vaTreeNode.setJstj(jstj);
        vaTreeNode.setDataType(tabType);
        vaTreeNode.setComment(comment);
        vaTreeNode.setzCount(gysl);
        vaTreeNode.setWh(wh);
        vaTreeNode.setFlag("GYDENEWPART");
        return vaTreeNode;
    }

    private Map<String, String> generalNodeMap(VaTreeNode vaTreeNode, DpPartNode dpPartNode) {
        Map<String, String> partMap = new HashMap<String, String>();
        partMap.put("partNumber", dpPartNode.getPart().getNumber());
        partMap.put("oid", String.valueOf(dpPartNode.getPart().getOid()));
        partMap.put("occId", dpPartNode.getOccId());
        partMap.put("occpath", dpPartNode.getOccpath());
        partMap.put("partName", dpPartNode.getPart().getName());
        partMap.put("materialNumber",vaTreeNode.getPart().getMaterialNumber());//材料编号
        partMap.put("materialName",vaTreeNode.getPart().getMaterialName());//材料名称
        partMap.put("materialBrand",vaTreeNode.getPart().getMaterialBrand());//材料牌号
        partMap.put("materialCrision",vaTreeNode.getPart().getMaterialCrision());//材料标准号
        partMap.put("material", "");
        partMap.put("dutu", "");
        partMap.put("remark", "");
        partMap.put("useCount", String.valueOf(dpPartNode.getPart().getAmount()));

        partMap.put("MTYPE", dpPartNode.getPart().getMtype());
        String dataType = vaTreeNode.getDataType();
        String wh = vaTreeNode.getWh();
        if(dataType!=null && "元器件".equals(dataType) && wh!=null){
            partMap.put("wh", vaTreeNode.getWh());
        }
        String adjustable = vaTreeNode.getPart().getAdjustable();
        if(adjustable != null) {
            partMap.put("adjustable", adjustable);
        }
        partMap.put("CSIZE", dpPartNode.getPart().getCsize());
        partMap.put("XHPH", dpPartNode.getPart().getXhph());
        partMap.put("JSTJ", dpPartNode.getPart().getJstj());
        partMap.put("GG", dpPartNode.getPart().getGg());
        partMap.put("DW", vaTreeNode.getDw());
        partMap.put("DW2", vaTreeNode.getDw2());
        partMap.put("bzh", dpPartNode.getBzh());
        partMap.put("dataType", dpPartNode.getDataType());
//        partMap.put("HASEPM", vaTreeNode.isHasEpmDoc() + "");
        if(vaTreeNode.getFlag().contains("SJZYK")){
            partMap.put("materialGg", vaTreeNode.getPart().getGg());//材料规格
            partMap.put("materialLb", vaTreeNode.getPart().getDataType());//材料类别
            partMap.put("materialPh", vaTreeNode.getPart().getCl());//材料牌号
            partMap.put("materialBzh", vaTreeNode.getPart().getBzh());//材料标准号
        }else{
            partMap.put("materialGg", vaTreeNode.getPart().getGg());//材料编号
            partMap.put("materialLb", vaTreeNode.getPart().getDataType());//材料名称
            partMap.put("materialPh", vaTreeNode.getPart().getXhph());//材料牌号
            partMap.put("materialBzh", vaTreeNode.getPart().getJstj());//材料标准号
        }

        if ("C".equals(dpPartNode.getPart().getZcmark())) {
            partMap.put("ZCMARK", "C");
        } else if ("Z".equals(dpPartNode.getPart().getZcmark())) {
            partMap.put("ZCMARK", "Z");
        }
        return partMap;
    }
}