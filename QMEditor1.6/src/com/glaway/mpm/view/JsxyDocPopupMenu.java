package com.glaway.mpm.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.dom4j.Element;

import com.glaway.mpm.util.IntfUtil;
import com.glaway.mpm.util.UserUtil;

import ext.casc.fileprint.FilePrintUtil2;

public class JsxyDocPopupMenu extends JPopupMenu implements ActionListener{
	private JMenuItem xiuding = new JMenuItem("修订");
//	private JMenuItem editJsxy = new JMenuItem("编辑");
	private JMenuItem submit = new JMenuItem("提交签审");
	private JMenuItem delete = new JMenuItem("删除");

	private NewTechnicsPart frame;
	private XWPartTreePanel treePanel;
	private String number;
	public JsxyDocPopupMenu(NewTechnicsPart frame,
			XWPartTreePanel treePanel) {
		super();
		this.frame = frame;
		this.treePanel = treePanel;
		setDefaultLightWeightPopupEnabled(false);
		setLightWeightPopupEnabled(false);
        add(xiuding);
        xiuding.addActionListener(this);
//		add(editJsxy);
//		editJsxy.addActionListener(this);

		add(submit);
		submit.addActionListener(this);
		add(delete);
		delete.addActionListener(this);

	}
	public void setMenuState(XWTreeObject xo) {
		if (xo==null) {
			setNullState();
		}else{
			if (xo instanceof JsxyDocTreeObject) {
			    submit.setEnabled(true);
			    delete.setEnabled(true);
			    xiuding.setEnabled(true);
				JsxyDocTreeObject xo1=(JsxyDocTreeObject)xo;
				String number = xo1.getNumber();
				String flag = (String) IntfUtil.getPeRemoteMethodInvoke("getLifeStateByDocNumber",
						new Class[] { String.class }, new Object[] {number});
				if (!"已批准".equals(flag)) {
				    xiuding.setEnabled(false);
				}else if (flag != null && (!"".equals(flag))
                        && !flag.equals("正在工作") && !flag.equals("修改中")) {
				    submit.setEnabled(false);
                }
				if (NewTechnicsPart.isDownload) {
				    submit.setEnabled(false);
				}
				String creator = frame.xwJsxyJPanel.creatorValue.getText();
				List list = UserUtil.getCurrentUserOid();
		        String name = (String) list.get(2);
		        if (!name.equals(creator)) {
		            submit.setEnabled(false);
		            xiuding.setEnabled(false);
                }

		        XWTreeNode node = (XWTreeNode) frame.xwPartTreePanel.getSelectedTreeNode().getParent().getParent();
                if (node.getObject() instanceof XWPartTreeObject) {
                    XWPartTreeObject partObject = (XWPartTreeObject)node.getObject();
                    if(!partObject.isAllowed()) {
                        submit.setEnabled(false);
                        xiuding.setEnabled(false);
                        delete.setEnabled(false);
                    }
                }

			}
		}

	}
	private void setNullState() {
//		editJsxy.setEnabled(false);
	    delete.setEnabled(false);
		submit.setEnabled(false);
		xiuding.setEnabled(false);
	}

	public Boolean isSubmit(String number) {
	    Boolean flag = (Boolean) IntfUtil.getPeRemoteMethodInvoke("isJsxySubmit",
                new Class[] { String.class }, new Object[] {number});
        return flag;
    }
	public Boolean isGenGaiAndIsWorking(String number) {
	    Boolean flag = (Boolean) IntfUtil.getPeRemoteMethodInvoke("isGenGaiAndIsWorking",
                new Class[] { String.class }, new Object[] {number});
        return flag;

    }

	@Override
	public void actionPerformed(ActionEvent e) {

		XWTreeNode node = frame.xwPartTreePanel.getSelectedTreeNode();
		JsxyDocTreeObject obj = (JsxyDocTreeObject) node.getObject();
		number = obj.getNumber();
		    if(e.getSource()==submit){
            if (isSubmit(number)) {
                JOptionPane.showMessageDialog(frame, "已经提交过签审，不能重复提交！", "提示",JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (!isGenGaiAndIsWorking(number)) {
                JOptionPane.showMessageDialog(frame, "此技术协议发生过更改或者状态不是正在工作！", "提示",JOptionPane.INFORMATION_MESSAGE);
                return;
            }

			Boolean flag = (Boolean) IntfUtil.getPeRemoteMethodInvoke("decompressZip1",
					new Class[] { String.class }, new Object[] {number});
			if (flag) {
				JOptionPane.showMessageDialog(frame, "提交签审成功！", "提示",JOptionPane.INFORMATION_MESSAGE);
			}else{
				JOptionPane.showMessageDialog(frame, "提交签审失败！", "提示",JOptionPane.INFORMATION_MESSAGE);
			}

		}else if (e.getSource()==xiuding) {
			Boolean flag = (Boolean) IntfUtil.getPeRemoteMethodInvoke("HasJsxyDocXiuDing",
					new Class[] { String.class }, new Object[] {number});
			if (flag) {
				JOptionPane.showMessageDialog(frame, "提交修订成功！", "提示",JOptionPane.INFORMATION_MESSAGE);
			}else{
				JOptionPane.showMessageDialog(frame, "提交修订失败,请确认其状态是否符合条件！", "提示",JOptionPane.INFORMATION_MESSAGE);
			}
			if (node!=null) {
			    XWTreeNode parent2 = (XWTreeNode) node.getParent();
                XWTreeObject object = parent2.getObject();
                if (object instanceof TechnicsMessageTreeObject) {
                    this.treePanel.getTree().collapsePath(new TreePath(((DefaultTreeModel) this.treePanel.getTree().getModel()).getPathToRoot(parent2)));
//                 this.treePanel.getTree().expandPath(new TreePath(( (TreePath) this.treePanel.getTree().getSelectionPath().getParentPath()).getPath()));
                    parent2.expand();
                    this.treePanel.getTree().updateUI();
                }
            }

		}else if (e.getSource()==delete) {
		    try{

		    XWTreeNode father = (XWTreeNode) frame.xwPartTreePanel.getSelectedTreeNode().getParent();
		    XWTreeObject object = father.getObject();
		    if (object instanceof TechnicsMessageTreeObject) {
		        TechnicsMessageTreeObject objTreeObject=(TechnicsMessageTreeObject) object;
		        Element data = objTreeObject.getTreeCellData();
		        String parentNumber = data.attributeValue("technicsNumber");
		        Boolean flag = (Boolean) IntfUtil.getPeRemoteMethodInvoke("deleteJsxyLink",
		                new Class[] { String.class,String.class }, new Object[] {parentNumber,number});

		        if (flag) {
		            JOptionPane.showMessageDialog(frame, "删除成功！", "提示",JOptionPane.INFORMATION_MESSAGE);
                }
		        XWTreeNode node2 = frame.xwPartTreePanel.getSelectedTreeNode();
		        frame.xwPartTreePanel.removeNode(node2);
            }

		    }catch(Exception e1){
                e1.printStackTrace();
            }

        }
	}

}
