package com.glaway.mpm.view;

import com.glaway.mpm.model.UploadTechnics;
import com.glaway.mpm.util.IconUtil;
import com.glaway.mpm.util.ObjectTransfer;
import com.glaway.mpm.util.XmlUtility;
import org.dom4j.Document;
import org.dom4j.Element;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TechnicsStepPopupMenu extends JPopupMenu implements ActionListener {

    private static final long serialVersionUID = 1L;

    private JMenuItem insertStep = new JMenuItem("插入工序");
    private JMenuItem setCheckStep = new JMenuItem("设置检验点");
    private JMenuItem setPreProcedure = new JMenuItem("设置前置工序");
    private JMenuItem insertProcedureTemplate = new JMenuItem("插入工序模板");
    private JMenuItem saveAsProcedureTemplate = new JMenuItem("存为工序模板");
    private JMenuItem saveAsProcedureParamTemplate = new JMenuItem("存为参数化工序模板");

    private JMenuItem copy = new JMenuItem("复制");

    private JMenuItem delete = new JMenuItem("删除");

    // 根据信维二期需求，添加菜单项
    private JMenu run = new JMenu("启动工具");
    private JMenuItem newFrockApply = new JMenuItem("新建工装申请卡");
    private JMenuItem runDiagram = new JMenuItem("工艺简图工具");
    private JMenuItem runMiddleModule = new JMenuItem("中间模型工具");
    private JMenuItem runVisual = new JMenuItem("可视化工具");
    private JMenuItem runAssembleCartoon = new JMenuItem("装配动画工具");

    private NewTechnicsPart frame;

    private TechnicsTreePanel treePanel;

    public TechnicsStepPopupMenu(NewTechnicsPart frame,
                                 TechnicsTreePanel treePanel) {
        super();
        this.frame = frame;
        this.treePanel = treePanel;

        setDefaultLightWeightPopupEnabled(false);
        setLightWeightPopupEnabled(false);

        add(insertStep);
        insertStep.addActionListener(this);
        addSeparator();// 分割线

        add(setCheckStep);
        setCheckStep.addActionListener(this);
        //add(setPreProcedure);
        //setPreProcedure.addActionListener(this);
        addSeparator();

        // 信维二期添加菜单
        add(run);
        run.add(runDiagram);
        run.add(runMiddleModule);
        run.add(runVisual);
//		run.add(runAssembleCartoon);
        runDiagram.addActionListener(this);
        runMiddleModule.addActionListener(this);
        runVisual.addActionListener(this);
        runAssembleCartoon.addActionListener(this);

        addSeparator();// 分割线
//		add(newFrockApply);
//		newFrockApply.addActionListener(this);

        insertProcedureTemplate.addActionListener(this);
        saveAsProcedureTemplate.addActionListener(this);
        saveAsProcedureParamTemplate.addActionListener(this);
        add(insertProcedureTemplate);
        add(saveAsProcedureTemplate);
        add(saveAsProcedureParamTemplate);

        addSeparator();// 分割线
        copy.addActionListener(this);
        delete.addActionListener(this);
        add(copy);
        add(delete);

        initMenuItemIcon();
    }

    // 设置菜单项图标
    private void initMenuItemIcon() {
        copy.setIcon(IconUtil.getImageIcon(IconUtil.COPY));
        delete.setIcon(IconUtil.getImageIcon(IconUtil.DELETE));
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == insertStep) {
            try {
                frame.insertProductStep(false);
            } catch (Exception e) {

            }
        }

        if (event.getSource() == setCheckStep) {
            try {
                frame.setCheckStep(false);
            } catch (Exception e) {

            }
        }
        //设置前置工序监听事件
        if (event.getSource() == setPreProcedure) {
            try {
                frame.setPreProdure();
            } catch (Exception e) {

            }

        }

        if (event.getSource() == newFrockApply) {
            try {
                frame.newFrockApply();
            } catch (Exception e) {

            }
        }
        if (event.getSource() == insertProcedureTemplate) {
            try {
                frame.insertProductStep(true);
            } catch (Exception e) {

            }
        }

        if (event.getSource() == saveAsProcedureTemplate) {
            try {
                saveAsProcedureTemplate();
            } catch (Exception e) {

            }
        }

        if (event.getSource() == saveAsProcedureParamTemplate) {
            try {
                saveAsProcedureParamTemplate();
            } catch (Exception e) {

            }
        }

        if (event.getSource() == copy) {
            try {
                frame.copy();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "复制工艺出现错误！", "提示",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
        if (event.getSource() == delete) {
            try {
                frame.delete();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "删除操作出现错误！", "提示",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
        // 信维二期添加菜单
        if (event.getSource() == runDiagram)// 启动3D
        {
            try {
                frame.runDiagramProgram();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "启动工艺简图工具出现错误！", "提示",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }

        if (event.getSource() == runMiddleModule)// 启动3D
        {
            try {
                frame.runMiddleModuleProgram();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "启动中间模型工具出现错误！", "提示",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }

        if (event.getSource() == runVisual)// 启动3D
        {
            try {
                frame.runVisualProgram();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "启动可视化工具出现错误！", "提示",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }

        if (event.getSource() == runAssembleCartoon)// 启动3D
        {
            try {
                frame.runAssembleCartoonProgram();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "启动装配动画工具出现错误！", "提示",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }

    }

    public void setMenuState(XWTreeObject xo) {
        XWTreeNode technode = treePanel.getCurrentTechnicsNode();
        boolean bool = false;
        if (technode != null) {
            Element techEle = technode.getObject().getTreeCellData();
            UploadTechnics technics = ObjectTransfer.technicsElementToUploadTechnics(techEle);
            if (NewTechnicsPart.downLoadTech.contains(technics)) {
                bool = true;
            }
        }
        if (xo == null) {
            setNullSelectedStatus();
        }
        if (xo instanceof XWTechnicsTreeObject) {
            setTechnicsSelectedStatus(xo);
        } else if (xo instanceof XWStepTreeObject) {
            if (bool) {
                setStepSelectedStatus();
            } else {
                setStepSelectedStatus(xo);
            }
        } else if (xo instanceof XWPaceTreeObject) {
            setPaceSelectedStatus();
        } else if (xo instanceof XWPartTreeObject) {
            setPartSelectedStatus();
        } else if (xo instanceof XWProductTreeObject) {
            setProductSelectedStatus();
        }
    }

    private void setTechnicsSelectedStatus(XWTreeObject node) {
        insertStep.setEnabled(false);
        setCheckStep.setEnabled(false);
        setPreProcedure.setEnabled(false);
        insertProcedureTemplate.setEnabled(false);
        saveAsProcedureTemplate.setEnabled(true);
        saveAsProcedureParamTemplate.setEnabled(true);
        delete.setEnabled(true);
        copy.setEnabled(false);
        Document tech = frame.getCurrentTechnics();
        if (tech != null) {
            try {
                Element ele = XmlUtility.getTechnicsElement(tech);
                String lifecycle = ele.attributeValue("lifecycle");
                if (lifecycle != null && (!"".equals(lifecycle))
                        && !lifecycle.equals("正在工作") && !lifecycle.equals("修改中")) {
                    delete.setEnabled(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setStepSelectedStatus(XWTreeObject node) {
        insertStep.setEnabled(true);
        setCheckStep.setEnabled(true);
        setPreProcedure.setEnabled(true);
        insertProcedureTemplate.setEnabled(true);
        saveAsProcedureTemplate.setEnabled(true);
        saveAsProcedureParamTemplate.setEnabled(true);
        delete.setEnabled(true);
        copy.setEnabled(true);
        String techType = "";// =this.getTechType(node.getTreeCellData());
        try {
            techType = frame.getTechType(node.getTreeCellData());
        } catch (Exception e) {
            e.printStackTrace();
        }

        XWStepTreeObject curStep = (XWStepTreeObject) node;
        String stepNumber = curStep.getTreeCellData().attributeValue("stepNumber");
        int number = XmlUtility.getSubFigure(stepNumber);
        if (number == -1) {
            insertStep.setEnabled(false);
            setCheckStep.setEnabled(false);
            setPreProcedure.setEnabled(false);
            insertProcedureTemplate.setEnabled(false);
            saveAsProcedureTemplate.setEnabled(true);
            saveAsProcedureParamTemplate.setEnabled(true);
        } else {
            XWTreeNode currentNode = treePanel.getSelectedTreeNode();
            XWTreeNode nextNode = (XWTreeNode) currentNode.getNextSibling();
            if (nextNode == null) {
                insertStep.setEnabled(true);
                setCheckStep.setEnabled(true);
                setPreProcedure.setEnabled(true);
                insertProcedureTemplate.setEnabled(true);
                saveAsProcedureTemplate.setEnabled(true);
                saveAsProcedureParamTemplate.setEnabled(true);
            } else {
                XWStepTreeObject nextStep = (XWStepTreeObject) nextNode.getObject();
                String nextStepNumber = nextStep.getTreeCellData().attributeValue("stepNumber");
                int nextNumber = XmlUtility.getSubFigure(nextStepNumber);
                if (nextNumber == -1) {
                    insertStep.setEnabled(true);
                    setCheckStep.setEnabled(true);
                    setPreProcedure.setEnabled(true);
                    insertProcedureTemplate.setEnabled(true);
                    saveAsProcedureTemplate.setEnabled(true);
                    saveAsProcedureParamTemplate.setEnabled(true);
                } else {
                    if (nextNumber == number + 1) {
                        insertStep.setEnabled(false);
                        setCheckStep.setEnabled(false);
                        setPreProcedure.setEnabled(false);
                        insertProcedureTemplate.setEnabled(false);
                        saveAsProcedureTemplate.setEnabled(true);
                        saveAsProcedureParamTemplate.setEnabled(true);
                    } else {
                        insertStep.setEnabled(true);
                        setCheckStep.setEnabled(true);
                        setPreProcedure.setEnabled(true);
                        insertProcedureTemplate.setEnabled(true);
                        saveAsProcedureTemplate.setEnabled(true);
                        saveAsProcedureParamTemplate.setEnabled(true);
                    }
                }
            }
        }

        //
        try {
            Document doc = frame.getCurrentTechnics();
            if (doc != null) {
                Element techEle = XmlUtility.getTechnicsElement(doc);
                String unite = XmlUtility.getAttributeValue(techEle, "unite");
                String creatorOid = XmlUtility.getAttributeValue(techEle, "creatorOid");
                if (unite == null)
                    unite = "";
                if (creatorOid == null)
                    creatorOid = "";
                if (unite.equals("routeUnite") || unite.equals("unite"))// 合编工艺
                {
                    String user = frame.getCurrentUser();
                    if (!creatorOid.equals(user))// 不是当前用户的情况下
                    {
                        String responser = XmlUtility.getAttributeValue(curStep.getTreeCellData(), "responser");
                        if ((responser != null && responser.trim().length() > 0)
                                && (user != null && user.trim().length() > 0)) {
                            if (!user.equals(responser))// 工序责任人不是当前用户，不准编辑
                            {
                                insertStep.setEnabled(false);
                                setCheckStep.setEnabled(false);
                                setPreProcedure.setEnabled(false);
                                insertProcedureTemplate.setEnabled(false);
                                saveAsProcedureTemplate.setEnabled(true);
                                saveAsProcedureParamTemplate.setEnabled(true);
                                delete.setEnabled(false);
                            }
                        }
                    }
                }

                String lifecycle = techEle.attributeValue("lifecycle");
                String creator = techEle.attributeValue("creator");
                if ((lifecycle != null && (!"".equals(lifecycle))
                        && !lifecycle.equals("正在工作") && !lifecycle.equals("修改中"))
                        || !creator.equals(NewTechnicsPart.currentUser)) {
                    delete.setEnabled(false);
                    copy.setEnabled(false);
                    insertStep.setEnabled(false);
                    setCheckStep.setEnabled(false);
                    setPreProcedure.setEnabled(false);
                    insertProcedureTemplate.setEnabled(false);
                    newFrockApply.setEnabled(false);
                    insertProcedureTemplate.setEnabled(false);
                    saveAsProcedureTemplate.setEnabled(true);
                    saveAsProcedureParamTemplate.setEnabled(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPaceSelectedStatus() {
        insertStep.setEnabled(false);
        setCheckStep.setEnabled(false);
        setPreProcedure.setEnabled(false);
        insertProcedureTemplate.setEnabled(false);
        saveAsProcedureTemplate.setEnabled(true);
        saveAsProcedureParamTemplate.setEnabled(true);
        delete.setEnabled(true);
        copy.setEnabled(false);
        Document tech = frame.getCurrentTechnics();
        if (tech != null) {
            try {
                Element ele = XmlUtility.getTechnicsElement(tech);
                String lifecycle = ele.attributeValue("lifecycle");
                if (lifecycle != null && (!"".equals(lifecycle))
                        && !lifecycle.equals("正在工作") && !lifecycle.equals("修改中")) {
                    delete.setEnabled(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setPartSelectedStatus() {
        insertStep.setEnabled(false);
        setCheckStep.setEnabled(false);
        setPreProcedure.setEnabled(false);
        insertProcedureTemplate.setEnabled(false);
        saveAsProcedureTemplate.setEnabled(true);
        saveAsProcedureParamTemplate.setEnabled(true);
        delete.setEnabled(false);
        copy.setEnabled(false);
    }

    private void setProductSelectedStatus() {
        insertStep.setEnabled(false);
        setCheckStep.setEnabled(false);
        setPreProcedure.setEnabled(false);
        insertProcedureTemplate.setEnabled(false);
        saveAsProcedureTemplate.setEnabled(true);
        saveAsProcedureParamTemplate.setEnabled(true);
        delete.setEnabled(false);
        copy.setEnabled(false);
    }

    private void setNullSelectedStatus() {
        insertStep.setEnabled(false);
        setCheckStep.setEnabled(false);
        setPreProcedure.setEnabled(false);
        insertProcedureTemplate.setEnabled(false);
        saveAsProcedureTemplate.setEnabled(true);
        saveAsProcedureParamTemplate.setEnabled(true);
        delete.setEnabled(false);
        copy.setEnabled(false);
    }

    private void setStepSelectedStatus() {
        insertStep.setEnabled(false);
        setCheckStep.setEnabled(false);
        setPreProcedure.setEnabled(false);
        insertProcedureTemplate.setEnabled(false);
        saveAsProcedureTemplate.setEnabled(true);
        saveAsProcedureParamTemplate.setEnabled(true);
        delete.setEnabled(true);
        run.setEnabled(false);
        copy.setEnabled(true);
        Document tech = frame.getCurrentTechnics();
        if (tech != null) {
            try {
                Element ele = XmlUtility.getTechnicsElement(tech);
                String lifecycle = ele.attributeValue("lifecycle");
                if (lifecycle != null && (!"".equals(lifecycle))
                        && !lifecycle.equals("正在工作") && !lifecycle.equals("修改中")) {
                    delete.setEnabled(false);
                    setCheckStep.setEnabled(false);
                    setPreProcedure.setEnabled(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void saveAsProcedureTemplate() throws Exception {
        XWTreeNode node = treePanel.getSelectedTreeNode();
        if (node != null) {
            XWTreeObject xo = node.getObject();
            if (xo instanceof XWStepTreeObject) {
                Element stepElement = xo.getTreeCellData();
                if (stepElement != null && stepElement.getDocument() != null) {
//                    /**移除质量记录表信息*/
//                    MPMParameterProcessor.removeComSpeElements(stepElement);
//                    /**移除参装件信息*/
//                    XmlUtility.removeCzjElements(stepElement);
                    SaveAsStepTemplateDialog sstd = new SaveAsStepTemplateDialog(frame, stepElement);
                    sstd.showDialog();
                }
            }
        }
    }

    public void saveAsProcedureParamTemplate() throws Exception {
        XWTreeNode node = treePanel.getSelectedTreeNode();
        if (node != null) {
            XWTreeObject xo = node.getObject();
            if (xo instanceof XWStepTreeObject) {
                Element stepElement = xo.getTreeCellData();
                if (stepElement != null && stepElement.getDocument() != null) {
//                    /**移除质量记录表信息*/
//                    MPMParameterProcessor.removeComSpeElements(stepElement);
//                    /**移除参装件信息*/
//                    XmlUtility.removeCzjElements(stepElement);
                    SaveAsStepParamTemplateDialog sstd = new SaveAsStepParamTemplateDialog(frame, stepElement);
                    sstd.showDialog();
                }
            }
        }
    }
}