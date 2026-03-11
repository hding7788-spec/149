package com.glaway.mpm.erp;

import com.glaway.mpm.erp.component.TechnicsMaterialJCombox;
import com.glaway.mpm.util.IconUtil;
import com.glaway.mpm.view.GwIconButton;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.XWTreeNode;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import ext.ases.techMaterial.bean.*;
import jclass.bwt.JCComboBox;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;

/**
 * @program: SAST-149-PDM
 * @description:
 * @author: MChen
 * @create: 2021-01-20 13:34
 */
public class addTechQuotaDialog extends AbstractERPDialog {

    private NewTechnicsPart frame;
    private XWTreeNode node;
    private JDialog jdialog;
    private JLabel databaseLable;

    private Vector databaseModel; //
    private Vector wzlbMode;
    private Vector<KVItem> wzlbMode2;

    //物资名称
    private JLabel chmcLable;
    private TechnicsMaterialJCombox wzmcJbo = new TechnicsMaterialJCombox("物资名称");
    //物资简称
    private JLabel wzjcLabel;
    private JTextField wzjcText;
    //编码优选级别
    private JLabel bmyxjbLabel;
    private JTextField bmyxjbText;
    //编码状态
    private JLabel bmztLabel;
    private JTextField bmztText;
    //编码类型
    private JLabel bmlxLabel;
    private JTextField bmlxText;
    //编码等级
    private JLabel bmdjLabel;
    private JTextField bmdjText;
    //换算率
    private JLabel hslLabel;
    private JTextField hslText;
    //系数
    private JLabel xsLabel;
    private JTextField xsText;
    //牌号
    private JLabel phLabel;
    private TechnicsMaterialJCombox phJbo = new TechnicsMaterialJCombox("牌号");
    //规格
    private JLabel ggLabel;
    private TechnicsMaterialJCombox ggJbo = new TechnicsMaterialJCombox("规格");
    //采用标准
    private JLabel cybzLabel;
    private JTextField cybzText;
    //特殊说明
    private JLabel tssmLabel;
    private JTextField tssmText;
    //是否进口
    private JLabel sfjkLabel;
    private JTextField sfjkText;
    //型号规格
    private JLabel xhggLabel;
    private JTextField xhggText;
    //质量等级
    private JLabel zldjLabel;
    private JTextField zldjText;
    //总规范
    private JLabel zgfLabel;
    private JTextField zgfText;
    //详细规范
    private JLabel xxgfLabel;
    private JTextField xxgfText;
    //型号
    private JLabel xhLabel;
    private JTextField xhText;
    //封装形式
    private JLabel fzxsLabel;
    private JTextField fzxsText;
    //外形尺寸
    private JLabel wxccLabel;
    private JTextField wxccText;
    //专用条件
    private JLabel zytjLabel;
    private JTextField zytjText;
    //附加协议
    private JLabel fjxyLabel;
    private JTextField fjxyText;
    //抗辐射指标TID
    private JLabel kfszbtidLabel;
    private JTextField kfszbtidText;
    //抗辐射指标SEE
    private JLabel kfszbseeLabel;
    private JTextField kfszbseeText;
    //性能参数
    private JLabel xncsLabel;
    private JTextField xncsText;
    //是否静电敏感
    private JLabel sfjdmgLabel;
    private JTextField sfjdmgText;
    //静电敏感等级
    private JLabel jdmgdjLabel;
    private JTextField jdmgdjText;
    //湿敏等级
    private JLabel smdjLabel;
    private JTextField smdjText;
    //供应状态
    private JLabel gyztLabel;
    private JTextField gyztText;
    //精度
    private JLabel jdLabel;
    private JTextField jdText;
    //质量特征
    private JLabel zltzLabel;
    private JTextField zltzText;
    //品种规格标准
    private JLabel pzggbzLabel;
    private JTextField pzggbzText;
    //标准号
    private JLabel bzhLabel;
    private TechnicsMaterialJCombox bzhJbo = new TechnicsMaterialJCombox("标准号");
    //材料
    private JLabel clLabel;
    private JTextField clText;
    //机械性能等级或硬度
    private JLabel jxxndjLabel;
    private JTextField jxxndjText;
    //表面处理
    private JLabel bmclLabel;
    private JTextField bmclText;
    //热处理
    private JLabel rclLabel;
    private JTextField rclText;
    //产品型式
    private JLabel cpxsLabel;
    private JTextField cpxsText;
    //产品等级
    private JLabel cpdjLabel;
    private JTextField cpdjText;
    //板拧形式
    private JLabel nbxsLabel;
    private JTextField nbxsText;
    //生产厂家
    private JLabel sccjLabel;
    private TechnicsMaterialJCombox sccjJbo = new TechnicsMaterialJCombox("生产厂家");
    //计量单位
    private JLabel jldwLabel;
    private JComboBox jldwJComboBox;
    //单位
    private JLabel gydwLabel;
    private JComboBox gydwJComboBox;


    public LinkedHashMap<JComponent, String> componentMap;
    public LinkedHashMap<JComponent, String> standardMap;
    public LinkedHashMap<JComponent, String> materialMap;
    public LinkedHashMap<JComponent, String> nonMaterialMap;
    public LinkedHashMap<JComponent, String> compoundMap;
    public LinkedHashMap<JComponent, String> jdclMap;
    public LinkedHashMap<JComponent, String> hgpMap;

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


    public addTechQuotaDialog(NewTechnicsPart frame, String title, XWTreeNode node, String type,JTabbedPane technicsJTabbedPane ,TechnicsQuotaDZYQJJPanel technicsQuotaDZYQJJPanel,
                              TechnicsQuotaBZJGJJPanel technicsQuotaBZJGJJPanel, TechnicsQuotaJSCLJPanel technicsQuotaJSCLJPanel, TechnicsQuotaFJSCLJPanel technicsQuotaFJSCLJPanel,
                              TechnicsQuotaFHCLJPanel technicsQuotaFHCLJPanel, TechnicsQuotaJDCLJPanel technicsQuotaJDCLJPanel, TechnicsQuotaHGPJPanel technicsQuotaHGPJPanel) {
        super(frame);
        this.currentDialog = this;
        this.technicsJTabbedPane=technicsJTabbedPane;
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
        initComponents();
        initAttrCollect();
        transForList();
        initDimension();
        initActions();
        initLayout(type);
        loadInitDatas();
        this.setVisible(true);
        this.setResizable(false);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    }

    public void transForList() {


    }

    public void initAttrCollect() {
        componentMap = new LinkedHashMap<JComponent, String>();
        componentMap.put(wzmcJbo, "WZMC");
        componentMap.put(xhggText, "XHGG");
        componentMap.put(zldjText, "ZLDJ");
        componentMap.put(sccjJbo, "SCCJ");
        componentMap.put(jldwJComboBox, "JLDW");
        componentMap.put(zgfText, "ZGF");
        componentMap.put(xxgfText, "XXGF");
        componentMap.put(xhText, "XH");
        componentMap.put(fzxsText, "FZXS");
        componentMap.put(wxccText, "WXCC");
        componentMap.put(zytjText, "ZYTJ");
        componentMap.put(fjxyText, "FJXY");
        componentMap.put(tssmText, "TSSM");
        componentMap.put(sfjkText, "SFJK");
        componentMap.put(kfszbtidText, "KFSZBTID");
        componentMap.put(kfszbseeText, "KFSZBSEE");
        componentMap.put(xncsText, "XNCS");
        componentMap.put(sfjdmgText, "SFJDMG");
        componentMap.put(jdmgdjText, "JDMGDJ");
        componentMap.put(smdjText, "SMDJ");
        componentMap.put(wzjcText, "WZJC");
        componentMap.put(bmyxjbText, "BMYXJB");
        componentMap.put(bmztText, "BMZT");
        componentMap.put(bmlxText, "BMLX");
        componentMap.put(bmdjText, "BMDJ");

        componentList = new ArrayList<Object>();
        componentList.add(chmcLable);
        componentList.add(wzmcJbo);
        componentList.add(wzjcLabel);
        componentList.add(wzjcText);
        componentList.add(bmyxjbLabel);
        componentList.add(bmyxjbText);
        componentList.add(bmztLabel);
        componentList.add(bmztText);
        componentList.add(bmlxLabel);
        componentList.add(bmlxText);
        componentList.add(bmdjLabel);
        componentList.add(bmdjText);
        componentList.add(xhggLabel);
        componentList.add(xhggText);
        componentList.add(zldjLabel);
        componentList.add(zldjText);
        componentList.add(zgfLabel);
        componentList.add(zgfText);
        componentList.add(xxgfLabel);
        componentList.add(xxgfText);
        componentList.add(xhLabel);
        componentList.add(xhText);
        componentList.add(fzxsLabel);
        componentList.add(fzxsText);
        componentList.add(wxccLabel);
        componentList.add(wxccText);
        componentList.add(zytjLabel);
        componentList.add(zytjText);
        componentList.add(fjxyLabel);
        componentList.add(fjxyText);
        componentList.add(tssmLabel);
        componentList.add(tssmText);
        componentList.add(sfjkLabel);
        componentList.add(sfjkText);
        componentList.add(kfszbtidLabel);
        componentList.add(kfszbtidText);
        componentList.add(kfszbseeLabel);
        componentList.add(kfszbseeText);
        componentList.add(xncsLabel);
        componentList.add(xncsText);
        componentList.add(sfjdmgLabel);
        componentList.add(sfjdmgText);
        componentList.add(jdmgdjLabel);
        componentList.add(jdmgdjText);
        componentList.add(smdjLabel);
        componentList.add(smdjText);
        componentList.add(sccjLabel);
        componentList.add(sccjJbo);
        componentList.add(jldwLabel);
        componentList.add(jldwJComboBox);

        standardMap = new LinkedHashMap<JComponent, String>();
        standardMap.put(wzmcJbo, "WZMC");
        standardMap.put(ggJbo, "GG");
        standardMap.put(bzhJbo, "BZH");
        standardMap.put(jxxndjText, "JXXNDJ");
        standardMap.put(jldwJComboBox, "JLDW");
        standardMap.put(clText, "CL");
        standardMap.put(bmclText, "BMCL");
        standardMap.put(rclText, "RCL");
        standardMap.put(sccjJbo, "SCCJ");
        standardMap.put(cpxsText, "CPXS");
        standardMap.put(cpdjText, "CPDJ");
        standardMap.put(nbxsText, "NBXS");
        standardMap.put(tssmText, "TSSM");
        standardMap.put(sfjkText, "SFJK");
        standardMap.put(wzjcText, "WZJC");
        standardMap.put(bmyxjbText, "BMYXJB");
        standardMap.put(bmztText, "BMZT");
        standardMap.put(bmlxText, "BMLX");
        standardMap.put(bmdjText, "BMDJ");

        standardList = new ArrayList<Object>();
        standardList.add(chmcLable);
        standardList.add(wzmcJbo);
        standardList.add(wzjcLabel);
        standardList.add(wzjcText);
        standardList.add(bmyxjbLabel);
        standardList.add(bmyxjbText);
        standardList.add(bmztLabel);
        standardList.add(bmztText);
        standardList.add(bmlxLabel);
        standardList.add(bmlxText);
        standardList.add(bmdjLabel);
        standardList.add(bmdjText);
        standardList.add(bzhLabel);
        standardList.add(bzhJbo);
        standardList.add(ggLabel);
        standardList.add(ggJbo);
        standardList.add(clLabel);
        standardList.add(clText);
        standardList.add(jxxndjLabel);
        standardList.add(jxxndjText);
        standardList.add(bmclLabel);
        standardList.add(bmclText);
        standardList.add(rclLabel);
        standardList.add(rclText);
        standardList.add(cpxsLabel);
        standardList.add(cpxsText);
        standardList.add(cpdjLabel);
        standardList.add(cpdjText);
        standardList.add(nbxsLabel);
        standardList.add(nbxsText);
        standardList.add(tssmLabel);
        standardList.add(tssmText);
        standardList.add(sfjkLabel);
        standardList.add(sfjkText);
        standardList.add(sccjLabel);
        standardList.add(sccjJbo);
        standardList.add(jldwLabel);
        standardList.add(jldwJComboBox);

        materialMap = new LinkedHashMap<JComponent, String>();
        materialMap.put(wzmcJbo, "WZMC");
        materialMap.put(phJbo, "PH");
        materialMap.put(ggJbo, "GG");
        materialMap.put(gyztText, "GYZT");
        materialMap.put(cybzText, "CYBZ");
        materialMap.put(gydwJComboBox, "GYDW");
        materialMap.put(pzggbzText, "PZGGBZ");
        materialMap.put(jdText, "JD");
        materialMap.put(zltzText, "ZLTZ");
        materialMap.put(sccjJbo, "SCCJ");
        materialMap.put(wzjcText, "WZJC");
        materialMap.put(tssmText, "TSSM");
        materialMap.put(sfjkText, "SFJK");
        materialMap.put(hslText, "HSL");
        materialMap.put(xsText, "XS");
        materialMap.put(bmyxjbText, "BMYXJB");
        materialMap.put(bmztText, "BMZT");
        materialMap.put(bmlxText, "BMLX");
        materialMap.put(bmdjText, "BMDJ");

        materialList = new ArrayList<Object>();
        materialList.add(chmcLable);
        materialList.add(wzmcJbo);
        materialList.add(wzjcLabel);
        materialList.add(wzjcText);
        materialList.add(bmyxjbLabel);
        materialList.add(bmyxjbText);
        materialList.add(bmztLabel);
        materialList.add(bmztText);
        materialList.add(bmlxLabel);
        materialList.add(bmlxText);
        materialList.add(bmdjLabel);
        materialList.add(bmdjText);
        materialList.add(hslLabel);
        materialList.add(hslText);
        materialList.add(xsLabel);
        materialList.add(xsText);
        materialList.add(phLabel);
        materialList.add(phJbo);
        materialList.add(gyztLabel);
        materialList.add(gyztText);
        materialList.add(cybzLabel);
        materialList.add(cybzText);
        materialList.add(jdLabel);
        materialList.add(jdText);
        materialList.add(zltzLabel);
        materialList.add(zltzText);
        materialList.add(pzggbzLabel);
        materialList.add(pzggbzText);
        materialList.add(tssmLabel);
        materialList.add(tssmText);
        materialList.add(sfjkLabel);
        materialList.add(sfjkText);
        materialList.add(sccjLabel);
        materialList.add(sccjJbo);
        materialList.add(ggLabel);
        materialList.add(ggJbo);
        materialList.add(gydwLabel);
        materialList.add(gydwJComboBox);

        nonMaterialMap = new LinkedHashMap<JComponent, String>();
        nonMaterialMap.put(wzmcJbo, "WZMC");
        nonMaterialMap.put(phJbo, "PH");
        nonMaterialMap.put(ggJbo, "GG");
        nonMaterialMap.put(cybzText, "CYBZ");
        nonMaterialMap.put(gydwJComboBox, "GYDW");
        nonMaterialMap.put(sccjJbo, "SCCJ");
        nonMaterialMap.put(tssmText, "TSSM");
        nonMaterialMap.put(sfjkText, "SFJK");
        nonMaterialMap.put(wzjcText, "WZJC");
        nonMaterialMap.put(hslText, "HSL");
        nonMaterialMap.put(xsText, "XS");
        nonMaterialMap.put(bmyxjbText, "BMYXJB");
        nonMaterialMap.put(bmztText, "BMZT");
        nonMaterialMap.put(bmlxText, "BMLX");
        nonMaterialMap.put(bmdjText, "BMDJ");

        nonMaterialList = new ArrayList<Object>();
        nonMaterialList.add(chmcLable);
        nonMaterialList.add(wzmcJbo);
        nonMaterialList.add(wzjcLabel);
        nonMaterialList.add(wzjcText);
        nonMaterialList.add(bmyxjbLabel);
        nonMaterialList.add(bmyxjbText);
        nonMaterialList.add(bmztLabel);
        nonMaterialList.add(bmztText);
        nonMaterialList.add(bmlxLabel);
        nonMaterialList.add(bmlxText);
        nonMaterialList.add(bmdjLabel);
        nonMaterialList.add(bmdjText);
        nonMaterialList.add(hslLabel);
        nonMaterialList.add(hslText);
        nonMaterialList.add(xsLabel);
        nonMaterialList.add(xsText);
        nonMaterialList.add(phLabel);
        nonMaterialList.add(phJbo);
        nonMaterialList.add(ggLabel);
        nonMaterialList.add(ggJbo);
        nonMaterialList.add(cybzLabel);
        nonMaterialList.add(cybzText);
        nonMaterialList.add(tssmLabel);
        nonMaterialList.add(tssmText);
        nonMaterialList.add(sfjkLabel);
        nonMaterialList.add(sfjkText);
        nonMaterialList.add(sccjLabel);
        nonMaterialList.add(sccjJbo);
        nonMaterialList.add(gydwLabel);
        nonMaterialList.add(gydwJComboBox);

        compoundMap = new LinkedHashMap<JComponent, String>();

        compoundMap.put(wzmcJbo, "WZMC");
        compoundMap.put(phJbo, "PH");
        compoundMap.put(ggJbo, "GG");
        compoundMap.put(cybzText, "CYBZ");
        compoundMap.put(jldwJComboBox, "JLDW");
        compoundMap.put(sccjJbo, "SCCJ");
        compoundMap.put(tssmText, "TSSM");
        compoundMap.put(sfjkText, "SFJK");
        compoundMap.put(wzjcText, "WZJC");
        compoundMap.put(hslText, "HSL");
        compoundMap.put(xsText, "XS");
        compoundMap.put(bmyxjbText, "BMYXJB");
        compoundMap.put(bmztText, "BMZT");
        compoundMap.put(bmlxText, "BMLX");
        compoundMap.put(bmdjText, "BMDJ");

        compoundList = new ArrayList<Object>();
        compoundList.add(chmcLable);
        compoundList.add(wzmcJbo);
        compoundList.add(wzjcLabel);
        compoundList.add(wzjcText);
        compoundList.add(bmyxjbLabel);
        compoundList.add(bmyxjbText);
        compoundList.add(bmztLabel);
        compoundList.add(bmztText);
        compoundList.add(bmlxLabel);
        compoundList.add(bmlxText);
        compoundList.add(bmdjLabel);
        compoundList.add(bmdjText);
        compoundList.add(hslLabel);
        compoundList.add(hslText);
        compoundList.add(xsLabel);
        compoundList.add(xsText);
        compoundList.add(phLabel);
        compoundList.add(phJbo);
        compoundList.add(ggLabel);
        compoundList.add(ggJbo);
        compoundList.add(cybzLabel);
        compoundList.add(cybzText);
        compoundList.add(tssmLabel);
        compoundList.add(tssmText);
        compoundList.add(sfjkLabel);
        compoundList.add(sfjkText);
        compoundList.add(sccjLabel);
        compoundList.add(sccjJbo);
        compoundList.add(jldwLabel);
        compoundList.add(jldwJComboBox);
        //机电材料
        jdclMap = new LinkedHashMap<JComponent, String>();
        jdclMap.put(wzmcJbo, "WZMC");
        jdclMap.put(phJbo, "PH");
        jdclMap.put(bzhJbo, "BZH");
        jdclMap.put(ggJbo, "GG");
        jdclMap.put(jldwJComboBox, "JLDW");
        jdclMap.put(sccjJbo, "SCCJ");
        jdclList = new ArrayList<Object>();
        jdclList.add(chmcLable);
        jdclList.add(wzmcJbo);
        jdclList.add(phLabel);
        jdclList.add(phJbo);
        jdclList.add(bzhLabel);
        jdclList.add(bzhJbo);
        jdclList.add(ggLabel);
        jdclList.add(ggJbo);
        jdclList.add(jldwLabel);
        jdclList.add(jldwJComboBox);
        jdclList.add(sccjLabel);
        jdclList.add(sccjJbo);
        //火工品
        hgpMap = new LinkedHashMap<JComponent, String>();
        hgpMap.put(wzmcJbo, "WZMC");
        hgpMap.put(phJbo, "PH");
        hgpMap.put(bzhJbo, "BZH");
        hgpMap.put(jldwJComboBox, "JLDW");
        hgpMap.put(sccjJbo, "SCCJ");
        hgpList = new ArrayList<Object>();
        hgpList.add(chmcLable);
        hgpList.add(wzmcJbo);
        hgpList.add(bzhLabel);
        hgpList.add(bzhJbo);
        hgpList.add(phLabel);
        hgpList.add(phJbo);
        hgpList.add(sccjLabel);
        hgpList.add(sccjJbo);
        hgpList.add(jldwLabel);
        hgpList.add(jldwJComboBox);
    }


    @Override
    protected void initActions() {

    }

    @Override
    protected void initComponents() {
        databaseLable = new JLabel("基础数据库");
        databaseLable.setForeground(Color.red);

        chmcLable = new JLabel("物资名称");

        wzjcLabel = new JLabel("物资简称");
        wzjcText = new JTextField();

        bmyxjbLabel = new JLabel("编码优选级别");
        bmyxjbText = new JTextField();

        bmztLabel = new JLabel("编码状态");
        bmztText = new JTextField();

        bmlxLabel = new JLabel("编码类型");
        bmlxText = new JTextField();

        bmdjLabel = new JLabel("编码等级");
        bmdjText = new JTextField();

        hslLabel = new JLabel("换算率");
        hslText = new JTextField();

        xsLabel = new JLabel("系数");
        xsText = new JTextField();

        phLabel = new JLabel("牌号");

        gyztLabel = new JLabel("供应状态");
        gyztText = new JTextField();

        cybzLabel = new JLabel("采用标准");
        cybzText = new JTextField();

        jdLabel = new JLabel("精度");
        jdText = new JTextField();

        zltzLabel = new JLabel("质量特征");
        zltzText = new JTextField();

        pzggbzLabel = new JLabel("品种规格标准");
        pzggbzText = new JTextField();

        tssmLabel = new JLabel("特殊说明");
        tssmText = new JTextField();

        sfjkLabel = new JLabel("是否进口");
        sfjkText = new JTextField();

        bzhLabel = new JLabel("标准号");
        clLabel = new JLabel("材料");
        clText = new JTextField();
        jxxndjLabel = new JLabel("机械性能等级或硬度");
        jxxndjText = new JTextField();
        bmclLabel = new JLabel("表面处理");
        bmclText = new JTextField();
        rclLabel = new JLabel("热处理");
        rclText = new JTextField();
        cpxsLabel = new JLabel("产品型式");
        cpxsText = new JTextField();
        cpdjLabel = new JLabel("产品等级");
        cpdjText = new JTextField();
        nbxsLabel = new JLabel("板拧形式");
        nbxsText = new JTextField();
        xhggLabel = new JLabel("型号规格");
        xhggText = new JTextField();
        zldjLabel = new JLabel("质量等级");
        zldjText = new JTextField();
        zgfLabel = new JLabel("总规范");
        zgfText = new JTextField();
        xxgfLabel = new JLabel("详细规范");
        xxgfText = new JTextField();
        xhLabel = new JLabel("型号");
        xhText = new JTextField();
        fzxsLabel = new JLabel("封装形式");
        fzxsText = new JTextField();
        wxccLabel = new JLabel("外形尺寸");
        wxccText = new JTextField();
        zytjLabel = new JLabel("专用条件");
        zytjText = new JTextField();
        fjxyLabel = new JLabel("附加协议");
        fjxyText = new JTextField();
        kfszbtidLabel = new JLabel("抗辐射指标TID");
        kfszbtidText = new JTextField();
        kfszbseeLabel = new JLabel("抗辐射指标SEE");
        kfszbseeText = new JTextField();
        xncsLabel = new JLabel("性能参数");
        xncsText = new JTextField();
        sfjdmgLabel = new JLabel("是否静电敏感");
        sfjdmgText = new JTextField();
        jdmgdjLabel = new JLabel("静电敏感等级");
        jdmgdjText = new JTextField();
        smdjLabel = new JLabel("湿敏等级");
        smdjText = new JTextField();
        ggLabel = new JLabel("规格");
        sccjLabel = new JLabel("生产厂家");
        String[] keyComponentValues = {"个","只","件","升","毫升","立方米","平方米","米","毫米","克","千克","磅(lb)","英寸(inch)"};
        jldwLabel = new JLabel("计量单位");
        jldwJComboBox=new JComboBox(keyComponentValues);
        gydwLabel = new JLabel("单位");
        gydwJComboBox=new JComboBox(keyComponentValues);
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
        if ("元器件".equals(type)) {
            layOutList = componentList;
        } else if ("标准紧固件".equals(type)) {
            layOutList = standardList;
        } else if ("金属材料".equals(type)) {
            layOutList = materialList;
        } else if ("非金属材料".equals(type)) {
            layOutList = nonMaterialList;
        } else if ("复合材料".equals(type)) {
            layOutList = compoundList;
        } else if ("机电材料".equals(type)) {
            layOutList = jdclList;
        } else if ("火工品".equals(type)) {
            layOutList = hgpList;
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
                final Map<String, String> conditionMap = new HashMap<String, String>();
                LinkedHashMap<JComponent, String> conditionLinkMap = new LinkedHashMap<JComponent, String>();
                List<Object> nameList = new ArrayList<Object>();
                //根据不同的页签获取不同值
                if ("元器件".equals(type)) {
                    conditionLinkMap = componentMap;
                    nameList=componentList;
                } else if ("标准紧固件".equals(type)) {
                    conditionLinkMap = standardMap;
                    nameList=standardList;
                } else if ("金属材料".equals(type)) {
                    conditionLinkMap = materialMap;
                    nameList=materialList;
                } else if ("非金属材料".equals(type)) {
                    conditionLinkMap = nonMaterialMap;
                    nameList=nonMaterialList;
                } else if ("复合材料".equals(type)) {
                    conditionLinkMap = compoundMap;
                    nameList=compoundList;
                } else if ("机电材料".equals(type)) {
                    conditionLinkMap = jdclMap;
                    nameList=jdclList;
                } else if ("火工品".equals(type)) {
                    conditionLinkMap = hgpMap;
                    nameList=hgpList;
                }
                //校验物资名称必填
                String text1 = (String) wzmcJbo.getSelectedItem();
                if ("null".equals(text1)||"".equals(text1)||text1==null) {
                    JOptionPane.showMessageDialog(owner, "物资名称必填");
                    return;
                }

                //校验工艺物资名称是否合法
                final Map<String,String> validateConditionMap=new HashMap<String, String>();
                for (JComponent key : conditionLinkMap.keySet()) {
                    int i = nameList.indexOf(key);
                    String text="";
                    if (key instanceof JTextField) {
                        JTextField jField= (JTextField) key;
                         text = jField.getText();
                    }else if(key instanceof JComboBox){
                        JComboBox jComboBox= (JComboBox) key;
                        text= (String) jComboBox.getSelectedItem();
                    }
                    JLabel o = (JLabel) nameList.get(i - 1);
                    if (!"null".equals(text)&&!"".equals(text)&&text!=null) {
                        String labelValue = o.getText();
                        if ("单位".equals(labelValue)) {
                            labelValue="工艺单位";
                        }
                        validateConditionMap.put(labelValue,text);
                    }
                }
                String msg = ErpToWCIntf.validateTmInfo(validateConditionMap);
                if (!"null".equals(msg)&&!"".equals(msg)&&msg!=null) {
                    JOptionPane.showMessageDialog(owner, msg);
                    return;
                }
                for (JComponent key : conditionLinkMap.keySet()) {

                    String text="";
                    if (key instanceof JTextField) {
                        JTextField jField= (JTextField) key;
                        text = jField.getText();
                    }else if(key instanceof JComboBox){
                        JComboBox jComboBox= (JComboBox) key;
                        text= (String) jComboBox.getSelectedItem();
                    }
                    if ("null".equals(text)) {
                        text = "";
                    }
                    String column = conditionLinkMap.get(key);
                    conditionMap.put(column, text);
                }
                final VaActionProgressBar progressBar = new VaActionProgressBar(frame, "保存数据", "正在保存数据,请等待...", "保存数据中");
                Thread thread = new Thread() {
                    public void run() {
                        List list = ErpToWCIntf.getRealTechMaterialInfo(type, conditionMap);
                        if (list.size() > 0) {
                            addRowInfo(list, type);
                            progressBar.finish();
                            progressBar.setVisible(false);
                            currentDialog.dispose();
                        } else {
                            int flag = JOptionPane.showConfirmDialog(owner, "是否创建新的物资条目信息？", "确认", JOptionPane.OK_CANCEL_OPTION);
                            if (flag == 0) {
                                //创建细新的物资条目信息，并返回
                                String number = "GYWZTM" + ErpToWCIntf.getTechnicsMaterialEntriesNum();
                                Object techniMaterial = ErpToWCIntf.createTechniMaterial(conditionMap, type,number);
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
                                currentDialog.dispose();
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
     * 加载数据
     *
     * @param objData
     * @param type
     */
    public void loadCurrentData(Object objData, String type) {
        DefaultTableModel model = null;
        if ("元器件".equals(type)) {
            model = (DefaultTableModel) technicsQuotaDZYQJJPanel.getTableModel();
        } else if ("标准紧固件".equals(type)) {
            model = (DefaultTableModel) technicsQuotaBZJGJJPanel.getTable().getModel();
        } else if ("金属材料".equals(type)) {
            model = (DefaultTableModel) technicsQuotaJSCLJPanel.getTableModel();
        } else if ("非金属材料".equals(type)) {
            model = (DefaultTableModel) technicsQuotaFJSCLJPanel.getTableModel();
        } else if ("复合材料".equals(type)) {
            model = (DefaultTableModel) technicsQuotaFHCLJPanel.getTableModel();
        } else if ("机电材料".equals(type)) {
            model = (DefaultTableModel) technicsQuotaJDCLJPanel.getTableModel();
        } else if ("火工品".equals(type)) {
            model = (DefaultTableModel) technicsQuotaHGPJPanel.getTableModel();
        }
        int row = model.getRowCount();
        //model.setRowCount(0);
        Object obj = objData;
        if (obj instanceof TMEEleComponentsPartLinkBean) {
            TMEEleComponentsPartLinkBean bean = (TMEEleComponentsPartLinkBean) obj;
            Object[] values = new Object[componentMap.size() + 14];
            values[0] = bean.getTechnicsmaterialentriesid();
            values[1] = false;
            values[2] = String.valueOf("主要材料");
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
                    values[intFlag + 1] = "";
                    values[intFlag + 2] = bean.getWzbm();
                    intFlag = intFlag + 2;
                }
                intFlag++;
            }
            model.addRow(values);
            JTable dzyqjTable = technicsQuotaDZYQJJPanel.getTable();
            int rowCount = dzyqjTable.getRowCount();
            setTabTitle(0, "电子元器件", technicsJTabbedPane, rowCount );

        } else if (obj instanceof TMEStandPartLinkBean) {
            TMEStandPartLinkBean bean = (TMEStandPartLinkBean) obj;
            Object[] values = new Object[standardMap.size() + 12];
            values[0] = bean.getTechnicsmaterialentriesid();
            values[1] = false;
            values[2] = String.valueOf("主要材料");
            int intFlag = 3;
            for (JComponent key : standardMap.keySet()) {
                String s = standardMap.get(key);
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
                    values[intFlag + 1] = "";
                    values[intFlag + 2] = bean.getWzbm();
                    intFlag = intFlag + 2;
                }
                intFlag++;
            }
            model.addRow(values);
            JTable bzjTable = technicsQuotaBZJGJJPanel.getTable();
            int rowCount = bzjTable.getRowCount();
            setTabTitle(1, "标准紧固件", technicsJTabbedPane, rowCount );
        } else if (obj instanceof TMEMetallicPartLinkBean) {
            TMEMetallicPartLinkBean bean = (TMEMetallicPartLinkBean) obj;
            Object[] values = new Object[materialMap.size() + 12];
            values[0] = bean.getTechnicsmaterialentriesid();
            values[1] = false;
            values[2] = String.valueOf("原材料");
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
                    values[intFlag + 1] = "";
                    values[intFlag + 2] = bean.getWzbm();
                    intFlag = intFlag + 2;
                }
                intFlag++;
            }
            model.addRow(values);
            JTable jsclTable = technicsQuotaJSCLJPanel.getTable();
            int rowCount = jsclTable.getRowCount();
            setTabTitle(2, "金属材料", technicsJTabbedPane, rowCount );
        } else if (obj instanceof TMENonMetallicPartLinkBean) {
            TMENonMetallicPartLinkBean bean = (TMENonMetallicPartLinkBean) obj;
            Object[] values = new Object[nonMaterialMap.size() +12];
            values[0] = bean.getTechnicsmaterialentriesid();
            values[1] = false;
            values[2] = String.valueOf("原材料");
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
                    values[intFlag + 1] = "";
                    values[intFlag + 2] = bean.getWzbm();
                    intFlag = intFlag + 2;
                }
                intFlag++;
            }
            model.addRow(values);
            JTable fjsclTable = technicsQuotaFJSCLJPanel.getTable();
            int rowCount = fjsclTable.getRowCount();
            setTabTitle(3, "非金属材料", technicsJTabbedPane, rowCount );
        } else if (obj instanceof TMECompoundMaterialPartLinkBean) {
            TMECompoundMaterialPartLinkBean bean = (TMECompoundMaterialPartLinkBean) obj;
            Object[] values = new Object[compoundMap.size() + 12];
            values[0] = bean.getTechnicsmaterialentriesid();
            values[1] = false;
            values[2] = String.valueOf("主要材料");
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
                    values[intFlag + 1] = "";
                    values[intFlag + 2] = bean.getWzbm();
                    intFlag = intFlag + 2;
                }
                intFlag++;
            }
            model.addRow(values);
            JTable fhclTable = technicsQuotaFHCLJPanel.getTable();
            int rowCount = fhclTable.getRowCount();
            setTabTitle(4, "复合材料", technicsJTabbedPane, rowCount );

        } else if (obj instanceof TMEEleMachinePartLinkBean) {
            TMEEleMachinePartLinkBean bean = (TMEEleMachinePartLinkBean) obj;
            Object[] values = new Object[jdclMap.size() + 12];
            values[0] = bean.getTechnicsmaterialentriesid();
            values[1] =false;
            values[2] = String.valueOf("主要材料");
            int intFlag = 3;
            for (JComponent key : jdclMap.keySet()) {
                String s = jdclMap.get(key);
                Object getMethod = getGetMethod(bean, s);
                String value = "";
                if (getMethod == null) {
                    value = "";
                } else {
                    value = String.valueOf(getMethod);
                }
                values[intFlag] = value;
                if ("GG".equals(s)) {
                    values[intFlag + 1] = "";
                    intFlag = intFlag + 1;
                }
                if ("SCCJ".equals(s)) {
                    values[intFlag + 1] = bean.getWzbm();
                    intFlag = intFlag + 1;
                }
                intFlag++;
            }
            model.addRow(values);
            JTable jdclTable = technicsQuotaJDCLJPanel.getTable();
            int rowCount = jdclTable.getRowCount();
            setTabTitle(5, "机电材料", technicsJTabbedPane, rowCount );
        } else if (obj instanceof TMEExpDevicePartLinkBean) {
            TMEExpDevicePartLinkBean bean = (TMEExpDevicePartLinkBean) obj;
            Object[] values = new Object[hgpMap.size() + 12];
            values[0] = bean.getTechnicsmaterialentriesid();
            values[1] =false;
            values[2] = String.valueOf("主要材料");
            int intFlag = 3;
            for (JComponent key : hgpMap.keySet()) {
                String s = hgpMap.get(key);
                Object getMethod = getGetMethod(bean, s);
                String value = "";
                if (getMethod == null) {
                    value = "";
                } else {
                    value = String.valueOf(getMethod);
                }
                values[intFlag] = value;
                if ("BZH".equals(s)) {
                    values[intFlag + 1] = "";
                    intFlag = intFlag + 1;
                }
                if ("SCCJ".equals(s)) {
                    values[intFlag + 1] = bean.getWzbm();
                    intFlag = intFlag + 1;
                }
                intFlag++;
            }
            model.addRow(values);
            JTable hgpTable = technicsQuotaHGPJPanel.getTable();
            int rowCount = hgpTable.getRowCount();
            setTabTitle(6, "火工品", technicsJTabbedPane, rowCount);
        }
    }

    public void addRowInfo(List list, String type) {
        Object o = list.get(0);
        loadCurrentData(o, type);
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
}