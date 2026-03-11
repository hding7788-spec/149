package com.glaway.mpm.pbombuilder.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Panel;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import com.glaway.mpm.pbombuilder.gui.CmTheme;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.tree.action.BomTreeReportAction;
import com.glaway.mpm.pbombuilder.util.CmUtil;

public class CmReportTreeRenderer extends JPanel implements TreeCellRenderer {
	private static final long serialVersionUID = -4714326633293893623L;
	private static final String CLASSNAME = CmTreeRenderer.class.getName();
	private static final CmLogger log = CmLogger.getLogger(CLASSNAME);

	protected JLabel labelDisplay;
	protected JLabel information;

	private int[] pixWorking = new int[256];
	private int[] pixCo = new int[256];
	private int[] pixCoYou = new int[256];

	private int[] pixLocator = new int[256];
	private int[] pixViewable = new int[256];

	public CmReportTreeRenderer() {
		setLayout(null);
		setOpaque(false);
		add(labelDisplay = new JLabel());
		add(information = new JLabel());

		labelDisplay.setForeground(Color.black);
		labelDisplay.setOpaque(false);

		information.setOpaque(false);

		Image WORKING_GLYPH = CmUtil.getImageFromServer("workingcopy_glyph.gif");

		PixelGrabber pixGrabWorking = new PixelGrabber(WORKING_GLYPH, 0, 0, 16, 16, pixWorking, 0, 16);
		Image CHECKOUT_GLYPH = CmUtil.getImageFromServer("checkout_glyph.gif");
		PixelGrabber pixGrabCo = new PixelGrabber(CHECKOUT_GLYPH, 0, 0, 16, 16, pixCo, 0, 16);
		Image CHECKOUTYOU_GLYPH = CmUtil.getImageFromServer("checkoutyou_glyph.gif");
		PixelGrabber pixGrabCoYou = new PixelGrabber(CHECKOUTYOU_GLYPH, 0, 0, 16, 16, pixCoYou, 0, 16);
		Image VIEWABLE_GLYPH = CmUtil.getImageFromServer("viewable_glyph.gif");
		PixelGrabber pixGrabViewable = new PixelGrabber(VIEWABLE_GLYPH, 0, 0, 16, 16, pixViewable, 0, 16);
		Image LOCATOR_GLYPH = CmUtil.getImageFromServer("variant_loc_glyph.gif");
		PixelGrabber pixGrabLocator = new PixelGrabber(LOCATOR_GLYPH, 0, 0, 16, 16, pixLocator, 0, 16);

		try {
			pixGrabWorking.grabPixels();
			pixGrabCo.grabPixels();
			pixGrabCoYou.grabPixels();
			pixGrabViewable.grabPixels();
			pixGrabLocator.grabPixels();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {
		setEnabled(tree.isEnabled());
		CmTreeNode node = (CmTreeNode) value;

		String stringValue = null;
		if (stringValue == null)
			stringValue = tree.convertValueToText(value, sel, expanded, leaf, row, hasFocus);
		labelDisplay.setText(stringValue);

		if (sel) {
			labelDisplay.setOpaque(true);
			labelDisplay.setBackground(CmTheme.CM_TURQUOISE);
			labelDisplay.setForeground(Color.WHITE);
		} else if (node.isMarked()) {
			labelDisplay.setOpaque(true);
			labelDisplay.setBackground(CmTheme.CM_BLUE);
			labelDisplay.setForeground(Color.WHITE);
		} else {
			labelDisplay.setOpaque(false);
			labelDisplay.setForeground(Color.BLACK);
		}
		lightTheChangeNode(node);

		Image img = null;
		if (tree.getModel().getRoot().equals(value)) {
			img = CmUtil.getImageFromServer("cart.gif");
		} else if ("middle".equals(node.getPart().getPartType())) {
			img = CmUtil.getImageFromServer("zhongjian.gif");
		} else if ("assistant".equals(node.getPart().getPartType())) {
			img = CmUtil.getImageFromServer("fujian.gif");
		} else {
			img = CmUtil.getImageFromServer("iconPart.gif");
		}
		int status = 0;
		if (status != 0) {
			if (status == 2)
				img = addGlyph(img, pixCo);
			else if (status == 1)
				img = addGlyph(img, pixWorking);
			else if (status == 3)
				img = addGlyph(img, pixCoYou);
		}

		ImageIcon icon = new ImageIcon(img);
		labelDisplay.setIcon(icon);

		return this;
	}

	public Dimension getPreferredSize() {

		Dimension d_label = labelDisplay.getPreferredSize();
		Dimension d_info = information.getPreferredSize();

		return new Dimension(d_label.width + d_info.width + 6, 16);

	}

	public void doLayout() {
		Dimension d_label = labelDisplay.getPreferredSize();
		Dimension d_info = information.getPreferredSize();

		int y_label = 0;
		int y_info = 0;

		labelDisplay.setLocation(0, y_label);
		labelDisplay.setBounds(0, y_label, d_label.width + 2, d_label.height);

		information.setLocation(0 + d_label.width + 2, y_info);
		information.setBounds(0 + d_label.width + 2, y_info, d_info.width, d_info.height);
	}

	private Image addGlyph(Image image, int[] pix) {
		Panel temp = new Panel();
		Image ret = null;
		int k = image.getWidth(temp);
		int l = image.getHeight(temp);
		int i1 = k * l;

		if (i1 > 256) {
			i1 = 256;
		}

		if (i1 > 0) {
			int ai[] = new int[i1];
			int aiend[] = new int[i1];
			try {
				PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, k, l, ai, 0, k);
				ColorModel colormodel = pixelgrabber.getColorModel();
				pixelgrabber.grabPixels();
				for (int j1 = 0; j1 < i1; j1++) {
					if (colormodel.getAlpha(pix[j1]) > 0)
						aiend[j1] = pix[j1];
					else
						aiend[j1] = ai[j1];
				}
				ret = temp.createImage(new MemoryImageSource(k, l, aiend, 0, k));
			} catch (InterruptedException ex) {
				ex.printStackTrace();
				return image;
			}
		}
		image.flush();
		return ret;
	}

	public void lightTheChangeNode(CmTreeNode node){
		List<CmReportNode> list=null;
		for(CmReportNode report:list){
			if(isHasEqualPart(node.getPart(),report.getEbomPart()) && isHasEqualPart(node.getPart(),report.getPbomPart())){
				//更新节点
				labelDisplay.setForeground(Color.BLUE);
			}
			else if(!isHasEqualPart(node.getPart(),report.getEbomPart()) && isHasEqualPart(node.getPart(),report.getPbomPart())){
				//新增节点
				labelDisplay.setForeground(Color.GREEN);
			}else if(isHasEqualPart(node.getPart(),report.getEbomPart()) && !isHasEqualPart(node.getPart(),report.getPbomPart())){
				//删除节点
				labelDisplay.setForeground(Color.RED);
			}
		}
	}

	public boolean isHasEqualPart(CmLightPart obj,CmLightPart report){
		if(null!=report && obj.getPartNumber().equals(report.getPartNumber())&& obj.getPartName().equals(report.getPartName())){
			return true;
		}
		else{
			return false;
		}
	}
}
