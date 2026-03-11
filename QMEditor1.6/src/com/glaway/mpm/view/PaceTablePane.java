package com.glaway.mpm.view;

import com.faw_qm.speChar.speChar.SpeClassUtil;
import com.faw_qm.speChar.speChar.SpeIcon;
import com.faw_qm.speChar.view.MyUtilities;
import com.glaway.mpm.pdf.HtmlGenerator;
import com.glaway.mpm.pdf.PDFPreviewFactory;
import com.glaway.mpm.qmIntf.participatePart.ParticipatePartAddDialog;
import com.glaway.mpm.qmIntf.template.PaceSearchDialog;
import com.glaway.mpm.util.*;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.ResourceIntf;
import com.glaway.speciaword.common.CommonHelper;
import org.dom4j.Document;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.rmi.server.UID;
import java.util.List;
import java.util.*;

public class PaceTablePane extends NewLinkJPanel {
    private static final long serialVersionUID = 1L;

    private static VaLogger logger = VaLogger.getLogger(ResourceIntf.class);

    private NewTechnicsPart frame;
    private TechnicsStepJPanel_XW stepPanel;
    private boolean isEdit = true;

    private Element paces = null;

    private HashMap pacesCashe = new HashMap();

    protected JButton dynamicAssemblagePicture = new IconButton("/images/button_pic_review.png", "装配工具");

    private JButton batchCreateJButton = new IconButton("/images/paste.gif", "批量");
    //	private JButton customColumnButton = new IconButton("/images/paste.gif", "自定义列顺序");
    private JButton saveAsPaceTemlate = new IconButton("/images/save.gif", "存为工步模板");
    private JButton insertPaceTemlate = new IconButton("/images/insert.gif", "插入工步模板");
    private JButton previewPacePDF = new IconButton("/images/insert.gif", "PDF预览");
    public TechnicsPaceJDialog dia = null;
    private static final Font useFont = new Font("Dialog", 0, 16);
    private static JButton btn = new JButton();
    private static final FontMetrics metrics = btn.getFontMetrics(useFont);

    private Element techEle = null;

    private String imageFolder;

    public PaceTablePane(NewTechnicsPart fra, TechnicsStepJPanel_XW stepPanel) {
        super(stepPanel);
        this.frame = fra;
        this.stepPanel = stepPanel;
        this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.table.setCellSelectionEnabled(true);
        this.panel.add(this.batchCreateJButton, new GridBagConstraints(1, 0, 1,
                1, 1.0D, 0.0D, 10, 0, new Insets(5, 5, 0, 5), 0, 0));
//		this.panel.add(this.customColumnButton, new GridBagConstraints(1, 0, 1,
//				1, 1.0D, 0.0D, 10, 0, new Insets(5, 5, 0, 5), 0, 0));
        this.batchCreateJButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PaceTablePane.this.batchCreatePaces();
            }
        });

        this.panel.add(this.dynamicAssemblagePicture, new GridBagConstraints(1,
                6, 1, 1, 1.0D, 0.0D, 10, 0, new Insets(5, 5, 0, 5), 0, 0));

//		this.panel.add(this.customColumnButton, new GridBagConstraints(1,
//				7, 1, 1, 1.0D, 0.0D, 10, 0, new Insets(5, 5, 0, 5), 0, 0));
//		this.customColumnButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//
//				new ChangeColumnOrderDialog();
//			}
//		});
        this.dynamicAssemblagePicture.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PaceTablePane.this.dynamicAssemblagePicture();
            }
        });
        //this.table.getColumnModel().getColumn(0).setCellRenderer(new IsKeyRenderer());
        //this.table.getColumnModel().getColumn(0).setCellEditor(new IsKeyEditor(new JCheckBox()));

        this.panel.add(this.saveAsPaceTemlate, new GridBagConstraints(1,
                7, 1, 1, 1.0D, 0.0D, 10, 0, new Insets(5, 5, 0, 5), 0, 0));
        this.saveAsPaceTemlate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PaceTablePane.this.saveAsPaceTemlate();
            }
        });
        this.panel.add(this.insertPaceTemlate, new GridBagConstraints(1,
                8, 1, 1, 1.0D, 0.0D, 10, 0, new Insets(5, 5, 0, 5), 0, 0));
        this.insertPaceTemlate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PaceTablePane.this.insertPaceTemlate();
            }
        });

        this.panel.add(this.previewPacePDF, new GridBagConstraints(1,
                9, 1, 1, 1.0D, 0.0D, 10, 0, new Insets(5, 5, 0, 5), 0, 0));
        this.previewPacePDF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Element pace = getSelectedPaceElement();
                if(pace == null){
                    JOptionPane.showMessageDialog(frame,"请选择工步节点进行预览");
                    return;
                }
                PDFPreviewFactory.previewPace(pace);
            }
        });

        if("SOP".equals(com.glaway.mpm.EditorConfig.startType)){
            previewPacePDF.setVisible(true);
        }else{
            previewPacePDF.setVisible(false);
        }

        //第一列：控制点(G)
        this.table.getColumnModel().getColumn(0).setCellRenderer(new LabelRender());

        //第二列：工步号
        this.table.getColumnModel().getColumn(1).setCellRenderer(new LabelRender());
        this.table.getColumnModel().getColumn(1).setCellEditor(new StepNumberEditor(new JTextField()));

        //第三列：工步内容
//		this.table.getColumnModel().getColumn(2).setCellRenderer(new PaceContentRenderer());
//		this.table.getColumnModel().getColumn(2).setCellEditor(this.pce);
        this.table.getColumnModel().getColumn(2).setCellRenderer(new SWRenderer());
        this.table.getColumnModel().getColumn(2).setCellEditor(new SWEditor());

        //第四列：设备
        this.table.getColumnModel().getColumn(9).setCellRenderer(new PaceContentRenderer());

        //第五列：工装及工具
        this.table.getColumnModel().getColumn(8).setCellRenderer(new PaceContentRenderer());

        //第六列：参装件
        this.table.getColumnModel().getColumn(5).setCellRenderer(new PaceContentRenderer());

        //第七列：工艺辅料
        this.table.getColumnModel().getColumn(6).setCellRenderer(new PaceContentRenderer());

        //第八列：刀具
        this.table.getColumnModel().getColumn(7).setCellRenderer(new PaceContentRenderer());

        //第九列：标准仪器仪表
        this.table.getColumnModel().getColumn(10).setCellRenderer(new PaceContentRenderer());

        //第十列：非标准仪器仪表
        this.table.getColumnModel().getColumn(11).setCellRenderer(new PaceContentRenderer());

        //第十一列：量具
        this.table.getColumnModel().getColumn(12).setCellRenderer(new PaceContentRenderer());

        //第十二列：程序号
        this.table.getColumnModel().getColumn(4).setCellRenderer(new LabelRender());
        this.table.getColumnModel().getColumn(4).setCellEditor(new StepProgramNoEditor(new JTextField()));


        //第十三列：备注
        this.table.getColumnModel().getColumn(14).setCellRenderer(new LabelRender());
        this.table.getColumnModel().getColumn(14).setCellEditor(new StepBeiZhuEditor(new JTextField()));


        this.table.getTableHeader().setReorderingAllowed(false);
        this.table.setRowHeight(25);

        //隐藏列bosid
        setHiddenColumn(13);
        setHiddenColumn(0);
        //第十四列：装配检测结果
        this.table.getColumnModel().getColumn(3).setCellRenderer(new LabelRender());
        this.table.getColumnModel().getColumn(3).setCellEditor(new StepZpjcjgEditor(new JTextField()));
        this.table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                try {
                    if ((PaceTablePane.this.frame != null) && ((PaceTablePane.this.frame instanceof NewTechnicsPart))) {
                        NewTechnicsPart tp = (NewTechnicsPart) PaceTablePane.this.frame;

                        tp.getEpTreePanel().deleteObservers();
                        tp.getEpTreePanel().addObserver(PaceTablePane.this);

                        tp.getMeasurePanel().deleteObservers();
                        tp.getMeasurePanel().addObserver(PaceTablePane.this);

                        tp.getsDashboardPanel().deleteObservers();
                        tp.getsDashboardPanel().addObserver(PaceTablePane.this);

                        tp.getUnSDashboardPanel().deleteObservers();
                        tp.getUnSDashboardPanel().addObserver(PaceTablePane.this);

                        tp.getResourceTreePanel().deleteObservers();
                        tp.getResourceTreePanel().addObserver(PaceTablePane.this);

                        tp.getKtTreePanel().deleteObservers();
                        tp.getKtTreePanel().addObserver(PaceTablePane.this);

                        tp.getMTPanel().deleteObservers();
                        tp.getMTPanel().addObserver(PaceTablePane.this);

                        tp.getCSTreePanel().deleteObservers();
                        tp.getCSTreePanel().addObserver(PaceTablePane.this);

                        tp.getTechnicsTreePanel().deleteObservers();
                        tp.getTechnicsTreePanel().addObserver(PaceTablePane.this);
                    }

                    Point p = e.getPoint();
                    int row = PaceTablePane.this.table.rowAtPoint(p);
                    if ((row < 0) || (row >= PaceTablePane.this.table.getRowCount()))
                        return;
                    String num = (String) PaceTablePane.this.table.getValueAt(row, 13);
                    if (e.getClickCount() == 1) {
                        int columnIndex = PaceTablePane.this.table.columnAtPoint(p);

                        // 让特殊字符能更改单击选定的工步表行
                        int rowIndex = PaceTablePane.this.table.rowAtPoint(p);
                        //PaceTablePane.this.getPce().editingColumn = columnIndex;
                        //PaceTablePane.this.getPce().editingRow = rowIndex;
                        NewTechnicsPart tp = (NewTechnicsPart) PaceTablePane.this.frame;
                        if (columnIndex == 1) {
                            tp.getLeftTab().setSelectedComponent(tp.getTechnicsTreePanel());
                        }
                        if (columnIndex == 2) {
                            tp.getLeftTab().setSelectedComponent(tp.getCSTreePanel());
                        } else if (columnIndex == 9) {
                            tp.getLeftTab().setSelectedComponent(tp.getEpTreePanel());
                        } else if (columnIndex == 8) {
                            tp.getLeftTab().setSelectedComponent(tp.getResourceTreePanel());
                        } else if (columnIndex == 6) {
                            tp.getLeftTab().setSelectedComponent(tp.getMTPanel());
                        } else if (columnIndex == 7) {
                            tp.getLeftTab().setSelectedComponent(tp.getKtTreePanel());
                        } else if (columnIndex == 10) {
                            tp.getLeftTab().setSelectedComponent(tp.getsDashboardPanel());
                        } else if (columnIndex == 11) {
                            tp.getLeftTab().setSelectedComponent(tp.getUnSDashboardPanel());
                        } else if (columnIndex == 12) {
                            tp.getLeftTab().setSelectedComponent(tp.getMeasurePanel());
                        }

                        Element pace = (Element) PaceTablePane.this.pacesCashe.get(num);
                        if (pace != null) {
                            org.dom4j.Document doc = pace.getDocument();
                            if (doc != null) {
                                Element tech = XmlUtility.getTechnicsElement(doc);
                                String cortonaID = pace.attributeValue("cortonaID");
                                if ((cortonaID != null) && (cortonaID.trim().length() > 0)) {
                                    tp.show3DPane(cortonaID);
                                } else {
                                    String creoView = pace.attributeValue("creoView");
                                    if ((creoView != null) && (creoView.trim().length() > 0)) {
                                        Vector v = new Vector();
                                        tp.show2DPane(v);
                                    }
                                }
                            }
                        }
                    }
                    if (e.getClickCount() == 2) {
                        int columnIndex = PaceTablePane.this.table.columnAtPoint(p);
                        if (columnIndex == 0 || (columnIndex > 4)) {
                            PaceTablePane.this.stopTableCellEditing();
                            PaceTablePane.this.getElements();
                            Element pace = (Element) PaceTablePane.this.pacesCashe.get(num);
                            if ((pace != null) && (pace.getDocument() != null) && PaceTablePane.this.dia == null) {
                                PaceTablePane.this.dia = new TechnicsPaceJDialog(PaceTablePane.this.frame,
                                        PaceTablePane.this.stepPanel, pace.getParent().getParent(), pace, PaceTablePane.this, row);
                                PaceTablePane.this.dia.setUIEnabled(PaceTablePane.this.isEdit);
                                Element ele = PaceTablePane.this.dia.showDialog();
                                if (ele == null) {
                                    PaceTablePane.this.setOneRowTableValue(pace, row);
                                } else {
                                    PaceTablePane.this.addProcess(ele);
                                }
//                                PaceTablePane.this.dia = null;
                            }
                        }
                    }
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        });
        JTableHeader header = this.table.getTableHeader();
        header.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                PaceTablePane.this.table.setRowHeight(25);
            }
        });
        init();
    }

    protected void insertPaceTemlate() {
        Element paceElement = null;
        NewTechnicsPart np = (NewTechnicsPart) this.frame;
        org.dom4j.Document doc = np.getCurrentTechnics();
        Element techElement = XmlUtility.getTechnicsElement(doc);
        Element stepElement = this.stepPanel.getElement();
        String stepNum = stepElement.attributeValue("stepNumber");
        String technicsNumber = techElement.attributeValue("technicsNumber");
        String technicsFilePath = WorkSpaceUtil.getTechnicsPath(technicsNumber);
        String path = WorkSpaceUtil.getPaceRootPath();
        PaceSearchDialog dialog = new PaceSearchDialog(path, np);
        Vector vec = dialog.showDialog();
        if ((vec != null) && (vec.size() > 0)) {
            String templateName = (String) vec.get(0);
            String filepath = path + "\\" + templateName;
            String filetype = filepath.substring(filepath.length() - 4, filepath.length());
            if (".xml".equals(filetype)) {
                filepath = filepath.substring(0, filepath.length() - 4);
            }
            String xmlName = filepath + "\\" + templateName;
            File xmlFile = new File(xmlName);
            if (!xmlFile.exists()) {
                JOptionPane.showMessageDialog(this, "本地模板文件丢失！", "提示", 1);
                WorkSpaceUtil.delete(new File(filepath));
            } else {
                Document procedureTemplateDoc = XmlUtility.getDocument(xmlFile);
                if (procedureTemplateDoc != null) {
                    Element mainEle = procedureTemplateDoc.getRootElement();
                    if (mainEle != null) {
                        paceElement = (Element) mainEle.clone();
                        /**移除参装件信息*/
                        if (paceElement != null) {
                            Element parts = paceElement.element("parts");
                            if (parts != null) {
                                paceElement.remove(parts);
                            }
                        }
                        XmlUtility.setAttributeValue(paceElement, "bsoID", new UID().toString());
                        XmlUtility.setAttributeValue(paceElement, "stepNumber", String.valueOf(this.tableModel.getRowCount() + 1));
                        List<Element> stepList = XmlUtility.getAllSteps(techElement);
                        for (int i = 0; i < stepList.size(); i++) {
                            Element step = stepList.get(i);
                            if (step.attributeValue("stepNumber").equals(stepNum)) {
                                XmlUtility.addChildProcedure(step, paceElement);
                            }
                        }
                        try {
                            XmlUtility.saveDocument(techElement.getDocument(), new File(technicsFilePath));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        setOneRowTableValue(paceElement);
                    }
                }
            }
        }
    }

    protected void addModelColumn() {
        this.tableModel.addColumn("控制点、检验点、关键特性");
        this.tableModel.addColumn("工步号");
        this.tableModel.addColumn("工步内容");
        this.tableModel.addColumn("装配检测结果");
        this.tableModel.addColumn("程序号");
        this.tableModel.addColumn("参装件");
        this.tableModel.addColumn("工艺辅料");
        this.tableModel.addColumn("刀具");
        this.tableModel.addColumn("工装");
        this.tableModel.addColumn("设备");
        this.tableModel.addColumn("标准仪器仪表");
        this.tableModel.addColumn("非标准仪器仪表");
        this.tableModel.addColumn("量具");
        this.tableModel.addColumn("bsoID");
        this.tableModel.addColumn("备注");
    }

    public Vector<Element> getElements() {
        Vector result = new Vector();
        if (this.table.getRowCount() > 0) {
            for (int i = 0; i < this.table.getRowCount(); i++) {
                String num = (String) this.table.getValueAt(i, 13);
                Element ele = (Element) this.pacesCashe.get(num);
                if (ele != null)
                    result.add(ele.clone());
            }
        }
        List list = null;
        if(this.paces!=null){
        	list = this.paces.elements();
        }
        if (list != null)
            list.clear();
        for (int i = 0; i < result.size(); i++) {
            Element ele = (Element) result.get(i);
            this.paces.add(ele);
            String id = ele.attributeValue("bsoID");
            this.pacesCashe.put(id, ele);
        }

        return result;
    }

    private void init() {
        SwingUtil.setFreezeColumnSize(this.table.getColumnModel(), new int[]{0}, 60);
        SwingUtil.setFreezeColumnSize(this.table.getColumnModel(), new int[]{1}, 60);
        SwingUtil.setMinColumnSize(this.table.getColumnModel(), new int[]{2}, 400);
        SwingUtil.setMinColumnSize(this.table.getColumnModel(), new int[]{3, 4, 5, 6, 7, 8, 9, 10, 11, 12}, 80);
    }

    public String getAllEquips(Element pace) {
        String equips = "";
        Vector vec = new Vector();
        List l1 = XmlUtility.getEquips(pace).elements();
        if ((l1 != null) && (l1.size() > 0)) {
            for (int i = 0; i < l1.size(); i++) {
                Element eq = (Element) l1.get(i);
                String te1 = XmlUtility.getAttributeValue(eq, "number");
                String te2 = XmlUtility.getAttributeValue(eq, "name");
                String te = te1 + " " + te2;
                if (!vec.contains(te))
                    vec.add(te);
            }
        }
        for (int i = 0; i < vec.size(); i++) {
            String te = (String) vec.get(i);
            if (equips.trim().length() > 0)
                equips = equips + "\n";
            equips = equips + te;
        }
        return equips;
    }

    public String getAllMeasures(Element pace) {
        String equips = "";
        Vector vec = new Vector();
        List l1 = XmlUtility.getMeasures(pace).elements();
        if ((l1 != null) && (l1.size() > 0)) {
            for (int i = 0; i < l1.size(); i++) {
                Element eq = (Element) l1.get(i);
                String te1 = XmlUtility.getAttributeValue(eq, "number");
                String te2 = XmlUtility.getAttributeValue(eq, "name");
                String te = te1 + " " + te2;
                if (!vec.contains(te))
                    vec.add(te);
            }
        }
        for (int i = 0; i < vec.size(); i++) {
            String te = (String) vec.get(i);
            if (equips.trim().length() > 0)
                equips = equips + "\n";
            equips = equips + te;
        }
        return equips;
    }

    public String getAllDashboards(Element pace) {
        String equips = "";
        Vector vec = new Vector();
        List l1 = XmlUtility.getSDashboards(pace).elements();
        if ((l1 != null) && (l1.size() > 0)) {
            for (int i = 0; i < l1.size(); i++) {
                Element eq = (Element) l1.get(i);
                String te1 = XmlUtility.getAttributeValue(eq, "number");
                String te2 = XmlUtility.getAttributeValue(eq, "name");
                String te = te1 + " " + te2;
                if (!vec.contains(te))
                    vec.add(te);
            }
        }
        for (int i = 0; i < vec.size(); i++) {
            String te = (String) vec.get(i);
            if (equips.trim().length() > 0)
                equips = equips + "\n";
            equips = equips + te;
        }
        return equips;
    }

    public String getAllUnSDashboards(Element pace) {
        String equips = "";
        Vector vec = new Vector();
        List l1 = XmlUtility.getUnsdashboards(pace).elements();
        if ((l1 != null) && (l1.size() > 0)) {
            for (int i = 0; i < l1.size(); i++) {
                Element eq = (Element) l1.get(i);
                String te1 = XmlUtility.getAttributeValue(eq, "number");
                String te2 = XmlUtility.getAttributeValue(eq, "name");
                String te = te1 + " " + te2;
                if (!vec.contains(te))
                    vec.add(te);
            }
        }
        for (int i = 0; i < vec.size(); i++) {
            String te = (String) vec.get(i);
            if (equips.trim().length() > 0)
                equips = equips + "\n";
            equips = equips + te;
        }
        return equips;
    }

    public String getAllTools(Element pace) {
        String tools = "";
        Vector vec = new Vector();
        List l1 = XmlUtility.getTools(pace).elements();
        if ((l1 != null) && (l1.size() > 0)) {
            for (int i = 0; i < l1.size(); i++) {
                Element tool = (Element) l1.get(i);
                String te1 = XmlUtility.getAttributeValue(tool, "toolNum");
                String te2 = XmlUtility.getAttributeValue(tool, "toolName");
                String te = te1 + " " + te2;
                if (!vec.contains(te))
                    vec.add(te);
            }
        }
        for (int i = 0; i < vec.size(); i++) {
            String te = (String) vec.get(i);
            if (tools.trim().length() > 0)
                tools = tools + "\n";
            tools = tools + te;
        }
        return tools;
    }

    public String getAllKnifeTools(Element pace) {
        String tools = "";
        Vector vec = new Vector();
        List l1 = XmlUtility.getKnifeTools(pace).elements();
        if ((l1 != null) && (l1.size() > 0)) {
            for (int i = 0; i < l1.size(); i++) {
                Element tool = (Element) l1.get(i);
                String te1 = XmlUtility.getAttributeValue(tool, "toolName");
                String te2 = XmlUtility.getAttributeValue(tool, "knifetype");
                String te3 = XmlUtility.getAttributeValue(tool, "rkzj");
                String te = te1;
                if ((null != te2) && (!"".equals(te2))) {
                    te = te2;
                }
                if (te3 != null && !"".equals(te3)) {
                    te = te + "_" + te3;
                }

                if (!vec.contains(te))
                    vec.add(te);
            }
        }
        for (int i = 0; i < vec.size(); i++) {
            String te = (String) vec.get(i);
            if (tools.trim().length() > 0)
                tools = tools + "\n";
            tools = tools + te;
        }
        return tools;
    }

    public String getAllMaterials(Element pace) {
        String materials = "";
        Vector vec = new Vector();
        List l1 = XmlUtility.getMaterials(pace).elements();
        if ((l1 != null) && (l1.size() > 0)) {
            for (int i = 0; i < l1.size(); i++) {
                Element mal = (Element) l1.get(i);
                String te1 = XmlUtility.getAttributeValue(mal, "materialNumber");
                String te2 = XmlUtility.getAttributeValue(mal, "materialName");
                String te = te1 + " " + te2;
                if (!vec.contains(te))
                    vec.add(te);
            }
        }
        for (int i = 0; i < vec.size(); i++) {
            String te = (String) vec.get(i);
            if (materials.trim().length() > 0)
                materials = materials + "\n";
            materials = materials + te;
        }
        return materials;
    }

    public String getAllParts(Element pace) {
        String part = "";
        Vector vec = new Vector();
        Vector cashe = new Vector();
        List l1 = XmlUtility.getParts(pace).elements();
        Map<String, Integer> czjMap = new HashMap<String, Integer>();
        if ((l1 != null) && (l1.size() > 0)) {
            int useCount;
            for (int i = 0; i < l1.size(); i++) {
                Element pt = (Element) l1.get(i);
                String te = XmlUtility.getAttributeValue(pt, "partNumber");
                String partName = XmlUtility.getAttributeValue(pt, "partName");
                String GG = XmlUtility.getAttributeValue(pt, "GG");
                String XHPH = XmlUtility.getAttributeValue(pt, "XHPH");
                String MTYPE = XmlUtility.getAttributeValue(pt, "MTYPE");
                String bzh = XmlUtility.getAttributeValue(pt, "bzh");
                String dataType = XmlUtility.getAttributeValue(pt, "dataType");
                if (!"自制件".equals(MTYPE)) {
                    if ("标准件".equals(dataType)) {
                        te = partName + "(" + GG + "、" + bzh + ")";
                    } else {
                        te = partName + "(" + GG + "、" + XHPH + ")";
                    }
                }
                if (czjMap.containsKey(te)) {
                    useCount = czjMap.get(te);
                    useCount++;
                    czjMap.put(te, useCount);
                } else {
                    czjMap.put(te, 1);
                }
//                String count = XmlUtility.getAttributeValue(pt, "useCount");
//                if ((count == null) || (count.trim().length() == 0))
//                    count = "1";
//                if (!vec.contains(te)) {
//                    vec.add(te);
//                    if ((count != null) && (count.trim().length() > 0) && (!count.equals("1")))
//                        cashe.add(te + " x " + count);
//                    else
//                        cashe.add(te);
//                }
            }
        }
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Integer> entry : czjMap.entrySet()) {
            if (entry.getValue() == 1) {
                sb.append(entry.getKey() + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            } else {
                sb.append(entry.getKey() + "*" + entry.getValue() + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            }
        }
//        for (int i = 0; i < cashe.size(); i++) {
//            String te = (String) cashe.get(i);
//            if (part.trim().length() > 0)
//                part = part + "\r\n";
//            part = part + te;
//        }
        logger.debug("part====" + sb.toString());
        return sb.toString();
    }

    public void setOneRowTableValue(Element element) {
        if ((element != null) && (element.getName().equals("QMProcedureInfo"))) {
            String isKey = XmlUtility.getAttributeValue(element, "isKey");
            String paceNum = XmlUtility.getAttributeValue(element, "stepNumber");
            String progamrNo = XmlUtility.getAttributeValue(element, "programNo");
            String ZPJCJG = XmlUtility.getAttributeValue(element, "ZPJCJG");
            if (ZPJCJG == null) {
                ZPJCJG = "";
            }
            if (progamrNo == null) {
                progamrNo = "";
            }
            String gbbz = XmlUtility.getAttributeValue(element, "gbbz");
            if (gbbz == null) {
                gbbz = "";
            }
            String paceContent = XmlUtility.getProcedureContent(element);
            paceContent = CommonHelper.replaceReadSeperator(paceContent, getImageFolder());
            String shop = XmlUtility.getAttributeValue(element, "workShop");
            String type = XmlUtility.getAttributeValue(element, "workType");
            String bsoID = XmlUtility.getAttributeValue(element, "bsoID");
            if ((bsoID == null) || (bsoID.trim().length() == 0)) {
                bsoID = new UID().toString();
                XmlUtility.setAttributeValue(element, "bsoID", bsoID);
            }
            this.pacesCashe.put(bsoID, element);

            String equips = getAllEquips(element);
            String measures = getAllMeasures(element);
            String tools = getAllTools(element);
            String knifeTools = getAllKnifeTools(element);
            String materials = getAllMaterials(element);
            String parts = getAllParts(element);
            String dashboards = getAllDashboards(element);
            String unsdashboards = getAllUnSDashboards(element);
            super.addProcess();
            int i = this.tableModel.getRowCount();
            i--;
            this.table.setRowHeight(i, 25);
            this.tableModel.setValueAt("", i, 0);
            this.tableModel.setValueAt(paceNum, i, 1);
            this.tableModel.setValueAt(paceContent, i, 2);
            this.tableModel.setValueAt(equips, i, 9);
            this.tableModel.setValueAt(tools, i, 8);
            this.tableModel.setValueAt(parts, i, 5);
            this.tableModel.setValueAt(materials, i, 6);
            this.tableModel.setValueAt(knifeTools, i, 7);
            this.tableModel.setValueAt(dashboards, i, 10);
            this.tableModel.setValueAt(unsdashboards, i, 11);
            this.tableModel.setValueAt(measures, i, 12);
            this.tableModel.setValueAt(progamrNo, i, 4);
            this.tableModel.setValueAt(gbbz, i, 14);
            this.tableModel.setValueAt(bsoID, i, 13);
            this.tableModel.setValueAt(ZPJCJG, i, 3);
        }
    }

    public void setPartTableValue(Element element) {
        if ((element != null) && (element.getName().equals("QMProcedureInfo"))) {
            String parts = getAllParts(element);
            super.addProcess();
            int i = this.tableModel.getRowCount();
            i--;
            logger.debug("i= " + i);
            this.tableModel.setValueAt(parts, i, 5);
        }
    }

    public void setOneRowTableValue(Element element, int row) {
        if ((row < 0) || (row >= this.table.getRowCount()))
            return;
        if ((element != null) && (element.getName().equals("QMProcedureInfo"))) {
            String isKey = XmlUtility.getAttributeValue(element, "isKey");
            String paceNum = XmlUtility.getAttributeValue(element, "stepNumber");
            String progamrNo = XmlUtility.getAttributeValue(element, "programNo");
            String ZPJCJG = XmlUtility.getAttributeValue(element, "ZPJCJG");
            if (ZPJCJG == null) {
                ZPJCJG = "";
            }
            if (progamrNo == null) {
                progamrNo = "";
            }
            String gbbz = XmlUtility.getAttributeValue(element, "gbbz");
            if (gbbz == null) {
                gbbz = "";
            }
            String paceContent = XmlUtility.getProcedureContent(element);
            paceContent = CommonHelper.replaceReadSeperator(paceContent, getImageFolder());
            String shop = XmlUtility.getAttributeValue(element, "workShop");
            String type = XmlUtility.getAttributeValue(element, "workType");
            String bsoID = XmlUtility.getAttributeValue(element, "bsoID");
            if ((bsoID == null) || (bsoID.trim().length() == 0)) {
                bsoID = new UID().toString();
                XmlUtility.setAttributeValue(element, "bsoID", bsoID);
            }
            this.pacesCashe.put(bsoID, element);

            String equips = getAllEquips(element);
            String measures = getAllMeasures(element);
            String tools = getAllTools(element);
            String knifeTools = getAllKnifeTools(element);
            String materials = getAllMaterials(element);
            String parts = getAllParts(element);
            String dashboards = getAllDashboards(element);
            String unsdashboards = getAllUnSDashboards(element);

            this.tableModel.setValueAt(isKey, row, 0);
            this.tableModel.setValueAt(paceNum, row, 1);
            this.tableModel.setValueAt(paceContent, row, 2);
            this.tableModel.setValueAt(equips, row, 9);
            this.tableModel.setValueAt(tools, row, 8);
            this.tableModel.setValueAt(parts, row, 5);
            this.tableModel.setValueAt(materials, row, 6);
            this.tableModel.setValueAt(knifeTools, row, 7);
            this.tableModel.setValueAt(dashboards, row, 10);
            this.tableModel.setValueAt(unsdashboards, row, 11);
            this.tableModel.setValueAt(measures, row, 12);
            this.tableModel.setValueAt(progamrNo, row, 4);
            this.tableModel.setValueAt(gbbz, row, 14);
            this.tableModel.setValueAt(bsoID, row, 13);
            this.tableModel.setValueAt(ZPJCJG, row, 3);

        }
    }

    public void setTableValues(Vector<Element> vec) {
        this.pacesCashe.clear();
        clearTable();
        for (int i = 0; i < vec.size(); i++) {
            Element element = (Element) vec.get(i);
            setOneRowTableValue(element);
        }
    }

    public void setPartValues(Vector<Element> vec) {
        this.pacesCashe.clear();
        clearTable();
        for (int i = 0; i < vec.size(); i++) {
            Element element = (Element) vec.get(i);
            setPartTableValue(element);
        }
    }

    //备用setTableValues
    public void setTableValues(Vector<Element> vec, int selectIndex) {
        setTableValues(vec);
        this.table.setRowSelectionInterval(selectIndex, selectIndex);
    }

    public void displayPaceDatas() {
        int select = this.table.getSelectedRow();
        if (this.paces != null) {
            List list = this.paces.elements();
            if ((list != null) && (list.size() > 0)) {
                int num = 1;
                for (int i = 0; i < list.size(); i++) {
                    Element pace = (Element) list.get(i);
                    String temp = XmlUtility.getAttributeValue(pace, "stepNumber");
                    Vector cashe = XmlUtility.getPaceNumberCashe();
                    if ((temp != null) && (!cashe.contains(temp))) {
                        XmlUtility.setAttributeValue(pace, "stepNumber", "" + num);
                        num++;
                    }
                }
                Vector vec = new Vector();
                vec.addAll(list);
                setTableValues(vec);
            } else {
                clearTable();
            }
        }
        if ((select >= 0) && (select < this.table.getRowCount())) {
            this.table.setRowSelectionInterval(select, select);
        }

        repaint();
    }

    public void setPaces(Element paces) {
        try {
            this.paces = paces;
            if ((this.frame != null) && ((this.frame instanceof NewTechnicsPart))) {
                NewTechnicsPart np = (NewTechnicsPart) this.frame;
                org.dom4j.Document doc = np.getCurrentTechnics();
                if (doc != null) {
                    Element tech = XmlUtility.getTechnicsElement(doc);
                    String technicsType = tech.attributeValue("technicsType");
                    if ((technicsType != null) && (technicsType.equals("零件工艺"))) {
                        this.table.getTableHeader().getColumnModel().getColumn(5).setMaxWidth(0);
                        this.table.getTableHeader().getColumnModel().getColumn(5).setMinWidth(0);
                        this.table.getColumnModel().getColumn(5).setMaxWidth(0);
                        this.table.getColumnModel().getColumn(5).setPreferredWidth(0);
                        this.table.getColumnModel().getColumn(5).setWidth(0);
                        this.table.getColumnModel().getColumn(5).setMinWidth(0);
                        this.dynamicAssemblagePicture.setVisible(false);
                    } else {
                        this.table.getTableHeader().getColumnModel().getColumn(5).setMaxWidth(2147483647);
                        this.table.getTableHeader().getColumnModel().getColumn(5).setMinWidth(200);
                        this.table.getColumnModel().getColumn(5).setMaxWidth(2147483647);
                        this.table.getColumnModel().getColumn(5).setPreferredWidth(200);
                        this.table.getColumnModel().getColumn(5).setWidth(200);
                        this.table.getColumnModel().getColumn(5).setMinWidth(200);
                        this.table.getColumnModel().getColumn(5).setCellRenderer(new SWRenderer());
                        this.dynamicAssemblagePicture.setVisible(true);
                    }
                }
            }
        } catch (Exception localException) {
        }
    }

    protected void removeProcess() {
        int[] rows = this.table.getSelectedRows();
        for (int i = 0; i < rows.length; i++) {
            String num = (String) this.tableModel.getValueAt(rows[i], 13);
            Element ele = (Element) this.pacesCashe.get(num);
            if (ele != null) {
                this.pacesCashe.remove(num);
                if (ele.getParent() != null) {
                    Element parent = ele.getParent();
                    parent.remove(ele);
                }
            }
        }
        super.removeProcess();
    }

    public Element getPaces() {
        return this.paces;
    }

    public void addProcess() {
        Element ele = XmlUtility.createProcedure();
        XmlUtility.setAttributeValue(ele, "stepNumber", "1");
        XmlUtility.setAttributeValue(ele, "bsoID", new UID().toString());
        addProcess(ele);
    }

    public void addProcess(Element ele) {
        String group = TechnicsUtil.getGroupName(NewTechnicsPart.getPbomOid());
        group = TechnicsUtil.getGroupValue(group);
        if (ele != null) {
            getElements();
            int select = this.table.getSelectedRow();
            List list = this.paces.elements();
            int end = list.size() - 1;
            super.addProcess();
            if ((select == end) || (select == -1)) {
                select = end;
                list.add(ele);
            } else {
                list.add(select + 1, ele);
                this.tableModel.moveRow(select + 1, end, select + 2);
            }

            this.table.setRowHeight(select + 1, 25);
            setOneRowTableValue(ele, select + 1);
            this.pacesCashe.put(ele.attribute("bsoID"), ele);

            int num = 1;
            for (int i = 0; i < this.table.getRowCount(); i++) {
                String id = (String) this.table.getValueAt(i, 13);
                Element pace = (Element) this.pacesCashe.get(id);
                String temp = XmlUtility.getAttributeValue(pace, "stepNumber");
                Vector cashe = XmlUtility.getPaceNumberCashe();
                if ((temp != null) && (!cashe.contains(temp))) {
                    XmlUtility.setAttributeValue(pace, "stepNumber", "" + num);
                    this.table.setValueAt("" + num, i, 1);
                    //this.table.setValueAt(group, i, 3);
                    num++;
                }
            }

            this.table.setRowSelectionInterval(select + 1, select + 1);
            this.table.scrollRectToVisible(this.table.getCellRect(select + 1, 0, true));
        }
        repaint();
    }

    public void setUIEnabled(boolean b) {
        this.isEdit = b;
        super.setUIEnabled(b);
        this.dynamicAssemblagePicture.setEnabled(b);
        this.batchCreateJButton.setEnabled(b);
        this.saveAsPaceTemlate.setEnabled(b);
        this.insertPaceTemlate.setEnabled(b);
//		this.customColumnButton.setEnabled(b);
    }

    public void update(Observable o, Object arg) {
        if (!isEnabled()) {
            return;
        }
        if ((o != null) && ((o instanceof CommonObservable))) {
            CommonObservable co = (CommonObservable) o;
            int type = co.getType();
            if (arg != null) {
                logger.debug("ARG=====" + arg + "===type====" + type);
                int[] select = this.table.getSelectedRows();
                if ((select != null) && (select.length == 1)) {
                    int row = select[0];
                    if ((row >= 0) && (row < this.table.getRowCount())) {
                        String num = (String) this.tableModel.getValueAt(row, 13);
                        if ((num != null) && (num.trim().length() > 0)) {
                            Element pace = (Element) this.pacesCashe.get(num);
                            if (pace != null) {
                                if (type == 3) { //TODO 149修改材料
                                    if ((arg instanceof Map)) {
                                        Map m = (Map) arg;
                                        String oid = (String) m.get("oid");
                                        if (oid != null) {
                                            Element material = XmlUtility.getMaterial(pace, oid);
                                            if (material == null) {
                                                material = XmlUtility.createMaterial();
                                                XmlUtility.setAttributeValue(material, "oid", (String) m.get("oid"));
                                                XmlUtility.setAttributeValue(material, "materialNumber", (String) m.get("materialNumber"));
                                                XmlUtility.setAttributeValue(material, "materialName", (String) m.get("materialName"));
                                                XmlUtility.setAttributeValue(material, "mindex", (String) m.get("mindex"));
                                                XmlUtility.setAttributeValue(material, "csize", (String) m.get("csize"));
                                                XmlUtility.setAttributeValue(material, "jstj", (String) m.get("jstj"));
                                                XmlUtility.setAttributeValue(material, "jldw", (String) m.get("jldw"));
                                                XmlUtility.setAttributeValue(material, "fjtj", (String) m.get("fjtj"));
                                                XmlUtility.setAttributeValue(material, "sl", "1");
                                                XmlUtility.addMaterial(pace, material);
                                                setOneRowTableValue(pace, row);
                                                this.table.scrollRectToVisible(this.table.getCellRect(row, 0, true));
                                                repaint();
                                            }
                                        }
                                    }
                                } else if (type == 0) {
                                    logger.debug("commonString=====" + arg);
                                    if ((arg instanceof String)) {
                                        this.table.requestFocus();
                                        String commonString = (String) arg;
                                        int editingColumn = table.getSelectedColumn();
                                        int editingRow = table.getSelectedRow();
                                        if (editingRow != -1) {
                                            boolean flag = table.editCellAt(editingRow, 2);
                                            TableCellEditor editor = table.getCellEditor();
                                            if (editor instanceof SWEditor) {
                                                ((SWEditor) editor).insertText(commonString);
                                            }
                                        }
                                    }
                                } else if (type == 2) {
                                    if ((arg instanceof Map)) {
                                        Map m = (Map) arg;
                                        String oid = (String) m.get("oid");
                                        if (oid != null) {
                                            Element tool = XmlUtility.getTool(pace, oid);
                                            if (tool == null) {
                                                tool = XmlUtility.createTool();
                                                XmlUtility.setAttributeValue(tool, "toolNum", (String) m.get("toolNum"));
                                                XmlUtility.setAttributeValue(tool, "toolName", (String) m.get("toolName"));
                                                XmlUtility.setAttributeValue(tool, "frockType", (String) m.get("frockType"));
                                                XmlUtility.setAttributeValue(tool, "csize", (String) m.get("csize"));
                                                XmlUtility.setAttributeValue(tool, "mindex", (String) m.get("mindex"));
                                                XmlUtility.setAttributeValue(tool, "oid", (String) m.get("oid"));
                                                XmlUtility.addTool(pace, tool);
                                                setOneRowTableValue(pace, row);
                                                this.table.scrollRectToVisible(this.table.getCellRect(row, 0, true));
                                                repaint();
                                            }
                                        }
                                    }
                                } else if (type == 1) {
                                    if ((arg instanceof Map)) {
                                        Map m = (Map) arg;
                                        String oid = (String) m.get("oid");
                                        if (oid != null) {
                                            Element equip = XmlUtility.getEquip(pace, oid);
                                            if (equip == null) {
                                                equip = XmlUtility.createEquip();
                                                XmlUtility.setAttributeValue(equip, "number", (String) m.get("number"));
                                                XmlUtility.setAttributeValue(equip, "name", (String) m.get("name"));
                                                XmlUtility.setAttributeValue(equip, "equipmentType", (String) m.get("equipmentType"));
                                                XmlUtility.setAttributeValue(equip, "csize", (String) m.get("csize"));
                                                XmlUtility.setAttributeValue(equip, "pindex", (String) m.get("mindex"));
                                                XmlUtility.setAttributeValue(equip, "oid", (String) m.get("oid"));
                                                XmlUtility.addEquip(pace, equip);
                                                setOneRowTableValue(pace, row);
                                                this.table.scrollRectToVisible(this.table.getCellRect(row, 0, true));
                                                repaint();
                                            }
                                        }
                                    }
                                } else if (type == 4) {
                                    if ((arg instanceof Map)) {
                                        Map m = (Map) arg;
                                        String oid = (String) m.get("oid");
                                        if (oid != null) {
                                            Element tool = XmlUtility.getKnifeTool(pace, oid);
                                            if (tool == null) {
                                                tool = XmlUtility.createKnifeTool();
                                                XmlUtility.setAttributeValue(tool, "toolNum", (String) m.get("toolNum"));
                                                XmlUtility.setAttributeValue(tool, "toolName", (String) m.get("toolName"));
                                                XmlUtility.setAttributeValue(tool, "knifetype", (String) m.get("knifetype"));
                                                XmlUtility.setAttributeValue(tool, "cmat", (String) m.get("cmat"));
                                                XmlUtility.setAttributeValue(tool, "rkzj", (String) m.get("rkzj"));
                                                XmlUtility.setAttributeValue(tool, "jczj", (String) m.get("jczj"));
                                                XmlUtility.setAttributeValue(tool, "rkcd", (String) m.get("rkcd"));
                                                XmlUtility.setAttributeValue(tool, "zcd", (String) m.get("zcd"));
                                                XmlUtility.setAttributeValue(tool, "gc", (String) m.get("gc"));
                                                XmlUtility.setAttributeValue(tool, "zxjgcc", (String) m.get("zxjgcc"));
                                                XmlUtility.setAttributeValue(tool, "zdjgcc", (String) m.get("zdjgcc"));
                                                XmlUtility.setAttributeValue(tool, "jgxs", (String) m.get("jgxs"));
                                                XmlUtility.setAttributeValue(tool, "jklx", (String) m.get("jklx"));
                                                XmlUtility.setAttributeValue(tool, "jsbz", (String) m.get("jsbz"));
                                                XmlUtility.setAttributeValue(tool, "rkyjbj", (String) m.get("rkyjbj"));
                                                XmlUtility.setAttributeValue(tool, "cs", (String) m.get("cs"));
                                                XmlUtility.setAttributeValue(tool, "oid", (String) m.get("oid"));
                                                XmlUtility.addKnifeTool(pace, tool);
                                                setOneRowTableValue(pace, row);
                                                this.table.scrollRectToVisible(this.table.getCellRect(row, 0, true));
                                                repaint();
                                            }
                                        }
                                    }
                                } else if (type == 5) {
                                    if ((arg instanceof Map)) {
                                        Map m = (Map) arg;
                                        String oid = (String) m.get("oid");
                                        if (oid != null) {
                                            Element dashboard = XmlUtility.getSDashboard(pace, oid);
                                            if (dashboard == null) {
                                                dashboard = XmlUtility.createSDashboard();
                                                XmlUtility.setAttributeValue(dashboard, "number", (String) m.get("number"));
                                                XmlUtility.setAttributeValue(dashboard, "name", (String) m.get("name"));
                                                XmlUtility.setAttributeValue(dashboard, "equipmentType", (String) m.get("equipmentType"));
                                                XmlUtility.setAttributeValue(dashboard, "csize", (String) m.get("csize"));
                                                XmlUtility.setAttributeValue(dashboard, "pindex", (String) m.get("mindex"));
                                                XmlUtility.setAttributeValue(dashboard, "oid", (String) m.get("oid"));
                                                XmlUtility.addSDashboard(pace, dashboard);
                                                setOneRowTableValue(pace, row);
                                                this.table.scrollRectToVisible(this.table.getCellRect(row, 0, true));
                                                repaint();
                                            }
                                        }
                                    }
                                } else if (type == 6) {
                                    if ((arg instanceof Map)) {
                                        Map m = (Map) arg;
                                        String oid = (String) m.get("oid");
                                        if (oid != null) {
                                            Element dashboard = XmlUtility.getUnSDashboard(pace, oid);
                                            if (dashboard == null) {
                                                dashboard = XmlUtility.createUnSDashboard();
                                                XmlUtility.setAttributeValue(dashboard, "number", (String) m.get("number"));
                                                XmlUtility.setAttributeValue(dashboard, "name", (String) m.get("name"));
                                                XmlUtility.setAttributeValue(dashboard, "equipmentType", (String) m.get("equipmentType"));
                                                XmlUtility.setAttributeValue(dashboard, "csize", (String) m.get("csize"));
                                                XmlUtility.setAttributeValue(dashboard, "pindex", (String) m.get("mindex"));
                                                XmlUtility.setAttributeValue(dashboard, "oid", (String) m.get("oid"));
                                                XmlUtility.addUnsdashboard(pace, dashboard);
                                                setOneRowTableValue(pace, row);
                                                this.table.scrollRectToVisible(this.table.getCellRect(row, 0, true));
                                                repaint();
                                            }
                                        }
                                    }
                                } else if (type == 7) {
                                    if ((arg instanceof Map)) {
                                        Map m = (Map) arg;
                                        String oid = (String) m.get("oid");
                                        if (oid != null) {
                                            Element measure = XmlUtility.getMeasure(pace, oid);
                                            if (measure == null) {
                                                measure = XmlUtility.createMeasure();
                                                XmlUtility.setAttributeValue(measure, "number", (String) m.get("toolNum"));
                                                XmlUtility.setAttributeValue(measure, "name", (String) m.get("toolName"));
                                                XmlUtility.setAttributeValue(measure, "csize", (String) m.get("csize"));
                                                XmlUtility.setAttributeValue(measure, "pindex", (String) m.get("mindex"));
                                                XmlUtility.setAttributeValue(measure, "oid", (String) m.get("oid"));
                                                XmlUtility.addMeasure(pace, measure);
                                                setOneRowTableValue(pace, row);
                                                this.table.scrollRectToVisible(this.table.getCellRect(row, 0, true));
                                                repaint();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean hasKeyPace() {
        if (this.table.getRowCount() > 0) {
            stopTableCellEditing();
            for (int i = 0; i < this.table.getRowCount(); i++) {
                String bool = (String) this.table.getValueAt(i, 0);
                if ((bool != null) && (bool.trim().length() > 0)) {
                    if (bool.equalsIgnoreCase("true"))
                        return true;
                }
            }
        }
        return false;
    }

    public String getKey(Map map, String value) {
        if ((map == null) || (value == null))
            return "NOKEY";
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            Object key = it.next();
            Object temp = map.get(key);
            if (temp.equals(value)) {
                return key.toString();
            }
        }
        return "NOKEY";
    }

    /**
     * 装配工具
     */
    public void dynamicAssemblagePicture() {
        try {
            if ((this.frame != null) && ((this.frame instanceof NewTechnicsPart))) {
                stopTableCellEditing();
                getElements();
                NewTechnicsPart np = (NewTechnicsPart) this.frame;
                np.saveProcess(this.paces);

                org.dom4j.Document doc = np.getCurrentTechnics();
                if (doc != null) {
                    Element tech = XmlUtility.getTechnicsElement(doc);
                    String poid = tech.attributeValue("parentPartOid");
                    String ppartNumber = tech.attributeValue("parentPartNumber");
                    String partOid = tech.attributeValue("partOid");
                    String partNumber = tech.attributeValue("partNumber");
                    String technicsCategory = tech.attributeValue("technicsCategory");
                    String technicsNumber = tech.attributeValue("technicsNumber");
                    String technicsName = tech.attributeValue("technicsName");
                    String filePath = "";
                    if ("rework".equals(technicsCategory)) {
                        filePath = WorkSpaceUtil.getReWorkTechnicsPathByTechnicsName(technicsNumber, technicsName);
                    } else if ("temp".equals(technicsCategory)) {
                        filePath = WorkSpaceUtil.getTempTechnicsPathByTechnicsName(technicsNumber, technicsName);
                    } else {
                        filePath = WorkSpaceUtil.getTechnicsPath(technicsNumber);
                    }

                    String occId = tech.attributeValue("occId");
                    String partName = tech.attributeValue("partName");
                    String material = tech.attributeValue("material");
                    String dutu = tech.attributeValue("dutu");
                    String remark = tech.attributeValue("remark");
                    String useCount = tech.attributeValue("useCount");
                    Map map = new HashMap();
                    map.put("poid", poid);
                    map.put("ppartNumber", ppartNumber);
                    map.put("partOid", partOid);
                    map.put("partNumber", partNumber);
                    map.put("xmlPath", filePath);
                    map.put("occId", occId);
                    map.put("partName", partName);
                    map.put("material", material);
                    map.put("dutu", dutu);
                    map.put("remark", remark);
                    map.put("useCount", useCount);
                    ParticipatePartAddDialog.showDialog(map, np);
                }
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }


    protected void saveAsPaceTemlate() {
        Element pace = getSelectedPaceElement();
        if (pace != null && pace.getDocument() != null) {
            int index = this.table.getSelectedRow();
            SaveAsPaceTemplateDialog sptd = new SaveAsPaceTemplateDialog(frame, pace, index);
            sptd.showDialog();
        }
    }

    protected void changeRow() {
        if ((this.frame != null) && ((this.frame instanceof NewTechnicsPart))) {
            NewTechnicsPart tp = (NewTechnicsPart) this.frame;
            tp.getUniversalToolBar().setAssemblageEnabled(this.table);
        }
    }

    public void batchCreatePaces() {
        try {
            NumberInputDialog nid = new NumberInputDialog(this.frame, false);
            int count = nid.showDialog();
            for (int i = 0; i < count; i++) {
                addProcess();
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    public Element getSelectedPaceElement() {
        Element pace = null;
        int index = this.table.getSelectedRow();
        if ((index >= 0) && (index < this.table.getRowCount())) {
            String bsoID = (String) this.table.getValueAt(index, 13);
            if (bsoID != null) {
                pace = (Element) this.pacesCashe.get(bsoID);
            }
        }

        return pace;
    }

    public void refreshPartDatas() {
        for (int i = 0; i < this.table.getRowCount(); i++) {
            String bsoID = (String) this.table.getValueAt(i, 13);
            if (bsoID != null) {
                Element pace = (Element) this.pacesCashe.get(bsoID);
                String parts = getAllParts(pace);
                this.table.setValueAt(parts, i, 5);
            }
        }
        if (this.dia != null) {
            this.dia.refreshPartDatas();
        }
    }

    class IsKeyEditor extends DefaultCellEditor {
        JPanel panel = null;
        JCheckBox checkBox = null;
        private JTable editingTable = null;
        private int editingRow = -1;
        private int editingColumn = -1;

        IsKeyEditor(JCheckBox box) {
            super(box);
            this.checkBox = box;
            this.panel = new JPanel();
            this.panel.setLayout(new GridBagLayout());
            this.panel.add(this.checkBox, new GridBagConstraints(0, 0, 1, 1,
                    0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
        }

        public Component getTableCellEditorComponent(JTable table,
                                                     Object value, boolean isSelected, int row, int column) {
            this.editingTable = table;
            this.editingRow = row;
            this.editingColumn = column;
            this.checkBox.setSelected(false);
            if ((value != null) && ((value instanceof String))) {
                if (value.toString().equalsIgnoreCase("true")) {
                    this.checkBox.setSelected(true);
                }
            }
            return this.panel;
        }

        public Object getCellEditorValue() {
            boolean bool = this.checkBox.isSelected();

            if (this.editingTable != null) {
                if ((this.editingRow >= 0)
                        && (this.editingRow < this.editingTable.getRowCount())
                        && (this.editingColumn >= 0)
                        && (this.editingColumn < this.editingTable.getColumnCount())) {
                    this.editingTable.setValueAt(String.valueOf(bool), this.editingRow, this.editingColumn);
                    String bsoID = (String) this.editingTable.getValueAt(this.editingRow, 13);
                    if (bsoID != null) {
                        Element pace = (Element) PaceTablePane.this.pacesCashe.get(bsoID);
                        if (pace != null) {
                            XmlUtility.setAttributeValue(pace, "isKey", String.valueOf(bool));
                        }
                    }
                }
            }
            return String.valueOf(bool);
        }

        public int getClickCountToStart() {
            return 1;
        }
    }

    class PaceContentRenderer implements TableCellRenderer {
        JTextPane textPane = new JTextPane();

        public PaceContentRenderer() {
            super();
            textPane.setFont(new Font("dialog", 0, 14));
        }

        @Override
        public Component getTableCellRendererComponent(final JTable table,
                                                       Object obj, boolean isSelected, boolean hasFocus,
                                                       final int row, final int column) {
            int width = table.getColumnModel().getColumn(column).getWidth();
            int height = table.getRowHeight(row);
            int h = height;
            textPane.setText(CappJavaUtil.convertNull(obj));
            textPane.setBorder(null);
            int speHeight = (int) textPane.getPreferredSize().getHeight();
            if (table.getRowHeight(row) < speHeight) {
                table.setRowHeight(row, speHeight);
            }

            if ((obj != null) && ((obj instanceof String))) {
                String txt = obj.toString();
                if (txt.length() > 0) {
                    ArrayList LineList = SpeClassUtil.splitContent(obj.toString(), PaceTablePane.useFont);
                    ArrayList lineEnds = MyUtilities.splitString(LineList, width, PaceTablePane.metrics);
                    ArrayList result = new ArrayList();
                    ArrayList list = MyUtilities.getLines(LineList, lineEnds);

                    int maxWidth = width;
                    if (list != null) {
                        for (int i = 0; i < list.size(); i++) {
                            ArrayList al = (ArrayList) list.get(i);
                            int w = 0;
                            for (int m = 0; m < al.size(); m++) {
                                Object o = al.get(m);
                                if ((o instanceof String)) {
                                    w += PaceTablePane.metrics.stringWidth(o.toString());
                                }
                                if ((o instanceof SpeIcon)) {
                                    SpeIcon icon = (SpeIcon) o;
                                    w += icon.getIconWidth();
                                }
                            }
                            if (w > maxWidth) {
                                maxWidth = w;
                            }
                        }
                        int rows = list.size();
                        h = 25 * rows;
                        if (h < height)
                            h = height;
                        if (height != h) {
                            table.setRowHeight(row, h);
                            PaceTablePane.this.repaint();
                        }
                    }
                }
            }

            if (isSelected) {
                textPane.setBackground(table.getSelectionBackground());
            } else {
                textPane.setBackground(Color.WHITE);
            }
            return this.textPane;

        }
    }

    // ====meixin====
    class SWEditor extends DefaultCellEditor {

        private static final long serialVersionUID = 1L;

        private SpecialWordPanel panle = null;
        private JTable editingTable = null;
        private int editingRow = -1;
        private int editingColumn = -1;

        public SWEditor() {
            super(new JTextField());
            panle = new SpecialWordPanel(frame, getImageFolder());
            editorComponent = panle;

            delegate = new DefaultCellEditor.EditorDelegate() {
                @Override
                public Object getCellEditorValue() {
                    return panle.getText();
                }

                @Override
                public void setValue(Object value) {
                    panle.setText((value != null) ? CommonHelper.replaceReadSeperator(value.toString(), getImageFolder()) : "");
                }
            };
        }

        public Component getTableCellEditorComponent(JTable table,
                                                     Object value, boolean isSelected, int row, int column) {
            panle.setTechnicsPath(getImageFolder());
            this.editingTable = table;
            this.editingRow = row;
            this.editingColumn = column;
            String text = CommonHelper.replaceReadSeperator(JavaUtil.convertNull(value), getImageFolder());
            panle.setText(text);
            return this.panle;
        }

        @Override
        public Object getCellEditorValue() {
            String content = panle.getText();
            if (content.contains("<!--EndFragment-->\n")) {
                content = content.replace("<!--EndFragment-->\n", "");
                content = content.replace("<!--EndFragment-->", "");
            }
            this.editingTable.setValueAt(content, this.editingRow, this.editingColumn);
            String bsoID = (String) this.editingTable.getValueAt(this.editingRow, 13);
            if (bsoID != null) {
                Element pace = (Element) PaceTablePane.this.pacesCashe.get(bsoID);
                if (pace != null) {
                    if (content != null) {
                        content = content.replaceAll(CommonUtil.SPECIAL_SPACE, "");
                    }
                    content = HtmlGenerator.removeImageTags(content);
                    String text = CommonHelper.replaceSaveSeperator(content, getImageFolder());
                    XmlUtility.setProcedureContent(pace, text);
                }
            }
            return panle.getText();
        }

        public void insertText(String str) {
            if (str != null && editorComponent != null) {
                ((SpecialWordPanel) editorComponent).insertText(str);
            }
        }

    }

    class SWRenderer extends SpecialWordPanel implements TableCellRenderer {

        private static final long serialVersionUID = 1L;
        private final DefaultTableCellRenderer adaptee = new DefaultTableCellRenderer();
        @SuppressWarnings("unchecked")
        private final Map cellSizes = new HashMap();

        public SWRenderer() {
            super(frame, getImageFolder());
        }

        @Override
        public Component getTableCellRendererComponent(final JTable table,
                                                       Object obj, boolean isSelected, boolean hasFocus,
                                                       final int row, final int column) {
            adaptee.getTableCellRendererComponent(table, obj, isSelected, hasFocus, row, column);
            // setForeground(adaptee.getForeground());
            // setBackground(adaptee.getBackground());
            setBorder(null);
            setFont(adaptee.getFont());
            setText(adaptee.getText());

            TableColumnModel columnModel = table.getColumnModel();
            setSize(columnModel.getColumn(column).getWidth(), 100000);
            int height_wanted = (int) getPreferredSize().getHeight();
            addSize(table, row, column, height_wanted);
            height_wanted = findTotalMaximumRowSize(table, row);
            if (height_wanted != table.getRowHeight(row)) {
                // if(table.getRowCount() > 1){
                table.setRowHeight(row, height_wanted);
                // }
            }
            // this.addCheckDocumentChangeListener(new
            // CheckDocumentChangeInterface() {
            // @Override
            // public void documentContentChange(String content) {
            // int height_wanted = (int) getPreferredSize().getHeight();
            // addSize(table, row, column, height_wanted);
            // height_wanted = findTotalMaximumRowSize(table, row);
            // if (height_wanted != table.getRowHeight(row)) {
            // // if(table.getRowCount() > 1){
            // table.setRowHeight(row, height_wanted);
            // // }
            // }
            // }
            // });

            if (isSelected) {
                setBackgroundColor(table.getSelectionBackground());
            } else {
                setBackgroundColor(Color.WHITE);
            }

            return this;
        }

        @SuppressWarnings("unchecked")
        private void addSize(JTable table, int row, int column, int height) {
            Map rows = (Map) cellSizes.get(table);
            if (rows == null) {
                cellSizes.put(table, rows = new HashMap());
            }
            Map rowheights = (Map) rows.get(new Integer(row));
            if (rowheights == null) {
                rows.put(new Integer(row), rowheights = new HashMap());
            }
            rowheights.put(new Integer(column), new Integer(height));
        }

        @SuppressWarnings("unchecked")
        private int findTotalMaximumRowSize(JTable table, int row) {
            int maximum_height = 0;
            Enumeration columns = table.getColumnModel().getColumns();
            while (columns.hasMoreElements()) {
                TableColumn tc = (TableColumn) columns.nextElement();
                TableCellRenderer cellRenderer = tc.getCellRenderer();
                if (cellRenderer instanceof SWRenderer) {
                    SWRenderer tar = (SWRenderer) cellRenderer;
                    maximum_height = Math.max(maximum_height, tar.findMaximumRowSize(table, row));
                }
            }

            return maximum_height;
        }

        @SuppressWarnings("unchecked")
        private int findMaximumRowSize(JTable table, int row) {
            Map rows = (Map) cellSizes.get(table);
            if (rows == null)
                return 0;
            Map rowheights = (Map) rows.get(new Integer(row));
            if (rowheights == null)
                return 0;
            int maximum_height = 0;
            for (Iterator it = rowheights.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry entry = (Map.Entry) it.next();
                int cellHeight = ((Integer) entry.getValue()).intValue();
                maximum_height = Math.max(maximum_height, cellHeight);
            }
            return maximum_height;
        }

    }

    class StepProgramNoEditor extends DefaultCellEditor {
        private JTextField stepProgramNoField = null;
        private JTable editingTable = null;
        private int editingRow = -1;
        private int editingColumn = -1;

        StepProgramNoEditor(JTextField textField) {
            super(textField);
            this.stepProgramNoField = textField;
            this.stepProgramNoField.setHorizontalAlignment(0);
        }

        public Component getTableCellEditorComponent(JTable table,
                                                     Object value, boolean isSelected, int row, int column) {
            this.editingTable = table;
            this.editingRow = row;
            this.editingColumn = column;
            this.stepProgramNoField.setText("");

            if ((value != null) && ((value instanceof String))) {
                this.stepProgramNoField.setText(value.toString());
            }
            return this.stepProgramNoField;
        }

        public Object getCellEditorValue() {
            String text = this.stepProgramNoField.getText();
            if (this.editingTable != null) {
                if ((this.editingRow >= 0)
                        && (this.editingRow < this.editingTable.getRowCount())
                        && (this.editingColumn >= 0)
                        && (this.editingColumn < this.editingTable.getColumnCount())) {
                    this.editingTable.setValueAt(text, this.editingRow, this.editingColumn);
                    for (int i = 0; i < this.editingTable.getRowCount(); i++) {
                        String temp = (String) this.editingTable.getValueAt(i, 4);
                        String paceID = (String) this.editingTable.getValueAt(i, 13);
                        if ((temp != null) && (paceID != null) && (PaceTablePane.this.pacesCashe.get(paceID) != null)) {
                            Element pace = (Element) PaceTablePane.this.pacesCashe.get(paceID);
                            XmlUtility.setAttributeValue(pace, "programNo", temp);
                        }
                    }
                }
            }

            return text;
        }
    }

    class StepBeiZhuEditor extends DefaultCellEditor {
        private JTextField stepbzField = null;
        private JTable editingTable = null;
        private int editingRow = -1;
        private int editingColumn = -1;

        StepBeiZhuEditor(JTextField textField) {
            super(textField);
            this.stepbzField = textField;
            this.stepbzField.setHorizontalAlignment(0);
        }

        public Component getTableCellEditorComponent(JTable table,
                                                     Object value, boolean isSelected, int row, int column) {
            this.editingTable = table;
            this.editingRow = row;
            this.editingColumn = column;
            this.stepbzField.setText("");

            if ((value != null) && ((value instanceof String))) {
                this.stepbzField.setText(value.toString());
            }
            return this.stepbzField;
        }

        public Object getCellEditorValue() {
            String text = this.stepbzField.getText();
            if (this.editingTable != null) {
                if ((this.editingRow >= 0)
                        && (this.editingRow < this.editingTable.getRowCount())
                        && (this.editingColumn >= 0)
                        && (this.editingColumn < this.editingTable.getColumnCount())) {
                    this.editingTable.setValueAt(text, this.editingRow, this.editingColumn);
                    for (int i = 0; i < this.editingTable.getRowCount(); i++) {
                        String temp = (String) this.editingTable.getValueAt(i, 14);
                        String paceID = (String) this.editingTable.getValueAt(i, 13);
                        if ((temp != null) && (paceID != null) && (PaceTablePane.this.pacesCashe.get(paceID) != null)) {
                            Element pace = (Element) PaceTablePane.this.pacesCashe.get(paceID);
                            XmlUtility.setAttributeValue(pace, "gbbz", temp);
                        }
                    }
                }
            }

            return text;
        }
    }

    class StepZpjcjgEditor extends DefaultCellEditor {
        private JTextField stepbzField = null;
        private JTable editingTable = null;
        private int editingRow = -1;
        private int editingColumn = -1;

        StepZpjcjgEditor(JTextField textField) {
            super(textField);
            this.stepbzField = textField;
            this.stepbzField.setHorizontalAlignment(0);
        }

        public Component getTableCellEditorComponent(JTable table,
                                                     Object value, boolean isSelected, int row, int column) {
            this.editingTable = table;
            this.editingRow = row;
            this.editingColumn = column;
            this.stepbzField.setText("");

            if ((value != null) && ((value instanceof String))) {
                this.stepbzField.setText(value.toString());
            }
            return this.stepbzField;
        }

        public Object getCellEditorValue() {
            String text = this.stepbzField.getText();
            if (this.editingTable != null) {
                if ((this.editingRow >= 0)
                        && (this.editingRow < this.editingTable.getRowCount())
                        && (this.editingColumn >= 0)
                        && (this.editingColumn < this.editingTable.getColumnCount())) {
                    this.editingTable.setValueAt(text, this.editingRow, this.editingColumn);
                    for (int i = 0; i < this.editingTable.getRowCount(); i++) {
                        String temp = (String) this.editingTable.getValueAt(i, 3);
                        String paceID = (String) this.editingTable.getValueAt(i, 13);
                        if ((temp != null) && (paceID != null) && (PaceTablePane.this.pacesCashe.get(paceID) != null)) {
                            Element pace = (Element) PaceTablePane.this.pacesCashe.get(paceID);
                            XmlUtility.setAttributeValue(pace, "ZPJCJG", temp);
                        }
                    }
                }
            }

            return text;
        }
    }

    class StepNumberEditor extends DefaultCellEditor {
        private JTable editingTable = null;
        private int editingRow = -1;
        private int editingColumn = -1;
        private JTextField stepNumberField = null;

        StepNumberEditor(JTextField textField) {
            super(textField);
            this.stepNumberField = textField;
            this.stepNumberField.setHorizontalAlignment(0);
        }

        public Component getTableCellEditorComponent(JTable table,
                                                     Object value, boolean isSelected, int row, int column) {
            this.editingTable = table;
            this.editingRow = row;
            this.editingColumn = column;
            this.stepNumberField.setText("");
            if ((value != null) && ((value instanceof String))) {
                this.stepNumberField.setText(value.toString());
            }
            return this.stepNumberField;
        }

        public boolean check(String text, int row) {
            boolean flag = false;
            if (text != null) {
                if ((text.length() > 1) && (!text.startsWith("-"))) {
                    flag = true;
                }
                if ((text.equals("A")) || (text.equals("B"))) {
                    if (this.editingTable != null) {
                        if ((this.editingRow >= 0)
                                && (this.editingRow < this.editingTable.getRowCount())
                                && (this.editingColumn >= 0)
                                && (this.editingColumn < this.editingTable.getColumnCount())) {
                            for (int i = 0; i < this.editingTable.getRowCount(); i++) {
                                if (i != row) {
                                    String temp = (String) this.editingTable.getValueAt(i, 1);
                                    if ((temp != null) && (temp.equals(text))) {
                                        flag = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return flag;
        }

        public Object getCellEditorValue() {
            String text = this.stepNumberField.getText();
            if (text == null)
                text = "";
            text = text.replaceAll("　", "").trim();
            if (text.trim().length() > 0) {
                text = XmlUtility.toSemiangle(text);
            }
            if (this.editingTable != null) {
                if ((this.editingRow >= 0)
                        && (this.editingRow < this.editingTable.getRowCount())
                        && (this.editingColumn >= 0)
                        && (this.editingColumn < this.editingTable.getColumnCount())) {
                    if (check(text, this.editingRow)) {
                        JOptionPane.showMessageDialog(PaceTablePane.this.frame, "工步号重复或不合法，请重新输入！", "提示", 1);
                        text = "0";
                    }
                    this.editingTable.setValueAt(text, this.editingRow, this.editingColumn);

                    int max = 1;
                    for (int i = 0; i < this.editingTable.getRowCount(); i++) {
                        String temp = (String) this.editingTable.getValueAt(i, 1);
                        String paceID = (String) this.editingTable.getValueAt(i, 13);
                        if ((temp != null) && (paceID != null)
                                && (PaceTablePane.this.pacesCashe.get(paceID) != null)) {
                            Element pace = (Element) PaceTablePane.this.pacesCashe.get(paceID);
                            Vector cashe = XmlUtility.getPaceNumberCashe();
                            if (cashe.contains(temp)) {
                                if (temp.equals("检")) {
                                    XmlUtility.setAttributeValue(pace, "procedureType", "procedureType");
                                }
                            } else {
                                String num = String.valueOf(max++);
                                this.editingTable.setValueAt("" + num, i, 1);
                                temp = "" + num;
                            }

                            XmlUtility.setAttributeValue(pace, "stepNumber", temp);
                            if (i == this.editingRow) {
                                text = temp;
                            }
                        }
                    }
                }
            }
            return text;
        }

        public int getClickCountToStart() {
            return 1;
        }
    }

    class WorkShopEditor extends DefaultCellEditor {
        JPanel panel = null;
        JComboBox combo = null;
        private JTable editingTable = null;
        private int editingRow = -1;
        private int editingColumn = -1;
        private Map workShop = null;

        WorkShopEditor(JComboBox box) {
            super(box);
            this.combo = box;
            this.panel = new JPanel();
            this.panel.setLayout(new GridBagLayout());
            this.panel.add(this.combo, new GridBagConstraints(0, 0, 1, 1, 0.0D,
                    0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));

            this.workShop = ResourceIntf.getWorkShops();
            this.combo.removeAllItems();
            this.combo.addItem("");
            Collection coll;
            if ((this.workShop != null) && (this.workShop.size() > 0)) {
                coll = this.workShop.values();
                Iterator it = coll.iterator();
                while (it.hasNext()) {
                    box.addItem(it.next());
                }
            }
            this.combo.setSelectedItem("");
        }

        public Component getTableCellEditorComponent(JTable table,
                                                     Object value, boolean isSelected, int row, int column) {
            this.editingTable = table;
            this.editingRow = row;
            this.editingColumn = column;
            int width = table.getColumnModel().getColumn(column).getWidth();
            if (value == null)
                value = "";
            this.combo.setSelectedItem(value);

            this.combo.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (PaceTablePane.WorkShopEditor.this.editingTable != null) {
                        if ((PaceTablePane.WorkShopEditor.this.editingRow >= 0)
                                && (PaceTablePane.WorkShopEditor.this.editingRow < PaceTablePane.WorkShopEditor.this.editingTable
                                .getRowCount())
                                && (PaceTablePane.WorkShopEditor.this.editingColumn >= 0)
                                && (PaceTablePane.WorkShopEditor.this.editingColumn < PaceTablePane.WorkShopEditor.this.editingTable
                                .getColumnCount())) {
                            PaceTablePane.WorkShopEditor.this.editingTable
                                    .setValueAt("", PaceTablePane.WorkShopEditor.this.editingRow,
                                            PaceTablePane.WorkShopEditor.this.editingColumn + 1);
                            String bsoID = (String) PaceTablePane.WorkShopEditor.this.editingTable
                                    .getValueAt(PaceTablePane.WorkShopEditor.this.editingRow, 13);
                            if (bsoID != null) {
                                Element pace = (Element) PaceTablePane.this.pacesCashe.get(bsoID);
                                if (pace != null) {
                                    XmlUtility.setAttributeValue(pace, "workType", "");
                                    XmlUtility.setAttributeValue(pace, "workTypeID", "");
                                }
                            }
                        }
                    }
                }
            });
            this.combo.setMaximumSize(new Dimension(width, 24));
            this.combo.setMinimumSize(new Dimension(width, 24));
            this.combo.setPreferredSize(new Dimension(width, 24));
            return this.panel;
        }

        public Object getCellEditorValue() {
            Object result = this.combo.getSelectedItem();
            if (result == null)
                result = "";
            if (this.editingTable != null) {
                if ((this.editingRow >= 0)
                        && (this.editingRow < this.editingTable.getRowCount())
                        && (this.editingColumn >= 0)
                        && (this.editingColumn < this.editingTable.getColumnCount())) {
                    this.editingTable.setValueAt(result, this.editingRow, this.editingColumn);
                    String bsoID = (String) this.editingTable.getValueAt(this.editingRow, 13);
                    if (bsoID != null) {
                        Element pace = (Element) PaceTablePane.this.pacesCashe.get(bsoID);
                        if (pace != null) {
                            XmlUtility.setAttributeValue(pace, "workShop", result.toString());
                            if (this.workShop != null) {
                                XmlUtility.setAttributeValue(pace,
                                        "workShopID", PaceTablePane.this.getKey(this.workShop, result.toString()));
                            }
                        }
                    }
                }
            }

            // ItemListener[] listeners = this.combo.getItemListeners();
            // if (listeners != null) {
            // for (int i = 0; i < listeners.length; i++) {
            // this.combo.removeItemListener(listeners[i]);
            // }
            // }
            return result;
        }

        public int getClickCountToStart() {
            return 1;
        }
    }

    class WorkSpaceEditor extends DefaultCellEditor {
        JPanel panel = null;
        JComboBox combo = null;
        private JTable editingTable = null;
        private Map paceWorkType = null;
        private int editingRow = -1;
        private int editingColumn = -1;

        WorkSpaceEditor(JComboBox box) {
            super(box);
            this.combo = box;
            this.panel = new JPanel();
            this.panel.setLayout(new GridBagLayout());
            this.panel.add(this.combo, new GridBagConstraints(0, 0, 1, 1, 0.0D,
                    0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
        }

        public Component getTableCellEditorComponent(JTable table,
                                                     Object value, boolean isSelected, int row, int column) {
            this.editingTable = table;
            this.editingRow = row;
            this.editingColumn = column;

            this.combo.removeAllItems();
            this.combo.addItem("");

            this.paceWorkType = null;
            String workShop = (String) this.editingTable.getValueAt(this.editingRow, this.editingColumn - 1);
            if ((workShop != null) && (workShop.trim().length() > 0)) {
                Map stepWorkShop = ResourceIntf.getWorkShops();
                String shopID = PaceTablePane.this.getKey(stepWorkShop, workShop);
                this.paceWorkType = ResourceUtil.generate812ShopTypeMap(shopID);
                logger.debug("工步工种========" + this.paceWorkType);
                if ((this.paceWorkType != null) && (this.paceWorkType.size() > 0)) {
                    Collection coll = this.paceWorkType.values();
                    Iterator it = coll.iterator();
                    while (it.hasNext()) {
                        this.combo.addItem(it.next());
                    }
                }
            } else {
                this.combo.removeAllItems();
                this.combo.setSelectedItem("");
            }

            this.combo.setSelectedItem("");

            int width = table.getColumnModel().getColumn(column).getWidth();
            if (value == null)
                value = "";
            this.combo.setSelectedItem(value);
            this.combo.setMaximumSize(new Dimension(width, 24));
            this.combo.setMinimumSize(new Dimension(width, 24));
            this.combo.setPreferredSize(new Dimension(width, 24));
            return this.panel;
        }

        public Object getCellEditorValue() {
            Object result = this.combo.getSelectedItem();
            if (result == null)
                result = "";
            if (this.editingTable != null) {
                if ((this.editingRow >= 0)
                        && (this.editingRow < this.editingTable.getRowCount())
                        && (this.editingColumn >= 0)
                        && (this.editingColumn < this.editingTable.getColumnCount())) {
                    this.editingTable.setValueAt(result, this.editingRow, this.editingColumn);
                    String bsoID = (String) this.editingTable.getValueAt(this.editingRow, 13);
                    if (bsoID != null) {
                        Element pace = (Element) PaceTablePane.this.pacesCashe.get(bsoID);
                        if (pace != null) {
                            XmlUtility.setAttributeValue(pace, "workType", result.toString());
                            if (this.paceWorkType != null) {
                                XmlUtility.setAttributeValue(pace, "workTypeID", PaceTablePane.this
                                        .getKey(this.paceWorkType, result.toString()));
                            }
                        }
                    }
                }
            }
            return result;
        }

        public int getClickCountToStart() {
            return 1;
        }
    }

    /**
     * 增加参装件
     *
     * @param vec
     */
    public void addPartValues(String paceOid) {
        if (this.table.getRowCount() > 0) {
            stopTableCellEditing();
            for (int i = 0; i < this.table.getRowCount(); i++) {
                String bsoId = JavaUtil.convertNull(table.getValueAt(i, 13));
                if (bsoId.equals(paceOid)) {
                    Element paceElement = (Element) this.pacesCashe.get(bsoId);
                    table.setValueAt(getAllParts(paceElement), i, 5);
                }
            }
        }
    }

    /**
     * 删除参装件
     *
     * @param vec
     */
    public void deletePartValues(String paceOid,
                                 Vector<Map<String, String>> parts2) {
        if (this.table.getRowCount() > 0) {
            stopTableCellEditing();
            for (int i = 0; i < this.table.getRowCount(); i++) {
                String bsoId = JavaUtil.convertNull(table.getValueAt(i, 13));
                if (bsoId.equals(paceOid)) {
                    Element paceElement = (Element) this.pacesCashe.get(bsoId);
                    String parts = JavaUtil.convertNull(table.getValueAt(i, 5));
                    List<String> paceCzjNumberList = getPaceELementCzj(paceElement);
                    for (Map<String, String> map : parts2) {
                        String number = map.get("partNumber");
                        String occId = map.get("occId");
                        String occpath = map.get("occpath");
                        if(paceCzjNumberList.contains(number)) {
                            removePart(paceElement, number, occId, occpath);
                        }
                    }
                    table.setValueAt(getAllParts(paceElement), i, 5);
                }
            }
        }
    }

    public static List<String> getPaceELementCzj(Element paceElement) {
        List<String> czjNumberList = new ArrayList<String>();
        Element pacePartsElement = XmlUtility.getParts(paceElement);
        if (pacePartsElement != null) {
            List<Element> czjElements = pacePartsElement.elements("QMPartInfo");
            for (Element czjElement : czjElements) {
                if (!czjNumberList.contains(czjElement.attributeValue("partNumber"))) {
                    czjNumberList.add(czjElement.attributeValue("partNumber"));
                }
            }
        }
        return czjNumberList;
    }

    /**
     * 删除参装件,返回是否还有参装件
     *
     * @param paceElement
     * @param oid
     */
    public static boolean removePart(Element paceElement, String partNumber,
                                     String occId, String occpath) {
        Element part = paceElement.element("parts");
        List<Element> partsElements = part.elements("QMPartInfo");
        for (Element partElement : partsElements) {
            if (partNumber.equals(partElement.attributeValue("partNumber")) && occId.equals(partElement.attributeValue("occId"))) {
                part.remove(partElement);
                return true;
            }
        }
//        return PbomUtil.deletePart(partsElements, partNumber, occId, occpath);
        return false;
    }

    public String getImageFolder() {
        return imageFolder;
    }

    public void setImageFolder(String imageFolder) {
        this.imageFolder = imageFolder;
    }


    /**
     * 将复制的工步对象添加至工步面板表格中
     *
     * @param element
     * @author 王鑫磊、杨青
     * @校对 马崇奇
     * @date 2015-6-3
     */
    public void pasteOneRowTableValue(Element element) {
        if ((element != null) && (element.getName().equals("QMProcedureInfo"))) {
            String isKey = XmlUtility.getAttributeValue(element, "isKey");
            String paceNum = XmlUtility.getAttributeValue(element, "stepNumber");
            String progamrNo = XmlUtility.getAttributeValue(element, "programNo");
            String ZPJCJG = XmlUtility.getAttributeValue(element, "ZPJCJG");
            if (ZPJCJG == null) {
                ZPJCJG = "";
            }
            if (progamrNo == null) {
                progamrNo = "";
            }
            String gbbz = XmlUtility.getAttributeValue(element, "gbbz");
            if (gbbz == null) {
                gbbz = "";
            }
            String paceContent = XmlUtility.getProcedureContent(element);
            paceContent = CommonHelper.replaceReadSeperator(paceContent, getImageFolder());
            String shop = XmlUtility.getAttributeValue(element, "workShop");
            String type = XmlUtility.getAttributeValue(element, "workType");
            String bsoID = XmlUtility.getAttributeValue(element, "bsoID");
            if ((bsoID == null) || (bsoID.trim().length() == 0)) {
                bsoID = new UID().toString();
                XmlUtility.setAttributeValue(element, "bsoID", bsoID);
            }
            this.pacesCashe.put(bsoID, element);

            String equips = getAllEquips(element);
            String measures = getAllMeasures(element);
            String tools = getAllTools(element);
            String knifeTools = getAllKnifeTools(element);
            String materials = getAllMaterials(element);
            String dashboards = getAllDashboards(element);
            String unsdashboards = getAllUnSDashboards(element);
            super.addProcess();
            int i = this.tableModel.getRowCount();
            i--;
            this.table.setRowHeight(i, 25);
            this.tableModel.setValueAt(isKey, i, 0);
            this.tableModel.setValueAt(paceNum, i, 1);
            this.tableModel.setValueAt(paceContent, i, 2);
            this.tableModel.setValueAt(equips, i, 9);
            this.tableModel.setValueAt(tools, i, 8);
            this.tableModel.setValueAt(null, i, 5);
            this.tableModel.setValueAt(materials, i, 6);
            this.tableModel.setValueAt(knifeTools, i, 7);
            this.tableModel.setValueAt(dashboards, i, 10);
            this.tableModel.setValueAt(unsdashboards, i, 11);
            this.tableModel.setValueAt(measures, i, 12);
            this.tableModel.setValueAt(progamrNo, i, 4);
            this.tableModel.setValueAt(gbbz, i, 14);
            this.tableModel.setValueAt(bsoID, i, 13);
            this.tableModel.setValueAt(ZPJCJG, i, 3);

        }

    }


}
