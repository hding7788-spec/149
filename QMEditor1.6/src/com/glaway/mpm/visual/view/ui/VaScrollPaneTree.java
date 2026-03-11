package com.glaway.mpm.visual.view.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.tree.TreeNode;

import org.dom4j.Document;
import org.dom4j.Element;

import com.glaway.mpm.qmIntf.fittingTool.view.FittingsDistributionFrame;
import com.glaway.mpm.util.LoadConfig;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.util.XmlUtil;
import com.glaway.mpm.visual.bean.VaEPartInstance;
import com.glaway.mpm.visual.bean.VaLightPart;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.VaContext;
import com.glaway.mpm.visual.view.tree.VaTree;
import com.glaway.mpm.visual.view.tree.VaTreeNode;

/**
 * <br>Created on 2010-10-23
 * @author Dennis Huang - ���ٽ�
 */
public class VaScrollPaneTree extends JScrollPane {
   private static final long serialVersionUID = 5106882626216586255L;
   private static final VaLogger log = VaLogger.getLogger(VaScrollPaneTree.class);

   private Image             image;

  public  VaTree vaTree;
   String roottype;

   public VaScrollPaneTree(String roottype) {
      super();
      this.roottype=roottype;
      setOpaque(true);
      getViewport().setOpaque(false);
//      image = CmUtil.getImageFromServer("background.gif");
//
//      vaTree = new VaTree("EBOM",null);
     vaTree = new VaTree(new VaTreeNode(roottype));
     vaTree.initTree(VaContext.getCurrentPartNumber(),LoadConfig.getInstance().getPbomView(),roottype);
      try {
    	vaTree.setRootVisible(true);
		setViewportView(vaTree);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   }

//   public VaScrollPaneTree(){
//
//   }

   public VaTree getVaTree() {
	return vaTree;
}

public void setVaTree(VaTree vaTree) {
	this.vaTree = vaTree;
}

public void paintComponent(Graphics g) {
      super.paintComponent(g);
      setBackground(Color.WHITE);

      if (image != null) {
         int height = image.getHeight(this);
         int width = image.getWidth(this);

         if (height != -1 && height > getHeight())
            height = getHeight();

         if (width != -1 && width > getWidth())
            width = getWidth();

         int x = (int) (((double) (getWidth() - width)) / 2.0);
         int y = (int) (((double) (getHeight() - height)) / 2.0);

         g.drawImage(image, x, y, width, height, this);
      }
   }

   public VaTree getTree() {
      return (VaTree) getViewport().getView();
   }
}