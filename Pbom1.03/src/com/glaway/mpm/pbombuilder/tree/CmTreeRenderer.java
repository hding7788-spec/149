package com.glaway.mpm.pbombuilder.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Panel;
import java.awt.font.TextAttribute;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import com.glaway.mpm.pbombuilder.gui.CmTheme;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.tree.action.BomTreeReportAction;
import com.glaway.mpm.pbombuilder.tree.dialog.BomTreeReportDialog;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.CmUtil;
import com.glaway.mpm.pbombuilder.util.ErpUtil;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;


public class CmTreeRenderer extends JPanel implements TreeCellRenderer {
   private static final long serialVersionUID = -4714326633293893623L;
   private static final String CLASSNAME = CmTreeRenderer.class.getName();
   private static final CmLogger log = CmLogger.getLogger(CLASSNAME);

   protected JCheckBox chkSelect;

   protected JLabel labelDisplay;
   protected JLabel information;

   private int[] pixWorking = new int[256];
   private int[] pixCo = new int[256];
   private int[] pixCoYou = new int[256];

   private int[] pixLocator = new int[256];
   private int[] pixViewable = new int[256];

   public CmTreeRenderer() {
      setLayout(null);
      setOpaque(false);
      add(chkSelect = new JCheckBox());
      add(labelDisplay = new JLabel());
      add(information = new JLabel());

      chkSelect.setOpaque(false);

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

   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
      setEnabled(tree.isEnabled());
      CmTreeNode node = (CmTreeNode) value;
      chkSelect.setSelected(node.isSelected());
      chkSelect.setEnabled(true);
      chkSelect.setVisible(true);

      String stringValue = null;//TODO node.getPart() != null ? (String) node.getPart().getDisplayIdentifier() : null;
      if (stringValue == null)
         stringValue = tree.convertValueToText(value, sel, expanded, leaf, row, hasFocus);

//      stringValue = stringValue + " "+node.getOccId()+":"+node.getOccpath();

      labelDisplay.setText(stringValue);

      if (sel) {
         labelDisplay.setOpaque(true);
         labelDisplay.setBackground(CmTheme.CM_TURQUOISE);
         labelDisplay.setForeground(Color.WHITE);
      } else if (node.isMarked()) {
         labelDisplay.setOpaque(true);
         labelDisplay.setBackground(new Color(110,221,249));
         labelDisplay.setForeground(Color.WHITE);
      } else if(node.isSelected() && node.getPart().isSearch()){
         labelDisplay.setOpaque(true);
    	 labelDisplay.setBackground(new Color(255, 255, 0));
    	 labelDisplay.setForeground(new Color(0, 0, 0));
      }
      else {
         labelDisplay.setOpaque(false);
         labelDisplay.setForeground(Color.BLACK);
      }


      //版本更新要高亮显示
      if(!sel && null!=node.getParent() && node.getPart().isReversion()){
    	  labelDisplay.setForeground(new Color(255,0,255));
      }

      //节点是否有编辑过
      if(!sel && null!=node.getParent() && (node.getPart().isEdit() || node.getPart().isEditOfAssistCount())){
    	  labelDisplay.setForeground(new Color(0,136,255));
      }


      //节点被移动过
      if(!sel && (node.getPart().isMove() || node.getPart().isChangeOfStructure())){
    	  labelDisplay.setForeground(new Color(255,153,0));
      }

      //已归档的灰显、斜体
      if(isPbomNode(node) && CmCommonStringUtil.isHasFilingOfObj(node)){
    	  if(sel){
    		  labelDisplay.setForeground(Color.WHITE);
    	  }else{
    		  labelDisplay.setForeground(Color.GRAY);
    	  }
    	  labelDisplay.setFont(new Font("宋体", 3, 12));
      }
      else{
    	  labelDisplay.setFont(new Font("宋体", 0, 12));
      }

      //TODO 如果当前part是外购件或自制件且未设置存货编码
//      if(isPbomNode(node) && !CmCommonStringUtil.isHasFilingOfObj(node)){
//    	  long oid = node.getPart().getOid();
//    	  String parentPartNumber = node.getPart().getParentPartNumber();
//          String partType = PBOMEditorToWCIntf.getPartTypeByPartOid(oid);
//          if((partType != null) && (ErpUtil.ERP_PARTTYPE_WGJ.equals(partType) || ErpUtil.ERP_PARTTYPE_BZJ.equals(partType))) {
//        	  //String chbm = PBOMEditorToWCIntf.getPartIBAValueByPartOid(oid, "CHBM");
//        	  String chbm = PBOMEditorToWCIntf.getPartLinkIBAValueByPartOid(parentPartNumber,oid, "CHBM");
//    		  if(chbm == null || "".equals(chbm)) {
//    			  labelDisplay.setForeground(Color.BLUE);
//    		  }
//          }
//      }

      lightTheChangeNodeOfReport(node,sel);

      Image img = null;
      if (tree.getModel().getRoot().equals(value)) {
         img = CmUtil.getImageFromServer("cart.gif");
      }
      else if("middle".equals(node.getPart().getPartType())){
    	  img = CmUtil.getImageFromServer("middle.gif");
      }
      else if("middle2".equals(node.getPart().getPartType())){
    	  img = CmUtil.getImageFromServer("middle.gif");
      }
      else if("assistant".equals(node.getPart().getPartType())){
    	  img = CmUtil.getImageFromServer("assist.gif");
      }
      else if("PurchasedPart".equals(node.getPart().getPartType())){
    	  //外购件
    	  img = CmUtil.getImageFromServer("PurchasedPart.png");
      }
      else if("StandardPart".equals(node.getPart().getPartType())){
    	  //标准件
    	  img = CmUtil.getImageFromServer("StandardPart.png");
      }
      else if("ALKPart".equals(node.getPart().getPartType())){
    	  //科研件
    	  img = CmUtil.getImageFromServer("ALKPart.png");
      }
      else if("SHPart".equals(node.getPart().getPartType())){
    	  //三化件
    	  img = CmUtil.getImageFromServer("SHPart.png");
      }
      else if("mp".equals(node.getPart().getPartType())){
    	  //毛坯件
    	  img = CmUtil.getImageFromServer("fujian.gif");
      }
      else if("zuhe".equals(node.getPart().getPartType())){
    	  //工艺组合件
    	  img = CmUtil.getImageFromServer("assist.gif");
      }
      else {
    	  img = CmUtil.getImageFromServer("iconPart.gif");
      }
      int status = 0;// TODO node.getStatus();
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
      Image eimg = null;
      if(node.getPart().getEchangeIndex()>0 && !"EBOM".equals(node.toString()) && isEbomNode(node)){
    	  //EBOM变更类型  ：1-版本变化，2-新增，3-删除
          if(node.getPart().getEchangeIndex()==1){
        	  eimg = CmUtil.getImageFromServer("ebom_version.gif");
          }else if(node.getPart().getEchangeIndex()==2){
        	  eimg = CmUtil.getImageFromServer("ebom_add.gif");
          }else if(node.getPart().getEchangeIndex()==3){
        	  Map<Attribute, Object> map = new HashMap<Attribute, Object>();
              map.put(TextAttribute.FONT, labelDisplay.getFont());//原字体
              map.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);//增加的属性
              map.put(TextAttribute.FOREGROUND, Color.RED);//增加的属性
              labelDisplay.setFont(Font.getFont(map));//设置新字体
          }
    	  if(null != eimg){
    		  information.setOpaque(true);
    		  if (status != 0) {
    		         if (status == 2)
    		        	 eimg = addGlyph(img, pixCo);
    		         else if (status == 1)
    		        	 eimg = addGlyph(img, pixWorking);
    		         else if (status == 3)
    		        	 eimg = addGlyph(img, pixCoYou);
    		      }
    		  ImageIcon eicon = new ImageIcon(eimg);
    		  information.setIcon(eicon);
    	  }else{
    		  information.setOpaque(false);
    		  information.setIcon(null);
    	  }
      }else if(!"EBOM".equals(node.toString()) && !isEbomNode(node) && node.getPart().isChange()){
    	  eimg = CmUtil.getImageFromServer("process_plan.gif");
    	  ImageIcon eicon = new ImageIcon(eimg);
		  information.setIcon(eicon);
      }
      else{
    	  information.setOpaque(false);
    	  information.setIcon(null);
      }
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

      return new Dimension(d_check.width + d_label.width + d_info.width + 6, 16);

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

      int y_check = 0;
      int y_label = 0;
      int y_info = 0;

      if (d_check.height > d_label.height)
         y_check = (d_label.height - d_check.height) / 2;

      chkSelect.setLocation(0, y_check);
      chkSelect.setBounds(0, y_check, d_check.width, d_check.height);

      labelDisplay.setLocation(d_check.width, y_label);
      labelDisplay.setBounds(d_check.width, y_label, d_label.width + 2, d_label.height);

      information.setLocation(d_check.width + d_label.width + 2, y_info);
      information.setBounds(d_check.width + d_label.width + 2, y_info, d_info.width, d_info.height);
   }

   // -----------------------------------------------
   /**
    * addGlyph
    *
    * @param image - Image
    * @param pix - int[]
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
//         log.warn("::::::::il>256: " + i1);
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

   public boolean isEbomNode(CmTreeNode node){
	   if(getRootNode(node).toString().equals("EBOM")){
		   return true;
	   }
	   else{
		   return false;
	   }
   }

   public boolean isPbomNode(CmTreeNode node){
	   if(getRootNode(node).toString().equals("PBOM")){
		   return true;
	   }
	   else{
		   return false;
	   }
   }

   public CmTreeNode getRootNode(CmTreeNode node){
	   if(node.getParent()!=null){
		   return getRootNode((CmTreeNode)node.getParent());
	   }
	   else{
		   return node;
	   }
   }

   /**
    * 将报表中节点高亮显示
    * @param node
    *
    */
   public void lightTheChangeNodeOfReport(CmTreeNode node,boolean sel){
	   int i=BomTreeReportDialog.getTheReportNodeStatus(node);
		switch (i){
			case 0:
				if(!sel && BomTreeReportAction.flag && "new".equals(node.getPart().getOperType())){
					labelDisplay.setForeground(new Color(0,153,51));
				}
				break;
			case 1:
				//节点有变化
				if(!sel){
					labelDisplay.setForeground(new Color(0,136,255));
				}
				break;
			case 2:
				//新增节点
				if(!sel){
					labelDisplay.setForeground(new Color(0,153,51));
				}
				break;
			case 3:
				//删除节点
				if(!sel){
					labelDisplay.setForeground(new Color(221,75,57));
				}
		}
	}

}
