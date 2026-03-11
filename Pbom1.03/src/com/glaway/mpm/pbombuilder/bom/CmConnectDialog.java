package com.glaway.mpm.pbombuilder.bom;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.UnsupportedLookAndFeelException;

import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTRuntimeException;

import com.glaway.mpm.pbombuilder.gui.CmTheme;
import com.glaway.mpm.pbombuilder.jws.CmContext;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.tree.CmScrollPaneTree;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.dialog.SetPbomAttributeDialog;
import com.glaway.mpm.pbombuilder.util.CmSearchHelper;
import com.glaway.mpm.pbombuilder.util.LoadConfig;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.ptc.jws.JWSUtil;

public class CmConnectDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private static final CmLogger log = CmLogger.getLogger(CmConnectDialog.class.getName());
	public static String partOid;
	public static Long planningOid;
	public static Long designOid;
	public static String productName = "";

	/**
	 * @author chenyunlong
	 * @date 2013-5-7
	 * @param args
	 *
	 */
	public static void main(String[] args) {
		try {
			LookUtils.setLookAndTheme(new Plastic3DLookAndFeel(), new CmTheme());
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		try {
			String tempoid;
			if (args == null || args.length == 0) {
				tempoid = "";
			} else {
				tempoid = args[0];
			}
			String partOid = tempoid;
			 new CmConnectDialog(partOid);
//			new CmConnectDialog("1232365");
//			 new CmConnectDialog("488700");
//			 new CmConnectDialog("584762");
			log.debug("args[0]----" + partOid);
		} catch (NumberFormatException e) {
			new CmConnectDialog("0");
		}
	}

	public CmConnectDialog(String partOid) {
		super();
		try {
			this.partOid = checkInput(partOid);
		} catch (WTRuntimeException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(CmContext.getMainFrame(), "输入异常！");
			this.dispose();
		} catch (WTException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(CmContext.getMainFrame(), "输入异常！");
			this.dispose();
		}
		log.debug("partOid=" + partOid);
		CmScrollPaneTree scrollTreePane = new CmScrollPaneTree(new CmTreeNode("PBOM"));
		CmTree pbomtree = scrollTreePane.getTree();
		SetPbomAttributeDialog dialog=SetPbomAttributeDialog.getInstance(pbomtree.getRoot(),pbomtree);
		dialog.showDialog(pbomtree.getRoot(),pbomtree);
	}

	private String checkInput(String poid) throws WTRuntimeException, WTException {
		log.debug("=====partNumber=====" + poid);
		String partNumber = poid;
		if (null == partNumber || "".equals(partNumber.trim()) || "0".equals(partNumber.trim())) {
			partNumber = JOptionPane.showInputDialog(this, "请输入整件编号");
		}
		if (null == partNumber  ) {
			if (CmContext.isJWS())
				JWSUtil.shutdown();
			System.exit(0);
		} else if ("".equals(partNumber.trim())) {
			JOptionPane.showMessageDialog(CmContext.getMainFrame(), "输入不能为空！");
			checkInput(partNumber);
		} else {
			WTPart part;
			try {
				part = (WTPart) CmSearchHelper.search(WTPart.class, Long.valueOf(partNumber));
				planningOid = PBOMEditorToWCIntf.getViewByName(LoadConfig.getInstance().getPbomView());
				designOid = PBOMEditorToWCIntf.getViewByName("Design");
				if (part == null) {
					JOptionPane.showMessageDialog(CmContext.getMainFrame(), "找不到该整件！");
					partNumber = "0";
					checkInput(partNumber);
				} else {
					productName = part.getContainer().getName();
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return partNumber;
		}
		return "";
	}
}
