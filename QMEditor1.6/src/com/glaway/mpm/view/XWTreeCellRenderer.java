package com.glaway.mpm.view;

import com.glaway.mpm.util.UserUtil;
import org.dom4j.Document;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.List;

public class XWTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 1L;

    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, this.selected, expanded, leaf, row, hasFocus);
        if (sel) {
            setForeground(getTextSelectionColor());
        } else {
            setForeground(getTextNonSelectionColor());
        }
        if ((value instanceof XWTreeNode)) {
            XWTreeNode node = (XWTreeNode) value;
            setText(node.getDisplayName());
            setToolTipText(node.getTipNoteText());
            setIcon(new ImageIcon(node.getOpenImage()));
            if ((node.getObject() instanceof XWPartTreeObject) && !node.isUsed()) {
                setForeground(Color.gray);
            } else if (node.getObject() instanceof TechnicsMessageTreeObject) {
                TechnicsMessageTreeObject obj = (TechnicsMessageTreeObject) node.getObject();
                Element technicsElement = obj.getTechnicsDocument().getRootElement().element("QMFawTechnicsInfo");
                String state = technicsElement.attributeValue("lifecycle");

                if (!"正在工作".equals(state) && !"修改中".equals(state)) {
                    setForeground(Color.gray);
                }
                String creator = technicsElement.attributeValue("creator");
                List list = UserUtil.getCurrentUserOid();
                String userName = (String) list.get(0);
                if (!creator.equals(userName)) {
                    setForeground(Color.gray);
                }

                XWTreeNode pNode = node.getP();
                if (pNode != null) {
                    boolean isAllowed = Boolean.FALSE;
                    if (pNode.getObject() instanceof XWPartTreeObject) {
                        XWPartTreeObject partObject = (XWPartTreeObject) pNode.getObject();
                        isAllowed = partObject.isAllowed();
                    }else if(pNode.getObject() instanceof XWPartTreeObjectForSearchTech){
                        XWPartTreeObjectForSearchTech partObject = (XWPartTreeObjectForSearchTech) pNode.getObject();
//                        if ("正在工作".equals(state) || "修改中".equals(state)) {
//                            isAllowed = Boolean.TRUE;
//                        }
                        isAllowed = partObject.isAllowed();
                    }
                    if (!isAllowed) {
                        setForeground(Color.gray);
                    }
                }

            } else if (node.getObject() instanceof ReportTechnicsTreeObject) {
                ReportTechnicsTreeObject obj = (ReportTechnicsTreeObject) node.getObject();
                if (obj != null) {
                    Document document = obj.getTechnicsDocument();
                    if (document != null) {
                        Element technicsElement = document.getRootElement().element("XWReportTechnicsInfo");
                        String state = technicsElement.attributeValue("lifecycle");
                        if (!"正在工作".equals(state) && !"修改中".equals(state)) {
                            setForeground(Color.gray);
                        }
                        String creator = technicsElement.attributeValue("creator");
                        List list = UserUtil.getCurrentUserOid();
                        String userName = (String) list.get(0);
                        if (!creator.equals(userName)) {
                            setForeground(Color.gray);
                        }

                        XWTreeNode pNode = node.getP();
                        if (pNode != null) {
                            boolean isAllowed = Boolean.FALSE;
                            if (pNode.getObject() instanceof XWPartTreeObject) {
                                XWPartTreeObject partObject = (XWPartTreeObject) pNode.getObject();
                                isAllowed = partObject.isAllowed();
                            }else if(pNode.getObject() instanceof XWPartTreeObjectForSearchTech){
                                XWPartTreeObjectForSearchTech partObject = (XWPartTreeObjectForSearchTech) pNode.getObject();
                                isAllowed = partObject.isAllowed();
                            }
                            if (!isAllowed) {
                                setForeground(Color.gray);
                            }
                        }

                    }
                }
            }
        }
        this.selected = sel;
        return this;
    }
}
