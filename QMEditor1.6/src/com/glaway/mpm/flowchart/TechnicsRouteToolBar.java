package com.glaway.mpm.flowchart;


import com.glaway.mpm.util.IconUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class TechnicsRouteToolBar extends JToolBar {
    private TechnicsRouteJPanel panel;
    private Action setToDefault;
    private Action addSingleLineUnit;
    private Action addBidirectionalSingleLineUnit;
    private Action addHorizontalDoubleLineUnit;
    private Action addBidirectionalHorizontalDoubleLineUnit;
    private Action addVerticalDoubleLineUnit;
    private Action addBidirectionalVerticalDoubleLineUnit;
    private Action addHorizontalTrebleLineUnit;
    private Action addBidirectionalHorizontalTrebleLineUnit;
    private Action addVerticalTrebleLineUnit;
    private Action addBidirectionalVerticalTrebleLineUnit;
    private Action addHorizontalQuintupleLineUnit;
    private Action addBidirectionalHorizontalQuintupleLineUnit;
    private Action toUp;
    private Action toBottom;
    private Action toLeft;
    private Action toRight;
    private Action toHorizontalCenter;
    private Action toVerticalCenter;
    private Action deleteOneLine;
    private Action deleteUselessLine;
    private Action createImage;
    private Action saveTechnicsRoute;
    private Action refresh;
    private Action reSet;//重置×10
    private Action reSet1;//重置×5
    private Action createmoreImage;

    private Action showEditAllImage;//全屏编辑

    public TechnicsRouteToolBar(TechnicsRouteJPanel panel) {
        this.panel = panel;

        this.setToDefault = new AbstractAction("恢复成默认鼠标",
                IconUtil.getImageIcon("/images/PVLite_IconSelectMode.gif")) {
            public void actionPerformed(ActionEvent e) {
                TechnicsRouteToolBar.this.panel.mouseMode = 1;
            }
        };
        this.setToDefault.putValue("ShortDescription", "恢复成默认鼠标");
        add(this.setToDefault);
        addSeparator();

        this.addSingleLineUnit = new AbstractAction("添加一段直线",
                IconUtil.getImageIcon("/images/single_line.gif")) {
            public void actionPerformed(ActionEvent e) {
                TechnicsRouteToolBar.this.panel.mouseMode = 2;
                TechnicsRouteToolBar.this.panel.setAddLineState();
                TechnicsRouteToolBar.this.panel.repaint();
            }
        };
        this.addSingleLineUnit.putValue("ShortDescription", "添加一段直线");
        add(this.addSingleLineUnit);

        this.addBidirectionalSingleLineUnit = new AbstractAction("添加一段双向直线",
                IconUtil.getImageIcon("/images/single_d.png")) {
            public void actionPerformed(ActionEvent e) {
                TechnicsRouteToolBar.this.panel.mouseMode = 7;
                TechnicsRouteToolBar.this.panel.setAddLineState();
                TechnicsRouteToolBar.this.panel.repaint();
            }
        };
        this.addBidirectionalSingleLineUnit.putValue("ShortDescription",
                "添加一段双向直线");
        add(this.addBidirectionalSingleLineUnit);

        this.addHorizontalDoubleLineUnit = new AbstractAction("添加二段水平线",
                IconUtil.getImageIcon("/images/horizontal_double_line.gif")) {
            public void actionPerformed(ActionEvent e) {
                TechnicsRouteToolBar.this.panel.mouseMode = 3;
                TechnicsRouteToolBar.this.panel.setAddLineState();
                TechnicsRouteToolBar.this.panel.repaint();
            }
        };
        this.addHorizontalDoubleLineUnit
                .putValue("ShortDescription", "添加二段水平线");
        add(this.addHorizontalDoubleLineUnit);

        this.addBidirectionalHorizontalDoubleLineUnit = new AbstractAction(
                "添加二段双向水平线",
                IconUtil.getImageIcon("/images/horizontal_double_d.png")) {
            public void actionPerformed(ActionEvent e) {
                TechnicsRouteToolBar.this.panel.mouseMode = 8;
                TechnicsRouteToolBar.this.panel.setAddLineState();
                TechnicsRouteToolBar.this.panel.repaint();
            }
        };
        this.addBidirectionalHorizontalDoubleLineUnit.putValue(
                "ShortDescription", "添加二段双向水平线");
        add(this.addBidirectionalHorizontalDoubleLineUnit);

        this.addVerticalDoubleLineUnit = new AbstractAction("添加二段垂直线",
                IconUtil.getImageIcon("/images/vertical_double_line.gif")) {
            public void actionPerformed(ActionEvent e) {
                TechnicsRouteToolBar.this.panel.mouseMode = 4;
                TechnicsRouteToolBar.this.panel.setAddLineState();
                TechnicsRouteToolBar.this.panel.repaint();
            }
        };
        this.addVerticalDoubleLineUnit.putValue("ShortDescription", "添加二段垂直线");
        add(this.addVerticalDoubleLineUnit);

        this.addBidirectionalVerticalDoubleLineUnit = new AbstractAction(
                "添加二段双向垂直线",
                IconUtil.getImageIcon("/images/vertical_double_d.png")) {
            public void actionPerformed(ActionEvent e) {
                TechnicsRouteToolBar.this.panel.mouseMode = 9;
                TechnicsRouteToolBar.this.panel.setAddLineState();
                TechnicsRouteToolBar.this.panel.repaint();
            }
        };
        this.addBidirectionalVerticalDoubleLineUnit.putValue(
                "ShortDescription", "添加二段双向垂直线");
        add(this.addBidirectionalVerticalDoubleLineUnit);

        this.addHorizontalTrebleLineUnit = new AbstractAction("添加三段水平线",
                IconUtil.getImageIcon("/images/horizontal_treble_line.gif")) {
            public void actionPerformed(ActionEvent e) {
                TechnicsRouteToolBar.this.panel.mouseMode = 5;
                TechnicsRouteToolBar.this.panel.setAddLineState();
                TechnicsRouteToolBar.this.panel.repaint();
            }
        };
        this.addHorizontalTrebleLineUnit
                .putValue("ShortDescription", "添加三段水平线");
        add(this.addHorizontalTrebleLineUnit);

        this.addBidirectionalHorizontalTrebleLineUnit = new AbstractAction(
                "添加三段双向水平线",
                IconUtil.getImageIcon("/images/horizontal_treble_d.png")) {
            public void actionPerformed(ActionEvent e) {
                TechnicsRouteToolBar.this.panel.mouseMode = 10;
                TechnicsRouteToolBar.this.panel.setAddLineState();
                TechnicsRouteToolBar.this.panel.repaint();
            }
        };
        this.addBidirectionalHorizontalTrebleLineUnit.putValue(
                "ShortDescription", "添加三段双向水平线");
        add(this.addBidirectionalHorizontalTrebleLineUnit);

        this.addVerticalTrebleLineUnit = new AbstractAction("添加三段垂直线",
                IconUtil.getImageIcon("/images/vertical_treble_line.gif")) {
            public void actionPerformed(ActionEvent e) {
                TechnicsRouteToolBar.this.panel.mouseMode = 6;
                TechnicsRouteToolBar.this.panel.setAddLineState();
                TechnicsRouteToolBar.this.panel.repaint();
            }
        };
        this.addVerticalTrebleLineUnit.putValue("ShortDescription", "添加三段垂直线");
        add(this.addVerticalTrebleLineUnit);

        this.addBidirectionalVerticalTrebleLineUnit = new AbstractAction(
                "添加三段双向垂直线",
                IconUtil.getImageIcon("/images/vertical_treble_d.png")) {
            public void actionPerformed(ActionEvent e) {
                TechnicsRouteToolBar.this.panel.mouseMode = 11;
                TechnicsRouteToolBar.this.panel.setAddLineState();
                TechnicsRouteToolBar.this.panel.repaint();
            }
        };
        this.addBidirectionalVerticalTrebleLineUnit.putValue(
                "ShortDescription", "添加三段双向垂直线");
        add(this.addBidirectionalVerticalTrebleLineUnit);

        this.addHorizontalQuintupleLineUnit = new AbstractAction("添加五段水平线",
                IconUtil.getImageIcon("/images/horizontal_quintuple.png")) {
            public void actionPerformed(ActionEvent e) {
                TechnicsRouteToolBar.this.panel.mouseMode = 12;
                TechnicsRouteToolBar.this.panel.setAddLineState();
                TechnicsRouteToolBar.this.panel.repaint();
            }
        };
        this.addHorizontalQuintupleLineUnit.putValue("ShortDescription",
                "添加五段水平线");
        add(this.addHorizontalQuintupleLineUnit);

        this.addBidirectionalHorizontalQuintupleLineUnit = new AbstractAction(
                "添加五段双向水平线",
                IconUtil.getImageIcon("/images/horizontal_quintuple_d.png")) {
            public void actionPerformed(ActionEvent e) {
                TechnicsRouteToolBar.this.panel.mouseMode = 13;
                TechnicsRouteToolBar.this.panel.setAddLineState();
                TechnicsRouteToolBar.this.panel.repaint();
            }
        };
        this.addBidirectionalHorizontalQuintupleLineUnit.putValue(
                "ShortDescription", "添加五段双向水平线");
        add(this.addBidirectionalHorizontalQuintupleLineUnit);
        addSeparator();

        this.toUp = new AbstractAction("向上对齐",
                IconUtil.getImageIcon("/images/align_v_top.gif")) {
            public void actionPerformed(ActionEvent e) {
                if (TechnicsRouteToolBar.this.panel.multiSelectedVector.size() > 1)
                    TechnicsRouteToolBar.this.panel.toUp();
            }
        };
        this.toUp.putValue("ShortDescription", "向上对齐");
        add(this.toUp);

        this.toBottom = new AbstractAction("向下对齐",
                IconUtil.getImageIcon("/images/align_v_bottom.gif")) {
            public void actionPerformed(ActionEvent e) {
                if (TechnicsRouteToolBar.this.panel.multiSelectedVector.size() > 1)
                    TechnicsRouteToolBar.this.panel.toBottom();
            }
        };
        this.toBottom.putValue("ShortDescription", "向下对齐");
        add(this.toBottom);

        this.toLeft = new AbstractAction("向左对齐",
                IconUtil.getImageIcon("/images/align_h_left.gif")) {
            public void actionPerformed(ActionEvent e) {
                if (TechnicsRouteToolBar.this.panel.multiSelectedVector.size() > 1)
                    TechnicsRouteToolBar.this.panel.toLeft();
            }
        };
        this.toLeft.putValue("ShortDescription", "向左对齐");
        add(this.toLeft);

        this.toRight = new AbstractAction("向右对齐",
                IconUtil.getImageIcon("/images/align_h_right.gif")) {
            public void actionPerformed(ActionEvent e) {
                if (TechnicsRouteToolBar.this.panel.multiSelectedVector.size() > 1)
                    TechnicsRouteToolBar.this.panel.toRight();
            }
        };
        this.toRight.putValue("ShortDescription", "向右对齐");
        add(this.toRight);

        this.toHorizontalCenter = new AbstractAction("水平居中对齐",
                IconUtil.getImageIcon("/images/align_h_centers.gif")) {
            public void actionPerformed(ActionEvent e) {
                if (TechnicsRouteToolBar.this.panel.multiSelectedVector.size() > 1)
                    TechnicsRouteToolBar.this.panel.toHorizontalCenter();
            }
        };
        this.toHorizontalCenter.putValue("ShortDescription", "水平居中对齐");
        add(this.toHorizontalCenter);

        this.toVerticalCenter = new AbstractAction("垂直居中对齐",
                IconUtil.getImageIcon("/images/align_v_centers.gif")) {
            public void actionPerformed(ActionEvent e) {
                if (TechnicsRouteToolBar.this.panel.multiSelectedVector.size() > 1)
                    TechnicsRouteToolBar.this.panel.toVerticalCenter();
            }
        };
        this.toVerticalCenter.putValue("ShortDescription", "垂直居中对齐");
        add(this.toVerticalCenter);
        addSeparator();

        this.deleteOneLine = new AbstractAction("清除选中线段",
                IconUtil.getImageIcon("/images/delete_edit.gif")) {
            public void actionPerformed(ActionEvent e) {
                TechnicsRouteUnit unit = TechnicsRouteToolBar.this.panel.tempUnit;
                if ((unit != null) && ((unit instanceof SingleLineUnit))) {
                    ((SingleLineUnit) unit)
                            .deleteSelf(TechnicsRouteToolBar.this.panel);
                    TechnicsRouteToolBar.this.panel.repaint();
                }
            }
        };
        this.deleteOneLine.putValue("ShortDescription", "清除选中线段");
        add(this.deleteOneLine);

        this.deleteUselessLine = new AbstractAction("清除无用线段",
                IconUtil.getImageIcon("/images/clear.gif")) {
            public void actionPerformed(ActionEvent e) {
                TechnicsRouteToolBar.this.panel.deleteUselessLine();
            }
        };
        this.deleteUselessLine.putValue("ShortDescription", "清除无用线段");
        add(this.deleteUselessLine);
        addSeparator();

        this.createImage = new AbstractAction("生成图片",
                IconUtil.getImageIcon("/images/jpg.gif")) {
            public void actionPerformed(ActionEvent e) {
                try {
                    TechnicsRouteToolBar.this.panel.createImage();
                } catch (Exception e1) {

                    e1.printStackTrace();
                }
            }
        };
        this.createImage.putValue("ShortDescription", "生成图片");
        add(this.createImage);

        this.saveTechnicsRoute = new AbstractAction("保存工艺路线图",
                IconUtil.getImageIcon("/images/save_template.gif")) {
            public void actionPerformed(ActionEvent e) {
                TechnicsRouteToolBar.this.panel.saveTechnicsRoute();
            }
        };
        this.saveTechnicsRoute.putValue("ShortDescription", "保存工艺路线图");
        add(this.saveTechnicsRoute);

        this.refresh = new AbstractAction("[刷新]:×5",
                IconUtil.getImageIcon("/images/refresh.png")) {
            public void actionPerformed(ActionEvent e) {
                try {

                    //TechnicsRouteToolBar.this.panel.refreshTechnicsRoute();
                    int isRefresh = JOptionPane.showConfirmDialog(null, "刷新后会重新设置所有工序为串行连接，请确认是否需要执行此操作？", "确定", JOptionPane.YES_NO_OPTION);
                    if (isRefresh == JOptionPane.YES_OPTION) {
                        TechnicsRouteToolBar.this.panel.reSetTechnicsRoute1();
                        TechnicsRouteToolBar.this.panel.saveTechnicsRoute2();
                    }
                } catch (Exception e1) {

                    e1.printStackTrace();
                    JOptionPane
                            .showMessageDialog(null, "刷新工艺路线图出现错误！", "提示", 1);
                }
            }
        };
        this.refresh.putValue("ShortDescription", "[刷新]:×5");
        add(this.refresh);

        this.reSet1 = new AbstractAction("重置（×5）",
                IconUtil.getImageIcon("/images/field_to_local.gif")) {
            public void actionPerformed(ActionEvent e) {
                try {
                    TechnicsRouteToolBar.this.panel.reSetTechnicsRoute1();
                } catch (Exception e1) {

                    e1.printStackTrace();
                    JOptionPane
                            .showMessageDialog(null, "重置工艺路线图出现错误！", "提示", 1);
                }
            }
        };
        this.reSet1.putValue("ShortDescription", "重置(×5)");
        //add(this.reSet1);

        this.reSet = new AbstractAction("重置",
                IconUtil.getImageIcon("/images/field_to_local.gif")) {
            public void actionPerformed(ActionEvent e) {
                try {
                    TechnicsRouteToolBar.this.panel.reSetTechnicsRoute();
                } catch (Exception e1) {

                    e1.printStackTrace();
                    JOptionPane
                            .showMessageDialog(null, "重置工艺路线图出现错误！", "提示", 1);
                }
            }
        };
        this.reSet.putValue("ShortDescription", "重置(×10)");
        //add(this.reSet);

        this.createmoreImage = new AbstractAction("5×6",
                IconUtil.getImageIcon("/images/field_to_local.gif")) {
            public void actionPerformed(ActionEvent e) {
                try {
//                    int isRefresh = JOptionPane.showConfirmDialog(null, "刷新后会重新设置所有工序为串行连接，请确认是否需要执行此操作？", "确定", JOptionPane.YES_NO_OPTION);
//                    if (isRefresh == JOptionPane.YES_OPTION) {
                    TechnicsRouteToolBar.this.panel.createmoreImage();
                    TechnicsRouteToolBar.this.panel.saveTechnicsRoute2();
//                    }
                } catch (Exception e1) {

                    e1.printStackTrace();

                }
            }
        };
        this.createmoreImage.putValue("ShortDescription", "5×6");
        add(this.createmoreImage);


        this.showEditAllImage = new AbstractAction("全屏编辑",
                IconUtil.getImageIcon("/images/allEidtTechtu.png")) {
            public void actionPerformed(ActionEvent e) {
                try {
                    new ShowEditAllTechnicsRouteDialog(TechnicsRouteToolBar.this.panel, TechnicsRouteToolBar.this.panel.parentFrame, TechnicsRouteToolBar.this);
                } catch (Exception e1) {

                    e1.printStackTrace();

                }
            }
        };
        this.showEditAllImage.putValue("ShortDescription", "全屏编辑");
        add(this.showEditAllImage);


    }

    public void setToolBarEnabled(boolean flag) {
        this.setToDefault.setEnabled(flag);
        this.addSingleLineUnit.setEnabled(flag);
        this.addBidirectionalSingleLineUnit.setEnabled(flag);
        this.addHorizontalDoubleLineUnit.setEnabled(flag);
        this.addBidirectionalHorizontalDoubleLineUnit.setEnabled(flag);
        this.addVerticalDoubleLineUnit.setEnabled(flag);
        this.addBidirectionalVerticalDoubleLineUnit.setEnabled(flag);
        this.addHorizontalTrebleLineUnit.setEnabled(flag);
        this.addBidirectionalHorizontalTrebleLineUnit.setEnabled(flag);
        this.addVerticalTrebleLineUnit.setEnabled(flag);
        this.addBidirectionalVerticalTrebleLineUnit.setEnabled(flag);
        this.addHorizontalQuintupleLineUnit.setEnabled(flag);
        this.addBidirectionalHorizontalQuintupleLineUnit.setEnabled(flag);
        this.toUp.setEnabled(flag);
        this.toBottom.setEnabled(flag);
        this.toLeft.setEnabled(flag);
        this.toRight.setEnabled(flag);
        this.toHorizontalCenter.setEnabled(flag);
        this.toVerticalCenter.setEnabled(flag);
        this.deleteOneLine.setEnabled(flag);
        this.deleteUselessLine.setEnabled(flag);
        this.createImage.setEnabled(flag);
        this.saveTechnicsRoute.setEnabled(flag);
        this.refresh.setEnabled(flag);
        this.reSet.setEnabled(flag);
    }

    public void setEditAllImageToolBarEnabled(boolean flag) {
        this.showEditAllImage.setEnabled(flag);
    }
}
