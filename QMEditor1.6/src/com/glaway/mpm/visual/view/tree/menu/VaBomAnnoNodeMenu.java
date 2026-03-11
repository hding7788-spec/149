package com.glaway.mpm.visual.view.tree.menu;

import java.awt.Container;
import java.awt.Desktop;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;

import com.glaway.mpm.qmIntf.decoratePView.showPanel.view.DpPviewAction;
import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpPaceNode;
import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpStepNode;
import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpTreePanel;
import com.glaway.mpm.task.CmTaskExecutor;
import com.glaway.mpm.task.CmTaskExecutorCallback;
import com.glaway.mpm.task.exception.CmTaskException;
import com.glaway.mpm.task.util.CmTaskHelper;
import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.visual.view.VaContext;
import com.glaway.mpm.visual.view.action.VaPviewAction;
import com.glaway.mpm.visual.view.pview.VaPViewFactory;
import com.glaway.mpm.visual.view.pview.VaPViewImpl;
import com.glaway.mpm.visual.view.tree.VaTree;
import com.glaway.mpm.visual.view.tree.VaTreeNode;

public class VaBomAnnoNodeMenu extends JMenu implements CmTaskExecutor {
	private static final long serialVersionUID = -8396429397442896329L;
	private VaTree tree;
	private String currStepNum;
	private String currPaceNum;
	private String techPath;
	private String annoDirPath;
	private Window owner;
	private boolean executorActive;

	public VaBomAnnoNodeMenu(VaTree tree, VaTreeNode currNode, Window owner) {
		this.tree = tree;
		this.owner = owner;
		setText("注释集");
		// setIconStr("paste.gif");

		if (currNode instanceof DpStepNode) {
			DpStepNode stepNode = (DpStepNode) currNode;
			this.currStepNum = stepNode.getStep().getOid().replace(':', '`');
			this.currPaceNum = "EMPTY";
		} else if (currNode instanceof DpPaceNode) {
			DpPaceNode paceNode = (DpPaceNode) currNode;
			DpStepNode parentStepNode = (DpStepNode) (currNode.getParent());
			this.currPaceNum = paceNode.getPace().getOid().replace(':', '`');
			this.currStepNum = parentStepNode.getStep().getOid().replace(':', '`');
		}
		String xmlPath = VaContext.getCurrentTechXMLPath();
		this.techPath = xmlPath.substring(0, xmlPath.lastIndexOf("\\"));
		this.annoDirPath = this.techPath + "\\anno";
		final String annoFilePath = checkAnnoAdded();
		if (annoFilePath != null) {
			JMenuItem modifyAnno = new JMenuItem();
			modifyAnno.setText("修改");
			modifyAnno.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {

					File annoFile = new File(annoFilePath);
					File annoDir = annoFile.getParentFile();
					// File[] files = annoDir.listFiles();
					// for (int i = 0; i < files.length; i++) {
					// if ((files[i].getName().endsWith(".pvs") ||
					// files[i].getName().endsWith(".ol")))
					// files[i].delete();
					// }
					openPVS(annoDir.getAbsolutePath() + "\\anno.pvs");

				}
			});
			this.add(modifyAnno);

			JMenuItem updateAnno = new JMenuItem();
			updateAnno.setText("更新");
			updateAnno.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {

					File annoFile = new File(annoFilePath);
					File annoDir = annoFile.getParentFile();
					// File[] files = annoDir.listFiles();
					// for (int i = 0; i < files.length; i++) {
					// if((files[i].getName().endsWith(".pvs") ||
					// files[i].getName().endsWith(".ol")))
					// files[i].delete();
					// }
					String annoPath = savePVS(annoDir.getAbsolutePath() + "\\anno.pvs");
					if (annoPath != null) {
						openPVS(annoDir.getAbsolutePath() + "\\anno.pvs");
					} else {
						JOptionPane.showMessageDialog(VaBomAnnoNodeMenu.this.owner, "pvs保存失败，无法创建注释集");
					}

				}
			});
			this.add(updateAnno);

			JMenuItem deleteAnno = new JMenuItem();
			deleteAnno.setText("删除");
			deleteAnno.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {

					int returnValue = JOptionPane.showConfirmDialog(null, "确定要删除吗？", "提示", 0);
					if (returnValue == 0) {
						File annoFile = new File(annoFilePath);
						File annoDir = annoFile.getParentFile();
						File[] files = annoDir.listFiles();
						for (int i = 0; i < files.length; i++) {
							if (!files[i].delete()) {
								JOptionPane.showMessageDialog(null, "删除文件" + files[i] + "失败!");
							}
						}
						annoDir.delete();
					}
				}
			});
			this.add(deleteAnno);
		}
		JMenuItem createAnno = new JMenuItem();
		createAnno.setText("新建");
		createAnno.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				try {
					CmTaskHelper.registerTaskExecutor("VaBomAnnoNodeMenu.createAnno", VaBomAnnoNodeMenu.this, true);
					TreePath[] paths = VaBomAnnoNodeMenu.this.tree.getSelectionPaths();
//					System.out.println("--------paths-----"+paths.length);
					DpTreePanel dpPanel = null;
					for (int i = 0; i < paths.length; i++) {
						VaTreeNode node = (VaTreeNode) paths[i].getLastPathComponent();
//						System.out.println("-----select node----"+node.getPart().getNumber());
						Container jc = VaBomAnnoNodeMenu.this.tree.getParent();
//						System.out.println("--------jc-----"+jc);
						while (jc != null) {
							if (jc instanceof DpTreePanel) {
								dpPanel = (DpTreePanel) jc;
								break;
							}
							jc = jc.getParent();
						}

					}
					if (dpPanel != null) {
						dpPanel.pviewAction();
					}
				} catch (CmTaskException e) {

					e.printStackTrace();
				}

			}
		});

		this.add(createAnno);

	}

	public synchronized void createAnno(Object render, Object params, CmTaskExecutorCallback callback) {
		File pvsDir = null;
		File annoDir = new File(annoDirPath);
		if (!annoDir.exists() || !annoDir.isDirectory()) {
			annoDir.mkdir();
		}
		if (currPaceNum == "EMPTY") {
			pvsDir = new File(annoDirPath + "\\" + currStepNum);
			if (!pvsDir.exists()) {
				pvsDir.mkdir();
			}

		} else {
			File sDir = new File(annoDirPath + "\\" + currStepNum);
			if (!sDir.exists()) {
				sDir.mkdir();
			}
			pvsDir = new File(annoDirPath + "\\" + currStepNum + "\\" + currPaceNum);
			if (!pvsDir.exists()) {
				pvsDir.mkdir();
			}
		}
		File[] files = pvsDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile())
				files[i].delete();
		}
		String pvsPath = savePVS(pvsDir.getAbsoluteFile() + "\\anno.pvs");
		if (pvsPath == null) {
			return;
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		openPVS(pvsPath);
		this.setExecutorActive(false);
	}

	private String checkAnnoAdded() {
		File techDir = new File(techPath);
		File annoFile = null;
		if (techDir.exists() && techDir.isDirectory()) {
			if (currPaceNum == "EMPTY") {
				annoFile = new File(annoDirPath + "\\" + currStepNum + "\\anno.etb");

			} else {
				annoFile = new File(annoDirPath + "\\" + currStepNum + "\\" + currPaceNum + "\\anno.etb");
			}
		}
		if (annoFile.exists() && annoFile.isFile()) {
			return annoFile.getAbsolutePath();
		}
		return null;
	}

	private String savePVS(String pvsPath) {
		VaPViewImpl pview = VaPViewFactory.getPViewImpl(VaPViewFactory.PV_NAME_DP);

		return pview.savePVS(pvsPath);
	}

	private boolean openPVS(String pvsPath) {
		Vector vec = new Vector();
		vec.add(pvsPath);
		// DpCreoAnnoDialog d = new DpCreoAnnoDialog(vec,null);
		// d.setVisible(true);
		try {
			Process p = Runtime.getRuntime().exec("cmd /c start \"\"  \"" + pvsPath + "\"");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		try {
			Runtime.getRuntime().exec("cmd /c start \"\"  \"" + "C:\\ab\\3090985\\al6_000_993_asm.pvs" + "\"");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isExecutorActive() {
		return executorActive;
	}

	public void setExecutorActive(boolean executorActive) {
		this.executorActive = executorActive;
	}
	// private void updateAnno(VaTreeNode cNode, String path, String dirName) {
	// File annoDir = new File(path + "");
	// if (annoDir == null || annoDir.isDirectory()) {
	// JOptionPane.showMessageDialog(null, "注释集目录创建失败!");
	// }
	//
	// }
}
