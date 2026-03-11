package com.glaway.mpm.visual.view.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Panel;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.util.VaUtil;
import com.glaway.mpm.visual.view.VaTheme;

public class VaTreeRenderer extends JPanel implements TreeCellRenderer {
	private static final long		serialVersionUID	= -4714326633293893623L;
	private static final String		CLASSNAME			= VaTreeRenderer.class.getName();
	private static final VaLogger	log					= VaLogger.getLogger(CLASSNAME);

	protected JCheckBox				chkSelect;

	protected JLabel				labelDisplay;
	protected JLabel				information;
	protected JLabel				labelAnno;

	private int[]					pixWorking			= new int[256];
	private int[]					pixCo				= new int[256];
	private int[]					pixCoYou			= new int[256];

	private int[]					pixLocator			= new int[256];
	private int[]					pixViewable			= new int[256];

	public VaTreeRenderer() {
		setLayout(null);
		setOpaque(false);
		add(chkSelect = new JCheckBox());
		add(labelDisplay = new JLabel());
		add(information = new JLabel());
		add(labelAnno = new JLabel());

		chkSelect.setOpaque(false);

		labelDisplay.setForeground(Color.black);
		labelDisplay.setOpaque(false);

		information.setOpaque(false);

		Image WORKING_GLYPH = VaUtil.getImageFromServer("workingcopy_glyph.gif");

		PixelGrabber pixGrabWorking = new PixelGrabber(WORKING_GLYPH, 0, 0, 16, 16, pixWorking, 0, 16);
		Image CHECKOUT_GLYPH = VaUtil.getImageFromServer("checkout_glyph.gif");
		PixelGrabber pixGrabCo = new PixelGrabber(CHECKOUT_GLYPH, 0, 0, 16, 16, pixCo, 0, 16);
		Image CHECKOUTYOU_GLYPH = VaUtil.getImageFromServer("checkoutyou_glyph.gif");
		PixelGrabber pixGrabCoYou = new PixelGrabber(CHECKOUTYOU_GLYPH, 0, 0, 16, 16, pixCoYou, 0, 16);
		Image VIEWABLE_GLYPH = VaUtil.getImageFromServer("viewable_glyph.gif");
		PixelGrabber pixGrabViewable = new PixelGrabber(VIEWABLE_GLYPH, 0, 0, 16, 16, pixViewable, 0, 16);
		Image LOCATOR_GLYPH = VaUtil.getImageFromServer("variant_loc_glyph.gif");
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

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
      setEnabled(tree.isEnabled());
      VaTreeNode node = (VaTreeNode) value;

      chkSelect.setSelected(node.isSelected());
      chkSelect.setEnabled(true);
//      if (node.isRoot()) {
//         chkSelect.setVisible(false);
//      } else {
         chkSelect.setVisible(true);
//      }
         if(node.isAnno()){
        	 labelAnno.setIcon(new ImageIcon(VaTreeRenderer.class.getResource("/images/cappBook.gif")));

         }else{
        	 labelAnno.setIcon(null);
         }

      String stringValue = null;//TODO node.getPart() != null ? (String) node.getPart().getDisplayIdentifier() : null;
      if (stringValue == null)
         stringValue = tree.convertValueToText(value, sel, expanded, leaf, row, hasFocus);
      labelDisplay.setText(stringValue);

      if (sel) {
         labelDisplay.setOpaque(true);
         labelDisplay.setBackground(VaTheme.VA_TURQUOISE);
         labelDisplay.setForeground(Color.WHITE);
      } else if (node.isMarked()) {
         labelDisplay.setOpaque(true);
         labelDisplay.setBackground(VaTheme.VA_BLUE);
         labelDisplay.setForeground(Color.WHITE);
      } else if(node.isUsed()) {
         labelDisplay.setOpaque(true);
         labelDisplay.setBackground(Color.WHITE);
         labelDisplay.setForeground(Color.gray);
      } else if (node.isMore()) {
		  labelDisplay.setOpaque(true);
		  labelDisplay.setBackground(Color.WHITE);
		  labelDisplay.setForeground(Color.red);
	  }else {
    	  labelDisplay.setOpaque(false);
    	  labelDisplay.setForeground(Color.BLACK);
      }

//      Image img = null;
//      if (tree.getModel().getRoot().equals(value)) {
//         img = VaUtil.getImageFromServer("cart.gif");
//      } else {
//         img = VaTypeHelper.getTypeIcon(node.getPart().getType());
//      }
//      int status = 0;// TODO node.getStatus();
//      if (status != 0) {
//         if (status == 2)
//            img = addGlyph(img, pixCo);
//         else if (status == 1)
//            img = addGlyph(img, pixWorking);
//         else if (status == 3)
//            img = addGlyph(img, pixCoYou);
//      }

//      ImageIcon icon = new ImageIcon(img);
		//FPBOM列表根据类型不同展示不同图标  add by chenjianhui 2019.10.11
		if("标准紧固件".equals(node.getDataType()) || "标准件".equals(node.getDataType())){
			labelDisplay.setIcon(new ImageIcon(VaTreeRenderer.class.getResource("/images/importjob.gif")));
		}else if("元器件".equals(node.getDataType())){
			labelDisplay.setIcon(new ImageIcon(VaTreeRenderer.class.getResource("/images/part_sn.gif")));
		}else if("金属材料".equals(node.getDataType())){
			labelDisplay.setIcon(new ImageIcon(VaTreeRenderer.class.getResource("/images/phantom_part.png")));
		}else if("非金属材料".equals(node.getDataType())){
			labelDisplay.setIcon(new ImageIcon(VaTreeRenderer.class.getResource("/images/adv_config_part.gif")));
		}else if("复合材料".equals(node.getDataType())){
			labelDisplay.setIcon(new ImageIcon(VaTreeRenderer.class.getResource("/images/configuration_context.png")));
		}else if("机电材料".equals(node.getDataType())){
			labelDisplay.setIcon(new ImageIcon(VaTreeRenderer.class.getResource("/images/config_part.gif")));
		}else if("火工品".equals(node.getDataType())){
			labelDisplay.setIcon(new ImageIcon(VaTreeRenderer.class.getResource("/images/part_picker_tab.gif")));
		}else if("劳防、文版用品".equals(node.getDataType())){
			labelDisplay.setIcon(new ImageIcon(VaTreeRenderer.class.getResource("/images/partM_cadManager.gif")));
		}else{
			labelDisplay.setIcon(new ImageIcon(VaTreeRenderer.class.getResource("/image/part.gif")));
		}
		//FPBOM图标分类end
//      labelDisplay.setIcon(new ImageIcon(VaTreeRenderer.class.getResource("/image/part.gif")));
            // Setting tooltips on nodes
      /*      String tooltipVal = null;
       String tooltipPattern = DICContext.getProperty("DIC.Attributes.TooltipValue.RE");
       if (tooltipPattern == null || "".equals(tooltipPattern)) {
       tooltipVal = null;
       } else {
       //if(node.getPart() != null) log.debug("node.getPart().getAttributes(): " + node.getPart().getAttributes());
       Hashtable<String, String> attsHash = node.getPart() == null ? null : node.getPart().getAttributes();
       if (attsHash != null)
       tooltipVal = DICUtil.replacePlaceHolderInParam(attsHash, tooltipPattern);// replacePlaceHolderInParam(attsHash, tooltipPattern);
       }
       if (tooltipVal != null)
       setToolTipText(tooltipVal);
       icon = new ImageIcon(img);  */

      //information.setIcon(icon);
      return this;
   }

	// -----------------------------------------------
	/**
	 *
	 * getPreferredSize
	 *
	 * @return dimension - Dimension
	 *
	 */
	// ------------------------------------------------
	public Dimension getPreferredSize() {

		Dimension d_check = chkSelect.getPreferredSize();
		Dimension d_label = labelDisplay.getPreferredSize();
		Dimension d_info = information.getPreferredSize();
		Dimension d_anno = labelAnno.getPreferredSize();

		return new Dimension(d_check.width + d_label.width + d_info.width + d_anno.width +16 + 6, 16);

	}

	// -----------------------------------------------
	/**
	 *
	 * doLayout
	 *
	 */
	// ------------------------------------------------
	public void doLayout() {
		Dimension d_check = chkSelect.getPreferredSize();
		Dimension d_label = labelDisplay.getPreferredSize();
		Dimension d_info = information.getPreferredSize();
		Dimension d_anno = labelAnno.getPreferredSize();

		int y_check = 0;
		int y_label = 0;
		int y_info = 0;
		int y_anno = 0;

		if (d_check.height > d_label.height)
			y_check = (d_label.height - d_check.height) / 2;

		chkSelect.setLocation(0, y_check);
		chkSelect.setBounds(0, y_check, d_check.width, d_check.height);

		labelDisplay.setLocation(d_check.width, y_label);
		labelDisplay.setBounds(d_check.width, y_label, d_label.width + 2, d_label.height);

		information.setLocation(d_check.width + d_label.width + 2, y_info);
		information.setBounds(d_check.width + d_label.width + 2, y_info, d_info.width, d_info.height);

		labelAnno.setLocation(d_check.width + d_label.width + d_info.width + 2, y_anno);
		labelAnno.setBounds(d_check.width + d_label.width + d_info.width + 2, y_info, d_anno.width+16, d_anno.height);

	}

	// -----------------------------------------------
	/**
	 * addGlyph
	 *
	 * @param image
	 *            - Image
	 * @param pix
	 *            - int[]
	 * @return ret - input image overlapped by glyph
	 */
	// ------------------------------------------------
	private Image addGlyph(Image image, int[] pix) {
		Panel temp = new Panel();
		Image ret = null;
		int k = image.getWidth(temp);
		int l = image.getHeight(temp);
		int i1 = k * l;

		if (i1 > 256) {
			// log.warn("::::::::il>256: " + i1);
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
}
