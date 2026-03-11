package com.glaway.mpm.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.TreeNode;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import wt.doc.WTDocument;
import wt.util.WTException;

import com.glaway.mpm.qmIntf.template.TemplateSearchDialog;
import com.glaway.mpm.util.BomXMLUtil;
import com.glaway.mpm.util.FileChooserTool;
import com.glaway.mpm.util.FilesUtil;
import com.glaway.mpm.util.IntfUtil;
import com.glaway.mpm.util.TechnicsReleaseUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import com.glaway.mpm.util.WorkInProcessUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import com.ptc.windchill.uwgm.common.prefs.res.newCadDocPrefsResource;
/*   author:chenming
 *   data:2016.04.20
 *   comment：修改工艺文件编号，同时保存到xml中，并保存到PDM中
 * */
public class EditTechnicsNumberDialog extends JDialog{
	private NewTechnicsPart frame;
	private JPanel panel =new JPanel();
	private JPanel panel0 =new JPanel();
	private JPanel panel2 =new JPanel();
	private JButton sure=new JButton("确定");
	private JButton close=new JButton("取消");
	private JTextField value= new JTextField();
	private XWTreeNode node;
	public EditTechnicsNumberDialog(NewTechnicsPart parent,
			XWTreeNode node) {
		super(parent, true);
		this.frame = parent;
		this.node=node;
		setTitle("修改工艺文件编号");
        Dimension dimension2 = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((int) (dimension2.getWidth() - 500) / 2 , (int) (dimension2.getHeight() - (625)) / 2, 600, 480);
         initComponent();
         setVisible(true);
	}
    private void initComponent() {
        Container container = getContentPane();
        JScrollPane scrollPane = new JScrollPane(panel0);
        container.add(scrollPane);
        panel0.setLayout(new BorderLayout());
        panel.setLayout(new GridBagLayout());
        panel0.add(panel,BorderLayout.CENTER);
        panel0.add(panel2,BorderLayout.SOUTH);

        panel2.setLayout(new GridBagLayout());
        panel2.add(new JLabel(), new GridBagConstraints(0, 0, 1, 1, 1.0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        sure.setPreferredSize(new Dimension(70, 23));
        sure.setMinimumSize(new Dimension(70, 23));
        sure.setMaximumSize(new Dimension(70, 23));
        panel2.add(sure, new GridBagConstraints(1, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                        5, 5, 15, 15), 0, 0));
        close.setPreferredSize(new Dimension(70, 23));
        close.setMinimumSize(new Dimension(70, 23));
        close.setMaximumSize(new Dimension(70, 23));
        panel2.add(close, new GridBagConstraints(2, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                        5, 5, 15, 15), 0, 0));

        sure.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                XWTreeNode node = frame.xwPartTreePanel.getSelectedTreeNode();
                XWTreeObject object = node.getObject();
                Element element = object.getTreeCellData();
                String technicsNumber=element.attributeValue("technicsNumber");
                if (object instanceof TechnicsMessageTreeObject) {
                    XWTreeNode parent = (XWTreeNode) node.getParent();
                    XWTreeObject pObj = parent.getObject();
                    if (pObj instanceof XWTreeObject) {
                        for (int i = 0; i < parent.getChildCount(); i++) {
                            XWTreeNode childAt = (XWTreeNode) parent.getChildAt(i);
                            XWTreeObject childObj = childAt.getObject();
                            if (childObj instanceof TechnicsMessageTreeObject) {
                                Element treeElement = childObj.getTreeCellData();
                                String childTechnicsNumber=treeElement.attributeValue("technicsNumber");
                                String pplanNumber=treeElement.attributeValue("pplanNumber");
                                if (!childTechnicsNumber.equals(technicsNumber)) {
                                    if (pplanNumber.equals(value.getText())) {
                                        JOptionPane.showMessageDialog(frame, "该工艺文件编号已存在，请核对后再确认！", "提示", 1);
                                        return;
                                    }
                                }

                            }
                        }
                    }
                }

                setValue(value);
                dispose();
            }
        });
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

      //设置网格布局管理器参数
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(5, 5, 0, 0);
        gridBagConstraints.gridwidth = 2;

        //第一行：工艺文件编号
        JLabel mindex_label = new JLabel("工艺文件编号：");
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        panel.add(mindex_label, gridBagConstraints);

        XWTreeObject object = node.getObject();
        if (object instanceof TechnicsMessageTreeObject) {
            Element data = object.getTreeCellData();
            String number = data.attributeValue("pplanNumber");
            value.setText(number);
        }
        value.setPreferredSize(new Dimension(300, 23));
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        panel.add(value, gridBagConstraints);

    }
    public static void main(String[] args) {
        EditTechnicsNumberDialog dialog = new EditTechnicsNumberDialog(null, null);
        dialog.setVisible(true);
        dialog.setSize(200, 100);
    }
    public JTextField getValue() {
        return value;
    }
    public void setValue(JTextField value) {
        this.value = value;
    }

}
